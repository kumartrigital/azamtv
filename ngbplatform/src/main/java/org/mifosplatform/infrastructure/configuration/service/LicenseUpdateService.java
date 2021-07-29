package org.mifosplatform.infrastructure.configuration.service;

import javax.servlet.ServletRequest;

import org.mifosplatform.infrastructure.configuration.data.LicenseData;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;

public interface LicenseUpdateService {

	void updateLicenseKey(ServletRequest req, MifosPlatformTenant tenant);
	
	boolean checkIfKeyIsValid(String licenseKey, MifosPlatformTenant tenant);

	LicenseData getLicenseDetails(String licenseKey);

}
