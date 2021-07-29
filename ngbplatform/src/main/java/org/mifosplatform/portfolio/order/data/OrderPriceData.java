package org.mifosplatform.portfolio.order.data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.LocalDate;

public class OrderPriceData {

	private final Long id;
	private final Long orderId;
	private final Long serviceId;
	private final String chargeDuration;
	private final String durationType;
	private final BigDecimal price;
	private final Long productId;

	private String chargeCode;
	private String chargeType;
	private LocalDate invoiceTillDate;
	private Long createdBy;
	private Date createdDate;
	private Date lastModifiedDate;
	private Long lastModifiedBy;
	private Long clientId;
	private LocalDate billStartDate;
	private LocalDate billEndDate;
	private LocalDate nextBillDate;
	private String billingCycle;
	private String billingFrequency;
	private String chargeDescription;
	private String serviceDescription;
	private String productDescription;
	private String chargeOwner;


	public OrderPriceData(Long id,Long orderId,Long clientId,String serviceDescription, String chargeDescription, String chargeDuration,
			String durationtype, BigDecimal price, LocalDate billStartDate, LocalDate billEndDate, LocalDate nextBillDate,
			LocalDate invoiceTillDate, String billingAlign, String billingFrequency,String productDescription, String chargeOwner) {

		this.id=id;
		this.orderId=orderId;
		this.clientId=clientId;
		this.serviceDescription=serviceDescription;
		this.chargeDescription=chargeDescription;
		this.chargeDuration=chargeDuration;
		this.durationType=durationtype;
		this.price=price;
		this.billStartDate=billStartDate;
		this.billEndDate=billEndDate;
		this.nextBillDate=nextBillDate;
		this.serviceId=null;
		this.invoiceTillDate=invoiceTillDate;
		this.billingFrequency=billingFrequency;
		this.billingCycle= billingAlign!=null?this.getbillingCycle(billingAlign):null;
		this.productDescription = productDescription;
		this.productId = null;
		this.chargeOwner = chargeOwner;


	}



	private String getbillingCycle(String billingAlign) {
		 String day = null;
			if(this.billingFrequency.equalsIgnoreCase("Weekly")){
				if(billingAlign.equalsIgnoreCase("y")){
				return "Every Monday";
		     }else {
			SimpleDateFormat f = new SimpleDateFormat("EEEE");
		      day="Every "+f.format(billStartDate.toDate());
		     return day;
		    }
	     }else if(this.billingFrequency.equalsIgnoreCase("Monthly")){
			if(billingAlign.equalsIgnoreCase("y")){
				day="1st day of the Month";
				return day;
			}
			else{
				 Calendar cal = Calendar.getInstance();
				    cal.setTime(billStartDate.toDate());
				 if(cal.get(5) == 2 ){
					 day = "Every "+cal.get(Calendar.DAY_OF_MONTH)+"nd of the Month";
				 }else if(cal.get(5) == 3){
					 day = "Every "+cal.get(Calendar.DAY_OF_MONTH)+"rd of the Month";
				 }
				 else{
					 
				     day = "Every "+cal.get(Calendar.DAY_OF_MONTH)+"th of the Month";
				 }
				    return day;
			}
		}
			return day;
		
		
		
	}



	public OrderPriceData(Long id, Long orderId, Long serviceId,String chargeCode, String chargeType, String chargeDuration,
			String durationType, Date invoiceTillDate, BigDecimal price,Long createdbyId, Date createdDate, Date lastModefiedDate,
			Long lastModefiedId,Long productId, String chargeOwner) {
		this.id=id;
		this.orderId=orderId;
		this.serviceId=serviceId;
		this.chargeCode=chargeCode;
		this.chargeType=chargeType;
		this.chargeDuration=chargeDuration;
		this.durationType=durationType;
		this.price=price;
		this.productId = productId;
		this.chargeOwner = chargeOwner;
	}



	public String getChargeOwner() {
		return chargeOwner;
	}



	public void setChargeOwner(String chargeOwner) {
		this.chargeOwner = chargeOwner;
	}



	public Long getClientId() {
		return clientId;
	}



	public LocalDate getBillStartDate() {
		return billStartDate;
	}



	public LocalDate getBillEndDate() {
		return billEndDate;
	}



	public LocalDate getNextBillDate() {
		return nextBillDate;
	}



	public String getBillingCycle() {
		return billingCycle;
	}



	public String getBillingFrequency() {
		return billingFrequency;
	}

	public Long getId() {
		return id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public String getChargeType() {
		return chargeType;
	}

	public String getChargeDuration() {
		return chargeDuration;
	}

	public String getDurationType() {
		return durationType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public LocalDate getInvoiceTillDate() {
		return invoiceTillDate;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public Long getLastModifiedBy() {
		return lastModifiedBy;
	}

	public String getChargeDescription() {
		return chargeDescription;
	}

	public void setChargeDescription(String chargeDescription) {
		this.chargeDescription = chargeDescription;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}
	
	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.serviceDescription = productDescription;
	}

	public Long getProductId() {
		return productId;
	}
	
}
