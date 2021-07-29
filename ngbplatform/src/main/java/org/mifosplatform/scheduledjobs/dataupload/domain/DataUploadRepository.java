package org.mifosplatform.scheduledjobs.dataupload.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataUploadRepository extends JpaRepository<DataUpload, Long>, JpaSpecificationExecutor<DataUpload>{

}


