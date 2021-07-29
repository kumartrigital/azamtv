package org.mifosplatform.celcom.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
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

import org.mifosplatform.celcom.domain.PlanTypeEnum;
import org.mifosplatform.celcom.domain.SearchTypeEnum;
import org.mifosplatform.celcom.service.CelcomReadPlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.chargeorder.data.BillDetailsData;
import org.mifosplatform.infrastructure.codes.service.CodeReadPlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/celcom")
@Component
@Scope("singleton")
public class CelcomApiResource {
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<ClientData> toApiJsonSerializer;
	private final CelcomReadPlatformService celcomReadPlatformService;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	 
	@Autowired
	public CelcomApiResource(final PlatformSecurityContext context,
			final ToApiJsonSerializer<ClientData> toApiJsonSerializer, 
			final CelcomReadPlatformService celcomReadPlatformService,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final MCodeReadPlatformService mCodeReadPlatformService) {
		
		this.context = context;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.celcomReadPlatformService = celcomReadPlatformService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.mCodeReadPlatformService=mCodeReadPlatformService;
	}

	@GET
	@Path("getclient360")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getClientTotaldata(@QueryParam("key") final String key,
    		@QueryParam("value") final String value) {
		
		this.context.authenticatedUser();
		final ClientData clientdata = this.celcomReadPlatformService.retriveClientTotalData(key, value);
		return this.toApiJsonSerializer.serialize(clientdata);
	}
	
	
	@GET
	@Path("getcrmplans")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getPlan(@QueryParam("key") final String key,
    	@QueryParam("value") final String value ,@QueryParam("PlanTypeEnum")final String PlanTypeEnum,@QueryParam("SearchTypeEnum")final String SearchTypeEnum,
    	@QueryParam("planTypeData")final String planTypeData,@QueryParam("searchType")final String searchType ) {

		this.context.authenticatedUser();
		System.out.println(SearchTypeEnum+" "+PlanTypeEnum+" "+planTypeData+" "+value);
		final List<PlanData> plan= this.celcomReadPlatformService.retrivePlans(key, value,PlanTypeEnum,SearchTypeEnum,searchType);
		
		return this.toApiJsonSerializer.serialize(plan);
	}
	
	@POST
	@Path("createclient")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCelcomClient().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	
	@POST
	@Path("createclientsimpleactivation")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create1(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCelcomClientSimpleActivation().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	

	@POST
	@Path("createoffice")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createOffice(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCelcomOffice().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String retrievePlantype(@Context final UriInfo uriInfo) {
	final List<PlanTypeEnum> PlanTypeEnum = this.celcomReadPlatformService.retrievePlanTypeEnum();
	final List<SearchTypeEnum> SearchTypeEnum = this.celcomReadPlatformService.retrieveSearchTypeEnum();
	final PlanData data =new PlanData(PlanTypeEnum,SearchTypeEnum);
	data.setPlanTypeData(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.PLAN_TYPE));
	return this.toApiJsonSerializer.serialize(data);
	}
	
	@POST
	@Path("syncplan")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String syncPlan(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().syncPlanCelcom().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	
	@POST
	@Path("payments")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String payments(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCelcomPayment().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	
	@POST
	@Path("adjustments")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String adjustments(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createAdjustmentsCelcom().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	
	
	
	@POST
	@Path("Renewalplan")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String Renewalplan(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().Renewalplan().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	

	
	@PUT
	@Path("updateclient/{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	 public String update(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateCelcomClient(clientId).withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	

	@POST
	@Path("createagreement")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createagreement(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCelcomAgreement().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	
	
	@POST
	@Path("officepayments")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String officepayments(final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createCelcomOfficePayment().withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	
	@GET
	@Path("billdetails")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String getBillDetails(@Context final UriInfo uriInfo,@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson,
			@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,@QueryParam("offset") final Integer offset) {
		this.context.authenticatedUser();
		final SearchSqlQuery searchCodes =SearchSqlQuery.forSearch(sqlSearch, offset,limit);
		final List<BillDetailsData> billDetailsData = this.celcomReadPlatformService.retriveBillDetails(searchCodes,clientId);
		return this.toApiJsonSerializer.serialize(billDetailsData);
	}
	
	@POST
	@Path("productmapping/{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String productmapping(final String apiRequestBodyAsJson,@PathParam("productId") final Long productId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().productMappingCelcom(productId).withJson(apiRequestBodyAsJson).build(); 
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	

}
