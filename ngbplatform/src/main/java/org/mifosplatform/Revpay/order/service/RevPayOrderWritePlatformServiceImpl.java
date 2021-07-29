package org.mifosplatform.Revpay.order.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.mifosplatform.Revpay.order.domain.RevPayOrderRepository;
import org.mifosplatform.Revpay.order.exception.OrderNotCreatedException;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.finance.officebalance.domain.OfficeBalance;
import org.mifosplatform.finance.officebalance.domain.OfficeBalanceRepository;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGateway;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.logistics.agent.api.ItemSaleAPiResource;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.organisation.priceregion.data.PriceRegionData;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class RevPayOrderWritePlatformServiceImpl implements RevPayOrderWritePlatformService {

	private final RevPayOrderRepository revPayOrderRepo;

	private final PaymentGatewayRepository paymentGatewayRepository;

	private final OrderWritePlatformService orderWritePlatformService;

	private final FromJsonHelper fromApiJsonHelper;

	private HttpServletResponse httpServletResponse;

	private final ItemSaleAPiResource itemSaleAPiResource;

	private final ItemDetailsRepository itemDetailsRepository;

	private final OfficeBalanceRepository officeBalanceRepository;

	private final OrderRepository orderRepository;

	private final PriceRepository priceRepository;

	private final ContractRepository contractRepository;

	private final PlanRepository planRepository;

	private final ClientServiceRepository clientServiceRepository;

	@Autowired
	public RevPayOrderWritePlatformServiceImpl(final RevPayOrderRepository revPayOrderRepo,
			OrderWritePlatformService orderWritePlatformService, FromJsonHelper fromApiJsonHelper,
			PaymentGatewayRepository paymentGatewayRepository, final ItemSaleAPiResource itemSaleAPiResource,
			final ItemDetailsRepository itemDetailsRepository, final OfficeBalanceRepository officeBalanceRepository,
			final OrderRepository orderRepository, final PriceRepository priceRepository,
			final ContractRepository contractRepository, final PlanRepository planRepository,
			final ClientServiceRepository clientServiceRepository) {
		this.revPayOrderRepo = revPayOrderRepo;
		this.orderWritePlatformService = orderWritePlatformService;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.paymentGatewayRepository = paymentGatewayRepository;
		this.itemSaleAPiResource = itemSaleAPiResource;
		this.itemDetailsRepository = itemDetailsRepository;
		this.officeBalanceRepository = officeBalanceRepository;
		this.orderRepository = orderRepository;
		this.priceRepository = priceRepository;
		this.contractRepository = contractRepository;
		this.planRepository = planRepository;
		this.clientServiceRepository = clientServiceRepository;

	}

	@Override
	public CommandProcessingResult createOrder(JsonCommand command) {
		JSONObject revorder = null;
		try {

			String type = command.stringValueOfParameterName("type");
			PaymentGateway paymentGateway = new PaymentGateway();

			if (type.equalsIgnoreCase("setupbox_payment")) {
				paymentGateway.setObsId(command.longValueOfParameterNamed("toOffice"));
				paymentGateway.setAmountPaid(new BigDecimal(command.stringValueOfParameterName("amount")));
				paymentGateway.setPaymentId(getTxid());
				paymentGateway.setPartyId(paymentGateway.getPaymentId());
				paymentGateway.setReceiptNo("RAVE_STB_" + paymentGateway.getPaymentId());
				paymentGateway.setStatus("intiated");
				paymentGateway.setPaymentDate(new Date());
				paymentGateway.setSource("REVPAY");
				paymentGateway.setRemarks("NOTHING");
				paymentGateway.setType(type);
				paymentGateway.setDeviceId(command.stringValueOfParameterName("itemsaleId"));
				paymentGatewayRepository.save(paymentGateway);
				revorder = new JSONObject();
				revorder.put("txid", paymentGateway.getPaymentId());
				revorder.put("revorder", "order created sucussfully");
				revorder.put("callbackUrl", "https://52.22.65.59:8877/ngbplatform/api/v1/revpay/orderlock/"
						+ paymentGateway.getPaymentId() + "/");
			}

			else if (type.equalsIgnoreCase("leaseverification_payment")) {
				paymentGateway.setObsId(command.longValueOfParameterNamed("toOffice"));
				paymentGateway.setAmountPaid(new BigDecimal(command.stringValueOfParameterName("amount")));
				paymentGateway.setPaymentId(getTxid());
				paymentGateway.setPartyId(paymentGateway.getPaymentId());
				paymentGateway.setReceiptNo("RAVE_STB_" + paymentGateway.getPaymentId());
				paymentGateway.setStatus("intiated");
				paymentGateway.setPaymentDate(new Date());
				paymentGateway.setSource("REVPAY");
				paymentGateway.setRemarks("NOTHING");
				paymentGateway.setType(type);
				paymentGateway.setDeviceId(command.stringValueOfParameterName("mobileNo"));
				paymentGatewayRepository.save(paymentGateway);
				revorder = new JSONObject();
				revorder.put("txid", paymentGateway.getPaymentId());
				revorder.put("revorder", "order created sucussfully");
				revorder.put("callbackUrl", "https://52.22.65.59:8877/ngbplatform/api/v1/revpay/orderlock/"
						+ paymentGateway.getPaymentId() + "/");
			}

			else if (type.equalsIgnoreCase("activation_Payment")) {
				revorder = new JSONObject();

				paymentGateway.setObsId(command.longValueOfParameterNamed("OfficeId"));
				paymentGateway.setAmountPaid(new BigDecimal(command.stringValueOfParameterName("amount")));
				paymentGateway.setPaymentId(getTxid());
				paymentGateway.setPartyId(paymentGateway.getPaymentId());
				paymentGateway.setReceiptNo("RAVE_STB_Activation" + paymentGateway.getPaymentId());
				paymentGateway.setStatus("intiated");
				paymentGateway.setPaymentDate(new Date());
				paymentGateway.setSource("REVPAY");
				paymentGateway.setRemarks("NOTHING");
				paymentGateway.setType(type);
				org.json.simple.JSONArray address = new org.json.simple.JSONArray();
				org.json.simple.JSONArray devices = new org.json.simple.JSONArray();
				org.json.simple.JSONArray plans = new org.json.simple.JSONArray();

				JSONObject activation = new JSONObject();
				JSONObject addressjsonBilling = new JSONObject();
				JSONObject addressjson = new JSONObject();
				JSONObject devicejson = new JSONObject();
				JSONObject purchasePlanjson = new JSONObject();
				JSONObject basePlanjson = new JSONObject();

				activation.put("forename", command.stringValueOfParameterNamed("forename"));
				activation.put("surname", command.stringValueOfParameterNamed("surname"));
				activation.put("gender", "male");
				activation.put("email", command.stringValueOfParameterNamed("email"));
				activation.put("mobile", command.stringValueOfParameterNamed("mobile"));
				activation.put("dob", 12101990);
				activation.put("officeId", 2);

				addressjson.put("addressNo", command.stringValueOfParameterNamed("addressNo"));
				addressjson.put("street", "");
				addressjson.put("city", command.stringValueOfParameterName("city"));
				addressjson.put("state", command.stringValueOfParameterName("state"));
				addressjson.put("country", command.stringValueOfParameterName("country"));
				addressjson.put("district", command.stringValueOfParameterName("district"));
				addressjson.put("zipCode", command.stringValueOfParameterName("zipcode"));
				addressjson.put("addressType", "PRIMARY");
				address.add(addressjson);

				addressjsonBilling.put("addressNo", command.stringValueOfParameterName("addressNo"));
				addressjsonBilling.put("street", "");
				addressjsonBilling.put("city", command.stringValueOfParameterName("city"));
				addressjsonBilling.put("state", command.stringValueOfParameterName("state"));
				addressjsonBilling.put("country", command.stringValueOfParameterName("country"));
				addressjsonBilling.put("district", command.stringValueOfParameterName("district"));
				addressjsonBilling.put("zipCode", command.stringValueOfParameterName("zipcode"));
				addressjsonBilling.put("addressType", "BILLING");
				address.add(addressjsonBilling);

				devicejson.put("deviceId", command.stringValueOfParameterName("stbNo"));

				devices.add(devicejson);

				plans.add(basePlanjson.put("planCode", "BASEPACK"));
				plans.add(purchasePlanjson.put("planCode", "PURCHASE"));

				activation.put("salutation", "Mr.");
				activation.put("address", address);
				activation.put("devices", devices);
				activation.put("plans", plans);
				paymentGateway.setType(type);
				paymentGateway.setReProcessDetail(activation.toString());
				paymentGateway.setDeviceId(command.stringValueOfParameterName("stbNo"));
				paymentGatewayRepository.save(paymentGateway);
				devices.clear();
				plans.clear();
				address.clear();
				revorder.put("txid", paymentGateway.getPaymentId());
				revorder.put("revorder", "order created sucussfully");
				revorder.put("callbackUrl", "https://52.22.65.59:8877/ngbplatform/api/v1/revpay/orderlock/"
						+ paymentGateway.getPaymentId() + "/");

			} else if (type.equalsIgnoreCase("account_topup")) {
				paymentGateway.setObsId(command.longValueOfParameterNamed("toOffice"));
				paymentGateway.setAmountPaid(new BigDecimal(command.stringValueOfParameterName("amount")));
				paymentGateway.setPaymentId(getTxid());
				paymentGateway.setPartyId(paymentGateway.getPaymentId());
				paymentGateway.setReceiptNo("RAVE_STB_" + paymentGateway.getPaymentId());
				paymentGateway.setStatus("intiated");
				paymentGateway.setPaymentDate(new Date());
				paymentGateway.setSource("REVPAY");
				paymentGateway.setRemarks("NOTHING");
				paymentGateway.setType(type);
				paymentGateway.setDeviceId(command.stringValueOfParameterName("toOffice"));
				paymentGatewayRepository.save(paymentGateway);
				revorder = new JSONObject();

				revorder.put("txid", paymentGateway.getPaymentId());
				revorder.put("revorder", "order created sucussfully");
				revorder.put("callbackUrl", "https://52.22.65.59:8877/ngbplatform/api/v1/revpay/orderlock/"
						+ paymentGateway.getPaymentId() + "/");

			} else if (type.equalsIgnoreCase("subscription_renewal")) {
				paymentGateway.setObsId(Long.parseLong(command.stringValueOfParameterName("clientId")));
				paymentGateway.setOfficeId(Long.parseLong(command.stringValueOfParameterName("officeId")));
				paymentGateway.setDeviceId(command.stringValueOfParameterName("stb"));
				paymentGateway.setPaymentDate(new Date());
				BigDecimal amountPaid = new BigDecimal(command.stringValueOfParameterName("amount"));
				paymentGateway.setAmountPaid(amountPaid);
				String Txid = getTxid();
				paymentGateway.setPaymentId(Txid);
				paymentGateway.setPartyId(Txid);
				paymentGateway.setReceiptNo("RAVEPAY_" + Txid);
				paymentGateway.setSource("RAVEPAY");
				paymentGateway.setStatus("Initiated");
				paymentGateway.setReffernceId(command.stringValueOfParameterName("refId"));
				paymentGateway.setType(type);
				paymentGateway.setRemarks("NOTHING");

				revorder = new JSONObject();
				revorder.put("txid", paymentGateway.getPaymentId());
				final Order order = this.orderRepository.findOne(Long.parseLong(paymentGateway.getReffernceId()));

				final Price price = this.priceRepository.findOne(order.getPlanId());
				Long contractId = (long) 0;

				if (price != null) {
					final String contractPeriod = price.getContractPeriod();
					Contract contract = this.contractRepository.findOneByContractId(contractPeriod);
					contractId = contract.getId();
				}

				JSONObject orderJson = new JSONObject();

				orderJson.put("priceId", order.getPlanId());
				orderJson.put("renewalPeriod", contractId);
				orderJson.put("priceId", order.getPlanId());
				orderJson.put("description", "Order Renewal By Redemption");
				orderJson.put("channel", "Revpay");

				paymentGateway.setReProcessDetail(orderJson.toString());
				paymentGatewayRepository.save(paymentGateway);

				revorder.put("revorder", "order created sucussfully");
				revorder.put("callbackUrl", "https://52.22.65.59:8877/ngbplatform/api/v1/revpay/orderlock/"
						+ paymentGateway.getPaymentId() + "/");
			}

			else if (type.equalsIgnoreCase("subscription_add")) {
				paymentGateway.setObsId(Long.parseLong(command.stringValueOfParameterName("clientId")));
				paymentGateway.setOfficeId(Long.parseLong(command.stringValueOfParameterName("officeId")));
				paymentGateway.setDeviceId(command.stringValueOfParameterName("stb"));
				paymentGateway.setPaymentDate(new Date());
				BigDecimal amountPaid = new BigDecimal(command.stringValueOfParameterName("amount"));
				paymentGateway.setAmountPaid(amountPaid);
				String Txid = getTxid();
				paymentGateway.setPaymentId(Txid);
				paymentGateway.setPartyId(Txid);
				paymentGateway.setReceiptNo("RAVEPAY_" + Txid);
				paymentGateway.setSource("RAVEPAY");
				paymentGateway.setStatus("Initiated");
				paymentGateway.setReffernceId(command.stringValueOfParameterName("refId"));
				paymentGateway.setType(type);
				paymentGateway.setRemarks("NOTHING");

				final JsonElement elementjson = fromApiJsonHelper.parse(command.json());

				JsonArray planCodes = fromApiJsonHelper.extractJsonArrayNamed("devices", elementjson);

				String planCode = null;
				ClientService clientService = clientServiceRepository
						.findClientServicewithClientId(paymentGateway.getObsId());
				for (JsonElement j : planCodes) {
					JsonCommand deviceComm = new JsonCommand(null, j.toString(), j, fromApiJsonHelper, null, null, null,
							null, null, null, null, null, null, null, null, null);
					planCode = deviceComm.stringValueOfParameterName("planCode");
					
					final Plan plan = this.planRepository.findwithPlanCode(planCode);
					
					Price price = priceRepository.findplansByPlanIdChargeOwnerSelf(plan.getId());
					
					JsonObject orderJson = new JsonObject();
					orderJson.addProperty("planid", plan.getId());
					orderJson.addProperty("planCode", plan.getPlanCode());
					orderJson.addProperty("plandescription", plan.getDescription());
					orderJson.addProperty("planpoid", 0);
					orderJson.addProperty("dealpoid", 0);
					orderJson.addProperty("paytermCode", price.getContractPeriod());
					orderJson.addProperty("contractperiod", plan.getDuration().getId());
					orderJson.addProperty("clientserviceid", clientService.getServiceId());
					orderJson.addProperty("billAlign", true);
					orderJson.addProperty("clientId", clientService.getClientId());
					orderJson.addProperty("locale", "en");
					orderJson.addProperty("dateFormat", "dd MMMM yyyy");
					String dateFormat = "dd MMMM yyyy";
					SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
					orderJson.addProperty("startdate", formatter.format(new Date()));
					paymentGateway.setReProcessDetail(orderJson.toString());
				}
				paymentGatewayRepository.save(paymentGateway);

				revorder = new JSONObject();
				revorder.put("txid", paymentGateway.getPaymentId());
				revorder.put("revorder", "order created sucussfully");
				revorder.put("callbackUrl", "https://52.22.65.59:8877/ngbplatform/api/v1/revpay/orderlock/"
						+ paymentGateway.getPaymentId() + "/");
			}

			else if (type.equalsIgnoreCase("redemption")) {
				paymentGateway.setDeviceId(command.stringValueOfParameterName("stbNo"));
				paymentGateway.setAmountPaid(new BigDecimal(command.stringValueOfParameterName("amount")));
				paymentGateway.setPaymentId(getTxid());
				paymentGateway.setPartyId(paymentGateway.getPaymentId());
				paymentGateway.setReceiptNo("RAVE_" + paymentGateway.getPaymentId());
				paymentGateway.setStatus("intiated");
				paymentGateway.setPaymentDate(new Date());
				paymentGateway.setSource("REVPAY");
				paymentGateway.setReffernceId(command.stringValueOfParameterName("clientId"));
				paymentGateway.setRemarks("NOTHING");
				paymentGateway.setType(type);
				paymentGatewayRepository.save(paymentGateway);
				revorder = new JSONObject();
				revorder.put("txid", paymentGateway.getPaymentId());
				paymentGatewayRepository.save(paymentGateway);
				revorder.put("revorder", "order created sucussfully");
				revorder.put("callbackUrl", "https://52.22.65.59:8877/ngbplatform/api/v1/revpay/orderlock/"
						+ paymentGateway.getPaymentId() + "/");
			}
			return new CommandProcessingResult(revorder);

		} catch (Exception e) {
			e.printStackTrace();
			throw new OrderNotCreatedException(e.getLocalizedMessage());
		}

	}

	public String getTxid() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println(timestamp);
		return String.valueOf(timestamp.getTime());
	}

	public String revTransactionStatus(Long txid) {
		String status = null;
		String revResponse = null;
		try {
			RestTemplate rest = new RestTemplate();

			String VERIFY_ENDPOINT = "https://api.ravepay.co/flwv3-pug/getpaidx/api/v2/verify";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			JSONObject revRequest = new JSONObject();
			revRequest.put("txref", txid);
			// FLWPUBK-acb0630ea1c150dabf363efa007d3a0b-X
			revRequest.put("SECKEY", "FLWSECK_TEST-09b25bed4e4027011c8d5613fc73945a-X");
			// revRequest.put("SECKEY", "FLWSECK-05da041a69e8b6ac206ae74f8f7c4bd8-X");

			HttpEntity<String> request = new HttpEntity<>(revRequest.toString(), headers);
			revResponse = rest.postForObject(VERIFY_ENDPOINT, request, String.class);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return revResponse.toString();

	}

	private void handleCodeDataIntegrityIssues(JsonCommand command, Exception dve) {
		throw new PlatformDataIntegrityException("error.msg.office.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

}
