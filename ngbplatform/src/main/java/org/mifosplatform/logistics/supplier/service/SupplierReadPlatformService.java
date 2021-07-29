package org.mifosplatform.logistics.supplier.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.logistics.supplier.data.SupplierData;

public interface SupplierReadPlatformService {
	
	public List<SupplierData> retrieveSupplier();

	public Page<SupplierData> retrieveSupplier(SearchSqlQuery searchSupplier);

	public List<SupplierData> retrieveSupplier(Long supplierId);

	public List<SupplierData> retrieveSupplierForDropdown(String supplierDescription);

}
