package org.mifosplatform.portfolio.product.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.product.service.ProductWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "PRODUCT", action = "CREATE")
public class CreateProductCommandHandler implements NewCommandSourceHandler{

	private final ProductWritePlatformService productWritePlatformService;

	@Autowired
	public CreateProductCommandHandler(ProductWritePlatformService productWritePlatformService) {
		
		this.productWritePlatformService = productWritePlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.productWritePlatformService.createNewProduct(command);
	}

}
