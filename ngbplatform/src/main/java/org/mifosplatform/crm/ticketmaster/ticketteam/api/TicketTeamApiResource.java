package org.mifosplatform.crm.ticketmaster.ticketteam.api;

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
import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.crm.ticketmaster.ticketteam.data.TicketTeamData;
import org.mifosplatform.crm.ticketmaster.ticketteam.service.TicketTeamReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/ticketteam")
@Component
@Scope("singleton")
public class TicketTeamApiResource {

private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	
	private final static String RESOURCE_TYPE = "TICKETTEAM";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<TicketTeamData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private TicketTeamReadPlatformService ticketteamReadPlatformService;
	private final AppUserReadPlatformService appUserReadPlatformService;
	
	
	
	@Autowired
	public TicketTeamApiResource(
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<TicketTeamData> apiJsonSerializer, final PlatformSecurityContext context, 
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final TicketTeamReadPlatformService ticketteamReadPlatformService,
			final AppUserReadPlatformService appUserReadPlatformService) {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.ticketteamReadPlatformService = ticketteamReadPlatformService;
		this.appUserReadPlatformService = appUserReadPlatformService;
	}
	
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String ticketteam(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createTicketTeam().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@PUT
	@Path("{ticketteamId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateTicketteam(@PathParam("ticketteamId") final Long ticketteamId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateTicketteam(ticketteamId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@DELETE
	@Path("{ticketteamId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteTicketteam(@PathParam("ticketteamId") final Long ticketteamId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteTicketteam(ticketteamId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String TicketteamDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchTicketTeam = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<TicketTeamData> ticketteamDatas = ticketteamReadPlatformService.retrieveTicketTeam(searchTicketTeam);
		return apiJsonSerializer.serialize(ticketteamDatas);
	}

	@GET
	@Path("{ticketteamId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveTicketteamDetails(@Context final UriInfo uriInfo ,@PathParam("ticketteamId") final Long ticketteamId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final TicketTeamData ticketteamDatas = this.ticketteamReadPlatformService.retrieveTicketTeam(ticketteamId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, ticketteamDatas,RESPONSE_DATA_PARAMETERS);
		
	}
	
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final TicketTeamData ticketteamData = this.handleTemplateData(null);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, ticketteamData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	private TicketTeamData handleTemplateData(TicketTeamData ticketteamData) {
		if(ticketteamData == null){
			ticketteamData = new TicketTeamData();
		}
		ticketteamData.setAppUserDatas(this.appUserReadPlatformService.retrieveAppUserDataForDropdown());
		return ticketteamData;
	}
	
	
	
	
	
	
	
	
	
	
}
