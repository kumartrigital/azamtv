/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.template.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TemplateRepository extends JpaRepository<Template, Long>, JpaSpecificationExecutor<Template> {

	@Query("from Template template where template.isDeleted ='N' and template.entity= ?1 and template.type = ?2")
    List<Template> findByEntityAndType(TemplateEntity entity, TemplateType type);
    
    @Query("from Template template where template.isDeleted ='N'")
	List<Template> findIsNotDeleted();
}