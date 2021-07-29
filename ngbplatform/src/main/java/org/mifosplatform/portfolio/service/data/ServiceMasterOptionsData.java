package org.mifosplatform.portfolio.service.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.emun.data.EnumValuesData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.plan.data.ServiceData;


public class ServiceMasterOptionsData {
private Collection<ServiceMasterData> serviceMasterOptions;
private Long id;
private String serviceCode;
private String serviceDescription;
private String serviceType;
private String serviceStatus;
private String serviceUnitType;
private String isOptional;
private Collection<EnumValuesData> serviceTypes;
private List<EnumOptionData> serviceUnitTypes,status;
private String isAutoProvision;
private Collection<MCodeData> serviceParamsData;
private String serviceCategory;
private Collection<ServiceDetailData> serviceDetailDatas;
private List<ServiceData> serviceCategorys;
private String systemCode;
private Long provisioningId;
private String paramName;
private Long serviceId;



public ServiceMasterOptionsData() {
}


public ServiceMasterOptionsData(final Long id, final String serviceCode,
		final String serviceDescription,final String serviceType, final String serviceUnitType, final String status,
		final String isOptional, final String isAutoProvision, final String serviceCategory) {
	
	this.id=id;
	this.serviceDescription=serviceDescription;
	this.serviceCode=serviceCode;
	this.serviceType=serviceType;
	this.serviceUnitType=serviceUnitType;
	this.serviceStatus=status;
	this.isOptional=isOptional;
	if(this.isOptional.equalsIgnoreCase("Y")){
		this.isOptional="Yes";
	}
	else{
		this.isOptional="No";
	}
	this.isAutoProvision=isAutoProvision;
	if(this.isAutoProvision.equalsIgnoreCase("Y")){
		this.isAutoProvision="Yes";
	}
	else{
		this.isAutoProvision="No";
	}
	this.serviceCategory = serviceCategory;
}


public ServiceMasterOptionsData(final Collection<EnumValuesData> serviceType,
		final List<EnumOptionData> serviceUnitType, final List<EnumOptionData> status,
		final Collection<MCodeData> serviceParamsData, List<ServiceData> serviceCategorys) {
	
	this.serviceTypes=serviceType;
	this.serviceUnitTypes=serviceUnitType;
	this.status=status;
	this.serviceParamsData = serviceParamsData;
	this.serviceCategorys = serviceCategorys;
}

public Collection<ServiceMasterData> getServiceMasterOptions() {
	return serviceMasterOptions;
}

public Long getId() {
	return id;
}

public String getServiceCode() {
	return serviceCode;
}

public String getserviceDescription() {
	return serviceDescription;
}

public String getServiceType() {
	return serviceType;
}

public void setServiceMasterOptions(final Collection<ServiceMasterData> serviceMasterOptions) {
	this.serviceMasterOptions = serviceMasterOptions;
}

public void setId(final Long id) {
	this.id = id;
}

public void setServiceCode(final String serviceCode) {
	this.serviceCode = serviceCode;
}

public void setServiceDescription(final String serviceDescription) {
	this.serviceDescription = serviceDescription;
}

public void setServiceType(final String serviceType) {
	this.serviceType = serviceType;
}

public void setServiceStatus(final String serviceStatus) {
	this.serviceStatus = serviceStatus;
}

public void setServiceUnitType(final String serviceUnitType) {
	this.serviceUnitType = serviceUnitType;
}

public void setIsOptional(final String isOptional) {
	this.isOptional = isOptional;
}

public void setServiceTypes(final Collection<EnumValuesData> serviceTypes) {
	this.serviceTypes = serviceTypes;
}

public void setServiceUnitTypes(final List<EnumOptionData> serviceUnitTypes) {
	this.serviceUnitTypes = serviceUnitTypes;
}

public void setStatus(final List<EnumOptionData> status) {
	this.status = status;
}

public String getServiceDescription() {
	return serviceDescription;
}

public String getServiceStatus() {
	return serviceStatus;
}

public String getServiceUnitType() {
	return serviceUnitType;
}

public String getIsOptional() {
	return isOptional;
}

public Collection<EnumValuesData> getServiceTypes() {
	return serviceTypes;
}

public List<EnumOptionData> getServiceUnitTypes() {
	return serviceUnitTypes;
}

public List<EnumOptionData> getStatus() {
	return status;
}

public Collection<MCodeData> getServiceParamsData() {
	return serviceParamsData;
}

public void setServiceParamsData(Collection<MCodeData> serviceParamsData) {
	this.serviceParamsData = serviceParamsData;
}

public void setServiceDetailData(Collection<ServiceDetailData> serviceDetailDatas) {
	this.serviceDetailDatas = serviceDetailDatas;
}

public Collection<ServiceDetailData> getServiceDetailDatas() {
	return serviceDetailDatas;
}

public void setServiceDetailDatas(Collection<ServiceDetailData> serviceDetailDatas) {
	this.serviceDetailDatas = serviceDetailDatas;
}


public void setServiceCategorys(List<ServiceData> serviceCategorys) {
	this.serviceCategorys = serviceCategorys;
}


public String getServiceCategory() {
	return serviceCategory;
}


public void setServiceCategory(String serviceCategory) {
	this.serviceCategory = serviceCategory;
}


public void setSystemCode(String systemCode) {
	this.systemCode = systemCode;
}


public void setProvisioningId(Long provisioningId) {
	this.provisioningId = provisioningId;
}


public void setParamName(String paramName) {
	this.paramName = paramName;
}


public void setServiceId(Long serviceId) {
	this.serviceId = serviceId;
}


public ServiceMasterOptionsData(final Collection<ServiceMasterData> serviceMasterOptions)
{
	this.serviceMasterOptions=serviceMasterOptions;
}


}
