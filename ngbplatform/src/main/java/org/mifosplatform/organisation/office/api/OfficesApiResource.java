/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.api;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.mifosplatform.celcom.domain.PaymentTypeEnum;
import org.mifosplatform.celcom.domain.SearchTypeEnum;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.office.service.OfficeWritePlatformService;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/offices")
@Component
@Scope("singleton")
public class OfficesApiResource {

	/**
	 * The set of parameters that are supported in response for
	 * {@link OfficeData}.
	 */
	private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("id", "name", "nameDecorated", "externalId",
					"openingDate", "hierarchy", "parentId", "parentName",
					"allowedParents", "officeTypes"));

	private final static  String RESOURCE_TYPE = "OFFICE";

	private final PlatformSecurityContext context;
	private final OfficeReadPlatformService readPlatformService;
	private final DefaultToApiJsonSerializer<OfficeData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<FinancialTransactionsData> toFinancialTransactionApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	public static final String OFFICE_TYPE = "Office Type";
	final private MCodeReadPlatformService mCodeReadPlatformService;
	private final CrmServices crmServices;

	private final OfficeWritePlatformService officeWritePlatformService ;

	@Autowired
	public OfficesApiResource(
			final PlatformSecurityContext context,
			final OfficeReadPlatformService readPlatformService,
			final DefaultToApiJsonSerializer<OfficeData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final CodeValueReadPlatformService codeValueReadPlatformService,
			final DefaultToApiJsonSerializer<FinancialTransactionsData> toFinancialTransactionApiJsonSerializer,
			final AddressReadPlatformService addressReadPlatformService,
			final MCodeReadPlatformService mCodeReadPlatformService,
			final CrmServices crmServices,final OfficeWritePlatformService officeWritePlatformService) {
		this.context = context;
		this.readPlatformService = readPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.toFinancialTransactionApiJsonSerializer = toFinancialTransactionApiJsonSerializer;
		this.addressReadPlatformService = addressReadPlatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		this.crmServices = crmServices;
		this.officeWritePlatformService = officeWritePlatformService;
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveOffices(@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(
				RESOURCE_TYPE);
		final Collection<OfficeData> offices = this.readPlatformService
				.retrieveAllOffices();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, offices,
				RESPONSE_DATA_PARAMETERS);
	}

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveOfficeTemplate(@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(
				RESOURCE_TYPE);
		OfficeData office = this.readPlatformService
				.retrieveNewOfficeTemplate();
		final Collection<OfficeData> allowedParents = this.readPlatformService.retrieveAllOfficesForDropdown();
		final List<PaymentTypeEnum> paymentTypeEnum = this.readPlatformService.retrievePaymentTypeEnum();
		final Collection<CodeValueData> officeTypes = this.codeValueReadPlatformService.retrieveCodeValuesByCode(OFFICE_TYPE);
		office = OfficeData.appendedTemplate(office, allowedParents,officeTypes);
		office = this.handleAddressTemplateData(office);
		office.setBusinessTypes(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.BUSINESS_TYPE));
		office.setPaymentTypeEnum(paymentTypeEnum);
		office.setSegmentTypes(this.codeValueReadPlatformService.retrieveCodeValuesByCode("DAS_Type"));
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, office,	RESPONSE_DATA_PARAMETERS);
	}

	private OfficeData handleAddressTemplateData(final OfficeData officeData) {

		final List<String> countryData = this.addressReadPlatformService
				.retrieveCountryDetails();
		final List<String> statesData = this.addressReadPlatformService
				.retrieveStateDetails();
		final List<String> citiesData = this.addressReadPlatformService
				.retrieveCityDetails();
		final List<EnumOptionData> enumOptionDatas = this.addressReadPlatformService
				.addressType();
		final List<String> districtData = this.addressReadPlatformService
				.retrieveDistrictDetails();
		final AddressData data = new AddressData(null, countryData, statesData,
				citiesData, enumOptionDatas, districtData);
		officeData.setAddressData(data);
		return officeData;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createOffice(final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
				.createOffice() //
				.withJson(apiRequestBodyAsJson) //
				.build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService
				.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	@GET
	@Path("{officeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retreiveOffice(@PathParam("officeId") final Long officeId,
			@Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);

		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());

		OfficeData office = this.readPlatformService.retrieveOffice(officeId);
		OfficeData officeBalanceData = this.crmServices.retriveOfficeData(office);
		if (officeBalanceData != null) {
			office = officeBalanceData;
		}
		if (settings.isTemplate()) {
			final Collection<OfficeData> allowedParents = this.readPlatformService.retrieveAllowedParents(officeId);
			final Collection<CodeValueData> codeValueDatas = this.codeValueReadPlatformService.retrieveCodeValuesByCode(OFFICE_TYPE);
			final List<PaymentTypeEnum> paymentTypeEnum = this.readPlatformService.retrievePaymentTypeEnum();
			office = OfficeData.appendedTemplate(office, allowedParents,codeValueDatas);
			office.setBusinessTypes(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.BUSINESS_TYPE));
			office.setPaymentTypeEnum(paymentTypeEnum);
			office.setSegmentTypes(this.codeValueReadPlatformService.retrieveCodeValuesByCode(""));
			office.setSegmentTypes(this.codeValueReadPlatformService.retrieveCodeValuesByCode("DAS_Type"));
			/*
			 * office.setCountryData(this.addressReadPlatformService.retrieveCountryDetails());
			 * office.setCitiesData(this.addressReadPlatformService.retrieveCityDetails());
			 * office.setStatesData(this.addressReadPlatformService.retrieveStateDetails());
			 */
		}
		return this.toApiJsonSerializer.serialize(settings, office,RESPONSE_DATA_PARAMETERS);
	}

	@PUT
	@Path("{officeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateOffice(@PathParam("officeId") final Long officeId,
			final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder() //
				.updateOffice(officeId) //
				.withJson(apiRequestBodyAsJson) //
				.build();

		final CommandProcessingResult result = this.commandsSourceWritePlatformService
				.logCommandSource(commandRequest);

		return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * Office financial Transactions
	 * */
	@GET
	@Path("financialtransactions/{officeId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveTransactionalData(
			@PathParam("officeId") final Long officeId,
			@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(
				RESOURCE_TYPE);
		final Collection<FinancialTransactionsData> transactionData = this.readPlatformService
				.retreiveOfficeFinancialTransactionsData(officeId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.toFinancialTransactionApiJsonSerializer.serialize(settings,
				transactionData, RESPONSE_DATA_PARAMETERS);
	}
	
	@GET
	@Path("{officeId}/print")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response printStatement(@PathParam("officeId") final Long officeId) {
			
		 final String printFileName=this.officeWritePlatformService.generatePartnerPdf(officeId);
		 final File file = new File(printFileName);
		 final ResponseBuilder response = Response.ok(file);
		 response.header("Content-Disposition", "attachment; filename=\"" +file.getName()+ "\"");
		 response.header("Content-Type", "application/pdf");
		 return response.build();
	}
	
	@GET
	@Path("officesView")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String OfficeDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final SearchSqlQuery searchOffice = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<OfficeData> OfficeDatas = readPlatformService.retrieveOfficeDetails(searchOffice);
		return this.toApiJsonSerializer.serialize(OfficeDatas);
	}
	
	

}