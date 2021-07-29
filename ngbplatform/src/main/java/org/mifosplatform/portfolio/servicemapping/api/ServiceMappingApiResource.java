package org.mifosplatform.portfolio.servicemapping.api;

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
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.finance.payments.service.PaymentReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.servicemapping.data.ServiceCodeData;
import org.mifosplatform.portfolio.servicemapping.data.ServiceMappingData;
import org.mifosplatform.portfolio.servicemapping.service.ServiceMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/servicemapping")
@Component
@Scope("singleton")
public class ServiceMappingApiResource {

	private final Set<String> RESPONSE_PARAMETERS = new HashSet<String>(
			Arrays.asList("id", "serviceCode", "serviceId","serviceIdentification", "status", "image"));
	
	private final String resourceNameForPermissions = "SERVICEMAPPING";
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final DefaultToApiJsonSerializer<ServiceMappingData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PlatformSecurityContext context;
	private final ServiceMappingReadPlatformService serviceMappingReadPlatformService;
	private final PlanReadPlatformService planReadPlatformService;
	private final PaymentReadPlatformService paymodeReadPlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;

	@Autowired
	public ServiceMappingApiResource(final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
			final DefaultToApiJsonSerializer<ServiceMappingData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
			final PlatformSecurityContext context,final ServiceMappingReadPlatformService serviceMappingReadPlatformService,
			final PlanReadPlatformService planReadPlatformService,final PaymentReadPlatformService paymodeReadPlatformService,
			final MCodeReadPlatformService mCodeReadPlatformService) {

		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.planReadPlatformService = planReadPlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.mCodeReadPlatformService=mCodeReadPlatformService;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
		this.serviceMappingReadPlatformService = serviceMappingReadPlatformService;
		this.paymodeReadPlatformService = paymodeReadPlatformService;

	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getServiceMapping(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,
			@QueryParam("offset") final Integer offset) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		//final List<ServiceMappingData> serviceMapping = this.serviceMappingReadPlatformService.getServiceMapping();
		final SearchSqlQuery searchCodes =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<ServiceMappingData> serviceMapping = this.serviceMappingReadPlatformService.getServiceMapping(searchCodes);
		return this.toApiJsonSerializer.serialize(serviceMapping);
	}

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getTemplateRelatedData(@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);

		final List<ServiceCodeData> serviceCodeData = this.serviceMappingReadPlatformService.getServiceCode();

		final List<EnumOptionData> status = this.planReadPlatformService.retrieveNewStatus();
		final Collection<McodeData> categories = this.paymodeReadPlatformService.retrievemCodeDetails("Service Category");
		final Collection<McodeData> subCategories = this.paymodeReadPlatformService.retrievemCodeDetails("Asset language");
		final Collection<MCodeData> provisionSysData = this.mCodeReadPlatformService.getCodeValue("Provisioning");
		final ServiceMappingData serviceMappingData = new ServiceMappingData(null,serviceCodeData, status, null, categories, subCategories,provisionSysData);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, serviceMappingData, RESPONSE_PARAMETERS);
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String addServiceMapping(@Context final UriInfo uriInfo, String jsonRequestBody) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createServiceMapping().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@GET
	@Path("{serviceMappingId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getServiceMappingForEdit(
			@PathParam("serviceMappingId") final Long serviceMappingId,
			@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final ServiceMappingData serviceMappingData = serviceMappingReadPlatformService.getServiceMapping(serviceMappingId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if(settings.isTemplate()){
		final List<EnumOptionData> status = this.planReadPlatformService.retrieveNewStatus();
		final Collection<McodeData> categories = this.paymodeReadPlatformService.retrievemCodeDetails("Service Category");
		final Collection<McodeData> subCategories = this.paymodeReadPlatformService.retrievemCodeDetails("Asset language");
		final Collection<MCodeData> provisionSysData = this.mCodeReadPlatformService.getCodeValue("Provisioning");
		serviceMappingData.setServiceCodeData(this.serviceMappingReadPlatformService.getServiceCode());
		
		serviceMappingData.setStatusData(status);
		serviceMappingData.setCategories(categories);
		serviceMappingData.setSubCategories(subCategories);
		serviceMappingData.setProvisionSysData(provisionSysData);
		}
		return this.toApiJsonSerializer.serialize(settings, serviceMappingData, RESPONSE_PARAMETERS);
	}

	@PUT
	@Path("{serviceMapId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateServiceMapping(
			@PathParam("serviceMapId") final Long serviceMapId,
			final String apiRequestBodyAsJson) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateServiceMapping(serviceMapId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	
}
