package org.mifosplatform.organisation.address.data;

public class StateDetails  {
	
	private final Long id;
	private final String stateName;

	public StateDetails(final Long id, final String stateName) {

	    this.id=id;
	    this.stateName=stateName;
	
	}

	public Long getId() {
		return id;
	}

	public String getStateName() {
		return stateName;
	}

}
