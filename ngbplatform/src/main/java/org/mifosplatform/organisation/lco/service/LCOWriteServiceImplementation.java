package org.mifosplatform.organisation.lco.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.billing.planprice.exceptions.PriceNotFoundException;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.lco.serialization.LCOCommandFromApiJsonDesrializer;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.jvtransaction.domain.JVTransaction;
import org.mifosplatform.portfolio.jvtransaction.domain.JVTransactionRepository;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.OrderPriceRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.plan.exceptions.PlanNotFundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class LCOWriteServiceImplementation implements LCOWritePlatformService {

	private final PlatformSecurityContext context;
	private final LCOCommandFromApiJsonDesrializer apiJsonDeserializer;
	private final JVTransactionRepository jVTransactionRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final OrderRepository orderRepository;
	private final OrderPriceRepository orderPriceRepository;
	private final OrderWritePlatformService orderWritePlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final PlanRepository planRepository;
	private final PriceRepository priceRepository;
	private final ClientReadPlatformService clientReadPlatformService;
	private final ConfigurationRepository configurationRepository;

	static JSONObject renewalDataJson = new JSONObject();
	private final static Logger logger = LoggerFactory.getLogger(LCOWriteServiceImplementation.class);

	@Autowired
	public LCOWriteServiceImplementation(PlatformSecurityContext context,
			LCOCommandFromApiJsonDesrializer apiJsonDesrializer, JVTransactionRepository jvTransactionRepository,
			FromJsonHelper fromApiJsonHelper, OrderRepository orderRepository,
			OrderPriceRepository orderPriceRepository, OrderWritePlatformService orderWritePlatformService,
			FromJsonHelper fromJsonHelper, PlanRepository planRepository, PriceRepository priceRepository,
			final ClientReadPlatformService clientReadPlatformService,
			final ConfigurationRepository configurationRepository) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDesrializer;
		this.jVTransactionRepository = jvTransactionRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.orderRepository = orderRepository;
		this.orderPriceRepository = orderPriceRepository;
		this.orderWritePlatformService = orderWritePlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.planRepository = planRepository;
		this.priceRepository = priceRepository;
		this.clientReadPlatformService = clientReadPlatformService;
		this.configurationRepository = configurationRepository;
	}

	@Override
	public CommandProcessingResult renewal(final JsonCommand command) {
		List<JVTransaction> jvTransactions = null;

		try {
			this.context.authenticatedUser();
			apiJsonDeserializer.validateForRenewal(command.json());

			final JsonArray lcoclientArray = command.arrayOfParameterNamed("lco").getAsJsonArray();
			jvTransactions = this.assembleDetails(lcoclientArray, command);
			this.jVTransactionRepository.save(jvTransactions);
			return new CommandProcessingResult(Long.valueOf(0));
		} catch (Exception e) {
			System.out.println(e);
			return CommandProcessingResult.empty();
		}
	}

	private List<JVTransaction> assembleDetails(JsonArray lcoClientArray, final JsonCommand command) {
		List<JVTransaction> jvTransactions = new ArrayList<JVTransaction>();
		JVTransaction jVTransaction = null;
		String dateFormat = command.stringValueOfParameterName("dateFormat");
		String[] lcoClients = null;
		lcoClients = new String[lcoClientArray.size()];
		if (lcoClientArray.size() > 0) {
			for (int i = 0; i < lcoClientArray.size(); i++) {
				lcoClients[i] = lcoClientArray.get(i).toString();
			}

			for (final String lcoClient : lcoClients) {
				final JsonElement element = this.fromApiJsonHelper.parse(lcoClient);
				final Long orderId = fromApiJsonHelper.extractLongNamed("orderId", element);
				Order order = this.orderRepository.findOne(orderId);
				final LocalDate startDate1 = fromApiJsonHelper.extractLocalDateNamed("startDate", element, dateFormat,
						Locale.getDefault());
				Date startDate = null;
				try {
					startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate1.toString());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startDate = org.apache.commons.lang3.time.DateUtils.addMonths(startDate, 1);

				order.setNextBillableDay(startDate);
				this.orderRepository.save(order);
				Plan plan = this.planRepository.findOne(order.getPlanId());
				if (plan == null)
					throw new PlanNotFundException();
				List<Price> priceList = new ArrayList<Price>();
				priceList = this.priceRepository.findplansByPlanID(plan.getId());
				if (priceList.size() == 0)
					throw new PriceNotFoundException("PriceNotFound for this plan");
				List<OrderPrice> orderPriceList = new ArrayList<OrderPrice>();

				orderPriceList = this.orderPriceRepository.findOrderList(order);
				for (OrderPrice op : orderPriceList) {
					op.setNextBillableDay(startDate);
					this.orderPriceRepository.save(op);
					final BigDecimal transAmount1 = op.getPrice();

					final Long clientId = fromApiJsonHelper.extractLongNamed("id", element);
					final LocalDate endDate = fromApiJsonHelper.extractLocalDateNamed("endDate", element, dateFormat,
							Locale.getDefault());
					final LocalDate jvDate = new LocalDate();
					jVTransaction = new JVTransaction(clientId, orderId, jvDate, startDate1, endDate, transAmount1);
					jvTransactions.add(jVTransaction);
				}
				JsonCommand renewalCommand = null;

				// {"description":"renewal","renewalPeriod":2,"priceId":1,"orderNo":"OR-000000015","planPoId":"MUSIC","dealPoId":"0","dateFormat":"dd
				// MMMM yyyy","disconnectionDate":"29 December 2020","locale":"en"}
				try {
					renewalDataJson.put("description", "renewal");
					renewalDataJson.put("renewalPeriod", 2);
					renewalDataJson.put("priceId", priceList.get(0).getId());
					renewalDataJson.put("orderNo", order.getOrderNo());
					renewalDataJson.put("planPoId", plan.getPlanCode());
					renewalDataJson.put("dealPoId", "0");
					renewalDataJson.put("dateFormat", "dd MMMM yyyy");
					String pattern = "dd MMMM yyyy";
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
					String disconnectionDate = simpleDateFormat.format(new Date());
					renewalDataJson.put("disconnectionDate", disconnectionDate);
					renewalDataJson.put("locale", "en");

					final JsonElement renewalElement = fromJsonHelper.parse(renewalDataJson.toString());
					renewalCommand = new JsonCommand(null, renewalDataJson.toString(), renewalElement, fromJsonHelper,
							null, null, null, null, null, null, null, null, null, null, null, null);

					this.orderWritePlatformService.renewalClientOrder(renewalCommand, order.getId());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// final BigDecimal transAmount = new
				// BigDecimal(fromApiJsonHelper.extractStringNamed("balanceAmount", element));

			}
		}

		return jvTransactions;
	}

	@Override
	public boolean clientBalanceCheck(Long clientId, Long orderId) {
		try {
			Configuration leaseChargeMonthlyConfiguration = this.configurationRepository
					.findOneByName(ConfigurationConstants.LEASE_STB_CHARGE_AMOUNT_PLANCODE);
			JSONObject object = null;

			object = new JSONObject(leaseChargeMonthlyConfiguration.getValue());

			ClientBalanceData clientBalanceData = clientReadPlatformService.findClientBalanceByClientId(clientId);
			BigDecimal orderCharge = BigDecimal.ZERO;
			Order order = orderRepository.findOne(orderId);
			List<OrderPrice> orderPrices = orderPriceRepository.findOrderList(order);

			for (OrderPrice orderPrice : orderPrices) {
				if (orderPrice.getChargeOwner().equalsIgnoreCase("self")) {
					orderCharge = orderPrice.getPrice();
				}
			}
			// return true if plan is FTA with free of cost 0
			Plan plan = this.planRepository.findOne(order.getPlanId());
			// if (plan!=null &&
			// !plan.getPlanCode().equalsIgnoreCase(object.getString("planCode"))) {
			if (plan != null) {
				if (orderCharge.compareTo(new BigDecimal(0)) == 0
						&& clientBalanceData.getBalanceAmount().compareTo(new BigDecimal(0)) == 0)
					return true;

				else if (clientBalanceData.getBalanceAmount().compareTo(new BigDecimal(0)) >= 0
						|| clientBalanceData.getBalanceAmount().abs().compareTo(orderCharge) < 0) {
					logger.info("renewal failed due client low balance :" + clientBalanceData.getClientId());
					return false;
				} else
					return true;

			} else {
				return true;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
