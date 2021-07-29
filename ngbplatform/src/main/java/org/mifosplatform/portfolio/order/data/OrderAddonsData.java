package org.mifosplatform.portfolio.order.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.portfolio.addons.data.AddonsPriceData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;

public class OrderAddonsData {
	
	private final Long id;
	private final Long serviceId;
	private final String serviceCode;
	private final String status;
	private final LocalDate startDate;
	private final LocalDate endDate;
	private final BigDecimal price;
	private final List<AddonsPriceData> addonsPriceDatas;
	private final List<SubscriptionData> contractPeriods;

	public OrderAddonsData(List<AddonsPriceData> addonsPriceDatas,List<SubscriptionData> contractPeriods) {
		
		this.addonsPriceDatas=addonsPriceDatas;
		this.contractPeriods=contractPeriods;
		this.id=null;
		this.serviceId=null;
		this.serviceCode=null;
		this.startDate=null;
		this.endDate=null;
		this.status=null;
		this.price=null;
	}

	public OrderAddonsData(Long id, Long serviceId, String serviceCode,LocalDate startDate, LocalDate endDate, String status,
			        BigDecimal price) {
											
		   this.id=id;
		   this.serviceId=serviceId;
		   this.serviceCode=serviceCode;
		   this.startDate=startDate;
		   this.endDate=endDate;
		   this.status=status;
		   this.price=price;
		   this.addonsPriceDatas=null;
		   this.contractPeriods=null;
		   
		   
		   
		   
	}

	public Long getId() {
		return id;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public String getStatus() {
		return status;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public List<AddonsPriceData> getAddonsPriceDatas() {
		return addonsPriceDatas;
	}

	public List<SubscriptionData> getContractPeriods() {
		return contractPeriods;
	}
	
	

}
