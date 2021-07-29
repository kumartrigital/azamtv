/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.data;

import static org.mifosplatform.portfolio.client.api.SavingsApiConstants.activationDateParamName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public final class ClientDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("billMode","address","parentId","clientPoId"));
    private final Set<String> supportedParentParameters = new HashSet<String>(Arrays.asList("accountNo","displayName"));
    private final Set<String> supportedBeesmartParameters = new HashSet<String>(Arrays.asList("accountNo","userId"));
    private final ClientReadPlatformService clientReadPlatformService;
    @Autowired
    public ClientDataValidator(final FromJsonHelper fromApiJsonHelper, final ClientReadPlatformService clientReadPlatformService) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.clientReadPlatformService = clientReadPlatformService;
        
    }

    public void validateForCreate(final String json, boolean isSelfcareEnable,boolean isPropertyCodeEnable) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiConstants.CLIENT_CREATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = fromApiJsonHelper.parse(json);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);
        
        /*List<ClientData> phonesAndEmailsList=this.clientReadPlatformService.retriveAllPhonesAndEmails();
        
        List<String> phones = new ArrayList<>();
        List<String> emails =  new ArrayList<>();
        
        for(ClientData clientData : phonesAndEmailsList){
        	phones.add(clientData.getPhone());
        	emails.add(clientData.getEmail());
        }*/
        
        final Boolean flag=fromApiJsonHelper.extractBooleanNamed("flag", element);
        if(flag==true){
        final String login = fromApiJsonHelper.extractStringNamed("login", element);
        baseDataValidator.reset().parameter("login").value(login).notBlank().notExceedingLengthOf(20);
        
        final String password = fromApiJsonHelper.extractStringNamed("password", element);
        baseDataValidator.reset().parameter("password").value(password).notBlank().notExceedingLengthOf(60);
        }
        
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.accountNoParamName, element)) {
            final String accountNo = fromApiJsonHelper.extractStringNamed(ClientApiConstants.accountNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.accountNoParamName).value(accountNo).notBlank().notExceedingLengthOf(20);
        }

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.externalIdParamName, element)) {
            final String externalId = fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIdParamName).value(externalId).ignoreIfNull()
                    .notExceedingLengthOf(100);
        }

        final Long officeId = fromApiJsonHelper.extractLongNamed(ClientApiConstants.officeIdParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.officeIdParamName).value(officeId).notNull().integerGreaterThanZero();

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.groupIdParamName, element)) {
            final Long groupId = fromApiJsonHelper.extractLongNamed(ClientApiConstants.groupIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.groupIdParamName).value(groupId).notNull().integerGreaterThanZero();
        }
        

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.clientCategoryParamName, element)) {
            final Long clientCategory = fromApiJsonHelper.extractLongNamed(ClientApiConstants.clientCategoryParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.clientCategoryParamName).value(clientCategory).notNull().integerGreaterThanZero();
        }

   //     final Boolean active = fromApiJsonHelper.extractBooleanNamed(ClientApiConstants.activeParamName, element);
        /*if (active != null) {
            if (active.booleanValue()) {
                final LocalDate joinedDate = fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.activationDateParamName, element);
                baseDataValidator.reset().parameter(ClientApiConstants.activationDateParamName).value(joinedDate).notNull();
            }
        } else {
            baseDataValidator.reset().parameter(ClientApiConstants.activeParamName).value(active).trueOrFalseRequired(false);
        }*/
        
       /* if (fromApiJsonHelper.parameterExists(ClientApiConstants.addressNoParamName, element)) {
            final String addrNo= fromApiJsonHelper.extractStringNamed(ClientApiConstants.addressNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.addressNoParamName).value(addrNo).notNull()
                    .notExceedingLengthOf(100);
        }*/
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.streetParamName, element)) {
            final String street = fromApiJsonHelper.extractStringNamed(ClientApiConstants.streetParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.streetParamName).value(street).notNull()
                    .notExceedingLengthOf(100);
        }
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.cityParamName, element)) {
            final String city= fromApiJsonHelper.extractStringNamed(ClientApiConstants.cityParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.cityParamName).value(city).notNull()
                    .notExceedingLengthOf(100);
        }
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.stateParamName, element)) {
            final String state = fromApiJsonHelper.extractStringNamed(ClientApiConstants.stateParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.stateParamName).value(state).notNull()
                    .notExceedingLengthOf(100);
        }
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.countryParamName, element)) {
            final String country= fromApiJsonHelper.extractStringNamed(ClientApiConstants.countryParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.countryParamName).value(country).notNull()
                    .notExceedingLengthOf(100);
        }
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.zipCodeParamName, element)) {
            final String zipCode= fromApiJsonHelper.extractStringNamed(ClientApiConstants.zipCodeParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.zipCodeParamName).value(zipCode).notNull()
                    .notExceedingLengthOf(100);
        }
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.phoneParamName, element)) {
            final String phone = fromApiJsonHelper.extractStringNamed(ClientApiConstants.phoneParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.phoneParamName).value(phone).notNull()
                    .notExceedingLengthOf(16);
         /*   boolean contains = phones.contains(phone);
            if(contains)
            	dataValidationErrors.add(ApiParameterError.parameterError("Duplicate Phone Number","Duplicate Phone Number", "phone",phone));
         */   
        }if (fromApiJsonHelper.parameterExists(ClientApiConstants.homePhoneNumberParamName, element)) {
            final String homePhoneNumber = fromApiJsonHelper.extractStringNamed(ClientApiConstants.homePhoneNumberParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.homePhoneNumberParamName).value(homePhoneNumber).notNull()
                    .notExceedingLengthOf(15);
        }
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.emailParamName, element)) {
            final String email = fromApiJsonHelper.extractStringNamed(ClientApiConstants.emailParamName, element);
       
            if(email!=null){
            	  baseDataValidator.reset().parameter(ClientApiConstants.emailParamName).value(email).notNull();
            	final Boolean isValid = email.matches(ClientApiConstants.EMAIL_REGEX);
         /*   boolean contains = emails.contains(email);
            if(contains)
            	dataValidationErrors.add(ApiParameterError.parameterError("Duplicate Email Id","Duplicate Email Id", "email",email));
            if(!isValid)
            	dataValidationErrors.add(ApiParameterError.parameterError("Invalid Email Address","Invalid Email Address", "email",email));
         */   	
            }   
        }
        if (isSelfcareEnable) {
        	final String email = fromApiJsonHelper.extractStringNamed(ClientApiConstants.emailParamName, element);
        	baseDataValidator.reset().parameter(ClientApiConstants.emailParamName).value(email).notNull();
        }
        
        if (isPropertyCodeEnable) {
        	final String addressNo = fromApiJsonHelper.extractStringNamed(ClientApiConstants.addressNoParamName, element);
        	if(addressNo == null)
        		dataValidationErrors.add(ApiParameterError.parameterError("error.msg.propertycode.not.null","Property Code can not be null", "propertyCode",addressNo));
        }
        
        
        final String billMode=this.fromApiJsonHelper.extractStringNamed(ClientApiConstants.billModeParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.billModeParamName).value(billMode).notBlank();
        
        
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.districtParamName, element)) {
            final String district= fromApiJsonHelper.extractStringNamed(ClientApiConstants.districtParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.districtParamName).value(district).notNull()
                    .notExceedingLengthOf(100);
        }
        
        
        
        /*if (fromApiJsonHelper.parameterExists(ClientApiConstants.billDayOfMonthParamName, element)) {
            final Long billDayOfMonth = fromApiJsonHelper.extractLongNamed(ClientApiConstants.billDayOfMonthParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.billDayOfMonthParamName).value(billDayOfMonth).notNull().integerGreaterThanZero();
        }
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.billCurrencyParamName, element)) {
            final Long billCurrency = fromApiJsonHelper.extractLongNamed(ClientApiConstants.billCurrencyParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.billCurrencyParamName).value(billCurrency).notNull().integerGreaterThanZero();
        }
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.billFrequencyParamName, element)) {
            final Long billFrequency = fromApiJsonHelper.extractLongNamed(ClientApiConstants.billFrequencyParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.billFrequencyParamName).value(billFrequency).notNull();
        }*/
        
        if(fromApiJsonHelper.parameterExists(ClientApiConstants.idKeyParamName, element)){
        	if(!fromApiJsonHelper.parameterExists(ClientApiConstants.idValueParamName, element)){
        		dataValidationErrors.add(ApiParameterError.parameterError("Id Proof Value can not be null","error.msg.ProofValue.not.null", "Id Value",""));
        	}else{
        		String idValue = fromApiJsonHelper.extractStringNamed("idValue", element);
        		if(idValue==null||idValue.equals("")){
        			dataValidationErrors.add(ApiParameterError.parameterError("Id Proof Value can not be null","error.msg.ProofValue.not.null", "Id Value",""));
                }
        	}
        }
        
        if(fromApiJsonHelper.parameterExists(ClientApiConstants.idValueParamName, element)){
        	final String idValue = fromApiJsonHelper.extractStringNamed("idValue", element);
        	if(!fromApiJsonHelper.parameterExists(ClientApiConstants.idKeyParamName, element)){
        		dataValidationErrors.add(ApiParameterError.parameterError("Value is Entered Select The Id Also","error.msg.ProofId.not.null", "Id Value",idValue));
        	}
        }
        
        
        System.out.println(dataValidationErrors);
        
        
         throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiConstants.CLIENT_UPDATE_REQUEST_DATA_PARAMETERS);
        final JsonElement element = fromApiJsonHelper.parse(json);
        
        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);
        
        final Long officeId = fromApiJsonHelper.extractLongNamed(ClientApiConstants.officeIdParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.officeIdParamName).value(officeId).notNull().integerGreaterThanZero();
        
        boolean atLeastOneParameterPassedForUpdate = false;
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.accountNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String accountNo = fromApiJsonHelper.extractStringNamed(ClientApiConstants.accountNoParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.accountNoParamName).value(accountNo).notBlank().notExceedingLengthOf(20);
        }

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.externalIdParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String externalId = fromApiJsonHelper.extractStringNamed(ClientApiConstants.externalIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.externalIdParamName).value(externalId).notExceedingLengthOf(100);
        }
        

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.clientCategoryParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String categeory = fromApiJsonHelper.extractStringNamed(ClientApiConstants.clientCategoryParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.clientCategoryParamName).value(categeory).notNull().notExceedingLengthOf(100);
        }
        
        
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.phoneParamName, element)) {
            final String phone = fromApiJsonHelper.extractStringNamed(ClientApiConstants.phoneParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.phoneParamName).value(phone).notNull()
                    .notExceedingLengthOf(16);
        }
      
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.emailParamName, element)) {
            final String email = fromApiJsonHelper.extractStringNamed(ClientApiConstants.emailParamName, element);
            
      //      baseDataValidator.reset().parameter(ClientApiConstants.emailParamName).value(email).notNull();
                 
            if(email!=null){
            	final Boolean isValid = email.matches(ClientApiConstants.EMAIL_REGEX);
            	if(!isValid)
            dataValidationErrors.add(ApiParameterError.parameterError("Invalid Email Address","Invalid Email Address", "email",email));
            
            }
            
        }
        

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.clientCategoryParamName, element)) {
            final Long categoryId = fromApiJsonHelper.extractLongNamed(ClientApiConstants.clientCategoryParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.clientCategoryParamName).value(categoryId).notNull().integerGreaterThanZero();
        }

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.fullnameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.lastnameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.middlenameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

        if (fromApiJsonHelper.parameterExists(ClientApiConstants.firstnameParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
        }

        final Boolean active = fromApiJsonHelper.extractBooleanNamed(ClientApiConstants.activeParamName, element);
        if (active != null) {
            if (active.booleanValue()) {
                final LocalDate joinedDate = fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.activationDateParamName, element);
                baseDataValidator.reset().parameter(ClientApiConstants.activationDateParamName).value(joinedDate).notNull();
            }
        }

        if (!atLeastOneParameterPassedForUpdate) {
            final Object forceError = null;
            baseDataValidator.reset().anyOfNotNull(forceError);
        }
        
        final String addressKeyParamName = "addressType";
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.addressKeyParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String addressKey = fromApiJsonHelper.extractStringNamed(ClientApiConstants.addressKeyParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.addressKeyParamName).value(addressKey).notExceedingLengthOf(100);
        }
        
        final String addressNoParamName = "addressNo";
        if (fromApiJsonHelper.parameterExists(ClientApiConstants.addressNoParamName, element)) {
            atLeastOneParameterPassedForUpdate = true;
            final String addressNo = fromApiJsonHelper.extractStringNamed(ClientApiConstants.addressNoParamName, element);
			baseDataValidator.reset().parameter(ClientApiConstants.addressNoParamName).value(addressNo).notExceedingLengthOf(100);
        }
        
		
		final String streetParamName = "street";
	    if (fromApiJsonHelper.parameterExists(ClientApiConstants.streetParamName, element)) {
	         atLeastOneParameterPassedForUpdate = true;
	         final String street = fromApiJsonHelper.extractStringNamed(ClientApiConstants.streetParamName, element);
			 baseDataValidator.reset().parameter(ClientApiConstants.streetParamName).value(street).notExceedingLengthOf(100);
	     }
        
        final String zipParamName = "zipCode";
	    if (fromApiJsonHelper.parameterExists(ClientApiConstants.zipParamName, element)) {
	         atLeastOneParameterPassedForUpdate = true;
	         final String zipCode = fromApiJsonHelper.extractStringNamed(ClientApiConstants.zipParamName, element);
			 baseDataValidator.reset().parameter(ClientApiConstants.zipParamName).value(zipCode).notExceedingLengthOf(100);
	     }
	   
        final String cityParamName = "city";
	    if (fromApiJsonHelper.parameterExists(ClientApiConstants.cityParamName, element)) {
	         atLeastOneParameterPassedForUpdate = true;
	         final String city = fromApiJsonHelper.extractStringNamed(ClientApiConstants.cityParamName, element);
			 baseDataValidator.reset().parameter(ClientApiConstants.cityParamName).value(city).notExceedingLengthOf(100);
	     }
	    
		final String stateParamName = "state";
	    if (fromApiJsonHelper.parameterExists(ClientApiConstants.stateParamName, element)) {
	         atLeastOneParameterPassedForUpdate = true;
	         final String state = fromApiJsonHelper.extractStringNamed(ClientApiConstants.stateParamName, element);
			 baseDataValidator.reset().parameter(ClientApiConstants.stateParamName).value(state).notExceedingLengthOf(100);
	     }
        
		final String countryParamName = "country";
	    if (fromApiJsonHelper.parameterExists(ClientApiConstants.countryParamName, element)) {
	         atLeastOneParameterPassedForUpdate = true;
	         final String country = fromApiJsonHelper.extractStringNamed(ClientApiConstants.countryParamName, element);
			 baseDataValidator.reset().parameter(ClientApiConstants.countryParamName).value(country).notExceedingLengthOf(100);
	     }
	    
        final String districtParamName = "district";
	    if (fromApiJsonHelper.parameterExists(ClientApiConstants.districtParamName, element)) {
	         atLeastOneParameterPassedForUpdate = true;
	         final String district = fromApiJsonHelper.extractStringNamed(ClientApiConstants.districtParamName, element);
			 baseDataValidator.reset().parameter(ClientApiConstants.districtParamName).value(district).notExceedingLengthOf(100);
	     }
	    

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateActivation(final JsonCommand command) {
        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiConstants.ACTIVATION_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate activationDate = fromApiJsonHelper.extractLocalDateNamed(activationDateParamName, element);
        baseDataValidator.reset().parameter(activationDateParamName).value(activationDate).notNull();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            //
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }

	public void validateClose(JsonCommand command) {

        final String json = command.json();

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, ClientApiConstants.CLIENT_CLOSE_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(ClientApiConstants.CLIENT_RESOURCE_NAME);

        final JsonElement element = command.parsedJson();

        final LocalDate closureDate = this.fromApiJsonHelper.extractLocalDateNamed(ClientApiConstants.closureDateParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.closureDateParamName).value(closureDate).notNull();

        final Long closureReasonId = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.closureReasonIdParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.closureReasonIdParamName).value(closureReasonId).notNull()
                .longGreaterThanZero();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

	public void ValidateBillMode(final JsonCommand command) {
		
		 final String json = command.json();
		
		 if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("billMode");

	        final JsonElement element = fromApiJsonHelper.parse(json);
	        
	        final String billMode=this.fromApiJsonHelper.extractStringNamed("billMode", element);
	        baseDataValidator.reset().parameter("billMode").value(billMode).notBlank();
	        
	
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}

	public void ValidateParent(final JsonCommand command) {
		
		 final String json = command.json();
			
		 if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParentParameters);

	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("parentclient");

	        final JsonElement element = fromApiJsonHelper.parse(json);
	        
	        final String accountNo=this.fromApiJsonHelper.extractStringNamed("accountNo", element);
	        baseDataValidator.reset().parameter("accountNo").value(accountNo).notBlank();
	
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}
	
	public void ValidateBeesmartUpdateClient(final JsonCommand command) {
		
		final String json = command.json();
		
		if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }
		
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedBeesmartParameters);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("beesmart");
		
		final JsonElement element = fromApiJsonHelper.parse(json);
		
		final String accountNo=this.fromApiJsonHelper.extractStringNamed("accountNo", element);
		baseDataValidator.reset().parameter("accountNo").value(accountNo).notBlank();
		
		final String userId=this.fromApiJsonHelper.extractStringNamed("userId", element);
		baseDataValidator.reset().parameter("userId").value(userId).notBlank().validateforNumeric();
		
		throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}
	
}