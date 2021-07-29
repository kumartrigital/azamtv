package org.mifosplatform.logistics.itemdetails.service;

import java.util.List;

import org.json.JSONException;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.itemdetails.data.AllocationHardwareData;
import org.mifosplatform.logistics.itemdetails.data.ItemDetailsData;
import org.mifosplatform.logistics.itemdetails.data.ItemSerialNumberData;
import org.mifosplatform.logistics.itemdetails.data.ItemMasterIdData;
import org.mifosplatform.logistics.itemdetails.data.QuantityData;

public interface ItemDetailsReadPlatformService {


	public ItemSerialNumberData retriveAllocationData(List<String> itemSerialNumbers,QuantityData quantityData, ItemMasterIdData itemMasterIdData);
	
	public AllocationHardwareData retriveInventoryItemDetail(String serialNumber);

	List<String> retriveSerialNumbers();

	public Page<ItemDetailsData> retriveAllItemDetails(SearchSqlQuery searchItemDetails,String officeName,String itemCode);

	public List<String> retriveSerialNumbersOnKeyStroke(Long oneTimeSaleId,String query, Long officeId, Long pairedItemId);

	public List<ItemDetailsData> retriveSerialNumbersOnKeyStroke(String query);
	
	public List<ItemDetailsData> retriveByItemSaleId(Long itemId);

	
	public ItemDetailsData retriveSerialNumber(String query);

	public ItemDetailsData retriveSingleItemDetail(Long itemId);

	

	public ItemData retriveItemDetailsDataBySerialNum(String query, Long clientId, Long officeId);
	
	public List<String> retriveSerialNumbersOnKeyStrokeForpairedItems(final String serialNo, final String query);

	Integer updateItemDetail(String json) throws JSONException;

	ItemDetailsData retriveGrnId(Long id);
	
}
