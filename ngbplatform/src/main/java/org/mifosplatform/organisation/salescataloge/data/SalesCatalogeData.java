package org.mifosplatform.organisation.salescataloge.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.salescatalogemapping.data.SalesCatalogeMappingData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.PlanData;

public class SalesCatalogeData {
	private Long   id;
	private String name;
	private Long salesPlanCategoryId;
	private String salesPlanCategoryName;
	
	private Collection<SalesCatalogeMappingData> salesCatalogeMappingData ;
	private String planDescription;
	private String planCode;
	private List<PlanData> plans;
	private List<PlanData> selectedPlans;
	private SalesCatalogeData salesCatalogeData;
	private Collection<MCodeData> salesPlanCategoryData;
	private List<SalesCatalogeData> salescatalogeDatas;
	private List<SalesCatalogeData> devicePlanDatas;
	private List<SalesCatalogeData> allPlanDatas;
	private List<SubscriptionData> subscriptiondata;
	private List<PlanCodeData> planData;
	
	public SalesCatalogeData() {
	}

	public SalesCatalogeData(Long id, String name,Long salesPlanCategoryId,String salesPlanCategoryName) {
		this.id = id;
		this.name = name;
		this.salesPlanCategoryId = salesPlanCategoryId;
		this.salesPlanCategoryName = salesPlanCategoryName;
	}

	public SalesCatalogeData(List<PlanData> data, SalesCatalogeData salesCatalogeData,
			List<PlanData> plans,Collection<MCodeData> salesPlanCategoryData) {
		
		if(plans!=null){
			this.id = id;
			this.planCode = planCode;
			this.planDescription = planDescription;
			}
           this.plans = data;
           this.salesCatalogeData = salesCatalogeData;
           this.selectedPlans = plans;
           this.salesPlanCategoryData = salesPlanCategoryData;
           
           
	}

	public SalesCatalogeData(List<SalesCatalogeData> allPlanDatas) {
		this.allPlanDatas = allPlanDatas;
	}

	public SalesCatalogeData(List<PlanCodeData> planData, List<SubscriptionData> contractPeriod) {
		this.planData = planData;
		this.subscriptiondata = contractPeriod;
	}

	public Long getId() {
		return id;
	}

	public String getname() {
		return name;
	}

	public String getplanCode() {
		return planCode;
	}

	public String getplanDescription() {
		return planDescription;
	}
	public Long getSalesPlanCategoryId() {
		return salesPlanCategoryId;
	}
	public String getSalesPlanCategoryName() {
		return salesPlanCategoryName;
	}
	
	public Collection<SalesCatalogeMappingData> getSalesCatalogeMappingData() {
		return salesCatalogeMappingData;
	}

	public void setSalesCatalogeMappingData(Collection<SalesCatalogeMappingData> salesCatalogeMappingData) {
		this.salesCatalogeMappingData = salesCatalogeMappingData;
	}
	
	public List<SalesCatalogeData> getSalescatalogeDatas() {
		return salescatalogeDatas;
	}
	public List<SalesCatalogeData> getDevicePlanDatas() {
		return devicePlanDatas;
	}
	public List<SalesCatalogeData> getAllPlanDatas() {
		return allPlanDatas;
	}


	
}
