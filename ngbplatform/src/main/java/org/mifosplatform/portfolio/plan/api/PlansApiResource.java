package org.mifosplatform.portfolio.plan.api;

import java.util.ArrayList;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.emun.data.EnumValuesConstants;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.infrastructure.codes.service.CodeReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.monetary.service.OrganisationCurrencyReadPlatformService;
import org.mifosplatform.organisation.partner.data.PartnersData;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.PlanQulifierData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.product.service.ProductReadPlatformService;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
* 
*
* @author istream
*/
@Path("/plans")
@Component
@Scope("singleton")
public class PlansApiResource  {
	
//Response Data parameters
	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "planCode", "plan_description", "startDate","isHwReq",
            "endDate", "status", "service_code", "service_description","Period", "charge_code", "charge_description","servicedata","contractPeriod","provisionSystem",
            "service_type", "charge_type", "allowedtypes","selectedservice","bill_rule","billiingcycle","servicedata","services","statusname","planstatus","volumeTypes","planType","currencyId","isAdvance"));
	 
		private static final String RESOURCE_NAME_FOR_PERMISSION="PLAN";//resourceNameForPermissions = "PLAN";
		private  final  PlatformSecurityContext context;
	    private final DefaultToApiJsonSerializer<PlanData> toApiJsonSerializer;
	    private final DefaultToApiJsonSerializer<PlanQulifierData> apiJsonSerializer;
	    private final ApiRequestParameterHelper apiRequestParameterHelper;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final PlanReadPlatformService planReadPlatformService;
	    private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
	    private final MCodeReadPlatformService mCodeReadPlatformService;
	    private final CodeReadPlatformService codeReadPlatformService;
	    private final OrganisationCurrencyReadPlatformService currencyReadPlatformService;
		private final ProductReadPlatformService productReadPlatformService;
	    private final CrmServices crmServices; 
	    private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
	
	    
	   
	    @Autowired
	    public PlansApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<PlanData> toApiJsonSerializer,
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	    		final PlanReadPlatformService planReadPlatformService,final ServiceMasterReadPlatformService serviceMasterReadPlatformService,
	    		final MCodeReadPlatformService mCodeReadPlatformService,final CodeReadPlatformService codeReadPlatformService,
	    		final DefaultToApiJsonSerializer<PlanQulifierData> apiJsonSerializer,
	    		final OrganisationCurrencyReadPlatformService currencyReadPlatformService,final ProductReadPlatformService productReadPlatformService,
	    		CrmServices crmServices,final ContractPeriodReadPlatformService contractPeriodReadPlatformService) {
	    	
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.mCodeReadPlatformService=mCodeReadPlatformService;
		        this.planReadPlatformService=planReadPlatformService;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.serviceMasterReadPlatformService=serviceMasterReadPlatformService;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.codeReadPlatformService = codeReadPlatformService;
		        this.apiJsonSerializer = apiJsonSerializer;
		        this.currencyReadPlatformService = currencyReadPlatformService;
		        this.productReadPlatformService = productReadPlatformService;
		        this.crmServices=crmServices;
		        this.contractPeriodReadPlatformService = contractPeriodReadPlatformService;
	    }	
	    
	/**
	 * @param apiRequestBodyAsJson
	 * @return
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createPlan(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest=new CommandWrapperBuilder().createPlan().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * @param uriInfo
	 * @return
	 */
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlanTemplate(@Context final UriInfo uriInfo) {
		 
		context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSION);
		PlanData planData = this.handleTemplateData(null);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, planData, RESPONSE_DATA_PARAMETERS);
	
	}
	
	
	private PlanData handleTemplateData(PlanData planData) {
		
		 planData = this.retrivePlanServices(planData);
		 /*if(planData == null) {
			 planData = new PlanData();
		 }*/
		 
		 planData.setBillRuleDatas(this.codeReadPlatformService.retrievebillRules(EnumValuesConstants.ENUMVALUE_PROPERTY_BILLING_RULES));
		 planData.setPlanStatus(this.planReadPlatformService.retrieveNewStatus());
		 planData.setCurrencydata(this.currencyReadPlatformService.retrieveCurrencyConfiguration());
		 planData.setProvisionSysData(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_PROVISIONING));
		 planData.setPlanTypeData(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.PLAN_TYPE));
		 planData.setVolumeTypes(this.planReadPlatformService.retrieveVolumeTypes());
		 planData.setServiceMasterDatas(this.serviceMasterReadPlatformService.retriveServices("S"));
		 planData.setSubscriptiondata(this.contractPeriodReadPlatformService.retrieveAllSubscription());
		 return planData;
	}

	
	/**
	 * @param planType
	 * @param uriInfo
	 * @return
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllPlans(@QueryParam("planType") final String planType,  @Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset) {
 		 context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSION);
 		final SearchSqlQuery searchPlan = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		//final List<PlanData> products = this.planReadPlatformService.retrievePlanData(planType);
		final Page<PlanData> products = this.planReadPlatformService.retrievePlanData(planType,searchPlan);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		//return this.toApiJsonSerializer.serialize(settings, products, RESPONSE_DATA_PARAMETERS);
		return this.toApiJsonSerializer.serialize(products);
	}
	
	/**
	 * @param planId
	 * @param uriInfo
	 * @return
	 */
	@GET
	@Path("{planId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlanDetails(@PathParam("planId") final Long planId,@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSION);
		PlanData singlePlandata = this.planReadPlatformService.retrievePlanData(planId);
		singlePlandata.setSubscriptiondata(this.contractPeriodReadPlatformService.retrieveAllSubscription());
		singlePlandata = this.retrivePlanServices(singlePlandata);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if(settings.isTemplate()) {
			singlePlandata=handleTemplateData(singlePlandata);
		}
		
		return this.toApiJsonSerializer.serialize(settings, singlePlandata, RESPONSE_DATA_PARAMETERS);
	}

	private PlanData retrivePlanServices(PlanData singlePlandata) {
		final List<ServiceData> data = this.productReadPlatformService.retriveProducts("P",null);
		 List<ServiceData> products = new ArrayList<>();
		if(singlePlandata != null){
			products = this.planReadPlatformService.retrieveSelectedProducts(singlePlandata.getId());
			 int size = data.size();
			 final int selectedsize = products.size();
			 for (int i = 0; i < selectedsize; i++){
					final Long selected = products.get(i).getId();
					for (int j = 0; j < size; j++) {
						final Long avialble = data.get(j).getId();
						if (selected.equals(avialble)) {
							data.remove(j);
							size--;
						}
					}
				}
			 singlePlandata.setProducts(data);
			 singlePlandata.setSelectedProducts(products);
		}else {
			singlePlandata = new PlanData();singlePlandata.setProducts(data);
		}
		
		
		return singlePlandata;
	}

	/**
	 * @param planId
	 * @param apiRequestBodyAsJson
	 * @return
	 */
	@PUT
	@Path("{planCode}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updatePlan(@PathParam("planCode") final Long planId,final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePlan(planId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);
	}
	
	
	 /**
	 * @param planId
	 * @return
	 */
	   @DELETE
		@Path("{planCode}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String deletePlan(@PathParam("planCode") final Long planId) {
		  final CommandWrapper commandRequest = new CommandWrapperBuilder().deletePlan(planId).build();
          final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
          return this.toApiJsonSerializer.serialize(result);

		}
	   
	    @GET
		@Path("planQulifier/{planId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrievePlanQulifierDetails(@PathParam("planId") final Long planId,@Context final UriInfo uriInfo) {
			
			context.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSION);
			
			List<PartnersData> partnersDatas =this.planReadPlatformService.retrievePartnersData(planId);
			List<PartnersData> availablePartnersDatas = this.planReadPlatformService.retrieveAvailablePartnersData(planId);
			PlanQulifierData data=new PlanQulifierData(partnersDatas,availablePartnersDatas);  
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.apiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
		}
	    
	    @PUT
		@Path("planQulifier/{planId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String updatePlanQulifierData(@PathParam("planId") final Long planId,final String apiRequestBodyAsJson) {
			 final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePlanQualifier(planId).withJson(apiRequestBodyAsJson).build();
			 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			  return this.toApiJsonSerializer.serialize(result);
		}
	    
	    @GET
		@Path("getcrmplans")
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String getPlan(@QueryParam("key") final String key, @QueryParam("value") final String value) {
			this.context.authenticatedUser();
			final List<PlanData> plan= this.crmServices.retrivePlans(key, value);
			return this.toApiJsonSerializer.serialize(plan);
		}
	    
		@GET
		@Path("bouque")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrivebouque(@Context final UriInfo uriInfo){
			final List<PlanData>plandata = this.planReadPlatformService.retrivebouque();
			final List<PlanData>plandata1 = this.planReadPlatformService.retrivebouque();
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(plandata);
			
		}
	    
		@GET
		@Path("Nonbouque")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retriveNonbouque(@Context final UriInfo uriInfo){
			final List<PlanData>plandata = this.planReadPlatformService.retriveNonbouque();
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return this.toApiJsonSerializer.serialize(plandata);
			
		}
	    
	    
	    
	    
	    
	    
}