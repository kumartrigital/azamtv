package org.mifosplatform.portfolio.order.domain;

public enum UserActionStatusTypeEnum {

	ACTIVATION(1, "CategoryType.active"), 
	DISCONNECTION(2, "CategoryType.disconnected"),
	RECONNECTION(3, "CategoryType.reconnection"), 
	MESSAGE(4,"CategoryType.message"), 
	RENEWAL_BEFORE_AUTOEXIPIRY(5,"CategoryType.renewal before autoexipiry"),
	RENEWAL_AFTER_AUTOEXIPIRY(6,"CategoryType.renewal after autoexipiry"),
	DEVICE_SWAP(7,"CategoryType.device swap"),
	CHANGE_PLAN(8, "CategoryType.change plan"),
	EXTENSION(9, "CategoryType.extension"),
	CHANGE_GROUP(10,"CategoryType.changegroup"),
	TERMINATION(11,"CategoryType.termination"),
	RETRACK(12,"CategoryType.retrack"),
	SUSPENTATION(13,"CategoryType.suspension"),
	REACTIVATION(14,"CategoryType.reactive"),
	ADDON_ACTIVATION(15,"CategoryType.addon activation"), 
	ADDON_DISCONNECTION(16,"CategoryType.addon disconnected"),
	ADD_PLAN(18,"CategoryType.add_plan"),
	INVALID(17,"CategoryType.invalid"),
	MODIFY_SERVICE(19,"CategoryType.modify");

	private final Integer value;
	private final String code;

	private UserActionStatusTypeEnum(final Integer value, final String code) {
		this.value = value;
		this.code = code;
	}

	public Integer getValue() {
		return this.value;
	}

	public String getCode() {
		return code;
	}

	public static UserActionStatusTypeEnum fromInt(final Integer frequency) {

		UserActionStatusTypeEnum actionStatusTypeEnum = UserActionStatusTypeEnum.INVALID;
		switch (frequency) {
		case 1:
			actionStatusTypeEnum = UserActionStatusTypeEnum.ACTIVATION;
			break;
		case 2:
			actionStatusTypeEnum = UserActionStatusTypeEnum.DISCONNECTION;
			break;

		case 3:
			actionStatusTypeEnum = UserActionStatusTypeEnum.RECONNECTION;
			break;

		case 4:
			actionStatusTypeEnum = UserActionStatusTypeEnum.MESSAGE;

		case 5:
			actionStatusTypeEnum = UserActionStatusTypeEnum.RENEWAL_BEFORE_AUTOEXIPIRY;

		case 6:
			actionStatusTypeEnum = UserActionStatusTypeEnum.RENEWAL_AFTER_AUTOEXIPIRY;

		case 7:
			actionStatusTypeEnum = UserActionStatusTypeEnum.DEVICE_SWAP;

		case 8:
			actionStatusTypeEnum = UserActionStatusTypeEnum.CHANGE_PLAN;
		case 9:
			actionStatusTypeEnum = UserActionStatusTypeEnum.EXTENSION;

		case 10:
			actionStatusTypeEnum = UserActionStatusTypeEnum.CHANGE_GROUP;

		case 11:
			actionStatusTypeEnum = UserActionStatusTypeEnum.TERMINATION;

		case 12:
			actionStatusTypeEnum = UserActionStatusTypeEnum.RETRACK;

		case 13:
			actionStatusTypeEnum = UserActionStatusTypeEnum.SUSPENTATION;

		case 14:
			actionStatusTypeEnum = UserActionStatusTypeEnum.REACTIVATION;

		case 15:
			actionStatusTypeEnum = UserActionStatusTypeEnum.ADDON_ACTIVATION;

		case 16:
			actionStatusTypeEnum = UserActionStatusTypeEnum.ADDON_DISCONNECTION;

		case 18:
			actionStatusTypeEnum = UserActionStatusTypeEnum.ADD_PLAN;
		case 19:
			actionStatusTypeEnum = UserActionStatusTypeEnum.MODIFY_SERVICE;
		default:
			actionStatusTypeEnum = UserActionStatusTypeEnum.INVALID;
			break;
		}
		return actionStatusTypeEnum;
	}
}
