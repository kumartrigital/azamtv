package org.mifosplatform.finance.chargeorder.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.finance.chargeorder.service.ChargingCustomerOrders;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateChargeCommandHandler  implements NewCommandSourceHandler {
	
	private final ChargingCustomerOrders chargingCustomerOrders;
	
	@Autowired
    public CreateChargeCommandHandler(final ChargingCustomerOrders chargingCustomerOrders) {
        this.chargingCustomerOrders = chargingCustomerOrders;
    }
	
    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {

        return this.chargingCustomerOrders.createNewCharges(command);
    }
    

}
