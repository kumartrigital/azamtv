package org.mifosplatform.crm.clientprospect.data;

public class ProspectDetailCallStatus {

	private Long statusId;
	private String callStatus;

	public ProspectDetailCallStatus() {
	}

	public ProspectDetailCallStatus(final Long statusId, final String callStatus) {
		this.statusId = statusId;
		this.callStatus = callStatus;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public String getCallStatus() {
		return callStatus;
	}

	public void setCallStatus(String callStatus) {
		this.callStatus = callStatus;
	}
}
