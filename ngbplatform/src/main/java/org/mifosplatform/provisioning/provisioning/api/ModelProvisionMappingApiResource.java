package org.mifosplatform.provisioning.provisioning.api;

import java.util.Arrays;
import java.util.HashSet;
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
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.provisioning.networkelement.service.NetworkElementReadPlatformService;
import org.mifosplatform.provisioning.provisioning.data.ModelProvisionMappingData;
import org.mifosplatform.provisioning.provisioning.service.ModelProvisionMappingReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/modelprovisionmapping")
@Component
@Scope("singleton")
public class ModelProvisionMappingApiResource {
	
	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "provisioningId", "modelId", "isDeleted"));
	private final String RESOURCENAMEFORPERMISSIONS = "MODELPROVISIONMAPPING";
	
	private final DefaultToApiJsonSerializer<ModelProvisionMappingData> toApiJsonSerializer;
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final ModelProvisionMappingReadPlatformService modelProvisionMappingReadPlatformService;
	private final NetworkElementReadPlatformService networkElementReadPlatformService;
	
	@Autowired
	public ModelProvisionMappingApiResource(
			DefaultToApiJsonSerializer<ModelProvisionMappingData> toApiJsonSerializer,
			PlatformSecurityContext context,
			PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			MCodeReadPlatformService mCodeReadPlatformService,
			ApiRequestParameterHelper apiRequestParameterHelper,
			final ModelProvisionMappingReadPlatformService modelProvisionMappingReadPlatformService,
			final NetworkElementReadPlatformService networkElementReadPlatformService) {
		
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.modelProvisionMappingReadPlatformService = modelProvisionMappingReadPlatformService;
	    this.networkElementReadPlatformService = networkElementReadPlatformService;
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveModelProvisionMapping(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		final SearchSqlQuery searchModelProvisionMapping =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<ModelProvisionMappingData> mrnDetailsDatas = this.modelProvisionMappingReadPlatformService.retrieveAll(searchModelProvisionMapping);
		return toApiJsonSerializer.serialize(mrnDetailsDatas);
	}
	
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePropertyTemplate(@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		final ModelProvisionMappingData modelProvisionData = hadleTemplateData(null);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, modelProvisionData,RESPONSE_DATA_PARAMETERS);

	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createModelProvisionMapping(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createModelProvisionMapping().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{modelProvisionMappingId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveSingleModelProvisionMapping(@Context final UriInfo uriInfo ,@PathParam("modelProvisionMappingId") final Long modelProvisionMappingId){
		
		this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		ModelProvisionMappingData modelProvisionMappingData = this.modelProvisionMappingReadPlatformService.retrieveSingleModelProvisionMapping(modelProvisionMappingId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	
		if(settings.isTemplate()){
			modelProvisionMappingData = this.hadleTemplateData(modelProvisionMappingData);
		}
		return this.toApiJsonSerializer.serialize(settings,modelProvisionMappingData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	@PUT
	@Path("{modelProvisionMappingId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateModelProvisionMapping(final String jsonRequestBody, @PathParam("modelProvisionMappingId") final Long modelProvisionMappingId){
		final CommandWrapper command = new CommandWrapperBuilder().updateModelProvisionMapping(modelProvisionMappingId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	@DELETE
	@Path("{modelProvisionMappingId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String deleteModelProvisionMapping(final String jsonRequestBody, @PathParam("modelProvisionMappingId") final Long modelProvisionMappingId){
		final CommandWrapper command = new CommandWrapperBuilder().deleteModelProvisionMapping(modelProvisionMappingId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return this.toApiJsonSerializer.serialize(result);
	}
	
	
	private ModelProvisionMappingData hadleTemplateData(ModelProvisionMappingData modelProvisionData) {
		if(null == modelProvisionData){
			modelProvisionData = new ModelProvisionMappingData();
		}
		//modelProvisionData.setProvisionDatas(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_PROVISIONING));
		//modelProvisionData.setItemModelDatas(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_ITEM_MODEL));
		modelProvisionData.setProvisionDatas(this.networkElementReadPlatformService.retrieveNetworkElements());
		return modelProvisionData;
	}
	

	
}