package org.mifosplatform.organisation.address.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.data.AddressLocationDetails;
import org.mifosplatform.organisation.address.data.CityDetailsData;
import org.mifosplatform.organisation.address.data.CountryDetails;

public interface AddressReadPlatformService {


	List<AddressData> retrieveSelectedAddressDetails(String selectedname);

	List<AddressData> retrieveAddressDetailsBy(Long clientId, String addressType);

	List<AddressData> retrieveAddressDetails();
	
	List<String> retrieveCountryDetails();

	List<String> retrieveStateDetails();

	List<String> retrieveCityDetails();
	
	List<String> retrieveDistrictDetails();

	List<AddressData> retrieveCityDetails(String selectedname);

	List<EnumOptionData> addressType();

	AddressData retrieveAdressBy(String cityName);
	
	List<AddressData> retrieveAdressBystate(String stateName);

	List<CountryDetails> retrieveCountries();
	
	List<AddressData> retrieveClientAddressDetails(Long clientId);
	
	Page<AddressLocationDetails> retrieveAllAddressLocations(SearchSqlQuery searchAddresses);

	List<CityDetailsData> retrieveCitywithCodeDetails();

	List<CityDetailsData> retrieveAddressDetailsByCityName(String cityName);

	AddressData retriveAddressByCity(String city);
	
	
	

}

