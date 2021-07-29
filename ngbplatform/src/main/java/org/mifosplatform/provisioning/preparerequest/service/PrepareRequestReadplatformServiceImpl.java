package org.mifosplatform.provisioning.preparerequest.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.DataSourcePerTenantService;
import org.mifosplatform.logistics.onetimesale.data.AllocationDetailsData;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateConstants;
import org.mifosplatform.organisation.message.service.BillingMessageTemplateWritePlatformService;
import org.mifosplatform.portfolio.allocation.service.AllocationReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderAddons;
import org.mifosplatform.portfolio.order.domain.OrderAddonsRepository;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.planmapping.domain.PlanMapping;
import org.mifosplatform.portfolio.planmapping.domain.PlanMappingRepository;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMapping;
import org.mifosplatform.portfolio.servicemapping.domain.ServiceMappingRepository;
import org.mifosplatform.provisioning.preparerequest.data.PrepareRequestData;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequest;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequsetRepository;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class PrepareRequestReadplatformServiceImpl  implements PrepareRequestReadplatformService{

	
	  private final DataSourcePerTenantService dataSourcePerTenantService;
	  private final OrderRepository orderRepository;
	  private final ProcessRequestRepository processRequestRepository;
	  private final PrepareRequsetRepository prepareRequsetRepository;
	  private final AllocationReadPlatformService allocationReadPlatformService;
	  private final ServiceMappingRepository provisionServiceDetailsRepository;
	  private final ServiceMasterRepository serviceMasterRepository;
	  private final OrderAddonsRepository orderAddonsRepository;
	  public final static String PROVISIONGSYS_COMVENIENT="Comvenient";
	  private final PlanMappingRepository planMappingRepository;
	  private final BillingMessageTemplateWritePlatformService billingMessageTemplateWritePlatformService;
	

	    @Autowired
	    public PrepareRequestReadplatformServiceImpl(final DataSourcePerTenantService dataSourcePerTenantService,final OrderRepository orderRepository,
	    		final ServiceMasterRepository serviceMasterRepository,final ProcessRequestRepository processRequestRepository,
	    		final AllocationReadPlatformService allocationReadPlatformService,final PrepareRequsetRepository prepareRequsetRepository,
	    		final ServiceMappingRepository provisionServiceDetailsRepository,final PlanMappingRepository planMappingRepository,
	    		final OrderAddonsRepository orderAddonsRepository,final BillingMessageTemplateWritePlatformService billingMessageTemplateWritePlatformService) {
	            
	    	    
	    	    this.orderRepository=orderRepository;
	    	    this.serviceMasterRepository=serviceMasterRepository;
	    	    this.orderAddonsRepository=orderAddonsRepository;
	            this.processRequestRepository=processRequestRepository;
	            this.prepareRequsetRepository=prepareRequsetRepository;
	            this.dataSourcePerTenantService = dataSourcePerTenantService;
	            this.allocationReadPlatformService=allocationReadPlatformService;
	            this.provisionServiceDetailsRepository=provisionServiceDetailsRepository;
	            this.planMappingRepository=planMappingRepository;
	            this.billingMessageTemplateWritePlatformService=billingMessageTemplateWritePlatformService;
	        
	    }

	
	@Override
	public List<PrepareRequestData> retrieveDataForProcessing() {
			
		try {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
				final ProvisioningMapper mapper = new ProvisioningMapper();
				final String sql = "select " + mapper.provisioningLookupSchema();
				return jdbcTemplate.query(sql, mapper, new Object[] { });
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
			}

	private static final class ProvisioningMapper implements RowMapper<PrepareRequestData> {

			public String provisioningLookupSchema() {
				return "  pr.id AS id,pr.client_id AS clientId,pr.order_id AS orderId,pr.provisioning_sys AS provisioningSystem,c.firstname AS userName," +
						" p.is_hw_req AS hwRequired,p.plan_code AS planName,pr.request_type AS requestType FROM b_prepare_request pr,m_client c,b_orders o," +
						" b_plan_master p WHERE pr.client_id = c.id AND o.plan_id = p.id AND pr.order_id = o.id" +
						" AND (pr.is_provisioning = 'N' OR pr.status = 'PENDING') GROUP BY pr.order_id DESC";
			}

			@Override
			public PrepareRequestData mapRow(final ResultSet rs,final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long clientId = rs.getLong("clientId");
			final Long orderId = rs.getLong("orderId");
			final String requestType = rs.getString("requestType");
		    final String planName=rs.getString("planName");	
			final String userName=rs.getString("userName");
			final String ishwReq=rs.getString("hwRequired");
			final String provisioningSys=rs.getString("provisioningSystem");
			
			return new PrepareRequestData(id, clientId,orderId, requestType,null,userName,provisioningSys,planName,ishwReq,null);
				}
			}	
			
			@Override
			public List<Long> retrieveRequestClientOrderDetails(Long clientId) {
				try {
				
			        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
				   OrderIdMapper planIdMapper = new OrderIdMapper();
				   String sql = "select" + planIdMapper.planIdSchema();
				  return jdbcTemplate.query(sql, planIdMapper,new Object[] { clientId });
				
			
			} catch (EmptyResultDataAccessException e) {
				return null;
				}
			}
			private static final class OrderIdMapper implements RowMapper<Long> {

				@Override
				public Long  mapRow(ResultSet resultSet, int rowNum) throws SQLException {
					return resultSet.getLong("orderId");
				}
				

				public String planIdSchema() {
					return "  os.id as orderId FROM b_orders os where os.client_id=?";
							
				}
				
				
			}

			@Override
			public CommandProcessingResult processingClientDetails(PrepareRequestData requestData) {
				
				String errorMessage = null;
				PrepareRequest prepareRequest = this.prepareRequsetRepository.findOne(requestData.getRequestId());
				
				try { 
					String requestType = null;
					ProcessRequest processRequest = null;
					Long processResultId = Long.valueOf(0);
					Order order = this.orderRepository.findOne(requestData.getOrderId());
					AllocationDetailsData detailsData = this.allocationReadPlatformService.getTheHardwareItemDetails(requestData.getOrderId());
					requestType = requestData.getRequestType();
					PlanMapping planMapping = this.planMappingRepository.findOneByPlanId(order.getPlanId());

					if ((requestData.getIshardwareReq().equalsIgnoreCase("Y") && detailsData == null) || planMapping == null) {
						String status = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getValue().toString();
						if (prepareRequest != null) {
							prepareRequest.setStatus(status);
							this.prepareRequsetRepository.save(prepareRequest);
						}

						if (planMapping == null) {
							errorMessage = "Plan Mapping Not Found For this Plan, planId="+ order.getPlanId() + ", orderId=" + order.getId();
						} else {
							errorMessage = "Hardware Mapping Not Found For this Order, orderId=" + order.getId() + ", planId=" + order.getPlanId();
						}

						// Update Order Status
						order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId());
						this.orderRepository.saveAndFlush(order);

					} else {
						String HardWareId = null;
						if (detailsData != null) {
							HardWareId = detailsData.getSerialNo();
						}
						processRequest = new ProcessRequest(requestData.getRequestId(), order.getClientId(), order.getId(),
								requestData.getProvisioningSystem(), requestType, 'N', 'N');
						List<OrderLine> orderLineData = order.getServices();
						JSONObject jsonObject = new JSONObject();

						if (planMapping != null) {
							jsonObject.put("planIdentification", planMapping.getPlanIdentification());
						}

						JSONArray serviceArray = new JSONArray();
						if (requestData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.CHANGE_PLAN.toString())) {
							Order oldOrder = this.orderRepository.findOldOrderByOrderNO(order.getOrderNo());
							List<OrderLine> orderdetails = oldOrder.getServices();
							planMapping = this.planMappingRepository.findOneByPlanId(oldOrder.getPlanId());

							if (planMapping != null) {
								jsonObject.put("oldPlanIdentification", planMapping.getPlanIdentification());
							} else {
								errorMessage = "Plan Mapping Not Found For the Old Plan, planId="+ order.getPlanId() + ", orderId=" + order.getId();
							}

							for (OrderLine orderLine : orderdetails) {

								JSONObject oldsubjson = new JSONObject();
								List<ServiceMapping> provisionServiceDetails = this.provisionServiceDetailsRepository.findOneByServiceId(orderLine.getProductId());

								ServiceMaster service = this.serviceMasterRepository.findOne(orderLine.getProductId());
								if (!provisionServiceDetails.isEmpty()) {
									oldsubjson.put("oldServiceIdentification", provisionServiceDetails.get(0).getServiceIdentification());
								}
								oldsubjson.put("oldServiceType", service.getServiceType());
								serviceArray.add(oldsubjson);
							}
							jsonObject.put("oldServices", new Gson().toJson(serviceArray));
						}

						JSONArray newServiceArray = new JSONArray();
						if (requestData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.DEVICE_SWAP.toString())) {
							AllocationDetailsData allocationDetailsData = this.allocationReadPlatformService
									.getDisconnectedHardwareItemDetails(requestData.getOrderId(), requestData.getClientId());
							jsonObject.put("clientId", order.getClientId());
							jsonObject.put("OldHWId", allocationDetailsData.getSerialNo());
							jsonObject.put("NewHWId", HardWareId);
						}
						if (requestType.equalsIgnoreCase(UserActionStatusTypeEnum.ADDON_ACTIVATION.toString())) {

							List<OrderAddons> orderAddons = this.orderAddonsRepository.findAddonsByOrderId(requestData.getOrderId());
							
							for (OrderAddons orderAddon : orderAddons) {

								List<ServiceMapping> provisionServiceDetails = this.provisionServiceDetailsRepository
										.findOneByServiceId(orderAddon.getServiceId());
								ServiceMaster service = this.serviceMasterRepository.findOne(orderAddon.getServiceId());
								JSONObject subjson = new JSONObject();
								subjson.put("serviceName", service.getServiceCode());
								subjson.put("addonId", requestData.getAddonId());
								if (!provisionServiceDetails.isEmpty()) {
									subjson.put("serviceIdentification", provisionServiceDetails.get(0).getServiceIdentification());
								}
								subjson.put("serviceType", service.getServiceType());
								newServiceArray.add(subjson.toString());
							}
						} else {
							for (OrderLine orderLine : orderLineData) {
								List<ServiceMapping> provisionServiceDetails = this.provisionServiceDetailsRepository
										.findOneByServiceId(orderLine.getProductId());
								ServiceMaster service = this.serviceMasterRepository.findOne(orderLine.getProductId());
								JSONObject subjson = new JSONObject();
								subjson.put("serviceName", service.getServiceCode());
								if (!provisionServiceDetails.isEmpty()) {
									subjson.put("serviceIdentification", provisionServiceDetails.get(0).getServiceIdentification());
								}
								subjson.put("serviceType", service.getServiceType());
								newServiceArray.add(subjson.toString());
							}
						}
						jsonObject.put("services", new Gson().toJson(newServiceArray));
						ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(orderLineData.get(0).getId(), 
								orderLineData.get(0).getProductId(), jsonObject.toString(), "Recieved", HardWareId, order.getStartDate(),
								order.getEndDate(), null, null, 'N', requestType, null);
						processRequest.add(processRequestDetails);
						this.processRequestRepository.save(processRequest);
						String status = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getValue().toString();
						processResultId = processRequest.getId();
						if (prepareRequest != null) {
							prepareRequest.setIsProvisioning('Y');
							prepareRequest.setStatus(status);
							this.prepareRequsetRepository.save(prepareRequest);
						}
					}
					/*
					 * if(requestData.getProvisioningSystem().equalsIgnoreCase("None")){
					 * order.setStatus(new Long(1)); this.orderRepository.save(order); }
					 */
					if(null != errorMessage && null != prepareRequest && prepareRequest.getIsNotify() == 'N') { 
						this.billingMessageTemplateWritePlatformService.processEmailNotification(order.getClientId(), 
								order.getId(), errorMessage, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_PENDING_REQUEST);
						prepareRequest.setIsNotify('Y');
						this.prepareRequsetRepository.save(prepareRequest);
					}
					return new CommandProcessingResult(processResultId);

				} catch (Exception exception) {
					String className = exception.getClass().getSimpleName();
					errorMessage = " Throwing " + className + " Exception , Reason: " + stackTraceToString(exception);
					if (prepareRequest != null && prepareRequest.getIsNotify() == 'N') {
						prepareRequest.setIsProvisioning('N');
						prepareRequest.setStatus("Pending");
						prepareRequest.setIsNotify('Y');
						this.billingMessageTemplateWritePlatformService.processEmailNotification(prepareRequest.getClientId(), 
								prepareRequest.getOrderId(), errorMessage, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_PENDING_REQUEST);
						this.prepareRequsetRepository.save(prepareRequest);
					}
					exception.printStackTrace();
					return new CommandProcessingResult(Long.valueOf(-1));
				}
			}
			
			public String stackTraceToString(Throwable e) {
				StringBuilder sb = new StringBuilder();
				for (StackTraceElement element : e.getStackTrace()) {
					sb.append(element.toString());
					sb.append("\n");
				}
				return sb.toString();
			}
			
