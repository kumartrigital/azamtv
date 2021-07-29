package org.mifosplatform.collectionbatch.unitofmeasurement.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.collectionbatch.unitofmeasurement.data.UnitOfmeasurementData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class UnitOfmeasurementReadPlatformServiceImpl implements UnitOfmeasurementReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<UnitOfmeasurementData> paginationHelper = new PaginationHelper<UnitOfmeasurementData>();
	
	
	@Autowired
	public UnitOfmeasurementReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	@Override
	public Page<UnitOfmeasurementData> retriveAllUnitOfmeasurement(SearchSqlQuery searchUnitOfmeasurement) {
		UOMMapper uomMapper = new UOMMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(uomMapper.schema());
        sqlBuilder.append(" where id IS NOT NULL ");
        
        String sqlSearch = searchUnitOfmeasurement.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " uom_name like '%"+sqlSearch+"%' OR"
	    			+ " uom_description like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }
       // sqlBuilder.append(" and is_deleted = 'N' ");

        if (searchUnitOfmeasurement.isLimited()) {
            sqlBuilder.append(" limit ").append(searchUnitOfmeasurement.getLimit());
        }

        if (searchUnitOfmeasurement.isOffset()) {
            sqlBuilder.append(" offset ").append(searchUnitOfmeasurement.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, uomMapper);
	}
	
	
	private class UOMMapper implements RowMapper<UnitOfmeasurementData> {
	    @Override
		public UnitOfmeasurementData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
		    final String Name = rs.getString("Name");
			final String Description = rs.getString("Description");
			
			
			return new UnitOfmeasurementData(id, Name, Description);
		}
	    
		public String schema() {
			
			return "um.id AS id, um.uom_name AS Name, um.uom_description AS Description from r_uom um";
			
		}
	}


	@Override
	public UnitOfmeasurementData retriveUnitOfmeasurement(Long id) {
		try{
			
			UOMMapper uomMapper = new UOMMapper();
			String sql = "SELECT "+uomMapper.schema()+" WHERE um.id = ?";
			return jdbcTemplate.queryForObject(sql, uomMapper,new Object[]{id});
			}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}


	/*@Override
	public List<UnitOfmeasurementData> retrieveUomForDropdown() {
	
		return null;
	}*/
	
	@Override
	public List<UnitOfmeasurementData> retrieveUomForDropdown() {
		try{
			TemplateMapper1 templateMapper = new TemplateMapper1();
			String sql = "SELECT "+templateMapper.schema();
			return jdbcTemplate.query(sql, templateMapper,new Object[]{});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
	}
		class TemplateMapper1 implements RowMapper<UnitOfmeasurementData> {
		    @Override
			public UnitOfmeasurementData mapRow(ResultSet rs, int rowNum) throws SQLException {
				
		    	final Long uomId = rs.getLong("uomId");
				final String uomName = rs.getString("uomName");
			
			   
				
				return new UnitOfmeasurementData(uomId, uomName);
			}
		    
			public String schema() {
				
				return " um.id AS uomId, um.uom_name AS uomName from r_uom um ";
				
			}
		}

}
