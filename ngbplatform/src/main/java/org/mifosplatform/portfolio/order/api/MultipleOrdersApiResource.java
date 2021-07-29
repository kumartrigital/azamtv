package org.mifosplatform.portfolio.order.api;

import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/multipleorders")
@Component
@Scope("singleton")
public class MultipleOrdersApiResource {
	
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer;
	
	@Autowired
	public MultipleOrdersApiResource(final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
			final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer){
		this.commandsSourceWritePlatformService=commandSourceWritePlatformService;
		this.toApiJsonSerializer=toApiJsonSerializer;
	}
	
	@POST
	@Path("{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createMultipleOrder(@PathParam("clientId") final Long clientId, String apiRequestBodyAsJson) throws JSONException {
	
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createMultipleOrder(clientId).withClientId(clientId).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("cancel/{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String disconnectMultipleOrder(@PathParam("clientId") final Long clientId, String apiRequestBodyAsJson) throws JSONException {
	
		final CommandWrapper commandRequest = new CommandWrapperBuilder().disconnectMultipleOrder(clientId).withJson(apiRequestBodyAsJson).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}
	
}
