package org.mifosplatform.provisioning.entitlements.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.provisioning.entitlements.data.ClientEntitlementData;
import org.mifosplatform.provisioning.entitlements.data.EntitlementsData;
import org.mifosplatform.provisioning.entitlements.data.StakerData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningData;


public interface EntitlementReadPlatformService {
	

	List<EntitlementsData> getProcessingData(Long id, String provisioningSystem, String serviceType);

	ClientEntitlementData getClientData(Long clientId);

	StakerData getData(String mac);

	List<EntitlementsData> getBeeniusProcessingData(Long no, String provisioningSystem);

	List<EntitlementsData> getZebraOTTProcessingData(Long no, String provisioningSystem);

	List<EntitlementsData> getCubiWareProcessingData(Long no, String provisioningSystem);
	Page<EntitlementsData> retrivegroupbyfingerprint(String limit , String offset);
	//List<EntitlementsData> getProcessingData(Long id,String provisioningSys,String serviceType, Long clientId);
	
}
