package org.mifosplatform.billing.selfcare.service;

import java.util.List;

import org.mifosplatform.billing.selfcare.data.SelfCareData;

public interface SelfCareReadPlatformService {
	
	public Long getClientId(String uniqueReference);

	public String getEmail(Long clientId);
	
	public SelfCareData login(String userName, String password);
/*=======
	public Long login(String userName, String password);
	
>>>>>>> obsplatform-1.01*/

	List<SelfCareData> authlogin(String authToken, String username);
}
