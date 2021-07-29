package org.mifosplatform.organisation.address.api;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.data.AddressLocationDetails;
import org.mifosplatform.organisation.address.data.CityDetailsData;
import org.mifosplatform.organisation.address.exception.AddressNoRecordsFoundException;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.portfolio.service.data.ServiceMasterData;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@Path("/address")
@Component
@Scope("singleton")
public class ClientAddressApiResource {
	
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("addressid","clientId",
            "addressNo","street","zipCode","city","state","country","datas","countryData","stateData","cityData","addressOptionsData","district","districtData"));
    private final String resourceNameForPermissions = "ADDRESS";
    private final String resourceNameForLocationPermissions = "LOCATION";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<AddressData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;

	@Autowired
	public ClientAddressApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<AddressData> toApiJsonSerializer, 
			final ApiRequestParameterHelper apiRequestParameterHelper,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final AddressReadPlatformService addressReadPlatformService, final ServiceMasterReadPlatformService serviceMasterReadPlatformService) {
		
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.addressReadPlatformService=addressReadPlatformService;
		        this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
		    }
	@GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAddressTemplateInfo(@Context final UriInfo uriInfo) {
        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
    final List<String> countryData = this.addressReadPlatformService.retrieveCountryDetails();
    final List<String> statesData = this.addressReadPlatformService.retrieveStateDetails();
    final List<String> citiesData = this.addressReadPlatformService.retrieveCityDetails();
    final List<EnumOptionData> enumOptionDatas = this.addressReadPlatformService.addressType();
    final List<String> districtData = this.addressReadPlatformService.retrieveDistrictDetails();
    final AddressData data=new AddressData(null,countryData,statesData,citiesData,enumOptionDatas,districtData);
    final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
    return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
    
    }
	
	@GET
    @Path("{selectedname}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAddressdetails( @Context final UriInfo uriInfo,	@PathParam("selectedname") final String selectedname ) {
        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        final List<AddressData> citiesData = this.addressReadPlatformService.retrieveCityDetails(selectedname);
        final List<EnumOptionData> enumOptionDatas = this.addressReadPlatformService.addressType();
        final AddressData data=new AddressData(citiesData, null, null, null,enumOptionDatas,null);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
    }
	
	/**
	 * this method is using for getting Client Address data with clientId
	 */
	@GET
	@Path("addressdetails/{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveClientAddress( @PathParam("clientId") final Long clientId,@QueryParam("addressType")String addressType, @Context final UriInfo uriInfo) {
        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        final List<AddressData> addressdata = this.addressReadPlatformService.retrieveAddressDetailsBy(clientId,addressType);
        final List<String> citiesData = this.addressReadPlatformService.retrieveCityDetails();
        final List<EnumOptionData> enumOptionDatas = this.addressReadPlatformService.addressType();
        final AddressData data=new AddressData(addressdata,null,null,citiesData,enumOptionDatas,null);
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	}
	
	/**
	 * this method is using for getting state and country name by using city name
	 */
	@GET
	@Path("template/{cityName}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAddressByCityName(@PathParam("cityName") final String cityName , @Context final UriInfo uriInfo) {
        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
      
        final AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(cityName);
     
        if(addressData== null){
        	throw new AddressNoRecordsFoundException("city");
        }
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, addressData, RESPONSE_DATA_PARAMETERS);
	}
	
	
	@GET
	@Path("template/statename/{stateName}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAddressBystateName(@PathParam("stateName") final String stateName , @Context final UriInfo uriInfo) {
        context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
      
        final List<AddressData> addressData = this.addressReadPlatformService.retrieveAdressBystate(stateName);
     
        if(addressData== null){
        	throw new AddressNoRecordsFoundException("state");
        }
        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, addressData, RESPONSE_DATA_PARAMETERS);
	}
	
	
	
	
	/**
	 * this method is using for creating new  Client Address
	 */
	@POST
	@Path("{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createNewClientAddress(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().createAddress(clientId).withJson(apiRequestBodyAsJson).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
	}

	/**
	 * this method is using for editing Client Address
	 */
	/*@PUT
	@Path("{clientId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String updateClientAddress(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson){
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateAddress(clientId).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);

	}*/
	@PUT
	@Path("rec/{addrId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String updateAddress(@PathParam("addrId") final Long addrId, final String apiRequestBodyAsJson){
		
		JSONObject object;
		try {
			object = new JSONObject(apiRequestBodyAsJson);
			JSONArray addressArray = object.optJSONArray("addressJson");
			CommandProcessingResult result = null;
			JSONObject addressObject = null;
			for(int i=0; i<addressArray.length() ; i++){
				addressObject = addressArray.optJSONObject((i));
				final CommandWrapper commandRequest = new CommandWrapperBuilder().updateAddressNew(addressObject.getLong("id")).withJson(addressObject.toString()).build();
				result= this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			}
			  return this.toApiJsonSerializer.serialize(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * this method is using for getting data in Address Master
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAddress(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,  @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		context.authenticatedUser().validateHasReadPermission(resourceNameForLocationPermissions);
		final SearchSqlQuery searchAddresses =SearchSqlQuery.forSearch(sqlSearch, offset,limit );
		final Page<AddressLocationDetails> addresses = this.addressReadPlatformService.retrieveAllAddressLocations(searchAddresses);
		 return this.toApiJsonSerializer.serialize(addresses);
	}
	
	/**
	 * this method is using for creating Country , State , and City in Address Master
	 */
	@POST
	@Path("{entityType}/new")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createAddressLocation(@PathParam("entityType") final String entityType, final String jsonRequestBody) {
		    final CommandWrapper commandRequest = new CommandWrapperBuilder().createLocation(entityType).withJson(jsonRequestBody).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
	
	}
	
	/**
	 * this method is using for editing Country , State , and City in Address Master
	 */
	@PUT
	@Path("{entityType}/{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String updateAddressLocation(@PathParam("entityType") final String entityType,@PathParam("id") final Long id, final String apiRequestBodyAsJson){
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().updateLocation(entityType,id).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);
		  
	}
	
	/**
	 * this method is using for deleting Country , State , and City in Address Master
	 */
	@DELETE
	@Path("{entityType}/{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String deleteAddressLocation(@PathParam("entityType") final String entityType,@PathParam("id") final Long id, final String apiRequestBodyAsJson){
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteLocation(entityType,id).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);

	}
	
	@GET
	@Path("countrydetails")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String retrieveCountryDetails(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,  @QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset){
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final List<String> countryData = this.addressReadPlatformService.retrieveCountryDetails();
		 return this.toApiJsonSerializer.serialize(countryData);
	}
	
	@GET
	@Path("/city")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAddressDetailsWithcityName(@Context final UriInfo uriInfo,@QueryParam("query") final String cityName){
		
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final List<CityDetailsData> cityDetails = this.addressReadPlatformService.retrieveAddressDetailsByCityName(cityName);
		 return this.toApiJsonSerializer.serialize(cityDetails);
	}
	
	@DELETE
	@Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String deleteclientAddress(@PathParam("id") final Long id, final String apiRequestBodyAsJson){
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteAddress(id).withJson(apiRequestBodyAsJson).build();
		 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		  return this.toApiJsonSerializer.serialize(result);

	}
	
	@POST
	@Path("service/{addressId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String modifyServiceFeasibilty(@PathParam("addressId") final Long addressId, final String apiRequestBodyAsJson) {
		 final CommandWrapper commandRequest = new CommandWrapperBuilder().createServiceAvailability(addressId).withJson(apiRequestBodyAsJson).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        return this.toApiJsonSerializer.serialize(result);
	}
	
	@GET
	@Path("service/{addressType}/{addressId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String getServiceFeasibilty(@PathParam("addressType") final String addressType,@PathParam("addressId") final Long addressId, final String apiRequestBodyAsJson) {
		List<ServiceMasterData> serviceMasterDatas = this.serviceMasterReadPlatformService.retrieveAllServiceAvailabilty(addressType, addressId);
		return this.toApiJsonSerializer.serialize(serviceMasterDatas);
	}
	
	@GET
	@Path("services/{addressType}/{address}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String getServiceFeasibiltyByCity(@PathParam("addressType") final String addressType,@PathParam("address") final String address, final String apiRequestBodyAsJson) {
		List<ServiceMasterData> serviceMasterDatas = this.serviceMasterReadPlatformService.retrieveAllServiceAvailabiltyByCity(addressType, address);
		return this.toApiJsonSerializer.serialize(serviceMasterDatas);
	}
	
}
