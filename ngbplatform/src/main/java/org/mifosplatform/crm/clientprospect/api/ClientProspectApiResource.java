package org.mifosplatform.crm.clientprospect.api;

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

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.data.ClientProspectData;
import org.mifosplatform.crm.clientprospect.data.ProspectDetailAssignedToData;
import org.mifosplatform.crm.clientprospect.data.ProspectDetailCallStatus;
import org.mifosplatform.crm.clientprospect.data.ProspectDetailData;
import org.mifosplatform.crm.clientprospect.data.ProspectPlanCodeData;
import org.mifosplatform.crm.clientprospect.data.ProspectStatusRemarkData;
import org.mifosplatform.crm.clientprospect.service.ClientProspectReadPlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/prospects")
@Component
@Scope("singleton")
public class ClientProspectApiResource {

	private final String RESOURCETYPE = "PROSPECT";

	private final PlatformSecurityContext context;
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final ClientProspectReadPlatformService clientProspectReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final MCodeReadPlatformService codeReadPlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	private final ToApiJsonSerializer<ClientProspectData> apiJsonSerializerString;

	private final Set<String> PROSPECT_RESPONSE_DATA_PARAMETER = new HashSet<String>(Arrays.asList("id", "type", "firstName", "middleName",
			"lastName","homePhoneNumber", "workPhoneNumber", "mobileNumber","email", "address", "area", "district", "city", "region",
			"zipCode", "sourceOfPublicity", "plan","preferredCallingTime", "note", "status", "callStatus","assignedTo", "notes"));
	
	private final Set<String> PROSPECTDETAIL_RESPONSE_DATA_PARAMETER = new HashSet<String>(
			Arrays.asList("callStatus", "preferredCallingTime", "assignedTo", "notes", "locale", "prospectId"));
	
	private final Set<String> PROSPECTDETAILREMARK_RESPONSE_DATA_PARAMETER = new HashSet<String>(
			Arrays.asList("statusRemarkId", "statusRemark"));

	private final ToApiJsonSerializer<ClientProspectData> apiJsonSerializer;
	private final ToApiJsonSerializer<ProspectDetailData> apiJsonSerializerForProspectDetail;
	private final ToApiJsonSerializer<ProspectStatusRemarkData> apiJsonSerializerForStatusRemark;

