package org.mifosplatform.logistics.item.domain;
public enum UnitEnumType {

		METERS(1, "CategoryType.meters"), //
		PIECES(2, "CategoryType.pieces"),
        HOURS(3, "CategoryType.hours"),
        DAYS(4, "CategoryType.days"),
        ACCESSORIES(5, "CategoryType.accessories"),
		INVALID(4, "CategoryType.invalid");


	    private final Integer value;
		private final String code;

	    private UnitEnumType(final Integer value, final String code) {
	        this.value = value;
			this.code = code;
	    }

	    public Integer getValue() {
	        return this.value;
	    }

		public String getCode() {
			return code;
		}

		public static UnitEnumType fromInt(final Integer frequency) {

			UnitEnumType enumType = UnitEnumType.INVALID;
			switch (frequency) {
			case 1:
				enumType = UnitEnumType.METERS;
				break;
			case 2:
				enumType = UnitEnumType.PIECES;
				break;

			case 3:
				enumType = UnitEnumType.HOURS;
				break;
				
			case 4:
				enumType = UnitEnumType.DAYS;
				break;
				
			case 5:
				enumType = UnitEnumType.ACCESSORIES;
				break;
				
			default:
				enumType = UnitEnumType.INVALID;
				break;
			}
			return enumType;
		}
	}



