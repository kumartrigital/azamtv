package org.mifosplatform.portfolio.addons.data;

import java.math.BigDecimal;

public class AddonsPriceData {
	
	private final Long id;
	private final String serviceCode;
	private final Long serviceId;
	private final BigDecimal price;
    private final String chargecodeDescription;
	private final Long chargeCodeId;
    
	public AddonsPriceData(Long id, Long serviceId, String serviceCode,
			BigDecimal price, Long chargeCodeId, String chargecodeDescription) {
		
		this.id=id;
		this.serviceCode=serviceCode;
		this.serviceId=serviceId;
		this.price=price;
		this.chargeCodeId = chargeCodeId;
		this.chargecodeDescription = chargecodeDescription;
	}

}
