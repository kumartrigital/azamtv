package org.mifosplatform.logistics.onetimesale.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_onetime_sale")
public class OneTimeSale extends AbstractAuditableCustom<AppUser, Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "units", length = 65536)
	private String units;

	@Column(name = "charge_code")
	private String chargeCode;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;
	
	@Column(name = "quantity")
	private String quantity;

	@Column(name = "total_price")
	private BigDecimal totalPrice;

	@Column(name = "sale_date")
	private Date saleDate;

	@Column(name = "item_id")
	private Long itemId;
	
	@Column(name = "bill_id")
	private Long billId;

	@Column(name = "is_invoiced", nullable = false)
	private char isInvoiced = 'N';
	
	@Column(name="hardware_allocated",nullable=false)
	private String hardwareAllocated = "UNALLOCATED";

	@Column(name = "discount_id")
	private Long discountId;
	
	@Column(name = "is_deleted", nullable = false)
	private char isDeleted = 'N';
	
	@Column(name = "office_id")
	private Long officeId;
	
	@Column(name="device_mode")
	private String deviceMode;
	
	@Column(name="contract_period")
	private Long contractPeriod;
	
	@Column(name = "invoice_id")
	private Long invoiceId;
	
	@Column(name = "client_service_id")
	private Long clientServiceId;
	
	@Column(name = "resource_id")
	private Long resourceId;
	

	
	
	public OneTimeSale(){}
	
	public OneTimeSale(final Long clientId, final Long itemId,final String units,final String quantity,
			final  String chargeCode, final BigDecimal unitPrice,final BigDecimal totalPrice,
            final LocalDate saleDate, final Long discountId, final Long officeId,final String saleType, 
            final Long contractPeriod,final Long clientServiceId ,final Long resourceId) {

	this.clientId=clientId;
	this.itemId=itemId;
	this.units=units;
	this.chargeCode=chargeCode;
	this.unitPrice=unitPrice;
	this.totalPrice=totalPrice;
	this.quantity=quantity;
	this.saleDate=saleDate.toDate();
	this.discountId=discountId;
	this.officeId=officeId;
	if(saleType.equalsIgnoreCase("SECONDSALE")){
			this.isInvoiced='Y';
		}
	this.deviceMode=saleType;
	this.contractPeriod=contractPeriod;
	this.clientServiceId = clientServiceId;
	this.resourceId=resourceId;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getUnits() {
		return units;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public String getQuantity() {
		return quantity;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public Date getSaleDate() {
		return saleDate;
	}

	public Long getItemId() {
		return itemId;
	}

	public char getIsInvoiced() {
		return isInvoiced;
	}

	public void setIsInvoiced(char isInvoiced) {
		this.isInvoiced = isInvoiced;
	}
	
	public void updateBillId(Long billId) {
        this.billId=billId;
       
    }
	
	public char getIsDeleted() {
		return isDeleted;
	}
	
	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public String getHardwareAllocated() {
		return hardwareAllocated;
	}
	
	public void setHardwareAllocated(String hardwareAllocated) {
		this.hardwareAllocated = hardwareAllocated;
	}

	public void setStatus() {
		this.hardwareAllocated = "UNALLOCATED";
		
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getBillId() {
		return billId;
	}

	public Long getDiscountId() {
		return discountId;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public String getDeviceMode() {
		return deviceMode;
	}

	public Long getContractPeriod() {
		return contractPeriod;
	}
	
	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	
	}

	public Long getInvoiceId() {
		return invoiceId;
	}
	
	public Long getClientServiceId() {
		return clientServiceId;
	}

	public void setClientServiceId(Long clientServiceId) {
		this.clientServiceId = clientServiceId;
	}
	
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public static OneTimeSale fromJson(final Long clientId, final JsonCommand command, final ItemMaster item,final BigDecimal conversionPrice,final Long clientCurrency) {
		
			final String saleType = command.stringValueOfParameterNamed("saleType");
		    final Long itemId=command.longValueOfParameterNamed("itemId");
		    final String units = item.getUnits();
		    final String chargeCode = command.stringValueOfParameterNamed("chargeCode");
		    final String quantity = command.stringValueOfParameterNamed("quantity");
		    final BigDecimal unitPrice=conversionPrice;
		    final BigDecimal totalPrice=conversionPrice;
		    final LocalDate saleDate=command.localDateValueOfParameterNamed("saleDate");
		    final Long discountId=command.longValueOfParameterNamed("discountId");
		    final Long officeId=command.longValueOfParameterNamed("officeId");
		    final Long contractPeriod=command.longValueOfParameterNamed("contractPeriod");
		    final Long clientServiceId = command.longValueOfParameterNamed("clientServiceId");
		    final Long resourceId = clientCurrency;
		    
          return new OneTimeSale(clientId, itemId, units, quantity, chargeCode, unitPrice, totalPrice, 
        		  saleDate,discountId,officeId,saleType,contractPeriod,clientServiceId,resourceId);
	}

	

}
