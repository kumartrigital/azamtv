package org.mifosplatform.collectionbatch.ratableusagemetric.data;

import java.util.List;

public class RatableUsageMetricData {
	
	private Long id;
	private Long chargeCodeId;
	private Long templateId;
	private String rumName;
	private String rumExpression;
	private String templateName;
	private Long rumId;
	private Long fieldId;
	private String fieldName;
	private List<RatableUsageMetricData> FieldNamesDatas;

	public RatableUsageMetricData(Long id, Long chargeCodeId, Long templateId,String rumName,String rumExpression) {
		
		this.id = id;
		this.chargeCodeId = chargeCodeId;
		this.templateId = templateId;
		this.rumName = rumName;
		this.rumExpression = rumExpression;
	}


	public RatableUsageMetricData(Long templateId, String templateName) {
		this.templateId=templateId;
		this.templateName=templateName;
	}
	
	public RatableUsageMetricData(final Long rumId,final  String rumName,final Long id) {
		this.rumId=rumId;
		this.rumName=rumName;
		this.id=id;
	}


	public RatableUsageMetricData() {
		// TODO Auto-generated constructor stub
	}


	public RatableUsageMetricData(Long fieldId, String fieldName, final Long id1,  final Long id2) {
		this.fieldId=fieldId;
		this.fieldName=fieldName;
	}


	public Long getChargeCodeId() {
		return chargeCodeId;
	}


	public void setChargeCodeId(Long chargeCodeId) {
		this.chargeCodeId = chargeCodeId;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getTemplateId() {
		return templateId;
	}


	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}


	public String getRumName() {
		return rumName;
	}


	public void setRumName(String rumName) {
		this.rumName = rumName;
	}


	public String getRumExpression() {
		return rumExpression;
	}


	public void setRumExpression(String rumExpression) {
		this.rumExpression = rumExpression;
	}


	public void setFieldNamesData(List<RatableUsageMetricData> FieldNamesDatas) {
		this.FieldNamesDatas=FieldNamesDatas;
	}


	public List<RatableUsageMetricData> getFieldNamesDatas() {
		return FieldNamesDatas;
	}


	
	

}
