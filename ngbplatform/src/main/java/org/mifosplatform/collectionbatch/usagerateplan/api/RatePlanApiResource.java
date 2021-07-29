package org.mifosplatform.collectionbatch.usagerateplan.api;

import java.util.Arrays;
import java.util.HashSet;
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

import org.mifosplatform.billing.planprice.service.PriceReadPlatformService;
import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.collectionbatch.ratableusagemetric.service.RatableUsageMetricReadPlatformService;
import org.mifosplatform.collectionbatch.timemodel.service.TimeModelReadPlatformService;
import org.mifosplatform.collectionbatch.unitofmeasurement.service.UnitOfmeasurementReadPlatformService;
import org.mifosplatform.collectionbatch.usagerateplan.data.RatePlanData;
import org.mifosplatform.collectionbatch.usagerateplan.service.UsagePlanReadPlatformService;
import org.mifosplatform.collectionbatch.usageratequantitytier.service.UsageRateQuantityTierReadPlatformService;
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
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/rateplan")
@Component
@Scope("singleton")
public class RatePlanApiResource {
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	private final static String RESOURCE_TYPE = "RATEPLAN";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<RatePlanData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private PriceReadPlatformService priceReadPlatformService;
	final private TimeModelReadPlatformService timeModelReadPlatformService;
	final private RatableUsageMetricReadPlatformService ratableUsageMetricReadPlatformService;
	final private UsagePlanReadPlatformService usagePlanReadPlatformService;
	final private UnitOfmeasurementReadPlatformService unitOfmeasurementReadPlatformService;
	final private CurrencyReadPlatformService  currencyReadPlatformService;
	final private UsageRateQuantityTierReadPlatformService  usageRateQuantityTierReadPlatformService;
	final private MCodeReadPlatformService mCodeReadPlatformService;
	
	@Autowired
	public RatePlanApiResource(
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<RatePlanData> apiJsonSerializer, final PlatformSecurityContext context, 
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PriceReadPlatformService priceReadPlatformService,
			final TimeModelReadPlatformService timeModelReadPlatformService,
			final RatableUsageMetricReadPlatformService ratableUsageMetricReadPlatformService,
			final UsagePlanReadPlatformService usagePlanReadPlatformService ,
			final UnitOfmeasurementReadPlatformService unitOfmeasurementReadPlatformService,
			final CurrencyReadPlatformService currencyReadPlatformService,
			final UsageRateQuantityTierReadPlatformService usageRateQuantityTierReadPlatformService,
			final MCodeReadPlatformService codeReadPlatformService)
			
			 {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.priceReadPlatformService=priceReadPlatformService;
		this.timeModelReadPlatformService=timeModelReadPlatformService;
		this.ratableUsageMetricReadPlatformService=ratableUsageMetricReadPlatformService;
		this.usagePlanReadPlatformService=usagePlanReadPlatformService;
		this.unitOfmeasurementReadPlatformService=unitOfmeasurementReadPlatformService;
		this.currencyReadPlatformService=currencyReadPlatformService;
		this.usageRateQuantityTierReadPlatformService=usageRateQuantityTierReadPlatformService;
		this.mCodeReadPlatformService = codeReadPlatformService;
	
		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createUsageplan(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createUsageplan().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final RatePlanData ratePlanData = this.handleTemplateData(null);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, ratePlanData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	private RatePlanData handleTemplateData(RatePlanData ratePlanData) {
		if(ratePlanData == null){
			ratePlanData = new RatePlanData();
		}
		
		ratePlanData.setPlanpriceDatas(this.priceReadPlatformService.retrievePlanPricesForDropdown());
		ratePlanData.setRumDatas(this.ratableUsageMetricReadPlatformService.retrieveRumForDropdown());
		ratePlanData.setTimeModels(this.timeModelReadPlatformService.retrieveTimeModelForDropdown());
		ratePlanData.setUomDatas(this.unitOfmeasurementReadPlatformService.retrieveUomForDropdown());
		ratePlanData.setCurrencyDatas(this.currencyReadPlatformService.retrieveCurrency());
		ratePlanData.setTimeperiodDatas(this.timeModelReadPlatformService.retrieveTimePeriodForDropdown());
		ratePlanData.setglRealatedData(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.GL_ID));
		return ratePlanData;
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveAllRateplans(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchRatablePlans = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<RatePlanData> ratePlanDatas = usagePlanReadPlatformService.retriveAllRateplans(searchRatablePlans);
		return apiJsonSerializer.serialize(ratePlanDatas);
	}
	
	
	
}
