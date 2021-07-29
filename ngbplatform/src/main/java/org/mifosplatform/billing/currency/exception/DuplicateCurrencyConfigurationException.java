package org.mifosplatform.billing.currency.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * @author hugo
 * 
 *         this class {@link RuntimeException} thrown when a code is not found.
 */
public class DuplicateCurrencyConfigurationException extends
		AbstractPlatformDomainRuleException {

	private static final long serialVersionUID = 1L;

	public DuplicateCurrencyConfigurationException(final String country) {
		super("currency.is.already.configured.with.this.country",
				"Currency is already cinfigured with " + country, country);
	}

	public DuplicateCurrencyConfigurationException(Long currencyConfigId) {
		super(
				"error.msg.countryCurrency.not.found",
				"countryCurrency with this id" + currencyConfigId + "not exist",
				currencyConfigId);
	}

}
