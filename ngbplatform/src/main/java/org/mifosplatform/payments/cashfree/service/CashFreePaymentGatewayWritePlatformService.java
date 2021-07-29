package org.mifosplatform.payments.cashfree.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.payments.cashfree.data.CashFreeData;

public interface CashFreePaymentGatewayWritePlatformService {

	String makePayment(String json);
	
	String createTransactions(String json);

	String createStatus(String json);

	String getLink(String json);

	String settlements(String json);

	String settlement(String json);

	CommandProcessingResult VerifyCredentials();

	String createRefund(String json);

	String FetchAllRefunds(String json);

	String FetchSingleRefunds(String json);

	CashFreeData generatePaymentToken(String json);

	String PaymentEmail(String json);

	String PaymentDetails(String json);

}
