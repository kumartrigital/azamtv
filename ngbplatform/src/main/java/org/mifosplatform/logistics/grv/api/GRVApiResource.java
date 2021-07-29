package org.mifosplatform.logistics.grv.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.service.ItemSaleReadPlatformService;
import org.mifosplatform.logistics.grv.data.GRVData;
import org.mifosplatform.logistics.grv.service.GRVReadPlatformService;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.mrn.data.InventoryTransactionHistoryData;
import org.mifosplatform.logistics.mrn.data.MRNDetailsData;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Component
@Scope("singleton")
@Path("/grv")
public class GRVApiResource {
	
	private final Set<String> RESPONSE_PARAMETERS = new HashSet<String>(Arrays.asList("grvId","movement","transactionDate","requestedDate","itemDescription","fromOffice","toOffice","orderdQuantity","receivedQuantity","status","officeId","officeName","parentId","movedDate","notes"));
	private final static String RESOURCE_TYPE = "GRVDETAILS";
	
	private final  PlatformSecurityContext context;
	private final ToApiJsonSerializer<GRVData> apiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	private final GRVReadPlatformService grvReadPlatformService;
	
	
	
	@Autowired
	public GRVApiResource(final PlatformSecurityContext context,
			final ToApiJsonSerializer<GRVData> apiJsonSerializer, 
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService, 
			final ApiRequestParameterHelper apiRequestParameterHelper,final OfficeReadPlatformService officeReadPlatformService,
			final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,final GRVReadPlatformService grvReadPlatformService) {
       
		this.context = context;
		this.apiJsonSerializer = apiJsonSerializer;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.officeReadPlatformService = officeReadPlatformService;
		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;
		this.grvReadPlatformService = grvReadPlatformService;
	}

	@GET
	@Path("template")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String retriveGrvTemplate(@Context UriInfo uriInfo){
		
		this.context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final GRVData grvData = this.handleTemplatedata(null);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, grvData, RESPONSE_PARAMETERS);
	}
	
	
	@GET
	@Path("{grvId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveSingleGrv(@Context final UriInfo uriInfo , @PathParam("grvId") final Long grvId){
		context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
		final GRVData grvData = grvReadPlatformService.retriveSingleGrvData(grvId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return apiJsonSerializer.serialize(settings,grvData,RESPONSE_PARAMETERS);
	}


	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createGRV(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createGRV().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return this.apiJsonSerializer.serialize(result);
	}	
	
	
	
	private GRVData handleTemplatedata(GRVData grvData) {
		if(grvData == null){
			grvData = new GRVData();
		}
		grvData.setOfficeData( this.officeReadPlatformService.retrieveAllOfficesForDropdown());
		grvData.setItemMasterData(this.oneTimeSaleReadPlatformService.retrieveItemData());
		
		return grvData;
	}

}
