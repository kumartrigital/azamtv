package org.mifosplatform.collectionbatch.template.domain;

import java.util.ArrayList;
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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.billing.discountmaster.domain.DiscountDetails;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "r_input_master")
public class Templates extends AbstractPersistable<Long> {
	/*
	 * @Id
	 * 
	 * @GeneratedValue
	 * 
	 * @Column(name = "id") private Long id;
	 */

	@Column(name = "template_name", nullable = false, length = 10)
	private String templateName;

	@Column(name = "delimited", length = 10)
	private char delimited;

	@Column(name = "delimiter", nullable = false, length = 10)
	private String delimiter;

	@Column(name = "number_of_fields", nullable = false)
	private Long numberOfFields;

	@Column(name = "is_header", length = 10)
	private char isHeader;

	@Column(name = "header_record_type", nullable = false, length = 10)
	private String headerRecordType;

	@Column(name = "event_record_type", nullable = false, length = 10)
	private String eventRecordType;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "templates", orphanRemoval = true)
	private Set<TemplateField> templateField = new HashSet<TemplateField>();
	
	public Templates() {
	
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public char getDelimited() {
		return delimited;
	}

	public void setDelimited(char delimited) {
		this.delimited = delimited;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Long getNumberOfFields() {
		return numberOfFields;
	}

	public void setNumberOfFields(Long numberOfFields) {
		this.numberOfFields = numberOfFields;
	}

	public char getIsHeader() {
		return isHeader;
	}

	public void setIsHeader(char isHeader) {
		this.isHeader = isHeader;
	}

	public String getHeaderRecordType() {
		return headerRecordType;
	}

	public void setHeaderRecordType(String headerRecordType) {
		this.headerRecordType = headerRecordType;
	}

	public String getEventRecordType() {
		return eventRecordType;
	}

	public void setEventRecordType(String eventRecordType) {
		this.eventRecordType = eventRecordType;
	}

	public Set<TemplateField> getTemplateField() {
		return templateField;
	}

	public void setTemplateField(Set<TemplateField> templateField) {
		this.templateField = templateField;
	}

	public void addColumnsDetails(final Set<TemplateField> selectedColumns) {
		if (this != null) {
			this.templateField.clear();
		}
		for (TemplateField templateField : selectedColumns) {
			templateField.update(this);
			this.templateField.add(templateField);
		}
	}

	public Templates(final String templateName, final boolean delimited, final String delimiter, final Long numberOfFields,
			final boolean isHeader, final String headerRecordType, final String eventRecordType) {

		this.templateName = templateName;
		this.delimited = delimited ? 'Y' : 'N';
		this.delimiter = delimiter;
		this.numberOfFields = numberOfFields;
		this.isHeader = isHeader ? 'Y' : 'N';
		this.headerRecordType = headerRecordType;
		this.eventRecordType = eventRecordType;
	}

	public static Templates formJson(JsonCommand command) {

		String templateName = command.stringValueOfParameterNamed("templateName");
		boolean delimited = command.booleanPrimitiveValueOfParameterNamed("delimited");
		String delimiter = command.stringValueOfParameterNamed("delimiter");
		Long numberOfFields = command.longValueOfParameterNamed("numberOfFields");
		boolean isHeader = command.booleanPrimitiveValueOfParameterNamed("isHeader");
		String headerRecordType = command.stringValueOfParameterNamed("headerRecordType");
		String eventRecordType = command.stringValueOfParameterNamed("eventRecordType");

		return new Templates(templateName, delimited, delimiter, numberOfFields, isHeader, headerRecordType,
				eventRecordType);
	}

	/*public void addColumnDetails(Set<TemplateField> selectedColumns) {
		if (this.templateField != null) {
			this.templateField.clear();
		}
		for (TemplateField templateField : selectedColumns) {
			templateField.update(this);
			this.templateField.add(templateField);
		}

	}*/
	public Map<String, Object> update(final JsonCommand command) {
    final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String templateNameNamedParamName = "templateName";
		final String delimitedNamedParamName = "delimited";
		final String delimiterNamedParamName = "delimiter";
		final String numberOfFieldsNamedParamName = "numberOfFields";
		final String isHeaderNamedParamName = "isHeader";
		final String headerRecordTypeNamedParamName = "headerRecordType";
		final String eventRecordTypeNamedParamName = "eventRecordType";
			
		if(command.isChangeInStringParameterNamed(templateNameNamedParamName, this.templateName)){
			final String newValue = command.stringValueOfParameterNamed(templateNameNamedParamName);
			actualChanges.put(templateNameNamedParamName, newValue);
			this.templateName = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final boolean delimitedReqParamName =command.booleanPrimitiveValueOfParameterNamed("delimited");
		this.delimited=delimitedReqParamName?'Y':'N';
		
		if(command.isChangeInStringParameterNamed(delimiterNamedParamName, this.delimiter)){
			final String newValue = command.stringValueOfParameterNamed(delimiterNamedParamName);
			actualChanges.put(delimiterNamedParamName, newValue);
			this.delimiter = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		if(command.isChangeInLongParameterNamed(numberOfFieldsNamedParamName,this.numberOfFields)){
			final Long newValue = command.longValueOfParameterNamed(numberOfFieldsNamedParamName);
			actualChanges.put(numberOfFieldsNamedParamName, newValue);
			this.numberOfFields =newValue;
		}
		final boolean isHeaderParamName =command.booleanPrimitiveValueOfParameterNamed("isHeader");
		this.isHeader=isHeaderParamName?'Y':'N';
		
		if(command.isChangeInStringParameterNamed(headerRecordTypeNamedParamName, this.headerRecordType)){
			final String newValue = command.stringValueOfParameterNamed(headerRecordTypeNamedParamName);
			actualChanges.put(headerRecordTypeNamedParamName, newValue);
			this.headerRecordType = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		if(command.isChangeInStringParameterNamed(eventRecordTypeNamedParamName, this.eventRecordType)){
			final String newValue = command.stringValueOfParameterNamed(eventRecordTypeNamedParamName);
			actualChanges.put(eventRecordTypeNamedParamName, newValue);
			this.eventRecordType = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		/* final String columnsParamName = "columns";
	     if (command.isChangeInArrayParameterNamed(columnsParamName, getColumnsAsIdStringArray())) {
	        final String[] newValue = command.arrayValueOfParameterNamed(columnsParamName);
	        actualChanges.put(columnsParamName, newValue);
	     }
		*/
		return actualChanges;
		
	}
	
	/* private String[] getColumnsAsIdStringArray() {
		 	
		 final List<String> roleIds = new ArrayList<>();
        	for (final TemplateField detail : this.templateField) {
        		roleIds.add(detail.getId().toString());
        	}
        	return roleIds.toArray(new String[roleIds.size()]);
	 }*/

	
	public void addDetails(TemplateField templateField) {
		templateField.update(this);
		this.templateField.add(templateField);
	}
	

}
