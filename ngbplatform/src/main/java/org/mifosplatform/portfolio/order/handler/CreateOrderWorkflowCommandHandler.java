package org.mifosplatform.portfolio.order.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.service.OrderWorkflowWritePlaftormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "ORDERWORKFLOW", action = "CREATE")
public class CreateOrderWorkflowCommandHandler implements NewCommandSourceHandler {

	private final OrderWorkflowWritePlaftormService orderWorkflowPlaftormService;
	
	@Autowired
	public CreateOrderWorkflowCommandHandler(final OrderWorkflowWritePlaftormService orderWorkflowPlaftormService){
		this.orderWorkflowPlaftormService = orderWorkflowPlaftormService;
	}
	
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.orderWorkflowPlaftormService.createOrderWorkflow(command);
	}
	
	

}
