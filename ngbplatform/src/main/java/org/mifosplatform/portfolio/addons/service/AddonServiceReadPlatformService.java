package org.mifosplatform.portfolio.addons.service;

import java.util.List;

import org.mifosplatform.portfolio.addons.data.AddonsData;
import org.mifosplatform.portfolio.addons.data.AddonsPriceData;

public interface AddonServiceReadPlatformService {

	List<AddonsData> retrieveAllPlatformData();

	AddonsData retrieveSingleAddonData(Long addonId);

	List<AddonsPriceData> retrieveAddonPriceDetails(Long addonId);

	List<AddonsPriceData> retrievePlanAddonDetails(Long planId,String chargeCode);

}
