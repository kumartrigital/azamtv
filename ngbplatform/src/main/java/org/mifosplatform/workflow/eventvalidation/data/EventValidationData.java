package org.mifosplatform.workflow.eventvalidation.data;

import java.util.List;

import org.mifosplatform.finance.payments.data.McodeData;

public class EventValidationData {

	private Long id;
	private String eventName;
	private String process;
	private String codeValue;
	private String prePost;
	private List<McodeData> eventData;
	private String isDeleted;

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public EventValidationData() {
		// TODO Auto-generated constructor stub
	}

	public EventValidationData(final List<McodeData> templateData) {

		this.eventData = templateData;
	}

	public EventValidationData(final Long id, final String eventName, 
			final String process, final String prePost, 
			final String isDeleted) {

		this.id = id;
		this.eventName = eventName;
		this.process = process;
		this.prePost = prePost;
		this.isDeleted = isDeleted;
	}

	public String getBatchName() {
		return process;
	}

	public void setBatchName(String process) {
		this.process = process;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public List<McodeData> getEventData() {
		return eventData;
	}

	public void setEventData(List<McodeData> eventData) {
		this.eventData = eventData;
	}

	public String getPrePost() {
		return prePost;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

}
