package org.mifosplatform.collectionbatch.unitofmeasurement.handler;

import org.mifosplatform.collectionbatch.unitofmeasurement.service.UnitOfmeasurementWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@CommandType(entity = "UNITOFMEASUREMENT", action = "UPDATE")
public class UpdateUnitOfmeasurementCommandHandler implements NewCommandSourceHandler {

private UnitOfmeasurementWritePlatformService unitofmeasurementWritePlatformService;
	
	@Autowired
    public UpdateUnitOfmeasurementCommandHandler(final UnitOfmeasurementWritePlatformService unitofmeasurementWritePlatformService) {
        this.unitofmeasurementWritePlatformService = unitofmeasurementWritePlatformService;
    }
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.unitofmeasurementWritePlatformService.updateUnitOfmeasurement(command,command.entityId());
	}

}
