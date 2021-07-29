package org.mifosplatform.finance.paymentsgateway.api;

import java.util.Arrays;
import java.util.HashSet;
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
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayConfigurationReadPlatformService;
import org.mifosplatform.infrastructure.configuration.data.ConfigurationData;
import org.mifosplatform.infrastructure.configuration.data.ConfigurationPropertyData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/paymentgatewayconfigs")
@Component
@Scope("singleton")

/**
 * The class <code>PaymentGatewayConfigurationApiResource</code> is developed for
 * configuration and Storing the details of Third party PaymentGateway systems. 
 * 
 * @author ashokreddy
 *
 */
public class PaymentGatewayConfigurationApiResource {
	
	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("globalConfiguration"));

    private static final String RESOURCENAMEFORPERMISSIONS = "PAYMENTGATEWAYCONFIG";

    private final PlatformSecurityContext context;
    private final PaymentGatewayConfigurationReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<ConfigurationData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<ConfigurationPropertyData> propertyDataJsonSerializer;

    @Autowired
    public PaymentGatewayConfigurationApiResource(final PlatformSecurityContext context,
            final PaymentGatewayConfigurationReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<ConfigurationData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final DefaultToApiJsonSerializer<ConfigurationPropertyData> propertyDataJsonSerializer) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.propertyDataJsonSerializer=propertyDataJsonSerializer;
    }
    
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllConfigurations(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final ConfigurationData configurationData = this.readPlatformService.retrievePaymentGatewayConfiguration();
        
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        
        return this.toApiJsonSerializer.serialize(settings, configurationData, RESPONSE_DATA_PARAMETERS);
    }
    
    @GET
    @Path("{configId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveSingleConfiguration(@PathParam("configId") final Long configId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final ConfigurationPropertyData configurationData = this.readPlatformService.retrievePaymentGatewayConfiguration(configId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        
        return this.propertyDataJsonSerializer.serialize(settings, configurationData, this.RESPONSE_DATA_PARAMETERS);
    }
    
    @PUT
    @Path("{configId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updatePaymentGatewayConfiguration(@PathParam("configId") final Long configId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePaymentGatewayConfig(configId).withJson(apiRequestBodyAsJson).build();
        
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        
        return this.toApiJsonSerializer.serialize(result);
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String createPaymentGatewayConfiguration(final String jsonRequestBody){
    	
    	final CommandWrapper commandRequest = new CommandWrapperBuilder().createPaymentGatewayConfig().withJson(jsonRequestBody).build();
    	final CommandProcessingResult result= this.commandsSourceWritePlatformService.logCommandSource(commandRequest); 
    	return this.toApiJsonSerializer.serialize(result);
    	
    }

}
