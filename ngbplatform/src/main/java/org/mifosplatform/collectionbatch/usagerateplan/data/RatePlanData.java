package org.mifosplatform.collectionbatch.usagerateplan.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.billing.planprice.data.PricingData;
import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.collectionbatch.timemodel.data.TimeModelData;
import org.mifosplatform.collectionbatch.unitofmeasurement.data.UnitOfmeasurementData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;



public class RatePlanData {
	
	private Long id;
	private String timeModelName;
	private String ratingType;
	private String rumName;
	private Long timeModelId;
	
	private List<TimeModelData> timemdelDatas;
	private List<RatableUsageMetricData> RumDatas;
	private List<PricingData> PlanpriceDatas;
	private List<UnitOfmeasurementData> UomDatas;
	private List<CurrencyData> currencyDatas;
	private List<TimeModelData> TimeperiodDatas;
	private Collection<MCodeData> glRealatedDatas;
	
	public RatePlanData() {
		
	}

	public  RatePlanData(Long id, Long timeModelId, String ratingType, String rumName, String timeModelName) {
		this.id=id;
		this.timeModelId=timeModelId;
		this.timeModelName=timeModelName;
		this.ratingType=ratingType;
		this.rumName=rumName;
		
		
	}

	public List<TimeModelData> getTimemdelDatas() {
		return timemdelDatas;
	}

	public void setTimeModels(List<TimeModelData> timemdelDatas) {
		this.timemdelDatas = timemdelDatas;
	}

	public void setRumDatas(List<RatableUsageMetricData> RumDatas) {
		this.RumDatas=RumDatas;
		
	}

	public List<RatableUsageMetricData> getRumDatas() {
		return RumDatas;
	}

	public void setPlanpriceDatas(List<PricingData> PlanpriceDatas) {
		this.PlanpriceDatas=PlanpriceDatas;
		
	}

	public List<PricingData> getPlanpriceDatas() {
		return PlanpriceDatas;
	}

	public void setUomDatas(List<UnitOfmeasurementData> UomDatas) {
		this.UomDatas=UomDatas;
		
	}

	public List<UnitOfmeasurementData> getUomDatas() {
		return UomDatas;
	}

	public void setCurrencyDatas(List<CurrencyData> currencyDatas) {
		this.currencyDatas = currencyDatas;
		
	}

	public List<CurrencyData> getCurrencyDatas() {
		return currencyDatas;
	}
	
	public void setTimeperiodDatas(List<TimeModelData> TimeperiodDatas) {
		this.TimeperiodDatas=TimeperiodDatas;
		
	}

	public List<TimeModelData> getTimeperiodDatas() {
		return TimeperiodDatas;
	}

	public void setglRealatedData(Collection<MCodeData> glRealatedDatas) {
		this.glRealatedDatas=glRealatedDatas;
		
	}
	public Collection<MCodeData> getGlRealatedDatas() {
		return glRealatedDatas;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTimeModelName() {
		return timeModelName;
	}

	public void setTimeModelName(String timeModelName) {
		this.timeModelName = timeModelName;
	}
	
	public String getRatingType() {
		return ratingType;
	}

	public void setRatingType(String ratingType) {
		this.ratingType = ratingType;
	}
	
	public String getRumName() {
		return rumName;
	}

	public void setRumName(String rumName) {
		this.rumName = rumName;
	}

	public Long getTimeModelId() {
		return timeModelId;
	}

	public void setTimeModelId(Long timeModelId) {
		this.timeModelId = timeModelId;
	}
}
