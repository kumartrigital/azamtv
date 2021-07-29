package org.mifosplatform.freeradius.radius.api;

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

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.freeradius.radius.data.RadiusServiceData;
import org.mifosplatform.freeradius.radius.service.RadiusReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author hugo
 * this api class used to create,update and delete diff radius details 
 */
@Path("/freeradius")
@Component
@Scope("singleton")
public class RadiusAPiResource {
	
	private static final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "planCode", "plan_description", "startDate","isHwReq",
            "endDate", "status", "service_code", "service_description","Period", "charge_code", "charge_description","servicedata","contractPeriod","provisionSystem",
            "service_type", "charge_type", "allowedtypes","selectedservice","bill_rule","billiingcycle","servicedata","services","statusname","planstatus","volumeTypes"));

	private final String resourceNameForPermissions = "RADIUS";
	private final PlatformSecurityContext context;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final RadiusReadPlatformService radiusReadPlatformService;
    private final DefaultToApiJsonSerializer<RadiusServiceData> toApiJsonSerializer;
    private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	
	@Autowired
	public RadiusAPiResource(final PlatformSecurityContext context,final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,final RadiusReadPlatformService radiusReadPlatformService,
			final DefaultToApiJsonSerializer<RadiusServiceData> toApiJsonSerializer) {
		
		this.context = context;
		this.radiusReadPlatformService = radiusReadPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
	}

	/**
	 * @param uriInfo
	 * @return retrieved all nas details
	 */
	@GET
	@Path("nas")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllNasDetails(@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final String nasData = this.radiusReadPlatformService.retrieveAllNasDetails();
		return nasData;
	}
	
	
	@POST
	@Path("nas")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createNas(final String apiRequestBodyAsJson) {

		context.authenticatedUser();
		final String nasData = this.radiusReadPlatformService.createNas(apiRequestBodyAsJson);
		return nasData;
	}
	
	@GET
	@Path("nas/{nasId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveNasDetail(@PathParam("nasId") final Long nasId) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final String radServiceData = this.radiusReadPlatformService.retrieveNasDetail(nasId);
		return radServiceData;
	}
	
	
	@DELETE
	@Path("nas/{nasId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteNasDetail(@PathParam("nasId") final Long nasId) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final String radServiceData = this.radiusReadPlatformService.deleteNasDetail(nasId);
		return radServiceData;
	}
	
	@GET
	@Path("radservice")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllRadserviceDetails(@Context final UriInfo uriInfo,@QueryParam("attribute") final String attribute) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final String radServiceData = this.radiusReadPlatformService.retrieveAllRadServiceDetails(attribute);
		return radServiceData;
	}
	
	@GET
	@Path("radservice/{radServiceId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveRadserviceDetail(@PathParam("radServiceId") final Long radServiceId,@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		final RadiusServiceData radServiceData = this.radiusReadPlatformService.retrieveRadServiceDetail(radServiceId);
		if(settings.isTemplate()){
		final List<RadiusServiceData> radServiceTemplateData = this.radiusReadPlatformService.retrieveRadServiceTemplateData(radServiceId);
		radServiceData.setRadServiceTemplateData(radServiceTemplateData);
		}
		return this.toApiJsonSerializer.serialize(settings,radServiceData,RESPONSE_DATA_PARAMETERS);
	}
	
	
	@POST
	@Path("radservice")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createRadService(final String apiRequestBodyAsJson) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final CommandProcessingResult result = this.radiusReadPlatformService.createRadService(apiRequestBodyAsJson);
		  return this.toApiJsonSerializer.serialize(result);
	}

	
	@PUT
	@Path("radservice/{radServiceId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String editRadService(@PathParam("radServiceId") final Long radServiceId,final String apiRequestBodyAsJson) {

		context.authenticatedUser();
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateRadService(radServiceId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	
	@DELETE
	@Path("radservice/{radServiceId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteRadService(@PathParam("radServiceId") final Long radServiceId) {

		context.authenticatedUser();
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteRadService(radServiceId).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	
	}
	
	@GET
	@Path("raduser2/template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveRadserviceTemplateData(@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		final List<RadiusServiceData> radServiceTemplateData = this.radiusReadPlatformService.retrieveRadServiceTemplateData(Long.valueOf(-1));
		/*RadiusServiceData aa = new RadiusServiceData();
		aa.setRadServiceTemplateData(temp);
		final List<RadiusServiceData> radServiceTemplateData = new ArrayList<RadiusServiceData>();
		radServiceTemplateData.add(aa);*/
		return this.toApiJsonSerializer.serialize(settings, radServiceTemplateData, RESPONSE_DATA_PARAMETERS);
		//return radServiceTemplateData;
	}

	@GET
	@Path("onlineusers")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllOnlineUsers(@Context final UriInfo uriInfo,@QueryParam("custattr") final String custattr,
			@QueryParam("limit") final String limit,@QueryParam("offset") final String offset,
			@QueryParam("checkOnline") final String checkOnline, @QueryParam("userName") final String userName,
			@QueryParam("partner") final Long partner) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final String radServiceData = this.radiusReadPlatformService.retrieveAllOnlineUsers(custattr, limit, offset, checkOnline, userName, partner);
		return radServiceData;
	}
	
	@POST
	@Path("nas/reload")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createReloadNases(final String apiRequestBodyAsJson) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final String nasData = this.radiusReadPlatformService.createReloadNases(apiRequestBodyAsJson);
		return nasData;
	}

}
