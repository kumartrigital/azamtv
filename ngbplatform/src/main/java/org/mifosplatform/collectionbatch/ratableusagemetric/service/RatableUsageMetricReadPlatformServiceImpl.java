package org.mifosplatform.collectionbatch.ratableusagemetric.service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
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
public class RatableUsageMetricReadPlatformServiceImpl implements RatableUsageMetricReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<RatableUsageMetricData> paginationHelper = new PaginationHelper<RatableUsageMetricData>();
	
	
	@Autowired
	public RatableUsageMetricReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	
	@Override
	public Page<RatableUsageMetricData> retriveAllRatableUsageMetric(SearchSqlQuery searchRatableUsageMetric) {
		RatableMapper ratableMapper = new RatableMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(ratableMapper.schema());
        sqlBuilder.append(" where id IS NOT NULL ");
        
        String sqlSearch = searchRatableUsageMetric.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " charge_code_Id like '%"+sqlSearch+"%' OR"
	    			+ " template_id like '%"+sqlSearch+"%' OR"
	    			+ " rum_name like '%"+sqlSearch+"%' OR"
	    			+ " rum_expression like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }
       // sqlBuilder.append(" and is_deleted = 'N' ");

        if (searchRatableUsageMetric.isLimited()) {
            sqlBuilder.append(" limit ").append(searchRatableUsageMetric.getLimit());
        }

        if (searchRatableUsageMetric.isOffset()) {
            sqlBuilder.append(" offset ").append(searchRatableUsageMetric.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, ratableMapper);
	}
	
	
	
	private class RatableMapper implements RowMapper<RatableUsageMetricData> {
	    @Override
		public RatableUsageMetricData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
		    final Long chargeCodeId = rs.getLong("chargeCodeId");
			final Long templateId = rs.getLong("templateId");
		    final String rumName = rs.getString("rumName");
			final String rumExpression = rs.getString("rumExpression");
			
			
			return new RatableUsageMetricData(id,chargeCodeId, templateId, rumName, rumExpression);
		}
	    
		public String schema() {
			
			return " rm.id AS id, rm.charge_code_Id AS chargeCodeId, rm.template_id AS templateId, rm.rum_name AS rumName, "+
                   " rm.rum_expression AS rumExpression from r_rum rm ";
			
		}
	}

	
	
	
	@Override
	public RatableUsageMetricData retrieveRatableUsageMetric(Long id) {
		try{
			
			RatableMapper ratableMapper = new RatableMapper();
			String sql = "SELECT "+ratableMapper.schema()+" WHERE rm.id = ?";
			return jdbcTemplate.queryForObject(sql, ratableMapper,new Object[]{id});
			}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}



	@Override
	public List<RatableUsageMetricData> retrieveTemplateForDropdown() {
		try{
			TemplateMapper templateMapper = new TemplateMapper();
			String sql = "SELECT "+templateMapper.schema();
			return jdbcTemplate.query(sql, templateMapper,new Object[]{});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
	}
		class TemplateMapper implements RowMapper<RatableUsageMetricData> {
		    @Override
			public RatableUsageMetricData mapRow(ResultSet rs, int rowNum) throws SQLException {
				
		    	final Long templateId = rs.getLong("templateId");
				final String templateName = rs.getString("templateName");
			
			   
				
				return new RatableUsageMetricData(templateId, templateName);
			}
		    
			public String schema() {
				
				return " tp.id AS templateId, tp.template_name AS templateName from r_input_master tp ";
				
			}
		}
		
		@Override
		public List<RatableUsageMetricData> retrieveRumForDropdown() {
			try{
				TemplateMapper1 templateMapper = new TemplateMapper1();
				String sql = "SELECT "+templateMapper.schema();
				return jdbcTemplate.query(sql, templateMapper,new Object[]{});
				}catch(EmptyResultDataAccessException ex){
					return null;
				}
		}
			class TemplateMapper1 implements RowMapper<RatableUsageMetricData> {
			    @Override
				public RatableUsageMetricData mapRow(ResultSet rs, int rowNum) throws SQLException {
					
			    	final Long rumId = rs.getLong("rumId");
					final String rumName = rs.getString("rumName");
				
				   
					
					return new RatableUsageMetricData(rumId, rumName,null);
				}
			    
				public String schema() {
					
					return " rm.id AS rumId, rm.rum_name AS rumName from r_rum rm ";
					
				}
			}
		
		
	}

	


