package org.mifosplatform.organisation.redemption.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RedemptionReadPlatformServiceImpl implements RedemptionReadPlatformService{

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public RedemptionReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource){
		
		this.context=context;
		this.jdbcTemplate=new JdbcTemplate(dataSource);
		
	}

	@Transactional
	@Override
	public List<Long> retrieveOrdersData(final Long clientId,final Long planId){
		
		try{
			
			this.context.authenticatedUser();
		final OrderMapper mapper = new OrderMapper();
		final String sql = "select id as orderId from b_orders where client_id = ? and plan_id = ? and is_deleted = 'N' ";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId, planId });
		
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
	}
	
	private static final class OrderMapper implements RowMapper<Long>{
		
		@Override
		public Long mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			
			final Long orderId=resultSet.getLong("orderId");
			
			return orderId;
		}
		
	}

}
