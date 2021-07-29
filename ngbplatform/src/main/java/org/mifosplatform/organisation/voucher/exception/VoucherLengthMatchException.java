package org.mifosplatform.organisation.voucher.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * Throw BeginWithLengthProcessingException Exception, 
 * if Voucher's table "length" field value
 * and "beginWith" field CharacterString length Both
 * are contain same value then this 
 * Exception should be Throw internally 
 * 
 * @author ashokreddy
 */

@SuppressWarnings("serial")
public class VoucherLengthMatchException extends AbstractPlatformDomainRuleException {

	/** Constructor with Default Message*/
	public VoucherLengthMatchException() {
		super("error.msg.voucher.no.length.match.exception", " VoucherPin's Length and BeginWith Contain same Length ", " VoucherPin's Length and BeginWith Contain same Length ");
		// TODO Auto-generated constructor stub
	}
}
