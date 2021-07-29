package org.mifosplatform.logistics.itemdetails.data;

import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.provisioning.provisioning.data.ModelProvisionMappingData;


public class ItemDetailsData {
	
	private final Collection<InventoryGrnData> inventoryGrnDatas;
	private final Collection<MCodeData> qualityDatas;
	private final Collection<MCodeData> statusDatas;
	private Collection<MCodeData> swapDevicesReasons;
	private final Long id;
	private final Long itemMasterId; 
	private String serialNumber;
	private final Long grnId;
	private String provisioningSerialNumber;
	
	private String quality;
	private final String status;
	private final Long officeId;
	private final String clientId;
	private final Long warranty;
	private final LocalDate warranty_date;
	private final String remarks;
	private final String itemDescription;
	private final String supplier;
	private final String officeName;
	private final String accountNumber;
	private Collection<OfficeData> officeData;
	private Collection<ItemData> itemMasterData;
	private String units;
	private Long quantity;
	private String isPairing;
	private Long pairedItemId;
	private String pairedItemCode;
	private Collection<ModelProvisionMappingData> modelProvisionMappingData;
	private String itemCode;
	private String itemModel;
	private String entityname;
	private String Chipid;
	private String PairedItem;
	

	
	public ItemDetailsData(Collection<InventoryGrnData> inventoryGrnData,Collection<MCodeData> qualityDatas,Collection<MCodeData> statusDatas,
			String serialNumber, String provisionSerialNumber, Collection<ModelProvisionMappingData> modelProvisionMappingData) {
		
		this.inventoryGrnDatas=inventoryGrnData;
		this.qualityDatas=qualityDatas;
		this.statusDatas=statusDatas;
		this.officeId=null;
		this.id=null;
		this.itemMasterId=null;
		this.serialNumber=serialNumber;
		this.grnId=null;
		this.provisioningSerialNumber=provisionSerialNumber;
		this.quality=null;
		this.status=null;
		this.warranty=null;
		this.warranty_date=null;
		this.remarks=null;
		this.itemDescription = null;
		this.supplier = null;
		this.clientId = null;
		this.officeName = null;
		this.accountNumber = null;
		this.itemModel = null;
		this.modelProvisionMappingData = modelProvisionMappingData;
		
		
	}
	
	
	public ItemDetailsData(Collection<InventoryGrnData> inventoryGrnData,Collection<MCodeData> qualityDatas,Collection<MCodeData> statusDatas,
			Long saleid,Long OfficeId,String status,String quantiy,String serialNumber, String provisionSerialNumber, Collection<ModelProvisionMappingData> modelProvisionMappingData) {
		
		this.inventoryGrnDatas=inventoryGrnData;
		this.qualityDatas=qualityDatas;
		this.statusDatas=statusDatas;
		this.officeId=OfficeId;
		this.id=saleid;
		this.itemMasterId=null;
		this.serialNumber=serialNumber;
		this.grnId=null;
		this.provisioningSerialNumber=provisionSerialNumber;
		this.quality=quantiy;
		this.status=status;
		this.warranty=null;
		this.warranty_date=null;
		this.remarks=null;
		this.itemDescription = null;
		this.supplier = null;
		this.clientId = null;
		this.officeName = null;
		this.accountNumber = null;
		this.itemModel = null;
		this.modelProvisionMappingData = modelProvisionMappingData;
		
		
	}

	public ItemDetailsData(final Long id,final  Long itemMasterId,final String serialNumber,final Long grnId,final  String provisioningSerialNumber,final  String quality,
			final String status,final Long warranty,final LocalDate warranty_date,final String remarks,final String itemDescription,final String supplier,final Long clientId,final String officeName, 
			final String accountNumber, final String units,final Long quantity,final String isPairing,final Long pairedItemId,
			final String pairedItemCode, final String itemModel,final String itemCode) {
		
		this.id=id;
		this.itemMasterId=itemMasterId;
		this.serialNumber=serialNumber;
		this.grnId=grnId;
		this.provisioningSerialNumber=provisioningSerialNumber;
		this.quality=quality;
		this.status=status;
		this.warranty=warranty;
		this.warranty_date=warranty_date;
		this.remarks=remarks;
		this.itemDescription = itemDescription;
		this.officeId=null;
		this.supplier = supplier;
		this.clientId = clientId.toString();
		this.officeName = officeName;
		this.accountNumber = accountNumber;
		this.inventoryGrnDatas=null;
		this.qualityDatas=null;
		this.statusDatas=null;
		this.units = units;
		this.quantity = quantity;
		this.isPairing = isPairing;
		this.pairedItemId = pairedItemId;
		this.pairedItemCode = pairedItemCode;
		this.itemModel = itemModel;
		this.itemCode = itemCode;
	}
	
