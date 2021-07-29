package org.mifosplatform.portfolio.client.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.domain.ClientBillProfileInfo;
import org.mifosplatform.portfolio.client.domain.ClientBillProfileInfoRepository;
import org.mifosplatform.portfolio.client.exception.ClientBillInfoNotFoundException;
import org.mifosplatform.portfolio.client.serialization.ClientBillInfoCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ClientBillInfoWritePlatformServiceImpl  implements ClientBillInfoWritePlatformService{

	private final static Logger logger = (Logger) LoggerFactory.getLogger(ClientBillInfoWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
    private final ClientBillInfoCommandFromApiJsonDeserializer apiJsonDeserializer;
    private final ClientBillProfileInfoRepository  clientbillprofileinfoRepository;


    @Autowired
	public ClientBillInfoWritePlatformServiceImpl(PlatformSecurityContext context,
			ClientBillInfoCommandFromApiJsonDeserializer apiJsonDeserializer,
			ClientBillProfileInfoRepository  clientbillprofileinfoRepository) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.clientbillprofileinfoRepository  = clientbillprofileinfoRepository;
	}
    
    
    private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
   
    }


	@Override
	public CommandProcessingResult updateClientBillInfo(JsonCommand command, Long clientId){
	   try{
		   
		   this.context.authenticatedUser();
		   this.apiJsonDeserializer.validateForCreate(command.json());
		   ClientBillProfileInfo clientbillprofileinfo = this.retrieveCodeBy(clientId);
		   final Map<String, Object> changes = clientbillprofileinfo.update(command);
		   if(!changes.isEmpty()){
			   this.clientbillprofileinfoRepository.save(clientbillprofileinfo);
		   }
		   return new CommandProcessingResultBuilder() //
	       .withCommandId(command.commandId()) //
	       .withEntityId(clientId) //
	       .with(changes) //
	       .build();
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
		      return new CommandProcessingResult(Long.valueOf(-1));
		  }
		}
		
	
	private ClientBillProfileInfo retrieveCodeBy(final Long clientId) {
		final ClientBillProfileInfo clientbillprofileinfo = this.clientbillprofileinfoRepository.findOne(clientId);
		if (clientbillprofileinfo == null) { throw new ClientBillInfoNotFoundException(clientId); }
		return clientbillprofileinfo;
	}	
}
