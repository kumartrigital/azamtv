package org.mifosplatform.logistics.grn.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.logistics.itemdetails.data.InventoryGrnData;

public interface GrnReadPlatformService {

	public Collection<InventoryGrnData> retriveGrnDetails();
	
	InventoryGrnData retriveGrnDetailTemplate(Long grnId);
	
	public boolean validateForExist(final Long grnId);
	
	public Collection<InventoryGrnData> retriveGrnIds();
	
	public Page<InventoryGrnData> retriveGrnDetails(SearchSqlQuery searchGrn);
	
	public Collection<InventoryGrnData> retriveGrnIdswithItemId(final Long itemMasterId);
	
	public List<InventoryGrnData> retriveGrnDetailsByofficeIdAndOrderStatus(final Long officeId,final Integer orderStatus);

	public List<InventoryGrnData> retriveGrnDetailsByofficeIdAndItemType(Long officeId, Long itemType);

	public InventoryGrnData retriveAllGrnDetailsByPoNo(String purchaseNo);

	InventoryGrnData retriveGrnIdByPoNo(String poNo);

	public List<String> getPoNoListOnKeyStroke(String purchaseNo);
	
	
}
