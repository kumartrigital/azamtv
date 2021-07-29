package org.mifosplatform.logistics.grn.api;

import java.util.Arrays;
import java.util.Collection;
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
import org.mifosplatform.logistics.grn.data.GrnPoNoData;
import org.mifosplatform.logistics.grn.service.GrnReadPlatformService;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.logistics.itemdetails.data.InventoryGrnData;
import org.mifosplatform.logistics.supplier.data.SupplierData;
import org.mifosplatform.logistics.supplier.service.SupplierReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/grn")
@Component
@Scope("singleton")
public class GrnApiResource {
	
	private final Set<String> RESPONSE_DATA_PONO_NUMBER_PARAMETERS = new HashSet<String>(Arrays.asList("purchageNo"));
	private final Set<String> RESPONSE_DATA_GRN_DETAILS_PARAMETERS = new HashSet<String>(Arrays.asList("id", "purchaseDate", "supplierId",
			                        "itemMasterId","orderdQuantity", "receivedQuantity","id", "itemMasterId", "serialNumber", "grnId","provisioningSerialNumber",
			                        "quality", "status","warranty", "remarks"));
	
	private final String resourceNameForGrnPermissions = "GRN";
	private final PlatformSecurityContext context;
	private final ItemReadPlatformService itemReadPlatformService;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final SupplierReadPlatformService supplierReadPlatformService;
	private final GrnReadPlatformService inventoryGrnReadPlatformService;
	private final DefaultToApiJsonSerializer<InventoryGrnData> toApiJsonSerializerForGrn;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
    private final OfficeRepository officeRepository;
	
	@Autowired
	public GrnApiResource(final PlatformSecurityContext context,ApiRequestParameterHelper apiRequestParameterHelper,
			PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final GrnReadPlatformService inventoryGrnReadPlatformService,
			final DefaultToApiJsonSerializer<InventoryGrnData> toApiJsonSerializerForGrn,final OfficeReadPlatformService officeReadPlatformService,
			 final ItemReadPlatformService itemReadPlatformService,
			 final SupplierReadPlatformService supplierReadPlatformService,final OfficeRepository officeRepository) {
		
		this.context=context;
	    this.toApiJsonSerializerForGrn = toApiJsonSerializerForGrn;
	    this.apiRequestParameterHelper = apiRequestParameterHelper;
	    this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	    this.inventoryGrnReadPlatformService = inventoryGrnReadPlatformService;
	    this.officeReadPlatformService = officeReadPlatformService;
	    this.itemReadPlatformService = itemReadPlatformService;
	    this.supplierReadPlatformService = supplierReadPlatformService;
	    this.officeRepository = officeRepository;
	}

	/*
	 * this method is for storing GRN details into b_grn table
	 * */
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String addGrnDetails(final String jsonRequestBody){
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createGrn().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializerForGrn.serialize(result);
	}
	
