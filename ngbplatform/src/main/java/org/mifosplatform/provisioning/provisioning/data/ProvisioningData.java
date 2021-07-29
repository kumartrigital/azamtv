package org.mifosplatform.provisioning.provisioning.data;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.organisation.ippool.data.IpPoolData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.GroupData;
import org.mifosplatform.portfolio.order.data.OrderLineData;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;

public class ProvisioningData {

	private Long provisioningSystem;
	private String commandName;
	private String provisioningSystemName;
	private Long id;
	private String status;
	private List<McodeData> commands;
	private List<McodeData> provisioning;
	private List<NetworkElementData> networkElementDatas;
	private List<ProvisioningCommandParameterData> commandParameters;
	


	private Collection<MCodeData> vlanDatas;
	private List<OrderLineData> services;
	private List<IpPoolData> ipPoolDatas;
	private List<ServiceParameterData> parameterDatas,serviceDatas;
	private Collection<GroupData> groupDatas;
	private List<ProvisionAdapter> provisionAdapterData;
	private String paramNotes;
	private ClientData clientData;
	private Long client_id;
	private String request_type;
	private LocalDate created_date;
	private String response_message;
	private String response_status;

	

	public ProvisioningData(final Long id,final Long provisioningSystem,
			final String CommandName,final String Status,final String provisioningSystemName ){
		this.id=id;
		this.provisioningSystem=provisioningSystem;
		this.commandName=CommandName;
		this.status=Status;
		this.provisioningSystemName = provisioningSystemName;
	}
	

	public ProvisioningData(final List<McodeData> provisioning, final List<McodeData> commands) {
		
		this.commands=commands;
		this.provisioning=provisioning;		
	}
	
	public ProvisioningData(final List<NetworkElementData> provisioning, final List<McodeData> commands,String empty) {
		
		this.commands=commands;
		this.networkElementDatas=provisioning;		
	}


	
	public ProvisioningData(final Collection<MCodeData> vlanDatas,
			final List<IpPoolData> ipPoolDatas, 
			final List<OrderLineData> services,final List<ServiceParameterData> serviceDatas, 
			final List<ServiceParameterData> parameterDatas, 
			final Collection<GroupData> groupDatas) {
		
		this.vlanDatas=vlanDatas;
		this.services=services;
		this.ipPoolDatas=ipPoolDatas;
		this.parameterDatas=parameterDatas;
		this.serviceDatas=serviceDatas;
		this.groupDatas=groupDatas;
	}

	

	public ProvisioningData(List<ProvisioningCommandParameterData> commandParameters) {
		this.commandParameters = commandParameters;
	}

	public ProvisioningData(Long provisioningSystem, ClientData clientData){
		this.provisioningSystem=provisioningSystem;
		this.setClientData(clientData);
	}

	public ProvisioningData() {
		// For ProvisioningAdapterApiResource.java
	}


	public Long getProvisioningSystem() {
		return provisioningSystem;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public String getCommandName() {
		return commandName;
	}


	public Long getId() {
		return id;
	}


	public String getStatus() {
		return status;
	}


	public List<McodeData> getCommands() {
		return commands;
	}


	public List<McodeData> getProvisioning() {
		return provisioning;
	}


	public void setCommands(List<McodeData> commands) {
		this.commands = commands;
	}


	public void setProvisioning(List<McodeData> provisioning) {
		this.provisioning = provisioning;
	}
	
	public List<ProvisioningCommandParameterData> getCommandParameters() {
		return commandParameters;
	}


	public void setCommandParameters(
			List<ProvisioningCommandParameterData> commandParameters) {
		this.commandParameters = commandParameters;
	}


	public List<ProvisionAdapter> getProvisionAdapterData() {
		return provisionAdapterData;
	}


	public void setProvisionAdapterData(List<ProvisionAdapter> provisionAdapterData) {
		this.provisionAdapterData = provisionAdapterData;
	}
	public String getParamNotes() {
		return paramNotes;
	}


	public void setParamNotes(String paramNotes) {
		this.paramNotes = paramNotes;
	}


	public ClientData getClientData() {
		return clientData;
	}


	public void setClientData(ClientData clientData) {
		this.clientData = clientData;
	}
    public Long getClient_id() {
		return client_id;
	}


	public void setClient_id(Long client_id) {
		this.client_id = client_id;
	}


	public String getRequest_type() {
		return request_type;
	}


	public void setRequest_type(String request_type) {
		this.request_type = request_type;
	}


	public LocalDate getCreated_date() {
		return created_date;
	}


	public void setCreated_date(LocalDate created_date) {
		this.created_date = created_date;
	}


	public String getResponse_message() {
		return response_message;
	}


	public void setResponse_message(String response_message) {
		this.response_message = response_message;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getResponse_status() {
		return response_status;
	}


	public void setResponse_status(String response_status) {
		this.response_status = response_status;
	}
	
	public List<NetworkElementData> getNetworkElementDatas() {
		return networkElementDatas;
	}


	public void setNetworkElementDatas(List<NetworkElementData> networkElementDatas) {
		this.networkElementDatas = networkElementDatas;
	}

	
	
	
}
