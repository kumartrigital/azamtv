package org.mifosplatform.portfolio.order.api;

import java.util.Collection;
import java.util.List;

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

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.crm.ticketmaster.ticketmapping.service.TicketMappingReadPlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderWorkflowData;
import org.mifosplatform.portfolio.order.service.OrderWorkflowReadPlatfromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/orderworkflow")
@Component
@Scope("singleton")
public class OrderWorkflowApiResource {

	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer;
	private final MCodeReadPlatformService codeReadPlatformService;
	private final TicketMappingReadPlatformService ticketmappingReadPlatformService;
	private final OrderWorkflowReadPlatfromService orderWorkflowReadPlatfromService;
	
	  
	@Autowired
	public OrderWorkflowApiResource(PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer, final MCodeReadPlatformService codeReadPlatformService,
			final TicketMappingReadPlatformService ticketmappingReadPlatformService, final OrderWorkflowReadPlatfromService orderWorkflowReadPlatfromService) {
		this.commandsSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.codeReadPlatformService = codeReadPlatformService;
		this.ticketmappingReadPlatformService = ticketmappingReadPlatformService;
		this.orderWorkflowReadPlatfromService = orderWorkflowReadPlatfromService;
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String workflowProcess(String apiRequestBodyAsJson) throws JSONException {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createOrderWorkflow().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@GET
    @Path("template/{clientServiceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplateData(@PathParam("clientServiceId") final Long clientServiceId,@Context final UriInfo uriInfo) {

		OrderWorkflowData orderWorkflowData = this.orderWorkflowReadPlatfromService.getPresentStatus(clientServiceId);
        final Collection<MCodeData> statusDatas =   codeReadPlatformService.getCodeValue(CodeNameConstants.SERVICE_CUSTOME_STATUS);
        final List<TicketTeamMappingData> ticketmappingDatas = this.ticketmappingReadPlatformService.retrieveTicketTeamForDropdown();
        orderWorkflowData.addDropdowns(statusDatas, ticketmappingDatas);
        return this.toApiJsonSerializer.serialize(orderWorkflowData);
    }
}
