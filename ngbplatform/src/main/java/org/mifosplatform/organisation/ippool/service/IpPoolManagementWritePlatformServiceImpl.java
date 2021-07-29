package org.mifosplatform.organisation.ippool.service;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.ippool.data.IpGeneration;
import org.mifosplatform.organisation.ippool.data.IpPoolManagementData;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementDetail;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementJpaRepository;
import org.mifosplatform.organisation.ippool.exception.IpAddresNotAvailableException;
import org.mifosplatform.organisation.ippool.serialization.IpPoolManagementCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author ashokreddy
 * 
 */
@Service
public class IpPoolManagementWritePlatformServiceImpl implements
		IpPoolManagementWritePlatformService {

	private final PlatformSecurityContext context;
	private final IpPoolManagementCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final IpPoolManagementJpaRepository ipPoolManagementJpaRepository;
	private final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService;
	private final ConfigurationRepository globalConfigurationRepository;
	private final MCodeReadPlatformService codeReadPlatformService;
	private final ProcessRequestRepository processRequestRepository;

	@Autowired
	public IpPoolManagementWritePlatformServiceImpl(
			final PlatformSecurityContext context,
			final IpPoolManagementJpaRepository ipPoolManagementJpaRepository,
			final IpPoolManagementCommandFromApiJsonDeserializer apiJsonDeserializer,
			final IpPoolManagementReadPlatformService ipPoolManagementReadPlatformService,
			final ConfigurationRepository globalConfigurationRepository,
			final MCodeReadPlatformService codeReadPlatformService,
			final ProcessRequestRepository processRequestRepository) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.ipPoolManagementJpaRepository = ipPoolManagementJpaRepository;
		this.ipPoolManagementReadPlatformService = ipPoolManagementReadPlatformService;
		this.globalConfigurationRepository = globalConfigurationRepository;
		this.codeReadPlatformService = codeReadPlatformService;
		this.processRequestRepository = processRequestRepository;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public CommandProcessingResult createIpPoolManagement(JsonCommand command) {

		try {
			context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());

			String ipAddress = command.stringValueOfParameterNamed("ipAddress");
			Long subnet = command.longValueOfParameterNamed("subnet");
			String notes = command.stringValueOfParameterNamed("notes");
			Long type = command.longValueOfParameterNamed("type");
			Map<String, Object> generatedIPPoolID = new HashedMap();

			if (subnet != null) {
				
				String ipData = ipAddress + "/" + subnet;
				IpGeneration util = new IpGeneration(ipData, this.ipPoolManagementReadPlatformService);
				Configuration configuration = globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_INCLUDE_NETWORK_BROADCAST_IP);

				util.setInclusiveHostCount(configuration.getValue().equalsIgnoreCase("true"));
				String[] data = util.getInfo().getAllAddresses();
				for (int i = 0; i < data.length; i++) {
					int j = i + 1;
					IpPoolManagementDetail ipPoolManagementDetail = new IpPoolManagementDetail(data[i], 'I', type, notes, subnet);
					this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
					generatedIPPoolID.put(String.valueOf(j), ipPoolManagementDetail.getId());
				}
			} else {
				String i = "1";
				IpPoolManagementDetail ipPoolManagementDetail = new IpPoolManagementDetail(ipAddress, 'I', type, notes, null);
				this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
				generatedIPPoolID.put(i, ipPoolManagementDetail.getId());
			}
			return new CommandProcessingResultBuilder().with(generatedIPPoolID).build();

		} catch (DataIntegrityViolationException dve) {
			Throwable realCause = dve.getMostSpecificCause();
			if (realCause.getMessage().contains("unique_ip")) {
				final String name = command.stringValueOfParameterNamed("unique_ip");
				throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name '" + name + "' already exists");
			} else if (realCause.getMessage().contains("ip_address")) {
				final String name = command.stringValueOfParameterNamed("ip_address");
				throw new PlatformDataIntegrityException("error.msg.code.duplicate.name", "A code with name '" + name + "' already exists");
			}
			return new CommandProcessingResult(new Long(1));
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult editIpPoolManagement(JsonCommand command) {

		try {
			context.authenticatedUser();
			this.apiJsonDeserializer.validateForUpdate(command.json());

			String ipAddress = command.stringValueOfParameterNamed("ipAddress");
			String statusType = command.stringValueOfParameterNamed("statusType");
			String notes = command.stringValueOfParameterNamed("notes");
			String poolDescription = command.stringValueOfParameterNamed("ipPoolDescription");
			Long type = command.longValueOfParameterNamed("type");
			Long subNet = command.longValueOfParameterNamed("subnet");
			String ipRange = command.stringValueOfParameterNamed("ipRange");

			if (ipRange.length() > 0) {
				String maxRangeIp = ipAddress.replaceAll(ipAddress.substring(Math.max(ipAddress.length() - 2, 0)), ipRange);
				Long maxIpValue=Long.valueOf(maxRangeIp.substring(maxRangeIp.lastIndexOf(".")+1));
				Long ipValue=Long.valueOf(ipAddress.substring(ipAddress.lastIndexOf(".")+1));
				// check given ip range is incremental way or not
				if(ipValue.compareTo(maxIpValue)>0){
					throw new PlatformDataIntegrityException("error.msg.ipaddress.give.incremental.iprange", "please select incremental iprange address", "ipRange");
				}
				List<IpPoolManagementDetail> IpPoolManagementDetails = this.ipPoolManagementJpaRepository.findBetweenIpAddresses(ipAddress, maxRangeIp);
				for (IpPoolManagementDetail ipPoolDetail : IpPoolManagementDetails) {
					ipPoolDetail.setStatus(statusType.trim().charAt(0));
					ipPoolDetail.setNotes(notes);
					ipPoolDetail.setIpPoolDescription(poolDescription);
					ipPoolDetail.setType(type);
					ipPoolDetail.setSubnet(subNet);
					this.ipPoolManagementJpaRepository.save(ipPoolDetail);
					
				}
			} else {
				IpPoolManagementDetail ipPoolManagementDetail = this.ipPoolManagementJpaRepository.findOne(command.entityId());
				ipPoolManagementDetail.setStatus(statusType.trim().charAt(0));
				ipPoolManagementDetail.setNotes(notes);
				ipPoolManagementDetail.setIpPoolDescription(poolDescription);
				ipPoolManagementDetail.setType(type);
				ipPoolManagementDetail.setSubnet(subNet);
				this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
			}
			return new CommandProcessingResult(command.entityId());

		} catch (DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		}

	}

	@Override
	public CommandProcessingResult updateIpStatus(Long entityId) {

		try {

			this.context.authenticatedUser();
			IpPoolManagementDetail ipPoolManagementDetail = this.ipPoolManagementJpaRepository.findOne(entityId);

			if (ipPoolManagementDetail == null) {
				throw new IpAddresNotAvailableException(entityId.toString());
			}
			InetAddress inet = InetAddress.getByName(ipPoolManagementDetail.getIpAddress());
			boolean status = inet.isReachable(5000);
			if (status) {
				return new CommandProcessingResult("ACTIVE");
			} else {
				return new CommandProcessingResult("DeACTIVE");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return CommandProcessingResult.empty();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandProcessingResult updateIpDescription(JsonCommand command) {

		context.authenticatedUser();
		this.apiJsonDeserializer.validateForUpdateDecription(command.json());

		Map<String, Object> generatedIPPoolID = new HashedMap();
		String search = command.stringValueOfParameterNamed("ipAndSubnet");
		IpGeneration ipGeneration = new IpGeneration(search, this.ipPoolManagementReadPlatformService);
		Configuration configuration = globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_INCLUDE_NETWORK_BROADCAST_IP);
		ipGeneration.setInclusiveHostCount(configuration.getValue().equalsIgnoreCase("true"));
		String[] data = ipGeneration.getInfo().getsubnetAddresses();
		String ipPoolDescription = ipGeneration.getInfo().getNetmask();

		for (int i = 0; i < data.length; i++) {
			int j = i + 1;
			IpPoolManagementData ipPoolManagementData = this.ipPoolManagementReadPlatformService.retrieveIdByIpAddress(data[i]);
			
			if(ipPoolManagementData != null){
				IpPoolManagementDetail ipPoolManagementDetail = this.ipPoolManagementJpaRepository.findOne(ipPoolManagementData.getId());
				ipPoolManagementDetail.setIpPoolDescription(ipPoolDescription); // netMask
																				// id
				this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
				generatedIPPoolID.put(String.valueOf(j), ipPoolManagementDetail.getId());
			}
			
		}
		return new CommandProcessingResultBuilder().with(generatedIPPoolID).build();
	}

	@Override
	public CommandProcessingResult updateIpAddressStatus(JsonCommand command) {

		try {

			@SuppressWarnings("unused")
			String[] ipAddressArray = null;
			final String ipValue = command.stringValueOfParameterNamed("ipAddress");
			final String status = command.stringValueOfParameterNamed("status");
			IpPoolManagementDetail ipPoolManagementDetail = this.ipPoolManagementJpaRepository.findAllocatedIpAddressData(ipValue);

			if (ipPoolManagementDetail != null) {
				ipPoolManagementDetail.setStatus(status.trim().charAt(0));
				ipPoolManagementDetail.setClientId(null);
				this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
			}

			/*
			 * if(ipValue.contains("/")){ IpGeneration ipGeneration=new
			 * IpGeneration(ipValue,this.ipPoolManagementReadPlatformService);
			 * ipAddressArray=ipGeneration.getInfo().getsubnetAddresses();
			 * 
			 * for(int i=0;i<ipAddressArray.length;i++){ IpPoolManagementDetail
			 * ipPoolManagementDetail=
			 * this.ipPoolManagementJpaRepository.findAllocatedIpAddressData
			 * (ipAddressArray[i]);
			 * 
			 * if(ipPoolManagementDetail != null){
			 * ipPoolManagementDetail.setStatus(status.charAt(0));
			 * ipPoolManagementDetail.setClientId(null);
			 * this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
			 * } } }else{
			 * 
			 * JSONArray ipAddresses = new JSONArray(ipValue); for(int
			 * i=0;i<ipAddresses.length();i++){ IpPoolManagementDetail
			 * ipPoolManagementDetail=
			 * this.ipPoolManagementJpaRepository.findAllocatedIpAddressData
			 * (ipAddresses.getString(i)); if(ipPoolManagementDetail != null){
			 * ipPoolManagementDetail.setStatus(status.charAt(0));
			 * ipPoolManagementDetail.setClientId(null);
			 * this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
			 * } } }
			 */

			return new CommandProcessingResult(1l);
		} catch (DataIntegrityViolationException exception) {
			exception.printStackTrace();
			return null;
		}
	}

	@Override
	public CommandProcessingResult updateStaticIpStatus(JsonCommand command) {
		
		try {

			final String ipAddress = command.stringValueOfParameterNamed("ipAddress");
			final String status = command.stringValueOfParameterNamed("status");
			final Long clientId = command.longValueOfParameterNamed("clientId");
			final String staticIpType = command.stringValueOfParameterNamed("staticIpType");
			final String poolName = command.stringValueOfParameterNamed("poolName");
			
			IpPoolManagementDetail ipPoolBasedOnclientId = this.ipPoolManagementJpaRepository.findByClientId(clientId);
			
			if(ipPoolBasedOnclientId != null && staticIpType.equalsIgnoreCase("remove")){
				
				ipPoolBasedOnclientId.setStatus(status.trim().charAt(0));
				ipPoolBasedOnclientId.setClientId(null);
				this.ipPoolManagementJpaRepository.save(ipPoolBasedOnclientId);
				processRequestData(command, clientId);
				
			}else if(ipPoolBasedOnclientId != null && staticIpType.equalsIgnoreCase("create")){
				
				throw new IpAddresNotAvailableException(clientId);
			}else if(!staticIpType.equalsIgnoreCase("remove") && staticIpType.equalsIgnoreCase("create")){
				
				IpPoolManagementDetail ipPoolManagementDetail = this.ipPoolManagementJpaRepository.findIpAddressData(ipAddress);
				if (ipPoolManagementDetail != null) {
					ipPoolManagementDetail.setStatus(status.trim().charAt(0));
					ipPoolManagementDetail.setClientId(clientId);
					this.ipPoolManagementJpaRepository.save(ipPoolManagementDetail);
					processRequestData(command, clientId);
				}else if(ipPoolManagementDetail == null && poolName.equalsIgnoreCase("Adhoc")){
					throw new IpAddresNotAvailableException("");
				}else if(ipPoolManagementDetail == null && !poolName.equalsIgnoreCase("Adhoc")){
					Collection<MCodeData> codeValueDatas = this.codeReadPlatformService.getCodeValue(CodeNameConstants.CODE_IP_TYPE);
					Long type = null;
					if(codeValueDatas.iterator().next().getmCodeValue().equalsIgnoreCase("Public")){
						type = codeValueDatas.iterator().next().getId();
					}
					IpPoolManagementDetail ipPoolCreate = new IpPoolManagementDetail(ipAddress, status.trim().charAt(0), type, null, null);
					ipPoolCreate.setClientId(clientId);
					this.ipPoolManagementJpaRepository.save(ipPoolCreate);
					processRequestData(command, clientId);
				}
			}else{
				throw new IpAddresNotAvailableException();
			}
			
			return new CommandProcessingResult(1l);
		} catch (DataIntegrityViolationException exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	public void processRequestData(JsonCommand command, Long clientId){
		
		final ProcessRequest processRequest=new ProcessRequest(Long.valueOf(0), clientId, Long.valueOf(0), 
				"Radius", "STATIC_IP", 'N', 'N');
		final ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(Long.valueOf(0), Long.valueOf(0),
				command.json(), "Recieved", "", new Date(), null, 
				null,null, 'N',"STATIC_IP","");

  		processRequest.add(processRequestDetails);
  		this.processRequestRepository.save(processRequest);
	}

}
