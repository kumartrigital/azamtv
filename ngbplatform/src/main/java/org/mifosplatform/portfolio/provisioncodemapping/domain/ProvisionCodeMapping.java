package org.mifosplatform.portfolio.provisioncodemapping.domain;



import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_provision_code_mapping")
public class ProvisionCodeMapping extends AbstractPersistable<Long> {
	
	@Column(name="provision_code", nullable=false)
	private String provisionCode;

	@Column(name="network_code", nullable=false)
	private String networkCode;
	
	@Column(name="provision_value", nullable=false)
	private String provisionValue;
	
	@Column(name="is_deleted")
	private char isDeleted;
	
	public ProvisionCodeMapping(){
		
	}

	public ProvisionCodeMapping(String provisionCode, String networkCode, String provisionValue) {
		
		this.provisionCode = provisionCode;
		this.networkCode = networkCode;
		this.provisionValue = provisionValue;
		this.isDeleted = 'N';
	
	}
	
	public String getProvisionCode(){
		return provisionCode;
	}
	
	public void setProvisionCode(String provisionCode){
		this.provisionCode = provisionCode;
	}
	
	public String getNetworkCode(){
		return networkCode;
	}
	
	public void setNetworkCode(String networkCode){
		this.networkCode = networkCode;
	}
	
	public String getProvisionValue(){
		return provisionValue;
	}
	
	public void setProvisionValue(String provisionValue){
		this.provisionValue = provisionValue;
	}
	
public static ProvisionCodeMapping fromJson(final JsonCommand command) {
		
		final String provisionCode = command.stringValueOfParameterNamed("provisionCode");
		final String networkCode = command.stringValueOfParameterNamed("networkCode");
		final String provisionValue = command.stringValueOfParameterNamed("provisionValue");
		
		return new ProvisionCodeMapping(provisionCode,networkCode,provisionValue);
	}


public Map<String, Object> update(JsonCommand command) {
	
	final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
	
	final String provisionCodeNamedParamName = "provisionCode";
	final String networkCodeNamedParamName = "networkCode";
	final String provisionValueNamedParamName = "provisionValue";
	
	if(command.isChangeInStringParameterNamed(provisionCodeNamedParamName, this.provisionCode)){
		final String newValue = command.stringValueOfParameterNamed(provisionCodeNamedParamName);
		actualChanges.put(provisionCodeNamedParamName, newValue);
		this.provisionCode = StringUtils.defaultIfEmpty(newValue,null);
	}
	
	if(command.isChangeInStringParameterNamed(networkCodeNamedParamName, this.networkCode)){
		final String newValue = command.stringValueOfParameterNamed(networkCodeNamedParamName);
		actualChanges.put(networkCodeNamedParamName, newValue);
		this.networkCode = StringUtils.defaultIfEmpty(newValue, null);
	}
	
	if(command.isChangeInStringParameterNamed(provisionValueNamedParamName, this.provisionValue)){
		final String newValue = command.stringValueOfParameterNamed(provisionValueNamedParamName);
		actualChanges.put(provisionValueNamedParamName, newValue);
		this.provisionValue = StringUtils.defaultIfEmpty(newValue, null);
	}
	
	return actualChanges;

	
	
}

    public char getIsDeleted() {
	 return isDeleted;
	}

    public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}
    
    public void delete() {
      this.isDeleted = 'Y';
    }
	
	
}
