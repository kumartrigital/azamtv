package org.mifosplatform.infrastructure.configuration.data;

import java.util.Date;

public class LicenseData {
	
	private final Date keyDate;
	private final String clientName;

	public LicenseData(Date keyDate, String clientName) {
	
		this.keyDate=keyDate;
		this.clientName=clientName;
	}

	public Date getKeyDate() {
		return keyDate;
	}

	public String getClientName() {
		return clientName;
	}
	
	

}
