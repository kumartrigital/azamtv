package org.mifosplatform.infrastructure.dataqueries.api;

import java.util.List;

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
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.dataqueries.data.DatatableData;
import org.mifosplatform.infrastructure.dataqueries.data.GenericResultsetData;
import org.mifosplatform.infrastructure.dataqueries.service.GenericDataService;
import org.mifosplatform.infrastructure.dataqueries.service.ReadWriteNonCoreDataService;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hugo
 * 
 */
@Path("/datatables")
@Component
@Scope("singleton")
public class DatatablesApiResource {

	private final PlatformSecurityContext context;
	private final GenericDataService genericDataService;
	private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
	private final ToApiJsonSerializer<GenericResultsetData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;

	@Autowired
	public DatatablesApiResource(
			final PlatformSecurityContext context,
			final GenericDataService genericDataService,
			final ReadWriteNonCoreDataService readWriteNonCoreDataService,
			final ToApiJsonSerializer<GenericResultsetData> toApiJsonSerializer,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService) {
		this.context = context;
		this.genericDataService = genericDataService;
		this.readWriteNonCoreDataService = readWriteNonCoreDataService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
	}

	/**
	 * @param apptable
	 * @param uriInfo
	 * @return retrieved all data tables list or retrieved all data tables which
	 *         are tie-up with application table
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveDatatables(@QueryParam("apptable") final String apptable,@Context final UriInfo uriInfo) {

		final List<DatatableData> result = this.readWriteNonCoreDataService.retrieveDatatableNames(apptable);
		final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serializePretty(prettyPrint, result);
	}

	/**
	 * @param apiRequestBodyAsJson
	 *            create new data table
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createNewDatatable(final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createDatatable(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * @param datatable
	 * @param uriInfo
	 * @return retrieved single data table for editing
	 */
	@GET
	@Path("{datatable}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSingleDatatable(@PathParam("datatable") final String datatable,@Context final UriInfo uriInfo) {

		final DatatableData result = this.readWriteNonCoreDataService.retrieveSingleDatatable(datatable);
		final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serializePretty(prettyPrint, result);
	}

	/**
	 * @param datatableName
	 * @param apiRequestBodyAsJson
	 * @return update data table
	 */
	@PUT
	@Path("{datatableName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateDatatable(@PathParam("datatableName") final String datatableName,final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateDatatable(datatableName, apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * @param datatableName
	 * @param apiRequestBodyAsJson
	 * @return delete single data table
	 */
	@DELETE
	@Path("{datatableName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteDatatable(@PathParam("datatableName") final String datatableName,final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteDatatable(datatableName).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * @param datatable
	 * @param apptableId
	 * @param order
	 * @param uriInfo
	 * @return this method used retrieve  data tables with particular
	 *         application table id
	 */
	@GET
	@Path("{datatable}/{apptableId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveDatatable(
			@PathParam("datatable") final String datatable,
			@PathParam("apptableId") final Long apptableId,
			@QueryParam("order") final String order,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasDatatableReadPermission(
				datatable);

		final GenericResultsetData results = this.readWriteNonCoreDataService
				.retrieveDataTableGenericResultSet(datatable, apptableId,
						order, null);

		String json = "";
		final boolean genericResultSet = ApiParameterHelper
				.genericResultSet(uriInfo.getQueryParameters());
		if (genericResultSet) {
			final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
					.getQueryParameters());
			json = this.toApiJsonSerializer.serializePretty(prettyPrint,
					results);
		} else {
			json = this.genericDataService
					.generateJsonFromGenericResultsetData(results);
		}

		return json;
	}

	/**
	 * @param datatable
	 * @param apptableId
	 * @param datatableId
	 * @param order
	 * @param uriInfo
	 * @return this method used retrieve single data table with it id nd
	 * particular application table id 
	 */
	@GET
	@Path("{datatable}/{apptableId}/{datatableId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveDatatableManyEntrys(
			@PathParam("datatable") final String datatable,
			@PathParam("apptableId") final Long apptableId,
			@PathParam("datatableId") final Long datatableId,
			@QueryParam("order") final String order,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasDatatableReadPermission(
				datatable);

		final GenericResultsetData results = this.readWriteNonCoreDataService
				.retrieveDataTableGenericResultSet(datatable, apptableId,
						order, datatableId);

		String json = "";
		final boolean genericResultSet = ApiParameterHelper
				.genericResultSet(uriInfo.getQueryParameters());
		if (genericResultSet) {
			final boolean prettyPrint = ApiParameterHelper.prettyPrint(uriInfo
					.getQueryParameters());
			json = this.toApiJsonSerializer.serializePretty(prettyPrint,
					results);
		} else {
			json = this.genericDataService
					.generateJsonFromGenericResultsetData(results);
		}

		return json;
	}

	/**
	 * @param datatable
	 * @param apptableId
	 * @param apiRequestBodyAsJson
	 * @return
	 */
	@POST
	@Path("{datatable}/{apptableId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createDatatableEntry(
			@PathParam("datatable") final String datatable,
			@PathParam("apptableId") final Long apptableId,
			final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
				.createDatatableEntry(datatable, apptableId, null) //
				.withJson(apiRequestBodyAsJson) //
				.build();

		final CommandProcessingResult result = this.commandSourceWritePlatformService
				.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * @param datatable
	 * @param apptableId
	 * @param apiRequestBodyAsJson
	 * @return
	 * single entry updation 
	 */
	@PUT
	@Path("{datatable}/{apptableId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateDatatableEntryOnetoOne(
			@PathParam("datatable") final String datatable,
			@PathParam("apptableId") final Long apptableId,
			final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
				.updateDatatable(datatable, apptableId, null) //
				.withJson(apiRequestBodyAsJson) //
				.build();

		final CommandProcessingResult result = this.commandSourceWritePlatformService
				.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * @param datatable
	 * @param apptableId
	 * @param datatableId
	 * @param apiRequestBodyAsJson
	 * @return
	 * multi entry updation
	 */
	@PUT
	@Path("{datatable}/{apptableId}/{datatableId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateDatatableEntryOneToMany(
			@PathParam("datatable") final String datatable,
			@PathParam("apptableId") final Long apptableId,
			@PathParam("datatableId") final Long datatableId,
			final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
				.updateDatatable(datatable, apptableId, datatableId) //
				.withJson(apiRequestBodyAsJson) //
				.build();

		final CommandProcessingResult result = this.commandSourceWritePlatformService
				.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * @param datatable
	 * @param apptableId
	 * @return
	 */
	@DELETE
	@Path("{datatable}/{apptableId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteDatatableEntries(
			@PathParam("datatable") final String datatable,
			@PathParam("apptableId") final Long apptableId) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
				.deleteDatatable(datatable, apptableId, null) //
				.build();

		final CommandProcessingResult result = this.commandSourceWritePlatformService
				.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * @param datatable
	 * @param apptableId
	 * @param datatableId
	 * @return
	 * de
	 */
	@DELETE
	@Path("{datatable}/{apptableId}/{datatableId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteDatatableEntry(
			@PathParam("datatable") final String datatable,
			@PathParam("apptableId") final Long apptableId,
			@PathParam("datatableId") final Long datatableId) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteDatatable(datatable, apptableId, datatableId).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

}
