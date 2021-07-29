package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface ChargingOrderWritePlatformService {

	
	CommandProcessingResult updateBillingOrder(List<ChargeData> billingOrderCommands);
	
	void updateClientBalance(JsonCommand clientBalanceComman);
	
	void updateClientVoucherBalance(BigDecimal amount,Long clientId, boolean isWalletEnable);

	void UpdateOfficeCommision(BillItem invoice, Long agreementId);

	void updateClientNonCurrencyBalance(JsonCommand clientBalanceCommand);
	
}
