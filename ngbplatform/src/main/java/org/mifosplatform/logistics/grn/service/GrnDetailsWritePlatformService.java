package org.mifosplatform.logistics.grn.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface GrnDetailsWritePlatformService {


	CommandProcessingResult addGrnDetails(JsonCommand command);

	CommandProcessingResult editGrnDetails(JsonCommand command, Long entityId);
	
	 CommandProcessingResult updateGrnOrderStatus(Integer orderStatus,
			Long grnId) ;
}
