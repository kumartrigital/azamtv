package org.mifosplatform.collectionbatch.unitofmeasurement.api;

import java.util.Arrays;
import java.util.HashSet;
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
import org.mifosplatform.collectionbatch.unitofmeasurement.data.UnitOfmeasurementData;
import org.mifosplatform.collectionbatch.unitofmeasurement.service.UnitOfmeasurementReadPlatformService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Path("/unitofmeasurement")
@Component
@Scope("singleton")
public class UnitOfmeasurementApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	private final static String RESOURCE_TYPE = "UNITOFMEASUREMENT";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<UnitOfmeasurementData> apiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	final private UnitOfmeasurementReadPlatformService unitofmeasurementReadPlatformService;
	
	@Autowired
	public UnitOfmeasurementApiResource(final PlatformSecurityContext context,
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<UnitOfmeasurementData> apiJsonSerializer,
		    final ApiRequestParameterHelper apiRequestParameterHelper,
		    final UnitOfmeasurementReadPlatformService unitofmeasurementReadPlatformService) {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.unitofmeasurementReadPlatformService = unitofmeasurementReadPlatformService;
		
	}
	
	
	
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createUnitOfmeasurement(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createUnitOfmeasurement().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveAllUnitOfmeasurement(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchUnitOfmeasurement = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<UnitOfmeasurementData> ratableDatas = unitofmeasurementReadPlatformService.retriveAllUnitOfmeasurement(searchUnitOfmeasurement);
		return apiJsonSerializer.serialize(ratableDatas);
	}
	
	@GET
	@Path("{uomId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveUnitOfmeasurement(@Context final UriInfo uriInfo ,@PathParam("uomId") final Long id){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final UnitOfmeasurementData unitDatas = this.unitofmeasurementReadPlatformService.retriveUnitOfmeasurement(id);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, unitDatas,RESPONSE_DATA_PARAMETERS);
		
	}
	
	@PUT
	@Path("{uomId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateUnitOfmeasurement(@PathParam("uomId") final Long id,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateUnitOfmeasurement(id).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}

}
