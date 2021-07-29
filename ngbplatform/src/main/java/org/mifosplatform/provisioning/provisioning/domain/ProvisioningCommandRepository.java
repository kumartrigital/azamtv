package org.mifosplatform.provisioning.provisioning.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 
 * @author ashokreddy
 *
 */
public interface ProvisioningCommandRepository extends JpaRepository<ProvisioningCommand, Long>{
	
	@Query("from ProvisioningCommand provisioningCommand where provisioningCommand.commandName =:commandName and provisioningCommand.isDeleted ='N'")
	ProvisioningCommand findByCommandName(@Param("commandName") String commandName);

}
