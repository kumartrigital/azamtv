package org.mifosplatform.collectionbatch.timemodel.api;

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

import org.mifosplatform.collectionbatch.timemodel.data.TimeModelData;
import org.mifosplatform.collectionbatch.timemodel.service.TimeModelReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/timemodel")
@Component
@Scope("singleton")
public class TimeModelApiResource {

	private final static String RESOURCE_TYPE = "TIMEMODEL";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<TimeModelData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private TimeModelReadPlatformService timeModelReadPlatformService ;
	
	

	
	@Autowired
	public TimeModelApiResource(
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<TimeModelData> apiJsonSerializer, final PlatformSecurityContext context, 
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final AppUserReadPlatformService appUserReadPlatformService,
			final TimeModelReadPlatformService timeModelReadPlatformService
			) {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.timeModelReadPlatformService=timeModelReadPlatformService;
	
		
	}
	
	/*@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createTimemodel(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createTimemodel().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}*/
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createTimemodel(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createTimemodel().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAddress(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
		      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		final SearchSqlQuery searchTimemodels =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<TimeModelData> timemodels = this.timeModelReadPlatformService .retrieveAllTimemodels(searchTimemodels);
		return apiJsonSerializer.serialize(timemodels);
	}
	
	
	@PUT
	@Path("{timemodelId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateBroadcaster(@PathParam("timemodelId") final Long timemodelId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateTimemodel(timemodelId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@DELETE
	@Path("{timemodelId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteTimemodel(@PathParam("timemodelId") final Long timemodelId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteTimemodel(timemodelId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	
}
