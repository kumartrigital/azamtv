package org.mifosplatform.portfolio.addons.data;

import java.util.List;

import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.servicemapping.data.ServiceMappingData;

public class AddonsData {

	private final List<PlanCodeData> planDatas;
	private final List<ChargeCodeData> chargeCodeDatas;
	private final List<PriceRegionData> priceRegionData;
	private final List<ServiceMappingData> servicedatas;
	private  Long id;
	private  String planCode;
	private  String chargeCode;
	private  String priceRegion;
	private List<AddonsPriceData> addonsPrices;
	
	
	public AddonsData(AddonsData addonsData, List<PlanCodeData> planDatas,
			List<ChargeCodeData> chargeCodeDatas,List<PriceRegionData> priceRegionData,
			    List<ServiceMappingData> servicedatas) {
		
		this.servicedatas=servicedatas;
		this.planDatas=planDatas;
		this.chargeCodeDatas=chargeCodeDatas;
		this.priceRegionData=priceRegionData;
		
		if(addonsData != null){
			this.id=addonsData.getId();
			this.planCode=addonsData.getPlanCode();
			this.chargeCode=addonsData.getChargeCode();
			this.priceRegion=addonsData.getPriceRegion();
			this.addonsPrices=addonsData.getAddonsPrices();
		}
	
	}


	public AddonsData(Long id, String planCode, String chargeCode,String priceRegion) {
		this.servicedatas=null;
		this.planDatas=null;
		this.chargeCodeDatas=null;
		this.priceRegionData=null;
		this.id=id;
		this.planCode=planCode;
		this.chargeCode=chargeCode;
		this.priceRegion=priceRegion;
	}


	public void setPrices(List<AddonsPriceData> addonsPrices) {
		this.addonsPrices=addonsPrices;
		
	}


	public List<PlanCodeData> getPlanDatas() {
		return planDatas;
	}


	public List<ChargeCodeData> getChargeCodeDatas() {
		return chargeCodeDatas;
	}


	public List<PriceRegionData> getPriceRegionData() {
		return priceRegionData;
	}


	public List<ServiceMappingData> getServicedatas() {
		return servicedatas;
	}


	public Long getId() {
		return id;
	}


	public String getPlanCode() {
		return planCode;
	}


	public String getChargeCode() {
		return chargeCode;
	}


	public String getPriceRegion() {
		return priceRegion;
	}


	public List<AddonsPriceData> getAddonsPrices() {
		return addonsPrices;
	}
	
	

}
