/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.activationprocess.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeRepository;
import org.mifosplatform.billing.chargecode.service.ChargeCodeReadPlatformService;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.billing.selfcare.domain.SelfCareTemporary;
import org.mifosplatform.billing.selfcare.domain.SelfCareTemporaryRepository;
import org.mifosplatform.billing.selfcare.exception.SelfCareNotVerifiedException;
import org.mifosplatform.billing.selfcare.exception.SelfCareTemporaryEmailIdNotFoundException;
import org.mifosplatform.billing.selfcare.service.SelfCareRepository;
import org.mifosplatform.cms.mediadevice.exception.DeviceIdNotFoundException;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.chargeorder.service.ChargingOrderWritePlatformService;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepository;
import org.mifosplatform.infrastructure.codes.service.CodeReadPlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.inview.service.InviewWritePlatformService;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberAlreadyExistException;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberNotFoundException;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsReadPlatformService;
import org.mifosplatform.logistics.onetimesale.api.OneTimeSalesApiResource;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleWritePlatformService;
import org.mifosplatform.logistics.ownedhardware.service.OwnedHardwareWritePlatformService;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.exception.AddressNoRecordsFoundException;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.service.MessagePlatformEmailService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.redemption.api.RedemptionApiResource;
import org.mifosplatform.organisation.voucher.domain.VoucherDetailsRepository;
import org.mifosplatform.organisation.voucher.service.VoucherReadPlatformService;
import org.mifosplatform.portfolio.activationprocess.domain.LeaseDetails;
import org.mifosplatform.portfolio.activationprocess.domain.LeaseDetailsRepository;
import org.mifosplatform.portfolio.activationprocess.exception.BvnNotVerificationException;
import org.mifosplatform.portfolio.activationprocess.exception.ClientAlreadyCreatedException;
import org.mifosplatform.portfolio.activationprocess.exception.LeaseDetailsNotFoundException;
import org.mifosplatform.portfolio.activationprocess.exception.MobileNumberLengthException;
import org.mifosplatform.portfolio.activationprocess.exception.MobileNumberDuplicationException;
import org.mifosplatform.portfolio.activationprocess.exception.NINNOTVerificationException;
import org.mifosplatform.portfolio.activationprocess.exception.OTPNOTVerificationException;
import org.mifosplatform.portfolio.activationprocess.exception.PhotoNotVerificationException;
import org.mifosplatform.portfolio.activationprocess.exception.SomethingWentWrongException;
import org.mifosplatform.portfolio.activationprocess.serialization.ActivationProcessCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.client.api.ClientsApiResource;
import org.mifosplatform.portfolio.client.data.ClientBillInfoData;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.service.ClientBillInfoReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientIdentifierWritePlatformService;
import org.mifosplatform.portfolio.client.service.ClientWritePlatformService;
import org.mifosplatform.portfolio.clientservice.api.ClientServiceApiResource;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceWriteplatformService;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderLineRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanDetails;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.plan.exceptions.PlanNotFundException;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;
import org.mifosplatform.provisioning.networkelement.domain.NetworkElement;
import org.mifosplatform.provisioning.networkelement.domain.NetworkElementRepository;
import org.mifosplatform.provisioning.networkelement.service.NetworkElementReadPlatformService;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ActivationProcessWritePlatformServiceJpaRepositoryImpl implements ActivationProcessWritePlatformService {

	private final static Logger logger = LoggerFactory
			.getLogger(ActivationProcessWritePlatformServiceJpaRepositoryImpl.class);

	private final PlatformSecurityContext context;
	private final FromJsonHelper fromJsonHelper;
	private final ItemRepository itemRepository;
	private final ClientWritePlatformService clientWritePlatformService;
	private final OneTimeSaleWritePlatformService oneTimeSaleWritePlatformService;
	private final OrderWritePlatformService orderWritePlatformService;
	private final ConfigurationRepository configurationRepository;
	private final OwnedHardwareWritePlatformService ownedHardwareWritePlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	private final ActivationProcessCommandFromApiJsonDeserializer commandFromApiJsonDeserializer;
	private final ItemDetailsRepository itemDetailsRepository;
	private final SelfCareTemporaryRepository selfCareTemporaryRepository;
	private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	private final CodeValueRepository codeValueRepository;
	private final ClientIdentifierWritePlatformService clientIdentifierWritePlatformService;
	private final PriceRepository priceRepository;
	private final ChargeCodeRepository chargeCodeRepository;
	private final SelfCareRepository selfCareRepository;
	private final ContractRepository contractRepository;
	private final PlanRepository planRepository;
	private final OneTimeSalesApiResource oneTimeSalesApiResource;
	private final ClientServiceApiResource clientServiceApiResource;
	private final ClientsApiResource clientsApiResource;
	private final CrmServices crmServices;
	private final OrderRepository orderRepository;
	private final OrderLineRepository orderLineRepository;
	private final ClientRepository clientRepository;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
	private final CodeReadPlatformService codeReadPlatformService;
	private final ItemReadPlatformService itemReadPlatformService;
	private final NetworkElementRepository networkElementRepository;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	private final ActiondetailsWritePlatformService actiondetailsWritePlatformService;
	private final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService;
	private final ClientServiceWriteplatformService clientServiceWriteplatformService;
	private final ChargeCodeReadPlatformService chargeCodeReadPlatformService;
	private final PlanReadPlatformService planReadPlatformService;
	private final InviewWritePlatformService inviewWritePlatformService;
	private final ItemDetailsReadPlatformService itemDetailsReadPlatformService;
	private final FromJsonHelper fromApiJsonHelper;
	private final RedemptionApiResource redemptionApiResource;
	private final VoucherReadPlatformService voucherReadPlatformService;
	private final OfficeRepository officeRepository;
	private final NetworkElementReadPlatformService networkElementReadPlatformService;
	private final VoucherDetailsRepository voucherDetailsRepository;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final LeaseDetailsRepository leaseDetailsRepository;
	// private final ResendOtpMessage resendOtpMessage;

	static JSONObject activation = new JSONObject();
	static org.json.simple.JSONArray address = new org.json.simple.JSONArray();
	static org.json.simple.JSONArray client = new org.json.simple.JSONArray();
	static org.json.simple.JSONArray clientServiceData = new org.json.simple.JSONArray();
	static org.json.simple.JSONArray clientServiceDetails = new org.json.simple.JSONArray();
	static org.json.simple.JSONArray deviceData = new org.json.simple.JSONArray();
	static org.json.simple.JSONArray serialNumber = new org.json.simple.JSONArray();
	static org.json.simple.JSONArray planData = new org.json.simple.JSONArray();
	final static String locale = "en";
	final static String dateFormat = "dd MMMM yyyy";
	final static Boolean isNewplan = true;

	static JSONObject clientjson = new JSONObject();
	static JSONObject clientServiceJson = new JSONObject();
	static JSONObject clientServiceDetailsJson = new JSONObject();
	static JSONObject addressjson = new JSONObject();
	static JSONObject addressjsonBilling = new JSONObject();
	static JSONObject deviceJson = new JSONObject();
	static JSONObject serialNumberJson = new JSONObject();
	static JSONObject pairableItemDetailsJson = new JSONObject();
	static JSONObject planDataJson1 = new JSONObject();
	static JSONObject adjustmentJson = new JSONObject();
	static SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
	static Date nextBilldate = null;
	static Date disconnectionDate = null;
	String deviceId = null;
	ItemData itemData = null;
	Long clientServiceId = null;
	String leasePlanCode = null;
	Configuration leaseChargeMonthlyConfiguration = null;
	String basePackCode = null;
	JSONObject object = null;

	@Autowired
	public ActivationProcessWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
			final FromJsonHelper fromJsonHelper, final ClientWritePlatformService clientWritePlatformService,
			final OneTimeSaleWritePlatformService oneTimeSaleWritePlatformService,
			final OrderWritePlatformService orderWritePlatformService,
			final ConfigurationRepository globalConfigurationRepository,
			final OwnedHardwareWritePlatformService ownedHardwareWritePlatformService,
			final AddressReadPlatformService addressReadPlatformService,
			final ActivationProcessCommandFromApiJsonDeserializer commandFromApiJsonDeserializer,
			final ItemDetailsRepository itemDetailsRepository,
			final SelfCareTemporaryRepository selfCareTemporaryRepository,
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final CodeValueRepository codeValueRepository, final ItemRepository itemRepository,
			final PriceRepository priceRepository,
			final BillingMessageTemplateRepository billingMessageTemplateRepository,
			final MessagePlatformEmailService messagePlatformEmailService,
			final BillingMessageRepository messageDataRepository,
			final ClientIdentifierWritePlatformService clientIdentifierWritePlatformService,
			final ChargeCodeRepository chargeCodeRepository, final SelfCareRepository selfCareRepository,
			final ContractRepository contractRepository, final PlanRepository planRepository,
			final OneTimeSalesApiResource oneTimeSalesApiResource,
			final ClientServiceApiResource clientServiceApiResource, final ClientsApiResource clientsApiResource,
			final ChargeCodeReadPlatformService chargeCodeReadPlatformService, final CrmServices crmServices,
			final OrderRepository orderRepository, final CodeReadPlatformService codeReadPlatformService,
			final OrderLineRepository orderLineRepository, final ClientRepository clientRepository,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final ServiceMasterReadPlatformService serviceMasterReadPlatformService,
			final ItemReadPlatformService itemReadPlatformService,
			final NetworkElementRepository networkElementRepository,
			final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final ActiondetailsWritePlatformService actiondetailsWritePlatformService,
			final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService,
			final ClientServiceWriteplatformService clientServiceWriteplatformService,
			final PlanReadPlatformService planReadPlatformService,
			final InviewWritePlatformService inviewWritePlatformService, final FromJsonHelper fromApiJsonHelper,
			final ItemDetailsReadPlatformService itemDetailsReadPlatformService,
			final RedemptionApiResource redemptionApiResource,
			final VoucherReadPlatformService voucherReadPlatformService, final OfficeRepository officeRepository,
			final NetworkElementReadPlatformService networkElementReadPlatformService,
			final VoucherDetailsRepository voucherDetailsRepository,
			final ChargingOrderWritePlatformService chargingOrderWritePlatformService,
			final LeaseDetailsRepository leaseDetailsRepository)

	{

		this.context = context;
		this.itemRepository = itemRepository;
		this.fromJsonHelper = fromJsonHelper;
		this.clientWritePlatformService = clientWritePlatformService;
		this.oneTimeSaleWritePlatformService = oneTimeSaleWritePlatformService;
		this.orderWritePlatformService = orderWritePlatformService;
		this.configurationRepository = globalConfigurationRepository;
		this.ownedHardwareWritePlatformService = ownedHardwareWritePlatformService;
		this.addressReadPlatformService = addressReadPlatformService;
		this.commandFromApiJsonDeserializer = commandFromApiJsonDeserializer;
		this.itemDetailsRepository = itemDetailsRepository;
		this.priceRepository = priceRepository;
		this.chargeCodeRepository = chargeCodeRepository;
		this.selfCareTemporaryRepository = selfCareTemporaryRepository;
		this.contractRepository = contractRepository;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.selfCareRepository = selfCareRepository;
		this.codeValueRepository = codeValueRepository;
		this.clientIdentifierWritePlatformService = clientIdentifierWritePlatformService;
		this.planRepository = planRepository;
		this.oneTimeSalesApiResource = oneTimeSalesApiResource;
		this.clientServiceApiResource = clientServiceApiResource;
		this.clientsApiResource = clientsApiResource;
		this.crmServices = crmServices;
		this.orderRepository = orderRepository;
		this.orderLineRepository = orderLineRepository;
		this.clientRepository = clientRepository;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
		this.codeReadPlatformService = codeReadPlatformService;
		this.itemReadPlatformService = itemReadPlatformService;
		this.networkElementRepository = networkElementRepository;
		this.actionDetailsReadPlatformService = actionDetailsReadPlatformService;
		this.actiondetailsWritePlatformService = actiondetailsWritePlatformService;
		this.clientBillInfoReadPlatformService = clientBillInfoReadPlatformService;
		this.clientServiceWriteplatformService = clientServiceWriteplatformService;
		this.chargeCodeReadPlatformService = chargeCodeReadPlatformService;
		this.planReadPlatformService = planReadPlatformService;
		this.inviewWritePlatformService = inviewWritePlatformService;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.itemDetailsReadPlatformService = itemDetailsReadPlatformService;
		this.redemptionApiResource = redemptionApiResource;
		this.voucherReadPlatformService = voucherReadPlatformService;
		this.officeRepository = officeRepository;
		this.networkElementReadPlatformService = networkElementReadPlatformService;
		this.voucherDetailsRepository = voucherDetailsRepository;
		this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
		this.leaseDetailsRepository = leaseDetailsRepository;
		// this.resendOtpMessage = resendOtpMessage;
	}

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("external_id")) {

			final String externalId = command.stringValueOfParameterNamed("externalId");
			throw new PlatformDataIntegrityException("error.msg.client.duplicate.externalId",
					"Client with externalId `" + externalId + "` already exists", "externalId", externalId);
		} else if (realCause.getMessage().contains("account_no_UNIQUE")) {
			final String accountNo = command.stringValueOfParameterNamed("accountNo");

			throw new PlatformDataIntegrityException("error.msg.client.duplicate.accountNo",
					"Client with accountNo `" + accountNo + "` already exists", "accountNo", accountNo);
		} else if (realCause.getMessage().contains("email_key")) {
			final String email = command.stringValueOfParameterNamed("email");
			throw new PlatformDataIntegrityException("error.msg.client.duplicate.email",
					"Client with email `" + email + "` already exists", "email", email);
		}

		logAsErrorUnexpectedDataIntegrityException(dve);
		throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

	@Transactional
	@Override
	public CommandProcessingResult createSimpleActivation(JsonCommand newcommand) {

		Long clientId = null;
		logger.info("ActivationProcessWritePlatformServiceJpaRepositoryImpl.createSimpleActivation() started");
		leaseChargeMonthlyConfiguration = this.configurationRepository
				.findOneByName(ConfigurationConstants.LEASE_STB_CHARGE_AMOUNT_PLANCODE);
		try {
			JSONObject newJson = new JSONObject();
			JsonCommand command = null;
			String forename = null;
			final JsonElement elementjson = fromJsonHelper.parse(newcommand.json());

			// CONVERT INVIEW FORMAT TO NGB FORMAT
			Configuration isPaywizard = configurationRepository
					.findOneByName(ConfigurationConstants.PAYWIZARD_INTEGRATION);

			Configuration NINVerification = configurationRepository
					.findOneByName(ConfigurationConstants.NIN_VERIFICATION);

			if (null != NINVerification && NINVerification.isEnabled()) {

				Long NINId = newcommand.longValueOfParameterNamed("NIN");

				try {
					this.validateKey_NIN(newcommand, NINId);
				} catch (Exception e) {
					throw new NINNOTVerificationException("NIN Id is Not verified");
				}
			}

			if (null != isPaywizard && isPaywizard.isEnabled()) {

				Long officeId = newcommand.longValueOfParameterNamed("officeId");
				if (officeId == null) {
					throw new OfficeNotFoundException(officeId);
				}
				Office officeData = this.officeRepository.findOne(officeId);
				if (officeId == null || officeData == null) {
					throw new OfficeNotFoundException(officeId);
				}
				// verify device
				JsonArray deviceArray = fromJsonHelper.extractJsonArrayNamed("devices", elementjson);
				for (JsonElement j : deviceArray) {
					JsonCommand deviceComm = new JsonCommand(null, j.toString(), j, fromJsonHelper, null, null, null,
							null, null, null, null, null, null, null, null, null);
					deviceId = deviceComm.stringValueOfParameterName("deviceId");
				}

				itemData = this.itemDetailsReadPlatformService.retriveItemDetailsDataBySerialNum(deviceId, null, 2l);

				if (newcommand.stringValueOfParameterNamed("mobile").length() != 10) {
					throw new MobileNumberLengthException(newcommand.stringValueOfParameterNamed("mobile"));
				}
				if (itemData == null) {
					throw new DeviceIdNotFoundException(deviceId);
				}

				// inviewWritePlatformService.createClient(newcommand);
				command = convertIntoNGBJson(newcommand);

				String jsoncommand = command.json();
				JSONObject jsonObject = new JSONObject(jsoncommand);
				logger.info("inview json payload :" + jsonObject);

			} else if (newcommand.stringValueOfParameterName("forename") != null) {

				forename = newcommand.stringValueOfParameterName("forename");

				Long officeId = newcommand.longValueOfParameterNamed("officeId");
				if (officeId == null) {
					throw new OfficeNotFoundException(officeId);
				}
				Office officeData = this.officeRepository.findOne(officeId);
				if (officeId == null || officeData == null) {
					throw new OfficeNotFoundException(officeId);
				}
				// verify device
				JsonArray deviceArray = fromJsonHelper.extractJsonArrayNamed("devices", elementjson);
				for (JsonElement j : deviceArray) {
					JsonCommand deviceComm = new JsonCommand(null, j.toString(), j, fromJsonHelper, null, null, null,
							null, null, null, null, null, null, null, null, null);
					deviceId = deviceComm.stringValueOfParameterName("deviceId");
				}

				itemData = this.itemDetailsReadPlatformService.retriveItemDetailsDataBySerialNum(deviceId, null,
						officeId);
				if (newcommand.stringValueOfParameterNamed("mobile").length() != 10) {
					throw new MobileNumberLengthException(newcommand.stringValueOfParameterNamed("mobile"));
				}
				if (itemData == null) {
					throw new DeviceIdNotFoundException(deviceId);
				}

				command = convertIntoNGBJson(newcommand);

				String jsoncommand = command.json();
				JSONObject jsonObject = new JSONObject(jsoncommand);
				logger.info("activation json payload :" + jsonObject);

			} else {
				command = newcommand;
			}
			final JsonElement element = fromJsonHelper.parse(command.json());

			JsonArray clientDataArray = fromJsonHelper.extractJsonArrayNamed("clientData", element);

			if (clientDataArray.size() != 0) {
				for (JsonElement clientData : clientDataArray) {

					final JsonElement parsedCommand = this.fromApiJsonHelper.parse(clientData.toString());

					final JsonCommand clientjsonCommand = new JsonCommand(null, clientData.toString(), parsedCommand,
							fromApiJsonHelper, null, null, null, null, null, null, null, null, null, null, null, null);

					CommandProcessingResult resultClient = clientWritePlatformService.createClient(clientjsonCommand);

					JSONObject resultString = new JSONObject(resultClient);
					clientId = resultString.getLong("clientId");
					break;
				}
			} else {
				this.throwError("Client Service");
			}

			Client client = this.clientRepository.findOne(clientId);

			String json = command.json();
			JSONObject jsonObject = new JSONObject(json);
			JSONArray clientServiceArray = jsonObject.getJSONArray("clientServiceData");
			if (clientServiceArray != null) {
				for (int i = 0; i < clientServiceArray.length(); i++) {
					JSONObject clientServiceObject = clientServiceArray.getJSONObject(i);
					clientServiceObject.put("clientPoId", client.getPoid());
					clientServiceObject.put("accountNo", client.getAccountNo());
				}
			}
			newJson.put("clientData", clientDataArray);
			newJson.put("clientServiceData", clientServiceArray);
			newJson.put("deviceData", jsonObject.getJSONArray("deviceData"));
			newJson.put("planData", jsonObject.getJSONArray("planData"));

			final CommandWrapper commandRequest = new CommandWrapperBuilder().createClientSimpleActivation(clientId)
					.withJson(newJson.toString()).build();

			final CommandProcessingResult result = this.commandsSourceWritePlatformService
					.logCommandSource(commandRequest);

			logger.info("ActivationProcessWritePlatformServiceJpaRepositoryImpl.createSimpleActivation() ending");
			// condition to avoid first month lease charge for newly activating customers by
			// crediting after deduction
			/*
			 * if (null != leaseChargeMonthlyConfiguration &&
			 * leaseChargeMonthlyConfiguration.isEnabled()) { if (leasePlanCode != null &&
			 * leasePlanCode.equalsIgnoreCase(object.getString("leaseplanCode")))
			 * prepareClientBalanceObject(clientId, object.getLong("leaseplanPrice")); if
			 * (null != basePackCode &&
			 * basePackCode.equalsIgnoreCase(object.getString("basepackplancode")))
			 * prepareClientBalanceObject(clientId, object.getLong("baseplanPrice"));
			 * 
			 * }
			 */
			return result;
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(newcommand, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (JSONException dve) {
			dve.printStackTrace();
			return new CommandProcessingResult(Long.valueOf(-1));

		}
	}

	private void prepareClientBalanceObject(Long clientId, Long planPrice) {
		// TODO Auto-generated method stub
		JsonObject clientBalanceObject = new JsonObject();
		clientBalanceObject.addProperty("clientId", clientId);
		// System.out.println("chargeclinetId" + clientId);
		clientBalanceObject.addProperty("amount", BigDecimal.valueOf(planPrice));
		clientBalanceObject.addProperty("isWalletEnable", false);
		clientBalanceObject.addProperty("clientServiceId", 0);
		clientBalanceObject.addProperty("currencyId", 104);
		clientBalanceObject.addProperty("locale", "en");
		final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
		JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
				clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null, null, null,
				null, null);
		this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);

	}

	private JsonCommand convertIntoNGBJson(JsonCommand newCommand) {

		// TODO Auto-generated method stub
		JsonCommand command = null;
		JsonCommand deviceComm = null;
		JsonCommand addressComm = null;
		try {
			final JsonElement element = fromJsonHelper.parse(newCommand.json());
			JsonArray addressDataArray = fromJsonHelper.extractJsonArrayNamed("address", element);

			for (JsonElement j : addressDataArray) {

				addressComm = new JsonCommand(null, j.toString(), j, fromJsonHelper, null, null, null, null, null, null,
						null, null, null, null, null, null);

			}
			String city = addressComm.stringValueOfParameterName("city");
			AddressData addressData = addressReadPlatformService.retriveAddressByCity(city);
			if (addressData == null) {
				throw new AddressNoRecordsFoundException(city);
			}
			addressjson.put("addressNo", addressComm.stringValueOfParameterName("addressNo"));
			addressjson.put("street", "");
			addressjson.put("city", addressComm.stringValueOfParameterName("city"));
			addressjson.put("state", addressComm.stringValueOfParameterName("state"));
			addressjson.put("country", addressData.getCountry());
			addressjson.put("district", addressData.getDistrict());
			addressjson.put("zipCode", addressComm.stringValueOfParameterName("postCode"));
			addressjson.put("addressType", "PRIMARY");
			address.add(addressjson);

			addressjsonBilling.put("addressNo", addressComm.stringValueOfParameterName("addressNo"));
			addressjsonBilling.put("street", "");
			addressjsonBilling.put("city", addressComm.stringValueOfParameterName("city"));
			addressjsonBilling.put("state", addressComm.stringValueOfParameterName("state"));
			addressjsonBilling.put("country", addressData.getCountry());
			addressjsonBilling.put("district", addressData.getDistrict());
			addressjsonBilling.put("zipCode", addressComm.stringValueOfParameterName("postCode"));
			addressjsonBilling.put("addressType", "BILLING");
			address.add(addressjsonBilling);

			String pattern = dateFormat;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			String date = simpleDateFormat.format(new Date());

			clientjson.put("locale", locale);
			clientjson.put("dateFormat", dateFormat);
			clientjson.put("activationDate", date);
			clientjson.put("title", newCommand.stringValueOfParameterName("salutation"));
			clientjson.put("firstname", newCommand.stringValueOfParameterName("forename"));
			clientjson.put("lastname", newCommand.stringValueOfParameterName("surname"));
			clientjson.put("officeId", newCommand.longValueOfParameterNamed("officeId"));
			clientjson.put("clientCategory", 20);
			clientjson.put("active", true);

			clientjson.put("phone", newCommand.stringValueOfParameterName("mobile"));
			clientjson.put("email", newCommand.stringValueOfParameterName("email"));
			clientjson.put("billMode", "both");
			clientjson.put("flag", false);
			clientjson.put("entryType", "IND");
			clientjson.put("address", address);

			client.add(clientjson);
			address.clear();

			JsonArray planarray = fromJsonHelper.extractJsonArrayNamed("plans", element);
			String planCode = null;
			// condition to avoid first month lease charge for newly activating customers by
			// crediting after deduction

			Long serviceId = null;
			for (JsonElement j : planarray) {

				deviceComm = new JsonCommand(null, j.toString(), j, fromJsonHelper, null, null, null, null, null, null,
						null, null, null, null, null, null);
				planCode = deviceComm.stringValueOfParameterName("planCode");

				/*
				 * if (null != leaseChargeMonthlyConfiguration &&
				 * leaseChargeMonthlyConfiguration.isEnabled()) { object = new
				 * JSONObject(leaseChargeMonthlyConfiguration.getValue()); if
				 * (planCode.equalsIgnoreCase(object.getString("leaseplanCode"))) leasePlanCode
				 * = planCode; if
				 * (planCode.equalsIgnoreCase(object.getString("basepackplancode")))
				 * basePackCode = planCode; }
				 */

				PlanData planData1 = planReadPlatformService.retrivePlanByPlanCode(planCode);

				if (planData1 == null)
					throw new PlanNotFundException(planCode);
				JSONObject planDataJson = new JSONObject();
				planDataJson.put("locale", locale);
				planDataJson.put("dateFormat", dateFormat);
				planDataJson.put("planCode", planData1.getId());
				planDataJson.put("contractPeriod", planData1.getContractPeriodId());
				planDataJson.put("paytermCode", planData1.getChargeCycle());
				planDataJson.put("isNewplan", isNewplan);

				String channel = newCommand.stringValueOfParameterName("channel");
				if (channel == null) {
					channel = "NGB Client";
				}

				planDataJson.put("channel", channel);
				planDataJson.put("billAlign", false);
				planDataJson.put("start_date", date);
				serviceId = planData1.getServiceId();
				planData.add(planDataJson);

			}

			List<NetworkElementData> networkElementlist = this.networkElementReadPlatformService
					.retrieveNetworkElements();

			clientServiceDetailsJson.put("status", "new");
			clientServiceDetailsJson.put("parameterId", 192);

			if (networkElementlist.size() != 0) {
				clientServiceDetailsJson.put("parameterValue", networkElementlist.get(0).getId());
			}

			clientServiceJson.put("clientServiceDetails", clientServiceDetailsJson);

			clientServiceDetails.add(clientServiceDetailsJson);

			clientServiceJson.put("clientServiceDetails", clientServiceDetails);
			clientServiceDetails.clear();
			clientServiceJson.put("serviceId", serviceId);
			clientServiceData.add(clientServiceJson);

			// deviceData
			deviceJson.put("locale", locale);
			deviceJson.put("dateFormat", dateFormat);
			deviceJson.put("officeId", itemData.getOfficeId());
			deviceJson.put("itemId", itemData.getItemMasterId());
			deviceJson.put("quantity", "1");
			deviceJson.put("chargeCode", itemData.getChargeCode());
			deviceJson.put("unitPrice", itemData.getUnitPrice());
			deviceJson.put("totalPrice", itemData.getUnitPrice());
			deviceJson.put("discountId", 1);
			deviceJson.put("saleType", "NEWSALE");
			deviceJson.put("saleDate", date);

			serialNumberJson.put("serialNumber", deviceId);
			serialNumberJson.put("status", "allocated");
			serialNumberJson.put("saleType", "NEWSALE");
			serialNumberJson.put("itemMasterId", itemData.getItemMasterId());
			serialNumberJson.put("isNewHw", "Y");
			serialNumberJson.put("itemType", itemData.getItemCode());
			serialNumber.add(serialNumberJson);

			deviceJson.put("serialNumber", serialNumber);
			serialNumber.clear();
			// deviceJson.put("pairableItemDetails", pairableItemDetailsJson);
			deviceJson.put("isPairing", "N");
			deviceData.add(deviceJson);
			// plan data
			/*
			 * planDataJson.put("locale", locale); planDataJson.put("dateFormat",
			 * dateFormat); planDataJson.put("planCode", planData1.getId());
			 * planDataJson.put("contractPeriod", planData1.getContractPeriodId());
			 * planDataJson.put("paytermCode", planData1.getChargeCycle());
			 * planDataJson.put("isNewplan", isNewplan);
			 * 
			 * String channel = newCommand.stringValueOfParameterName("channel"); if
			 * (channel == null) { channel = "NGB Client"; }
			 * 
			 * planDataJson.put("channel", channel); planDataJson.put("billAlign", false);
			 * planDataJson.put("start_date", date);
			 * 
			 * planData.add(planDataJson);
			 */

			activation.put("clientData", client);
			client.clear();
			activation.put("clientServiceData", clientServiceData);
			clientServiceData.clear();
			activation.put("deviceData", deviceData);
			deviceData.clear();
			activation.put("planData", planData);
			planData.clear();
			final JsonElement activationElement = fromJsonHelper.parse(activation.toString());
			command = new JsonCommand(null, activation.toString(), activationElement, fromJsonHelper, null, null, null,
					null, null, null, null, null, null, null, null, null);
			return command;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private JsonElement addingclientAndclientServiceTodevice(JsonElement deviceDataElement, Long clientId,
			Long clientServiceId) throws JSONException {
		JsonObject deviceData = deviceDataElement.getAsJsonObject();
		deviceData.addProperty("clientServiceId", clientServiceId);
		JsonArray deviceArray = deviceData.getAsJsonArray("serialNumber");
		for (JsonElement deviceArrayElement : deviceArray) {
			JsonObject deviceArrayObject = deviceArrayElement.getAsJsonObject();
			deviceArrayObject.addProperty("clientId", clientId);
			deviceArrayObject.addProperty("orderId", clientId);
		}
		return deviceData;
	}

	private void throwError(String dest) {
		throw new PlatformDataIntegrityException("error.msg." + dest + ".not.found", dest + " Not Found");
	}

	@Transactional
	@Override
	public CommandProcessingResult activationProcess(final JsonCommand command) {
		Long newClientServiceId = null;
		try {
			context.authenticatedUser();
			CommandProcessingResult resultClient = null;
			CommandProcessingResult resultSale = null;
			/// CommandProcessingResult resultAllocate=null;
			CommandProcessingResult resultOrder = null;
			final JsonElement element = fromJsonHelper.parse(command.json());
			JsonArray clientData = fromJsonHelper.extractJsonArrayNamed("client", element);
			JsonArray saleData = fromJsonHelper.extractJsonArrayNamed("sale", element);
			JsonArray owndevices = fromJsonHelper.extractJsonArrayNamed("owndevice", element);
			// JsonArray allocateData = fromJsonHelper.extractJsonArrayNamed("allocate",
			// element);
			JsonArray bookOrder = fromJsonHelper.extractJsonArrayNamed("bookorder", element);
			// create client service
			JSONObject clientServiceObject = null;

			for (JsonElement j : clientData) {

				JsonCommand comm = new JsonCommand(null, j.toString(), j, fromJsonHelper, null, null, null, null, null,
						null, null, null, null, null, null, null);
				try {
					resultClient = this.clientWritePlatformService.createClient(comm);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				clientServiceObject = new JSONObject();
				clientServiceObject.put("clientId", resultClient.getClientId());
				clientServiceObject.put("serviceId", comm.longValueOfParameterNamed("serviceId"));
				JSONArray clientServiceDetails = new JSONArray();
				JSONObject detailsObject = new JSONObject();
				detailsObject.put("status", "new");
				detailsObject.put("parameterId", comm.longValueOfParameterNamed("parameterId"));
				detailsObject.put("parameterValue", comm.longValueOfParameterNamed("parameterValue"));
				clientServiceDetails.put(detailsObject);
				clientServiceObject.put("clientServiceDetails", clientServiceDetails);
				String clientServiceResult = this.clientServiceApiResource.create(clientServiceObject.toString());
				JSONObject object = new JSONObject(clientServiceResult);
				newClientServiceId = object.getLong("resourceId");
			}

			// Configuration
			// configuration=configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_DEVICE_AGREMENT_TYPE);
			// if(configuration.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)){
			if (saleData.size() != 0) {
				for (JsonElement sale : saleData) {
					JsonObject salee = sale.getAsJsonObject();
					salee.addProperty("clientServiceId", newClientServiceId);
					sale = salee;
					JsonCommand comm = new JsonCommand(null, sale.toString(), sale, fromJsonHelper, null, null, null,
							null, null, null, null, null, null, null, null, null);
					JSONObject saleObject = new JSONObject(comm.json());
					if (!saleObject.has("pairedSerialNo")) {
						resultSale = this.oneTimeSaleWritePlatformService.createOneTimeSale(comm,
								resultClient.getClientId());
					} else {
						String pairedSerialNo = saleObject.getString("pairedSerialNo");
						String pairedItemId = saleObject.getString("pairedItemId");
						String pairedUnitPrice = saleObject.getString("pairedUnitPrice");
						String pairedItemType = saleObject.getString("pairedItemType");

						saleObject.remove(pairedSerialNo);
						saleObject.remove(pairedItemId);
						saleObject.remove(pairedUnitPrice);
						saleObject.remove(pairedItemType);

						saleObject.put("pairableItemDetails", this.preparingPairableDeviceObject(pairedSerialNo,
								saleObject, resultClient.getClientId(), pairedItemId, pairedUnitPrice, pairedItemType));
						this.oneTimeSalesApiResource.createNewMultipleSale(resultClient.getClientId(), "NEWSALE",
								saleObject.toString());

					}

				}
			} // else
				// if(configuration.getValue().equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)){
			else if (owndevices.size() != 0) {
				for (JsonElement ownDevice : owndevices) {

					JsonCommand comm = new JsonCommand(null, ownDevice.toString(), ownDevice, fromJsonHelper, null,
							null, null, null, null, null, null, null, null, null, null, null);
					resultSale = this.ownedHardwareWritePlatformService.createOwnedHardware(comm,
							resultClient.getClientId());
				}

			}

			for (JsonElement order : bookOrder) {
				JsonObject orderr = order.getAsJsonObject();
				orderr.addProperty("clientServiceId", newClientServiceId);
				order = orderr;
				JsonCommand comm = new JsonCommand(null, order.toString(), order, fromJsonHelper, null, null, null,
						null, null, null, null, null, null, null, null, null);
				resultOrder = this.orderWritePlatformService.createOrder(resultClient.getClientId(), comm, null);

			}

			// client service activation
			JSONObject clientServiceActivationObject = new JSONObject();
			clientServiceActivationObject.put("clientId", resultClient.getClientId());
			this.clientServiceApiResource.createClientServiceActivation(newClientServiceId,
					clientServiceActivationObject.toString());

			return resultClient;

		} catch (DataIntegrityViolationException dve) {

			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (JSONException dve) {
			throw new PlatformDataIntegrityException("error.msg.client.jsonexception.", "JSON Exception Occured");
		}

	}

	private JSONObject preparingPairableDeviceObject(String pairedSerialNo, JSONObject saleObject, final Long clientId,
			String pairedItemId, String pairedUnitPrice, String pairedItemType) throws JSONException {
		JSONObject pairedJSONObject = new JSONObject();
		pairedJSONObject.put("dateFormat", saleObject.get("dateFormat"));
		pairedJSONObject.put("locale", saleObject.get("locale"));
		pairedJSONObject.put("officeId", saleObject.get("officeId"));
		pairedJSONObject.put("chargeCode", saleObject.get("chargeCode"));
		pairedJSONObject.put("saleType", saleObject.get("saleType"));
		pairedJSONObject.put("discountId", saleObject.get("discountId"));
		pairedJSONObject.put("saleDate", saleObject.get("saleDate"));
		pairedJSONObject.put("clientServiceId", saleObject.get("clientServiceId"));
		pairedJSONObject.put("quantity", saleObject.get("quantity"));
		pairedJSONObject.put("isPairing", "N");

		pairedJSONObject.put("itemId", pairedItemId);
		pairedJSONObject.put("unitPrice", pairedUnitPrice);
		pairedJSONObject.put("totalPrice", pairedUnitPrice);

		JSONArray pairedJSONArray = new JSONArray();
		JSONObject pairedArrayObject = new JSONObject();
		pairedArrayObject.put("serialNumber", pairedSerialNo);
		pairedArrayObject.put("orderId", clientId);
		pairedArrayObject.put("clientId", clientId);
		pairedArrayObject.put("status", "allocated");
		pairedArrayObject.put("isNewHw", "Y");
		pairedArrayObject.put("itemMasterId", pairedItemId);
		pairedArrayObject.put("itemType", pairedItemType);

		pairedJSONArray.put(pairedArrayObject);
		pairedJSONObject.put("serialNumber", pairedJSONArray);

		return pairedJSONObject;
	}

	private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
		logger.error(dve.getMessage(), dve);
	}

	@Transactional
	@Override
	public CommandProcessingResult selfRegistrationProcess(JsonCommand command) {

		try {

			context.authenticatedUser();
			Configuration deviceStatusConfiguration = configurationRepository
					.findOneByName(ConfigurationConstants.CONFIR_PROPERTY_REGISTRATION_DEVICE);

			commandFromApiJsonDeserializer.validateForCreate(command.json(), deviceStatusConfiguration.isEnabled());
			Long id = new Long(1);
			CommandProcessingResult resultClient = null;
			CommandProcessingResult resultSale = null;
			CommandProcessingResult resultOrder = null;
			CommandProcessingResult resultRedemption = null;
			String device = null;
			String dateFormat = "dd MMMM yyyy";
			String activationDate = new SimpleDateFormat(dateFormat).format(DateUtils.getDateOfTenant());

			String fullname = command.stringValueOfParameterNamed("fullname");
			String firstName = command.stringValueOfParameterNamed("firstname");
			String lastname = command.stringValueOfParameterNamed("lastname");
			String city = command.stringValueOfParameterNamed("city");
			String address = command.stringValueOfParameterNamed("address");
			Long phone = command.longValueOfParameterNamed("phone");
			Long homePhoneNumber = command.longValueOfParameterNamed("homePhoneNumber");
			String email = command.stringValueOfParameterNamed("email");
			String nationalId = command.stringValueOfParameterNamed("nationalId");
			String deviceId = command.stringValueOfParameterNamed("device");
			String deviceName = command.stringValueOfParameterName("deviceName");
			String deviceAgreementType = command.stringValueOfParameterNamed("deviceAgreementType");
			String password = command.stringValueOfParameterNamed("password");
			String isMailCheck = command.stringValueOfParameterNamed("isMailCheck");
			String passport = command.stringValueOfParameterNamed("passport");
			Long planId = command.longValueOfParameterNamed("planId");
			String duration = command.stringValueOfParameterNamed("duration");
			String pinNumber = command.stringValueOfParameterNamed("pinNumber");
			boolean fromSelfcare = command.booleanPrimitiveValueOfParameterNamed("fromSelfcare");
			SelfCareTemporary temporary = null;

			if (isMailCheck == null || isMailCheck.isEmpty()) {
				temporary = selfCareTemporaryRepository
						.findOneByEmailId(command.stringValueOfParameterNamed("uniqueReferenceNo"));

				if (temporary == null) {
					throw new SelfCareTemporaryEmailIdNotFoundException(
							command.stringValueOfParameterNamed("uniqueReferenceNo"));

				} else if (temporary.getStatus().equalsIgnoreCase("ACTIVE")) {
					throw new ClientAlreadyCreatedException();

				} else if (temporary.getStatus().equalsIgnoreCase("INACTIVE")) {
					throw new SelfCareNotVerifiedException(email);
				}
			}

			// if (temporary.getStatus().equalsIgnoreCase("PENDING")){

			String zipCode = command.stringValueOfParameterNamed("zipCode");
			// client creation
			CodeValue codeValue = this.codeValueRepository.findOneByCodeValue("Normal");
			JSONObject clientcreation = new JSONObject();
			clientcreation.put("officeId", new Long(1));
			clientcreation.put("clientCategory", codeValue.getId());
			clientcreation.put("firstname", firstName);
			if (fullname == null || fullname.isEmpty()) {
				clientcreation.put("lastname", lastname);
			} else {
				clientcreation.put("lastname", fullname);
			}
			clientcreation.put("phone", phone);
			clientcreation.put("homePhoneNumber", homePhoneNumber);
			clientcreation.put("entryType", "IND");// new Long(1));
			clientcreation.put("email", email);
			clientcreation.put("locale", "en");
			clientcreation.put("active", true);
			clientcreation.put("dateFormat", dateFormat);
			clientcreation.put("activationDate", activationDate);
			clientcreation.put("flag", false);
			clientcreation.put("zipCode", zipCode);
			clientcreation.put("device", deviceId);
			clientcreation.put("password", password);
			clientcreation.put("billMode", "both");
			clientcreation.put("addressNo", address);
			clientcreation.put("authToken", command.stringValueOfParameterName("authToken"));
			clientcreation.put("lat", command.stringValueOfParameterName("lat"));
			clientcreation.put("long", command.stringValueOfParameterName("long"));
			clientcreation.put("uniqueReferenceNo", command.stringValueOfParameterName("uniqueReferenceNo"));

			JSONArray addressArray = new JSONArray();

			Configuration City = this.configurationRepository.findOneByName(ConfigurationConstants.REGISTER_CITY);
			if (City != null && City.isEnabled()) {
				if (city == null || city == "") {
					city = City.getValue();
					clientcreation.put("zipCode", 500000);
				}
			}

			if (!city.isEmpty()) {
				final AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(city);
				if (addressData != null) {
					addressArray.put(getAddressData(addressData, "PRIMARY", command));
					addressArray.put(getAddressData(addressData, "BILLING", command));
				} else {
					throw new NoSuchElementException("City Not Found");
				}
			} else {
				throw new NoSuchElementException("Please Create City");
			}

			clientcreation.put("address", addressArray);
			if (nationalId != null && !nationalId.equalsIgnoreCase("")) {
				clientcreation.put("externalId", nationalId);
			}

			final JsonElement element = fromJsonHelper.parse(clientcreation.toString());
			JsonCommand clientCommand = new JsonCommand(null, clientcreation.toString(), element, fromJsonHelper, null,
					null, null, null, null, null, null, null, null, null, null, null);
			try {
				resultClient = this.clientWritePlatformService.createClient(clientCommand);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (resultClient == null) {
				throw new PlatformDataIntegrityException("error.msg.client.creation.failed", "Client Creation Failed",
						"Client Creation Failed");
			}
			logger.info("responseOfClient: " + resultClient);
			if (passport != null && !passport.equalsIgnoreCase("")) {
				CodeValue passportcodeValue = this.codeValueRepository.findOneByCodeValue("Passport");
				JSONObject clientIdentifierJson = new JSONObject();
				clientIdentifierJson.put("documentTypeId", passportcodeValue.getId());
				clientIdentifierJson.put("documentKey", passport);
				final JsonElement idenfierJsonEement = fromJsonHelper.parse(clientIdentifierJson.toString());
				JsonCommand idenfierJsonCommand = new JsonCommand(null, clientIdentifierJson.toString(),
						idenfierJsonEement, fromJsonHelper, null, null, null, null, null, null, null, null, null, null,
						null, null);
				this.clientIdentifierWritePlatformService.addClientIdentifier(resultClient.getClientId(),
						idenfierJsonCommand);
			}

			if (temporary != null) {
				temporary.setStatus("ACTIVE");
				this.selfCareTemporaryRepository.saveAndFlush(temporary);
			}

			// Service Creation

			JSONArray clientServiceDetailsArray = new JSONArray();
			JSONObject clientserviceObject = new JSONObject();
			ServiceData serviceData = this.serviceMasterReadPlatformService.retriveServiceParam("OTT");
			if (serviceData == null) {
				throw new PlatformApiDataValidationException(
						"OTT Services Are Not Present, Please Create the OTT Services ", "", null);
			}
			clientserviceObject.put("serviceId", serviceData.getId());
			JSONObject clientServiceDetailsObject = new JSONObject();
			clientServiceDetailsObject.put("status", "new");
			clientServiceDetailsObject.put("parameterId", serviceData.getParamName());
			NetworkElement networkElement = this.networkElementRepository.findwithCode("OTT");
			clientServiceDetailsObject.put("parameterValue", networkElement.getId());
			clientServiceDetailsObject.put("casName", "OTT");

			clientServiceDetailsArray.put(clientServiceDetailsObject);
			clientserviceObject.put("clientServiceDetails", clientServiceDetailsArray);
			clientserviceObject.put("clientId", resultClient.getClientId());

			final JsonElement clientServiceElement = fromJsonHelper.parse(clientserviceObject.toString());
			JsonCommand clientServiceCommand = new JsonCommand(null, clientServiceElement.toString(),
					clientServiceElement, fromJsonHelper, null, null, null, null, null, null, null, null, null, null,
					null, null);
			CommandProcessingResult clientServiceResult = this.clientServiceWriteplatformService
					.createClientService(clientServiceCommand);

			// book device
			if (deviceStatusConfiguration != null) {

				if (deviceStatusConfiguration.isEnabled()) {

					JSONObject bookDevice = new JSONObject();
					/*
					 * deviceStatusConfiguration = configurationRepository
					 * .findOneByName(ConfigurationConstants.CONFIG_PROPERTY_DEVICE_AGREMENT_TYPE);
					 */

					/*
					 * if (deviceStatusConfiguration != null&& deviceStatusConfiguration.isEnabled()
					 * &&
					 * deviceStatusConfiguration.getValue().equalsIgnoreCase(ConfigurationConstants.
					 * CONFIR_PROPERTY_SALE)) {
					 */
					if (deviceAgreementType.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_SALE)) {

						device = command.stringValueOfParameterNamed("device");

						ItemDetails detail = itemDetailsRepository.getInventoryItemDetailBySerialNum(device);

						if (detail == null) {
							throw new SerialNumberNotFoundException(device);
						}

						ItemMaster itemMaster = this.itemRepository.findOne(detail.getItemMasterId());

						if (itemMaster == null) {
							throw new ItemNotFoundException(deviceId);
						}

						if (detail != null && detail.getStatus().equalsIgnoreCase("Used")) {
							throw new SerialNumberAlreadyExistException(device);
						}

						JSONObject serialNumberObject = new JSONObject();
						serialNumberObject.put("serialNumber", device);
						serialNumberObject.put("clientId", resultClient.getClientId());
						serialNumberObject.put("status", "allocated");
						serialNumberObject.put("itemMasterId", detail.getItemMasterId());
						serialNumberObject.put("isNewHw", "Y");
						JSONArray serialNumber = new JSONArray();
						serialNumber.put(0, serialNumberObject);

						bookDevice.put("chargeCode", itemMaster.getChargeCode());
						bookDevice.put("unitPrice", itemMaster.getUnitPrice());
						bookDevice.put("itemId", itemMaster.getId());
						bookDevice.put("discountId", id);
						bookDevice.put("officeId", detail.getOfficeId());
						bookDevice.put("totalPrice", itemMaster.getUnitPrice());

						bookDevice.put("quantity", id);
						bookDevice.put("locale", "en");
						bookDevice.put("dateFormat", dateFormat);
						bookDevice.put("saleType", "NEWSALE");
						bookDevice.put("saleDate", activationDate);
						bookDevice.put("serialNumber", serialNumber);

						final JsonElement deviceElement = fromJsonHelper.parse(bookDevice.toString());
						JsonCommand comm = new JsonCommand(null, bookDevice.toString(), deviceElement, fromJsonHelper,
								null, null, null, null, null, null, null, null, null, null, null, null);

						resultSale = this.oneTimeSaleWritePlatformService.createOneTimeSale(comm,
								resultClient.getClientId());

						if (resultSale == null) {
							throw new PlatformDataIntegrityException("error.msg.client.device.assign.failed",
									"Device Assign Failed for ClientId :" + resultClient.getClientId(),
									"Device Assign Failed");
						}
						logger.info("responseOfSale: " + resultSale);
					} else if (deviceAgreementType.equalsIgnoreCase(ConfigurationConstants.CONFIR_PROPERTY_OWN)) {

						List<ItemMaster> itemMaster = this.itemRepository.findAll();
						bookDevice.put("locale", "en");
						bookDevice.put("dateFormat", dateFormat);
						bookDevice.put("allocationDate", activationDate);
						bookDevice.put("provisioningSerialNumber", deviceName);
						bookDevice.put("itemType", itemMaster.get(0).getId());
						bookDevice.put("serialNumber", deviceId);
						bookDevice.put("status", "ACTIVE");

						final JsonElement deviceElement = fromJsonHelper.parse(bookDevice.toString());
						command = new JsonCommand(null, bookDevice.toString(), deviceElement, fromJsonHelper, null,
								null, null, null, null, null, null, null, null, null, null, null);

						resultSale = this.ownedHardwareWritePlatformService.createOwnedHardware(command,
								resultClient.getClientId());

						if (resultSale == null) {
							throw new PlatformDataIntegrityException("error.msg.client.device.assign.failed",
									"Device Assign Failed for ClientId :" + resultClient.getClientId(),
									"Device Assign Failed");
						}
						logger.info("responseOfOwnHW: " + resultSale);
					} else {

					}
				}
			}

			// book order
			Configuration selfregistrationconfiguration = configurationRepository
					.findOneByName(ConfigurationConstants.CONFIR_PROPERTY_SELF_REGISTRATION);

			if (selfregistrationconfiguration != null && selfregistrationconfiguration.isEnabled()) {

				if (selfregistrationconfiguration.getValue() != null
						&& !selfregistrationconfiguration.getValue().isEmpty()) {

					JSONObject configurationJson = new JSONObject(selfregistrationconfiguration.getValue());
					List<Plan> plans = this.planRepository.findwithPlanName(configurationJson.getString("planName"));
					Plan plan = null;
					for (Plan planDB : plans) {
						plan = planDB;
					}
					JSONObject ordeJson = new JSONObject();
					ordeJson.put("id", plan.getId());
					ordeJson.put("planCode", plan.getId());
					ordeJson.put("planDescription", plan.getDescription());
					ordeJson.put("clientServiceId", clientServiceResult.getResourceId());
					ordeJson.put("billAlign", "true");
					ordeJson.put("clientId", resultClient.getClientId());
					ordeJson.put("locale", "en");
					ordeJson.put("dateFormat", "dd MMMM yyyy");
					Date date = new Date();
					SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
					ordeJson.put("start_date", formatter1.format(date));
					ordeJson.put("isNewplan", "true");
					ordeJson.put("fromSelfcare", fromSelfcare);
					if (plan.getDuration() != null) {
						ChargeCodeData chargeCodeData = this.chargeCodeReadPlatformService
								.retrieveChargeCodeAndContractPeriodUsingPlanDuration(plan.getDuration().getId());
						ordeJson.put("contractPeriod", chargeCodeData.getBillFrequencyCode());
						ordeJson.put("paytermCode", chargeCodeData.getContractId());
					} else {
						ordeJson.put("paytermCode", "1 Month");
						ordeJson.put("contractPeriod", "1");
					}
					final JsonElement orderElement = fromJsonHelper.parse(ordeJson.toString());
					command = new JsonCommand(null, ordeJson.toString(), orderElement, fromJsonHelper, null, null, null,
							null, null, null, null, null, null, null, null, null);
					resultOrder = this.orderWritePlatformService.createOrder(resultClient.getClientId(), command, null);

					if (resultOrder == null) {
						throw new PlatformDataIntegrityException("error.msg.client.order.creation",
								"Book Order Failed for ClientId:" + resultClient.getClientId(), "Book Order Failed");
					}
					logger.info("responseOfOrder: " + resultOrder);
				}
				/*
				 * else {
				 * 
				 * String paytermCode = command.stringValueOfParameterNamed("paytermCode");
				 * String contractPeriod =
				 * command.stringValueOfParameterNamed("contractPeriod"); Long planCode =
				 * command.longValueOfParameterNamed("planCode"); Contract contract
				 * =this.contractRepository.findOneByContractId(contractPeriod); List<Price>
				 * prices=this.priceRepository.findChargeCodeByPlanAndContract(planCode,
				 * contractPeriod); Plan planName = this.planRepository.findOne(planCode);
				 * if(planName == null || planName.isDeleted() == 'Y' ){
				 * 
				 * throw new PlatformDataIntegrityException("error.msg.order.id.not.exist",
				 * "Plan doesn't exist with this id " + planCode, "plan code not exist"); }
				 * Contract contractId =
				 * this.contractRepository.findOneByContractId(contractPeriod); if(contractId ==
				 * null){ throw new
				 * PlatformDataIntegrityException("error.msg.contractperiod.not.exist",
				 * "Contract Period doesn't exist with this contractPeriod " + contractPeriod,
				 * "Contract Period not exist"); }
				 * 
				 * if(!prices.isEmpty()){ ChargeCodeMaster chargeCodeMaster =
				 * this.chargeCodeRepository.findOneByChargeCode(prices.get(0).getChargeCode());
				 * if(chargeCodeMaster != null){ paytermCode =
				 * chargeCodeMaster.getBillFrequencyCode(); } }else if(prices.isEmpty()){ throw
				 * new PlatformDataIntegrityException("error.msg.prices.not.exist",
				 * "Plan Price is not define with this Duration " + contractPeriod,
				 * "Plan Price is not define with this Duration"); } if(contract != null){
				 * contractPeriod = contract.getId().toString(); }
				 * 
				 * JSONObject ordeJson = new JSONObject();
				 * 
				 * ordeJson.put("planCode", planCode); ordeJson.put("contractPeriod",
				 * contractPeriod); ordeJson.put("paytermCode", paytermCode);
				 * ordeJson.put("billAlign", false); ordeJson.put("locale", "en");
				 * ordeJson.put("isNewplan", true); ordeJson.put("dateFormat", dateFormat);
				 * ordeJson.put("start_date", activationDate);
				 * 
				 * CommandWrapper commandRequest = new
				 * CommandWrapperBuilder().createOrder(resultClient.getClientId()).withJson(
				 * ordeJson.toString()).build(); resultOrder =
				 * this.portfolioCommandSourceWritePlatformService.logCommandSource(
				 * commandRequest);
				 * 
				 * if (resultOrder == null) { throw new
				 * PlatformDataIntegrityException("error.msg.client.order.creation",
				 * "Book Order Failed for ClientId:" + resultClient.getClientId(),
				 * "Book Order Failed"); } logger.info("responseOfOrder: "+resultOrder); }
				 */ }

			// client service activation
			JSONObject clientServiceActivationObject = new JSONObject();
			clientServiceActivationObject.put("clientId", resultClient.getClientId());
			clientServiceActivationObject.put("fromSelfcare", fromSelfcare);
			final JsonElement clientServiceActivationElement = fromJsonHelper
					.parse(clientServiceActivationObject.toString());
			command = new JsonCommand(null, clientServiceActivationObject.toString(), clientServiceActivationElement,
					fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null, null);
			resultOrder = this.clientServiceWriteplatformService
					.createClientServiceActivation(clientServiceResult.getResourceId(), command);

			// this.clientServiceApiResource.createClientServiceActivation(clientServiceResult.getResourceId(),clientServiceActivationObject.toString());

			// redemption creation
			Configuration isRedemptionconfiguration = configurationRepository
					.findOneByName(ConfigurationConstants.CONFIG_IS_REDEMPTION);

			if (isRedemptionconfiguration != null && isRedemptionconfiguration.isEnabled()) {

				JSONObject redemptionJson = new JSONObject();
				redemptionJson.put("clientId", resultClient.getClientId());
				redemptionJson.put("pinNumber", pinNumber);
				CommandWrapper commandRequest = new CommandWrapperBuilder().createRedemption()
						.withJson(redemptionJson.toString()).build();
				resultRedemption = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
				if (resultRedemption == null) {
					throw new PlatformDataIntegrityException("error.msg.redemption.creation",
							"Redemption Failed for ClientId:" + resultClient.getClientId(), "Redemption Failed");
				}
				logger.info("responseOfRedemption: " + resultRedemption);
			}
			SelfCare selfCare = this.selfCareRepository.findOneByClientId(resultClient.getClientId());
			final Map<String, Object> changes = new LinkedHashMap<String, Object>(1);
			changes.put("username", selfCare.getUserName());
			changes.put("password", selfCare.getPassword());
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(resultClient.getClientId()) //
					.with(changes) //
					.build();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (JSONException e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	@Transactional
	@Override
	public CommandProcessingResult createClientSimpleActivation(JsonCommand command, final Long clientId) {
		Long clientServiceId = null;
		JsonCommand comm = null;
		String clientServicePoId = null;
		Set<String> substances = null;
		// System.out.println(command.json());
		try {
			CommandProcessingResult result = this.crmServices.createClientSimpleActivation(command);

			if (result != null) {
				clientServicePoId = result.getResourceIdentifier();
				substances = result.getSubstances();
			}

			final JsonElement element = fromJsonHelper.parse(command.json());

			JsonArray clientServiceDataArray = fromJsonHelper.extractJsonArrayNamed("clientServiceData", element);
			if (clientServiceDataArray.size() != 0) {
				for (JsonElement clientServiceData : clientServiceDataArray) {
					JsonObject clientService = clientServiceData.getAsJsonObject();
					clientService.addProperty("clientId", clientId);
					String temp1 = null;
					if (result != null) {
						temp1 = result.getResourceIdentifier();
					}
					clientService.addProperty("clientservicePoId", clientServicePoId);

					String resultClientService = this.clientServiceApiResource.create(clientService.toString());

					JSONObject clientServiiceObject = new JSONObject(resultClientService);
					clientServiceId = clientServiiceObject.getLong("resourceId");
					break;
				}
			} else {
				this.throwError("Client Service");
			}

			JsonArray deviceDataArray = fromJsonHelper.extractJsonArrayNamed("deviceData", element);
			if (deviceDataArray.size() != 0) {
				for (JsonElement deviceDataElement : deviceDataArray) {
					String pairableData = null;
					JsonElement deviceData = this.addingclientAndclientServiceTodevice(deviceDataElement, clientId,
							clientServiceId);

					JSONObject pairable = new JSONObject(deviceData.toString());
					if (pairable.has("pairableItemDetails")) {
						String pairableItemDetails = pairable.getString("pairableItemDetails");
						deviceData = addingclientAndclientServiceTodevice(fromJsonHelper.parse(pairableItemDetails),
								clientId, clientServiceId);

						pairable.put("pairableItemDetails", deviceData);

					}
					pairableData = pairable.toString();
					pairableData = pairableData.replace("\\", "");
					pairableData = pairableData.replace("\"{", "{");
					pairableData = pairableData.replace("}\"", "}");
					this.oneTimeSalesApiResource.createNewMultipleSale(clientId, "NEWSALE", pairableData);

					break;
				}
			} else {
				this.throwError("Device");
			}
			JsonArray planDataArray = fromJsonHelper.extractJsonArrayNamed("planData", element);
			if (planDataArray.size() != 0) {
				for (JsonElement planDataElement : planDataArray) {
					JsonObject planData = planDataElement.getAsJsonObject();
					planData.addProperty("clientServiceId", clientServiceId);
					planData.addProperty("orderNo", this.retriveOrderNo(planData, substances));
					planDataElement = planData;
					comm = new JsonCommand(null, planDataElement.toString(), planDataElement, this.fromJsonHelper, null,
							null, null, null, null, null, null, null, null, null, null, null);
					CommandProcessingResult cmd = this.orderWritePlatformService.createOrder(clientId, comm, null);

					Order order = this.orderRepository.findOne(cmd.resourceId());

					if (substances != null) {
						this.updatingPurchaseProductPoIdinOrderLine(order, substances);
					}

					// break;
				}
			} else {
				this.throwError("Plan");
			}

			JSONObject clientServiceActivationObject = new JSONObject();
			clientServiceActivationObject.put("clientId", clientId);

			this.clientServiceApiResource.createClientServiceActivation(clientServiceId,
					clientServiceActivationObject.toString());
			clientServiceId = clientServiceId;
			return new CommandProcessingResultBuilder().withClientId(clientId).build();
		} catch (DataIntegrityViolationException dve) {

			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (JSONException dve) {
			throw new PlatformDataIntegrityException("error.msg.client.jsonexception.", "JSON Exception Occured");
		}
	}

	private void updatingPurchaseProductPoIdinOrderLine(Order order, Set<String> substances) {

		List<OrderLine> orderlines = order.getServices();
		for (OrderLine orderLine : orderlines) {
			for (String substance : substances) {
				if ((this.getValueFromSubstance(substance, 0)).equalsIgnoreCase(order.getOrderNo())
						&& (this.getValueFromSubstance(substance, 2))
								.equalsIgnoreCase(String.valueOf(orderLine.getProductPoId()))) {
					orderLine.setPurchaseProductPoId(Long.valueOf(this.getValueFromSubstance(substance, 3)));
					break;
				}
			}
			this.orderLineRepository.saveAndFlush(orderLine);
		}

	}

	private String retriveOrderNo(JsonObject planData, Set<String> substances) {
		JSONObject object = null;
		String returnValue = null;
		try {
			String obj = planData.toString();
			object = new JSONObject(obj);
			if (substances != null) {
				for (String value : substances) {
					if (this.getValueFromSubstance(value, 1).equalsIgnoreCase(object.optString("planPoId"))) {
						returnValue = this.getValueFromSubstance(value, 0);
						break;
					}
				}
			}
			return returnValue;
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException("order.no.exception", "JSON Exception Occured");
		}
	}

	private String getValueFromSubstance(String value, int i) {
		String arr[] = value.split("_");
		System.out.println(arr[i]);
		return arr[i];
	}

	@Transactional
	@Override
	public CommandProcessingResult createCustomerActivation(JsonCommand command) {

		Date today = new Date();
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
		String date = DATE_FORMAT.format(today);
		JSONObject jsonObject = new JSONObject();
		CommandProcessingResult result = null;
		final CommandProcessingResult clientResult = this.prepareClientJsonArray(command, date);
		try {
			jsonObject.put("clientData", "[]");

			Client newClient = this.clientRepository.findOne(clientResult.getClientId());
			if (newClient.getEmail().equalsIgnoreCase("rcsb@gmail.com")) {
				newClient.setEmail(newClient.getAccountNo() + "@gmail.com");
			}
			if (newClient.getPhone().equalsIgnoreCase("1234567890")) {
				newClient.setPhone(newClient.getPhone());
			}

			this.clientRepository.save(newClient);
			jsonObject.put("clientServiceData", this.prepareClientServiceArray(command, newClient));
			jsonObject.put("deviceData", this.preparedeviceDataJsonArray(command, date));
			jsonObject.put("planData", this.prepareplanDataJson(command, date, clientResult.getClientId()));
			final List<ActionDetaislData> actionDetailsDatas = this.actionDetailsReadPlatformService
					.retrieveActionDetails(EventActionConstants.EVENT_CUSTOMER_ACTIVATION);
			if (!actionDetailsDatas.isEmpty()) {
				this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas, clientResult.getClientId(),
						clientResult.getClientId().toString(), null);
			}

			final CommandWrapper commandRequest = new CommandWrapperBuilder()
					.createClientSimpleActivation(clientResult.getClientId()).withJson(jsonObject.toString()).build(); //
			result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	private CommandProcessingResult prepareClientJsonArray(JsonCommand command, String date) {
		JSONObject clientObject = new JSONObject();
		JSONArray clienyArray = new JSONArray();
		CommandProcessingResult result = null;
		try {
			clientObject.put("activationDate", date);
			clientObject.put("entryType", "IND");
			clientObject.put("officeId", command.stringValueOfParameterName("officeId"));
			CodeValue codeValue = this.codeValueRepository.findOneByCodeValue("Normal");
			if (codeValue != null) {
				clientObject.put("clientCategory", codeValue.getId().toString());
			}
			clientObject.put("billMode", "both");
			clientObject.put("firstname", command.stringValueOfParameterName("firstname"));
			clientObject.put("lastname", ".");
			clientObject.put("phone", command.stringValueOfParameterName("phone"));
			clientObject.put("email", command.stringValueOfParameterName("email"));
			clientObject.put("externalId", command.stringValueOfParameterName("externalId"));
			clientObject.put("idKey", command.stringValueOfParameterName("idKey"));
			clientObject.put("idValue", command.stringValueOfParameterName("idValue"));
			String city = command.stringValueOfParameterName("city");

			JSONArray addressArray = new JSONArray();
			if (!city.isEmpty()) {
				final AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(city);
				if (addressData != null) {
					addressArray.put(getAddressData(addressData, "PRIMARY", command));
					addressArray.put(getAddressData(addressData, "BILLING", command));
				} else {
					return null;
				}
			} else {
				return null;
			}
			clientObject.put("address", addressArray);
			clientObject.put("locale", "en");
			clientObject.put("active", "true");
			clientObject.put("dateFormat", "dd MMMM yyyy");
			clientObject.put("flag", "false");
			System.out.println("customer json" + clientObject.toString());
			final CommandWrapper commandRequest = new CommandWrapperBuilder().createClient()
					.withJson(clientObject.toString()).build();
			result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	private JSONObject getAddressData(AddressData addressData, String addressType, JsonCommand command)
			throws JSONException {
		JSONObject addressObject = new JSONObject();
		addressObject.put("city", addressData.getCity());
		addressObject.put("state", addressData.getState());
		addressObject.put("country", addressData.getCountry());
		addressObject.put("district", addressData.getDistrict());
		addressObject.put("addressType", addressType);
		addressObject.put("street", command.stringValueOfParameterName("street"));
		addressObject.put("zipCode", addressData.getZip());
		addressObject.put("addressNo", command.stringValueOfParameterName("addressNo"));

		return addressObject;
	}

	private JSONArray prepareClientServiceArray(JsonCommand command, Client newClient) {
		try {
			JSONArray clientServiceArray = new JSONArray();
			JSONArray clientServiceDetailsArray = new JSONArray();
			JSONObject clientserviceObject = new JSONObject();
			ServiceData serviceData = this.serviceMasterReadPlatformService
					.retriveServiceParam(command.stringValueOfParameterName("serviceCode"));
			clientserviceObject.put("serviceId", serviceData.getId());
			clientserviceObject.put("clientPoId", newClient.getPoid());
			clientserviceObject.put("accountNo", newClient.getAccountNo());
			JSONObject clientServiceDetailsObject = new JSONObject();
			clientServiceDetailsObject.put("status", "new");
			clientServiceDetailsObject.put("parameterId", serviceData.getParamName());
			NetworkElement networkElement = this.networkElementRepository
					.findwithCode(command.stringValueOfParameterName("provisioningSystem"));
			clientServiceDetailsObject.put("parameterValue", networkElement.getId());
			clientServiceDetailsObject.put("casName", command.stringValueOfParameterName("provisioningSystem"));

			clientServiceDetailsArray.put(clientServiceDetailsObject);
			clientserviceObject.put("clientServiceDetails", clientServiceDetailsArray);
			clientServiceArray.put(clientserviceObject);
			return clientServiceArray;
		} catch (Exception e) {
			return null;
		}
	}

	private JSONArray preparedeviceDataJsonArray(JsonCommand command, String date) {
		try {
			JSONArray array = new JSONArray();
			JSONObject deviceObject = new JSONObject();
			ItemData itemData = this.itemReadPlatformService
					.retrieveItemDetailsForItem(command.stringValueOfParameterName("stb_serialNumber"));
			if (Long.parseLong(command.stringValueOfParameterName("officeId")) != itemData.getOfficeId()) {
				return null;
			}
			// adding required fields to deviceJson Object
			deviceObject.put("locale", "en");
			deviceObject.put("dateFormat", "dd MMMM yyyy");

			deviceObject.put("officeId", itemData.getOfficeId());
			deviceObject.put("itemId", itemData.getId());
			deviceObject.put("chargeCode", "OTC");
			deviceObject.put("unitPrice", itemData.getUnitPrice());
			deviceObject.put("quantity", String.valueOf(1));
			deviceObject.put("discountId", 1);
			deviceObject.put("totalPrice", itemData.getUnitPrice());
			deviceObject.put("saleType", "NEWSALE");
			deviceObject.put("saleDate", date);
			deviceObject.put("serialNumber", this.prepareserialNumberJson(command, true, itemData));
			if (command.hasParameter("pairable_serialNumber")) {
				if (!command.stringValueOfParameterName("pairable_serialNumber").isEmpty()) {
					deviceObject.put("isPairing", "Y");
					deviceObject.put("pairableItemDetails", this.preparepairableItemDetailsJson(command, date));
				}
			} else {
				deviceObject.put("isPairing", "N");
			}

			return array.put(deviceObject);
		} catch (Exception e) {
			return null;
		}
	}

	private JSONArray prepareplanDataJson(JsonCommand command, String date, Long clientId) {
		try {
			ClientBillInfoData clientBillInfoData = this.clientBillInfoReadPlatformService
					.retriveSingleClientBillInfo(clientId);
			Plan plan = this.planRepository.findwithName(command.stringValueOfParameterName("planCode"));
			PlanData planData = this.planReadPlatformService.retrivePlan(plan.getId());

			JSONArray array = new JSONArray();
			JSONObject planObject = new JSONObject();
			// adding required fields to planJson Object
			planObject.put("billAlign", Boolean.parseBoolean("false"));
			planObject.put("autoRenew", "");
			planObject.put("planCode", plan.getId());
			if (plan.getIsPrepaid() == 'N') {
				planObject.put("contractPeriod", Integer.parseInt("1"));
			} else {
				// planObject.put("contractPeriod",clientBillInfoData.getBillFrequency());
				planObject.put("contractPeriod", planData.getContractPeriodId());
			}
			planObject.put("planPoId", plan.getPlanPoid());
			Set<PlanDetails> details = plan.getPlanDetails();
			for (PlanDetails detail : details)
				planObject.put("dealPoId", detail.getDealPoid());
			planObject.put("planName", plan.getPlanCode());
			// planObject.put("paytermCode", clientBillInfoData.getBillFrequencyCode());
			planObject.put("paytermCode", planData.getChargeCycle());
			planObject.put("isNewplan", Boolean.parseBoolean("true"));
			planObject.put("locale", "en");
			planObject.put("dateFormat", "dd MMMM yyyy");
			planObject.put("start_date", date);
			array.put(planObject);
			return array;
		} catch (Exception e) {
			return null;
		}
	}

	private JSONObject preparepairableItemDetailsJson(JsonCommand command, String date) {
		try {
			JSONObject ItemDetailsObject = new JSONObject();
			// adding required fields to deviceJson Object
			ItemData itemData = this.itemReadPlatformService
					.retrieveItemDetailsForItem(command.stringValueOfParameterName("pairable_serialNumber"));
			if (Long.parseLong(command.stringValueOfParameterName("officeId")) != itemData.getOfficeId()) {
				return null;
			}
			ItemDetailsObject.put("locale", "en");
			ItemDetailsObject.put("dateFormat", "dd MMMM yyyy");
			ItemDetailsObject.put("officeId", Integer.parseInt(command.stringValueOfParameterName("officeId")));
			ItemDetailsObject.put("itemId", itemData.getId());
			ItemDetailsObject.put("chargeCode", "OTC");
			ItemDetailsObject.put("unitPrice", itemData.getUnitPrice());
			ItemDetailsObject.put("quantity", String.valueOf(1));
			ItemDetailsObject.put("discountId", 1);
			ItemDetailsObject.put("totalPrice", itemData.getUnitPrice());
			ItemDetailsObject.put("saleType", "NEWSALE");
			ItemDetailsObject.put("saleDate", date);
			ItemDetailsObject.put("serialNumber", this.prepareserialNumberJson(command, false, itemData));
			ItemDetailsObject.put("isPairing", "N");

			return ItemDetailsObject;
		} catch (Exception e) {
			return null;
		}
	}

	private JSONArray prepareserialNumberJson(JsonCommand command, boolean isStb, ItemData itemData) {
		try {
			JSONArray array = new JSONArray();
			JSONObject serialNumberObject = new JSONObject();
			// adding required fields to deviceJson Object
			if (isStb) {
				serialNumberObject.put("serialNumber", command.stringValueOfParameterName("stb_serialNumber"));
				serialNumberObject.put("status", "allocated");
				serialNumberObject.put("itemMasterId", itemData.getId());
				serialNumberObject.put("isNewHw", "Y");
				serialNumberObject.put("saleType", "NEWSALE");
				serialNumberObject.put("itemType", "STB");
			} else {
				serialNumberObject.put("serialNumber", command.stringValueOfParameterName("pairable_serialNumber"));
				serialNumberObject.put("status", "allocated");
				serialNumberObject.put("itemMasterId", itemData.getId());
				serialNumberObject.put("isNewHw", "Y");
				serialNumberObject.put("saleType", "NEWSALE");
				serialNumberObject.put("itemType", "SC");
			}

			array.put(serialNumberObject);

			return array;
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult createServiceActivationWithoutDevice(JsonCommand command, final Long clientId) {
		Long clientServiceId = null;
		JsonCommand comm = null;
		String clientServicePoId = null;
		Set<String> substances = null;
		System.out.println(command.json());
		try {
			CommandProcessingResult result = this.crmServices.createClientSimpleActivation(command);
			if (result != null) {
				clientServicePoId = result.getResourceIdentifier();
				substances = result.getSubstances();
			}

			final JsonElement element = fromJsonHelper.parse(command.json());

			JsonArray clientServiceDataArray = fromJsonHelper.extractJsonArrayNamed("clientServiceData", element);
			if (clientServiceDataArray.size() != 0) {
				for (JsonElement clientServiceData : clientServiceDataArray) {
					JsonObject clientService = clientServiceData.getAsJsonObject();
					clientService.addProperty("clientId", clientId);
					String temp1 = null;
					if (result != null) {
						temp1 = result.getResourceIdentifier();
					}
					clientService.addProperty("clientservicePoId", clientServicePoId);
					String resultClientService = this.clientServiceApiResource.create(clientService.toString());
					JSONObject clientServiiceObject = new JSONObject(resultClientService);
					clientServiceId = clientServiiceObject.getLong("resourceId");
					break;
				}
			} else {
				this.throwError("Client Service");
			}

			JsonArray planDataArray = fromJsonHelper.extractJsonArrayNamed("planData", element);
			if (planDataArray.size() != 0) {
				for (JsonElement planDataElement : planDataArray) {
					JsonObject planData = planDataElement.getAsJsonObject();
					planData.addProperty("clientServiceId", clientServiceId);
					planData.addProperty("orderNo", this.retriveOrderNo(planData, substances));
					planDataElement = planData;
					comm = new JsonCommand(null, planDataElement.toString(), planDataElement, this.fromJsonHelper, null,
							null, null, null, null, null, null, null, null, null, null, null);
					CommandProcessingResult cmd = this.orderWritePlatformService.createOrder(clientId, comm, null);
					Order order = this.orderRepository.findOne(cmd.resourceId());
					if (substances != null) {
						this.updatingPurchaseProductPoIdinOrderLine(order, substances);
					}
					break;
				}
			} else {
				this.throwError("Plan");
			}

			// client service activation
			JSONObject clientServiceActivationObject = new JSONObject();
			clientServiceActivationObject.put("clientId", clientId);
			this.clientServiceApiResource.createClientServiceActivation(clientServiceId,
					clientServiceActivationObject.toString());

			return new CommandProcessingResultBuilder().withClientId(clientId).build();
		} catch (DataIntegrityViolationException dve) {

			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (JSONException dve) {
			throw new PlatformDataIntegrityException("error.msg.client.jsonexception.", "JSON Exception Occured");
		}
	}

	@Override
	public CommandProcessingResult validateKey_NIN(JsonCommand command, Long NINId) {

		JSONObject payload = new JSONObject();
		try {
			payload.put("firstname", command.stringValueOfParameterName("forename"));
			payload.put("lastname", command.stringValueOfParameterName("surname"));
			payload.put("phone", command.stringValueOfParameterName("mobile"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ResponseEntity<String> apiResponse = this.validationNIN(NINId, payload);
		JSONObject result = null;
		try {
			result = new JSONObject(apiResponse.getBody());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HttpStatus statuscode = apiResponse.getStatusCode();

		if (statuscode.value() == 201) {
			return CommandProcessingResult.parsingResult(result);

		} else {
			throw new NINNOTVerificationException("NIN Id is Not verified");
		}

	}

	public ResponseEntity<String> validationNIN(Long NIN, JSONObject requestPayload) {
		ResponseEntity<String> result = null;

		try {
			RestTemplate restTemplate = new RestTemplate();

			String VERIFY_ENDPOINT = "https://vapi.verifyme.ng/v1/verifications/identities/nin/" + NIN;
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization",
					"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjg0ODQ1LCJlbnYiOiJ0ZXN0IiwiaWF0IjoxNjIyNzk2MzMxfQ.RPq3hcDDsLOzHwh-wHF-8vaTbPw3nfj0EoggmrN-qYE");
			headers.add("Content-Type", "application/json");
			HttpEntity<String> request = new HttpEntity<>(requestPayload.toString(), headers);

			result = restTemplate.exchange(VERIFY_ENDPOINT, HttpMethod.POST, request, String.class);
		} catch (Exception e) {
			throw new NINNOTVerificationException("NIN Id is Not verified");

		}
		return result;
	}

	public ResponseEntity<String> BvnVerification(Long BVN, JSONObject requestPayload) {
		ResponseEntity<String> result = null;

		try {
			RestTemplate restTemplate = new RestTemplate();

			String VERIFY_ENDPOINT = "https://vapi.verifyme.ng/v1/verifications/identities/bvn/" + BVN;
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization",
					"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjg0ODQ1LCJlbnYiOiJ0ZXN0IiwiaWF0IjoxNjIyNzk2MzMxfQ.RPq3hcDDsLOzHwh-wHF-8vaTbPw3nfj0EoggmrN-qYE");
			headers.add("Content-Type", "application/json");
			HttpEntity<String> request = new HttpEntity<>(requestPayload.toString(), headers);
			result = restTemplate.exchange(VERIFY_ENDPOINT, HttpMethod.POST, request, String.class);
		} catch (Exception e) {
			throw new BvnNotVerificationException("BVN Id is Not verified");

		}
		return result;
	}

	@Override
	public Boolean photoVerification(String NIN, String path) {
		ResponseEntity<String> result = null;
		Boolean match = null;

		try {
			System.out.println("ActivationProcessWritePlatformServiceJpaRepositoryImpl.photoVerification()");

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

			String VERIFY_ENDPOINT = "https://vapi.verifyme.ng/v1/verifications/identities/biometrics";

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			headers.add("Authorization",
					"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjg0ODQ1LCJlbnYiOiJ0ZXN0IiwiaWF0IjoxNjIyNzk2MzMxfQ.RPq3hcDDsLOzHwh-wHF-8vaTbPw3nfj0EoggmrN-qYE");

			MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
			params.add("photoUrl", path);
			params.add("idType", "nin");
			params.add("idNumber", NIN);

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);

			result = restTemplate.exchange(VERIFY_ENDPOINT, HttpMethod.POST, requestEntity, String.class);

			JSONObject jsonObject = new JSONObject(result.getBody());

			org.json.JSONObject data = jsonObject.getJSONObject("data");

			org.json.JSONObject photoMatching = data.getJSONObject("photoMatching");

			match = photoMatching.getBoolean("match");

			if (result.getStatusCode() != HttpStatus.CREATED) {
				throw new PhotoNotVerificationException("Photo is Not verified");

			}

		} catch (Exception e) {
			throw new PhotoNotVerificationException("Photo is Not verified");

		}
		return match;
	}

	@Override
	public ResponseEntity<String> OTP_MESSAGE(String mobileNo, String Otp) {
		ResponseEntity<String> result = null;

		try {
			RestTemplate restTemplate = new RestTemplate();

			String OTP_ENDPOINT = "https://termii.com/api/sms/send";
			HttpHeaders headers = new HttpHeaders();

			headers.add("Content-Type", "application/json");
			headers.add("Cookie", "termii-sms=64Eq2KHTk3xNkRKHRaxrWA5WbfBp4lMNxHpMcx4Y");

			System.out.println(OTP_ENDPOINT);
			JSONObject requestpayload = new JSONObject();
			requestpayload.put("to", "234" + mobileNo);
			requestpayload.put("from", "Moreplex tv");
			requestpayload.put("sms", "otp verification " + Otp);
			requestpayload.put("type", "plain");
			requestpayload.put("channel", "generic");
			requestpayload.put("api_key", "TLISRdXrYknFK30dLcvfmtqGTdXHozF1QY0hhAe1JBcJDqRLr2Mwej3Q5We7J1");

			HttpEntity<String> request = new HttpEntity<>(requestpayload.toString(), headers);
			System.out.println("ActivationProcessWritePlatformService.OTP_MESSAGEs()");
			result = restTemplate.postForEntity(OTP_ENDPOINT, request, String.class);
			System.out.println("ActivationProcessWritePlatformService.OTP_MESSAGEs()" + result);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			System.out.println("ActivationProcessWritePlatformService.OTP_MESSAGEs()" + result);
			throw new NINNOTVerificationException("something went wrong " + e.getLocalizedMessage());

		} catch (Exception e) {
			System.out.println("ActivationProcessWritePlatformService.OTP_MESSAGEs()" + result);
			e.printStackTrace();
			throw new NINNOTVerificationException("something went wrong " + e.getLocalizedMessage());

		}
		return result;
	}

	@Override
	public CommandProcessingResult createLeaseDetails(JsonCommand command) {
		LeaseDetails details = new LeaseDetails();
		try {
			LeaseDetails leaseDetailscheck = leaseDetailsRepository
					.findLeaseDetailsByMobileNo(command.stringValueOfParameterName("mobile"));
			if (leaseDetailscheck != null) {
				throw new MobileNumberDuplicationException("Mobile Number Not Found ");
			}
			if (command.stringValueOfParameterNamed("mobile").length() != 10) {
				throw new MobileNumberLengthException(command.stringValueOfParameterNamed("mobile"));
			}

			LeaseDetails leaseDetails = details.fromjson(command);
			String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
			leaseDetails.setStatus("Otp_Pending");
			leaseDetails.setOtp(otp);
			String ImageBase64Encoder = command.stringValueOfParameterNamed("image");

			String path = this.saveImage(ImageBase64Encoder);

			this.photoVerification(leaseDetails.getNIN(), path);

			// this.OTP_MESSAGE(details.getMobileNumber(), otp);

			leaseDetailsRepository.saveAndFlush(leaseDetails);
			return CommandProcessingResult.parsingResult(leaseDetails);

		} catch (Exception e) {
			return new CommandProcessingResult(e.getLocalizedMessage());
		}

	}

	public CommandProcessingResult validateMobileAndNIN(JsonCommand command) {

		String mobileNO = command.stringValueOfParameterName("mobileNO");
		String otp = command.stringValueOfParameterName("otp");
		LeaseDetails leaseDetails = leaseDetailsRepository.findLeaseDetailsByMobileNo(mobileNO);
		JSONObject requestPayload = new JSONObject();

		if (leaseDetails.getStatus().equals("Otp_Pending")) {
			if (otp.equals(leaseDetails.getOtp())) {
				leaseDetails.setStatus("Payment_pending");
				leaseDetailsRepository.save(leaseDetails);
			} else {
				throw new OTPNOTVerificationException("OTP is Not verified");
			}
			return CommandProcessingResult.parsingResult("otp verified");
		} else if (leaseDetails.getStatus().equals("NIN_Pending")) {
			try {
				requestPayload.put("firstname", leaseDetails.getFirstName());
				requestPayload.put("lastname", leaseDetails.getLastName());
				requestPayload.put("phone", leaseDetails.getMobileNumber());
			} catch (JSONException e) {
				e.printStackTrace();
				return new CommandProcessingResult(e.getLocalizedMessage());
			}
			ResponseEntity<String> apiResponse = this.validationNIN(Long.parseLong(leaseDetails.getNIN()),
					requestPayload);
			if (!apiResponse.getStatusCode().equals(HttpStatus.CREATED)) {
				throw new NINNOTVerificationException("NIN Id is Not verified");
			}

			leaseDetails.setStatus("Photo_Verification_Pending");
			leaseDetailsRepository.save(leaseDetails);
			return CommandProcessingResult.parsingResult("NIN verified");
		} else if (leaseDetails.getStatus().equals("Photo_Verification_Pending")) {
			String conformation = command.stringValueOfParameterName("conformation");
			if (conformation == null) {
				Boolean status = this.photoVerification(leaseDetails.getNIN(), leaseDetails.getImagePath());
				if (status == false) {
					throw new PhotoNotVerificationException("photo Not Verified");
				}
				leaseDetails.setImageVerification("API");
			} else {
				leaseDetails.setImageVerification("manual");
			}
			leaseDetails.setStatus("Bvn_Pending");
			leaseDetailsRepository.save(leaseDetails);

			return CommandProcessingResult.parsingResult(leaseDetails);
		} else if (leaseDetails.getStatus().equals("Bvn_Pending")) {

			try {
				requestPayload.put("firstname", leaseDetails.getFirstName());
				requestPayload.put("lastname", leaseDetails.getLastName());
				requestPayload.put("phone", leaseDetails.getMobileNumber());
			} catch (JSONException e) {
				e.printStackTrace();
				return new CommandProcessingResult(e.getLocalizedMessage());
			}
			// ResponseEntity<String> apiResponse =
			// this.validationNIN(Long.parseLong(leaseDetails.getNIN()),
			// requestPayload);

			leaseDetails.setAccountNo(command.stringValueOfParameterName("accountNo"));
			leaseDetails.setBankName(command.stringValueOfParameterName("bankName"));
			leaseDetails.setStatus("Registration_Pending");

			leaseDetailsRepository.save(leaseDetails);

			return CommandProcessingResult.parsingResult(leaseDetails);
		}

		else if (leaseDetails.getStatus().equals("Registration_Pending")) {
			String result = command.stringValueOfParameterName("ActivationResult");
			if (result.equalsIgnoreCase("Success")) {
				String deviceId = command.stringValueOfParameterName("deviceId");
				String voucherId = command.stringValueOfParameterName("voucherId");
				leaseDetails.setDevice(deviceId);
				leaseDetails.setVoucher(voucherId);
				leaseDetails.setStatus("Registration_Success");
			}
			if (result.equalsIgnoreCase("Failed")) {
				leaseDetails.setStatus("Registration_Failed");
			}
			leaseDetailsRepository.save(leaseDetails);
			return CommandProcessingResult.parsingResult(leaseDetails);
		} else if (leaseDetails.getStatus().equals("Registration_Success")) {
			return CommandProcessingResult.parsingResult(leaseDetails);
		} else {
			throw new SomethingWentWrongException("something went wrong please contact support team");
		}

	}

	@Override
	public CommandProcessingResult ResendOtpMessage(JsonCommand command) {
		try {
			String mobileNO = command.stringValueOfParameterName("mobileNO");
			System.out.println("ActivationProcessWritePlatformServiceJpaRepositoryImpl.ResendOtpMessage()");
			LeaseDetails leaseDetails = leaseDetailsRepository.findLeaseDetailsByMobileNo(mobileNO);

			if (leaseDetails == null) {
				throw new LeaseDetailsNotFoundException(mobileNO);
			}

			String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
			leaseDetails.setOtp(otp);
			try {
				ResponseEntity<String> result = this.OTP_MESSAGE(leaseDetails.getMobileNumber(), otp);

				if (!result.getStatusCode().equals(HttpStatus.OK)) {
					throw new OTPNOTVerificationException(result.getBody());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			leaseDetailsRepository.saveAndFlush(leaseDetails);
			return CommandProcessingResult.parsingResult(leaseDetails);

		} catch (Exception e) {
			e.printStackTrace();
			return new CommandProcessingResult(e.getLocalizedMessage());
		}
	}

	@Override
	public String saveImage(String base64Encoder) {
		String[] strings = base64Encoder.split(",");
		String extension;
		switch (strings[0]) {
		case "data:image/jpeg;base64":
			extension = ".jpeg";
			break;
		case "data:image/png;base64":
			extension = ".png";
			break;
		default:
			extension = ".jpg";
			break;
		}
		// convert base64 string to binary data
		byte[] data = DatatypeConverter.parseBase64Binary(strings[1]);
		String currentDirectory = System.getProperty("user.dir");
		currentDirectory = currentDirectory.substring(0, currentDirectory.lastIndexOf("/"));

		String uniqueNumber = this.OTP();
		String path = currentDirectory + "/webapps/Verification_Images/leaseImage" + uniqueNumber + extension;
		File file = new File(path);
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			outputStream.write(data);
			System.out.println("ActivationProcessWritePlatformServiceJpaRepositoryImpl.saveImage()" + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String imagePath = "https://52.22.65.59:8877/Verification_Images/leaseImage" + uniqueNumber + extension;
		return imagePath;
	}

	public String OTP() {
		String numbers = "0123456789";
		Random rndm_method = new Random();
		char[] otp = new char[4];
		for (int i = 0; i < 4; i++) {
			otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
		}
		return otp.toString();
	}

	public static void main(String args[]) {
		ActivationProcessWritePlatformService.OTP_MESSAGEs("8135742906", "123456");
	}
}