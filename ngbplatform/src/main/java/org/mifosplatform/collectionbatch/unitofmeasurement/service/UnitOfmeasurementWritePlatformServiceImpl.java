package org.mifosplatform.collectionbatch.unitofmeasurement.service;

import java.util.Map;

import org.mifosplatform.collectionbatch.ratableusagemetric.domain.RatableUsageMetric;
import org.mifosplatform.collectionbatch.ratableusagemetric.exception.RatableUsageMetricNotFoundException;
import org.mifosplatform.collectionbatch.unitofmeasurement.domain.UnitOfmeasurement;
import org.mifosplatform.collectionbatch.unitofmeasurement.domain.UnitOfmeasurementRepository;
import org.mifosplatform.collectionbatch.unitofmeasurement.exception.UnitOfmeasurementNotFoundException;
import org.mifosplatform.collectionbatch.unitofmeasurement.serialization.UnitOfmeasurementCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class UnitOfmeasurementWritePlatformServiceImpl implements UnitOfmeasurementWritePlatformService{
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(UnitOfmeasurementWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final UnitOfmeasurementCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final UnitOfmeasurementRepository unitofmeasurementRepository;
	
	
	@Autowired
	public UnitOfmeasurementWritePlatformServiceImpl(PlatformSecurityContext context,
			UnitOfmeasurementCommandFromApiJsonDeserializer apiJsonDeserializer,
			final UnitOfmeasurementRepository unitofmeasurementRepository) {
	
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.unitofmeasurementRepository = unitofmeasurementRepository;
	
	}

	@Override
	public CommandProcessingResult createUnitOfmeasurement(JsonCommand command) {

		try{
		
		this.context.authenticatedUser();
		this.apiJsonDeserializer.validateForCreate(command.json());
		final UnitOfmeasurement unitofmeasurement = UnitOfmeasurement.formJson(command);
		
		this.unitofmeasurementRepository.save(unitofmeasurement);
		return new CommandProcessingResultBuilder().withEntityId(unitofmeasurement.getId()).build();
		
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
	public CommandProcessingResult updateUnitOfmeasurement(JsonCommand command, Long id) {
		   try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   UnitOfmeasurement unitofmeasurement = this.retrieveCodeBy(id);
			   final Map<String, Object> changes = unitofmeasurement.update(command);
			   if(!changes.isEmpty()){
				   this.unitofmeasurementRepository.save(unitofmeasurement);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(id) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
			}
	private UnitOfmeasurement retrieveCodeBy(final Long id) {
		final UnitOfmeasurement unitofmeasurement = this.unitofmeasurementRepository.findOne(id);
		if (unitofmeasurement == null) { throw new UnitOfmeasurementNotFoundException(id); }
		return unitofmeasurement;
	}
}
