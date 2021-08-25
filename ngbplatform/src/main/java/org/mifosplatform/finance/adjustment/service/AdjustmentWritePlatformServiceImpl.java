package org.mifosplatform.finance.adjustment.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.adjustment.api.AdjustmentApiResource;
import org.mifosplatform.finance.adjustment.domain.Adjustment;
import org.mifosplatform.finance.adjustment.domain.AdjustmentRepository;
import org.mifosplatform.finance.adjustment.serializer.AdjustmentCommandFromApiJsonDeserializer;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceReadPlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfo;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfoRepository;
import org.mifosplatform.organisation.partner.domain.PartnerBalanceRepository;
import org.mifosplatform.organisation.partner.domain.OfficeControlBalance;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientBillProfileInfo;
import org.mifosplatform.portfolio.client.domain.ClientBillProfileInfoRepository;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;

@Service
public class AdjustmentWritePlatformServiceImpl implements AdjustmentWritePlatformService {

	private final PlatformSecurityContext context;
	private final AdjustmentRepository adjustmentRepository;
	private final ClientBalanceRepository clientBalanceRepository;
	private final AdjustmentCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ClientRepository clientRepository;
	private final PartnerBalanceRepository partnerBalanceRepository;
	private final OfficeAdditionalInfoRepository infoRepository;
	private final CrmServices crmServices;
	private final OrderWritePlatformService orderWritePlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final ClientBillProfileInfoRepository clientBillProfileInfoRepository;
	private final AdjustmentApiResource adjustmentApiResource;

	@Autowired
	public AdjustmentWritePlatformServiceImpl(final PlatformSecurityContext context,
			final AdjustmentRepository adjustmentRepository, final ClientBalanceRepository clientBalanceRepository,
			final AdjustmentCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final ClientBalanceReadPlatformService clientBalanceReadPlatformService,
			final AdjustmentReadPlatformService adjustmentReadPlatformService, final ClientRepository clientRepository,
			final PartnerBalanceRepository partnerBalanceRepository,
			final OfficeAdditionalInfoRepository infoRepository, final CrmServices crmServices,
			final OrderWritePlatformService orderWritePlatformService, final FromJsonHelper fromJsonHelper,
			final ClientBillProfileInfoRepository clientBillProfileInfoRepository,
			final AdjustmentApiResource adjustmentApiResource) {

		this.context = context;
		this.adjustmentRepository = adjustmentRepository;
		this.clientBalanceRepository = clientBalanceRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.clientRepository = clientRepository;
		this.partnerBalanceRepository = partnerBalanceRepository;
		this.infoRepository = infoRepository;
		this.crmServices = crmServices;
		this.orderWritePlatformService = orderWritePlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.clientBillProfileInfoRepository = clientBillProfileInfoRepository;
		this.adjustmentApiResource = adjustmentApiResource;
	}

