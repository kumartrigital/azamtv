package org.mifosplatform.portfolio.contract.api;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.chargecode.data.DurationTypeData;
import org.mifosplatform.billing.chargecode.service.ChargeCodeReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/subscriptions")
@Component
@Scope("singleton")
public class ContractPeriodApiResource {
	  private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id",
	           "subscriptionPeriod","subscriptionType","units","allowedtypes","subscriptionTypeId"));
        
	  private final static String RESOURCENAMEFORPERMISSIONS = "CONTRACT";
	    private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<SubscriptionData> toApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
	    private final ChargeCodeReadPlatformService chargeCodeReadPlatformService;
	    
	    
	    @Autowired
	    public ContractPeriodApiResource(final PlatformSecurityContext context, final DefaultToApiJsonSerializer<SubscriptionData> toApiJsonSerializer,
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final ChargeCodeReadPlatformService chargeCodeReadPlatformService, 
	    final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final ContractPeriodReadPlatformService contractPeriodReadPlatformService) {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.contractPeriodReadPlatformService=contractPeriodReadPlatformService;
		        this.chargeCodeReadPlatformService=chargeCodeReadPlatformService;
		    }		
		
	
    /**
	 * using this method for Posting contract details
	 */
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createNewContract(final String apiRequestBodyAsJson) {
		
		  final CommandWrapper commandRequest = new CommandWrapperBuilder().createContract().withJson(apiRequestBodyAsJson).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * Using this method getting one contract list using by contractId
	 */
	@GET
	@Path("{contractId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveOneContractDetails(@PathParam("contractId") final Long contractId, @Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		SubscriptionData subscriptionData = this.contractPeriodReadPlatformService.retrieveSubscriptionData(contractId);
		 final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		 final List<DurationTypeData> durationTypeData = this.chargeCodeReadPlatformService.getDurationType();
		 subscriptionData = new SubscriptionData(durationTypeData, subscriptionData);
	        return this.toApiJsonSerializer.serialize(settings, subscriptionData, RESPONSE_DATA_PARAMETERS);
	}
	
	/**
	 * Using this method getting all contracts list
	 */
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveAllContracts(@Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		final Collection<SubscriptionData> subscriptionData=this.contractPeriodReadPlatformService.retrieveAllSubscription();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, subscriptionData, RESPONSE_DATA_PARAMETERS);
	}
	
	/**
	 * using this method editing contract details
	 */
	@PUT
	@Path("{contractId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String updateSubscription(@PathParam("contractId") final Long contractId, final String apiRequestBodyAsJson){

		
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateContract(contractId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);
	}
	
	/**
	 * using this method deleting  contract details by contractId
	 */
	 @DELETE
		@Path("{contractId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String deleteSubscription(@PathParam("contractId") final Long contractId) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteContract(contractId).build();

	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

	        return this.toApiJsonSerializer.serialize(result);

		}
	 
	 /**
	 * template for creating contract details
	 */
	 @GET
		@Path("template")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveAllSubscriptionDetails(@Context final UriInfo uriInfo) {

		 context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
			
			final List<DurationTypeData> durationTypeData = this.chargeCodeReadPlatformService.getDurationType();
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters()); 
			final SubscriptionData subscriptionData = new SubscriptionData(durationTypeData);
	         return this.toApiJsonSerializer.serialize(settings, subscriptionData, RESPONSE_DATA_PARAMETERS);
		}
	 

}
