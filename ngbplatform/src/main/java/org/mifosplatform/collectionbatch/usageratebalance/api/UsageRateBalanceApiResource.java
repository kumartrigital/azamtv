package org.mifosplatform.collectionbatch.usageratebalance.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.collectionbatch.ratableusagemetric.service.RatableUsageMetricReadPlatformService;
import org.mifosplatform.collectionbatch.timemodel.service.TimeModelReadPlatformService;
import org.mifosplatform.collectionbatch.unitofmeasurement.service.UnitOfmeasurementReadPlatformService;
import org.mifosplatform.collectionbatch.usageratebalance.data.UsageBalanceData;
import org.mifosplatform.collectionbatch.usageratequantitytier.service.UsageRateQuantityTierReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/usageratebalance")
@Component
@Scope("singleton")
public class UsageRateBalanceApiResource {
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	private final static String RESOURCE_TYPE = "USAGERATEBALANCE";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<UsageBalanceData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private RatableUsageMetricReadPlatformService ratableUsageMetricReadPlatformService;
	final private UnitOfmeasurementReadPlatformService unitOfmeasurementReadPlatformService;
	final private CurrencyReadPlatformService currencyReadPlatformService;
	final private TimeModelReadPlatformService timeModelReadPlatformService;
	final private UsageRateQuantityTierReadPlatformService usageRateQuantityTierReadPlatformService;
	 private final MCodeReadPlatformService mCodeReadPlatformService;
	
	
	

	
	@Autowired
	public UsageRateBalanceApiResource(
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<UsageBalanceData> apiJsonSerializer, final PlatformSecurityContext context, 
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final AppUserReadPlatformService appUserReadPlatformService,
			final RatableUsageMetricReadPlatformService ratableUsageMetricReadPlatformService,
			final UnitOfmeasurementReadPlatformService unitOfmeasurementReadPlatformService,
			final CurrencyReadPlatformService currencyReadPlatformService,
			final TimeModelReadPlatformService timeModelReadPlatformService,
			final UsageRateQuantityTierReadPlatformService usageRateQuantityTierReadPlatformService,
			final MCodeReadPlatformService codeReadPlatformService
			) {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.ratableUsageMetricReadPlatformService=ratableUsageMetricReadPlatformService;
		this.unitOfmeasurementReadPlatformService=unitOfmeasurementReadPlatformService;
		this.currencyReadPlatformService=currencyReadPlatformService;
		this.timeModelReadPlatformService=timeModelReadPlatformService;
		this.usageRateQuantityTierReadPlatformService=usageRateQuantityTierReadPlatformService;
		this.mCodeReadPlatformService = codeReadPlatformService;
	
		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createUsagebalance(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createUsagebalance().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	

	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final UsageBalanceData usageBalanceData = this.handleTemplateData(null);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, usageBalanceData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	private UsageBalanceData handleTemplateData(UsageBalanceData usageBalanceData) {
		if(usageBalanceData == null){
			usageBalanceData = new UsageBalanceData();
		}
		
		usageBalanceData.setUomDatas(this.unitOfmeasurementReadPlatformService.retrieveUomForDropdown());
		usageBalanceData.setRumDatas(this.ratableUsageMetricReadPlatformService.retrieveRumForDropdown());
		usageBalanceData.setCurrencyDatas(this.currencyReadPlatformService.retrieveCurrency());
		usageBalanceData.setTimeperiodDatas(this.timeModelReadPlatformService.retrieveTimePeriodForDropdown());
		usageBalanceData.setTierDatas(this.usageRateQuantityTierReadPlatformService.retrieveTierForDropdown());
		usageBalanceData.setglRealatedData(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.GL_ID));
		usageBalanceData.setUsageData(this.usageRateQuantityTierReadPlatformService.retrieveUsageRateQuantityTierForDropdown());
		return usageBalanceData;
	}
	
	
	
	
}
