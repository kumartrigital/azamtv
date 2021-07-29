package org.mifosplatform.collectionbatch.ratableusagemetric.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.broadcaster.domain.Broadcaster;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "r_rum")
public class RatableUsageMetric extends AbstractPersistable<Long>{

	@Column(name="charge_code_Id", nullable=false, length=10)
	private Long chargeCodeId;
	
	@Column(name="template_id", nullable=false, length=100)
	private Long templateId;
	
	@Column(name="rum_name", nullable=false, length=100)
	private String rumName;
	
	@Column(name="rum_expression", nullable=false, length=100)
	private String rumExpression;
	
	
	public RatableUsageMetric()
	{
	
	}
	

	public RatableUsageMetric(Long chargeCodeId, Long templateId,String rumName, String rumExpression) {
		
		this.chargeCodeId = chargeCodeId;
		this.templateId = templateId;
		this.rumName = rumName;
		this.rumExpression = rumExpression;
		
	}


	public static RatableUsageMetric formJson(JsonCommand command) {
		
		
		Long chargeCodeId = command.longValueOfParameterNamed("chargeCodeId");
		Long templateId = command.longValueOfParameterNamed("templateId");
		String rumName = command.stringValueOfParameterNamed("rumName");
		String rumExpression = command.stringValueOfParameterNamed("rumExpression");
		
		
		return new RatableUsageMetric(chargeCodeId, templateId, rumName, rumExpression);
	}





	public Map<String, Object> update(JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String chargeCodeIdNamedParamName = "chargeCodeId";
		final String templateIdNamedParamName = "templateId";
		final String rumNameNamedParamName = "rumName";
		final String rumExpressionNamedParamName = "rumExpression";
		
		
		if(command.isChangeInLongParameterNamed(chargeCodeIdNamedParamName, this.chargeCodeId)){
			final String newValue = command.stringValueOfParameterNamed(chargeCodeIdNamedParamName);
			actualChanges.put(chargeCodeIdNamedParamName, newValue);
			this.chargeCodeId = new Long(newValue);
		}
		
		if(command.isChangeInLongParameterNamed(templateIdNamedParamName, this.templateId)){
			final String newValue = command.stringValueOfParameterNamed(templateIdNamedParamName);
			actualChanges.put(templateIdNamedParamName, newValue);
			this.chargeCodeId = new Long(newValue);
		}
		
		if(command.isChangeInStringParameterNamed(rumNameNamedParamName, this.rumName)){
			final String newValue = command.stringValueOfParameterNamed(rumNameNamedParamName);
			actualChanges.put(rumNameNamedParamName, newValue);
			this.rumName = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		if(command.isChangeInStringParameterNamed(rumExpressionNamedParamName, this.rumExpression)){
			final String newValue = command.stringValueOfParameterNamed(rumExpressionNamedParamName);
			actualChanges.put(rumExpressionNamedParamName, newValue);
			this.rumExpression = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		return actualChanges;
	
	}
	

}
