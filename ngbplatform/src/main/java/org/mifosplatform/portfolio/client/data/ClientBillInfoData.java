package org.mifosplatform.portfolio.client.data;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.chargecode.data.BillFrequencyCodeData;
import org.mifosplatform.billing.emun.data.EnumValuesData;
import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;

public class ClientBillInfoData {
	
	private Long clientId;
	private Long billDayOfMonth;
	private Long billCurrency;
	private Long billFrequency;
	private String billSegment;
	private LocalDate nextBillDay;
	private LocalDate lastBillDay;
	private Long lastBillNo;
	private Long paymentType;
	private Boolean billSuppressionFlag;
	private Long billSuppressionId;
	private Boolean firstBill;
	private Boolean hotBill;
	private String billFrequencyCode;
	private Collection<EnumValuesData> serviceTypes;
	private Long officeClientId;
	

	private ApplicationCurrencyConfigurationData currencyData;
	private List<BillFrequencyCodeData> billFrequencyCodeData;
	private List<BillSupressionData> billSupressionData;
	
	public ClientBillInfoData(){}
	
	public ClientBillInfoData(final Long clientId, final Long billDayOfMonth, final Long billCurrency, final Long billFrequency,
			final String billSegment, final LocalDate nextBillDay, final LocalDate lastBillDay, final Long lastBillNo, final Long paymentType,
			final Boolean billSuppressionFlag, final Long billSuppressionId, final Boolean firstBill, final Boolean hotBill,final Long officeClientId) {
		
		this.clientId = clientId;
		this.billDayOfMonth = billDayOfMonth;
		this.billCurrency = billCurrency;
		this.billFrequency = billFrequency;
		this.billSegment = billSegment;
		this.nextBillDay = nextBillDay;
		this.lastBillDay = lastBillDay;
		this.lastBillNo = lastBillNo;
		this.paymentType = paymentType;
		this.billSuppressionFlag = billSuppressionFlag;
		this.billSuppressionId = billSuppressionId;
		this.firstBill = firstBill;
		this.hotBill = hotBill;
		this.officeClientId=officeClientId;
	
	}

	public Long getOfficeClientId() {
		return officeClientId;
	}

	public void setOfficeClientId(Long officeClientId) {
		this.officeClientId = officeClientId;
	}

	public ClientBillInfoData(Long billFrequency, String billFrequencyCode) {
		this.billFrequency = billFrequency;
		this.billFrequencyCode = billFrequencyCode;
	}

	public Long getClientId() {
		return clientId;
	}

	public Long getBillDayOfMonth() {
		return billDayOfMonth;
	}

	public Long getBillCurrency() {
		return billCurrency;
	}

	public Long getBillFrequency() {
		return billFrequency;
	}

	public String getBillSegment() {
		return billSegment;
	}

	public LocalDate getNextBillDay() {
		return nextBillDay;
	}

	public LocalDate getLastBillDay() {
		return lastBillDay;
	}

	public Long getLastBillNo() {
		return lastBillNo;
	}

	public Long getPaymentType() {
		return paymentType;
	}

	public Boolean getBillSuppressionFlag() {
		return billSuppressionFlag;
	}

	public Long getBillSuppressionId() {
		return billSuppressionId;
	}

	public Boolean getFirstBill() {
		return firstBill;
	}

	public Boolean getHotBill() {
		return hotBill;
	}

	public void setCurrencyData(ApplicationCurrencyConfigurationData currencyData) {
		this.currencyData = currencyData;
	}

	public void setBillFrequencyCodeData(List<BillFrequencyCodeData> billFrequencyCodeData) {
		this.billFrequencyCodeData = billFrequencyCodeData;
	}

	public void setBillSupressionData(List<BillSupressionData> billSupressionData) {
		this.billSupressionData = billSupressionData;
		
	}
	
	public String getBillFrequencyCode() {
		return billFrequencyCode;
	}

	public void setBillFrequencyCode(String  billFrequencyCode) {
		this.billFrequencyCode = billFrequencyCode;
	}

	public void setServiceTypes(Collection<EnumValuesData> serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	

}
