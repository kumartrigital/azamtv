package org.mifosplatform.celcom.domain;

public enum PaymentTypeEnum {
	Advance(0,"Advance"),
	Arrear(1,"Arrear"),
	Prepaid(2,"Prepaid");
	
	private final Integer id;
	private final String Name;
    private PaymentTypeEnum(final Integer id,final String Name) {
        this.id = id;
        this.Name = Name;
    }
    public Integer getValue() {
        return this.id;
    }
    
    public String getCode() {
		return Name;
	}
    public static PaymentTypeEnum fromInt(final Integer frequency) {

    	PaymentTypeEnum paymentTypeEnum = PaymentTypeEnum.Advance;
		switch (frequency) {
		case 1:
			paymentTypeEnum = PaymentTypeEnum.Arrear;
			break;
		case 2:
			paymentTypeEnum = PaymentTypeEnum.Prepaid;
			break;
		default:
			paymentTypeEnum = PaymentTypeEnum.Advance;
			break;
		}
		return paymentTypeEnum;
	}
}
