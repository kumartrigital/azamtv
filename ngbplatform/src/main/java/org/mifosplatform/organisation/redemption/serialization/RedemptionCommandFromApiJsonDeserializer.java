package org.mifosplatform.organisation.redemption.serialization;

import java.lang.reflect.Type;
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
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class RedemptionCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("clientId","pinNumber","Amount",
    		"authToken", "generatedKey", "returnUrl","channel"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public RedemptionCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("redemption");

        final JsonElement element = fromApiJsonHelper.parse(json);

        final String clientId = fromApiJsonHelper.extractStringNamed("clientId", element);
        baseDataValidator.reset().parameter("clientId").value(clientId).notBlank().notExceedingLengthOf(100);
        
        final String pinNumber = fromApiJsonHelper.extractStringNamed("pinNumber", element);
        baseDataValidator.reset().parameter("pinNumber").value(pinNumber).notBlank();
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
    
    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}