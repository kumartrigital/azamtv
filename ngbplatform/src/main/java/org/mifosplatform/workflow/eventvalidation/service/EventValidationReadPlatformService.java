package org.mifosplatform.workflow.eventvalidation.service;

import java.util.List;

import org.mifosplatform.workflow.eventvalidation.data.EventValidationData;

public interface EventValidationReadPlatformService {

	List<EventValidationData> retrieveAllEventValidation();

	void checkForCustomValidations(Long clientId,String eventName, String strjson, Long userId);
	
}
