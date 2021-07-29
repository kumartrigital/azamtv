package org.mifosplatform.workflow.eventvalidation.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.exception.ActivePlansFoundException;
import org.mifosplatform.workflow.eventvalidation.data.EventValidationData;
import org.mifosplatform.workflow.eventvalidation.domain.EventValidationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
public class EventValidationReadPlatformServiceImpl implements
		EventValidationReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final SimpleJdbcCall jdbcCall;
	private final EventValidationRepository eventValidationRepository;
	 

	@Autowired
	public EventValidationReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource,
			final EventValidationRepository eventValidationRepository) {
		
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	    this.jdbcCall= new SimpleJdbcCall(dataSource);
	    this.eventValidationRepository=eventValidationRepository;
	}

	@Override
	public List<EventValidationData> retrieveAllEventValidation() {

		try {
			context.authenticatedUser();
			final EventValidationMapper mapper = new EventValidationMapper();
			final String sql = "select " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] {});

		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}

	}

	private static final class EventValidationMapper implements
			RowMapper<EventValidationData> {

		public String schema() {
			return "be.id as id,be.event_name as eventName,be.process as process,be.pre_post as prePost,"
					+ " is_deleted as isDeleted from b_event_validation be order by be.is_deleted";

		}

		@Override
		public EventValidationData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String eventName = rs.getString("eventName");
			String process = rs.getString("process");
			String prePost = rs.getString("prePost");
			String isDeleted = rs.getString("isDeleted");

			return new EventValidationData(id, eventName, process, prePost,
					isDeleted);
		}
	}
	
	
	@Override
	public void checkForCustomValidations(Long clientId,String eventName,String strjson,final Long userId) {
	       
		
		//EventValidation eventValidation=this.eventValidationRepository.findOneByEventName(eventName);
		
	//	if(eventValidation != null && eventValidation.isDeleted() == 'N'){
		
	

			jdbcCall.setProcedureName("custom_validation");
			MapSqlParameterSource parameterSource = new MapSqlParameterSource();
			parameterSource.addValue("p_userid", userId, Types.INTEGER);
			parameterSource.addValue("p_clientid", clientId, Types.INTEGER);
			parameterSource.addValue("jsonstr", strjson, Types.VARCHAR);
			System.out.println(strjson);
			parameterSource.addValue("event_name", eventName, Types.VARCHAR);
			Map<String, Object> out = jdbcCall.execute(parameterSource);
			
			Integer errCode=0;
			String errMsg=null;
			if(out != null){
				errCode=(Integer)out.get("err_code");
				errMsg=(String)out.get("err_msg"); 
			}
				  
			if(errCode != 0 && errMsg != null){
			  throw new ActivePlansFoundException(errMsg); 
		   }
		
	}

}
