/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.mifosplatform.billing.chargecode.service.ChargeCodeReadPlatformService;
import org.mifosplatform.billing.emun.service.EnumReadplaformService;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.billing.selfcare.service.SelfCareRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.exception.UnrecognizedQueryParamException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberNotFoundException;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.monetary.service.OrganisationCurrencyReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.allocation.service.AllocationReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientAdditionalData;
import org.mifosplatform.portfolio.client.data.ClientBillInfoData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.client.service.BillSupressionProfileInfoReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientBillInfoReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientCategoryData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.client.service.GroupData;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clients")
@Component
@Scope("singleton")
public class ClientsApiResource {

	private static final Set<String> RESPONSE_DATA_PARAMETERS = null;
	private final PlatformSecurityContext context;
	private final ClientReadPlatformService clientReadPlatformService;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
	private final ToApiJsonSerializer<ClientAdditionalData> jsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	private final AllocationReadPlatformService allocationReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	private final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository;
	private final SelfCareRepository selfCareRepository;
	private final MCodeReadPlatformService codeReadPlatformService;
	private final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService;
	private final CrmServices crmServices;
	private final OrganisationCurrencyReadPlatformService currencyReadPlatformService;
	private final ChargeCodeReadPlatformService chargeCodeReadPlatformService;
	private final BillSupressionProfileInfoReadPlatformService billSupressionProfileInfoReadPlatformService;
	private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	private final OrderReadPlatformService orderReadPlatformService;
	private final EnumReadplaformService enumReadplaformService;

	@Autowired
	public ClientsApiResource(final PlatformSecurityContext context,
			final ClientReadPlatformService readPlatformService,
			final OfficeReadPlatformService officeReadPlatformService,
			final ToApiJsonSerializer<ClientData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final AddressReadPlatformService addressReadPlatformService,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final AllocationReadPlatformService allocationReadPlatformService,
			final ConfigurationRepository configurationRepository,
			final PaymentGatewayConfigurationRepository gatewayConfigurationRepository,
			final SelfCareRepository selfCareRepository, final MCodeReadPlatformService mCodeReadPlatformService,
			final ToApiJsonSerializer<ClientAdditionalData> jsonSerializer,
			final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService, final CrmServices crmServices,
			final OrganisationCurrencyReadPlatformService currencyReadPlatformService,
			final ChargeCodeReadPlatformService chargeCodeReadPlatformService,
			final BillSupressionProfileInfoReadPlatformService billSupressionProfileInfoReadPlatformService,
			final ClientServiceReadPlatformService clientServiceReadPlatformService,
			final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,
			final OrderReadPlatformService orderReadPlatformService,
			final EnumReadplaformService enumReadplaformService) {

		this.context = context;
		this.paymentGatewayConfigurationRepository = gatewayConfigurationRepository;
		this.clientReadPlatformService = readPlatformService;
		this.officeReadPlatformService = officeReadPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.addressReadPlatformService = addressReadPlatformService;
		this.allocationReadPlatformService = allocationReadPlatformService;
		this.codeReadPlatformService = mCodeReadPlatformService;
		this.configurationRepository = configurationRepository;
		this.selfCareRepository = selfCareRepository;
		this.jsonSerializer = jsonSerializer;
		this.clientBillInfoReadPlatformService = clientBillInfoReadPlatformService;
		this.crmServices = crmServices;
		this.currencyReadPlatformService = currencyReadPlatformService;
		this.chargeCodeReadPlatformService = chargeCodeReadPlatformService;
		this.billSupressionProfileInfoReadPlatformService = billSupressionProfileInfoReadPlatformService;
		this.clientServiceReadPlatformService = clientServiceReadPlatformService;
		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;
		this.orderReadPlatformService = orderReadPlatformService;
		this.enumReadplaformService = enumReadplaformService;

	}

