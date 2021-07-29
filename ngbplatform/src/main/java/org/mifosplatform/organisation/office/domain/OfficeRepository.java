/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfficeRepository extends JpaRepository<Office, Long>,
		JpaSpecificationExecutor<Office> {
	
	@Query("from Office office where office.name=:name")
	Office findwithName(@Param("name") String name);
	
	@Query("from Office office where office.externalId=:externalId")
	Office findwithCode(@Param("externalId") String externalId);
}
