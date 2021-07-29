package org.mifosplatform.portfolio.product.api;

import java.util.Arrays;
import java.util.HashSet;
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
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.product.data.ProductData;
import org.mifosplatform.portfolio.product.service.ProductReadPlatformService;
import org.mifosplatform.portfolio.provisioncodemapping.data.ProvisionCodeMappingData;
import org.mifosplatform.portfolio.provisioncodemapping.service.ProvisionCodeMappingReadPlatformService;
import org.mifosplatform.portfolio.service.data.ServiceMasterOptionsData;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/product")
@Component
@Scope("singleton")
public class ProductApiResource {

	  private final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id","productCode","productDescription","productCategory","serviceCode",
				/*"provisioningCode","validityStart","validityEnd",*/"status"));
	
	  private final static String RESOURCENAMEFORPERMISSIONS = "PRODUCT";
	  private final static  String RESOURCE_TYPE = "PRODUCT";
	
	  private final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	  private final ToApiJsonSerializer toApiJsonSerializer;
	  private final PlatformSecurityContext context;
	  private final ProductReadPlatformService productReadPlatformService ;
	  private final ApiRequestParameterHelper apiRequestParameterHelper;
	  private final ProvisionCodeMappingReadPlatformService provisionCodeMappingReadPlatformService;
	  private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
	  private final PlanReadPlatformService planReadPlatformService;
      private final MCodeReadPlatformService mCodeReadPlatformService;

	
	 
   	 
	  
	  
    @Autowired
	public ProductApiResource(PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer toApiJsonSerializer, final PlatformSecurityContext context,final ProductReadPlatformService productReadPlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper,final ProvisionCodeMappingReadPlatformService provisionCodeMappingReadPlatformService,
			final ServiceMasterReadPlatformService serviceMasterReadPlatformService,
			final PlanReadPlatformService planReadPlatformService,final MCodeReadPlatformService mCodeReadPlatformService) {
    	
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.context = context;
		this.productReadPlatformService = productReadPlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.provisionCodeMappingReadPlatformService = provisionCodeMappingReadPlatformService;
		this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
		this.planReadPlatformService = planReadPlatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
	}



	// using this method posting  service data 	 	 
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
    public String createNewProduct(final String apiRequestBodyAsJson) {
					
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createProduct().withJson(apiRequestBodyAsJson).build();
	    final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
				
		}
	
	/**
	 * using this method getting  all services data 
	 */	
	 	@GET
		@Consumes({ MediaType.APPLICATION_JSON })
		@Produces({ MediaType.APPLICATION_JSON })
		public String retrieveAllProducts(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch, @QueryParam("limit") final Integer limit,
				@QueryParam("offset") final Integer offset) {
	 		
		   
	       this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		   final SearchSqlQuery searchProduct =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		   final Page<ProductData> productDatas = this.productReadPlatformService.retrieveProduct(searchProduct);
		   return this.toApiJsonSerializer.serialize(productDatas);
		}
	 	
	 	 /**
		 * this method for getting  service data using by id
		 */
	 	
		@GET
		@Path("{productId}")
		@Consumes({MediaType.APPLICATION_JSON})
		@Produces({MediaType.APPLICATION_JSON})
		public String retrieveSingleProductDetails(@Context final UriInfo uriInfo ,@PathParam("productId") final Long productId){
			this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
			ProductData productData = this.productReadPlatformService.retrieveProduct(productId);
			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
			if(productData != null){
				productData.setProductDetailData(this.productReadPlatformService.retrieveProductDetails(productId,null));
				if(settings.isTemplate()){
					productData = this.handleTemplateData(productData);
				}
			}
			
			return this.toApiJsonSerializer.serialize(settings,productData,RESPONSE_DATA_PARAMETERS);
			
		}
		
		 /**
		 * using this method editing single service data 
		 */	
		    @PUT
			@Path("{productId}")
			@Consumes({MediaType.APPLICATION_JSON})
			@Produces({MediaType.APPLICATION_JSON})
			public String updateProduct(@PathParam("productId") final Long productId, final String apiRequestBodyAsJson){

			 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateProduct(productId).withJson(apiRequestBodyAsJson).build();
			 final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
			  return this.toApiJsonSerializer.serialize(result);
			}
		    
		    /**
			 * using this method deleting single service data 
			 */	
		 	    @DELETE
				@Path("{productId}")
				@Consumes({MediaType.APPLICATION_JSON})
				@Produces({MediaType.APPLICATION_JSON})
				public String deleteProduct(@PathParam("productId") final Long productId) {
				 final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteProduct(productId).build();
		        final CommandProcessingResult result = this.portfolioCommandSourceWritePlatformService.logCommandSource(commandRequest);
		        return this.toApiJsonSerializer.serialize(result);

				}
    
		 	   @GET
		 		@Path("template")
		 		@Consumes({MediaType.APPLICATION_JSON})
		 		@Produces({MediaType.APPLICATION_JSON})
		 		public String retrieveTemplateData(@Context final UriInfo uriInfo){
		 			this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		 			
		 			final ProductData productData = this.handleTemplateData(null);
		 					
		 			final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		 			return this.toApiJsonSerializer.serialize(settings, productData,RESPONSE_DATA_PARAMETERS);
		 			
		 		}
		 	    
		 	 private ProductData handleTemplateData(ProductData productData) {
					
		 		 if (productData == null){
		 			productData = new ProductData();
					}
		 		 
		 		productData.setProvisionCodeMappingDatas(this.provisionCodeMappingReadPlatformService.retrieveProvisionCodeMappingsForDropdown());
		 		productData.setServiceMasterDatas(this.serviceMasterReadPlatformService.retriveServices("S"));
		 		productData.setStatus(this.planReadPlatformService.retrieveNewStatus());
		 		productData.setServiceParamsData(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CODE_SERVICE_PARAMS));
		 		productData.setServiceCategorys(this.serviceMasterReadPlatformService.retriveServices("S"));
		 		return productData;
					
				}
		 	 
		 	@GET
		 	   @Path("{serviceId}/{paramCategory}")
		 	   @Consumes({MediaType.APPLICATION_JSON})
		 	   @Produces({MediaType.APPLICATION_JSON})
		 	   public String retrieveServiceDetails(@PathParam("serviceId") final Long serviceId,
		 	  @PathParam("paramCategory") final String paramCategory,@Context final UriInfo uriInfo) {
		 		   
		 	  this.context.authenticatedUser().validateHasReadPermission(RESOURCENAMEFORPERMISSIONS);
		 	  ProductData productData = new  ProductData();
		 	  //productData.setProductDetailData(this.productReadPlatformService.retrieveProductDetailsAgainestMasterIdandParamCategory(productId,paramCategory));
		 	  productData.setServiceDetailData(this.serviceMasterReadPlatformService.retrieveServiceDetailsAgainestMasterIdandParamCategory(serviceId,paramCategory));
		 	  
		 	  
		 	  final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		 	  return this.toApiJsonSerializer.serialize(settings, productData, RESPONSE_DATA_PARAMETERS);
		   }
    
}
