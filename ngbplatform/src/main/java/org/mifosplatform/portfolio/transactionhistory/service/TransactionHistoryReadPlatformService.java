package org.mifosplatform.portfolio.transactionhistory.service;

import java.util.Date;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.transactionhistory.data.TransactionHistoryData;

public interface TransactionHistoryReadPlatformService {

	
	public Page<TransactionHistoryData> retriveTransactionHistoryClientId(SearchSqlQuery searchTransactionHistory, Long clientId,String fromDate,String toDate);

	public Page<TransactionHistoryData> retriveTransactionHistoryById(SearchSqlQuery searchTransactionHistory, Long clientId);
	
	public Page<TransactionHistoryData> retriveTransactionHistoryUserId(SearchSqlQuery searchTransactionHistory, Long resourceId,String fromDate,String toDate);
	
	public Page<TransactionHistoryData> retriveTransactionHistoryOfficeId(SearchSqlQuery searchTransactionHistory,String fromDate,String toDate);

	Page<TransactionHistoryData> retriveTransactionHistoryOnClients(
			SearchSqlQuery searchTransactionHistory);
	
}
