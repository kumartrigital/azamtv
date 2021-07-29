package org.mifosplatform.organisation.voucher.api;

import java.util.ArrayList;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.domain.ItemSale;
import org.mifosplatform.logistics.agent.domain.ItemSaleRepository;
import org.mifosplatform.logistics.agent.exception.ItemSaleIdNotFoundException;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.voucher.data.ExportVoucherData;
import org.mifosplatform.organisation.voucher.data.VoucherData;
import org.mifosplatform.organisation.voucher.data.VoucherPinConfigValueData;
import org.mifosplatform.organisation.voucher.data.VoucherRequestData;
import org.mifosplatform.organisation.voucher.domain.ExportVoucherRepository;
import org.mifosplatform.organisation.voucher.domain.VoucherDetailsRepository;
import org.mifosplatform.organisation.voucher.exception.RequestedQuantityNotFoundException;
import org.mifosplatform.organisation.voucher.service.VoucherReadPlatformService;
import org.mifosplatform.organisation.voucher.service.VoucherWritePlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

/**
 * The class <code>VoucherPinApiResource</code> is developed for Generating the
 * Vouchers. Using this voucher Subscriber/client can Pay his due amount (or)
 * pay money for Pre-paid Plans.
 * <p>
 * A <code>VoucherPinApiResource</code> includes methods for Generating the
 * Vouchers and Downloading the Vouchers List.
 * 
 * @author ashokreddy
 * @author rakesh
 */

@Path("/vouchers")
@Component
@Scope("singleton")
public class VoucherPinApiResource {

	/**
	 * The set of parameters that are supported in response for {@link CodeData}
	 */
	private static final Set<String> RESPONSE_PARAMETERS = new HashSet<String>(Arrays.asList("id", "batchName",
			"batchDescription", "length", "beginWith", "pinCategory", "pinType", "quantity", "serialNo", "expiryDate",
			"dateFormat", "pinValue", "pinNO", "pinExtention", "unitPrice","locale", "chargeAmount", "voucherData"));

	private static String resourceNameForPermissions = "VOUCHER";
	private static String resourceNameFordownloadFilePermissions = "DOWNLOAD_FILE";

	private final PlatformSecurityContext context;
	private final VoucherReadPlatformService readPlatformService;
	private final DefaultToApiJsonSerializer<VoucherData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService writePlatformService;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	private final VoucherWritePlatformService voucherWritePlatformService;
	private final ItemSaleRepository itemSaleRepository;
	private final ExportVoucherRepository exportVoucherRepository;
	private final ItemReadPlatformService itemReadPlatformService;
	private final PlanRepository planRepository;
	private final PriceRepository priceRepository;
	private final VoucherDetailsRepository voucherDetailsRepository;


	@Autowired
	public VoucherPinApiResource(final PlatformSecurityContext context,
			final VoucherReadPlatformService readPlatformService,
			final DefaultToApiJsonSerializer<VoucherData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService writePlatformService,
			final OfficeReadPlatformService officeReadPlatformService,
			final MCodeReadPlatformService mCodeReadPlatformService,
			final VoucherWritePlatformService voucherWritePlatformService, final ItemSaleRepository itemSaleRepository,
			final ExportVoucherRepository exportVoucherRepository,
			final ItemReadPlatformService itemReadPlatformService,
			final PlanRepository planRepository,
			final PriceRepository priceRepository,
			final VoucherDetailsRepository voucherDetailsRepository) {

		this.context = context;
		this.readPlatformService = readPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.writePlatformService = writePlatformService;
		this.officeReadPlatformService = officeReadPlatformService;
		this.voucherWritePlatformService = voucherWritePlatformService;
		this.itemSaleRepository = itemSaleRepository;
		this.exportVoucherRepository = exportVoucherRepository;
		this.itemReadPlatformService = itemReadPlatformService;
		this.planRepository = planRepository;
		this.priceRepository = priceRepository;
		this.voucherDetailsRepository = voucherDetailsRepository;

	}

