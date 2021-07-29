package org.mifosplatform.organisation.message.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;

/**
 * 
 * @author ashokreddy
 *
 */
public interface BillingMessageTemplateWritePlatformService {
	
CommandProcessingResult addMessageTemplate(final JsonCommand json);

CommandProcessingResult updateMessageTemplate(final JsonCommand command);

CommandProcessingResult deleteMessageTemplate(final JsonCommand command);

void processEmailNotification(Long clientId, Long orderId, String exceptionReason, String messageTemplateName);

BillingMessageTemplate getMessageTemplate(String messageTemplateName);

}
