package org.mifosplatform.portfolio.plan.serialization;

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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer for code JSON to validate API request.
 */
@Component
public final class PlanCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("planCode","planDescription","locale",
    		"dateFormat","startDate","endDate","status","chargeCode","roles","billRule","isHwReq",
    		"isMultiCurrency","provisioingSystem","products","duration","volume","isPrepaid","units","allowTopup","planType","currencyId","serviceCode","serviceId","isAdvance"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public PlanCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("plan");

        final JsonElement element = fromApiJsonHelper.parse(json);

        final String planCode = fromApiJsonHelper.extractStringNamed("planCode", element);
        baseDataValidator.reset().parameter("planCode").value(planCode).notBlank().notExceedingLengthOf(10);
        final String planDescription = fromApiJsonHelper.extractStringNamed("planDescription", element);
        baseDataValidator.reset().parameter("planDescription").value(planDescription).notBlank();
        final LocalDate startDate = fromApiJsonHelper.extractLocalDateNamed("startDate", element);
        baseDataValidator.reset().parameter("startDate").value(startDate).notBlank();
        final Long status = fromApiJsonHelper.extractLongNamed("status", element);
        baseDataValidator.reset().parameter("status").value(status).notNull();
        final Long billRule = fromApiJsonHelper.extractLongNamed("billRule", element);
        baseDataValidator.reset().parameter("billRule").value(billRule).notNull();
        final String provisioingSystem = fromApiJsonHelper.extractStringNamed("provisioingSystem", element);
        baseDataValidator.reset().parameter("provisioingSystem").value(provisioingSystem).notBlank();
        final String[] products = fromApiJsonHelper.extractArrayNamed("products", element);
        baseDataValidator.reset().parameter("products").value(products).arrayNotEmpty();
        final boolean isPrepaid=fromApiJsonHelper.extractBooleanNamed("isPrepaid", element);

        if(isPrepaid){
        	final String volumeType=fromApiJsonHelper.extractStringNamed("volume", element);
        	baseDataValidator.reset().parameter("volume").value(volumeType).notNull();
        	final BigDecimal units=fromApiJsonHelper.extractBigDecimalWithLocaleNamed("units", element);
        	baseDataValidator.reset().parameter("units").value(units).notNull();
        	final Long duration=fromApiJsonHelper.extractLongNamed("duration", element);
        	baseDataValidator.reset().parameter("duration").value(duration).notNull();
        	
        }
        final String currencyId = fromApiJsonHelper.extractStringNamed("currencyId", element);
		baseDataValidator.reset().parameter("currencyId").value(currencyId).notBlank();

		
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("code");

        final JsonElement element = fromApiJsonHelper.parse(json);
        if (fromApiJsonHelper.parameterExists("name", element)) {
            final String name = fromApiJsonHelper.extractStringNamed("name", element);
            baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }
}