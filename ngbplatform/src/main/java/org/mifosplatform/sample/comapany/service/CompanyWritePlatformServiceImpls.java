package org.mifosplatform.sample.comapany.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.sample.comapany.domain.CompanyRegistration;
import org.mifosplatform.sample.comapany.domain.CompanyRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class CompanyWritePlatformServiceImpls implements CompanyWritePlatformService {

	@Autowired
	private PlatformSecurityContext context;
	
	@Autowired 
	private CompanyRegistrationRepository companyRepo;
	
	@Override
	public CommandProcessingResult create(JsonCommand command) {
		
		try{
		
		this.context.authenticatedUser();
		//this.apiJsonDeserializer.validateForCreate(command.json());
		final CompanyRegistration company = CompanyRegistration.formJson(command);
		//this.crmServices.broadcasterconfig(command);
		this.companyRepo.save(company);
		return new CommandProcessingResultBuilder().withEntityId(company.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
	
	@Override
	public CommandProcessingResult updateBroadcaster(JsonCommand command, Long entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandProcessingResult deleteBroadcaster(Long entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
