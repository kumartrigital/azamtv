package org.mifosplatform.collectionbatch.timemodel.service;

import java.util.Map;

import org.mifosplatform.collectionbatch.timemodel.domain.TimeModel;
import org.mifosplatform.collectionbatch.timemodel.domain.TimemodelRepository;
import org.mifosplatform.collectionbatch.timemodel.exception.TimeModelNotFoundException;
import org.mifosplatform.collectionbatch.timemodel.serialization.TimeModelCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class TimeModelWritePlatformServiceImpl implements TimeModelWritePlatformService {
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(TimeModelWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final TimeModelCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final TimemodelRepository timemodelRepository;
	private final FromJsonHelper fromApiJsonHelper;
	
	

	@Autowired
	public TimeModelWritePlatformServiceImpl(PlatformSecurityContext context,
			TimeModelCommandFromApiJsonDeserializer apiJsonDeserializer,
			TimemodelRepository timemodelRepository,
			FromJsonHelper fromApiJsonHelper) {
	
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.timemodelRepository = timemodelRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		
		
	}

	@Override
	public CommandProcessingResult createTimemodel(JsonCommand command) {
	try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final TimeModel timeModel = TimeModel.formJson(command);
			
			this.timemodelRepository.saveAndFlush(timeModel);
			return new CommandProcessingResultBuilder().withEntityId(timeModel.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}
	/*	try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			TimeModel timeModel = TimeModel.formJson(command);
			final JsonArray timesArray = command.arrayOfParameterNamed("times").getAsJsonArray();
			timeModel=assembleDiscountDetails(timesArray,timeModel); 
			this.timemodelRepository.save(timeModel);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					        .withEntityId(timeModel.getId()).build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	private TimeModel assembleDiscountDetails(JsonArray timesArray, TimeModel timeModel)  {
		try {
			String[]  times = null;
			times = new String[timesArray.size()];
			if(timesArray.size() > 0){
				for(int i = 0; i < timesArray.size(); i++){
					times[i] = timesArray.get(i).toString();
				}
		
			for (final String time : times) {
				final JsonElement element = fromApiJsonHelper.parse(time);
				//LocalDate localDate = new LocalDate(new Date());
				final String timeperiodName =fromApiJsonHelper.extractStringNamed("timeperiodName", element);
				final Long startYear = fromApiJsonHelper.extractLongNamed("startYear", element);
				final Long endYear = fromApiJsonHelper.extractLongNamed("endYear",element);
				final Long startMonth = fromApiJsonHelper.extractLongNamed("startMonth", element);
				final Long endMonth = fromApiJsonHelper.extractLongNamed("endMonth", element);
				final Long startDay = fromApiJsonHelper.extractLongNamed("startDay", element);
				final Long endDay = fromApiJsonHelper.extractLongNamed("endDay",element);
				DateFormat formatter = new SimpleDateFormat("HH:mm");
				//DateFormat formatter1 = new SimpleDateFormat("HH:mm");
				final Time startTime = new Time(formatter.parse(fromApiJsonHelper.extractStringNamed("startTime", element)).getTime());
				final Time endTime = new Time(formatter.parse(fromApiJsonHelper.extractStringNamed("endTime", element)).getTime());
				TimePeriod timePeriod = new TimePeriod(timeperiodName, startYear,endYear,startMonth,endMonth,startDay,endDay,startTime,endTime);
				timeModel.addDetails(timePeriod);
				}
			
		}
			return timeModel;
		}catch(ParseException pe) {
			return null;
		}
	*/
			 
	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
        /*if (realCause.getMessage().contains("external_id")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("error.msg.client.duplicate.externalId", "Client with externalId `" + externalId
                    + "` already exists", "externalId", externalId);
            
        } */

        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
	
	@Override
	public CommandProcessingResult updateTimemodel(JsonCommand command, Long timemodelId) {
		 try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   TimeModel timeModel = this.retrieveCodeBy(timemodelId);
			   final Map<String, Object> changes = timeModel.update(command);
			   if(!changes.isEmpty()){
				   this.timemodelRepository.save(timeModel);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(timemodelId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
	      }
    
	private TimeModel retrieveCodeBy(final Long timemodelId) {
		final TimeModel timeModel = this.timemodelRepository.findOne(timemodelId);
		if (timeModel == null) { throw new TimeModelNotFoundException(timemodelId); }
		return timeModel;
	}
	
	

	@Override
	public CommandProcessingResult deleteTimemodel(Long timemodelId) {
		
		try{
			this.context.authenticatedUser();
			TimeModel timeModel = this.retrieveCodeBy(timemodelId);
			if(timeModel.getIsActive()=='Y'){
				throw new TimeModelNotFoundException(timemodelId);
			}
			timeModel.delete();
			this.timemodelRepository.saveAndFlush(timeModel);
			return new CommandProcessingResultBuilder().withEntityId(timemodelId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	
}
