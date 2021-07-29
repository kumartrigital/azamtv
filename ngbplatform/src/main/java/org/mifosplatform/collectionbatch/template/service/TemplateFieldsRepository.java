package org.mifosplatform.collectionbatch.template.service;

import org.mifosplatform.collectionbatch.template.domain.TemplateField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TemplateFieldsRepository extends JpaRepository<TemplateField, Long>, JpaSpecificationExecutor<TemplateField>{

}
