package org.mifosplatform.organisation.partner.service;

import java.util.Collection;

import org.mifosplatform.organisation.partner.data.PartnersData;

public interface PartnersReadPlatformService {

	Collection<PartnersData> retrieveAllPartners();

	PartnersData retrieveSinglePartnerDetails(Long partnerId);

	PartnersData retrievePartnerImage(Long userId);


}
