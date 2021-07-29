package org.mifosplatform.organisation.voucher.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;

public class VoucherRequestData {

	
	private Long itemId;
	private Long purchasefrom;
	private Long purchaseBy;
	private Long receivedQuantity;
	private String chargeCode;
	private String status;
	private Date purchaseDate;
	private Long orderQuantity;
	private BigDecimal unitPrice;
	private BigDecimal chargeAmount;
	private Long exportedQuantity;
	private Long redeemedQuantity;
	private Long allocatedQuantity;
	private List<VoucherData> voucherData;
	
	public VoucherRequestData(Long id, Date requestedDate, Long orderdQuantity, Long exportedQuantity,
			Long redeemedQuantity, Long receivedQuantity, String status, String itemDescription,
			BigDecimal chargeAmount, BigDecimal unitPrice,Long allocatedQuantity) {
		// TODO Auto-generated constructor stub
		this.itemId = id;
		this.purchaseDate = requestedDate;
		this.orderQuantity = orderdQuantity;
		this.receivedQuantity = receivedQuantity;
		this.exportedQuantity = exportedQuantity;
		this.redeemedQuantity = redeemedQuantity;
		this.status = status;
		this.chargeAmount = chargeAmount;
		this.unitPrice = unitPrice;
		this.allocatedQuantity = allocatedQuantity;
		
	}

	public VoucherRequestData() {
		// TODO Auto-generated constructor stub
	}

	public Long getAllocatedQuantity() {
		return allocatedQuantity;
	}

	public void setAllocatedQuantity(Long allocatedQuantity) {
		this.allocatedQuantity = allocatedQuantity;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getPurchasefrom() {
		return purchasefrom;
	}

	public void setPurchasefrom(Long purchasefrom) {
		this.purchasefrom = purchasefrom;
	}

	public Long getPurchaseBy() {
		return purchaseBy;
	}

	public void setPurchaseBy(Long purchaseBy) {
		this.purchaseBy = purchaseBy;
	}

	public Long getReceivedQuantity() {
		return receivedQuantity;
	}

	public void setReceivedQuantity(Long receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Long getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(Long orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
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

	public List<VoucherData> getVoucherData() {
		return voucherData;
	}

	public void setVoucherData(List<VoucherData> voucherData) {
		this.voucherData = voucherData;
	}

	
}
