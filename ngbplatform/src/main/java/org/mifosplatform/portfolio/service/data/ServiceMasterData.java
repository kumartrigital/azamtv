package org.mifosplatform.portfolio.service.data;

public class ServiceMasterData {

private String serviceType;
private String categoryType;

private long id;
private String level;
private Long levelId;
private Long serviceId;
private String serviceCode;


public ServiceMasterData(final String serviceType,final String categoryType)
{
	this.serviceType=serviceType;
	this.categoryType=categoryType;
}

	public ServiceMasterData(final Long id,final String level,final Long levelId, final Long serviceId){
		this.id=id;
		this.level = level;
		this.levelId = levelId;
		this.serviceId = serviceId;
	}
	
	public ServiceMasterData(final Long id,final String level,final Long levelId, final String serviceCode){
		this.id=id;
		this.level = level;
		this.levelId = levelId;
		this.serviceCode= serviceCode;
	}

public String getServiceType() {
	return serviceType;
}

public void setServiceType(final String serviceType) {
	this.serviceType = serviceType;
}

public String getCategoryType() {
	return categoryType;
}

public void setCategoryType(final String categoryType) {
	this.categoryType = categoryType;
}

}
