package org.mifosplatform.billing.chargecode.service;

import java.util.List;

import org.mifosplatform.billing.chargecode.data.BillFrequencyCodeData;
import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.billing.chargecode.data.ChargeTypeData;
import org.mifosplatform.billing.chargecode.data.DurationTypeData;

/**
 * @author hugo
 * 
 */
public interface ChargeCodeReadPlatformService {

	List<ChargeCodeData> retrieveAllChargeCodes();

	List<ChargeTypeData> getChargeType();

	List<DurationTypeData> getDurationType();

	List<BillFrequencyCodeData> getBillFrequency();

	ChargeCodeData retrieveSingleChargeCodeDetails(Long chargeCodeId);

	ChargeCodeData retrieveChargeCodeForRecurring(Long planId);

	ChargeCodeData retrieveChargeCodeAndContractPeriodUsingPlanDuration(
			Long chargeDuration);
}
