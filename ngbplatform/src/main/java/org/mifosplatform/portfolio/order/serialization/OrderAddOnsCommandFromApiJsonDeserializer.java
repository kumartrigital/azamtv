package org.mifosplatform.portfolio.order.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
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
public final class OrderAddOnsCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("addonServices","serviceId","startDate","endDate","contractId","price",
    		"locale","planName","dateFormat"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public OrderAddOnsCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("addons");
        final JsonElement element = fromApiJsonHelper.parse(json);
        final Long contractId = fromApiJsonHelper.extractLongNamed("contractId", element);
		baseDataValidator.reset().parameter("contractId").value(contractId).notNull();
		final LocalDate startDate = fromApiJsonHelper.extractLocalDateNamed("startDate", element);
		baseDataValidator.reset().parameter("startDate").value(startDate).notBlank();
        final JsonArray addonServicesArray = fromApiJsonHelper.extractJsonArrayNamed("addonServices", element);
        String[] serviceParameters = null;
		serviceParameters = new String[addonServicesArray.size()];
		int arraysize = addonServicesArray.size();
		baseDataValidator.reset().parameter(null).value(arraysize).integerGreaterThanZero();
		for (int i = 0; i < addonServicesArray.size(); i++) {
			serviceParameters[i] = addonServicesArray.get(i).toString();
		}

		for (String serviceParameter : serviceParameters) {

			final JsonElement attributeElement = fromApiJsonHelper.parse(serviceParameter);
			final Long serviceId = fromApiJsonHelper.extractLongNamed("serviceId", attributeElement);
			baseDataValidator.reset().parameter("serviceId").value(serviceId).notNull();
			final BigDecimal price= fromApiJsonHelper.extractBigDecimalWithLocaleNamed("price", attributeElement);
			baseDataValidator.reset().parameter("price").value(price).notNull();
		
		}

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }


    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }


}