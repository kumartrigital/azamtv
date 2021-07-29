package org.mifosplatform.organisation.voucher.data;

/**
 * The class <code>VoucherpinTypeData</code> is a Bean class,
 * contains only getter and setter methods to store and retrieve data.
 *
 *  @author ashokreddy
 */
public class VoucherpinTypeData {

	/** The pinNo is used for String storage. */
	private String pinNo;

	public VoucherpinTypeData(final String pinNo){
		this.pinNo=pinNo;
	}

	public String getPinNo() {
		return pinNo;
	}
	
	
}
