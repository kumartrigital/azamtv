/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.domain;

import org.mifosplatform.infrastructure.security.domain.PlatformUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppUserRepository
		extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser>, PlatformUserRepository {
	// no behaviour added

	@Query("from AppUser user where user.email =:email and user.deleted is false")
	AppUser findByEmail(@Param("email") String email);

	@Query("from AppUser user where user.username =:username")
	AppUser findByUsername(@Param("username") String username);

	@Query(value = "select * from  m_appuser where id = :userid and secret_key =:key and secret_key_status is false", nativeQuery = true)
	AppUser findBySecretKey(@Param("userid") final Long userid, @Param("key") final String key);

}
