package org.mifosplatform.obrm.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.plan.data.PlanData;

public interface ObrmWritePlatformService {

	public CommandProcessingResult createClient(JsonCommand jsonCommand);
	
	public CommandProcessingResult createClientSimpleActivation(JsonCommand command);


	public CommandProcessingResult createOffice(JsonCommand jsonCommand);

	public CommandProcessingResult syncplan(PlanData planData);

}
