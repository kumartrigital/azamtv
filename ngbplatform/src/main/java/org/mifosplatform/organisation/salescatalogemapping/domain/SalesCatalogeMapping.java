package org.mifosplatform.organisation.salescatalogemapping.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.salescataloge.domain.SalesCataloge;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_sales_cataloge_mapping")
public class SalesCatalogeMapping extends AbstractPersistable<Long>{
	
	@ManyToOne
    @JoinColumn(name="cataloge_id", nullable=false)
	private SalesCataloge salesCataloge;
	
	@Column(name="plan_id", nullable=false)
	private Long planId;
	
	@Column(name="is_deleted")
	private char isDeleted;

	
	
	public SalesCatalogeMapping() {
	}

	public SalesCatalogeMapping( Long planId) {
		this.planId = planId;
		this.isDeleted = 'N';
	}

	public SalesCataloge getSalesCataloge() {
		return salesCataloge;
	}

	/*public void setCatalogeId(Long catalogeId) {
		this.catalogeId = catalogeId;
	}*/
	
	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	
	public static SalesCatalogeMapping formJson(JsonCommand command) {
		
		//Long catalogeId  = command.longValueOfParameterNamed("catalogeId");
		Long planId  = command.longValueOfParameterNamed("planId");
		
		return new SalesCatalogeMapping(planId);
	}
	
	public Map<String, Object> update(JsonCommand command) {
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		//final String catalogeIdNamedParamName = "catalogeId";
		final String planIdNamedParamName = "planId";
		
		/*if(command.isChangeInLongParameterNamed(catalogeIdNamedParamName, this.catalogeId)){
			final Long newValue = command.longValueOfParameterNamed(catalogeIdNamedParamName);
			actualChanges.put(catalogeIdNamedParamName, newValue);
			this.catalogeId = newValue;
		}*/
		
		if(command.isChangeInLongParameterNamed(planIdNamedParamName, this.planId)){
			final Long newValue = command.longValueOfParameterNamed(planIdNamedParamName);
			actualChanges.put(planIdNamedParamName, newValue);
			this.planId = newValue;
		}
		
		return actualChanges;
	}
	
	public void update(final SalesCataloge salesCataloge) {
		this.salesCataloge=salesCataloge;
		
	}
	
	public char getIsDeleted() {
		return isDeleted;
	}
	
	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void delete() {
		if(this.isDeleted != 'Y'){
			/*this.planId = this.planId+"_"+this.getId();*/
			this.isDeleted = 'Y';
		}
	}

	@Override
	public String toString() {
		return "SalesCatalogeMapping [salesCataloge=" + salesCataloge + ", planId=" + planId + ", isDeleted="
				+ isDeleted + "]";
	}

	
}
