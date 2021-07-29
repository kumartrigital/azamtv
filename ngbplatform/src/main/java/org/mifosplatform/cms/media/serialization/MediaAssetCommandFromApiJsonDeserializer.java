package org.mifosplatform.cms.media.serialization;

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
public final class MediaAssetCommandFromApiJsonDeserializer {

    /**
     * The parameters supported for this command.
     */
    private final Set<String> supportedParameters = new HashSet<String>(Arrays.asList("mediaTitle", "mediatype",
    		  "catageoryId", "genre", "releaseDate", "overview", "subject", "mediaImage", "duration", "contentProvider",
    		  "rated", "mediaRating", "ratingCount", "status", "mediaassetAttributes", "mediaAssetLocations", "locale",
    		  "dateFormat", "attributeName", "formatType", "languageId", "cpShareValue", "mediaTypeCheck", "mediaDetailType", "location","attributevalue","attributeNickname"));
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public MediaAssetCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("mediaasset");

        final JsonElement element = fromApiJsonHelper.parse(json);

        final String mediaTitle = fromApiJsonHelper.extractStringNamed("mediaTitle", element);
        baseDataValidator.reset().parameter("mediaTitle").value(mediaTitle).notBlank().notExceedingLengthOf(250);
        
        final Integer mediaCategoryId = fromApiJsonHelper.extractIntegerSansLocaleNamed("catageoryId", element);
        baseDataValidator.reset().parameter("catageoryId").value(mediaCategoryId).notNull().integerGreaterThanZero();
        
        final Long contentProvider = fromApiJsonHelper.extractLongNamed("contentProvider", element);
        baseDataValidator.reset().parameter("contentProvider").value(contentProvider).notExceedingLengthOf(70);
        
        final LocalDate releaseDate = fromApiJsonHelper.extractLocalDateNamed("releaseDate", element);
        baseDataValidator.reset().parameter("releaseDate").value(releaseDate).notBlank();
         
        final String status=fromApiJsonHelper.extractStringNamed("status", element);
        baseDataValidator.reset().parameter("status").value(status).notBlank().notExceedingLengthOf(20);
        
        final BigDecimal cpShareValue = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("cpShareValue", element);
        baseDataValidator.reset().parameter("cpShareValue").value(cpShareValue).inMinAndMaxAmountRange(BigDecimal.ZERO, BigDecimal.valueOf(100));
      
        
       /*final String mediaType = fromApiJsonHelper.extractStringNamed("mediatype", element);
        baseDataValidator.reset().parameter("mediatype").value(mediaType).notBlank().notExceedingLengthOf(50);
        final String image = fromApiJsonHelper.extractStringNamed("mediaImage", element);
        baseDataValidator.reset().parameter("mediaImage").value(image).notBlank();
        final String duration = fromApiJsonHelper.extractStringNamed("duration", element);
        baseDataValidator.reset().parameter("duration").value(duration).notBlank();
        final String genre = fromApiJsonHelper.extractStringNamed("genre", element);
        baseDataValidator.reset().parameter("genre").value(genre).notBlank();
        final String rated = fromApiJsonHelper.extractStringNamed("rated", element);
        baseDataValidator.reset().parameter("rated").value(rated).notBlank();
        final BigDecimal rating=fromApiJsonHelper.extractBigDecimalWithLocaleNamed("mediaRating", element);
        baseDataValidator.reset().parameter("mediaRating").value(rating).notNull();
        final LocalDate releaseDate = fromApiJsonHelper.extractLocalDateNamed("releaseDate", element);
        baseDataValidator.reset().parameter("releaseDate").value(releaseDate).notBlank();
        final String subject = fromApiJsonHelper.extractStringNamed("subject", element);
        baseDataValidator.reset().parameter("subject").value(subject).notBlank();
        
        */
        
        
       /**
         * This for validating media attributes and locations
         * This is for Advance Media
         * */
        
        final String mediaTypeCheck = fromApiJsonHelper.extractStringNamed("mediaTypeCheck", element);
        
