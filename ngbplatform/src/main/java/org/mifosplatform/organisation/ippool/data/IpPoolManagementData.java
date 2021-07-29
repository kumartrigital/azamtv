package org.mifosplatform.organisation.ippool.data;

import java.util.List;

/**
 * 
 * @author ashokreddy
 *
 */
public class IpPoolManagementData {
	
	private Long id;
	private String ipAddress;
	private String ipPoolDescription;
	private String status;
	private Long clientId;
	private String clientName;
	private String notes;
	List<String> ipAddressData;
	private Long type;
	private String typeCodeValue;
	private Long subNet;
	
	public IpPoolManagementData(final Long id, final String ipAddress, final String poolName,
			final String status, final Long clientId, final String clientName, 
			final String notes, final Long type, final String typeCodeValue, final Long subNet) {
		
		this.id=id;
		this.ipAddress=ipAddress;
		this.ipPoolDescription=poolName;
		this.status=status;
		this.clientId=clientId;
		this.clientName=clientName;
		this.notes=notes;
		this.type=type;
		this.typeCodeValue=typeCodeValue;
		this.subNet=subNet;
	}

	public IpPoolManagementData(List<String> ipAddressData) {
		this.ipAddressData=ipAddressData;
	}

	public IpPoolManagementData(Long id) {
		this.id=id;
	}

	public Long getId() {
		return id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getIpPoolDescription() {
		return ipPoolDescription;
	}

	public String getStatus() {
		return status;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public String getNotes() {
		return notes;
	}

	public List<String> getIpAddressData() {
		return ipAddressData;
	}

	public Long getType() {
		return type;
	}

	public String getTypeCodeValue() {
		return typeCodeValue;
	}

	public Long getSubNet() {
		return subNet;
	}
	
	
}
