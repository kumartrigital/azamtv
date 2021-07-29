package org.mifosplatform.logistics.itemdetails.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsAllocation;

public interface ItemDetailsWritePlatformService {


	CommandProcessingResult addItem(JsonCommand json,Long orderId);
	
	CommandProcessingResult allocateHardware(JsonCommand command);
	
	ItemDetailsAllocation deAllocateHardware(String serialNo,Long clientId, String remarks);
	
	CommandProcessingResult updateItem(Long itemId,JsonCommand json);
	
	CommandProcessingResult deAllocateHardware(JsonCommand command);

	CommandProcessingResult deleteItem(Long itemId, JsonCommand command);

	
	
	CommandProcessingResult swapDevice(JsonCommand command);
}
