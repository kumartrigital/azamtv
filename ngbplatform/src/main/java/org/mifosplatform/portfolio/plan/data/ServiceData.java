package org.mifosplatform.portfolio.plan.data;

import java.math.BigDecimal;

import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;

public class ServiceData {

	private  Long id;
	private String serviceCode;
	private String planDescription;
	private String planCode;
	private Long discountId;
	private BigDecimal price;
	private String chargeCode;
	private String chargeVariant;
	private Long planId;
	private String serviceDescription;
	private String priceregion;
	private Long contractId;
	private String duration;
	private String billingFrequency;
	private String isPrepaid;
	private String serviceType;
	private String chargeDescription;
	private String image;
	private Long currencyId;
	private String currencyCode;
	private Long  productId;
	private String  productCode;
	private String  productDescription;
	private Long    serviceId;
	private Long productPoId;
	private String paramName;
	private String roundingType;
	private Long  glId;
	private String chargeOwner;

	
	
	public ServiceData(final Long id, final String planCode,final  String productCode,final String planDescription,final  String chargeCode,
			final String chargingVariant,final BigDecimal price,final String priceregion,final Long contractId,final String duration,
			final String billingFrequency, final Long discountId, String isPrepaid, Long currencyId,String currencyCode,final String serviceCode,final Long productId,
			final String roundingType,final Long  glId, final String chargeOwner) {

		this.id = id;
		this.productCode = productCode;
		this.planDescription = planDescription;
		this.planCode = planCode;
		this.chargeCode = chargeCode;
		this.chargeVariant = chargingVariant;
		this.price = price;
		this.priceregion=priceregion;
		this.contractId=contractId;
		this.duration=duration;
		this.billingFrequency=billingFrequency;
		this.planId=null;
		this.discountId = discountId;
		this.serviceDescription=null;
		this.serviceType=null;
		this.isPrepaid=isPrepaid;
		this.chargeDescription=null;
		this.currencyId=currencyId;
		this.currencyCode=currencyCode;
		this.serviceCode = serviceCode;
		this.productId = productId;
		this.roundingType=roundingType;
		this.glId=glId;
		this.chargeOwner=chargeOwner;

	}


	public String getChargeOwner() {
		return chargeOwner;
	}


	public void setChargeOwner(String chargeOwner) {
		this.chargeOwner = chargeOwner;
	}


	public ServiceData(final Long id,final Long planId,final String planCode,final String chargeCode,final  String productCode,
			final String productDescription,final String chargeDescription, final String priceRegion,final String serviceType,final String isPrepaid,
			final Long currencyId,final String currencyCode,String serviceCode,String serviceDescription, final String chargeOwner) {
		
		this.id = id;
		this.planId = planId;
		this.discountId = null;
		this.productCode = productCode;
		this.planDescription = null;
		this.planCode = planCode;
		this.chargeCode = chargeCode;
		this.chargeDescription=chargeDescription;
		this.chargeVariant = null;
		this.price = null;
		this.productDescription = productDescription;
		this.priceregion=priceRegion;
		this.serviceType=serviceType;
		this.isPrepaid=isPrepaid;
		this.contractId=null;
		this.duration=null;
		this.billingFrequency=null;
		this.currencyId=currencyId;
		this.currencyCode=currencyCode;
		this.serviceCode=serviceCode;
		this.serviceDescription=serviceDescription;
		this.chargeOwner=chargeOwner;
		

	}
	
	public ServiceData(final Long id,final  String productCode,final String productDescription,final String image,Long serviceId,String serviceCode) {
		
		this.id = id;
		this.productCode = productCode;
		this.productDescription = productDescription;
		this.image=image;
		this.serviceId = serviceId;
		this.serviceCode = serviceCode;
		this.serviceDescription = null;
		this.planId = null;
		this.discountId = null;
		this.planDescription = null;
		this.planCode = null;
		this.chargeCode = null;
		this.chargeDescription=null;
		this.chargeVariant = null;
		this.price = null;
		this.priceregion=null;
		this.serviceType=null;
		this.isPrepaid=null;
		this.contractId=null;
		this.duration=null;
		this.billingFrequency=null;

	}

	public ServiceData(Long id, Long productId, String productCode,String serviceType, Long productPoId){
		this.id = id;
		this.productId = productId;
		this.productCode = productCode;
		this.serviceType = serviceType;
		this.productPoId = productPoId;
	}
	
	public ServiceData(Long id, String paramName){
		this.id = id;
		this.paramName=paramName;
	}
	
	public Long getId() {
		return id;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public Long getDiscountId() {
		return discountId;
	}

	public String getPlanCode() {
		return planCode;
	}
	
	public String getPriceregion() {
		return priceregion;
	}

	
	public Long getPlanId() {
		return planId;
	}

	public String getPlanDescription() {
		return planDescription;
	}


	public BigDecimal getPrice() {
		return price;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public String getChargeVariant() {
		return chargeVariant;
	}


	public String getParamName() {
		return paramName;
	}


	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public Long getContractId() {
		return contractId;
	}

	public String getDuration() {
		return duration;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public String getIsPrepaid() {
		return isPrepaid;
	}

	public String getChargeDescription() {
		return chargeDescription;
	}

	public String getServiceType() {
		return serviceType;
	}
	
	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	
	public String getProductCode() {
		return productCode;
	}
	
	public String getProductDescription() {
		return productDescription;
	}
	
	public Long getProductId() {
		return productId;
	}

	public Long getServiceId() {
		return serviceId;
	}


	public Long getProductPoId() {
		return productPoId;
	}


	public void setProductPoId(Long productPoId) {
		this.productPoId = productPoId;
	}


	public void setProductId(Long productId) {
		this.productId = productId;
	}


	public String getRoundingType() {
		return roundingType;
	}


	public void setRoundingType(String roundingType) {
		this.roundingType = roundingType;
	}


	public Long getGlId() {
		return glId;
	}


	public void setGlId(Long glId) {
		this.glId = glId;
	}


	
	



}
