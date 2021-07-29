package org.mifosplatform.celcom.service;

import java.util.List;

import org.mifosplatform.celcom.domain.PlanTypeEnum;
import org.mifosplatform.celcom.domain.SearchTypeEnum;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.chargeorder.data.BillDetailsData;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.plan.data.PlanData;

public interface CelcomReadPlatformService {

	ClientData retriveClientTotalData(String key,String value);

	List<PlanData> retrivePlans(String key, String value,String PlanTypeEnum,String SearchTypeEnum,String searchType);

	List<PlanTypeEnum> retrievePlanTypeEnum();

	List<SearchTypeEnum> retrieveSearchTypeEnum();

	FinancialTransactionsData retriveClientBillData(String key, String value);

	OfficeData retriveOfficeData(OfficeData officeData);
	
	List<BillDetailsData> retriveBillDetails(SearchSqlQuery searchCodes,Long clientId);
	

}
