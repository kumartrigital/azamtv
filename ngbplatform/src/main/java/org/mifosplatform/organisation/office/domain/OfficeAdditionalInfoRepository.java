package org.mifosplatform.organisation.office.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfficeAdditionalInfoRepository extends
		JpaRepository<OfficeAdditionalInfo, Long>,
		JpaSpecificationExecutor<OfficeAdditionalInfo> {


	@Query("from OfficeAdditionalInfo info where info.office =:office")
	OfficeAdditionalInfo findoneByoffice(@Param("office") Office office);
}