	@Autowired
	public ClientProspectApiResource(
			final PlatformSecurityContext context,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
			final ToApiJsonSerializer<ClientProspectData> apiJsonSerializer,
			final ClientProspectReadPlatformService clientProspectReadPlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final MCodeReadPlatformService codeReadPlatformService,
			final ToApiJsonSerializer<ProspectDetailData> apiJsonSerializerForProspectDetail,
			final ToApiJsonSerializer<ClientProspectData> apiJsonSerializerString,
			final ToApiJsonSerializer<ProspectStatusRemarkData> apiJsonSerializerForStatusRemark,
			final AddressReadPlatformService addressReadPlatformService) {
		this.context = context;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
		this.clientProspectReadPlatformService = clientProspectReadPlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.codeReadPlatformService = codeReadPlatformService;
		this.apiJsonSerializerForProspectDetail = apiJsonSerializerForProspectDetail;
		this.apiJsonSerializerForStatusRemark = apiJsonSerializerForStatusRemark;
		this.addressReadPlatformService = addressReadPlatformService;
		this.apiJsonSerializerString = apiJsonSerializerString;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String retriveProspects(@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(RESOURCETYPE);
		final Collection<ClientProspectData> clientProspectData = this.clientProspectReadPlatformService.retriveClientProspect();
		// Collection<MCodeData> sourceOfPublicityData = codeReadPlatformService.getCodeValue("Source Type");
		// clientProspectData.setSourceOfPublicityData(sourceOfPublicityData);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, clientProspectData, PROSPECT_RESPONSE_DATA_PARAMETER);
	}

	/**
	 * during Leads click
	 * @param uriInfo
	 * @param sqlSearch
	 * @param limit
	 * @param offset
	 * @return
	 */
	@GET
	@Path("allprospects")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String retriveProspectsForNewClient(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch, 
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset) {

		AppUser user = context.authenticatedUser();
		user.validateHasReadPermission(RESOURCETYPE);
		final SearchSqlQuery clientProspect = SearchSqlQuery.forSearch(sqlSearch, offset, limit);
		final Page<ClientProspectData> clientProspectData = this.clientProspectReadPlatformService.retriveClientProspect(clientProspect,user.getId());
		return this.apiJsonSerializer.serialize(clientProspectData);
	}
	
	/**
	 * During click on New Prospect/Prospect Creation
	 * @param uriInfo
	 * @return
	 */
	@GET
	@Path("template")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String retriveProspectsTemplate(@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(RESOURCETYPE);
		
		final Collection<MCodeData> sourceOfPublicityData = codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_SOURCE_TYPE);
		final ClientProspectData clientProspectData = new ClientProspectData();// .clientProspectReadPlatformService.retriveClientProspectTemplate();
		final Collection<ProspectPlanCodeData> planData = clientProspectReadPlatformService.retrivePlans();
		clientProspectData.setPlanData(planData);
		clientProspectData.setSourceOfPublicityData(sourceOfPublicityData);
		clientProspectData.setStatus("New");

		//final List<String> countryData = this.addressReadPlatformService.retrieveCountryDetails();
		//final List<String> statesData = this.addressReadPlatformService.retrieveStateDetails();
		final List<String> citiesData = this.addressReadPlatformService.retrieveCityDetails();
		//clientProspectData.setCountryData(countryData);
		//clientProspectData.setStateData(statesData);
		clientProspectData.setCityData(citiesData);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, clientProspectData, PROSPECT_RESPONSE_DATA_PARAMETER);
	}
	
	/**
	 * During Prospect Creation
	 * @param jsonRequestBody
	 * @return
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String createProspects(final String jsonRequestBody) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createProspect().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return apiJsonSerializer.serialize(result);
	}
	
	/**
	 * calling for specific Prospect
	 * @param uriInfo
	 * @param id
	 * @return
	 */
	@GET
	@Path("{prospectId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String getSingleClient(@Context final UriInfo uriInfo,
			@PathParam("prospectId") final Long prospectId) {

		AppUser user = context.authenticatedUser();
		user.validateHasReadPermission(RESOURCETYPE);
		final ClientProspectData clientData = clientProspectReadPlatformService.retriveSingleClient(prospectId, user.getId());
		final Collection<MCodeData> sourceOfPublicityData = codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_SOURCE_TYPE);
		final Collection<ProspectPlanCodeData> planData = clientProspectReadPlatformService.retrivePlans();
		clientData.setPlanData(planData);
		clientData.setSourceOfPublicityData(sourceOfPublicityData);
		
		final List<String> countryData = this.addressReadPlatformService.retrieveCountryDetails();
		final List<String> statesData = this.addressReadPlatformService.retrieveStateDetails();
		final List<String> citiesData = this.addressReadPlatformService.retrieveCityDetails();
		clientData.setCountryData(countryData);
		clientData.setStateData(statesData);
		clientData.setCityData(citiesData);

		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializerString.serialize(settings, clientData, PROSPECT_RESPONSE_DATA_PARAMETER);
	}
	
	/**
	 * During Update Prospect
	 * @param prospectId
	 * @param jsonRequestBody
	 * @return
	 */
	@PUT
	@Path("{prospectId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String updateProspectDetails(@PathParam("prospectId") final Long prospectId,
			final String jsonRequestBody) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateProspect(prospectId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = commandSourceWritePlatformService.logCommandSource(commandRequest);
		return apiJsonSerializer.serialize(result);
	}
	
	/**
	 * During cancel/delete Prospect
	 * @param uriInfo
	 * @param prospectId
	 * @return
	 */
	@GET
	@Path("cancel/{prospectId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveDataForCancle(@Context final UriInfo uriInfo,
			@PathParam("prospectId") final Long prospectId) {
		
		context.authenticatedUser().validateHasReadPermission(RESOURCETYPE);
		final Collection<MCodeData> mCodeData = codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_STATUS_REMARK);
		final List<ProspectStatusRemarkData> statusRemarkData = new ArrayList<ProspectStatusRemarkData>();
		
		for (MCodeData codeData : mCodeData) {
			statusRemarkData.add(new ProspectStatusRemarkData(codeData.getId(), codeData.getmCodeValue()));
		}
		
		final ProspectStatusRemarkData data = new ProspectStatusRemarkData();
		data.setStatusRemarkData(statusRemarkData);
		
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializerForStatusRemark.serialize(settings, data, PROSPECTDETAILREMARK_RESPONSE_DATA_PARAMETER);

	}
	
	/**
	 * During Deleteion of a prospect
	 * @param prospectId
	 * @param jsonRequestBody
	 * @return
	 */
	@DELETE
	@Path("{prospectId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String deleteProspect(@PathParam("prospectId") final Long prospectId,
			@QueryParam("statusRemark") final String statusRemark,  String jsonRequestBody) {

		jsonRequestBody="{statusRemark:\""+statusRemark+"\"}";
		System.out.println(jsonRequestBody);
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteProspect(prospectId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = commandSourceWritePlatformService.logCommandSource(commandRequest);
		return apiJsonSerializer.serialize(result);
	}

	/**
	 * During Followup
	 * @param uriInfo
	 * @param prospectId
	 * @return
	 */
	@GET
	@Path("followup/{prospectId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String retriveProspects(@Context final UriInfo uriInfo, @PathParam("prospectId") final Long prospectId) {
		
		AppUser user = context.authenticatedUser();
		user.validateHasReadPermission(RESOURCETYPE);
		
		final ProspectDetailData clientProspectData = this.clientProspectReadPlatformService.retriveClientProspect(prospectId);
		final Collection<MCodeData> mCodeData = codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_CALL_STATUS);
		final List<ProspectDetailCallStatus> callStatusData = new ArrayList<ProspectDetailCallStatus>();
		final List<ProspectDetailAssignedToData> assignedToData = clientProspectReadPlatformService.retrieveUsers();
		
		for (MCodeData code : mCodeData) {
			final ProspectDetailCallStatus p = new ProspectDetailCallStatus(code.getId(), code.getmCodeValue());
			callStatusData.add(p);
		}
		
		clientProspectData.setCallStatusData(callStatusData);
		clientProspectData.setAssignedToData(assignedToData);
		
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializerForProspectDetail.serialize(settings, clientProspectData, PROSPECTDETAIL_RESPONSE_DATA_PARAMETER);
	}
	
	/**
	 * during Followup saving
	 * @param prospectId
	 * @param jasonRequestBody
	 * @return
	 */
	@PUT
	@Path("followup/{prospectId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String followUpProspect(@PathParam("prospectId") final Long prospectId,
			final String jasonRequestBody) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().followUpProspect(prospectId).withJson(jasonRequestBody).build();
		final CommandProcessingResult result = commandSourceWritePlatformService.logCommandSource(commandRequest);
		return apiJsonSerializer.serialize(result);
	}
	
	/** 
	 * Convert to Client
	 * @param prospectId
	 * @param jsonRequestBody
	 * @return
	 */
	@POST
	@Path("converttoclient/{prospectId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String convertProspecttoClientCreation(@PathParam("prospectId") final Long prospectId,
			final String jsonRequestBody) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().convertProspectToClient(prospectId).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return apiJsonSerializer.serialize(result);
	}

	/**
	 * calling on specific prospect 
	 * @param uriInfo
	 * @param prospectdetailid
	 * @return
	 */
	@GET
	@Path("{prospectdetailid}/history")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String history(@Context final UriInfo uriInfo,
			@PathParam("prospectdetailid") final Long prospectdetailid) {
		
		AppUser user = context.authenticatedUser();
		user.validateHasReadPermission(RESOURCETYPE);
		final List<ProspectDetailData> prospectDetailData = this.clientProspectReadPlatformService.retriveProspectDetailHistory(prospectdetailid, user.getId());
		final ProspectDetailData data = new ProspectDetailData(prospectDetailData);
		
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializerForProspectDetail.serialize(settings, data, PROSPECT_RESPONSE_DATA_PARAMETER);
	}

	@PUT
	@Path("elevate/{prospectId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String elevateProspect(@PathParam("prospectId") final Long prospectId,final String jasonRequestBody) {

		final CommandWrapper command = new CommandWrapperBuilder().elevateProspect(prospectId).withJson(jasonRequestBody).build();
		final CommandProcessingResult result = commandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	
	
	
}
