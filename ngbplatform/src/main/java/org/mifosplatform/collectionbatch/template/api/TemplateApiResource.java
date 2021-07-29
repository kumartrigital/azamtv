package org.mifosplatform.collectionbatch.template.api;

import java.util.Arrays;
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

import org.mifosplatform.collectionbatch.template.data.TemplateData;
import org.mifosplatform.collectionbatch.template.service.TemplateReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.data.QuoteData;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.organisation.broadcaster.service.BroadcasterReadPlatformService;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/template")
@Component
@Scope("singleton")
public class TemplateApiResource {

private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	
	private final static String RESOURCE_TYPE = "TEMPLATE";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<TemplateData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private TemplateReadPlatformService templateReadPlatformService;
	
	
	
	
	
	@Autowired
	public TemplateApiResource(
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final ToApiJsonSerializer<TemplateData> apiJsonSerializer, final PlatformSecurityContext context, 
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final AppUserReadPlatformService appUserReadPlatformService,
			final TemplateReadPlatformService templateReadPlatformService) {
		
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.templateReadPlatformService=templateReadPlatformService;
		
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String templates(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createTemplates().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("{templateId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateTemplates(@PathParam("templateId") final Long templateId,final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateTemplates(templateId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
		 return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String TemplateDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchTemplate = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<TemplateData> templateDatas = templateReadPlatformService.retrieveTemplates(searchTemplate);
		return apiJsonSerializer.serialize(templateDatas);
	}
	
	

	@GET
	@Path("{templateId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveTemplateDetails(@Context final UriInfo uriInfo ,@PathParam("templateId") final Long templateId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
	/*final TemplateData templateDatas = this.templateReadPlatformService.retrieveTemplate(templateId);*/
		final TemplateData templateData = new TemplateData(this.templateReadPlatformService.retrieveTemplate(templateId));
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return apiJsonSerializer.serialize(templateData);
		
	}
	
	
	
}
