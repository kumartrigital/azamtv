package org.mifosplatform.collectionbatch.usagerateplan.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.collectionbatch.usagerateplan.data.RatePlanData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class UsagePlanReadPlatformServiceImpl implements UsagePlanReadPlatformService {
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<RatePlanData> paginationHelper = new PaginationHelper<RatePlanData>();
	private final PlatformSecurityContext context;

	@Autowired
	public UsagePlanReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,
			final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}

	@Override
	public Page<RatePlanData> retriveAllRateplans(SearchSqlQuery searchRatablePlans) {

		RatePlanMapper ratePlanMapper = new RatePlanMapper();

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select distinct ");
		sqlBuilder.append(ratePlanMapper.schema());
		sqlBuilder.append(" where ur.id IS NOT NULL ");

		String sqlSearch = searchRatablePlans.getSqlSearch();
		String extraCriteria = null;
		if (sqlSearch != null) {
			sqlSearch = sqlSearch.trim();
			extraCriteria = " and (id like '%" + sqlSearch + "%' OR" + " timemodel_name like '%" + sqlSearch + "%' OR" + " timemodel_id like '%" + sqlSearch + "%' OR"
					+ " rating_type like '%" + sqlSearch + "%' OR" 
					+ " rum_name like '%" + sqlSearch + "%')";
		}

		if (null != extraCriteria) {
			sqlBuilder.append(extraCriteria);
		}
		// sqlBuilder.append(" and is_deleted = 'N' ");

		if (searchRatablePlans.isLimited()) {
			sqlBuilder.append(" limit ").append(searchRatablePlans.getLimit());
		}

		if (searchRatablePlans.isOffset()) {
			sqlBuilder.append(" offset ").append(searchRatablePlans.getOffset());
		}

		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
				new Object[] {}, ratePlanMapper);
	}
	private class RatePlanMapper implements RowMapper<RatePlanData> {
	    @Override
		public RatePlanData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
	    	final Long timemodelId = rs.getLong("timemodelId");
		    final String timeModelName = rs.getString("timeModelName");
			final String ratingType = rs.getString("ratingType");
		    final String rumName = rs.getString("rumName");
			
			
			return new RatePlanData(id,timemodelId, timeModelName, ratingType, rumName);
		}
	    
		public String schema() {
			
			return "ur.id AS id,tm.id AS timemodelId, tm.timemodel_name AS timemodelName, ur.rating_type AS ratingType,rm.rum_name AS rumName "+
				   "from  r_usage_rateplan ur join r_timemodel tm  on tm.id=ur.timemodel_id join r_rum rm on rm.id = ur.rum_id";
			
		}
	}
}
