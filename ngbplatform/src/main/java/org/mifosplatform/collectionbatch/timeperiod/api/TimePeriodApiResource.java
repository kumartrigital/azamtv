package org.mifosplatform.collectionbatch.timeperiod.api;

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

import org.mifosplatform.collectionbatch.timeperiod.data.TimePeriodNewData;
import org.mifosplatform.collectionbatch.timeperiod.service.TimeperiodReadPlatformService;
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
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/timeperiod")
@Component
@Scope("singleton")
public class TimePeriodApiResource {
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	private final static String RESOURCE_TYPE = "TIMEMODEL";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<TimePeriodNewData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private TimeperiodReadPlatformService timeperiodReadPlatformService;
	
	
	@Autowired
	public TimePeriodApiResource(
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<TimePeriodNewData> apiJsonSerializer, final PlatformSecurityContext context, 
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final AppUserReadPlatformService appUserReadPlatformService,
			final TimeperiodReadPlatformService timeperiodReadPlatformService
			
			) {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.timeperiodReadPlatformService=timeperiodReadPlatformService;
	
		
	}
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createTimemodel(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createTimeperiod().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@PUT
	@Path("{timeperiodId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateTimeperiod(@PathParam("timeperiodId") final Long timeperiodId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateTimeperiod(timeperiodId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{timeperiodId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveTimeperiods(@Context final UriInfo uriInfo ,@PathParam("timeperiodId") final Long timeperiodId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final TimePeriodNewData timePeriodNewDatas = this.timeperiodReadPlatformService.retriveTimeperiods(timeperiodId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, timePeriodNewDatas,RESPONSE_DATA_PARAMETERS);		
	}
	
	@DELETE
	@Path("{timeperiodId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteChannel(@PathParam("timeperiodId") final Long timeperiodId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteTimePeriod(timeperiodId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String ChannelDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchTimePeriodNewData = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<TimePeriodNewData> timePeriodNewDatas = timeperiodReadPlatformService.retrieveTimePeriodData(searchTimePeriodNewData);
		return apiJsonSerializer.serialize(timePeriodNewDatas);
	}
}
