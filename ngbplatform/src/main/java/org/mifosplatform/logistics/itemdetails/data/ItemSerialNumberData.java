package org.mifosplatform.logistics.itemdetails.data;

import java.util.List;


public class ItemSerialNumberData {

	private String serialNumber;

	private List<String> serialNumbers;
	private Long quantity;
	private Long itemMasterId;
	
	public ItemSerialNumberData(List<String> serials,Long quantity,Long itemMasterId){
		this.serialNumbers = serials;
		this.quantity = quantity;
		this.itemMasterId = itemMasterId;
	}
	
	
	public String getSerialNumbers() {
		return serialNumber;
	}

	public void setSerialNumbers(String serialNumbers) {
		this.serialNumber = serialNumbers;
	}

		
	public ItemSerialNumberData() {
	}
	
	public ItemSerialNumberData(final String serialNumbers){
		this.serialNumber = serialNumbers;
	}


	public ItemSerialNumberData(List<String> itemSerialNumbers) {
		this.serialNumbers = itemSerialNumbers;
	}	
}
