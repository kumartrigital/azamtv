/**
 * 
 */
package org.mifosplatform.cms.eventmaster.api;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.chargecode.data.ChargesData;
import org.mifosplatform.cms.eventmaster.data.EventDetailsData;
import org.mifosplatform.cms.eventmaster.data.EventMasterData;
import org.mifosplatform.cms.eventmaster.domain.EventMaster;
import org.mifosplatform.cms.eventmaster.service.EventMasterReadPlatformService;
import org.mifosplatform.cms.media.data.MediaAssetData;
import org.mifosplatform.cms.media.service.MediaAssetReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Class to Create, Update and Delete {@link EventMaster}
 * 
 * @author Pavani
 * @author Rakesh
 *
 */
@Path("/eventmaster")
@Component
@Scope("singleton")
public class EventMasterApiResource {
	
	private final Set<String> RESPONSE_PARAMETERS = new HashSet<String>(Arrays.asList("id", "eventName", "eventDescription", "status", 
			"eventStartDate", "eventEndDate", "chargeData", "eventValidity"));
	
	private final String resourceNameForPermissions = "EVENT";
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final DefaultToApiJsonSerializer<EventMasterData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PlatformSecurityContext context;
	private final EventMasterReadPlatformService eventMasterReadPlatformService;
	private final MediaAssetReadPlatformService assetReadPlatformService;
	private final ItemReadPlatformService itemReadPlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	/**
	 * @param commandSourceWritePlatformService
	 * @param toApiJsonSerializer
	 * @param context
	 * @param apiRequestParameterHelper
	 * @param assetReadPlatformService
	 * @param eventMasterReadPlatformService
	 */
	@Autowired
	public EventMasterApiResource(final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
								  final DefaultToApiJsonSerializer<EventMasterData> toApiJsonSerializer,
								  final PlatformSecurityContext context,
								  final ApiRequestParameterHelper apiRequestParameterHelper,
								  final MediaAssetReadPlatformService assetReadPlatformService,
								  final EventMasterReadPlatformService eventMasterReadPlatformService,
								  final ItemReadPlatformService itemReadPlatformService,
								  final MCodeReadPlatformService mCodeReadPlatformService) {
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper =  apiRequestParameterHelper;
		this.context = context;
		this.assetReadPlatformService = assetReadPlatformService;
		this.eventMasterReadPlatformService = eventMasterReadPlatformService;
		this.itemReadPlatformService = itemReadPlatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
	}
	
	/**
	 * Generic Method to get Template Related Data
	 * for {@link EventMaster}
	 * 
	 * for popUp data
	 * @param uriInfo
	 * @return
	 */
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveEventMasterTempleteData(@Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		responseParameters.addAll(RESPONSE_PARAMETERS);
		final EventMasterData templetData = handleEventMasterTemplateData(responseParameters);		
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	
		return this.toApiJsonSerializer.serialize(settings, templetData, RESPONSE_PARAMETERS);
	}
	public EventMasterData handleEventMasterTemplateData(final Set<String> responseParameters) {
	    final List<MediaAssetData> mediaData   = this.assetReadPlatformService.retrieveAllAssetdata();
		final List<EnumOptionData> statusData = this.eventMasterReadPlatformService.retrieveNewStatus();
		final List<ChargesData> chargeDatas = this.itemReadPlatformService.retrieveChargeCode();
		final Collection<MCodeData> eventCategeorydata = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_EVENT_CATEGORY);
		final EventMasterData singleEvent  = new EventMasterData(mediaData, statusData, null, chargeDatas, eventCategeorydata);
		
		return singleEvent;	
	}
	
	
	/**
	 * Generic Method for Retrieving single {@link EventMaster}
	 * 
	 * @param eventId
	 * @param uriInfo
	 * @return
	 */
	@GET
	@Path("{eventId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveSingleEventMaster(@PathParam("eventId")final Integer eventId, @Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(uriInfo.getQueryParameters());
		responseParameters.addAll(RESPONSE_PARAMETERS);
		final List<MediaAssetData> mediaData   = this.assetReadPlatformService.retrieveAllAssetdata();
		final List<EnumOptionData> statusData = this.eventMasterReadPlatformService.retrieveNewStatus();
		final List<EventDetailsData> eventdetails = this.eventMasterReadPlatformService.retrieveEventDetailsData(eventId);
		final List<ChargesData> chargeDatas = this.itemReadPlatformService.retrieveChargeCode();
		final Collection<MCodeData> eventCategeorydata = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_EVENT_CATEGORY);
		final EventMasterData event = this.eventMasterReadPlatformService.retrieveEventMasterDetails(eventId);
		
		int mediaDataSize = mediaData.size();
		final int eventdetailsSize = eventdetails.size();
		for(int i = 0; i < eventdetailsSize; i++) {
			Long selected = eventdetails.get(i).getMediaId();
			for(int j = 0; j < mediaDataSize; j++) {
				Long available = mediaData.get(j).getMediaId();
				if(selected == available) {
					mediaData.remove(j);
					mediaDataSize--;
				}
			}
		}
		event.setMediaAsset(mediaData);
		event.setStatusData(statusData);
		event.setSelectedMedia(eventdetails);
		event.setChargeData(chargeDatas);
		event.setEventCategeorydata(eventCategeorydata);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	
		return this.toApiJsonSerializer.serialize(settings, event, RESPONSE_PARAMETERS);
	}
	
	/**
	 * Method to retrieve {@link EventMaster}
	 * 
	 * @param uriInfo
	 * @return
	 */
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveAllEventMasterData(@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final List<EventMasterData> eventMasterDatas = this.eventMasterReadPlatformService.retrieveEventMasterData();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, eventMasterDatas, RESPONSE_PARAMETERS);
	}
	
	/**
	 * Generic Method for Posting and creating new {@link EventMaster}
	 * 
	 * @param jsonBodyRequest
	 * @return
	 */
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createEventMaster(final String jsonRequestBody) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createEventMaster().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	/**
	 * Generic Method to Update single {@link EventMaster}
	 * 
	 * @param eventId
	 * @param jsonRequestBody
	 * @return
	 */
	@PUT
	@Path("{eventId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String updateEventMaster(@PathParam("eventId")final Long eventId, final String jsonRequestBody) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateEventMaster(eventId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	/**
	 * Generic Method to delete single {@link EventMaster}
	 * 
	 * @param eventId
	 * @return
	 */
	@DELETE
	@Path("{eventId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String deleteEventMaster(@PathParam("eventId") final Long eventId) {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteEventMaster(eventId).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
}