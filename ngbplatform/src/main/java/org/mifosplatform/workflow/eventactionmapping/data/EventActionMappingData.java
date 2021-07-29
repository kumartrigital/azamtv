package org.mifosplatform.workflow.eventactionmapping.data;

import java.util.List;

import org.mifosplatform.finance.payments.data.McodeData;

public class EventActionMappingData {

	private String process;
	private Long id;
	private String eventName;
	private String actionName;
	private String codeValue;
	private List<McodeData> actionData;
	private List<McodeData> eventData;
	private Long orderBy;
	private boolean prePost;
	private String processParams;
	private String isDeleted;
	private boolean issynchronous;
	private String is_synchronous;
	

	public EventActionMappingData(final Long id, final String eventName,
			final String actionName, final String process,
			final String codeValue,
			final List<EventActionMappingData> actionData,
			final List<EventActionMappingData> eventData,final Long orderBy, final boolean prePost, final String processParams, final boolean issynchronous) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.eventName = eventName;
		this.actionName = actionName;
		this.process = process;
		this.codeValue = codeValue;
		this.orderBy = orderBy;
		this.prePost = prePost;
		this.processParams = processParams;
		this.issynchronous = issynchronous;

	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public EventActionMappingData() {
		// TODO Auto-generated constructor stub
	}

	public EventActionMappingData(final List<McodeData> actionData,
			final List<McodeData> eventData) {
		// TODO Auto-generated constructor stub
		this.actionData = actionData;
		this.eventData = eventData;
	}

	public EventActionMappingData(final Long id, final String eventName,
			final String actionName, final String process, 
			final Long orderBy, final boolean prePost, final String processParams,
			final String isDeleted, final boolean issynchronous) {
		this.id = id;
		this.eventName = eventName;
		this.actionName = actionName;
		this.process = process;
		this.orderBy = orderBy;
		this.prePost = prePost;
		this.processParams = processParams;
		this.isDeleted = isDeleted;
		this.issynchronous = issynchronous;
		if(issynchronous){
			is_synchronous="Yes";
		}else{
			is_synchronous="No";
		}
		
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

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public List<McodeData> getActionData() {
		return actionData;
	}

	public void setActionData(List<McodeData> actionData) {
		this.actionData = actionData;
	}

	public List<McodeData> getEventData() {
		return eventData;
	}

	public void setEventData(List<McodeData> eventData) {
		this.eventData = eventData;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public Long getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Long orderBy) {
		this.orderBy = orderBy;
	}

	public boolean getPrePost() {
		return prePost;
	}

	public void setPrePost(boolean prePost) {
		this.prePost = prePost;
	}

	public String getProcessParams() {
		return processParams;
	}

	public void setProcessParams(String processParams) {
		this.processParams = processParams;
	}
	
	public boolean getissynchronous() {
		return issynchronous;
	}

	public void setissynchronouss(boolean issynchronous) {
		this.issynchronous = issynchronous;
	}
    
	
	
}
