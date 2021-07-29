package org.mifosplatform.provisioning.networkelement.api;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.celcom.domain.PaymentTypeEnum;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.broadcaster.service.BroadcasterReadPlatformService;

import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;

import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;
import org.mifosplatform.provisioning.networkelement.domain.NetworkElement;
import org.mifosplatform.provisioning.networkelement.domain.StatusTypeEnum;
import org.mifosplatform.provisioning.networkelement.service.NetworkElementReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/networkelement")
@Component
@Scope("singleton")
public class NetworkElementApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	
	private final static  String RESOURCE_TYPE = "NETWORKELEMENT";
    
	private final NetworkElementReadPlatformService readPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	 private final DefaultToApiJsonSerializer<NetworkElementData> toApiJsonSerializer;
	final private NetworkElementReadPlatformService networkelementReadPlatformService;
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<NetworkElementApiResource> apiJsonSerializer;

	
	@Autowired
	public NetworkElementApiResource(
			PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer<NetworkElementApiResource> apiJsonSerializer,final PlatformSecurityContext context,
			final NetworkElementReadPlatformService networkelementReadPlatformService,final ApiRequestParameterHelper apiRequestParameterHelper,
			final DefaultToApiJsonSerializer<NetworkElementData> toApiJsonSerializer,final NetworkElementReadPlatformService readPlatformService)
			{
	    
		this.readPlatformService = readPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
        this.networkelementReadPlatformService = networkelementReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createNetworkElement(@QueryParam("LanguageEnum")final String LanguageEnum,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createNetworkElement().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
			
	}
	

	@PUT
	@Path("{networkelementId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateNetworkElement(@PathParam("networkelementId") final Long networkelementId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateNetworkelement(networkelementId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}

	@GET
	@Path("{networkelementId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveNetworkelEmentDetails(@Context final UriInfo uriInfo ,@PathParam("networkelementId") final Long networkelementId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		NetworkElementData networkelementData = this.networkelementReadPlatformService.retrieveNetworkElement(networkelementId);
		return this.apiJsonSerializer.serialize(networkelementData);
		
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String NetworkelementDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchNetworkElement = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<NetworkElementData> NetworkElementDatas = networkelementReadPlatformService.retrieveNetworkElement(searchNetworkElement);
		return apiJsonSerializer.serialize(NetworkElementDatas);
	}
	@GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveNetworkElementTemplate(@Context final UriInfo uriInfo) {

        final List<StatusTypeEnum> statusTypeEnum = this.readPlatformService.retrieveStatusTypeEnum();
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        NetworkElementData networkelement = new NetworkElementData(statusTypeEnum);
        return this.toApiJsonSerializer.serialize(settings, networkelement, RESPONSE_DATA_PARAMETERS);
    }
	
	@DELETE
	@Path("{networkelementId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteNetworkElement(@PathParam("networkelementId") final Long networkelementId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteNetworkElement(networkelementId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}

	
	
}
