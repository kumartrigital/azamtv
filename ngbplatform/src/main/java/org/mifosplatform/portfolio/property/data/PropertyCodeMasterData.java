package org.mifosplatform.portfolio.property.data;


public class PropertyCodeMasterData {

	private Long id;
	private String propertyCodeType;
	private String code;
	private String description;
	private String referenceValue;

	public PropertyCodeMasterData(final Long id,final String propertyCodeType, final String code, 
										final String description, final String referenceValue) {

		this.id = id;
		this.propertyCodeType = propertyCodeType;
		this.code = code;
		this.description = description;
		this.referenceValue = referenceValue;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	

}
