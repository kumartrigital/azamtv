package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.data.ProcessDate;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.finance.chargeorder.domain.Charge;
import org.mifosplatform.finance.chargeorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.finance.chargeorder.serialization.ChargingOrderCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformServiceImpl;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.slabRate.service.SlabRateWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ChargingCustomerOrders {

	private final static Logger logger = LoggerFactory.getLogger(ChargingCustomerOrders.class);

	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final GenerateChargesForOrderService generateChargesForOrderService;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final ChargingOrderCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ConfigurationRepository globalConfigurationRepository;
	private final OrderReadPlatformService orderReadPlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final PlanRepository planRepository;
	private final SlabRateWritePlatformService slabRateWritePlatformService;
	private final OrderWritePlatformService orderWritePlatformService;
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public ChargingCustomerOrders(final ChargingOrderReadPlatformService chargingOrderReadPlatformService,
			final GenerateChargesForOrderService generateChargesForOrderService,
			final ChargingOrderWritePlatformService chargingOrderWritePlatformService,
			final ChargingOrderCommandFromApiJsonDeserializer apiJsonDeserializer,
			final ConfigurationRepository globalConfigurationRepository, final BillItemRepository billItemRepository,
			final OrderReadPlatformService orderReadPlatformService, final FromJsonHelper fromJsonHelper,
			final PlanRepository planRepository, final SlabRateWritePlatformService slabRateWritePlatformService,
			final @Lazy OrderWritePlatformService orderWritePlatformService, final FromJsonHelper fromApiJsonHelper) {

		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.generateChargesForOrderService = generateChargesForOrderService;
		this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.globalConfigurationRepository = globalConfigurationRepository;
		this.orderReadPlatformService = orderReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.planRepository = planRepository;
		this.slabRateWritePlatformService = slabRateWritePlatformService;
		this.orderWritePlatformService = orderWritePlatformService;
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	public CommandProcessingResult createNewCharges(JsonCommand command) {

		try {
			// validation not written
			this.apiJsonDeserializer.validateForCreate(command.json());
			LocalDate processDate = ProcessDate.fromJson(command);
			List<BillItem> invoice = this.invoicingSingleClient(command.entityId(), processDate);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(invoice.get(0).getId()).build();
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	public List<BillItem> invoicingSingleClient(Long clientId, LocalDate processDate) {

		LocalDate initialProcessDate = processDate;
		Date nextBillableDate = null;
		// Get list of qualified orders of customer
		List<BillingOrderData> billingOrderDatas = chargingOrderReadPlatformService.retrieveOrderIds(clientId,
				processDate);

		if (billingOrderDatas.size() != 0) {

			boolean prorataWithNextBillFlag = this
					.checkInvoiceConfigurations(ConfigurationConstants.CONFIG_PRORATA_WITH_NEXT_BILLING_CYCLE);
			Map<String, List<Charge>> groupOfCharges = new HashMap<String, List<Charge>>();

			for (BillingOrderData billingOrderData : billingOrderDatas) {

				nextBillableDate = billingOrderData.getNextBillableDate();
				if (prorataWithNextBillFlag && ("Y".equalsIgnoreCase(billingOrderData.getBillingAlign()))
						&& billingOrderData.getInvoiceTillDate() == null) {
					LocalDate alignEndDate = new LocalDate(nextBillableDate).dayOfMonth().withMaximumValue();
					if (!processDate.toDate().after(alignEndDate.toDate()))
						processDate = alignEndDate.plusDays(2);
				} else {
					processDate = initialProcessDate;
				}
				while (processDate.toDate().after(nextBillableDate)
						|| processDate.toDate().compareTo(nextBillableDate) == 0) {

					groupOfCharges = chargeLinesForServices(billingOrderData, clientId, processDate, groupOfCharges);

					if (!groupOfCharges.isEmpty()
							&& groupOfCharges.containsKey(billingOrderData.getOrderId().toString())) {
						List<Charge> charges = groupOfCharges.get(billingOrderData.getOrderId().toString());
						nextBillableDate = new LocalDate(charges.get(charges.size() - 1).getEntDate()).plusDays(1)
								.toDate();
					} else if (!groupOfCharges.isEmpty()
							&& groupOfCharges.containsKey(billingOrderData.getChargeCode())) {
						List<Charge> charges = groupOfCharges.get(billingOrderData.getChargeCode());
						nextBillableDate = new LocalDate(charges.get(charges.size() - 1).getEntDate()).plusDays(1)
								.toDate();
					}
				}

			}

			return this.generateChargesForOrderService.createBillItemRecords(groupOfCharges, clientId);

		} else {
			//throw new BillingOrderNoRecordsFoundException();
			List<BillItem> billItem = new ArrayList<BillItem>();
			return billItem;
		}
	}

	public Map<String, List<Charge>> chargeLinesForServices(BillingOrderData billingOrderData, Long clientId,
			LocalDate processDate, Map<String, List<Charge>> groupOfCharges) {

		// Get qualified order complete details
		List<BillingOrderData> chargeServices = this.chargingOrderReadPlatformService.retrieveBillingOrderData(clientId,
				processDate, billingOrderData.getOrderId());

		List<ChargeData> chargeDatas = this.generateChargesForOrderService.generatebillingOrder(chargeServices);

		this.chargingOrderWritePlatformService.updateBillingOrder(chargeDatas);

		return this.generateChargesForOrderService.createNewChargesForServices(chargeDatas, groupOfCharges);
	}

	public List<BillItem> singleOrderInvoice(Long orderId, Long clientId, LocalDate processDate) {

		// Get qualified order complete details
		List<BillingOrderData> chargeServices = this.chargingOrderReadPlatformService.retrieveBillingOrderData(clientId,
				processDate, orderId);

		List<ChargeData> chargeDatas = this.generateChargesForOrderService.generatebillingOrder(chargeServices);

		// BillItem
		List<BillItem> billItems = this.generateChargesForOrderService.generateCharge(chargeDatas);

		// Update order-price
		this.chargingOrderWritePlatformService.updateBillingOrder(chargeDatas);
		logger.info("Top-Up:---------------------" + chargeDatas.get(0).getNextBillableDate());

		List<OrderData> orderData = this.orderReadPlatformService.orderDetailsForClientBalance(orderId);
		for (BillItem billItem : billItems) {

			JsonObject clientBalanceObject = new JsonObject();
			clientBalanceObject.addProperty("clientId", clientId);
			clientBalanceObject.addProperty("amount", billItem.getInvoiceAmount());
			clientBalanceObject.addProperty("isWalletEnable", false);
			clientBalanceObject.addProperty("clientServiceId", orderData.get(0).getClientServiceId());
			clientBalanceObject.addProperty("currencyId", billItem.getCurrencyId());
			clientBalanceObject.addProperty("locale", "en");

			final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
			JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
					clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null, null, null,
					null, null);

			// Update Client Balance
			this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
		}
		return billItems;

	}

	public boolean checkInvoiceConfigurations(final String configName) {

		Configuration configuration = this.globalConfigurationRepository.findOneByName(configName);
		if (configuration != null && configuration.isEnabled()) {
			return true;
		} else {
			return false;
		}

	}

	/*
	 * public GenerateChargeData chargesForServices(BillingOrderData
	 * billingOrderData, Long clientId,LocalDatße processDate,BillItem
	 * invoice,boolean singleInvoiceFlag) {
	 * 
	 * // Get qualified order complete details List<BillingOrderData> products =
	 * this.chargingOrderReadPlatformService.retrieveBillingOrderData(clientId,
	 * processDate,billingOrderData.getOrderId());
	 * 
	 * List<ChargeData> chargeDatas =
	 * this.generateChargesForOrderService.generatebillingOrder(products);
	 * 
	 * if(singleInvoiceFlag){
	 * 
	 * invoice =
	 * this.generateChargesForOrderService.generateMultiOrderCharges(chargeDatas,
	 * invoice);
	 * 
	 * // Update order-price
	 * this.chargingOrderWritePlatformService.updateBillingOrder(chargeDatas);
	 * System.out.println("---------------------"+
	 * chargeDatas.get(0).getNextBillableDate());
	 * 
	 * return new GenerateChargeData(clientId,
	 * chargeDatas.get(0).getNextBillableDate(),
	 * invoice.getInvoiceAmount(),invoice);
	 * 
	 * }else{
	 * 
	 * // BillItem BillItem singleInvoice =
	 * this.generateChargesForOrderService.generateCharge(chargeDatas);
	 * 
	 * // Update order-price
	 * this.chargingOrderWritePlatformService.updateBillingOrder(chargeDatas);
	 * System.out.println("---------------------"+
	 * chargeDatas.get(0).getNextBillableDate());
	 * 
	 * // Update Client Balance
	 * this.chargingOrderWritePlatformService.updateClientBalance(singleInvoice.
	 * getInvoiceAmount(), clientId, false);
	 * 
	 * return new GenerateChargeData(clientId,
	 * chargeDatas.get(0).getNextBillableDate(),singleInvoice.getInvoiceAmount(),
	 * singleInvoice); } return new GenerateChargeData(clientId,
	 * chargeDatas.get(0).getNextBillableDate(),
	 * invoice.getInvoiceAmount(),invoice); }
	 */

	/*
	 * if (singleInvoiceFlag) {
	 * 
	 * this.billItemRepository.save(invoiceData.getInvoice());
	 * 
	 * // Update Client Balance
	 * this.chargingOrderWritePlatformService.updateClientBalance(invoiceData.
	 * getInvoice().getInvoiceAmount(), clientId,false); }
	 */

}
