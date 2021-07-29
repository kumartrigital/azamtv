package org.mifosplatform.scheduledjobs.scheduledjobs.data;

import org.joda.time.DateTime;

public class EventActionData {
 
	private final Long id;
	private final String eventAction;
	private final String entityName;
	private final String actionName;
	private final String jsonData;
	private final Long resourceId;
	private final Long orderId;
	private final Long clientId;
	private final String status;
	private final DateTime transactionDate;
	
	
	public EventActionData(Long id, String eventaction, String entityName,
			String actionName, String jsonData, Long resourceId, Long orderId, Long clientId,
			String status, DateTime transactionDate) {
		
		this.id=id;
		this.eventAction=eventaction;
		this.entityName=entityName;
		this.actionName=actionName;
		this.jsonData=jsonData;
		this.resourceId=resourceId;
		this.orderId=orderId;
		this.clientId=clientId;
		this.status = status;
		this.transactionDate = transactionDate;
	}


	public Long getId() {
		return id;
	}


	public String getEventAction() {
		return eventAction;
	}


	public String getEntityName() {
		return entityName;
	}


	public String getActionName() {
		return actionName;
	}


	public Long getResourceId() {
		return resourceId;
	}


	public String getJsonData() {
		return jsonData;
	}


	public Long getOrderId() {
		return orderId;
	}


	public Long getClientId() {
		return clientId;
	}


	public String getStatus() {
		return status;
	}


	public DateTime getTransactionDate() {
		return transactionDate;
	}
	

}
