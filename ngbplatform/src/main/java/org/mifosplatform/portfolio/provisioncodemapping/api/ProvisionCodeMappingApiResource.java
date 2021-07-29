package org.mifosplatform.portfolio.provisioncodemapping.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mapping.data.ChannelMappingData;
import org.mifosplatform.portfolio.product.service.ProductReadPlatformService;
import org.mifosplatform.portfolio.provisioncodemapping.data.ProvisionCodeMappingData;
import org.mifosplatform.portfolio.provisioncodemapping.service.ProvisionCodeMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/provisioncodemapping")
@Component
@Scope("singleton")
public class ProvisionCodeMappingApiResource {
	
	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id","provisionCode","networkCode","provisionValue"));
			
	private final static  String RESOURCE_TYPE = "PROVISIONCODEMAPPING";
	
	private final ToApiJsonSerializer toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	private final PlatformSecurityContext context;
	private final ProvisionCodeMappingReadPlatformService provisionCodeMappingReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final ProductReadPlatformService productReadPlatformService;
	
	
	@Autowired
	public ProvisionCodeMappingApiResource(ToApiJsonSerializer toApiJsonSerializer,
		   PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
		   PlatformSecurityContext context,ProvisionCodeMappingReadPlatformService provisionCodeMappingReadPlatformService,
		   final ApiRequestParameterHelper apiRequestParameterHelper,ProductReadPlatformService productReadPlatformService) {

		this.toApiJsonSerializer = toApiJsonSerializer;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.context = context;
		this.provisionCodeMappingReadPlatformService = provisionCodeMappingReadPlatformService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;	
        this.productReadPlatformService = productReadPlatformService;
	}

	// using this method posting  ProvisionCodeMapping data 
	
		@POST
		@Produces({MediaType.APPLICATION_JSON})
		@Consumes({MediaType.APPLICATION_JSON})
	    public String createProvisionCodeMapping(final String apiRequestBodyAsJson) {
						
			final CommandWrapper commandRequest = new CommandWrapperBuilder().createProvisionCodeMapping().withJson(apiRequestBodyAsJson).build();
		    final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
					
			}
		
		/**
		 * using this method getting  all ProvisionCodeMapping data 
		 */	
		 	@GET
			@Consumes({ MediaType.APPLICATION_JSON })
			@Produces({ MediaType.APPLICATION_JSON })
			public String retrieveAllProvisionCodeMappingDetails(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,
					@QueryParam("offset") final Integer offset) {
		 		
			   
		       this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
			   final SearchSqlQuery searchProvisionCodeMapping =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
			   final Page<ProvisionCodeMappingData> provisionCodeMappingDatas = this.provisionCodeMappingReadPlatformService.retrieveProvisionCodeMapping(searchProvisionCodeMapping);
			   return this.toApiJsonSerializer.serialize(provisionCodeMappingDatas);
			}
		 	
			 /**
			 * this method for getting  ProvisionCodeMapping data using by id
			 */
		 	
			@GET
			@Path("{provisioncodemappingId}")
			@Consumes({MediaType.APPLICATION_JSON})
			@Produces({MediaType.APPLICATION_JSON})
			public String retrieveSingleProvisionCodeMappingDetails(@Context final UriInfo uriInfo ,@PathParam("provisioncodemappingId") final Long provisioncodemappingId){
				this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
				ProvisionCodeMappingData provisionCodeMappingData = this.provisionCodeMappingReadPlatformService.retrieveProvisionCodeMapping(provisioncodemappingId);
				final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());		
				return this.toApiJsonSerializer.serialize(settings,provisionCodeMappingData,RESPONSE_DATA_PARAMETERS);
				
			}
			

			/**
			 * using this method editing ProvisionCodeMapping data 
			 */	
			    @PUT
				@Path("{provisioncodemappingId}")
				@Consumes({MediaType.APPLICATION_JSON})
				@Produces({MediaType.APPLICATION_JSON})
				public String updateProvisionCodeMapping(@PathParam("provisioncodemappingId") final Long provisioncodemappingId, final String apiRequestBodyAsJson){

				 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateProvisionCodeMapping(provisioncodemappingId).withJson(apiRequestBodyAsJson).build();
				 final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
				  return this.toApiJsonSerializer.serialize(result);
				}
			    
			    /**
				 * using this method deleting single ProvisionCodeMapping data 
				 */	
			 	    @DELETE
					@Path("{provisioncodemappingId}")
					@Consumes({MediaType.APPLICATION_JSON})
					@Produces({MediaType.APPLICATION_JSON})
					public String deleteProvisionCodeMapping(@PathParam("provisioncodemappingId") final Long provisioncodemappingId) {
					 final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteProvisionCodeMapping(provisioncodemappingId).build();
			        final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
			        return this.toApiJsonSerializer.serialize(result);

					}
			 	    
			 	  
			    

}
