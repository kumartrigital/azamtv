package org.mifosplatform.Revpay.order.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_revpay_order")
public class RevpayOrder extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "client_id")
	private Long clientId;
	@Column(name = "action")
	private String action;
	@Column(name = "transaction_status")
	private Integer transactionStatus;
	@Column(name = "flwref")
	private String flwref;
	@Column(name = "tx_id")
	private String txId;
	@Column(name = "amount")
	private Double amount;
	@Column(name = "status")
	private String status;
	@Column(name = "purchase_type")
	private String purchaseType;
	@Column(name = "stb_no")
	private String stbNo;
	@Column(name = "ref_id")
	private String refId;
	@Column(name = "voucher_no")
	private String voucherNo;
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Column(name = "updated_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;
	

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(Integer transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getFlwref() {
		return flwref;
	}

	public void setFlwref(String flwref) {
		this.flwref = flwref;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(String purchaseType) {
		this.purchaseType = purchaseType;
	}

	public String getStbNo() {
		return stbNo;
	}

	public void setStbNo(String stbNo) {
		this.stbNo = stbNo;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	@Override
	public String toString() {
		return "RevpayOrder [clientId=" + clientId + ", action=" + action + ", transactionStatus=" + transactionStatus
				+ ", flwref=" + flwref + ", txId=" + txId + ", amount=" + amount + ", status=" + status
				+ ", purchaseType=" + purchaseType + ", stbNo=" + stbNo + ", refId=" + refId + ", voucherNo="
				+ voucherNo + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	
	

}
