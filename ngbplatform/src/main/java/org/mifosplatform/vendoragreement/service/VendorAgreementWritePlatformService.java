/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendoragreement.service;

import java.io.IOException;

import org.json.JSONException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.vendoragreement.data.VendorAgreementData;

public interface VendorAgreementWritePlatformService {

    CommandProcessingResult createVendorAgreement(JsonCommand command);

	CommandProcessingResult updateVendorAgreement(Long vendorAgreementId, JsonCommand command);

	CommandProcessingResult createNewVendorAgreement(JsonCommand command);

	CommandProcessingResult updateNewVendorAgreement(JsonCommand command, Long vendorAgreementId);

}
