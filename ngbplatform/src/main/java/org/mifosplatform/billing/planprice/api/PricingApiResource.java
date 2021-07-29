package org.mifosplatform.billing.planprice.api;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.billing.chargecode.service.ChargeCodeReadPlatformService;
import org.mifosplatform.billing.discountmaster.service.DiscountReadPlatformService;
import org.mifosplatform.billing.planprice.data.PricingData;
import org.mifosplatform.billing.planprice.service.PriceReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;
import org.mifosplatform.organisation.monetary.service.OrganisationCurrencyReadPlatformService;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.organisation.priceregion.service.RegionalPriceReadplatformService;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hugo
 * Class for Retrieving,posting and update of price details for plan
 *
 */
@Path("/prices")
@Component
@Scope("singleton")
public class PricingApiResource {

	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("planCode","planId","serviceId","chargeId","price","serviceCode","chargeCode",
			"chargeVariantId","discountId","planCode","id", "isPrepaid","serviceData","priceId","chargeData","data", "chargeCode","chargeVaraint","price","priceregion","priceRegionData","currencyId"));
    	
		private final String resourceNameForPermissions = "PRICE";
		private final PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<PricingData> toApiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final PriceReadPlatformService priceReadPlatformService;
	    private final RegionalPriceReadplatformService regionalPriceReadplatformService;
	    private final PlanReadPlatformService planReadPlatformService;
	    private final ChargeCodeReadPlatformService chargeCodeReadPlatformService;
	    private final DiscountReadPlatformService discountReadPlatformService;
	    private final OrganisationCurrencyReadPlatformService currencyReadPlatformService;
	    private final PlanRepository planRepository;
	    private final MCodeReadPlatformService mCodeReadPlatformService;
	    @Autowired
	    public PricingApiResource(final PlatformSecurityContext context,final RegionalPriceReadplatformService regionalPriceReadplatformService, 
	    		final DefaultToApiJsonSerializer<PricingData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
	    		final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final PriceReadPlatformService priceReadPlatformService,
	    		final PlanReadPlatformService planReadPlatformService,final ChargeCodeReadPlatformService chargeCodeReadPlatformService,
	    		final  DiscountReadPlatformService discountReadPlatformService,final PlanRepository planRepository,
	    		final OrganisationCurrencyReadPlatformService currencyReadPlatformService,
	    		final MCodeReadPlatformService codeReadPlatformService) {
		        
	    		this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.planReadPlatformService=planReadPlatformService;
		        this.priceReadPlatformService=priceReadPlatformService;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.chargeCodeReadPlatformService=chargeCodeReadPlatformService;
		        this.regionalPriceReadplatformService=regionalPriceReadplatformService;
		        this.discountReadPlatformService=discountReadPlatformService;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.currencyReadPlatformService = currencyReadPlatformService;
		        this.planRepository = planRepository;
		        this.mCodeReadPlatformService = codeReadPlatformService;
		    }	
	    
	  
	    @POST
	    @Path("{planId}")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String createPrice(@PathParam("planId") final Long planId,final String apiRequestBodyAsJson) {
	    	final CommandWrapper commandRequest = new CommandWrapperBuilder().createPrice(planId).withJson(apiRequestBodyAsJson).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
	    }
	    
	    @GET
	    @Path("template")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String retrievePricing(@QueryParam("planId") final Long planId,@Context final UriInfo uriInfo) {
	    	PricingData pricingData=null;
	        pricingData=handleTemplateData(planId,pricingData);
	    	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    	return this.toApiJsonSerializer.serialize(settings, pricingData, RESPONSE_DATA_PARAMETERS);
	}

	    private PricingData handleTemplateData(Long planId,PricingData pricingData) {
	    	
	    	List<ServiceData> serviceData = this.priceReadPlatformService.retrieveServiceDetails(planId);
	    	final ApplicationCurrencyConfigurationData currencydata=this.currencyReadPlatformService.retrieveCurrencyConfiguration();
	    	List<ChargeCodeData> chargeCode = this.chargeCodeReadPlatformService.retrieveAllChargeCodes();
	    	List<EnumOptionData> datas = this.priceReadPlatformService.retrieveChargeVariantData();
	    	List<DiscountMasterData> discountMasterDatas= this.discountReadPlatformService.retrieveAllDiscounts();
	    	List<PriceRegionData> priceRegionData = this.regionalPriceReadplatformService.getPriceRegionsDetails();
	    	final Collection<MCodeData> glRealatedData = mCodeReadPlatformService.getCodeValue(CodeNameConstants.GL_ID);
	    	List<SubscriptionData> contractPeriods = this.planReadPlatformService.retrieveSubscriptionData(null,null);
	    	//final ApplicationCurrencyConfigurationData currencydata=this.currencyReadPlatformService.retrieveCurrencyConfiguration();
			
	    		for(int i=0;i<contractPeriods.size();i++){
	    			if(contractPeriods.get(i).getSubscriptionType().equalsIgnoreCase("None")){
	    				contractPeriods.remove(contractPeriods.get(i));
	 		}
	    		}
	    		
	    		PricingData newPricingData= new PricingData(serviceData,currencydata,chargeCode, datas,discountMasterDatas,serviceData.get(0).getPlanCode(), planId,pricingData,
	    					            priceRegionData,glRealatedData,contractPeriods,serviceData.get(0).getIsPrepaid(),serviceData.get(0).getCurrencyId(),serviceData.get(0).getCurrencyCode(),serviceData.get(0).getChargeOwner());
	    		newPricingData.setDuration(this.planReadPlatformService.retrievePlanData(planId).getDuration());
	    		return newPricingData;
	    }


		@GET
	    @Path("{planId}")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String retrievePrice(@PathParam("planId") final Long planId,@Context final UriInfo uriInfo) {
	    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	    	List<ServiceData> serviceData = this.priceReadPlatformService.retrievePriceDetails(planId,null);
	    	PricingData data = new PricingData(serviceData);
	    	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    	if(settings.isTemplate()){
	    		data = this.handleTemplateData(planId,data);
			}
	    	return this.toApiJsonSerializer.serialize(data);
	    }
	
	    @GET
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String retrievePlanAndPriceDetails(@Context final UriInfo uriInfo,@QueryParam("region") final String region) {
	    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	    	List<PricingData> pricingDatas = this.priceReadPlatformService.retrievePlanAndPriceDetails(region);
	    	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    	return this.toApiJsonSerializer.serialize(settings, pricingDatas, RESPONSE_DATA_PARAMETERS);
	    }
	
	    @GET
	    @Path("pricedetails/{priceId}")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String retrieveIndividualPrice(@PathParam("priceId") final String priceId,@Context final UriInfo uriInfo) {
	    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	    	PricingData singlePriceData = this.priceReadPlatformService.retrieveSinglePriceDetails(priceId);
	    	singlePriceData=handleTemplateData(singlePriceData.getPlanId(), singlePriceData);
	    	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    	return this.toApiJsonSerializer.serialize(settings, singlePriceData, RESPONSE_DATA_PARAMETERS);
	    }
	 
	    @PUT
	    @Path("update/{priceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String updatePrice(@PathParam("priceId") final Long priceId, final String apiRequestBodyAsJson){
	    	final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePrice(priceId).withJson(apiRequestBodyAsJson).build();
	    	final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	    	return this.toApiJsonSerializer.serialize(result);
		}
	    
	    @DELETE
		@Path("{priceId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String deletePrice(@PathParam("priceId") final Long priceId) {
	    	final CommandWrapper commandRequest = new CommandWrapperBuilder().deletePrice(priceId).build();
	    	final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	    	return this.toApiJsonSerializer.serialize(result);
		}

}
	