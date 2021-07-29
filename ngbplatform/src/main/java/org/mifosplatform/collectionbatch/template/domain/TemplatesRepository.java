package org.mifosplatform.collectionbatch.template.domain;

import org.mifosplatform.collectionbatch.template.domain.Templates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TemplatesRepository extends JpaRepository<Templates, Long>, JpaSpecificationExecutor<Templates> {

}
