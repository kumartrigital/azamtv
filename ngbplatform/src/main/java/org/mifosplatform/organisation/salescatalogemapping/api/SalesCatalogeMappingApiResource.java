package org.mifosplatform.organisation.salescatalogemapping.api;

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
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.salescataloge.service.SalesCatalogeReadPlatformService;
import org.mifosplatform.organisation.salescatalogemapping.data.SalesCatalogeMappingData;
import org.mifosplatform.organisation.salescatalogemapping.service.SalesCatalogeMappingReadPlatformService;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/salescatalogemapping")
@Component
@Scope("singleton")
public class SalesCatalogeMappingApiResource {


	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));;
	private final static String RESOURCE_TYPE = "SALESCATALOGEMAPPING";
	
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<SalesCatalogeMappingData> apiJsonSerializer;
	final private PlatformSecurityContext context;
	final private SalesCatalogeMappingReadPlatformService salesCatalogeMappingReadPlatformService;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private SalesCatalogeReadPlatformService salesCatalogeReadPlatformService;
	final private PlanReadPlatformService planReadPlatformService;
	final private MCodeReadPlatformService mCodeReadPlatformService;

	@Autowired
	public SalesCatalogeMappingApiResource(
			PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer<SalesCatalogeMappingData> apiJsonSerializer,final PlatformSecurityContext context,
			final SalesCatalogeMappingReadPlatformService salesCatalogeMappingReadPlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper,final SalesCatalogeReadPlatformService 
			salesCatalogeReadPlatformService,final PlanReadPlatformService planReadPlatformService,
			final MCodeReadPlatformService mCodeReadPlatformService) {
				
		        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
				this.apiJsonSerializer = apiJsonSerializer;
				this.context = context;
				this.salesCatalogeMappingReadPlatformService = salesCatalogeMappingReadPlatformService;
				this.apiRequestParameterHelper = apiRequestParameterHelper;
				this.salesCatalogeReadPlatformService = salesCatalogeReadPlatformService;
				this.planReadPlatformService = planReadPlatformService;
				this.mCodeReadPlatformService = mCodeReadPlatformService;
	}

	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String SalesCatalogeMappingDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchSalesCatalogeMapping = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<SalesCatalogeMappingData> salescatalogemappingDatas = salesCatalogeMappingReadPlatformService.retrieveSalesCatalogeMapping(searchSalesCatalogeMapping);
		return apiJsonSerializer.serialize(salescatalogemappingDatas);
	}
	


	@GET
	@Path("{salescatalogemappingId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveSalesCatalogeMappingDetails(@Context final UriInfo uriInfo ,@PathParam("salescatalogemappingId") final Long salescatalogemappingId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		SalesCatalogeMappingData salescatalogemappingData = this.salesCatalogeMappingReadPlatformService.retrieveSalesCatalogeMapping(salescatalogemappingId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if(settings.isTemplate()){
			salescatalogemappingData = this.handleTemplateData(salescatalogemappingData);
		}
		return this.apiJsonSerializer.serialize(settings,salescatalogemappingData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createSalesCatalogeMapping(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createSalesCatalogeMapping().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);	
		
	}
	
	@PUT
	@Path("{salescatalogemappingId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateSalesCatalogeMapping(@PathParam("salescatalogemappingId") final Long salescatalogemappingId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateSalesCatalogeMapping(salescatalogemappingId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@DELETE
	@Path("{salescatalogemappingId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteSalesCatalogeMapping(@PathParam("salescatalogemappingId") final Long salescatalogemappingId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteSalesCatalogeMapping(salescatalogemappingId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final SalesCatalogeMappingData salescatalogemappingData = this.handleTemplateData(null);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, salescatalogemappingData,RESPONSE_DATA_PARAMETERS);
		
	}

	private SalesCatalogeMappingData handleTemplateData(SalesCatalogeMappingData salescatalogemappingData) {
		if(salescatalogemappingData == null){
			salescatalogemappingData = new SalesCatalogeMappingData();
		}
		salescatalogemappingData.setSalesCatalogeDatas(this.salesCatalogeReadPlatformService.retrieveSalesCatalogesForDropdown());
		salescatalogemappingData.setPlanDatas(this.planReadPlatformService.retrievePlanDataForDropdown());
		salescatalogemappingData.setSalesCatalogeMappingDatas(this.salesCatalogeMappingReadPlatformService.retrieveSalesCatalogeMappingData());
		salescatalogemappingData.setSalesPlanCategoryData(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_SALES_CATEGORY));
		return salescatalogemappingData;
	}
	
	
}
