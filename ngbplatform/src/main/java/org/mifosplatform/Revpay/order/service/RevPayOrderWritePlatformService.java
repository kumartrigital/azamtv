package org.mifosplatform.Revpay.order.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface RevPayOrderWritePlatformService {

	public CommandProcessingResult createOrder(JsonCommand command);

	public String revTransactionStatus(Long txid);
}
