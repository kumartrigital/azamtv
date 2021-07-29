package org.mifosplatform.collectionbatch.usageratequantitytier.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.collectionbatch.usageratequantitytier.data.UsageRateQuantityTierData;
import org.mifosplatform.collectionbatch.usageratequantitytier.service.UsageRateQuantityTierReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;




@Path("/usageratequantitytier")
@Component
@Scope("singleton")
public class UsageRateQuantityTierApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	private final static String RESOURCE_TYPE = "USAGERATEQUANTITYTIER";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<UsageRateQuantityTierData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private UsageRateQuantityTierReadPlatformService usageRateQuantityTierReadPlatformService;
	
	

	
	@Autowired
	public UsageRateQuantityTierApiResource(
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<UsageRateQuantityTierData> apiJsonSerializer, final PlatformSecurityContext context, 
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final AppUserReadPlatformService appUserReadPlatformService,
			final UsageRateQuantityTierReadPlatformService usageRateQuantityTierReadPlatformService
			) {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.usageRateQuantityTierReadPlatformService = usageRateQuantityTierReadPlatformService;
	
		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createUsageRateQuantityTier(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createUsageRateQuantityTier().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@GET
	@Path("dropdown")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveUsageRateQuantityTierForDropdown(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final List<UsageRateQuantityTierData> usageRateQuantityTierDatas = this.usageRateQuantityTierReadPlatformService.retrieveUsageRateQuantityTierForDropdown();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, usageRateQuantityTierDatas,RESPONSE_DATA_PARAMETERS);
		
	}

}
