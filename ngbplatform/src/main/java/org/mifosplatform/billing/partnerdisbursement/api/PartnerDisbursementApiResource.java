package org.mifosplatform.billing.partnerdisbursement.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.billing.partnerdisbursement.data.PartnerDisbursementData;
import org.mifosplatform.billing.partnerdisbursement.service.PartnerDisbursementReadPlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/patnerdisbursement")
@Component
@Scope("singleton")
public class PartnerDisbursementApiResource {

	/**
	 * The set of parameters that are supported in response for {@link CodeData}
	 */
	private static final Set<String> RESPONSE_PARAMETERS = new HashSet<String>(Arrays.asList("id", "source", "partnerName", "transDate",
					"chargeAmount", "commissionAmount", "netAmount", "percentage"));

	private static String resourceNameForPermissions = "PARTNERDISBURSEMENT";
	public static final String SOURCE_TYPE = "Source Category";

	private final PlatformSecurityContext context;
	private final PartnerDisbursementReadPlatformService readPlatformService;
	private final DefaultToApiJsonSerializer<PartnerDisbursementData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final MCodeReadPlatformService mCodeReadPlatformService;

	@Autowired
	public PartnerDisbursementApiResource(final PlatformSecurityContext context,
			final PartnerDisbursementReadPlatformService readPlatformService,
			final DefaultToApiJsonSerializer<PartnerDisbursementData> toApiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final MCodeReadPlatformService mCodeReadPlatformService) {

		this.context = context;
		this.readPlatformService = readPlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		
	}
	
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSettlementData(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,
			@QueryParam("sourceType") final String sourceType, @QueryParam("partnerType") final String partnerType) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final SearchSqlQuery search = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<PartnerDisbursementData> patnerDisbursementData = this.readPlatformService.getAllData(search, sourceType, partnerType);
		return this.toApiJsonSerializer.serialize(patnerDisbursementData);
	}
	
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSettlemenTemplatetData(@Context final UriInfo uriInfo) {
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		final Collection<MCodeData> sourceData = this.mCodeReadPlatformService.getCodeValue(SOURCE_TYPE);
		final List<PartnerDisbursementData> patnerData = this.readPlatformService.getPatnerData();
		PartnerDisbursementData templateData = new PartnerDisbursementData(sourceData, patnerData);
		return this.toApiJsonSerializer.serialize(settings, templateData, RESPONSE_PARAMETERS);
		}
	}