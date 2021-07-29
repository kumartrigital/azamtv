package org.mifosplatform.collectionbatch.usagerateplan.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "r_usage_rateplan")
public class RatePlan  extends AbstractPersistable<Long>{

	
	@Column(name="planprice_id", nullable=false, length=100)
	private Long planPriceId;

	@Column(name="timemodel_id", nullable=false, length=100)
	private Long timeModelId;
	
	@Column(name="rum_id", nullable=false, length=100)
	private Long rumId;
	
	@Column(name="rating_type", nullable=false, length=100)
	private String ratingType;


	public Long getPlanPriceId() {
		return planPriceId;
	}

	public void setPlanPriceId(Long planPriceId) {
		this.planPriceId = planPriceId;
	}

	public Long getTimeModelId() {
		return timeModelId;
	}

	public void setTimeModelId(Long timeModelId) {
		this.timeModelId = timeModelId;
	}

	public Long getRumId() {
		return rumId;
	}

	public void setRumId(Long rumId) {
		this.rumId = rumId;
	}
	
	public String getRatingType() {
		return ratingType;
	}

	public void setRatingType(String ratingType) {
		this.ratingType = ratingType;
	}
	
	public RatePlan(final Long planPriceId,final Long timeModelId,final Long rumId,final String ratingType) {
		this.planPriceId=planPriceId;
		this.timeModelId=timeModelId;
		this.rumId=rumId;
		this.ratingType=ratingType;
	}

	public static RatePlan formJson(JsonCommand command) {

		Long planPriceId = command.longValueOfParameterNamed("planPriceId");
		Long timeModelId = command.longValueOfParameterNamed("timeModelId");
		Long rumId = command.longValueOfParameterNamed("rumId");
		String ratingType = command.stringValueOfParameterNamed("ratingType");
		

		return new RatePlan(planPriceId, timeModelId,rumId,ratingType);

	}
	
	
	
	
}
