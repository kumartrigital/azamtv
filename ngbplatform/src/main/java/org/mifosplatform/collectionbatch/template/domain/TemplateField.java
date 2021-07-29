package org.mifosplatform.collectionbatch.template.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "r_input_details")
public class TemplateField {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "template_id")
	private Templates templates;

	@Column(name = "field_name", nullable = false, length = 10)
	private String fieldName;

	@Column(name = "field_type", nullable = false, length = 10)
	private String fieldType;

	@Column(name = "length", length = 10)
	private Long length;

	@Column(name = "identifier_type", nullable = false, length = 10)
	private String identifierType;
 
	public TemplateField() {
	
	}
	
	
	public TemplateField(final String fieldName, final String fieldType, final Long length,
			final String identifierType) {
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.length = length;
		this.identifierType = identifierType;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Templates getTemplates() {
		return templates;
	}

	public void setTemplates(Templates templates) {
		this.templates = templates;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}

	public void update(Templates templates) {

		this.templates = templates;

	}

}
