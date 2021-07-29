package org.mifosplatform.logistics.item.serialization;

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

@Component
public class ItemCommandFromApiJsonDeserializer {
	 
	
	 	Set<String> supportedParameters = new HashSet<String>(Arrays.asList("itemCode","itemDescription","units","chargeCode","locale","unitPrice","warranty","itemClass",
	 			"reorderLevel","chargeCode", "itemPrices", "regionId", "price", "removeItemPrices","supplierId", "isProvisioning", "isSelector","selectorDescription","currencyId"));
	    private final FromJsonHelper fromApiJsonHelper;

	    @Autowired
	    public ItemCommandFromApiJsonDeserializer(final FromJsonHelper fromApiJsonHelper) {
	        this.fromApiJsonHelper = fromApiJsonHelper;
	    }

	    public void validateForCreate(final String json) {
	    	
	        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

	        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
	        fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParameters);

	        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("item");

	        final JsonElement element = fromApiJsonHelper.parse(json);

	        final String itemCode = fromApiJsonHelper.extractStringNamed("itemCode", element);
	        final String itemDescription = fromApiJsonHelper.extractStringNamed("itemDescription", element);
	        final String itemClass = fromApiJsonHelper.extractStringNamed("itemClass", element);
	        final String chargeCode = fromApiJsonHelper.extractStringNamed("chargeCode",element);
	        final String units = fromApiJsonHelper.extractStringNamed("units", element);
	       /* final Integer w = fromApiJsonHelper.extractIntegerNamed("warranty", element, fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject()));
	        Long warranty = null;
	        if(w!=null){
	        warranty = w.longValue();
	        }*/
	        
	        final BigDecimal defaultPrice = fromApiJsonHelper.extractBigDecimalNamed("unitPrice", element, fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject()));
	        
	        final BigDecimal warranty1 = fromApiJsonHelper.extractBigDecimalNamed("warranty", element, fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject()));
	        final Integer warranty = fromApiJsonHelper.extractIntegerNamed("warranty",element, fromApiJsonHelper.extractLocaleParameter(element.getAsJsonObject()));
	        final Long currencyId = fromApiJsonHelper.extractLongNamed("currencyId", element);
	        
	        baseDataValidator.reset().parameter("itemCode").value(itemCode).notBlank().notExceedingLengthOf(10);
	        baseDataValidator.reset().parameter("itemDescription").value(itemDescription).notBlank();
	        baseDataValidator.reset().parameter("chargeCode").value(chargeCode).notNull().notExceedingLengthOf(10);
	        baseDataValidator.reset().parameter("warranty").value(warranty).notNull().notExceedingLengthOf(2);
			baseDataValidator.reset().parameter("itemClass").value(itemClass).notBlank();
			baseDataValidator.reset().parameter("units").value(units).notBlank();
			baseDataValidator.reset().parameter("defaultPrice").value(defaultPrice).notNull();
	        baseDataValidator.reset().parameter("currencyId").value(currencyId).notBlank();

			final JsonArray itemPricesArray = fromApiJsonHelper.extractJsonArrayNamed("itemPrices", element);
	        String[] itemPriceRegions = null;
	        itemPriceRegions = new String[itemPricesArray.size()];
	        final int itemPricesArraySize = itemPricesArray.size();
		    baseDataValidator.reset().parameter("itemPrices").value(itemPricesArraySize).integerGreaterThanZero();
	        if(itemPricesArraySize > 0){
		    for(int i = 0; i < itemPricesArray.size(); i++){
		    	itemPriceRegions[i] = itemPricesArray.get(i).toString();
		    	
		    }
	
			 for (final String itemPriceRegion : itemPriceRegions) {
				 
				     final JsonElement attributeElement = fromApiJsonHelper.parse(itemPriceRegion);
				     final BigDecimal price = fromApiJsonHelper.extractBigDecimalNamed("price", attributeElement, fromApiJsonHelper.extractLocaleParameter(attributeElement.getAsJsonObject()));
				     baseDataValidator.reset().parameter("price").value(price).notNull().notExceedingLengthOf(22);
				 
				     final String regionId = fromApiJsonHelper.extractStringNamed("regionId", attributeElement);
				     baseDataValidator.reset().parameter("regionId").value(regionId).notBlank().notExceedingLengthOf(30);

			  }
	        }
	        throwExceptionIfValidationWarningsExist(dataValidationErrors);
	    }
	    
	    
	    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
	        if (!dataValidationErrors.isEmpty()) { 
	        	throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist","Validation errors exist.",dataValidationErrors); }
	    }

}
