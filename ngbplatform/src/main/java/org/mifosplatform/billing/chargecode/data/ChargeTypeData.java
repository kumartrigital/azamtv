package org.mifosplatform.billing.chargecode.data;

public class ChargeTypeData {

	private Long id;
	private String chargeType;

	public ChargeTypeData() {

	}

	public ChargeTypeData(final Long id, final String chargeType) {
		this.id = id;
		this.chargeType = chargeType;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getChargeType() {
		return this.chargeType;
	}

	public void setChargeType(final String chargeType) {
		this.chargeType = chargeType;
	}

}
