package org.mifosplatform.portfolio.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.discountmaster.domain.DiscountDetails;
import org.mifosplatform.billing.discountmaster.domain.DiscountMaster;
import org.mifosplatform.billing.discountmaster.domain.DiscountMasterRepository;
import org.mifosplatform.billing.discountmaster.exception.DiscountMasterNotFoundException;
import org.mifosplatform.billing.planprice.data.PriceData;
import org.mifosplatform.cms.eventorder.exception.InsufficientAmountException;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.officebalance.data.OfficeBalanceData;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.activationprocess.service.ActivationProcessWritePlatformServiceJpaRepositoryImpl;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderDiscount;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.order.exceptions.NoRegionalPriceFound;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.slabRate.data.SlabRateData;
import org.mifosplatform.portfolio.slabRate.service.SlabRateReadPlatformService;
import org.mifosplatform.portfolio.slabRate.service.SlabRateWritePlatformService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jcraft.jsch.Logger;

@Service
public class OrderAssembler {

	private final OrderDetailsReadPlatformServices orderDetailsReadPlatformServices;
	private final ContractRepository contractRepository;
	private final ConfigurationRepository configurationRepository;
	private final DiscountMasterRepository discountMasterRepository;
	private final ClientRepository clientRepository;
	private final ClientServiceRepository clientServiceRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final SlabRateReadPlatformService slabRateReadPlatformService;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final SlabRateWritePlatformService slabRateWritePlatformService;

