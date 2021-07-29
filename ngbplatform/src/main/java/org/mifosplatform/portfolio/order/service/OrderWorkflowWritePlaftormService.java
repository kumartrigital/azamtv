package org.mifosplatform.portfolio.order.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface OrderWorkflowWritePlaftormService {

	CommandProcessingResult createOrderWorkflow(JsonCommand command);

	CommandProcessingResult orderWorkflow(JsonCommand command);

}
