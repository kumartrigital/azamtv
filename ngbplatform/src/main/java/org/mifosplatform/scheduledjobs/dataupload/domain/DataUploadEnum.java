package org.mifosplatform.scheduledjobs.dataupload.domain;

public enum DataUploadEnum {

	NEW(1, "CategoryType.direct"), //
	COMPLETED(2, "CategoryType.cash"),
	ERROR(3, "CategoryType.cash"),
	  INVALID(4, "CategoryType.invalid");


    private final Integer value;
	private final String code;

    private DataUploadEnum(final Integer value, final String code) {
        this.value = value;
		this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

	public String getCode() {
		return code;
	}

	public static DataUploadEnum fromInt(final Integer frequency) {

		DataUploadEnum repaymentFrequencyType = DataUploadEnum.INVALID;
		switch (frequency) {
		case 1:
			repaymentFrequencyType = DataUploadEnum.NEW;
			break;
		case 2:
			repaymentFrequencyType = DataUploadEnum.COMPLETED;
			break;

		case 3:
			repaymentFrequencyType = DataUploadEnum.ERROR;
			break;


		default:
			repaymentFrequencyType = DataUploadEnum.INVALID;
			break;
		}
		return repaymentFrequencyType;
	}
}
