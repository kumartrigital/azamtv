package org.mifosplatform.crm.clientprospect.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.axis2.databinding.types.soapencoding.Decimal;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.salescataloge.domain.SalesCataloge;
import org.mifosplatform.organisation.salescatalogemapping.domain.SalesCatalogeMapping;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_quote") 

public class Quote extends AbstractPersistable<Long> {
	
	
	@Column(name="lead_id")
	private Long leadId;
	
	@Column(name = "quote_date")
	private Date quoteDate;
    
	@Column(name = "quote_status")
	private String quoteStatus;
	
	@Column(name = "total_charge")
	private BigDecimal totalCharge;
	
	@Column(name = "notes")
	private String Notes;
	
	@Column(name = "quote_no")
	private String quoteNo;
	
	@Column(name="is_deleted")
	private char isDeleted;
	
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "quote", orphanRemoval = true)
	private Set<QuoteOrder> quoteorder = new HashSet<QuoteOrder>();


	
   public Quote() {}
	
	public Quote(Long leadId,Date quoteDate,String quoteStatus,BigDecimal totalCharge,String Notes,String quoteNo ) {
		this.leadId = leadId;
		this.quoteDate = quoteDate;
		this.quoteStatus = quoteStatus;
		this.totalCharge = totalCharge;
		this.Notes = Notes;
		this.quoteNo = quoteNo;
		this.isDeleted = 'N';
	}
	
	private String[] getServiceAsIdStringArray() {
	 	
		 final List<String> roleIds = new ArrayList<>();
      	for (final QuoteOrder details : this.quoteorder) {
      		roleIds.add(details.getId().toString());
      	}
      	return roleIds.toArray(new String[roleIds.size()]);
	 }
	
	
	
	
	public static Quote formJson(JsonCommand command) {
		Long leadId = command.longValueOfParameterNamed("leadId");
		Date quoteDate = command.DateValueOfParameterNamed("quoteDate");
		String quoteStatus = command.stringValueOfParameterNamed("quoteStatus");
		BigDecimal totalCharge = command.bigDecimalValueOfParameterNamed("totalCharge");
		String Notes  = command.stringValueOfParameterNamed("Notes");
		String quoteNo = command.stringValueOfParameterNamed("quoteNo");
		return new Quote(leadId,quoteDate,quoteStatus,totalCharge,Notes,quoteNo);
	}
	
	
	public Long getLeadId() {
		return leadId;
	}

	public void setLeadId(Long leadId) {
		this.leadId = leadId;
	}

	public Date getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	public String getQuoteStatus() {
		return quoteStatus;
	}

	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}

	public BigDecimal getTotalCharge() {
		return totalCharge;
	}

	public void setTotalCharge(BigDecimal totalCharge) {
		this.totalCharge = totalCharge;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}
	
	public String getQuoteNO() {
		return quoteNo;
	}

	public void setQuoteNo(String quoteNo) {
		this.quoteNo = quoteNo;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public Set<QuoteOrder> getDetails() {
		return quoteorder;
	}

	
	public void addServicePlan(Set<QuoteOrder> selectedServicePlans) {
		/*this.salescatalogemapping.clear();*/
		for(QuoteOrder quoteOrder:this.quoteorder){
			boolean isExist =false;
			for(QuoteOrder selectedServicePlan:selectedServicePlans){
				if(quoteOrder.getPlanName()==(selectedServicePlan.getPlanName()) && quoteOrder.getServiceCode()==(selectedServicePlan.getServiceCode())
					 && quoteOrder.getIsDeleted() =='N'){
					isExist=true;
					selectedServicePlans.remove(selectedServicePlan);break;
				}
			}
			if(!isExist){
				quoteOrder.delete();
			}
		}
		for(QuoteOrder selectedServicePlan:selectedServicePlans){
			selectedServicePlan.update(this);
			this.quoteorder.add(selectedServicePlan);
		}
		
		
		
	}
	
	public Map<String, Object> update(JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
				
				final String nameNamedParamName = "Notes";
				
				if(command.isChangeInStringParameterNamed(nameNamedParamName, this.Notes)){
					final String newValue = command.stringValueOfParameterNamed(nameNamedParamName);
					actualChanges.put(nameNamedParamName, newValue);
					this.Notes = StringUtils.defaultIfEmpty(newValue,null);
				}
				
				
				
	           final String totalChargeNamedParamName = "totalCharge";
				
				if(command.isChangeInBigDecimalParameterNamed(totalChargeNamedParamName, this.totalCharge)){
					final BigDecimal newValue = command.bigDecimalValueOfParameterNamed(totalChargeNamedParamName);
					actualChanges.put(totalChargeNamedParamName, newValue);
					this.totalCharge = newValue;
				}
				/*final String salesPlanCategoryIdNamedParamName = "salesPlanCategoryId";
				
				if(command.isChangeInLongParameterNamed(salesPlanCategoryIdNamedParamName, this.salesPlanCategoryId)){
					final Long newValue = command.longValueOfParameterNamed(salesPlanCategoryIdNamedParamName);
					actualChanges.put(salesPlanCategoryIdNamedParamName, newValue);
					this.salesPlanCategoryId = newValue;
				}*/
				
				/* final String servicePlanDetailsParamName = "servicePlanDetails";
			        if (command.isChangeInArrayParameterNamed(servicePlanDetailsParamName, getServiceAsIdStringArray())) {
			            final String[] newValue = command.arrayValueOfParameterNamed(servicePlanDetailsParamName);
			            actualChanges.put(servicePlanDetailsParamName, newValue);
			        }*/
				
				return actualChanges;
			}
	
	

	public void delete() {
		this.isDeleted = 'Y';	
		for(QuoteOrder quote:this.quoteorder){
			quote.delete();
		}
	}
	
}
