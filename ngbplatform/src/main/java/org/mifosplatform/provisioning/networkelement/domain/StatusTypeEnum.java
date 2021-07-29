package org.mifosplatform.provisioning.networkelement.domain;



public enum StatusTypeEnum {
	Active(1,"Active"),
	InActive(0,"InActive");

	
	private final Integer id;
	private final String Name;
    private StatusTypeEnum(final Integer id,final String Name) {
        this.id = id;
        this.Name = Name;
    }
    public Integer getValue() {
        return this.id;
    }
    
    public String getCode() {
		return Name;
	}
    public static StatusTypeEnum fromInt(final Integer frequency) {

    	StatusTypeEnum statusTypeEnum = StatusTypeEnum.Active;
		
    	switch (frequency) {
		case 1:
			statusTypeEnum = StatusTypeEnum.Active;
			break;
		case 2:
			statusTypeEnum = StatusTypeEnum.InActive;
			break;
		default:
			statusTypeEnum = StatusTypeEnum.Active;
			break;
		}
		return statusTypeEnum;
	}

}
