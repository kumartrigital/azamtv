package org.mifosplatform.logistics.itemdetails.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.supplier.data.SupplierData;
import org.mifosplatform.organisation.office.data.OfficeData;

public class InventoryGrnData {

	private Long id;
	private LocalDate purchaseDate;
	private Long supplierId;
	private Long itemMasterId;
	private Long orderdQuantity;
	private Long receivedQuantity;
	private Long createdById;
	private Date createdDate;
	private Date lastModifiedDate;
	private Long lastModifiedById;
	private Long testId;
	private Long balanceQuantity;
	private Long oficeId;
	
	private List<ItemData> itemData;
	private Collection<OfficeData> officeData;	
	private List<SupplierData> supplierData;
	private String itemDescription;
	private String supplierName;
	private String officeName;
	

	private String purchaseNo;
	private Long officeId;
	private Long availableQuantity;
	private String units;
	private String itemcode;
	private Long itemAmount;
	private Integer orderStatus;
	private Long itemType;
	

	public InventoryGrnData(){
		this.id=null;
		this.itemMasterId=null;
		this.orderdQuantity=null;
		this.purchaseDate= DateUtils.getLocalDateOfTenant();
		this.receivedQuantity=null;
		this.supplierId=null;
		
	}
	
	public InventoryGrnData(Long testId, final String itemDescription) {
		this.testId = testId;
		this.itemDescription = itemDescription;
	}
	
	public InventoryGrnData(Long id,LocalDate purchaseDate,Long supplierId,Long itemMasterId,Long orderedQuantity,Long receivedQuantity){
		this.id=id;
		this.itemMasterId=itemMasterId;
		this.orderdQuantity=orderedQuantity;
		this.purchaseDate=purchaseDate;
		this.receivedQuantity=receivedQuantity;
		this.supplierId=supplierId;		
		this.balanceQuantity = orderedQuantity-receivedQuantity;
	}
	
	public InventoryGrnData(Long id,LocalDate purchaseDate,Long itemMasterId,Long orderedQuantity,Long receivedQuantity,String itemDescription, String supplierName,String purchaseNo,Long supplierId,Long officeId, String units,String officeName){
		this.id=id;
		this.itemMasterId=itemMasterId;
		this.orderdQuantity=orderedQuantity;
		this.purchaseDate=purchaseDate;
		this.receivedQuantity=receivedQuantity;
		this.supplierId=supplierId;		
		this.balanceQuantity = orderedQuantity-receivedQuantity;
		this.itemDescription = itemDescription;
		this.supplierName = supplierName;
		this.purchaseNo = purchaseNo;
		this.officeId=officeId;
		this.units = units;
		this.officeName = officeName;
	}
	
	
	public InventoryGrnData(final Long id,final LocalDate purchaseDate,final Long supplierId,final Long itemMasterId,final Long orderedQuantity,final Long receivedQuantity,final String itemDescription, final String supplierName,final String officeName,final String purchaseNo, final Long availableQuantity, final Long itemAmount){
		this.id=id;
		this.itemMasterId=itemMasterId;
		this.orderdQuantity=orderedQuantity;
		this.purchaseDate=purchaseDate;
		this.receivedQuantity=receivedQuantity;
		this.supplierId=supplierId;		
		this.balanceQuantity = orderedQuantity-receivedQuantity;
		this.itemDescription = itemDescription;
		this.supplierName = supplierName;
		this.officeName = officeName;
		this.purchaseNo = purchaseNo;
		this.availableQuantity = availableQuantity;
		this.itemAmount = itemAmount;
	}
	
	public InventoryGrnData(/*List<ItemData> itemData,*/Collection<OfficeData> officeData, List<SupplierData> supplierData) {
		/*this.itemData  = itemData;*/
		this.officeData = officeData;
		this.supplierData = supplierData;
	}
	
	public InventoryGrnData(Long testId) {
		this.testId = testId;
	}
	
	
	
	public InventoryGrnData(Long id, Long itemId,String itemDescription) {
		
		this.id = id;
		this.itemMasterId = itemId;
		this.itemDescription = itemDescription;
		
	}

