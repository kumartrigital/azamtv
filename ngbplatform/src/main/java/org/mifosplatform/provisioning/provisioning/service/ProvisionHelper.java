
package org.mifosplatform.provisioning.provisioning.service;

import java.util.Collection;
import java.util.List;

import net.sf.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.organisation.ippool.data.IpGeneration;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementDetail;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementJpaRepository;
import org.mifosplatform.organisation.ippool.exception.IpAddresAllocatedException;
import org.mifosplatform.organisation.ippool.exception.IpNotAvailableException;
import org.mifosplatform.organisation.ippool.service.IpPoolManagementReadPlatformService;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.service.GroupData;
import org.mifosplatform.portfolio.group.service.GroupReadPlatformService;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@Service
public class ProvisionHelper {

	private final FromJsonHelper fromJsonHelper;
	private final ServiceParametersRepository serviceParametersRepository;
	private final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	private final ClientRepository clientRepository;
	private final IpPoolManagementJpaRepository ipPoolManagementJpaRepository;
	private final OrderRepository orderRepository;
	private final PlanRepository planRepository;
	private final ProvisioningReadPlatformService provisioningReadPlatformService;
	private final GroupReadPlatformService groupReadPlatformService;
	
@Autowired
public ProvisionHelper(final FromJsonHelper fromJsonHelper,final ServiceParametersRepository serviceParametersRepository,
		final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService,final ConfigurationRepository configurationRepository,
		final IpPoolManagementJpaRepository ipPoolManagementJpaRepository,final ClientRepository clientRepository,
		final OrderRepository orderRepository,final PlanRepository planRepository,final ProvisioningReadPlatformService provisioningReadPlatformService,
		final GroupReadPlatformService groupReadPlatformService){
	
	this.fromJsonHelper=fromJsonHelper;
	this.serviceParametersRepository=serviceParametersRepository;
	this.configurationRepository=configurationRepository;
	this.ipPoolManagementJpaRepository=ipPoolManagementJpaRepository;
	this.clientRepository=clientRepository;
	this.orderRepository=orderRepository;
	this.planRepository=planRepository;
	this.provisioningReadPlatformService=provisioningReadPlatformService;
	this.ipPoolManagementReadPlatformService=ipPoolManagementReadPlatformService;
	this.groupReadPlatformService=groupReadPlatformService;
	
}
	
	
	public JSONObject provisionAssemblerForm(final JsonElement element,final Long clientId,final String macId,final String planName, 
			                               Long orderId, String iprange, String ipType, Long subnet) {
	
		final JsonArray serviceParameters = fromJsonHelper.extractJsonArrayNamed("serviceParameters", element);
		final JSONObject jsonObject = new JSONObject();
		String[] ipAddressArray = null;
		for (JsonElement jsonElement : serviceParameters) {
		

		final ServiceParameters serviceParameter = ServiceParameters.fromJson(jsonElement, fromJsonHelper, clientId, orderId,
						planName, "ACTIVE", iprange, subnet);
		
		this.serviceParametersRepository.saveAndFlush(serviceParameter);

		// ip_pool_data status updation
		final String paramName = fromJsonHelper.extractStringNamed("paramName", jsonElement);

		if (paramName.equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_IPADDRESS)) {

			if (iprange.equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_SUBNET)) {
				final String ipAddress = fromJsonHelper.extractStringNamed("paramValue", jsonElement);
				String ipData = ipAddress + "/" + subnet;
				IpGeneration ipGeneration = new IpGeneration(ipData, this.ipPoolManagementReadPlatformService);
				final Configuration configuration = this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_INCLUDE_NETWORK_BROADCAST_IP);
				ipGeneration.setInclusiveHostCount(configuration.getValue().equalsIgnoreCase("true"));
				ipAddressArray = ipGeneration.getInfo().getsubnetAddresses();
				
				for (int i = 0; i < ipAddressArray.length; i++) {
					IpPoolManagementDetail ipPoolManagementDetail = this.ipPoolManagementJpaRepository.findIpAddressData(ipAddressArray[i]);	
					if (ipPoolManagementDetail == null) {
						throw new IpAddresAllocatedException(
								ipAddressArray[i]);
					}
				}
				
				jsonObject.put(ProvisioningApiConstants.PROV_DATA_SUBNET, subnet);

			} else {
				ipAddressArray = fromJsonHelper.extractArrayNamed("paramValue", jsonElement);
			}

