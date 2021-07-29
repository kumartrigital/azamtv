package org.mifosplatform.crm.ticketmaster.ticketmapping.api;

import java.util.ArrayList;
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
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.crm.ticketmaster.ticketmapping.service.TicketMappingReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.organisation.usercataloge.data.UserCatalogeData;
import org.mifosplatform.useradministration.data.AppUserData;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/ticketmapping")
@Component
@Scope("singleton")
public class TicketMappingApiResource {
	
	
	private final static String RESOURCE_TYPE = "TICKETMAPPING";
	private static final Set<String> RESPONSE_DATA_PARAMETERS = null;
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<TicketTeamMappingData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private TicketMappingReadPlatformService ticketmappingReadPlatformService;
	private final AppUserReadPlatformService appUserReadPlatformService;
	
	
	
	
	@Autowired
	public TicketMappingApiResource(
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<TicketTeamMappingData> apiJsonSerializer, final PlatformSecurityContext context, 
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final TicketMappingReadPlatformService ticketmappingReadPlatformService,
			final AppUserReadPlatformService appUserReadPlatformService) {
			
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.ticketmappingReadPlatformService = ticketmappingReadPlatformService;
		this.appUserReadPlatformService = appUserReadPlatformService;
		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String ticketmapping(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createTicketMapping().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final TicketTeamMappingData ticketmappingData = this.handleTemplateData(null);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, ticketmappingData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	
	@PUT
	@Path("{ticketmappingId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateTicketmapping(@PathParam("ticketmappingId") final Long ticketmappingId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateTicketmapping(ticketmappingId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@DELETE
	@Path("{ticketmappingId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteTicketmapping(@PathParam("ticketmappingId") final Long ticketmappingId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteTicketmapping(ticketmappingId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String TicketmappingDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchTicketMapping = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<TicketTeamMappingData> ticketteammappingDatas = ticketmappingReadPlatformService.retrieveTicketMapping(searchTicketMapping);
		return apiJsonSerializer.serialize(ticketteammappingDatas);
	}

	@GET
	@Path("{ticketmappingId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveTicketmappingDetails(@Context final UriInfo uriInfo ,@PathParam("ticketmappingId") final Long ticketmappingId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		TicketTeamMappingData ticketTeamMappingData=new TicketTeamMappingData();
		//List<TicketTeamMappingData> ticketTeamMappingDataList = this.ticketmappingReadPlatformService.retrieveTicketMapping(ticketmappingId);
		//for(TicketTeamMappingData ticketTeamMappingDataObject : ticketTeamMappingDataList ) {
			ticketTeamMappingData.setTeamId(ticketmappingId);
			//ticketTeamMappingData.setTeamCode(ticketTeamMappingData.getTeamCode());
			//break;
		//}
		ticketTeamMappingData=this.handleTemplateData(ticketTeamMappingData);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, ticketTeamMappingData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	
	
	/*private TicketTeamMappingData handleTemplateData(TicketTeamMappingData ticketmappingData) {
		if(ticketmappingData == null){
			ticketmappingData = new TicketTeamMappingData();
		}
		ticketmappingData.setTicketTeam(this.ticketmappingReadPlatformService.retrieveTicketTeamForDropdown());
		ticketmappingData.setAppUserDatas(this.appUserReadPlatformService.retrieveAppUserDataForDropdown());
		return ticketmappingData;
	}*/
	
	
	private TicketTeamMappingData handleTemplateData(TicketTeamMappingData ticketmappingData){
		if(ticketmappingData == null){
			ticketmappingData = new TicketTeamMappingData();
		}
		ticketmappingData.setTicketTeam(this.ticketmappingReadPlatformService.retrieveTicketTeamForDropdown());
		final List<AppUserData> availableUsers = this.ticketmappingReadPlatformService.retrieveAppUserDataForDropdown();
		List<TicketTeamMappingData> selectedUsers = new ArrayList<>();
		//Long userId=this.context.authenticatedUser().getId();
		if(availableUsers != null){
			selectedUsers = this.ticketmappingReadPlatformService.retrieveSelectedUsers(ticketmappingData.getTeamId());
			//selectedSalesCataloges = this.userCatalogeReadPlatformService.retrieveSelectedSalesCatalogeForPlanCategory(userId);

			int size = availableUsers.size();
			final int selectedsize = selectedUsers.size();
				for (int i = 0; i < selectedsize; i++)
	     			{
					final Long selected = selectedUsers.get(i).getUserId();
					for (int j = 0; j < size; j++) {
						final Long avialble = availableUsers.get(j).getId();
						if (selected.equals(avialble)) {
							availableUsers.remove(j);
							size--;
						}
					}
				}
	     }
		ticketmappingData.setSelectedUsersDatas(selectedUsers);
		ticketmappingData.setAvailableUsersDatas(availableUsers);
		return ticketmappingData;
	}
	
	
}


