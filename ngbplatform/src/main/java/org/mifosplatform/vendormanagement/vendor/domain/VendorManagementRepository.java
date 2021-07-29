package org.mifosplatform.vendormanagement.vendor.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VendorManagementRepository  extends
JpaRepository<VendorManagement, Long>,
JpaSpecificationExecutor<VendorManagement>{

}

