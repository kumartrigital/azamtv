package org.mifosplatform.logistics.hardwaredeviceplan.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.domain.ItemPairing;
import org.mifosplatform.logistics.onetimesale.domain.ItemPairingRepository;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Path("/hardwareplan")
@Component
@Scope("singleton")
public class HardwarePlanApiResource {
	
	
	
	private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	
	
    @Autowired
	public HardwarePlanApiResource(ToApiJsonSerializer<ClientData> toApiJsonSerializer,
		   final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
			
	    	this.toApiJsonSerializer = toApiJsonSerializer;
			this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	
    }

	@POST
	@Path("addhardwareplan/{clientId}/{clientServiceId}")
	@Consumes({ MediaType.APPLICATION_JSON }) 
    @Produces({ MediaType.APPLICATION_JSON })
	public String createHardwarePlan(final String apiRequestBodyAsJson,@PathParam("clientId") final Long clientId,@PathParam("clientServiceId")final Long clientServiceId){
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createHardwarePlans(clientId,clientServiceId).withJson(apiRequestBodyAsJson).build(); 
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		
		return this.toApiJsonSerializer.serialize(result);
		
	}
	
}
