package org.mifosplatform.organisation.voucher.domain;


import org.mifosplatform.organisation.voucher.data.VoucherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 
 * @author ashokreddy
 *
 */

public interface VoucherRepository extends JpaRepository<Voucher, Long>,JpaSpecificationExecutor<Voucher>{

	

}
