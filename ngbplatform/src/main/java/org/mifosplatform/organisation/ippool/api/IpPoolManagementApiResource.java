package org.mifosplatform.organisation.ippool.api;

import java.util.Collection;
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
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.ippool.data.IpGeneration;
import org.mifosplatform.organisation.ippool.data.IpPoolData;
import org.mifosplatform.organisation.ippool.data.IpPoolManagementData;
import org.mifosplatform.organisation.ippool.service.IpPoolManagementReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.mifosplatform.infrastructure.codes.data.CodeData;


@Path("/ippooling")
@Component
@Scope("singleton")

/**
 * The class <code>IpPoolManagementApiResource</code> is developed for
 * Creating ip address using subnet and mask bit. 
 * 
 * @author ashokreddy
 *
 */
public class IpPoolManagementApiResource {

	/**
	 * The set of parameters that are supported in response for {@link CodeData}
	 *//*
	private static final Set<String> RESPONSE_PARAMETERS = new HashSet<String>(Arrays.asList("id", "ipPoolDescription",
			"ipAddress","subnet","clientId","clientName","status","codeValueDatas"));*/
	
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PlatformSecurityContext context;
	
	private final DefaultToApiJsonSerializer<IpPoolManagementData> toApiJsonSerializer;
	private final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService;
	private final MCodeReadPlatformService codeReadPlatformService;
	
	private final String resourceNameForPermissions="READ_IPPOOLMANAGEMENT";
	private final ConfigurationRepository globalConfigurationRepository;
	
	
	
	@Autowired
	public IpPoolManagementApiResource(final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final PlatformSecurityContext context,final DefaultToApiJsonSerializer<IpPoolManagementData> toApiJsonSerializer,
			final MCodeReadPlatformService codeReadPlatformService,final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService,
			final ConfigurationRepository globalConfigurationRepository)
	{
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.context=context;
		this.toApiJsonSerializer=toApiJsonSerializer;
		this.ipPoolManagementReadPlatformService=ipPoolManagementReadPlatformService;
		this.codeReadPlatformService=codeReadPlatformService;
		this.globalConfigurationRepository=globalConfigurationRepository;
	}
	
	/**
	 * This method <code>createIppoolManagement</code> is 
	 * Used for Creating a Ippool with Netmask.
	 * 
	 * @param requestData
	 * 			Containg input data in the Form of JsonObject.
	 * @return
	 */
	@POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createIppoolManagement(final String requestData) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().createIpPoolManagement().withJson(requestData).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    } 
	
	/**
	 * This method <code>retrieveAllIpPoolData</code> is 
	 * Used for retrieving all ip address.
	 * 
	 * @return
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllIpPoolData(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,@QueryParam("status") final String status) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		
		final SearchSqlQuery searchItemDetails =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		  String[] data=null;
		if(sqlSearch !=null && sqlSearch.contains("/")){
  		  sqlSearch.trim();
  		  IpGeneration ipGeneration=new IpGeneration(sqlSearch,this.ipPoolManagementReadPlatformService);
  		  Configuration configuration = globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_INCLUDE_NETWORK_BROADCAST_IP);
  		  ipGeneration.setInclusiveHostCount(configuration.getValue().equalsIgnoreCase("true"));
          data=ipGeneration.getInfo().getsubnetAddresses();
			
		}
		Page<IpPoolManagementData> paymentData = ipPoolManagementReadPlatformService.retrieveIpPoolData(searchItemDetails,status,data);
		return this.toApiJsonSerializer.serialize(paymentData);

	}
	
	/**
	 * This method <code>retrieveTemplate</code> 
	 * used for Retrieving the all mandatory/necessary data
	 * For creating a Ippooling.
	 * 
	 * @param uriInfo
	 * 			Containing Url information 
	 * @return
	 */
	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveTemplate(@Context final UriInfo uriInfo) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		Collection<MCodeData> codeValueDatas=this.codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_IP_TYPE);
		IpPoolData ipPoolData=new IpPoolData(codeValueDatas);
		return this.toApiJsonSerializer.serialize(ipPoolData);

	}
	
	@GET
	@Path("search")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveIpPoolIDs(@Context final UriInfo uriInfo,@QueryParam("query") final String query) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<String> ippoolDatas = ipPoolManagementReadPlatformService.retrieveIpPoolIDArray(query);
		IpPoolManagementData data=new IpPoolManagementData(ippoolDatas);
		return this.toApiJsonSerializer.serialize(data);

	}
	
	@PUT
	@Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String editIpPool(@PathParam("id") final Long id,final String requestData) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateIpPoolManagement(id).withJson(requestData).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    } 
	
	
	@GET
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveClientIpPoolDetails(@Context final UriInfo uriInfo,@PathParam("clientId") final Long clientId) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<IpPoolManagementData> ipPoolManagementDatas = ipPoolManagementReadPlatformService.retrieveClientIpPoolDetails(clientId);
		return this.toApiJsonSerializer.serialize(ipPoolManagementDatas);
	}
	
	@PUT
	@Path("ping/{id}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String gatStatusOfIP(@PathParam("id") final Long id,final String requestData) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateIpStatus(id).withJson(requestData).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    } 
	
	@PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateNetMaskAsDescription(final String requestData) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateIpDescription().withJson(requestData).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    } 
	
	@PUT
	@Path("updatestatus")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateIpStatus(final String requestData) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateIpAddressStatus().withJson(requestData).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    } 
	
	@PUT
	@Path("staticip")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateStaticIp(final String requestData) {
		
        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateStaticIpAddress().withJson(requestData).build();
        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
        return this.toApiJsonSerializer.serialize(result);
    }
	
	@GET
	@Path("id/{poolId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSingleIpPoolDetails(@Context final UriInfo uriInfo,@PathParam("poolId") final Long poolId) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final Collection<MCodeData> codeValueDatas=this.codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_IP_TYPE);
		final List<IpPoolManagementData> ipPoolManagementDatas = ipPoolManagementReadPlatformService.retrieveSingleIpPoolDetails(poolId);
		final IpPoolData singleIpPoolData=new IpPoolData(codeValueDatas,ipPoolManagementDatas);
		return this.toApiJsonSerializer.serialize(singleIpPoolData);
	}
}




