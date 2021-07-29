package org.mifosplatform.logistics.itemdetails.domain;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_grn")
public class InventoryGrn extends  AbstractPersistable<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3473332897093868940L;
	
	
	
	
	@Column(name="item_master_id")
	private Long itemMasterId;
		
	@Column(name="orderd_quantity")
	private Long orderdQuantity;
	
	@Column(name="purchase_date")
	private Date purchaseDate;
	
	@Column(name="received_quantity")
	private Long receivedQuantity;
	
	@Column(name="supplier_id")
	private Long supplierId;
	
	@Column(name="office_id")
	private Long officeId;
	
	@Column(name="po_no")
	private String purchaseNo;
	
	@Column(name="stock_quantity")
	private Long stockQuantity;
	
	@Column(name="item_amount")
	private Long itemAmount;
	
	@Column(name="order_status")
	private Integer orderStatus;

	public InventoryGrn(final Long itemMasterId,final Long orderdQuantity,final Date purchaseDate,final Long receivedQuantity,final Long supplierId,final Long officeId,String purchaseNo){
		this.itemMasterId = itemMasterId;
		this.orderdQuantity = orderdQuantity;
		this.purchaseDate = purchaseDate;
		this.receivedQuantity = receivedQuantity;
		this.supplierId = supplierId;
		this.officeId = officeId;
		this.purchaseNo = StringUtils.isEmpty(purchaseNo)?null:purchaseNo;
		this.stockQuantity = receivedQuantity;
		this.itemAmount=0L;
		this.orderStatus=0;
	}
	
	public InventoryGrn(final Long itemMasterId,final Long orderdQuantity,final Date purchaseDate,final Long receivedQuantity,final Long supplierId,final Long officeId,String purchaseNo, final Long itemAmount){
		this.itemMasterId = itemMasterId;
		this.orderdQuantity = orderdQuantity;
		this.purchaseDate = purchaseDate;
		this.receivedQuantity = receivedQuantity;
		this.supplierId = supplierId;
		this.officeId = officeId;
		this.purchaseNo = StringUtils.isEmpty(purchaseNo)?null:purchaseNo;
		this.stockQuantity = receivedQuantity;
		this.itemAmount=itemAmount;
		this.orderStatus=0;
	}
	
	
	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Long getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(Long itemAmount) {
		this.itemAmount = itemAmount;
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


	public Date getPurchaseDate() {
		return purchaseDate;
	}


	public void setPurchaseDate(Date purchaseDate) {
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


	public InventoryGrn(){}



	public Long getOfficeId() {
		return officeId;
	}


	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}



	public Long getStockQuantity() {
		return stockQuantity;
	}








	public void setStockQuantity(Long stockQuantity) {
		this.stockQuantity = stockQuantity;
	}








	public static InventoryGrn fromJson(JsonCommand command) {
		
		Long itemMasterId = command.longValueOfParameterNamed("itemMasterId");
		Long orderdQuantity = command.bigDecimalValueOfParameterNamed("orderdQuantity").longValue();
		Long supplierId  = command.longValueOfParameterNamed("supplierId");
		Date purchaseDate = command.DateValueOfParameterNamed("purchaseDate") ;
		Long officeId = command.longValueOfParameterNamed("officeId");
		String purchaseNo = command.stringValueOfParameterNamed("purchaseNo");
		if(command.longValueOfParameterNamed("itemAmount")!=null)
		{
		Long itemAmount = command.longValueOfParameterNamed("itemAmount");
		return new InventoryGrn(itemMasterId,orderdQuantity,purchaseDate,Long.valueOf(0),supplierId,officeId,purchaseNo,itemAmount);
		}
		return new InventoryGrn(itemMasterId,orderdQuantity,purchaseDate,Long.valueOf(0),supplierId,officeId,purchaseNo);
	}

	
	public Map<String, Object> update(JsonCommand command) {
		 final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		 
		  	final String itemMasterId = "itemMasterId";
	        if (command.isChangeInLongParameterNamed(itemMasterId, this.itemMasterId)) {
	            final Long newValue = command.longValueOfParameterNamed(itemMasterId);
	            actualChanges.put(itemMasterId, newValue);
	            this.itemMasterId = newValue;
	        }
	        
	        final String orderdQuantity = "orderdQuantity";
	        if (command.isChangeInLongParameterNamed(orderdQuantity, this.orderdQuantity)) {
	            final Long newValue = command.longValueOfParameterNamed(orderdQuantity);
	            actualChanges.put(orderdQuantity, newValue);
	            this.orderdQuantity = newValue;
	        }
	        
	        final String supplierId = "supplierId";
	        if (command.isChangeInLongParameterNamed(supplierId, this.orderdQuantity)) {
	            final Long newValue = command.longValueOfParameterNamed(supplierId);
	            actualChanges.put(supplierId, newValue);
	            this.supplierId = newValue;
	        }
	        
	        final String purchaseDate = "purchaseDate";
	        if (command.isChangeInDateParameterNamed(purchaseDate, this.purchaseDate)) {
	            final Date newValue = command.DateValueOfParameterNamed(purchaseDate);
	            actualChanges.put(purchaseDate, newValue);
	            this.purchaseDate = newValue;
	        }
	        
	        final String officeId = "officeId";
	        if (command.isChangeInLongParameterNamed(officeId, this.officeId)) {
	            final Long newValue = command.longValueOfParameterNamed(officeId);
	            actualChanges.put(officeId, newValue);
	            this.officeId = newValue;
	        }
	        
	        final String purchaseNo = "purchaseNo";
	        if (command.isChangeInStringParameterNamed(purchaseNo, this.purchaseNo)) {
	            final String newValue = command.stringValueOfParameterNamed(purchaseNo);
	            actualChanges.put(purchaseNo, newValue);
	            this.purchaseNo = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        final String orderStatusNamedParamName = "orderStatus";
	        if (command.isChangeInIntegerParameterNamed(orderStatusNamedParamName, this.orderStatus)) {
	            final Integer newValue = command.integerValueOfParameterNamed(orderStatusNamedParamName);
	            actualChanges.put(orderStatusNamedParamName, newValue);
	            this.orderStatus = newValue;
	        }
	        			
	        
	        return actualChanges;

	}
	
}
