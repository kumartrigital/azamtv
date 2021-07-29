package org.mifosplatform.portfolio.property.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_property_master",uniqueConstraints = { @UniqueConstraint(name = "property_code_type_with_its_code",columnNames = { "property_code_type", "code"}) })
public class PropertyCodesMaster extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "property_code_type",nullable=false)
	private String propertyCodeType;
	
	@Column(name = "code",nullable=false)
	private String code;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "reference_value")
	private String referenceValue;
	
	@Column(name = "is_deleted")
	private char isDeleted = 'N';

	public PropertyCodesMaster() {
	}

	public PropertyCodesMaster(final String propertyCodeType, final String code, final String description,final String referenceValue) {

		this.propertyCodeType = propertyCodeType;
		this.code = code;
		this.description = description;
		this.referenceValue = referenceValue;

	}

	
	public static PropertyCodesMaster fromJson(final JsonCommand command) {
	    final String propertyCodeType = command.stringValueOfParameterNamed("propertyCodeType");
	    final String code = command.stringValueOfParameterNamed("code");
	    final String description = command.stringValueOfParameterNamed("description");
	    final String referenceValue = command.stringValueOfParameterNamed("referenceValue");
	    return new PropertyCodesMaster(propertyCodeType,code,description,referenceValue);
	}

	public String getPropertyCodeType() {
		return propertyCodeType;
	}

	public void setPropertyCodeType(String propertyCodeType) {
		this.propertyCodeType = propertyCodeType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(String referenceValue) {
		this.referenceValue = referenceValue;
	}

	public Map<String, Object> update(JsonCommand command) {
		
        final Map<String, Object> actualChanges = new ConcurrentHashMap<String, Object>(1);
		
		final String codeTypeParamName = "propertyCodeType";
		if (command.isChangeInStringParameterNamed(codeTypeParamName,this.propertyCodeType)) {
			final String newValue = command.stringValueOfParameterNamed(codeTypeParamName);
			actualChanges.put(codeTypeParamName, newValue);
			this.propertyCodeType = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String codeParamName = "code";
		if (command.isChangeInStringParameterNamed(codeParamName,this.code)) {
			final String newValue = command.stringValueOfParameterNamed(codeParamName);
			actualChanges.put(codeParamName, newValue);
			this.code = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String descriptionParamName = "description";
		if (command.isChangeInStringParameterNamed(descriptionParamName,this.description)) {
			final String newValue = command.stringValueOfParameterNamed(descriptionParamName);
			actualChanges.put(descriptionParamName, newValue);
			this.description =StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String referenceValueParamName = "referenceValue";
		if (command.isChangeInStringParameterNamed(referenceValueParamName,this.referenceValue)) {
			final String newValue = command.stringValueOfParameterNamed(referenceValueParamName);
			actualChanges.put(referenceValueParamName, newValue);
			this.referenceValue = StringUtils.defaultIfEmpty(newValue, null);
		}	
		
		return actualChanges;
	}

	public void deleted() {
		if (this.isDeleted == 'N') {
			this.isDeleted = 'Y';
			this.code = this.code+"_"+this.getId();
		}
	}


}
