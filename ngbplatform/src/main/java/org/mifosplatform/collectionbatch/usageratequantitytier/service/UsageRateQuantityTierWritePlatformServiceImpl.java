package org.mifosplatform.collectionbatch.usageratequantitytier.service;

import java.math.BigDecimal;

import org.mifosplatform.billing.discountmaster.domain.DiscountDetails;
import org.mifosplatform.billing.discountmaster.domain.DiscountMaster;
import org.mifosplatform.collectionbatch.usageratequantitytier.domain.UsageRateQuantityTier;
import org.mifosplatform.collectionbatch.usageratequantitytier.domain.UsageRateQuantityTierRepository;
import org.mifosplatform.collectionbatch.usageratequantitytier.serialization.UsageRateQuantityTierCommandFromApiJsonDeserializer;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@Service
public class UsageRateQuantityTierWritePlatformServiceImpl implements UsageRateQuantityTierWritePlatformService{
	
	
	private final static Logger logger = (Logger) LoggerFactory.getLogger(UsageRateQuantityTierWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final UsageRateQuantityTierCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final  UsageRateQuantityTierRepository usageRateQuantityTierRepository;
	private final FromJsonHelper fromApiJsonHelper;
	
	
	@Autowired
	public UsageRateQuantityTierWritePlatformServiceImpl(PlatformSecurityContext context,
			UsageRateQuantityTierCommandFromApiJsonDeserializer apiJsonDeserializer,
			final UsageRateQuantityTierRepository usageRateQuantityTierRepository,
			final FromJsonHelper fromApiJsonHelper) {
	
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.usageRateQuantityTierRepository = usageRateQuantityTierRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
	
	}

	@Override
	public CommandProcessingResult createUsageRateQuantityTier(JsonCommand command) {
		

		/*try{
		
		this.context.authenticatedUser();
		this.apiJsonDeserializer.validateForCreate(command.json());
		final UsageRateQuantityTier usageratequantitytier = UsageRateQuantityTier.formJson(command);
		this.usageRateQuantityTierRepository.save(usageratequantitytier);
		return new CommandProcessingResultBuilder().withEntityId(usageratequantitytier.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}*/
		
		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
		    /*UsageRateQuantityTier usageratequantitytier = UsageRateQuantityTier.formJson(command);*/
		    Long usageRateplanId = command.longValueOfParameterNamed("usageRateplanId");
			final JsonArray rangeArray = command.arrayOfParameterNamed("range").getAsJsonArray();
			//UsageRateQuantityTier usageratequantitytier=assembleDiscountDetails(rangeArray,usageratequantitytier); 
			
			UsageRateQuantityTier usageratequantitytier = null;
			String[]  range = null;
			range = new String[rangeArray.size()];
			if(rangeArray.size() > 0){
				for(int i = 0; i < rangeArray.size(); i++){
					range[i] = rangeArray.get(i).toString();
				}
						for (final String ranges : range) {
				final JsonElement element = fromApiJsonHelper.parse(ranges);
				/*final String tierName = fromApiJsonHelper.extractStringNamed("tierName", element);
				final Long usageRateplanId = fromApiJsonHelper.extractLongNamed("usageRateplanId", element);*/
				final String tierName = fromApiJsonHelper.extractStringNamed("tierName",element);
				final Long startRange = fromApiJsonHelper.extractLongNamed("startRange", element);
				final Long endRange = fromApiJsonHelper.extractLongNamed("endRange", element);
				usageratequantitytier= new UsageRateQuantityTier(tierName,usageRateplanId,startRange, endRange);
			    this.usageRateQuantityTierRepository.save(usageratequantitytier);
				
			}	 
		}
			
			
			/*this.usageRateQuantityTierRepository.save(usageratequantitytier);*/
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					        .withEntityId(usageratequantitytier.getId()).build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	
	}
	/*private UsageRateQuantityTier assembleDiscountDetails(JsonArray rangeArray,
			UsageRateQuantityTier usageratequantitytier) {
		UsageRateQuantityTier usageRateQuantityTier=null;

		String[]  range = null;
		range = new String[rangeArray.size()];
		if(rangeArray.size() > 0){
			for(int i = 0; i < rangeArray.size(); i++){
				range[i] = rangeArray.get(i).toString();
			}
					for (final String ranges : range) {
			final JsonElement element = fromApiJsonHelper.parse(ranges);
			final String tierName = fromApiJsonHelper.extractStringNamed("tierName", element);
			final Long usageRateplanId = fromApiJsonHelper.extractLongNamed("usageRateplanId", element);
			final Long startRange = fromApiJsonHelper.extractLongNamed("startRange", element);
			final Long endRange = fromApiJsonHelper.extractLongNamed("endRange", element);
		   usageRateQuantityTier= new UsageRateQuantityTier(startRange, endRange);
		   this.usageRateQuantityTierRepository.save(usageratequantitytier);
			
		}	 
	}	
	
	return usageRateQuantityTier;
	}*/

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
    	final Throwable realCause = dve.getMostSpecificCause();
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
	}
}
