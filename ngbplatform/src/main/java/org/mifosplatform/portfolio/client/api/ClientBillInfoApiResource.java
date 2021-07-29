package org.mifosplatform.portfolio.client.api;

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
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.portfolio.client.data.ClientBillInfoData;
import org.mifosplatform.portfolio.client.service.ClientBillInfoReadPlatformService;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clientbillprofile")
@Component
@Scope("singleton")
public class ClientBillInfoApiResource {
	
	
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<ClientBillInfoData> apiJsonSerializer;
	final private ClientBillInfoReadPlatformService clientBillInfoReadPlatformService;
	
	@Autowired
	public ClientBillInfoApiResource(
			PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer<ClientBillInfoData> apiJsonSerializer,ClientBillInfoReadPlatformService clientBillInfoReadPlatformService) {
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.clientBillInfoReadPlatformService = clientBillInfoReadPlatformService;
	}


    @PUT
	@Path("{clientId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateClientBillProfile(@PathParam("clientId") final Long clientId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateClientBillInfo(clientId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);


		

	}
	
	@GET
	@Path("bills/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveclientbill(@PathParam("clientId") final Long clientId){
		final ClientBillInfoData clientBillInfoData = this.clientBillInfoReadPlatformService.retriveclientbill(clientId);
		return apiJsonSerializer.serialize(clientBillInfoData);
		
	}
}
