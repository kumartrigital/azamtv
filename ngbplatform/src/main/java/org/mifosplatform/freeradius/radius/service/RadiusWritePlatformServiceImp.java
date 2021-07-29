package org.mifosplatform.freeradius.radius.service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.mifosplatform.billing.discountmaster.service.DiscountWritePlatformServiceImpl;
import org.mifosplatform.freeradius.radius.exception.RadiusDetailsNotFoundException;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.domain.RadServiceTemp;
import org.mifosplatform.portfolio.order.domain.RadServuceTempRepository;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.processscheduledjobs.service.SheduleJobReadPlatformService;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisionActions;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisioningActionsRepository;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RadiusWritePlatformServiceImp implements RadiusWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(DiscountWritePlatformServiceImpl.class);
	
	private final PlatformSecurityContext context;
	private final RadServuceTempRepository radServiceRepository;
	private final ProvisioningActionsRepository provisioningActionsRepository;
	private final ProcessRequestRepository processRequestRepository;
	private final SheduleJobReadPlatformService sheduleJobReadPlatformService;
	private final RadiusReadPlatformService radiusReadPlatformService;
	private final ConfigurationRepository repository;

	@Autowired
	public RadiusWritePlatformServiceImp(final PlatformSecurityContext context,final RadServuceTempRepository radServiceRepository,
			final ProvisioningActionsRepository provisioningActionsRepository,final ProcessRequestRepository processRequestRepository,
			final SheduleJobReadPlatformService sheduleJobReadPlatformService,final RadiusReadPlatformService radiusReadPlatformService,
			final ConfigurationRepository repository) {
		this.context = context;
		this.radServiceRepository = radServiceRepository;
		this.provisioningActionsRepository = provisioningActionsRepository;
		this.processRequestRepository =  processRequestRepository;
		this.sheduleJobReadPlatformService = sheduleJobReadPlatformService;
		this.radiusReadPlatformService = radiusReadPlatformService;
		this.repository = repository;
	}

	@Transactional
	@Override
	public CommandProcessingResult updateRadService(final Long radServiceId,final JsonCommand command) {
		
		try{
			context.authenticatedUser();
			RadServiceTemp radService=this.radServiceRetrieveById(radServiceId);
			final Map<String, Object> changes = radService.update(command);
			if(!changes.isEmpty()){
				this.radServiceRepository.saveAndFlush(radService);
				ProvisionActions provisionActions=this.provisioningActionsRepository.findOneByProvisionType(ProvisioningApiConstants.PROV_EVENT_UPDATE_RADSERVICE);
				if(provisionActions.getIsEnable() == 'Y'){
					 JSONObject jsonObject = new JSONObject(changes);//preparing jsonObject with changes
					 ProcessRequest processRequest = new ProcessRequest(Long.valueOf(0), Long.valueOf(0), Long.valueOf(0),
							 provisionActions.getProvisioningSystem(),provisionActions.getAction(), 'N', 'N');

					 ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(Long.valueOf(0),
							 radServiceId, jsonObject.toString(), "Recieved",
							 null, new Date(), null, null, null, 'N', provisionActions.getAction(), null);
					 processRequest.add(processRequestDetails);
					 this.processRequestRepository.save(processRequest);
			        }	
		         }
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
				       .withEntityId(radService.getserviceId()).with(changes).build();
		}catch(final DataIntegrityViolationException dve){
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	@Transactional
	@Override
	public CommandProcessingResult deleteRadService(final Long radServiceId) {
		
     try {
		  JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
		  
		  if(data == null){
			throw new RadiusDetailsNotFoundException();
		   }
		  if(data.getProvSystem().equalsIgnoreCase("version-1")){
			  String url = data.getUrl() + "radservice/"+radServiceId;
		      String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
		      byte[] encoded = Base64.encodeBase64(credentials.getBytes());
		      String encodedPassword = new String(encoded);
			  String radServiceData = this.radiusReadPlatformService.processRadiusDelete(url, encodedPassword);
			  return new CommandProcessingResult(radServiceData);
		 }else{
			  final Configuration property = this.repository.findOneByName("freeradius_rest");
			  if(property == null){
					throw new RadiusDetailsNotFoundException();
			  }
			  String url = property.getValue() + "radservice/"+radServiceId;
		      String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
		      byte[] encoded = Base64.encodeBase64(credentials.getBytes());
		      String encodedPassword = new String(encoded);
			  String radServiceData = this.radiusReadPlatformService.processRadiusDelete(url, encodedPassword);
			  
		      /*RadServiceTemp radService=this.radServiceRetrieveById(radServiceId);
		      this.radServiceRepository.delete(radService.getserviceId());*/
		      /*ProvisionActions provisionActions=this.provisioningActionsRepository.findOneByProvisionType(ProvisioningApiConstants.PROV_EVENT_REMOVE_RADSERVICE);
				if(provisionActions.getIsEnable() == 'Y'){
					 ProcessRequest processRequest = new ProcessRequest(Long.valueOf(0), Long.valueOf(0), Long.valueOf(0),provisionActions.getProvisioningSystem(),
							 provisionActions.getAction(), 'N', 'N');
					 ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(Long.valueOf(0), radServiceId, new JSONObject().toString(), "Recieved",
							 null, new Date(), null, null, null, 'N', provisionActions.getAction(), null);
					 processRequest.add(processRequestDetails);
					 this.processRequestRepository.save(processRequest);
			        }*/
		      return new CommandProcessingResult(radServiceId);
		}
       }catch (IOException e) {
    	   
    		return new CommandProcessingResult(e.getMessage());
		}catch (final DataIntegrityViolationException dve) {
			throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
					"Unknown data integrity issue with resource: "+ dve.getMessage());
		}
}
	
	private RadServiceTemp radServiceRetrieveById(final Long entityId) {

		RadServiceTemp radService = this.radServiceRepository.findOne(entityId);
		if (radService == null) {
			throw new RadiusDetailsNotFoundException(entityId);
		}
		return radService;
	}
	
	private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();
		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "+ realCause.getMessage());
	}

}
