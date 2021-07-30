package org.mifosplatform.finance.clientbalance.data;

import java.math.BigDecimal;

public class ConverationDetails {

	private Long baseCurrency;

	private Long conversionCurrency;

	private BigDecimal price;

	private BigDecimal converatedAmount;

	private BigDecimal converationRate;

	public Long getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(Long baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public Long getConversionCurrency() {
		return conversionCurrency;
	}

	public void setConversionCurrency(Long conversionCurrency) {
		this.conversionCurrency = conversionCurrency;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getConveratedAmount() {
		return converatedAmount;
	}

	public void setConveratedAmount(BigDecimal converationAmount) {
		this.converatedAmount = converationAmount;
	}

	public BigDecimal getConverationRate() {
		return converationRate;
	}

	public void setConverationRate(BigDecimal converationRate) {
		this.converationRate = converationRate;
	}

}
