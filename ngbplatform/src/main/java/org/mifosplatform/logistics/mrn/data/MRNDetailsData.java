package org.mifosplatform.logistics.mrn.data;


import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.logistics.grv.data.GRVData;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.office.data.OfficeData;

public class MRNDetailsData {

	private String id;
	private LocalDate requestedDate;
	private String fromOffice;
	private String toOffice;
	private Long orderdQuantity;
	private Long receivedQuantity;
	private String status;
	private Long officeId;
	private Long parentId;
	private String officeName;
	private Collection<OfficeData> officeData;
	private Collection<ItemData> itemMasterData;
	private Long itemId;
	private String itemCode;
	private String itemDescription;
	private Collection<MRNDetailsData> mrnIds;
	private List<MRNDetailsData> itemsaleIds;
	private Long mrnId;
	private List<String> serialNumber;
	private List<String> cartoonNumber;
	private Long fromOfficeNum;
	private Long toOfficeNum;
	private String itemClass;
	private BigDecimal chargeAmount;
	private BigDecimal unitPrice;
	private Long exportedQuantity;
	private Long redeemedQuantity;
	private Long allocatedQuantity;
	//private String notes;
	
	List<GRVData> grvIds;
	
	public MRNDetailsData(final String id, final LocalDate requestedDate, final String fromOffice, final String toOffice, final Long orderdQuantity, 
			        final Long receivedQuantity, final String status, final String itemDescription/*,final String notes*/){
		this.id = id;
		this.requestedDate = requestedDate;
		this.fromOffice = fromOffice;
		this.toOffice = toOffice;
		this.orderdQuantity = orderdQuantity;
		this.receivedQuantity = receivedQuantity;
		this.status = status;
		this.itemDescription = itemDescription;
		/*this.notes = notes;*/
		
	}
	public MRNDetailsData(final String id, final LocalDate requestedDate, final String fromOffice, final String toOffice, final Long orderdQuantity, 
	        final Long receivedQuantity, final String status, final String itemDescription,final BigDecimal amount){
this.id = id;
this.requestedDate = requestedDate;
this.fromOffice = fromOffice;
this.toOffice = toOffice;
this.orderdQuantity = orderdQuantity;
this.receivedQuantity = receivedQuantity;
this.status = status;
this.itemDescription = itemDescription;
this.chargeAmount = amount;

}
	

	public MRNDetailsData(final String id, final LocalDate requestedDate, final String fromOffice, final String toOffice, final Long orderdQuantity, 
	        final Long receivedQuantity, final String status, final String itemDescription,final BigDecimal chargeAmount,final BigDecimal unitPrice){
this.id = id;
this.requestedDate = requestedDate;
this.fromOffice = fromOffice;
this.toOffice = toOffice;
this.orderdQuantity = orderdQuantity;
this.receivedQuantity = receivedQuantity;
this.status = status;
this.itemDescription = itemDescription;
this.chargeAmount = chargeAmount;
this.unitPrice = unitPrice;
this.chargeAmount = chargeAmount;

}
	public MRNDetailsData(final String id, final LocalDate requestedDate, final String fromOffice, final String toOffice, final Long orderdQuantity, 
	        final Long receivedQuantity, final String status, final String itemDescription,
	        final BigDecimal chargeAmount,final BigDecimal unitPrice,final Long exportedQuantity,final Long redeemedQuantity,final Long allocatedQuantity){
this.id = id;
this.requestedDate = requestedDate;
this.fromOffice = fromOffice;
this.toOffice = toOffice;
this.orderdQuantity = orderdQuantity;
this.receivedQuantity = receivedQuantity;
this.status = status;
this.itemDescription = itemDescription;
this.chargeAmount = chargeAmount;
this.unitPrice = unitPrice;
this.exportedQuantity = exportedQuantity;
this.redeemedQuantity = redeemedQuantity;
this.allocatedQuantity = allocatedQuantity;
/*this.notes = notes;*/

}
	public MRNDetailsData(Collection<OfficeData> officeData, Collection<ItemData> itemMasterData) {
		this.officeData = officeData;
		this.itemMasterData = itemMasterData;
	}
	
