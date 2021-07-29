/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.service;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.LocaleUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.billingmaster.domain.BillMaster;
import org.mifosplatform.infrastructure.codes.domain.Code;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepository;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.security.exception.NoAuthorizationException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.domain.Country;
import org.mifosplatform.organisation.address.domain.CountryRepository;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepository;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrencyRepositoryWrapper;
import org.mifosplatform.organisation.monetary.domain.MonetaryCurrency;
import org.mifosplatform.organisation.monetary.domain.Money;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAddress;
import org.mifosplatform.organisation.office.domain.OfficeAddressRepository;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.domain.OfficeTransaction;
import org.mifosplatform.organisation.office.domain.OfficeTransactionRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.office.serialization.OfficeCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.office.serialization.OfficeTransactionCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.client.api.ClientsApiResource;
import org.mifosplatform.portfolio.client.domain.AccountNumberGenerator;
import org.mifosplatform.portfolio.client.domain.AccountNumberGeneratorFactory;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class OfficeWritePlatformServiceJpaRepositoryImpl implements OfficeWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(OfficeWritePlatformServiceJpaRepositoryImpl.class);

	private final PlatformSecurityContext context;
	private final OfficeCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final OfficeTransactionCommandFromApiJsonDeserializer moneyTransferCommandFromApiJsonDeserializer;
	private final OfficeRepository officeRepository;
	private final OfficeTransactionRepository officeTransactionRepository;
	private final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository;
	private final OfficeAddressRepository addressRepository;
	private final CrmServices crmServices;
	private final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory;
	private final CodeValueRepository codeValueRepository;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final ClientsApiResource clientsApiResource;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final ConfigurationRepository configurationRepository;
	private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	private final ClientServiceRepository clientServiceRepository;
	private final TenantAwareRoutingDataSource dataSource;
	private final ApplicationCurrencyRepository applicationCurrencyRepo;
	private final CountryRepository countryRepository;

	@Autowired
	public OfficeWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
			final OfficeCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final OfficeTransactionCommandFromApiJsonDeserializer moneyTransferCommandFromApiJsonDeserializer,
			final OfficeRepository officeRepository, final OfficeTransactionRepository officeMonetaryTransferRepository,
			final ApplicationCurrencyRepositoryWrapper applicationCurrencyRepository,
			final OfficeAddressRepository addressRepository, final CrmServices crmServices,
			final AccountNumberGeneratorFactory accountIdentifierGeneratorFactory,
			final CodeValueRepository codeValueRepository, final OfficeReadPlatformService officeReadPlatformService,
			final ClientsApiResource clientsApiResource, final ConfigurationRepository configurationRepository,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final ClientServiceReadPlatformService clientServiceReadPlatformService,
			final ClientServiceRepository clientServiceRepository, final TenantAwareRoutingDataSource dataSource,
			final ApplicationCurrencyRepository applicationCurrencyRepo, final CountryRepository countryRepository) {

		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.moneyTransferCommandFromApiJsonDeserializer = moneyTransferCommandFromApiJsonDeserializer;
		this.officeRepository = officeRepository;
		this.officeTransactionRepository = officeMonetaryTransferRepository;
		this.applicationCurrencyRepository = applicationCurrencyRepository;
		this.addressRepository = addressRepository;
		this.crmServices = crmServices;
		this.accountIdentifierGeneratorFactory = accountIdentifierGeneratorFactory;
		this.codeValueRepository = codeValueRepository;
		this.officeReadPlatformService = officeReadPlatformService;
		this.clientsApiResource = clientsApiResource;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.configurationRepository = configurationRepository;
		this.clientServiceReadPlatformService = clientServiceReadPlatformService;
		this.clientServiceRepository = clientServiceRepository;
		this.dataSource = dataSource;
		this.applicationCurrencyRepo = applicationCurrencyRepo;
		this.countryRepository = countryRepository;
	}

	@Transactional
	@Override
	public CommandProcessingResult createOffice(final JsonCommand command) {

		try {

			// Creating client based on office Data
			JSONObject json = new JSONObject(command.json());
			JSONObject clientJson = (JSONObject) json.opt("clientData");
			String officeType = command.stringValueOfParameterName("officeType");
			CommandProcessingResult result1 = null;
			final Configuration configuration = this.configurationRepository
					.findOneByName(ConfigurationConstants.CONFIG_IS_OFFICE_CLIENT_ENABLE);
			if (configuration.isEnabled()) {
				if (clientJson != null) {
					final CommandWrapper commandRequest = new CommandWrapperBuilder().createClient()
							.withJson(clientJson.toString()).build();
					result1 = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

				}
				this.clientServiceReadPlatformService.insertclientServiceDetail(result1.getResourceId());

				List<ClientService> newClientServices = this.clientServiceRepository
						.findwithClientId(result1.getResourceId());
				for (ClientService newClientService : newClientServices) {
					this.clientServiceReadPlatformService.insertServiceParametersDetail(newClientService.getClientId(),
							newClientService.getId());
				}

			}

			// ended

			final AppUser currentUser = context.authenticatedUser();

			this.fromApiJsonDeserializer.validateForCreate(command.json());

			Long parentId = null;
			if (command.parameterExists("parentId")) {
				parentId = command.longValueOfParameterNamed("parentId");
			}

			final Office parent = validateUserPriviledgeOnOfficeAndRetrieve(currentUser, parentId);
			Office office = Office.fromJson(parent, command);
			if (result1 != null) {
				office.setClientId(result1.getResourceId());
			}
			CommandProcessingResult result = this.crmServices.createOffice(command);
			if (result != null) {
				office.setPoId(result.getResourceIdentifier());
				office.setSettlementPoId(result.getTransactionId());
			}
			OfficeAddress address = OfficeAddress.fromJson(command, office);
			office.setOfficeAddress(address);
			office.setDasType(this.codeValueRepository.findOne(command.longValueOfParameterNamed("dasType")));
			// pre save to generate id for use in office hierarchy

			Country country = countryRepository.findOneByCountryName(command.stringValueOfParameterNamed("country"));

			ApplicationCurrency currency = applicationCurrencyRepo.findOneByCode(country.getCountryCode());

			office.setCurrencyId(currency);
			this.officeRepository.saveAndFlush(office);

			office.generateHierarchy();

			/*
			 * final AccountNumberGenerator accountNoGenerator =
			 * this.accountIdentifierGeneratorFactory.determineOfficeAccountNoGenerator(
			 * office.getId()); String officeType =
			 * command.stringValueOfParameterName("officeType");
			 */
			office.updateExternalId(this.officeReadPlatformService.retriveMaxCountId(office.getOfficeType(),
					office.getParent().getHierarchy()));
			this.officeRepository.saveAndFlush(office);
			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(office.getId()) //
					.withOfficeId(office.getId()) //
					.build();
		} catch (DataIntegrityViolationException dve) {
			handleOfficeDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		} catch (JSONException e) {
			return null;
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateOffice(final Long officeId, final JsonCommand command) {

		try {
			final AppUser currentUser = context.authenticatedUser();

			this.fromApiJsonDeserializer.validateForUpdate(command.json());

			Long parentId = null;
			if (command.parameterExists("parentId")) {
				parentId = command.longValueOfParameterNamed("parentId");
			}

			final Office office = validateUserPriviledgeOnOfficeAndRetrieve(currentUser, officeId);

			this.crmServices.updateOffice(command);

			final Map<String, Object> changes = office.update(command);

			if (changes.containsKey("parentId")) {
				final Office parent = validateUserPriviledgeOnOfficeAndRetrieve(currentUser, parentId);
				office.update(parent);
			}

			if (changes.containsKey("dasType")) {
				office.setDasType(this.codeValueRepository.findOne(command.longValueOfParameterNamed("dasType")));
			}

			// update officeAddress
			final OfficeAddress officeAddress = this.addressRepository.findOneWithPartnerId(office);
			final Map<String, Object> addressChanges = officeAddress.update(command);

			if (!addressChanges.isEmpty()) {
				office.setOfficeAddress(officeAddress);
			}

			if (!changes.isEmpty()) {
				this.officeRepository.saveAndFlush(office);
			}

			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(office.getId()) //
					.withOfficeId(office.getId()) //
					.with(changes) //
					.build();
		} catch (DataIntegrityViolationException dve) {
			handleOfficeDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult officeTransaction(final JsonCommand command) {

		context.authenticatedUser();

		this.moneyTransferCommandFromApiJsonDeserializer.validateOfficeTransfer(command.json());

		Long officeId = null;
		Office fromOffice = null;
		final Long fromOfficeId = command.longValueOfParameterNamed("fromOfficeId");
		if (fromOfficeId != null) {
			fromOffice = this.officeRepository.findOne(fromOfficeId);
			officeId = fromOffice.getId();
		}
		Office toOffice = null;
		final Long toOfficeId = command.longValueOfParameterNamed("toOfficeId");
		if (toOfficeId != null) {
			toOffice = this.officeRepository.findOne(toOfficeId);
			officeId = toOffice.getId();
		}

		if (fromOffice == null && toOffice == null) {
			throw new OfficeNotFoundException(toOfficeId);
		}

		final String currencyCode = command.stringValueOfParameterNamed("currencyCode");
		final ApplicationCurrency appCurrency = this.applicationCurrencyRepository
				.findOneWithNotFoundDetection(currencyCode);

		final MonetaryCurrency currency = new MonetaryCurrency(appCurrency.getCode(), appCurrency.getDecimalPlaces());
		final Money amount = Money.of(currency, command.bigDecimalValueOfParameterNamed("transactionAmount"));

		final OfficeTransaction entity = OfficeTransaction.fromJson(fromOffice, toOffice, amount, command);

		this.officeTransactionRepository.save(entity);

		return new CommandProcessingResultBuilder() //
				.withCommandId(command.commandId()) //
				.withEntityId(entity.getId()) //
				.withOfficeId(officeId) //
				.build();
	}

	@Transactional
	@Override
	public CommandProcessingResult deleteOfficeTransaction(final Long transactionId, final JsonCommand command) {

		context.authenticatedUser();

		this.officeTransactionRepository.delete(transactionId);

		return new CommandProcessingResultBuilder() //
				.withCommandId(command.commandId()) //
				.withEntityId(transactionId) //
				.build();
	}

	/*
	 * Guaranteed to throw an exception no matter what the data integrity issue is.
	 */
	private void handleOfficeDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("externalid_org")) {
			final String externalId = command.stringValueOfParameterNamed("externalId");
			throw new PlatformDataIntegrityException("error.msg.office.duplicate.externalId",
					"Office with externalId `" + externalId + "` already exists", "externalId", externalId);
		} else if (realCause.getMessage().contains("name_org")) {
			final String name = command.stringValueOfParameterNamed("name");
			throw new PlatformDataIntegrityException("error.msg.office.duplicate.name",
					"Office with name `" + name + "` already exists", "name", name);
		}

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.office.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

	/*
	 * used to restrict modifying operations to office that are either the users
	 * office or lower (child) in the office hierarchy
	 */
	public Office validateUserPriviledgeOnOfficeAndRetrieve(final AppUser currentUser, final Long officeId) {

		final Long userOfficeId = currentUser.getOffice().getId();
		final Office userOffice = this.officeRepository.findOne(userOfficeId);
		if (userOffice == null) {
			throw new OfficeNotFoundException(userOfficeId);
		}

		if (userOffice.doesNotHaveAnOfficeInHierarchyWithId(officeId)) {
			throw new NoAuthorizationException(
					"User does not have sufficient priviledges to act on the provided office.");
		}

		Office officeToReturn = userOffice;
		if (!userOffice.identifiedBy(officeId)) {
			officeToReturn = this.officeRepository.findOne(officeId);
			if (officeToReturn == null) {
				throw new OfficeNotFoundException(officeId);
			}
		}

		return officeToReturn;
	}

	@Transactional
	@Override
	public String generatePartnerPdf(final Long officeId) {

		final String fileLocation = FileUtils.MIFOSX_BASE_DIR;
		/** Recursively create the directory if it does not exist **/
		if (!new File(fileLocation).isDirectory()) {
			new File(fileLocation).mkdirs();
		}
		final String PaymentDetailsLocation = fileLocation + File.separator + "StatementPdfFiles";
		if (!new File(PaymentDetailsLocation).isDirectory()) {
			new File(PaymentDetailsLocation).mkdirs();
		}
		final String printquoteLocation = PaymentDetailsLocation + File.separator + officeId + "_"
				+ DateUtils.getLocalDateOfTenant() + ".pdf";
		final Long id = Long.valueOf(officeId.toString());
		try {

			final String jpath = fileLocation + File.separator + "jasper";
			final MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
			final String jasperfilepath = jpath + File.separator + "Entity_" + tenant.getTenantIdentifier() + ".jasper";
			File destinationFile = new File(jasperfilepath);
			if (!destinationFile.exists()) {
				File sourceFile = new File(
						this.getClass().getClassLoader().getResource("Files/Entity.jasper").getFile());
				FileUtils.copyFileUsingApacheCommonsIO(sourceFile, destinationFile);
			}
			final Connection connection = this.dataSource.getConnection();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("officeId", id);
			parameters.put(JRParameter.REPORT_LOCALE, getLocale(tenant));
			/* This realPath parameter holds the location path of company image #rakesh# */
			parameters.put("realPath", this.getClass().getClassLoader().getResource("Files").getFile());
			final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperfilepath, parameters, connection);
			JasperExportManager.exportReportToPdfFile(jasperPrint, printquoteLocation);
			connection.close();
			System.out.println("Filling report successfully...");

		} catch (final DataIntegrityViolationException ex) {
			LOGGER.error("Filling report failed...\r\n" + ex.getLocalizedMessage());
			System.out.println("Filling report failed...");
			ex.printStackTrace();
		} catch (final JRException | JRRuntimeException e) {
			LOGGER.error("Filling report failed...\r\n" + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
			e.printStackTrace();
		} catch (final Exception e) {
			LOGGER.error("Filling report failed...\r\n" + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
			e.printStackTrace();
		}
		return printquoteLocation;
	}

	/**
	 * @param tenant
	 * @return Locale
	 */
	public Locale getLocale(MifosPlatformTenant tenant) {

		Locale locale = LocaleUtils.toLocale(tenant.getLocaleName());
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}

}