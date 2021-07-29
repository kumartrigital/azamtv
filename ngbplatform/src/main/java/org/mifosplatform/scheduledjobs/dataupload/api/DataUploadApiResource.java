package org.mifosplatform.scheduledjobs.dataupload.api;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiConstants;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.scheduledjobs.dataupload.command.DataUploadCommand;
import org.mifosplatform.scheduledjobs.dataupload.data.UploadStatusData;
import org.mifosplatform.scheduledjobs.dataupload.domain.DataUpload;
import org.mifosplatform.scheduledjobs.dataupload.domain.DataUploadRepository;
import org.mifosplatform.scheduledjobs.dataupload.service.DataUploadReadPlatformService;
import org.mifosplatform.scheduledjobs.dataupload.service.DataUploadWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;



 /**
 * @author hugo
 *
 */
 @Path("/datauploads")
 @Component
 @Scope("singleton")
 public class DataUploadApiResource {
	    
	  
	    
	    private final static String RESOURCENAME_FOR_PERMISSIONS = "DATAUPLOADS";
	    public InputStream inputStreamObject;
     	private final DataUploadWritePlatformService dataUploadWritePlatformService;
     	private final DataUploadRepository dataUploadRepository;
     	private final PlatformSecurityContext context;
     	private final DataUploadReadPlatformService dataUploadReadPlatformService;
     	private final DefaultToApiJsonSerializer< UploadStatusData> toApiJsonSerializer;
	
    	@Autowired
	    public DataUploadApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<UploadStatusData> defaulttoApiJsonSerializerforUploadStatus,
	            final DataUploadWritePlatformService dataUploadWritePlatformService,final DataUploadRepository dataUploadRepository,
	            final DataUploadReadPlatformService dataUploadReadPlatformService) {
	        
		 this.context = context;
		 this.dataUploadRepository=dataUploadRepository;
		 this.dataUploadReadPlatformService = dataUploadReadPlatformService;
		 this.dataUploadWritePlatformService=dataUploadWritePlatformService;
	     this.toApiJsonSerializer = defaulttoApiJsonSerializerforUploadStatus;
	      

	    }
	
 	    @GET
	    @Consumes({ MediaType.APPLICATION_JSON })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public String retrieveUploadFiles( @Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,
				@QueryParam("offset") final Integer offset) {
 	    	
		    context.authenticatedUser().validateHasReadPermission(RESOURCENAME_FOR_PERMISSIONS);
		 	final SearchSqlQuery searchUploads =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
			final Page<UploadStatusData> uploadstatusdata= this.dataUploadReadPlatformService.retrieveAllUploadStatusData(searchUploads);	
			return this.toApiJsonSerializer.serialize(uploadstatusdata);
		
	 
	    }
	 
	    @POST
	    @Path("/documents")
	    @Consumes({ MediaType.MULTIPART_FORM_DATA })
	    @Produces({ MediaType.APPLICATION_JSON })
	    public Response createUploadFile(@HeaderParam("Content-Length") final Long fileSize, @FormDataParam("file") final InputStream inputStream,
	            @FormDataParam("file") final FormDataContentDisposition fileDetails, @FormDataParam("file") final FormDataBodyPart bodyPart,
	            @FormDataParam("status") final String name, @FormDataParam("description") final String description,@QueryParam("isProcess") final boolean isProcess) {

	        FileUtils.validateFileSizeWithinPermissibleRange(fileSize, name, ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);
	        inputStreamObject=inputStream;
	        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
	        final Date date = DateUtils.getDateOfTenant();
	        final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd MMMM yyyy");
	        final LocalDate localdate = dtf.parseLocalDate(dateFormat.format(date));
	        final String fileUploadLocation = FileUtils.generateXlsFileDirectory();
	        final String fileName=fileDetails.getFileName();
	        	if (!new File(fileUploadLocation).isDirectory()) {
	        		new File(fileUploadLocation).mkdirs();
	        	}
	        final DataUploadCommand uploadStatusCommand=new DataUploadCommand(name,null,localdate,"",null,null,null,description,fileName,inputStream,fileUploadLocation);
	        CommandProcessingResult id = this.dataUploadWritePlatformService.addItem(uploadStatusCommand);
	        if(isProcess){
	        	this.processFile(id.resourceId(),null);
	        }
	        return Response.ok().entity(id.toString()).build();
     // return null;
	 }
	 
	@PUT
	@Path("{uploadfileId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String processFile(@PathParam("uploadfileId") final Long uploadfileId, @Context final UriInfo uriInfo) {
		
		   final CommandWrapper commandRequest = new CommandWrapperBuilder().updateUploadFile(uploadfileId).build();
		   //final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		   final CommandProcessingResult result = this.dataUploadWritePlatformService.processDatauploadFile(uploadfileId);
		   return this.toApiJsonSerializer.serialize(result);
	}
	

	@GET
	@Path("print/{uploadfileId}")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response downloadFile(@PathParam("uploadfileId") final Long id) {
		
		final DataUpload uploadStatus = this.dataUploadRepository.findOne(id);
		final String printFileName = uploadStatus.getUploadFilePath();
		final File file = new File(printFileName);
		ResponseBuilder response = Response.ok(file);
		response.header("Content-Disposition", "attachment; filename=\""+ printFileName + "\"");
        response.header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		return response.build();
	}

	@GET
	@Path("printlog/{logfileId}")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response logFile(@PathParam("logfileId") final Long id) {
		final DataUpload uploadStatus = this.dataUploadRepository.findOne(id);
		final String printFilePath = uploadStatus.getUploadFilePath();
		final String printFileName = printFilePath.replace("csv","log");
		final File file = new File(printFileName);
		ResponseBuilder response = Response.ok(file);
		response.header("Content-Disposition", "attachment; filename=\""+ printFileName + "\"");
       		response.header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		return response.build();
	}
	
}
