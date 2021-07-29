package org.mifosplatform.finance.billingmaster.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.finance.billingmaster.domain.BillDetail;
import org.mifosplatform.finance.billingmaster.domain.BillMaster;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface BillWritePlatformService {
	
	CommandProcessingResult updateBillMaster(List<BillDetail> billDetails,BillMaster billMaster, BigDecimal previousBal);

	String generateStatementPdf(Long billId);

	String generateInovicePdf(Long billId);
	
	String generatePaymentPdf(Long paymentId);
	
	String generateLeaseAggrement(String mobileNo);


	void sendPdfToEmail(String printFileName,Long clientId,String templateName);


}
