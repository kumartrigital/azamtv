package org.mifosplatform.organisation.voucher.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "b_voucher_export_request")
public class ExportVoucher{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "request_id", nullable = false)
	private String reqId;
	
	@Column(name = "request_date", nullable = false)
	private Date requestDate;

	@Column(name = "status" , nullable = false)
	private String status;

	@Column(name = "sale_ref_no", nullable = false)
	private Long saleRefNo;

	@Column(name = "quantity", nullable = false)
	private Long quantity;
	
	@Column(name = "request_by" , nullable = false)
	private Long requestBy;

	public ExportVoucher() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public ExportVoucher(String reqId, Date requestDate, String status, Long saleRefNo, Long quantity, Long requestBy) {
		super();
		this.reqId = reqId;
		this.requestDate = requestDate;
		this.status = status;
		this.saleRefNo = saleRefNo;
		this.quantity = quantity;
		this.requestBy = requestBy;
	}

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getSaleRefNo() {
		return saleRefNo;
	}

	public void setSaleRefNo(Long saleRefNo) {
		this.saleRefNo = saleRefNo;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Long getRequestBy() {
		return requestBy;
	}

	public void setRequestBy(Long requestBy) {
		this.requestBy = requestBy;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
}
