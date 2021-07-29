package org.mifosplatform.organisation.partneragreement.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

/**
 * A {@link RuntimeException} thrown when a code is not found.
 */
@SuppressWarnings("serial")
public class AgreementNotFoundException extends AbstractPlatformResourceNotFoundException {

	/**
	 * @param agreementId
	 */
	public AgreementNotFoundException(final Long agreementId) {
		super("error.msg.agreement.not.found", "Agreement with this id"+ agreementId + "not exist", agreementId);

	}

}