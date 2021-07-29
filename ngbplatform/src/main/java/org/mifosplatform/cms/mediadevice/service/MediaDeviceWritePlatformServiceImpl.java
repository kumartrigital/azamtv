package org.mifosplatform.cms.mediadevice.service;


import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.billing.selfcare.exception.SelfCareIdNotFoundException;
import org.mifosplatform.billing.selfcare.service.SelfCareRepository;
import org.mifosplatform.cms.mediadevice.exception.DeviceDetailsInActiveException;
import org.mifosplatform.cms.mediadevice.exception.DeviceIdNotFoundException;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.ownedhardware.data.OwnedHardware;
import org.mifosplatform.logistics.ownedhardware.domain.OwnedHardwareJpaRepository;
import org.mifosplatform.organisation.message.service.MessagePlatformEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class MediaDeviceWritePlatformServiceImpl implements MediaDeviceWritePlatformService {
	
	private final static Logger logger = LoggerFactory.getLogger(MediaDeviceWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final OwnedHardwareJpaRepository ownedHardwareJpaRepository;
	private final MediaDeviceReadPlatformService mediaDeviceReadPlatformService;
	private final SelfCareRepository selfCareRepository;
	private final MessagePlatformEmailService messagePlatformEmailService;
	private final ConfigurationRepository configurationRepository;
	
	@Autowired
	public MediaDeviceWritePlatformServiceImpl(final PlatformSecurityContext context,
			final OwnedHardwareJpaRepository ownedHardwareJpaRepository,
			final MediaDeviceReadPlatformService mediaDeviceReadPlatformService, 
			final SelfCareRepository selfCareRepository, 
			final MessagePlatformEmailService messagePlatformEmailService,
			final ConfigurationRepository configurationRepository){
		
		this.context = context;
		this.ownedHardwareJpaRepository  = ownedHardwareJpaRepository;
		this.mediaDeviceReadPlatformService = mediaDeviceReadPlatformService;
		this.selfCareRepository = selfCareRepository;
		this.messagePlatformEmailService = messagePlatformEmailService;
		this.configurationRepository = configurationRepository;
		
	}

	@Override
	public CommandProcessingResult updateMediaDetailsStatus(JsonCommand command) {
		  try{
				this.context.authenticatedUser();
				Long clientId = command.longValueOfParameterNamed("clientId");
				String deviceId  = command.getSupportedEntityType();
				OwnedHardware ownedHardware = mediaDetailsRetrieveById(deviceId);
				String status = command.stringValueOfParameterNamed("status");
				
				if(status.equalsIgnoreCase("ACTIVE")){
					
					Long deviceIds = this.mediaDeviceReadPlatformService.retrieveDeviceDataDetails(clientId,ownedHardware.getProvisioningSerialNumber());
					
						if(deviceIds == 0){
							ownedHardware.setStatus("ACTIVE");
						}
						else{
							throw new DeviceDetailsInActiveException(deviceId);
						}
				}
				else{

					ownedHardware.setStatus(status);

				}
				return new CommandProcessingResult(deviceId);
		  }catch (DataIntegrityViolationException dve) {
				handleCodeDataIntegrityIssues(command, dve);
				return  CommandProcessingResult.empty();
		  }
	}
	
	private OwnedHardware mediaDetailsRetrieveById(String deviceId) {
	
		OwnedHardware ownedHardware = this.ownedHardwareJpaRepository.findByProvisioningSerialNumber(deviceId);
		if(ownedHardware==null){throw new DeviceIdNotFoundException(deviceId);}
		return ownedHardware;
	}

	private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
		
		Throwable realCause = dve.getMostSpecificCause();

	        logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
		
	}

	@Override
	public CommandProcessingResult updateMediaDetailsCrashStatus(Long clientId,JsonCommand command) {
		try{
			String message = null;
			this.context.authenticatedUser();
			String crashReportString = command.stringValueOfParameterNamed("crashReportString");
			SelfCare selfCare = SelfCareRetrieveByClientId(clientId);
			selfCare.setStatus("INACTIVE");	
			
			Configuration configurationData = this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_MEDIA_CRASH_EMAIL);
			
			if(configurationData != null && configurationData.isEnabled() && configurationData.getValue() != null){
				message = this.messagePlatformEmailService.sendGeneralMessage(configurationData.getValue(), crashReportString, "Crash Api Report");
				if(message.equalsIgnoreCase("Success")){
					message = clientId.toString();
				}
			}
			
			if(message == null){
				message = "Please Enable the configuration Property";
			}
			
			return new CommandProcessingResult(message);
			
	  }catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return  CommandProcessingResult.empty();
	  }
	}

	private SelfCare SelfCareRetrieveByClientId(Long clientId) {
		
		SelfCare selfCare = selfCareRepository.findOneByClientId(clientId);
		if(selfCare==null){throw new SelfCareIdNotFoundException(clientId);}
		return selfCare;
	}


}
