/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendoragreement.api;

import java.io.File;
import java.io.IOException;
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

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiConstants;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.organisation.priceregion.service.RegionalPriceReadplatformService;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.useradministration.data.AppUserData;
import org.mifosplatform.vendoragreement.data.VendorAgreementData;
import org.mifosplatform.vendoragreement.domain.VendorAgreement;
import org.mifosplatform.vendoragreement.domain.VendorAgreementRepository;
import org.mifosplatform.vendoragreement.exception.AgreementfileNotFoundException;
import org.mifosplatform.vendoragreement.exception.VendorNotFoundException;
import org.mifosplatform.vendoragreement.service.VendorAgreementReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/vendoragreement")
@Component
@Scope("singleton")
public class VendorAgreementApiResource {

    /**
     * The set of parameters that are supported in response for
     * {@link AppUserData}.
     */
    private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));

    private static final String RESOURCENAMEFORPERMISSIONS = "VENDORAGREEMENT";
    private final PlatformSecurityContext context;
    private final VendorAgreementReadPlatformService vendorAgreementReadPlatformService;
    private final RegionalPriceReadplatformService regionalPriceReadplatformService;
    private final DefaultToApiJsonSerializer<VendorAgreementData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final VendorAgreementRepository vendorAgreementRepository;
    private final MCodeReadPlatformService mCodeReadPlatformService;
    public InputStream inputStreamObject;
    
    @Autowired
    public VendorAgreementApiResource(final PlatformSecurityContext context, final VendorAgreementReadPlatformService readPlatformService,
    		final RegionalPriceReadplatformService regionalPriceReadplatformService, final DefaultToApiJsonSerializer<VendorAgreementData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
             final VendorAgreementRepository vendorAgreementRepository,final MCodeReadPlatformService mCodeReadPlatformService) {

    	this.context = context;
        this.vendorAgreementReadPlatformService = readPlatformService;
        this.regionalPriceReadplatformService = regionalPriceReadplatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.vendorAgreementRepository = vendorAgreementRepository;
        this.mCodeReadPlatformService = mCodeReadPlatformService;
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String vendorAgreementTemplateDetails(@QueryParam("vendorId") final Long vendorId,@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
        VendorAgreementData vendor=handleTemplateData(vendorId);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, vendor, RESPONSE_DATA_PARAMETERS);
    }
    
    private VendorAgreementData handleTemplateData(Long vendorId) {
		
    	final List<PriceRegionData> priceRegionData = this.regionalPriceReadplatformService.getPriceRegionsDetails();
        //final List<EnumOptionData> statusData = this.planReadPlatformService.retrieveNewStatus();
    	final Collection<MCodeData> agreementTypes = this.mCodeReadPlatformService.getCodeValue("Agreement Type");
        final List<ServiceData> servicesData = this.vendorAgreementReadPlatformService.retrieveServices(vendorId);
        final List<PlanData> planDatas = this.vendorAgreementReadPlatformService.retrievePlans(vendorId);
		 
		return new VendorAgreementData(priceRegionData, agreementTypes, servicesData,
					planDatas);
			
	}
    
    @POST
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createUploadFile(@HeaderParam("Content-Length") final Long fileSize, @FormDataParam("file") final InputStream inputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetails, @FormDataParam("file") final FormDataBodyPart bodyPart,
            @FormDataParam("jsonData") final String jsonData) throws JSONException, IOException {

        FileUtils.validateFileSizeWithinPermissibleRange(fileSize, jsonData, ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);
        inputStreamObject=inputStream;
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        final Date date = DateUtils.getDateOfTenant();
        final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd MMMM yyyy");
        final LocalDate localdate = dtf.parseLocalDate(dateFormat.format(date));
        JSONObject object = new JSONObject(jsonData);
        
        if(fileDetails != null){
        final String fileUploadLocation = FileUtils.generateXlsFileDirectory();
        final String fileName = fileDetails.getFileName();
        	if (!new File(fileUploadLocation).isDirectory()) {
        		new File(fileUploadLocation).mkdirs();
        	}
        
        String fileLocation=null;
        fileLocation = FileUtils.saveToFileSystem(inputStream, fileUploadLocation, fileName);
        object.put("fileLocation", fileLocation);
        
        }
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
        .createVendorAgreement() //
        .withJson(object.toString()) //
        .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
 }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveVendorAgreements(@Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final List<VendorAgreementData> vendor = this.vendorAgreementReadPlatformService.retrieveAllVendorAgreements();

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, vendor, RESPONSE_DATA_PARAMETERS);
    }
    
    @GET
	@Path("{vendorId}") /** vendorId*/
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveVendorAgreementData(	@PathParam("vendorId") final Long vendorId,@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		List<VendorAgreementData> agreementData = this.vendorAgreementReadPlatformService.retrieveRespectiveAgreementData(vendorId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, agreementData,RESPONSE_DATA_PARAMETERS);
	}
    
    @GET
    @Path("{vendorAgreementId}/details")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveSingleVendorAgreement(@PathParam("vendorAgreementId") final Long vendorAgreementId, @Context final UriInfo uriInfo) {

        context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        VendorAgreementData vendorAgreeData = this.vendorAgreementReadPlatformService.retrieveVendorAgreement(vendorAgreementId);
        if(vendorAgreeData == null){
        	throw new VendorNotFoundException(vendorAgreementId.toString());
        }
        List<VendorAgreementData> vendorAgreementDetailsData = this.vendorAgreementReadPlatformService.retrieveVendorAgreementDetails(vendorAgreementId);
        vendorAgreeData.setVendorAgreementDetailsData(vendorAgreementDetailsData);
        
        if (settings.isTemplate()) {
        	final List<PriceRegionData> priceRegionData = this.regionalPriceReadplatformService.getPriceRegionsDetails();
        	final Collection<MCodeData> agreementTypes = this.mCodeReadPlatformService.getCodeValue("Agreement Type");
            final List<ServiceData> servicesData = this.vendorAgreementReadPlatformService.retrieveServices(vendorAgreeData.getVendorId());
            final List<PlanData> planDatas = this.vendorAgreementReadPlatformService.retrievePlans(vendorAgreeData.getVendorId());
            vendorAgreeData.setPriceRegionData(priceRegionData);
            vendorAgreeData.setPlanDatas(planDatas);
            vendorAgreeData.setAgreementTypes(agreementTypes);
            vendorAgreeData.setServicesData(servicesData);
        }

        return this.toApiJsonSerializer.serialize(settings, vendorAgreeData, RESPONSE_DATA_PARAMETERS);
    }
    
    @POST
    @Path("{vendorAgreementId}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateVendorAgreement(@HeaderParam("Content-Length") final Long fileSize, @FormDataParam("file") final InputStream inputStream,
            @FormDataParam("file") final FormDataContentDisposition fileDetails, @FormDataParam("file") final FormDataBodyPart bodyPart,
            @FormDataParam("jsonData") final String jsonData,@PathParam("vendorAgreementId") final Long vendorAgreementId) throws JSONException, IOException {

        FileUtils.validateFileSizeWithinPermissibleRange(fileSize, jsonData, ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);
        inputStreamObject=inputStream;
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        final Date date = DateUtils.getDateOfTenant();
        final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd MMMM yyyy");
        final LocalDate localdate = dtf.parseLocalDate(dateFormat.format(date));
        JSONObject object = new JSONObject(jsonData);
        
        if(fileDetails != null){
        final String fileUploadLocation = FileUtils.generateXlsFileDirectory();
        final String fileName=fileDetails.getFileName();
        	if (!new File(fileUploadLocation).isDirectory()) {
        		new File(fileUploadLocation).mkdirs();
        	}
        
        String fileLocation=null;
        fileLocation = FileUtils.saveToFileSystem(inputStream, fileUploadLocation, fileName);
        object.put("fileLocation", fileLocation);
        }
        
        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
        .updateVendorAgreement(vendorAgreementId) //
        .withJson(object.toString()) //
        .build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
 }
    
    @GET
	@Path("/download/{agreementId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response downloadAgreementFile(@PathParam("agreementId") final Long agreementId) {
		final VendorAgreement vendorAgreement = this.vendorAgreementRepository.findOne(agreementId);
		if(vendorAgreement == null){
			throw new AgreementfileNotFoundException(agreementId);
		}
		final String fileName = vendorAgreement.getVendorAgmtDocument();
		 final File file = new File(fileName);
		ResponseBuilder response = Response.ok(file);
		response.header("Content-Disposition", "attachment; filename=\""+ fileName+ "\"");
       		response.header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		return response.build();
		
	}
    
    @GET
	@Path("duration/{planId}") /** planId*/
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlanDurationData(@PathParam("planId") final Long planId,@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		List<VendorAgreementData> agreementData = this.vendorAgreementReadPlatformService.retrievePlanDurationData(planId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, agreementData,RESPONSE_DATA_PARAMETERS);
	}
    
    @POST
    @Path("New/{vendorAgreementId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String createNewVendorAgreement(@PathParam("vendorAgreementId") final Long vendorAgreementId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createNewVendorAgreement(vendorAgreementId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(command);
		return this.toApiJsonSerializer.serialize(result);
	}
    
    @PUT
	@Path("{vendorAgreementId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateNewVendorAgreement(@PathParam("vendorAgreementId") final Long vendorAgreementId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateNewVendorAgreement(vendorAgreementId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(command);
		return this.toApiJsonSerializer.serialize(result);
	}
    


    


    

}
