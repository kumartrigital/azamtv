package org.mifosplatform.collectionbatch.template.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.collectionbatch.template.data.TemplateData;
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
public class TemplateReadPlatformServiceImpl implements TemplateReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<TemplateData> paginationHelper = new PaginationHelper<TemplateData>();
	

	@Autowired
	public TemplateReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource
			) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Page<TemplateData> retrieveTemplates(SearchSqlQuery searchTemplate) {
		TemplateMapper templateMapper = new TemplateMapper();

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select ");
		sqlBuilder.append(templateMapper.schema());
		/* sqlBuilder.append(" where templateId IS NOT NULL "); */

		String sqlSearch = searchTemplate.getSqlSearch();
		String extraCriteria = null;
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.trim();
			extraCriteria = " and (templateId like '%" + sqlSearch + "%' OR" + " template_name like '%" + sqlSearch
					+ "%' OR" + " delimited like '%" + sqlSearch + "%' OR" + " delimiter like '%" + sqlSearch + "%' OR"
					+ " number_of_fields like '%" + sqlSearch + "%' OR" + " is_header like '%" + sqlSearch + "%' OR"
					+ " header_record_type like '%" + sqlSearch + "%' OR" + " event_record_type like '%" + sqlSearch
					+ "%' OR" + "%')";

		}

		if (null != extraCriteria) {
			sqlBuilder.append(extraCriteria);
		}
		/* sqlBuilder.append(" and is_deleted = 'N' "); */

		if (searchTemplate.isLimited()) {
			sqlBuilder.append(" limit ").append(searchTemplate.getLimit());
		}

		if (searchTemplate.isOffset()) {
			sqlBuilder.append(" offset ").append(searchTemplate.getOffset());
		}

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
				new Object[] {}, templateMapper);
	}

	private class TemplateMapper implements RowMapper<TemplateData> {
		@Override
		public TemplateData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long templateId = rs.getLong("templateId");
			final String templateName = rs.getString("templateName");
			final String delimited = rs.getString("delimited");
			final String delimiter = rs.getString("delimiter");
			final Long numberOfFields = rs.getLong("numberOfFields");
			final String isheader = rs.getString("isheader");
			final String headerecordtype = rs.getString("headerecordtype");
			final String eventrecordtype = rs.getString("eventrecordtype");
			final String fieldName = rs.getString("fieldName");
			final String fieldType = rs.getString("fieldType");
			final Long length = rs.getLong("length");
			final String identifierType = rs.getString("identifierType");

			return new TemplateData(templateId, templateName, delimited, delimiter, numberOfFields, isheader,
					headerecordtype, eventrecordtype, fieldName, fieldType, length, identifierType);

		}

		public String schema() {

			return " td.id as templateId, td.template_name AS templateName, td.delimited AS delimited, td.delimiter, td.number_of_fields AS numberOfFields, "
					+ " td.is_header AS isheader, td.header_record_type AS headerecordtype, td.event_record_type AS eventrecordtype, tf.field_name AS fieldName, "
					+ " tf.field_type AS fieldType,tf.length AS length,tf.identifier_type identifierType from r_input_master td,r_input_details tf where td.id = tf.template_id ";

		}
	}



	@Override
	
	public List<TemplateData> retrieveTemplate(final Long templateId) {
	
	try {
		ReadMapper readMapper = new ReadMapper();

		String sql = "select distinct "+readMapper.schema()+" where td.id = ? ";

		return jdbcTemplate.query(sql, readMapper, new Object[] {templateId});
	} catch (EmptyResultDataAccessException e) {
		return null;
	  }

}
	
	private static final class ReadMapper implements RowMapper<TemplateData> {
		
		public String schema(){
			
			return " td.id as templateId, td.template_name AS templateName, td.delimited AS delimited, td.delimiter, td.number_of_fields AS numberOfFields, "
					+ " td.is_header AS isheader, td.header_record_type AS headerecordtype, td.event_record_type AS eventrecordtype, tf.field_name AS fieldName, "
					+ " tf.field_type AS fieldType,tf.length AS length,tf.identifier_type identifierType from r_input_master td join r_input_details tf on tf.template_id =td.id ";
				
		}
		  @Override
	      public TemplateData mapRow(final ResultSet rs,final int rowNum) throws SQLException {
			  final Long templateId = rs.getLong("templateId");
				final String templateName = rs.getString("templateName");
				final String delimited = rs.getString("delimited");
				final String delimiter = rs.getString("delimiter");
				final Long numberOfFields = rs.getLong("numberOfFields");
				final String isheader = rs.getString("isheader");
				final String headerecordtype = rs.getString("headerecordtype");
				final String eventrecordtype = rs.getString("eventrecordtype");
				final String fieldName = rs.getString("fieldName");
				final String fieldType = rs.getString("fieldType");
				final Long length = rs.getLong("length");
				final String identifierType = rs.getString("identifierType");
	          TemplateData templateData= new TemplateData(templateId, templateName, delimited, delimiter, numberOfFields, isheader,
						headerecordtype, eventrecordtype, fieldName, fieldType, length, identifierType);
	        
	          return templateData;
	      }
	   }
	
	@Override
	public List<RatableUsageMetricData> retrieveFieldNamesForDropdown() {
		try{
			TemplateMapper2 templateMapper2 = new TemplateMapper2();
			String sql = "SELECT "+templateMapper2.schema();
			return jdbcTemplate.query(sql, templateMapper2,new Object[]{});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
	}
		class TemplateMapper2 implements RowMapper<RatableUsageMetricData> {
		    @Override
			public RatableUsageMetricData mapRow(ResultSet rs, int rowNum) throws SQLException {
				
		    	final Long fieldId = rs.getLong("fieldId");
				final String fieldName = rs.getString("fieldName");
			
			   
				
				return new RatableUsageMetricData(fieldId, fieldName,null,null);
			}
		    
			public String schema() {
				
				return " td.id AS fieldId, td.field_name AS fieldName from r_input_details td ";
				
			}
		}
	
	
	
	
	
	
	
	
	
	     

}
