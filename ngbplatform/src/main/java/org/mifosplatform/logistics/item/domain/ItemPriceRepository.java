/**
 * 
 */
package org.mifosplatform.logistics.item.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Rakesh
 *
 */
public interface ItemPriceRepository extends JpaRepository<ItemPrice,Long >,JpaSpecificationExecutor<ItemPrice>{

}