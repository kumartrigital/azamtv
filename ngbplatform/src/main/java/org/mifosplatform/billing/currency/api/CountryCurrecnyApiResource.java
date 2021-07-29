package org.mifosplatform.billing.currency.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.currency.data.CountryCurrencyData;
import org.mifosplatform.billing.currency.service.CountryCurrencyReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;
import org.mifosplatform.organisation.monetary.service.OrganisationCurrencyReadPlatformService;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author ramya
 * 
 */
@Path("/countrycurrency")
@Component
@Scope("singleton")
public class CountryCurrecnyApiResource {


	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "country", "currency", "status",
					"baseCurrency", "conversionRate"));

	private final String resorceNameForPermission = "COUNTRYCURRENCY";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<CountryCurrencyData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final CountryCurrencyReadPlatformService countryCurrencyReadPlatformService;
	private final OrganisationCurrencyReadPlatformService currencyReadPlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	private final PlanReadPlatformService planReadPlatformService;

	@Autowired
	public CountryCurrecnyApiResource(final PlatformSecurityContext context,
			final DefaultToApiJsonSerializer<CountryCurrencyData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final CountryCurrencyReadPlatformService countryCurrencyReadPlatformService,
			final OrganisationCurrencyReadPlatformService currencyReadPlatformService,
			final AddressReadPlatformService addressReadPlatformService,
			final PlanReadPlatformService planReadPlatformService) {
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.countryCurrencyReadPlatformService = countryCurrencyReadPlatformService;
		this.currencyReadPlatformService = currencyReadPlatformService;
		this.addressReadPlatformService = addressReadPlatformService;
		this.planReadPlatformService = planReadPlatformService;

	}

	/**
	 * @param uriInfo
	 * @return retrieved list of all Configured country currency details
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveCurrencyConfigurationDetails(@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resorceNameForPermission);
		final Collection<CountryCurrencyData> currencyDatas = this.countryCurrencyReadPlatformService.retrieveAllCurrencyConfigurationDetails();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings,currencyDatas,RESPONSE_DATA_PARAMETERS);
	}

	/**
	 * @param uriInfo
	 * @return retrieved template data for creating currency configuration
	 */
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(resorceNameForPermission);
		
		final CountryCurrencyData currencyDatas = this.handleTemplateData(null);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings,currencyDatas,RESPONSE_DATA_PARAMETERS);
		
	}

	/**
	 * @param apiRequestBodyAsJson
	 * @return
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createCountryCurrencyConfigurationDetails(final String apiRequestBodyAsJson) {
		
		context.authenticatedUser().validateHasReadPermission(resorceNameForPermission);
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createCountryCurrency().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);

	}

	/**
	 * @param currencyId
	 * @param uriInfo
	 * @return single country currency configuration details
	 */
	@GET
	@Path("{currencyId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getSingleCurrencyConfigurationDetails(@PathParam("currencyId") final Long currencyId, @Context final UriInfo uriInfo) {
		
		this.context.authenticatedUser().validateHasReadPermission(resorceNameForPermission);
		CountryCurrencyData currencyDatas = this.countryCurrencyReadPlatformService.retrieveSingleCurrencyConfigurationDetails(currencyId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if(settings.isTemplate()){
			currencyDatas = this.handleTemplateData(currencyDatas);
		}
		return this.toApiJsonSerializer.serialize(settings, currencyDatas,RESPONSE_DATA_PARAMETERS);
	}
	
     private CountryCurrencyData handleTemplateData(CountryCurrencyData currencyDatas) {
		
		if(currencyDatas == null){
			currencyDatas = new CountryCurrencyData();
		}
		
		currencyDatas.setCurrencydata(this.currencyReadPlatformService.retrieveCurrencyConfiguration());
		currencyDatas.setCountryData(this.addressReadPlatformService.retrieveCountryDetails());
		currencyDatas.setStatusData(this.planReadPlatformService.retrieveNewStatus());
		return currencyDatas;
	
	}

	/**
	 * @param currencyId
	 * @param apiRequestBodyAsJson
	 * @return update details
	 */
	@PUT
	@Path("{currencyId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateSingleCurrencyConfigurationDetails(@PathParam("currencyId") final Long currencyId,final String apiRequestBodyAsJson) {

		context.authenticatedUser().validateHasReadPermission(resorceNameForPermission);
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateCountryCurrency(currencyId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * @param currencyId
	 * @return
	 */
	@DELETE
	@Path("{currencyId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteSingleCurrencyConfigurationDetails(@PathParam("currencyId") final Long currencyId) {
		
		context.authenticatedUser().validateHasReadPermission(resorceNameForPermission);
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteCountryCurrency(currencyId).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);

	}

}
