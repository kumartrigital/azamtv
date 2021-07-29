package org.mifosplatform.crm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.metamodel.binding.EntityIdentifier;
import org.mifosplatform.celcom.service.CelcomReadWriteConsiliatrService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.chargeorder.data.BillDetailsData;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.obrm.service.ObrmReadWriteConsiliatrService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CRMServicesImpl implements CrmServices{

	private final ConfigurationRepository configurationRepository;
	private final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService;
	private final CelcomReadWriteConsiliatrService celcomReadWriteConsiliatrService;
	 
	@Autowired
	public CRMServicesImpl(ConfigurationRepository configurationRepository,
			final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService,
			final CelcomReadWriteConsiliatrService celcomReadWriteConsiliatrService) {
		
		this.configurationRepository = configurationRepository;
		this.obrmReadWriteConsiliatrService = obrmReadWriteConsiliatrService;
		this.celcomReadWriteConsiliatrService = celcomReadWriteConsiliatrService;
	}

	@Override
	public ClientData retriveClientTotalData(String key, String value) {
		String targetCCrm = null;
		if((targetCCrm = this.findOneTargetcCrm()) != null){
			Map<String,Object> inputs = new HashMap<String,Object>();
			inputs.put("key", key);inputs.put("value", value);inputs.put("target","client360");
			return (ClientData)this.processRequestCommandHandler(targetCCrm,inputs);
		}else{
			return null;
		}
	}
	
	@Override
	public CommandProcessingResult createClient(JsonCommand command) {
		String targetCCrm = null;
		if((targetCCrm = this.findOneTargetcCrm()) != null){
			Map<String,Object> inputs = new HashMap<String,Object>();
			inputs.put("json", command);
			inputs.put("target","createclient");
			return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
		}else{
			return null;
		}
		
	}
	


	@Override
	public CommandProcessingResult createOffice(JsonCommand command) {
		String targetCCrm = null;
		if((targetCCrm = this.findOneTargetcCrm()) != null){
			Map<String,Object> inputs = new HashMap<String,Object>();
			inputs.put("json", command);
			inputs.put("target","createOffice");
			return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
		}else{
			return null;
		}
		
	}
	
	
	@Override
	public CommandProcessingResult createClientSimpleActivation(JsonCommand command) {
		String targetCCrm = null;
		if((targetCCrm = this.findOneTargetcCrm()) != null){
			Map<String,Object> inputs = new HashMap<String,Object>();
			inputs.put("json", command);
			inputs.put("target","createClientSimpleActivation");
			return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
		}else{
			return null;
		}
		
	}


	
	@Override
	public List<PlanData> retrivePlans(String key, String value) {
		String targetCCrm = null;
		if((targetCCrm = this.findOneTargetcCrm()) != null){
			Map<String,Object> inputs = new HashMap<String,Object>();
			inputs.put("key", key);inputs.put("value", value);inputs.put("target","getplans");
			return (List<PlanData>)this.processRequestCommandHandler(targetCCrm,inputs);
		}else{
			return null;
		}
	}
	
	
	@Override
	public CommandProcessingResult createBillPlan(ProvisioningRequest provisioningRequest) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("provisioningRequest", provisioningRequest);
            inputs.put("target","createbillplan");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }


	}
	
	
	@Override
	public CommandProcessingResult updatingPurchaseProductPoIdinOrderLine(Order order, Set<String> substances) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("order", order);
            inputs.put("substances", substances);
            inputs.put("target","updatePurchaseProduct");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}

	@Override
	public CommandProcessingResult addPlan(String apiRequestBodyAsJson) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("jsonString", apiRequestBodyAsJson);
            inputs.put("target","addPlan");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}

	@Override
	public CommandProcessingResult suspendClientService(JsonCommand command) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","suspendClientService");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}
	
	@Override
	public CommandProcessingResult cancelPlan(String json) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("json", json);
            inputs.put("target","cancelPlan");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}

	@Override
	public CommandProcessingResult terminateClientService(JsonCommand command) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","terminateClientService");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}
	
	@Override
	public CommandProcessingResult reactivateClientService(JsonCommand command) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","reactivateClientService");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}
	
	
	
	@Override
		public CommandProcessingResult createPayment(JsonCommand command) {
			String targetCCrm = null;
	        if((targetCCrm = this.findOneTargetcCrm()) != null){
	            Map<String,Object> inputs = new HashMap<String,Object>();
	            inputs.put("command", command);
	            inputs.put("target","createPayment");
	            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
	        }else{
	            return null;
	        }
		}
	
	@Override
	public CommandProcessingResult addPlans(JsonCommand command) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","addPlans");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}
	
	@Override
	public CommandProcessingResult adjustments(JsonCommand command) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","adjustments");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}
	@Override
	public CommandProcessingResult billadjustment(JsonCommand command) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","createBillAdjustmentsCelcom");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}

	
	@Override
	public
	CommandProcessingResult cancelPlans(JsonCommand command) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","cancelPlans");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}
	

	
	@Override
	public CommandProcessingResult changePlan(String json) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("json", json);
            inputs.put("target","changePlan");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}    
    @Override
	public CommandProcessingResult renewalPlan(JsonCommand command) {
		String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","renewalPlan");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}
    
    @Override
   	public CommandProcessingResult createAgreement(JsonCommand command) {
   		String targetCCrm = null;
           if((targetCCrm = this.findOneTargetcCrm()) != null){
               Map<String,Object> inputs = new HashMap<String,Object>();
               inputs.put("command", command);
               inputs.put("target","createAgreement");
               return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
           }else{
               return null;
           }
   	}

    @Override
	public CommandProcessingResult swapDevice(JsonCommand command) {
    	String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","swapDevice");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}
	
    @Override
	public CommandProcessingResult getClientBills(String key, String value) {
    	String targetCCrm=null;
    	if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("key", key);
            inputs.put("value", value);
            inputs.put("target","clientBilling");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
    	}
    }

	@Override
	public CommandProcessingResult deleteAgreement(JsonCommand command) {
    	String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","deleteAgreement");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
	}
    @Override
	public CommandProcessingResult updateOffice(JsonCommand command) {
    	String targetCCrm = null;
        if((targetCCrm = this.findOneTargetcCrm()) != null){
            Map<String,Object> inputs = new HashMap<String,Object>();
            inputs.put("command", command);
            inputs.put("target","updateOffice");
            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
        }else{
            return null;
        }
    }

    @Override
  	public OfficeData retriveOfficeData(OfficeData officeData) {
      	String targetCCrm=null;
      	if((targetCCrm = this.findOneTargetcCrm()) != null){
              Map<String,Object> inputs = new HashMap<String,Object>();
              inputs.put("officeData", officeData);
              inputs.put("target","retriveOfficeData");
              return (OfficeData)this.processRequestCommandHandler(targetCCrm,inputs);
          }else{
              return null;
      	}
      }
	@Override
	public CommandProcessingResult createClientHardwarePlanActivation(JsonCommand command) {
		String targetCCrm = null;
		if((targetCCrm = this.findOneTargetcCrm()) != null){
			Map<String,Object> inputs = new HashMap<String,Object>();
			inputs.put("json", command);
			inputs.put("target","createClientHardwarePlanActivation");
			return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
		}else{
			return null;
		}
		
	}

	
	 @Override
		public CommandProcessingResult updateCelcomClient(JsonCommand command) {
	    	String targetCCrm = null;
	        if((targetCCrm = this.findOneTargetcCrm()) != null){
	            Map<String,Object> inputs = new HashMap<String,Object>();
	            inputs.put("json", command);
	            inputs.put("clientId",command.entityId());
	            inputs.put("target","updateCelcomClient");
	            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
	        }else{
	            return null;
	        }
	    }
	
	 
	 @Override
		public CommandProcessingResult createOfficePayment(JsonCommand command) {
			String targetCCrm = null;
	        if((targetCCrm = this.findOneTargetcCrm()) != null){
	            Map<String,Object> inputs = new HashMap<String,Object>();
	            inputs.put("command", command);
	            inputs.put("target","createOfficePayment");
	            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
	        }else{
	            return null;
	        }
		}
	 
	 @Override
		public CommandProcessingResult createOfficeAdjustmentsCelcom(JsonCommand command) {
			String targetCCrm = null;
	        if((targetCCrm = this.findOneTargetcCrm()) != null){
	            Map<String,Object> inputs = new HashMap<String,Object>();
	            inputs.put("command", command);
	            inputs.put("target","createOfficeAdjustmentsCelcom");
	            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
	        }else{
	            return null;
	        }
		}
	
	
	 @Override
		public List<BillDetailsData> billDetails(SearchSqlQuery searchCodes,Long clientId) {
	    	String targetCCrm = null;
	        if((targetCCrm = this.findOneTargetcCrm()) != null){
	            Map<String,Object> inputs = new HashMap<String,Object>();
	            inputs.put("clientId", clientId);
	            inputs.put("searchCodes", searchCodes);
	            inputs.put("target","billDetails");
	            return (List<BillDetailsData>)this.processRequestCommandHandler(targetCCrm,inputs);
	        }else{
	            return null;
	        }
	 }
		public CommandProcessingResult cancelPayment(JsonCommand command) {
			String targetCCrm = null;
	        if((targetCCrm = this.findOneTargetcCrm()) != null){
	            Map<String,Object> inputs = new HashMap<String,Object>();
	            inputs.put("command", command);
	            inputs.put("target","cancelPayment");
	            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
	        }else{
	            return null;
	        }
		}
		
		@Override
		public CommandProcessingResult createClientDiscount(JsonCommand command) {
			String targetCCrm = null;
			if((targetCCrm = this.findOneTargetcCrm()) != null){
				Map<String,Object> inputs = new HashMap<String,Object>();
				inputs.put("json", command);
				inputs.put("target","createClientDiscount");
				return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
			}else{
				return null;
			}
			
		}
		
		public CommandProcessingResult cancelPaymentforOffice(JsonCommand command) {
			String targetCCrm = null;
	        if((targetCCrm = this.findOneTargetcCrm()) != null){
	            Map<String,Object> inputs = new HashMap<String,Object>();
	            inputs.put("command", command);
	            inputs.put("target","cancelPaymentforOffice");
	            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
	        }else{
	            return null;
	        }
		}
	
		@Override
		public CommandProcessingResult broadcasterconfig(JsonCommand command) {
			String targetCCrm = null;
	        if((targetCCrm = this.findOneTargetcCrm()) != null){
	            Map<String,Object> inputs = new HashMap<String,Object>();
	            inputs.put("command", command);
	            inputs.put("target","BroadcasterConfigCelcom");
	            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
	        }else{
	            return null;
	        }
		}
	
		@Override
		public CommandProcessingResult channelconfig(JsonCommand command) {
			String targetCCrm = null;
	        if((targetCCrm = this.findOneTargetcCrm()) != null){
	            Map<String,Object> inputs = new HashMap<String,Object>();
	            inputs.put("command", command);
	            inputs.put("target","ChannelConfigCelcom");
	            return (CommandProcessingResult)this.processRequestCommandHandler(targetCCrm,inputs);
	        }else{
	            return null;
	        }
		}
	
	
	
	
	
	
	
	
	
	@Override
	public String findOneTargetcCrm() {
		try{ 
			final Configuration configuration=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_CRM_ENABLE);
		     if(null != configuration && configuration.isEnabled()){
		    	 return configuration.getValue();
		     }else{
		    	 return null;
		     }
		}catch(Exception e){
			 throw new PlatformDataIntegrityException("error.msg.configuration.not.available", e.getMessage());
		}
	}
	
	private Object processRequestCommandHandler(String targetCCrm, Map<String, Object> inputs) {
		switch(targetCCrm){
			case "Oracle":
				return this.obrmReadWriteConsiliatrService.obrmProcessCommandHandler(inputs);
			case "Celcom":
				return this.celcomReadWriteConsiliatrService.celcomProcessCommandHandler(inputs);
				
			default:
				 throw new PlatformDataIntegrityException("invalid.target.ccrm", "","","");	
		}
	}


	
	
	
}
