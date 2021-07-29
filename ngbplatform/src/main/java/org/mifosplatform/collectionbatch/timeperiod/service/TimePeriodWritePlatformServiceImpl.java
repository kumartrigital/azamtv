package org.mifosplatform.collectionbatch.timeperiod.service;

import java.util.Map;

import org.mifosplatform.collectionbatch.timeperiod.domain.TimePeriodsNew;
import org.mifosplatform.collectionbatch.timeperiod.domain.TimeperiodRepository;
import org.mifosplatform.collectionbatch.timeperiod.exception.TimePeriodNotFoundException;
import org.mifosplatform.collectionbatch.timeperiod.serialization.TimePeriodCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class TimePeriodWritePlatformServiceImpl implements TimePeriodWritePlatformService {
	private final static Logger logger = (Logger) LoggerFactory.getLogger(TimePeriodWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final TimePeriodCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final TimeperiodRepository timeperiodRepository;
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public TimePeriodWritePlatformServiceImpl(PlatformSecurityContext context,
			TimePeriodCommandFromApiJsonDeserializer apiJsonDeserializer, TimeperiodRepository timeperiodRepository,
			FromJsonHelper fromApiJsonHelper) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.timeperiodRepository = timeperiodRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;

	}

	@Override
	public CommandProcessingResult createTimeperiod(JsonCommand command) {
		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final TimePeriodsNew timePeriodsNew = TimePeriodsNew.formJson(command);

			this.timeperiodRepository.save(timePeriodsNew);
			return new CommandProcessingResultBuilder().withEntityId(timePeriodsNew.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		/*
		 * if (realCause.getMessage().contains("external_id")) {
		 * 
		 * final String externalId = command.stringValueOfParameterNamed("externalId");
		 * throw new
		 * PlatformDataIntegrityException("error.msg.client.duplicate.externalId",
		 * "Client with externalId `" + externalId + "` already exists", "externalId",
		 * externalId);
		 * 
		 * }
		 */

		throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

	@Override
	public CommandProcessingResult updateTimeperiod(JsonCommand command, Long timeperiodId) {
		try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   TimePeriodsNew  timePeriodsNew = this.retrieveCodeBy(timeperiodId);
			   final Map<String, Object> changes = timePeriodsNew.update(command);
			   if(!changes.isEmpty()){
				   this.timeperiodRepository.save(timePeriodsNew);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(timeperiodId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
	}
	private TimePeriodsNew retrieveCodeBy(final Long timeperiodId) {
		final TimePeriodsNew timePeriodsNew = this.timeperiodRepository.findOne(timeperiodId);
		if (timePeriodsNew == null) { throw new TimePeriodNotFoundException(timeperiodId); }
		return timePeriodsNew;
	}

	@Override
	public CommandProcessingResult deleteTimePeriod(Long timeperiodId) {
		try{
			this.context.authenticatedUser();
			TimePeriodsNew timePeriodsNew = this.retrieveTimePeriod(timeperiodId);
			if(timePeriodsNew.getIsDeleted()=='Y'){
				throw new TimePeriodNotFoundException(timeperiodId);
			}
			timePeriodsNew.delete();
			this.timeperiodRepository.saveAndFlush(timePeriodsNew);
			return new CommandProcessingResultBuilder().withEntityId(timeperiodId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}
	
	private TimePeriodsNew retrieveTimePeriod(final Long timeperiodId) {
		TimePeriodsNew timePeriodsNew = this.timeperiodRepository.findOne(timeperiodId);
		if (timePeriodsNew == null) { throw new TimePeriodNotFoundException(timeperiodId); }
		return timePeriodsNew;
	}

}
