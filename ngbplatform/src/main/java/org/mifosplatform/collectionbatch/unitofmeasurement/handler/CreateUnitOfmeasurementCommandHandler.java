package org.mifosplatform.collectionbatch.unitofmeasurement.handler;

import org.mifosplatform.collectionbatch.unitofmeasurement.service.UnitOfmeasurementWritePlatformService;
import org.mifosplatform.commands.annotation.CommandType;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@CommandType(entity = "UNITOFMEASUREMENT", action = "CREATE")
public class CreateUnitOfmeasurementCommandHandler implements NewCommandSourceHandler{
		
		
     private final UnitOfmeasurementWritePlatformService unitofmeasurementWritePlatformService;
		 
		 
		 
	@Autowired
	public CreateUnitOfmeasurementCommandHandler(UnitOfmeasurementWritePlatformService unitofmeasurementWritePlatformService) {
		this.unitofmeasurementWritePlatformService = unitofmeasurementWritePlatformService;
	}


	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.unitofmeasurementWritePlatformService.createUnitOfmeasurement(command);
	}

}
