package org.mifosplatform.portfolio.client.data;


public class BillSupressionData {
	
	private Long id;
	private Long minBillValue;
	private Long noOfCycleSuppress;
	
	public BillSupressionData(final Long id, final Long minBillValue, final Long noOfCycleSuppress) {
		
		this.id = id;
		this.minBillValue = minBillValue;
		this.noOfCycleSuppress = noOfCycleSuppress;
	
	}

	public Long getId() {
		return id;
	}

	public Long getMinBillValue() {
		return minBillValue;
	}

	public Long getNoOfCycleSuppress() {
		return noOfCycleSuppress;
	}
	
	

}
