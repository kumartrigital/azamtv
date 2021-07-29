package org.mifosplatform.organisation.redemption.service;

import java.util.List;


public interface RedemptionReadPlatformService {
	
	List<Long> retrieveOrdersData(Long clientId,Long planId);

}
