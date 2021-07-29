/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.clientservice.api;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.mifosplatform.provisioning.networkelement.service.NetworkElementReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clientservice")
@Component
@Scope("singleton")
public class ClientServiceApiResource {

	private final static String RESOURCENAMEFORPERMISSIONS = "CLIENTSERVICE"; 
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
   
	private final ToApiJsonSerializer<ClientServiceData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final PlatformSecurityContext context;
    private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final MCodeReadPlatformService mCodeReadPlatformService;
    private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	private final NetworkElementReadPlatformService networkElementReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
    
    @Autowired
    public ClientServiceApiResource(final ToApiJsonSerializer<ClientServiceData> toApiJsonSerializer,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final PlatformSecurityContext context,
            final ServiceMasterReadPlatformService serviceMasterReadPlatformService,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final MCodeReadPlatformService mCodeReadPlatformService,
            final ClientServiceReadPlatformService clientServiceReadPlatformService,
            final NetworkElementReadPlatformService networkElementReadPlatformService,final ClientReadPlatformService clientReadPlatformService) {
        
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.context = context;
        this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.mCodeReadPlatformService = mCodeReadPlatformService;
        this.clientServiceReadPlatformService = clientServiceReadPlatformService;
        this.networkElementReadPlatformService = networkElementReadPlatformService;
        this.clientReadPlatformService = clientReadPlatformService;
    }
    
    @GET
    @Path("{clientId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveServices(@Context final UriInfo uriInfo,@PathParam("clientId")final Long clientId) {
	 
    	this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
    	List<ClientServiceData> ClientServiceData = this.clientServiceReadPlatformService.retriveClientServices(clientId);
    	
    	return this.toApiJsonSerializer.serialize(ClientServiceData);
    	
	}
    
    
    @GET
    @Path("{clientId}/{clientServiceId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveServices(@Context final UriInfo uriInfo,@PathParam("clientId")final Long clientId,@PathParam("clientServiceId")final Long clientServiceId) {
	 
    	this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
    	ClientServiceData clientServiceData = this.clientServiceReadPlatformService.retriveClientService(clientServiceId);
    	if(clientServiceData != null){
    		clientServiceData.setServiceParameterData(this.clientServiceReadPlatformService.retriveClientServiceDetails(clientServiceData.getId()));
    	}
    	
    	return this.toApiJsonSerializer.serialize(clientServiceData);
    	
	}
    
    @GET
    @Path("details/{clientId}/{clientServiceId}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveServiceDetails(@Context final UriInfo uriInfo,@PathParam("clientId")final Long clientId,@PathParam("clientServiceId")final Long clientServiceId) {
	 
    	this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
    	ClientServiceData clientServiceData = new ClientServiceData(this.clientServiceReadPlatformService.retriveClientServiceDetails(clientServiceId));
    	
    	
    	return this.toApiJsonSerializer.serialize(clientServiceData);
    	
	}
    /**
     * This method is using for create a new clientservice
     * @JSON as body
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createClientService().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    
    
    @GET
    @Path("template")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String retrieveTempleteInfo(@Context final UriInfo uriInfo) {
	 
    	this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
    	
    	final ClientServiceData clientServiceData = this.handleTemplateData(null);
    	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, clientServiceData, RESPONSE_DATA_PARAMETERS);
	}
    
    @GET
	@Path("servicedetails/{serviceId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllServiceDetails(@PathParam("serviceId") final Long serviceId,@QueryParam("paramCategory") final String paramCategory,@QueryParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {
    	try{	
    		this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		
    		final Collection<ServiceDetailData> serviceDetailDatas = this.serviceMasterReadPlatformService.retrieveServiceDetails(serviceId,paramCategory);
    		for(ServiceDetailData serviceDetailData:serviceDetailDatas){
    			if(serviceDetailData.getParamType().equalsIgnoreCase("text")){
    				serviceDetailData.setDetailValue(serviceDetailData.getParamValue());
    			}else if(serviceDetailData.getParamType().equalsIgnoreCase("combo")){
    				//serviceDetailData.setDetails(this.mCodeReadPlatformService.retriveMcode(serviceDetailData.getParamValue()));
	    					serviceDetailData.setDetails(this.networkElementReadPlatformService.retriveNetworkElementsForService(serviceDetailData.getParamValue()));
	    				
    			}else if(serviceDetailData.getParamType().equalsIgnoreCase("boolean")){
    				if(serviceDetailData.getParamValue().equalsIgnoreCase("true"))
    					serviceDetailData.setDetail(true);
    				else
    					serviceDetailData.setDetail(false);
    			}else if(serviceDetailData.getParamType().equalsIgnoreCase("date")){
    				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    				serviceDetailData.setDetailDate(formatter.parse(serviceDetailData.getParamValue()));
    			}else if(serviceDetailData.getParamType().equalsIgnoreCase("Query")){
    				serviceDetailData.setClientDetails(this.clientReadPlatformService.retriveClientDetails(serviceDetailData.getParamValue(),clientId));
    			}
    		}
    		return this.toApiJsonSerializer.serialize(serviceDetailDatas);
    	}catch(Exception e){
    		throw new PlatformDataIntegrityException("please.check.properdate.in.service", "Involid Date provided in Service");
    	}
	   
	}
    
    @POST
    @Path("{clientServiceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createClientServiceActivation(@PathParam("clientServiceId") final Long clientServiceId, String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createClientServiceActivation(clientServiceId).withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    
    
    @PUT
    @Path("suspend/{clientServiceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String suspendClientService(@PathParam("clientServiceId") final Long clientServiceId, String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().suspendClientService(clientServiceId).withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    @PUT
    @Path("reactive/{clientServiceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String reactiveClientService(@PathParam("clientServiceId") final Long clientServiceId, String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().reactiveClientService(clientServiceId).withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    @PUT
    @Path("terminate/{clientServiceId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String terminateClientService(@PathParam("clientServiceId") final Long clientServiceId, String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().terminateClientService(clientServiceId).withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
    
    
    private ClientServiceData handleTemplateData(ClientServiceData clientServiceData){
    	if(clientServiceData == null){
    		clientServiceData = new ClientServiceData();
    	}
    	clientServiceData.setServiceData(this.serviceMasterReadPlatformService.retriveServices("S"));
    	return clientServiceData;
    }
    
    @PUT
	@Path("serviceparams/{serviceParameterDataId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateServiceparameter(@PathParam("serviceParameterDataId") final Long serviceParameterDataId,final String jsonRequestBody){
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateServiceparameter(serviceParameterDataId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
	}
    
}