package org.mifosplatform.billing.taxmapping.service;

import java.util.List;

import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.billing.taxmapping.data.TaxMapData;

/**
 * @author hugo
 * 
 */
public interface TaxMapReadPlatformService {

	List<TaxMapData> retriveTaxMapData(String chargeCode);

	TaxMapData retrievedSingleTaxMapData(Long id);

	List<ChargeCodeData> retrivedChargeCodeTemplateData();
	
	List<TaxMapData>  retriveTaxMapingData();
	
	

}
