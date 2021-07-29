package org.mifosplatform.portfolio.order.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
public final class OrderCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("id","planCode","locale","dateFormat","start_date","paytermCode","oldPlanPoId","oldDealPoId",
    		"contractPeriod","billAlign","price","description","renewalPeriod","disconnectReason","isPrepaid","disconnectionDate","ispaymentEnable","purchaseProductPoid",
    		"channel","paymentCode","amountPaid","paymentDate","receiptNo","promoId","startDate","isNewplan","suspensionDate","suspensionReason","catalogeId","billFrequencyCode",
    		"suspensionDescription","status","actionType","priceId","autoRenew","planId","duration","planDescription","serialnumber","allocation_type","orderId",
    		"planTypeName","plans","clientServiceId","nextBillDate","planName","planPoId","dealPoId","orderNo","clientPoId","clientServicePoId","plans","name","disconnectMultiple","fromNGB","serviceActivation","clientId","changePlanDetail",
    		"agreementStatus","officeId","sourceData","poId","settlementPoId", "authToken", "generatedKey", "returnUrl","products","bouqueCount","nonBouqueCount","fromSelfcare","oldplanId","Newplanprepaid","oldplanprepaid"));
    private final Set<String> retracksupportedParameters = new HashSet<String>(Arrays.asList("commandName","message","orderId","clientServiceId","type","commandDetails","clientId"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public OrderCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("order");

        final JsonElement element = fromApiJsonHelper.parse(json);

        final String planCode = fromApiJsonHelper.extractStringNamed("planCode", element);
        baseDataValidator.reset().parameter("planCode").value(planCode).notBlank();
        final LocalDateTime startDate = fromApiJsonHelper.extractLocalDateTimeNamed("start_date", element);
        baseDataValidator.reset().parameter("start_date").value(startDate).notBlank();
        final String paytermCode = fromApiJsonHelper.extractStringNamed("paytermCode", element);
        baseDataValidator.reset().parameter("paytermCode").value(paytermCode).notBlank();
        final String contractPeriod = fromApiJsonHelper.extractStringNamed("contractPeriod", element);
        baseDataValidator.reset().parameter("contractPeriod").value(contractPeriod).notBlank();

        final Long clientServiceId = fromApiJsonHelper.extractLongNamed("clientServiceId", element);
        baseDataValidator.reset().parameter("clientServiceId").value(clientServiceId).notBlank();
        
        final String channel = fromApiJsonHelper.extractStringNamed("channel", element);
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("code");
        final JsonElement element = fromApiJsonHelper.parse(json);
        final LocalDate startDate = fromApiJsonHelper.extractLocalDateNamed("start_date", element);
        baseDataValidator.reset().parameter("start_date").value(startDate).notBlank();
        
        if (fromApiJsonHelper.parameterExists("renewalPeriod", element)) {
            final String renewalPeriod = fromApiJsonHelper.extractStringNamed("renewalPeriod", element);
            baseDataValidator.reset().parameter("renewalPeriod").value(renewalPeriod).notBlank();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

	public void validateForRenewalOrder(String json) {
		   if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("order");

	        final JsonElement element = fromApiJsonHelper.parse(json);

	        final String renewalPeriod = fromApiJsonHelper.extractStringNamed("renewalPeriod", element);
	        baseDataValidator.reset().parameter("renewalPeriod").value(renewalPeriod).notBlank();
	        throwExceptionIfValidationWarningsExist(dataValidationErrors);
		
	}

	public void validateForDisconnectOrder(String json) {
		 if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("order");

	        final JsonElement element = fromApiJsonHelper.parse(json);
	        final LocalDate disconnectionDate = fromApiJsonHelper.extractLocalDateNamed("disconnectionDate", element);
	        baseDataValidator.reset().parameter("disconnectionDate").value(disconnectionDate).notBlank();
	        
	        final String disconnectReason = fromApiJsonHelper.extractStringNamed("disconnectReason", element);
	        baseDataValidator.reset().parameter("disconnectReason").value(disconnectReason).notBlank();
	     
	        throwExceptionIfValidationWarningsExist(dataValidationErrors);
		
	}

	public void validateForRetrack(String json) {
		
		if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, retracksupportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("order");
        final JsonElement element = fromApiJsonHelper.parse(json);
        
        final String commandName = fromApiJsonHelper.extractStringNamed("commandName", element);
        baseDataValidator.reset().parameter("commandName").value(commandName).notBlank();
      
        if(commandName!=null && commandName.equalsIgnoreCase("OSM")){
        	 final String message = fromApiJsonHelper.extractStringNamed("message", element);
             baseDataValidator.reset().parameter("message").value(message).notBlank().notExceedingLengthOf(160);
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
		
	}

	public void validateForPromo(String json) {
		
		if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("order");
        final JsonElement element = fromApiJsonHelper.parse(json);
        
        final Long promoId = fromApiJsonHelper.extractLongNamed("promoId", element);
        baseDataValidator.reset().parameter("promoId").value(promoId).notBlank();
        
        final LocalDate startDate=fromApiJsonHelper.extractLocalDateNamed("startDate", element);
        baseDataValidator.reset().parameter("startDate").value(startDate).notBlank();
      
       
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
		
	}

	public void validateForOrderSuspension(final String json) {
		
		if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);
        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("order");
        final JsonElement element = fromApiJsonHelper.parse(json);
        final LocalDate suspensionDate=fromApiJsonHelper.extractLocalDateNamed("suspensionDate", element);
        baseDataValidator.reset().parameter("suspensionDate").value(suspensionDate).notBlank();
        final String suspensionReason = fromApiJsonHelper.extractStringNamed("suspensionReason", element);
        baseDataValidator.reset().parameter("suspensionReason").value(suspensionReason).notBlank();
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
		
	}
	
	public void validateForOrderRenewalWithClient(final String json) {
		
		if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
		
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("orderrenewal");
		final JsonElement element = fromApiJsonHelper.parse(json);
		final String planId=fromApiJsonHelper.extractStringNamed("planId", element);
		baseDataValidator.reset().parameter("planId").value(planId).notBlank().validateforNumeric();
		final String duration = fromApiJsonHelper.extractStringNamed("duration", element);
		baseDataValidator.reset().parameter("duration").value(duration).notBlank();
		
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
		
	}
}