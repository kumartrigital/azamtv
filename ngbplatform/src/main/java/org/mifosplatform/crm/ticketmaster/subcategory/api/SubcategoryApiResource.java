package org.mifosplatform.crm.ticketmaster.subcategory.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import org.mifosplatform.crm.ticketmaster.subcategory.data.SubcategoryDataT;
import org.mifosplatform.crm.ticketmaster.subcategory.service.SubcategoryReadPlatformService;
import org.mifosplatform.crm.ticketmaster.subcategory.service.SubcategoryWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * @Written by H
 * for adding Subcategory items
 *
 */


@Path("/createSubcategory")
@Component
@Scope("singleton")
public class SubcategoryApiResource {
	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));

	private final String resourceNameForPermission = "TICKET_SUBCATEGORY";
	
	private final static String RESOURCE_TYPE = "SUBCATEGORY";
	final private SubcategoryWritePlatformService subcategoryWritePlatformService;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private DefaultToApiJsonSerializer<SubcategoryDataT> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<TicketMasterData> toTicketJsonSerializer;
	
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	final private MCodeReadPlatformService codeReadPlatformService;

	final private SubcategoryReadPlatformService subcategoryReadPlatformService;

	@Autowired
	public SubcategoryApiResource(final PlatformSecurityContext context,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final DefaultToApiJsonSerializer<SubcategoryDataT> toApiJsonSerializer,
			final SubcategoryWritePlatformService subcategoryWritePlatformService,
			final MCodeReadPlatformService codeReadPlatformService,
			final DefaultToApiJsonSerializer<TicketMasterData> toTicketJsonSerializer,
			final SubcategoryReadPlatformService subcategoryReadPlatformService) {

		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.context = context;
		this.subcategoryWritePlatformService = subcategoryWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.codeReadPlatformService=codeReadPlatformService;
		this.toTicketJsonSerializer=toTicketJsonSerializer;
		this.subcategoryReadPlatformService =subcategoryReadPlatformService;
	}


	/**
	 * Method for Inserting sub category Data into DB
	 * @return
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String createSubcategory(final String jsonRequestBody) {
		final CommandWrapper command = new CommandWrapperBuilder().createSubcategory().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = commandsSourceWritePlatformService.logCommandSource(command);
		return this.toApiJsonSerializer.serialize(result);

	}
	
	
	// Retrieving Data from DB by Id 

		@GET
		@Path("{id}")
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveSingleSubcategoryDetails(@Context final UriInfo uriInfo, @PathParam("id") final Long id) {
			this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
			SubcategoryDataT subcategoryData = this.subcategoryReadPlatformService.retrieveSubcategory(id);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
					.process(uriInfo.getQueryParameters());
			return toApiJsonSerializer.serialize(subcategoryData);
		
		}
	
			
		// Retrieving ALL Data from DB
		@GET
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveAllSubcategoryDetails(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch,
				@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset) {

			this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
			final SearchSqlQuery searchSubcategory = SearchSqlQuery.forSearch(sqlSearch, offset, limit);
			final Page<SubcategoryDataT> subcategoryData1 = this.subcategoryReadPlatformService.retrieveSubcategory(searchSubcategory);
			return toApiJsonSerializer.serialize(subcategoryData1);
				
				
		}		
				
	

	/**
	 * Method for Retrieving Main Category Details for template
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
		
		
			final Collection<MCodeData> sourceData = codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_TICKET_SOURCE);
			final TicketMasterData templateData = handleTicketTemplateData(sourceData);
			return this.toTicketJsonSerializer.serialize(settings, templateData, RESPONSE_DATA_PARAMETERS); 
		/*return null;*/
		}
	
	private TicketMasterData handleTicketTemplateData(final Collection<MCodeData> sourceData) {
		final Collection<MCodeData> datas = this.codeReadPlatformService.getCodeValue("Problem Code");
		
		return  new TicketMasterData(datas, null,null,null,null,null);
	}
	
	

}