	/**
	 * this method is using for getting template data to create a client
	 */
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveTemplate(@Context final UriInfo uriInfo,
			@QueryParam("commandParam") final String commandParam) {

		context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
		ClientData clientData = this.clientReadPlatformService.retrieveTemplate();
		if (is(commandParam, "close")) {
			clientData = this.clientReadPlatformService
					.retrieveAllClosureReasons(ClientApiConstants.CLIENT_CLOSURE_REASON);
		}
		final Configuration configurationProperty = this.configurationRepository
				.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_LOGIN);
		clientData.setConfigurationProperty(configurationProperty);
		clientData = handleAddressTemplateData(clientData);
		clientData.setPreferences(this.codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_PREFERENCE));
		clientData.setIdProofs(this.codeReadPlatformService.getCodeValue("Identification Proofs"));
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, clientData,
				ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
	}

	@GET
	@Path("additionalinfo/template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveClientAdditionalInfoTemplate(@Context final UriInfo uriInfo,
			@QueryParam("commandParam") final String commandParam) {

		Collection<MCodeData> genderDatas = this.codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_GENDER);
		Collection<MCodeData> nationalityDatas = this.codeReadPlatformService
				.getCodeValue(CodeNameConstants.CODE_NATIONALITY);
		Collection<MCodeData> customeridentificationDatas = this.codeReadPlatformService
				.getCodeValue(CodeNameConstants.CODE_CUSTOMER_IDENTIFIER);
		Collection<MCodeData> cummunitcationDatas = this.codeReadPlatformService
				.getCodeValue(CodeNameConstants.CODE_PREFER_COMMUNICATION);
		Collection<MCodeData> languagesDatas = this.codeReadPlatformService
				.getCodeValue(CodeNameConstants.CODE_PREFER_LANG);
		Collection<MCodeData> ageGroupDatas = this.codeReadPlatformService
				.getCodeValue(CodeNameConstants.CODE_AGE_GROUP);
		ClientAdditionalData clientAdditionalData = new ClientAdditionalData(genderDatas, nationalityDatas,
				customeridentificationDatas, cummunitcationDatas, languagesDatas, ageGroupDatas);

		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.jsonSerializer.serialize(settings, clientAdditionalData,
				ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
	}

	private ClientData handleAddressTemplateData(final ClientData clientData) {

		final List<String> countryData = this.addressReadPlatformService.retrieveCountryDetails();
		final List<String> statesData = this.addressReadPlatformService.retrieveStateDetails();
		final List<String> citiesData = this.addressReadPlatformService.retrieveCityDetails();
		final List<EnumOptionData> enumOptionDatas = this.addressReadPlatformService.addressType();
		final List<String> districtData = this.addressReadPlatformService.retrieveDistrictDetails();
		final AddressData data = new AddressData(null, countryData, statesData, citiesData, enumOptionDatas,
				districtData);
		clientData.setAddressTemplate(data);
		return clientData;
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAll(@QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("clientId") final Long clientId, @QueryParam("firstName") final String firstname,
			@QueryParam("lastName") final String lastname, @QueryParam("email") final String email,
			@QueryParam("phone") final String phone, @QueryParam("city") final String city,
			@QueryParam("officeId") final Long officeId, @QueryParam("categoryType") final String categoryType,
			@QueryParam("externalId") final String externalId,@QueryParam("offset") final Integer offset,@QueryParam("limit") final Integer limit) {

		this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
		final SearchParameters searchParameters = SearchParameters.newforClients(sqlSearch, officeId, externalId,
				firstname, lastname, email, phone, city, categoryType, clientId, offset,limit);
		final Page<ClientData> clientData = this.clientReadPlatformService.retrieveAllClients(searchParameters);
		return this.toApiJsonSerializer.serialize(clientData);
	}

	/**
	 * this method is using for getting template data in editing a client
	 * 
	 * @throws JSONException
	 */
	@SuppressWarnings("unused")
	@GET
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveOne(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo)
			throws JSONException {

		this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
		Configuration configurationProperty = this.configurationRepository
				.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_BALANCE_CHECK);
		ClientData clientData = this.clientReadPlatformService.retrieveOne(clientId);
		String balanceCheck = "N";
		if (configurationProperty.isEnabled()) {
			balanceCheck = "Y";
		}
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		if (settings.isTemplate()) {

			final List<OfficeData> allowedOffices = new ArrayList<OfficeData>(
					officeReadPlatformService.retrieveAllOfficesForDropdown());
			final Collection<ClientCategoryData> categoryDatas = this.clientReadPlatformService
					.retrieveClientCategories();
			final Collection<GroupData> groupDatas = this.clientReadPlatformService.retrieveGroupData();
			final List<String> allocationDetailsDatas = this.allocationReadPlatformService
					.retrieveHardWareDetails(clientId);
			clientData = ClientData.templateOnTop(clientData, allowedOffices, categoryDatas, groupDatas,
					allocationDetailsDatas, null);
			clientData.setAddressData(this.addressReadPlatformService.retrieveAddressDetailsBy(clientData.getId(),
					clientData.addressType));
			clientData.setAddressOptionsData(this.addressReadPlatformService.addressType());
			clientData.setCityData(this.addressReadPlatformService.retrieveCityDetails());
		} else {
			final List<String> allocationDetailsDatas = this.allocationReadPlatformService
					.retrieveHardWareDetails(clientId);
			clientData = ClientData.templateOnTop(clientData, null, null, null, allocationDetailsDatas, balanceCheck);
		}

		final SelfCare selfcare = this.selfCareRepository.findOneByClientId(clientId);
		clientData.setSelfcare(selfcare);
		final PaymentGatewayConfiguration paypalconfigurationProperty = this.paymentGatewayConfigurationRepository
				.findOneByName(ConfigurationConstants.PAYMENTGATEWAY_IS_PAYPAL_CHECK);
		clientData.setConfigurationProperty(paypalconfigurationProperty);
		final PaymentGatewayConfiguration paypalconfigurationPropertyForIos = this.paymentGatewayConfigurationRepository
				.findOneByName(ConfigurationConstants.PAYMENTGATEWAY_IS_PAYPAL_CHECK_IOS);
		clientData.setConfigurationPropertyForIos(paypalconfigurationPropertyForIos);
		return this.toApiJsonSerializer.serialize(settings, clientData,
				ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
	}

	/**
	 * this method is using for getting template data in editing a client
	 */
	@GET
	@Path("additionalinfo/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveClientAdditionalInfo(@PathParam("id") final Long id, @Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
		ClientAdditionalData clientAdditionalData = this.clientReadPlatformService.retrieveClientAdditionalData(id);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());

		if (settings.isTemplate()) {

			Collection<MCodeData> genderDatas = this.codeReadPlatformService
					.getCodeValue(CodeNameConstants.CODE_GENDER);
			Collection<MCodeData> nationalityDatas = this.codeReadPlatformService
					.getCodeValue(CodeNameConstants.CODE_NATIONALITY);
			Collection<MCodeData> customeridentificationDatas = this.codeReadPlatformService
					.getCodeValue(CodeNameConstants.CODE_CUSTOMER_IDENTIFIER);
			Collection<MCodeData> cummunitcationDatas = this.codeReadPlatformService
					.getCodeValue(CodeNameConstants.CODE_PREFER_COMMUNICATION);
			Collection<MCodeData> languagesDatas = this.codeReadPlatformService
					.getCodeValue(CodeNameConstants.CODE_PREFER_LANG);
			Collection<MCodeData> ageGroupDatas = this.codeReadPlatformService
					.getCodeValue(CodeNameConstants.CODE_AGE_GROUP);
			if (clientAdditionalData != null) {
				clientAdditionalData.setAgeGroupDatas(ageGroupDatas);
				clientAdditionalData.setGenderDatas(genderDatas);
				clientAdditionalData.setNationalityDatas(nationalityDatas);
				clientAdditionalData.setCustomeridentificationDatas(customeridentificationDatas);
				clientAdditionalData.setCummunitcationDatas(cummunitcationDatas);
				clientAdditionalData.setLanguagesDatas(languagesDatas);
			} else {
				clientAdditionalData = new ClientAdditionalData(genderDatas, nationalityDatas,
						customeridentificationDatas, cummunitcationDatas, languagesDatas, ageGroupDatas);
			}
		}
		return this.jsonSerializer.serialize(settings, clientAdditionalData,
				ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
	}

	/**
	 * this method is using for create a new client
	 * 
	 * @param command
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String create(final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createClient().withJson(apiRequestBodyAsJson)
				.build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@POST
	@Path("additionalinfo/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createClientAdditionalInfo(@PathParam("clientId") final Long clientId,
			final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createClientAdditional(clientId)
				.withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@PUT
	@Path("additionalinfo/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateClientAdditionalInfo(@PathParam("clientId") final Long clientId,
			final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateClientAdditional(clientId)
				.withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * this method is using for edit a client
	 */
	@PUT
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String update(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateClient(clientId)
				.withJson(apiRequestBodyAsJson).build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	@DELETE
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String delete(@PathParam("clientId") final Long clientId) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
				.deleteClient(clientId) //
				.build(); //

		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * this method is using for closing a client
	 */
	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String activate(@PathParam("clientId") final Long clientId, @QueryParam("command") final String commandParam,
			final String apiRequestBodyAsJson) {

		final CommandWrapperBuilder builder = new CommandWrapperBuilder().withJson(apiRequestBodyAsJson);

		CommandProcessingResult result = null;
		if (is(commandParam, "activate")) {
			final CommandWrapper commandRequest = builder.activateClient(clientId).build();
			result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

		} else if (is(commandParam, "close")) {

			final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteClient(clientId)
					.withJson(apiRequestBodyAsJson).build();

			result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		}
		if (result == null) {
			throw new UnrecognizedQueryParamException("command", commandParam, new Object[] { "activate" });
		}

		return this.toApiJsonSerializer.serialize(result);
	}

	private boolean is(final String commandParam, final String commandValue) {
		return StringUtils.isNotBlank(commandParam) && commandParam.trim().equalsIgnoreCase(commandValue);
	}

	@GET
	@Path("search/{columnName}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveSearchClient(@Context final UriInfo uriInfo, @PathParam("columnName") final String columnName,
			@QueryParam("columnValue") final String columnValue) {

		this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
		/*
		 * ClientData clientdata = null; try { clientdata =
		 * this.clientReadPlatformService.retrieveSearchClientId(columnName,
		 * columnValue); } catch (Exception e) { if (columnName.equals("serial_no"))
		 * throw new SerialNumberNotFoundException(columnValue); if
		 * (columnName.equals("account_no")) throw new
		 * ClientNotFoundException(columnValue, 0l); }
		 */
		ClientData clientdata = this.clientReadPlatformService.retrieveSearchClientId(columnName,columnValue);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, clientdata,
				ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);

	}

	@GET
	@Path("searching/{officeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveSearchOffice(@PathParam("officeId") final Long officeId,
			@QueryParam("query") final String query, @Context final UriInfo uriInfo) {

		context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
		ClientData clientData = null;
		final Collection<OfficeData> offices = this.officeReadPlatformService.retrieveAllOfficesForSearch(query);
		if (offices != null) {
			clientData = ClientData.template(null, null, offices, null, null);
		}
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, clientData,
				ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);
	}

	@GET
	@Path("billprofile/{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveClientBillInfoDetails(@Context final UriInfo uriInfo,
			@PathParam("clientId") final Long clientId) {

		this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
		ClientBillInfoData clientBillData = this.clientBillInfoReadPlatformService
				.retrieveClientBillInfoDetails(clientId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		if (settings.isTemplate()) {
			clientBillData = this.handleTemplateData(clientBillData);
		}
		return this.toApiJsonSerializer.serialize(clientBillData);
	}

	private ClientBillInfoData handleTemplateData(ClientBillInfoData clientBillData) {

		if (clientBillData == null) {
			clientBillData = new ClientBillInfoData();
		}
		clientBillData.setServiceTypes(this.enumReadplaformService.getEnumValues("service_type"));
		clientBillData.setCurrencyData(this.currencyReadPlatformService.retrieveCurrencyConfiguration());
		clientBillData.setBillFrequencyCodeData(this.chargeCodeReadPlatformService.getBillFrequency());
		clientBillData.setBillSupressionData(this.billSupressionProfileInfoReadPlatformService.getBillSupression());
		return clientBillData;

	}

	@SuppressWarnings("unused")
	@GET
	@Path("getclient360")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveCustomer(@QueryParam("key") final String key, @QueryParam("value") final String value,
			@Context final UriInfo uriInfo) throws JSONException {
		this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants.CLIENT_RESOURCE_NAME);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		ClientData clientData = this.crmServices.retriveClientTotalData(key, value);
		if (clientData == null) {
			clientData = this.clientReadPlatformService.retrieveOne(key, value);

		}
		if (clientData != null) {
			clientData.setClientServiceData(
					this.clientServiceReadPlatformService.retriveClientServices(clientData.getId()));
			clientData.setOneTimeSaleData(
					this.oneTimeSaleReadPlatformService.retrieveClientOneTimeSalesData(clientData.getId()));
			clientData.setOrderData(this.orderReadPlatformService.retrieveClientOrderDetails(clientData.getId()));

			if (settings.isTemplate()) {
				clientData = this.handleTemplateData360(clientData);
			}

		}

		return this.toApiJsonSerializer.serialize(settings, clientData,
				ClientApiConstants.CLIENT_RESPONSE_DATA_PARAMETERS);

	}

	private ClientData handleTemplateData360(ClientData clientData) {

		clientData.setAddressData(
				this.addressReadPlatformService.retrieveAddressDetailsBy(clientData.getId(), clientData.addressType));
		clientData.setAddressOptionsData(this.addressReadPlatformService.addressType());
		clientData.setCityData(this.addressReadPlatformService.retrieveCityDetails());
		clientData.setOfficeData(this.officeReadPlatformService.retrieveAllOfficesForDropdown());
		clientData.setClientCategoryDatas(this.clientReadPlatformService.retrieveClientCategories());
		clientData.setIdProofs(this.codeReadPlatformService.getCodeValue("Identification Proofs"));
		return clientData;

	}

	@GET
	@Path("dashboard")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retriveUserInformnation(@Context final UriInfo uriInfo) {
		/*
		 * this.context.authenticatedUser().validateHasReadPermission(ClientApiConstants
		 * .CLIENT_RESOURCE_NAME);
		 */
		AppUser logedInUser = this.context.authenticatedUser();
		ClientData clientData = this.clientReadPlatformService.userdeviceinformation(logedInUser.getOffice().getId());
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());

		return this.toApiJsonSerializer.serialize(settings, clientData, RESPONSE_DATA_PARAMETERS);

	}

}