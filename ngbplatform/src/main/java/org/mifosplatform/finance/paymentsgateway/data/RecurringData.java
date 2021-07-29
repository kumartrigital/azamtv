package org.mifosplatform.finance.paymentsgateway.data;

public class RecurringData {

	private Long id;
	private Long clientId;
	private Long orderId;
	private String subscriberId;
	
	public RecurringData(Long id, Long clientId, Long orderId, String subscriberId){
		
		this.id = id;
		this.clientId = clientId;
		this.orderId = orderId;
		this.subscriberId = subscriberId;
	}

	public Long getId() {
		return id;
	}

	public Long getClientId() {
		return clientId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getSubscriberId() {
		return subscriberId;
	}
	
	

}
