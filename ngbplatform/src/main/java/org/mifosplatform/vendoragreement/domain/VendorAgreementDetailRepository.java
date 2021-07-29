package org.mifosplatform.vendoragreement.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendorAgreementDetailRepository  extends
JpaRepository<VendorAgreementDetail, Long>,
JpaSpecificationExecutor<VendorAgreementDetail>{
	@Query("from VendorAgreementDetail details where details.vendor =:vendor")
	List<VendorAgreementDetail> findOneByAgreementId(@Param("vendor") VendorAgreement vendor);
}


