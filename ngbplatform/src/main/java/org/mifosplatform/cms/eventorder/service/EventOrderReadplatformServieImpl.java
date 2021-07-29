package org.mifosplatform.cms.eventorder.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.joda.time.LocalDate;
import org.mifosplatform.cms.eventmaster.data.EventMasterData;
import org.mifosplatform.cms.eventorder.data.EventOrderData;
import org.mifosplatform.cms.eventorder.data.EventOrderDeviceData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Service;

@Service
public class EventOrderReadplatformServieImpl implements EventOrderReadplatformServie {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	  private CustomValidationProcedure myStoredProcedure; 

	@Autowired
	public EventOrderReadplatformServieImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		 this. myStoredProcedure=new CustomValidationProcedure(this.jdbcTemplate.getDataSource());

	}

	

	@Override
	public List<OneTimeSaleData> retrieveEventOrderData(Long clientId) {
		EventOrderDataMapper mapper = new EventOrderDataMapper();

		String sql = "select " + mapper.schema()
				+ " and  e.client_id = ?  group by e.id";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	private static final class EventOrderDataMapper implements
			RowMapper<OneTimeSaleData> {

		public String schema() {
			return "  e.id AS orderid,e.client_id AS clientId,e.event_id AS eventId,e.eventprice_id AS eventpriceId,0 AS movieLink,e.booked_price AS bookedPrice," +
					" e.charge_code AS chargeCode,e.is_invoiced AS isInvoiced,c.charge_type as chargeType, c.tax_inclusive as  taxInclusive," +
					" p.discount_id as discountId,p.currencyId FROM b_modorder e, b_charge_codes c,b_mod_pricing p  where e.charge_code = c.charge_code " +
					" and p.id=e.eventprice_id and e.is_invoiced = 'N' ";

		}

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long orderid = rs.getLong("orderid");
			Long clientId = rs.getLong("clientId");
			String chargeCode = rs.getString("chargeCode");
			BigDecimal bookedPrice = rs.getBigDecimal("bookedPrice");
			String isInvoiced = rs.getString("isInvoiced");
			String chargeType = rs.getString("chargeType");
			final Long discountId=rs.getLong("discountId");
			final Integer taxInclusive = rs.getInt("taxInclusive");
			final Long currencyId=rs.getLong("currencyId");
			OneTimeSaleData oneTimeSaleData=new OneTimeSaleData(orderid,clientId, null, chargeCode,chargeType, null,null, bookedPrice,isInvoiced,orderid,discountId,taxInclusive,null,null,null);
			oneTimeSaleData.setCurrencyId(currencyId);
			return oneTimeSaleData;

		}
	}

	@Override
	public boolean CheckClientCustomalidation(Long clientId) {
		 Map<String, Object> string= myStoredProcedure.execute(clientId);
	       
	       return  ((Boolean) string.get("status")).booleanValue();
	     
	}

	@Override
	public List<EventOrderDeviceData> getDevices(Long clientId) {
		final String sql = "select al.serial_no as serialNumber from b_allocation al where client_id=? and al.is_deleted ='N'" +
				" union select oh.serial_number as serialNumber from b_owned_hardware oh where client_id=? and oh.is_deleted ='N'";
		EventOrderDeviceMapper rowMapper = new EventOrderDeviceMapper();
		return this.jdbcTemplate.query(sql,rowMapper,new Object[]{clientId,clientId});
	}
	
	@SuppressWarnings("unused")
	private final class EventOrderDeviceMapper implements RowMapper<EventOrderDeviceData>{
		@Override
		public EventOrderDeviceData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final String serialNumber = rs.getString("serialNumber");
			return new EventOrderDeviceData(serialNumber);
		}
		
	}
	
	
	@Override
	public List<EventMasterData> getEvents() {
		
		final String sql = "select id as id, event_name as eventName, event_description as eventDescription from b_mod_master where status=1";
		EventMasterDataMapper rowMapper = new EventMasterDataMapper();
		return this.jdbcTemplate.query(sql,rowMapper);
	}
	
	private final class EventMasterDataMapper implements RowMapper<EventMasterData>{
		@Override
		public EventMasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String eventName = rs.getString("eventName");
			final String eventDescription = rs.getString("eventDescription");
			return new EventMasterData(id,eventName,eventDescription);
		}
	}
	
	
	@Override
	public BigDecimal retriveEventPrice(String fType, String oType, Long clientId) {
		final String sql = "select price from b_mod_pricing where format_type=? and Opt_type=? and client_typeid=(select category_type from m_client where m_client.id = ?) and is_deleted='n' limit 1";
		return jdbcTemplate.queryForObject(sql, new Object[]{fType,oType,clientId},BigDecimal.class);
	}
	
	@Override
	public Long getCurrentRow(String fType, String oType, Long clientId){
		final String sql = "select id from b_mod_pricing where format_type=? and Opt_type=? and client_typeid=(select category_type from m_client where m_client.id = ?) and is_deleted='n' limit 1";
		return jdbcTemplate.queryForLong(sql, new Object[]{fType,oType,clientId});
	}
	
	@Override
	public List<EventOrderData> getTheClientEventOrders(Long clientId) {
		
		try{
		EventOrderMapper mapper = new EventOrderMapper();
		String sql = "select " + mapper.schema()
				+ " and eo.client_id = ? ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}catch(EmptyResultDataAccessException accessException){
	           return null;	
	}
	}

	private static final class EventOrderMapper implements
			RowMapper<EventOrderData> {

		public String schema() {

			return " eo.id AS id,eo.event_bookeddate AS bookedDate,em.event_name AS eventName,eo.charge_code AS chargeCode,em.status as status,eo.booked_price AS price,em.event_description as eventdescription" +
					" FROM b_modorder eo, b_mod_master em WHERE eo.event_id = em.id ";

		}

		@Override
		public EventOrderData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long orderid = rs.getLong("id");
			LocalDate bookedDate = JdbcSupport.getLocalDate(rs,"bookedDate");
			String eventName = rs.getString("eventName");
			BigDecimal bookedPrice = rs.getBigDecimal("price");
			String chargeCode = rs.getString("chargeCode");
			int statusId = rs.getInt("status");
			EnumOptionData Enumstatus=OrderStatusEnumaration.OrderStatusType(statusId);
			String status=Enumstatus.getValue();
			String eventdescription = rs.getString("eventdescription");
			return new EventOrderData(orderid,bookedDate,eventName,bookedPrice,chargeCode,status,eventdescription);

		}
		}
}
@SuppressWarnings("unused")
final class CustomValidationProcedure extends StoredProcedure {
        
  final static String SQL ="custom_validate_eventorders";//.getProcedureName();

	public CustomValidationProcedure(DataSource dataSource) {
		 super(dataSource,SQL);  
		 declareParameter( new SqlParameter( "clientid", Types.INTEGER) ); //declaring sql in parameter to pass input   
	      declareParameter( new SqlOutParameter( "status", Types.BOOLEAN ) ); //declaring sql out parameter   
	      compile();     
        }
		
			@SuppressWarnings("rawtypes")
		public Map execute(int emp_id) {
				//MyStoredProcedure myStoredProcedure;
				  Map<String, Object> results = super.execute(emp_id);   
				   return (Map) results.get("name"); 
    }
			}
