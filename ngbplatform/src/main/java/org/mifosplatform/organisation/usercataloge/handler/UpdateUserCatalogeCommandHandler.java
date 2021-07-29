package org.mifosplatform.organisation.usercataloge.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.usercataloge.service.UserCatalogeWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "USERCATALOGE", action = "UPDATE")
public class UpdateUserCatalogeCommandHandler implements  NewCommandSourceHandler{
	
	private final UserCatalogeWritePlatformService userCatalogeWritePlatformService;

	@Autowired
	public UpdateUserCatalogeCommandHandler(UserCatalogeWritePlatformService userCatalogeWritePlatformService) {
		this.userCatalogeWritePlatformService = userCatalogeWritePlatformService;
	}


	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.userCatalogeWritePlatformService.updateUserCataloge(command,command.entityId());

	}

}
