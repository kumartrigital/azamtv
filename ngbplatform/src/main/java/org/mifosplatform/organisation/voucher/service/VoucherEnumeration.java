package org.mifosplatform.organisation.voucher.service;


import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.voucher.domain.VoucherPinCategory;

/**
 * 
 * @author ashokreddy
 *
 */
public class VoucherEnumeration {
	
	private final static String CODEPREFIX = "deposit.interest.compounding.period.";
	
	
	public static EnumOptionData enumOptionData(final int id) {
		return enumOptionData(VoucherPinCategory.fromInt(id));
	}

	public static EnumOptionData enumOptionData(VoucherPinCategory pin) {
		
		
		EnumOptionData optionData;
		
		switch (pin) {
		case NUMERIC:
			optionData = new EnumOptionData(VoucherPinCategory.NUMERIC.getValue().longValue(), CODEPREFIX + VoucherPinCategory.NUMERIC.getCode(), "NUMERIC");
			break;
		case ALPHA:
			optionData = new EnumOptionData(VoucherPinCategory.ALPHA.getValue().longValue(), CODEPREFIX + VoucherPinCategory.ALPHA.getCode(), "ALPHA");
			break;
		case ALPHANUMERIC:
			optionData = new EnumOptionData(VoucherPinCategory.ALPHANUMERIC.getValue().longValue(), CODEPREFIX + VoucherPinCategory.ALPHANUMERIC.getCode(), "ALPHANUMERIC");
			break;
		default:
			optionData = new EnumOptionData(VoucherPinCategory.INVALID.getValue().longValue(), VoucherPinCategory.INVALID.getCode(), "INVALID");
			break;
		}
		return optionData;
	
	}

}
