package org.mifosplatform.provisioning.processscheduledjobs.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.ticketmaster.data.ClientTicketData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.service.DataSourcePerTenantService;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.security.service.TenantDetailsService;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.ScheduleJobData;
import org.mifosplatform.scheduledjobs.scheduledjobs.domain.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SheduleJobReadPlatformServiceImpl implements SheduleJobReadPlatformService {
	
	
	
	private final TenantDetailsService tenantDetailsService;
	private final DataSourcePerTenantService dataSourcePerTenantService;

@Autowired
public SheduleJobReadPlatformServiceImpl(final DataSourcePerTenantService dataSourcePerTenantService,
			final TenantDetailsService tenantDetailsService) {
		
	this.dataSourcePerTenantService = dataSourcePerTenantService;
	this.tenantDetailsService = tenantDetailsService;

}

	private static final class SheduleJobMapper implements
			RowMapper<ScheduleJobData> {

		public String sheduleLookupSchema() {
			return " sr.id as id,sr.report_name as reportName,sr.report_sql as query  from stretchy_report sr ";
		}

		@Override
		public ScheduleJobData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			final Long id = rs.getLong("id");
			final String batchName = rs.getString("reportName");
			final String query = rs.getString("query");

			return new ScheduleJobData(id, batchName,query);
			}
			}

			@Override
			public List<Long> getClientIds(String query, JobParameterData data) {
				try {
				
			        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
					final ClientIdMapper mapper = new ClientIdMapper();
					if("Y".equalsIgnoreCase(data.isDynamic())){
						return jdbcTemplate.query(query, mapper, new Object[] { });
					  }else{
						  query=query.toLowerCase().replace("now()","'"+(data.getProcessDate() !=null ? data.getProcessDate() : data.getDueDate()).toString()+"'");
						  return jdbcTemplate.query(query, mapper, new Object[] {});
						}
					} catch (EmptyResultDataAccessException e) {
					    return null;
					}
				}
			
			  private static final class ClientIdMapper implements RowMapper<Long> {
				@Override
				public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				final Long clientId = rs.getLong("clientId");
			       return clientId;
					}
				}
			
			@Override
			public Long getMessageId(String messageTemplateName) {
				
				try {  
			        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
					final MessageIdMapper mapper = new MessageIdMapper();
					final String sql = "select " + mapper.getmessageId();
					return jdbcTemplate.queryForObject(sql, mapper, new Object[]{ messageTemplateName });
					} catch (EmptyResultDataAccessException e) {
					return null;
					}
					}
					private static final class MessageIdMapper implements RowMapper<Long> {

					public String getmessageId() {
					//return "mt.id as id from b_template mt where mt.name=?";
				     return "mt.id as id from b_message_template mt where mt.template_description=?";

					}
					@Override
					public Long mapRow(final ResultSet rs,  final int rowNum) throws SQLException {
					final Long id = rs.getLong("id");
					return id ;
					}
				}

					@Override
					public List<ScheduleJobData> retrieveSheduleJobParameterDetails(String paramValue) {
						try {
							
					        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
							final SheduleJobMapper mapper = new SheduleJobMapper();
							final String sql = "select " + mapper.sheduleLookupSchema()+"where sr.report_name=?";
							return jdbcTemplate.query(sql, mapper, new Object[] { paramValue });
							} catch (EmptyResultDataAccessException e) {
							return null;
							}
					}

					@Override
					public JobParameterData getJobParameters(String jobName) {
						try
						{
						   List<JobParameters> jobParameters=this.retrieveJobParameters(jobName);//scheduledJobDetail.getDetails();
						   return new JobParameterData(jobParameters); 
						   
						}catch(EmptyResultDataAccessException exception){
							return null;
						}
					}
					private List<JobParameters> retrieveJobParameters(String jobName) {
						try {
					        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
							final SheduleJobParametersMapper mapper = new SheduleJobParametersMapper();
							final String sql = "select " + mapper.sheduleLookupSchema();
							return jdbcTemplate.query(sql, mapper, new Object[] { jobName });
							} catch (EmptyResultDataAccessException e) {
							return null;
							}
					}
					
					private static final class SheduleJobParametersMapper implements RowMapper<JobParameters> {
						public String sheduleLookupSchema() {
						return "p.id as id,p.job_id as jobId,p.param_name as paramName,p.param_type as paramType,p.param_default_value as defaultValue," +
								"p.param_value as paramValue,p.is_dynamic as isDynamic,p.query_values as queryValue from job_parameters p, job j " +
								"where j.id=p.job_id  and j.name=?";
						}

						@Override
						public JobParameters mapRow(final ResultSet rs, final int rowNum) throws SQLException {
						final Long id = rs.getLong("id");
						final Long jobId = rs.getLong("jobId");
						final String paramType = rs.getString("paramType");
						final String paramName = rs.getString("paramName");
						final String defaultValue = rs.getString("defaultValue");
						final String paramValue = rs.getString("paramValue");
						final String isDynamic = rs.getString("isDynamic");
						final String queryValue = rs.getString("queryValue");
						return new JobParameters(id,jobId,paramName,paramType,defaultValue,paramValue,isDynamic,queryValue);
						}
						}

					@Override
					public List<ScheduleJobData> getJobQeryData() {
						try {
							        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
									final SheduleJobMapper mapper = new SheduleJobMapper();
									final String sql = "select " + mapper.sheduleLookupSchema()+"where sr.report_category='Scheduling Job'";
									return jdbcTemplate.query(sql, mapper, new Object[] {  });
									} catch (EmptyResultDataAccessException e) {
									return null;
									}

									
							}
				



	@Override
	public String retrieveMessageData(Long id) {
		// TODO Auto-generated method stub
		try {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(
					dataSourcePerTenantService.retrieveDataSource());
			final MessageMapper mapperdata = new MessageMapper();
			String query = mapperdata.retrieveId(id);
			return jdbcTemplate.queryForObject(query, mapperdata,
					new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class MessageMapper implements RowMapper<String> {
		private Long id;

		public String retrieveId(Long val) {
			this.id = val;
			return "SELECT sms_conf(" + val + ")";
		}

		@Override
		public String mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			String prepare = "sms_conf(" + id + ")";
			String message = rs.getString(prepare);
			return message;

		}
	}
	
	@Override
	public List<ScheduleJobData> retrieveSheduleJobDetails(String paramValue) {

		try {

			final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default");
	        ThreadLocalContextUtil.setTenant(tenant);
	        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
			final SheduleJobMapper1 mapper = new SheduleJobMapper1();

			final String sql = "select " + mapper.sheduleLookupSchema();

			return jdbcTemplate.query(sql, mapper, new Object[] { paramValue });
			} catch (EmptyResultDataAccessException e) {
			return null;
			}

			
	}
	
	private static final class SheduleJobMapper1 implements RowMapper<ScheduleJobData> {

		public String sheduleLookupSchema() {
		return "  b.id as id,b.report_name as batchName,b.report_sql as query from stretchy_report b where b.report_name=?";

		}

		@Override
		public ScheduleJobData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
         
		final Long id = rs.getLong("id");
		final String batchName = rs.getString("batchName");
		final String query = rs.getString("query");
		
		
		return new ScheduleJobData(id, batchName,query);
		}
	 }

	@Override
	public List<Long> getBillIds(String query,JobParameterData data) {
		try {
			
	        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
			final BillIdMapper mapper = new BillIdMapper();
			if("Y".equalsIgnoreCase(data.isDynamic())){
			        return jdbcTemplate.query(query, mapper, new Object[] { });
			  }else{
				    query=query.toLowerCase().replace("now()","?");
					return jdbcTemplate.query(query, mapper, new Object[] { data.getProcessDate() });
			     }
		       } catch (EmptyResultDataAccessException e) {
				return null;
			}
		}
	
	  private static final class BillIdMapper implements RowMapper<Long> {
		@Override
		public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
	               return  rs.getLong("billId");
			}
		}

	@Override
	public List<Long> retrieveAddonsForDisconnection(LocalDate processingDate) {
	
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
		AddonMapper mapper = new AddonMapper();
		final String sql="select  id from b_orders_addons od where Date_format(od.end_date, '%Y-%m-%d') < ? and od.status='ACTIVE'";
		return jdbcTemplate.query(sql, mapper, new Object[] { processingDate.toString() });
	}
	
	private static final class AddonMapper implements RowMapper<Long> {
		@Override
		public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		return rs.getLong("id");
		
			}
		}

	@Override
	public List<ClientTicketData> getEscaltedTickets(String query, JobParameterData data) {
		try {
		
	        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
			final TicketEscalationMapper mapper = new TicketEscalationMapper();
			if("Y".equalsIgnoreCase(data.isDynamic())){
				return jdbcTemplate.query(query, mapper, new Object[] { });
			  }else{
				  query=query.toLowerCase().replace("now()","'"+(data.getProcessDate() !=null ? data.getProcessDate() : data.getDueDate()).toString()+"'");
				  return jdbcTemplate.query(query, mapper, new Object[] {});
				}
			} catch (EmptyResultDataAccessException e) {
			    return null;
			}
		}
	private static final class TicketEscalationMapper implements RowMapper<ClientTicketData> {
		@Override
		public ClientTicketData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long ticketId = rs.getLong("ticketId");
			final String ticketStatus = rs.getString("ticketStatus");
			final Long escalations = rs.getLong("escalations");
			final String managerName = rs.getString("managerName");
			final String managerEmail = rs.getString("managerEmail");
			final String ticketNo = rs.getString("ticketNo");
			final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");
            ClientTicketData clientTicketData = new ClientTicketData(ticketId, ticketStatus, escalations, managerName, managerEmail, ticketNo, createdDate);
	    	return clientTicketData;
		}
	}
	
}
