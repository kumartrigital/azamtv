package org.mifosplatform.collectionbatch.usageratequantitytier.data;

public class UsageRateQuantityTierData {
    
	private Long id;
	private Long planPriceId;
	private Long rumId;
	private Long timeModelId;
	private String rumName;
	private String timemodelName;
	private Long tierId;
	private String tierName;

	public UsageRateQuantityTierData(Long planPriceId, Long rumId, Long timeModelId,String rumName,String timemodelName) {
		this.planPriceId = planPriceId;
		this.rumId = rumId;
		this.timeModelId = timeModelId;
		this.rumName=rumName;
		this.timemodelName=timemodelName;
	}

	public UsageRateQuantityTierData(Long tierId, String tierName) {
	
		this.tierId=tierId;
		this.tierName=tierName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlanPriceId() {
		return planPriceId;
	}

	public void setPlanPriceId(Long planPriceId) {
		this.planPriceId = planPriceId;
	}

	public Long getRumId() {
		return rumId;
	}

	public void setRumId(Long rumId) {
		this.rumId = rumId;
	}

	public Long getTimeModelId() {
		return timeModelId;
	}

	public void setTimeModelId(Long timeModelId) {
		this.timeModelId = timeModelId;
	}

	public String getRumName() {
		return rumName;
	}

	public void setRumName(String rumName) {
		this.rumName = rumName;
	}

	public String getTimemodelName() {
		return timemodelName;
	}

	public void setTimemodelName(String timemodelName) {
		this.timemodelName = timemodelName;
	}

	public Long getTierId() {
		return tierId;
	}

	public void setTierId(Long tierId) {
		this.tierId = tierId;
	}

	public String getTierName() {
		return tierName;
	}

	public void setTierName(String tierName) {
		this.tierName = tierName;
	}
	
	
	
	
	
	
	
	
	
}
