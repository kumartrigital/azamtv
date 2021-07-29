package org.mifosplatform.portfolio.activationprocess.service;

import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.logistics.hardwaredeviceplan.api.HardwareDevicePlanApiResource;
import org.mifosplatform.portfolio.clientservice.api.ClientServiceApiResource;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderLineRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class HardwarePlanActivationProcessWritePlatformServiceImpl implements HardwarePlanActivationProcessWritePlatformService{

	private final static Logger logger = LoggerFactory.getLogger(ActivationProcessWritePlatformServiceJpaRepositoryImpl.class);
	
	private final CrmServices crmServices;
	private final FromJsonHelper fromJsonHelper;
	private final ClientServiceApiResource clientServiceApiResource;
	private final OrderWritePlatformService orderWritePlatformService;
	private final HardwareDevicePlanApiResource hardwareDevicePlanApiResource;
	private final OrderRepository orderRepository;
	private final OrderLineRepository orderLineRepository;
	private final FromJsonHelper fromApiJsonHelper;
	
	@Autowired
	public HardwarePlanActivationProcessWritePlatformServiceImpl(final CrmServices crmServices,final FromJsonHelper fromJsonHelper,
		   final ClientServiceApiResource clientServiceApiResource,final OrderWritePlatformService orderWritePlatformService,final HardwareDevicePlanApiResource hardwareDevicePlanApiResource,
		   final OrderRepository orderRepository,final OrderLineRepository orderLineRepository,final FromJsonHelper fromApiJsonHelper) {
		this.crmServices = crmServices;
		this.fromJsonHelper = fromJsonHelper;
		this.clientServiceApiResource = clientServiceApiResource;
		this.orderWritePlatformService = orderWritePlatformService;
		this.hardwareDevicePlanApiResource = hardwareDevicePlanApiResource;
		this.orderRepository = orderRepository;
		this.orderLineRepository = orderLineRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	// using this method for client activation using hardwareplan
	    @Transactional
	    @Override
		public CommandProcessingResult createClientHardwarePlanActivation(JsonCommand command, Long clientId) {
			
			Long clientServiceId = null;
	    	JsonCommand comm=null;
	    	String clientServicePoId=null;
	    	Set<String> substances = null;
	        CommandProcessingResult cmd=null;
	        
	    	try{
	    		
	    		CommandProcessingResult result = this.crmServices.createClientHardwarePlanActivation(command);
	    		if(result != null){
	    			clientServicePoId = result.getResourceIdentifier();
	    			substances = result.getSubstances();
	    		}
	    		
	    		final JsonElement element = fromJsonHelper.parse(command.json());
	    		
		        JsonArray clientServiceDataArray = fromJsonHelper.extractJsonArrayNamed("clientServiceData", element);
		        if(clientServiceDataArray.size() != 0){
		        	for(JsonElement clientServiceData:clientServiceDataArray){
		        		JsonObject clientService = clientServiceData.getAsJsonObject();
		        		clientService.addProperty("clientId",clientId);
		        		String temp1=null;
		        		if(result!=null){
		        			temp1=result.getResourceIdentifier();
			        	}
		        		clientService.addProperty("clientservicePoId", clientServicePoId);
		        		String resultClientService = this.clientServiceApiResource.create(clientService.toString());
		        		JSONObject clientServiiceObject = new JSONObject(resultClientService);
		        		clientServiceId = clientServiiceObject.getLong("resourceId");
		        		break;
		           }
		        }else{
		        	this.throwError("Client Service");
		        }
	    		
		      //this is for add Hardwareplan
		       cmd=this.orderFun(clientId,clientServiceId,substances,element,"Hardware");
		        
		        
		     // this for add device
	    		JsonArray deviceDataArray = fromJsonHelper.extractJsonArrayNamed("deviceData", element);
	    		if(deviceDataArray.size() != 0){
	    			for(JsonElement deviceDataElement:deviceDataArray){
	    				JsonElement deviceData = this.addingclientAndclientServiceTodevice(deviceDataElement,clientId,clientServiceId,cmd);
	    				JSONObject pairable = new JSONObject(deviceData.toString());
	    				
	    				if(pairable.has("pairableItemDetails")){
	    					String pairableItemDetails = pairable.getString("pairableItemDetails");
	    					deviceData = addingclientAndclientServiceTodevice(fromJsonHelper.parse(pairableItemDetails),clientId,clientServiceId,cmd);
	    					pairable.put("pairableItemDetails", String.valueOf(deviceData));
	    				}
	    				this.hardwareDevicePlanApiResource.createHardwareDevicePlanSale(clientId, "NEWSALE", pairable.toString());
	    				break;
	    			}
	    			
	    		}
	    		else{
	    			this.throwError("Device");
	    		}
	    		
	    		//this is for add plan
	    		 this.orderFun(clientId,clientServiceId,substances,element,"General");
		        
		        
		        //client service activation 
		         JSONObject clientServiceActivationObject = new JSONObject();
		         clientServiceActivationObject.put("clientId", clientId);
		         this.clientServiceApiResource.createClientServiceActivation(clientServiceId, clientServiceActivationObject.toString());
	    		
	    		return new CommandProcessingResultBuilder().withClientId(clientId).build();
	    	}catch (DataIntegrityViolationException dve) {
	        	
	            handleDataIntegrityIssues(command, dve);
	            return new CommandProcessingResult(Long.valueOf(-1));
	        }catch (JSONException dve){
	        	 throw new PlatformDataIntegrityException("error.msg.client.jsonexception.", "JSON Exception Occured");
	        }
		}
	    
	    private CommandProcessingResult orderFun(Long clientId,Long clientServiceId,Set<String> substances,
	    		JsonElement element,String type){
	    	CommandProcessingResult cmd=null;
	    	JsonArray planDataArray = fromJsonHelper.extractJsonArrayNamed("planData", element);
	    	if(planDataArray.size() != 0){
	        	for(JsonElement planDataElement:planDataArray){
	        		JsonObject planData = planDataElement.getAsJsonObject();
	        		planData.addProperty("clientServiceId",clientServiceId);planDataElement = planData;
	        		planData.addProperty("orderNo",this.retriveOrderNo(planData,substances));
	        		planDataElement = planData;
	        		final String planTypeName = fromApiJsonHelper.extractStringNamed("planTypeName", planDataElement);
		        		if(planTypeName.equals(type)){
		        			JsonCommand comm=new JsonCommand(null, planDataElement.toString(),planDataElement, this.fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
			        		cmd=this.orderWritePlatformService.createOrder(clientId,comm,null);
				        	Order order = this.orderRepository.findOne(cmd.resourceId());
			        		if(substances !=null){this.updatingPurchaseProductPoIdinOrderLine(order,substances);}
			        		break;
			        	}
	        		}
	        }
	        else{
	        	this.throwError("Plan");
	        }
	    	return cmd;
	    }
	    
	    
	    private void updatingPurchaseProductPoIdinOrderLine(Order order,Set<String> substances) {
	    	
			List<OrderLine> orderlines = order.getServices();
			for(OrderLine orderLine:orderlines){
				for(String substance:substances){
					if((this.getValueFromSubstance(substance,0)).equalsIgnoreCase(order.getOrderNo()) &&(this.getValueFromSubstance(substance,2)).equalsIgnoreCase(String.valueOf(orderLine.getProductPoId()))){
						orderLine.setPurchaseProductPoId(Long.valueOf(this.getValueFromSubstance(substance,3)));
						break;
					}
				}
				this.orderLineRepository.saveAndFlush(orderLine);
			}
			
		}
	    
	    
	    private String retriveOrderNo(JsonObject planData, Set<String> substances) {
			JSONObject object = null;String returnValue = null;
			try {
				String obj = planData.toString();
				object = new JSONObject(obj);
				if(substances !=null){
				for(String value:substances){
					if(this.getValueFromSubstance(value,1).equalsIgnoreCase(object.optString("planPoId"))){
						returnValue = this.getValueFromSubstance(value,0);break;
					}
				}
			}
			return returnValue;
			} catch (JSONException e) {
				throw new PlatformDataIntegrityException("order.no.exception", "JSON Exception Occured");
			}
		}
	    
	    private String getValueFromSubstance(String value, int i) {
			String arr[] = value.split("_");
			System.out.println(arr[i]);
			return arr[i];
		}
		
	    
	    
		private void throwError(String dest){
	    	 throw new PlatformDataIntegrityException("error.msg."+dest+".not.found", dest+" Not Found");
	    }
		
		private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

	        Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("external_id")) {

	            final String externalId = command.stringValueOfParameterNamed("externalId");
	            throw new PlatformDataIntegrityException("error.msg.client.duplicate.externalId", "Client with externalId `" + externalId
	                    + "` already exists", "externalId", externalId);
	        } else if (realCause.getMessage().contains("account_no_UNIQUE")) {
	            final String accountNo = command.stringValueOfParameterNamed("accountNo");
	            throw new PlatformDataIntegrityException("error.msg.client.duplicate.accountNo", "Client with accountNo `" + accountNo
	                    + "` already exists", "accountNo", accountNo);
	        }else if (realCause.getMessage().contains("email_key")) {
	            final String email = command.stringValueOfParameterNamed("email");
	            throw new PlatformDataIntegrityException("error.msg.client.duplicate.email", "Client with email `" + email
	                    + "` already exists", "email", email);
	        }

	        logAsErrorUnexpectedDataIntegrityException(dve);
	        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource.");
	    }
		
		private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
	        logger.error(dve.getMessage(), dve);
	    }
		
		private JsonElement addingclientAndclientServiceTodevice(JsonElement deviceDataElement, Long clientId,Long clientServiceId,CommandProcessingResult cmd) {
	    	JsonObject deviceData = deviceDataElement.getAsJsonObject();
			deviceData.addProperty("clientServiceId",clientServiceId);
			JsonArray deviceArray = deviceData.getAsJsonArray("serialNumber");
			for(JsonElement deviceArrayElement:deviceArray){
				JsonObject  deviceArrayObject = deviceArrayElement.getAsJsonObject();
				deviceArrayObject.addProperty("clientId", clientId);
				deviceArrayObject.addProperty("orderId", clientId);
				deviceArrayObject.addProperty("resourceId", cmd.resourceId());
			}
	    	return deviceData;
	    }
	
}