        if("ADVANCEMEDIA".equalsIgnoreCase(mediaTypeCheck)){
        final JsonArray mediaassetAttributesArray=fromApiJsonHelper.extractJsonArrayNamed("mediaassetAttributes", element);
        
        //baseDataValidator.reset().parameter("mediaassetAttributes").value(mediaassetAttributesArray).
        String[] mediaassetAttributes = null;
	    mediaassetAttributes = new String[mediaassetAttributesArray.size()];
	    final int mediaassetAttributeSize = mediaassetAttributesArray.size();
	    baseDataValidator.reset().parameter("mediaassetAttributes").value(mediaassetAttributeSize).integerGreaterThanZero();
	    for(int i = 0; i < mediaassetAttributesArray.size(); i++){
	    	mediaassetAttributes[i] = mediaassetAttributesArray.get(i).toString();
	    	//JsonObject temp = mediaassetAttributesArray.getAsJsonObject();
	    }
	    	/**
	    	 * For Media Attributes
	    	 * */
		 for (final String mediaassetAttribute : mediaassetAttributes) {
			
			     final JsonElement attributeElement = fromApiJsonHelper.parse(mediaassetAttribute);
			     final String mediaAttributeType = fromApiJsonHelper.extractStringNamed("attributeType", attributeElement);
			     baseDataValidator.reset().parameter("attributeType").value(mediaAttributeType).notBlank().notExceedingLengthOf(60);
			     final String attributeName = fromApiJsonHelper.extractStringNamed("attributeName", attributeElement);
			     baseDataValidator.reset().parameter("attributeName").value(attributeName).notBlank().notExceedingLengthOf(60);
			     final String attributevalue = fromApiJsonHelper.extractStringNamed("attributevalue", attributeElement);
			     baseDataValidator.reset().parameter("attributevalue").value(attributevalue).notBlank().notExceedingLengthOf(60);
			     final String attributeNickname = fromApiJsonHelper.extractStringNamed("attributeNickname", attributeElement);
			     baseDataValidator.reset().parameter("attributeNickname").value(attributeNickname).notBlank().notExceedingLengthOf(60);
			     final String attributeImage = fromApiJsonHelper.extractStringNamed("attributeImage", attributeElement);
			     baseDataValidator.reset().parameter("attributeImage").value(attributeImage).notBlank().notExceedingLengthOf(150);
     
		  }
        
        final JsonArray mediaAssetLocationsArray = fromApiJsonHelper.extractJsonArrayNamed("mediaAssetLocations", element);
        // baseDataValidator.reset().parameter("mediaAssetLocations").value(mediaAssetLocationsArray).arrayNotEmpty();
        String[] mediaAssetLocations = null;
        mediaAssetLocations = new String[mediaAssetLocationsArray.size()];
        final int mediaassetLocationSize = mediaAssetLocationsArray.size();
	    baseDataValidator.reset().parameter("mediaAssetLocations").value(mediaassetLocationSize).integerGreaterThanZero();
	    for(int i = 0; i < mediaAssetLocationsArray.size(); i++){
	    	mediaAssetLocations[i] = mediaAssetLocationsArray.get(i).toString();
	    	//JsonObject temp = mediaassetAttributesArray.getAsJsonObject();
	    }
	    
	   /**
	    * For Media Location Validation
	    * */
	    
		 for (final String mediaAssetLocation : mediaAssetLocations) {
			 
			     final JsonElement attributeElement = fromApiJsonHelper.parse(mediaAssetLocation);
			     final Integer languageId = fromApiJsonHelper.extractIntegerSansLocaleNamed("languageId", attributeElement);
			     baseDataValidator.reset().parameter("languageId").value(languageId).notBlank().integerGreaterThanZero().notExceedingLengthOf(20);
			     final String formatType = fromApiJsonHelper.extractStringNamed("formatType", attributeElement);
			     baseDataValidator.reset().parameter("formatType").value(formatType).notBlank().notExceedingLengthOf(10);
			     final String location = fromApiJsonHelper.extractStringNamed("location", attributeElement);
			     baseDataValidator.reset().parameter("location").value(location).notBlank().notExceedingLengthOf(255);
		  }
     
        }else{
        	
        	final String location = fromApiJsonHelper.extractStringNamed("location", element);
            baseDataValidator.reset().parameter("location").value(location).notBlank().notExceedingLengthOf(255);
        }
              
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
        
    }

 

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    /**
	 * This is used for Validating media attributes and location
	 * whenever you requires use it
	 * */
	public void validateForCreateLocationAttributes(final String json) {
		
		if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("mediaasset");

        final JsonElement element = fromApiJsonHelper.parse(json);
        final String mediaDetailType = fromApiJsonHelper.extractStringNamed("mediaDetailType", element);
        
        /**
         * Validation checking starts from here
         * */
        if("ATTRIBUTES".equalsIgnoreCase(mediaDetailType)){
        	
        	final JsonArray mediaassetAttributesArray = fromApiJsonHelper.extractJsonArrayNamed("mediaassetAttributes", element);
        	String[] mediaassetAttributes = null;
        	mediaassetAttributes = new String[mediaassetAttributesArray.size()];
        	final int mediaassetAttributeSize = mediaassetAttributesArray.size();
        	baseDataValidator.reset().parameter("mediaassetAttributes").value(mediaassetAttributeSize).integerGreaterThanZero();
        	for(int i = 0; i < mediaassetAttributesArray.size(); i++){
	    	mediaassetAttributes[i] = mediaassetAttributesArray.get(i).toString();
	    	}
        	//For Media Attributes
        	for (final String mediaassetAttribute : mediaassetAttributes) {
			
			     final JsonElement attributeElement = fromApiJsonHelper.parse(mediaassetAttribute);
			     final String mediaAttributeType = fromApiJsonHelper.extractStringNamed("attributeType", attributeElement);
			     baseDataValidator.reset().parameter("attributeType").value(mediaAttributeType).notBlank().notExceedingLengthOf(60);
			     final String attributeName = fromApiJsonHelper.extractStringNamed("attributeName", attributeElement);
			     baseDataValidator.reset().parameter("attributeName").value(attributeName).notBlank().notExceedingLengthOf(60);
			     final String attributevalue = fromApiJsonHelper.extractStringNamed("attributevalue", attributeElement);
			     baseDataValidator.reset().parameter("attributevalue").value(attributevalue).notBlank().notExceedingLengthOf(60);
			     final String attributeNickname = fromApiJsonHelper.extractStringNamed("attributeNickname", attributeElement);
			     baseDataValidator.reset().parameter("attributeNickname").value(attributeNickname).notBlank().notExceedingLengthOf(60);
			     final String attributeImage = fromApiJsonHelper.extractStringNamed("attributeImage", attributeElement);
			     baseDataValidator.reset().parameter("attributeImage").value(attributeImage).notBlank().notExceedingLengthOf(150);
     
        	}
        }
        if("LOCATIONS".equalsIgnoreCase(mediaDetailType)){
        	final JsonArray mediaAssetLocationsArray = fromApiJsonHelper.extractJsonArrayNamed("mediaAssetLocations",element);
        	String[] mediaAssetLocations = null;
        	mediaAssetLocations = new String[mediaAssetLocationsArray.size()];
        	final int mediaassetLocationSize = mediaAssetLocationsArray.size();
        	baseDataValidator.reset().parameter("mediaAssetLocations").value(mediaassetLocationSize).integerGreaterThanZero();
        	for(int i = 0; i < mediaAssetLocationsArray.size(); i++){
        		mediaAssetLocations[i] = mediaAssetLocationsArray.get(i).toString();
        	}
        	//For Media Location Validation
        	for (final String mediaAssetLocation : mediaAssetLocations) {
			 
			     final JsonElement attributeElement = fromApiJsonHelper.parse(mediaAssetLocation);
			     final Integer languageId = fromApiJsonHelper.extractIntegerSansLocaleNamed("languageId", attributeElement);
			     baseDataValidator.reset().parameter("languageId").value(languageId).notBlank().integerGreaterThanZero().notExceedingLengthOf(20);
			     final String formatType = fromApiJsonHelper.extractStringNamed("formatType", attributeElement);
			     baseDataValidator.reset().parameter("formatType").value(formatType).notBlank().notExceedingLengthOf(10);
			     final String location = fromApiJsonHelper.extractStringNamed("location", attributeElement);
			     baseDataValidator.reset().parameter("location").value(location).notBlank().notExceedingLengthOf(255);
        	}
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
	}
}