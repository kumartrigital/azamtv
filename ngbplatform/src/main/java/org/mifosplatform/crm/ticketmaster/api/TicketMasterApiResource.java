package org.mifosplatform.crm.ticketmaster.api;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.data.ClientTicketData;
import org.mifosplatform.crm.ticketmaster.data.SubCategoryData;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.data.UsersData;
import org.mifosplatform.crm.ticketmaster.domain.TicketDetail;
import org.mifosplatform.crm.ticketmaster.domain.TicketDetailsRepository;
import org.mifosplatform.crm.ticketmaster.service.TicketMasterReadPlatformService;
import org.mifosplatform.crm.ticketmaster.service.TicketMasterWritePlatformService;
import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.crm.ticketmaster.ticketmapping.service.TicketMappingReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiConstants;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/tickets")
@Component
@Scope("singleton")
public class TicketMasterApiResource {
		
		private final String resourceNameForPermission = "TICKET";
		private final TicketMasterWritePlatformService ticketMasterWritePlatformService;
		private final TicketMasterReadPlatformService ticketMasterReadPlatformService ;
		private final DefaultToApiJsonSerializer<TicketMasterData> toApiJsonSerializer;
		private final DefaultToApiJsonSerializer<ClientTicketData> clientToApiJsonSerializer;
		private final DefaultToApiJsonSerializer<MCodeData> statusToApiJsonSerializer;
		private final DocumentWritePlatformService documentWritePlatformService;
		private final ApiRequestParameterHelper apiRequestParameterHelper;
		private final TicketDetailsRepository detailsRepository;
		private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
		private final PlatformSecurityContext context;
		final private MCodeReadPlatformService codeReadPlatformService;
		private final FromJsonHelper fromApiJsonHelper;
		private final TicketMappingReadPlatformService ticketmappingReadPlatformService;
		
		@Autowired
		public TicketMasterApiResource(final TicketMasterWritePlatformService ticketMasterWritePlatformService,final TicketMasterReadPlatformService ticketMasterReadPlatformService,
										final DefaultToApiJsonSerializer<TicketMasterData> toApiJsonSerializer, final DefaultToApiJsonSerializer<ClientTicketData> clientToApiJsonSerializer,
										final ApiRequestParameterHelper apiRequestParameterHelper, final TicketDetailsRepository detailsRepository,
										final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final PlatformSecurityContext context,
										final MCodeReadPlatformService codeReadPlatformService,
										final DocumentWritePlatformService documentWritePlatformService,
										final FromJsonHelper fromApiJsonHelper,
										final DefaultToApiJsonSerializer<MCodeData> statusToApiJsonSerializer,
										final TicketMappingReadPlatformService ticketmappingReadPlatformService)	{
			
			this.ticketMasterWritePlatformService = ticketMasterWritePlatformService;
			this.ticketMasterReadPlatformService = ticketMasterReadPlatformService;
		    this.toApiJsonSerializer = toApiJsonSerializer;
			this.clientToApiJsonSerializer = clientToApiJsonSerializer;
			this.apiRequestParameterHelper = apiRequestParameterHelper;
			this.detailsRepository = detailsRepository;
			this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
			this.context = context;
			this.codeReadPlatformService = codeReadPlatformService;
			this.documentWritePlatformService = documentWritePlatformService;
			this.fromApiJsonHelper = fromApiJsonHelper;
			this.statusToApiJsonSerializer = statusToApiJsonSerializer;
			this.ticketmappingReadPlatformService = ticketmappingReadPlatformService;

			
		}
		private final Set<String> RESPONSE_PARAMETERS = new HashSet<String>(Arrays.asList("id", "priorityType", "problemsDatas", "usersData", "status", "assignedTo", 
														"userName", "ticketDate", "lastComment", "masterData","comments"));   

