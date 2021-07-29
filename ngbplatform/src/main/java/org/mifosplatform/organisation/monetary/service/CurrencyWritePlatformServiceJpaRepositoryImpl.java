/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.monetary.service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.broadcaster.domain.Broadcaster;
import org.mifosplatform.organisation.broadcaster.exception.BroadcatserNotFoundException;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepository;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.mifosplatform.organisation.monetary.exception.CurrencyNotFoundException;
import org.mifosplatform.organisation.monetary.serialization.CurrencyCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.office.domain.OrganisationCurrency;
import org.mifosplatform.organisation.office.domain.OrganisationCurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CurrencyWritePlatformServiceJpaRepositoryImpl implements CurrencyWritePlatformService {

	private final PlatformSecurityContext context;
	private final CurrencyCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ApplicationCurrencyRepository applicationsCurrencyRepository;
	private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
	private final OrganisationCurrencyRepository organisationCurrencyRepository;
	private final CurrencyReadPlatformService currencyReadPlatformService;

	@Autowired
	public CurrencyWritePlatformServiceJpaRepositoryImpl(
			final PlatformSecurityContext context,final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
			final CurrencyCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final ApplicationCurrencyRepository applicationsCurrencyRepository,
			final OrganisationCurrencyRepository organisationCurrencyRepository,
			final CurrencyReadPlatformService currencyReadPlatformService) {
		
		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.applicationsCurrencyRepository = applicationsCurrencyRepository;
		this.applicationCurrencyRepository = applicationCurrencyRepository;
		this.organisationCurrencyRepository = organisationCurrencyRepository;
		this.currencyReadPlatformService = currencyReadPlatformService;
	}



	@Override
	public CommandProcessingResult create(JsonCommand command) {
		try{
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			ApplicationCurrency currency = ApplicationCurrency.formJson(command);
			Long id = this.currencyReadPlatformService.findMaxofId(command.stringValueOfParameterNamed("type"));
			if("0".equalsIgnoreCase(String.valueOf(id))){
				if("Currency".equalsIgnoreCase(command.stringValueOfParameterNamed("type"))){
					currency.setId(Long.valueOf(1));
				}else{
					currency.setId(Long.valueOf(1001));
				}
			}else {
				currency.setId(id);
			}
			
			this.applicationsCurrencyRepository.saveAndFlush(currency);
			return new CommandProcessingResultBuilder().withEntityId(currency.getId()).build();
		
		    }catch (DataIntegrityViolationException dve) {
	        handleDataIntegrityIssues(command, dve);
	        return  CommandProcessingResult.empty();
	       }
	}



	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {

    	final Throwable realCause = dve.getMostSpecificCause();
    	
    	if(realCause.getMessage().contains("code")){
            final String code = command.stringValueOfParameterNamed("code");
            throw new PlatformDataIntegrityException("error.msg.duplicate.code", "Currency with code `" + code
                    + "` already exists", "code", code);
        }
        
    	throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
  	
	}
	
	
	@Override
	public CommandProcessingResult updateCurrency(JsonCommand command, Long currencyId) {

		this.context.authenticatedUser();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		final String[] currencies = command.arrayValueOfParameterNamed("currencies");
		final Map<String, Object> changes = new LinkedHashMap<String, Object>();
		final List<String> allowedCurrencyCodes = new ArrayList<String>();
		final Set<OrganisationCurrency> allowedCurrencies = new HashSet<OrganisationCurrency>();
		for (final String currencyCode : currencies) {

			final ApplicationCurrency currency = this.applicationCurrencyRepository.findOneWithNotFoundDetection(currencyCode);

			final OrganisationCurrency allowedCurrency = currency.toOrganisationCurrency();

			allowedCurrencyCodes.add(currencyCode);
			allowedCurrencies.add(allowedCurrency);
		}

		changes.put("currencies", allowedCurrencyCodes.toArray(new String[allowedCurrencyCodes.size()]));

		this.organisationCurrencyRepository.deleteAll();
		this.organisationCurrencyRepository.save(allowedCurrencies);

		return new CommandProcessingResultBuilder() //
				.withCommandId(command.commandId()) //
				.with(changes) //
				.build();
	}


	  private ApplicationCurrency retrieveCodeBy(final Long currencyId) {
		final ApplicationCurrency currency = this.applicationsCurrencyRepository.findOne(currencyId);
		if (currency == null) { throw new CurrencyNotFoundException(currencyId); }
		return currency;
	}	
	@Override
	public CommandProcessingResult deleteCurrency(JsonCommand command, Long currencyId) {
		try{
			this.context.authenticatedUser();
			ApplicationCurrency currency = this.retrieveCodeBy(currencyId);
			if(currency.getIsDeleted()=='Y'){
				throw new CurrencyNotFoundException(currencyId);
			}
			currency.delete();
			this.applicationsCurrencyRepository.saveAndFlush(currency);
			return new CommandProcessingResultBuilder().withEntityId(currencyId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}
	



	@Override
	public CommandProcessingResult updateNewCurrency(JsonCommand command, Long currencyId) {
		try{
			   
			   this.context.authenticatedUser();
			   this.fromApiJsonDeserializer.validateForCreate(command.json());
			   ApplicationCurrency currency = this.retrieveCodeBy(currencyId);
			   final Map<String, Object> changes = currency.update(command);
			   if(!changes.isEmpty()){
				   this.applicationsCurrencyRepository.save(currency);
			   }
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(currencyId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
	     }


		


	
	}


