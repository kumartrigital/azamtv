package org.mifosplatform.provisioning.entitlements.data;

public class CubiwareData {

	private final String serialNo;
	private final Long id;
	private final String deviceModel;
	private final Long subscriberId;
	private final Long clientId;
	
	public CubiwareData(final String serialNo, final Long id, 
			final String deviceModel, final Long subscriberId, final Long clientId) {
		
		this.serialNo = serialNo;
		this.id = id;
		this.deviceModel = deviceModel;
		this.subscriberId = subscriberId;
		this.clientId = clientId;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public Long getId() {
		return id;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public Long getSubscriberId() {
		return subscriberId;
	}

	public Long getClientId() {
		return clientId;
	}
	
	
}
