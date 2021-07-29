package org.mifosplatform.billing.partnerdisbursement.service;

import java.util.List;

import org.mifosplatform.billing.partnerdisbursement.data.PartnerDisbursementData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;

public interface PartnerDisbursementReadPlatformService {

	Page<PartnerDisbursementData> getAllData(SearchSqlQuery searchVoucher, String souceType, String partnerType);

	List<PartnerDisbursementData> getPatnerData();

}

