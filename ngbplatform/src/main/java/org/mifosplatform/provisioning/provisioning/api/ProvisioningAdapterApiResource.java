package org.mifosplatform.provisioning.provisioning.api;

import java.io.File;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.jobs.exception.NoLogFileFoundException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.provisioning.provisioning.data.ProvisionAdapter;
import org.mifosplatform.provisioning.provisioning.data.ProvisioningData;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The class <code>ProvisioningAdapterApiResource</code> is developed for
 * Adapter Controlling From OBS.
 * 
 * We can Start,stop,Restart the Adapter from this Class. we can also
 * downloading log files of 1 week Period.
 * 
 * @author ashokreddy
 * 
 */
@Path("/adapter")
@Component
@Scope("singleton")
public class ProvisioningAdapterApiResource {

	private final String resourceNameForPermissions = "PROVISIONINGADAPTERSYSTEM";

	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<ProvisioningData> toApiJsonSerializer;
	private final ProvisioningWritePlatformService provisioningWritePlatformService;

	@Autowired
	public ProvisioningAdapterApiResource(
			final PlatformSecurityContext context,
			final DefaultToApiJsonSerializer<ProvisioningData> toApiJsonSerializer,
			final ProvisioningWritePlatformService provisioningWritePlatformService) {

		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.provisioningWritePlatformService = provisioningWritePlatformService;
	}

	/**
	 * This method <code>retrieveLogDetails</code> is Used for Retrieve Log
	 * Information. like previous 7 days log file names From specific Directory.
	 * 
	 * Note: Specific Directory should be sent in requestData.
	 * 
	 * @param requestData
	 *            Containg input data in the Form of JsonObject.
	 * @return
	 */
	@POST
	@Path("logs")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveLogDetails(final String requestData) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final List<ProvisionAdapter> output = this.provisioningWritePlatformService.gettingLogInformation(requestData);
		final ProvisioningData data = new ProvisioningData();
		data.setProvisionAdapterData(output);
		return this.toApiJsonSerializer.serialize(data);
	}

	/**
	 * This method <code>runningAdapterCommands</code> is Used for 
	 * Running the System Commands.
	 * 
	 * Note: command should be sent in requestData.
	 *  
	 * @param requestData
	 * 			 Containg input data in the Form of JsonObject.
	 * @return
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String runningAdapterCommands(final String requestData) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final String output = this.provisioningWritePlatformService.runAdapterCommands(requestData);
		final ProvisioningData data = new ProvisioningData();
		data.setStatus(output);
		return this.toApiJsonSerializer.serialize(data);
	}

	/**
	 * This method <code>runningAdapterCommands</code> is Used for 
	 * Downloading .csv log files from Specific path.
	 * 
	 * Note : log file path should be sent in the "printFilePath" parameter
	 * 
	 * @param printFilePath
	 * 			Downloading file path
	 * @return
	 */
	@GET
	@Path("printlog")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public Response logFile(@QueryParam("filePath") final String printFilePath) {
		
		if (printFilePath != null) {
			File file = new File(printFilePath);
			final ResponseBuilder response = Response.ok(file);
			response.header("Content-Disposition", "attachment; filename=\"" + printFilePath + "\"");
			response.header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			return response.build();
		} else {
			throw new NoLogFileFoundException();
		}
	}
}
