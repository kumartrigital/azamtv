package org.mifosplatform.billing.planprice.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;

public class PriceData {

	final private Long id;
	final private String chargeCode;
	//final private String serviceCode;
	final private Long productId;
	final private String chargeVariant;
	final private BigDecimal price;
	final private String chagreType;
	final private String chargeDuration;
	final private String durationType;
	final private Long serviceId;
	final private Long discountId;
	final private boolean taxInclusive;
	final private Long clientStateId;
	final private Long regionStateId;
	final private Long priceRegionCountry;
	final private Long clientCountry;
	private Long poid;
	private Long planId;
	private String currencyId;
	private String chargeOwner;
	
	
	public PriceData(final Long id,/*final String serviceCode*/ final Long productId,final String chargeCode,final String chargVariant,final BigDecimal price,
			final String chrgeType,final String chargeDuration,final String durationType,final Long serviceId, Long discountId, 
			boolean taxinclusive,Long stateId, Long countryId, Long regionState, Long regionCountryId,String currencyId,String chargeOwner)
	
	{

		this.id=id;
		this.chargeCode=chargeCode;
		//this.serviceCode=serviceCode;
		this.productId=productId;
		this.chargeVariant=chargVariant;
		this.price=price;
		this.chagreType=chrgeType;
		this.chargeDuration=chargeDuration;
		this.durationType=durationType;
		this.serviceId=serviceId;
		this.discountId=discountId;
		this.taxInclusive=taxinclusive;
		this.clientStateId=stateId;
	    this.clientCountry=countryId;
	    this.regionStateId=regionState;
	    this.priceRegionCountry=regionCountryId;
	    this.currencyId=currencyId;
	    this.chargeOwner=chargeOwner;
	    
	}
	public PriceData(Long planId, String chargeCode, Long poid,
			String chargingVariant, BigDecimal price, Long discountId,
			Long priceRegionId, String contractPeriod, String currencyId,String chargeOwner) {
		// TODO Auto-generated constructor stub
		this.taxInclusive=(Boolean) null;
		this.serviceId=null;
		this.regionStateId=null;
		this.productId=null;
		this.priceRegionCountry=priceRegionId;
		this.price= price;
		this.id=null;
		this.planId=planId;
		this.durationType=null;
		this.discountId=discountId;
		this.clientStateId=null;
		this.clientCountry=null;
		this.chargeVariant=chargingVariant;
		this.chargeDuration=contractPeriod;
		this.chargeCode=chargeCode;
		this.chagreType=null;
		this.poid=poid;
		this.currencyId=currencyId;
		this.chargeOwner=chargeOwner;
	}
	public String getChargeOwner() {
		return chargeOwner;
	}
	public void setChargeOwner(String chargeOwner) {
		this.chargeOwner = chargeOwner;
	}
	public String getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	public Long getId() {
		return id;
	}
	public String getChargeCode() {
		return chargeCode;
	}
	/*public String getServiceCode() {
		return serviceCode;
	}*/
	public Long getProductId() {
		return productId;
	}
	public String getChargingVariant() {
		return chargeVariant;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public String getChagreType() {
		return chagreType;
	}
	public String getChargeDuration() {
		return chargeDuration;
	}
	public String getDurationType() {
		return durationType;
	}
	public Long getServiceId() {
		return serviceId;
	}
	
	public Long getClientStateId() {
		return clientStateId;
	}
	public Long getRegionStateId() {
		return regionStateId;
	}
	
	public Long getPriceRegionCountry() {
		return priceRegionCountry;
	}
	public Long getClientCountry() {
		return clientCountry;
	}
	public Long getDiscountId() {
		return discountId;
	}
	/**
	 * @return the taxInclusive
	 */
	public boolean isTaxInclusive() {
		return taxInclusive;
	}
	
	public Long getPoid() {
		return poid;
	}
	public void setPoid(Long poid) {
		this.poid = poid;
	}
}
