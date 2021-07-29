package org.mifosplatform.finance.chargeorder.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_charge_tax")
public class ChargeTax extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "charge_id", insertable = true, updatable = true, nullable = true, unique = true)
	private Charge charge;

	@ManyToOne
	@JoinColumn(name = "billitem_id", insertable = true, updatable = true, nullable = true, unique = true)
	private BillItem billItem;

	@Column(name = "tax_code")
	private String taxCode;

	@Column(name = "tax_value")
	private Integer taxValue;

	@Column(name = "tax_percentage")
	private BigDecimal taxPercentage;

	@Column(name = "tax_amount")
	private BigDecimal taxAmount;

	@Column(name = "bill_id")
	private Long billId;
	
	@Column(name = "currency_id")
	private Long currencyId;

	public ChargeTax() {

	}

	public ChargeTax(final BillItem billItem, final Charge charge, final String taxCode, final Integer taxValue,
			final BigDecimal taxPercentage, final BigDecimal taxAmount,final Long currencyId) {

		this.charge = charge;
		this.billItem = billItem;
		this.taxCode = taxCode;
		this.taxValue = taxValue;
		this.taxPercentage = taxPercentage;
		this.taxAmount = taxAmount;
		this.currencyId=currencyId;
		
	}

	public Charge getCharge() {
		return charge;
	}

	public BillItem getInvoice() {
		return billItem;
	}

	public Long getBillId() {
		return billId;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public Integer getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(Integer taxValue) {
		this.taxValue = taxValue;
	}

	public BigDecimal getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(BigDecimal taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public void updateBillId(Long billId) {
		this.billId = billId;
	}

	public void update(Charge charge) {
		this.charge = charge;

	}

	public void setBillItem(BillItem billItem) {
		this.billItem = billItem;
	}
	
	

}
