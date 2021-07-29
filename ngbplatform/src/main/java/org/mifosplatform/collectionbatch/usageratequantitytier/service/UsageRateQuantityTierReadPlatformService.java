package org.mifosplatform.collectionbatch.usageratequantitytier.service;

import java.util.List;

import org.mifosplatform.collectionbatch.usageratequantitytier.data.UsageRateQuantityTierData;

public interface UsageRateQuantityTierReadPlatformService {

	List<UsageRateQuantityTierData> retrieveUsageRateQuantityTierForDropdown();
	List<UsageRateQuantityTierData> retrieveTierForDropdown();
}
