package org.mifosplatform.portfolio.clientdiscount.api;

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
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.portfolio.clientdiscount.data.ClientDiscountData;
import org.mifosplatform.portfolio.clientdiscount.service.ClientDiscountReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clientdiscount")
@Component
@Scope("singleton")
public class ClientDiscountApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));

	private final static  String RESOURCE_TYPE = "CLIENTDISCOUNT";
	
	private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	private final ToApiJsonSerializer<ClientDiscountData> apiJsonSerializer;
	private final PlatformSecurityContext context;
	private final ClientDiscountReadPlatformService clientDiscountReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	
	@Autowired
	public ClientDiscountApiResource(final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
		   final ToApiJsonSerializer<ClientDiscountData> apiJsonSerializer,final PlatformSecurityContext context,
		   final ClientDiscountReadPlatformService clientDiscountReadPlatformService,final ApiRequestParameterHelper apiRequestParameterHelper){
		
		   this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		   this.apiJsonSerializer = apiJsonSerializer;
		   this.context = context;
		   this.clientDiscountReadPlatformService = clientDiscountReadPlatformService;
		   this.apiRequestParameterHelper = apiRequestParameterHelper;
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String ChannelDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,@QueryParam("clientId") final Long clientId){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchClientDiscount = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<ClientDiscountData> clientDiscountDatas = clientDiscountReadPlatformService.retrieveClientDiscount(searchClientDiscount, clientId);
		return apiJsonSerializer.serialize(clientDiscountDatas);
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createClientDiscount(final String jsonRequestBody){
		
		final CommandWrapper command = new CommandWrapperBuilder().createClientDiscount().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
		
	}
	
	@PUT
	@Path("{clientDiscountId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateChannel(@PathParam("clientDiscountId") final Long clientDiscountId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateClientDiscount(clientDiscountId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@DELETE
	@Path("{clientDiscountId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteChannel(@PathParam("clientDiscountId") final Long clientDiscountId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteClientDiscount(clientDiscountId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{clientDiscountId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveChannelDetails(@Context final UriInfo uriInfo ,@PathParam("clientDiscountId") final Long clientDiscountId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		ClientDiscountData clientDiscountlData = this.clientDiscountReadPlatformService.retrieveClientDiscount(clientDiscountId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if(settings.isTemplate()){
			clientDiscountlData = this.handleTemplateData(clientDiscountlData);
		}
		
		return this.apiJsonSerializer.serialize(settings,clientDiscountlData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	private ClientDiscountData handleTemplateData(ClientDiscountData clientDiscountlData) {
		if(clientDiscountlData == null){
			clientDiscountlData = new ClientDiscountData();
		}
		
		return clientDiscountlData;
	}
	

}
