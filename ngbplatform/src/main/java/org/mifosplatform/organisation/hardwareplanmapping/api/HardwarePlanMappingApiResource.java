package org.mifosplatform.organisation.hardwareplanmapping.api;

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

import org.mifosplatform.billing.emun.data.EnumValuesData;
import org.mifosplatform.billing.emun.service.EnumReadplaformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.organisation.hardwareplanmapping.service.HardwarePlanReadPlatformService;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.provisioning.networkelement.service.NetworkElementReadPlatformService;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Mapping Hardware with Plan
 */
@Path("/hardwaremapping")
@Component
@Scope("singleton")
public class HardwarePlanMappingApiResource {

	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "planCode", "itemClass","provisioningId"));
	private final String resourceNameForPermissions = "PLANMAPPING";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<HardwarePlanData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final HardwarePlanReadPlatformService HardwarePlanReadPlatformService;
	private final ProvisioningReadPlatformService provisioningReadPlatformService;
	private final EnumReadplaformService enumReadplaformService;
	private final NetworkElementReadPlatformService networkElementReadPlatformService;

	@Autowired
	public HardwarePlanMappingApiResource(
			final PlatformSecurityContext context,
			final DefaultToApiJsonSerializer<HardwarePlanData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final HardwarePlanReadPlatformService HardwarePlanReadPlatformService,
			final ProvisioningReadPlatformService provisioningReadPlatformService,
			final EnumReadplaformService enumReadplaformService,
			final NetworkElementReadPlatformService networkElementReadPlatformService) {
		
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.HardwarePlanReadPlatformService = HardwarePlanReadPlatformService;
		this.provisioningReadPlatformService = provisioningReadPlatformService;
		this.enumReadplaformService = enumReadplaformService;
		this.networkElementReadPlatformService = networkElementReadPlatformService;
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllPlans(@QueryParam("itemClass") final Long itemClass,
			@Context final UriInfo uriInfo) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final List<HardwarePlanData> hardwarePlanDatas = this.HardwarePlanReadPlatformService.retrieveAllHardwareMappings(itemClass);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, hardwarePlanDatas, RESPONSE_DATA_PARAMETERS);
	}

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlanTemplate(@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		
		final HardwarePlanData planData = this.handleTemplateData(null);
		

		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, planData, RESPONSE_DATA_PARAMETERS);

	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createPlan(final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createHardwarePlan().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	
	@GET
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlanDetails(@PathParam("id") final Long id,@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		HardwarePlanData singlePlandata = this.HardwarePlanReadPlatformService.retrieveSingleHardwareMapping(id);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if(settings.isTemplate() && singlePlandata!=null){
			singlePlandata = this.handleTemplateData(singlePlandata);
		}
		return this.toApiJsonSerializer.serialize(settings, singlePlandata, RESPONSE_DATA_PARAMETERS);
	}
	
	
	@PUT
	@Path("{planMapId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updatePlanMapping(@PathParam("planMapId") final Long planMapId, final String apiRequestBodyAsJson) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePlanMapping(planMapId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	
	private HardwarePlanData handleTemplateData(HardwarePlanData planData){
		if(planData == null){
			planData = new HardwarePlanData();
		}
		planData.setItemClassData(this.enumReadplaformService.getEnumValues("item_class"));
		//planData.setProvisioning(this.provisioningReadPlatformService.retrieveProvisioningCategory());
		planData.setProvisioning(this.networkElementReadPlatformService.retrieveNetworkElements());
		
		
		return planData;
	}

}