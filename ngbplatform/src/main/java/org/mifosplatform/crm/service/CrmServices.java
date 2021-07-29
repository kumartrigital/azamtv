package org.mifosplatform.crm.service;

import java.util.List;
import java.util.Set;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.chargeorder.data.BillDetailsData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;

public interface CrmServices {
	
	

	public String findOneTargetcCrm();
	
	public ClientData retriveClientTotalData(String key,String value);

	public CommandProcessingResult createClient(JsonCommand command);

	CommandProcessingResult createOffice(JsonCommand command);
	
	CommandProcessingResult createClientSimpleActivation(JsonCommand command);
	
	
	List<PlanData> retrivePlans(String key,String value);

	public CommandProcessingResult createBillPlan(ProvisioningRequest processRequest);

	public CommandProcessingResult updatingPurchaseProductPoIdinOrderLine(Order order, Set<String> substances);

	CommandProcessingResult cancelPlan(String json);

	public CommandProcessingResult addPlan(String apiRequestBodyAsJson);

	public CommandProcessingResult suspendClientService(JsonCommand command);
	
	
	public CommandProcessingResult terminateClientService(JsonCommand command);

	public CommandProcessingResult reactivateClientService(JsonCommand command);

	CommandProcessingResult addPlans(JsonCommand command);

	
	
	CommandProcessingResult cancelPlans(JsonCommand command);


	public CommandProcessingResult changePlan(String json);
	
	
	CommandProcessingResult renewalPlan(JsonCommand command);

	public CommandProcessingResult swapDevice(JsonCommand command);
	
	CommandProcessingResult createPayment(JsonCommand command);

	CommandProcessingResult adjustments(JsonCommand command);

	CommandProcessingResult createAgreement(JsonCommand command);

	public CommandProcessingResult getClientBills(String key, String value);

	public CommandProcessingResult updateOffice(JsonCommand command);
	CommandProcessingResult deleteAgreement(JsonCommand command);

	CommandProcessingResult createClientHardwarePlanActivation(JsonCommand command);

	CommandProcessingResult updateCelcomClient(JsonCommand command);
	
	OfficeData retriveOfficeData(OfficeData officeData);

	CommandProcessingResult createOfficePayment(JsonCommand command);

	CommandProcessingResult createOfficeAdjustmentsCelcom(JsonCommand command);

    CommandProcessingResult cancelPayment(JsonCommand command);
	
	List<BillDetailsData>  billDetails( SearchSqlQuery searchCodes, Long clientId);

	CommandProcessingResult createClientDiscount(JsonCommand command);

	CommandProcessingResult cancelPaymentforOffice(JsonCommand command);

	
	
	CommandProcessingResult billadjustment(JsonCommand command);
	
	CommandProcessingResult broadcasterconfig(JsonCommand command);
	
	CommandProcessingResult channelconfig(JsonCommand command);
	
	
}
