package org.mifosplatform.portfolio.plan.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.partner.data.PartnersData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;

public interface PlanReadPlatformService {
	
	
	//List<PlanData> retrievePlanData(String planType);
	
	Page<PlanData> retrievePlanData(String planType, SearchSqlQuery searchSqlPlan);
	
	List<SubscriptionData> retrieveSubscriptionData(Long orderId, String planType);
	
	List<EnumOptionData> retrieveNewStatus();
	
	PlanData retrievePlanData(Long planId);
	
	List<ServiceData> retrieveSelectedProducts(Long planId);
	
	List<EnumOptionData> retrieveVolumeTypes();

	List<PartnersData> retrievePartnersData(Long planId);

	List<PartnersData> retrieveAvailablePartnersData(Long planId);

	List<PlanData> retrievePlanDataForDropdown();

	List<PlanCodeData> retrievePlanData(Long salesCatalogeId,Long planId, Long clientId,Long clientServiceId);

	List<PlanData> retrievePlansForPlanDataPoIds(Long planId);
	
	PlanData retrievePlanDataPoIds(Long planId);

	PlanData retrievePlanDataPoIdsUsingPlanCode(String planCode);
	
	PlanData retrievePlanDataPoIds1(String planCode);

	PlanData retrievePlanDataPoIdsNew(String planCode);

	List<PlanData> retrivebouque();
	List<PlanData> retriveNonbouque();
	
	PlanData retrivePlan(Long planId);

	PlanData retrivePlanByPlanCode(String planCode);

}
