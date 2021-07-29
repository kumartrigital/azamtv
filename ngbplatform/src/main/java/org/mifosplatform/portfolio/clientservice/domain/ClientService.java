package org.mifosplatform.portfolio.clientservice.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_client_service")
public class ClientService extends AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "Service_id")
	private Long serviceId;

	@Column(name = "status")
	private String status;

	@Column(name = "client_service_poid")
	private String clientServicePoid;

	@Column(name = "network_subscriber_id")
	private String networkSubscriberId;

	@Column(name = "auto_renewal")
	private char autoRenewal;
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "clientService", orphanRemoval = true)
	private List<ServiceParameters> serviceParameters = new ArrayList<ServiceParameters>();

	public ClientService() {
	}

	public ClientService(Long clientId, Long serviceId, String status) {
		this.clientId = clientId;
		this.serviceId = serviceId;
		this.status = status;
	}

	public ClientService(Long clientId, Long serviceId, String status, String clientServicePoid) {
		this.clientId = clientId;
		this.serviceId = serviceId;
		this.status = status;
		this.clientServicePoid = clientServicePoid;
	}

	public ClientService(Long clientId, Long serviceId, String status, String clientServicePoid,
			String networkSubscriberId) {
		this.clientId = clientId;
		this.serviceId = serviceId;
		this.status = status;
		this.clientServicePoid = clientServicePoid;
		this.networkSubscriberId = networkSubscriberId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getClientServicePoid() {
		return clientServicePoid;
	}

	public void setClientServicePoid(String clientServicePoid) {
		this.clientServicePoid = clientServicePoid;
	}

	public static ClientService createNew(JsonCommand command) {
		return null;
	}

	public static ClientService createNew(Long clientId, Long serviceId, String status) {
		return new ClientService(clientId, serviceId, status);
	}

	public static ClientService createNew_ClientServicePoid(Long clientId, Long serviceId, String status,
			String clientServicePoid) {
		return new ClientService(clientId, serviceId, status, clientServicePoid);
	}

	public void addDetails(ServiceParameters sp) {
		sp.update(this);
		this.serviceParameters.add(sp);

	}

	public List<ServiceParameters> getServiceParameters() {
		return serviceParameters;
	}

	public void setServiceParameters(List<ServiceParameters> serviceParameters) {
		this.serviceParameters = serviceParameters;
	}

	public String getNetworkSubscriberId() {
		return networkSubscriberId;
	}
	
	public char getAutoRenewal() {
		return autoRenewal;
	}

	public void setAutoRenewal(char autoRenewal) {
		this.autoRenewal = autoRenewal;
	}

	public void setNetworkSubscriberId(String networkSubscriberId) {
		this.networkSubscriberId = networkSubscriberId;
	}

}