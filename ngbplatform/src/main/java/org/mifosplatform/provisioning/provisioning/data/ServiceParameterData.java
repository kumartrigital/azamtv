package org.mifosplatform.provisioning.provisioning.data;

public class ServiceParameterData {
	
	private Long id;
	private Long clientId;
	private Long clientServiceId;
	private Long serviceId;
	private String serviceCode;
	private String serviceDescription;
	private Long orderId;
	private Long parameterId;
	private String paramName;
	private String paramValue;
	private String type;
	private String status;
	
	
	
	
	
	
	
	public ServiceParameterData(Long id, Long clientId, Long clientServiceId, Long serviceId, String serviceCode,
			String serviceDescription, Long orderId, Long parameterId, String paramName, String paramValue, String type,
			String status) {
		
		this.id = id;
		this.clientId = clientId;
		this.clientServiceId = clientServiceId;
		this.serviceId = serviceId;
		this.serviceCode = serviceCode;
		this.serviceDescription = serviceDescription;
		this.orderId = orderId;
		this.parameterId = parameterId;
		this.paramName = paramName;
		this.paramValue = paramValue;
		this.type = type;
		this.status = status;
	}

	public ServiceParameterData(final Long id, final String paramName,final String paramValue, final String type) {
		
		this.id=id;
		this.paramName=paramName;
		this.paramValue=paramValue;
		this.type=type;
	}

	public Long getId() {
		return id;
	}

	public String getParamName() {
		return paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	
}
