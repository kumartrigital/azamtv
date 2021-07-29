package org.mifosplatform.organisation.voucher.data;

public class VoucherPinConfigValueData {
	
	private String pinLength;
	private String beginWith;
	private String pinCategory;
	private String lenghtSerial;
	
	public VoucherPinConfigValueData() {}
	
	public VoucherPinConfigValueData(String pinLength, String beginWith, String pinCategory, String lenghtSerial) {
		super();
		this.pinLength = pinLength;
		this.beginWith = beginWith;
		this.pinCategory = pinCategory;
		this.lenghtSerial = lenghtSerial;
	}

	public String getPinLength() {
		return pinLength;
	}

	public void setPinLength(String pinLength) {
		this.pinLength = pinLength;
	}

	public String getBeginWith() {
		return beginWith;
	}

	public void setBeginWith(String beginWith) {
		this.beginWith = beginWith;
	}

	public String getPinCategory() {
		return pinCategory;
	}

	public void setPinCategory(String pinCategory) {
		this.pinCategory = pinCategory;
	}

	public String getLenghtSerial() {
		return lenghtSerial;
	}

	public void setLenghtSerial(String lenghtSerial) {
		this.lenghtSerial = lenghtSerial;
	}
	
	

}