			for (String ipAddress : ipAddressArray) {

				IpPoolManagementDetail ipPoolManagementDetail = this.ipPoolManagementJpaRepository.findIpAddressData(ipAddress);
				
				if (ipPoolManagementDetail == null) {
					throw new IpNotAvailableException(ipAddress);
				}
				ipPoolManagementDetail.setStatus('A');
				ipPoolManagementDetail.setClientId(clientId);
				this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
			}
		}
		jsonObject.put(serviceParameter.getParameterName(), serviceParameter.getParameterValue());
		
		}
		Client client = this.clientRepository.findOne(clientId);
		jsonObject.put(ProvisioningApiConstants.PROV_DATA_CLIENTID, client.getAccountNo());
		jsonObject.put(ProvisioningApiConstants.PROV_DATA_CLIENTNAME, client.getFirstname());
		jsonObject.put(ProvisioningApiConstants.PROV_DATA_ORDERID, orderId);
		jsonObject.put(ProvisioningApiConstants.PROV_DATA_PLANNAME, planName);
		jsonObject.put(ProvisioningApiConstants.PROV_DATA_MACID, macId);
		jsonObject.put(ProvisioningApiConstants.PROV_DATA_IPTYPE, ipType);
		return jsonObject;
	}


	public JSONObject buildJsonForOrderProvision(Long clientId,String planName, String requestType, String groupname,String serialNo,
			                          Long orderId, String newSwerialNumber, Long newOrderId, List<ServiceParameters> parameters){
	
		try {
			Client client = this.clientRepository.findOne(clientId);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(ProvisioningApiConstants.PROV_DATA_CLIENTID, client.getAccountNo());
			jsonObject.put(ProvisioningApiConstants.PROV_DATA_CLIENTNAME, client.getFirstname());
			jsonObject.put(ProvisioningApiConstants.PROV_DATA_ORDERID,orderId);
			jsonObject.put(ProvisioningApiConstants.PROV_DATA_MACID, newSwerialNumber);
				
			if (requestType.equalsIgnoreCase(UserActionStatusTypeEnum.CHANGE_PLAN.toString())) {
				jsonObject.put("New_" + ProvisioningApiConstants.PROV_DATA_PLANNAME, planName);
				Order oldorder = this.orderRepository.findOne(orderId);
				Plan plan = this.planRepository.findOne(oldorder.getPlanId());
				jsonObject.put("Old_" + ProvisioningApiConstants.PROV_DATA_PLANNAME, plan.getCode());
			
			} else{
				jsonObject.put(ProvisioningApiConstants.PROV_DATA_PLANNAME, planName);
			
			}if (groupname != null) {
				jsonObject.put(ProvisioningApiConstants.PROV_DATA_OLD_GROUPNAME, groupname);
			
			}if (serialNo != null) {
				jsonObject.put(ProvisioningApiConstants.PROV_DATA_OLD_SERIALNO, serialNo);
				jsonObject.put(ProvisioningApiConstants.PROV_DATA_NEW_SERIALNO, newSwerialNumber);
			
			}if (requestType.equalsIgnoreCase(UserActionStatusTypeEnum.TERMINATION.toString())) {
				jsonObject.put("perminateDelete", "true");
			}
			
			for (ServiceParameters serviceParameters : parameters) {
				String newParamName = null;
				String newParamValue = null;
				if (serviceParameters.getParameterName().equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_IPADDRESS)) {
					if (serviceParameters.getParameterValue().contains("/")) {
						jsonObject.put(ProvisioningApiConstants.PROV_DATA_IPTYPE, "Subnet");
					
					} else if (serviceParameters.getParameterValue().contains("[")) {
						JSONArray jsonArray;
						jsonArray = new JSONArray(serviceParameters.getParameterValue());
					if (jsonArray.length() > 1)
						jsonObject.put(ProvisioningApiConstants.PROV_DATA_IPTYPE, "Multiple");
				} else {
					jsonObject.put(ProvisioningApiConstants.PROV_DATA_IPTYPE, "Single");
				}
				newParamName = serviceParameters.getParameterName();
				newParamValue = serviceParameters.getParameterValue();
			}

				
				if (serviceParameters.getParameterName().equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_GROUPNAME)
					&& groupname != null) {
				
					jsonObject.put("NEW_" + serviceParameters.getParameterName(), serviceParameters.getParameterValue());
					newParamName = serviceParameters.getParameterName();
					newParamValue = serviceParameters.getParameterValue();
				
				} else {
					if (serviceParameters.getParameterName().equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_SERVICE) 
						&& requestType.equalsIgnoreCase(UserActionStatusTypeEnum.CHANGE_PLAN.toString())) {

						List<ServiceParameterData> serviceDatas = this.provisioningReadPlatformService.getSerivceParameters(orderId);
						List<ServiceParameterData> oldServiceDatas = this.provisioningReadPlatformService.getSerivceParameters(orderId);
						jsonObject.put("NEW_" + serviceParameters.getParameterName(), serviceDatas.get(0).getParamValue());
						jsonObject.put("OLD_" + serviceParameters.getParameterName(), oldServiceDatas.get(0).getParamValue());
						jsonObject.put(ProvisioningApiConstants.PROV_DATA_OLD_ORDERID, orderId);
						newParamName = serviceParameters.getParameterName();
						newParamValue = serviceDatas.get(0).getParamValue();
					
					} else if (serviceParameters.getParameterName().equalsIgnoreCase(ProvisioningApiConstants.PROV_DATA_GROUPNAME)
							&& requestType.equalsIgnoreCase(UserActionStatusTypeEnum.CHANGE_PLAN.toString())) {

						Collection<GroupData> groupDatas = this.groupReadPlatformService.retrieveGroupServiceDetails(newOrderId);
						for (GroupData groupData : groupDatas) {
							jsonObject.put("NEW_" + serviceParameters.getParameterName(), groupData.getGroupName());
							newParamValue = groupData.getGroupName();
						}

						jsonObject.put("OLD_" + serviceParameters.getParameterName(), serviceParameters.getParameterValue());
						newParamName = serviceParameters.getParameterName();
					
					} else {
						jsonObject.put(serviceParameters.getParameterName(), serviceParameters.getParameterValue());
						newParamName = serviceParameters.getParameterName();
						newParamValue = serviceParameters.getParameterValue();
					}
				}
				serviceParameters.setStatus("INACTIVE");
				this.serviceParametersRepository.save(serviceParameters);
				ServiceParameters newServiceParameters = new ServiceParameters(clientId,newOrderId, planName,
					newParamName, newParamValue, "ACTIVE");
			this.serviceParametersRepository.save(newServiceParameters);
			}
			return jsonObject;
	
		}catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
