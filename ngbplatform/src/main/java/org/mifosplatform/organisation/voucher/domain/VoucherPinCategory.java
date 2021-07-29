package org.mifosplatform.organisation.voucher.domain;

/**
 * 
 * @author ashokreddy
 *
 */
public enum VoucherPinCategory {

	   NUMERIC(1, "PinCategory.numeric"), //
	   ALPHA(2, "PinCategory.alpha"),//
	   ALPHANUMERIC(3,"PinCategory.alphanumeric"),//
	   INVALID(4, "CategoryType.invalid");

	    private final Integer value;
		private final String code;
		private static VoucherPinCategory pinCategory;

	    private VoucherPinCategory(final Integer value, final String code) {
	        this.value = value;
			this.code = code;
	    }

	    public Integer getValue() {
	        return this.value;
	    }

		public String getCode() {
			return code;
		}

		public static VoucherPinCategory fromInt(final Integer category) {

			switch (category) {
			case 1:
				pinCategory = VoucherPinCategory.NUMERIC;
				break;
			case 2:
				pinCategory = VoucherPinCategory.ALPHA;
				break;
			case 3:
				pinCategory = VoucherPinCategory.ALPHANUMERIC;
				break;

			default:
				pinCategory = VoucherPinCategory.INVALID;
				break;
			}
			return pinCategory;
		}
	}
