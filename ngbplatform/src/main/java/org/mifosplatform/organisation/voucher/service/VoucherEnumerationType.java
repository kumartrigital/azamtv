package org.mifosplatform.organisation.voucher.service;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.voucher.domain.VoucherPinType;

/**
 * 
 * @author ashokreddy
 *
 */
public class VoucherEnumerationType {

	final static String CODEPREFIX = "deposit.interest.compounding.period.";
	
	public static EnumOptionData enumOptionData(final int id) {
		return enumOptionData(VoucherPinType.fromInt(id));
	}

	public static EnumOptionData enumOptionData(VoucherPinType pin) {
		
		
		EnumOptionData optionData;
		switch (pin) {
		case VALUE:
			optionData = new EnumOptionData(VoucherPinType.VALUE.getValue().longValue(), CODEPREFIX + VoucherPinType.VALUE.getCode(), "VALUE");
			break;
		case DURATION:
			optionData = new EnumOptionData(VoucherPinType.DURATION.getValue().longValue(), CODEPREFIX + VoucherPinType.DURATION.getCode(), "DURATION");
			break;
		/*case PRODUCTION:
			optionData = new EnumOptionData(PinType.PRODUCTION.getValue().longValue(), codePrefix + PinType.PRODUCTION.getCode(), "PRODUCTION");
			break;*/
		default:
			optionData = new EnumOptionData(VoucherPinType.INVALID.getValue().longValue(), VoucherPinType.INVALID.getCode(), "INVALID");
			break;
		}
		return optionData;
	
	}
	
}
