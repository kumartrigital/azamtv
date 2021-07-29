/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.serialization;

import java.lang.reflect.Type;
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
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * Deserializer of JSON for office API.
 */
@Component
public final class OfficeCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("name", "parentId", "openingDate", "externalId",
            "locale", "dateFormat", "officeType","officeNumber","phoneNumber","email","city","state","country","poId","parentTransfer","dasType",
            "addressName","contactPerson","zip","businessType","district","pancardNo","companyRegNo","gstRegNo","commisionModel","subscriberdues", "payment","clientData"));

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public OfficeCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("office");

        final JsonElement element = fromApiJsonHelper.parse(json);

        final String name = fromApiJsonHelper.extractStringNamed("name", element);
        baseDataValidator.reset().parameter("name").value(name).notBlank();

        final LocalDate openingDate = fromApiJsonHelper.extractLocalDateNamed("openingDate", element);
        baseDataValidator.reset().parameter("openingDate").value(openingDate).notNull();

        if (fromApiJsonHelper.parameterExists("externalId", element)) {
            final String externalId = fromApiJsonHelper.extractStringNamed("externalId", element);
            baseDataValidator.reset().parameter("externalId").value(externalId).notExceedingLengthOf(100);
        }
        
        final String officeType = fromApiJsonHelper.extractStringNamed("officeType", element);
        baseDataValidator.reset().parameter("officeType").value(officeType).notNull();

        if (fromApiJsonHelper.parameterExists("parentId", element)) {
            final Long parentId = fromApiJsonHelper.extractLongNamed("parentId", element);
            baseDataValidator.reset().parameter("parentId").value(parentId).notNull().integerGreaterThanZero();
        }
        
        final String phoneNumber = fromApiJsonHelper.extractStringNamed("phoneNumber", element);
        baseDataValidator.reset().parameter("phoneNumber").value(phoneNumber).notBlank().validateMobileNumber(phoneNumber);
        
        final String email = fromApiJsonHelper.extractStringNamed("email", element);
        baseDataValidator.reset().parameter("email").value(email).notBlank().validateEmailExpresstion(email);
        
        final String city = fromApiJsonHelper.extractStringNamed("city", element);
        baseDataValidator.reset().parameter("city").value(city).notBlank();
        
        final String state = fromApiJsonHelper.extractStringNamed("state", element);
        baseDataValidator.reset().parameter("state").value(state).notBlank();

        final String country = fromApiJsonHelper.extractStringNamed("country", element);
        baseDataValidator.reset().parameter("country").value(country).notBlank();
        
        
        final String addressName = fromApiJsonHelper.extractStringNamed("addressName", element);
        baseDataValidator.reset().parameter("addressName").value(addressName).notBlank().notExceedingLengthOf(15);
        
        final String contactPerson = fromApiJsonHelper.extractStringNamed("contactPerson", element);
       /* if ( !contactPerson.matches("[a-zA-Z0-9]+")){

			dataValidationErrors.add(ApiParameterError.parameterError(" special characters not allowed in contactPerson"," special characters not allowed in contactPerson" , "contactPerson" ,contactPerson));

			 }*/ 
        baseDataValidator.reset().parameter("contactPerson").value(contactPerson).notBlank().notExceedingLengthOf(15);
        
        final String zip = fromApiJsonHelper.extractStringNamed("zip", element);
        baseDataValidator.reset().parameter("zip").value(zip).notBlank();
        
        final String businessType = fromApiJsonHelper.extractStringNamed("businessType", element);
        baseDataValidator.reset().parameter("businessType").value(businessType).notBlank();
        
        final String district = fromApiJsonHelper.extractStringNamed("district", element);
        baseDataValidator.reset().parameter("district").value(district).notBlank();
        
        final String pancardNo = fromApiJsonHelper.extractStringNamed("pancardNo", element);
        baseDataValidator.reset().parameter("pancardNo").value(pancardNo).notBlank().notExceedingLengthOf(20);

        final String companyRegNo = fromApiJsonHelper.extractStringNamed("companyRegNo", element);
        if ( !companyRegNo.matches("[a-zA-Z0-9]+")){

			dataValidationErrors.add(ApiParameterError.parameterError(" special characters not allowed in companyRegNo"," special characters not allowed in companyRegNo" , "companyRegNo" ,companyRegNo));

			 } 
		
        baseDataValidator.reset().parameter("companyRegNo").value(companyRegNo).notBlank().notExceedingLengthOf(21);

        final String gstRegNo = fromApiJsonHelper.extractStringNamed("gstRegNo", element);
        if ( !gstRegNo.matches("[a-zA-Z0-9]+")){

			dataValidationErrors.add(ApiParameterError.parameterError(" special characters not allowed in GST RegNo"," special characters not allowed in GST RegNo" , "gstRegNo" ,gstRegNo));

			 }
        baseDataValidator.reset().parameter("gstRegNo").value(gstRegNo).notBlank().notExceedingLengthOf(15);

        final String commisionModel = fromApiJsonHelper.extractStringNamed("commisionModel", element);
        baseDataValidator.reset().parameter("commisionModel").value(commisionModel).notBlank().notExceedingLengthOf(1);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("office");

        final JsonElement element = fromApiJsonHelper.parse(json);

        if (fromApiJsonHelper.parameterExists("name", element)) {
            final String name = fromApiJsonHelper.extractStringNamed("name", element);
            baseDataValidator.reset().parameter("name").value(name).notBlank().notExceedingLengthOf(100);
        }

        final String officeType = fromApiJsonHelper.extractStringNamed("officeType", element);
        baseDataValidator.reset().parameter("officeType").value(officeType).notNull();

        
        if (fromApiJsonHelper.parameterExists("openingDate", element)) {
            final LocalDate openingDate = fromApiJsonHelper.extractLocalDateNamed("openingDate", element);
            baseDataValidator.reset().parameter("openingDate").value(openingDate).notNull();
        }

        if (fromApiJsonHelper.parameterExists("externalId", element)) {
            final String externalId = fromApiJsonHelper.extractStringNamed("externalId", element);
            baseDataValidator.reset().parameter("externalId").value(externalId).notExceedingLengthOf(100);
        }

        if (fromApiJsonHelper.parameterExists("parentId", element)) {
            final Long parentId = fromApiJsonHelper.extractLongNamed("parentId", element);
            baseDataValidator.reset().parameter("parentId").value(parentId).notNull().integerGreaterThanZero();
        }
        
        if (fromApiJsonHelper.parameterExists("email", element)) {
            final String email = fromApiJsonHelper.extractStringNamed("email", element);
            baseDataValidator.reset().parameter("email").value(email).notBlank();
        }
        
        if (fromApiJsonHelper.parameterExists("phoneNumber", element)) {
            final String phoneNumber = fromApiJsonHelper.extractStringNamed("phoneNumber", element);
            baseDataValidator.reset().parameter("phoneNumber").value(phoneNumber).notBlank();
        }
        
        if (fromApiJsonHelper.parameterExists("city", element)) {
            final String city = fromApiJsonHelper.extractStringNamed("city", element);
            baseDataValidator.reset().parameter("city").value(city).notBlank();
        }
        
        if (fromApiJsonHelper.parameterExists("state", element)) {
            final String state = fromApiJsonHelper.extractStringNamed("state", element);
            baseDataValidator.reset().parameter("state").value(state).notBlank();
        }
        
        if (fromApiJsonHelper.parameterExists("country", element)) {
            final String country = fromApiJsonHelper.extractStringNamed("country", element);
            baseDataValidator.reset().parameter("country").value(country).notBlank();
        }
        
        if (fromApiJsonHelper.parameterExists("addressName", element)) {
            final String addressName = fromApiJsonHelper.extractStringNamed("addressName", element);
            baseDataValidator.reset().parameter("addressName").value(addressName).notBlank();
        }
        
        if (fromApiJsonHelper.parameterExists("contactPerson", element)) {
            final String contactPerson = fromApiJsonHelper.extractStringNamed("contactPerson", element);
            baseDataValidator.reset().parameter("contactPerson").value(contactPerson).notBlank();
        }
        
        if (fromApiJsonHelper.parameterExists("zip", element)) {
            final String zip = fromApiJsonHelper.extractStringNamed("zip", element);
            baseDataValidator.reset().parameter("zip").value(zip).notBlank();
        }
        
        if (fromApiJsonHelper.parameterExists("businessType", element)) {
            final String businessType = fromApiJsonHelper.extractStringNamed("businessType", element);
            baseDataValidator.reset().parameter("businessType").value(businessType).notBlank();
        }
        
        if (fromApiJsonHelper.parameterExists("district", element)) {
            final String district = fromApiJsonHelper.extractStringNamed("district", element);
            baseDataValidator.reset().parameter("district").value(district).notBlank();
        }
        
        
        /*final String email = fromApiJsonHelper.extractStringNamed("email", element);
        baseDataValidator.reset().parameter("email").value(email).notBlank();
        
        if(email!=null){
        	final Boolean isValid = email.matches(ClientApiConstants.EMAIL_REGEX);
        	if(!isValid)
        	dataValidationErrors.add(ApiParameterError.parameterError("error.invalid.email.address","Invalid Email Address", "email",email));
        
        } 
        
		final String city = fromApiJsonHelper.extractStringNamed("city", element);
		baseDataValidator.reset().parameter("city").value(city).notBlank().notExceedingLengthOf(100);
		
		final String state = fromApiJsonHelper.extractStringNamed("state", element);
		baseDataValidator.reset().parameter("state").value(state).notBlank().notExceedingLengthOf(100);
		
		final String country = fromApiJsonHelper.extractStringNamed("country", element);
		baseDataValidator.reset().parameter("country").value(country).notBlank().notExceedingLengthOf(100);*/

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}