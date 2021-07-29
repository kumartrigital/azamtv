package org.mifosplatform.celcom.domain;

public enum OfficeTypeEnum {

	MSO(30000000),
	DIST(32000000),
	LCO(31000000);
	
	private int id ;
	OfficeTypeEnum(int id){
		this.id = id;
	}
	
	
	
	public int getId() {
		return id;
	}




	public static int getValue(String name){
		int returnValue =30000000;
		switch(name){
			case "DIST":
				returnValue = OfficeTypeEnum.DIST.getId();
				break;
			case "LCO":
				returnValue = OfficeTypeEnum.LCO.getId();
				break;
		}
		return returnValue;
	}
}
