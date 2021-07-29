package org.mifosplatform.portfolio.clientdiscount.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.portfolio.clientdiscount.data.ClientDiscountData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientDiscountReadPlatformServiceImpl implements ClientDiscountReadPlatformService{
	

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<ClientDiscountData> paginationHelper = new PaginationHelper<ClientDiscountData>();
	private final PlatformSecurityContext context;
	
	
    @Autowired
	public ClientDiscountReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}

	@Override
	public Page<ClientDiscountData> retrieveClientDiscount(SearchSqlQuery searchClientDiscount, Long clientId) {
		ClientDiscountMapper clientDiscountMapper = new ClientDiscountMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(clientDiscountMapper.schema());
        sqlBuilder.append(" where c.id="+clientId+" and c.is_deleted = 'N'");
        
        String sqlSearch = searchClientDiscount.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " level like '%"+sqlSearch+"%' OR"
	    			+ " discount_type like '%"+sqlSearch+"%' OR"
	    			+ " discount_value like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }


        if (searchClientDiscount.isLimited()) {
            sqlBuilder.append(" limit ").append(searchClientDiscount.getLimit());
        }

        if (searchClientDiscount.isOffset()) {
            sqlBuilder.append(" offset ").append(searchClientDiscount.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, clientDiscountMapper);
	}

	@Override
	public ClientDiscountData retrieveClientDiscount(Long clientId) {

		try{
      	ClientDiscountMapper clientDiscountMapper = new ClientDiscountMapper();
		String sql = "SELECT "+clientDiscountMapper.schema()+" WHERE c.is_deleted = 'N' AND c.client_id = ?";
		return jdbcTemplate.queryForObject(sql, clientDiscountMapper,new Object[]{clientId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	
	}
	
	private class ClientDiscountMapper implements RowMapper<ClientDiscountData> {
	    @Override
		public ClientDiscountData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String level = rs.getString("level");
			final String discountType = rs.getString("discountType");
			final Long discountValue = rs.getLong("discountValue");
			
			return new ClientDiscountData(id, level, discountType, discountValue);
		}
	    
		public String schema() {
			
			return " c.id AS id, c.level AS level, c.discount_type AS discountType,c.discount_value AS discountValue from b_client_discount c";
				   
			
		}
	}


}
