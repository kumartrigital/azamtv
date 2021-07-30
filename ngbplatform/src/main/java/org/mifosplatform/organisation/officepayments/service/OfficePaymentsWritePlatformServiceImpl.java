package org.mifosplatform.organisation.officepayments.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepository;
import org.mifosplatform.organisation.officepayments.domain.OfficePayments;
import org.mifosplatform.organisation.officepayments.domain.OfficePaymentsRepository;
import org.mifosplatform.organisation.officepayments.exception.PaymentOfficeDetailsNotFoundException;
import org.json.JSONObject;
import org.mifosplatform.cms.journalvoucher.domain.JournalVoucher;
import org.mifosplatform.cms.journalvoucher.domain.JournalVoucherDetails;
import org.mifosplatform.cms.journalvoucher.domain.JournalvoucherDetailsRepository;
import org.mifosplatform.cms.journalvoucher.domain.JournalvoucherRepository;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.clientbalance.data.ConverationDetails;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceWritePlatformService;
import org.mifosplatform.finance.creditdistribution.domain.CreditDistribution;
import org.mifosplatform.finance.creditdistribution.domain.CreditDistributionRepository;
import org.mifosplatform.finance.officebalance.domain.OfficeBalance;
import org.mifosplatform.finance.officebalance.domain.OfficeBalanceRepository;
import org.mifosplatform.finance.payments.domain.Payment;
import org.mifosplatform.finance.payments.exception.CurrencyDetailsNotFoundException;
import org.mifosplatform.finance.payments.exception.PaymentDetailsNotFoundException;
import org.mifosplatform.finance.payments.exception.ReceiptNoDuplicateException;
import org.mifosplatform.organisation.officepayments.serialization.OfficePaymentsCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hugo
 *
 */
