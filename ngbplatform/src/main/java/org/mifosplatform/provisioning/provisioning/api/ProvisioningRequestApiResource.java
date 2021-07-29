package org.mifosplatform.provisioning.provisioning.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.resource.Singleton;


@Singleton
@Component
@Path("/provisioningrequest")
public class ProvisioningRequestApiResource {
	
	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	
	private final  DefaultToApiJsonSerializer<ProvisioningRequestData> toApiJsonSerializer;
	
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	
	@Autowired
	public ProvisioningRequestApiResource(final DefaultToApiJsonSerializer<ProvisioningRequestData> apiJsonSerializer,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService){
		
		this.toApiJsonSerializer=apiJsonSerializer;
		this.commandSourceWritePlatformService=commandSourceWritePlatformService;
		
	}
	
	@POST
	@Path("/{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createProvisioningRequest(@PathParam("orderId") final Long orderId,String apiRequestBodyAsJson){
		
		final CommandWrapper commandWrapper = new CommandWrapperBuilder().createProvisioningRequest(orderId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandWrapper);
		return this.toApiJsonSerializer.serialize(result);
		
	}

}
