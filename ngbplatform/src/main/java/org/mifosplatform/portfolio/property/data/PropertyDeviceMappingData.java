package org.mifosplatform.portfolio.property.data;

public class PropertyDeviceMappingData {

	final Long id;
	final String serialNumber;
	final String propertycode;
	final boolean serialNumberFlag;

	public PropertyDeviceMappingData(Long id, String serialNumber,
			String propertyCode, boolean serialNumberFlag) {

		this.id = id;
		this.serialNumber = serialNumber;
		this.propertycode = propertyCode;
		this.serialNumberFlag = serialNumberFlag;	
	}

	public boolean isSerialNumberFlag() {
		return serialNumberFlag;
	}

	public Long getId() {
		return id;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getPropertycode() {
		return propertycode;
	}
	
	

}
