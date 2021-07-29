package org.mifosplatform.portfolio.service.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;
import org.mifosplatform.portfolio.service.data.ServiceMasterData;
import org.mifosplatform.portfolio.service.data.ServiceMasterOptionsData;

public interface ServiceMasterReadPlatformService {
	

	List<ServiceData> retrieveAllServices(String serviceType);
	
	 Collection<ServiceMasterData> retrieveAllServiceMasterData() ;

	Page<ServiceMasterOptionsData> retrieveServices(SearchSqlQuery searchCodes,String serviceCategory);

	ServiceMasterOptionsData retrieveIndividualService(Long serviceId);

	List<EnumOptionData> retrieveServicesTypes();

	List<EnumOptionData> retrieveServiceUnitType();
	
	List<ServiceData> retriveServices(String serviceCategory);//serviceCategory must be 'P' or 'S'

	Collection<ServiceDetailData> retrieveServiceDetails(Long serviceId,String paramCategory);
	
	List<ServiceDetailData> retriveServiceDetailsOfPlan(Long planId);

	List<ServiceData> retriveServicesForDropdown(String serviceCode);
	
	Collection<ServiceDetailData> retrieveServiceDetailsAgainestMasterIdandParamCategory(Long serviceId,String paramCategory);

	ServiceData retriveServiceParam(String serviceCode);

	ServiceMasterOptionsData retriveServicesByUsingSerailNumber(String serialNumber, String serviceType,String serviceParamName);

	List<ServiceMasterData> retrieveAllServiceAvailabilty(String addressType, Long addressId);

	List<ServiceMasterData> retrieveAllServiceAvailabiltyByCity(String addressType, String address);

	
}
