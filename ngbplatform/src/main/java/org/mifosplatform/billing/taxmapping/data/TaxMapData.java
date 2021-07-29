package org.mifosplatform.billing.taxmapping.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;

public class TaxMapData {

	private Long id;
	private String chargeCode;
	private String taxCode;
	private LocalDate startDate;
	private String taxType;
	private BigDecimal rate;

	private Collection<ChargeCodeData> chargeCodesForTax;
	private Collection<MCodeData> taxTypeData;
	private String taxRegion;
	private Long taxRegionId;
	private List<PriceRegionData> priceRegionData;
	private List<TaxMapData> taxMapDatas;
	
	public TaxMapData() {
	}

	public TaxMapData(final String chargeCode, final String taxCode,  final LocalDate startDate,
			final String taxType, final BigDecimal rate) {
		this.chargeCode = chargeCode;
		this.taxCode = taxCode;
		this.startDate = startDate;
		this.taxType = taxType;
		this.rate = rate;
	}

	public TaxMapData(final Long id, final String chargeCode,
			final String taxCode, final LocalDate startDate,
			final String taxType, final BigDecimal rate, final String region,
			final Long taxRegionId) {
		this.id = id;
		this.chargeCode = chargeCode;
		this.taxCode = taxCode;
		this.startDate = startDate;
		this.taxType = taxType;
		this.rate = rate;
		this.taxRegion = region;
		this.taxRegionId = taxRegionId;
	}

	public TaxMapData(final Collection<MCodeData> taxTypeData,
			final List<PriceRegionData> priceRegionData,final String chargeCode) {
		this.priceRegionData = priceRegionData;
		this.taxTypeData = taxTypeData;
		this.chargeCode = chargeCode;
	}

	public TaxMapData(String taxCode) {
		this. taxCode = taxCode;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the chargeCode
	 */
	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	/**
	 * @return the taxCode
	 */
	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(final String taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * @return the startDate
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(final LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the type
	 */
	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(final String taxType) {
		this.taxType = taxType;
	}

	/**
	 * @return the rate
	 */
	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(final BigDecimal rate) {
		this.rate = rate;
	}

	/**
	 * @return the chargeCodesForTax
	 */
	public Collection<ChargeCodeData> getChargeCodesForTax() {
		return chargeCodesForTax;
	}

	public void setChargeCodesForTax(final Collection<ChargeCodeData> chargeCodesForTax) {
		this.chargeCodesForTax = chargeCodesForTax;
	}

	/**
	 * @return the taxRegion
	 */
	public String getTaxRegion() {
		return taxRegion;
	}

	public void setTaxRegion(String taxRegion) {
		this.taxRegion = taxRegion;
	}

	/**
	 * @return the taxRegionId
	 */
	public Long getTaxRegionId() {
		return taxRegionId;
	}
	
	public void setTaxRegionId(Long taxRegionId) {
		this.taxRegionId = taxRegionId;
	}

	/**
	 * @return the priceRegionData
	 */
	public List<PriceRegionData> getPriceRegionData() {
		return priceRegionData;
	}

	public void setPriceRegionData(List<PriceRegionData> priceRegionData) {
		this.priceRegionData = priceRegionData;
	}

	/**
	 * @return the taxTypeData
	 */
	public Collection<MCodeData> getTaxTypeData() {
		return taxTypeData;
	}
	
	public void setTaxTypeData(final Collection<MCodeData> taxTypeData) {
		this.taxTypeData = taxTypeData;

	}

	public List<TaxMapData> getTaxMapDatas() {
		return taxMapDatas;
	}

	public void setTaxMapDatas(List<TaxMapData> taxMapDatas) {
		this.taxMapDatas = taxMapDatas;
	}


}
