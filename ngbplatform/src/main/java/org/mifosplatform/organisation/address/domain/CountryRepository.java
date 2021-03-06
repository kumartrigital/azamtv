package org.mifosplatform.organisation.address.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CountryRepository extends JpaRepository<Country,Long>,JpaSpecificationExecutor<Country>{
	
	
	Country findOneByCountryName(String countryName);

}
