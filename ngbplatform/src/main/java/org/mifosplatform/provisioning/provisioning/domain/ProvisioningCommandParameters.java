package org.mifosplatform.provisioning.provisioning.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_command_parameters")
public class ProvisioningCommandParameters extends AbstractPersistable<Long> {

	@ManyToOne
	@JoinColumn(name = "command_id")
	private ProvisioningCommand commandprovisioning;

	@Column(name = "command_param")
	private String commandParam;

	@Column(name = "param_type")
	private String paramType;

	@Column(name = "param_default")
	private String paramDefault;
	
	@Column(name = "param_length")
	private Long ParamLength;

	@Column(name = "param_notes")
	private String paramNotes;
	
	public ProvisioningCommandParameters() {

	}

	public ProvisioningCommandParameters(final String commandParam,
			final String paramType, final String paramDefault, final Long ParamLength,final String paramNotes) {

		this.commandParam = commandParam;
		this.paramType = paramType;
		this.paramDefault = paramDefault;
		this.ParamLength = ParamLength; 
		this.paramNotes = paramNotes;
	}

	public ProvisioningCommand getCommandprovisioning() {
		return commandprovisioning;
	}

	public String getCommandParam() {
		return commandParam;
	}

	public String getParamType() {
		return paramType;
	}

	public String getParamDefault() {
		return paramDefault;
	}

	public void update(ProvisioningCommand command) {
		this.commandprovisioning = command;
	}

	public Long getParamLength() {
		return ParamLength;
	}
	
	public String getParamNotes() {
		return paramNotes;
	}
	
	

}
