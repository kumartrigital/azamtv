package org.mifosplatform.provisioning.provsionactions.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import org.mifosplatform.provisioning.provsionactions.data.ProvisioningActionData;
import org.mifosplatform.provisioning.provsionactions.service.ProvisionignActionReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.resource.Singleton;



@Singleton
@Component
@Path("/provisioningactions")
public class ProvisioningActionsApiResource {
	
	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id",
			"provisionType","action","provisionigSystem","isEnable"));
	private final  DefaultToApiJsonSerializer<ProvisioningActionData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final ProvisionignActionReadPlatformService provisionignActionReadPlatformService; 
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	
	@Autowired
	public ProvisioningActionsApiResource(final DefaultToApiJsonSerializer<ProvisioningActionData> apiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,final ProvisionignActionReadPlatformService provisionignActionReadPlatformService,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService){
		
		this.toApiJsonSerializer=apiJsonSerializer;
		this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.provisionignActionReadPlatformService=provisionignActionReadPlatformService;
		this.commandSourceWritePlatformService=commandSourceWritePlatformService;
		
	}
	
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllProvisioningActions(@Context final UriInfo uriInfo){
	
		final List<ProvisioningActionData> provisioningActionDatas=this.provisionignActionReadPlatformService.getAllProvisionActions();
	    final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    return this.toApiJsonSerializer.serialize(settings, provisioningActionDatas, this.RESPONSE_DATA_PARAMETERS);
	}
	
	@PUT
	@Path("/{provisionActionId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateProvisionActionStatus(@PathParam("provisionActionId") final Long provisionActionId,String apiRequestBodyAsJson){
		
		final CommandWrapper commandWrapper=new CommandWrapperBuilder().activeProvisionActions(provisionActionId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result=this.commandSourceWritePlatformService.logCommandSource(commandWrapper);
		return this.toApiJsonSerializer.serialize(result);
		
	}

}
