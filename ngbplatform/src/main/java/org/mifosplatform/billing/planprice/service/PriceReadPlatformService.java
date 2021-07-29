package org.mifosplatform.billing.planprice.service;

import java.util.List;

import org.mifosplatform.billing.planprice.data.PricingData;
import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.voucher.data.VoucherData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.ServiceData;

public interface PriceReadPlatformService {

	// List<ChargesData> retrieveChargeCode();

	List<EnumOptionData> retrieveChargeVariantData();

	// List<DiscountMasterData> retrieveDiscountDetails();

	List<SubscriptionData> retrievePaytermData();

	List<ServiceData> retrieveServiceDetails(Long planId);

	List<ServiceData> retrieveServiceCodeDetails(Long planCode);

	PricingData retrieveSinglePriceDetails(String priceId);

	List<PricingData> retrievePlanAndPriceDetails(String region);

	List<ServiceData> retrievePriceDetails(Long planId, String region);
	
	List<VoucherData> retrieveVoucherDatas(Long priceId);

	List<PricingData> retrievePlanPricesForDropdown();

	

}
