package org.mifosplatform.provisioning.provisioning.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetColumnHeaderData;
import org.mifosplatform.infrastructure.dataqueries.data.ResultsetRowData;
import org.mifosplatform.infrastructure.dataqueries.service.ReadReportingService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.order.data.BillPlanData;
import org.mifosplatform.portfolio.order.data.BillPlanProducts;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestWriteplatformService;
import org.mifosplatform.provisioning.provisioning.data.ProcessRequestData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningCommandParameterData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningRequestData;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProvisioningReadPlatformServiceImpl implements ProvisioningReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ReadReportingService readReportingService;
	private final ProcessRequestWriteplatformService processRequestWriteplatformService;
    private final PaginationHelper<ProvisioningData> paginationHelper = new PaginationHelper<ProvisioningData>();

	@Autowired
	public ProvisioningReadPlatformServiceImpl(
			final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource,
			final ReadReportingService readReportingService,
			final ProcessRequestWriteplatformService processRequestWriteplatformService) {

		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.readReportingService = readReportingService;
		this.processRequestWriteplatformService = processRequestWriteplatformService;

	}

	@Override
	public ProvisioningData retrieveIdData(Long id) {
		try {
			context.authenticatedUser();
			ProvisioningMapper rm = new ProvisioningMapper();
			final String sql = "select " + rm.schema() + " where p.is_deleted='N' and p.id=?";
			return jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<ProvisioningData> getProvisioningData(Long clientServiceId) {
		try {

			ProvisioningMapper rm = new ProvisioningMapper();
			String sql = "select distinct " + rm.schema();
			if(clientServiceId != null){
				sql = sql+" JOIN b_service_parameters sp on sp.clientservice_id = "+clientServiceId+" AND sp.parameter_value = ne.id";
			}
			sql = sql+" where p.is_deleted='N'";
			return jdbcTemplate.query(sql, rm, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ProvisioningMapper implements
			RowMapper<ProvisioningData> {

		public String schema() {
			return " p.id as id,p.provisioning_system as provisioningSystem,ne.system_code as provisioningSystemName,p.command_name as CommandName," +
					"p.status as status from b_command p Join b_network_element ne on ne.id = p.provisioning_system";
		}

		@Override
		public ProvisioningData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long id = rs.getLong("id");
			final Long provisioningSystem = rs.getLong("provisioningSystem");
			final String commandName = rs.getString("CommandName");
			final String status = rs.getString("status");
			final String provisioningSystemName = rs.getString("provisioningSystemName");
			//final String paramNotes = rs.getString("paramNotes");
			
			/*return new ProvisioningData(id, provisioningSystem, commandName, status,provisioningSystemName);*/
			ProvisioningData pd =new ProvisioningData(id, provisioningSystem, commandName, status,provisioningSystemName);
			//pd.setParamNotes(paramNotes);
			return pd;
			
					
			
		}
	}

	@Override
	public List<McodeData> retrieveProvisioningCategory() {
		
		this.context.authenticatedUser();
		final SystemDataMapper mapper = new SystemDataMapper();
		final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] { "Provisioning" });
	}

	@Override
	public List<McodeData> retrievecommands() {
		
		this.context.authenticatedUser();
		SystemDataMapper mapper = new SystemDataMapper();
		final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] { "Command" });
	}

	private static final class SystemDataMapper implements RowMapper<McodeData> {

		public String schema() {

			return " mc.id as id,mc.code_value as codeValue from m_code m,m_code_value mc where m.id = mc.code_id and m.code_name=? ";

		}

		@Override
		public McodeData mapRow(ResultSet rs, int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			String codeValue = rs.getString("codeValue");
			return new McodeData(id, codeValue);

		}

	}

	@Override
	public List<ProvisioningCommandParameterData> retrieveCommandParams(Long id) {
		try {

			ProvisioningCommandMapper rm = new ProvisioningCommandMapper();
			final String sql = "select " + rm.schema();
			return jdbcTemplate.query(sql, rm, new Object[] { id });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ProvisioningCommandMapper implements
			RowMapper<ProvisioningCommandParameterData> {

		public String schema() {
			return " c.id as id, c.command_param as commandParam,c.param_type as paramType,c.param_length as paramLength,c.param_default as paramDefault,c.param_notes as paramNotes from b_command_parameters c where c.command_id=? ";
		}

		@Override
		public ProvisioningCommandParameterData mapRow(final ResultSet rs,
				final int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			String commandParam = rs.getString("commandParam");
			String paramType = rs.getString("paramType");
			Long paramLength = rs.getLong("paramLength");
			String paramDefault = rs.getString("paramDefault");
			String paramNotes = rs.getString("paramNotes");
			return new ProvisioningCommandParameterData(id, commandParam, paramType, paramLength, paramDefault,paramNotes);
		}
	}
	
	@Override
	public List<ProvisioningData> getProcessRequestCommandData(Long provisioningSystemId) {
		try {
			context.authenticatedUser();
			ProvisioningMapper rm = new ProvisioningMapper();
			final String sql = "select " + rm.schema() + " where p.is_deleted='N' and p.provisioning_system=?";
			return jdbcTemplate.query(sql, rm, new Object[] { provisioningSystemId });
			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	

	@Transactional
	@Override
	public List<ServiceParameterData> getSerivceParameters(Long orderId) {

		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("${orderId}", orderId.toString());
		List<ServiceParameterData> parameterDatas = new ArrayList<ServiceParameterData>();
		final GenericResultsetData resultsetData = this.readReportingService.retrieveGenericResultset("Service", "parameter", queryParams, null);
		List<ResultsetRowData> datas = resultsetData.getData();
		List<String> row;
		Integer rSize;
		for (int i = 0; i < datas.size(); i++) {
			row = datas.get(i).getRow();
			rSize = row.size();
			for (int j = 0; j < rSize - 1; j++) {

				String id = datas.get(i).getRow().get(j);
				j++;
				String paramName = datas.get(i).getRow().get(j);
				j++;
				String paramValue = datas.get(i).getRow().get(j);
				j = j++;
				parameterDatas.add(new ServiceParameterData(Long.valueOf(id), paramName, paramValue, null));
			}
		}
		/*
		 * for(int i=0;i<columnHeaderDatas.size();i++){ for(int
		 * j=0;j<datas.size();j++){ String id=null; String paramName=null;
		 * String paramValue=null;
		 * if(columnHeaderDatas.get(i).getColumnName().equalsIgnoreCase("id")){
		 * 
		 * id=datas.get(i).getRow().get(i);
		 * 
		 * }else
		 * if(columnHeaderDatas.get(i).getColumnName().equalsIgnoreCase("paramName"
		 * )){
		 * 
		 * paramName=datas.get(i).getRow().get(i); }else{
		 * paramValue=datas.get(i).getRow().get(i); } parameterDatas.add(new
		 * ServiceParameterData(new Long(id), paramName, paramValue)); } }
		 */

		return parameterDatas;

		/*
		 * 
		 * try{ this.context.authenticatedUser(); ServiceParameterMapper
		 * mapper=new ServiceParameterMapper(); final String
		 * sql="select "+mapper.schema(); return this.jdbcTemplate.query(sql,
		 * mapper,new Object[] {orderId});
		 * 
		 * }catch(EmptyResultDataAccessException exception){ return null; }
		 */}

	private static final class ServiceParameterMapper implements
			RowMapper<ServiceParameterData> {

		public String provisionedschema() {
			return "  s.id AS id,s.parameter_name AS paramName,s.parameter_value AS paramValue  FROM b_service_parameters s "
					+ "  WHERE s.order_id = ? and status='ACTIVE'";
		}

		@Override
		public ServiceParameterData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String paramName = rs.getString("paramName");
			String paramValue = rs.getString("paramValue");
			return new ServiceParameterData(id, paramName, paramValue, null);
		}
	}

	@Transactional
	@Override
	public List<ServiceParameterData> getProvisionedSerivceParameters(
			Long orderId) {
		try {
			this.context.authenticatedUser();
			ServiceParameterMapper mapper = new ServiceParameterMapper();
			final String sql = "select " + mapper.provisionedschema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] { orderId });

		} catch (EmptyResultDataAccessException exception) {
			return null;
		}

	}

/*	@Override
	public Long getHardwareDetails(String oldHardWare, Long clientId,
			String name) {
		try {

			String sql;
			if (name.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)) {

				sql = "select a.id as id  from b_owned_hardware a where a.serial_number = ? and a.client_id=? and is_deleted = 'N' limit 1";

			} else {

				sql = "select i.id as id from  b_item_detail i where i.provisioning_serialno=? and i.client_id=1 limit 1";
			}
			return jdbcTemplate.queryForLong(sql, new Object[] { oldHardWare,
					clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}*/

	@Override
	public List<ProcessRequestData> getProcessRequestData(String clientServiceId) {

		context.authenticatedUser();

		ProcessRequestMapper mapper = new ProcessRequestMapper();

		String sql = "select " + mapper.schema()+" order by id desc " ;

		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientServiceId });
	}
	
	

	private static final class ProcessRequestMapper implements
			RowMapper<ProcessRequestData> {

		public String schema() {
			/*return "  p.id AS id,p.client_id AS clientId,o.id as orderId,o.order_no AS orderNo,p.request_type AS requestType,p.status AS isProcessed,"
					+ " null AS hardwareId,pr.response_message AS receiveMessage,pr.request_message AS sentMessage FROM b_provisioning_request p "
					+ " INNER JOIN b_provisioning_request_detail pr ON pr.provisioning_req_id = p.id left join b_orders o on p.order_id=o.id WHERE "
					+ "  o.order_no =? ";*/
			
			return  " p.id AS id, p.client_id AS clientId, p.request_type AS requestType, p.status AS isProcessed, null AS hardwareId, " +
					" pr.response_message AS receiveMessage, pr.request_message AS sentMessage, pr.response_message AS responseMessage, " +
					" pr.response_status AS taskId FROM b_provisioning_request p " +
					" INNER JOIN b_provisioning_request_detail pr ON pr.provisioning_req_id = p.id where p.clientservice_id = ? ";
		}

		public String schemaForId() {

			return " p.id as id,p.client_id as clientId, p.order_id as orderId,p.order_id as orderNo,p.request_type as requestType,p.is_processed as isProcessed, "
					+ " pr.hardware_id as hardwareId, pr.receive_message as receiveMessage, pr.sent_message as sentMessage "
					+ " from b_process_request p inner join b_process_request_detail pr on pr.processrequest_id=p.id where";
		}

		@Override
		public ProcessRequestData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			String requestType = rs.getString("requestType");
			String hardwareId = rs.getString("hardwareId");
			String receiveMessage = rs.getString("receiveMessage");
			String sentMessage = rs.getString("sentMessage");
			String isProcessed = rs.getString("isProcessed");
			final String responseMessage = rs.getString("responseMessage");
			final Long taskId = rs.getLong("taskId");
			return new ProcessRequestData(id, clientId, null, requestType,
					hardwareId, receiveMessage, sentMessage, isProcessed,
					null,responseMessage, taskId);
		}
				
	}

	@Override
	public ProcessRequestData getProcessRequestIDData(Long id) {

		context.authenticatedUser();

		ProcessRequestMapper mapper = new ProcessRequestMapper();

		String sql = "select " + mapper.schemaForId()+" p.order_id= ? group by p.id";
		 

		return this.jdbcTemplate.queryForObject(sql, mapper,
				new Object[] { id });
	}
	@Override
	public List<ProcessRequestData> getProcessRequestClientData(Long clientId) {

		context.authenticatedUser();

		ProcessRequestMapper mapper = new ProcessRequestMapper();

		String sql = "select " + mapper.schemaForId() + " p.client_id = ? and p.order_id=0 group by p.id;";
		
		return this.jdbcTemplate.query(sql, mapper, new Object[] {clientId  });

	}


	@Override
	public Collection<MCodeData> retrieveVlanDetails(String string) {

		Map<String, String> queryParams = new HashMap<String, String>();
		Collection<MCodeData> codeDatas = new ArrayList<MCodeData>();
		queryParams.put("${codeName}", string);
		final GenericResultsetData resultsetData = this.readReportingService
				.retrieveGenericResultset("VLAN_ID", "parameter", queryParams, null);
		List<ResultsetColumnHeaderData> columnHeaderDatas = resultsetData
				.getColumnHeaders();
		List<ResultsetRowData> datas = resultsetData.getData();

		List<String> row;
		Integer rSize;
		for (int i = 0; i < datas.size(); i++) {
			row = datas.get(i).getRow();
			rSize = row.size();
			for (int j = 0; j < rSize - 1; j++) {

				String id = datas.get(i).getRow().get(j);
				j++;
				String paramValue = datas.get(i).getRow().get(j);
				j = j++;
				codeDatas.add(new MCodeData(Long.valueOf(id), paramValue,null));
			}
		}

		/*
		 * for(int i=0;i<columnHeaderDatas.size();i++){ for(int
		 * j=0;j<datas.size();j++){ MCodeData codeData=new MCodeData();
		 * if(columnHeaderDatas.get(i).getColumnName().equalsIgnoreCase("id")){
		 * codeData.setmCodeValue(datas.get(i).getRow().get(i)); }else{
		 * codeData.setmCodeValue(datas.get(i).getRow().get(i)); }
		 * codeDatas.add(codeData); } }
		 */

		return codeDatas;
	}

	@Override
	public List<ProvisioningRequestData> retrieveUnProcessedProvisioningRequestData() {
		try {
			this.context.authenticatedUser();
			ProvisioningRequestMapper mapper = new ProvisioningRequestMapper();
			final String sql = "SELECT DISTINCT " + mapper.schema()+" where pr.status = 'N' ";
			return this.jdbcTemplate.query(sql, mapper, new Object[] { });

		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
		
	}
	
	private static final class ProvisioningRequestMapper implements RowMapper<ProvisioningRequestData> {

			public String schema() {
				return " pr.id AS id, pr.order_id AS orderId, mcv.code_value AS provisioningSystem, pr.request_type AS requestType "+
					   " FROM b_provisioning_request pr JOIN m_code_value mcv ON mcv.id = pr.provisioning_system ";
			}


			@Override
			public ProvisioningRequestData mapRow(final ResultSet rs, final int rowNum)
					throws SQLException {
				Long id = rs.getLong("id");
				Long orderId = rs.getLong("orderId");
				String provisioningSystem = rs.getString("provisioningSystem");
				String requestType = rs.getString("requestType");
				
				return new ProvisioningRequestData(id, orderId, provisioningSystem, requestType);
						
			}
		
	}

	@Override
	public BillPlanData retriveBillPlan(ProvisioningRequest provisioningRequest) {

        BillPlanMapper mapper = new BillPlanMapper();
        
         String sql = "SELECT DISTINCT " + mapper.schema()+" where p.id = "+provisioningRequest.getId();
        BillPlanData billPlanData =  this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { });
        
        List<Order> orders = this.processRequestWriteplatformService.retriveOrdersfromProvisioningRequest(provisioningRequest);
        StringBuilder orderIds = new StringBuilder("(");
        for(Order order:orders){
            orderIds.append(order.getId()+",");
            
        }
        orderIds.append(")");
        String ordersList = (orderIds.toString()).replace(",)",")");
        List<BillPlanProducts> billPlanProducts = this.retriveBillPlanProducts(ordersList);
        List<String> planPoids = new ArrayList<String>();
        for(BillPlanProducts billPlanProduct:billPlanProducts){
        	planPoids.add(billPlanProduct.getPlanPoId());
        }
        billPlanData.setBillPlanProducts(billPlanProducts);
        billPlanData.setPlanPoIds(planPoids);
        return billPlanData;
    }

	private static final class BillPlanMapper implements RowMapper<BillPlanData> {

        public String schema() {
            return " c.po_id as clientPoId,cs.client_service_poid as clientServicePoId "
                 + " from b_provisioning_request p join m_client c on c.id=p.client_id "
                 + " join b_client_service cs on cs.id = p.clientservice_id ";
        }


        @Override
        public BillPlanData mapRow(final ResultSet rs, final int rowNum)
                throws SQLException {
        	String clientPoId = rs.getString("clientPoId");
        	String clientServicePoId = rs.getString("clientServicePoId");
            return new BillPlanData(clientPoId,clientServicePoId);
                    
        }
    
    }
    
    
    private List<BillPlanProducts> retriveBillPlanProducts(String orders) {
        BillPlanProductsMapper mapper = new BillPlanProductsMapper();
        
         StringBuilder sql = new StringBuilder("SELECT DISTINCT ");
         sql.append(mapper.schema());
         sql.append(" where o.id in "+orders);
         return this.jdbcTemplate.query(sql.toString(), mapper, new Object[] {});
        
        
    }

    private static final class BillPlanProductsMapper implements RowMapper<BillPlanProducts> {

        public String schema() {
            return " o.order_no as packageId,p.plan_poid as planPoId, pt.product_poid as productPoId, "
                 + " ol.purchase_product_poid as purchasedProductPoId"
                 + " from b_orders o "
                 + " join b_plan_master p on p.id = o.plan_id "
                 + " join b_order_line ol on ol.order_id=o.id "
                 + " join b_product pt on ol.product_id = pt.id ";
                
        }


        @Override
        public BillPlanProducts mapRow(final ResultSet rs, final int rowNum)  throws SQLException {
            
            String productPoId = rs.getString("productPoId");
            String purchasedProductPoId = rs.getString("purchasedProductPoId");
            String packageId = rs.getString("packageId");
            String planPoId = rs.getString("planPoId");
            return new BillPlanProducts(productPoId,purchasedProductPoId,packageId,planPoId);
                    
        }
    
    }
    
    
    @Override
	public ProvisioningData retrieveClientAndServiceParam(Long clientServiceId) {
		try {

			ClientMapper cm = new ClientMapper();
			String sql = "select " + cm.schema()+" where sp.clientservice_id="+clientServiceId;
			return jdbcTemplate.queryForObject(sql, cm);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientMapper implements
			RowMapper<ProvisioningData> {

		public String schema() {
			return "c.id as id, c.account_no as accountNo, c.display_name as displayname, c.office_Id as officeid,"
					+ " c.email as email , c.firstname as firstname , c.lastname as lastname , c.phone as phone , "
					+ "c.password as clientPassword,"//cu.password as selfcarePassowrd,
					+ " sp.parameter_value as parametervalue from " +
					"b_service_parameters sp join m_client c on c.id=sp.client_id ";
/*					+ "join b_clientuser cu on c.id= cu.client_id";
*/		}

		@Override
		public ProvisioningData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long id = rs.getLong("id");
			final String accountNo = rs.getString("accountNo");
			final String displayName = rs.getString("displayname");
			final Long officeId = rs.getLong("officeid");
			final String email = rs.getString("email");
			final String firstname = rs.getString("firstname");
			final String lastname = rs.getString("lastname");
			final String phone = rs.getString("phone");
			final String clientPassword = rs.getString("clientPassword");
		//	final String selfcarepassword = rs.getString("selfcarePassowrd");
			final Long parameterValue = rs.getLong("parametervalue");
			ClientData clientData = ClientData.instance(accountNo, null, null, officeId, null, id,firstname , null,lastname, null, displayName, null, null, null, null, email, phone, null, null, null, null, null, null, null, null, null, null, null, null, null, null, clientPassword, null, null, null, null, null, null);
			//clientData.setSelfcarePassword(selfcarepassword);
			return new ProvisioningData(parameterValue, clientData);
		}
	}

	@Override
	public Page<ProvisioningData> retriveprovisioningfailure(String limit , String offset) {
	/*	try {
			context.authenticatedUser();
			ProvisioningfailMapper pm = new ProvisioningfailMapper();
			final String sql = "select " + pm.schema();
			return jdbcTemplate.query(sql, pm, new Object[] { });
			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}*/
	    final AppUser currentUser = context.authenticatedUser();
        final ProvisioningfailMapper pm = new ProvisioningfailMapper();
      
        
        
        
       
        final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");
        
        sqlBuilder.append(pm.schema());
     
        
       
        if (limit!=null) {
            sqlBuilder.append(" limit "+limit);
        }

        if (offset!=null) {
            sqlBuilder.append(" offset "+offset);
        }
         
        final String sqlCountRows = "SELECT FOUND_ROWS()";
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(),
                new Object[] {  }, pm);
	}
	private static final class ProvisioningfailMapper implements
	RowMapper<ProvisioningData> {

public String schema() {
	return "id,client_id,request_type,status ,created_date,response_message,response_status from provisioning_requests_vw where status='F' order by id desc";
}

@Override
public ProvisioningData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
	
	final Long id = rs.getLong("id");
	final Long client_id = rs.getLong("client_id");
	final String request_type = rs.getString("request_type");
	final String status = rs.getString("status");
    final LocalDate created_date = JdbcSupport.getLocalDate(rs, "created_date");
	final String responsemessage = rs.getString("response_message");
	final String responsestatus = rs.getString("response_status");
	
	//final String paramNotes = rs.getString("paramNotes");
	
	/*return new ProvisioningData(id, provisioningSystem, commandName, status,provisioningSystemName);*/
	ProvisioningData  provisioningData =new ProvisioningData(null);
	provisioningData.setId(id);
	provisioningData.setClient_id(client_id);
	provisioningData.setRequest_type(request_type);
	provisioningData.setStatus(status);
	provisioningData.setCreated_date(created_date);
	provisioningData.setResponse_message(responsemessage);
	provisioningData.setResponse_status(responsestatus);
	return provisioningData;
	
			
	
}
}
}
