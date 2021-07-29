/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.logistics.itemdetails.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class InventoryItemCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */



	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("grnId","itemMasterId","quality","serialNumber","provisioningSerialNumber",
			"remarks","status","warranty","locale","officeId","clientId","inventorylisttable_length","flag","itemModel","quantity","isPairing","pairedItemId","cartoonNumber","itemModelName","itemCode"));

    
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public InventoryItemCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final JsonCommand command) {
        if (StringUtils.isBlank(command.toString())) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, command.json(),supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("item");

        //final JsonElement element = fromApiJsonHelper.parse(command);
        //final JsonElement jsonElement = fromApiJsonHelper.parse(command.toString());

        final Integer itemMasterId = command.integerValueOfParameterNamed("itemMasterId");
        baseDataValidator.reset().parameter("itemMasterId").value(itemMasterId).notNull();
        
        final Long grnId = command.longValueOfParameterNamed("grnId");
        baseDataValidator.reset().parameter("grnId").value(grnId).notBlank();
        
        final String remarks = command.stringValueOfParameterNamed("remarks");
        final String cartoonNumber = command.stringValueOfParameterNamed("cartoonNumber");
        final String serialNumber = command.stringValueOfParameterNamed("serialNumber");
        final String provisioningSerialNumber  = command.stringValueOfParameterNamed("provisioningSerialNumber");
        final String status  = command.stringValueOfParameterNamed("status");
        baseDataValidator.reset().parameter("status").value(status).notNull();
        
        
        final String quality = command.stringValueOfParameterNamed("quality");
        baseDataValidator.reset().parameter("quality").value(quality).notBlank();
        final Long quantity = command.longValueOfParameterNamed("quantity");
        
		/*if(isSerialRequired){
			baseDataValidator.reset().parameter("serialNumber").value(serialNumber).notBlank().notNull();
			baseDataValidator.reset().parameter("provisioningSerialNumber").value(provisioningSerialNumber).notBlank().notNull();
		}else{
			baseDataValidator.reset().parameter("quantity").value(quantity).notBlank();
		}*/
		
        final String isPairing = command.stringValueOfParameterNamed("isPairing");
        baseDataValidator.reset().parameter("isPairing").value(isPairing);
        final Long pairedItemId = command.longValueOfParameterNamed("pairedItemId");
        baseDataValidator.reset().parameter("pairedItemId").value(pairedItemId);
		

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    
    
    public void validateForSwap(final JsonCommand command) {
        
    	if (StringUtils.isBlank(command.toString())) { throw new InvalidJsonException(); }

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("item");

        final String type = command.stringValueOfParameterNamed("type");
        baseDataValidator.reset().parameter("type").value(type).notNull();
        
        final String oldSerialNo = command.stringValueOfParameterNamed("oldSerialNo");
		baseDataValidator.reset().parameter("oldSerialNo").value(oldSerialNo).notNull();
		
		final String oldPSerialNo = command.stringValueOfParameterNamed("oldPSerialNo");
		baseDataValidator.reset().parameter("oldPSerialNo").value(oldPSerialNo).notNull();
        
		final Long clientId = command.longValueOfParameterNamed("clientId");
		baseDataValidator.reset().parameter("clientId").value(clientId).notNull();
		
		final String newSerialNo = command.stringValueOfParameterNamed("newSerialNo");
		baseDataValidator.reset().parameter("newSerialNo").value(newSerialNo).notNull();
		
		final String newPSerialNo = command.stringValueOfParameterNamed("newPSerialNo");
		baseDataValidator.reset().parameter("newPSerialNo").value(newPSerialNo).notNull();
		
		final Long itemId =  command.longValueOfParameterNamed("itemId");
		baseDataValidator.reset().parameter("itemId").value(itemId).notNull();
		
		final Long orderId =  command.longValueOfParameterNamed("orderId");
		baseDataValidator.reset().parameter("orderId").value(orderId).notNull();
		
		final Long clientServiceId = command.longValueOfParameterNamed("clientServiceId");
		baseDataValidator.reset().parameter("clientServiceId").value(clientServiceId).notNull();
		

        this.throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    

    public void validateForUpdate(final String json, Boolean isSerialRequired) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("code");

        final JsonElement element = fromApiJsonHelper.parse(json);
        /*if (fromApiJsonHelper.parameterExists("name", element)) {
            final String name = fromApiJsonHelper.extractStringNamed("name", element);
            baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);
        }*/
       if(isSerialRequired){
       final String provisioningSerialNumber = fromApiJsonHelper.extractStringNamed("provisioningSerialNumber", element);
       baseDataValidator.reset().parameter("provisioningSerialNumber").value(provisioningSerialNumber).notBlank().notExceedingLengthOf(100);
       
       if (fromApiJsonHelper.parameterExists("serialNumber", element)) {
       final String serialNumber = fromApiJsonHelper.extractStringNamed("serialNumber",element);
       baseDataValidator.reset().parameter("serialNumber").value(serialNumber).notBlank().notExceedingLengthOf(100);
       }    
       }else{
    	   final Long quantity = fromApiJsonHelper.extractLongNamed("quantity",element);
    	   baseDataValidator.reset().parameter("quantity").value(quantity).notBlank();
       }
       
       final String isPairing = fromApiJsonHelper.extractStringNamed("isPairing", element);
       baseDataValidator.reset().parameter("isPairing").value(isPairing);
       final Long piairedItemId = fromApiJsonHelper.extractLongNamed("piairedItemId", element);
       baseDataValidator.reset().parameter("piairedItemId").value(piairedItemId);
       
       
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    public void validateForSerialNumber(final String json, Boolean isSerialRequired) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("code");

        final JsonElement element = fromApiJsonHelper.parse(json);
        
       if(isSerialRequired){
       final String provisioningSerialNumber = fromApiJsonHelper.extractStringNamed("provisioningSerialNumber", element);
       //baseDataValidator.reset().parameter("provisioningSerialNumber").value(provisioningSerialNumber).notBlank().notExceedingLengthOf(100);
       
       if (fromApiJsonHelper.parameterExists("serialNumber", element)) {
       final String serialNumber = fromApiJsonHelper.extractStringNamed("serialNumber",element);
       baseDataValidator.reset().parameter("serialNumber").value(serialNumber).notBlank().notExceedingLengthOf(100);
       }    
       }else{
    	   final Long quantity = fromApiJsonHelper.extractLongNamed("quantity",element);
    	   baseDataValidator.reset().parameter("quantity").value(quantity).notBlank();
       }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}