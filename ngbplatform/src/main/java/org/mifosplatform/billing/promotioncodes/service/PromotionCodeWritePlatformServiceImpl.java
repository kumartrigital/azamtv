package org.mifosplatform.billing.promotioncodes.service;

import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.mifosplatform.billing.promotioncodes.domain.PromotionCodeMaster;
import org.mifosplatform.billing.promotioncodes.domain.PromotionCodeRepository;
import org.mifosplatform.billing.promotioncodes.exception.PromotionCodeNotFoundException;
import org.mifosplatform.billing.promotioncodes.serialization.PromotionCodeCommandFromApiJsonDeserializer;
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
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hugo
 * 
 */
@Service
public class PromotionCodeWritePlatformServiceImpl implements PromotionCodeWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(PromotionCodeWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final PromotionCodeCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final PromotionCodeRepository promotionCodeRepository;

	/**
	 * @param context
	 * @param apiJsonDeserializer
	 * @param promotionMappingRepository
	 */
	@Autowired
	public PromotionCodeWritePlatformServiceImpl(final PlatformSecurityContext context,
			final PromotionCodeCommandFromApiJsonDeserializer apiJsonDeserializer,
			final PromotionCodeRepository promotionMappingRepository) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.promotionCodeRepository = promotionMappingRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * #createPromotionCode(org.mifosplatform.infrastructure.core.api.JsonCommand
	 * )
	 */
	@Transactional
	@Override
	public CommandProcessingResult createPromotionCode(JsonCommand command) {

		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			PromotionCodeMaster promotioncode = PromotionCodeMaster.fromJson(command);
			this.promotionCodeRepository.save(promotioncode);
			return new CommandProcessingResult(promotioncode.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("promotioncode")) {
			final String name = command.stringValueOfParameterNamed("promotionCode");
			throw new PlatformDataIntegrityException(
		             "error.msg.promotionCode.duplicate.name",
					"A promotion code with'" + name + "'already exists",
					name, name);
		}
		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException(
				"error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "
						+ realCause.getMessage());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #updatePromotionCode(java.lang.Long,
	 * org.mifosplatform.infrastructure.core.api.JsonCommand)
	 */
	@Transactional
	@Override
	public CommandProcessingResult updatePromotionCode(final Long id,final JsonCommand command) {

		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			PromotionCodeMaster promotionCode = PromotionCodeRetrieveById(id);
			final Map<String, Object> changes = promotionCode.updatePromotion(command);
			if (!changes.isEmpty()) {
				this.promotionCodeRepository.saveAndFlush(promotionCode);
			}
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					    .withEntityId(promotionCode.getId()).with(changes).build();

		} catch (DataIntegrityViolationException dve) {

			if (dve.getCause() instanceof ConstraintViolationException) {
				handleCodeDataIntegrityIssues(command, dve);
			}
			return new CommandProcessingResult(Long.valueOf(-1L));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #deletePromotionCode(java.lang.Long)
	 */
	@Transactional
	@Override
	public CommandProcessingResult deletePromotionCode(Long id) {

		try {
			this.context.authenticatedUser();
			PromotionCodeMaster promotionCode = this.PromotionCodeRetrieveById(id);
			promotionCode.delete();
			this.promotionCodeRepository.save(promotionCode);
			return new CommandProcessingResult(id);

		} catch (Exception exception) {
			return null;
		}
	}

	private PromotionCodeMaster PromotionCodeRetrieveById(Long id) {

		PromotionCodeMaster promotionCode = this.promotionCodeRepository.findOne(id);
		if (promotionCode == null) {
			throw new PromotionCodeNotFoundException(id.toString());
		}
		return promotionCode;
	}
}
