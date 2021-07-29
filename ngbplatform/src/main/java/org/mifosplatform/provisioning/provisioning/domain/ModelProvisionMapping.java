package org.mifosplatform.provisioning.provisioning.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.logistics.supplier.domain.Supplier;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_item_attribute")
public class ModelProvisionMapping extends AbstractAuditableCustom<AppUser, Long>{
	
	@Column(name = "provisioning_id")
	private Long provisioningId;
	
	@Column(name = "model")
	private String model;
	
	@Column(name = "make")
	private String make;
	
	@Column(name = "item_type")
	private String itemType;
	
	@Column(name = "is_deleted", nullable = false)
	private char deleted='N';

	
	public ModelProvisionMapping() {
	}

	public ModelProvisionMapping(Long provisioningId, String model,String make,String itemType) {

		this.provisioningId = provisioningId;
		this.model = model;
		this.make=make;
		this.itemType=itemType;
		this.deleted ='N';
	}

	public static ModelProvisionMapping formJson(JsonCommand command) {
		
		final Long provisioningId = command.longValueOfParameterNamed("provisioningId");
		final String model = command.stringValueOfParameterNamed("model");
		final String make = command.stringValueOfParameterNamed("make");
		final String itemType = command.stringValueOfParameterNamed("itemType");
		
		return new ModelProvisionMapping(provisioningId, model,make,itemType);
	}

	public Long getProvisioningId() {
		return provisioningId;
	}

	public void setProvisioningId(Long provisioningId) {
		this.provisioningId = provisioningId;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public char getDeleted() {
		return deleted;
	}

	public void setDeleted(char deleted) {
		this.deleted = deleted;
	}

	public void delete() {
		this.deleted = 'Y';
	}
	
	public Map<String, Object> update(JsonCommand command) {
		
        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String provisioningIdParamName = "provisioningId";
		final String modelparamName = "model";
		
		
		
		if(command.isChangeInLongParameterNamed(provisioningIdParamName, this.provisioningId)){
			final Long newValue = command.longValueOfParameterNamed(provisioningIdParamName);
			actualChanges.put(provisioningIdParamName, newValue);
			this.provisioningId = newValue;
		}

		if(command.isChangeInStringParameterNamed(modelparamName, this.model)){
			final String newValue = command.stringValueOfParameterNamed(modelparamName);
			actualChanges.put(modelparamName, newValue);
			this.model = newValue;
		}
		
		final String makeParamName = "make";
		if(command.isChangeInStringParameterNamed(makeParamName, this.make)){
			final String newValue = command.stringValueOfParameterNamed(makeParamName);
			actualChanges.put(makeParamName, newValue);
			this.make = newValue;
		}
		
		final String itemTypeParamName = "itemType";
		if(command.isChangeInStringParameterNamed(itemTypeParamName, this.itemType)){
			final String newValue = command.stringValueOfParameterNamed(itemTypeParamName);
			actualChanges.put(itemTypeParamName, newValue);
			this.itemType = newValue;
		}
		
		return actualChanges;
	
	
	}

	
}
