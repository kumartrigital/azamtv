/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.clientservice.serialization;


import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.order.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class ClientServiceDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("clientId",
    		"serviceId","clientServiceDetails","accountNo","clientPoId","clientservicePoId", "authToken", "generatedKey", "returnUrl","fromSelfcare"));
    

    @Autowired
    public ClientServiceDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);
        final JsonElement element = fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_SERVICE_RESOURCE_NAME);
        
        final Long clientId = this.fromApiJsonHelper.extractLongNamed("clientId", element);
        baseDataValidator.reset().parameter("clientId").value(clientId).notBlank().notExceedingLengthOf(20);
        
        final Long serviceId = this.fromApiJsonHelper.extractLongNamed("serviceId", element);
        baseDataValidator.reset().parameter("serviceId").value(serviceId).notBlank().notExceedingLengthOf(60);
        
        
        this.validatingDetails(baseDataValidator,element);
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
    
    private void validatingDetails(DataValidatorBuilder baseDataValidator,JsonElement element){
    	final JsonArray detailsArray = fromApiJsonHelper.extractJsonArrayNamed("clientServiceDetails", element);
        String[] details = new String[detailsArray.size()];
        final int detailsSize = detailsArray.size();
        baseDataValidator.reset().parameter("clientServiceDetails").value(detailsSize).integerGreaterThanZero();
        
        if(detailsSize > 0){
	    for(int i = 0; i < detailsArray.size(); i++){
	    	details[i] = detailsArray.get(i).toString();
	    }
	    
	    for (final String detail : details) {
	    	final JsonElement detailElement = fromApiJsonHelper.parse(detail);
	    	
	    	final Long categoryId = this.fromApiJsonHelper.extractLongNamed("parameterId", detailElement);
	    	baseDataValidator.reset().parameter("parameterId").value(categoryId).notNull();
	    	
	    	final String parameterValue = this.fromApiJsonHelper.extractStringNamed("parameterValue", detailElement);
	    	baseDataValidator.reset().parameter("parameterValue").value(parameterValue).notNull();
	    	
	    	final String status = this.fromApiJsonHelper.extractStringNamed("status", detailElement);
	    	baseDataValidator.reset().parameter("status").value(status).notNull();
		  }
        }
    }

	public void validateForOrders(List<Order> orders) {
		if(orders.isEmpty()){
			throw new PlatformDataIntegrityException("error.msg.orders.not.found",
					"There are no more orders for this Client service,Please add Plan to client Service");
		}
		
	}

}