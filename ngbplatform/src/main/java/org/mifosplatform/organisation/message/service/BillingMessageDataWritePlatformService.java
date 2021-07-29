package org.mifosplatform.organisation.message.service;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

/**
 * 
 * @author ashokreddy
 *
 */
public interface BillingMessageDataWritePlatformService {

	/** 
	 * 
	 * @param command
	 * @param json
	 * @return
	 */
	CommandProcessingResult createMessageData(final Long command, final String json);

	CommandProcessingResult createMessageTemplate(final Long messageId, final String query);
	
	
}
