package org.mifosplatform.organisation.salescataloge.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.salescatalogemapping.domain.SalesCatalogeMapping;
import org.mifosplatform.portfolio.plan.domain.PlanDetails;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_sales_cataloge", uniqueConstraints = {@UniqueConstraint(name = "name_key", columnNames = { "name" })})
public class SalesCataloge extends AbstractPersistable<Long> {
	
	
	@Column(name="name", nullable=false)
	private String name;
	
	@Column(name="sales_plan_category_id", nullable=false)
	private Long salesPlanCategoryId;
	
	@Column(name="is_deleted")
	private char isDeleted;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "salesCataloge", orphanRemoval = true)
	private Set<SalesCatalogeMapping> salescatalogemapping = new HashSet<SalesCatalogeMapping>();

	public SalesCataloge() {}
	
	public SalesCataloge(String name,Long salesPlanCategoryId) {
		this.name = name;
		this.salesPlanCategoryId = salesPlanCategoryId;
		this.isDeleted = 'N';
	}
	
	public Set<SalesCatalogeMapping> getDetails() {
		return salescatalogemapping;
	}

	public void addPlan(Set<SalesCatalogeMapping> selectedPlans) {
		/*this.salescatalogemapping.clear();*/
		for(SalesCatalogeMapping salesCatalogeMapping:this.salescatalogemapping){
			boolean isExist =false;
			for(SalesCatalogeMapping selectedPlan:selectedPlans){
				if(salesCatalogeMapping.getPlanId()==(selectedPlan.getPlanId()) 
						&& salesCatalogeMapping.getIsDeleted() =='N'){
					isExist=true;
					selectedPlans.remove(selectedPlan);break;
				}
			}
			if(!isExist){
				salesCatalogeMapping.delete();
			}
		}
		for(SalesCatalogeMapping selectedPlan:selectedPlans){
			selectedPlan.update(this);
			this.salescatalogemapping.add(selectedPlan);
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Long getSalesPlanCategoryId() {
		return salesPlanCategoryId;
	}

	public void setSalesPlanCategoryId(Long salesPlanCategoryId) {
		this.salesPlanCategoryId = salesPlanCategoryId;
	}

	private String[] getPlansAsIdStringArray() {
	 	
		 final List<String> roleIds = new ArrayList<>();
       	for (final SalesCatalogeMapping details : this.salescatalogemapping) {
       		roleIds.add(details.getId().toString());
       	}
       	return roleIds.toArray(new String[roleIds.size()]);
	 }
	
	public static SalesCataloge formJson(JsonCommand command) {
		String name = command.stringValueOfParameterNamed("name");
		Long salesPlanCategoryId = command.longValueOfParameterNamed("salesPlanCategoryId");
		String salesPlanCategoryName  = command.stringValueOfParameterNamed("salesPlanCategoryName");
	
		return new SalesCataloge(name,salesPlanCategoryId);
	}
	
	
	public Map<String, Object> update(JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
				
				final String nameNamedParamName = "name";
				
				if(command.isChangeInStringParameterNamed(nameNamedParamName, this.name)){
					final String newValue = command.stringValueOfParameterNamed(nameNamedParamName);
					actualChanges.put(nameNamedParamName, newValue);
					this.name = StringUtils.defaultIfEmpty(newValue,null);
				}
				
				final String salesPlanCategoryIdNamedParamName = "salesPlanCategoryId";
				
				if(command.isChangeInLongParameterNamed(salesPlanCategoryIdNamedParamName, this.salesPlanCategoryId)){
					final Long newValue = command.longValueOfParameterNamed(salesPlanCategoryIdNamedParamName);
					actualChanges.put(salesPlanCategoryIdNamedParamName, newValue);
					this.salesPlanCategoryId = newValue;
				}
				
				 final String planDetailsParamName = "planDetails";
			        if (command.isChangeInArrayParameterNamed(planDetailsParamName, getPlansAsIdStringArray())) {
			            final String[] newValue = command.arrayValueOfParameterNamed(planDetailsParamName);
			            actualChanges.put(planDetailsParamName, newValue);
			        }
				
				return actualChanges;
			}
	
	public char getIsDeleted() {
		return isDeleted;
	}
	
	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void delete() {	
	this.isDeleted = 'Y';
	this.name=this.name+"_"+this.getId()+"_DELETED";
	for(SalesCatalogeMapping salescatalogemapping:this.salescatalogemapping){
		salescatalogemapping.delete();
	}
	
	}


}
