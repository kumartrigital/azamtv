package org.mifosplatform.organisation.feemaster.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.feemaster.service.FeeMasterWriteplatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateFeeMasterCommandHandler implements NewCommandSourceHandler {

	private final FeeMasterWriteplatformService feeMasterWriteplatformService;

	@Autowired
	public UpdateFeeMasterCommandHandler(final FeeMasterWriteplatformService feeMasterWriteplatformService) {

		this.feeMasterWriteplatformService = feeMasterWriteplatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult processCommand(final JsonCommand command) {

		return this.feeMasterWriteplatformService.updateFeeMaster(command);
	}

}
