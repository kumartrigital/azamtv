package org.mifosplatform.organisation.lco.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/lco")
@Component
@Scope("singleton")
public class LCOApiResource {
	
	private final String resourceNameForPermissions = "LCO";
	
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final ClientReadPlatformService clientReadPlatformService;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<ChannelData> apiJsonSerializer;
	
	@Autowired
	public LCOApiResource(PlatformSecurityContext context,
			ToApiJsonSerializer<ClientData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			ClientReadPlatformService clientReadPlatformService,
			PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer<ChannelData> apiJsonSerializer){
		this.context=context;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.clientReadPlatformService=clientReadPlatformService;
		this.portfolioCommandSourceWritePlatformService=portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer=apiJsonSerializer;
	}
	
	/*@GET
	@Path("/clients")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAll(@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("officeId") final Long officeId,
	            @QueryParam("externalId") final String externalId, @QueryParam("displayName") final String displayName,
	            @QueryParam("firstName") final String firstname, @QueryParam("lastName") final String lastname,
	            @QueryParam("underHierarchy") final String hierarchy, @QueryParam("offset") final Integer offset,
	            @QueryParam("limit") final Integer limit, @QueryParam("orderBy") final String orderBy,
	            @QueryParam("sortOrder") final String sortOrder,@QueryParam("groupName") final String groupName,
	            @QueryParam("status") final String status) {

	        this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        final SearchParameters searchParameters = SearchParameters.forClients(sqlSearch, officeId, externalId, displayName, firstname,
	                lastname, hierarchy, offset, limit, orderBy, sortOrder,groupName,status);
	        final Page<ClientData> clientData = this.clientReadPlatformService.retrieveAllClientsForLCO(searchParameters);
	        return this.toApiJsonSerializer.serialize(clientData);
	 }
	*/
	
	@PUT
	@Path("/renewal")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String renewal(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().renewalLCOclients().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
			
	}
	
	
	@GET
	@Path("{officeId}")//officeId
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePartnerAgreementData(	@PathParam("officeId") final Long officeId,@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	    /*List<ClientData> clientData = this.clientReadPlatformService.retrieveClientsForLCO(officeId);*/
	    final ClientData clientData = new ClientData(this.clientReadPlatformService.retrieveClientsForLCO(officeId));
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(clientData);
	}
	@GET
	@Path("/renewallist/{officeId}")//officeId
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveLcoRenewalClientData(	@PathParam("officeId") final Long officeId,@Context final UriInfo uriInfo,
			@QueryParam("fromDate") final String fromDate,
			@QueryParam("toDate") final String toDate) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	    /*List<ClientData> clientData = this.clientReadPlatformService.retrieveClientsForLCO(officeId);*/
	    final ClientData clientData = new ClientData(this.clientReadPlatformService.retrieveRenewalClientsForLCO(officeId,toDate,fromDate));
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(clientData);
	}
	
}
