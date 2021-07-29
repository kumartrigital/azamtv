package org.mifosplatform.portfolio.client.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.mifosplatform.infrastructure.configuration.data.ConfigurationPropertyData;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/beesmart")
@Component
@Scope("singleton")
public class BeesmartClientApiResource {

	private static final String CONFIGRESOURCENAMEFORPERMISSIONS = "CONFIGURATION";
	private final Set<String> RESPONSE_CONFIGDATA_PARAMETERS = new HashSet<String>(Arrays.asList("id","name","enabled","value","module","description"));
	
	private final PlatformSecurityContext context;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	private final ConfigurationReadPlatformService configurationReadPlatformService;
	private final DefaultToApiJsonSerializer<ConfigurationPropertyData> configurationPropertyDataJsonSerializer;
	
	@Autowired
	public BeesmartClientApiResource(final PlatformSecurityContext context,final ApiRequestParameterHelper apiRequestParameterHelper,
			final ToApiJsonSerializer<ClientData> toApiJsonSerializer,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final ClientReadPlatformService clientReadPlatformService,final ConfigurationRepository configurationRepository,
			final ConfigurationReadPlatformService configurationReadPlatformService,final DefaultToApiJsonSerializer<ConfigurationPropertyData> configurationPropertyDataJsonSerializer){
		
		this.context = context;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
		this.configurationRepository = configurationRepository;
		this.configurationReadPlatformService = configurationReadPlatformService;
		this.configurationPropertyDataJsonSerializer = configurationPropertyDataJsonSerializer;
		
	}
	
	@GET
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveClientWalletAmount(@PathParam("clientId") final Long clientId,@QueryParam("type") String type, @Context final UriInfo uriInfo) {
		
		 context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
	     final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	     ClientData clientData = this.clientReadPlatformService.retrieveClientWalletAmount(clientId,type);
	     if(clientData != null){
	    	 Configuration configurationProperty=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_BALANCE_CHECK);
	    	 String balanceCheck="N";
	    	 if(configurationProperty.isEnabled()){
	        	balanceCheck="Y";
	    	 }
	    	 clientData = ClientData.templateOnTop(clientData, null,null,null,null,balanceCheck);
	     }
		
	     return this.toApiJsonSerializer.serialize(settings, clientData, ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
	}
	
	@GET
	@Path("/config/{configName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveConfigData(@PathParam("configName") final String configName, @Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(CONFIGRESOURCENAMEFORPERMISSIONS);
		final ConfigurationPropertyData configurationData = this.configurationReadPlatformService.retrieveGlobalConfigurationByName(configName);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.configurationPropertyDataJsonSerializer.serialize(settings, configurationData, this.RESPONSE_CONFIGDATA_PARAMETERS);
	}
	
	 /**
     * this method is using for edit a client
     */
    @PUT
    @Path("update")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String update(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() 
                .updateBeesmartClient() 
                .withJson(apiRequestBodyAsJson) 
                .build(); 

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
    
    @DELETE
    @Path("{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String delete(final String apiRequestBodyAsJson,@PathParam("clientId") Long clientId) {
    	
    	final CommandWrapper commandRequest = new CommandWrapperBuilder() 
    	.deleteBeesmartClient(clientId) 
    	.withJson(apiRequestBodyAsJson) 
    	.build(); 
    	
    	final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
    	
    	return this.toApiJsonSerializer.serialize(result);
    }
}
