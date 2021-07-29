package org.mifosplatform.logistics.item.service;

import java.util.List;

import org.mifosplatform.billing.chargecode.data.ChargesData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.logistics.item.data.ItemData;

public interface ItemReadPlatformService {

	List<EnumOptionData> retrieveItemClassType();

	List<EnumOptionData> retrieveUnitTypes();

	List<ChargesData> retrieveChargeCode();

	List<ItemData> retrieveAllItems();

	ItemData retrieveSingleItemDetails(Long clientId, Long itemId, String region, boolean isWithClientId);

	Page<ItemData> retrieveAllItems(SearchSqlQuery searchItems);

	List<ItemData> retrieveAuditDetails(Long itemId);

	List<ItemData> retrieveItemPrice(Long itemId);

	List<ItemData> retrieveAllSupplierItems(Long supplierId);

	List<ItemData> retrieveAllItemsForDropdown();

	ItemData retrieveItemDetailsForItem(String serialNo);

	ItemData retriveSerialNum(Long clientId);

	Long checkAvailablequantityMSO();
	
}
