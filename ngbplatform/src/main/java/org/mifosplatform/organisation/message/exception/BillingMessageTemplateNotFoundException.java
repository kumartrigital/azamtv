package org.mifosplatform.organisation.message.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a template is not found.
 */
@SuppressWarnings("serial")
public class BillingMessageTemplateNotFoundException extends AbstractPlatformResourceNotFoundException {

	 public BillingMessageTemplateNotFoundException(final String templateName) {
	 super("error.msg.template.not.found", "message template with this name" + templateName + "not exist", templateName);

	 }
}

