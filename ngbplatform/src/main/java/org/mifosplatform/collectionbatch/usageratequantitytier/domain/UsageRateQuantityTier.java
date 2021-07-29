package org.mifosplatform.collectionbatch.usageratequantitytier.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.billing.discountmaster.domain.DiscountDetails;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "r_usagerate_qty_tier")
public class UsageRateQuantityTier extends AbstractPersistable<Long>{
	
	
	@Column(name="tier_name", nullable=false, length=100)
	private String tierName;
	
	@Column(name="usagerateplan_id", nullable=false, length=10)
	private Long usageRateplanId;
	
	@Column(name="start_range", nullable=false, length=100)
	private Long startRange;
	
	@Column(name="end_range", nullable=false, length=100)
	private Long endRange;

	

	
	
	
	public UsageRateQuantityTier(Long usageRateplanId, Long startRange, Long endRange, String tierName) {
		this.usageRateplanId = usageRateplanId;
		this.startRange = startRange;
		this.endRange = endRange;
		this.tierName = tierName;
	}


	public UsageRateQuantityTier(final String tierName,final  Long usageRateplanId, final Long startRange,final Long endRange) {
		this.tierName=tierName;
		this.usageRateplanId=usageRateplanId;
		this.startRange=startRange;
		this.endRange=endRange;
	}


	


	public static UsageRateQuantityTier formJson(JsonCommand command) {
		
		
		Long usageRateplanId = command.longValueOfParameterNamed("usageRateplanId");
		Long startRange = command.longValueOfParameterNamed("startRange");
		Long endRange = command.longValueOfParameterNamed("endRange");
		String tierName = command.stringValueOfParameterNamed("tierName");
		
		
		return new UsageRateQuantityTier( usageRateplanId, startRange,endRange, tierName);
	}



	public Long getUsageRateplanId() {
		return usageRateplanId;
	}


	public void setUsageRateplanId(Long usageRateplanId) {
		this.usageRateplanId = usageRateplanId;
	}


	public Long getStartRange() {
		return startRange;
	}


	public void setStartRange(Long startRange) {
		this.startRange = startRange;
	}


	public Long getEndRange() {
		return endRange;
	}


	public void setEndRange(Long endRange) {
		this.endRange = endRange;
	}


	public String getTierName() {
		return tierName;
	}


	public void setTierName(String tierName) {
		this.tierName = tierName;
	}
	

	
	

}
