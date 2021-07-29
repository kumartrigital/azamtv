package org.mifosplatform.organisation.salescatalogemapping.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.mifosplatform.organisation.salescataloge.domain.SalesCataloge;
import org.mifosplatform.organisation.salescataloge.domain.SalesCatalogeRepository;
import org.mifosplatform.organisation.salescataloge.exception.SalesCatalogeNotFoundException;
import org.mifosplatform.organisation.salescataloge.serialization.SalesCatalogeCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.salescatalogemapping.domain.SalesCatalogeMapping;
import org.mifosplatform.organisation.salescatalogemapping.domain.SalesCatalogeMappingRepository;
import org.mifosplatform.organisation.salescatalogemapping.exception.SalesCatalogeMappingNotFoundException;
import org.mifosplatform.organisation.salescatalogemapping.serialization.SalesCatalogeMappingCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SalesCatalogeMappingWritePlatformServiceImpl implements SalesCatalogeMappingWritePlatformService{

	private final PlatformSecurityContext context;
	private final SalesCatalogeMappingCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final SalesCatalogeMappingRepository salescatalogemappingRepository;
	
	@Autowired
	public SalesCatalogeMappingWritePlatformServiceImpl(PlatformSecurityContext context,
			SalesCatalogeMappingCommandFromApiJsonDeserializer apiJsonDeserializer, SalesCatalogeMappingRepository salescatalogemappingRepository) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.salescatalogemappingRepository = salescatalogemappingRepository;
	}
	
	
	@Override
	public CommandProcessingResult create(JsonCommand command) {
		try{
			context.authenticatedUser();
			apiJsonDeserializer.validateForCreate(command.json());
			SalesCatalogeMapping salescatalogemapping = SalesCatalogeMapping.formJson(command);
			this.salescatalogemappingRepository.saveAndFlush(salescatalogemapping);
			return new CommandProcessingResultBuilder().withEntityId(salescatalogemapping.getId()).build();
			
			}catch (DataIntegrityViolationException dve) {
			        handleDataIntegrityIssues(command, dve);
			        return  CommandProcessingResult.empty();
			}
	}

	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {final Throwable realCause = dve.getMostSpecificCause();
    
	throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
            "Unknown data integrity issue with resource.");
		
	}


	@Override
	public CommandProcessingResult updateSalesCatalogeMapping(JsonCommand command, Long salescatalogemappingId) {
		try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   SalesCatalogeMapping salescatalogemapping = this.retrieveSalesCatalogeMapping(salescatalogemappingId);
			   final Map<String, Object> changes = salescatalogemapping.update(command);
			   if(!changes.isEmpty()){
				   this.salescatalogemappingRepository.save(salescatalogemapping);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(salescatalogemappingId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
	}

	private SalesCatalogeMapping retrieveSalesCatalogeMapping(Long salescatalogemappingId) {
		SalesCatalogeMapping salescatalogemapping = this.salescatalogemappingRepository.findOne(salescatalogemappingId);
		if (salescatalogemapping == null) { throw new SalesCatalogeMappingNotFoundException(salescatalogemappingId); }
		return salescatalogemapping;
	}


	@Override
	public CommandProcessingResult deleteSalesCatalogeMapping(JsonCommand command, Long salescatalogemappingId) {
		try{
			this.context.authenticatedUser();
			SalesCatalogeMapping salescatalogemapping = this.retrieveSalesCatalogeMapping(salescatalogemappingId);
			if(salescatalogemapping.getIsDeleted()=='Y'){
				throw new SalesCatalogeMappingNotFoundException(salescatalogemappingId);
			}
			salescatalogemapping.delete();
			this.salescatalogemappingRepository.saveAndFlush(salescatalogemapping);
			return new CommandProcessingResultBuilder().withEntityId(salescatalogemappingId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
			
	
	}

}