	public InventoryGrnData(Long id, LocalDate purchaseDate, Long supplierId, Long itemMasterId,
			Long orderedQuantity, Long receivedQuantity, Long stockQuantity, String purchaseNo, Long officeId,
			Long itemAmount, Integer orderStatus, Long itemType) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.purchaseDate = purchaseDate;
		this.supplierId = supplierId;
		this.itemMasterId = itemMasterId;
		this.orderdQuantity = orderedQuantity;
		this.receivedQuantity = receivedQuantity;
		this.availableQuantity = stockQuantity;
		this.purchaseNo = purchaseNo;
		this.officeId = officeId;
		this.itemAmount = itemAmount;
		this.orderStatus = orderStatus;
		this.itemType = itemType;
	}

	public InventoryGrnData(Long id, LocalDate purchaseDate, Long supplierId, Long itemMasterId,
			Long orderedQuantity, Long receivedQuantity, Long stockQuantity, String purchaseNo, Long officeId,
			Long itemAmount, Integer orderStatus) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.purchaseDate = purchaseDate;
		this.supplierId = supplierId;
		this.itemMasterId = itemMasterId;
		this.orderdQuantity = orderedQuantity;
		this.receivedQuantity = receivedQuantity;
		this.availableQuantity = stockQuantity;
		this.purchaseNo = purchaseNo;
		this.officeId = officeId;
		this.itemAmount = itemAmount;
		this.orderStatus = orderStatus;
	}
	public InventoryGrnData(String purchaseNo) {
		// TODO Auto-generated constructor stub
		this.purchaseNo = purchaseNo;
	}

	public Long getItemType() {
		return itemType;
	}

	public void setItemType(Long itemType) {
		this.itemType = itemType;
	}

	public Long getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(Long itemAmount) {
		this.itemAmount = itemAmount;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public Long getLastModifiedById() {
		return lastModifiedById;
	}

	public void setLastModifiedById(Long lastModifiedById) {
		this.lastModifiedById = lastModifiedById;
	}

	public Long getItemMasterId() {
		return itemMasterId;
	}

	public void setItemMasterId(Long itemMasterId) {
		this.itemMasterId = itemMasterId;
	}

	public Long getOrderdQuantity() {
		return orderdQuantity;
	}

	public void setOrderdQuantity(Long orderdQuantity) {
		this.orderdQuantity = orderdQuantity;
	}

	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Long getReceivedQuantity() {
		return receivedQuantity;
	}

	public void setReceivedQuantity(Long receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Long getTestId() {
		return testId;
	}

	public void setTestId(Long testId) {
		this.testId = testId;
	}

	/**
	 * @return the itemData
	 */
	public List<ItemData> getItemData() {
		return itemData;
	}

	/**
	 * @param itemData the itemData to set
	 */
	public void setItemData(List<ItemData> itemData) {
		this.itemData = itemData;
	}

	/**
	 * @return the officeData
	 */
	public Collection<OfficeData> getOfficeData() {
		return officeData;
	}

	/**
	 * @param officeData the officeData to set
	 */
	public void setOfficeData(Collection<OfficeData> officeData) {
		this.officeData = officeData;
	}
	public String getPurchaseNo() {
		return purchaseNo;
	}

	public void setPurchaseNo(String purchaseNo) {
		this.purchaseNo = purchaseNo;
	}
	/**
	 * @return the supplierData
	 */
	public List<SupplierData> getSupplierData() {
		return supplierData;
	}

	/**
	 * @param supplierData the supplierData to set
	 */
	public void setSupplierData(List<SupplierData> supplierData) {
		this.supplierData = supplierData;
	}

	/**
	 * @return the balanceQuantity
	 */
	public Long getBalanceQuantity() {
		return balanceQuantity;
	}

	/**
	 * @param balanceQuantity the balanceQuantity to set
	 */
	public void setBalanceQuantity(Long balanceQuantity) {
		this.balanceQuantity = balanceQuantity;
	}

	/**
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}

	/**
	 * @param itemDescription the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}
	public String getItemcode() {
		return itemcode;
	}

	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}

	@Override
	public String toString() {
		return "InventoryGrnData [id=" + id + ", purchaseDate=" + purchaseDate + ", supplierId=" + supplierId
				+ ", itemMasterId=" + itemMasterId + ", orderdQuantity=" + orderdQuantity + ", receivedQuantity="
				+ receivedQuantity + ", createdById=" + createdById + ", createdDate=" + createdDate
				+ ", lastModifiedDate=" + lastModifiedDate + ", lastModifiedById=" + lastModifiedById + ", testId="
				+ testId + ", balanceQuantity=" + balanceQuantity + ", oficeId=" + oficeId + ", itemData=" + itemData
				+ ", officeData=" + officeData + ", supplierData=" + supplierData + ", itemDescription="
				+ itemDescription + ", supplierName=" + supplierName + ", officeName=" + officeName + ", purchaseNo="
				+ purchaseNo + ", officeId=" + officeId + ", availableQuantity=" + availableQuantity + ", units="
				+ units + ", itemcode=" + itemcode + ", itemAmount=" + itemAmount + ", orderStatus=" + orderStatus
				+ ", itemType=" + itemType + "]";
	}
	
	
}
