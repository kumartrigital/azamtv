package org.mifosplatform.logistics.supplier.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.logistics.supplier.domain.Supplier;
import org.mifosplatform.logistics.supplier.domain.SupplierJpaRepository;
import org.mifosplatform.logistics.supplier.serialization.SupplierCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class SupplierWritePlatformServiceImp implements
		SupplierWritePlatformService {

	private final static Logger logger = (Logger) LoggerFactory.getLogger(SupplierWritePlatformServiceImp.class);
	private final PlatformSecurityContext context;
	private final SupplierJpaRepository supplierJpaRepository;
	private final SupplierCommandFromApiJsonDeserializer apiJsonDeserializer;
	
	@Autowired
	public SupplierWritePlatformServiceImp(final PlatformSecurityContext context, final SupplierJpaRepository  supplierJpaRepository,
			final SupplierCommandFromApiJsonDeserializer apiJsonDeserializer) {
		
		this.context = context;
		this.supplierJpaRepository = supplierJpaRepository;
		this.apiJsonDeserializer = apiJsonDeserializer;
	}
	
	@Override
	public CommandProcessingResult createSupplier(JsonCommand command) {
		try{
		
		context.authenticatedUser();
		apiJsonDeserializer.validateForCreate(command.json());
		Supplier supplier = Supplier.formJson(command);
		supplierJpaRepository.save(supplier);
		return new CommandProcessingResultBuilder().withEntityId(supplier.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}
			
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

	    Throwable realCause = dve.getMostSpecificCause();
	    if (realCause.getMessage().contains("supplier_code")){
		   	throw new PlatformDataIntegrityException("validation.error.msg.inventory.supplier.duplicate.entry", "validation.error.msg.inventory.supplier.duplicate.entry", "validation.error.msg.inventory.supplier.duplicate.entry","");
		   	
	    }
	   logger.error(dve.getMessage(), dve);   	
	}
	
	@Override
	public CommandProcessingResult updateSupplier(JsonCommand command, Long supplierId) {
   try{
	   
	   this.context.authenticatedUser();
	   apiJsonDeserializer.validateForCreate(command.json());
	   Supplier supplier = retrieveCodeBy(supplierId);
	   final Map<String, Object> changes = supplier.update(command);
	   if(changes.isEmpty()){
			this.supplierJpaRepository.save(supplier);
	   }
	   return new CommandProcessingResultBuilder() //
       .withCommandId(command.commandId()) //
       .withEntityId(supplierId) //
       .with(changes) //
       .build();
	}catch (DataIntegrityViolationException dve) {
		handleDataIntegrityIssues(command, dve);
	      return new CommandProcessingResult(Long.valueOf(-1));
	  }
	}
	
	private Supplier retrieveCodeBy(final Long supplierId) {
        final Supplier supplier = this.supplierJpaRepository.findOne(supplierId);
        if (supplier == null) { throw new ItemNotFoundException(supplierId.toString()); }
        return supplier;
    }

}