	@Transactional
	@Override
	public CommandProcessingResult createAdjustment(final JsonCommand command) {

		try {

			/* this.context.authenticatedUser(); */
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			Adjustment adjustment = Adjustment.fromJson(command);

			if (command.booleanPrimitiveValueOfParameterNamed("withtax")) {
				this.crmServices.billadjustment(command);
			} else {
				this.crmServices.adjustments(command);
			}
			ClientBalance clientBalance = null;
			BigDecimal balance = BigDecimal.ZERO;
			Long clientServiceId = Long.valueOf(0);
			clientBalance = clientBalanceRepository.findByClientAndClientServiceId(adjustment.getClientId(),
					Long.valueOf(0), adjustment.getCurrencyId());

			this.adjustmentRepository.saveAndFlush(adjustment);
			boolean isWalletPayment = command.booleanPrimitiveValueOfParameterNamed("isWalletPayment");
			ClientBillProfileInfo ClientBillProfileInfo = this.clientBillProfileInfoRepository
					.findwithclientId(adjustment.getClientId());
			JSONObject clientBalanceObject = new JSONObject();
			try {
				clientBalanceObject.put("clientId", adjustment.getClientId());
				clientBalanceObject.put("amount", adjustment.getAmountPaid());
				clientBalanceObject.put("isWalletEnable", isWalletPayment);
				clientBalanceObject.put("clientServiceId", clientServiceId);
				// clientBalanceObject.put("currencyId",ClientBillProfileInfo.getBillCurrency());
				clientBalanceObject.put("currencyId", adjustment.getCurrencyId());
				clientBalanceObject.put("paymentType", adjustment.getAdjustmentType());
				clientBalanceObject.put("locale", "en");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
			JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
					clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null, null, null,
					null, null);

			if (clientBalance != null) {
				clientBalance.updateBalance(clientBalanceCommand);
			} else if (clientBalance == null) {

				if ("CREDIT".equalsIgnoreCase(adjustment.getAdjustmentType())) {
					balance = BigDecimal.ZERO.subtract(adjustment.getAmountPaid());
				} else {
					balance = BigDecimal.ZERO.add(adjustment.getAmountPaid());
				}

				clientBalance = ClientBalance.create(adjustment.getClientId(), balance, isWalletPayment ? 'Y' : 'N',
						clientServiceId, ClientBillProfileInfo.getBillCurrency());
			}

			this.clientBalanceRepository.saveAndFlush(clientBalance);

			final Client client = this.clientRepository.findOne(adjustment.getClientId());
			final OfficeAdditionalInfo officeAdditionalInfo = this.infoRepository.findoneByoffice(client.getOffice());
			if (officeAdditionalInfo != null) {
				if (officeAdditionalInfo.getIsCollective()) {
					System.out.println(officeAdditionalInfo.getIsCollective());
					this.updatePartnerBalance(client.getOffice(), adjustment);
				}
			}

			// Notify Payment Adjustment
			this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_CREATE_PAYMENT_ADJ,
					adjustment.getClientId(), adjustment.getAmountPaid().toString(), null);

			return new CommandProcessingResult(adjustment.getId());
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private void updatePartnerBalance(final Office office, final Adjustment adjustment) {

		final String accountType = "ADJUSTMENTS";
		OfficeControlBalance partnerControlBalance = this.partnerBalanceRepository
				.findOneWithPartnerAccount(office.getId(), accountType);
		if (partnerControlBalance != null) {
			if (adjustment.getAdjustmentType().equalsIgnoreCase("CREDIT")) {
				partnerControlBalance.update(adjustment.getAmountPaid().negate(), office.getId());
			} else {
				partnerControlBalance.update(adjustment.getAmountPaid(), office.getId());
			}

		} else {
			if (adjustment.getAdjustmentType().equalsIgnoreCase("CREDIT")) {
				partnerControlBalance = OfficeControlBalance.create(adjustment.getAmountPaid().negate(), accountType,
						office.getId());
			} else {
				partnerControlBalance = OfficeControlBalance.create(adjustment.getAmountPaid(), accountType,
						office.getId());
			}
		}
		this.partnerBalanceRepository.save(partnerControlBalance);
	}

	@Transactional
	@Override
	public CommandProcessingResult createjvAdjustment(final JsonCommand command) {

		String fromClient = command.stringValueOfParameterName("fromClient");
		String toClient = command.stringValueOfParameterName("toClient");
		String amount = command.stringValueOfParameterName("amount");
		Long currencyId = command.longValueOfParameterNamed("currencyId");
		String dateFormat = "dd MMMM yyyy";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		net.sf.json.JSONObject debitAdjustment = new net.sf.json.JSONObject();
		debitAdjustment.put("adjustment_type", "DEBIT");
		debitAdjustment.put("withtax", false);
		debitAdjustment.put("locale", "en");
		debitAdjustment.put("dateFormat",dateFormat);
		debitAdjustment.put("billpoId", null);
		debitAdjustment.put("amount_paid", amount);
		debitAdjustment.put("Remarks", "jv transaction from client :" + fromClient + "to :" + toClient);
		debitAdjustment.put("currencyId", currencyId);
		debitAdjustment.put("adjustment_code", 28);
		debitAdjustment.put("adjustment_date", formatter.format(new Date()));

		

		adjustmentApiResource.addNewAdjustment(Long.parseLong(fromClient), debitAdjustment.toString());

		net.sf.json.JSONObject creditAdjustment = new net.sf.json.JSONObject();
		creditAdjustment.put("adjustment_type", "CREDIT");
		creditAdjustment.put("withtax", false);
		creditAdjustment.put("locale", "en");
		creditAdjustment.put("dateFormat", "dd MMMM yyyy");
		creditAdjustment.put("billpoId", null);
		creditAdjustment.put("amount_paid", amount);
		creditAdjustment.put("Remarks", "jv transaction from client :" + fromClient + "to :" + toClient);
		creditAdjustment.put("currencyId", currencyId);
		creditAdjustment.put("adjustment_code", 28);
		creditAdjustment.put("adjustment_date", formatter.format(new Date()));

		adjustmentApiResource.addNewAdjustment(Long.parseLong(toClient), creditAdjustment.toString());

		return new CommandProcessingResult("success");
	}

}
