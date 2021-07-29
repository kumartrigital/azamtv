package org.mifosplatform.organisation.feemaster.service;

import java.math.BigDecimal;
import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.organisation.feemaster.domain.FeeDetail;
import org.mifosplatform.organisation.feemaster.domain.FeeDetailRepository;
import org.mifosplatform.organisation.feemaster.domain.FeeMaster;
import org.mifosplatform.organisation.feemaster.domain.FeeMasterRepository;
import org.mifosplatform.organisation.feemaster.exception.FeeMasterNotFoundException;
import org.mifosplatform.organisation.feemaster.serialization.FeeMasterCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class FeeMasterWriteplatformServiceImpl implements FeeMasterWriteplatformService {

	private final PlatformSecurityContext context;
	private final FeeMasterCommandFromApiJsonDeserializer feeMasterCommandFromApiJsonDeserializer;
	private final FeeMasterRepository feeMasterRepository;
	private final FeeDetailRepository feeDetailRepository;
	private final FromJsonHelper fromApiJsonHelper;
	
	
	@Autowired
	public FeeMasterWriteplatformServiceImpl(final PlatformSecurityContext context,
			final FeeMasterCommandFromApiJsonDeserializer feeMasterCommandFromApiJsonDeserializer,
			final FeeMasterRepository feeMasterRepository,final FeeDetailRepository feeDetailRepository,
            final FromJsonHelper fromApiJsonHelper) {

		this.context = context;
		this.feeMasterCommandFromApiJsonDeserializer = feeMasterCommandFromApiJsonDeserializer;
		this.feeMasterRepository = feeMasterRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.feeDetailRepository = feeDetailRepository;
	}
	
	
	@Override
	public CommandProcessingResult createFeeMaster(JsonCommand command) {
		
		try {
			this.context.authenticatedUser();
			this.feeMasterCommandFromApiJsonDeserializer.validateForCreate(command.json());
			FeeMaster feeMaster = FeeMaster.fromJson(command);

    		final JsonArray regionPricesArray = command.arrayOfParameterNamed("regionPrices").getAsJsonArray();
			String[] feeMasterPriceRegions = null;
			feeMasterPriceRegions = new String[regionPricesArray.size()];
			if(regionPricesArray.size() > 0){
			for(int i = 0; i < regionPricesArray.size(); i++){
				feeMasterPriceRegions[i] = regionPricesArray.get(i).toString();
			}
			
			for (final String feeMasterPriceRegion : feeMasterPriceRegions) {
							 
				final JsonElement element = fromApiJsonHelper.parse(feeMasterPriceRegion);
				
				final String regionId = fromApiJsonHelper.extractStringNamed("regionId", element);
				final BigDecimal amount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
				
				FeeDetail feeDetail = new FeeDetail(regionId, amount);
				feeMaster.addRegionPrices(feeDetail);

			}	 
			
			}		 
			
    		this.feeMasterRepository.save(feeMaster);
    		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(feeMaster.getId()).build();
    
    	} catch (DataIntegrityViolationException dve) {
    		handleItemDataIntegrityIssues(command, dve);
    		return CommandProcessingResult.empty();
    	}
	}
	
	 private void handleItemDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
	        Throwable realCause = dve.getMostSpecificCause();
	        if (realCause.getMessage().contains("fee_code")) {
	            final String name = command.stringValueOfParameterNamed("feeCode");
	            throw new PlatformDataIntegrityException("error.msg.fee.code.duplicate.name", "A Fee code with name '" + name + "' already exists");
	        } else if (realCause.getMessage().contains("fee_transaction_type")) {
	            final String name =command.stringValueOfParameterNamed("transactionType");
	            throw new PlatformDataIntegrityException("error.msg.fee.transaction.alredy.exists", "A Fee transactionType with this '" + name + "' already exists","transactionType");
	        }

	        //logger.error(dve.getMessage(), dve);
	        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource: " + realCause.getMessage());
	    }


	@Override
	public CommandProcessingResult updateFeeMaster(JsonCommand command) {

   	 try{
   		 this.context.authenticatedUser();
   		 this.feeMasterCommandFromApiJsonDeserializer.validateForCreate(command.json());
   		 FeeMaster feeMaster = retrieveCodeBy(command.entityId());
   		 
   		 
   		 final Map<String, Object> changes = feeMaster.update(command);
   		 
   		 final JsonArray regionPricesArray = command.arrayOfParameterNamed("regionPrices").getAsJsonArray();
   		 final JsonArray removeRegionPricesArray = command.arrayOfParameterNamed("removeRegionPrices").getAsJsonArray();
			 String[] feeMasterPriceRegions = new String[regionPricesArray.size()];
			 
			 if(removeRegionPricesArray.size() != 0){
				 
					String[] removedFeeMasterPriceRegions = new String[removeRegionPricesArray.size()];
		 			
		 			 for(int i = 0; i < removeRegionPricesArray.size(); i++){
		 				removedFeeMasterPriceRegions[i] = removeRegionPricesArray.get(i).toString();
		 			 }
		 			 
		 			 for (final String removedFeeMasterPriceRegion : removedFeeMasterPriceRegions) {
		 							 
		 				final JsonElement element = fromApiJsonHelper.parse(removedFeeMasterPriceRegion);
		 				final Long id = fromApiJsonHelper.extractLongNamed("id", element);
		 				final String regionId = fromApiJsonHelper.extractStringNamed("regionId", element);
			 			final BigDecimal amount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
			 			
		 				if(id != null){
		 					FeeDetail feeDetail =this.feeDetailRepository.findOne(id);
		 					feeDetail.setRegionId(regionId+"_"+id+"_Y");
		 					feeDetail.setAmount(amount);
		 					feeDetail.setIsDeleted('Y');
		 					feeDetailRepository.saveAndFlush(feeDetail);
		 				}	
		 			 }	
				 }
			 
			 if(regionPricesArray.size() > 0){
			 for(int i = 0; i < regionPricesArray.size(); i++){
				 feeMasterPriceRegions[i] = regionPricesArray.get(i).toString();
			 }
			 
			 for (final String feeMasterPriceRegion : feeMasterPriceRegions) {
							 
				final JsonElement element = fromApiJsonHelper.parse(feeMasterPriceRegion);
				final Long id = fromApiJsonHelper.extractLongNamed("id", element);
				final String regionId = fromApiJsonHelper.extractStringNamed("regionId", element);
	 			final BigDecimal amount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("amount", element);
	 			
				if(id != null){
					FeeDetail feeDetail =this.feeDetailRepository.findOne(id);
					feeDetail.setRegionId(regionId);
					feeDetail.setAmount(amount);
					feeDetailRepository.saveAndFlush(feeDetail);
	 				
				}else{
					
					FeeDetail feeDetail = new FeeDetail(regionId, amount);
					feeMaster.addRegionPrices(feeDetail);
				}
			  }	
			 }
   		 
			 feeMasterRepository.saveAndFlush(feeMaster);
   		
	   return new CommandProcessingResultBuilder() //
      .withCommandId(command.commandId()) //
      .withEntityId(command.entityId()) //
      .with(changes) //
      .build();
	}catch (DataIntegrityViolationException dve) {
	      handleItemDataIntegrityIssues(command, dve);
	      return new CommandProcessingResult(Long.valueOf(-1));
	  }

}
	private FeeMaster retrieveCodeBy(final Long id) {
        final FeeMaster feeMaster = this.feeMasterRepository.findOne(id);
        if (feeMaster == null) { throw new FeeMasterNotFoundException(id.toString()); }
        return feeMaster;
    }
	
	
	@Override
	public CommandProcessingResult deleteFeeMaster(Long id) {
		try{
			this.context.authenticatedUser();
			FeeMaster feeMaster=retrieveCodeBy(id);
			if(feeMaster.getDeleted()=='Y'){
				throw new ItemNotFoundException(id.toString());
			}
			feeMaster.delete();
			this.feeMasterRepository.save(feeMaster);
			return new CommandProcessingResultBuilder().withEntityId(id).build();
			
		}catch(DataIntegrityViolationException dve){
			handleItemDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}

}
