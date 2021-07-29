package org.mifosplatform.billing.currency.domain;

import org.mifosplatform.billing.currency.domain.CountryCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CountryCurrencyRepository extends
		JpaRepository<CountryCurrency, Long>,
		JpaSpecificationExecutor<CountryCurrency> {

	
	@Query("from CountryCurrency countryCurrency where countryCurrency.currency =:currency and  countryCurrency.baseCurrency =:baseCurrency")
	CountryCurrency findByCurrencyAndBaseCurrency(@Param("currency")Long currency,@Param("baseCurrency") Long baseCurrency);
	
	
	
}
