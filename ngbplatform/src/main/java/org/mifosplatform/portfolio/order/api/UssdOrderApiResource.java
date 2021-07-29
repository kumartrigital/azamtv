package org.mifosplatform.portfolio.order.api;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.cms.eventprice.service.EventPriceReadPlatformService;
import org.mifosplatform.cms.media.domain.MediaAsset;
import org.mifosplatform.cms.mediadetails.domain.MediaAssetRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGateway;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayRepository;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderUssdData;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderAddOnsReadPlaformService;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.provisioning.networkelement.service.NetworkElementReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Path("/order")
@Component
@Scope("singleton")
public class UssdOrderApiResource {
	private final static Logger logger = LoggerFactory.getLogger(UssdOrderApiResource.class);

	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("id", "cancelledStatus", "status", "contractPeriod", "nextBillDate", "flag", "currentDate",
					"plan_code", "units", "service_code", "allowedtypes", "data", "servicedata", "billing_frequency",
					"start_date", "contract_period", "billingCycle", "startDate", "invoiceTillDate", "orderHistory",
					"userAction", "ispaymentEnable", "paymodes", "orderServices", "orderDiscountDatas",
					"discountstartDate", "discountEndDate", "userName", "isAutoProvision"));

	private final String resourceNameForPermissions = "ORDER";
	private final PlatformSecurityContext context;
	private final PlanReadPlatformService planReadPlatformService;
	private final OrderReadPlatformService orderReadPlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final OrderAddOnsReadPlaformService orderAddOnsReadPlaformService;
	private final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<MediaAsset> toApiJsonSerializerMovie;
	private final DefaultToApiJsonSerializer<OrderUssdData> toApiJsonSerializerussd;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final OrderWritePlatformService orderWritePlatformService;
	private final HardwareAssociationReadplatformService associationReadplatformService;
	private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final CrmServices crmServices;
	private final OrderRepository orderRepository;
	private final NetworkElementReadPlatformService networkElementReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final MediaAssetRepository mediaAssetRepository;
	private final EventPriceReadPlatformService eventPriceReadPlatformService;
	private final PaymentGatewayRepository paymentGatewayRepository;

	@Autowired
	public UssdOrderApiResource(final PlatformSecurityContext context,
			final DefaultToApiJsonSerializer<OrderData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final OrderReadPlatformService orderReadPlatformService,
			final PlanReadPlatformService planReadPlatformService,
			final MCodeReadPlatformService mCodeReadPlatformService,
			final OrderAddOnsReadPlaformService orderAddOnsReadPlaformService,
			final OrderWritePlatformService orderWritePlatformService,
			final HardwareAssociationReadplatformService associationReadplatformService,
			final ClientServiceReadPlatformService clientServiceReadPlatformService,
			final OfficeReadPlatformService officeReadPlatformService, final CrmServices crmServices,
			final OrderRepository orderRepository,
			final NetworkElementReadPlatformService networkElementReadPlatformService,
			final ClientReadPlatformService clientReadPlatformService,
			final DefaultToApiJsonSerializer<OrderUssdData> toApiJsonSerializerussd,
			final DefaultToApiJsonSerializer<MediaAsset> toApiJsonSerializerMovie,
			final MediaAssetRepository mediaAssetRepository,
			final EventPriceReadPlatformService eventPriceReadPlatformService,
			final PaymentGatewayRepository paymentGatewayRepository) {

		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.planReadPlatformService = planReadPlatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		this.orderReadPlatformService = orderReadPlatformService;
		this.orderAddOnsReadPlaformService = orderAddOnsReadPlaformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.orderWritePlatformService = orderWritePlatformService;
		this.associationReadplatformService = associationReadplatformService;
		this.clientServiceReadPlatformService = clientServiceReadPlatformService;
		this.officeReadPlatformService = officeReadPlatformService;
		this.crmServices = crmServices;
		this.orderRepository = orderRepository;
		this.networkElementReadPlatformService = networkElementReadPlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
		this.toApiJsonSerializerussd = toApiJsonSerializerussd;
		this.toApiJsonSerializerMovie = toApiJsonSerializerMovie;
		this.mediaAssetRepository = mediaAssetRepository;
		this.eventPriceReadPlatformService = eventPriceReadPlatformService;
		this.paymentGatewayRepository = paymentGatewayRepository;

	}

	@GET
	@Path("{orderId}/status")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUssdDetailsByOrderID(@PathParam("orderId") final String orderId,
			@Context final UriInfo uriInfo) {

		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		OrderUssdData ussdData = null;

		if (orderId.length() == 15) {
			String serialNo = orderId.substring(0, 12);
			String movieCode = orderId.substring(13, 15);
			System.out.println("movie code " + movieCode);
			ClientData clientData = null;

			try {
				clientData = clientReadPlatformService.retrieveSearchClientId("serial_no", serialNo);
			} catch (Exception e) {
				ussdData = new OrderUssdData("INVALID_ORDER_ID", "Order ID is invalid format");
				return Response.status(HttpStatus.OK.value()).entity(ussdData).build();
			}
			MediaAsset movieData = mediaAssetRepository.findOneByOverView(movieCode);
			if (movieData == null) {
				ussdData = new OrderUssdData("INVALID_ORDER_ID", "Order ID is invalid format");
				return Response.status(HttpStatus.OK.value()).entity(ussdData).build();
			}
			Double Price = eventPriceReadPlatformService.findMoviePricingByMovieCode(movieData.getOverview());
			ussdData = new OrderUssdData("PENDING", "Payment for specified ID can be done", Price);
			return Response.status(HttpStatus.OK.value()).entity(ussdData).build();

		} else if (orderId.length() == 12) {
			ClientData clienthwdata = null;
			try {
				clienthwdata = clientReadPlatformService.retrieveSearchClientId("serial_no", orderId);
			} catch (Exception e) {
				ussdData = new OrderUssdData("INVALID_ORDER_ID", "Order ID is invalid format");
				return Response.status(HttpStatus.OK.value()).entity(ussdData).build();
			}

			ussdData = orderReadPlatformService.getOrderDetailsBySerialNo(orderId);
			return Response.status(HttpStatus.OK.value()).entity(ussdData).build();

		} else {
			ussdData = new OrderUssdData("INVALID_ORDER_ID", "Order ID is invalid format");
			return Response.status(HttpStatus.OK.value()).entity(ussdData).build();

		}
	}

	@POST
	@Path("/{orderId}/acquire/{referenceId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response postOrderUssd(@PathParam("orderId") final Long orderId,
			@PathParam("referenceId") String referenceId, final String apiRequestBodyAsJson,
			@Context final UriInfo uriInfo) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().orderUssd(orderId, referenceId)
				.withJson(apiRequestBodyAsJson).build();
		
		OrderUssdData ussdData = null;
		PaymentGateway paymentGateway = paymentGatewayRepository.findPaymentDetailsByPaymentId(referenceId);
		
		if (paymentGateway != null) {
			ussdData = new OrderUssdData("UnSuccessful", "Transaction UnSuccessful");
			return Response.status(422).entity(ussdData).build();
		}

		try {
			PaymentGateway paymentGatewayorder = new PaymentGateway();
			paymentGatewayorder.setPartyId(referenceId);
			paymentGatewayorder.setPaymentDate(new Date());
			paymentGatewayorder.setReceiptNo("USSD_" + referenceId);
			paymentGatewayorder.setSource("USSD");
			paymentGatewayorder.setPaymentId(referenceId);
			paymentGatewayorder.setStatus("Success");
			paymentGatewayorder.setRemarks("NOTHING_"+orderId);

			String serialNo = orderId.toString();
			
			if (serialNo.length() == 15) {
				logger.info("ussd tvod processing");
				String HWno = serialNo.substring(0, 12);
				paymentGatewayorder.setDeviceId(HWno);
				paymentGatewayorder.setType("USSD_pvod");
				paymentGatewayRepository.saveAndFlush(paymentGatewayorder);

			} else if (serialNo.length() == 12) {
				logger.info("ussd topup processing");
				paymentGatewayorder.setDeviceId(serialNo);
				paymentGatewayorder.setType("USSD_topup");
				paymentGatewayorder.setAmountPaid(new BigDecimal(1500.00));
				paymentGatewayRepository.saveAndFlush(paymentGatewayorder);
			}

			this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			ussdData = new OrderUssdData("Successful", "Transaction Successful");
			return null;
		}

		catch (Exception e) {
			return null;

		}

	}

}
