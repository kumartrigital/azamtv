package org.mifosplatform.portfolio.property.api;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.UriInfo;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiConstants;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.property.data.PropertyDefinationData;
import org.mifosplatform.portfolio.property.service.PropertyReadPlatformService;
import org.mifosplatform.scheduledjobs.dataupload.command.DataUploadCommand;
import org.mifosplatform.scheduledjobs.dataupload.service.DataUploadWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

/**
 * @author ranjith
 * * this api class used to create,update and delete different property master code's 
 * Date 25/04/2015
 */
@Path("/propertymaster")
@Component
@Scope("singleton")
public class PropertyMasterApiResource {

	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("propertyTypes", "codes"));

	public InputStream inputStreamObject;
	private final static String RESOURCENAMEFORPERMISSIONS = "PROPERTYCODEMASTER";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<PropertyDefinationData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	private final DataUploadWritePlatformService dataUploadWritePlatformService;
	private final PropertyReadPlatformService propertyReadPlatformService;

	@Autowired
	public PropertyMasterApiResource(final PlatformSecurityContext context,
			final DefaultToApiJsonSerializer<PropertyDefinationData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
			final MCodeReadPlatformService mCodeReadPlatformService,
			final DataUploadWritePlatformService dataUploadWritePlatformService,
			final PropertyReadPlatformService propertyReadPlatformService) {

		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		this.dataUploadWritePlatformService = dataUploadWritePlatformService;
		this.propertyReadPlatformService = propertyReadPlatformService;

	}

	/**
	 * template for creating property code master details
	 */
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePropertyCodeMasterMcodeData(	@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(	RESOURCENAMEFORPERMISSIONS);
		final Collection<MCodeData> propertyTypes = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.PROPERTY_CODE_TYPE);
		//final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(propertyTypes);
	}

	/**
	 * Using this method getting all property code master details
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllPropertyMasterDetails(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit,@QueryParam("offset") final Integer offset) {
		
		this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		final SearchSqlQuery searchPropertyDetails = SearchSqlQuery.forSearch(sqlSearch, offset, limit);
		final Page<PropertyDefinationData> propertyCodeMasterData = this.propertyReadPlatformService.retrieveAllPropertyMasterData(searchPropertyDetails);
		return this.toApiJsonSerializer.serialize(propertyCodeMasterData);
	}

	/**
	 * using this method posting property code master data
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createNewPropertyMaster(final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createPropertyMaster().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * using this method posting property code master data from uploaded file
	 */
	@POST
	@Path("/documents")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createNewPropertyMasterUpload(@HeaderParam("Content-Length") final Long fileSize,
			@FormDataParam("file") final InputStream inputStream,@FormDataParam("file") final FormDataContentDisposition fileDetails,
			@FormDataParam("file") final FormDataBodyPart bodyPart) {

		String name = "Property Master";
		FileUtils.validateFileSizeWithinPermissibleRange(fileSize, name,ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);
		inputStreamObject = inputStream;
		DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		final Date date = DateUtils.getDateOfTenant();
		final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd MMMM yyyy");
		final LocalDate localdate = dtf.parseLocalDate(dateFormat.format(date));
		final String fileUploadLocation = FileUtils.generateXlsFileDirectory();
		final String fileName = fileDetails.getFileName();
		if (!new File(fileUploadLocation).isDirectory()) {
			new File(fileUploadLocation).mkdirs();
		}
		final DataUploadCommand uploadStatusCommand = new DataUploadCommand(name, null, localdate, "", null, null, null, "", fileName,
				inputStream, fileUploadLocation);
		CommandProcessingResult result = this.dataUploadWritePlatformService.addItem(uploadStatusCommand);
		if (result != null) {
			this.dataUploadWritePlatformService.processDatauploadFile(result.resourceId());
		}
		return Response.ok().entity(result.toString()).build();

	}

	@GET
	@Path("/type")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAddressDetailsWithcityName(@Context final UriInfo uriInfo,@QueryParam("query") final String propertyType,@QueryParam("queryParam") final String propertyCode) {

		context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		final List<PropertyDefinationData> typeDetails = this.propertyReadPlatformService.retrievPropertyType(propertyType,propertyCode);
		return this.toApiJsonSerializer.serialize(typeDetails);
	}
	
	
	/**
	 * using this method get single property master  details
	 */
	@GET
	@Path("{codeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveOnePropertyMaster(@PathParam("codeId") final Long codeId,@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		final PropertyDefinationData propertyMaster = this.propertyReadPlatformService.retrieveSinglePropertyMaster(codeId);
		final ApiRequestJsonSerializationSettings settings =this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if (settings.isTemplate()) {
			final Collection<MCodeData> propertyTypes = this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.PROPERTY_CODE_TYPE);
			propertyMaster.setPropertyTypes(propertyTypes);
		}
		return this.toApiJsonSerializer.serialize(settings, propertyMaster,RESPONSE_DATA_PARAMETERS);
	}
	
	/**
	 * @param propertyId
	 * @param apiRequestBodyAsJson
	 * @return single property master data will be  update here
	 */
	@PUT
	@Path("{codeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updatePropertyMaster(@PathParam("codeId") final Long codeId,final String apiRequestBodyAsJson) {

		context.authenticatedUser();
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePropertyMaster(codeId).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * using this method we can remove single property master details
	 */
	@DELETE
	@Path("{codeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deletePropertyMaster(@PathParam("codeId") final Long codeId) {

		this.context.authenticatedUser();
		final CommandWrapper commandRequest = new CommandWrapperBuilder().deletePropertyMaster(codeId).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);

	}
	

}
