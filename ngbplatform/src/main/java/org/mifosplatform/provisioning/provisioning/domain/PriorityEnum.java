package org.mifosplatform.provisioning.provisioning.domain;

public enum PriorityEnum {
	
	ACTIVATION(1,"ACTIVATION"),
	SUSPENTATION(2,"SUSPENTATION"),
	REACTIVATION(3,"REACTIVATION"),
	TERMINATION(4,"TERMINATION"),
	ADD_PLAN(5,"ADD_PLAN"),
	CHANGE_PLAN(6,"CHANGE_PLAN"),
	DISCONNECTION(7,"DISCONNECTION"),
	BMail(8,"BMail"),
	Fingerprint(9,"Fingerprint"),
	OSD(10,"OSD");
	
	
	private int id;
	private String name;
	
	PriorityEnum(int id,String value){
		this.id = id;
		this.name=name;
	}

	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}



	public static int fromName(String name){
		int returnValue =0 ;
		switch(name){
			case "ACTIVATION":
				returnValue = PriorityEnum.ACTIVATION.getId();
				break;
			case "SUSPENTATION":
				returnValue = PriorityEnum.SUSPENTATION.getId();
				break;
			case "REACTIVATION":
				returnValue = PriorityEnum.REACTIVATION.getId();
				break;
			case "TERMINATION":
				returnValue = PriorityEnum.TERMINATION.getId();
				break;
			case "ADD_PLAN":
				returnValue = PriorityEnum.ADD_PLAN.getId();
				break;
			case "CHANGE_PLAN":
				returnValue = PriorityEnum.CHANGE_PLAN.getId();
				break;
			case "DISCONNECTION":
				returnValue = PriorityEnum.DISCONNECTION.getId();
				break;
			case "BMail":
				returnValue = PriorityEnum.BMail.getId();
				break;
			case "Fingerprint":
				returnValue = PriorityEnum.Fingerprint.getId();
				break;
			case "OSD":
				returnValue = PriorityEnum.OSD.getId();
				break;
				
			default:
				returnValue =0;
				break;
		}
		return returnValue;
	}
}
