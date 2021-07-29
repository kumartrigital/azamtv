/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.client.data.ClientAccountSummaryCollectionData;
import org.mifosplatform.portfolio.client.data.ClientAccountSummaryData;
import org.mifosplatform.portfolio.client.data.ClientAdditionalData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.group.service.SearchParameters;

public interface ClientReadPlatformService {

    ClientData retrieveTemplate();

    Page<ClientData> retrieveAll(SearchParameters searchParameters);

    ClientData retrieveOne(Long clientId);

    Collection<ClientData> retrieveAllForLookup(String extraCriteria);

    Collection<ClientData> retrieveAllForLookupByOfficeId(Long officeId);

    ClientAccountSummaryCollectionData retrieveClientAccountDetails(Long clientId);

    Collection<ClientAccountSummaryData> retrieveClientLoanAccountsByLoanOfficerId(Long clientId, Long loanOfficerId);

    ClientData retrieveClientByIdentifier(Long identifierTypeId, String identifierKey);

    Collection<ClientData> retrieveClientMembersOfGroup(Long groupId);
    
    Collection<ClientCategoryData> retrieveClientCategories();
    
    Collection<GroupData> retrieveGroupData();

	ClientData retrieveAllClosureReasons(String clientClosureReason);

	ClientCategoryData retrieveClientBillModes(Long clientId);

	List<ClientCategoryData> retrievingParentClients(String query);

	List<ClientCategoryData> retrievedParentAndChildData(Long parentClientId,Long clientId);

	Boolean countChildClients(Long entityId);

	ClientAdditionalData retrieveClientAdditionalData(Long clientId);

	ClientData retrieveClientWalletAmount(Long clientId,String type);

	Page<ClientData> retrieveAllClients(SearchParameters searchParameters);
	
	ClientData retrieveSearchClientId(String columnName,String columnValue);
	
	ClientData retrieveOne(String columnName,String columnValue);

	
	ClientData retrieveClientForcrm(String columnName,String columnValue);

/*	Page<ClientData> retrieveAllClientsForLCO(SearchParameters searchParameters);*/
	
	ClientData userdeviceinformation(Long officeId);


	List<ClientCategoryData> retrievedParentsChild(Long clientId);

	Long retriveMaxClientId();

	List<ClientData> retriveAllPhonesAndEmails();

	ClientData retriveClientDetailsForEvents(Long clientId);

	ClientServiceData retriveServiceId(String Serialnumber);

	ClientServiceData retriveClientServicesPoids(Long clientServiceId);

	List<ClientData> retriveAccountNo(String accountNo);

	ClientBalanceData findClientBalance(Long clientId);

	ClientData currencyInfo(Long clientId);

	ClientData retriveClientDetails(String paramValue, Long clientId);

	List<ClientData> retrieveClientsForLCO(Long officeId);

	List<ClientData> retrieveRenewalClientsForLCO(Long officeId, String fromDate, String toDate);

	ClientBalanceData findClientBalanceByClientId(Long clientId);
	

	


	
}