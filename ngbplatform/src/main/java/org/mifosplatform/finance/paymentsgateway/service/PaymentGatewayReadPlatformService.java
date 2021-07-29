package org.mifosplatform.finance.paymentsgateway.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.finance.paymentsgateway.data.PaymentGatewayDownloadData;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.infrastructure.core.service.Page;

/**
 * 
 * @author ashokreddy
 *
 */
public interface PaymentGatewayReadPlatformService {
	
	Long retrieveClientIdForProvisioning(String serialNum);

	Page<PaymentGatewayData> retrievePaymentGatewayData(SearchSqlQuery searchItemDetails, String type, String source);


	List<MediaEnumoptionData> retrieveTemplateData();

	PaymentGatewayData retrievePaymentGatewayIdData(Long id);

	String findReceiptNo(String receiptNo);

	Long getReceiptNoId(String receipt);

	List<PaymentGatewayDownloadData> retriveDataForDownload(String source,
			String startDate, String endDate, String status);

	List<PaymentGatewayData> retrievePendingDetails();
	
	

}
