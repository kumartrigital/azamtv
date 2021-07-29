package org.mifosplatform.portfolio.order.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.data.OrderAddonsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderAddOnsReadPlaformServiceImpl implements OrderAddOnsReadPlaformService {
	
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	
@Autowired
public OrderAddOnsReadPlaformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource){
	
	this.jdbcTemplate=new JdbcTemplate(dataSource);
	this.context=context;
}

	

@Override
public List<OrderAddonsData> retrieveAllOrderAddons(Long orderId) {

	try{
		this.context.authenticatedUser();
		final OrderAddonMapper mapper =new OrderAddonMapper(); 
		final String sql="select "+mapper.schema();
		
		return this.jdbcTemplate.query(sql, mapper,new Object[]{orderId});
		
	}catch(EmptyResultDataAccessException dve){
		return null;
	}
		
	}

private class OrderAddonMapper implements RowMapper<OrderAddonsData>{

	public String schema() {
	
		return " ad.id as id,ad.service_id as serviceId,s.service_code as serviceCode,ad.start_date as startDate, ad.end_date as endDate," +
				" ad.status as status,op.price as price FROM b_orders_addons ad, b_service s, b_order_price op " +
				" WHERE ad.service_id =s.id and op.service_id = s.id and ad.order_id=op.order_id and ad.order_id =? and ad.is_deleted = 'N' group by ad.id;";
	}
	
	@Override
	public OrderAddonsData mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		final Long id =rs.getLong("id");
		final Long serviceId =rs.getLong("serviceId");
		final String serviceCode = rs.getString("serviceCode");
		final LocalDate startDate=JdbcSupport.getLocalDate(rs,"startDate");
		final LocalDate endDate=JdbcSupport.getLocalDate(rs,"endDate");
		final String statu=rs.getString("status");
		final BigDecimal price=rs.getBigDecimal("price");
		return new OrderAddonsData(id,serviceId,serviceCode,startDate,endDate,statu,price);
	}

	
}

}

