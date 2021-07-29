package org.mifosplatform.logistics.agent.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.useradministration.domain.AppUser;


/**
 * @author hugo
 *
 */
@Entity
@Table(name="m_invoice")
public class ItemSaleInvoice extends AbstractAuditableCustom<AppUser,Long>{
	
	private static final long serialVersionUID = 1L;

	@OneToOne
	@JoinColumn(name="sale_id")
	private ItemSale itemsale;
	
	@Column(name="invoice_date")
	private Date invoiceDate;
	
	@Column(name="charge_amount")
	private BigDecimal chargeAmount;
	
	@Column(name="tax_amount")
	private BigDecimal taxAmount;
	
	@Column(name="tax_percantage")
	private BigDecimal taxPercantage;
	
	@Column(name="invoice_amount")
	private BigDecimal invoiceAmount;
	
	@Column(name="currency_id")
	private Long currencyId;
	
	
	public ItemSaleInvoice(Date date, BigDecimal chargeAmount, BigDecimal taxPercantage,Long currencyId ){
		
		
		this.chargeAmount=chargeAmount;
		this.taxPercantage=taxPercantage;
		this.invoiceDate=date;
		this.currencyId=currencyId;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public ItemSaleInvoice() {
		// TODO Auto-generated constructor stub
	}

	public static ItemSaleInvoice fromJson(final JsonCommand command) {
		
		final BigDecimal chargeAmount=command.bigDecimalValueOfParameterNamed("chargeAmount");
		final BigDecimal taxPercantage=command.bigDecimalValueOfParameterNamed("taxPercantage");
		final Long currencyId=command.longValueOfParameterNamed("currencyId");
		
		return new ItemSaleInvoice(DateUtils.getDateOfTenant(),chargeAmount,taxPercantage,currencyId);
		
	}

	public ItemSale getSale() {
		return itemsale;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	public BigDecimal getTaxPercantage() {
		return taxPercantage;
	}

	public void updateAmounts(BigDecimal taxAmount) {
		
		this.taxAmount=taxAmount;
		this.invoiceAmount=this.chargeAmount.add(taxAmount);
		
	}

	public void update(ItemSale itemSale) {

		this.itemsale=itemSale;
	}

	public void setTaxpercentage(BigDecimal taxrate) {
		
		this.taxPercantage=taxrate;

	}
	
	
	
	

}
