package org.mifosplatform.collectionbatch.template.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifosplatform.billing.discountmaster.domain.DiscountDetails;
import org.mifosplatform.billing.discountmaster.domain.DiscountMaster;
import org.mifosplatform.collectionbatch.template.domain.TemplateField;
import org.mifosplatform.collectionbatch.template.domain.Templates;
import org.mifosplatform.collectionbatch.template.domain.TemplatesRepository;
import org.mifosplatform.collectionbatch.template.serialization.TemplatesCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class TemplateWritePlatformServiceImpl implements TemplateWritePlatformService {

	private final static Logger logger = (Logger) LoggerFactory.getLogger(TemplateWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final TemplatesCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final TemplatesRepository templatesRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final TemplateFieldsRepository templateFieldsRepository;
	
	@Autowired
	public TemplateWritePlatformServiceImpl(PlatformSecurityContext context,
			TemplatesCommandFromApiJsonDeserializer apiJsonDeserializer, final TemplatesRepository templatesRepository,
			final FromJsonHelper fromApiJsonHelper,
			final TemplateFieldsRepository templateFieldsRepository) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.templatesRepository = templatesRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.templateFieldsRepository=templateFieldsRepository;
		
	}

	@Transactional
	@Override
	public CommandProcessingResult createTemplates(JsonCommand command) {

		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			Templates templates = Templates.formJson(command);

			/*
			 * final String[] columns = command.arrayValueOfParameterNamed("columns"); final
			 * Set<TemplateField> selectedColumns = assembleSetOfColumns(columns);
			 * templates.addColumnDetails(selectedColumns);
			 */
			final Set<TemplateField> allColumns = new HashSet<>();
			final JsonArray columnArray = command.arrayOfParameterNamed("columns");
			String[] columns = new String[columnArray.size()];
			if (columnArray.size() > 0) {
				for (int i = 0; i < columnArray.size(); i++) {
					columns[i] = columnArray.get(i).toString();
				}
				for (String columns1 : columns) {
					final JsonElement columnElement = this.fromApiJsonHelper.parse(columns1);
					TemplateField templateField = this.assembleDetails(command, columnElement);
					allColumns.add(templateField);
				}
			}

			templates.addColumnsDetails(allColumns);

			this.templatesRepository.save(templates);
			return new CommandProcessingResultBuilder().withEntityId(templates.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	private TemplateField assembleDetails(JsonCommand command, JsonElement columnElement) {

		final String fieldName = this.fromApiJsonHelper.extractStringNamed("fieldName", columnElement);
		final String fieldType = this.fromApiJsonHelper.extractStringNamed("fieldType", columnElement);
		final Long length = this.fromApiJsonHelper.extractLongNamed("length", columnElement);
		final String identifierType = this.fromApiJsonHelper.extractStringNamed("identifierType", columnElement);
		TemplateField templateField = new TemplateField(fieldName, fieldType, length, identifierType);
		return templateField;

	}

	/*
	 * private Set<TemplateField> assembleSetOfColumns(final String[] columnArray) {
	 * final Set<TemplateField> allColumns = new HashSet<>(); if
	 * (!ObjectUtils.isEmpty(columnArray)) { for (final String templateFields :
	 * columnArray) { if (templateFields != null) { TemplateField templateField =
	 * new TemplateField(null, null, null, null); allColumns.add(templateField); } }
	 * }
	 * 
	 * return allColumns; }
	 */

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();

		throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

	@Override
	public CommandProcessingResult updateTemplates(Long templateId, JsonCommand command) {

	/*	try {
			context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final Templates templates = retrieveTemplateBy(templateId);
			final Map<String, Object> changes = templates.update(command);
			final Set<TemplateField> allColumns = new HashSet<>();
			if(changes.containsKey("columns")) {
				final JsonArray columnArray = command.arrayOfParameterNamed("columns");
				String[] columns = new String[columnArray.size()];
				if (columnArray.size() > 0) {
					for (int i = 0; i < columnArray.size(); i++) {
						columns[i] = columnArray.get(i).toString();
					}
					for (String columns1 : columns) {
						final JsonElement columnElement = this.fromApiJsonHelper.parse(columns1);
						TemplateField templateField = this.assembleDetails(command, columnElement);
						allColumns.add(templateField);
					}
				}

				templates.addColumnsDetails(allColumns);


			}
				if (changes.containsKey("columns")) {
				final String[] templateIds = (String[]) changes.get("columns");
				TemplateField templateField = this.assembleDetails(command, columnElement);
				allColumns.add(templateField);
			}

			this.templatesRepository.save(templates);

			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(templateId) //
					.with(changes) //
					.build();
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}*/
		
		
		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final Templates templates = retrieveTemplateBy(templateId);
			List<TemplateField> details=new ArrayList<>(templates.getTemplateField());
			final JsonArray templatessArray = command.arrayOfParameterNamed("columns").getAsJsonArray();
			    String[] states =null;
			    states=new String[templatessArray.size()];
			    for(int i=0; i<templatessArray.size();i++){
			    	states[i] =templatessArray.get(i).toString();
			    }
				 for (String  columns : states) {
					  
					 final JsonElement element = fromApiJsonHelper.parse(columns);
					 
					 final String fieldName = this.fromApiJsonHelper.extractStringNamed("fieldName", element);
						final String fieldType = this.fromApiJsonHelper.extractStringNamed("fieldType", element);
						final Long length = this.fromApiJsonHelper.extractLongNamed("length", element);
						final Long id = fromApiJsonHelper.extractLongNamed("id", element);
						final String identifierType = this.fromApiJsonHelper.extractStringNamed("identifierType", element);
					 
						if(id != null){
							TemplateField templateFields =this.templateFieldsRepository.findOne(id);
							
							if(templateFields != null){
								templateFields.setFieldName(fieldName);
								templateFields.setFieldType(fieldType);
								templateFields.setLength(length);
								templateFields.setIdentifierType(identifierType);
								
								this.templateFieldsRepository.saveAndFlush(templateFields);
								if(details.contains(templateFields)){
								   details.remove(templateFields);
								}
							}
							}else {
								TemplateField templateField = new TemplateField(fieldName, fieldType, length, identifierType);
								templates.addDetails(templateField);
							}
							
					  }
						
				 templates.getTemplateField().removeAll(details);

					final Map<String, Object> changes = templates.update(command);
					this.templatesRepository.saveAndFlush(templates);
					
					return new CommandProcessingResultBuilder().withCommandId(command.commandId())
							       .withEntityId(templates.getId()).with(changes).build();
						
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
		return new CommandProcessingResult(Long.valueOf(-1));

	}
					 
		
	}

	private Templates retrieveTemplateBy(final Long templateId) {
		 final Templates templates = this.templatesRepository.findOne(templateId);
		 if (templates == null) { throw new CodeNotFoundException(templateId.toString()); }
	        return templates;
	}
	
}
