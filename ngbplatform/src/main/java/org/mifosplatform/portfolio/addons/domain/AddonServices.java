package org.mifosplatform.portfolio.addons.domain;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name="b_addons_service")
public class AddonServices extends AbstractAuditableCustom<AppUser,Long>{
	
	
	@Column(name = "plan_id")
	private Long planId;

	@Column(name = "charge_code")
	private String chargeCode;

	@Column(name = "price_region_id")
	private Long priceRegionId;
	
	@Column(name ="is_deleted")
	private char isDelete ='N';
	

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "addons", orphanRemoval = true)
	private Set<AddonsPrices> addonsPrices = new HashSet<AddonsPrices>();
	
	public AddonServices(){
		
	}
	
	public AddonServices(Long planId, String chargeCode, Long priceRegionId) {
		
		this.planId=planId;
		this.chargeCode=chargeCode;
		this.priceRegionId=priceRegionId;
	}

	public static AddonServices fromJson(JsonCommand command) {
        final Long planId=command.longValueOfParameterNamed("planId");
        final String chargeCode=command.stringValueOfParameterNamed("chargeCode");
        final Long priceRegionId =command.longValueOfParameterNamed("priceRegionId");
        
        return new AddonServices(planId,chargeCode,priceRegionId);
	}

	public void addAddonPrices(AddonsPrices addonsPrices) {
		addonsPrices.update(this);
		this.addonsPrices.add(addonsPrices);
		
	}

	public Long getPlanId() {
		return planId;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public Long getPriceRegionId() {
		return priceRegionId;
	}

	public char getIsDelete() {
		return isDelete;
	}

	public Set<AddonsPrices> getAddonsPrices() {
		return addonsPrices;
	}

	public void updateAddonPrices(Set<AddonsPrices> prices) {
		
		 if (!prices.isEmpty()) {
	          this.addonsPrices.clear();
	            this.addonsPrices.addAll(prices);
	        }
		
	}

	public Map<String, Object> update(JsonCommand command) {
		
		  final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);

        final String planIdParamName = "planId";
        if (command.isChangeInLongParameterNamed(planIdParamName, this.planId)) {
            final Long newValue = command.longValueOfParameterNamed(planIdParamName);
            this.planId=newValue;
            actualChanges.put(planIdParamName, newValue);
        }

        final String chargeCodeParamName = "chargeCode";
        if (command.isChangeInStringParameterNamed(chargeCodeParamName,this.chargeCode)) {
            final String newValue = command.stringValueOfParameterNamed(chargeCodeParamName);
            this.chargeCode=newValue;
            actualChanges.put(chargeCodeParamName, newValue);
        }
        
        final String priceRegionIdParamName = "priceRegionId";
        if (command.isChangeInLongParameterNamed(priceRegionIdParamName, this.priceRegionId)) {
            final Long newValue = command.longValueOfParameterNamed(priceRegionIdParamName);
            this.priceRegionId=newValue;
            actualChanges.put(priceRegionIdParamName, newValue);
        }
        
        return actualChanges;
	}

	public void delete() {
         if(this.isDelete != 'Y'){
        	 for(AddonsPrices addonsPrices:this.addonsPrices){
        		 addonsPrices.delete();
        	 }
        	 this.chargeCode="del_"+this.getId()+"_"+this.chargeCode;
        	 this.isDelete ='Y';
        	 
         }
		
	}

	

}
