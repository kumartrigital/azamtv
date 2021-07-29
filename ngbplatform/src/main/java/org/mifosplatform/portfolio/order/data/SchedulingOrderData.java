package org.mifosplatform.portfolio.order.data;

import org.joda.time.LocalDate;

public class SchedulingOrderData {

	private final Long id;
	private final LocalDate startDate;
	private final Long planId;
	private final Long contractId;
	private final String billingFreq;
	private String planDesc;
	private String contractPeriod;
	private Boolean isSerialnum;
	private String serialnum;
	private String propertyCode;
	private final String jsonData;

	public SchedulingOrderData(Long id, LocalDate startDate, Long planId,
			Long contractPeriod, String billingfreq, Boolean isSerialnum,
			String serialnum, String jsonData) {

		this.id = id;
		this.contractId = contractPeriod;
		this.planId = planId;
		this.billingFreq = billingfreq;
		this.startDate = startDate;
		this.planDesc = null;
		this.contractPeriod = null;
		this.setIsSerialnum(isSerialnum);
		this.setSerialnum(serialnum);
		this.jsonData = jsonData;

	}

	public String getJsonData() {
		return jsonData;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public Long getPlanId() {
		return planId;
	}

	public Long getContractId() {
		return contractId;
	}

	public String getBillingFreq() {
		return billingFreq;
	}

	public String getPlanDesc() {
		return planDesc;
	}

	public String getContractPeriod() {
		return contractPeriod;
	}

	public void setPlandesc(String planCode) {
		this.planDesc = planCode;
	}

	public void setContract(String subscriptionPeriod) {

		this.contractPeriod = subscriptionPeriod;

	}

	public Boolean getIsSerialnum() {
		return isSerialnum;
	}

	public void setIsSerialnum(Boolean isSerialnum) {
		this.isSerialnum = isSerialnum;
	}

	public String getSerialnum() {
		return serialnum;
	}

	public void setSerialnum(String serialnum) {
		this.serialnum = serialnum;
	}

	public String getPropertyCode() {
		return propertyCode;
	}

	public void setPropertyCode(String propertyCode) {

		this.propertyCode = propertyCode;
	}

}
