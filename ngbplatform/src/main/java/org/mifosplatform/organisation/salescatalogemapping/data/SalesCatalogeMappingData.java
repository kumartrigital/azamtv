package org.mifosplatform.organisation.salescatalogemapping.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.portfolio.plan.data.PlanData;

public class SalesCatalogeMappingData {
	
	private Long  id;
	private Long  catalogeId;
	private Long  planId;
	private String planCode;
	private String planDescription;
	private String name;
	
	//for template purpose
	private List<SalesCatalogeData> salesCatalogeDatas;
	private List<PlanData> planDatas;
	private List<SalesCatalogeMappingData> salesCatalogeMappingData;
	private Collection<MCodeData> salesPlanCategoryData;
	

	public SalesCatalogeMappingData() {
	}
	
	public SalesCatalogeMappingData(Long id, Long catalogeId, Long planId,String planCode,String planDescription,String name) {
		this.id = id;
		this.catalogeId = catalogeId;
		this.planId = planId;
		this.planCode = planCode;
		this.planDescription = planDescription;
		this.name = name;
	}
	
	public SalesCatalogeMappingData(final List<SalesCatalogeData> salesCatalogeDatas,final List<PlanData> planDatas,List<SalesCatalogeMappingData> salesCatalogeMappingData,
			                        final Collection<MCodeData> salesPlanCategoryData) {
		this.salesCatalogeDatas = salesCatalogeDatas;
		this.planDatas = planDatas;
		this.salesCatalogeMappingData = salesCatalogeMappingData;
		this.salesPlanCategoryData = salesPlanCategoryData;
	}

	public Long getId() {
		return id;
	}

	public Long getCatalogeId() {
		return catalogeId;
	}

	public Long getPlanId() {
		return planId;
	}
	
	public String getPlanCode() {
		return planCode;
	}

	public String getPlanDescription() {
		return planDescription;
	}

	public String getName() {
		return name;
	}

	public List<SalesCatalogeData> getSalesCatalogeDatas() {
		return salesCatalogeDatas;
	}
	
	public void setSalesCatalogeDatas(List<SalesCatalogeData> salesCatalogeDatas) {
		this.salesCatalogeDatas = salesCatalogeDatas;		
	}

	public List<PlanData> getPlanDatas() {
		return planDatas;
	}
	
	public void setPlanDatas(List<PlanData> planDatas) {
		this.planDatas = planDatas;	
		
	}

	public void setSalesCatalogeMappingDatas(List<SalesCatalogeMappingData> salesCatalogeMappingData) {
		this.salesCatalogeMappingData = salesCatalogeMappingData;
		
	}

	public Collection<MCodeData> getSalesPlanCategoryData() {
		return salesPlanCategoryData;
	}
	
	public void setSalesPlanCategoryData(Collection<MCodeData> salesPlanCategoryData) {
		this.salesPlanCategoryData = salesPlanCategoryData;		
	}

	
	
	

}
