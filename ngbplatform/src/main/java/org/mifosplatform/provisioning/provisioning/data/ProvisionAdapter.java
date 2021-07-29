package org.mifosplatform.provisioning.provisioning.data;

/**
 * 
 * @author ashokreddy
 *
 */
public class ProvisionAdapter {

	private String dateName;
	private String dateValue;
	
	public ProvisionAdapter(final String dateName, final String dateValue){
		this.dateName = dateName;
		this.dateValue = dateValue;
	}

	public String getDateName() {
		return dateName;
	}

	public String getDateValue() {
		return dateValue;
	}
	
	
}
