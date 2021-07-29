package org.mifosplatform.finance.paymentsgateway.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * 
 * @author ashokreddy
 *
 */
@Entity
@Table(name = "b_paymentgateway")
public class PaymentGateway extends AbstractAuditableCustom<AppUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "key_id")
	private String deviceId;

	@Column(name = "party_id")
	private String partyId;

	@Column(name = "payment_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;

	@Column(name = "amount_paid", scale = 6, precision = 19, nullable = false)
	private BigDecimal amountPaid;

	@Column(name = "receipt_no")
	private String receiptNo;

	@Column(name = "source")
	private String source;

	@Column(name = "t_details")
	private String details;

	@Column(name = "payment_id")
	private String paymentId;

	@Column(name = "obs_id")
	private Long obsId;

	@Column(name = "office_id")
	private Long officeId;

	@Column(name = "status")
	private String status;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "t_status")
	private String tStatus;

	@Column(name = "type")
	private String type;

	@Column(name = "is_auto", nullable = false)
	private boolean isAuto = true;

	@Column(name = "reprocess_detail")
	private String reProcessDetail;

	@Column(name = "refference_id")
	private String reffernceId;

	@Column(name = "ussd_details")
	private String UssdDetails;

	public PaymentGateway() {

	}

	public PaymentGateway(final String deviceId, final String partyId, final Date paymentDate,
			final BigDecimal amountPaid, final String receiptNo, final String source, final String details) {

		this.deviceId = deviceId;
		this.partyId = partyId;
		this.paymentDate = paymentDate;
		this.amountPaid = amountPaid;
		this.receiptNo = receiptNo;
		this.source = source;
		this.details = details;
	}

	public PaymentGateway(final String deviceId, final String transactionId, final BigDecimal amountPaid,
			final String phoneNo, final String type, final String tStatus, final String details, final Date date,
			final String source) {
		this.deviceId = deviceId;
		this.partyId = phoneNo;
		this.paymentDate = date;
		this.amountPaid = amountPaid;
		this.receiptNo = transactionId;
		this.source = source;
		this.details = details;
		this.type = type;
		this.tStatus = tStatus;
	}

	public Map<String, Object> fromJson(final JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		final String remarks = "remarks";
		if (command.isChangeInStringParameterNamed(remarks, this.remarks)) {
			final String newValue = command.stringValueOfParameterNamed("remarks");
			actualChanges.put(remarks, newValue);
			this.remarks = StringUtils.defaultIfEmpty(newValue, null);
		}
		final String status = "status";
		if (command.isChangeInStringParameterNamed(status, this.status)) {
			final String newValue = command.stringValueOfParameterNamed("status");
			actualChanges.put(status, newValue);
			this.status = StringUtils.defaultIfEmpty(newValue, null);
		}
		return actualChanges;

	}

	public String getDeviceId() {
		return deviceId;
	}

	public String getPartyId() {
		return partyId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public String getSource() {
		return source;
	}

	public String getDetails() {
		return details;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public Long getObsId() {
		return obsId;
	}

	public String getStatus() {
		return status;
	}

	public void setObsId(Long value) {
		this.obsId = value;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public boolean isAuto() {
		return isAuto;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(Object remarks) {
		this.remarks = (String) remarks;
	}

	public String getReProcessDetail() {
		return reProcessDetail;
	}

	public void setReProcessDetail(String reProcessDetail) {
		this.reProcessDetail = reProcessDetail;
	}

	public void setErrorRemarks(Object printStackTrace) {
		// TODO Auto-generated method stub

	}

	public String gettStatus() {
		return tStatus;
	}

	public void settStatus(String tStatus) {
		this.tStatus = tStatus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReffernceId() {
		return reffernceId;
	}

	public void setReffernceId(String reffernceId) {
		this.reffernceId = reffernceId;
	}

	public String getUssdDetails() {
		return UssdDetails;
	}

	public void setUssdDetails(String ussdDetails) {
		UssdDetails = ussdDetails;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	@Override
	public String toString() {
		return "PaymentGateway [deviceId=" + deviceId + ", partyId=" + partyId + ", paymentDate=" + paymentDate
				+ ", amountPaid=" + amountPaid + ", receiptNo=" + receiptNo + ", source=" + source + ", details="
				+ details + ", paymentId=" + paymentId + ", obsId=" + obsId + ", status=" + status + ", remarks="
				+ remarks + ", tStatus=" + tStatus + ", type=" + type + ", isAuto=" + isAuto + ", reProcessDetail="
				+ reProcessDetail + ", reffernceId=" + reffernceId + "]";
	}

}
