

package org.mifosplatform.infrastructure.configuration.exception;

/**
 * {@link RuntimeException} thrown when an invalid tenant identifier is used in
 * request to platform.
 * 
 * @see CustomRequestHeaderAuthenticationFilter
 */
@SuppressWarnings("serial")
public class LicenseKeyNotFoundException extends RuntimeException {

    public LicenseKeyNotFoundException(final String message) {
    	 super(message);
    }
}