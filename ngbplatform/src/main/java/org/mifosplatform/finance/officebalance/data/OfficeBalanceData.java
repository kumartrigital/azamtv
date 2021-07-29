package org.mifosplatform.finance.officebalance.data;

import java.math.BigDecimal;

public class OfficeBalanceData {

	private Long id;
	private Long officeId;
	private BigDecimal balanceAmount;
	private boolean subscriberDues;



	public OfficeBalanceData(Long id, Long officeId, BigDecimal balanceAmount) {

		this.id = id;
		this.officeId = officeId;
		this.balanceAmount = balanceAmount;

	}

	public Long getId() {
		return id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}
	
	public boolean getSubscriberDues() {
		return subscriberDues;
	}

	public void setSubscriberDues(boolean subscriberDues) {
		this.subscriberDues = subscriberDues;
	}

}
