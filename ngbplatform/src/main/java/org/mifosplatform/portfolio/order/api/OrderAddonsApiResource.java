package org.mifosplatform.portfolio.order.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.addons.data.AddonsPriceData;
import org.mifosplatform.portfolio.addons.service.AddonServiceReadPlatformService;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.order.data.OrderAddonsData;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/orderaddons")
@Component
@Scope("singleton")
public class OrderAddonsApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id","orderId","serviceId","startDate","endDate",
			"contracrId","status","provisionSystem"));
	
	 private final String resourceNameForPermissions = "ADDONS";
	 private final DefaultToApiJsonSerializer<OrderAddonsData> toApiJsonSerializer;
	 private final ApiRequestParameterHelper apiRequestParameterHelper;
	 private final PlatformSecurityContext context;
	 private final PlanReadPlatformService planReadPlatformService;
	 private final AddonServiceReadPlatformService addonServiceReadPlatformService;
	 private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

@Autowired	  
public OrderAddonsApiResource(final DefaultToApiJsonSerializer<OrderAddonsData> apiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
		 final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,final PlatformSecurityContext context,
		 final AddonServiceReadPlatformService addonServiceReadPlatformService,final PlanReadPlatformService planReadPlatformService){
	
	       this.context=context;	  
		  this.toApiJsonSerializer=apiJsonSerializer;
		  this.addonServiceReadPlatformService=addonServiceReadPlatformService;
		  this.apiRequestParameterHelper=apiRequestParameterHelper;
		  this.planReadPlatformService=planReadPlatformService;
		  this.commandsSourceWritePlatformService=commandSourceWritePlatformService;
		  
	  }
	  
    @POST
	@Path("{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String addOrderAddonServices(@PathParam("orderId") final Long orderId, final String apiRequestBodyAsJson){
			
	final CommandWrapper commandRequest = new CommandWrapperBuilder().createOrderAddons(orderId).withJson(apiRequestBodyAsJson).build();
	final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	return this.toApiJsonSerializer.serialize(result);
			
	}
    
    @GET
	@Path("template/{planId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveOrderTemplate(@PathParam("planId") final Long planId,@QueryParam("chargeCode") final String chargeCode,@Context final UriInfo uriInfo) {
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	List<AddonsPriceData> addonsPriceDatas =this.addonServiceReadPlatformService.retrievePlanAddonDetails(planId,chargeCode);
	List<SubscriptionData> contractPeriod=this.planReadPlatformService.retrieveSubscriptionData(null,null);
	OrderAddonsData addonsData =new OrderAddonsData(addonsPriceDatas,contractPeriod);
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, addonsData, RESPONSE_DATA_PARAMETERS);
	}
    
    @DELETE
	@Path("{orderAddonId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteOrder(@PathParam("orderAddonId") final Long orderAddonId) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().disconnectOrderAddon(orderAddonId).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}

}
