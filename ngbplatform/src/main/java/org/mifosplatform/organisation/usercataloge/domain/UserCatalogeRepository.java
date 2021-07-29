package org.mifosplatform.organisation.usercataloge.domain;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCatalogeRepository  extends JpaRepository<UserCataloge, Long>,
JpaSpecificationExecutor<UserCataloge> {
	
	@Query("from UserCataloge uc where uc.userId =:userId and uc.isDeleted='N'")
	Set<UserCataloge> findByUserIdValue(@Param("userId") Long userId);

}
