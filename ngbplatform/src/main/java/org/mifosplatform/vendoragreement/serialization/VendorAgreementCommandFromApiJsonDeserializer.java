package org.mifosplatform.vendoragreement.serialization;

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
public final class VendorAgreementCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("vendorCode", "agreementStatus",
    		  "startDate", "endDate", "contentType", "vendorDetails", "contentCode", "loyaltyType", "loyaltyShare",
    		  "priceRegion", "contentCost", "dateFormat", "locale", "removeVendorDetails","vendorId","fileLocation"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public VendorAgreementCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("vendor");

        final JsonElement element = fromApiJsonHelper.parse(json);

        final String agreementStatus = fromApiJsonHelper.extractStringNamed("agreementStatus", element);
        baseDataValidator.reset().parameter("agreementStatus").value(agreementStatus).notBlank().notExceedingLengthOf(20);
        
        final LocalDate agreementStartDate = fromApiJsonHelper.extractLocalDateNamed("startDate", element);
        baseDataValidator.reset().parameter("agreementStartDate").value(agreementStartDate).notBlank();
        
        final LocalDate agreementEndDate = fromApiJsonHelper.extractLocalDateNamed("endDate", element);
        baseDataValidator.reset().parameter("agreementEndDate").value(agreementEndDate).notBlank();
        int resultValue = agreementEndDate.compareTo(agreementStartDate);
        if(resultValue == -1){
        	
        	ApiParameterError error = ApiParameterError.parameterError("error.msg.enddate.greater.than.startdate",
            		"EndDate should be greater than StartDate","EndDate", resultValue);
        	dataValidationErrors.add(error);
        }
         
        final String contentType=fromApiJsonHelper.extractStringNamed("contentType", element);
        baseDataValidator.reset().parameter("contentType").value(contentType).notBlank().notExceedingLengthOf(20);
        
        /** Vendor Details */
        final JsonArray vendorDetailsArray = fromApiJsonHelper.extractJsonArrayNamed("vendorDetails", element);
        String[] vendorDetailsData = null;
        vendorDetailsData = new String[vendorDetailsArray.size()];
        final int mediaassetLocationSize = vendorDetailsArray.size();
	    baseDataValidator.reset().parameter("mediaAssetLocations").value(mediaassetLocationSize).integerGreaterThanZero();
	    for(int i = 0; i < vendorDetailsArray.size(); i++){
	    	vendorDetailsData[i] = vendorDetailsArray.get(i).toString();
	    }
	    
	   /**
	    * For Vendor Details Validation
	    * */
	    
		 for (final String vendorData : vendorDetailsData) {
			 
			     final JsonElement attributeElement = fromApiJsonHelper.parse(vendorData);
			     
			     final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", attributeElement);
			     baseDataValidator.reset().parameter("contentCode").value(contentCode).notBlank().notExceedingLengthOf(10);
			     
			     final String loyaltyType = fromApiJsonHelper.extractStringNamed("loyaltyType", attributeElement);
			     baseDataValidator.reset().parameter("loyaltyType").value(loyaltyType).notBlank().notExceedingLengthOf(10);
			     
			     final Integer priceRegion = fromApiJsonHelper.extractIntegerSansLocaleNamed("priceRegion", attributeElement);
			     baseDataValidator.reset().parameter("priceRegion").value(priceRegion).notBlank().integerGreaterThanZero().notExceedingLengthOf(20);
			     
			     if("NONE".equalsIgnoreCase(loyaltyType)){
			    	 final BigDecimal contentCost = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentCost", attributeElement);
				     baseDataValidator.reset().parameter("contentCost").value(contentCost).notBlank();
			     }else{
			    	 final BigDecimal loyaltyShare = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("loyaltyShare", attributeElement);
				     baseDataValidator.reset().parameter("loyaltyShare").value(loyaltyShare).inMinAndMaxAmountRange(BigDecimal.ZERO, BigDecimal.valueOf(100)).notBlank();
			     }
			           
		  }
              
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

}
