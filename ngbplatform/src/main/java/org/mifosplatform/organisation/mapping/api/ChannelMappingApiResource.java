package org.mifosplatform.organisation.mapping.api;

import java.util.ArrayList;
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
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.mrn.data.MRNDetailsData;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.channel.service.ChannelReadPlatformService;
import org.mifosplatform.organisation.mapping.data.ChannelMappingData;
import org.mifosplatform.organisation.mapping.service.ChannelMappingReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.organisation.usercataloge.data.UserCatalogeData;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.product.data.ProductData;
import org.mifosplatform.portfolio.product.service.ProductReadPlatformService;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/channelmapping")
@Component
@Scope("singleton")
public class ChannelMappingApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	
	private final static  String RESOURCE_TYPE = "CHANNELMAPPING";

    final private ChannelReadPlatformService channelReadPlatformService ;
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<ChannelMappingData> apiJsonSerializer;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private ChannelMappingReadPlatformService channelMappingReadPlatformService;
	final private ServiceMasterReadPlatformService serviceMasterReadPlatformService;
	final private ProductReadPlatformService productReadPlatformService;
	

	@Autowired
	public ChannelMappingApiResource(final PlatformSecurityContext context,
			PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer<ChannelMappingData> apiJsonSerializer,
			final ApiRequestParameterHelper apiRequestParameterHelper,
			final ChannelMappingReadPlatformService channelMappingReadPlatformService, final  ChannelReadPlatformService channelReadPlatformService,final  ServiceMasterReadPlatformService serviceMasterReadPlatformService,
			final ProductReadPlatformService productReadPlatformService) {
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.channelMappingReadPlatformService = channelMappingReadPlatformService;
		this.channelReadPlatformService = channelReadPlatformService;
		this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
		this.productReadPlatformService = productReadPlatformService;
	}

	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String ChannelMappingDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchChannelMapping = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<ChannelMappingData> channelmappingDatas = channelMappingReadPlatformService.retrieveChannelMapping(searchChannelMapping);
		return apiJsonSerializer.serialize(channelmappingDatas);
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createChannelMapping(final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createChannelMapping().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
		
	}
	
	@GET
	@Path("{productId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveChannelMappingDetails(@Context final UriInfo uriInfo ,@PathParam("productId") final Long productId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		//ChannelMappingData channelMappingData = this.channelMappingReadPlatformService.retrieveChannelMapping(channelmappingId);
		/*if(settings.isTemplate()){
			channelMappingData = this.handleTemplateData(channelMappingData);
		}*/
        ChannelMappingData channelMappingData = new ChannelMappingData();
        channelMappingData.setProductId(productId);
        channelMappingData=this.handleTemplateData(channelMappingData,null); 
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings,channelMappingData,RESPONSE_DATA_PARAMETERS);
	}
	
	@PUT
	@Path("{productId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateChannelMapping(@PathParam("productId") final Long productId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateChannelMapping(productId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@DELETE
	@Path("{productId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteChannel(@PathParam("productId") final Long productId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteChannelMapping(productId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo,@QueryParam("productCode") String productCode){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final ChannelMappingData channelMappingData = this.handleTemplateData(null,productCode);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, channelMappingData,RESPONSE_DATA_PARAMETERS);
		
	}

	private ChannelMappingData handleTemplateData(ChannelMappingData channelMappingData, String productCode) {
		if(channelMappingData == null){
			channelMappingData = new ChannelMappingData();
		}
		
		//channelMappingData.setChannelDatas(this.channelReadPlatformService.retrieveChannelsForDropdown());
		channelMappingData.setProductDatas(this.productReadPlatformService.retriveProducts("P",productCode));
		final List<ChannelData> availableChannels = this.channelReadPlatformService.retrieveChannelsForDropdown();
		List<ChannelData> selectedChannels = new ArrayList<>();
		//Long userId=this.context.authenticatedUser().getId();
		if(availableChannels != null){
			selectedChannels = this.channelMappingReadPlatformService.retrieveSelectedChannels(channelMappingData.getProductId());
			//selectedSalesCataloges = this.userCatalogeReadPlatformService.retrieveSelectedSalesCatalogeForPlanCategory(userId);

			int size = availableChannels.size();
			final int selectedsize = selectedChannels.size();
				for (int i = 0; i < selectedsize; i++)
	     			{
					final Long selected = selectedChannels.get(i).getId();
					for (int j = 0; j < size; j++) {
						final Long avialble = availableChannels.get(j).getId();
						if (selected.equals(avialble)) {
							availableChannels.remove(j);
							size--;
						}
					}
				}
	     }
		channelMappingData.setAvailaableChannels(availableChannels);
		channelMappingData.setSelectedChannels(selectedChannels);
		
		
		return channelMappingData;
		
	}
	
	/*@GET
	@Path("searching/{productId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveSearchOffice(@PathParam("productId") final Long productId, @QueryParam("productCode") final String productCode,@QueryParam("getAll") final boolean getAll, @Context final UriInfo uriInfo){
	    context.authenticatedUser().validateHasReadPermission(RESOURCE_TYPE);
    	final Collection<ProductData> productDatas = this.productReadPlatformService.retrieveAllProductForDropdown(productCode,getAll);
    	final MRNDetailsData mrnDetailsData = new MRNDetailsData(offices,null);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.apiJsonSerializer.serialize(settings,mrnDetailsData,RESPONSE_PARAMETERS);
	}*/
	
	

}
