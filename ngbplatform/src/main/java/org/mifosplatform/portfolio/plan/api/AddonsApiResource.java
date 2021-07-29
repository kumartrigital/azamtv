package org.mifosplatform.portfolio.plan.api;

import java.util.Arrays;
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

import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.billing.chargecode.service.ChargeCodeReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.organisation.priceregion.service.RegionalPriceReadplatformService;
import org.mifosplatform.portfolio.addons.data.AddonsData;
import org.mifosplatform.portfolio.addons.data.AddonsPriceData;
import org.mifosplatform.portfolio.addons.service.AddonServiceReadPlatformService;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.servicemapping.data.ServiceMappingData;
import org.mifosplatform.portfolio.servicemapping.service.ServiceMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/addons")
@Component
@Scope("singleton")
public class AddonsApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id","serviceId","planDatas","chargeCodeDatas","priceRegionData",
			"planId","chargeCode","priceRegion"));
	
	 private final String resourceNameForPermissions = "ADDONS";
	 private final PlatformSecurityContext context;
	 private final DefaultToApiJsonSerializer<AddonsData> toApiJsonSerializer;
	 private final ApiRequestParameterHelper apiRequestParameterHelper;
	 private final OrderReadPlatformService orderReadPlatformService;
	 private final AddonServiceReadPlatformService addonServiceReadPlatformService; 
	 private final ChargeCodeReadPlatformService chargeCodeReadPlatformService;
	 private final ServiceMappingReadPlatformService serviceMappingReadPlatformService;
	 private final RegionalPriceReadplatformService regionalPriceReadplatformService;
	 private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	  
 @Autowired
 public AddonsApiResource(final DefaultToApiJsonSerializer<AddonsData> apiJsonSerializer,final PlatformSecurityContext context,
			  final ApiRequestParameterHelper apiRequestParameterHelper,final OrderReadPlatformService orderReadPlatformService,
			  final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,final RegionalPriceReadplatformService regionalPriceReadplatformService,
			  final ChargeCodeReadPlatformService chargeCodeReadPlatformService,final ServiceMappingReadPlatformService serviceMappingReadPlatformService,
			  final AddonServiceReadPlatformService addonServiceReadPlatformService){
		  
		  this.toApiJsonSerializer=apiJsonSerializer;
		  this.context=context;
		  this.chargeCodeReadPlatformService=chargeCodeReadPlatformService;
		  this.regionalPriceReadplatformService=regionalPriceReadplatformService;
		  this.orderReadPlatformService=orderReadPlatformService;
		  this.serviceMappingReadPlatformService=serviceMappingReadPlatformService;
		  this.addonServiceReadPlatformService=addonServiceReadPlatformService;
		  this.apiRequestParameterHelper=apiRequestParameterHelper;
		  this.commandsSourceWritePlatformService=commandSourceWritePlatformService;
		  
	  }
	  
    @POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String addAddonServices(final String apiRequestBodyAsJson){
			
	final CommandWrapper commandRequest = new CommandWrapperBuilder().createAddons().withJson(apiRequestBodyAsJson).build();
	final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	return this.toApiJsonSerializer.serialize(result);
			
	}
    
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveAddonsTemplate(@Context final UriInfo uriInfo) {
		
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	AddonsData addonsData = null;
	addonsData=handleTemplateDate(addonsData);
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, addonsData, RESPONSE_DATA_PARAMETERS);
	}

	private AddonsData handleTemplateDate(AddonsData addonsData) {
		
		List<PlanCodeData> planDatas = this.orderReadPlatformService.retrieveAllPlatformData(Long.valueOf(0),null,null,null,null,null);
		List<ChargeCodeData> chargeCodeDatas = this.chargeCodeReadPlatformService.retrieveAllChargeCodes();
		List<PriceRegionData> priceRegionData = this.regionalPriceReadplatformService.getPriceRegionsDetails();
		final List<ServiceMappingData> servicedatas = this.serviceMappingReadPlatformService.retrieveOptionalServices("Y");
	   
		addonsData=new AddonsData(addonsData,planDatas,chargeCodeDatas,priceRegionData,servicedatas); 
		return addonsData;
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveAllAddons(@Context final UriInfo uriInfo) {
		
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	List<AddonsData> addonsData = this.addonServiceReadPlatformService.retrieveAllPlatformData();
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, addonsData, RESPONSE_DATA_PARAMETERS);
	}
	
	@GET
	@Path("{addonId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveSingleAddonDetails(@PathParam("addonId") final Long addonId,@Context final UriInfo uriInfo) {
		
	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	AddonsData addonData= this.addonServiceReadPlatformService.retrieveSingleAddonData(addonId);
	List<AddonsPriceData> addonsPrices = this.addonServiceReadPlatformService.retrieveAddonPriceDetails(addonId);
	addonData.setPrices(addonsPrices);
	addonData=handleTemplateDate(addonData);
	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, addonData, RESPONSE_DATA_PARAMETERS);
	}

	@PUT
	@Path("{addonId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateAddonServices(@PathParam("addonId") final Long addonId, final String apiRequestBodyAsJson){
			
	final CommandWrapper commandRequest = new CommandWrapperBuilder().updateAddons(addonId).withJson(apiRequestBodyAsJson).build();
	final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	return this.toApiJsonSerializer.serialize(result);
			
	}
	
	@DELETE
	@Path("{addonId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteAddonServices(@PathParam("addonId") final Long addonId){
			
	final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteAddons(addonId).build();
	final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	return this.toApiJsonSerializer.serialize(result);
			
	}
}
