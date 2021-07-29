package org.mifosplatform.portfolio.service.domain;

import java.util.ArrayList;
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
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.useradministration.domain.AppUser;

import com.google.gson.JsonElement;

@Entity
@Table(name = "b_service_availability")
public class ServiceAvailabilty extends AbstractAuditableCustom<AppUser, Long>{

	
	@Column(name = "level")
	private String addressType;
	
	@Column(name = "level_id")
	private Long addressId;
	
	@Column(name = "service_code")
	private Long serviceId;
	
/*	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "clientService", orphanRemoval = true)
	private List<ServiceParameters> serviceParameters = new ArrayList<ServiceParameters>();
*/	
	
	
	public ServiceAvailabilty(){
		
	}
	
	public ServiceAvailabilty(long addressId, Long  serviceId, String level){
		this.addressId = addressId;
		this.serviceId = serviceId;
		this.addressType = level;
	}	
	
	
	public static ServiceAvailabilty fromJson(JsonCommand command){
		String level = command.stringValueOfParameterName("addressType");
		Long serviceId = command.longValueOfParameterNamed("serviceId");
		Long addressId = command.longValueOfParameterNamed("addressId");
		return new ServiceAvailabilty(addressId, serviceId, level);
	}
	
	public ServiceAvailabilty update(Long id, Long addressId, String addressType, Long serviceId){
		setId(id);
		this.addressId = addressId;
		this.addressType = addressType;
		this.serviceId = serviceId;
		return this;
	}
	
	
	
	public void update(ServiceAvailabilty serviceAvailabilty){
		setId(serviceAvailabilty.getId());
		this.addressId = serviceAvailabilty.getAddressId();
		this.addressType = serviceAvailabilty.getAddressType();
		this.serviceId = serviceAvailabilty.getServiceId();
	}
	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

}