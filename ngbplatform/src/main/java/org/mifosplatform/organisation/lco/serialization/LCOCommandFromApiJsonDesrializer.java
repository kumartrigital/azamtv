package org.mifosplatform.organisation.lco.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
@Component
public class LCOCommandFromApiJsonDesrializer {
	
	private FromJsonHelper fromJsonHelper;
	private final Set<String> supportedParams = new HashSet<String>(Arrays.asList("lco","accountNo","balanceAmount","balanceCheck","displayName","endDate","id","orderId","phone","startDate","stbId","dateFormat","locale"));
	
	@Autowired
	public LCOCommandFromApiJsonDesrializer(FromJsonHelper fromJsonHelper) {
		// TODO Auto-generated constructor stub
		this.fromJsonHelper = fromJsonHelper;
	}
	
	public void validateForRenewal(String json) {
		
		if(StringUtils.isBlank(json)){
			throw new InvalidJsonException();
		}
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;}.getType(); 
		fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, supportedParams);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
		final DataValidatorBuilder baseValidatorBuilder = new DataValidatorBuilder(dataValidationErrors);
		final JsonElement element = this.fromJsonHelper.parse(json);
		final LocalDate startDate = fromJsonHelper.extractLocalDateNamed("startDate",element);
		baseValidatorBuilder.reset().parameter("startDate").value(startDate).notBlank();
		final LocalDate endDate = fromJsonHelper.extractLocalDateNamed("endDate",element);
		baseValidatorBuilder.reset().parameter("endDate").value(endDate).notBlank();
		
		
				
	}
}
