package org.mifosplatform.logistics.onetimesale.command;

import java.math.BigDecimal;
import java.util.Set;

import org.joda.time.LocalDate;

/**
 * @author hugo
 *
 */
public class OneTimeSaleCommand {
	private final Long itemId;
	private final String units;
	private final String chargeCode;
	private final String quantity;
	private final BigDecimal unitPrice;
	private final BigDecimal totalPrice;
	private final Set<String> modifiedParameters;
	private final LocalDate saleDate; 

	public OneTimeSaleCommand(final Set<String> modifiedParameters, final Long itemId,
			final String units, final String chargeCode, final BigDecimal unitPrice,
			final String quantity, final BigDecimal totalPrice, final LocalDate saleDate) {
		this.itemId = itemId;
		this.modifiedParameters = modifiedParameters;
		this.units = units;
		this.unitPrice = unitPrice;
		this.chargeCode = chargeCode;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.saleDate=saleDate;

	}

	public Long getItemId() {
		return itemId;
	}

	public String getUnits() {
		return units;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public String getQuantity() {
		return quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	
	

	public LocalDate getSaleDate() {
		return saleDate;
	}

	public Set<String> getModifiedParameters() {
		return modifiedParameters;
	}

}
