package org.mifosplatform.organisation.voucher.exception;

import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;

/**
 * Throw AlreadyProcessedException Exception, 
 * if you are trying to generate VoucherPins on 
 * already Processing Voucher Group/Batch.
 * The Voucher Group/Batch is Processed only once. 
 * @author ashokreddy
 */
@SuppressWarnings("serial")
public class AlreadyProcessedException extends AbstractPlatformDomainRuleException {

	/** Constructor with Default Message*/
	public AlreadyProcessedException() {
		super("error.msg.voucher.no.isprocessed.exception", " VoucherPin is already generated with this batchName   ", " VoucherPin is already generated with this batchName ");
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param msg
	 * 			use this value in the creation of Throw Exception Message
	 */
	public AlreadyProcessedException(final String msg) {
		super("error.msg.voucher.no.isprocessed.exception", " VoucherPin is already generated with this batchName   ", msg);
		// TODO Auto-generated constructor stub
	}

	public AlreadyProcessedException(final String msg,final Long priceId) {
		// TODO Auto-generated constructor stub
		super("error.msg.voucher.no.already.existed.with.thiscontract", " VoucherPins are already generated with this contract '"+msg+"'", msg);
	}

}
