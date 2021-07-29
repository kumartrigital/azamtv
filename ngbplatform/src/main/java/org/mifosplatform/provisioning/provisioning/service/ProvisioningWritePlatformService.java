package org.mifosplatform.provisioning.provisioning.service;

import java.util.List;
import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.provisioning.provisioning.data.ProvisionAdapter;

public interface ProvisioningWritePlatformService {

	CommandProcessingResult createProvisioning(JsonCommand command);

	CommandProcessingResult updateProvisioning(JsonCommand command);

	CommandProcessingResult deleteProvisioningSystem(JsonCommand command);

	CommandProcessingResult createNewProvisioningSystem(JsonCommand command,
			Long entityId);

	CommandProcessingResult updateProvisioningDetails(Long entityId);

	void updateHardwareDetails(Long clientId, String serialNumber,String oldSerialnumber, String provSerilaNum, String oldHardware);

	CommandProcessingResult postOrderDetailsForProvisioning(Order order, String planName,String reqType, Long prepareId, String groupName, String serialNo,
			Long orderId,String provisioningSys,Long addonId);


	CommandProcessingResult updateIpDetails(Long entityId, JsonCommand command);

	CommandProcessingResult confirmProvisioningDetails(Long entityId);

	String runAdapterCommands(String apiRequestBodyAsJson);

	List<ProvisionAdapter> gettingLogInformation(String apiRequestBodyAsJson);

	CommandProcessingResult postDetailsForProvisioning(Long clientId,Long resourceId, String requestType, String provisioningSystem, String hardwareId);

	CommandProcessingResult createProvisioningRequest(List<Order> orders, JsonCommand command,boolean checkValidation);

	Map<String,Object> getProvisioningRequestIdAndSP(ClientService clientService, Long clientServiceId);
	
	CommandProcessingResult createProvisioningRequestForCommandCenter(JsonCommand command);

	CommandProcessingResult updateProvisioningRequestDetailsData(JsonCommand command, Long entityId);

}
