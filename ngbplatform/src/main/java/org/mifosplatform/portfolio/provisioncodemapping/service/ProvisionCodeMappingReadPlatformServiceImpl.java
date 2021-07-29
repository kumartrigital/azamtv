package org.mifosplatform.portfolio.provisioncodemapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.portfolio.provisioncodemapping.data.ProvisionCodeMappingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ProvisionCodeMappingReadPlatformServiceImpl implements ProvisionCodeMappingReadPlatformService {

	
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<ProvisionCodeMappingData> paginationHelper = new PaginationHelper<ProvisionCodeMappingData>();
	
	
	@Autowired
	public ProvisionCodeMappingReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	//this is used to retrive a all records 

	@Override
	public Page<ProvisionCodeMappingData> retrieveProvisionCodeMapping(SearchSqlQuery searchProvisionCodeMapping) {
		
		ProvisionCodeMappingMapper provisionCodeMappingtMapper = new ProvisionCodeMappingMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(provisionCodeMappingtMapper.schema());
       // sqlBuilder.append(" where pcm.id IS NOT NULL and pcm.is_deleted = 'N'");
        
        String sqlSearch = searchProvisionCodeMapping.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " provision_code like '%"+sqlSearch+"%' OR"
	    			+ " network_code like '%"+sqlSearch+"%' OR"
	    			+ " provision_value like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }


        if (searchProvisionCodeMapping.isLimited()) {
            sqlBuilder.append(" limit ").append(searchProvisionCodeMapping.getLimit());
        }

        if (searchProvisionCodeMapping.isOffset()) {
            sqlBuilder.append(" offset ").append(searchProvisionCodeMapping.getOffset());
        }
		
        return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, provisionCodeMappingtMapper);
	
	}

	//this is used to retrive a particular record 

	@Override
	public ProvisionCodeMappingData retrieveProvisionCodeMapping(Long provisioncodemappingId) {
	
		try{
			ProvisionCodeMappingMapper provisionCodeMappingMapper = new ProvisionCodeMappingMapper();
			String sql = "SELECT "+provisionCodeMappingMapper.schema()+" WHERE pcm.is_deleted = 'N' AND pcm.id = ?";
			return jdbcTemplate.queryForObject(sql, provisionCodeMappingMapper,new Object[]{provisioncodemappingId});
			}catch(EmptyResultDataAccessException ex){
				return null;
		}
	
	}
	
	protected static final class ProvisionCodeMappingMapper implements RowMapper<ProvisionCodeMappingData> {

		@Override
		public ProvisionCodeMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
		final Long id=rs.getLong("id");
		final String provisionCode = rs.getString("provisionCode");
		final String networkCode = rs.getString("networkCode");
		final String provisionValue = rs.getString("provisionValue");

		

		return new ProvisionCodeMappingData(id,provisionCode,networkCode,provisionValue );
}

  public String schema() {
		
	return "pcm.id AS id,pcm.provision_code AS provisionCode,pcm.network_code AS networkCode,pcm.provision_value AS provisionValue  FROM b_provision_code_mapping pcm " ;
	
	}
		
	}

	@Override
	public List<ProvisionCodeMappingData> retrieveProvisionCodeMappingsForDropdown() {

		try{
			ProvisionCodeMappingDropdownMapper provisionCodeMappingMapper = new ProvisionCodeMappingDropdownMapper();
		String sql = "SELECT "+provisionCodeMappingMapper.schema()+" WHERE pcm.is_deleted = 'N'";
		return jdbcTemplate.query(sql, provisionCodeMappingMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
		
	}
	
	private class ProvisionCodeMappingDropdownMapper implements RowMapper<ProvisionCodeMappingData> {

	    @Override
		public ProvisionCodeMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String provisionCode = rs.getString("provisionCode");
		   
			
			return new ProvisionCodeMappingData(id,provisionCode);
		}
	    
		public String schema() {
			
			return " pcm.id AS id,pcm.provision_code AS provisionCode from b_provision_code_mapping pcm  ";
			
		}
	
		
	}


}
