package org.mifosplatform.collectionbatch.usagerateplan.service;

import org.mifosplatform.collectionbatch.usagerateplan.domain.RatePlan;
import org.mifosplatform.collectionbatch.usagerateplan.domain.UsageplanRepository;
import org.mifosplatform.collectionbatch.usagerateplan.serialization.RatePlanCommandFromApiJsonDeserializer;
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

@Service
public class UsagePlanWritePlatformServiceImpl implements UsagePlanWritePlatformService {
	private final static Logger logger = (Logger) LoggerFactory.getLogger(UsagePlanWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final RatePlanCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final UsageplanRepository usageplanRepository;

	@Autowired
	public UsagePlanWritePlatformServiceImpl(PlatformSecurityContext context,
			RatePlanCommandFromApiJsonDeserializer apiJsonDeserializer, UsageplanRepository usageplanRepository) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.usageplanRepository = usageplanRepository;

	}

	@Override
	public CommandProcessingResult createUsageplan(JsonCommand command) {
		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final RatePlan ratePlan = RatePlan.formJson(command);

			this.usageplanRepository.save(ratePlan);
			return new CommandProcessingResultBuilder().withEntityId(ratePlan.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		/*
		 * if (realCause.getMessage().contains("external_id")) {
		 * 
		 * final String externalId = command.stringValueOfParameterNamed("externalId");
		 * throw new
		 * PlatformDataIntegrityException("error.msg.client.duplicate.externalId",
		 * "Client with externalId `" + externalId + "` already exists", "externalId",
		 * externalId);
		 * 
		 * }
		 */

		throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

}
