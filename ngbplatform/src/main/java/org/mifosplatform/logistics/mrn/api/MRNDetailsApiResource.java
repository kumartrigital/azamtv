package org.mifosplatform.logistics.mrn.api;

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
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.service.ItemSaleReadPlatformService;
import org.mifosplatform.logistics.grv.data.GRVData;
import org.mifosplatform.logistics.grv.service.GRVReadPlatformService;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.mrn.data.InventoryTransactionHistoryData;
import org.mifosplatform.logistics.mrn.data.MRNDetailsData;
import org.mifosplatform.logistics.mrn.service.MRNDetailsReadPlatformService;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.voucher.service.VoucherReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("singleton")
@Path("/mrn")
public class MRNDetailsApiResource {

	
	
	private final Set<String> RESPONSE_PARAMETERS = new HashSet<String>(Arrays.asList("mrnId","movement","transactionDate","requestedDate","itemDescription","fromOffice","toOffice","orderdQuantity","receivedQuantity","status","officeId","officeName","parentId","movedDate"));
	private final static String RESOURCE_TYPE = "MRNDETAILS";
	private final static String RESOURCE = "MRNDETAILS";
	
	 private final  PlatformSecurityContext context;
	 private final ApiRequestParameterHelper apiRequestParameterHelper;
	 private final OfficeReadPlatformService officeReadPlatformService;
	 private final ItemSaleReadPlatformService agentReadPlatformService;
	 private final ToApiJsonSerializer<MRNDetailsData> apiJsonSerializer;
	 private final ToApiJsonSerializer<OfficeData>  officeApiJsonSerializer;
	 private final MRNDetailsReadPlatformService mrnDetailsReadPlatformService;
	 private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	 private final ToApiJsonSerializer<InventoryTransactionHistoryData> apiJsonSerializerForData;
	 private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	 private final GRVReadPlatformService gRVReadPlatformService;
	 private final VoucherReadPlatformService voucherReadPlatformService;

	@Autowired
	public MRNDetailsApiResource(final PlatformSecurityContext context, final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService
			,final MRNDetailsReadPlatformService mrnDetailsReadPlatformService, final ApiRequestParameterHelper apiRequestParameterHelper,
			final ToApiJsonSerializer<MRNDetailsData> apiJsonSerializer,final OfficeReadPlatformService officeReadPlatformService,
			final ToApiJsonSerializer<InventoryTransactionHistoryData> apiJsonSerializerForData,final ItemSaleReadPlatformService agentReadPlatformService,
			final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,final GRVReadPlatformService gRVReadPlatformService,
			final ToApiJsonSerializer<OfficeData>  officeApiJsonSerializer,final VoucherReadPlatformService voucherReadPlatformService) {
		
		this.context = context;
		this.apiJsonSerializer =  apiJsonSerializer;
		this.agentReadPlatformService=agentReadPlatformService;
		this.apiJsonSerializerForData = apiJsonSerializerForData;
		this.officeReadPlatformService = officeReadPlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.mrnDetailsReadPlatformService = mrnDetailsReadPlatformService;
		this.oneTimeSaleReadPlatformService=oneTimeSaleReadPlatformService;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.gRVReadPlatformService = gRVReadPlatformService;
		this.officeApiJsonSerializer=officeApiJsonSerializer;
		this.voucherReadPlatformService = voucherReadPlatformService;
	}
	
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveMRNDetails(@Context final UriInfo uriInfo ){
		context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final List<MRNDetailsData> mrnDetailsDatas = mrnDetailsReadPlatformService.retriveMRNDetails();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return apiJsonSerializer.serialize(settings,mrnDetailsDatas,RESPONSE_PARAMETERS);
	}
	

