package org.mifosplatform.celcom.service;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import org.apache.axis2.AxisFault;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.mifosplatform.celcom.service.InfranetWebServiceServiceStub.Opcode;
import org.mifosplatform.celcom.service.InfranetWebServiceServiceStub.OpcodeResponse;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class CelcomReadWriteConsiliatrServiceImpl implements CelcomReadWriteConsiliatrService {

	private final CelcomReadPlatformService celcomReadPlatformService;
	private final CelcomWritePlatformService celcomWritePlatformService;
	
	@Autowired
	public CelcomReadWriteConsiliatrServiceImpl(@Lazy final CelcomReadPlatformService celcomReadPlatformService,
			@Lazy final CelcomWritePlatformService celcomWritePlatformService) {
		
		this.celcomReadPlatformService = celcomReadPlatformService;
		this.celcomWritePlatformService = celcomWritePlatformService;
	}

	@Override
	public Object celcomProcessCommandHandler(Map<String, Object> inputs) {
		Object obj = null;
		switch(inputs.get("target").toString()){
			case "client360":
				obj = this.celcomReadPlatformService.retriveClientTotalData((String)inputs.get("key"),(String)inputs.get("value"));
				break;
				
			case "createclient":
				obj = this.celcomWritePlatformService.createClient((JsonCommand)inputs.get("json"));
				break;
			case "createOffice":
				obj=this.celcomWritePlatformService.createOffice((JsonCommand)inputs.get("json"));
				break;
			case "getplans":
				obj = this.celcomReadPlatformService.retrivePlans((String)inputs.get("key"),(String)inputs.get("value"),(String)inputs.get("PlanTypeEnum"),(String)inputs.get("SearchTypeEnum"),
						(String)inputs.get("searchType"));
				break;
			case "createClientSimpleActivation":
				obj=this.celcomWritePlatformService.createClientSimpleActivation((JsonCommand)inputs.get("json"));
				break;

			case "createbillplan":
                obj=this.celcomWritePlatformService.createBillPlan((ProvisioningRequest)inputs.get("provisioningRequest"));
                break;
              
			case "updatePurchaseProduct":
                obj=this.celcomWritePlatformService.updatePurchaseProductPoId((Order)inputs.get("order"),(Set<String>)inputs.get("substances"));
                break;
            
			case "addPlan":
                obj=this.celcomWritePlatformService.addPlan((String)inputs.get("jsonString"));
                break;
               
			case "suspendClientService":
				obj=this.celcomWritePlatformService.suspendClientService((JsonCommand)inputs.get("command"));
             break;
             
			case "cancelPlan":
                obj=this.celcomWritePlatformService.cancelPlan((String)inputs.get("json"));
                break;
                
			case "terminateClientService":
				obj=this.celcomWritePlatformService.terminateClientService((JsonCommand)inputs.get("command"));
             break;
             
			case "reactivateClientService":
				obj=this.celcomWritePlatformService.reactivateClientService((JsonCommand)inputs.get("command"));
             break; 
             
			case "createPayment":
				obj=this.celcomWritePlatformService.createPayment((JsonCommand)inputs.get("command"));
             break;
             
			case "adjustments":
				obj=this.celcomWritePlatformService.createAdjustmentsCelcom((JsonCommand)inputs.get("command"));
				
			break;
			case "addPlans":
				obj=this.celcomWritePlatformService.addPlans((JsonCommand)inputs.get("command"));
             break;
             
			case "cancelPlans":
				obj=this.celcomWritePlatformService.cancelPlans((JsonCommand)inputs.get("command"));
             break;
             
			case "changePlan":
				obj=this.celcomWritePlatformService.changePlan((String)inputs.get("json"));
             break; 
             
			case "renewalPlan":
				obj=this.celcomWritePlatformService.renewalplan((JsonCommand)inputs.get("command"));
             break;
			case "createAgreement":
				obj=this.celcomWritePlatformService.createCelcomAgreement((JsonCommand)inputs.get("command"));
             break;     
			case "swapDevice":
				obj=this.celcomWritePlatformService.swapDevice((JsonCommand)inputs.get("command"));
             break;
			case "deleteAgreement":
				obj=this.celcomWritePlatformService.deleteAgreement((JsonCommand)inputs.get("command"));
             break;
             
			case "clientBilling":
				obj = this.celcomReadPlatformService.retriveClientBillData((String)inputs.get("key"),(String)inputs.get("value"));
				break; 
				
			case "updateOffice":
				obj=this.celcomWritePlatformService.updateOffice((JsonCommand)inputs.get("command"));
	        break; 
	        
			case "retriveOfficeData":
				obj=this.celcomReadPlatformService.retriveOfficeData((OfficeData)inputs.get("officeData"));
	        break; 
			
			case "createClientHardwarePlanActivation":
				obj=this.celcomWritePlatformService.createClientHardwarePlanActivation((JsonCommand)inputs.get("json"));
			break;
			
			case "updateCelcomClient":
			obj=this.celcomWritePlatformService.updateCelcomClient((Long)inputs.get("clientId"),(JsonCommand)inputs.get("json"));
			break;
			
			case "createOfficePayment":
			obj=this.celcomWritePlatformService.createOfficePayment((JsonCommand)inputs.get("command"));
            break;
            
			case "createOfficeAdjustmentsCelcom":
			obj=this.celcomWritePlatformService.createOfficeAdjustmentsCelcom((JsonCommand)inputs.get("command"));
	        break;
	        
			case "billDetails":
				obj=this.celcomReadPlatformService.retriveBillDetails((SearchSqlQuery)inputs.get("searchCodes"),(Long)inputs.get("clientId"));
	        break; 
	        
			case "cancelPayment":
			obj=this.celcomWritePlatformService.cancelPayment((JsonCommand)inputs.get("command"));
		    break;
		    
			case "createClientDiscount":
			obj=this.celcomWritePlatformService.createClientDiscount((JsonCommand)inputs.get("json"));
			break;
			case "cancelPaymentforOffice":
			obj=this.celcomWritePlatformService.cancelPaymentforOffice((JsonCommand)inputs.get("command"));
			break;
			case "createBillAdjustmentsCelcom":
			obj=this.celcomWritePlatformService.createBillAdjustmentsCelcom((JsonCommand)inputs.get("command"));
			break;
			case "BroadcasterConfigCelcom":
			obj=this.celcomWritePlatformService.BroadcasterConfigCelcom((JsonCommand)inputs.get("command"));
			break;
			
			case "ChannelConfigCelcom":
				obj=this.celcomWritePlatformService.ChannelConfigCelcom((JsonCommand)inputs.get("command"));
				break;
				
			
			
		}
		return obj;
	}
	
	
	
	
	@Override
	public String processCelcomRequest(String opCodeString,  String sOAPMessage){
		try {
			System.out.println("input payload "+sOAPMessage);
			InfranetWebServiceServiceStub stub = new InfranetWebServiceServiceStub();
			Opcode opcode = new Opcode();
			opcode.setOpcode(opCodeString);
			opcode.setInputXML(sOAPMessage);
			opcode.setM_SchemaFile("?");
			OpcodeResponse opcodeResponse = stub.opcode(opcode);
			return this.parseJSON(opcodeResponse.getOpcodeReturn(),opCodeString);

		} catch(AxisFault e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());
		} catch (RemoteException e) {
			throw new PlatformDataIntegrityException("remote.exception", e.getMessage(), e.getMessage(), e.getMessage());
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException("json.exception", e.getMessage(), e.getMessage(), e.getMessage());
		}
	}
	
	
	private String parseJSON(String result,String opCodeString ) throws JSONException{
		JSONObject object = XML.toJSONObject(result);
		System.out.println("output payload "+ object.toString());
		this.validate(object,opCodeString);
		return object.toString();
	}

	private void validate(JSONObject object,String opCodeString) {
		opCodeString = "brm:"+opCodeString+"_outputFlist";
		if(object.has(opCodeString)){
			object = object.optJSONObject(opCodeString);
			if("1".equalsIgnoreCase(object.optString("brm:STATUS"))){
				String errorDescriptor = object.optString("brm:ERROR_DESCR");
				throw new PlatformDataIntegrityException("brm.request.failed",errorDescriptor,errorDescriptor,errorDescriptor);
			}
		}
		
	}
	
	
}
