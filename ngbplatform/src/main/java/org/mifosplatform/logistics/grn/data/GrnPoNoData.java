package org.mifosplatform.logistics.grn.data;

import java.util.List;

public class GrnPoNoData {
	
	List<String> purchaseNumbers;
	public GrnPoNoData()
	{
		
	}
	public GrnPoNoData(List<String> purchaseNumbers) {
		super();
		this.purchaseNumbers = purchaseNumbers;
	}
	public List<String> getPurchaseNumbers() {
		return purchaseNumbers;
	}
	public void setPurchaseNumbers(List<String> purchaseNumbers) {
		this.purchaseNumbers = purchaseNumbers;
	}
	

}
