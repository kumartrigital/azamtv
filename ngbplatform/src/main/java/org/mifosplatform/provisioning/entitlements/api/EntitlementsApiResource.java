package org.mifosplatform.provisioning.entitlements.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.billing.selfcare.service.SelfCareRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.provisioning.entitlements.data.ClientEntitlementData;
import org.mifosplatform.provisioning.entitlements.data.CubiwareData;
import org.mifosplatform.provisioning.entitlements.data.EntitlementsData;
import org.mifosplatform.provisioning.entitlements.data.StakerData;
import org.mifosplatform.provisioning.entitlements.exception.CubiwareFailureStatusCodeException;
import org.mifosplatform.provisioning.entitlements.exception.CubiwareRequiredDataNotFoundException;
import org.mifosplatform.provisioning.entitlements.service.EntitlementReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Path("/entitlements")
@Component
@Scope("singleton")
public class EntitlementsApiResource {

	private final static Logger logger = LoggerFactory.getLogger(EntitlementsApiResource.class);
	private  final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("id,prepareReqId,product,requestType,hardwareId,provisioingSystem,serviceId," +
			"results,error,status","orderId","startDate","endDate","serviceType"));

	private final String resourceNameForPermissions = "CREATE_ENTITLEMENT";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<EntitlementsData> toApiJsonSerializer;
	private final DefaultToApiJsonSerializer<ClientEntitlementData> toSerializer;
   private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private EntitlementReadPlatformService entitlementReadPlatformService;
	private final DaoAuthenticationProvider customAuthenticationProvider;
	private final ConfigurationRepository configurationRepository;
	private final SelfCareRepository selfCareRepository;
	private final DefaultToApiJsonSerializer<CubiwareData> toApiJsonSerializerForCubiware;
	
	@Autowired
	 public EntitlementsApiResource(final PlatformSecurityContext context,  final DefaultToApiJsonSerializer<EntitlementsData> toApiJsonSerializer,
	 final ApiRequestParameterHelper apiRequestParameterHelper, final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	 final EntitlementReadPlatformService comvenientReadPlatformService,final DaoAuthenticationProvider customAuthenticationProvider,
	 final DefaultToApiJsonSerializer<ClientEntitlementData> toSerializer,final ConfigurationRepository configurationRepository,
	 final SelfCareRepository selfCareRepository,final DefaultToApiJsonSerializer<CubiwareData> toApiJsonSerializerForCubiware)
	{
				        this.context = context;
				        this.toApiJsonSerializer = toApiJsonSerializer;
				        this.apiRequestParameterHelper = apiRequestParameterHelper;
				        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
				        this.entitlementReadPlatformService=comvenientReadPlatformService;
				        this.customAuthenticationProvider=customAuthenticationProvider;				     
				        this.toSerializer=toSerializer;
				        this.configurationRepository=configurationRepository;
				        this.selfCareRepository=selfCareRepository;
				        this.toApiJsonSerializerForCubiware=toApiJsonSerializerForCubiware;
	}	
	
	@GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
   public String retrievedata(@QueryParam("no") final Long No,@QueryParam("provisioningSystem") final String provisioningSystem,
		    @QueryParam("serviceType") final String serviceType,@Context final UriInfo uriInfo)
	{
	       this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        List<EntitlementsData> data=this.entitlementReadPlatformService.getProcessingData(No, provisioningSystem, serviceType);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	}
	
	@POST
	@Path("{msgId}")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String insertdata(@PathParam("msgId") final Long id,final String apiRequestBodyAsJson) {
			System.out.println("request json is  :"+apiRequestBodyAsJson);
		   final CommandWrapper commandRequest=new CommandWrapperBuilder().createEntitlement(id).withJson(apiRequestBodyAsJson).build();
		   final CommandProcessingResult result=this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		   return this.toApiJsonSerializer.serialize(result);
		}

	@GET
	@Path("/getuser")
    @Produces({ MediaType.APPLICATION_JSON })
   public String retrieveDeviceData(@Context final UriInfo uriInfo,@QueryParam("mac") final String Mac)
	{

		try {
		       StakerData data=this.entitlementReadPlatformService.getData(Mac);
		       final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		       EntitlementsData datas=new EntitlementsData();
		        if(data==null){			   
			        JSONObject object=new JSONObject();	     
			        object.put("error", "Does not have any orders");
			        object.put("status", "ERROR");	
			        object.put("results", JSONObject.NULL);					        		        		       	 
			        return object.toString();
		        }else{
			        datas.setStatus("OK");
			        datas.setResults(data);  
			        return this.toApiJsonSerializer.serialize(settings, datas, RESPONSE_DATA_PARAMETERS);
		        }  
	        } catch (JSONException e) {
	        	return e.getMessage();
			} catch (Exception e) {
	        	return e.getMessage();
			}      

	}
	
	@GET
	@Path("/getauth")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
   public String retrieveAuthorizeData(@QueryParam("login") final String username, @QueryParam("password") final String password,@Context final UriInfo uriInfo)
	{
		try {
		    logger.info("Request comming for Authentication");
	        ClientEntitlementData data=null;
	        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
	        Authentication authenticationCheck = this.customAuthenticationProvider.authenticate(authentication);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        
	        if (authenticationCheck.isAuthenticated()) {
		        String status="OK";
		        boolean results=true;
		        data=new ClientEntitlementData(status, results);
		        logger.info("Output - status : "+status+" ,result : "+results);
		        logger.info("Authentication Successful");
		        return this.toSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	        }else{
	        	JSONObject object=new JSONObject();	     
		        object.put("error", "Does not have any orders");
		        object.put("results", JSONObject.NULL);
				object.put("status", "ERROR");	        		        		       	     	        
	        	logger.info("Output - status : ERROR , error : Does not have any orders ,result : null");
	        	logger.info("Authentication Failure");
	        	return object.toString();
	        }
		} catch (JSONException e) {
        	return e.getMessage();
		} catch (Exception e) {
        	return e.getMessage();
		} 
	     
	      
	}
	
	@GET
	@Path("/beenius")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
   public String getDataForBeenius(@QueryParam("no") final Long No,@QueryParam("provisioningSystem") final String provisioningSystem,
		                     @Context final UriInfo uriInfo)
	{
	       context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        List<EntitlementsData> data=this.entitlementReadPlatformService.getBeeniusProcessingData(No,provisioningSystem);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	}
	
	@GET
	@Path("/zebraott")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
   public String getDataForZebraOtt(@QueryParam("no") final Long No,@QueryParam("provisioningSystem") final String provisioningSystem,
		                     @Context final UriInfo uriInfo)
	{
	       context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        List<EntitlementsData> data=this.entitlementReadPlatformService.getZebraOTTProcessingData(No,provisioningSystem);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	}
	
	@GET
	@Path("/cubiware")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
   public String getDataForCubiWare(@QueryParam("no") final Long No,@QueryParam("provisioningSystem") final String provisioningSystem,
		                     @Context final UriInfo uriInfo)
	{
	       context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	        List<EntitlementsData> data=this.entitlementReadPlatformService.getCubiWareProcessingData(No,provisioningSystem);
	        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
	        return this.toApiJsonSerializer.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	}
	
	@GET
	@Path("{clientId}/getDevice")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveDeviceData(@PathParam("clientId") final Long clientId, @Context final UriInfo uriInfo) {
		
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<CubiwareData> data = getDeviceData(clientId);
		if(null == data){
			return null;
		}
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializerForCubiware.serialize(settings, data, RESPONSE_DATA_PARAMETERS);
	}

	private List<CubiwareData> getDeviceData(Long clientId) {
		
		BufferedReader bufferedReader = null;
		String output,outputData;
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
			List<CubiwareData> arrayList = new ArrayList<CubiwareData>();

			String cubiwateObj = CubiwareCredentialReturn(clientId);

			JSONObject jsonObject = new JSONObject(cubiwateObj);

			String url = jsonObject.getString(ConfigurationConstants.CUBIWARE_URL_NAME);
			String encodedToken = jsonObject.getString(ConfigurationConstants.CUBIWARE_ENCODED_PASSWORD);
			Long subscriberId = jsonObject.getLong(ConfigurationConstants.CUBIWARE_SUBSCRIBERID);

			// HttpGet get = new HttpGet("http://202.88.232.250:10380/rest/customers/185/devices");
			String getUrl = url + ConfigurationConstants.CUBIWARE_CUSTOMER_URL + "/" 
					+ subscriberId + "/" + ConfigurationConstants.CUBIWARE_DEVICE_URL;

			HttpGet get = new HttpGet(getUrl);

			get.setHeader(ConfigurationConstants.CUBIWARE_AUTHORIZATION, ConfigurationConstants.CUBIWARE_BASIC_NAME + encodedToken);

			HttpResponse response = httpClient.execute(get);

			if (response.getStatusLine().getStatusCode() != 200) {
				Long statusCode = new Long(response.getStatusLine().getStatusCode());
				String error = String.valueOf(response.getStatusLine().getStatusCode());
				logger.info("Status Code:" + error + " for subdcriberId=" + subscriberId + ", clientId=" + clientId);
				
				if(statusCode>=400){
					throw new CubiwareFailureStatusCodeException(statusCode);
				}
				return null;
			}

			bufferedReader = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));	
			
			output = outputData = "";
			
			while ((output = bufferedReader.readLine()) != null) {
				outputData += output;
			}
			
			logger.info("Output From Cubiware: " + outputData);

			JSONObject devices = XML.toJSONObject(outputData).getJSONObject("devices");
			String device = devices.get("device").toString();

			if (device.startsWith("[")) {

				JSONArray devicesArray = devices.getJSONArray("device");

				for (int i = 0; i < devicesArray.length(); i++) {

					JSONObject deviceObj = devicesArray.getJSONObject(i);

					String serialNo = deviceObj.has("serial-number") ? deviceObj.getString("serial-number") : null;
					Long id = deviceObj.getJSONObject("id").getLong("content");
					String deviceModel = deviceObj.getJSONObject("device-model").getString("name");
					Long cubiwareSubscriberId = deviceObj.getJSONObject("customer-id").getLong("content");

					CubiwareData cubiwareData = new CubiwareData(serialNo, id, deviceModel, cubiwareSubscriberId, clientId);
					arrayList.add(cubiwareData);
				}

			} else if (device.startsWith("{")) {

				JSONObject deviceObj = devices.getJSONObject("device");
				String serialNo = deviceObj.has("serial-number") ? deviceObj.getString("serial-number") : null;
				Long id = deviceObj.getJSONObject("id").getLong("content");
				String deviceModel = deviceObj.getJSONObject("device-model").getString("name");
				Long cubiwareSubscriberId = deviceObj.getJSONObject("customer-id").getLong("content");

				CubiwareData cubiwareData = new CubiwareData(serialNo, id, deviceModel, cubiwareSubscriberId, clientId);
				arrayList.add(cubiwareData);
			}

			return arrayList;

		} catch (IOException e) {
			e.printStackTrace();
			logger.info("throwing IOException in getCubiwareData Api");
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			logger.info("throwing JSONException in getCubiwareData Api");
			return null;
		} finally {
			if(null != bufferedReader){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.info("throwing IOException in finally method of getCubiwareData Api");
				}
			}
			
		}
	}
	
	private String CubiwareCredentialReturn(Long clientId) throws JSONException{
		
		Configuration configuration = this.configurationRepository.findOneByName(ConfigurationConstants.CUBIWARE_CONFIG_PROPERTY);

		if (null != configuration && configuration.isEnabled()) {

			SelfCare selfcare = this.selfCareRepository.findOneByClientId(clientId);

			if (null == selfcare || selfcare.getZebraSubscriberId() == null
					|| selfcare.getZebraSubscriberId() <= 0) {
				throw new CubiwareRequiredDataNotFoundException(clientId, ConfigurationConstants.CUBIWARE_SUBSCRIBERID);
			}

			JSONObject jsonObject = new JSONObject(configuration.getValue());

			String url = jsonObject.has(ConfigurationConstants.CUBIWARE_URL_NAME) ? jsonObject
					.getString(ConfigurationConstants.CUBIWARE_URL_NAME) : null;

			String accessToken = jsonObject.has(ConfigurationConstants.CUBIWARE_ACCESS_TOKEN) ? jsonObject
					.getString(ConfigurationConstants.CUBIWARE_ACCESS_TOKEN) : null;
					
			if(null == url || null == accessToken) {
				String parameter = (null==url ? ConfigurationConstants.CUBIWARE_URL_NAME : ConfigurationConstants.CUBIWARE_ACCESS_TOKEN);
				throw new CubiwareRequiredDataNotFoundException(clientId, parameter);
			}
			
			String encodedpassword = accessToken.trim() + ":";
			String encodedToken = new String(Base64.encodeBase64(encodedpassword.getBytes()));
			
			jsonObject.put(ConfigurationConstants.CUBIWARE_SUBSCRIBERID, selfcare.getZebraSubscriberId());
			jsonObject.put(ConfigurationConstants.CUBIWARE_ENCODED_PASSWORD, encodedToken);
			
			return jsonObject.toString();
		}
		return resourceNameForPermissions;	
		
	}
	
	private String CubiwareUnAssignDevice(Long id, Long clientId) {
		
		try {
			
			HttpClient httpClient = new DefaultHttpClient();
			
			String cubiwateObj = CubiwareCredentialReturn(clientId);

			JSONObject jsonObject = new JSONObject(cubiwateObj);

			String url = jsonObject.getString(ConfigurationConstants.CUBIWARE_URL_NAME);
			String encodedToken = jsonObject.getString(ConfigurationConstants.CUBIWARE_ENCODED_PASSWORD);
			
			String putUrl = url + ConfigurationConstants.CUBIWARE_DEVICE_URL + "/" + id;		
			
			//HttpPut put = new HttpPut("http://202.88.232.250:10380/rest/devices/1166");
			HttpPut httpPut = new HttpPut(putUrl);
			httpPut.setHeader(ConfigurationConstants.CUBIWARE_AUTHORIZATION, ConfigurationConstants.CUBIWARE_BASIC_NAME + encodedToken);
			httpPut.setHeader("Content-Type", "application/xml");
			StringEntity se = new StringEntity(ConfigurationConstants.CUBIWARE_XML_DEVICE_DATA);
			httpPut.setEntity(se);
			
			HttpResponse response = httpClient.execute(httpPut);	
		
			if (response.getStatusLine().getStatusCode() != 200) {	
				Long statusCode = new Long(response.getStatusLine().getStatusCode());
				String error = String.valueOf(response.getStatusLine().getStatusCode());
				logger.info("Status Code:" + error + " for deviceId=" + id + ", clientId=" + clientId);
				
				if(statusCode>=400){
					throw new CubiwareFailureStatusCodeException(statusCode);
				}
				return null;
			}
			return ConfigurationConstants.PAYMENTGATEWAY_SUCCESS;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
			
	}
	
	private String CubiwareDeleteDevice(Long id, Long clientId) {
		
		try {
			
			HttpClient httpClient = new DefaultHttpClient();
			
			String cubiwateObj = CubiwareCredentialReturn(clientId);

			JSONObject jsonObject = new JSONObject(cubiwateObj);

			String url = jsonObject.getString(ConfigurationConstants.CUBIWARE_URL_NAME);
			String encodedToken = jsonObject.getString(ConfigurationConstants.CUBIWARE_ENCODED_PASSWORD);
			
			String deleteUrl = url + ConfigurationConstants.CUBIWARE_DEVICE_URL + "/" + id;		
			
			//HttpDelete get = new HttpDelete("http://202.88.232.250:10380/rest/devices/"+id1);
			HttpDelete httpDelete = new HttpDelete(deleteUrl);
			httpDelete.setHeader(ConfigurationConstants.CUBIWARE_AUTHORIZATION, ConfigurationConstants.CUBIWARE_BASIC_NAME + encodedToken);
			
			HttpResponse response = httpClient.execute(httpDelete);	
		
			if (response.getStatusLine().getStatusCode() != 200) {	
				Long statusCode = new Long(response.getStatusLine().getStatusCode());
				String error = String.valueOf(response.getStatusLine().getStatusCode());
				logger.info("Status Code:" + error + " for deviceId=" + id + ", clientId=" + clientId);
				
				if(statusCode>=400){
					throw new CubiwareFailureStatusCodeException(statusCode);
				}
				return null;
			}
			return ConfigurationConstants.PAYMENTGATEWAY_SUCCESS;
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@POST
	@Path("{clientId}/{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public String unAssignCubiwareDevice(@PathParam("clientId") final Long clientId, @PathParam("id") final Long id, 
			final String apiRequestBodyAsJson) {
		
		String response = CubiwareUnAssignDevice(id, clientId);
		if(null != response && response.equalsIgnoreCase(ConfigurationConstants.PAYMENTGATEWAY_SUCCESS)) {
			response = CubiwareDeleteDevice(id, clientId);
		}
		
		return response;
	}
	
	@GET
	@Path("groupbyfingerprint")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveprovisioning(@Context final UriInfo uriInfo, @QueryParam("limit") final String limit,@QueryParam("offset") final String offset){
		/*this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);*/
		final Page<EntitlementsData>entitlement = this.entitlementReadPlatformService.retrivegroupbyfingerprint(limit ,offset);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(entitlement);
		
	}
	

	
	
	
}
