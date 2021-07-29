package org.mifosplatform.portfolio.clientdiscount.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.portfolio.client.domain.Client;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_client_discount")
public class ClientDiscount extends AbstractPersistable<Long> {
	
	
	@Column(name="level", nullable=false)
	private String level;
	
	@Column(name="discount_type", nullable=false)
	private String discountType;
	
	@Column(name="discount_value", nullable=false)
	private Long discountValue;
	
	@Column(name="is_deleted")
	private char isDeleted;

	@ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

	public ClientDiscount(){}
	
	public ClientDiscount(String level, String discountType, Long discountValue) {
		
		this.level = level;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.isDeleted = 'N';
	}
	
	public ClientDiscount(String level, String discountType, Long discountValue,Client client) {
	
		this.level = level;
		this.discountType = discountType;
		this.discountValue = discountValue;
		this.isDeleted = 'N';
		this.client = client;
	}

	public String getLevel(){
		return level;
	}
	
	public void setLevel(String level){
		this.level = level;
	}
	
	public String getDiscountType() {
		return discountType;
	}
	
	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public Long getDiscountValue() {
		return discountValue;
	}
	
	public void setDiscountValue(Long discountValue) {
		this.discountValue = discountValue;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	public static ClientDiscount formJson(JsonCommand command) {
		String level = command.stringValueOfParameterNamed("level");
		String discountType = command.stringValueOfParameterNamed("discountType");
		Long discountValue  = command.longValueOfParameterNamed("discountValue");
		
		return new ClientDiscount(level, discountType, discountValue);
	}
	
	public Map<String, Object> update(JsonCommand command) {
		
final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String levelNameNamedParamName = "level";
		final String discountTypeCategoryNamedParamName = "discountType";
		final String discountValueNamedParamName = "discountValue";

		
		if(command.isChangeInStringParameterNamed(levelNameNamedParamName, this.level)){
			final String newValue = command.stringValueOfParameterNamed(levelNameNamedParamName);
			actualChanges.put(levelNameNamedParamName, newValue);
			this.level = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		if(command.isChangeInStringParameterNamed(discountTypeCategoryNamedParamName, this.discountType)){
			final String newValue = command.stringValueOfParameterNamed(discountTypeCategoryNamedParamName);
			actualChanges.put(discountTypeCategoryNamedParamName, newValue);
			this.discountType = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		if(command.isChangeInLongParameterNamed(discountValueNamedParamName, this.discountValue)){
			final Long newValue = command.longValueOfParameterNamed(discountValueNamedParamName);
			actualChanges.put(discountValueNamedParamName, newValue);
			this.discountValue = newValue;
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
	}

	public String celcomRequestInputForClientDiscount(String logedInUser) {
    	  	
		Long flatValue= this.discountValue;
		Long percentageValue = this.discountValue;
		if(this.getDiscountType().equalsIgnoreCase("flat")){
			percentageValue=Long.valueOf(0);
		}else{
			flatValue=Long.valueOf(0);
		}
    	StringBuilder sb = new StringBuilder("<COB_OP_CUST_UPDATE_CUSTOMER_inputFlist>");
    	sb.append("<POID>0.0.0.1 /account "+this.client.getPoid()+" 0</POID>");
    	sb.append("<PROGRAM_NAME>CRM|"+logedInUser+"</PROGRAM_NAME>");
		sb.append("<PROFILES elem=\"0\">");
		sb.append("<INHERITED_INFO>");
		sb.append("<CUSTOMER_CARE_INFO>");
		sb.append("<PARENT>0.0.0.1 /account "+this.client.getOffice().getPoId()+" 0</PARENT>");
		sb.append("<COB_FLD_CHILD_DSC_AMT>"+flatValue+"</COB_FLD_CHILD_DSC_AMT>");
		sb.append("<COB_FLD_CHILD_DSC_PERC>"+percentageValue+"</COB_FLD_CHILD_DSC_PERC>");
		sb.append("</CUSTOMER_CARE_INFO>");
		sb.append("</INHERITED_INFO>");
		sb.append("</PROFILES>");
		sb.append("</COB_OP_CUST_UPDATE_CUSTOMER_inputFlist>");
		return sb.toString();	
		
	
   }
	

}