		/**
		 * Method To Create TicketMaster 
		 * @param clientId
		 * @param jsonRequestBody
		 * @return String Json Result 
		 */
		@POST
	    @Path("{clientId}")
		@Consumes({ MediaType.MULTIPART_FORM_DATA })
		@Produces({ MediaType.APPLICATION_JSON })
		public String createTicketMaster(@PathParam("clientId") final Long clientId,
				@HeaderParam("Content-Length") final Long fileSize,@FormDataParam("file") final InputStream inputStream,
				@FormDataParam("file") final FormDataContentDisposition fileDetails,@FormDataParam("file") final FormDataBodyPart bodyPart,
				@FormDataParam("assignedTo") final int assignedTo,@FormDataParam("dateFormat") final String dateFormat,
				@FormDataParam("description") final String description,@FormDataParam("dueTime") final String dueTime,
				@FormDataParam("locale") final String locale,@FormDataParam("priority") final String priority,
				@FormDataParam("problemCode") final int problemCode,@FormDataParam("sourceOfTicket") final String sourceOfTicket,
				@FormDataParam("subCategory") final String subCategory,@FormDataParam("ticketDate") final String ticketDate,
				@FormDataParam("ticketTime") final String ticketTime,@FormDataParam("ticketURL") final String ticketURL) {
			
           try{
        	   
        	   FileUtils.validateFileSizeWithinPermissibleRange(fileSize, null, ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);
	        	
		       	 String fileUploadLocation = FileUtils.generateFileParentDirectory("clients",clientId);

		            /** Recursively create the directory if it does not exist **/
		            if (!new File(fileUploadLocation).isDirectory()) {
		                new File(fileUploadLocation).mkdirs();
		            }
		            String fileLocation = null;
		            if(fileDetails != null){
		             fileLocation = FileUtils.saveToFileSystem(inputStream, fileUploadLocation, fileDetails.getFileName());
		            }
		           
					JSONObject ticketJson = new JSONObject();
					ticketJson.put("assignedTo", assignedTo);
					ticketJson.put("dateFormat", dateFormat);
					ticketJson.put("description", description);
					ticketJson.put("dueTime", dueTime);
					ticketJson.put("locale", locale);
					ticketJson.put("priority", priority);
					ticketJson.put("problemCode", problemCode);
					ticketJson.put("sourceOfTicket", sourceOfTicket);
					ticketJson.put("ticketDate", ticketDate);
					ticketJson.put("ticketTime", ticketTime);
					ticketJson.put("ticketURL", ticketURL);
					ticketJson.put("fileLocation", fileLocation);
					ticketJson.put("subCategory", subCategory);
			
					CommandProcessingResult result = processCreateTicket(clientId, ticketJson.toString());
					return this.toApiJsonSerializer.serialize(result);
		           }catch(Exception e){
		        	   e.getStackTrace();
		        	   String result = null;
		        	   return result;
		           }
		}	
		
		
		@POST
	    @Path("ticketing/{clientId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String createTicket(@PathParam("clientId") final Long clientId,final String apiRequestBodyAsJson) {
			
			final CommandWrapper command = new CommandWrapperBuilder().createTicketMaster(clientId).withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(command);
			return toApiJsonSerializer.serialize(result);
			
           /*try{
					CommandProcessingResult result = processCreateTicket(clientId, apiRequestBodyAsJson);
					return this.toApiJsonSerializer.serialize(result);
		           }catch(Exception e){
		        	   e.getStackTrace();
		        	   String result = null;
		        	   return result;
		           }*/
		}
		
		
		public CommandProcessingResult processCreateTicket(Long clientId, String apiRequestData) {

			try {
				CommandProcessingResult result = null;
				Long userId = null;
				final SecurityContext context = SecurityContextHolder.getContext();
	        	if (context.getAuthentication() != null) {
	        		final AppUser appUser = this.context.authenticatedUser();
	        		userId = appUser.getId();
	        	}
	        	if(userId != null){	 
	        		final CommandWrapper commandRequest = new CommandWrapperBuilder().createTicketMaster(clientId).withJson(apiRequestData.toString()).build();
	        		result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        		return result;
	        	}else{
	        		final JsonElement parsedCommand = this.fromApiJsonHelper.parse(apiRequestData.toString());
	        		final JsonCommand command = JsonCommand.from(apiRequestData.toString(), parsedCommand, this.fromApiJsonHelper, "TICKET", clientId,
	        													null, null, clientId, null, null, null, null, null, null, null);
	        		result =this.ticketMasterWritePlatformService.createTicketMaster(command);
	        		return result;
	        	}
		            
			} catch (Exception e) {
				return null;
			}
			
		}
		
