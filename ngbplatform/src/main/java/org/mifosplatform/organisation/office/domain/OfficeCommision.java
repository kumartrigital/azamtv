package org.mifosplatform.organisation.office.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.mifosplatform.organisation.partneragreement.data.AgreementData;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "b_office_commission")
public class OfficeCommision extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Column(name = "charge_id", nullable = false)
	private Long chargeId;

	@Column(name = "office_id",nullable = false)
	private Long officeId;

	@Column(name = "invoice_date",nullable = false)
	private Date invoiceDate;

	@Column(name = "source",nullable = false)
	private Long source;

	@Column(name = "share_amount",nullable = false)
	private BigDecimal shareAmount;

	@Column(name = "share_type",nullable = false)
	private String shareType;

	@Column(name = "comm_source",nullable = false)
	private String commisionSource;

	@Column(name = "amt",nullable = false)
	private BigDecimal commisionAmount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_dt", nullable = false)
	private Date createdDate;
	
	
	public OfficeCommision(){
		
	}

	public OfficeCommision(final AgreementData data) {
		
		this.chargeId = data.getChargeId();
		this.officeId = data.getOfficeId();
		this.invoiceDate = data.getStartDate().toDate();
		this.source = data.getSourceId();
		this.shareType = data.getShareType();
		this.shareAmount = data.getShareAmount();
		this.commisionSource = data.getSource();
		this.commisionAmount = data.getCommisionAmount();
		this.createdDate = new DateTime().toDate();
				
	}

	public static OfficeCommision fromJson(AgreementData data) {
		
		return new OfficeCommision(data);
	}


	public Long getChargeId() {
		return chargeId;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public Long getSource() {
		return source;
	}

	public BigDecimal getShareAmount() {
		return shareAmount;
	}

	public String getShareType() {
		return shareType;
	}

	public String getCommisionSource() {
		return commisionSource;
	}

	public BigDecimal getCommisionAmount() {
		return commisionAmount;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setChargeId(Long chargeId) {
		this.chargeId = chargeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public void setSource(Long source) {
		this.source = source;
	}

	public void setShareAmount(BigDecimal shareAmount) {
		this.shareAmount = shareAmount;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

	public void setCommisionSource(String commisionSource) {
		this.commisionSource = commisionSource;
	}

	public void setCommisionAmount(BigDecimal commisionAmount) {
		this.commisionAmount = commisionAmount;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	

}
