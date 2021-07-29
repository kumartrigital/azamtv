package org.mifosplatform.billing.chargecode.data;

public class ChargesData {

	private final Long id;
	private final String chargeCode;
	private final String chargeDescription;

	public ChargesData(final Long id, final String chargeCode,
			final String chargeDescription) {

		this.id = id;
		this.chargeCode = chargeCode;
		this.chargeDescription = chargeDescription;
		
	}

	public ChargesData(final Long id, final String chargeCode) {
		this.chargeDescription = null;
		this.id = id;
		this.chargeCode = chargeCode;
	}

	public Long getId() {
		return id;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public String getChargeDescription() {
		return chargeDescription;
	}
}
