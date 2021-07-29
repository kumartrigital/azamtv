package org.mifosplatform.portfolio.order.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.data.OrderWorkflowData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderWorkflowReadPlatformServiceImpl implements OrderWorkflowReadPlatfromService {

	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	@Autowired
	public OrderWorkflowReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	@Override
	public OrderWorkflowData getPresentStatus(Long clientServiceId) {
		context.authenticatedUser();
		
		StringBuilder sqlBuilder = new StringBuilder("Select");
		sqlBuilder.append(" status from b_order_workflow where order_id =?");
		sqlBuilder.append(" order by id desc limit 1 ");
		StatusMapper statusMapper = new StatusMapper();
		return this.jdbcTemplate.queryForObject(sqlBuilder.toString(), statusMapper, new Object[] {clientServiceId});
	}

	private static final class StatusMapper implements RowMapper<OrderWorkflowData> {

		@Override
		public OrderWorkflowData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			String status = rs.getString("status");
			return new OrderWorkflowData(status);
		}
	}

}