	public MRNDetailsData(Collection<MRNDetailsData> mrnIds,List<MRNDetailsData> itemsaleIds,List<GRVData> grvIds) {
	this.mrnIds = mrnIds;
	this.itemsaleIds=itemsaleIds;
	this.grvIds = grvIds;
	}
	
	public MRNDetailsData(Long mrnId,String itemDescription, Long itemId,String itemsaleId) {
		
		this.id=itemsaleId;
		this.mrnId = mrnId;
		this.itemDescription = itemDescription;
		this.itemId = itemId;
	}

	public Long getExportedQuantity() {
		return exportedQuantity;
	}

	public void setExportedQuantity(Long exportedQuantity) {
		this.exportedQuantity = exportedQuantity;
	}

	public Long getRedeemedQuantity() {
		return redeemedQuantity;
	}

	public void setRedeemedQuantity(Long redeemedQuantity) {
		this.redeemedQuantity = redeemedQuantity;
	}

	public Long getAllocatedQuantity() {
		return allocatedQuantity;
	}

	public void setAllocatedQuantity(Long allocatedQuantity) {
		this.allocatedQuantity = allocatedQuantity;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public MRNDetailsData(List<String> serialNumber) {
		this.serialNumber = serialNumber;
	}
	

	public MRNDetailsData(List<String> cartoonNumber, Object object) {
		this.cartoonNumber = cartoonNumber;
	}
	
	/*public MRNDetailsData(List<String> cartoonNumber) {
		this.cartoonNumber = cartoonNumber;
	}*/
	

	public MRNDetailsData(Long agentId) {
		
			this.officeId=agentId;
	}

	public LocalDate getRequestedDate() {
		return requestedDate;
	}

	public void setRequestedDate(LocalDate requestedDate) {
		this.requestedDate = requestedDate;
	}

	public String getFromOffice() {
		return fromOffice;
	}

	public void setFromOffice(String fromOffice) {
		this.fromOffice = fromOffice;
	}

	public String getToOffice() {
		return toOffice;
	}

	public void setToOffice(String toOffice) {
		this.toOffice = toOffice;
	}

	public Long getOrderdQuantity() {
		return orderdQuantity;
	}

	public void setOrderdQuantity(Long orderdQuantity) {
		this.orderdQuantity = orderdQuantity;
	}

	public Long getReceivedQuantity() {
		return receivedQuantity;
	}

	public void setReceivedQuantity(Long receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public Collection<OfficeData> getOfficeData() {
		return officeData;
	}

	public void setOfficeData(Collection<OfficeData> officeData) {
		this.officeData = officeData;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public Collection<ItemData> getItemMasterData() {
		return itemMasterData;
	}

	
	public Collection<MRNDetailsData> getMrnIds() {
		return mrnIds;
	}

	public void setMrnIds(Collection<MRNDetailsData> mrnIds) {
		this.mrnIds = mrnIds;
	}

	public Long getMrnId() {
		return mrnId;
	}

	public void setMrnId(Long mrnId) {
		this.mrnId = mrnId;
	}

	public List<String> getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(List<String> serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Long getFromOfficeNum() {
		return fromOfficeNum;
	}

	public void setFromOfficeNum(Long fromOfficeNum) {
		this.fromOfficeNum = fromOfficeNum;
	}

	public Long getToOfficeNum() {
		return toOfficeNum;
	}

	public void setToOfficeNum(Long toOfficeNum) {
		this.toOfficeNum = toOfficeNum;
	} 
	
	public List<String> getCartoonNumber() {
		return cartoonNumber;
	}

	public void setCartoonNumber(List<String> cartoonNumber) {
		this.cartoonNumber = cartoonNumber;
	}

	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}
	
	public String getItemClass() {
		return itemClass;
	}
	
	
}
