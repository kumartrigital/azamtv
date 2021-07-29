package org.mifosplatform.provisioning.entitlements.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.provisioning.entitlements.data.ClientEntitlementData;
import org.mifosplatform.provisioning.entitlements.data.EntitlementsData;
import org.mifosplatform.provisioning.entitlements.data.StakerData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningData;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class EntitlementReadPlatformServiceImpl implements
		EntitlementReadPlatformService {

	private final static Logger logger = LoggerFactory.getLogger(EntitlementReadPlatformServiceImpl.class);
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<EntitlementsData> paginationHelper = new PaginationHelper<EntitlementsData>();
	private final PlatformSecurityContext context;

	

	@Autowired
	public EntitlementReadPlatformServiceImpl(
			final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	
	@Override

	public List<EntitlementsData> getProcessingData(Long id,String provisioningSys,String serviceType) {
		// TODO Auto-generated method stub
		String sql = "";
		ServicesMapper mapper = new ServicesMapper();		
		sql = "select " + mapper.schema();		
		if(provisioningSys != null){
			sql = sql + " and p.provisioning_system = '" + provisioningSys + "' order by p.id ";
		
		}/*if(serviceType != null){
			sql = sql + " and pr.service_type = '" + serviceType + "' ";
		}*/
		
		if (id != null) {
			sql = sql + " limit " + id;
		} 				
		List<EntitlementsData> detailsDatas = jdbcTemplate.query(sql, mapper,new Object[] {});
		return detailsDatas;
	}


	protected static final class ServicesMapper implements
			RowMapper<EntitlementsData> {

		@Override
		public EntitlementsData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			Long id = rs.getLong("id");
			Long version =rs.getLong("version");
			Long prdetailsId = rs.getLong("prDetailsId");
			String requestType = rs.getString("requestType");
			String requestMessage = rs.getString("requestMessage");
		    Long taskNo = rs.getLong("taskNo");
		    Long priority = rs.getLong("priority");
			return new EntitlementsData(id,version,prdetailsId, requestType, requestMessage, taskNo, priority);


		}

		public String schema() {

			/*return " p.id AS id,p.client_id AS clientId,p.provisioing_system AS provisioingSystem,pr.id AS prdetailsId,pr.service_type as servicetype," +
					" pr.sent_message AS sentMessage,pr.hardware_id AS hardwareId,pr.request_type AS requestType,o.plan_id AS planId,o.order_no AS orderNo," +
					" o.id as orderId,o.start_date as startDate,o.end_date as endDate, c.account_no as accountNo, c.email as email," +
					" c.firstname as firstName,c.lastname as lastName,c.office_id as officeId, u.username as username,u.password as userPassword," +
					" ifnull(c.fullname, c.firstname) as displayName, ifnull(c.login,c.id) as login, ifnull(c.password,'0000') as password" +
					" FROM b_process_request_detail pr, b_process_request p " +
					" LEFT JOIN b_orders o ON o.id = p.order_id LEFT JOIN m_client c ON c.id = p.client_id" +
					" LEFT JOIN b_clientuser u ON u.client_id = p.client_id WHERE p.id = pr.processrequest_id AND p.is_processed = 'N'";*/

			/*return " p.id AS id, p.client_id AS clientId, p.provisioning_system AS provisioingSystem, pr.id AS prdetailsId,pr.response_status as taskNo,"+
				   " pr.request_message AS sentMessage, p.request_type AS requestType, o.plan_id AS planId, o.order_no AS orderNo,"+
				   " o.id as orderId, o.start_date as startDate, o.end_date as endDate, c.account_no as accountNo, c.email as email,"+
				   " c.firstname as firstName, c.lastname as lastName, c.office_id as officeId, u.username as username, "+
				   " u.password as userPassword,ifnull(c.fullname, c.firstname) as displayName, ifnull(c.login, c.id) as login,"+
				   " ifnull(c.password, '0000') as password FROM b_provisioning_request_detail pr, "+
				   " b_provisioning_request p LEFT JOIN b_orders o ON o.id = p.order_id "+
				   " LEFT JOIN m_client c ON c.id = p.client_id LEFT JOIN b_clientuser u ON u.client_id = p.client_id "+
				   " WHERE p.id = pr.provisioning_req_id AND p.status = 'N' ";*/
			return " p.id as id,p.version as version,pr.id as prDetailsId,p.request_type as requestType,pr.request_message as requestMessage, pr.response_status as taskNo,p.priority as priority from "
				 + " b_provisioning_request p JOIN b_provisioning_request_detail pr on p.id = pr.provisioning_req_id AND p.status = 'N' ";
		}

	}

	@Override
	public ClientEntitlementData getClientData(Long clientId) {
		// TODO Auto-generated method stub
		
	    ClientMapper mapper=new ClientMapper();
	    String sql="Select "+ mapper.schema();
	    
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {clientId});
	}
	
	protected static final class ClientMapper implements
			RowMapper<ClientEntitlementData> {
		
		@Override
		public ClientEntitlementData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			String emailId = rs.getString("EmailId");
			String firstName = rs.getString("firstName");
			String lastName = rs.getString("lastName");
			String selfcareUsername = rs.getString("selfcareUsername");
		    String selfcarePassword = rs.getString("selfcarePassword");
		    
			return new ClientEntitlementData(emailId, firstName, lastName, selfcareUsername, selfcarePassword);
		}
		
		public String schema() {
			/*return " c.email as EmailId, c.display_name as fullName, ifnull(c.login,c.id) as login, " +
					" ifnull(c.password,'0000') as password, " +
					" sc.unique_reference as selfcareUsername, sc.password as selfcarePassword from m_client c " +
					" LEFT JOIN b_clientuser sc ON sc.client_id = c.id where c.id=?";*/
			
			return " c.email as EmailId, c.firstname as firstName,c.lastname as lastName," +
					" sc.unique_reference as selfcareUsername, sc.password as selfcarePassword " +
					" from m_client c LEFT JOIN b_clientuser sc ON sc.client_id = c.id where c.id=?";

		}
		
		}

	@Override
	public StakerData getData(String macAddress) {
		try{		
			logger.info("Staker Get method called");
			StakerMapper mapper = new StakerMapper();

			String sql = "select " + mapper.schema();

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {macAddress});
			}catch (final EmptyResultDataAccessException e) {
				logger.error("EmptyResultDataAccessException : "+e.getMessage());
				return null;
			}
	}
	
	protected static final class StakerMapper implements RowMapper<StakerData> {

			@Override
			public StakerData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
				
			     String mac=rs.getString("mac");
			     //Long Ls1=rs.getLong("Ls");
			     String status=rs.getString("status");
			     String fname=rs.getString("fname");
			     String phone=rs.getString("phone");
			     String end_date=rs.getString("end_date");
			     String tariff=rs.getString("tariff");
			     Long Ls=new Long(12345);
			     logger.info("Retrieving the data is: mac= "+mac+" ,ls= "+Ls+" ,status= "+status+" ,fname= "+fname+" ,phone= "+phone+" ,end_date= "+end_date+" ,tariff= "+tariff);
				return new StakerData(mac,Ls,status,fname,phone,end_date,tariff);
			
			}
			
			public String schema() {
				/*return " DISTINCT a.serial_no AS mac,a.client_id AS ls,o.order_status as status,c.firstname as fname,c.phone as phone," +
						" o.end_date as end_date, 'Ellinika' as tariff FROM b_allocation a, m_client c,b_plan_master pm,b_orders o," +
						" b_item_detail i WHERE a.client_id = c.id AND c.id= o.client_id" +
						" AND o.plan_id=pm.id AND a.serial_no=i.serial_no group by ls and a.serial_no=?";*/
				return "DISTINCT i.provisioning_serialno AS mac,i.client_id AS ls,o.order_status as status,c.firstname as fname," +
						" c.phone as phone,o.end_date as end_date, pm.plan_description as tariff FROM b_allocation a,m_client c,b_plan_master pm," +
						" b_orders o,b_item_detail i WHERE i.client_id = c.id AND c.id= o.client_id and i.serial_no=a.serial_no " +
						" AND o.plan_id=pm.id AND o.order_status =1 AND i.provisioning_serialno=? group by ls";
				
			}

}

	@Override
	public List<EntitlementsData> getBeeniusProcessingData(Long id,String provisioningSystem) {
		
		String sql = "";
		BeeniusServicesMapper mapper = new BeeniusServicesMapper();
		sql = "select " + mapper.schema();
		if (provisioningSystem != null) {
			sql = sql + " and bpr.provisioing_system = '" + provisioningSystem + "' ";
		}
		
		sql = sql + "group by bpr.id"; 
		if (id != null) {
			sql = sql + " limit " + id;
		}
		
		

		return jdbcTemplate.query(sql, mapper, new Object[] {});
		
	}
	
	protected static final class BeeniusServicesMapper implements RowMapper<EntitlementsData> {

		@Override
		public EntitlementsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			Long id = rs.getLong("id");
			Long prdetailsId = rs.getLong("prdetailsId");
			String provisioingSystem = rs.getString("provisioingSystem");
		
			String product = rs.getString("sentMessage");
			String macId = rs.getString("macId");
			String deviceId = rs.getString("deviceId");
			
			String requestType = rs.getString("requestType");
			String itemCode = rs.getString("itemCode");
			String itemDescription = rs.getString("itemDescription");
			
			Long clientId = rs.getLong("clientId");
			String accountNo = rs.getString("accountNo");
			String firstName = rs.getString("firstName");
			String lastName = rs.getString("lastName");
			
			String officeUID = rs.getString("officeUID");
			String branch = rs.getString("branch");
			String regionCode = rs.getString("regionCode");
			String regionName = rs.getString("regionName");
			String ipAddress = rs.getString("ipAddress");

			String userName = rs.getString("userName");
			String password = rs.getString("userPassword");
			Long subscriberId = rs.getLong("subscriberId");
			
			Long orderId = rs.getLong("orderId");
			LocalDate startDate=JdbcSupport.getLocalDate(rs, "startDate");
		    LocalDate endDate=JdbcSupport.getLocalDate(rs, "endDate");
			
			return new EntitlementsData(id,prdetailsId,provisioingSystem,null,product,macId,requestType,itemCode,
					       itemDescription,clientId,accountNo,firstName,lastName,officeUID,branch,regionCode,regionName,
					       deviceId,ipAddress,userName,password,subscriberId,orderId,startDate,endDate);
		}

		public String schema() {
		
			return "  bpr.id as id, c.id as clientId, c.account_no as accountNo,c.firstname as firstName,c.lastname as lastName,o.external_id as officeUID,o.name as branch, " +
				   "  bpr.provisioing_system AS provisioingSystem,bprd.id AS prdetailsId,bprd.sent_message AS sentMessage," +
				   "  ifnull(bid.provisioning_serialno ,oh.provisioning_serial_number) AS macId, ifnull(bid.serial_no ,oh.serial_number) AS deviceId," +
				   "  bprd.request_type AS requestType,bipd.ip_address as ipAddress,bim.item_code as itemCode,bim.item_description as itemDescription," +
				   "  bprm.priceregion_code as regionCode, bprm.priceregion_name as regionName, bcu.username as userName,bcu.password as userPassword," +
				   "  bcu.zebra_subscriber_id as subscriberId ,ord.id as orderId,ord.start_date as startDate,ord.end_date as endDate " +
				   "  from m_client c" +
				   "  LEFT JOIN m_office o ON (o.id = c.office_id)" +
				   "  RIGHT JOIN b_process_request bpr ON (c.id = bpr.client_id )" +
				   "  LEFT JOIN b_process_request_detail bprd ON (bpr.id = bprd.processrequest_id )" +
				   "  LEFT JOIN b_client_address bca ON (c.id=bca.client_id and address_key='PRIMARY')" +
				   "  LEFT JOIN b_clientuser  bcu ON (c.id=bcu.client_id )" +
				   "  LEFT JOIN b_state bs ON (bca.state = bs.state_name )" +
				   "  LEFT JOIN b_priceregion_detail bpd ON ((bpd.state_id = bs.id or bpd.state_id=0)  and bpd.country_id=bs.parent_code AND bpd.is_deleted = 'N')" +
				   "  LEFT JOIN b_priceregion_master bprm ON (bpd.priceregion_id = bprm.id )" +
				   "  LEFT JOIN b_ippool_details bipd ON (bpr.client_id = bipd.client_id)" +
				   "  LEFT JOIN b_item_detail bid ON (bprd.hardware_id = bid.provisioning_serialno)" +
				   "  LEFT JOIN b_item_master bim ON (bid.item_master_id = bim.id)" +
				   "  LEFT JOIN b_owned_hardware oh ON (bprd.hardware_id =oh.provisioning_serial_number AND oh.is_deleted = 'N')" +
				   "  LEFT JOIN b_orders ord ON (ord.id = bpr.order_id)" +
				   "  WHERE bpr.is_processed = 'N'";
		}

	}

	@Override
	public List<EntitlementsData> getZebraOTTProcessingData(Long no,
			String provisioningSystem) {

		String sql = "";
		ZebraOTTServicesMapper mapper = new ZebraOTTServicesMapper();

		sql = "select " + mapper.schema();

		if (provisioningSystem != null) {
			sql = sql + " and bpr.provisioing_system = '" + provisioningSystem
					+ "' ";
		}

		sql = sql + "group by bpr.id";
		if (no != null) {
			sql = sql + " limit " + no;
		}

		return jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	protected static final class ZebraOTTServicesMapper implements
			RowMapper<EntitlementsData> {

		@Override
		public EntitlementsData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			String accountNo = rs.getString("accountNo");
			String firstName = rs.getString("firstName");
			String lastName = rs.getString("lastName");
			String email = rs.getString("email");
			String phone = rs.getString("phone");
			
			String city = rs.getString("city");
			String zip = rs.getString("zip");
			String address = rs.getString("address");
			
			String provisioingSystem = rs.getString("provisioingSystem");
			Long serviceId = rs.getLong("serviceId");
			Long prdetailsId = rs.getLong("prdetailsId");
			String product = rs.getString("sentMessage");
			String macId = rs.getString("macId");
			String requestType = rs.getString("requestType");
			
			Long zebraSubscriberId = rs.getLong("zebraSubscriberId");
			BigDecimal itemPrice = rs.getBigDecimal("itemPrice");
			Long itemId = rs.getLong("itemId");
			String itemCode = rs.getString("itemCode");
			String itemDescription = rs.getString("itemDescription");
			
			return new EntitlementsData(id,clientId,accountNo,firstName,lastName,email,phone,city,zip,
					address,provisioingSystem,serviceId,prdetailsId,product,macId,requestType,zebraSubscriberId,
					itemPrice,itemId,itemCode,itemDescription);
		}

		public String schema() {
			
			return " bpr.id as id, c.id as clientId, c.account_no as accountNo,c.firstname as firstName,"
					+ " c.lastname as lastName,c.phone as phone,c.email as email,"
					+ " bca.city as city,bca.zip as zip,bca.address_no as address,"
					+ " bpr.provisioing_system AS provisioingSystem,bprd.service_id AS serviceId,"
					+ " bprd.id AS prdetailsId,bprd.sent_message AS sentMessage,"
					+ " bprd.hardware_id as macId,bprd.request_type AS requestType,"
					+ " bim.id as itemId,bim.item_code as itemCode,bim.item_description as itemDescription,"
					+ " bim.unit_price as itemPrice,bcu.zebra_subscriber_id as zebraSubscriberId"
					+ " from m_client c"
					+ " join b_process_request bpr on (c.id = bpr.client_id )"
					+ " join b_process_request_detail bprd on (bpr.id = bprd.processrequest_id )"
					+ " join b_client_address bca on (c.id=bca.client_id and address_key='PRIMARY')"
					+ " left join b_clientuser bcu on (c.id = bcu.client_id)"
					+ " left join b_item_detail bid on (bprd.hardware_id = bid.provisioning_serialno)"
					+ " left join b_item_master bim on (bid.item_master_id = bim.id)"
					+ " WHERE bpr.is_processed = 'N'" ;
		}

	}

	@Override
	public List<EntitlementsData> getCubiWareProcessingData(Long id, String provisioningSystem) {
		
		CubiWareServicesMapper mapper = new CubiWareServicesMapper();

		String sql = "select " + mapper.schema();
		
		if (provisioningSystem != null) sql = sql + " and bpr.provisioing_system = '" + provisioningSystem + "' ";

		sql = sql + "group by bpr.id";
		
		if (id != null) sql = sql + " limit " + id;
		
		return jdbcTemplate.query(sql, mapper, new Object[] {});

	}
	
	
	protected static final class CubiWareServicesMapper implements RowMapper<EntitlementsData> {

		@Override
		public EntitlementsData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			
			Long clientId = rs.getLong("clientId");
			String firstName = rs.getString("firstName");
			String lastName = rs.getString("lastName");
			String phone = rs.getString("phone");
			String email = rs.getString("email");
			String city = rs.getString("city");
			String zip  = rs.getString("zip");
			String street = rs.getString("street");
			String country = rs.getString("country");
			Long countryId = rs.getLong("countryId");
			
			String provisioingSystem = rs.getString("provisioingSystem");
			Long serviceId = rs.getLong("serviceId");
			Long prdetailsId = rs.getLong("prdetailsId");
			String product = rs.getString("sentMessage");
			String macId = rs.getString("macId");
			String deviceId = rs.getString("deviceId");
			String requestType = rs.getString("requestType");
			
			Long zebraSubscriberId = rs.getLong("zebraSubscriberId");
			Long regionId = rs.getLong("regionId");
			String regionName = rs.getString("regionName");
			String selfcareUsername = rs.getString("selfcareUsername");
			String selfcarePassword = rs.getString("selfcarePassword");
			String countryISD = rs.getString("countryISD");

			return new EntitlementsData(id, clientId, firstName, lastName, phone, email, city, zip, street, country, countryId,
					provisioingSystem, serviceId, prdetailsId, product, macId, deviceId, requestType,
					zebraSubscriberId, regionId, regionName, selfcareUsername, selfcarePassword, countryISD);
		}

		public String schema() {

			return " bpr.id as id, c.id as clientId,c.firstname as firstName,c.lastname as lastName, c.phone as phone," +
					" c.email as email,bca.city as city,bca.zip as zip,bca.street as street,bca.country as country,bpd.country_id as countryId," +
					" bpr.provisioing_system AS provisioingSystem,bprd.service_id AS serviceId,bprd.id AS prdetailsId," +
					" bprd.sent_message AS sentMessage, ifnull(bid.provisioning_serialno ,oh.provisioning_serial_number) AS macId," +
					" ifnull(bid.serial_no ,oh.serial_number) AS deviceId, bprd.request_type AS requestType," +
					" bcu.zebra_subscriber_id as zebraSubscriberId, bcu.username as selfcareUsername, bcu.password as selfcarePassword, " +
					" bprm.id as regionId, bprm.priceregion_name as regionName , bcc.country_isd as countryISD" +
					" from m_client c " +
					" left join m_office o on (o.id = c.office_id) " +
					" left join b_process_request bpr on (c.id = bpr.client_id ) " +
					" left join  b_clientuser  bcu on (c.id = bcu.client_id ) " +
					" left join b_process_request_detail bprd on (bpr.id = bprd.processrequest_id )" +
					" left join b_client_address bca on (c.id=bca.client_id and address_key='PRIMARY')" +
					" left join b_state bs on (bca.state = bs.state_name ) " +
					" left JOIN b_priceregion_detail bpd ON ((bpd.state_id = bs.id or bpd.state_id=0)  and bpd.country_id=bs.parent_code AND bpd.is_deleted = 'N')" +
					" left join b_priceregion_master bprm on (bpd.priceregion_id = bprm.id ) " +
					" left join b_ippool_details bipd on (bpr.client_id = bipd.client_id)" +
					" left join b_item_detail bid on (bprd.hardware_id = bid.provisioning_serialno) " +
					" left join b_item_master bim on (bid.item_master_id = bim.id) " +
					" left join b_owned_hardware oh on (bprd.hardware_id =oh.provisioning_serial_number AND oh.is_deleted = 'N')" +
					" left join b_country_currency bcc on (bpd.country_id = bcc.id AND bcc.is_deleted = 'N')" +
					" WHERE bpr.is_processed = 'N' ";
		}

	}

	@Override
	public Page<EntitlementsData> retrivegroupbyfingerprint(String limit, String offset) {
		
		this.context.authenticatedUser();
		final AppUser currentUser = context.authenticatedUser();

		String hierarchy = currentUser.getOffice().getHierarchy();
		String hierarchySearchString = hierarchy + "%";
        final GroupbyfingerprintMapper gm = new GroupbyfingerprintMapper();
      
        
        
        
       
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        
        sqlBuilder.append(gm.schema());
        sqlBuilder.append(" where action_name='RETRACKOSDMESSAGE' and command_as_json like ").toString();
        sqlBuilder.append("'%");
        sqlBuilder.append("\"");
        sqlBuilder.append("type");
        sqlBuilder.append("\"");
        sqlBuilder.append(":");
        sqlBuilder.append("\"");
        sqlBuilder.append("group");
        sqlBuilder.append("\"");
        sqlBuilder.append("%'");
        sqlBuilder.append("and o.hierarchy like '"+hierarchySearchString+"'");
        sqlBuilder.append("order by made_on_date DESC");
        if (limit!=null) {
            sqlBuilder.append(" limit "+limit);
        }

        if (offset!=null) {
            sqlBuilder.append(" offset "+offset);
        }
         
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(),
                new Object[] {}, gm);
		
	}
	private static final class GroupbyfingerprintMapper implements
	RowMapper<EntitlementsData> {

public String schema() {
	return  " ps.action_name as action_name,ps.entity_name as entity_name,ps.made_on_date as made_on_date ,ps.resource_id as resource_id ,ps.client_id as client_id,ps.command_as_json as command_as_json from m_portfolio_command_source ps "
			+"join m_appuser a on a.id = ps.maker_id join m_office o on o.id = a.office_id";
}

@Override
public EntitlementsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
	
	final String action_name = rs.getString("action_name");
	final String entity_name = rs.getString("entity_name");
    final LocalDate made_on_date = JdbcSupport.getLocalDate(rs, "made_on_date");
	final Long resource_id = rs.getLong("resource_id");
	final Long client_id = rs.getLong("client_id");
	final String command_as_json = rs.getString("command_as_json");
	
	//final String paramNotes = rs.getString("paramNotes");
	
	/*return new ProvisioningData(id, provisioningSystem, commandName, status,provisioningSystemName);*/
	EntitlementsData  entitlementsData =new EntitlementsData();
	
	entitlementsData.setAction_name(action_name);
	entitlementsData.setEntity_name(entity_name);
	entitlementsData.setMade_on_date(made_on_date);
	entitlementsData.setResource_id(resource_id);
	entitlementsData.setClient_id(client_id);
	entitlementsData.setCommand_as_json(command_as_json);
	return entitlementsData;
	
			
	
}
	}
}
