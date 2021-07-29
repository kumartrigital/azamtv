package org.mifosplatform.provisioning.provisioning.data;

public class ProvisioningRequestData {

	private Long id;
	private Long orderId;
	private String provisionigSystem;
	private String requestType;
	
	
	
	
	public ProvisioningRequestData() {
		super();
	}


	public ProvisioningRequestData(Long id, Long orderId, String provisionigSystem, String requestType) {

		this.id = id;
		this.orderId = orderId;
		this.provisionigSystem = provisionigSystem;
		this.requestType = requestType;
	}


	public Long getId() {
		return id;
	}


	public Long getOrderId() {
		return orderId;
	}


	public String getProvisionigSystem() {
		return provisionigSystem;
	}


	public String getRequestType() {
		return requestType;
	}
	
	
	
	
}