	public ItemDetailsData(final Long id,String entityname,String serialNumber,final Long grnId,String Chipid,final String status,
			final Long warranty,final LocalDate warranty_date,final String itemCode,final String itemDescription,final String accountNumber,
			final String isPairing,String PairedItem,
			final String itemModel,final Long pairedItemId, String clientId,
			final String quality) {
		
		this.inventoryGrnDatas=null;
		this.qualityDatas=null;
		this.statusDatas=null;
		this.swapDevicesReasons=null;
		this.itemMasterId=null;
		this.provisioningSerialNumber=null;
		this.quality=quality;
		this.officeId=null;
		this.clientId =clientId;
		this.remarks=null;
		this.supplier = null;
		this.officeName =null;
		this.id=id;
		this.entityname=entityname;
		this.serialNumber=serialNumber;
		this.grnId=grnId;
		this.Chipid=Chipid;
		this.status=status;
		this.warranty=warranty;
		this.warranty_date=warranty_date;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.accountNumber = accountNumber;
		this.isPairing = isPairing;
		this.PairedItem = PairedItem;
		this.itemModel = itemModel;
		this.pairedItemId = pairedItemId;
		
	}
	
	public ItemDetailsData(Collection<OfficeData> officeData, Collection<ItemData> itemMasterData) {
		this.officeData = officeData;
		this.itemMasterData = itemMasterData;
		this.inventoryGrnDatas = null;
		this.qualityDatas = null;
		this.statusDatas = null;
		this.id = null;
		this.itemMasterId = null;
		this.serialNumber = null;
		this.grnId = null;
		this.provisioningSerialNumber = null;
		this.quality = null;
		this.status = null;
		this.officeId = null;
		this.clientId = null;
		this.warranty = null;
		this.warranty_date = null;
		this.remarks = null;
		this.itemDescription = null;
		this.supplier = null;
		this.officeName = null;
		this.accountNumber = null;
		this.itemModel = null;
	}

	public ItemDetailsData(final long id2,final long item_master_id,final long pairedItemId) {
		
		this.grnId=id2;
		this.itemMasterId=item_master_id;
		this.pairedItemId=pairedItemId;
		this.inventoryGrnDatas = null;
		this.qualityDatas = null;
		this.statusDatas = null;
		this.id = null;
		//this.itemMasterId = null;
		this.serialNumber = null;
		//this.grnId = null;
		this.provisioningSerialNumber = null;
		this.quality = null;
		this.status = null;
		this.officeId = null;
		this.clientId = null;
		this.warranty = null;
		this.warranty_date = null;
		this.remarks = null;
		this.itemDescription = null;
		this.supplier = null;
		this.officeName = null;
		this.accountNumber = null;
		this.itemModel = null;
	}

	public Collection<InventoryGrnData> getInventoryGrnDatas() {
		return inventoryGrnDatas;
	}

	public Collection<MCodeData> getQualityDatas() {
		return qualityDatas;
	}

	public Collection<MCodeData> getStatusDatas() {
		return statusDatas;
	}

	public Long getId() {
		return id;
	}

	public Long getItemMasterId() {
		return itemMasterId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}
	
	public void serialNumber(String serialNumber) {
		this.serialNumber = provisioningSerialNumber;
	}

	public Long getGrnId() {
		return grnId;
	}

	public String getProvisioningSerialNumber() {
		return provisioningSerialNumber;
	}

	public void setProvisioningSerialNumber(String provisioningSerialNumber) {
		this.provisioningSerialNumber = provisioningSerialNumber;
	}

	public String getQuality() {
		return quality;
	}
	
	public void setQuality(String quality) {
		this.quality = quality;
	}

	public String getStatus() {
		return status;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public String getClientId() {
		return clientId;
	}

	public Long getWarranty() {
		return warranty;
	}
	public LocalDate getWarranty_date() {
		return warranty_date;
	}

	public String getRemarks() {
		return remarks;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public String getSupplier() {
		return supplier;
	}

	public String getOfficeName() {
		return officeName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}
	
	public Collection<OfficeData> getOfficeData() {
		return officeData;
	}

	public void setOfficeData(Collection<OfficeData> officeData) {
		this.officeData = officeData;
	}

	public Collection<ItemData> getItemMasterData() {
		return itemMasterData;
	}

	public void setItemMasterData(Collection<ItemData> itemMasterData) {
		this.itemMasterData = itemMasterData;
	}

	public String getIsPairing() {
		return isPairing;
	}

	public void setIsPairing(String isPairing) {
		this.isPairing = isPairing;
	}

	public Long getPairedItemId() {
		return pairedItemId;
	}

	public void setPairedItemId(Long pairedItemId) {
		this.pairedItemId = pairedItemId;
	}
     
	public String getitemModel() {
		return itemModel;
	}

	public void setitemModel(String itemModel) {
		this.itemModel = itemModel;
	}

	public Collection<ModelProvisionMappingData> getModelProvisionMappingData() {
		return modelProvisionMappingData;
	}

	public void setModelProvisionMappingData(Collection<ModelProvisionMappingData> modelProvisionMappingData) {
		this.modelProvisionMappingData = modelProvisionMappingData;
	}

	public Collection<MCodeData> getSwapDevicesReasons() {
		return swapDevicesReasons;
	}
	
	public void setSwapDevicesReasons(Collection<MCodeData> swapDevicesReasons) {
		this.swapDevicesReasons=swapDevicesReasons;
	}


	
}
