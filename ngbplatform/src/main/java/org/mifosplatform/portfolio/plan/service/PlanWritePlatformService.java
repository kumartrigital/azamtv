package org.mifosplatform.portfolio.plan.service;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

/**
 * @author hugo
 *
 */
public interface PlanWritePlatformService {

	
	CommandProcessingResult createPlan(JsonCommand command);

	CommandProcessingResult updatePlan(Long entityId, JsonCommand command);

	CommandProcessingResult deleteplan(Long entityId);

	CommandProcessingResult updatePlanQualifierData(Long entityId,
			JsonCommand command);

	

}
