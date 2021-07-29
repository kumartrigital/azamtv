package org.mifosplatform.collectionbatch.ratableusagemetric.service;

import java.util.Map;

import org.mifosplatform.collectionbatch.ratableusagemetric.domain.RatableUsageMetric;
import org.mifosplatform.collectionbatch.ratableusagemetric.domain.RatableUsageMetricRepository;
import org.mifosplatform.collectionbatch.ratableusagemetric.exception.RatableUsageMetricNotFoundException;
import org.mifosplatform.collectionbatch.ratableusagemetric.serialization.RatableUsageMetricCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.broadcaster.domain.Broadcaster;
import org.mifosplatform.organisation.broadcaster.exception.BroadcatserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;



@Service
public class RatableUsageMetricWritePlatformServiceImpl implements RatableUsageMetricWritePlatformService{
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(RatableUsageMetricWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final RatableUsageMetricCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final RatableUsageMetricRepository ratableUsageMetricRepository;
	
	
	@Autowired
	public RatableUsageMetricWritePlatformServiceImpl(PlatformSecurityContext context,
			RatableUsageMetricCommandFromApiJsonDeserializer apiJsonDeserializer,
			final RatableUsageMetricRepository ratableUsageMetricRepository) {
	
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.ratableUsageMetricRepository = ratableUsageMetricRepository;
	
	}

	@Override
	public CommandProcessingResult createRatableUsageMetric(JsonCommand command) {

			try{
			
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final RatableUsageMetric ratableusagemetric = RatableUsageMetric.formJson(command);
			
			this.ratableUsageMetricRepository.save(ratableusagemetric);
			return new CommandProcessingResultBuilder().withEntityId(ratableusagemetric.getId()).build();
			
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
		public CommandProcessingResult updateRatableUsageMetric(JsonCommand command, Long id) {
			   try{
				   
				   this.context.authenticatedUser();
				   this.apiJsonDeserializer.validateForCreate(command.json());
				   RatableUsageMetric ratableusagemetric = this.retrieveCodeBy(id);
				   final Map<String, Object> changes = ratableusagemetric.update(command);
				   if(!changes.isEmpty()){
					   this.ratableUsageMetricRepository.save(ratableusagemetric);
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
		
		private RatableUsageMetric retrieveCodeBy(final Long id) {
			final RatableUsageMetric ratableusagemetric = this.ratableUsageMetricRepository.findOne(id);
			if (ratableusagemetric == null) { throw new RatableUsageMetricNotFoundException(id); }
			return ratableusagemetric;
		}

		

		/*@Override
		public CommandProcessingResult deleteRatableUsageMetric(Long id) {
			try{
				this.context.authenticatedUser();
				RatableUsageMetric ratableusagemetric = this.retrieveCodeBy(id);
				if(ratableusagemetric.getIsDeleted()=='Y'){
					throw new BroadcatserNotFoundException(id);
				}
				ratableusagemetric.delete();
				this.ratableUsageMetricRepository.saveAndFlush(ratableusagemetric);
				return new CommandProcessingResultBuilder().withEntityId(id).build();
				
			}catch(DataIntegrityViolationException dve){
				handleDataIntegrityIssues(null, dve);
				return new CommandProcessingResult(Long.valueOf(-1));
			}
			
		}	*/

}
