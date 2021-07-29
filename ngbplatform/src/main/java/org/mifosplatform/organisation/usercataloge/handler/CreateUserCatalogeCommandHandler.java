package org.mifosplatform.organisation.usercataloge.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.usercataloge.service.UserCatalogeWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "USERCATALOGE", action = "CREATE")
public class CreateUserCatalogeCommandHandler implements NewCommandSourceHandler{

	private final UserCatalogeWritePlatformService userCatalogeWritePlatformService;
	
	
	@Autowired
	public CreateUserCatalogeCommandHandler(final UserCatalogeWritePlatformService userCatalogeWritePlatformService) {
		this.userCatalogeWritePlatformService = userCatalogeWritePlatformService;
	}



	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.userCatalogeWritePlatformService.create(command);
	}

}
