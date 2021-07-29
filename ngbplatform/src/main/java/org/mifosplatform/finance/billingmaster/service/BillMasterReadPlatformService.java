package org.mifosplatform.finance.billingmaster.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.chargeorder.data.BillDetailsData;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.service.Page;

public interface BillMasterReadPlatformService {

	List<FinancialTransactionsData> retrieveFinancialData(Long clientId);

	Page<FinancialTransactionsData> retrieveInvoiceFinancialData(SearchSqlQuery searchFinancialTransaction, Long clientId);

	List<FinancialTransactionsData> getFinancialTransactionData(Long id);

	Page<BillDetailsData> retrieveStatments(SearchSqlQuery searchCodes, Long clientId);

	BigDecimal retrieveClientBalance(Long clientId);

	List<FinancialTransactionsData> retrieveSingleInvoiceData(Long invoiceId);

	List<BillDetailsData> retrieveStatementDetails(Long billId);

	Page<FinancialTransactionsData> retrieveSampleData(SearchSqlQuery searchFinancialTransaction, Long clientId,String type);

	List<FinancialTransactionsData> retriveDataForDownload(Long clientId,String fromDate, String toDate);
	
	List<BillDetailsData> retrieveStatements(SearchSqlQuery searchCodes,Long clientId);

	Page<FinancialTransactionsData> retrieveStatementsData(SearchSqlQuery searchFinancialTransactionNew, Long clientId,
			String type, String fromDate, String toDate);

	Page<FinancialTransactionsData> retrieveInvoiceFinancialDataByOfficeId(SearchSqlQuery searchTransactionHistory,
			Long officeId);
}
