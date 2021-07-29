package org.mifosplatform.celcom.domain;

public enum PlanTypeEnum {
	All(0,"All"),
	Hardware(1,"Hardware"),
	Subscription(2,"Subscription"),
	OneTimeCharges(3,"OneTimeCharges");
	

    private final Integer id;
	private final String Name;

    private PlanTypeEnum(final Integer id, final String Name) {
        this.id = id;
		this.Name = Name;
    }

    public Integer getValue() {
        return this.id;
    }

	public String getCode() {
		return Name;
	}
	public static PlanTypeEnum fromInt(final Integer frequency) {

		PlanTypeEnum planTypeEnum = PlanTypeEnum.All;
		switch (frequency) {
		case 1:
			planTypeEnum = PlanTypeEnum.Hardware;
			break;
		case 2:
			planTypeEnum = PlanTypeEnum.Subscription;
			break;
		case 3:
			planTypeEnum = PlanTypeEnum.OneTimeCharges;
			break;
		default:
			planTypeEnum = PlanTypeEnum.All;
			break;
		}
		return planTypeEnum;
	}
}
