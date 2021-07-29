package org.mifosplatform.collectionbatch.timeperiod.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.collectionbatch.timeperiod.data.TimePeriodNewData;
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
public class TimePeriodReadPlatformServiceImpl  implements TimeperiodReadPlatformService{
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<TimePeriodNewData> paginationHelper = new PaginationHelper<TimePeriodNewData>();
	
	
	@Autowired
	public TimePeriodReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public TimePeriodNewData retriveTimeperiods(Long timeperiodId) {
        try{
			
			TimePeriodMapper timePeriodMapper = new TimePeriodMapper();
			String sql = "SELECT "+timePeriodMapper.schema()+" WHERE tp.id = ?";
			return jdbcTemplate.queryForObject(sql, timePeriodMapper,new Object[]{timeperiodId});
			}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
	private class TimePeriodMapper implements RowMapper<TimePeriodNewData> {
	    @Override
		public TimePeriodNewData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long timeperiodId = rs.getLong("timeperiodId");
		    final Long timemodelId = rs.getLong("timemodelId");
			final String timeperiodName = rs.getString("timeperiodName");
			
			
			return new TimePeriodNewData(timeperiodId, timemodelId, timeperiodName);
		}
	    
		public String schema() {
			
			return "tp.id AS timeperiodId,tm.id AS timemodelId,tp.timeperiod_name AS timeperiodName  from r_time_period tp join " + 
				   "r_timemodel tm on tm.id=tp.timemodel_id";
			
		}
	}

	@Override
	public Page<TimePeriodNewData> retrieveTimePeriodData(SearchSqlQuery searchTimePeriodNewData) {
		
		TimePeriodNewMapper timePeriodNewMapper = new TimePeriodNewMapper();
			
			final StringBuilder sqlBuilder = new StringBuilder(200);
	        sqlBuilder.append("select ");
	        sqlBuilder.append(timePeriodNewMapper.schema());
	        sqlBuilder.append(" where tp.id IS NOT NULL and tp.is_deleted = 'N'");
	        
	        String sqlSearch = searchTimePeriodNewData.getSqlSearch();
	        String extraCriteria = null;
		    if (sqlSearch != null) {
		    	sqlSearch=sqlSearch.trim();
		    	extraCriteria = " and (timeperiod_id like '%"+sqlSearch+"%' OR" 
		    			+ " timemodel_id like '%"+sqlSearch+"%' OR"
		    			+ " timeperiod_name like '%"+sqlSearch+"%' OR"
		    			+ " timemodel_name like '%"+sqlSearch+"%' OR"
		    			+ " startyear like '%"+sqlSearch+"%' OR"
		    			+ " endyear like '%"+sqlSearch+"%' OR"
		    			+ " startmonth like '%"+sqlSearch+"%' OR"
		    			+ " endmonth like '%"+sqlSearch+"%' OR"
		    			+ " startday like '%"+sqlSearch+"%' OR"
		    			+ " endday like '%"+sqlSearch+"%')";
		    }
	        
	        if (null != extraCriteria) {
	            sqlBuilder.append(extraCriteria);
	        }


	        if (searchTimePeriodNewData.isLimited()) {
	            sqlBuilder.append(" limit ").append(searchTimePeriodNewData.getLimit());
	        }

	        if (searchTimePeriodNewData.isOffset()) {
	            sqlBuilder.append(" offset ").append(searchTimePeriodNewData.getOffset());
	        }
			
			return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
			        new Object[] {}, timePeriodNewMapper);
		}
		
		
		private class TimePeriodNewMapper implements RowMapper<TimePeriodNewData> {
		    @Override
			public TimePeriodNewData mapRow(ResultSet rs, int rowNum) throws SQLException {
				
		    	final Long timeperiodId = rs.getLong("timeperiodId");
		    	final Long timemodelId = rs.getLong("timemodelId");
				final String timeperiodName = rs.getString("timeperiodName");
				final String timeModelName = rs.getString("timeModelName");
				final Long startYear = rs.getLong("startYear");
				final Long endYear = rs.getLong("endYear");
				final String startMonth = rs.getString("startMonth");
				final String endMonth = rs.getString("endMonth");
				final String startDay = rs.getString("startDay");
				final String endDay = rs.getString("endDay");
				
			   
				
				return new TimePeriodNewData(timeperiodId, timemodelId,timeperiodName, timeModelName, startYear, endYear, startMonth, endMonth,  startDay, endDay);
			}
		    
			public String schema() {
				
				return "tp.id AS timeperiodId,tm.id AS timemodelId,tp.timeperiod_name AS timeperiodName, tm.timemodel_name AS timeModelName,tp.startyear As startYear,tp.endyear As endYear, "
						+ "tp.startmonth As startMonth,tp.endmonth As endMonth,tp.startday As startDay,tp.endday As endDay FROM r_time_period tp JOIN  r_timemodel tm on tm.id=tp.timemodel_id";
				
			}
		}


}
