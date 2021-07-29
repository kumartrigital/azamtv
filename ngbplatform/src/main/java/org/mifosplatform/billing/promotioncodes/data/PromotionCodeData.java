package org.mifosplatform.billing.promotioncodes.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.contract.data.PeriodData;

/**
 * @author hugo
 * 
 */
public class PromotionCodeData {

	private Long id;
	private String promotionCode;
	private String promotionDescription;
	private String durationType;
	private Long duration;
	private String discountType;
	private LocalDate startDate;
	private BigDecimal discountRate;
	private Collection<MCodeData> discountTypeData;
	private List<PeriodData> contractTypedata;

	public PromotionCodeData(Long id, String promotionCode,
			String promotionDescription, String durationType, Long duration,
			String discountType, BigDecimal discountRate, LocalDate startDate) {

		this.id = id;
		this.promotionCode = promotionCode;
		this.promotionDescription = promotionDescription;
		this.durationType = durationType;
		this.duration = duration;
		this.discountType = discountType;
		this.discountRate = discountRate;
		this.startDate = startDate;
	}

	public PromotionCodeData() {
		// TODO Auto-generated constructor stub
	}

	public PromotionCodeData(final Collection<MCodeData> discountTypeData,
			final List<PeriodData> contractTypedata) {

		this.discountTypeData = discountTypeData;
		this.contractTypedata = contractTypedata;

	}

	public Long getId() {
		return id;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public String getPromotionDescription() {
		return promotionDescription;
	}

	public String getDurationType() {
		return durationType;
	}

	public Long getDuration() {
		return duration;
	}

	public String getDiscountType() {
		return discountType;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public Collection<MCodeData> getDiscountTypeData() {
		return discountTypeData;
	}

	public void setDiscounTypeData(final Collection<MCodeData> discountTypeData) {
		this.discountTypeData = discountTypeData;
	}

	public List<PeriodData> getContractTypedata() {
		return contractTypedata;
	}

	public void setContractTypedata(final List<PeriodData> contractTypedata) {
		this.contractTypedata = contractTypedata;
	}

}
