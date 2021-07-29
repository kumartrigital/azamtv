package org.mifosplatform.vendormanagement.vendor.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.vendoragreement.exception.VendorNotFoundException;
import org.mifosplatform.vendormanagement.vendor.domain.VendorManagement;
import org.mifosplatform.vendormanagement.vendor.domain.VendorManagementRepository;
import org.mifosplatform.vendormanagement.vendor.serialization.VendorManagementCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

	
@Service
public class VendorManagementWritePlatformServiceImpl implements VendorManagementWritePlatformService{
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(VendorManagementWritePlatformServiceImpl.class);	
	
	private PlatformSecurityContext context;
	private VendorManagementRepository vendormanagementRepository; 
	private VendorManagementCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	 
	@Autowired
	public VendorManagementWritePlatformServiceImpl(final PlatformSecurityContext context, 
			final VendorManagementRepository vendormanagementRepository, 
			final VendorManagementCommandFromApiJsonDeserializer fromApiJsonDeserializer) {
		this.context = context;
		this.vendormanagementRepository = vendormanagementRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createVendorManagement(JsonCommand command) {
		
		try{
			
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final VendorManagement vendor=VendorManagement.fromJson(command);
			
			this.vendormanagementRepository.save(vendor);
			return new CommandProcessingResult(vendor.getId());
		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	

	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("uvendor_code_key")) {
			final String vendorCode = command.stringValueOfParameterNamed("vendorCode");
			throw new PlatformDataIntegrityException("error.msg.vendor.code.duplicate", "A code with name '" + vendorCode + "' already exists");
		} else if (realCause.getMessage().contains("uvendor_mobileno_key")) {
			final String vendormobileNo = command.stringValueOfParameterNamed("vendormobileNo");
			throw new PlatformDataIntegrityException("error.msg.vendor.mobileno.duplicate", "A code with name '" + vendormobileNo + "' already exists");
		} else if (realCause.getMessage().contains("uvendor_landlineno_key")) {
			final String vendorTelephoneNo = command.stringValueOfParameterNamed("vendorLandlineNo");
			throw new PlatformDataIntegrityException("error.msg.vendor.landlineno.duplicate", "A code with name '" + vendorTelephoneNo + "' already exists");
		} else if (realCause.getMessage().contains("uvendor_emailid_key")) {
			final String vendorEmailId = command.stringValueOfParameterNamed("vendorEmailId");
			throw new PlatformDataIntegrityException("error.msg.vendor.emailid.duplicate", "A code with name '" + vendorEmailId + "' already exists");
		}

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());

	}

	@Transactional
	@Override
	public CommandProcessingResult updateVendorManagement(Long vendorId, JsonCommand command) {
		try{
		this.context.authenticatedUser();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		VendorManagement vendor=retrieveCodeBy(vendorId);
		
		final Map<String, Object> changes = vendor.update(command);
		if(!changes.isEmpty()){
			this.vendormanagementRepository.saveAndFlush(vendor);
		}
		return new CommandProcessingResultBuilder() //
	       .withCommandId(command.commandId()) //
	       .withEntityId(vendorId) //
	       .with(changes) //
	       .build();
		}catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	private VendorManagement retrieveCodeBy(final Long vendorId) {
        final VendorManagement vendor = this.vendormanagementRepository.findOne(vendorId);
        if (vendor == null || vendor.isDeleted()) { throw new VendorNotFoundException(vendorId.toString()); }
        return vendor;
    }
	
	 @Transactional
	 @Override
	 public CommandProcessingResult deleteVendorManagement(final Long vendorId) {

		 final VendorManagement vendor = this.vendormanagementRepository.findOne(vendorId);
	     if (vendor == null || vendor.isDeleted()) { 
	    	 throw new VendorNotFoundException(vendorId.toString());
	     }
	     vendor.delete();
	     this.vendormanagementRepository.save(vendor);

	     return new CommandProcessingResultBuilder().withEntityId(vendorId).build();
	  }

}
