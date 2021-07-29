/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.mifosplatform.vendoragreement.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.vendoragreement.service.VendorAgreementWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateVendorAgreementCommandHandler implements NewCommandSourceHandler {

    private final VendorAgreementWritePlatformService writePlatformService;

    @Autowired
    public UpdateVendorAgreementCommandHandler(final VendorAgreementWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        final Long vendorAgreementId = command.entityId();
        
        return this.writePlatformService.updateVendorAgreement(vendorAgreementId, command);
    }
}
