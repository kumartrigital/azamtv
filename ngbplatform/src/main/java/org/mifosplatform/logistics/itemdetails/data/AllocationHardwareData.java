package org.mifosplatform.logistics.itemdetails.data;

import java.util.List;

public class AllocationHardwareData {

	private List<ItemSerialNumberData> serialNumbers;
	private Long quantity;
	private Long itemMasterId;
	private Long itemDetailsId;
	private Long clientId;
	private Long saleId;
	private Long allocationId;
	private String quality;
	private String serialNumber;
	
	public AllocationHardwareData() {}
	
	public AllocationHardwareData(List<ItemSerialNumberData> serialNumbers,Long quantity,Long itemMasterId){
		this.serialNumbers = serialNumbers;
		this.quantity = quantity;
		this.itemMasterId = itemMasterId;
	}
	
	
	public AllocationHardwareData(Long id, Long clientId, Long saleId,Long allocationId) {
             
		    this.itemDetailsId=id;
		    this.clientId=clientId;
		    this.saleId=saleId;
		    this.allocationId=allocationId;
	
	}
	public AllocationHardwareData(Long id, Long clientId, Long saleId,Long allocationId,String quality) {
        
	    this.itemDetailsId=id;
	    this.clientId=clientId;
	    this.saleId=saleId;
	    this.allocationId=allocationId;
	    this.setQuality(quality);

}

	public AllocationHardwareData(Long id, Long clientId, String quality,String serialNo) {
		this.itemDetailsId = id;
		this.clientId = clientId;
		this.quality = quality;
		this.serialNumber=serialNo;
	}

	public List<ItemSerialNumberData> getSrialNumbers() {
		return serialNumbers;
	}
	public void setSrialNumbers(
			List<ItemSerialNumberData> srialNumbers) {
		this.serialNumbers = srialNumbers;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	public Long getItemMasterId() {
		return itemMasterId;
	}
	public void setItemMasterId(Long itemMasterId) {
		this.itemMasterId = itemMasterId;
	}

	public List<ItemSerialNumberData> getSerialNumbers() {
		return serialNumbers;
	}

	public Long getItemDetailsId() {
		return itemDetailsId;
	}

	public Long getClientId() {
		return clientId;
	}

	public Long getSaleId() {
		return saleId;
	}

	public Long getAllocationId() {
		return allocationId;
	}
	
	

	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @return the quality
	 */
	public String getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(String quality) {
		this.quality = quality;
	}
	
	
}
