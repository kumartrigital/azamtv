/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.service;

import java.util.Map;

import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.codes.exception.CodeValueNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.documentmanagement.domain.Document;
import org.mifosplatform.infrastructure.documentmanagement.domain.DocumentRepository;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.command.ClientIdentifierCommand;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientIdentifier;
import org.mifosplatform.portfolio.client.domain.ClientIdentifierRepository;
import org.mifosplatform.portfolio.client.domain.ClientRepositoryWrapper;
import org.mifosplatform.portfolio.client.exception.ClientIdentifierNotFoundException;
import org.mifosplatform.portfolio.client.exception.DuplicateClientIdentifierException;
import org.mifosplatform.portfolio.client.serialization.ClientIdentifierCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hugo
 *
 */
@Service
public class ClientIdentifierWritePlatformServiceImp implements ClientIdentifierWritePlatformService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientIdentifierWritePlatformServiceImp.class);

    private final PlatformSecurityContext context;
    private final ClientRepositoryWrapper clientRepository;
    private final ClientIdentifierRepository clientIdentifierRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final ClientIdentifierCommandFromApiJsonDeserializer apiJsonDeserializer;
    private final DocumentRepository documentRepository;
    

    @Autowired
    public ClientIdentifierWritePlatformServiceImp(final PlatformSecurityContext context,
            final ClientRepositoryWrapper clientRepository, final ClientIdentifierRepository clientIdentifierRepository,
            final CodeValueRepositoryWrapper codeValueRepository,
            final ClientIdentifierCommandFromApiJsonDeserializer apiJsonDeserializer,final DocumentRepository documentRepository) {
        this.context = context;
        this.clientRepository = clientRepository;
        this.clientIdentifierRepository = clientIdentifierRepository;
        this.codeValueRepository = codeValueRepository;
        this.apiJsonDeserializer = apiJsonDeserializer;
        this.documentRepository = documentRepository;
    }

    /* (non-Javadoc)
     * @see #addClientIdentifier(java.lang.Long, org.mifosplatform.infrastructure.core.api.JsonCommand)
     */
    @Transactional
    @Override
    public CommandProcessingResult addClientIdentifier(final Long clientId, final JsonCommand command) {

        this.context.authenticatedUser();
        final ClientIdentifierCommand clientIdentifierCommand = this.apiJsonDeserializer.commandFromApiJson(command.json());
        clientIdentifierCommand.validateForCreate();

        final String documentKey = clientIdentifierCommand.getDocumentKey();
        String documentTypeLabel = null;
        Long documentTypeId = null;
        try {
            final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);

            final CodeValue documentType = this.codeValueRepository.findOneWithNotFoundDetection(clientIdentifierCommand
                    .getDocumentTypeId());
            documentTypeId = documentType.getId();
            documentTypeLabel = documentType.label();

            final ClientIdentifier clientIdentifier = ClientIdentifier.fromJson(client, documentType, command);

            this.clientIdentifierRepository.save(clientIdentifier);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(client.officeId()) //
                    .withClientId(clientId) //
                    .withEntityId(clientIdentifier.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleClientIdentifierDataIntegrityViolation(documentTypeLabel, documentTypeId, documentKey, dve);
            return CommandProcessingResult.empty();
        }
    }

    /* (non-Javadoc)
     * @see #updateClientIdentifier(java.lang.Long, java.lang.Long, org.mifosplatform.infrastructure.core.api.JsonCommand)
     */
    @Transactional
    @Override
    public CommandProcessingResult updateClientIdentifier(final Long clientId, final Long identifierId, final JsonCommand command) {

        this.context.authenticatedUser();
        final ClientIdentifierCommand clientIdentifierCommand = this.apiJsonDeserializer.commandFromApiJson(command.json());
        clientIdentifierCommand.validateForUpdate();

        String documentTypeLabel = null;
        String documentKey = null;
        Long documentTypeId = clientIdentifierCommand.getDocumentTypeId();
        try {
            CodeValue documentType = null;

            final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
            final ClientIdentifier clientIdentifierForUpdate = this.clientIdentifierRepository.findOne(identifierId);
            if (clientIdentifierForUpdate == null) { throw new ClientIdentifierNotFoundException(identifierId); }

            final Map<String, Object> changes = clientIdentifierForUpdate.update(command);

            if (changes.containsKey("documentTypeId")) {
                documentType = this.codeValueRepository.findOneWithNotFoundDetection(documentTypeId);
                if (documentType == null) { throw new CodeValueNotFoundException(documentTypeId); }

                documentTypeId = documentType.getId();
                documentTypeLabel = documentType.label();
                clientIdentifierForUpdate.update(documentType);
            }

            if (changes.containsKey("documentTypeId") && changes.containsKey("documentKey")) {
                documentTypeId = clientIdentifierCommand.getDocumentTypeId();
                documentKey = clientIdentifierCommand.getDocumentKey();
            } else if (changes.containsKey("documentTypeId") && !changes.containsKey("documentKey")) {
                documentTypeId = clientIdentifierCommand.getDocumentTypeId();
                documentKey = clientIdentifierForUpdate.documentKey();
            } else if (!changes.containsKey("documentTypeId") && changes.containsKey("documentKey")) {
                documentTypeId = clientIdentifierForUpdate.documentTypeId();
                documentKey = clientIdentifierForUpdate.documentKey();
            }

            if (!changes.isEmpty()) {
                this.clientIdentifierRepository.saveAndFlush(clientIdentifierForUpdate);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(client.officeId()) //
                    .withClientId(clientId) //
                    .withEntityId(identifierId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleClientIdentifierDataIntegrityViolation(documentTypeLabel, documentTypeId, documentKey, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    /* (non-Javadoc)
     * @see #deleteClientIdentifier(java.lang.Long, java.lang.Long, java.lang.Long)
     */
    @Transactional
    @Override
    public CommandProcessingResult deleteClientIdentifier(final Long clientId, final Long identifierId, final Long fileId, final Long commandId) {

        final Client client = this.clientRepository.findOneWithNotFoundDetection(clientId);
        
        if(fileId == null){
        final ClientIdentifier clientIdentifier = this.clientIdentifierRepository.findOne(identifierId);
        if (clientIdentifier == null) { throw new ClientIdentifierNotFoundException(identifierId); }
        this.clientIdentifierRepository.delete(clientIdentifier);
        }else{
        	final Document document = this.documentRepository.findOne(fileId);
        	document.delete();
	        this.documentRepository.save(document);
        }
        return new CommandProcessingResultBuilder() //
        .withCommandId(commandId) //
        .withOfficeId(client.officeId()) //
        .withClientId(clientId) //
        .withEntityId(identifierId) //
        .build();
    }

    private void handleClientIdentifierDataIntegrityViolation(final String documentTypeLabel, final Long documentTypeId,
            final String documentKey, final DataIntegrityViolationException dve) {

        if (dve.getMostSpecificCause().getMessage().contains("unique_client_identifier")) {
            throw new DuplicateClientIdentifierException(documentTypeLabel);
        } else if (dve.getMostSpecificCause().getMessage().contains("unique_identifier_key")) { throw new DuplicateClientIdentifierException(
                documentTypeId, documentTypeLabel, documentKey); }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.clientIdentifier.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
    	LOGGER.error(dve.getMessage(), dve);
    }

	
}