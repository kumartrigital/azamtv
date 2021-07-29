package org.mifosplatform.provisioning.provsionactions.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;



@Entity
@Table(name="b_provisioning_actions")
public class ProvisionActions extends AbstractPersistable<Long>{
	
	@Column(name="provision_type")
	private String provisiongType;
	
	@Column(name="action")
	private String action;
	
	@Column(name="provisioning_system")
	private String provisioningSystem;
	
	@Column(name="is_enable")
	private char isEnable;
	
	@Column(name="is_delete")
	private char isDelete;
	
	
	public  ProvisionActions() {
		// TODO Auto-generated constructor stub
	}


	public String getProvisiongType() {
		return provisiongType;
	}


	public String getAction() {
		return action;
	}


	public char isEnable() {
		return isEnable;
	}


	public char isDelete() {
		return isDelete;
	}


	public String getProvisioningSystem() {
		return provisioningSystem;
	}


	public char getIsEnable() {
		return isEnable;
	}


	public char getIsDelete() {
		return isDelete;
	}


	public void updateStatus(boolean status) {
		this.isEnable=status?'Y':'N';
		
	}
	
	
	
		
	}


