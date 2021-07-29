package org.mifosplatform.celcom.service;

import java.util.Set;

import org.mifosplatform.finance.officebalance.domain.OfficeBalance;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;

public interface CelcomWritePlatformService {

	public CommandProcessingResult createClient(JsonCommand jsonCommand);
	
	public CommandProcessingResult createClientSimpleActivation(JsonCommand command);


	public CommandProcessingResult createOffice(JsonCommand jsonCommand);

	public CommandProcessingResult syncplan(JsonCommand jsonCommand);


	public CommandProcessingResult createBillPlan(ProvisioningRequest provisioningRequest);

	public CommandProcessingResult updatePurchaseProductPoId(Order order, Set<String> set);

	public CommandProcessingResult addPlan(String string);

	public CommandProcessingResult suspendClientService(JsonCommand jsonCommand);

	CommandProcessingResult cancelPlan(String json);

	CommandProcessingResult terminateClientService(JsonCommand command);

	CommandProcessingResult reactivateClientService(JsonCommand command);

	public CommandProcessingResult createPayment(JsonCommand command);

	public CommandProcessingResult createAdjustmentsCelcom(JsonCommand command);

	CommandProcessingResult addPlans(JsonCommand command);

	CommandProcessingResult cancelPlans(JsonCommand command);
	
	CommandProcessingResult renewalplan(JsonCommand command);


	CommandProcessingResult changePlan(String json);

	public CommandProcessingResult swapDevice(JsonCommand jsonCommand);

	CommandProcessingResult createCelcomAgreement(JsonCommand command);


	CommandProcessingResult updateOffice(JsonCommand command);
	CommandProcessingResult deleteAgreement(JsonCommand command);
	
	public CommandProcessingResult createClientHardwarePlanActivation(JsonCommand command);

	public CommandProcessingResult updateCelcomClient(Long entityId, JsonCommand command);

	public CommandProcessingResult createOfficePayment(JsonCommand command);

	CommandProcessingResult createOfficeAdjustmentsCelcom(JsonCommand command);

	CommandProcessingResult cancelPayment(JsonCommand command);

	public CommandProcessingResult createClientDiscount(JsonCommand jsonCommand);
	
	public CommandProcessingResult updateCreditLimit(JsonCommand command, OfficeBalance officeBalance);
	
	// TODO Hemanth CREDITLIMIT

	CommandProcessingResult cancelPaymentforOffice(JsonCommand jsonCommand);

	public CommandProcessingResult createBillAdjustmentsCelcom(JsonCommand command);
	
	
	public CommandProcessingResult BroadcasterConfigCelcom(JsonCommand command);
	
	public CommandProcessingResult ChannelConfigCelcom(JsonCommand command);
	
	public CommandProcessingResult ProductConfigCelcom(Long productId);



	

	
	

	
}