@Service
public class OfficePaymentsWritePlatformServiceImpl implements OfficePaymentsWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(OfficePaymentsWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final OfficePaymentsRepository officePaymentsRepository;
	private final OfficePaymentsCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final OfficeBalanceRepository officeBalanceRepository;
	private final CrmServices crmServices;
	private final CreditDistributionRepository creditDistributionRepository;
	private final JournalvoucherRepository journalvoucherRepository;
	private final JournalvoucherDetailsRepository journalvoucherDetailsRepository;
	private final ConfigurationRepository configurationRepository;
	private final ApplicationCurrencyRepository applicationCurrencyRepository;
	private final ClientBalanceWritePlatformService clientBalanceWritePlatformService;

	@Autowired
	public OfficePaymentsWritePlatformServiceImpl(final PlatformSecurityContext context,
			final OfficePaymentsRepository officePaymentsRepository,
			final OfficePaymentsCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final OfficeBalanceRepository officeBalanceRepository, final CrmServices crmServices,
			final CreditDistributionRepository creditDistributionRepository,
			final JournalvoucherRepository journalvoucherRepository,
			final JournalvoucherDetailsRepository journalvoucherDetailsRepository,
			final ConfigurationRepository configurationRepository,
			final ApplicationCurrencyRepository applicationCurrencyRepository,
			final ClientBalanceWritePlatformService clientBalanceWritePlatformService) {

		this.context = context;
		this.officePaymentsRepository = officePaymentsRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.officeBalanceRepository = officeBalanceRepository;
		this.crmServices = crmServices;
		this.creditDistributionRepository = creditDistributionRepository;
		this.journalvoucherRepository = journalvoucherRepository;
		this.journalvoucherDetailsRepository = journalvoucherDetailsRepository;
		this.configurationRepository = configurationRepository;
		this.applicationCurrencyRepository = applicationCurrencyRepository;
		this.clientBalanceWritePlatformService = clientBalanceWritePlatformService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * #createOfficePayment(org.mifosplatform.infrastructure.core.api.JsonCommand)
	 */
	@Transactional
	@Override
	public CommandProcessingResult createOfficePayment(final JsonCommand command) {
//add currency code
		try {
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			this.crmServices.createOfficePayment(command);
			final OfficePayments officePayments = OfficePayments.fromJson(command);

			ApplicationCurrency applicationCurrency = applicationCurrencyRepository
					.findOneByCode(command.stringValueOfParameterName("currencyCode"));

			if (applicationCurrency == null) {
				throw new CurrencyDetailsNotFoundException(command.stringValueOfParameterName("currencyCode"));
			}
			officePayments.setCurrencyId(applicationCurrency);

			ApplicationCurrency basecurrency = applicationCurrencyRepository.findOneByCode("NGN");

			if (!basecurrency.getCode().equals(applicationCurrency.getCode())) {

				ConverationDetails conversionChargesValue = clientBalanceWritePlatformService.conversionDetails(
						basecurrency.getId(), officePayments.getCurrencyId().getId(), officePayments.getAmountPaid());

				officePayments.setAmountPaid(conversionChargesValue.getConveratedAmount());

				JSONObject conversionObject = new JSONObject();

				try {
					conversionObject.put("baseCurrency", "NGN");
					conversionObject.put("conversionCurrency", officePayments.getCurrencyId().getCode());
					conversionObject.put("ConverationRate", conversionChargesValue.getConverationRate());
					conversionObject.put("amount", conversionChargesValue.getPrice());
					conversionObject.put("conversionAmount", conversionChargesValue.getConveratedAmount());

					officePayments.setConversionCharges(conversionObject.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			this.officePaymentsRepository.save(officePayments);

			final String collectorName = command.stringValueOfParameterNamed("collectorName");
			final Long collectionBy = command.longValueOfParameterNamed("collectionBy");
			final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amountPaid");

			if (!collectorName.equalsIgnoreCase("Service Provider")) {
				JournalVoucher journalVoucher = new JournalVoucher(DateUtils.getDateOfTenant(), "officePayments");
				this.journalvoucherRepository.saveAndFlush(journalVoucher);
				JournalVoucherDetails journalVoucherDetail = new JournalVoucherDetails(journalVoucher.getId(),
						officePayments.getOfficeId().toString(), "Entity", "Debit", "officePayments",
						amountPaid.doubleValue());
				JournalVoucherDetails journalVoucherDetails = new JournalVoucherDetails(journalVoucher.getId(),
						collectionBy.toString(), "Entity", "Credit", "officePayments", amountPaid.doubleValue());
				this.journalvoucherDetailsRepository.saveAndFlush(journalVoucherDetail);
				this.journalvoucherDetailsRepository.saveAndFlush(journalVoucherDetails);
			}

			if (collectionBy != null) {
				OfficeBalance officeBalance = this.officeBalanceRepository.findOneByOfficeId(collectionBy);

				if (officeBalance != null) {
					officeBalance.updateBalance("CREDIT", officePayments.getAmountPaid());

				} else if (officeBalance == null) {

					BigDecimal balance = BigDecimal.ZERO.subtract(officePayments.getAmountPaid());
					officeBalance = OfficeBalance.create(collectionBy, balance);
				}
				this.officeBalanceRepository.saveAndFlush(officeBalance);
			}

			if (officePayments.getOfficeId() != null) {
				OfficeBalance officeBalances = this.officeBalanceRepository
						.findOneByOfficeId(officePayments.getOfficeId());

				if (officeBalances != null) {
					officeBalances.updateBalance("CREDIT", officePayments.getAmountPaid());

				} else if (officeBalances == null) {

					BigDecimal balance = BigDecimal.ZERO.subtract(officePayments.getAmountPaid());
					officeBalances = OfficeBalance.create(officePayments.getOfficeId(), balance);
				}
				this.officeBalanceRepository.saveAndFlush(officeBalances);
			}
			// }

			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(officePayments.getId()).withOfficeId(command.entityId()).build();
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("receipt_no")) {
			final String name = command.stringValueOfParameterNamed("receiptNo");
			throw new PlatformDataIntegrityException("error.msg.officePayment_receiptNo.duplicate.name",
					"A Receipt Number with this Code'" + name + "'already exists", "receiptNo", name);
		}

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());

	}

	@Override
	public CommandProcessingResult cancelofficepayment(JsonCommand command, final Long paymentId) {

		try {
			this.fromApiJsonDeserializer.validateForCancel(command.json());
			this.crmServices.cancelPaymentforOffice(command);
			final OfficePayments officePayments = this.officePaymentsRepository.findOne(paymentId);
			if (officePayments == null) {
				throw new PaymentOfficeDetailsNotFoundException(paymentId.toString());
			}
			final OfficePayments cancelPay = new OfficePayments(officePayments.getofficeId(),
					officePayments.getAmountPaid(), DateUtils.getLocalDateOfTenant(), officePayments.getRemarks(),
					officePayments.getPaymodeId(), officePayments.getReceiptNo(), officePayments.isWallet(),
					officePayments.getId(), officePayments.getCollectionBy());
			cancelPay.cancelPayment(command);
			this.officePaymentsRepository.save(cancelPay);
			officePayments.cancelPayment(command);
			this.officePaymentsRepository.save(officePayments);
			final OfficeBalance officeBalance = officeBalanceRepository.findOneByOfficeId(officePayments.getOfficeId());
			BigDecimal initialBalance = officeBalance.getBalanceAmount();

			if (officeBalance.getBalanceAmount().intValue() < 0) {
				officeBalance.updateBalance("DEBIT", officePayments.getAmountPaid());
				initialBalance = officeBalance.getBalanceAmount().add(initialBalance.abs());
				officeBalance.setBalanceAmount(initialBalance);
				this.officeBalanceRepository.saveAndFlush(officeBalance);

			} else {
				officeBalance.updateBalance("DEBIT", officePayments.getAmountPaid());
				this.officeBalanceRepository.saveAndFlush(officeBalance);
			}
			final OfficeBalance collectedByOfficeBalance = officeBalanceRepository
					.findOneByOfficeId(officePayments.getCollectionBy());
			if (collectedByOfficeBalance != null)
				collectedByOfficeBalance.updateBalance("DEBIT", officePayments.getAmountPaid());
			this.officeBalanceRepository.saveAndFlush(collectedByOfficeBalance);

			return new CommandProcessingResult(cancelPay.getId(), officeBalance.getofficeId());

		} catch (DataIntegrityViolationException exception) {
			System.out.println("Exception Message ::" + exception + "Get Message ::" + exception.getClass());
			handleDataIntegrityIssues(command, exception);
			return CommandProcessingResult.empty();
		}

	}

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("receipt_no")) {
			throw new ReceiptNoDuplicateException(command.stringValueOfParameterNamed("receiptNo"));
		}

		LOGGER.error(dve.getMessage(), dve);
	}

}