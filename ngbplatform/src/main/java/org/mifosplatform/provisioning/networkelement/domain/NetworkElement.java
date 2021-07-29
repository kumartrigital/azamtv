package org.mifosplatform.provisioning.networkelement.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

import org.springframework.data.jpa.domain.AbstractPersistable;



@Entity
@Table(name="b_network_element")
public class NetworkElement extends AbstractPersistable<Long>{
	
	@Column(name="system_code", nullable=false)
	private String systemcode;
	
	@Column(name="system_name", nullable=false)
	private String systemname;
	
	@Column(name="status", nullable=false)
	private String status;
	
		
	@Column(name="is_deleted")
	private char isDeleted;
	
	@Column(name="is_group_supported")
	private char isGroupSupported;
	
	
	
	public NetworkElement() {}
	
	public NetworkElement(String systemcode,String systemname,String status, boolean isGroupSupported) {
		
		this.systemcode = systemcode;
		this.systemname = systemname;
		this.status = status;
		this.isDeleted = 'N';
		if(isGroupSupported){
			this.isGroupSupported='Y';
		}else{
			this.isGroupSupported='N';
		}
	}
	
	

	public String getsystemcode() {
		return systemcode;
	}

	public void setsystemcode(String systemcode) {
		this.systemcode = systemcode;
	}

	public String getsystemname() {
		return systemname;
	}

	public void setsystemname(String systemname) {
		this.systemname = systemname;
	}
   
	public String getstatus() {
		return status;
	}

	public void setstatus(String status) {
		this.status = status;
	}
	
	public char getisGroupSupported() {
		return isGroupSupported;
	}
	
	public void setisGroupSupported(char isGroupSupported) {
		this.isGroupSupported = isGroupSupported;
	}
	
		public static NetworkElement formJson(JsonCommand command) {
		String systemcode = command.stringValueOfParameterNamed("systemcode");
		String systemname = command.stringValueOfParameterNamed("systemname");
		String status = command.stringValueOfParameterNamed("status");
		boolean isGroupSupported = command.booleanPrimitiveValueOfParameterNamed("isGroupSupported");
		
		return new NetworkElement(systemcode, systemname, status, isGroupSupported);
	}

	
	public Map<String, Object> update(JsonCommand command) {
		
final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String systemcodeNamedParamName = "systemcode";
		final String systemnameNamedParamName = "systemname";
		final String statusNamedParamName = "status";
		final String isGroupSupportedNamedParamName = "isGroupSupported";
		
		
		if(command.isChangeInStringParameterNamed(systemcodeNamedParamName, this.systemcode)){
			final String newValue = command.stringValueOfParameterNamed(systemcodeNamedParamName);
			actualChanges.put(systemcodeNamedParamName, newValue);
			this.systemcode = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		if(command.isChangeInStringParameterNamed(systemnameNamedParamName, this.systemname)){
			final String newValue = command.stringValueOfParameterNamed(systemnameNamedParamName);
			actualChanges.put(systemnameNamedParamName, newValue);
			this.systemname = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		
		
		if(command.isChangeInStringParameterNamed(statusNamedParamName, this.status)){
			final String newValue = command.stringValueOfParameterNamed(statusNamedParamName);
			actualChanges.put(statusNamedParamName, newValue);
			this.status = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		
			final Boolean newvalueofisGroupSupported = command.booleanPrimitiveValueOfParameterNamed(isGroupSupportedNamedParamName);
			if(String.valueOf(newvalueofisGroupSupported?'Y':'N').equalsIgnoreCase(String.valueOf(this.isGroupSupported))){
			actualChanges.put(isGroupSupportedNamedParamName, newvalueofisGroupSupported);
			
		}
			this.isGroupSupported = newvalueofisGroupSupported?'Y':'N';
		
				
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
