package org.mifosplatform.logistics.hardwaredeviceplan.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.logistics.hardwaredeviceplan.api.HardwareDevicePlanApiResource;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsWritePlatformService;
import org.mifosplatform.logistics.onetimesale.api.OneTimeSalesApiResource;
import org.mifosplatform.portfolio.order.api.OrdersApiResource;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class HardwarePlansWritePlatformServiceImpl implements HardwarePlansWritePlatformService {

	private final FromJsonHelper fromJsonHelper;
	private final OneTimeSalesApiResource oneTimeSalesApiResource;
	private final OrderWritePlatformService orderWritePlatformService;
	private final OrdersApiResource ordersApiResource;
	private final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	private final HardwareDevicePlanApiResource hardwareDevicePlanApiResource;
	
	
	@Autowired
	public HardwarePlansWritePlatformServiceImpl(final FromJsonHelper fromJsonHelper,final OneTimeSalesApiResource oneTimeSalesApiResource,final OrderWritePlatformService orderWritePlatformService,
			final OrdersApiResource ordersApiResource,final ItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService,
			final HardwareDevicePlanApiResource hardwareDevicePlanApiResource){
		this.fromJsonHelper = fromJsonHelper;
		this.oneTimeSalesApiResource = oneTimeSalesApiResource;
		this.orderWritePlatformService = orderWritePlatformService;
		this.ordersApiResource = ordersApiResource;
		this.inventoryItemDetailsWritePlatformService = inventoryItemDetailsWritePlatformService;
	    this.hardwareDevicePlanApiResource = hardwareDevicePlanApiResource;
	}

	@Transactional
	@Override
	public CommandProcessingResult createHardwarePlans(JsonCommand command,final Long clientId,final Long clientServiceId) {
		/*Long clientServiceId = (long) 1;*/
    	JsonCommand comm=null;
    	CommandProcessingResult cmd=null;
    	try{
    		final JsonElement element = fromJsonHelper.parse(command.json());
    		
    		//this is for add plan
    		JsonArray planDataArray = fromJsonHelper.extractJsonArrayNamed("planData", element);
	        if(planDataArray.size() != 0){
	        	for(JsonElement planDataElement:planDataArray){
	        		JsonObject planData = planDataElement.getAsJsonObject();
	        		planData.addProperty("clientServiceId",clientServiceId);planDataElement = planData;
	        		comm=new JsonCommand(null, planDataElement.toString(),planDataElement, this.fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null,null);
		        	cmd=this.orderWritePlatformService.createOrder(clientId,comm,null);break;
	        	}
	        }
	        else{
	        	this.throwError("Plan");
	        }
	        
    		
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
    		
    		
	        return new CommandProcessingResultBuilder().withClientId(clientId).build();
	        
    	}catch (DataIntegrityViolationException dve) {
        	
            handleDataIntegrityIssues(command, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }catch (JSONException dve){
        	 throw new PlatformDataIntegrityException("error.msg.client.jsonexception.", "JSON Exception Occured");
        }
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

	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {
	        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource.");
		
	}

	private void throwError(String dest){
    	 throw new PlatformDataIntegrityException("error.msg."+dest+".not.found", dest+" Not Found");
    }
	
}
