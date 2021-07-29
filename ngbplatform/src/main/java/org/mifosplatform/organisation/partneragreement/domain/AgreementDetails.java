package org.mifosplatform.organisation.partneragreement.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "m_office_agreement_detail", uniqueConstraints = @UniqueConstraint(columnNames = {
		"agreement_id", "plan_id" }, name = "agreement_dtl_ai_ps_mc_uniquekey"))
public class AgreementDetails extends AbstractAuditableCustom<AppUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "agreement_id", insertable = true, updatable = true, nullable = true, unique = true)
	private Agreement agreements;

	/*@Column(name = "source")
	private Long sourceType;

	@Column(name = "share_amount")
	private BigDecimal shareAmount;

	@Column(name = "share_type")
	private String shareType;*/
	
	@Column(name = "plan_id")
	private Long planId;

	@Column(name = "contract_period")
	private Long contractPeriod;

	@Column(name = "billing_frequency")
	private String billingFrequency;

	
	@Column(name = "start_date")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "is_deleted")
	private char isDeleted;
	
	@Column(name = "purchase_product_poId")
	private String purchaseProductPoId;
	
	@Column(name = "package_id")
	private String packageId;
	
	public AgreementDetails() {

	}

	public AgreementDetails(final Long planId, final Long contractPeriod, final String billingFrequency,  final LocalDate startDate,final LocalDate endDate
			                /*final String purchaseProductPoId,final String packageId*/) {
		
		/*this.sourceType = source;
		this.shareType = shareType;
		this.shareAmount =shareAmount;*/
		this.planId = planId;
		this.contractPeriod = contractPeriod;
		this.billingFrequency =billingFrequency;
		this.startDate = startDate.toDate();
		this.isDeleted = 'N';
		if(endDate !=null)
		this.endDate = endDate.toDate();
		this.purchaseProductPoId = purchaseProductPoId;
		this.packageId = packageId;
		
	}

	public Agreement getAgreements() {
		return agreements;
	}

	/*public Long getSourceType() {
		return sourceType;
	}

	public BigDecimal getShareAmount() {
		return shareAmount;
	}

	public String getShareType() {
		return shareType;
	}*/
	
	public Long getPlanId() {
		return planId;
	}

	public Long getContractPeriod() {
		return contractPeriod;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}


	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public char getIsDeleted() {
		return isDeleted;
	}
	

	/*public void setSourceType(Long sourceType) {
		this.sourceType = sourceType;
	}

	public void setShareAmount(BigDecimal shareAmount) {
		this.shareAmount = shareAmount;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}*/
	
	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public void setContractPeriod(Long contractPeriod) {
		this.contractPeriod = contractPeriod;
	}

	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public void update (final Agreement agreement){
		this.agreements = agreement;
	}

	public String getPurchaseProductPoId() {
		return purchaseProductPoId;
	}

	public void setPurchaseProductPoId(String purchaseProductPoId) {
		this.purchaseProductPoId = purchaseProductPoId;
	}
	
	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}


}
