package org.mifosplatform.billing.planprice.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.planprice.data.PriceData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.product.domain.Product;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_plan_pricing")
public class Price extends AbstractPersistable<Long> {

	@Column(name = "plan_id")
	private Long planCode;

	/*
	 * @Column(name = "service_code") private Long serviceCode;
	 */

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "charge_code")
	private String chargeCode;

	@Column(name = "charging_variant")
	private String chargingVariant;

	@Column(name = "price", scale = 6, precision = 19, nullable = false)
	private BigDecimal price;

	@Column(name = "discount_id", nullable = false)
	private Long discountId;

	@Column(name = "price_region_id", nullable = false)
	private Long priceRegion;

	@Column(name = "is_deleted")
	private String isDeleted = "n";

	@Column(name = "duration")
	private String contractPeriod;

	@Column(name = "currencyId")
	private String currencyId;

	@Column(name = "rounding_type")
	private String roundingType;

	@Column(name = "gl_id")
	private Long glId;

	@Column(name = "chargeOwner")
	private String chargeOwner;

	public Price() {
	}

	public Price(final Long planCode, final String chargeCode, final Long productId, final String chargingVariant,
			final BigDecimal price, final Long discountId, Long priceregion, final String contractPeriod,
			final String currencyId, final String roundingType, final Long glId, final String chargeOwner) {

		this.planCode = planCode;
		this.productId = productId;
		this.chargeCode = chargeCode;
		this.chargingVariant = chargingVariant;
		this.price = price;
		this.discountId = discountId;
		this.priceRegion = priceregion;
		this.contractPeriod = contractPeriod;
		this.currencyId = currencyId;
		this.roundingType = roundingType;
		this.glId = glId;
		this.chargeOwner = chargeOwner;
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

	public String getChargeOwner() {
		return chargeOwner;
	}

	public void setChargeOwner(String chargeOwner) {
		this.chargeOwner = chargeOwner;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public void setChargingVariant(String chargingVariant) {
		this.chargingVariant = chargingVariant;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setDiscountId(Long discountId) {
		this.discountId = discountId;
	}

	public void setPriceRegion(Long priceRegion) {
		this.priceRegion = priceRegion;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Price(BigDecimal price, Long planCode, Long productId, String chargeCode, String contractPeriod,
			String chargeOwner) {
		this.price = price;
		this.planCode = planCode;
		this.productId = productId;
		this.chargeCode = chargeCode;
		this.chargingVariant = "2";
		this.price = price;
		this.discountId = (long) 1;
		this.priceRegion = (long) 1;
		this.contractPeriod = contractPeriod;
		this.currencyId = "356";
		this.chargeOwner = chargeOwner;
	}

	public String getChargingVariant() {
		return chargingVariant;
	}

	public void setCharging_variant(String chargingVariant) {
		this.chargingVariant = chargingVariant;
	}

	public Long getPlanCode() {
		return planCode;
	}

	public void setPlanCode(Long planCode) {
		this.planCode = planCode;
	}
	/*
	 * public Long getServiceCode() { return serviceCode; }
	 */

	public Long getProductId() {
		return productId;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Long getDiscountId() {
		return discountId;
	}

	public Long getPriceRegion() {
		return priceRegion;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public String getContractPeriod() {
		return contractPeriod;
	}

	public String currencyId() {
		return currencyId;
	}

	public void delete() {
		this.isDeleted = "y";
	}

	public String getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}

	public void setContractPeriod(String contractPeriod) {
		this.contractPeriod = contractPeriod;
	}

	public static Price fromJson(JsonCommand command, List<ServiceData> serviceData, Long planId) {
		// final Long planCode = command.longValueOfParameterNamed("planCode");
		final Long productId = command.longValueOfParameterNamed("productId");
		final String chargeCode = command.stringValueOfParameterNamed("chargeCode");
		final String chargevariant = command.stringValueOfParameterNamed("chargevariant");
		final Long discountId = command.longValueOfParameterNamed("discountId");
		final Long priceregion = command.longValueOfParameterNamed("priceregion");
		final BigDecimal price = command.bigDecimalValueOfParameterNamed("price");
		final String duration = command.stringValueOfParameterNamed("duration");
		final String currencyId = command.stringValueOfParameterNamed("currencyId");
		final String roundingType = command.stringValueOfParameterNamed("roundingType");
		final Long glId = command.longValueOfParameterNamed("glId");
		final String chargeOwner = command.stringValueOfParameterName("chargeOwner");
		System.out.println("charge owner" +chargeOwner);
		/*
		 * for (ServiceData data : serviceData) { if (data.getChargeCode() != null) { if
		 * ((data.getPlanId() == planId))
		 * if(data.getServiceCode().equalsIgnoreCase(serviceCode)){
		 * if(data.getChargeCode().equalsIgnoreCase(chargeCode)) { throw new
		 * ChargeCOdeExists(data.getChargeDescription()); } } } }
		 */
		return new Price(planId, chargeCode, productId, chargevariant, price, discountId, priceregion, duration,
				currencyId, roundingType, glId, chargeOwner);

	}

	public static Price fromJson(JsonCommand command, Long planId) {
		// final Long planCode = command.longValueOfParameterNamed("planCode");
		final Long productId = command.longValueOfParameterNamed("productId");
		final String chargeCode = command.stringValueOfParameterNamed("chargeCode");
		final String chargevariant = command.stringValueOfParameterNamed("chargevariant");
		final Long discountId = command.longValueOfParameterNamed("discountId");
		final Long priceregion = command.longValueOfParameterNamed("priceregion");
		final BigDecimal price = command.bigDecimalValueOfParameterNamed("price");
		final String duration = command.stringValueOfParameterNamed("duration");
		final String currency = command.stringValueOfParameterNamed("currency");
		final String roundingType = command.stringValueOfParameterNamed("roundingType");
		final Long glId = command.longValueOfParameterNamed("glId");
		final String chargeOwner = command.stringValueOfParameterName("chargeOwner");
		/*
		 * for (ServiceData data : serviceData) { if (data.getChargeCode() != null) { if
		 * ((data.getPlanId() == planId))
		 * if(data.getServiceCode().equalsIgnoreCase(serviceCode)){
		 * if(data.getChargeCode().equalsIgnoreCase(chargeCode)) { throw new
		 * ChargeCOdeExists(data.getChargeDescription()); } } } }
		 */
		return new Price(planId, chargeCode, productId, chargevariant, price, discountId, priceregion, duration,
				currency, roundingType, glId, chargeOwner);

	}

	public Map<String, Object> update(JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);

		/*
		 * final String serviceCodeParamName = "serviceCode"; if
		 * (command.isChangeInStringParameterNamed(serviceCodeParamName,
		 * this.serviceCode)) { final String newValue =
		 * command.stringValueOfParameterNamed(serviceCodeParamName);
		 * actualChanges.put(serviceCodeParamName, newValue); this.serviceCode =
		 * StringUtils.defaultIfEmpty(newValue, null); }
		 */

		final String productIdParamName = "productId";
		if (command.isChangeInLongParameterNamed(productIdParamName, this.productId)) {
			final Long newValue = command.longValueOfParameterNamed(productIdParamName);
			actualChanges.put(productIdParamName, newValue);
			this.productId = newValue;
		}

		final String chargeCodeParamName = "chargeCode";
		if (command.isChangeInStringParameterNamed(chargeCodeParamName, this.chargeCode)) {
			final String newValue = command.stringValueOfParameterNamed(chargeCodeParamName);
			actualChanges.put(chargeCodeParamName, newValue);
			this.chargeCode = StringUtils.defaultIfEmpty(newValue, null);
		}
		final String chargingVariantParamName = "chargingVariant";
		if (command.isChangeInStringParameterNamed(chargingVariantParamName, this.chargingVariant)) {
			final String newValue = command.stringValueOfParameterNamed(chargingVariantParamName);
			actualChanges.put(chargingVariantParamName, newValue);
			this.chargingVariant = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String contractPeriodParamName = "duration";
		if (command.isChangeInStringParameterNamed(contractPeriodParamName, this.contractPeriod)) {
			String newValue = command.stringValueOfParameterNamed(contractPeriodParamName);
			// newValue=this.isPrepaid == 'Y'?newValue:null;
			actualChanges.put(contractPeriodParamName, newValue);
			this.contractPeriod = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String discountIdParamName = "discountId";
		if (command.isChangeInLongParameterNamed(discountIdParamName, this.discountId)) {
			final Long newValue = command.longValueOfParameterNamed(discountIdParamName);
			actualChanges.put(discountIdParamName, newValue);
			this.discountId = newValue;
		}

		final String priceregionParamName = "priceregion";
		if (command.isChangeInLongParameterNamed(priceregionParamName, this.priceRegion)) {
			final Long newValue = command.longValueOfParameterNamed(priceregionParamName);
			actualChanges.put(priceregionParamName, newValue);
			this.priceRegion = newValue;
		}

		final String priceParamName = "price";
		if (command.isChangeInBigDecimalParameterNamed(priceParamName, this.price)) {
			final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(priceParamName);
			actualChanges.put(priceParamName, newValue);
			this.price = newValue;
		}

		if (command.isChangeInStringParameterNamed("currencyId", this.currencyId)) {
			final String newValue = command.stringValueOfParameterNamed("currencyId");
			actualChanges.put("currencyId", newValue);
			this.currencyId = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInStringParameterNamed("roundingType", this.roundingType)) {
			final String newValue = command.stringValueOfParameterNamed("roundingType");
			actualChanges.put("roundingType", newValue);
			this.roundingType = StringUtils.defaultIfEmpty(newValue, null);
		}
		final String chargeOwner = "chargeOwner";
		if (command.isChangeInStringParameterNamed(chargeOwner, this.chargeOwner)) {
			String newValue = command.stringValueOfParameterNamed(chargeOwner);
			// newValue=this.isPrepaid == 'Y'?newValue:null;
			actualChanges.put(chargeOwner, newValue);
			this.chargeOwner = StringUtils.defaultIfEmpty(newValue, null);
		}

		return actualChanges;

	}

	public static Price fromCelcomJson(JSONObject product, Long planId, Long productId) {
		try {
			String chargeCode = "MSC";
			String chargingVariant = "2";
			BigDecimal price = new BigDecimal(product.getInt("brm:SCALED_AMOUNT"));
			Long discountId = (long) 1;
			Long priceRegionId = (long) 1;
			String contractPeriod = "Monthly";
			String currencyId = "356";
			return new Price(planId, chargeCode, productId, chargingVariant, price, discountId, priceRegionId,
					contractPeriod, currencyId, null, null,null);
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage());
		}
	}

	/*
	 * public static List<Price> fromCelcomJson(String result, Long planId) {
	 * List<Price> priceList = new ArrayList<Price>(); try{ JSONObject object = new
	 * JSONObject(result);
	 * object=object.getJSONObject("brm:COB_OP_CUST_SEARCH_PLAN_outputFlist");
	 * JSONObject plan=object.getJSONObject("brm:PLAN"); JSONArray dealsArray;
	 * if(object.optJSONArray("brm:DEALS")!=null){ dealsArray =
	 * plan.optJSONArray("brm:DEALS"); }else{ dealsArray = new
	 * JSONArray("["+plan.getString("brm:DEALS")+"]"); } for(int
	 * i=0;i<dealsArray.length();i++){ JSONObject deals=dealsArray.getJSONObject(i);
	 * JSONArray productsArray; if(object.optJSONArray("brm:PRODUCTS")!=null){
	 * productsArray = deals.optJSONArray("brm:PRODUCTS"); }else{ productsArray =
	 * new JSONArray("["+deals.getString("brm:PRODUCTS")+"]"); } for(int
	 * j=0;j<productsArray.length();j++){ JSONObject
	 * product=productsArray.getJSONObject(j); String
	 * productPoid=product.optString("brm:POID"); String[]
	 * productPoidArray=productPoid.split(""); Long
	 * poid=Long.parseLong(productPoidArray[2]); String chargeCode="MSC"; String
	 * chargingVariant="2"; BigDecimal price=new
	 * BigDecimal(product.getInt("brm:SCALED_AMOUNT")); Long discountId=(long) 1;
	 * Long priceRegionId=(long) 1; String contractPeriod="Monthly"; String
	 * currencyId="356"; Price priceObject=new Price(planId, chargeCode, poid,
	 * chargingVariant, price, discountId, priceRegionId, contractPeriod,
	 * currencyId); priceList.add(priceObject); } } return priceList;
	 * }catch(JSONException e){ throw new
	 * PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(),
	 * e.getMessage()); } }
	 */
	public void update(Price price) {
		// TODO Auto-generated method stub
		this.price = price.getPrice();
		this.planCode = price.getPlanCode();
		this.productId = price.getProductId();
		this.chargeCode = price.getChargeCode();
		this.chargingVariant = price.getChargingVariant();
		this.discountId = price.getDiscountId();
		this.priceRegion = price.getPriceRegion();
		this.contractPeriod = price.getContractPeriod();
		this.currencyId = price.getCurrencyId();
	}

}
