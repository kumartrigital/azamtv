package org.mifosplatform.organisation.salescatalogemapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.salescatalogemapping.data.SalesCatalogeMappingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SalesCatalogeMappingReadPlatformServiceImpl implements SalesCatalogeMappingReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<SalesCatalogeMappingData> paginationHelper = new PaginationHelper<SalesCatalogeMappingData>();
	private final PlatformSecurityContext context;
	
	@Autowired
	public SalesCatalogeMappingReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource, final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}


	@Override
	public SalesCatalogeMappingData retrieveSalesCatalogeMapping(Long salescatalogemappingId) {
		try{
			SalesCatalogeMappingMapper salesCatalogeMappingMapper = new SalesCatalogeMappingMapper();
			String sql = "SELECT "+salesCatalogeMappingMapper.schema()+" WHERE scm.is_deleted = 'N' AND scm.id = ?";
			return jdbcTemplate.queryForObject(sql, salesCatalogeMappingMapper,new Object[]{salescatalogemappingId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	
	}
	
	
	@Override
	public Page<SalesCatalogeMappingData> retrieveSalesCatalogeMapping(SearchSqlQuery searchSalesCatalogeMapping) {

		SalesCatalogeMappingMapper salesCatalogeMappingMapper = new SalesCatalogeMappingMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(salesCatalogeMappingMapper.schema());
        sqlBuilder.append(" where scm.id IS NOT NULL and scm.is_deleted = 'N'");
        
        String sqlSearch = searchSalesCatalogeMapping.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " cataloge_id like '%"+sqlSearch+"%' OR"
	    			+ " plan_id like '%"+sqlSearch+"%' OR"
	    			+ " plan_code like '%"+sqlSearch+"%' OR"
	    			+ " plan_description like '%"+sqlSearch+"%' OR"
	    			+ " name like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }


        if (searchSalesCatalogeMapping.isLimited()) {
            sqlBuilder.append(" limit ").append(searchSalesCatalogeMapping.getLimit());
        }

        if (searchSalesCatalogeMapping.isOffset()) {
            sqlBuilder.append(" offset ").append(searchSalesCatalogeMapping.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, salesCatalogeMappingMapper);
		
	}

	
	private class SalesCatalogeMappingMapper implements RowMapper<SalesCatalogeMappingData> {

		@Override
		public SalesCatalogeMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final Long catalogeId = rs.getLong("catalogeId");
			final Long planId = rs.getLong("planId");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			final String name = rs.getString("name");
			
			return new SalesCatalogeMappingData(id,catalogeId,planId,planCode,planDescription,name);
			
		}
		
       public String schema() {
			
     	  
    	    
    	   return "scm.id AS id, scm.cataloge_id AS catalogeId, scm.plan_id AS planId," +
    	          "p.plan_code AS planCode,p.plan_poid as planPoId, pd.deal_poid as dealPoId, p.plan_description AS planDescription,scd.name AS name " +  
    	          "From b_sales_cataloge_mapping scm join b_plan_master p on p.id =scm.plan_id " +
    	          "join  b_sales_cataloge scd on scd.id = scm.cataloge_id join b_plan_detail pd on pd.plan_id=p.id";

		  }
		
	}
	
	
	@Override
	public List<SalesCatalogeMappingData> retrieveSalesCatalogeMappingData() {

		try{
			SalesCatalogeMappingMapper salesCatalogeMappingMapper = new SalesCatalogeMappingMapper();
			String sql = "SELECT "+salesCatalogeMappingMapper.schema()+" WHERE scm.is_deleted = 'N'";
			return jdbcTemplate.query(sql, salesCatalogeMappingMapper);
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	
	
		
	}
	

}
