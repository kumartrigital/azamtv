package org.mifosplatform.portfolio.property.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyMasterRepository extends JpaRepository<PropertyMaster, Long>,
JpaSpecificationExecutor<PropertyMaster>{

	@Query("from PropertyMaster propertyMaster where propertyMaster.propertyCode =:propertyCode and propertyMaster.isDeleted ='N'")
	PropertyMaster findoneByPropertyCode(@Param("propertyCode") String propertyCode);

}

