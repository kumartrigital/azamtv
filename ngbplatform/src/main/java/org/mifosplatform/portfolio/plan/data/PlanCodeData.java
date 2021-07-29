package org.mifosplatform.portfolio.plan.data;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.service.DateUtils;

public class PlanCodeData {
	private final Long id;
	private final String planCode;
	private final List<ServiceData> availableServices;
	private final LocalDate starDate;
	private final String isPrepaid;
	private final String planDescription;
	private final String planTypeName;

	private final Long planPoId;
	private final Long dealPoId;
	private final Long catalogeId;
	private final String catalogeName;
	private final String chargeCycle;
	private final Long contractPeriodId;
	private  BigDecimal price;

	public PlanCodeData(final Long id,final String planCode,final List<ServiceData> data,final String isPrepaid,
			final String planDescription ,final Long planPoId,final Long dealPoId,final String planTypeName,final Long catalogeId,
			final String catalogeName,final String chargeCycle,final Long contractPeriodId)
	{
		this.id=id;
		this.planCode=planCode;
		this.availableServices=data;
		this.starDate=DateUtils.getLocalDateOfTenant();
		this.isPrepaid=isPrepaid;
		this.planDescription=planDescription;
		this.planTypeName=planTypeName;
		this.planPoId=planPoId;
		this.dealPoId=dealPoId;
		this.catalogeId=catalogeId;
		this.catalogeName=catalogeName;
		this.chargeCycle = chargeCycle;
		this.contractPeriodId = contractPeriodId;

	}

	public PlanCodeData(final Long id,final String planCode,final List<ServiceData> data,final String isPrepaid,
			final String planDescription ,final Long planPoId,final Long dealPoId,final String planTypeName,final Long catalogeId,
			final String catalogeName,final String chargeCycle,final Long contractPeriodId, final BigDecimal price)
	{
		this.id=id;
		this.planCode=planCode;
		this.availableServices=data;
		this.starDate=DateUtils.getLocalDateOfTenant();
		this.isPrepaid=isPrepaid;
		this.planDescription=planDescription;
		this.planTypeName=planTypeName;
		this.planPoId=planPoId;
		this.dealPoId=dealPoId;
		this.catalogeId=catalogeId;
		this.catalogeName=catalogeName;
		this.chargeCycle = chargeCycle;
		this.contractPeriodId = contractPeriodId;
		this.price = price;

	}
	public Long getPlanPoid() {
		return planPoId;
	}

	public Long getId() {
		return id;
	}

	public Long getDealPoid() {
		return dealPoId;
	}

	public String getPlanCode() {
		return planCode;
	}

	public List<ServiceData> getData() {
		return availableServices;
	}

	public LocalDate getStartDate() {
		return starDate;
	}

	/**
	 * @return the availableServices
	 */
	public List<ServiceData> getAvailableServices() {
		return availableServices;
	}

	/**
	 * @return the start_date
	 */
	public LocalDate getStart_date() {
		return starDate;
	}

	/**
	 * @return the isPrepaid
	 */
	public String getIsPrepaid() {
		return isPrepaid;
	}

	public String getPlanTypeName() {
		return planTypeName;
	}
	
	public Long getCatalogeId() {
		return catalogeId;
	}
	
	public String getCatalogeName() {
		return catalogeName;
	}
	
	public String getChargeCycle() {
		return chargeCycle;
	}
	
	public Long getContractPeriodId() {
		return contractPeriodId;
	}

}
