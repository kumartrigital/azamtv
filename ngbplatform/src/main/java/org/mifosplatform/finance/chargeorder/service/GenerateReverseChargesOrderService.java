package org.mifosplatform.finance.chargeorder.service;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.domain.BillItem;

public interface GenerateReverseChargesOrderService {

	List<ChargeData> generateReverseBillingOrder(List<BillingOrderData> billingOrderProducts,LocalDate disconnectDate);

	List<BillItem> generateNegativeCharge(List<ChargeData> billingOrderCommands);

}
