package org.mifosplatform.vendoragreement.domain;

import java.util.List;

import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendorAgreementRepository  extends
JpaRepository<VendorAgreement, Long>,
JpaSpecificationExecutor<VendorAgreement>{
	@Query("from VendorAgreement agmt where agmt.vendorId =:vendorId")
	List<VendorAgreement> findOneByVendorId(@Param("vendorId") Long vendorId);
}

