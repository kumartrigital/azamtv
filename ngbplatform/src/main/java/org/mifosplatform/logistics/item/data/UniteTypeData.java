package org.mifosplatform.logistics.item.data;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.logistics.item.domain.UnitEnumType;

public class UniteTypeData {

	public static EnumOptionData UnitClassType(final int id) {
		return UnitClassType(UnitEnumType.fromInt(id));
	}

	public static EnumOptionData UnitClassType(final UnitEnumType type) {
		final String codePrefix = "deposit.interest.compounding.period.";
		EnumOptionData optionData = null;
		switch (type) {
		case METERS:
			optionData = new EnumOptionData(UnitEnumType.METERS.getValue().longValue(), codePrefix + UnitEnumType.METERS.getCode(), "METERS");
			break;
			
		case PIECES:
			optionData = new EnumOptionData(UnitEnumType.PIECES.getValue().longValue(), codePrefix + UnitEnumType.PIECES.getCode(), "PIECES");
			break;

		case HOURS:
			optionData = new EnumOptionData(UnitEnumType.HOURS.getValue().longValue(), codePrefix + UnitEnumType.HOURS.getCode(), "HOURS");
			break;
		
		case DAYS:
			optionData = new EnumOptionData(UnitEnumType.DAYS.getValue().longValue(), codePrefix + UnitEnumType.DAYS.getCode(), "DAYS");
			break;
			
		case ACCESSORIES:
			optionData = new EnumOptionData(UnitEnumType.ACCESSORIES.getValue().longValue(), codePrefix + UnitEnumType.ACCESSORIES.getCode(), "ACCESSORIES");
			break;
				
		default:
			optionData = new EnumOptionData(UnitEnumType.INVALID.getValue().longValue(), UnitEnumType.INVALID.getCode(), "INVALID");
			break;
		}
		return optionData;
	}

}
