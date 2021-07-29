package org.mifosplatform.sample.comapany.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRegistrationRepository extends JpaRepository<CompanyRegistration, Long> {

}
