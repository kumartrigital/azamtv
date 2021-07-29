package org.mifosplatform.portfolio.slabRate.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.slabRate.data.SlabRateData;
import org.mifosplatform.portfolio.slabRate.service.SlabRateReadPlatformService;
import org.mifosplatform.portfolio.slabRate.service.SlabRateWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/slabrate")
@Component
@Scope("singleton")
public class SlabRateApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
    
	private final static  String RESOURCE_TYPE = "SLABRATE";
	
	private final ToApiJsonSerializer<SlabRateData> apiJsonSerializer;
	private final PlatformSecurityContext context;
	private final SlabRateReadPlatformService slabRateReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final SlabRateWritePlatformService slabRateWritePlatformService;
	
	@Autowired
	public SlabRateApiResource(final ToApiJsonSerializer<SlabRateData> apiJsonSerializer,final PlatformSecurityContext context,
			final SlabRateReadPlatformService slabRateReadPlatformService,ApiRequestParameterHelper apiRequestParameterHelper,
			final SlabRateWritePlatformService slabRateWritePlatformService){
		
		 this.apiJsonSerializer = apiJsonSerializer;
		 this.context = context;
		 this.slabRateReadPlatformService =slabRateReadPlatformService;
		 this.apiRequestParameterHelper = apiRequestParameterHelper;
		 this.slabRateWritePlatformService = slabRateWritePlatformService;
	}

	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String SlabRateDetails(){
	
	this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
	final List<SlabRateData> slabdatas = slabRateReadPlatformService.retrieveSlabRates();
	return apiJsonSerializer.serialize(slabdatas);
	
	}
	@GET
	@Path("slab")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String SlabRatebyIds(@QueryParam("SlabFrom") final String SlabFrom, @QueryParam("slabTo") final String slabTo){
		
	this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
	final List<SlabRateData> slabdatas = slabRateReadPlatformService.retrieveSlabRatesbyId(SlabFrom,slabTo);
	return apiJsonSerializer.serialize(slabdatas);
	
	}
	
	@POST
	@Path("calculation")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String calculation(final String jsonRequestBody){
		CommandProcessingResult result = this.slabRateWritePlatformService.Calculation(jsonRequestBody);
		return apiJsonSerializer.serialize(result);
	}
	

}
