package org.mifosplatform.provisioning.provisioning.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_provisioning_request_detail")
public class ProvisioningRequestDetail  extends AbstractPersistable<Long>{
	
	private static final long serialVersionUID = 1L;
	@ManyToOne
	@JoinColumn(name = "provisioning_req_id")
	private ProvisioningRequest provisioningRequest;
	
	@Column(name = "request_message")
	private String requestMessage;
	
	@Column(name = "response_message")
	private String responseMessage;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "response_date")
	private Date responseDate;
	
	@Column(name = "response_status")
	private String responseStatus;

	public ProvisioningRequestDetail() {
	}

	public ProvisioningRequestDetail(String requestMessage,
			String responseMessage, Date responseDate, String responseStatus) {

		this.requestMessage = requestMessage;
		this.responseMessage = responseMessage;
		this.responseDate = responseDate;
		this.responseStatus = responseStatus;
	}

	public void updateStatus(JsonCommand command) {
		this.responseStatus = command.stringValueOfParameterNamed("receivedStatus");
		this.responseMessage = command.stringValueOfParameterNamed("receiveMessage");		
		this.responseDate = new Date();
	}
	
	public ProvisioningRequest getProvisioningRequest() {
		return provisioningRequest;
	}

	public void setProvisioningRequest(ProvisioningRequest provisioningRequest) {
		this.provisioningRequest = provisioningRequest;
	}

	public String getRequestMessage() {
		return requestMessage;
	}

	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public Date getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Date responseDate) {
		this.responseDate = responseDate;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}
	
}
