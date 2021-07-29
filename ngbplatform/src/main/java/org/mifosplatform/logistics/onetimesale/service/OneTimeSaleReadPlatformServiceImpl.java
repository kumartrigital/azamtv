package org.mifosplatform.logistics.onetimesale.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * @author hugo
 *
 */
@Service
public class OneTimeSaleReadPlatformServiceImpl implements	OneTimeSaleReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public OneTimeSaleReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	/* (non-Javadoc)
	 * @see #retrieveItemData()
	 */
	@Override
	public List<ItemData> retrieveItemData() {
		
		 this.context.authenticatedUser();
		 final ItemMapper mapper = new ItemMapper();
		 final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class ItemMapper implements RowMapper<ItemData> {

		public String schema() {
			
			return "i.id AS id,i.item_code AS itemCode,i.units AS units,i.unit_price AS unitPrice,(select enum_value from r_enum_value where enum_name = 'item_class' and enum_id = i.item_class) as itemClassName " +
					" FROM b_item_master i  where i.is_deleted='N'";
		}

		@Override
		public ItemData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String itemCode = rs.getString("itemCode");
			final String units = rs.getString("units");
			final BigDecimal unitPrice = rs.getBigDecimal("unitPrice");
			final String itemClassName = rs.getString("itemClassName");
			return new ItemData(id, itemCode,units,unitPrice,itemClassName);
		}
	}

	/* (non-Javadoc)
	 * @see #retrieveClientOneTimeSalesData(java.lang.Long)
	 */
	@Override
	public List<OneTimeSaleData> retrieveClientOneTimeSalesData(final Long clientId) {
		
		this.context.authenticatedUser();
		final SalesDataMapper mapper = new SalesDataMapper();
		final String sql = "select " + mapper.schema()
		          + " where a.is_deleted='N' and a.client_id = ? ";
		    
				//+ " where o.item_id=i.id  and o.client_id=? and o.device_mode = 'NEWSALE' ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	private static final class SalesDataMapper implements RowMapper<OneTimeSaleData> {

		public String schema() {
			
			/*return "o.id AS id,i.id AS itemId, i.item_code AS itemCode, i.item_class as itemClass, a.serial_no as serialNo,o.sale_date as saleDate,o.charge_code AS chargeCode," +
					" pdm.property_code as propertyCode,o.quantity as quantity,o.total_price as totalPrice,o.hardware_allocated as hardwareAllocated,o.units as units,o.device_mode as saleType,id.warranty_date as warrantyDate, "
					+ " id.id as itemDetailId,id.provisioning_serialno as provserialnumber,id.quality as quality,s.service_code as serviceCode,s.service_description as serviceDescription,o.client_service_id as clientServiceId, "
					+ " if(id.is_pairing ='Y',(select id from b_item_master where id=id.paired_item_id),null) as pairedItemId,if(id.is_pairing ='Y',(select item_code from b_item_master where id=id.paired_item_id),null) as pairedItemCode FROM b_item_master i JOIN b_onetime_sale o ON i.id=o.item_id and o.is_deleted='N' " +
					" LEFT JOIN b_allocation a ON a.order_id=o.id and a.is_deleted = 'N' " +
					" LEFT JOIN b_propertydevice_mapping pdm ON pdm.serial_number = a.serial_no" +
					" LEFT JOIN b_item_detail id ON id.serial_no = a.serial_no AND id.is_deleted = 'N' " +
					" JOIN b_client_service cs on o.client_service_id = cs.id and cs.is_deleted = 'N' " +
					" JOIN b_service s On cs.service_id = s.id and s.is_deleted = 'N' ";*/
			return "a.order_id AS id,i.id AS itemId,i.item_code AS itemCode,i.item_class as itemClass,a.serial_no as serialNo,"+
				   "a.allocation_date as saleDate, 'Plan' AS chargeCode,pdm.property_code as propertyCode, 1 as quantity,0 as totalPrice,"+
				   "a.status as hardwareAllocated,i.units as units,a.order_type as saleType,id.warranty_date as warrantyDate,id.id as itemDetailId,"+
				   "id.provisioning_serialno as provserialnumber,id.quality as quality,s.service_code as serviceCode,s.service_description as serviceDescription,"+
				   "a.clientservice_id as clientServiceId, if(id.is_pairing = 'Y',(select id from b_item_master where id = id.paired_item_id),null) as pairedItemId,"+
				   "if(id.is_pairing = 'Y',(select  item_code from b_item_master where id = id.paired_item_id), null) as pairedItemCode, cs.status as clientServiceStatus,"+
				   "(select e.enum_value from r_enum_value e where e.enum_id =i.item_class and e.enum_name = 'item_class') AS itemClassName FROM b_allocation a "+
				   "LEFT JOIN b_item_master i ON i.id = a.item_master_id and a.is_deleted = 'N'"+
				   "LEFT JOIN b_propertydevice_mapping pdm ON pdm.serial_number = a.serial_no "+
				   "LEFT JOIN b_item_detail id ON id.serial_no = a.serial_no AND id.is_deleted = 'N' "+
				   "JOIN b_client_service cs ON a.clientservice_id = cs.id and cs.is_deleted = 'N' "+
				   "JOIN b_service s ON cs.service_id = s.id and s.is_deleted = 'N' ";
		}

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum)throws SQLException {

			final Long id = rs.getLong("id");
			final LocalDate saleDate = JdbcSupport.getLocalDate(rs, "saleDate");
			final String itemCode = rs.getString("itemCode");
			final String chargeCode = rs.getString("chargeCode");
			final String quantity = rs.getString("quantity");
			final BigDecimal totalPrice = rs.getBigDecimal("totalPrice");
			final String haardwareAllocated = rs.getString("hardwareAllocated");
			final String itemClass = rs.getString("itemClass");
			final String serialNo = rs.getString("serialNo");
			final String units = rs.getString("units");
			final String propertyCode =rs.getString("propertyCode");
			final String saleType = rs.getString("saleType");
			final LocalDate warrantyDate = JdbcSupport.getLocalDate(rs, "warrantyDate");
			final Long itemDetailId = rs.getLong("itemDetailId");
			final String provserialnumber = rs.getString("provserialnumber");
			final String quality = rs.getString("quality");
			final String pairedItemCode = rs.getString("pairedItemCode");
			final String serviceCode = rs.getString("serviceCode");
			final String serviceDescription = rs.getString("serviceDescription");
			final Long clientServiceId = rs.getLong("clientServiceId");
			final Long itemId = rs.getLong("itemId");
			final Long pairedItemId = JdbcSupport.getLong(rs,"pairedItemId");
			final String clientServiceStatus = rs.getString("clientServiceStatus");
			final String itemClassName = rs.getString("itemClassName");
			OneTimeSaleData saleData = new OneTimeSaleData(id, saleDate, itemCode, chargeCode,quantity, totalPrice,haardwareAllocated,itemClass,serialNo,units,saleType,warrantyDate,
			propertyCode,itemDetailId,provserialnumber,quality,serviceCode,serviceDescription,pairedItemCode,itemId,pairedItemId,itemClassName,null);
			saleData.setClientServiceId(clientServiceId);
			saleData.setClientServiceStatus(clientServiceStatus);
			return saleData;

		}

	}
	
	/* (non-Javadoc)
	 * @see #retrieveClientOneTimeSalesData(java.lang.Long)
	 */
	@Override
	public List<OneTimeSaleData> retrieveClientOneTimeSalesDataForActivation(final Long clientId) {
		
		//this.context.authenticatedUser();
		final SalesDataMappers mapper = new SalesDataMappers();
		final String sql = "select " + mapper.schema()
		          + " where a.client_id = ? ";
		    
				//+ " where o.item_id=i.id  and o.client_id=? and o.device_mode = 'NEWSALE' ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	private static final class SalesDataMappers implements RowMapper<OneTimeSaleData> {

		public String schema() {
			
			/*return "o.id AS id,i.id AS itemId, i.item_code AS itemCode, i.item_class as itemClass, a.serial_no as serialNo,o.sale_date as saleDate,o.charge_code AS chargeCode," +
					" pdm.property_code as propertyCode,o.quantity as quantity,o.total_price as totalPrice,o.hardware_allocated as hardwareAllocated,o.units as units,o.device_mode as saleType,id.warranty_date as warrantyDate, "
					+ " id.id as itemDetailId,id.provisioning_serialno as provserialnumber,id.quality as quality,s.service_code as serviceCode,s.service_description as serviceDescription,o.client_service_id as clientServiceId, "
					+ " if(id.is_pairing ='Y',(select id from b_item_master where id=id.paired_item_id),null) as pairedItemId,if(id.is_pairing ='Y',(select item_code from b_item_master where id=id.paired_item_id),null) as pairedItemCode FROM b_item_master i JOIN b_onetime_sale o ON i.id=o.item_id and o.is_deleted='N' " +
					" LEFT JOIN b_allocation a ON a.order_id=o.id and a.is_deleted = 'N' " +
					" LEFT JOIN b_propertydevice_mapping pdm ON pdm.serial_number = a.serial_no" +
					" LEFT JOIN b_item_detail id ON id.serial_no = a.serial_no AND id.is_deleted = 'N' " +
					" JOIN b_client_service cs on o.client_service_id = cs.id and cs.is_deleted = 'N' " +
					" JOIN b_service s On cs.service_id = s.id and s.is_deleted = 'N' ";*/
			return "a.id AS id,i.id AS itemId,i.item_code AS itemCode,i.item_class as itemClass,a.serial_no as serialNo,"+
				   "a.allocation_date as saleDate, 'Plan' AS chargeCode,pdm.property_code as propertyCode, 1 as quantity,0 as totalPrice,"+
				   "a.status as hardwareAllocated,i.units as units,a.order_type as saleType,id.warranty_date as warrantyDate,id.id as itemDetailId,"+
				   "id.provisioning_serialno as provserialnumber,id.quality as quality,s.service_code as serviceCode,s.service_description as serviceDescription,"+
				   "a.clientservice_id as clientServiceId, if(id.is_pairing = 'Y',(select id from b_item_master where id = id.paired_item_id),null) as pairedItemId,"+
				   "if(id.is_pairing = 'Y',(select  item_code from b_item_master where id = id.paired_item_id), null) as pairedItemCode," +
				   "(select e.enum_value from r_enum_value e where e.enum_id =i.item_class and e.enum_name = 'item_class') AS itemClassName FROM b_allocation a "+
				   "LEFT JOIN b_item_master i ON i.id = a.item_master_id and a.is_deleted = 'N'"+
				   "LEFT JOIN b_propertydevice_mapping pdm ON pdm.serial_number = a.serial_no "+
				   "LEFT JOIN b_item_detail id ON id.serial_no = a.serial_no AND id.is_deleted = 'N' "+
				   "JOIN b_client_service cs ON a.clientservice_id = cs.id and cs.is_deleted = 'N' "+
				   "JOIN b_service s ON cs.service_id = s.id and s.is_deleted = 'N' ";
		}

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum)throws SQLException {

			final Long id = rs.getLong("id");
			final LocalDate saleDate = JdbcSupport.getLocalDate(rs, "saleDate");
			final String itemCode = rs.getString("itemCode");
			final String chargeCode = rs.getString("chargeCode");
			final String quantity = rs.getString("quantity");
			final BigDecimal totalPrice = rs.getBigDecimal("totalPrice");
			final String haardwareAllocated = rs.getString("hardwareAllocated");
			final String itemClass = rs.getString("itemClass");
			final String serialNo = rs.getString("serialNo");
			final String units = rs.getString("units");
			final String propertyCode =rs.getString("propertyCode");
			final String saleType = rs.getString("saleType");
			final LocalDate warrantyDate = JdbcSupport.getLocalDate(rs, "warrantyDate");
			final Long itemDetailId = rs.getLong("itemDetailId");
			final String provserialnumber = rs.getString("provserialnumber");
			final String quality = rs.getString("quality");
			final String pairedItemCode = rs.getString("pairedItemCode");
			final String serviceCode = rs.getString("serviceCode");
			final String serviceDescription = rs.getString("serviceDescription");
			final Long clientServiceId = rs.getLong("clientServiceId");
			final Long itemId = rs.getLong("itemId");
			final Long pairedItemId = JdbcSupport.getLong(rs,"pairedItemId");
			final String itemClassName = rs.getString("itemClassName");
			
			OneTimeSaleData saleData = new OneTimeSaleData(id, saleDate, itemCode, chargeCode,quantity, totalPrice,haardwareAllocated,itemClass,serialNo,units,saleType,warrantyDate,
					propertyCode,itemDetailId,provserialnumber,quality,serviceCode,serviceDescription,pairedItemCode,itemId,pairedItemId,itemClassName,null);
			saleData.setClientServiceId(clientServiceId);
			
			return saleData;

		}

	}
	
	/* (non-Javadoc)
	 * @see #retrieveOnetimeSalesForInvoice(java.lang.Long)
	 */
	@Override
	public List<OneTimeSaleData> retrieveOnetimeSalesForInvoice(final Long clientId) {
		
		this.context.authenticatedUser();
		final OneTimeSalesDataMapper mapper = new OneTimeSalesDataMapper();
	    final String sql = "select " + mapper.schema() + " and ots.is_invoiced='N' and im.id=ots.item_id  and ots.client_id = ? ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	private static final class OneTimeSalesDataMapper implements RowMapper<OneTimeSaleData> {

		public String schema() {
			return " ots.id as oneTimeSaleId, ots.client_id AS clientId,ots.units AS units,ots.charge_code AS chargeCode,ots.unit_price AS unitPrice,"+
				   " ots.quantity AS quantity,ots.total_price AS totalPrice,ots.is_invoiced as isInvoiced,ots.item_id as itemId,ots.discount_id as discountId,ots.office_id as officeId, "+
				   " cc.tax_inclusive as taxInclusive,cc.charge_type as chargeType,ots.client_service_id As clientServiceId,ots.resource_id As currencyId FROM b_onetime_sale ots,b_charge_codes cc,b_item_master im where ots.charge_code=cc.charge_code ";

		}

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum)throws SQLException {

			final Long oneTimeSaleId = rs.getLong("oneTimeSaleId");
			final Long clientId = rs.getLong("clientId");
			final String units = rs.getString("units");
			final String chargeCode = rs.getString("chargeCode");
			final BigDecimal unitPrice = rs.getBigDecimal("unitPrice");
			final String quantity = rs.getString("quantity");
			final BigDecimal totalPrice = rs.getBigDecimal("totalPrice");
			final String isInvoiced = rs.getString("isInvoiced");
			final Long itemId = rs.getLong("itemId");
			final Long discountId=rs.getLong("discountId");
			final Integer taxInclusive = rs.getInt("taxInclusive");
			final String chargeType = rs.getString("chargeType");
			final Long clientServiceId = rs.getLong("clientServiceId");
			final Long currencyId = rs.getLong("currencyId");
			final Long officeId = rs.getLong("officeId");
			OneTimeSaleData oneTimeSaleData =  new OneTimeSaleData(oneTimeSaleId,clientId, units, chargeCode,chargeType, unitPrice,quantity, totalPrice,isInvoiced,
					                    itemId,discountId,taxInclusive,clientServiceId,currencyId,null);
			
			oneTimeSaleData.setOfficeId(officeId);
			return oneTimeSaleData;
		}

	}

	/* (non-Javadoc)
	 * @see #retrieveSingleOneTimeSaleDetails(java.lang.Long)
	 */
	@Override
	public OneTimeSaleData retrieveSingleOneTimeSaleDetails(final Long saleId) {
		
		this.context.authenticatedUser();
		final OneTimeSalesDataMapper mapper = new OneTimeSalesDataMapper();
		final String sql = "select " + mapper.schema() + " where ots.id = ? ";

		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { saleId });
	}

	/* (non-Javadoc)
	 * @see #retrieveAllocationDetails(java.lang.Long)
	 */
	@Override
	public List<AllocationDetailsData> retrieveAllocationDetails(final Long orderId) {
		
		this.context.authenticatedUser();
		final AllocationDataMapper mapper = new AllocationDataMapper();
		final String sql = "select " + mapper.schema()+ " and a.order_id=? ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { orderId });
	}

	private static final class AllocationDataMapper implements	RowMapper<AllocationDetailsData> {

		public String schema() {
			return "  a.id AS id,id.id AS itemDetailId,id.quality as quality,a.status as hardwareStatus,i.item_description AS itemDescription,a.serial_no AS serialNo,a.allocation_date AS allocationDate" +
					" FROM b_allocation a, b_item_master i, b_item_detail id WHERE  a.item_master_id = i.id   AND a.is_deleted = 'N' and id.client_id = a.client_id " +
					"  and id.serial_no = a.serial_no ";

		}
		
		public String schemaForUnallocated(Long clientId) {
			return "  a.id AS id,id.id AS itemDetailId,id.quality as quality,a.status as hardwareStatus,"+
					" i.item_description AS itemDescription,a.serial_no AS serialNo,a.allocation_date AS allocationDate "+
					" FROM b_allocation a, b_item_master i, b_item_detail id "+
					" WHERE  a.item_master_id = i.id "+
					" and a.client_id = "+clientId+" and id.serial_no = a.serial_no ";

		}

		@Override
		public AllocationDetailsData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long itemDetailId = rs.getLong("itemDetailId");
			final String itemDescription = rs.getString("itemDescription");
            final String serialNo = rs.getString("serialNo");
		    final LocalDate allocationDate=JdbcSupport.getLocalDate(rs,"allocationDate");
		    final String quality = rs.getString("quality");
		    final String hardwareStatus = rs.getString("hardwareStatus");
		    
			return new AllocationDetailsData(id,itemDescription,serialNo,allocationDate,itemDetailId,null,quality,hardwareStatus);

		}
	}

	/* (non-Javadoc)
	 * @see #retrieveAllocationDetailsBySerialNo(java.lang.String)
	 */
	@Override
	public AllocationDetailsData retrieveAllocationDetailsBySerialNo(final String serialNo) {
 
		try{
			
		final AllocationDataMapper mapper = new AllocationDataMapper();
		final String sql = "select " + mapper.schema()+ " and a.serial_no=?";

		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { serialNo });
		
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
	
	}
	
	@Override
	public List<AllocationDetailsData> retrieveUnAllocationDetails(final Long orderId, final Long clientId) {
		
		this.context.authenticatedUser();
		final AllocationDataMapper mapper = new AllocationDataMapper();
		final String sql = "select " + mapper.schemaForUnallocated(clientId)+ " and a.order_id=? ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] { orderId });
	}

	@Override
	public List<OneTimeSaleData> retrivePairedItemOfaItem(final String serialNo) {
		
		this.context.authenticatedUser();
		final OneTimeSaleDataMapper mapper = new OneTimeSaleDataMapper();
		 String sql = "select " + mapper.schema() + " where ip.serial_no_1 =? and ip.is_deleted = 'N' ";
		List<OneTimeSaleData> oneTimeSalesData = this.jdbcTemplate.query(sql, mapper, new Object[] { serialNo });
		//return this.jdbcTemplate.query(sql, mapper, new Object[] { serialNo });
		if(oneTimeSalesData.isEmpty())
		{
			sql="select " + mapper.schemaForPaired() + " where ip.serial_no_2 =? and ip.is_deleted = 'N' ";
			oneTimeSalesData = this.jdbcTemplate.query(sql, mapper, new Object[] { serialNo });
		}
		return oneTimeSalesData;
	}
	
	private class OneTimeSaleDataMapper implements RowMapper<OneTimeSaleData> {

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final String itemCode = rs.getString("itemCode");
			final String serialNo = rs.getString("serialNo");
			return new OneTimeSaleData(itemCode,serialNo) ;
			
		}
		
		public String schema() {
			
			return " ip.item_type_2 as itemCode,ip.serial_no_2 as serialNo "+
				   " from b_item_pairing ip ";
			
			
		}
		public String schemaForPaired() {
			
			return " ip.item_type_1 as itemCode,ip.serial_no_1 as serialNo "+
				   " from b_item_pairing ip ";
			
			
		}
		
		
	}
	
	@Override
	public List<OneTimeSaleData> retrieveItemDetailsForInvoice(final Long clientId,final String serialNo) {
		this.context.authenticatedUser();
		final ItemDetailsDataMapper mapper = new ItemDetailsDataMapper();
	    final String sql = "select distinct " + mapper.schema() + " and ba.client_id = ? and ba.item_master_id = bim.id and  bid.client_id = ba.client_id and ba.serial_no = '"+serialNo+"' ";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId});
	}
	
	private static final class ItemDetailsDataMapper implements RowMapper<OneTimeSaleData> {

		public String schema() {
			return " ba.id as oneTimeSaleId,ba.client_id AS clientId, bim.units AS units,bim.charge_code AS chargeCode,"+
				   " bim.unit_price AS unitPrice,bim.unit_price AS totalBasePrice,bim.id as itemId,cc.tax_inclusive as taxInclusive,"+
                   " ba.clientservice_id as clientServiceId,bim.currency_id as baseCurrencyId,"+
				   " cc.charge_type as chargeType FROM b_charge_codes cc,b_allocation ba,b_item_master bim,b_item_detail bid where bim.charge_code = cc.charge_code ";
		}

		@Override
		public OneTimeSaleData mapRow(ResultSet rs, int rowNum)throws SQLException {

			final Long oneTimeSaleId = rs.getLong("oneTimeSaleId");
			final Long clientId = rs.getLong("clientId");
			final String units = rs.getString("units");
			final String chargeCode = rs.getString("chargeCode");
			final BigDecimal unitPrice = rs.getBigDecimal("unitPrice");
			final BigDecimal totalBasePrice = rs.getBigDecimal("totalBasePrice");
			final Long itemId = rs.getLong("itemId");
			final Integer taxInclusive = rs.getInt("taxInclusive");
			final String chargeType = rs.getString("chargeType");
			final Long clientServiceId = rs.getLong("clientServiceId");
			final String baseCurrencyId = rs.getString("baseCurrencyId");
			return new OneTimeSaleData(oneTimeSaleId,clientId, units, chargeCode,chargeType, unitPrice,totalBasePrice,
					                    itemId,taxInclusive,clientServiceId,baseCurrencyId,null);

		}

	}
	
	}

