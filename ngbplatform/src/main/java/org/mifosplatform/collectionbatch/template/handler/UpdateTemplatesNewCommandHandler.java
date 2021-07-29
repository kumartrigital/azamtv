package org.mifosplatform.collectionbatch.template.handler;

import org.mifosplatform.collectionbatch.template.service.TemplateWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "TEMPLATES", action = "UPDATE")
public class UpdateTemplatesNewCommandHandler implements NewCommandSourceHandler{

	private final TemplateWritePlatformService templateWritePlatformService;

	@Autowired
	public UpdateTemplatesNewCommandHandler(TemplateWritePlatformService templateWritePlatformService) {
		this.templateWritePlatformService = templateWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.templateWritePlatformService.updateTemplates(command.entityId(), command);
	}


}
