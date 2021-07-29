/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.service;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.exception.GlobalConfigurationPropertyNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

@Service
public class GlobalConfigurationWritePlatformServiceJpaRepositoryImpl implements GlobalConfigurationWritePlatformService {

    private final PlatformSecurityContext context;
    private final ConfigurationRepository repository;
    private final GlobalConfigurationDataValidator globalConfigurationDataValidator;

    @Autowired
    public GlobalConfigurationWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final ConfigurationRepository codeRepository,
            final GlobalConfigurationDataValidator globalConfigurationDataValidator) {
        this.context = context;
        this.repository = codeRepository;
        this.globalConfigurationDataValidator=globalConfigurationDataValidator;
    }

    
    @Transactional
    @Override
    public CommandProcessingResult update(final Long configId, final JsonCommand command) {

        this.context.authenticatedUser();

        try {
            this.globalConfigurationDataValidator.validateForUpdate(command);

            final Configuration configItemForUpdate = this.repository.findOne(configId);

            final Map<String, Object> changes = configItemForUpdate.update(command);

            if (!changes.isEmpty()) {
                this.repository.save(configItemForUpdate);
            }

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(configId).with(changes).build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command,dve);
            return CommandProcessingResult.empty();
        }

    }
    
    @Override
	public CommandProcessingResult create(JsonCommand command) {
		try{
			
			this.context.authenticatedUser();
			this.globalConfigurationDataValidator.validateForCreate(command);
			final String userName = command.stringValueOfParameterNamed(ConfigurationConstants.NAME);
			final String mailId = command.stringValueOfParameterNamed(ConfigurationConstants.MAIL);
			final String password = command.stringValueOfParameterNamed(ConfigurationConstants.PASSWORD);
			final String hostName=command.stringValueOfParameterNamed(ConfigurationConstants.HOSTNAME);
			final String port=command.stringValueOfParameterNamed(ConfigurationConstants.PORT);
			final String starttls=command.stringValueOfParameterNamed(ConfigurationConstants.STARTTLS);
			final String setContentString = command.stringValueOfParameterNamed(ConfigurationConstants.SETCONENTSTRING);
						
			final String unencodedPassword = password;
			String encodedString = Base64.encodeBase64String(unencodedPassword.getBytes());
			
			/**  For Decoding above string
			 * 
			 * byte[] decodeString = Base64.decodeBase64(encodedString);
			 * System.out.println("decodeString: "+new String(decodeString));
			 */

			JsonObject json =new JsonObject();
			json.addProperty("mailId", mailId);
			json.addProperty("password", encodedString);
			json.addProperty("hostName", hostName);
			json.addProperty("port", port);
			json.addProperty("starttls", starttls);
			json.addProperty("setContentString", setContentString);
			final Configuration globalConfigurationProperty = Configuration.fromJson(command, userName, json.toString());
	        
			this.repository.save(globalConfigurationProperty);
			
			return new CommandProcessingResultBuilder().withEntityId(globalConfigurationProperty.getId()).build();
		}
		catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command,dve);
            return CommandProcessingResult.empty();
        }
	}
    
    @SuppressWarnings("unused")
	private Configuration retrieveBy(final String propertyName) {
        final Configuration property = this.repository.findOneByName(propertyName);
        if (property == null) { throw new GlobalConfigurationPropertyNotFoundException(propertyName); }
        return property;
    }
    
    private void handleDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("name_config")) {
            final String username = command.stringValueOfParameterNamed(ConfigurationConstants.NAME);
            final StringBuilder defaultMessageBuilder = new StringBuilder("Name with").append(username)
                    .append(" already exists.");
            throw new PlatformDataIntegrityException("error.msg.smtp.duplicate.name", defaultMessageBuilder.toString(), "username",
                    username);
        }
    //    logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.globalConfiguration.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}