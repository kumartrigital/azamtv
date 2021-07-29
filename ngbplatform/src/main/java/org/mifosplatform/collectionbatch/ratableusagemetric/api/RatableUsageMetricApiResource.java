package org.mifosplatform.collectionbatch.ratableusagemetric.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
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

import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.collectionbatch.ratableusagemetric.service.RatableUsageMetricReadPlatformService;
import org.mifosplatform.collectionbatch.template.service.TemplateReadPlatformService;
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
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;




@Path("/ratableusagemetric")
@Component
@Scope("singleton")
public class RatableUsageMetricApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	private final static String RESOURCE_TYPE = "RATABLEUSAGEMETRIC";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<RatableUsageMetricData> apiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	final private RatableUsageMetricReadPlatformService ratableUsageMetricReadPlatformService;
	final private TemplateReadPlatformService templateReadPlatformService;
	
	
	@Autowired
	public RatableUsageMetricApiResource(final PlatformSecurityContext context,
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<RatableUsageMetricData> apiJsonSerializer,
		    final ApiRequestParameterHelper apiRequestParameterHelper,
		    final RatableUsageMetricReadPlatformService ratableUsageMetricReadPlatformService,
		    final TemplateReadPlatformService templateReadPlatformService) {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.ratableUsageMetricReadPlatformService = ratableUsageMetricReadPlatformService;
		this.templateReadPlatformService=templateReadPlatformService;
		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createRatableUsageMetric(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createRatableUsageMetric().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveAllRatableUsageMetric(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchRatableUsageMetric = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<RatableUsageMetricData> ratableDatas = ratableUsageMetricReadPlatformService.retriveAllRatableUsageMetric(searchRatableUsageMetric);
		return apiJsonSerializer.serialize(ratableDatas);
	}
	
	
	@GET
	@Path("{ratableId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveRatableUsageMetric(@Context final UriInfo uriInfo ,@PathParam("ratableId") final Long id){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final RatableUsageMetricData ratableDatas = this.ratableUsageMetricReadPlatformService.retrieveRatableUsageMetric(id);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, ratableDatas,RESPONSE_DATA_PARAMETERS);
		
	}
	
	
	@PUT
	@Path("{ratableId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateRatableUsageMetric(@PathParam("ratableId") final Long id,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateRatableUsageMetric(id).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	/*@DELETE
	@Path("{ratableId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteRatableUsageMetric(@PathParam("ratableId") final Long id) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteRatableUsageMetric(id).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}*/
	
	@GET
	@Path("dropdown")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateForDropdown(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final List<RatableUsageMetricData> ratableDatas = this.ratableUsageMetricReadPlatformService.retrieveTemplateForDropdown();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, ratableDatas,RESPONSE_DATA_PARAMETERS);
		
	}
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final RatableUsageMetricData ratableUsageMetricData = this.handleTemplateData(null);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, ratableUsageMetricData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	private RatableUsageMetricData handleTemplateData(RatableUsageMetricData ratableUsageMetricData) {
		if(ratableUsageMetricData == null){
			ratableUsageMetricData = new RatableUsageMetricData();
		}
		ratableUsageMetricData.setFieldNamesData(this.templateReadPlatformService.retrieveFieldNamesForDropdown());
		return ratableUsageMetricData;
	}
	
	
	
	
	
	
	
	

}
