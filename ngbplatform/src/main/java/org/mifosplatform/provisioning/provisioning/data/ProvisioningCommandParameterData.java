package org.mifosplatform.provisioning.provisioning.data;

import java.util.Collection;
import java.util.Date;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class ProvisioningCommandParameterData {

	private Long id;
	private String commandParam;
	private String paramType;
	private Long paramLength;
	private String paramDefault;//for text paramtype
	private boolean isparamValue;//for boolean paramtype
	private Date paramValue;//for date paramtype
	private Collection<MCodeData> paramValues;//for combo paramtype
	private String paramNotes;
	
	
	public ProvisioningCommandParameterData(final Long id, String commandParam,
			final String paramType, final Long paramLength, String paramDefault,String paramNotes){
		this.id=id;
		this.commandParam=commandParam;
		this.paramType=paramType;
		this.paramLength=paramLength;
		this.paramDefault=paramDefault;
		this.paramNotes=paramNotes;
		
	}

	public String getCommandParam() {
		return commandParam;
	}

	public String getParamType() {
		return paramType;
	}

	public Long getId() {
		return id;
	}

	public Long getParamLength() {
		return paramLength;
	}
	
	public String getParamDefault() {
		return paramDefault;
	}

	public boolean isIsparamValue() {
		return isparamValue;
	}

	public void setIsparamValue(boolean isparamValue) {
		this.isparamValue = isparamValue;
	}

	public Date getParamValue() {
		return paramValue;
	}

	public void setParamValue(Date paramValue) {
		this.paramValue = paramValue;
	}

	public Collection<MCodeData> getParamValues() {
		return paramValues;
	}

	public void setParamValues(Collection<MCodeData> paramValues) {
		this.paramValues = paramValues;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCommandParam(String commandParam) {
		this.commandParam = commandParam;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	public void setParamLength(Long paramLength) {
		this.paramLength = paramLength;
	}

	public void setParamDefault(String paramDefault) {
		this.paramDefault = paramDefault;
	}
	public void setParamNotes(String paramNotes) {
		this.paramNotes = paramNotes;
	}
	public String getParamNotes() {
		return paramNotes;
	}
	
	
	
}
