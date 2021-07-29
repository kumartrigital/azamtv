package org.mifosplatform.cms.journalvoucher.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JournalvoucherDetailsRepository extends JpaRepository<JournalVoucherDetails, Long>,
JpaSpecificationExecutor<JournalVoucherDetails>{

}
