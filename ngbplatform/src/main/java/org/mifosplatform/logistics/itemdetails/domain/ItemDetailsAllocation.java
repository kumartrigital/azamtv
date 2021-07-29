package org.mifosplatform.logistics.itemdetails.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.useradministration.domain.AppUser;

import com.google.gson.JsonElement;



@Entity
@Table(name="b_allocation")
public class ItemDetailsAllocation extends AbstractAuditableCustom<AppUser,Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*order_id is nothing but a ID of b_onetime_sale table*/
	
	@Column(name="order_id",nullable=false,length=20)
	private Long orderId;
	
	@Column(name="client_id",nullable=false,length=20)
	private Long clientId; 
	
	@Column(name="item_master_id",nullable=false,length=20)
	private Long itemMasterId;
	
	@Column(name="serial_no",nullable=false,length=100)
	private String serialNumber;
	
	@Column(name="allocation_date",nullable=true)
	private Date allocationDate;
	
	@Column(name="status",nullable=true)
	private String status;
	
	@Column(name="order_type",nullable=true)
	private String orderType;
	
	@Column(name="clientservice_id",nullable=true)
	private Long clientServiceId;

	@Column(name="is_deleted")
	private String isDeleted="N";
	
	@Column(name="remarks")
	private String remarks;
	
	
	public ItemDetailsAllocation(){}

	public ItemDetailsAllocation(final Long orderId,final Long clientId,final Long itemMasterId,final String serialNumber,Date allocationDate,String status ){
		this.orderId = orderId;
		this.clientId = clientId;
		this.itemMasterId = itemMasterId;
		this.serialNumber = serialNumber;
		this.allocationDate = allocationDate;
		this.status = status;
	}
	
	
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getItemMasterId() {
		return itemMasterId;
	}

	public void setItemMasterId(Long itemMasterId) {
		this.itemMasterId = itemMasterId;
	}

	
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Long getClientServiceId() {
		return clientServiceId;
	}

	public void setClientServiceId(Long clientServiceId) {
		this.clientServiceId = clientServiceId;
	}

	public Date getAllocationDate() {
		return allocationDate;
	}

	public void setAllocationDate(Date allocationDate) {
		this.allocationDate = allocationDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public static ItemDetailsAllocation fromJson(JsonElement j, FromJsonHelper fromJsonHelper) {
		 
		final Long orderId = fromJsonHelper.extractLongNamed("orderId",j);
		final Long clientId = fromJsonHelper.extractLongNamed("clientId", j);
		final Long itemMasterId = fromJsonHelper.extractLongNamed("itemMasterId", j);
		final String serialNumber = fromJsonHelper.extractStringNamed("serialNumber", j);
		final Date allocationDate = DateUtils.getDateOfTenant();
		final String status = fromJsonHelper.extractStringNamed("status", j);
		final String ordertype = fromJsonHelper.extractStringNamed("orderType", j);
		final Long clientserviceid = fromJsonHelper.extractLongNamed("clientServiceId", j);
		return new ItemDetailsAllocation(orderId,clientId,itemMasterId,serialNumber,allocationDate,status);
	}

	public void deAllocate(String remarks) {
  
		  this.isDeleted="Y";
		  this.status="unallocated";
		  this.remarks=remarks;
	}
	
	
}
