package org.mifosplatform.billing.chargecode.data;

public class DurationTypeData {

	private Long id;
	private String durationTypeCode;

	public DurationTypeData() {
	}

	public DurationTypeData(final Long id,final String durationTypeCode) {

		this.id = id;
		this.durationTypeCode = durationTypeCode;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setDurationType(final String durationTypeCode) {
		this.durationTypeCode = durationTypeCode;
	}

	public String getDurationTypeCode() {
		return this.durationTypeCode;
	}

}
