package org.mifosplatform.inview.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class PaywizardFailureException extends AbstractPlatformResourceNotFoundException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PaywizardFailureException() {
        super("error.msg.paywizard.failure.exception", "Paywizard failure exception", "");
    } 

}
