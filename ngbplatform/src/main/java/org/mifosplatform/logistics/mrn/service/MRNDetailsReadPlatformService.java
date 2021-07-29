package org.mifosplatform.logistics.mrn.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.logistics.mrn.data.InventoryTransactionHistoryData;
import org.mifosplatform.logistics.mrn.data.MRNDetailsData;

public interface MRNDetailsReadPlatformService {

	List<MRNDetailsData> retriveMRNDetails();

	Collection<MRNDetailsData> retriveMrnIds();

	List<String> retriveSerialNumbers(Long fromOffice);

	Page<InventoryTransactionHistoryData> retriveHistory(SearchSqlQuery searchItemHistory);
	
	MRNDetailsData retriveSingleMrnDetail(Long mrnId);

	Page<MRNDetailsData> retriveMRNDetails(SearchSqlQuery searchMRNDetails);

	InventoryTransactionHistoryData retriveSingleMovedMrn(Long mrnId);

	List<String> retriveSerialNumbersForItems(Long officeId, String serialNumber);
	
	List<String> retriveCartonNumber(Long mrnId);
	
	List<String> retriveCartoonNumbersForItems(Long officeId, String cartoonNumber);

	String retriveSerialNumbersFromMrn(String serialNumber, Long mrnId);

	Page<MRNDetailsData> retriveMRNDetailsByOfficeId(SearchSqlQuery searchItemDetails, Long officeId);

	

	

}
