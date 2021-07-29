package org.mifosplatform.organisation.hardwareplanmapping.data;

public class HardwareMappingDetailsData {

	private final Long planId;
	private final Long orderId;
	private final String planCode;

	public HardwareMappingDetailsData(final Long planId, 
			final Long orderId, final String planCode) {

		this.planId = planId;
		this.planCode = planCode;
		this.orderId = orderId;
	}

	public Long getPlanId() {
		return planId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getPlanCode() {
		return planCode;
	}

}