		/**
		 * Method to Close Ticket
		 * @param ticketId
		 * @param jsonRequestBody
		 * @return
		 */
		@PUT
		@Path("{ticketId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String closeTicket(@PathParam("ticketId") final Long ticketId, final String jsonRequestBody) {
			final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteTicketMaster(ticketId).withJson(jsonRequestBody).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		}
		
		
		/**
		 * Listing tickets
		 * */		
		@GET
		@Path("alltickets")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String assignedAllTickets(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch,
				@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset, @QueryParam("statusType") final String statusType,
				@QueryParam("fromDate") final String fromDate,
				@QueryParam("toDate") final String toDate,
				@QueryParam("type") final String type){
			
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
			final SearchSqlQuery searchTicketMaster = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		    final Page<ClientTicketData> data = this.ticketMasterReadPlatformService.retrieveAssignedTicketsForNewClient(searchTicketMaster,statusType,fromDate,toDate,type);
		    
	        return this.clientToApiJsonSerializer.serialize(data);
		}
		
		/**
		 * Method for Retrieving Ticket Details for template
		 * @param uriInfo
		 * @return
		 */
		@GET
		@Path("template")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveTicketMasterTemplateData(@Context final UriInfo uriInfo, @QueryParam("templateFor") final String templateFor) {
			
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			
			if(templateFor != null && "closeticket".equalsIgnoreCase(templateFor)){
				final Collection<MCodeData> closedStatusdata = this.codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_TICKET_STATUS, "2");
				return this.statusToApiJsonSerializer.serialize(settings, closedStatusdata, RESPONSE_PARAMETERS);
				
			}else{
				final Collection<MCodeData> sourceData = codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_TICKET_SOURCE);
				final TicketMasterData templateData = handleTicketTemplateData(sourceData);
				return this.toApiJsonSerializer.serialize(settings, templateData, RESPONSE_PARAMETERS);
			}
		}
		
		/**
		 * Method for retrieve Single Ticket for clientId 
		 * @param clientId
		 * @param uriInfo
		 * @return
		 */
		@GET
	    @Path("{clientId}")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveSingleClientTicketDetails(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {

			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
	        final List<TicketMasterData> data = this.ticketMasterReadPlatformService.retrieveClientTicketDetails(clientId);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_PARAMETERS);
	    }
		   
	    /**
	     * Method to get TicketDetails for update
	     * @param clientId
	     * @param ticketId
	     * @param uriInfo
	     * @return
	     */
	    @GET
	    @Path("{clientId}/update/{ticketId}")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveClientSingleTicketDetails(@PathParam("clientId")final Long clientId , @PathParam("ticketId") final Long ticketId,
	    												@Context final UriInfo uriInfo) {
	    	
	    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
	    	final TicketMasterData data = this.ticketMasterReadPlatformService.retrieveSingleTicketDetails(clientId, ticketId);
	        final Collection<MCodeData> statusdata = this.codeReadPlatformService.getCodeValue("Ticket Status", "1");
	        data.setStatusData(statusdata);
	        final List<UsersData>  userData = this.ticketMasterReadPlatformService.retrieveUsers();
	        data.setUsersData(userData);
	        final List<EnumOptionData> priorityData = this.ticketMasterReadPlatformService.retrievePriorityData();
			final Collection<MCodeData> problemsDatas = this.codeReadPlatformService.getCodeValue("Problem Code");
			final List<TicketTeamMappingData> ticketmappingData = this.ticketmappingReadPlatformService.retrieveTicketTeamForDropdown();
			data.setTicketTeamMappingData(ticketmappingData);
			data.setPriorityType(priorityData);
			data.setProblemsDatas(problemsDatas);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_PARAMETERS);
	    }
	   
		/**
		 * Method to retrieve Ticket History
		 * @param ticketId
		 * @param uriInfo
		 * @return
		 */
		@GET
	    @Path("{ticketId}/history")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String ticketHistory(@PathParam("ticketId") final Long ticketId, @Context final UriInfo uriInfo) {
			
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
			final String description = this.ticketMasterWritePlatformService.retrieveTicketProblems(ticketId);
	        final List<TicketMasterData> data = this.ticketMasterReadPlatformService.retrieveClientTicketHistory(ticketId);
	        
	        final TicketMasterData masterData = new TicketMasterData(description,data);
             
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, masterData, RESPONSE_PARAMETERS);
	        
	    }
		
		/**
		 * Method to download file and print if exists
		 * @param ticketId
		 * @return
		 */
		@GET
		@Path("{ticketId}/print")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public Response downloadFile(@PathParam("ticketId") final Long ticketId) {

			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
			final TicketDetail ticketDetail = this.detailsRepository.findOne(ticketId);
			final String printFileName = ticketDetail.getAttachments();
			final File file = new File(printFileName);
			final ResponseBuilder response = Response.ok(file);
			response.header("Content-Disposition", "attachment; filename=\"" + printFileName + "\"");
			response.header("Content-Type", "application/pdf");
			return response.build();
		}
		
		/**
		 * Method to handle Template Data
		 * @param responseParameters
		 * @param singleTicketData
		 * @return
		 */
		private TicketMasterData handleTicketTemplateData(final Collection<MCodeData> sourceData) {
			final List<EnumOptionData> priorityData = this.ticketMasterReadPlatformService.retrievePriorityData();
			final Collection<MCodeData> datas = this.codeReadPlatformService.getCodeValue("Problem Code");
			final List<UsersData>  userData = this.ticketMasterReadPlatformService.retrieveUsers();
			final Collection<MCodeData> statusData = codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_TICKET_STATUS);
			final List<TicketTeamMappingData> ticketmappingData = this.ticketmappingReadPlatformService.retrieveTicketTeamForDropdown();
			
			
			
			return  new TicketMasterData(datas, userData, priorityData, sourceData,statusData,ticketmappingData);
		}
		
		@GET
		@Path("{clientId}/{ticketId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveClientSingleTicket(@PathParam("clientId")final Long clientId, @PathParam("ticketId") final Long ticketId, @Context final UriInfo uriInfo) {
		  
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
		    final TicketMasterData data = this.ticketMasterReadPlatformService.retrieveTicket(clientId, ticketId);
		    final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		    return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_PARAMETERS);
		     
		}
		
		
		@GET
		@Path("subcategory")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveTicketMasterSubCategory(@Context final UriInfo uriInfo, @QueryParam("category") final Integer category) {
			
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			
			final TicketMasterData templateData = handleTicketSubCategoryData(category);
				return this.toApiJsonSerializer.serialize(settings, templateData, RESPONSE_PARAMETERS);
			}
		
		private TicketMasterData handleTicketSubCategoryData(final int category) {
			final List<SubCategoryData>  subCategoryData = this.ticketMasterReadPlatformService.retrieveSubCategory(category);
			
			return  new TicketMasterData(subCategoryData);
		}
		
		
		/**
		 * Method to insert new sub category into Database
		 * 
		 * @added by H
		 *//*
		@POST
		@Path("subcategory")
		@Produces({ MediaType.APPLICATION_JSON })
		@Consumes({ MediaType.APPLICATION_JSON })
		public String createSubcategory(final String jsonRequestBody) {
			final CommandWrapper command = new CommandWrapperBuilder().createSubcategory().withJson(jsonRequestBody).build();
			final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(command);
			return toApiJsonSerializer.serialize(result);

		}
		*/

		
		/**
		 * Method to update Ticket
		 * @param ticketId
		 * @param jsonRequestBody
		 * @return
		 */
		@PUT
		@Path("ticketing/{ticketId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String updateTicket(@PathParam("ticketId") final Long ticketId, final String jsonRequestBody) {
			final CommandWrapper commandRequest = new CommandWrapperBuilder().updateTicketMaster(ticketId).withJson(jsonRequestBody).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		}	
		
		@POST
	    @Path("office/{officeId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String createOfficeTicket(@PathParam("officeId") final Long officeId,final String apiRequestBodyAsJson) {
			
           try{
					CommandProcessingResult result = processCreateOfficeTicket(officeId, apiRequestBodyAsJson);
					return this.toApiJsonSerializer.serialize(result);
		           }catch(Exception e){
		        	   e.getStackTrace();
		        	   String result = null;
		        	   return result;
		           }
		}
		
		@GET
	    @Path("office/{officeId}")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveOfficeTicketDetails(@PathParam("officeId") final Long officeId, @Context final UriInfo uriInfo) {

			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
	        final List<TicketMasterData> data = this.ticketMasterReadPlatformService.retrieveOfficeTicketDetails(officeId);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_PARAMETERS);
	    }
		@GET
	    @Path("officeid/{officeId}")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveTicketDetailsByOfficeId(@PathParam("officeId") final Long officeId, @Context final UriInfo uriInfo) {

			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
	        final List<TicketMasterData> data = this.ticketMasterReadPlatformService.retrieveTicketDetailsByOfficeId(officeId);
	        //final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(data);
	    }
		
		public CommandProcessingResult processCreateOfficeTicket(Long officeId, String apiRequestData) {

			try {
				CommandProcessingResult result = null;
				Long userId = null;
				final SecurityContext context = SecurityContextHolder.getContext();
	        	if (context.getAuthentication() != null) {
	        		final AppUser appUser = this.context.authenticatedUser();
	        		userId = appUser.getId();
	        	}
	        	if(userId != null){	 
	        		final CommandWrapper commandRequest = new CommandWrapperBuilder().createTicketOffice(officeId).withJson(apiRequestData.toString()).build();
	        		result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        		return result;
	        	}else{
	        		final JsonElement parsedCommand = this.fromApiJsonHelper.parse(apiRequestData.toString());
	        		final JsonCommand command = JsonCommand.from(apiRequestData.toString(), parsedCommand, this.fromApiJsonHelper, "TICKET", officeId,
	        													null, null, officeId, null, null, null, null, null, null, null);
	        		result =this.ticketMasterWritePlatformService.createTicketMaster(command);
	        		return result;
	        	}
		            
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
		}

		@PUT
		@Path("office/{ticketId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String updateOfficeTicket(@PathParam("ticketId") final Long ticketId, final String jsonRequestBody) {
			final CommandWrapper commandRequest = new CommandWrapperBuilder().updateOfficeTicket(ticketId).withJson(jsonRequestBody).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		}	
		
		@PUT
		@Path("office/close/{ticketId}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String closeOfficeTicket(@PathParam("ticketId") final Long ticketId, final String jsonRequestBody) {
			final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteOfficeTicket(ticketId).withJson(jsonRequestBody).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		}
		
		@GET
	    @Path("office/{officeId}/{ticketId}")
	    @Consumes({MediaType.APPLICATION_JSON})
	    @Produces({MediaType.APPLICATION_JSON})
	    public String retrieveOfficeSingleTicketDetails( @PathParam("officeId") final Long officeId,
	    		@PathParam("ticketId") final Long ticketId,@Context final UriInfo uriInfo) {
	    	
	    	context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
	    	TicketMasterData data = this.ticketMasterReadPlatformService.retrieveSingleOfficeTicketDetails(officeId, ticketId);
	    	final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	    	if(settings.isTemplate()){
	    		data = this.handleTemplateData(data);
			}
	        
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_PARAMETERS);
	    }
		
		private TicketMasterData handleTemplateData(TicketMasterData data) {
			if(data == null){
				data = new TicketMasterData();
			}
			
			final Collection<MCodeData> statusdata = this.codeReadPlatformService.getCodeValue("Ticket Status", "1");
			data.setStatusData(statusdata);
	        final List<UsersData>  userData = this.ticketMasterReadPlatformService.retrieveUsers();
	        data.setUsersData(userData);
	        final List<EnumOptionData> priorityData = this.ticketMasterReadPlatformService.retrievePriorityData();
			final Collection<MCodeData> problemsDatas = this.codeReadPlatformService.getCodeValue("Problem Code");
			data.setPriorityType(priorityData);
			data.setProblemsDatas(problemsDatas);
			return  data;
		}
			
		
		@GET
		@Path("teamusers")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveTicketMasterTeamUsers(@Context final UriInfo uriInfo, @QueryParam("teamusers") final Long teamusers) {
			
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			
			final TicketMasterData templateData = handleTicketTicketUsersData(teamusers);
				return this.toApiJsonSerializer.serialize(settings, templateData, RESPONSE_PARAMETERS);
			}
		
		private TicketMasterData handleTicketTicketUsersData(final Long teamusers) {
			
			 List<TicketTeamMappingData> selectedusersData= this.ticketmappingReadPlatformService.retrieveSelectedUsers(teamusers);
			return  new TicketMasterData(selectedusersData,null);
		}
		
		
		@GET
		@Path("ticketing/{ticketId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveTicketsDetails(@Context final UriInfo uriInfo ,@PathParam("ticketId") final Long ticketId){
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermission);
			List<TicketMasterData> data = this.ticketMasterReadPlatformService.retrieveTicketsDetails(ticketId);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_PARAMETERS);
			
		}
		
		
		
		
}