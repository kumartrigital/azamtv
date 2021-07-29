package org.mifosplatform.organisation.message.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.organisation.message.data.BillingMessageTemplateData;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author ashokreddy
 *
 */
@Service
public class BillingMessageDataWritePlatformServiceImpl implements BillingMessageDataWritePlatformService {

	private final BillingMesssageReadPlatformService billingMesssageReadPlatformService;
	private final TemplateService templateService;
	

	@Autowired
	public BillingMessageDataWritePlatformServiceImpl(final BillingMesssageReadPlatformService billingMesssageReadPlatformService,
			final TemplateService templateService) {
	
		this.billingMesssageReadPlatformService = billingMesssageReadPlatformService;
		this.templateService = templateService;
		
	}

	@Override
	public CommandProcessingResult createMessageData(Long id, String json) {

		final BillingMessageTemplateData templateData = this.billingMesssageReadPlatformService.retrieveMessageTemplate(id);
		
		final List<BillingMessageTemplateData> messageparam = this.billingMesssageReadPlatformService.retrieveMessageParams(id);
		
		final List<BillingMessageTemplateData> clientData = this.billingMesssageReadPlatformService.retrieveData(id, json, 
				templateData, messageparam, billingMesssageReadPlatformService);

		return new CommandProcessingResultBuilder().withEntityId(id).build();

	}

	@Override
	public CommandProcessingResult createMessageTemplate(final Long messageId,final String query) {
		try {
			final Template template = this.templateService.findOneById(messageId);
		    this.billingMesssageReadPlatformService.retrieveMessageQuery(query,template);
			return new CommandProcessingResultBuilder().withEntityId(messageId).build();
		} catch (Exception e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
}