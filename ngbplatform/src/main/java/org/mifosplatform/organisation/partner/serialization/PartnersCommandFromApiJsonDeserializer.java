package org.mifosplatform.organisation.partner.serialization;

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

@Component
public class PartnersCommandFromApiJsonDeserializer {

	/*
	 * The parameters supported for this command.
	 */
	private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("partnerType","partnerCode", "partnerName","loginName","password","phoneNumber","email",
			                            "city","state","country","currency","contactName","roleName","companyLogo","parentId","officeType","repeatPassword",
			                            "isCollective","creditLimit","locale","officeNumber","userId","roles","externalId","businessType","dateFormat","openingDate","district"));
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public PartnersCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}
	
	/**
	 * @param json
	 * check validation for create partner
	 */
	public void validateForCreate(final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("partner");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String partnerName = fromApiJsonHelper.extractStringNamed("partnerName", element);
		baseDataValidator.reset().parameter("partnerName").value(partnerName).notBlank().notExceedingLengthOf(20);
		
		
		final String loginName = fromApiJsonHelper.extractStringNamed("loginName", element);
		baseDataValidator.reset().parameter("loginName").value(loginName).notBlank().notExceedingLengthOf(20);
		
		final String password = fromApiJsonHelper.extractStringNamed("password", element);
        baseDataValidator.reset().parameter("password").value(password).notBlank().notExceedingLengthOf(60);
        
        final String repeatPassword = fromApiJsonHelper.extractStringNamed("repeatPassword", element);
        baseDataValidator.reset().parameter("repeatPassword").value(repeatPassword).notBlank().notExceedingLengthOf(60);
        
       // final BigDecimal creditLimit = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("creditLimit", element);
        
     /*   if(creditLimit != null){
        baseDataValidator.reset().parameter("creditLimit").value(creditLimit).
        }*/
        
        final Long parentId = fromApiJsonHelper.extractLongNamed("parentId", element);
        baseDataValidator.reset().parameter("parentId").value(parentId).notBlank();
        
       /* final String contactName = fromApiJsonHelper.extractStringNamed("contactName", element);
        baseDataValidator.reset().parameter("contactName").value(contactName).notBlank().notExceedingLengthOf(100);*/
        
        final String phone = fromApiJsonHelper.extractStringNamed("phoneNumber", element);
        baseDataValidator.reset().parameter("phoneNumber").value(phone).notBlank().notExceedingLengthOf(30);
        
        final String email = fromApiJsonHelper.extractStringNamed("email", element);
        baseDataValidator.reset().parameter("email").value(email).notBlank();
        
		final String city = fromApiJsonHelper.extractStringNamed("city", element);
		baseDataValidator.reset().parameter("city").value(city).notBlank().notExceedingLengthOf(100);
		
		final String district = fromApiJsonHelper.extractStringNamed("district", element);
		baseDataValidator.reset().parameter("district").value(district).notBlank().notExceedingLengthOf(100);
		
		
		final String state = fromApiJsonHelper.extractStringNamed("state", element);
		baseDataValidator.reset().parameter("state").value(state).notBlank().notExceedingLengthOf(100);
		
		final String country = fromApiJsonHelper.extractStringNamed("country", element);
		baseDataValidator.reset().parameter("country").value(country).notBlank().notExceedingLengthOf(100);
		
		final String currency = fromApiJsonHelper.extractStringNamed("currency", element);
        baseDataValidator.reset().parameter("currency").value(currency).notBlank();
        
        final LocalDate openingDate = fromApiJsonHelper.extractLocalDateNamed("openingDate", element);
        baseDataValidator.reset().parameter("openingDate").value(openingDate).notNull();

        if (fromApiJsonHelper.parameterExists("externalId", element)) {
            final String externalId = fromApiJsonHelper.extractStringNamed("externalId", element);
            baseDataValidator.reset().parameter("externalId").value(externalId).notExceedingLengthOf(100);
        }
        
        final String officeType = fromApiJsonHelper.extractStringNamed("officeType", element);
        baseDataValidator.reset().parameter("officeType").value(officeType).notNull();
        
        final String businessType = fromApiJsonHelper.extractStringNamed("businessType", element);
        baseDataValidator.reset().parameter("businessType").value(businessType).notBlank();
        
        
	
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}
	
	/**
	 * @param json
	 * check validation for update partner
	 */
	public void validateForUpdate(final String json) {
		
		if (StringUtils.isBlank(json)) {
			throw new InvalidJsonException();
		}

		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json,supportedParameters);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("partner");

		final JsonElement element = fromApiJsonHelper.parse(json);

		final String partnerName = fromApiJsonHelper.extractStringNamed("partnerName", element);
		baseDataValidator.reset().parameter("partnerName").value(partnerName).notBlank().notExceedingLengthOf(20);
		
		final String loginName = fromApiJsonHelper.extractStringNamed("loginName", element);
		baseDataValidator.reset().parameter("loginName").value(loginName).notBlank().notExceedingLengthOf(20);
		
        
        final BigDecimal creditLimit = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("creditLimit", element);
        
        if(creditLimit != null){
        baseDataValidator.reset().parameter("creditLimit").value(creditLimit).integerGreaterThanZero();
        }
        
        final Long parentId = fromApiJsonHelper.extractLongNamed("parentId", element);
        baseDataValidator.reset().parameter("parentId").value(parentId).notBlank();
        
       /* final String contactName = fromApiJsonHelper.extractStringNamed("contactName", element);
        baseDataValidator.reset().parameter("contactName").value(contactName).notBlank().notExceedingLengthOf(100);*/
        
        final String phoneNumber = fromApiJsonHelper.extractStringNamed("phoneNumber", element);
        baseDataValidator.reset().parameter("phone").value(phoneNumber).notBlank().notExceedingLengthOf(30);
        
        final String email = fromApiJsonHelper.extractStringNamed("email", element);
        baseDataValidator.reset().parameter("email").value(email).notBlank();
        
		final String city = fromApiJsonHelper.extractStringNamed("city", element);
		baseDataValidator.reset().parameter("city").value(city).notBlank().notExceedingLengthOf(100);
		
		final String district = fromApiJsonHelper.extractStringNamed("district", element);
		baseDataValidator.reset().parameter("district").value(district).notBlank().notExceedingLengthOf(100);
		
		final String state = fromApiJsonHelper.extractStringNamed("state", element);
		baseDataValidator.reset().parameter("state").value(state).notBlank().notExceedingLengthOf(100);
		
		final String country = fromApiJsonHelper.extractStringNamed("country", element);
		baseDataValidator.reset().parameter("country").value(country).notBlank().notExceedingLengthOf(100);
		
		final String currency = fromApiJsonHelper.extractStringNamed("currency", element);
        baseDataValidator.reset().parameter("currency").value(currency).notBlank();
        
        final LocalDate openingDate = fromApiJsonHelper.extractLocalDateNamed("openingDate", element);
        baseDataValidator.reset().parameter("openingDate").value(openingDate).notNull();

        if (fromApiJsonHelper.parameterExists("externalId", element)) {
            final String externalId = fromApiJsonHelper.extractStringNamed("externalId", element);
            baseDataValidator.reset().parameter("externalId").value(externalId).notExceedingLengthOf(100);
        }
        
        final String officeType = fromApiJsonHelper.extractStringNamed("officeType", element);
        baseDataValidator.reset().parameter("officeType").value(officeType).notNull();
        
        final String businessType = fromApiJsonHelper.extractStringNamed("businessType", element);
        baseDataValidator.reset().parameter("businessType").value(businessType).notBlank();
	
        
        throwExceptionIfValidationWarningsExist(dataValidationErrors);

	}


	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}
	}
}