	/**
	 * This method <code>createVoucherBatch</code> is Used for Creating a
	 * Batch/Group with specify the characteristic. Like Name/Description of Group
	 * and length of the VoucherPins in Group, Category of the
	 * VoucherPin(Numeric/Alphabetic/AlphaNumeric) in Group, Starting String of
	 * VoucherPin in Group, Quantity of VoucherPins in Group, Expire Date of
	 * Vouchers in the Group , Value of VoucherPin etc..
	 * 
	 * Note: using this method we didn't Generate VoucherPins.
	 * 
	 * @param requestData Containg input data in the Form of JsonObject.
	 * @return
	 */

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createVoucherBatch(final String requestData) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createVoucherGroup().withJson(requestData)
				.build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * This method <code>retrieveTemplate</code> used for Retrieving the all
	 * mandatory/necessary data For creating a VoucherPin Group/Batch.
	 * 
	 * @param uriInfo Containing Url information
	 * @return
	 */
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveTemplate(@Context final UriInfo uriInfo,
			@QueryParam("isBatchTemplate") final String isBatchTemplate) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final List<EnumOptionData> pinCategoryData = this.readPlatformService.pinCategory();
		final List<EnumOptionData> pinTypeData = this.readPlatformService.pinType();
		final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOffices();
		final VoucherPinConfigValueData voucherPinConfigValueData = this.readPlatformService
				.getVoucherPinConfigValues(ConfigurationConstants.CONFIG_VOUCHERPIN_VALUES);
		final Collection<MCodeData> valueMCodeDatas= this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.VOUCHER_VALUE);
		final VoucherData voucherData = new VoucherData(pinCategoryData, pinTypeData, offices,
				voucherPinConfigValueData, valueMCodeDatas);

		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());

		return this.toApiJsonSerializer.serialize(settings, voucherData, RESPONSE_PARAMETERS);
	}

	@GET
	@Path("cancel/template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrievevoucherCancelTemplate(@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final Collection<MCodeData> reasondatas = this.mCodeReadPlatformService
				.getCodeValue(CodeNameConstants.CODE_VOUCHER_CANCEL_REASON);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		final VoucherData voucherData = new VoucherData(reasondatas);
		return this.toApiJsonSerializer.serialize(settings, voucherData, RESPONSE_PARAMETERS);
	}

	/**
	 * This method <code>retrieveVoucherGroups</code> used for Retrieving the All
	 * Voucherpins Data.
	 * 
	 * @param uriInfo Containing Url information
	 * @return
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveVoucherGroups(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
		      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final SearchSqlQuery searchVouchers = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<VoucherData> randomGenerator = this.readPlatformService.getAllData(searchVouchers);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(randomGenerator);
	}

	/**
	 * This method <code>retrieveVoucherGroups</code> used for Retrieving the All
	 * Voucherpin Groups/Batch wise Data.
	 * 
	 * @param uriInfo Containing Url information
	 * @return
	 */
	@Path("voucherslist/{id}")
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveVouchersByid(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,
			@QueryParam("statusType") final String statusType, @PathParam("id") final Long id) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final SearchSqlQuery searchVoucher = SearchSqlQuery.forSearch(sqlSearch, offset, limit);
		final Page<VoucherData> randomGenerator = this.readPlatformService.getAllVoucherById(searchVoucher, statusType,
				id);
		return this.toApiJsonSerializer.serialize(randomGenerator);
	}

	@Path("voucherslistbystatus")
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveVouchersByStatus(@Context final UriInfo uriInfo,
			@QueryParam("statusType") final String statusType, @QueryParam("limit") final Long quantity) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final List<VoucherData> randomGenerator = this.readPlatformService.getAllVoucherByStatus(statusType, quantity);
		return this.toApiJsonSerializer.serialize(randomGenerator);
	}

	/**
	 * This method <code>retrieveVoucherPinList</code> Used to retrieve the
	 * VoucherPins list of a Voucher Group/Batch based on batchId. We can get the
	 * Data in the Format of .csv(comma separated value).
	 * 
	 * @param batchId Voucher Group/Batch id value.
	 * @param uriInfo Containing Url information
	 * @return
	 */
	@GET
	@Path("{batchId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON, "application/x-msdownload", "application/vnd.ms-excel", "application/pdf",
			"text/html" })
	public Response retrieveVoucherPinList(@PathParam("batchId") final Long batchId, @Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(resourceNameFordownloadFilePermissions);
		final StreamingOutput result = this.readPlatformService.retrieveVocherDetailsCsv(batchId);
		return Response.ok().entity(result).type("application/x-msdownload")
				.header("Content-Disposition", "attachment;filename=" + "Vochers_" + batchId + ".csv").build();
	}

	/**
	 * This method <code>generateVoucherPins</code> Used for Generating VoucherPins.
	 * We are passing Group/Batch Id as Parameter, Based on this batchId we can get
	 * the Details of a Batch/Group. like quantity,length,type etc...
	 * 
	 * @param batchId Voucher Group/Batch id value.
	 * @param uriInfo Containing Url information
	 * @return
	 */

	@POST
	@Path("{batchId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String generateVoucherPins(@PathParam("batchId") final Long batchId, @Context final UriInfo uriInfo) {

		final JsonObject object = new JsonObject();
		object.addProperty("batchId", batchId);
		final CommandWrapper commandRequest = new CommandWrapperBuilder().generateVoucherPin(batchId)
				.withJson(object.toString()).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@GET
	@Path("verify")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveVoucherPinDetails(@QueryParam("pinNumber") final String pinNumber,
			@Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		String pinType = this.voucherDetailsRepository.findVoucherType(pinNumber);
		List<VoucherData> voucherData = this.readPlatformService.retrivePinDetails(pinNumber, pinType);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, voucherData, RESPONSE_PARAMETERS);
	}

	@PUT
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateVoucherPins(@PathParam("id") final Long id, final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateVoucherPin(id)
				.withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@POST
	@Path("delete/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteVoucherPins(@PathParam("id") final Long id, final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteVoucherPin(id)
				.withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@PUT
	@Path("cancel/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String cancelVoucherPin(@PathParam("id") final Long id, final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().cancelVoucherPin(id)
				.withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}
	/*
	 * @GET
	 * 
	 * @Path("searchingbypurchaseno")
	 * 
	 * @Consumes({ MediaType.APPLICATION_JSON })
	 * 
	 * @Produces({ MediaType.APPLICATION_JSON }) public String
	 * retrieveVoucherByPurchaseNo(@QueryParam("query") final String
	 * purchaseNo,@Context final UriInfo uriInfo) {
	 * 
	 * context.authenticatedUser().validateHasReadPermission(
	 * resourceNameForPermissions); final List<VoucherData> voucherData =
	 * this.readPlatformService.getVocherDetailsByPurchaseNo(purchaseNo); return
	 * this.toApiJsonSerializer.serialize(voucherData); }
	 */

	/*
	 * @GET
	 * 
	 * @Path("batchName/{batchName}")
	 * 
	 * @Consumes({ MediaType.APPLICATION_JSON })
	 * 
	 * @Produces({ MediaType.APPLICATION_JSON, "application/x-msdownload",
	 * "application/vnd.ms-excel", "application/pdf", "text/html" }) public Response
	 * retrieveVoucherPinListByBatchName(@PathParam("batchName") final String
	 * batchName,
	 * 
	 * @Context final UriInfo uriInfo) {
	 * 
	 * context.authenticatedUser().validateHasReadPermission(
	 * resourceNameFordownloadFilePermissions); final StreamingOutput result =
	 * this.readPlatformService.retrieveVocherDetailsCsvByBatchName(batchName);
	 * System.out.println("Streaming output ::" + Response.ok().entity(result));
	 * return Response.ok().entity(result).type("application/x-msdownload")
	 * .header("Content-Disposition", "attachment;filename=" + "Vochers_" +
	 * batchName + ".csv").build(); }
	 */
	/* GuruNadh */
	@PUT
	@Path("movevoucher/{officeid}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String moveVoucher(@PathParam("officeid") final Long officeId, String apiRequestBodyAsJson)
			throws JSONException {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		Long newStatuscount = null;
		Long reqQuantity = null;
		Boolean isProduct = false;
		try {
			JSONObject jsonObject = new JSONObject(apiRequestBodyAsJson);
			Long saleRefNo = jsonObject.getLong("saleRefNo");
			ItemSale itemSale = itemSaleRepository.findOne(saleRefNo);
			reqQuantity = itemSale.getOrderQuantity();
			Long fromOffice = jsonObject.getLong("fromOffice");
			ItemData itemData = this.itemReadPlatformService.retrieveSingleItemDetails(null, itemSale.getItemId(), null,
					false);
			List<String> planList = new ArrayList<String>();
			planList = this.planRepository.findAllPlanCode();
			if(planList.contains(itemData.getItemCode())) {
				//if (itemData.getItemCode().equalsIgnoreCase("DAF") || itemData.getItemCode().equalsIgnoreCase("DAFT")) {
				isProduct = true;
				Plan plan = this.planRepository.findwithPlanCode(itemData.getItemCode());
				Price price = this.priceRepository.findplansByPlanIdChargeOwnerSelf(plan.getId());	
				if(plan!=null) {
				newStatuscount = this.readPlatformService.retriveQuantityByStatus("NEW", fromOffice,
						null,price.getId(), isProduct);
				}else {
					throw new ItemNotFoundException(null,itemData.getItemCode());
				}
			} else {
				newStatuscount = this.readPlatformService.retriveQuantityByStatus("NEW", fromOffice,
						itemSale.getUnitPrice(), null,isProduct);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (newStatuscount >= reqQuantity) {
			final CommandWrapper commandRequest = new CommandWrapperBuilder().moveVoucher(officeId)
					.withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
		} else {
			throw new RequestedQuantityNotFoundException();

		}

	}

	/* GuruNadh */
	@POST
	@Path("export/{saleRefId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public String exportVoucher(@PathParam("saleRefId") final Long saleRefId, String apiRequestBodyAsJson)
			throws JSONException {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final CommandWrapper commandRequest = new CommandWrapperBuilder().exportVoucher(saleRefId)
				.withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);

	}

	/* GuruNadh */
	@GET
	@Path("requestdetails/{saleRefId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getvoucherRequestDetails(@PathParam("saleRefId") final Long saleRefId,
			@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		ItemSale itemSale = itemSaleRepository.findOne(saleRefId);
		if (itemSale == null) {
			throw new ItemSaleIdNotFoundException(saleRefId);
		}
		final VoucherRequestData voucher = this.readPlatformService.retrieveVocherRequestDetails(saleRefId);
		return this.toApiJsonSerializer.serialize(voucher);
	}

	/* GuruNadh */
	@GET
	@Path("exportrequestdetails/{officeId}/{saleReqNo}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveExportRequestDeatils(@PathParam("officeId") final Long officeId,
			@PathParam("saleReqNo") final Long salereqNo, @Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		/*
		 * ExportVoucherData data =
		 * this.exportVoucherRepository.findByRequestBy(officeId);
		 * System.out.print(data);
		 */
		final List<ExportVoucherData> exportVoucher = this.readPlatformService.retrieveExportRequestDetails(officeId ,salereqNo);
		System.out.println(exportVoucher.toString());
		
		return this.toApiJsonSerializer.serializeTime(exportVoucher);
	}

	/* GuruNadh */
	@GET
	@Path("download/{requestId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveexportVoucherDetails(@PathParam("requestId") final String requestId,
			@Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		/*
		 * ExportVoucherData data =
		 * this.exportVoucherRepository.findByRequestId(requestId);
		 * System.out.print(data);
		 */
		final ExportVoucherData exportVoucher = this.readPlatformService.exportVoucherDetails(requestId);
		return this.toApiJsonSerializer.serialize(exportVoucher);
	}

}
