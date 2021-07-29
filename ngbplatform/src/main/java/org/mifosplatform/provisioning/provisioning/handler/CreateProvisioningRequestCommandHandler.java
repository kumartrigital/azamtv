package org.mifosplatform.provisioning.provisioning.handler;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "PROVISIONINGREQUEST", action = "CREATE")
public class CreateProvisioningRequestCommandHandler  implements NewCommandSourceHandler{
	
	private ProvisioningWritePlatformService provisioningWritePlatformService;
	private OrderRepository orderRepository;

	@Autowired
	public CreateProvisioningRequestCommandHandler(final ProvisioningWritePlatformService provisioningWritePlatformService,
			OrderRepository orderRepository) {
		this.provisioningWritePlatformService = provisioningWritePlatformService;
		this.orderRepository = orderRepository;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		List<Order> orders = new ArrayList<Order>();
		orders.add(this.orderRepository.findOne(command.entityId()));
		return this.provisioningWritePlatformService.createProvisioningRequest(orders,command,true);
	}
	

}
