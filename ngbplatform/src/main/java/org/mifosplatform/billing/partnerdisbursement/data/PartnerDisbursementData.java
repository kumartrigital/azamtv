package org.mifosplatform.billing.partnerdisbursement.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class PartnerDisbursementData {
	
	private Long id;
	private String source;
	private String partnerName;
	private Date transDate;
	private Double chargeAmount;
	private Double commissionAmount;
	private Double netAmount;
	private Double shareAmount;
	private String percentage;
	private Collection<MCodeData> sourceData;
	private List<PartnerDisbursementData> patnerData;
	
	
	public PartnerDisbursementData() {
		// TODO Auto-generated constructor stub
	}

	public PartnerDisbursementData(Long id, String partnerName,
			Date transDate, String source, String percentage,
			 Double commissionAmount, Double chargeAmount,Double netAmount,Double shareAmount) {
		
		this.id=id;
		this.partnerName = partnerName;
		this.transDate = transDate;
		this.source = source;
		this.percentage = percentage;
		this.commissionAmount = commissionAmount;
		this.chargeAmount = chargeAmount;
		this.netAmount = netAmount;
		this.shareAmount = shareAmount;
		
	}

	public PartnerDisbursementData(Long id, String partnerName) {
		
		this.setId(id);
		this.partnerName = partnerName;
	}

	public PartnerDisbursementData(Collection<MCodeData> sourceData,
			List<PartnerDisbursementData> patnerData) {
		this.sourceData = sourceData;
		this.patnerData = patnerData;
	}
	
	

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getPartner() {
		return partnerName;
	}

	public void setPartner(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public Date getTransDate() {
		return transDate;
	}

	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	public Double getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(Double chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public Double getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(Double commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public Double getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(Double netAmount) {
		this.netAmount = netAmount;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public Collection<MCodeData> getSourceData() {
		return sourceData;
	}

	public List<PartnerDisbursementData> getPatnerData() {
		return patnerData;
	}

	public Double getShareAmount() {
		return shareAmount;
	}

	public void setShareAmount(Double shareAmount) {
		this.shareAmount = shareAmount;
	}
	
	
}
