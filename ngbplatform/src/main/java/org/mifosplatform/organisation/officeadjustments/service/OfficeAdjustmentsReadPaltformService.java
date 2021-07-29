package org.mifosplatform.organisation.officeadjustments.service;

import java.util.List;

import org.mifosplatform.finance.officebalance.data.OfficeBalanceData;

public interface OfficeAdjustmentsReadPaltformService {

	List<OfficeBalanceData> retrieveOfficeBalance(Long entityId);

}