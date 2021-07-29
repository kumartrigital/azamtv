package org.mifosplatform.portfolio.clientdiscount.service;

import java.util.Map;

import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.clientdiscount.domain.ClientDiscount;
import org.mifosplatform.portfolio.clientdiscount.domain.ClientDiscountRepository;
import org.mifosplatform.portfolio.clientdiscount.serialization.ClientDiscountCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ClientDiscountWritePlatformServiceImpl implements  ClientDiscountWritePlatformService{

	private final PlatformSecurityContext context;
	private final ClientDiscountCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ClientDiscountRepository clientDiscountRepository;
	private final CrmServices crmServices;
	private final ClientRepository clientRepository;
	
	@Autowired
	public ClientDiscountWritePlatformServiceImpl(PlatformSecurityContext context,
			ClientDiscountCommandFromApiJsonDeserializer apiJsonDeserializer,
			ClientDiscountRepository clientDiscountRepository,CrmServices crmServices,
			final ClientRepository clientRepository) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.clientDiscountRepository = clientDiscountRepository;
		this.crmServices = crmServices;
		this.clientRepository = clientRepository;
	}


	@Override
	public CommandProcessingResult create(JsonCommand command) {
		try{
		context.authenticatedUser();
		apiJsonDeserializer.validateForCreate(command.json());
		ClientDiscount clientDiscount = ClientDiscount.formJson(command);
		clientDiscount.setClient(this.clientRepository.findOne(command.longValueOfParameterNamed("clientId")));
        CommandProcessingResult result = this.crmServices.createClientDiscount(command);
		clientDiscountRepository.saveAndFlush(clientDiscount);
		return new CommandProcessingResultBuilder().withEntityId(clientDiscount.getId()).build();
		
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
	public CommandProcessingResult updateClientDiscount(JsonCommand command, Long clientDiscountId) {

		   try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   ClientDiscount clientDiscount = this.retrieveClientDiscount(clientDiscountId);
			   final Map<String, Object> changes = clientDiscount.update(command);
			   if(!changes.isEmpty()){
				   this.clientDiscountRepository.save(clientDiscount);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(clientDiscountId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
			
	}
	
	@Override
	public CommandProcessingResult deleteClientDiscount(Long clientDiscountId) {

		try{
			this.context.authenticatedUser();
			ClientDiscount clientDiscount = this.retrieveClientDiscount(clientDiscountId);
			if(clientDiscount.getIsDeleted()=='Y'){
				throw new ChannelNotFoundException(clientDiscountId);
			}
			clientDiscount.delete();
			this.clientDiscountRepository.saveAndFlush(clientDiscount);
			return new CommandProcessingResultBuilder().withEntityId(clientDiscountId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	
	}

	private ClientDiscount retrieveClientDiscount(final Long clientDiscountId) {
		ClientDiscount clientDiscount = this.clientDiscountRepository.findOne(clientDiscountId);
		if (clientDiscount == null) { throw new ChannelNotFoundException(clientDiscountId); }
		return clientDiscount;
	}
	

}
