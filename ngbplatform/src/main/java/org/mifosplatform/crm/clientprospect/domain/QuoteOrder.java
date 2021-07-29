package org.mifosplatform.crm.clientprospect.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.organisation.salescataloge.domain.SalesCataloge;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_quote_order")

public class QuoteOrder extends AbstractPersistable<Long>{
	@ManyToOne
	@JoinColumn(name="quote_id")
	private Quote quote;
	
	@Column(name = "service_code")
	private String serviceCode;
	
	@Column(name = "plan_name")
	private String planName;
	
	@Column(name = "plan_recurirng_charge")
	private BigDecimal planRecurirngCharge;
	
	
	@Column(name = "plan_onetime_charge")
	private BigDecimal planonetimeCharge;
	
	
	@Column(name = "charge_code")
	private String chargeCode;
	
	@Column(name="is_deleted")
	private char isDeleted;


	public QuoteOrder() {}

	public QuoteOrder(String serviceCode,String planName,BigDecimal planRecurirngCharge,BigDecimal planonetimeCharge,String chargeCode ) {
	
		this.serviceCode = serviceCode;
		this.planName = planName;
		this.planRecurirngCharge = planRecurirngCharge;
		this.planonetimeCharge = planonetimeCharge;
		this.chargeCode = chargeCode;
		this.isDeleted = 'N';
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public BigDecimal getPlanRecurirngCharge() {
		return planRecurirngCharge;
	}

	public void setPlanRecurirngCharge(BigDecimal planRecurirngCharge) {
		this.planRecurirngCharge = planRecurirngCharge;
	}

	public BigDecimal getPlanonetimeCharge() {
		return planonetimeCharge;
	}

	public void setPlanonetimeCharge(BigDecimal planonetimeCharge) {
		this.planonetimeCharge = planonetimeCharge;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void update(final Quote quote) {
		this.quote=quote;
		
	}

	public void delete() {
		if(this.isDeleted != 'Y'){
			this.isDeleted = 'Y';
		}
	}
	

}
