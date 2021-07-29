package org.mifosplatform.logistics.supplier.api;

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

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.supplier.data.SupplierData;
import org.mifosplatform.logistics.supplier.service.SupplierReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Path("/suppliers")
@Component
@Scope("singleton")
public class SupplierApiResource {
	
	private final static String resourceType = "SUPPLIER";
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<SupplierData> apiJsonSerializer;
	final private SupplierReadPlatformService supplierReadPlatformService;
	
	@Autowired
	public SupplierApiResource(final PlatformSecurityContext context, final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,
			final ToApiJsonSerializer<SupplierData> apiJsonSerializer,final SupplierReadPlatformService supplierReadPlatformService) {
		
		this.context = context;
		this.apiJsonSerializer = apiJsonSerializer;
		this.supplierReadPlatformService = supplierReadPlatformService;
		this.portfolioCommandSourceWritePlatformService = commandSourceWritePlatformService;
		
	}
	
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveSupplierDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		context.authenticatedUser().validateHasReadPermission(resourceType);
		final SearchSqlQuery searchSupplier =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<SupplierData> mrnDetailsDatas = supplierReadPlatformService.retrieveSupplier(searchSupplier);
		return apiJsonSerializer.serialize(mrnDetailsDatas);
	}
	
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createMRN(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createSupplier().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@PUT
	@Path("{supplierId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateSupplier(@PathParam("supplierId") final Long supplierId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateSupplier(supplierId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{supplierId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveSupplierDetails(@Context final UriInfo uriInfo ,@PathParam("supplierId") final Long supplierId){
		context.authenticatedUser().validateHasReadPermission(resourceType);
		final List<SupplierData> mrnDetailsDatas = supplierReadPlatformService.retrieveSupplier(supplierId);
		return apiJsonSerializer.serialize(mrnDetailsDatas);
	}
	
}
