package org.mifosplatform.provisioning.provsionactions.data;

public class ProvisioningActionData {
	
	private final Long id;
	private final String provisionType;
	private final String action;
	private final String isEnable;
	private final String provisioningSystem;

	public ProvisioningActionData(Long id, String provisiontype, String action,
			String provisioningSystem, String isEnable) {
		
		this.id=id;
		this.provisionType=provisiontype;
		this.provisioningSystem=provisioningSystem;
		this.action=action;
		 this.isEnable=isEnable;
				
	}

	public Long getId() {
		return id;
	}

	public String getProvisionType() {
		return provisionType;
	}

	public String getAction() {
		return action;
	}

	public String getIsEnable() {
		return isEnable;
	}

	public String getProvisioningSystem() {
		return provisioningSystem;
	}
	
	

}