/* @Override
 public CommandProcessingResult processingClientDetails(PrepareRequestData requestData) {
	
PrepareRequest prepareRequest=this.prepareRequsetRepository.findOne(requestData.getRequestId());
try{
	String requestType=null;
	ProcessRequest processRequest=null;
	Long processResultId=Long.valueOf(0);
	Order order=this.orderRepository.findOne(requestData.getOrderId());
	AllocationDetailsData detailsData=this.allocationReadPlatformService.getTheHardwareItemDetails(requestData.getOrderId());
	requestType=requestData.getRequestType();
	PlanMapping planMapping= this.planMappingRepository.findOneByPlanId(order.getPlanId());
	
		if((requestData.getIshardwareReq().equalsIgnoreCase("Y") && detailsData == null) || planMapping == null){
			String status=OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getValue().toString();
			if(prepareRequest != null){
				prepareRequest.setStatus(status);
		 		this.prepareRequsetRepository.save(prepareRequest);
			}
		 
		 //Update Order Status
		 order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId());
		 this.orderRepository.saveAndFlush(order);
		
		}else {
			String HardWareId=null;
				if(detailsData!=null){
					HardWareId=detailsData.getSerialNo();
				}	
			processRequest=new ProcessRequest(requestData.getRequestId(),order.getClientId(), order.getId(),
		    requestData.getProvisioningSystem(),requestType,'N','N');
			List<OrderLine> orderLineData=order.getServices();
			JSONObject jsonObject = new JSONObject();
		 
		 if(planMapping != null){
			 jsonObject.put("planIdentification", planMapping.getPlanIdentification());
		 }
		 
		 JSONArray serviceArray = new JSONArray();
		 if(requestData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.CHANGE_PLAN.toString())){
			 Order oldOrder=this.orderRepository.findOldOrderByOrderNO(order.getOrderNo());
			 List<OrderLine> orderdetails=oldOrder.getServices();
			 planMapping= this.planMappingRepository.findOneByPlanId(oldOrder.getPlanId());
			 
			 if(planMapping != null){
				 jsonObject.put("oldPlanIdentification", planMapping.getPlanIdentification());
			 }
			 
			 for(OrderLine orderLine:orderdetails){
				 
				 JSONObject oldsubjson = new JSONObject();
				 List<ServiceMapping> provisionServiceDetails=this.provisionServiceDetailsRepository.findOneByServiceId(orderLine.getServiceId());
				 
				 ServiceMaster service=this.serviceMasterRepository.findOne(orderLine.getServiceId());
				 if(!provisionServiceDetails.isEmpty()){
					 oldsubjson.put("oldServiceIdentification", provisionServiceDetails.get(0).getServiceIdentification());
				 }
				 oldsubjson.put("oldServiceType", service.getServiceType());
				 serviceArray.add(oldsubjson);
			 }
			 jsonObject.put("oldServices", new Gson().toJson(serviceArray));
		 }

		 JSONArray newServiceArray = new JSONArray();
		 if(requestData.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.DEVICE_SWAP.toString())){
			 AllocationDetailsData allocationDetailsData=this.allocationReadPlatformService.getDisconnectedHardwareItemDetails(requestData.getOrderId(),requestData.getClientId());
			 jsonObject.put("clientId", order.getClientId());
			 jsonObject.put("OldHWId", allocationDetailsData.getSerialNo());
			 jsonObject.put("NewHWId", HardWareId);
		 }
		 if(requestType.equalsIgnoreCase(UserActionStatusTypeEnum.ADDON_ACTIVATION.toString())){
			 
			 List<OrderAddons> orderAddons=this.orderAddonsRepository.findAddonsByOrderId(requestData.getOrderId());
			 for(OrderAddons orderAddon:orderAddons){
				 
				 List<ServiceMapping> provisionServiceDetails=this.provisionServiceDetailsRepository.findOneByServiceId(orderAddon.getServiceId());
				 	ServiceMaster service=this.serviceMasterRepository.findOne(orderAddon.getServiceId());
				 	 JSONObject subjson = new JSONObject();
					 subjson.put("serviceName", service.getServiceCode());
					 subjson.put("addonId", requestData.getAddonId());
					 if(!provisionServiceDetails.isEmpty()){
						 subjson.put("serviceIdentification", provisionServiceDetails.get(0).getServiceIdentification());
					 }
					 subjson.put("serviceType", service.getServiceType());
					 newServiceArray.add(subjson.toString());	 
			 }
		 }else{
			 for(OrderLine orderLine:orderLineData){
				 List<ServiceMapping> provisionServiceDetails=this.provisionServiceDetailsRepository.findOneByServiceId(orderLine.getServiceId());
			 		ServiceMaster service=this.serviceMasterRepository.findOne(orderLine.getServiceId());
			 		JSONObject subjson = new JSONObject();
				 subjson.put("serviceName", service.getServiceCode());
				 if(!provisionServiceDetails.isEmpty()){
				 subjson.put("serviceIdentification", provisionServiceDetails.get(0).getServiceIdentification());
				 }
				 subjson.put("serviceType", service.getServiceType());
				 newServiceArray.add(subjson.toString());	 
			 }
		 }
		 jsonObject.put("services", new Gson().toJson(newServiceArray));
		 ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(orderLineData.get(0).getId(),
				 orderLineData.get(0).getServiceId(),jsonObject.toString(),"Recieved",
				 HardWareId,order.getStartDate(),order.getEndDate(),null,null,'N',requestType,null);
		 processRequest.add(processRequestDetails);
		 this.processRequestRepository.save(processRequest);				
		 String status=OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getValue().toString();
		 processResultId=processRequest.getId();
		 if(prepareRequest != null){
			 prepareRequest.setIsProvisioning('Y');
			 prepareRequest.setStatus(status);
			 this.prepareRequsetRepository.save(prepareRequest);
		 }
	 }
	 if(requestData.getProvisioningSystem().equalsIgnoreCase("None")){
		 order.setStatus(new Long(1));
		 this.orderRepository.save(order);
	 }
	 return new CommandProcessingResult(processResultId);	
 
	 }catch(Exception exception){
	 if(prepareRequest != null){
		 prepareRequest.setIsProvisioning('N');
		 prepareRequest.setStatus("Pending");
		 this.prepareRequsetRepository.save(prepareRequest);
	 }
	 exception.printStackTrace();
	 return new CommandProcessingResult(Long.valueOf(-1));
 }
 }*/

 @Override
 public List<Long> getPrepareRequestDetails(Long id) {
	 try {
		 
		 JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
		 PrepareRequestIdMapper planIdMapper = new PrepareRequestIdMapper();
		 String sql = "select" + planIdMapper.planIdSchema();
		 return jdbcTemplate.query(sql, planIdMapper,new Object[] { id });
	 } catch (EmptyResultDataAccessException e) {
		 return null;
	 }
 }
 private static final class PrepareRequestIdMapper implements RowMapper<Long> {
	 
	 @Override
	 public Long  mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		 return resultSet.getLong("id");
	 }
	 public String planIdSchema() {
		 return " pr.id as id from b_prepare_request pr " +
				 " where pr.order_id=? and pr.is_provisioning ='N' and pr.request_type='ACTIVATION'";
	 }
 }
 
 @Override
 public int getLastPrepareId(Long orderId) {
	 try {
		 JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
		 String sql = "select max(id)from b_prepare_request where order_id=? and request_type='ACTIVATION'";
		 
		 return jdbcTemplate.queryForInt(sql, new Object[] { orderId });
	 } catch (EmptyResultDataAccessException e) {
		 return 0;
	 }	
 	}		

}
