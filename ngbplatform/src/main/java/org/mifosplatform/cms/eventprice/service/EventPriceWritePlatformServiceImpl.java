/**
 * 
 */
package org.mifosplatform.cms.eventprice.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.billing.planprice.exceptions.DuplicatEventPrice;
import org.mifosplatform.cms.eventmaster.domain.EventMaster;
import org.mifosplatform.cms.eventmaster.domain.EventMasterRepository;
import org.mifosplatform.cms.eventprice.data.EventPriceData;
import org.mifosplatform.cms.eventprice.domain.EventPrice;
import org.mifosplatform.cms.eventprice.domain.EventPriceRepository;
import org.mifosplatform.cms.eventprice.serialization.EventPriceFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.updatecomparing.UpdateCompareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link Service} Class for {@link EventPricing} Write Service implements
 * {@link EventPriceWritePlatformService}
 * 
 * @author pavani
 *
 */
@Service
public class EventPriceWritePlatformServiceImpl implements EventPriceWritePlatformService {

	private final PlatformSecurityContext context;
	private final EventPriceFromApiJsonDeserializer apiJsonDeserializer;
	private final EventPriceRepository eventPricingRepository;
	private final EventPriceReadPlatformService eventPricingReadPlatformService;
	private final EventMasterRepository eventMasterRepository;
	final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
	final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
			.resource("eventPricing");

	@Autowired
	public EventPriceWritePlatformServiceImpl(final PlatformSecurityContext context,
			final EventPriceFromApiJsonDeserializer apiJsonDeserializer,
			final EventPriceRepository eventPricingRepository, final EventMasterRepository eventMasterRepository,
			final EventPriceReadPlatformService eventPricingReadPlatformService) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.eventPricingRepository = eventPricingRepository;
		this.eventPricingReadPlatformService = eventPricingReadPlatformService;
		this.eventMasterRepository = eventMasterRepository;

	}

	@Transactional
	@Override
	public CommandProcessingResult createEventPrice(final JsonCommand command) {
		try {
			// this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final Long eventId = command.longValueOfParameterNamed("eventId");
			final EventMaster eventMaster = this.eventMasterRepository.findOne(eventId);
			final EventPrice eventPricing = EventPrice.fromJson(command, eventMaster);
			final List<EventPriceData> eventDetails = this.eventPricingReadPlatformService
					.retrieventPriceData(command.entityId());
			for (final EventPriceData eventDetail : eventDetails) {

				if (eventPricing.getFormatType().equalsIgnoreCase(eventDetail.getFormatType())
						&& eventPricing.getClientType() == eventDetail.getClientType()
						&& eventPricing.getOptType().equalsIgnoreCase(eventDetail.getOptType())) {
					throw new DuplicatEventPrice(eventPricing.getFormatType());
				}
			}
			this.eventPricingRepository.save(eventPricing);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(eventPricing.getId()).build();
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	public CommandProcessingResult updateEventPrice(final JsonCommand command) {
		try {
			this.apiJsonDeserializer.validateForCreate(command.json());
			System.out.println("EventPriceWritePlatformServiceImpl.updateEventPrice()"+command.json().toString());
			System.out.println(command.entityId());
			EventPrice eventPrice = this.eventPricingRepository.findOne(command.entityId());
			final EventMaster eventMaster = eventPrice.getEventId();
			final EventPrice newEventPricing = EventPrice.fromJson(command, eventMaster);
			eventPrice = (EventPrice) UpdateCompareUtil.compare(eventPrice, newEventPricing);
			this.eventPricingRepository.save(eventPrice);
			return new CommandProcessingResultBuilder().withEntityId(eventPrice.getId())
					.withCommandId(command.commandId()).build();
		} catch (DataIntegrityViolationException dev) {
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult deleteEventPrice(final JsonCommand command) {
		try {
			final EventPrice eventPricing = this.eventPricingRepository.findOne(command.entityId());
			eventPricing.setIsDeleted('y');
			this.eventPricingRepository.save(eventPricing);
			return new CommandProcessingResultBuilder().withEntityId(eventPricing.getId()).build();
		} catch (DataIntegrityViolationException dev) {
			return CommandProcessingResult.empty();
		}
	}

	@SuppressWarnings("unused")
	private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
		throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
				"Event Price Already Exists.", dataValidationErrors);
	}
}
