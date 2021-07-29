/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.template.service;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.template.domain.Template;
import org.mifosplatform.template.domain.TemplateEntity;
import org.mifosplatform.template.domain.TemplateMapper;
import org.mifosplatform.template.domain.TemplateRepository;
import org.mifosplatform.template.domain.TemplateType;
import org.mifosplatform.template.exception.TemplateNotFoundException;
import org.mifosplatform.template.serialization.TemplateCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class TemplateServiceImp implements TemplateService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TemplateServiceImp.class);
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_TEXT = "text";
   // private static final String PROPERTY_MAPPERS = "mappers";
    private static final String PROPERTY_ENTITY = "entity";
    private static final String PROPERTY_TYPE = "type";

    private final TemplateRepository templateRepository;
    private final TemplateCommandFromApiJsonDeserializer apiJsonDeserializer;
    
    @Autowired
    public TemplateServiceImp(final TemplateRepository templateRepository,
    		final TemplateCommandFromApiJsonDeserializer apiJsonDeserializer){
    	
    	this.templateRepository = templateRepository;
    	this.apiJsonDeserializer = apiJsonDeserializer;
    }
    
    

    @Transactional
    @Override
    public CommandProcessingResult createTemplate(final JsonCommand command) {
       
    try{
    	this.apiJsonDeserializer.validateForCreate(command.json());
        // FIXME - handle cases where data integrity constraints are fired from
        // database when saving.
        final Template template = Template.fromJson(command);
        this.templateRepository.saveAndFlush(template);
        return new CommandProcessingResultBuilder().withEntityId(template.getId()).build();
       }catch(final DataIntegrityViolationException dve){
    	    handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
      }
    }
    
	@Transactional
    @Override
    public CommandProcessingResult updateTemplate(final Long templateId, final JsonCommand command) {
     
		try{
		this.apiJsonDeserializer.validateForCreate(command.json());
        // FIXME - handle cases where data integrity constraints are fired from
        // database when saving.
        final Template template = findOneById(templateId);
        template.setName(command.stringValueOfParameterNamed(PROPERTY_NAME));
        template.setText(command.stringValueOfParameterNamed(PROPERTY_TEXT));
        template.setEntity(TemplateEntity.values()[command.integerValueSansLocaleOfParameterNamed(PROPERTY_ENTITY)]);
        final int templateTypeId = command.integerValueSansLocaleOfParameterNamed(PROPERTY_TYPE);
        TemplateType type = null;
        switch (templateTypeId) {
            case 0 :
                type = TemplateType.DOCUMENT;
                break;
            case 1 :
                type = TemplateType.EMAIL;
                break;
            case 2 :
            	type = TemplateType.SMS;
            	 break;
            case 3 :
            	type = TemplateType.OSD;
            	 break;
        }
        template.setType(type);

        final JsonArray array = command.arrayOfParameterNamed("mappers");
        final List<TemplateMapper> mappersList = new ArrayList<>();
        for (final JsonElement element : array) {
            mappersList.add(new TemplateMapper(element.getAsJsonObject()
                    .get("mappersorder").getAsInt(), element.getAsJsonObject()
                    .get("mapperskey").getAsString(), element.getAsJsonObject()
                    .get("mappersvalue").getAsString()));
        }
        template.setMappers(mappersList);

        this.templateRepository.saveAndFlush(template);

        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(template.getId()).build();
    }catch(final DataIntegrityViolationException dve){
	    handleCodeDataIntegrityIssues(command, dve);
		return new CommandProcessingResult(Long.valueOf(-1));
      }
	}

    @Transactional
    @Override
    public CommandProcessingResult removeTemplate(final Long templateId) {
    	
        final Template template = findOneById(templateId);
        template.delete();
        this.templateRepository.save(template);
        return new CommandProcessingResultBuilder().withEntityId(templateId).build();
    }

    /*@Transactional
    @Override
    public Template updateTemplate(final Template template) {
        return this.templateRepository.saveAndFlush(template);
    }*/

    @Override
    public List<Template> getAllByEntityAndType(final TemplateEntity entity, final TemplateType type) {

        return this.templateRepository.findByEntityAndType(entity, type);
    }
    

    @Override
    public List<Template> getAll() {
        return this.templateRepository.findIsNotDeleted();
    }

    @Override
    public Template findOneById(final Long id) {
        final Template template = this.templateRepository.findOne(id);
        if (template == null) {
            throw new TemplateNotFoundException(id);
        }
        return template;
    }
    
    private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("unq_name")) {
			final String name = command.stringValueOfParameterNamed("name");
			throw new PlatformDataIntegrityException("error.msg.tempalte.duplicate.name",
					"A template with '" + name + "'already exists","name", name);
		}

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "+ realCause.getMessage());

	}
}
