package org.mifosplatform.finance.billingmaster.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_bill_master")
public class BillMaster extends AbstractPersistable<Long>{

	private static final long serialVersionUID = 1L;

	@Column(name = "bill_no")
	private Long billNumber;

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "bill_date")
	private Date billDate;

	@Column(name = "bill_startdate")
	private Date billStartDate;

	@Column(name = "bill_enddate")
	private Date billEndDate;

	@Column(name = "due_date")
	private Date dueDate;

	@Column(name = "previous_balance")
	private BigDecimal previousBalance;

	@Column(name = "charges_amount")
	private BigDecimal chargeAmount;

	@Column(name = "adjustment_amount")
	private BigDecimal adjustmentAmount;

	@Column(name = "tax_amount")
	private BigDecimal taxAmount;

	@Column(name = "paid_amount")
	private BigDecimal paidAmount;

	@Column(name = "due_amount")
	private BigDecimal dueAmount;

	@Column(name = "filename")
	private String fileName;

	@Column(name = "promotion_description")
	private String promotionDescription;
	
	@Column(name = "parent_id")
	private Long parentId;
	
	@Column(name = "is_deleted")
	private char isDeleted;
	
	@Column(name = "deposit_refund_amount")
	private BigDecimal depositRefundAmount;

	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "billMaster", orphanRemoval = true)
	private List<BillDetail> billDetails = new ArrayList<BillDetail>();

	public BillMaster(){

	}

	public BillMaster(final Long billNumber, final Long clientId, final Date billDate,
			final Date billStartDate, final Date billEndDate, final Date dueDate,
			final BigDecimal previousBalance, final BigDecimal chargeAmount,
			final BigDecimal adjustmentAmount, final BigDecimal taxAmount,
			final BigDecimal paidAmount, final BigDecimal dueAmount, final String fileName,
			final String promotionDescription, final Long parentId,final BigDecimal depositRefundAmount) {

		this.billNumber = billNumber;
		this.clientId = clientId;
		this.billDate = billDate;
		this.billStartDate = billStartDate;
		this.billEndDate = billEndDate;
		this.dueDate = dueDate;
		this.previousBalance = previousBalance;
		this.chargeAmount = chargeAmount;
		this.adjustmentAmount = adjustmentAmount;
		this.taxAmount = taxAmount;
		this.paidAmount = paidAmount;
		this.dueAmount = dueAmount;
		this.promotionDescription = promotionDescription;
		this.fileName = "invoice";
		this.parentId = parentId;
		this.isDeleted = 'N';
		this.depositRefundAmount = depositRefundAmount;

	}

	public Long getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(final Long billNumber) {
		this.billNumber = billNumber;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(final Long clientId) {
		this.clientId = clientId;
	}

	public Date getBillDate() {
		return billDate;
	}

	public void setBillDate(final Date billDate) {
		this.billDate = billDate;
	}

	public Date getBillStartDate() {
		return billStartDate;
	}

	public void setBillStartDate(final Date billStartDate) {
		this.billStartDate = billStartDate;
	}

	public Date getBillEndDate() {
		return billEndDate;
	}

	public void setBillEndDate(final Date billEndDate) {
		this.billEndDate = billEndDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(final Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getPreviousBalance() {
		return previousBalance;
	}

	public void setPreviousBalance(final BigDecimal previousBalance) {
		this.previousBalance = previousBalance;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(final BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public BigDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public void setAdjustmentAmount(final BigDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(final BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(final BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(final BigDecimal dueAmount) {
		this.dueAmount = dueAmount;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public String getPromotionDescription() {
		return promotionDescription;
	}

	public void setPromotionDescription(final String promotionDescription) {
		this.promotionDescription = promotionDescription;
	}

	public void addBillDetails(BillDetail billDetail) {
         billDetail.updateBillMaster(this);
         this.billDetails.add(billDetail);
	}

	public Long getparentId() {
		return parentId;
	}

	public List<BillDetail> getBillDetails() {
		return billDetails;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(final char isDeleted) {
		this.isDeleted = isDeleted;
	}
	
   public void delete() {
		
		this.isDeleted = 'Y';
	}

	public void setDepositRefundAmount(final BigDecimal depositRefundAmount) {
		this.depositRefundAmount = depositRefundAmount;

	}
	
}