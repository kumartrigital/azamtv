package org.mifosplatform.billing.chargecode.data;

public class BillFrequencyCodeData {

	private Long id;
	private String billFrequencyCode;

	public BillFrequencyCodeData() {

	}

	public BillFrequencyCodeData(final Long id, final String billFrequencyCode) {
		this.id = id;
		this.billFrequencyCode = billFrequencyCode;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getBillFrequencyCode() {
		return this.billFrequencyCode;
	}

	public void setBillFrequencyCode(final String billFrequencyCode) {
		this.billFrequencyCode = billFrequencyCode;
	}

}
