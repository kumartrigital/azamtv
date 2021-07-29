package org.mifosplatform.crm.ticketmaster.subcategory.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Written by H
 * for adding Subcategory items
 *
 */

public interface CreateSubcategoryRepository extends JpaRepository<CreateSubcategory, Long>, JpaSpecificationExecutor<CreateSubcategory>{

}
