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
public class VoucherIsNotProductTypeException extends AbstractPlatformDomainRuleException {

	/** Constructor with Default Message*/
	public VoucherIsNotProductTypeException() {
		super("error.msg.voucher.not.product type.exception", " VoucherPin is not a valid for topup  ", " VoucherPin requesting of type product ");
		// TODO Auto-generated constructor stub
	}
}
