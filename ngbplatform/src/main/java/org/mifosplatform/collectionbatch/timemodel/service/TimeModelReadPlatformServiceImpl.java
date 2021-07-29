package org.mifosplatform.collectionbatch.timemodel.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.collectionbatch.timemodel.data.TimeModelData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressLocationDetails;
import org.mifosplatform.organisation.address.service.AddressReadPlatformServiceImpl.AddressLocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TimeModelReadPlatformServiceImpl implements TimeModelReadPlatformService {
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<TimeModelData> paginationHelper = new PaginationHelper<TimeModelData>();
	private final PlatformSecurityContext context;

	@Autowired
	public TimeModelReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,
			final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}

	@Override
	public List<TimeModelData> retrieveTimeModelForDropdown() {
		try {
			TemplateMapper templateMapper = new TemplateMapper();
			String sql = "SELECT " + templateMapper.schema();
			return jdbcTemplate.query(sql, templateMapper, new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	class TemplateMapper implements RowMapper<TimeModelData> {
		@Override
		public TimeModelData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long timemodelId = rs.getLong("timemodelId");
			final String timemodelName = rs.getString("timemodelName");

			return new TimeModelData(timemodelId, timemodelName);
		}

		public String schema() {

			return " tm.id AS timemodelId, tm.timemodel_name AS timemodelName from r_timemodel tm ";

		}
	}
	
	@Override
	public List<TimeModelData> retrieveTimePeriodForDropdown() {
		try {
			TemplateMapper2 templateMapper2 = new TemplateMapper2();
			String sql = "SELECT " + templateMapper2.schema();
			return jdbcTemplate.query(sql, templateMapper2, new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	class TemplateMapper2 implements RowMapper<TimeModelData> {
		@Override
		public TimeModelData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long timeperiodId = rs.getLong("timeperiodId");
			final String timeperiodName = rs.getString("timeperiodName");

			return new TimeModelData(timeperiodId, timeperiodName,null);
		}

		public String schema() {

			return " tp.id AS timeperiodId, tp.timeperiod_name AS timeperiodName from r_time_period tp ";

		}
	}

	@Override
	public Page<TimeModelData> retrieveAllTimemodels(SearchSqlQuery searchTimemodels) {
	
		try{
			context.authenticatedUser();
			final TimeModelMapper timeModelMapper=new TimeModelMapper();
			
			final StringBuilder sqlBuilder = new StringBuilder(200);
			  sqlBuilder.append("select ");
			  sqlBuilder.append(timeModelMapper.schema());
			  String sqlSearch=searchTimemodels.getSqlSearch();
			  String extraCriteria = "";
			    if (sqlSearch != null) {
			    	sqlSearch=sqlSearch.trim();
			    	extraCriteria = "  where timemodel_name like '%"+sqlSearch+"%' "; 
			    }
			    
			    sqlBuilder.append(extraCriteria);
			    
			    if (searchTimemodels.isLimited()) {
		            sqlBuilder.append(" limit ").append(searchTimemodels.getLimit());
		        }
			    if (searchTimemodels.isOffset()) {
		            sqlBuilder.append(" offset ").append(searchTimemodels.getOffset());
		        }
			    return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		                new Object[] {},timeModelMapper);
		}catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	
	public static final class TimeModelMapper implements RowMapper<TimeModelData>{
		public String schema() {
			
			return  "tm.id as timemodelId,tm.timemodel_name as timeModelName,tm.description as description,"+
				    "tp.id as timeperiodId,tp.timeperiod_name as timeperiodName,tp.startyear as StartYear,tp.endyear as endYear,tp.startmonth as startMonth,tp.endmonth as endMonth,tp.startday as startDay ,tp.endday as endDay ,tp.starttime as starttime ,tp.endtime as endtime "+ 
				    "from r_timemodel tm  "+ 
				    "left join r_time_period tp  on (tp.timemodel_id=tm.id and tp.is_deleted='N')"+
				    "where tm.is_active='Y'";
			        
		}
		@Override
		public TimeModelData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long timemodelId=rs.getLong("timemodelId");
			final String timeModelName=rs.getString("timeModelName");
			final String description=rs.getString("description");
			final String timeperiodName=rs.getString("timeperiodName");
			final Long timeperiodId=rs.getLong("timeperiodId");
			final Long StartYear=rs.getLong("StartYear");
			final Long endYear=rs.getLong("endYear");
			final String startMonth=rs.getString("startMonth");
			final String endMonth=rs.getString("endMonth");
			final String startDay=rs.getString("startDay");
			final String endDay=rs.getString("endDay");
			final String startTime=rs.getString("startTime");
			final String endTime=rs.getString("endTime");
			
				return new TimeModelData(timemodelId,timeModelName,description,null,timeperiodName,timeperiodId,StartYear,endYear,startMonth,endMonth,startDay,endDay,startTime,endTime);
			}
		}

	
	
	
	
	
	
}
