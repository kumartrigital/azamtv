package org.mifosplatform.logistics.mrn.data;

import org.joda.time.LocalDate;

public class InventoryTransactionHistoryData {
	
	private final LocalDate transactionDate;
	private final String serialNumber;
	private final String itemDescription;
	private final String fromOffice;
	private final String toOffice;
	private final String refType;
	private final String movement;
	
	public InventoryTransactionHistoryData(final LocalDate transactionDate,final String itemDescription, 
			final String fromOffice, final String toOffice, final String serialNumber, final String refType, final String movement){
		
		this.transactionDate = transactionDate;
		this.itemDescription = itemDescription;
		this.fromOffice = fromOffice;
		this.toOffice = toOffice;
		this.serialNumber = serialNumber;
		this.refType = refType;
		this.movement = movement;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}
	
	public String getItemDescription() {
		return itemDescription;
	}



	public String getFromOffice() {
		return fromOffice;
	}

	

	public String getToOffice() {
		return toOffice;
	}



	public String getSerialNumber() {
		return serialNumber;
	}

	

	public String getRefType() {
		return refType;
	}

	

	public String getMovement() {
		return movement;
	}


	
}
