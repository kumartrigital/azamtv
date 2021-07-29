package org.mifosplatform.organisation.priceregion.service;

import java.util.List;

import org.mifosplatform.organisation.priceregion.data.PriceRegionData;

public interface RegionalPriceReadplatformService {

	List<PriceRegionData> getPriceRegionsDetails();

	PriceRegionData getTheClientRegionDetails(Long clientId);


}
