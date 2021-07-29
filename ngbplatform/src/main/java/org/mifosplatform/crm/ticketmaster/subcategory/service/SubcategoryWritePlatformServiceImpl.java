package org.mifosplatform.crm.ticketmaster.subcategory.service;


import org.mifosplatform.crm.ticketmaster.subcategory.domain.CreateSubcategory;
import org.mifosplatform.crm.ticketmaster.subcategory.domain.CreateSubcategoryRepository;
import org.mifosplatform.crm.ticketmaster.subcategory.serialization.SubcategoryCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Written by H
 * for adding Subcategory items
 *
 */

@Service
public class SubcategoryWritePlatformServiceImpl implements SubcategoryWritePlatformService {

	
	private final PlatformSecurityContext context;
	private final SubcategoryCommandFromApiJsonDeserializer fromapiJsonDeserializer;
	private final CreateSubcategoryRepository subcategoryRepository;
	private JpaRepository<CreateSubcategory, Long> CreateSubcategoryRepository;
	
	private final static Logger logger = LoggerFactory.getLogger(SubcategoryWritePlatformServiceImpl.class);

	@Autowired
	public SubcategoryWritePlatformServiceImpl(PlatformSecurityContext context,
			SubcategoryCommandFromApiJsonDeserializer fromapiJsonDeserializer, final CreateSubcategoryRepository subcategoryRepository) {

		this.context = context;
		this.fromapiJsonDeserializer = fromapiJsonDeserializer;
		this.subcategoryRepository = subcategoryRepository;
	}

	@Transactional
	@Override
	public CommandProcessingResult createSubcategory(JsonCommand command) {
		
		// JSON Inserting to DB

				try {

					context.authenticatedUser();
					fromapiJsonDeserializer.validateForCreate(command.json());
					CreateSubcategory subcategory = CreateSubcategory.fromJson(command);
					subcategoryRepository.saveAndFlush(subcategory);
					return new CommandProcessingResultBuilder().withEntityId(subcategory.getId()).build();

				} catch (DataIntegrityViolationException dve) {
					handleDataIntegrityIssues(command, dve);
					return CommandProcessingResult.empty();
				} 
	}
	private void handleDataIntegrityIssues(final JsonCommand command,
			final DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
        
    	throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
}
