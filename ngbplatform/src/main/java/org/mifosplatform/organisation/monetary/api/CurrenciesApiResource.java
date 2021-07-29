/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.monetary.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.organisation.monetary.service.CurrencyWritePlatformService;
import org.mifosplatform.organisation.monetary.service.OrganisationCurrencyReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/currencies")
@Component
@Scope("singleton")
public class CurrenciesApiResource {

	
	

	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("selectedCurrencyOptions", "currencyOptions"));

	private final String resourceNameForPermissions = "CURRENCY";
	
	private final PlatformSecurityContext context;
	private final OrganisationCurrencyReadPlatformService readPlatformService;
	private final CurrencyReadPlatformService readPlatformServices;
	private final DefaultToApiJsonSerializer<ApplicationCurrencyConfigurationData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<CurrencyData> toApiJsonSerializerForCurrency;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final CurrencyReadPlatformService  currencyReadPlatformService;

	private final CurrencyWritePlatformService currencyWritePlatformService;

	@Autowired
	public CurrenciesApiResource(
			final PlatformSecurityContext context,
			final OrganisationCurrencyReadPlatformService readPlatformService,
			final CurrencyReadPlatformService readPlatformServices,
			final DefaultToApiJsonSerializer<ApplicationCurrencyConfigurationData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final DefaultToApiJsonSerializer<CurrencyData> toApiJsonSerializerForCurrency,
			final CurrencyReadPlatformService  currencyReadPlatformService,
			final CurrencyWritePlatformService currencyWritePlatformService) {
		this.context = context;
		this.readPlatformService = readPlatformService;
		this.readPlatformServices = readPlatformServices;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.toApiJsonSerializerForCurrency = toApiJsonSerializerForCurrency;
		this.currencyReadPlatformService = currencyReadPlatformService;
		this.currencyWritePlatformService=currencyWritePlatformService;
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveCurrencies(@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final ApplicationCurrencyConfigurationData configurationData = this.readPlatformService.retrieveCurrencyConfiguration();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, configurationData, RESPONSE_DATA_PARAMETERS);
	}


	@GET
	@Path("all")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllCurrencies(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch,
		      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset) {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final SearchSqlQuery searchCurrency = SearchSqlQuery.forSearch(sqlSearch, offset,limit);
		Page<CurrencyData> currencyDatas = this.readPlatformServices.retriveCurrencies(searchCurrency);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializerForCurrency.serialize(currencyDatas);
	}
	
	@GET
	@Path("{currenciesId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveCurrenciesDetails(@Context final UriInfo uriInfo ,@PathParam("currenciesId") final Long currenciesId){
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final CurrencyData currencyDatas = this.currencyReadPlatformService.retriveCurrencies(currenciesId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializerForCurrency.serialize(settings, currencyDatas,RESPONSE_DATA_PARAMETERS);
		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createCurrency(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createCurrency().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(command);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("{currencyId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateCurrency(@PathParam("currencyId") final Long currencyId,final String apiRequestBodyAsJson) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateCurrency(currencyId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("update/{currencyId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateNewCurrency(@PathParam("currencyId") final Long currencyId,final String apiRequestBodyAsJson) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateNewCurrency(currencyId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	
	
	@DELETE
	@Path("{currencyId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteCurrency(@PathParam("currencyId") final Long currencyId,final String apiRequestBodyAsJson) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteCurrency(currencyId).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(command);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	
	
	
}