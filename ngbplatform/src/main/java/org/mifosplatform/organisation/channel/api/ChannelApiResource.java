package org.mifosplatform.organisation.channel.api;

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

import org.mifosplatform.billing.emun.data.EnumValuesData;
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
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.organisation.broadcaster.service.BroadcasterReadPlatformService;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.channel.domain.LanguageEnum;
import org.mifosplatform.organisation.channel.service.ChannelReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/channel")
@Component
@Scope("singleton")
public class ChannelApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id"));
	
	private final static  String RESOURCE_TYPE = "CHANNEL";

	final private BroadcasterReadPlatformService broadcasterReadPlatformService;
	final private ChannelReadPlatformService channelReadPlatformService;
	final private ApiRequestParameterHelper apiRequestParameterHelper;
	final private PlatformSecurityContext context;
	final private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;
	final private ToApiJsonSerializer<ChannelData> apiJsonSerializer;
	final private MCodeReadPlatformService mCodeReadPlatformService;
	
	
	
	
	
	@Autowired
	public ChannelApiResource(
			PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ToApiJsonSerializer<ChannelData> apiJsonSerializer, final PlatformSecurityContext context,final ChannelReadPlatformService channelReadPlatformService,
			final ApiRequestParameterHelper apiRequestParameterHelper,final BroadcasterReadPlatformService broadcasterReadPlatformService,final MCodeReadPlatformService mCodeReadPlatformService) {
	    
		this.channelReadPlatformService = channelReadPlatformService;
		this.apiRequestParameterHelper = apiRequestParameterHelper;
		this.context = context;
		this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiJsonSerializer = apiJsonSerializer;
		this.broadcasterReadPlatformService = broadcasterReadPlatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
	}


	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String ChannelDetails(@Context final UriInfo uriInfo , @QueryParam("sqlSearch") final String sqlSearch,
			      @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final SearchSqlQuery searchChannel = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<ChannelData> channelDatas = channelReadPlatformService.retrieveChannel(searchChannel);
		return apiJsonSerializer.serialize(channelDatas);
	}


	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String createChannel(@QueryParam("LanguageEnum")final String LanguageEnum,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().createChannel().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
			
	}
	
	@PUT
	@Path("{channelId}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	public String updateChannel(@PathParam("channelId") final Long channelId,final String jsonRequestBody){
		final CommandWrapper command = new CommandWrapperBuilder().updateChannel(channelId).withJson(jsonRequestBody).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("{channelId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retriveChannelDetails(@Context final UriInfo uriInfo ,@PathParam("channelId") final Long channelId){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		ChannelData channelData = this.channelReadPlatformService.retrieveChannel(channelId);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		if(settings.isTemplate()){
			channelData = this.handleTemplateData(channelData);
		}
		
		return this.apiJsonSerializer.serialize(settings,channelData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	@DELETE
	@Path("{channelId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteChannel(@PathParam("channelId") final Long channelId) {

		final CommandWrapper command = new CommandWrapperBuilder().deleteChannel(channelId).build();
		final CommandProcessingResult result = portfolioCommandSourceWritePlatformService.logCommandSource(command);
		return apiJsonSerializer.serialize(result);
	}
	
	/*@GET
	@Path("dropdown")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveChannelsForDropdown(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		final List<ChannelData> channelDatas = this.channelReadPlatformService.retrieveChannelsForDropdown();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, channelDatas,RESPONSE_DATA_PARAMETERS);
		
	}*/
	
	@GET
	@Path("template")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String retrieveTemplateData(@Context final UriInfo uriInfo){
		this.context.authenticatedUser().validateHasReadPermission(this.RESOURCE_TYPE);
		
		final ChannelData channelData = this.handleTemplateData(null);
				
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.apiJsonSerializer.serialize(settings, channelData,RESPONSE_DATA_PARAMETERS);
		
	}
	
	private ChannelData handleTemplateData(ChannelData channelData) {
		if(channelData == null){
			channelData = new ChannelData();
		}
		
		channelData.setLanguageEnum(this.channelReadPlatformService.retrieveLanguageEnum());
		channelData.setBroadcasterDatas(this.broadcasterReadPlatformService.retrieveBroadcastersForDropdown());
		channelData.setChannelCategorys(this.mCodeReadPlatformService.getCodeValue(CodeNameConstants.CHANNEL_CATEGORY));
		return channelData;
	}

}
