package org.mifosplatform.crm.ticketmaster.subcategory.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.crm.ticketmaster.subcategory.service.SubcategoryWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Written by H
 * for adding Subcategory items
 *
 */
@Service
@CommandType(entity = "SUBCATEGORY", action = "CREATE")
public class CreateSubcategoryCommandHandler implements NewCommandSourceHandler {

	private final SubcategoryWritePlatformService subcategoryWritePlatformService;

	@Autowired
	public CreateSubcategoryCommandHandler(final SubcategoryWritePlatformService subcategoryWritePlatformService) {
		this.subcategoryWritePlatformService = subcategoryWritePlatformService;
	}
	
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {
		 return this.subcategoryWritePlatformService.createSubcategory(command);
	}

}