	@GET
	@Path("/view")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveMRNDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,
			         @QueryParam("offset") final Integer offset){
		
		context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final SearchSqlQuery searchItemDetails =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<MRNDetailsData> mrnDetailsDatas = mrnDetailsReadPlatformService.retriveMRNDetails(searchItemDetails);
		return apiJsonSerializer.serialize(mrnDetailsDatas);
	}
	
	@GET
	@Path("/view/{officeId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveMRNDetailsByOfficeId(@Context final UriInfo uriInfo ,@PathParam("officeId") Long officeId,  @QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,
			         @QueryParam("offset") final Integer offset){
		
		context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final SearchSqlQuery searchItemDetails =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<MRNDetailsData> mrnDetailsDatas = mrnDetailsReadPlatformService.retriveMRNDetailsByOfficeId(searchItemDetails,officeId);
		return apiJsonSerializer.serialize(mrnDetailsDatas);
	}
	
	
	@GET
	@Path("template")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String retriveMrnTemplate(@Context UriInfo uriInfo){
		context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final Collection<OfficeData> officeData = this.officeReadPlatformService.retrieveAllOfficesForDropdown();
		final Collection<ItemData> itemMasterData = this.oneTimeSaleReadPlatformService.retrieveItemData();
		final MRNDetailsData mrnDetailsData = new MRNDetailsData(officeData,itemMasterData);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
	}
	
	@Path("template/ids")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String retriveSerialNumbers(@Context final UriInfo uriInfo, @QueryParam("mrnId") final Long mrnId,
			@QueryParam("itemsaleId") final Long itemsaleId,@QueryParam("grvId") final Long grvId){
		
		context.authenticatedUser();
		if(mrnId!=null && mrnId > 0){
			
			final List<String> serialNumber = mrnDetailsReadPlatformService.retriveSerialNumbers(mrnId);
			final MRNDetailsData mrnDetailsData = new MRNDetailsData(serialNumber);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
		}
		if(itemsaleId!=null && itemsaleId > 0){
			
			final List<String> serialNumberForItems = mrnDetailsReadPlatformService.retriveSerialNumbersForItems(itemsaleId,null);
			final MRNDetailsData mrnDetailsData = new MRNDetailsData(serialNumberForItems);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
		}if(grvId!=null && grvId > 0){
			
			final List<String> serialNumberForItems = this.gRVReadPlatformService.retriveSerialNumbersForItems(grvId);
			final MRNDetailsData mrnDetailsData = new MRNDetailsData(serialNumberForItems);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
		}
		final Collection<MRNDetailsData> mrnIds = mrnDetailsReadPlatformService.retriveMrnIds();
		final List<MRNDetailsData> itemsaleIds = agentReadPlatformService.retriveItemsaleIds();
		final List<GRVData> grvIds = this.gRVReadPlatformService.retriveGrvIds();
		
		final MRNDetailsData mrnDetailsData = new MRNDetailsData(mrnIds,itemsaleIds,grvIds);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createMRN(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createMRN().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@Path("movemrn")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String moveMRN(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().moveMRN().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@Path("moveitemsale")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String moveitemsale(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().moveItemSale().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@Path("movegrv")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String moveGRV(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().moveGRV().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("/history")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveMMRNHistory(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit, 
			    @QueryParam("offset") final Integer offset){
		
		context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final SearchSqlQuery searchItemDetails =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<InventoryTransactionHistoryData> mrnDetailsDatas = mrnDetailsReadPlatformService.retriveHistory(searchItemDetails);
		return apiJsonSerializerForData.serialize(mrnDetailsDatas);
	
	}
	
	
	@GET
	@Path("movemrn/{mrnId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveSingleMrn(@Context final UriInfo uriInfo, @PathParam("mrnId") final Long mrnId){
		context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final InventoryTransactionHistoryData mrnDetailsDatas = mrnDetailsReadPlatformService.retriveSingleMovedMrn(mrnId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return apiJsonSerializerForData.serialize(settings,mrnDetailsDatas,RESPONSE_PARAMETERS);
	
	}
	
	@GET
	@Path("{mrnId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveSingleMrnDetail(@Context final UriInfo uriInfo , @PathParam("mrnId") final Long mrnId){
		context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final MRNDetailsData mrnDetailsDatas = mrnDetailsReadPlatformService.retriveSingleMrnDetail(mrnId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return apiJsonSerializer.serialize(settings,mrnDetailsDatas,RESPONSE_PARAMETERS);
	}
	
	
	@Path("movemrncarton")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String movemrncarton(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().movemrncarton().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	
	@Path("template/id")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String retriveCartoonNumbers(@Context final UriInfo uriInfo, @QueryParam("mrnId") final Long mrnId,
			@QueryParam("itemsaleId") final Long itemsaleId){
		
		context.authenticatedUser();
		if(mrnId!=null && mrnId > 0){
			final List<String> cartoonNumber = mrnDetailsReadPlatformService.retriveCartonNumber(mrnId);
			final MRNDetailsData mrnDetailsData = new MRNDetailsData(cartoonNumber,null);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
		}
		/*if(itemsaleId!=null && itemsaleId > 0){
			final List<String> CartoonNumbersForItems = mrnDetailsReadPlatformService.retriveCartoonNumbersForItems(itemsaleId,null);
			final MRNDetailsData mrnDetailsData = new MRNDetailsData(CartoonNumbersForItems);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			return apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
		}*/
		final Collection<MRNDetailsData> mrnIds = mrnDetailsReadPlatformService.retriveMrnIds();
		final List<MRNDetailsData> itemsaleIds = agentReadPlatformService.retriveItemsaleIds();
		final MRNDetailsData mrnDetailsData = new MRNDetailsData(mrnIds,itemsaleIds,null);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
	}
	    
	@GET
	@Path("searching/{officeId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveSearchOffice(@PathParam("officeId") final Long officeId, @QueryParam("officename") final String officename,@QueryParam("getAll") final boolean getAll, @Context final UriInfo uriInfo){
	    context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
    	final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForDropdown(officename,getAll);
    	final MRNDetailsData mrnDetailsData = new MRNDetailsData(offices,null);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
	}
	/*
	 * @GET
	 * 
	 * @Path("itemsalemove/{statusType}/{quantity}")
	 * 
	 * @Produces({MediaType.TEXT_PLAIN})
	 * 
	 * @Consumes({MediaType.APPLICATION_JSON}) public String
	 * retriveVoucher(@PathParam("statusType") final String
	 * status,@PathParam("quantity") final Long quantity) { List<VoucherData>
	 * voucherData = null; String csv = "/home/trigital/voucherData.csv"; try {
	 * voucherData = this.voucherReadPlatformService.getAllVoucherByStatus(status,
	 * quantity); String stringArray[] = voucherData.toArray(new
	 * String[voucherData.size()]); for (String k : stringArray) {
	 * System.out.println(k); } CSVWriter csvWriter = new CSVWriter(new
	 * FileWriter(csv)); csvWriter.writeNext(stringArray); csvWriter.close();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return "csv file"; }
	 * 
	 */
}
