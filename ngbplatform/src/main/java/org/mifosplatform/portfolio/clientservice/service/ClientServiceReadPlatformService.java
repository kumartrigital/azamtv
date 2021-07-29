/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.clientservice.service;

import java.util.List;

import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;

public interface ClientServiceReadPlatformService {

	List<ClientServiceData> retriveClientServices(Long clientId);
	
	ClientServiceData retriveClientService(Long id);

	List<ServiceParameterData> retriveClientServiceDetails(Long serviceId);

	Integer insertclientServiceDetail(Long resourceId);

	Integer insertServiceParametersDetail(Long clientId, Long id);

	List<ClientServiceData> retriveActiveClientsInOrg(Long officeId);

	
	
	

}
