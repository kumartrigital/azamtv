package org.mifosplatform.organisation.broadcaster.api;

import java.util.Arrays;
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
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.organisation.broadcaster.service.BroadcasterReadPlatformService;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.channel.service.ChannelReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/broadcaster")
@Component
@Scope("singleton")
public class BroadcasterApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	
	private final static String RESOURCE_TYPE = "BROADCASTER";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<BroadcasterData> apiJsonSerializer;
	final private BroadcasterReadPlatformService broadcasterReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final ChannelReadPlatformService channelReadPlatformService;


	
	@Autowired
	public BroadcasterApiResource(
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<BroadcasterData> apiJsonSerializer, final PlatformSecurityContext context, 
			final BroadcasterReadPlatformService broadcasterReadPlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper,final ChannelReadPlatformService channelReadPlatformService) {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.broadcasterReadPlatformService = broadcasterReadPlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.channelReadPlatformService = channelReadPlatformService;
	}
	
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String BroadcasterDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchBroadcaster = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<BroadcasterData> broadcasterDatas = broadcasterReadPlatformService.retrieveBroadcaster(searchBroadcaster);
		return apiJsonSerializer.serialize(broadcasterDatas);
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createBroadcaster(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createBroadcaster().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{broadcasterId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveBroadcasterDetails(@Context final UriInfo uriInfo ,@PathParam("broadcasterId") final Long broadcasterId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final BroadcasterData broadcasterDatas = this.broadcasterReadPlatformService.retrieveBroadcaster(broadcasterId);
		broadcasterDatas.setChannelDatas(this.channelReadPlatformService.retrieveChannelName(broadcasterId));
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, broadcasterDatas,RESPONSE_DATA_PARAMETERS);
		
	}
	
	@PUT
	@Path("{broadcasterId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateBroadcaster(@PathParam("broadcasterId") final Long broadcasterId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateBroadcaster(broadcasterId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	

	@DELETE
	@Path("{broadcasterId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteBroadcaster(@PathParam("broadcasterId") final Long broadcasterId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteBroadcaster(broadcasterId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}

	
	@GET
	@Path("dropdown")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveBroadcasterForDropdown(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final List<BroadcasterData> broadcasterDatas = this.broadcasterReadPlatformService.retrieveBroadcastersForDropdown();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, broadcasterDatas,RESPONSE_DATA_PARAMETERS);
		
	}

}
