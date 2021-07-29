package org.mifosplatform.organisation.salescataloge.api;

import java.util.ArrayList;
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

import org.mifosplatform.billing.payterms.data.PaytermData;
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
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.organisation.salescataloge.service.SalesCatalogeReadPlatformService;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/salescataloge")
@Component
@Scope("singleton")
public class SalesCatalogeApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	private final static  String RESOURCE_TYPE = "SALESCATALOGE";
	
	private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	private final ToApiJsonSerializer apiJsonSerializer;
	final private PlatformSecurityContext context;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	private final SalesCatalogeReadPlatformService salesCatalogeReadPlatformService;
	private final PlanReadPlatformService planReadPlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;

	@Autowired
	public SalesCatalogeApiResource(PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer apiJsonSerializer,final PlatformSecurityContext context,
			final ApiRequestParameterHelper apiRequestParameterHelper,final SalesCatalogeReadPlatformService salesCatalogeReadPlatformService,
			final PlanReadPlatformService planReadPlatformService,final MCodeReadPlatformService mCodeReadPlatformService) {
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.context = context;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.salesCatalogeReadPlatformService = salesCatalogeReadPlatformService;
		this.planReadPlatformService = planReadPlatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
	}

	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String SalesCatalogeDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchSalesCataloge = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<SalesCatalogeData> salescatalogeDatas = salesCatalogeReadPlatformService.retrieveSalesCataloge(searchSalesCataloge);
		return apiJsonSerializer.serialize(salescatalogeDatas);
	}
	
	@GET
	@Path("{salescatalogeId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String SalesCatalogeDetails(@Context final UriInfo uriInfo ,@PathParam("salescatalogeId") final Long salescatalogeId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		SalesCatalogeData salesCatalogeData = this.salesCatalogeReadPlatformService.retrieveSalesCataloge(salescatalogeId);
		salesCatalogeData=handleTemplateData(salesCatalogeData);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings,salesCatalogeData,RESPONSE_DATA_PARAMETERS);
	}

	private SalesCatalogeData handleTemplateData(SalesCatalogeData salesCatalogeData) {
		final List<PlanData> data = this.planReadPlatformService.retrievePlanDataForDropdown();
		final Collection<MCodeData> salesPlanCategoryData = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_SALES_CATEGORY);
		List<PlanData> plans = new ArrayList<>();
		 if(salesCatalogeData != null){
			 
			 plans = this.salesCatalogeReadPlatformService.retrieveSelectedPlans(salesCatalogeData.getId());
			int size = data.size();
			final int selectedsize = plans.size();
				for (int i = 0; i < selectedsize; i++)
	     			{
					final Long selected = plans.get(i).getId();
					for (int j = 0; j < size; j++) {
						final Long avialble = data.get(j).getId();
						if (selected.equals(avialble)) {
							data.remove(j);
							size--;
						}
					}
				}
	     }
		 return new SalesCatalogeData(data,salesCatalogeData,plans,salesPlanCategoryData);
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createSalesCataloge(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createSalesCataloge().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);		
	}
	
	@PUT
	@Path("{salescatalogeId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateSalesCataloge(@PathParam("salescatalogeId") final Long salescatalogeId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateSalesCataloge(salescatalogeId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}

	@DELETE
	@Path("{salescatalogeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteSalesCataloge(@PathParam("salescatalogeId") final Long salescatalogeId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteSalesCataloge(salescatalogeId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("dropdown")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String SalesCatalogeDetailsForDropDown(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final List<SalesCatalogeData> salesCatalogeData = this.salesCatalogeReadPlatformService.retrieveSalesCatalogesForDropdown();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings,salesCatalogeData,RESPONSE_DATA_PARAMETERS);
	}
	
	@GET
	@Path("salescataloges/{salesCatalogeId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String SalesCatalogeDetailsForPlans(@Context final UriInfo uriInfo ,@PathParam("salesCatalogeId") final Long salesCatalogeId,@QueryParam("planId")Long planId,@QueryParam("clientId")Long clientId,@QueryParam("clientServiceId")Long clientServiceId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		SalesCatalogeData salesCatalogeData = handleTemplateRelatedData(salesCatalogeId,planId,clientId,clientServiceId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings,salesCatalogeData,RESPONSE_DATA_PARAMETERS);
	}
	
private SalesCatalogeData handleTemplateRelatedData(Long salesCatalogeId, Long planId,Long clientId,Long clientServiceId) {
		
	final List<PlanCodeData> planData = this.planReadPlatformService.retrievePlanData(salesCatalogeId,planId,clientId,clientServiceId);
	  final List<SubscriptionData> contractPeriod=this.planReadPlatformService.retrieveSubscriptionData(null,null);

		return new SalesCatalogeData(planData,contractPeriod);
	}
	
}
