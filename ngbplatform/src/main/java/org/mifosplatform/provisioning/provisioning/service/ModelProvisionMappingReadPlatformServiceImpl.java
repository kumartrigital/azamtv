package org.mifosplatform.provisioning.provisioning.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.provisioning.provisioning.data.ModelProvisionMappingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ModelProvisionMappingReadPlatformServiceImpl implements ModelProvisionMappingReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<ModelProvisionMappingData> paginationHelper = new PaginationHelper<ModelProvisionMappingData>();
	
	@Autowired
	public ModelProvisionMappingReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public Page<ModelProvisionMappingData> retrieveAll(SearchSqlQuery searchModelProvisionMapping) {
		ModelProvisionMappingMapper mapper = new ModelProvisionMappingMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.schema());
        sqlBuilder.append(" where mp.is_deleted = 'N' ");
        String sqlSearch = searchModelProvisionMapping.getSqlSearch();
        String extraCriteria = "";
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " provisioning_id like '%"+sqlSearch+"%' OR"
	    			+ " model like '%"+sqlSearch+"%')";
	    }
        sqlBuilder.append(extraCriteria);

        if (searchModelProvisionMapping.isLimited()) {
            sqlBuilder.append(" limit ").append(searchModelProvisionMapping.getLimit());
        }

        if (searchModelProvisionMapping.isOffset()) {
            sqlBuilder.append(" offset ").append(searchModelProvisionMapping.getOffset());
        }
		
        return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, mapper);
	}
	
	
	@Override
	public ModelProvisionMappingData retrieveSingleModelProvisionMapping(Long modelProvisionMappingId) {

    	
    	final ModelProvisionMappingMapper mapper = new ModelProvisionMappingMapper();
		final String sql = "select " + mapper.schemaForNormal()+" where mp.is_deleted = 'N' and mp.id = ?";
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {modelProvisionMappingId});
    
	}
	
	@Override
	public List<ModelProvisionMappingData> retrieveAllForDropDown() {

		final ModelProvisionMappingMapper mapper = new ModelProvisionMappingMapper();
		final String sql = "select " + mapper.schemaForNormal()+" where mp.is_deleted = 'N' ";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
    
	}
	
	
	private class ModelProvisionMappingMapper implements RowMapper<ModelProvisionMappingData> {
		@Override
		public ModelProvisionMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final Long provisioningId = rs.getLong("provisioningId");
			final String model = rs.getString("model");
			final String provisioningName = rs.getString("provisioningName");
			final String make = rs.getString("make");
			final String itemType = rs.getString("itemType");
			return new ModelProvisionMappingData(id,provisioningId,model,provisioningName,make,itemType);
		}
		
		public String schema() {
			
			return " SQL_CALC_FOUND_ROWS  mp.id as id,mp.provisioning_id as provisioningId,mp.model as model,"+
			       " p.system_code as provisioningName,mp.make as make,mp.item_type as itemType "+
				   " from b_item_attribute mp join b_network_element  p ON p.id = mp.provisioning_id ";
			
		}
		public String schemaForNormal() {
			
			return " mp.id as id,mp.provisioning_id as provisioningId,mp.model as model,"+
			       " p.system_code as provisioningName,mp.make as make,mp.item_type as itemType "+
				   " from b_item_attribute mp join b_network_element  p ON p.id = mp.provisioning_id ";
			       
			
		}
	}

}
