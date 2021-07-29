package org.mifosplatform.useradministration.handler;

import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.useradministration.service.AppUserWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(entity = "APPUSER", action = "VALIDATEKEY")

public class ValidateKeyCommanadHandler implements NewCommandSourceHandler {

    private final AppUserWritePlatformService writePlatformService;

    @Autowired
    public ValidateKeyCommanadHandler(final AppUserWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        final Long userId = command.entityId();
        
        return this.writePlatformService.validateKey(userId, command);
    }
}