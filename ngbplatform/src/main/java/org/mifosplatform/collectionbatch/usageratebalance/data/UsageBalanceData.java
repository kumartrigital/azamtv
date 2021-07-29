package org.mifosplatform.collectionbatch.usageratebalance.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.collectionbatch.timemodel.data.TimeModelData;
import org.mifosplatform.collectionbatch.unitofmeasurement.data.UnitOfmeasurementData;
import org.mifosplatform.collectionbatch.usageratequantitytier.data.UsageRateQuantityTierData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;

public class UsageBalanceData {
	
	private List<RatableUsageMetricData> RumDatas;
	private List<UnitOfmeasurementData> UomDatas;
	private List<CurrencyData> currencyDatas;
	private List<TimeModelData> TimeperiodDatas;
	private List<UsageRateQuantityTierData> TierDatas;
	private Collection<MCodeData> glRealatedDatas;
	private List<UsageRateQuantityTierData> UsageDatas;
	
	

	public List<RatableUsageMetricData> getRumDatas() {
		return RumDatas;
	}

	public void setRumDatas(List<RatableUsageMetricData> RumDatas) {
		this.RumDatas=RumDatas;
		
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

	public void setTierDatas(List<UsageRateQuantityTierData> TierDatas) {
		this.TierDatas=TierDatas;
		
	}

	public List<UsageRateQuantityTierData> getTierDatas() {
		return TierDatas;
	}

	public void setglRealatedData(Collection<MCodeData> glRealatedDatas) {
		this.glRealatedDatas=glRealatedDatas;
		
	}

	public Collection<MCodeData> getGlRealatedDatas() {
		return glRealatedDatas;
	}

	public void setGlRealatedDatas(Collection<MCodeData> glRealatedDatas) {
		this.glRealatedDatas = glRealatedDatas;
	}

	public void setUsageData(List<UsageRateQuantityTierData> UsageDatas) {
		this.UsageDatas=UsageDatas;
		
	}
	public List<UsageRateQuantityTierData> getUsageDatas() {
		return UsageDatas;
	}
	
	
	
}
