package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.billing.planprice.api.PricingApiResource;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.data.ChargeTaxCommand;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.domain.BillItemRepository;
import org.mifosplatform.finance.chargeorder.domain.Charge;
import org.mifosplatform.finance.chargeorder.domain.ChargeTax;
import org.mifosplatform.finance.chargeorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceWritePlatformService;
import org.mifosplatform.finance.officebalance.domain.OfficeBalance;
import org.mifosplatform.finance.officebalance.domain.OfficeBalanceRepository;
import org.mifosplatform.finance.secondarysubscriberdues.service.SecondarySubscriberDuesWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientBillInfoData;
import org.mifosplatform.portfolio.client.service.ClientBillInfoReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class GenerateChargesForOrderServiceImp implements GenerateChargesForOrderService {

	private final static Logger logger = LoggerFactory.getLogger(GenerateChargesForOrderServiceImp.class);

	private final GenerateCharges generateCharges;
	private final BillItemRepository billItemRepository;
	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final OrderReadPlatformService orderReadPlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService;
	private final ClientBalanceWritePlatformService clientBalanceWritePlatformService;
	private final ConfigurationRepository configurationRepository;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final SecondarySubscriberDuesWritePlatformService secondarySubscriberDuesWritePlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final OfficeBalanceRepository officeBalanceRepository;
	private final OfficeRepository officeRepository;
	private final ClientBalanceRepository clientBalanceRepository;
	private final PriceRepository priceRepository;

	@Autowired
	public GenerateChargesForOrderServiceImp(final GenerateCharges generateCharges,
			final BillItemRepository billItemRepository,
			final ChargingOrderReadPlatformService chargingOrderReadPlatformService,
			final ChargingOrderWritePlatformService chargingOrderWritePlatformService,
			final OrderReadPlatformService orderReadPlatformService, final FromJsonHelper fromJsonHelper,
			final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService,
			final ClientBalanceWritePlatformService clientBalanceWritePlatformService,
			final ConfigurationRepository configurationRepository,
			final OfficeReadPlatformService officeReadPlatformService,

			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			@Lazy final SecondarySubscriberDuesWritePlatformService secondarySubscriberDuesWritePlatformService,
			final ClientReadPlatformService clientReadPlatformService,
			final OfficeBalanceRepository officeBalanceRepository, final OfficeRepository officeRepository,
			final ClientBalanceRepository clientBalanceRepository, final PriceRepository priceRepository) {

		this.generateCharges = generateCharges;
		this.billItemRepository = billItemRepository;
		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
		this.orderReadPlatformService = orderReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.clientBillInfoReadPlatformService = clientBillInfoReadPlatformService;
		this.clientBalanceWritePlatformService = clientBalanceWritePlatformService;
		this.configurationRepository = configurationRepository;
		this.officeReadPlatformService = officeReadPlatformService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.secondarySubscriberDuesWritePlatformService = secondarySubscriberDuesWritePlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
		this.officeBalanceRepository = officeBalanceRepository;
		this.officeRepository = officeRepository;
		this.clientBalanceRepository = clientBalanceRepository;
		this.priceRepository = priceRepository;
	}

	@Override
	public List<ChargeData> generatebillingOrder(List<BillingOrderData> products) {

		ChargeData billingOrderCommand = null;
		List<ChargeData> billingOrderCommands = new ArrayList<ChargeData>();

		if (products.size() != 0) {

			for (BillingOrderData billingOrderData : products) {
				// discount master
				DiscountMasterData discountMasterData = null;
				List<DiscountMasterData> discountMasterDatas = chargingOrderReadPlatformService
						.retrieveDiscountOrders(billingOrderData.getClientOrderId(), billingOrderData.getOderPriceId());

				if (discountMasterDatas.size() != 0) {
					discountMasterData = discountMasterDatas.get(0);
				}

				if (billingOrderData.getOrderStatus() == 3) {
					billingOrderCommand = generateCharges.getCancelledOrderBill(billingOrderData, discountMasterData);
					billingOrderCommands.add(billingOrderCommand);
				}

				else if ("NRC".equals(billingOrderData.getChargeType())) {

					logger.info("---- NRC ---");
					billingOrderCommand = generateCharges.getOneTimeBill(billingOrderData, discountMasterData);
					billingOrderCommands.add(billingOrderCommand);

				} else if ("RC".equals(billingOrderData.getChargeType())) {

					logger.info("---- RC ---");
					if ("month(s)".equalsIgnoreCase(billingOrderData.getDurationType())) {
						if ("N".equalsIgnoreCase(billingOrderData.getBillingAlign())) {

							billingOrderCommand = generateCharges.getMonthyBill(billingOrderData, discountMasterData);
							billingOrderCommands.add(billingOrderCommand);

						} else if ("Y".equalsIgnoreCase(billingOrderData.getBillingAlign())) {

							if (billingOrderData.getInvoiceTillDate() == null) {

								billingOrderCommand = generateCharges.getProrataMonthlyFirstBill(billingOrderData,
										discountMasterData);
								billingOrderCommands.add(billingOrderCommand);

							} else if (billingOrderData.getInvoiceTillDate() != null) {

								billingOrderCommand = generateCharges.getNextMonthBill(billingOrderData,
										discountMasterData);
								billingOrderCommands.add(billingOrderCommand);
							}
						}

						// weekly
					} else if ("week(s)".equalsIgnoreCase(billingOrderData.getDurationType())) {

						if ("N".equalsIgnoreCase(billingOrderData.getBillingAlign())) {

							billingOrderCommand = generateCharges.getWeeklyBill(billingOrderData, discountMasterData);
							billingOrderCommands.add(billingOrderCommand);

						} else if ("Y".equalsIgnoreCase(billingOrderData.getBillingAlign())) {

							if (billingOrderData.getInvoiceTillDate() == null) {

								billingOrderCommand = generateCharges.getProrataWeeklyFirstBill(billingOrderData,
										discountMasterData);
								billingOrderCommands.add(billingOrderCommand);

							} else if (billingOrderData.getInvoiceTillDate() != null) {

								billingOrderCommand = generateCharges.getNextWeeklyBill(billingOrderData,
										discountMasterData);
								billingOrderCommands.add(billingOrderCommand);
							}
						}

						// daily
					} else if ("Day(s)".equalsIgnoreCase(billingOrderData.getDurationType())) {

						billingOrderCommand = generateCharges.getDailyBill(billingOrderData, discountMasterData);
						billingOrderCommands.add(billingOrderCommand);

					}
				} /*
					 * else if(generateCharges.isChargeTypeUC(billingOrderData)){
					 * 
					 * System.out.println("---- UC ---"); }
					 */

			}
		} else {
			throw new BillingOrderNoRecordsFoundException();
		}

		return billingOrderCommands;
	}

	@Transactional
	@Override
	public List<BillItem> generateCharge(List<ChargeData> billingOrderCommands) {

		BigDecimal invoiceAmount = BigDecimal.ZERO;
		BigDecimal totalChargeAmount = BigDecimal.ZERO;
		BigDecimal netTaxAmount = BigDecimal.ZERO;
		ClientBillInfoData clientBillInfoData = null;
		List<BillItem> invoiceItems = new ArrayList<BillItem>();
		Map<Long, BillItem> billItemMap = new HashMap<Long, BillItem>();
		Charge charge = null;
		Office office = null;

		for (ChargeData billingOrderCommand : billingOrderCommands) {
			BillItem invoice = null;
			OfficeData officeData = null;

			if (billingOrderCommand.getChargeOwner() != null
					&& billingOrderCommand.getChargeOwner().equalsIgnoreCase("self")) {
				if (billItemMap.containsKey(billingOrderCommand.getClientId())) {
					invoice = billItemMap.get(billingOrderCommand.getClientId());
				} else {
					invoice = new BillItem(billingOrderCommand.getClientId(), DateUtils.getLocalDateOfTenant().toDate(),
							invoiceAmount, invoiceAmount, netTaxAmount, "active");
					billItemMap.put(billingOrderCommand.getClientId(), invoice);
				}
			} else {
				officeData = this.officeReadPlatformService.retriveOfficeDetail(billingOrderCommand.getClientId());

				if (billItemMap.containsKey(officeData.getClientId())) {
					invoice = billItemMap.get(officeData.getClientId());
				} else {
					invoice = new BillItem(officeData.getClientId(), DateUtils.getLocalDateOfTenant().toDate(),
							invoiceAmount, invoiceAmount, netTaxAmount, "active");
					billItemMap.put(officeData.getClientId(), invoice);
				}
			}
			clientBillInfoData = this.clientBillInfoReadPlatformService
					.retrieveClientBillInfoDetails(billingOrderCommand.getClientId());

			/*
			 * if(billingOrderCommand.getChargeOwner().equalsIgnoreCase("self")) {
			 */
			BigDecimal conversionPrice = this.clientBalanceWritePlatformService.conversion(
					billingOrderCommand.getCurrencyId(), clientBillInfoData.getBillCurrency(),
					billingOrderCommand.getPrice());
			/*
			 * }else if(billingOrderCommand.getChargeOwner().equalsIgnoreCase("parent") {
			 * if(collectionBy != null){ OfficeBalance officeBalance
			 * =this.officeBalanceRepository.findOneByOfficeId(collectionBy);
			 * 
			 * if(officeBalance != null){
			 * officeBalance.updateBalance("CREDIT",officePayments.getAmountPaid());
			 * 
			 * }else if(officeBalance == null){
			 * 
			 * BigDecimal balance=BigDecimal.ZERO.subtract(officePayments.getAmountPaid());
			 * officeBalance =OfficeBalance.create(collectionBy,balance); }
			 * this.officeBalanceRepository.saveAndFlush(officeBalance); }
			 */
			BigDecimal netChargeTaxAmount = BigDecimal.ZERO;
			BigDecimal discountAmount = BigDecimal.ZERO;
			BigDecimal netChargeAmount = conversionPrice;
			String discountCode = "None";

			if (billingOrderCommand.getDiscountMasterData() != null) {
				discountAmount = billingOrderCommand.getDiscountMasterData().getDiscountAmount();
				discountCode = billingOrderCommand.getDiscountMasterData().getDiscountCode();
				netChargeAmount = billingOrderCommand.getPrice().subtract(discountAmount);

			}

			List<ChargeTaxCommand> chargeTaxCommands = billingOrderCommand.getListOfTax();
			if (billingOrderCommand.getChargeOwner() != null
					&& billingOrderCommand.getChargeOwner().equalsIgnoreCase("self")) {
				charge = new Charge(billingOrderCommand.getClientId(), billingOrderCommand.getClientOrderId(),
						billingOrderCommand.getOrderPriceId(), billingOrderCommand.getChargeCode(),
						billingOrderCommand.getChargeType(), discountCode, conversionPrice, discountAmount,
						netChargeAmount, billingOrderCommand.getStartDate(), billingOrderCommand.getEndDate(),
						clientBillInfoData.getBillCurrency(), billingOrderCommand.getChargeOwner());
			} else {
				// ClientData clientData =
				// this.clientReadPlatformService.retrieveOne(billingOrderCommand.getClientId());
				charge = new Charge(clientBillInfoData.getOfficeClientId(), billingOrderCommand.getClientOrderId(),
						billingOrderCommand.getOrderPriceId(), billingOrderCommand.getChargeCode(),
						billingOrderCommand.getChargeType(), discountCode, conversionPrice, discountAmount,
						netChargeAmount, billingOrderCommand.getStartDate(), billingOrderCommand.getEndDate(),
						clientBillInfoData.getBillCurrency(), billingOrderCommand.getChargeOwner());

			}

			if (!chargeTaxCommands.isEmpty()) {

				for (ChargeTaxCommand chargeTaxCommand : chargeTaxCommands) {

					if (BigDecimal.ZERO.compareTo(chargeTaxCommand.getTaxAmount()) < 0) {

						netChargeTaxAmount = netChargeTaxAmount.add(chargeTaxCommand.getTaxAmount());
						ChargeTax invoiceTax = new ChargeTax(invoice, charge, chargeTaxCommand.getTaxCode(),
								chargeTaxCommand.getTaxValue(), chargeTaxCommand.getTaxPercentage(),
								chargeTaxCommand.getTaxAmount(), clientBillInfoData.getBillCurrency());
						charge.addChargeTaxes(invoiceTax);
					}
				}

				if (billingOrderCommand.getTaxInclusive() != null) {
					if (isTaxInclusive(billingOrderCommand.getTaxInclusive())
							&& chargeTaxCommands.get(0).getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
						netChargeAmount = netChargeAmount.subtract(netChargeTaxAmount);
						charge.setNetChargeAmount(netChargeAmount);
						charge.setChargeAmount(netChargeAmount);
					}
				}
			}

			netTaxAmount = netTaxAmount.add(netChargeTaxAmount);
			totalChargeAmount = totalChargeAmount.add(netChargeAmount);
			invoice.addCharges(charge);
			invoiceAmount = totalChargeAmount.add(netTaxAmount);
			invoice.setNetChargeAmount(totalChargeAmount);
			invoice.setTaxAmount(netTaxAmount);
			invoice.setInvoiceAmount(invoiceAmount);
			invoice.setCurrencyId(clientBillInfoData.getBillCurrency());
			if (billingOrderCommand.getChargeOwner() != null
					&& billingOrderCommand.getChargeOwner().equalsIgnoreCase("self")) {
				billItemMap.put(billingOrderCommand.getClientId(), invoice);
			} else {
				billItemMap.put(officeData.getClientId(), invoice);
			}
			// return this.billItemRepository.saveAndFlush(invoice);
		}
		for (Map.Entry<Long, BillItem> entry : billItemMap.entrySet()) {
			this.billItemRepository.saveAndFlush(entry.getValue());
			invoiceItems.add(entry.getValue());
		}

		return invoiceItems;
	}

	public BigDecimal getInvoiceAmount(List<ChargeData> billingOrderCommands) {

		BigDecimal invoiceAmount = BigDecimal.ZERO;
		for (ChargeData billingOrderCommand : billingOrderCommands) {
			invoiceAmount = invoiceAmount.add(billingOrderCommand.getPrice());
		}
		return invoiceAmount;
	}

	public Boolean isTaxInclusive(Integer taxInclusive) {

		Boolean isTaxInclusive = false;
		if (taxInclusive == 1)
			isTaxInclusive = true;

		return isTaxInclusive;
	}

	@Override
	public Map<String, List<Charge>> createNewChargesForServices(List<ChargeData> billingOrderCommands,
			Map<String, List<Charge>> groupOfCharges) {

		LinkedList<Charge> listOfCharges = new LinkedList<Charge>();
		Charge charge = null;
		for (ChargeData billingOrderCommand : billingOrderCommands) {

			ClientBillInfoData clientBillInfoData = this.clientBillInfoReadPlatformService
					.retrieveClientBillInfoDetails(billingOrderCommand.getClientId());

			if (billingOrderCommand.getCurrencyId() <= 1000) {

				BigDecimal conversionPrice = billingOrderCommand.getPrice();

				// commanded for multiCurrency
				/*
				 * this.clientBalanceWritePlatformService.conversion(
				 * billingOrderCommand.getCurrencyId(), clientBillInfoData.getBillCurrency(),
				 * billingOrderCommand.getPrice());
				 */

				BigDecimal netChargeTaxAmount = BigDecimal.ZERO;
				BigDecimal discountAmount = BigDecimal.ZERO;
				// BigDecimal netChargeAmount = billingOrderCommand.getPrice();
				BigDecimal netChargeAmount = conversionPrice;
				String discountCode = "None";

				if (billingOrderCommand.getDiscountMasterData() != null) {
					discountAmount = billingOrderCommand.getDiscountMasterData().getDiscountAmount();
					discountCode = billingOrderCommand.getDiscountMasterData().getDiscountCode();
					netChargeAmount = netChargeAmount.subtract(discountAmount);

				}

				List<ChargeTaxCommand> invoiceTaxCommands = billingOrderCommand.getListOfTax();
				if (billingOrderCommand.getChargeOwner() != null
						&& billingOrderCommand.getChargeOwner().equalsIgnoreCase("self")) {
					charge = new Charge(billingOrderCommand.getClientId(), billingOrderCommand.getClientOrderId(),
							billingOrderCommand.getOrderPriceId(), billingOrderCommand.getChargeCode(),
							billingOrderCommand.getChargeType(), discountCode, conversionPrice, discountAmount,
							netChargeAmount, billingOrderCommand.getStartDate(), billingOrderCommand.getEndDate(),
							clientBillInfoData.getBillCurrency(), billingOrderCommand.getChargeOwner());

				} else {
					// ClientData clientData =
					// this.clientReadPlatformService.retrieveOne(billingOrderCommand.getClientId());
					charge = new Charge(clientBillInfoData.getOfficeClientId(), billingOrderCommand.getClientOrderId(),
							billingOrderCommand.getOrderPriceId(), billingOrderCommand.getChargeCode(),
							billingOrderCommand.getChargeType(), discountCode, conversionPrice, discountAmount,
							netChargeAmount, billingOrderCommand.getStartDate(), billingOrderCommand.getEndDate(),
							clientBillInfoData.getBillCurrency(), billingOrderCommand.getChargeOwner());

				}

				for (ChargeTaxCommand invoiceTaxCommand : invoiceTaxCommands) {

					if (BigDecimal.ZERO.compareTo(invoiceTaxCommand.getTaxAmount()) < 0) {

						netChargeTaxAmount = netChargeTaxAmount.add(invoiceTaxCommand.getTaxAmount());
						ChargeTax invoiceTax = new ChargeTax(null, charge, invoiceTaxCommand.getTaxCode(),
								invoiceTaxCommand.getTaxValue(), invoiceTaxCommand.getTaxPercentage(),
								invoiceTaxCommand.getTaxAmount(), clientBillInfoData.getBillCurrency());
						charge.addChargeTaxes(invoiceTax);
					}
				}

				if (billingOrderCommand.getTaxInclusive() != null) {
					if (isTaxInclusive(billingOrderCommand.getTaxInclusive())
							&& invoiceTaxCommands.get(0).getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
						netChargeAmount = netChargeAmount.subtract(netChargeTaxAmount);
						charge.setNetChargeAmount(netChargeAmount);
						charge.setChargeAmount(netChargeAmount);
					}
				}
				// Grouping same charge code orders
				if (groupOfCharges.containsKey(charge.getChargeCode()) && billingOrderCommand.getIsAggregate()) {
					groupOfCharges.get(charge.getChargeCode()).add(charge);
				} else {
					listOfCharges.add(charge);
					groupOfCharges.put(charge.getOrderId().toString(), listOfCharges);
				}
			} else if (billingOrderCommand.getCurrencyId() > 1000) {
				JsonObject clientBalanceObject = new JsonObject();

				List<OrderData> orderData = this.orderReadPlatformService
						.orderDetailsForClientBalance(billingOrderCommand.getClientOrderId());

				clientBalanceObject.addProperty("clientId", billingOrderCommand.getClientId());
				clientBalanceObject.addProperty("amount", billingOrderCommand.getPrice());
				clientBalanceObject.addProperty("isWalletEnable", false);
				clientBalanceObject.addProperty("clientServiceId", orderData.get(0).getClientServiceId());
				clientBalanceObject.addProperty("currencyId", billingOrderCommand.getCurrencyId());

				clientBalanceObject.addProperty("locale", "en");
				Date date = new Date();
				SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
				clientBalanceObject.addProperty("validFrom", formatter1.format(date));

				/*
				 * LocalDate convertedDate =new LocalDate(date); convertedDate =
				 * convertedDate.withDayOfMonth(convertedDate.getMonthOfYear());
				 * clientBalanceObject.addProperty("validTo",convertedDate.toString());
				 */

				LocalDate convertedDate = new LocalDate(date);
				LocalDate lastDayOfMonth = convertedDate.dayOfMonth().withMaximumValue();
				clientBalanceObject.addProperty("validTo", lastDayOfMonth.toString());

				final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
				JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
						clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null, null,
						null, null, null);

				this.chargingOrderWritePlatformService.updateClientNonCurrencyBalance(clientBalanceCommand);
			}
		}
		return groupOfCharges;
	}

	@Override
	public Map<String, List<Charge>> calculateNewChargesForServices(List<ChargeData> billingOrderCommands,
			Map<String, List<Charge>> groupOfCharges) {

		LinkedList<Charge> listOfCharges = new LinkedList<Charge>();
		Charge charge = null;
		for (ChargeData billingOrderCommand : billingOrderCommands) {

			ClientBillInfoData clientBillInfoData = this.clientBillInfoReadPlatformService
					.retrieveClientBillInfoDetails(billingOrderCommand.getClientId());

			if (billingOrderCommand.getCurrencyId() <= 1000) {

				BigDecimal conversionPrice = this.clientBalanceWritePlatformService.conversion(
						billingOrderCommand.getCurrencyId(), clientBillInfoData.getBillCurrency(),
						billingOrderCommand.getPrice());

				BigDecimal netChargeTaxAmount = BigDecimal.ZERO;
				BigDecimal discountAmount = BigDecimal.ZERO;
				// BigDecimal netChargeAmount = billingOrderCommand.getPrice();
				BigDecimal netChargeAmount = conversionPrice;
				String discountCode = "None";

				if (billingOrderCommand.getDiscountMasterData() != null) {
					discountAmount = billingOrderCommand.getDiscountMasterData().getDiscountAmount();
					discountCode = billingOrderCommand.getDiscountMasterData().getDiscountCode();
					netChargeAmount = netChargeAmount.subtract(discountAmount);

				}

				List<ChargeTaxCommand> invoiceTaxCommands = billingOrderCommand.getListOfTax();
				if (billingOrderCommand.getChargeOwner() != null
						&& billingOrderCommand.getChargeOwner().equalsIgnoreCase("self")) {
					charge = new Charge(billingOrderCommand.getClientId(), billingOrderCommand.getClientOrderId(),
							billingOrderCommand.getOrderPriceId(), billingOrderCommand.getChargeCode(),
							billingOrderCommand.getChargeType(), discountCode, conversionPrice, discountAmount,
							netChargeAmount, billingOrderCommand.getStartDate(), billingOrderCommand.getEndDate(),
							clientBillInfoData.getBillCurrency(), billingOrderCommand.getChargeOwner());

				} else {
					// ClientData clientData =
					// this.clientReadPlatformService.retrieveOne(billingOrderCommand.getClientId());
					charge = new Charge(clientBillInfoData.getOfficeClientId(), billingOrderCommand.getClientOrderId(),
							billingOrderCommand.getOrderPriceId(), billingOrderCommand.getChargeCode(),
							billingOrderCommand.getChargeType(), discountCode, conversionPrice, discountAmount,
							netChargeAmount, billingOrderCommand.getStartDate(), billingOrderCommand.getEndDate(),
							clientBillInfoData.getBillCurrency(), billingOrderCommand.getChargeOwner());

				}

				for (ChargeTaxCommand invoiceTaxCommand : invoiceTaxCommands) {

					if (BigDecimal.ZERO.compareTo(invoiceTaxCommand.getTaxAmount()) < 0) {

						netChargeTaxAmount = netChargeTaxAmount.add(invoiceTaxCommand.getTaxAmount());
						ChargeTax invoiceTax = new ChargeTax(null, charge, invoiceTaxCommand.getTaxCode(),
								invoiceTaxCommand.getTaxValue(), invoiceTaxCommand.getTaxPercentage(),
								invoiceTaxCommand.getTaxAmount(), clientBillInfoData.getBillCurrency());
						charge.addChargeTaxes(invoiceTax);
					}
				}

				if (billingOrderCommand.getTaxInclusive() != null) {
					if (isTaxInclusive(billingOrderCommand.getTaxInclusive())
							&& invoiceTaxCommands.get(0).getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
						netChargeAmount = netChargeAmount.subtract(netChargeTaxAmount);
						charge.setNetChargeAmount(netChargeAmount);
						charge.setChargeAmount(netChargeAmount);
					}
				}
				// Grouping same charge code orders
				if (groupOfCharges.containsKey(charge.getChargeCode()) && billingOrderCommand.getIsAggregate()) {
					groupOfCharges.get(charge.getChargeCode()).add(charge);
				} else {
					listOfCharges.add(charge);
					groupOfCharges.put(charge.getOrderId().toString(), listOfCharges);
				}
			} else if (billingOrderCommand.getCurrencyId() > 1000) {
				JsonObject clientBalanceObject = new JsonObject();

				List<OrderData> orderData = this.orderReadPlatformService
						.orderDetailsForClientBalance(billingOrderCommand.getClientOrderId());

				clientBalanceObject.addProperty("clientId", billingOrderCommand.getClientId());
				clientBalanceObject.addProperty("amount", billingOrderCommand.getPrice());
				clientBalanceObject.addProperty("isWalletEnable", false);
				clientBalanceObject.addProperty("clientServiceId", orderData.get(0).getClientServiceId());
				clientBalanceObject.addProperty("currencyId", billingOrderCommand.getCurrencyId());
				clientBalanceObject.addProperty("locale", "en");
				Date date = new Date();
				SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
				clientBalanceObject.addProperty("validFrom", formatter1.format(date));

				LocalDate convertedDate = new LocalDate(date);
				LocalDate lastDayOfMonth = convertedDate.dayOfMonth().withMaximumValue();
				clientBalanceObject.addProperty("validTo", lastDayOfMonth.toString());

				final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
				JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
						clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null, null,
						null, null, null);

			}
		}
		return groupOfCharges;
	}

	@Override
	public List<BillItem> createBillItemRecords(Map<String, List<Charge>> mappedCharges, Long clientId) {

		Configuration prepaidConfiguration = this.configurationRepository
				.findOneByName(ConfigurationConstants.PREPAID_SERVICE);
		// Configuration isBalanceCheck =
		// this.configurationRepository.findOneByName(ConfigurationConstants.BALANCE_CHECK);
		OfficeData officeData = this.officeReadPlatformService.retriveOfficeDetail(clientId);
		boolean isChargeowner = false;
		String chargeOwner = null;
		BillItem billItem = null;
		List<BillItem> billItemList = new ArrayList<BillItem>();
		Map<Long, BillItem> billItemMap = new HashMap<Long, BillItem>();

		Office office = null;
		Long orderId = null;

		for (Entry<String, List<Charge>> key : mappedCharges.entrySet()) {

			ClientBillInfoData clientBillInfoData = this.clientBillInfoReadPlatformService
					.retrieveClientBillInfoDetails(clientId);

			/*
			 * BigDecimal conversionPrice =
			 * this.clientBalanceWritePlatformService.conversion(orderData.getCurrencyId(),
			 * clientBillInfoData.getBillCurrency(),billItem.getInvoiceAmount());
			 */
			/*
			 * if(billingOrderCommand.getChargeOwner().equalsIgnoreCase("self")) {
			 * if(billItemMap.containsKey(billingOrderCommand.getClientId())) { invoice =
			 * billItemMap.get(billingOrderCommand.getClientId()); }else { invoice = new
			 * BillItem(billingOrderCommand.getClientId(),
			 * DateUtils.getLocalDateOfTenant().toDate(), invoiceAmount, invoiceAmount,
			 * netTaxAmount, "active"); billItemMap.put(billingOrderCommand.getClientId(),
			 * invoice); } }else { officeData =
			 * this.officeReadPlatformService.retriveOfficeDetail(billingOrderCommand.
			 * getClientId());
			 * 
			 * if(billItemMap.containsKey(officeData.getClientId())) { invoice =
			 * billItemMap.get(officeData.getClientId()); }else { invoice = new
			 * BillItem(officeData.getClientId(), DateUtils.getLocalDateOfTenant().toDate(),
			 * invoiceAmount, invoiceAmount, netTaxAmount, "active");
			 * billItemMap.put(officeData.getClientId(), invoice); } }
			 */
			/*
			 * billItem = new BillItem(clientId, DateUtils.getLocalDateOfTenant().toDate(),
			 * invoiceAmount, invoiceAmount, netTaxAmount, "active");
			 */
			for (Charge charge : key.getValue()) {
				BigDecimal netTaxAmount = BigDecimal.ZERO;
				BigDecimal invoiceAmount = BigDecimal.ZERO;
				BigDecimal totalChargeAmount = BigDecimal.ZERO;

				if (charge.getChargeOwner() != null && charge.getChargeOwner().equalsIgnoreCase("self")) {

					for (ChargeTax chargeTax : charge.getChargeTaxs()) {
						netTaxAmount = netTaxAmount.add(chargeTax.getTaxAmount());
					}
					if (billItemMap.containsKey(clientId)) {

						BigDecimal netTaxAmount_local = BigDecimal.ZERO;
						BigDecimal invoiceAmount_local = BigDecimal.ZERO;
						BigDecimal totalChargeAmount_local = BigDecimal.ZERO;
						// billItem = billItemMap.get(charge.getClientId());
						billItem = billItemMap.get(clientId);

						for (ChargeTax chargeTax : charge.getChargeTaxs()) {
							chargeTax.setBillItem(billItem);
						}

						// invoiceAmount_local = billItem.getInvoiceAmount();
						invoiceAmount_local = charge.getChargeAmount();
						netTaxAmount_local = billItem.getTaxAmount();
						// totalChargeAmount_local = billItem.getNetChargeAmount();

						totalChargeAmount_local = totalChargeAmount_local.add(charge.getNetChargeAmount());
						netTaxAmount_local = netTaxAmount_local.add(netTaxAmount);
						invoiceAmount_local = invoiceAmount_local.add(netTaxAmount_local);

						// invoiceAmount_local =
						// invoiceAmount_local.add(totalChargeAmount_local.add(netTaxAmount_local));
						orderId = charge.getOrderId();
						billItem.setNetChargeAmount(totalChargeAmount_local);
						billItem.setTaxAmount(netTaxAmount_local);
						billItem.setInvoiceAmount(invoiceAmount_local);

					} else {
						invoiceAmount = BigDecimal.ZERO;
						totalChargeAmount = totalChargeAmount.add(charge.getNetChargeAmount());
						invoiceAmount = totalChargeAmount.add(netTaxAmount);
						billItem = new BillItem(clientId, DateUtils.getLocalDateOfTenant().toDate(), invoiceAmount,
								invoiceAmount, netTaxAmount, "active");
						for (ChargeTax chargeTax : charge.getChargeTaxs()) {
							chargeTax.setBillItem(billItem);
						}

					}

				} else {
					officeData = this.officeReadPlatformService.retriveOfficeDetail(clientId);
					// if(billItemMap.containsKey(officeData.getClientId())) {
					if (billItemMap.containsKey(0l)) {

						BigDecimal netTaxAmount_local = BigDecimal.ZERO;
						BigDecimal invoiceAmount_local = BigDecimal.ZERO;
						BigDecimal totalChargeAmount_local = BigDecimal.ZERO;
						billItem = billItemMap.get(officeData.getClientId());
						for (ChargeTax chargeTax : charge.getChargeTaxs()) {
							chargeTax.setBillItem(billItem);
						}

						invoiceAmount_local = billItem.getInvoiceAmount();
						netTaxAmount_local = billItem.getTaxAmount();
						totalChargeAmount_local = billItem.getNetChargeAmount();

						// totalChargeAmount_local =
						// totalChargeAmount_local.add(charge.getNetChargeAmount());
						netTaxAmount_local = netTaxAmount_local.add(netTaxAmount);
						// invoiceAmount_local =
						// invoiceAmount_local.add(totalChargeAmount_local.add(netTaxAmount_local));
						invoiceAmount_local = invoiceAmount_local.add(netTaxAmount_local);

						orderId = charge.getOrderId();
						billItem.setNetChargeAmount(totalChargeAmount_local);
						billItem.setTaxAmount(netTaxAmount_local);
						billItem.setInvoiceAmount(invoiceAmount_local);
					} else {
						invoiceAmount = BigDecimal.ZERO;

						totalChargeAmount = totalChargeAmount.add(charge.getNetChargeAmount());
						invoiceAmount = totalChargeAmount.add(netTaxAmount);
						billItem = new BillItem(officeData.getClientId(), DateUtils.getLocalDateOfTenant().toDate(),
								invoiceAmount, invoiceAmount, netTaxAmount, "active");

						for (ChargeTax chargeTax : charge.getChargeTaxs()) {
							chargeTax.setBillItem(billItem);
						}

					}

				}

				orderId = charge.getOrderId();
				billItem.setCurrencyId(clientBillInfoData.getBillCurrency());
				billItem.addCharges(charge);
				if (charge.getChargeOwner() != null && charge.getChargeOwner().equalsIgnoreCase("self")) {
					billItem.setClientId(charge.getClientId());
					billItemMap.put(charge.getClientId(), billItem);
				} else {
					isChargeowner = true;
					office = this.officeRepository.findOne(officeData.getId());
					billItem.setClientId(office.getClientId());
					billItemMap.put(office.getClientId(), billItem);
				}

			}
			for (Map.Entry<Long, BillItem> entry : billItemMap.entrySet()) {

				/* Save item record */
				billItem = this.billItemRepository.saveAndFlush(entry.getValue());
				// this.billItemRepository.saveAndFlush(entry.getValue());

				List<OrderData> orderData = this.orderReadPlatformService.orderDetailsForClientBalance(orderId);

				if (prepaidConfiguration.isEnabled()) {
					if (officeData.getBusinessType().equalsIgnoreCase("Secondary")) {
						if (officeData.getSubscriberDues()) {
							if (orderData.get(0).getIsPrepaid().equalsIgnoreCase("Y")) {
								CommandProcessingResult result = this.secondarySubscriberDuesWritePlatformService
										.secondarySubscriberDues(clientId, officeData.getId(),
												billItem.getInvoiceAmount());
							}
						} else {
							throw new PlatformDataIntegrityException("No SubscriberDue", "No SubscriberDue",
									"No SubscriberDue");
						}
					}
				}

				for (OrderData orderDateDetails : orderData) {

					Price planPricing = priceRepository.findplansByPlanIdChargeOwnerSelf(orderDateDetails.getPlanId());

					JsonObject clientBalanceObject = new JsonObject();
					clientBalanceObject.addProperty("id", billItem.getId());
					if (billItem.getCharges().get(0).getChargeOwner().equalsIgnoreCase("parent")) {
						OfficeBalance officeBalance = this.officeBalanceRepository
								.findOneByOfficeId(officeData.getId());

						/*
						 * if (office.getPayment() == '3' &&
						 * (officeBalance.getBalanceAmount()).compareTo(billItem.getInvoiceAmount()) >
						 * 0) { throw new
						 * OfficeBalanceIsNotEnoughException(officeBalance.getBalanceAmount()); } else {
						 */
						// System.out.println("chargeOfficeclinetId" +office.getClientId());
						clientBalanceObject.addProperty("clientId", office.getClientId());
						clientBalanceObject.addProperty("amount", billItem.getInvoiceAmount());
						clientBalanceObject.addProperty("isWalletEnable", false);
						// clientBalanceObject.addProperty("clientServiceId",
						// orderData.get(0).getClientServiceId());
						clientBalanceObject.addProperty("clientServiceId", orderDateDetails.getClientServiceId());
						clientBalanceObject.addProperty("currencyId", planPricing.getCurrencyId());

						// clientBalanceObject.addProperty("currencyId", billItem.getCurrencyId());
						// clientBalanceObject.addProperty("clientServiceId",
						// orderData.get(0).getCurrencyId());

						clientBalanceObject.addProperty("locale", "en");

						final JsonElement clientServiceElementNew = fromJsonHelper
								.parse(clientBalanceObject.toString());
						JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
								clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null,
								null, null, null, null);

						// updating office balance
						officeBalance.updateBalance("DEBIT", billItem.getInvoiceAmount());
						this.officeBalanceRepository.saveAndFlush(officeBalance);
						this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
						System.out.println("charging for parent");
						// }
					} else if (billItem.getCharges().get(0).getChargeOwner().equalsIgnoreCase("self")) {
						// ClientData clientData = this.clientReadPlatformService.retrieveOne("id",
						// clientId.toString());
						/*
						 * if(clientData!=null &&
						 * clientData.getBalanceAmount().compareTo(billItem.getInvoiceAmount())>0) {
						 * throw new ClientBalanceNotEnoughException(); }else {
						 */
						clientBalanceObject.addProperty("clientId", clientId);
						// System.out.println("chargeclinetId" + clientId);
						clientBalanceObject.addProperty("amount", billItem.getInvoiceAmount());
						clientBalanceObject.addProperty("isWalletEnable", false);
						clientBalanceObject.addProperty("clientServiceId", orderDateDetails.getClientServiceId());
						clientBalanceObject.addProperty("currencyId", planPricing.getCurrencyId());
						clientBalanceObject.addProperty("locale", "en");

						final JsonElement clientServiceElementNew = fromJsonHelper
								.parse(clientBalanceObject.toString());
						JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
								clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null,
								null, null, null, null);
						this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
						System.out.println("charging for self");
					}
				}
				// }
				billItemList.add(entry.getValue());

			}

			/*
			 * invoiceAmount = totalChargeAmount.add(netTaxAmount);
			 * billItem.setNetChargeAmount(totalChargeAmount);
			 * billItem.setTaxAmount(netTaxAmount); billItem.setInvoic
			 * eAmount(invoiceAmount);
			 * billItem.setCurrencyId(clientBillInfoData.getBillCurrency());
			 */
			// billItem = this.billItemRepository.saveAndFlush(billItem);

		}
		return billItemList;
	}

}
