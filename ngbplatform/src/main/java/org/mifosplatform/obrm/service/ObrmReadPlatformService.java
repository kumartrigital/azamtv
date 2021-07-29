package org.mifosplatform.obrm.service;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.domain.Plan;

public interface ObrmReadPlatformService {

	ClientData retriveClientTotalData(String key,String value);

	String syncPlanToNGB(String key, String value);

}
