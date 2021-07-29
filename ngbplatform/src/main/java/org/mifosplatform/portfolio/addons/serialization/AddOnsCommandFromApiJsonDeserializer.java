package org.mifosplatform.portfolio.addons.serialization;

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
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class AddOnsCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("addons","serviceId","chargeCode","price","planId","locale",
    		"priceRegionId","isDeleted"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public AddOnsCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("addons");
        final JsonElement element = fromApiJsonHelper.parse(json);

        
        final Long planId = fromApiJsonHelper.extractLongNamed("planId", element);
        baseDataValidator.reset().parameter("planId").value(planId).notNull();
        final String chargeCode = fromApiJsonHelper.extractStringNamed("chargeCode", element);
        baseDataValidator.reset().parameter("chargeCode").value(chargeCode).notBlank();
        final Long priceRegionId = fromApiJsonHelper.extractLongNamed("priceRegionId", element);
        baseDataValidator.reset().parameter("priceRegionId").value(priceRegionId).notNull();
        
        final JsonArray addonServicesArray = fromApiJsonHelper.extractJsonArrayNamed("addons", element);
	    final int addonsAttributeSize = addonServicesArray.size();
	    baseDataValidator.reset().parameter("addons").value(addonsAttributeSize).integerGreaterThanZero();
        String[] serviceParameters = null;
		serviceParameters = new String[addonServicesArray.size()];
		int arraysize = addonServicesArray.size();
		baseDataValidator.reset().parameter(null).value(arraysize).integerGreaterThanZero();
		for (int i = 0; i < addonServicesArray.size(); i++) {
			serviceParameters[i] = addonServicesArray.get(i).toString();
		}
	
		for (JsonElement jsonElement : addonServicesArray) {
			
			final BigDecimal price = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("price", jsonElement);
			baseDataValidator.reset().parameter("price").value(price).notNull();
		}

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }


    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }


}