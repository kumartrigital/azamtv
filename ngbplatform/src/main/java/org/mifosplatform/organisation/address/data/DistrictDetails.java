package org.mifosplatform.organisation.address.data;

public class DistrictDetails {

	private final Long id;
	private final String districtName;

	public DistrictDetails(final Long id, final String districtName) {

	    this.id=id;
	    this.districtName=districtName;
	
	}

	public Long getId() {
		return id;
	}

	public String getDistrictName() {
		return districtName;
	}
}
