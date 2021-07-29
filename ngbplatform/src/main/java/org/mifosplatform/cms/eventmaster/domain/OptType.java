/**
 * 
 */
package org.mifosplatform.cms.eventmaster.domain;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

/**
 * {@link EnumOptionData} for Opt Type
 * 
 * @author pavani
 * @author Rakesh
 */
public enum OptType {

	RENT(1, "Category.Rent"),
	OWN(2, "Category.own"),
	INVALID(3, "category.invalid");
	
	/** The Value is used to store code number*/
	private final Integer value;
	
	/** The code is used to store code */
	private final String code;
	
	/**
	 * @param value
	 * @param code
	 * */
	private OptType(final Integer value, final String code) {
		this.value = value;
		this.code = code;
	}

	/**
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * method to converts value to code
	 * @param frequency {@code Integer}
	 * return optType {@code OptType}
	 * 
	 * */
	public static OptType fromInt(final Integer frequency) {
		
		OptType optType = OptType.INVALID;
		switch(frequency) {
		case 1:
			optType = OptType.RENT;
			break;
		case 2:
			optType = OptType.OWN;
			break;
		default:
			optType = OptType.INVALID;
			break;
		}
		return optType;
	}
}