	@Autowired
	public OrderAssembler(final OrderDetailsReadPlatformServices orderDetailsReadPlatformServices,
			final ContractRepository contractRepository, final DiscountMasterRepository discountMasterRepository,
			final ConfigurationRepository configurationRepository, final ClientRepository clientRepository,
			final ClientServiceRepository clientServiceRepository, final FromJsonHelper fromApiJsonHelper,
			final SlabRateReadPlatformService slabRateReadPlatformService,
			final OfficeReadPlatformService officeReadPlatformService,
			final ClientReadPlatformService clientReadPlatformService,
			final SlabRateWritePlatformService SlabRateWritePlatformService) {

		this.orderDetailsReadPlatformServices = orderDetailsReadPlatformServices;
		this.contractRepository = contractRepository;
		this.discountMasterRepository = discountMasterRepository;
		this.configurationRepository = configurationRepository;
		this.clientRepository = clientRepository;
		this.clientServiceRepository = clientServiceRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.slabRateReadPlatformService = slabRateReadPlatformService;
		this.officeReadPlatformService = officeReadPlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
		this.slabRateWritePlatformService = SlabRateWritePlatformService;

	}

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderAssembler.class);

	public Order assembleOrderDetails(JsonCommand command, Long clientId, Plan plan) {

		List<OrderLine> serviceDetails = new ArrayList<OrderLine>();
		List<OrderPrice> orderprice = new ArrayList<OrderPrice>();
		List<PriceData> datas = new ArrayList<PriceData>();
		Long orderStatus = null;
		LocalDate endDate = null;
		BigDecimal discountRate = BigDecimal.ZERO;
		List<ServiceData> details = null;
		JsonArray productDetails = null;
		OrderPrice price = null;
		String bouqueCount = null;
		String nonBouqueCount = null;
		Long count = null;
		BigDecimal totalOrderPrice = BigDecimal.ZERO;

		Order order = Order.fromJson(clientId, command);
		order.setOrderNo(command.stringValueOfParameterNamed("orderNo"));
		if (command.hasParameter("products")) {
			productDetails = command.arrayOfParameterNamed("products").getAsJsonArray();
			bouqueCount = command.stringValueOfParameterNamed("bouqueCount");
			nonBouqueCount = command.stringValueOfParameterNamed("nonBouqueCount");
			count = Long.valueOf(bouqueCount) + Long.valueOf(nonBouqueCount);

		} else {
			details = this.orderDetailsReadPlatformServices.retrieveAllServices(order.getPlanId());
		}

		datas = this.orderDetailsReadPlatformServices.retrieveAllPrices(order.getPlanId(), order.getBillingFrequency(),
				clientId);

		/*
		 * if(datas.isEmpty()){
		 * datas=this.orderDetailsReadPlatformServices.retrieveDefaultPrices(order.
		 * getPlanId(),order.getBillingFrequency(),clientId); }
		 */
		if (datas.isEmpty()) {
			throw new NoRegionalPriceFound();
		}

		Contract contractData = this.contractRepository.findOne(order.getContarctPeriod());

		
		LocalDate startDate=new LocalDate(order.getStartDate());		

		if (plan.getProvisionSystem().equalsIgnoreCase("None")) {
			orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId();

		} else {
			ClientService clientService = this.clientServiceRepository
					.findOne(command.longValueOfParameterNamed("clientServiceId"));
			if (clientService.getStatus().equalsIgnoreCase("NEW")) {
				orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId();
			} else {
				orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId();
			}

			if ("Y".equalsIgnoreCase(String.valueOf(plan.isPrepaid()))) {
				orderStatus = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId();
			}
		}



		// Calculate EndDate
		endDate = calculateEndDate(startDate, contractData.getSubscriptionType(), contractData.getUnits());

		order = new Order(order.getClientId(), plan.getId(), orderStatus, null, order.getBillingFrequency(), startDate,
				endDate, order.getContarctPeriod(), serviceDetails, orderprice, order.getbillAlign(),
				UserActionStatusTypeEnum.ACTIVATION.toString(), plan.isPrepaid(), order.isAutoRenewal(),
				order.getClientServiceId());

		Configuration configuration = this.configurationRepository
				.findOneByName(ConfigurationConstants.CONFIG_ALIGN_BIILING_CYCLE);

		if (configuration != null && plan.isPrepaid() == 'N') {
			order.setBillingAlign(configuration.isEnabled() ? 'Y' : 'N');
			if (configuration.isEnabled() && endDate != null) {
				order.setEndDate(endDate.dayOfMonth().withMaximumValue());
			}
		}
		BigDecimal priceforHistory = BigDecimal.ZERO;

		for (PriceData data : datas) {
			LocalDate billstartDate = startDate;
			LocalDate billEndDate = null;

			// end date is null for rc
			if (data.getChagreType().equalsIgnoreCase("RC") && endDate != null) {
				billEndDate = new LocalDate(order.getEndDate());
			} else if (data.getChagreType().equalsIgnoreCase("NRC")) {
				billEndDate = billstartDate;
			}

			final DiscountMaster discountMaster = this.discountMasterRepository.findOne(data.getDiscountId());
			if (discountMaster == null) {
				throw new DiscountMasterNotFoundException();
			}

			// If serviceId Not Exist
			Long productId = data.getProductId();
			if (command.hasParameter("products")) {
				if (productId == 0) {
					BigDecimal slabPrice = null;
					List<SlabRateData> slabRates = this.slabRateReadPlatformService.retrieveSlabRates();

					for (SlabRateData slabrate : slabRates) {
						if (count >= Long.valueOf(slabrate.getSlabFrom())
								&& count <= Long.valueOf(slabrate.getSlabTo())) {
							slabPrice = BigDecimal.valueOf(slabrate.getRate());
						}
					}

					price = new OrderPrice(productId, data.getChargeCode(), data.getChargingVariant(), slabPrice, null,
							data.getChagreType(), data.getChargeDuration(), data.getDurationType(),
							billstartDate.toDate(), billEndDate, data.isTaxInclusive(), data.getChargeOwner());
					price.setCurrencyId(Long.valueOf(data.getCurrencyId()));
					order.addOrderDeatils(price);
					priceforHistory = priceforHistory.add(slabPrice);
					totalOrderPrice = totalOrderPrice.add(price.getPrice());

				}
				for (JsonElement productDetail : productDetails) {
					Long product = this.fromApiJsonHelper.extractLongNamed("productId", productDetail);
					if (productId.equals(product)) {
						price = new OrderPrice(productId, data.getChargeCode(), data.getChargingVariant(),
								data.getPrice(), null, data.getChagreType(), data.getChargeDuration(),
								data.getDurationType(), billstartDate.toDate(), billEndDate, data.isTaxInclusive(),
								data.getChargeOwner());
						price.setCurrencyId(Long.valueOf(data.getCurrencyId()));
						order.addOrderDeatils(price);
						priceforHistory = priceforHistory.add(data.getPrice());
						totalOrderPrice = totalOrderPrice.add(price.getPrice());

					}
				}
			} else {
				price = new OrderPrice(productId, data.getChargeCode(), data.getChargingVariant(), data.getPrice(),
						null, data.getChagreType(), data.getChargeDuration(), data.getDurationType(),
						billstartDate.toDate(), billEndDate, data.isTaxInclusive(), data.getChargeOwner());
				price.setCurrencyId(Long.valueOf(data.getCurrencyId()));
				order.addOrderDeatils(price);
				priceforHistory = priceforHistory.add(data.getPrice());
				totalOrderPrice = totalOrderPrice.add(price.getPrice());
			}
			Client client = this.clientRepository.findOne(clientId);
			List<DiscountDetails> discountDetails = discountMaster.getDiscountDetails();
			for (DiscountDetails discountDetail : discountDetails) {
				if (client.getCategoryType().equals(Long.valueOf(discountDetail.getCategoryType()))) {
					discountRate = discountDetail.getDiscountRate();
				} else if (discountRate.equals(BigDecimal.ZERO)
						&& Long.valueOf(discountDetail.getCategoryType()).equals(Long.valueOf(0))) {
					discountRate = discountDetail.getDiscountRate();
				}
			}

			// discount Order
			OrderDiscount orderDiscount = new OrderDiscount(order, price, discountMaster.getId(),
					discountMaster.getStartDate(), null, discountMaster.getDiscountType(), discountRate);
			// price.addOrderDiscount(orderDiscount);
			order.addOrderDiscount(orderDiscount);
		}
		// end


		if (command.hasParameter("products")) {
			for (JsonElement productDetail : productDetails) {
				Long productId = this.fromApiJsonHelper.extractLongNamed("productId", productDetail);
				String serviceType = this.fromApiJsonHelper.extractStringNamed("serviceType", productDetail);

				OrderLine orderdetails = new OrderLine(productId, null, serviceType, plan.getStatus(), 'N');
				order.addServiceDeatils(orderdetails);
			}
		} else {
			for (ServiceData data : details) {
				OrderLine orderdetails = new OrderLine(data.getProductId(), data.getProductPoId(),
						data.getServiceType(), plan.getStatus(), 'N');
				// this condition is for updating purchase_product_poid for multiple plans
				if (command.stringValueOfParameterName("purchaseProductPoid") != null) {
					orderdetails.setPurchaseProductPoId(
							Long.parseLong(command.stringValueOfParameterName("purchaseProductPoid")));
				}
				order.addServiceDeatils(orderdetails);

			}
		}


		return order;

	}

	// Calculate EndDate
	public LocalDate calculateEndDate(LocalDate startDate, String durationType, Long duration) {

		LocalDate contractEndDate = null;
		if (durationType.equalsIgnoreCase("DAY(s)")) {
			contractEndDate = startDate.plusDays(duration.intValue() - 1);
		} else if (durationType.equalsIgnoreCase("MONTH(s)")) {
			contractEndDate = startDate.plusMonths(duration.intValue()).minusDays(1);
		} else if (durationType.equalsIgnoreCase("YEAR(s)")) {
			contractEndDate = startDate.plusYears(duration.intValue()).minusDays(1);
		} else if (durationType.equalsIgnoreCase("week(s)")) {
			contractEndDate = startDate.plusWeeks(duration.intValue()).minusDays(1);
		}
		return contractEndDate;
	}

	public Order setDatesOnOrderActivation(Order order, LocalDate startDate) {

		Contract contract = this.contractRepository.findOne(order.getContarctPeriod());
		LocalDate endDate = this.calculateEndDate(startDate, contract.getSubscriptionType(), contract.getUnits());
		if (order.getbillAlign() == 'Y' && endDate != null) {
			order.setEndDate(endDate.dayOfMonth().withMaximumValue());
		} else {
			order.setEndDate(endDate);
		}

		for (OrderPrice orderPrice : order.getPrice()) {
			LocalDate billstartDate = startDate;

			orderPrice.setBillStartDate(billstartDate);
			// end date is null for rc
			if (orderPrice.getChargeType().equalsIgnoreCase("RC") && endDate != null) {
				orderPrice.setBillEndDate(new LocalDate(order.getEndDate()));
			} else if (endDate == null) {
				orderPrice.setBillEndDate(endDate);
			} else if (orderPrice.getChargeType().equalsIgnoreCase("NRC")) {
				orderPrice.setBillEndDate(billstartDate);
			}
		}
		return order;
	}
}
