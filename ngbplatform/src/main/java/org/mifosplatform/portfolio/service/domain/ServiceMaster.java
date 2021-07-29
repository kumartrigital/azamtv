package org.mifosplatform.portfolio.service.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_service", uniqueConstraints = @UniqueConstraint(name = "service_code_key", columnNames = { "service_code" }))
public class ServiceMaster extends AbstractPersistable<Long> {

	@Column(name = "service_code", nullable = false, length = 20)
	private String serviceCode;

	@Column(name = "service_description", nullable = false, length = 100)
	private String serviceDescription;

	@Column(name = "service_type", nullable = false, length = 100)
	private String serviceType;
	
	@Column(name = "service_unittype", nullable = false, length = 100)
	private String serviceUnittype;
	
	@Column(name = "status", nullable = false, length = 100)
	private String status;
	
	@Column(name = "is_optional", nullable = false, length = 100)
	private char isOptional;
	
	@Column(name = "service_category", nullable = false, length = 100)
	private String serviceCategory;
	
	@Column(name = "is_auto")
	private char isAutoProvision;

	@Column(name = "is_deleted")
	private String isDeleted="n";
	/*
	@Column(name = "product_poid", length =100)
	private String productPoid;*/
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "serviceMaster", orphanRemoval = true)
	private List<ServiceDetails> serviceDetails = new ArrayList<ServiceDetails>();
	

public List<ServiceDetails> getServiceDetails() {
		return serviceDetails;
	}


public ServiceMaster()
{}


public static ServiceMaster fromJson(final JsonCommand command) {
	
    final String serviceCode = command.stringValueOfParameterNamed("serviceCode");
    final String serviceDescription = command.stringValueOfParameterNamed("serviceDescription");
    final String serviceType = command.stringValueOfParameterNamed("serviceType");
    final String status= command.stringValueOfParameterNamed("status");
    final boolean isOptional= command.booleanPrimitiveValueOfParameterNamed("isOptional");
    final boolean isAutoProvision= command.booleanPrimitiveValueOfParameterNamed("isAutoProvision");
    final String serviceCategory = command.stringValueOfParameterNamed("serviceCategory");
    return new ServiceMaster(serviceCode,serviceDescription,serviceType,status,isOptional,isAutoProvision, serviceCategory);
}

	public ServiceMaster(final String serviceCode, final String serviceDescription,
			final String serviceType,final String  status,final boolean isOptional, 
			final boolean isAutoProvision, final String serviceCategory) {
		this.serviceCode = serviceCode;
		this.serviceDescription = serviceDescription;
		this.serviceType = serviceType;
		this.status=status;
		this.isOptional=isOptional?'Y':'N';
		this.isAutoProvision=isAutoProvision?'Y':'N';
		this.serviceCategory = serviceCategory;
	}


	public String getServiceCode() {
		return this.serviceCode;
	}

	public String getServiceDescription() {
		return this.serviceDescription;
	}

	public String getServiceType() {
		return this.serviceType;
	}


	public String getIsDeleted() {
		return this.isDeleted;
	}

	

	public String getServiceUnittype() {
		return serviceUnittype;
	}


	public String getStatus() {
		return status;
	}


	public char getIsOptional() {
		return isOptional;
	}


	public char isAuto() {
		return isAutoProvision;
	}


	public  Map<String, Object> update(final JsonCommand command) {
		
		  final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		  final String serviceCodeParamName = "serviceCode";
	        if (command.isChangeInStringParameterNamed(serviceCodeParamName, this.serviceCode)) {
	            final String newValue = command.stringValueOfParameterNamed(serviceCodeParamName);
	            actualChanges.put(serviceCodeParamName, newValue);
	            this.serviceCode = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String servicedescParamName = "serviceDescription";
	        if (command.isChangeInStringParameterNamed(servicedescParamName, this.serviceDescription)) {
	            final String newValue = command.stringValueOfParameterNamed(servicedescParamName);
	            actualChanges.put(servicedescParamName, newValue);
	            this.serviceDescription = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        final String serviceTypeParamName = "serviceType";
			if (command.isChangeInStringParameterNamed(serviceTypeParamName,
					this.serviceType)) {
				final String newValue = command.stringValueOfParameterNamed(serviceTypeParamName);
				actualChanges.put(serviceTypeParamName, newValue);
				this.serviceType=StringUtils.defaultIfEmpty(newValue,null);
			}
			
			final String serviceUnitTypeParamName = "serviceUnitType";
	        if (command.isChangeInStringParameterNamed(serviceUnitTypeParamName, this.serviceUnittype)) {
	            final String newValue = command.stringValueOfParameterNamed(serviceUnitTypeParamName);
	            actualChanges.put(serviceUnitTypeParamName, newValue);
	            this.serviceUnittype = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String serviceCategoryParamName = "serviceCategory";
	        if (command.isChangeInStringParameterNamed(serviceCategoryParamName, this.serviceCategory)) {
	            final String newValue = command.stringValueOfParameterNamed(serviceCategoryParamName);
	            actualChanges.put(serviceCategoryParamName, newValue);
	            this.serviceCategory = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        
	        final String statusParamName = "status";
	        if (command.isChangeInStringParameterNamed(statusParamName, this.status)) {
	            final String newValue = command.stringValueOfParameterNamed(statusParamName);
	            actualChanges.put(statusParamName, newValue);
	            this.status = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	       
	        final boolean isOptional= command.booleanPrimitiveValueOfParameterNamed("isOptional");
	        this.isOptional=isOptional?'Y':'N';

	        final boolean isAutoProvision= command.booleanPrimitiveValueOfParameterNamed("isAutoProvision");
	        this.isAutoProvision=isAutoProvision?'Y':'N';
	        return actualChanges;
	}

	public void delete() {
		if(isDeleted.equalsIgnoreCase("y"))
		{}
		else
		{
			this.serviceCode=this.serviceCode+"_"+this.getId();
			isDeleted="y";
		}
	}


	public ServiceData todata() {
		return new ServiceData(getId(),null,null,null,this.serviceCode,this.serviceDescription,null,null,null,null,null,null,null,null,null);
	}
	
	public void addDetails(ServiceDetails serviceDetail) {
		serviceDetail.update(this);
		this.serviceDetails.add(serviceDetail);

	}

}
