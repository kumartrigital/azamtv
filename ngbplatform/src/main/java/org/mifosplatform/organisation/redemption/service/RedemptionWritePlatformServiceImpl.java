package org.mifosplatform.organisation.redemption.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeMaster;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeRepository;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.cms.journalvoucher.domain.JournalVoucher;
import org.mifosplatform.cms.journalvoucher.domain.JournalVoucherDetails;
import org.mifosplatform.cms.journalvoucher.domain.JournalvoucherDetailsRepository;
import org.mifosplatform.cms.journalvoucher.domain.JournalvoucherRepository;
import org.mifosplatform.finance.chargeorder.service.ChargingOrderWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.redemption.exception.PinNumberAlreadyUsedException;
import org.mifosplatform.organisation.redemption.exception.PinNumberNotFoundException;
import org.mifosplatform.organisation.redemption.serialization.RedemptionCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.voucher.domain.Voucher;
import org.mifosplatform.organisation.voucher.domain.VoucherDetails;
import org.mifosplatform.organisation.voucher.domain.VoucherDetailsRepository;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientBillProfileInfo;
import org.mifosplatform.portfolio.client.domain.ClientBillProfileInfoRepository;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.client.exception.ClientStatusException;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.clientservice.exception.ClientServiceNotFoundException;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class RedemptionWritePlatformServiceImpl implements RedemptionWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(RedemptionWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromJsonHelper;
	private final VoucherDetailsRepository voucherDetailsRepository;
	private final ClientRepository clientRepository;
	private final OrderWritePlatformService orderWritePlatformService;
	private final RedemptionReadPlatformService redemptionReadPlatformService;
	private final RedemptionCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final OrderRepository orderRepository;
	private final ChargeCodeRepository chargeCodeRepository;
	private final JournalvoucherRepository journalvoucherRepository;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final static String VALUE_PINTYPE = "VALUE";
	private final static String PRODUCE_PINTYPE = "PRODUCT";
	private final static int RECONNECT_ORDER_STATUS = 3;
	private final static int RENEWAL_ORDER_STATUS = 1;
	private final static String USED = "USED";
	private final PriceRepository priceRepository;
	private final ContractRepository contractRepository;
	private final ClientBillProfileInfoRepository clientBillProfileInfoRepository;
	private final JournalvoucherDetailsRepository journalvoucherDetailsRepository;
	private final ConfigurationRepository configurationRepository;
	private final ClientServiceRepository clientServiceRepository;

	@Autowired
	public RedemptionWritePlatformServiceImpl(final PlatformSecurityContext context,
			final VoucherDetailsRepository voucherDetailsRepository, final ClientRepository clientRepository,
			final FromJsonHelper fromJsonHelper, final OrderWritePlatformService orderWritePlatformService,
			final RedemptionReadPlatformService redemptionReadPlatformService, final OrderRepository orderRepository,
			final RedemptionCommandFromApiJsonDeserializer apiJsonDeserializer,
			final JournalvoucherRepository journalvoucherRepository, final PriceRepository priceRepository,
			final ContractRepository contractRepository, final ChargeCodeRepository chargeCodeRepository,
			final ChargingOrderWritePlatformService chargingOrderWritePlatformService,
			final ClientBillProfileInfoRepository clientBillProfileInfoRepository,
			final JournalvoucherDetailsRepository journalvoucherDetailsRepository,
			final ConfigurationRepository configurationRepository,
			final ClientServiceRepository clientServiceRepository) {

		this.context = context;
		this.fromJsonHelper = fromJsonHelper;
		this.orderRepository = orderRepository;
		this.clientRepository = clientRepository;
		this.fromApiJsonDeserializer = apiJsonDeserializer;
		this.orderWritePlatformService = orderWritePlatformService;
		this.chargeCodeRepository = chargeCodeRepository;
		this.redemptionReadPlatformService = redemptionReadPlatformService;
		this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
		this.voucherDetailsRepository = voucherDetailsRepository;
		this.journalvoucherRepository = journalvoucherRepository;
		this.priceRepository = priceRepository;
		this.contractRepository = contractRepository;
		this.clientBillProfileInfoRepository = clientBillProfileInfoRepository;
		this.journalvoucherDetailsRepository = journalvoucherDetailsRepository;
		this.configurationRepository = configurationRepository;
		this.clientServiceRepository = clientServiceRepository;

	}

	/**
	 * Implementing createRedemption method
	 * 
	 * @throws Exception
	 */
	@Transactional
	@Override
	public CommandProcessingResult createRedemption(final JsonCommand command) {

		try {

			final String simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy").format(DateUtils.getDateOfTenant());
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final Long clientId = command.longValueOfParameterNamed("clientId");
			final String pinNum = command.stringValueOfParameterNamed("pinNumber");
			final String channel  = command.stringValueOfParameterName("channel");
			this.clientObjectRetrieveById(clientId);
			Long resourceId = Long.valueOf(0);
			CommandProcessingResult result = null;
			System.out
					.println("RedemptionWritePlatformServiceImpl.createrRedemption() started for Pin Number " + pinNum);
			/*
			 * Client client = this.clientRepository.findOne(clientId);
			 */
			//ClientService clientService = clientServiceRepository.findClientServicewithClientId(clientId);
			/*
			 * if (client != null && client.getStatus() != 300) { throw new
			 * ClientStatusException(clientId); }
			 */

			final VoucherDetails voucherDetails = retrieveRandomDetailsByPinNo(pinNum);
			if (voucherDetails != null) {
				final Voucher voucher = voucherDetails.getVoucher();
				final String pinType = voucher.getPinType();
				final String pinTypeValue = voucher.getPinValue();
				final Long priceId = voucher.getPriceId();

				BigDecimal pinValue = BigDecimal.ZERO;

				if (pinType.equalsIgnoreCase(VALUE_PINTYPE) && pinTypeValue != null) {
					pinValue = new BigDecimal(pinTypeValue);
					ClientBillProfileInfo ClientBillProfileInfo = this.clientBillProfileInfoRepository
							.findwithclientId(clientId);

					JsonObject clientBalanceObject = new JsonObject();
					clientBalanceObject.addProperty("clientId", clientId);
					clientBalanceObject.addProperty("amount", pinValue.negate());
					clientBalanceObject.addProperty("isWalletEnable", false);
					clientBalanceObject.addProperty("currencyId", ClientBillProfileInfo.getBillCurrency());
					clientBalanceObject.addProperty("locale", "en");

					final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
					JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
							clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null,
							null, null, null, null);

					System.out.println(
							"RedemptionWritePlatformServiceImpl.createrRedemption() update balance for  clientId "
									+ clientId);
					this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
					resourceId = clientId;
				}

				if (pinType.equalsIgnoreCase(PRODUCE_PINTYPE) && pinTypeValue != null) {

				LOGGER.info("RedemptionWritePlatformServiceImpl.createrRedemption() found pin type as product for Pin Number :"+ pinNum + "with value :" + pinTypeValue);
					final Long planId = Long.parseLong(pinTypeValue);
					final List<Long> orderIds = this.redemptionReadPlatformService.retrieveOrdersData(clientId, planId);
					final Price price = this.priceRepository.findOne(priceId);
					Long contractId = (long) 0;

					if (price != null) {
						final String contractPeriod = price.getContractPeriod();
						Contract contract = this.contractRepository.findOneByContractId(contractPeriod);
						contractId = contract.getId();
						pinValue = price.getPrice();
					}
					final JsonObject json = new JsonObject();

					if (orderIds.isEmpty() && (price != null)) {
						// paywizard
						Configuration isPaywizard = configurationRepository
								.findOneByName(ConfigurationConstants.PAYWIZARD_INTEGRATION);

						if (null != isPaywizard && isPaywizard.isEnabled()) {

						} else {
							ChargeCodeMaster chargeCode = this.chargeCodeRepository
									.findOneByChargeCode(price.getChargeCode());
							json.addProperty("billAlign", false);
							json.addProperty("planCode", planId);
							json.addProperty("contractPeriod", contractId);
							json.addProperty("isNewplan", true);
							json.addProperty("paytermCode", chargeCode.getBillFrequencyCode());
							json.addProperty("locale", "en");
							json.addProperty("dateFormat", "dd MMMM yyyy");
							json.addProperty("start_date", simpleDateFormat);
							json.addProperty("channel", channel);
							final JsonCommand commd = new JsonCommand(null, json.toString(), json, fromJsonHelper, null,
									clientId, null, null, null, null, null, null, null, null, null, null);
							result = this.orderWritePlatformService.createOrder(clientId, commd, null);
							resourceId = result.resourceId();

						}
					} else {

						final Long orderId = orderIds.get(0);

						final Order order = this.orderRepository.findOne(orderId);

						if (order.getStatus() == RECONNECT_ORDER_STATUS) {
							this.orderWritePlatformService.reconnectOrder(orderId,channel);

						} else if (order.getStatus() == RENEWAL_ORDER_STATUS) {
							json.addProperty("priceId", priceId);
							json.addProperty("renewalPeriod", contractId);
							json.addProperty("priceId", priceId);
							json.addProperty("description", "Order Renewal By Redemption");
							json.addProperty("channel", channel);
							final JsonCommand commd = new JsonCommand(null, json.toString(), json, fromJsonHelper, null,
									clientId, null, null, clientId, null, null, null, null, null, null, null);
							result = this.orderWritePlatformService.renewalClientOrder(commd, orderId);
							resourceId = result.resourceId();
						}
					}

					ClientBillProfileInfo ClientBillProfileInfo = this.clientBillProfileInfoRepository
							.findwithclientId(clientId);
					JSONObject clientBalanceObject = new JSONObject();
					Long clientServiceId = Long.valueOf(0);
					try {
						clientBalanceObject.put("clientId", clientId);
						clientBalanceObject.put("amount", pinValue.negate());
						clientBalanceObject.put("isWalletEnable", false);
						clientBalanceObject.put("clientServiceId", clientServiceId);
						clientBalanceObject.put("currencyId", ClientBillProfileInfo.getBillCurrency());
						clientBalanceObject.put("locale", "en");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
					JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
							clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null,
							null, null, null, null);

					// this.chargingOrderWritePlatformService.updateClientVoucherBalance(pinValue.negate(),
					// clientId, false);

					this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);

				}

				// JournalVoucher journalVoucher=new
				// JournalVoucher(resourceId,DateUtils.getDateOfTenant(),"Redemption",pinValue.doubleValue(),null,clientId);

				System.out.println(
						"RedemptionWritePlatformServiceImpl.createrRedemption() creating journal entry  for Pin Number "
								+ pinNum);
				JournalVoucher journalVoucher = new JournalVoucher(DateUtils.getDateOfTenant(), "Redemption");
				this.journalvoucherRepository.saveAndFlush(journalVoucher);
				JournalVoucherDetails journalVoucherDetail = new JournalVoucherDetails(journalVoucher.getId(), pinNum,
						"Redemption", "Debit", "Voucher", pinValue.doubleValue());
				JournalVoucherDetails journalVoucherDetails = new JournalVoucherDetails(journalVoucher.getId(),
						clientId.toString(), "Client", "Credit", "Voucher", pinValue.doubleValue());
				this.journalvoucherDetailsRepository.saveAndFlush(journalVoucherDetail);
				this.journalvoucherDetailsRepository.saveAndFlush(journalVoucherDetails);

				System.out.println(
						"RedemptionWritePlatformServiceImpl.createrRedemption() changing status for Pin Number "
								+ pinNum);

				voucherDetails.setClientId(clientId);
				voucherDetails.setStatus(USED);
				voucherDetails.setSaleDate(DateUtils.getDateTimeOfTenant());

				this.voucherDetailsRepository.save(voucherDetails);
			} else {
				throw new PinNumberNotFoundException(pinNum);
			}
			return new CommandProcessingResult(voucherDetails.getId(), clientId);
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (Exception e) {
			LOGGER.info("RedemptionWritePlatformServiceImpl.createrRedemption() failed to reedem voucher "
					+ e.getMessage());
			return new CommandProcessingResult(Long.valueOf(-1));
			//
		}

	}

	private VoucherDetails retrieveRandomDetailsByPinNo(String pinNumber) {

		final VoucherDetails voucherDetails = this.voucherDetailsRepository.findOneByPinNumber(pinNumber);

		if (voucherDetails == null) {
			throw new PinNumberNotFoundException(pinNumber);

		} else if (voucherDetails.getClientId() != null || voucherDetails.getStatus().equalsIgnoreCase("USED")) {
			throw new PinNumberAlreadyUsedException(pinNumber);
		}
		return voucherDetails;
	}

	private Client clientObjectRetrieveById(final Long clientId) {

		final Client client = this.clientRepository.findOne(clientId);
		if (client == null) {
			throw new ClientNotFoundException(clientId);
		}
		return client;
	}

	private void handleCodeDataIntegrityIssues(final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());

	}

}
