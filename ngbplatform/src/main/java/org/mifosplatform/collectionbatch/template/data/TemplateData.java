package org.mifosplatform.collectionbatch.template.data;

import java.util.List;

public class TemplateData {

	private Long templateId;
	private String templateName;
	private String delimited;
	private String delimiter;
	private Long numberOfFields;
	private String isheader;
	private String headerecordtype;
	private String eventrecordtype;
	private String fieldName;
	private String fieldType;
	private Long length;
	private String identifierType;
	private List<TemplateData> templateDatas;

	public TemplateData(Long templateId, String templateName, String delimited, String delimiter, Long numberOfFields,
			String isheader, String headerecordtype, String eventrecordtype, String fieldName, String fieldType,
			Long length, String identifierType) {

		this.templateId = templateId;
		this.templateName = templateName;
		this.delimited = delimited;
		this.delimiter = delimiter;
		this.numberOfFields = numberOfFields;
		this.isheader = isheader;
		this.headerecordtype = headerecordtype;
		this.eventrecordtype = eventrecordtype;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.length = length;
		this.identifierType = identifierType;

	}
	
	public TemplateData(List<TemplateData> templateDatas) {
		this.templateDatas = templateDatas;
	}

}
