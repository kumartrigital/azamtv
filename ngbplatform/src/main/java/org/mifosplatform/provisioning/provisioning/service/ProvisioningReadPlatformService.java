package org.mifosplatform.provisioning.provisioning.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.order.data.BillPlanData;
import org.mifosplatform.provisioning.provisioning.data.ProcessRequestData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningCommandParameterData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningData;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningRequestData;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;

public interface ProvisioningReadPlatformService {

	List<ProvisioningData> getProvisioningData(Long clientServiceId);
	
	List<McodeData> retrieveProvisioningCategory();

	List<McodeData> retrievecommands();

	ProvisioningData retrieveIdData(Long id);

	List<ProvisioningCommandParameterData> retrieveCommandParams(Long id);

	List<ServiceParameterData> getSerivceParameters(Long orderId);

	List<ServiceParameterData> getProvisionedSerivceParameters(Long orderId);

	//Long getHardwareDetails(String oldHardWare, Long clientId, String name);

	List<ProcessRequestData> getProcessRequestData(String orderNo);
	
	List<ProcessRequestData> getProcessRequestClientData(Long clientId);

	ProcessRequestData getProcessRequestIDData(Long id);
	
	//ProcessRequestData getProcessRequestClientData(Long clientId);

	Collection<MCodeData> retrieveVlanDetails(String string);
	
	List<ProvisioningRequestData> retrieveUnProcessedProvisioningRequestData();
	
	List<ProvisioningData> getProcessRequestCommandData(Long provisioningSystemId);
	
	BillPlanData retriveBillPlan(ProvisioningRequest provisioningRequest);

	ProvisioningData retrieveClientAndServiceParam(Long clientServiceId);
	
	Page<ProvisioningData> retriveprovisioningfailure(String limit , String offset);
	
}
