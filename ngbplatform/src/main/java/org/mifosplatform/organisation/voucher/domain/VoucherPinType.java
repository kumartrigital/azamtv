package org.mifosplatform.organisation.voucher.domain;

/**
 * 
 * @author ashokreddy
 *
 */

public enum VoucherPinType {

	   VALUE(1, "PinType.value"), //
	   DURATION(2, "PinType.duration"),//
	   //PRODUCTION(3,"PinType.production"),//
	   INVALID(3, "PinType.invalid");

	    private final Integer value;
		private final String code;

	    private VoucherPinType(final Integer value, final String code) {
	        this.value = value;
			this.code = code;
	    }

	    public Integer getValue() {
	        return this.value;
	    }

		public String getCode() {
			return code;
		}

		public static VoucherPinType fromInt(final Integer type) {
			
			VoucherPinType pinType;
			
			switch (type) {
			case 1:
				pinType = VoucherPinType.VALUE;
				break;
			case 2:
				pinType = VoucherPinType.DURATION;
				break;
			/*case 3:
				pinType = PinType.PRODUCTION;
				break;
*/
			default:
				pinType = VoucherPinType.INVALID;
				break;
			}
			return pinType;
		}
	
}
