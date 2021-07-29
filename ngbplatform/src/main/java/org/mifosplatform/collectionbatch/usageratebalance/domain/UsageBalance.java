package org.mifosplatform.collectionbatch.usageratebalance.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_usagerate_balance")
public class UsageBalance extends AbstractPersistable<Long>{
	
	@Column(name = "rateplan_id")
	private Long ratePlanId;
	
	@Column(name = "tier_id")
	private Long tierId;
	
	@Column(name = "rum")
	private Long rum;
	
	@Column(name = "timeperiod_id")
	private Long timeperiodId;
	
	@Column(name = "gl_id")
	private Long glId;
	
	@Column(name = "uom")
	private Long uom;
	
	@Column(name = "unit")
	private Long unit;
	
	@Column(name = "rate", scale = 6, precision = 19, nullable = false)
	private BigDecimal rate;
	
	@Column(name = "currency_id")
	private Long currencyId;

	public UsageBalance(final Long ratePlanId,final  Long tierId,final Long rum,final Long timeperiodId,final Long glId,final Long uom,
			final Long unit,final BigDecimal rate,final Long currencyId) {
		
		this.ratePlanId=ratePlanId;
		this.tierId=tierId;
		this.rum=rum;
		this.timeperiodId=timeperiodId;
		this.glId=glId;
		this.uom=uom;
		this.unit=unit;
		this.rate=rate;
		this.currencyId=currencyId;
	}

	public UsageBalance(final Long tierId,final  Long rum,final Long uom,final  Long timeperiodId,final  Long unit,final BigDecimal rate) {
	
		this.tierId=tierId;
		this.rum=rum;
		this.uom=uom;
		this.timeperiodId=timeperiodId;
		this.unit=unit;
		this.rate=rate;
		
	}

	public UsageBalance(Long ratePlanId, Long glId, Long currencyId, Long tierId, Long rum, Long uom,
			Long timeperiodId, Long unit, BigDecimal rate) {
	
		
		this.ratePlanId=ratePlanId;
		this.glId=glId;
		this.currencyId=currencyId;
		this.tierId=tierId;
		this.rum=rum;
		this.uom=uom;
		this.timeperiodId=timeperiodId;
		this.unit=unit;
		this.rate=rate;
	}

	public Long getRatePlanId() {
		return ratePlanId;
	}

	public void setRatePlanId(Long ratePlanId) {
		this.ratePlanId = ratePlanId;
	}

	public Long getTierId() {
		return tierId;
	}

	public void setTierId(Long tierId) {
		this.tierId = tierId;
	}

	public Long getRum() {
		return rum;
	}

	public void setRum(Long rum) {
		this.rum = rum;
	}

	public Long getTimeperiodId() {
		return timeperiodId;
	}

	public void setTimeperiodId(Long timeperiodId) {
		this.timeperiodId = timeperiodId;
	}

	public Long getGlId() {
		return glId;
	}

	public void setGlId(Long glId) {
		this.glId = glId;
	}

	public Long getUom() {
		return uom;
	}

	public void setUom(Long uom) {
		this.uom = uom;
	}

	public Long getUnit() {
		return unit;
	}

	public void setUnit(Long unit) {
		this.unit = unit;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}
	
	
	

	public static UsageBalance formJson(JsonCommand command) {
		final Long ratePlanId = command.longValueOfParameterNamed("ratePlanId");
	    final Long tierId = command.longValueOfParameterNamed("tierId");
	    final Long rum = command.longValueOfParameterNamed("rum");
	    final Long timeperiodId = command.longValueOfParameterNamed("timeperiodId");
	    final Long glId = command.longValueOfParameterNamed("glId");
	    final Long uom = command.longValueOfParameterNamed("uom");
	    final Long unit = command.longValueOfParameterNamed("unit");
	    final BigDecimal rate=command.bigDecimalValueOfParameterNamed("rate");
	    final Long currencyId = command.longValueOfParameterNamed("currencyId");
	    
	return new UsageBalance(ratePlanId, tierId, rum,timeperiodId, glId, uom,unit,rate,currencyId);
	}

	
	
	
	
}