	@PUT
	@Path("{grnId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String updateGrnDetails(@PathParam("grnId") final Long grnId,final String jsonRequestBody){
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().editGrn(grnId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializerForGrn.serialize(result);
	}
	
	@PUT
	@Path("{grnId}/orderstatus")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String updateOrderStatus(@PathParam("grnId") final Long grnId,final String jsonRequestBody){
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateGrnOrderStatus(grnId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializerForGrn.serialize(result);
	}
	
	
	@GET
	@Path("{grnId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveGrnDetails(@PathParam("grnId") final Long grnId,@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForGrnPermissions);
		InventoryGrnData inventoryGrnData = this.inventoryGrnReadPlatformService.retriveGrnDetailTemplate(grnId);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerForGrn.serialize(settings, inventoryGrnData, RESPONSE_DATA_GRN_DETAILS_PARAMETERS);
		
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveGrnDetailsPaginate(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch, 
			             @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForGrnPermissions); 
		final SearchSqlQuery searchGrn =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		Page<InventoryGrnData> inventoryGrnData  = this.inventoryGrnReadPlatformService.retriveGrnDetails(searchGrn);
		return this.toApiJsonSerializerForGrn.serialize(inventoryGrnData);
	
	}
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String getGrnTemplate(@Context final UriInfo uriInfo) {
		
		 context.authenticatedUser().validateHasReadPermission(resourceNameForGrnPermissions);
		 List<SupplierData> supplierData = this.supplierReadPlatformService.retrieveSupplier();
		 Collection<OfficeData> officeData = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
		 //List<ItemData> itemData = this.itemReadPlatformService.retrieveAllItems();
		 InventoryGrnData inventoryGrnData =  new InventoryGrnData(/*itemData,*/officeData,supplierData);
		 final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializerForGrn.serialize(settings, inventoryGrnData, RESPONSE_DATA_GRN_DETAILS_PARAMETERS);
	}
	
	@GET
	@Path("searching")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveSearchsupplier(@QueryParam("supplierDescription") final String supplierDescription, @Context final UriInfo uriInfo){
	    context.authenticatedUser().validateHasReadPermission(resourceNameForGrnPermissions);
    	List<SupplierData> supplierData = this.supplierReadPlatformService.retrieveSupplierForDropdown(supplierDescription);
    	InventoryGrnData inventoryGrnData =  new InventoryGrnData(null,supplierData);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializerForGrn.serialize(settings, inventoryGrnData, RESPONSE_DATA_GRN_DETAILS_PARAMETERS);
	}
	
	@GET
	@Path("{officeId}/{orderStatus}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveGrnByOfficeIdAndOrderStatus(@PathParam("officeId") final Long officeId,@PathParam("orderStatus") final Integer orderStatus,@Context final UriInfo uriInfo){
	    context.authenticatedUser().validateHasReadPermission(resourceNameForGrnPermissions);
		List<InventoryGrnData> inventoryGrnData  = this.inventoryGrnReadPlatformService.retriveGrnDetailsByofficeIdAndOrderStatus(officeId,orderStatus);
		return this.toApiJsonSerializerForGrn.serialize(inventoryGrnData);
    }
	 
	@GET
	@Path("getgrnby/{officeId}/{itemType}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveGrnByOfficeIdAndItemType(@PathParam("officeId") final Long officeId,@PathParam("itemType") final Long itemType,@Context final UriInfo uriInfo){
	    context.authenticatedUser().validateHasReadPermission(resourceNameForGrnPermissions);
	    final Office clientOffice = this.officeRepository.findOne(officeId);
	    if (clientOffice == null) { throw new OfficeNotFoundException(officeId); }
		List<InventoryGrnData> inventoryGrnData  = this.inventoryGrnReadPlatformService.retriveGrnDetailsByofficeIdAndItemType(officeId,itemType);
		return this.toApiJsonSerializerForGrn.serialize(inventoryGrnData);
    }
	 
	@GET
	@Path("searchinggrnbypono")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveGrnByPoNo(@QueryParam("query") final String purchaseNo,@Context final UriInfo uriInfo){
	    context.authenticatedUser().validateHasReadPermission(resourceNameForGrnPermissions);
	    InventoryGrnData inventoryGrnData  = this.inventoryGrnReadPlatformService.retriveAllGrnDetailsByPoNo(purchaseNo);
		return this.toApiJsonSerializerForGrn.serialize(inventoryGrnData);
    }
	
	
	@GET
	@Path("searchingforpono")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrivePoNoListOnKeyStroke(@QueryParam("query") final String purchaseNo, @Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForGrnPermissions);
		List<String> poNoList = this.inventoryGrnReadPlatformService
				.getPoNoListOnKeyStroke(purchaseNo);
		GrnPoNoData grnPoNoData = new GrnPoNoData(poNoList);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializerForGrn.serialize(grnPoNoData);
	}

}
