package org.mifosplatform.finance.paymentsgateway.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;

/**
 * 
 * @author ashokreddy
 *
 */
public class PaymentGatewayData {

	private Long id;
	private Long paymentId;
	private String serialNo;
	private DateTime paymentDate;
	private String receiptNo;
	private BigDecimal amountPaid;
	private String status;
	private String phoneNo;
	private String clientName;
	private List<MediaEnumoptionData> statusData;
	private String remarks;
	private LocalDate paymendDate;
	private Object phoneMSISDN;
	private String reprocessDetail;
	private String source;

	public PaymentGatewayData(final Long id, final String serialNo, final String phoneNo,
			final DateTime paymentDate, final BigDecimal amountPaid, final String receiptNo,
			final String clientName, final String status, final Long paymentId, final String remarks,
			final String reprocessDetail, final String source) {
		this.id=id;
		this.paymentId=paymentId;
		this.serialNo=serialNo;
		this.paymentDate=paymentDate;
		this.receiptNo=receiptNo;
		this.amountPaid=amountPaid;
		this.status=status;
		this.phoneNo=phoneNo;
		this.clientName=clientName;
		this.remarks=remarks;
		this.reprocessDetail = reprocessDetail;
		this.source = source;
	}

	

	public Long getId() {
		return id;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public DateTime getPaymentDate() {
		return paymentDate;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public String getStatus() {
		return status;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public String getClientName() {
		return clientName;
	}

	public List<MediaEnumoptionData> getStatusData() {
		return statusData;
	}

	public void setStatusData(List<MediaEnumoptionData> statusData) {
		this.statusData = statusData;
	}

	public String getRemarks() {
		return remarks;
	}

	public LocalDate getPaymendDate() {
		return paymendDate;
	}

	public Object getPhoneMSISDN() {
		return phoneMSISDN;
	}

	public String getReprocessDetail() {
		return reprocessDetail;
	}

	public void setReprocessDetail(String reprocessDetail) {
		this.reprocessDetail = reprocessDetail;
	}

	public String getSource() {
		return source;
	}
	
	
}
