package org.mifosplatform.organisation.voucher.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExportVoucherRepository extends JpaRepository<ExportVoucher, String>,
JpaSpecificationExecutor<ExportVoucher>{

	
}
