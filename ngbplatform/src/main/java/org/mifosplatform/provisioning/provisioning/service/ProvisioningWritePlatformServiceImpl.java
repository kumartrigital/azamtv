package org.mifosplatform.provisioning.provisioning.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.crm.ticketmaster.domain.PriorityTypeEnum;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.ActivePlansFoundException;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.organisation.broadcaster.domain.Broadcaster;
import org.mifosplatform.organisation.broadcaster.exception.BroadcatserNotFoundException;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.organisation.hardwareplanmapping.service.HardwarePlanReadPlatformService;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementDetail;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementJpaRepository;
import org.mifosplatform.organisation.ippool.exception.IpNotAvailableException;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfo;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.portfolio.allocation.domain.HardwareAssociationRepository;
import org.mifosplatform.portfolio.association.domain.HardwareAssociation;
import org.mifosplatform.portfolio.association.exception.PairingNotExistException;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderLine;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.planmapping.domain.PlanMapping;
import org.mifosplatform.portfolio.planmapping.domain.PlanMappingRepository;
import org.mifosplatform.portfolio.planmapping.execption.PlanMappingNotExist;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;
import org.mifosplatform.provisioning.networkelement.domain.NetworkElement;
import org.mifosplatform.provisioning.networkelement.domain.NetworkElementRepository;
import org.mifosplatform.provisioning.networkelement.service.NetworkElementReadPlatformService;
import org.mifosplatform.provisioning.preparerequest.data.PrepareRequestData;
import org.mifosplatform.provisioning.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestReadplatformService;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestWriteplatformService;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provisioning.data.ProvisionAdapter;
import org.mifosplatform.provisioning.provisioning.domain.PriorityEnum;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningCommand;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningCommandParameters;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningCommandRepository;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestDetail;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestRepository;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParametersRepository;
import org.mifosplatform.provisioning.provisioning.exceptions.ProvisioningRequestNotFoundException;
import org.mifosplatform.provisioning.provisioning.serialization.ProvisioningCommandFromApiJsonDeserializer;
import org.mifosplatform.workflow.eventaction.service.EventActionWritePlatformServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.sf.json.JSONObject;

@Service
public class ProvisioningWritePlatformServiceImpl implements ProvisioningWritePlatformService {
	private final static Logger logger = LoggerFactory.getLogger(ProvisioningWritePlatformServiceImpl.class);

	private final FromJsonHelper fromJsonHelper;
	private final PlatformSecurityContext context;
	private final OrderRepository orderRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final ServiceMasterRepository serviceMasterRepository;
	private final ProcessRequestRepository processRequestRepository;
	private final OrderReadPlatformService orderReadPlatformService;
	private final HardwareAssociationRepository associationRepository;
	private final ServiceParametersRepository serviceParametersRepository;
	private final ProvisioningCommandRepository provisioningCommandRepository;
	private final IpPoolManagementJpaRepository ipPoolManagementJpaRepository;
	private final ProvisionHelper provisionHelper;
	private final PlanRepository planRepository;
	private final OfficeRepository officeRepository;
	private final PlanMappingRepository planMappingRepository;
	private final PrepareRequestReadplatformService prepareRequestReadplatformService;
	private final ItemDetailsRepository inventoryItemDetailsRepository;
	private final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ProcessRequestReadplatformService processRequestReadplatformService;
	private final ProcessRequestWriteplatformService processRequestWriteplatformService;
	private final ProvisioningRequestRepository provisioningRequestRepository;
	private final ClientServiceRepository clientServiceRepository;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final HardwarePlanReadPlatformService hardwarePlanReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	private final ClientRepository clientRepository;
	private final ConfigurationRepository configurationRepository;
	private final NetworkElementReadPlatformService networkElementReadPlatformService;
	private final NetworkElementRepository networkElementRepository;
	private final ClientServiceReadPlatformService clientServiceReadPlatformService;

	@Autowired
	public ProvisioningWritePlatformServiceImpl(final PlatformSecurityContext context,
			final ItemDetailsRepository inventoryItemDetailsRepository,
			final ProvisioningCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final FromJsonHelper fromApiJsonHelper, final OrderReadPlatformService orderReadPlatformService,
			final ProvisioningCommandRepository provisioningCommandRepository,
			final ServiceParametersRepository parametersRepository,
			final ProcessRequestRepository processRequestRepository, final OrderRepository orderRepository,
			final FromJsonHelper fromJsonHelper, final HardwareAssociationRepository associationRepository,
			final ServiceMasterRepository serviceMasterRepository, final ProvisionHelper provisionHelper,
			final OfficeRepository officeRepository,
			final ProcessRequestReadplatformService processRequestReadplatformService,
			final IpPoolManagementJpaRepository ipPoolManagementJpaRepository,
			final ProcessRequestWriteplatformService processRequestWriteplatformService,
			final PlanRepository planRepository,
			final PrepareRequestReadplatformService prepareRequestReadplatformService,
			final PlanMappingRepository planMappingRepository,
			final ProvisioningRequestRepository provisioningRequestRepository,
			final ClientServiceRepository clientServiceRepository,
			final MCodeReadPlatformService mCodeReadPlatformService,
			final ServiceMasterReadPlatformService serviceMasterReadPlatformService,
			final CodeValueReadPlatformService codeValueReadPlatformService,
			final HardwarePlanReadPlatformService hardwarePlanReadPlatformService,
			final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,
			final ClientRepository clientRepository, final ConfigurationRepository configurationRepository,
			final NetworkElementReadPlatformService networkElementReadPlatformService,
			final NetworkElementRepository networkElementRepository,
			final ClientServiceReadPlatformService clientServiceReadPlatformService) {

		this.context = context;
		this.fromJsonHelper = fromJsonHelper;
		this.orderRepository = orderRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.associationRepository = associationRepository;
		this.planRepository = planRepository;
		this.provisionHelper = provisionHelper;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.serviceMasterRepository = serviceMasterRepository;
		this.serviceParametersRepository = parametersRepository;
		this.processRequestRepository = processRequestRepository;
		this.planMappingRepository = planMappingRepository;
		this.prepareRequestReadplatformService = prepareRequestReadplatformService;
		this.officeRepository = officeRepository;
		this.orderReadPlatformService = orderReadPlatformService;
		this.provisioningCommandRepository = provisioningCommandRepository;
		this.ipPoolManagementJpaRepository = ipPoolManagementJpaRepository;
		this.inventoryItemDetailsRepository = inventoryItemDetailsRepository;
		this.processRequestReadplatformService = processRequestReadplatformService;
		this.processRequestWriteplatformService = processRequestWriteplatformService;
		this.provisioningRequestRepository = provisioningRequestRepository;
		this.clientServiceRepository = clientServiceRepository;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.hardwarePlanReadPlatformService = hardwarePlanReadPlatformService;
		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;
		this.clientRepository = clientRepository;
		this.configurationRepository = configurationRepository;
		this.networkElementReadPlatformService = networkElementReadPlatformService;
		this.networkElementRepository = networkElementRepository;
		this.clientServiceReadPlatformService = clientServiceReadPlatformService;

	}

	@Override
	public CommandProcessingResult createProvisioning(JsonCommand command) {

		try {
			String paramDefault = null;
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForProvisioning(command.json());
			final ProvisioningCommand provisioningCommand = ProvisioningCommand.from(command);
			final JsonElement element = fromApiJsonHelper.parse(command.json());
			final JsonArray commandArray = fromApiJsonHelper.extractJsonArrayNamed("commandParameters", element);

			if (commandArray != null) {
				for (JsonElement jsonelement : commandArray) {
					final String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);
					final String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);

					if (fromApiJsonHelper.parameterExists("paramDefault", jsonelement)) {
						paramDefault = fromApiJsonHelper.extractStringNamed("paramDefault", jsonelement);
					}
					final String paramNotes = fromApiJsonHelper.extractStringNamed("paramNotes", jsonelement);
					final Long paramLength = fromApiJsonHelper.extractLongNamed("paramLength", jsonelement);
					ProvisioningCommandParameters data = new ProvisioningCommandParameters(commandParam, paramType,
							paramDefault, paramLength, paramNotes);
					provisioningCommand.addCommandParameters(data);
				}
			}

			this.provisioningCommandRepository.save(provisioningCommand);

			return new CommandProcessingResult(provisioningCommand.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private void handleCodeDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {

	}

	@Override
	public CommandProcessingResult updateProvisioning(final JsonCommand command) {

		try {
			String paramDefault = null;
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForProvisioning(command.json());
			final ProvisioningCommand provisioningCommand = this.provisioningCommandRepository
					.findOne(command.entityId());
			provisioningCommand.getCommandparameters().clear();
			provisioningCommand.updateProvisioning(command);

			final JsonElement element = fromApiJsonHelper.parse(command.json());
			final JsonArray commandArray = fromApiJsonHelper.extractJsonArrayNamed("commandParameters", element);

			if (commandArray != null) {
				for (JsonElement jsonelement : commandArray) {
					String commandParam = fromApiJsonHelper.extractStringNamed("commandParam", jsonelement);
					String paramType = fromApiJsonHelper.extractStringNamed("paramType", jsonelement);

					if (fromApiJsonHelper.parameterExists("paramDefault", jsonelement)) {
						paramDefault = fromApiJsonHelper.extractStringNamed("paramDefault", jsonelement);
					}
					Long paramLength = fromApiJsonHelper.extractLongNamed("paramLength", jsonelement);
					String paramNotes = fromApiJsonHelper.extractStringNamed("paramNotes", jsonelement);
					ProvisioningCommandParameters data = new ProvisioningCommandParameters(commandParam, paramType,
							paramDefault, paramLength, paramNotes);
					provisioningCommand.addCommandParameters(data);
				}
			}

			this.provisioningCommandRepository.save(provisioningCommand);

			return new CommandProcessingResult(provisioningCommand.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Override
	public CommandProcessingResult deleteProvisioningSystem(JsonCommand command) {
		try {
			this.context.authenticatedUser();
			final ProvisioningCommand provisioningCommand = this.provisioningCommandRepository
					.findOne(command.entityId());

			if (provisioningCommand.getIsDeleted() != 'Y') {
				provisioningCommand.setIsDeleted('Y');
			}

			this.provisioningCommandRepository.save(provisioningCommand);

			return new CommandProcessingResult(provisioningCommand.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult createNewProvisioningSystem(final JsonCommand command, final Long entityId) {

		try {

			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForAddProvisioning(command.json());
			final Long orderId = command.longValueOfParameterNamed("orderId");
			final Long clientId = command.longValueOfParameterNamed("clientId");
			final String planName = command.stringValueOfParameterNamed("planName");
			final String macId = command.stringValueOfParameterNamed("macId");
			final String ipType = command.stringValueOfParameterNamed("ipType");
			final String iprange = command.stringValueOfParameterNamed("ipRange");
			final Long subnet = command.longValueOfParameterNamed("subnet");

			final ItemDetails inventoryItemDetails = this.inventoryItemDetailsRepository
					.getInventoryItemDetailBySerialNum(macId);
			final HardwareAssociation hardwareAssociation = this.associationRepository.findOneByOrderId(orderId);

			if (hardwareAssociation == null || inventoryItemDetails == null) {
				throw new PairingNotExistException(orderId);
			}

			final JsonElement element = fromJsonHelper.parse(command.json());
			JSONObject jsonData = this.provisionHelper.provisionAssemblerForm(element, clientId, macId, planName,
					orderId, iprange, ipType, subnet);

			ProcessRequest processRequest = new ProcessRequest(Long.valueOf(0), clientId, orderId,
					ProvisioningApiConstants.PROV_PACKETSPAN, UserActionStatusTypeEnum.ACTIVATION.toString(), 'N', 'N');
			final Order order = this.orderRepository.findOne(orderId);
			List<OrderLine> orderLines = order.getServices();

			for (OrderLine orderLine : orderLines) {

				ServiceMaster service = this.serviceMasterRepository.findOne(orderLine.getProductId());
				jsonData.put(ProvisioningApiConstants.PROV_DATA_SERVICETYPE, service.getServiceType());
				ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(orderLine.getId(),
						orderLine.getProductId(), jsonData.toString(), "Recieved",
						inventoryItemDetails.getProvisioningSerialNumber(), order.getStartDate(), order.getEndDate(),
						null, null, 'N', UserActionStatusTypeEnum.ACTIVATION.toString(), service.getServiceType());

				processRequest.add(processRequestDetails);
			}

			this.processRequestRepository.saveAndFlush(processRequest);
			return new CommandProcessingResult(Long.valueOf(processRequest.getId()), clientId);

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (net.sf.json.JSONException e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateProvisioningDetails(final Long entityId) {

		try {
			this.context.authenticatedUser();
			final ProvisioningRequest provisioningRequest = this.provisioningRequestRepository.findOne(entityId);

			if (provisioningRequest != null) {
				provisioningRequest.setStatus('N');
				int version = provisioningRequest.getVersion();
				provisioningRequest.setVersion(++version);
				this.provisioningRequestRepository.saveAndFlush(provisioningRequest);
			}
			return new CommandProcessingResult(entityId, provisioningRequest.getClientId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Override
	public void updateHardwareDetails(final Long clientId, final String serialNumber, final String oldSerialnumber,
			final String provSerilaNum, final String oldHardware) {

		final Long activeorders = this.orderReadPlatformService.retrieveClientActiveOrderDetails(clientId,
				oldSerialnumber, null);

		if (activeorders != 0) {
			throw new ActivePlansFoundException(oldSerialnumber);
		}
		// Update in Association table if Exist
		final List<HardwareAssociation> hardwareAssociations = this.associationRepository
				.findOneByserialNo(oldSerialnumber);

		if (!hardwareAssociations.isEmpty()) {
			for (HardwareAssociation hardwareAssociation : hardwareAssociations) {
				hardwareAssociation.updateserailNum(serialNumber);
				this.associationRepository.saveAndFlush(hardwareAssociation);
			}
		}
		// Update ProcessRequest
		final Long ProcessReqId = this.processRequestReadplatformService.retrievelatestReqId(clientId, oldHardware);

		if (ProcessReqId != null && !ProcessReqId.equals(Long.valueOf(0))) {
			ProcessRequest processRequest = this.processRequestRepository.findOne(ProcessReqId);
			List<ProcessRequestDetails> processRequestDetails = processRequest.getProcessRequestDetails();
			for (ProcessRequestDetails details : processRequestDetails) {
				details.update(provSerilaNum);
			}
			this.processRequestRepository.saveAndFlush(processRequest);
		}
	}

	@Override
	public CommandProcessingResult postOrderDetailsForProvisioning(final Order order, final String planName,
			final String requestType, final Long prepareId, final String groupname, final String serialNo,
			final Long orderId, final String provisioningSys, Long addonId) {

		
		System.out.println("ProvisioningWritePlatformServiceImpl.postOrderDetailsForProvisioning()");
		// try {
		Long commandProcessId = null;
		String serialNumber = null;
		HardwareAssociation hardwareAssociation = this.associationRepository.findOneByOrderId(order.getId());
		Plan plan = this.planRepository.findOne(order.getPlanId());

		PlanMapping planMapping = this.planMappingRepository.findOneByPlanId(order.getPlanId());

		if (planMapping == null && plan.getProvisionSystem().equalsIgnoreCase("None")) {
			throw new PlanMappingNotExist(plan.getPlanCode());
		}

		if (hardwareAssociation == null && plan.isHardwareReq() == 'Y') {
			throw new PairingNotExistException(order.getId());
		} else if (hardwareAssociation != null) {
			serialNumber = hardwareAssociation.getSerialNo();
		}
		List<ServiceParameters> parameters = this.serviceParametersRepository.findDataByOrderId(orderId);

		if (!parameters.isEmpty()) {
			ItemDetails inventoryItemDetails = null;
			if ("ALLOT".equalsIgnoreCase(hardwareAssociation.getAllocationType())) {
				inventoryItemDetails = this.inventoryItemDetailsRepository
						.getInventoryItemDetailBySerialNum(serialNumber);
				if (inventoryItemDetails == null) {
					throw new PairingNotExistException(order.getId());
				}
			}

			ProcessRequest processRequest = new ProcessRequest(prepareId, order.getClientId(), order.getId(),
					ProvisioningApiConstants.PROV_PACKETSPAN, requestType, 'N', 'N');
			List<OrderLine> orderLines = order.getServices();
			JSONObject jsonData = this.provisionHelper.buildJsonForOrderProvision(order.getClientId(), planName,
					requestType, groupname, serialNo, orderId, inventoryItemDetails.getSerialNumber(), order.getId(),
					parameters);

			for (OrderLine orderLine : orderLines) {

				ServiceMaster service = this.serviceMasterRepository.findOne(orderLine.getProductId());
				jsonData.put(ProvisioningApiConstants.PROV_DATA_SERVICETYPE, service.getServiceType());
				ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(orderLine.getId(),
						orderLine.getProductId(), jsonData.toString(), "Recieved",
						inventoryItemDetails.getProvisioningSerialNumber(), order.getStartDate(), order.getEndDate(),
						null, null, 'N', requestType, service.getServiceType());
				processRequest.add(processRequestDetails);
			}
			this.processRequestRepository.save(processRequest);
			commandProcessId = processRequest.getId();

		} else {
			PrepareRequestData prepareRequestData = new PrepareRequestData(Long.valueOf(0), order.getClientId(),
					orderId, requestType, serialNumber, null, provisioningSys, planName,
					String.valueOf(plan.isHardwareReq()), addonId);

			CommandProcessingResult commandProcessingResult = this.prepareRequestReadplatformService
					.processingClientDetails(prepareRequestData);
			commandProcessId = commandProcessingResult.resourceId();

		}
		return new CommandProcessingResult(commandProcessId);
		// } catch (DataIntegrityViolationException dve) {
		// handleCodeDataIntegrityIssues(null, dve);
		// return new CommandProcessingResult(Long.valueOf(-1));
		// }

	}

	@Transactional
	@Override
	public CommandProcessingResult confirmProvisioningDetails(Long entityId) {

		try {
			this.context.authenticatedUser();

			// final ProcessRequest processRequest =
			// this.processRequestRepository.findOne(entityId);

			final ProvisioningRequest processRequest = this.provisioningRequestRepository.findOne(entityId);
			if (processRequest == null) {
				throw new ProvisioningRequestNotFoundException(entityId);
			}
			processRequest.setStatus('C');
			// processRequest.setNotify();
			// final List<ProcessRequestDetails> details =
			// processRequest.getProcessRequestDetails();
			final List<ProvisioningRequestDetail> details = processRequest.getProvisioningRequestDetail();

			for (ProvisioningRequestDetail processRequestDetails : details) {
				// processRequestDetails.setRecievedMessage("Manually Confirmed");
				processRequestDetails.setResponseMessage("Manually Confirmed");
			}

			this.provisioningRequestRepository.save(processRequest);
			this.processRequestWriteplatformService.notifyProcessingDetails(processRequest, 'Y');

			return new CommandProcessingResult(entityId, processRequest.getClientId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateIpDetails(final Long orderId, final JsonCommand command) {

		IpPoolManagementDetail ipPoolManagement = null;
		Long clientId = null;
		try {
			this.context.authenticatedUser();
			// this.fromApiJsonDeserializer.validateForUpDateIpDetails(command.json());
			clientId = command.longValueOfParameterNamed("clientId");
			final JsonElement element = fromJsonHelper.parse(command.json());

			final String[] removeIpsArray = fromApiJsonHelper.extractArrayNamed("removeIps", element);
			final String[] newIpsArray = fromApiJsonHelper.extractArrayNamed("newIps", element);
			// find duplicate ips in String Array
			List<String> tmpList = Arrays.asList(newIpsArray);
			Set<String> uniqueList = new HashSet<String>(tmpList);
			if (uniqueList.size() < tmpList.size()) {
				throw new IpNotAvailableException(orderId);
			}

			final JSONArray array = new JSONArray();
			List<ServiceParameters> parameters = this.serviceParametersRepository.findDataByOrderId(orderId);

			if (parameters != null) {
				for (ServiceParameters serviceData : parameters) {

					if (ProvisioningApiConstants.PROV_DATA_IPADDRESS.equalsIgnoreCase(serviceData.getParameterName())) {

						for (String newIp : newIpsArray) {
							array.put(newIp);
						}
						serviceData.setParameterValue(array.toString());

						if (removeIpsArray.length >= 1) {
							for (int i = 0; i < removeIpsArray.length; i++) {
								ipPoolManagement = this.ipPoolManagementJpaRepository
										.findByIpAddress(removeIpsArray[i]);
								if (ipPoolManagement == null) {
									throw new IpNotAvailableException(removeIpsArray[i]);
								}
								ipPoolManagement.setStatus('F');
								ipPoolManagement.setClientId(null);
								ipPoolManagement.setSubnet(null);
								this.ipPoolManagementJpaRepository.save(ipPoolManagement);
							}

						}

						if (newIpsArray.length >= 1) {

							for (int i = 0; i < newIpsArray.length; i++) {
								ipPoolManagement = this.ipPoolManagementJpaRepository.findByIpAddress(newIpsArray[i]);
								if (ipPoolManagement == null) {
									throw new IpNotAvailableException(newIpsArray[i]);
								}
								ipPoolManagement.setStatus('A');
								ipPoolManagement.setClientId(clientId);
								// ipPoolManagement.setSubnet(null);
								this.ipPoolManagementJpaRepository.save(ipPoolManagement);
							}
						}
						this.serviceParametersRepository.save(serviceData);
					}
				}
			} else {

			}
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(null, dve);
		}

		return new CommandProcessingResult(orderId, clientId);

	}

	/**
	 * this Method <code> runAdapterCommands </code> used for run System commands.
	 * 
	 * @author ashokreddy
	 */
	@Override
	public String runAdapterCommands(String apiRequestBodyAsJson) {

		try {
			org.json.JSONObject object = new org.json.JSONObject(apiRequestBodyAsJson);
			String command = object.getString("command");
			return ProvisioningWritePlatformServiceImpl.runScript(command);

		} catch (JSONException e) {
			return e.getLocalizedMessage();
		}
	}

	public static String runScript(String command) {

		try {
			System.out.println("Processing the command ...");
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String s, output = "";
			while ((s = br.readLine()) != null) {
				System.out.println(s);
				output = output + s + ",";
			}

			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
				output = output + s + ",";
			}

			System.out.println("Command Processing Completed ...");

			return output;

		} catch (IOException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}

	@Override
	public List<ProvisionAdapter> gettingLogInformation(String apiRequestBodyAsJson) {
		try {
			org.json.JSONObject object = new org.json.JSONObject(apiRequestBodyAsJson);
			Long days = object.getLong("days");
			String dateFormat = object.getString("dateFormat");
			final String startDate = object.getString("startDate");

			DateFormat dateformater = new SimpleDateFormat(dateFormat);
			String todayDate = dateformater.format(DateUtils.getDateOfTenant());

			String logFileLocation = object.getString("logFileLocation");
			String datearray[] = calculateDate(days, startDate, dateFormat);
			List<ProvisionAdapter> logLocation = new ArrayList<ProvisionAdapter>();

			for (String date : datearray) {
				if (todayDate.equalsIgnoreCase(date)) {
					logLocation.add(new ProvisionAdapter(date, logFileLocation));
				} else {
					String filedata = logFileLocation + "." + date;
					File f = new File(filedata);
					if (f.exists() && !f.isDirectory()) {
						logLocation.add(new ProvisionAdapter(date, filedata));
					}
				}

			}

			return logLocation;

		} catch (JSONException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}

	}

	@Override
	public CommandProcessingResult postDetailsForProvisioning(Long clientId, Long resourceId, String requestType,
			String provisioningSystem, String hardwareId) {

		Long defaultValue = Long.valueOf(0);
		JSONObject jsonObject = new JSONObject();
		if (requestType.equalsIgnoreCase(ProvisioningApiConstants.REQUEST_CREATE_AGENT)) {
			Office office = this.officeRepository.findOne(resourceId);
			if (office != null) {
				OfficeAdditionalInfo additionalInfo = office.getOfficeAdditionalInfo();
				if (additionalInfo != null) {
					jsonObject.put("agentName", office.getName());
					jsonObject.put("agentId", office.getId());
					jsonObject.put("agentDescription", additionalInfo.getContactName());
				}
				resourceId = Long.valueOf(0L);
			}
		}

		ProcessRequest processRequest = new ProcessRequest(defaultValue, clientId, resourceId, provisioningSystem,
				requestType, 'N', 'N');
		ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(defaultValue, defaultValue,
				jsonObject.toString(), "Recieved", hardwareId, DateUtils.getDateOfTenant(), DateUtils.getDateOfTenant(),
				null, null, 'N', requestType, null);
		processRequest.add(processRequestDetails);
		this.processRequestRepository.save(processRequest);
		return new CommandProcessingResult(processRequest.getId());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandProcessingResult createProvisioningRequest(List<Order> orders, JsonCommand command,
			boolean checkValidate) {
		String provisioningSystemId = null;
		JSONArray deviceDetails = null;
		try {
			String requestType = command.stringValueOfParameterNamed("requestType");
			Long clientServiceId = command.longValueOfParameterNamed("clientServiceId");
			Long oldOrderId = command.longValueOfParameterNamed("oldOrderId");
			String type = command.stringValueOfParameterNamed("type");
			String sub_action = command.stringValueOfParameterName("sub_action");
			ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);

			Map<String, Object> hardwareAndDeviceDetails = this.retriveHardwareMappingAndSalesDetails(clientService);
			List<OneTimeSaleData> deviceDatas = (List<OneTimeSaleData>) hardwareAndDeviceDetails.get("deviceDetails");
			if (!deviceDatas.isEmpty()) {
				if (checkValidate) {
					this.fromApiJsonDeserializer.validateForHardwareAndDevice(hardwareAndDeviceDetails);
				}
				deviceDetails = this.retrivedeviceDetailsforProvisioningRequest(
						(List<HardwarePlanData>) hardwareAndDeviceDetails.get("hardwareMappingDetails"),
						(List<OneTimeSaleData>) hardwareAndDeviceDetails.get("deviceDetails"), command);
			}

			Map<String, Object> provAndSpDetails = this.getProvisioningRequestIdAndSP(clientService, null);
			provisioningSystemId = provAndSpDetails.get("provisioningSystemId").toString();
			JSONArray extraJsonArray = this.PreRequiresOfProvisioningFunctionality(requestType, provisioningSystemId,
					type);

			ProvisioningRequest provisioningRequest = new ProvisioningRequest(clientService.getClientId(),
					clientService.getId(), requestType, provisioningSystemId, 'N',
					DateUtils.getLocalDateOfTenant().toDate(), null, PriorityEnum.fromName(requestType), 1);

			provisioningRequest.addDetails(
					new ProvisioningRequestDetail(this.retriveRequestMessage(orders, clientService, provAndSpDetails,
							deviceDetails, oldOrderId, extraJsonArray, requestType, command), null, null, "1"));

			this.provisioningRequestRepository.saveAndFlush(provisioningRequest);

			return new CommandProcessingResult(provisioningRequest.getId());
		} catch (DataIntegrityViolationException dve) {
			this.handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	@Override
	public CommandProcessingResult createProvisioningRequestForCommandCenter(JsonCommand command) {
		try {
			Long clientId = command.longValueOfParameterNamed("clientId");
			Long clientServiceId = command.longValueOfParameterNamed("clientServiceId");
			String provisioningSystem = command.stringValueOfParameterNamed("provisioningSystem");
			String requestType = command.stringValueOfParameterNamed("requestType");
			JsonArray requestMessage = command.arrayOfParameterNamed("requestMessage");
			JsonArray groupInfo = null;

			JSONObject requestMessageObject = new JSONObject();
			requestMessageObject.put("request_type", requestType);
			requestMessageObject.put("type", command.stringValueOfParameterNamed("type"));
			requestMessageObject.put("Action", "1");
			if (clientServiceId != null) {
				Map<String, Object> hardwareAndDeviceDetails = this
						.retriveHardwareMappingAndSalesDetails(this.clientServiceRepository.findOne(clientServiceId));
				JSONArray deviceDetails = this.retrivedeviceDetailsforProvisioningRequest(
						(List<HardwarePlanData>) hardwareAndDeviceDetails.get("hardwareMappingDetails"),
						(List<OneTimeSaleData>) hardwareAndDeviceDetails.get("deviceDetails"), command);
				requestMessageObject.put("newDeviceInfo", String.valueOf(deviceDetails));
				if (requestType.equals("RETRACK") || requestType.equals("HARDRETRACK")) {
					JSONArray orderDetails = this.orderInfoJsonPreparation(
							this.orderRepository.findOrdersOnlyByClientService(clientServiceId),
							this.clientServiceRepository.findOne(clientServiceId), provisioningSystem);
					requestMessageObject.put("newOrderList", String.valueOf(orderDetails));
					requestMessageObject.put("oldOrderList", String.valueOf(orderDetails));
					requestMessageObject.put("clientInfo", command.stringValueOfParameterName("clientInfo"));

					List<ClientService> clientServices = clientServiceRepository
							.findClientServicewithClientIds(clientId);

					JSONArray serviceInfo = new JSONArray();

					JSONArray task = new JSONArray();

					for (ClientService clientService : clientServices) {
						JSONObject serviceDetails = new JSONObject();
						serviceDetails.put("serviceId", clientService.getServiceId());
						serviceDetails.put("clientServiceId", clientService.getId());
						serviceDetails.put("networkSubscriberId", clientService.getNetworkSubscriberId());
						serviceDetails.put("Network_node", provisioningSystem);
						serviceInfo.put(serviceDetails);
					}

					requestMessageObject.put("serviceInfo", String.valueOf(serviceInfo));

					JSONObject taskList = new JSONObject();
					taskList.put("task_id", 1);
					taskList.put("task_name", requestType);
					task.put(taskList);

					requestMessageObject.put("serviceInfo", String.valueOf(serviceInfo));
					requestMessageObject.put("TaskList", String.valueOf(task));

				}
			}
			int i = 0;
			for (JsonElement requestMessageJsonElement : requestMessage) {
				if (this.fromApiJsonHelper.parameterExists("groupInfo", requestMessageJsonElement)) {
					JsonArray groupInfoArray = this.fromApiJsonHelper.extractJsonArrayNamed("groupInfo",
							requestMessageJsonElement);
					requestMessageObject.put("groupInfo", groupInfoArray.toString());
					i++;
				}

				if (this.fromApiJsonHelper.parameterExists("paramDetailsInfo", requestMessageJsonElement)) {
					JsonArray paramsDetailInfoArray = this.fromApiJsonHelper.extractJsonArrayNamed("paramDetailsInfo",
							requestMessageJsonElement);
					requestMessageObject.put("paramsDetailsInfo", paramsDetailInfoArray.toString());
					i++;
				}

				if (i == 0) {
					requestMessageObject.put("paramsDetailsInfo", requestMessage.toString());
					i++;
				}
			}

			ProvisioningRequest provisioningRequest = new ProvisioningRequest(clientId, clientServiceId, requestType,
					provisioningSystem, 'N', new Date(), new Date(), PriorityEnum.fromName(requestType), 1);

			provisioningRequest
					.addDetails(new ProvisioningRequestDetail(String.valueOf(requestMessageObject), null, null, "1"));
			this.provisioningRequestRepository.saveAndFlush(provisioningRequest);

			return new CommandProcessingResult(provisioningRequest.getId());
		} catch (DataIntegrityViolationException dve) {
			this.handleCodeDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private String[] calculateDate(final Long days, final String startDate, final String dateFormat1)
			throws ParseException {

		String datearray[] = new String[days.intValue()];
		DateFormat dateFormat = new SimpleDateFormat(dateFormat1);
		Date date = dateFormat.parse(startDate);

		for (int day = 0; day < days; day++) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, -day);
			Date todate1 = cal.getTime();
			String fromdate = dateFormat.format(todate1);
			datearray[day] = fromdate;
		}

		return datearray;
	}

	private JSONArray PreRequiresOfProvisioningFunctionality(String requestType, String provisioningSystemId,
			String type) {
		// CodeValueData provisioningData =
		// this.codeValueReadPlatformService.retrieveCodeValue(Long.valueOf(provisioningSystemId));
		NetworkElementData provisioningData = this.networkElementReadPlatformService
				.retrieveNetworkElement(Long.valueOf(provisioningSystemId));
		JSONArray extraJsonArray = new JSONArray();
		switch (provisioningData.getsystemcode()) {
		case "ABV":
			switch (requestType) {
			case ProvisioningApiConstants.REQUEST_ACTIVATION:
				extraJsonArray.put(this.extraJsonObject(1, "ADD_CARD"));
				extraJsonArray.put(this.extraJsonObject(2, "BIND_STB"));
				extraJsonArray.put(this.extraJsonObject(3, "AREA_MODIFY"));
				extraJsonArray.put(this.extraJsonObject(4, "ADD_ENTITLEMENT"));
				break;

			case ProvisioningApiConstants.REQUEST_SUSPENTATION:
				extraJsonArray.put(this.extraJsonObject(1, "SUSPEND"));
				break;

			case ProvisioningApiConstants.REQUEST_REACTIVATION:
				extraJsonArray.put(this.extraJsonObject(1, "RECONNECTION"));
				break;
			case ProvisioningApiConstants.REQUEST_TERMINATION:
				extraJsonArray.put(this.extraJsonObject(1, "CANCEL_ENTITLEMENT"));
				extraJsonArray.put(this.extraJsonObject(2, "UNBIND_STB"));
				extraJsonArray.put(this.extraJsonObject(3, "DELETE_SC"));
				break;
			case ProvisioningApiConstants.REQUEST_CHANGE_PLAN:
				extraJsonArray.put(this.extraJsonObject(1, "CANCEL_ENTITLEMENT"));
				extraJsonArray.put(this.extraJsonObject(2, "ADD_ENTITLEMENT"));
				break;
			// cancallation
			case ProvisioningApiConstants.REQUEST_DISCONNECTION:
				extraJsonArray.put(this.extraJsonObject(1, "CANCEL_ENTITLEMENT"));
				break;

			case ProvisioningApiConstants.REQUEST_ADD_PLAN:
				extraJsonArray.put(this.extraJsonObject(1, "ADD_ENTITLEMENT"));
				break;

			case ProvisioningApiConstants.REQUEST_RETRACK:
				extraJsonArray.put(this.extraJsonObject(1, "REAUTH_ALL"));
				break;

			case ProvisioningApiConstants.REQUEST_SWAPDEVICE:
				extraJsonArray.put(this.extraJsonObject(1, "CANCEL_ENTITLEMENT"));
				extraJsonArray.put(this.extraJsonObject(2, "SMARTCARD_UNBIND"));
				extraJsonArray.put(this.extraJsonObject(3, "SMARTCARD_DELETE"));
				extraJsonArray.put(this.extraJsonObject(4, "SMARTCARD_ADD"));
				extraJsonArray.put(this.extraJsonObject(5, "SMARTCARD_BIND"));
				extraJsonArray.put(this.extraJsonObject(6, "AREA_MODIFY"));
				extraJsonArray.put(this.extraJsonObject(7, "ADD_ENTITLEMENT"));
				break;
			case ProvisioningApiConstants.REQUEST_SWAPSTB:
				extraJsonArray.put(this.extraJsonObject(1, "CANCEL_ENTITLEMENT"));
				extraJsonArray.put(this.extraJsonObject(2, "SMARTCARD_UNBIND"));
				extraJsonArray.put(this.extraJsonObject(3, "SMARTCARD_BIND"));
				extraJsonArray.put(this.extraJsonObject(4, "AREA_MODIFY"));
				extraJsonArray.put(this.extraJsonObject(5, "ADD_ENTITLEMENT"));
				break;

			case ProvisioningApiConstants.REQUEST_SWAPVC:
				extraJsonArray.put(this.extraJsonObject(1, "SWAP_SC"));
				break;

			case ProvisioningApiConstants.REQUEST_RENEWAL_AE:
				extraJsonArray.put(this.extraJsonObject(1, "ADD_ENTITLEMENT"));
				break;

			case ProvisioningApiConstants.REQUEST_RENEWAL_BE:
				extraJsonArray.put(this.extraJsonObject(1, "ADD_ENTITLEMENT"));
				break;
			default:
				break;
			}
			break;
		case "NSTV":
			switch (requestType) {
			case ProvisioningApiConstants.REQUEST_ACTIVATION:
				extraJsonArray.put(this.extraJsonObject(1, "OPEN_ACCOCUNT"));
				extraJsonArray.put(this.extraJsonObject(2, "PAIRING"));
				extraJsonArray.put(this.extraJsonObject(3, "ADD_ENTITLEMENT"));
				break;
			default:
				break;
			}
			break;
		default:
			extraJsonArray.put(this.extraJsonObject(1, requestType));
			break;
		// throw new
		// PlatformDataIntegrityException("invalid.provisioningSystem","invalid
		// provisioning System");

		}
		return extraJsonArray;
	}

	private JSONObject extraJsonObject(int id, String taskName) {
		JSONObject extraJsonObject = new JSONObject();
		extraJsonObject.put("task_id", id);
		extraJsonObject.put("task_name", taskName);
		return extraJsonObject;

	}

	@Override
	public Map<String, Object> getProvisioningRequestIdAndSP(ClientService clientService, Long clientServiceId) {
		if (clientService == null) {
			clientService = this.clientServiceRepository.findOne(clientServiceId);
		}
		String parameterId = null;
		String provisioningSystemId = null;
		Collection<MCodeData> mcodeDatas = this.mCodeReadPlatformService.getCodeValue("SP");
		for (MCodeData mcodeData : mcodeDatas) {
			if (mcodeData.getmCodeValue().equalsIgnoreCase("Network_node")) {
				parameterId = mcodeData.getId().toString();
				break;
			}
		}
		final List<ServiceParameters> serviceParameters = clientService.getServiceParameters();
		for (ServiceParameters serviceParameter : serviceParameters) {
			if (parameterId.equalsIgnoreCase(serviceParameter.getParameterName())) {
				provisioningSystemId = serviceParameter.getParameterValue();
				break;
			}
		}
		Map<String, Object> returnValue = new HashMap<String, Object>();
		returnValue.put("provisioningSystemId", provisioningSystemId);
		returnValue.put("spMcode", mcodeDatas);
		return returnValue;
	}

	private Map<String, Object> retriveHardwareMappingAndSalesDetails(ClientService clientService) {
		Map<String, Object> hardwareAndSalesDetails = new HashMap<String, Object>();
		List<OneTimeSaleData> neededSlesDatas = new ArrayList<OneTimeSaleData>();
		List<HardwarePlanData> requiredhardwarePlanDatas = new ArrayList<HardwarePlanData>();
		List<HardwarePlanData> hardwarePlanDatas = this.hardwarePlanReadPlatformService
				.retrieveAllHardwareMappings(null);
		for (HardwarePlanData hardwarePlanData : hardwarePlanDatas) {
			for (ServiceParameters serviceParameter : clientService.getServiceParameters()) {
				if (serviceParameter.getParameterValue()
						.equalsIgnoreCase(hardwarePlanData.getProvisioningId().toString()))
					requiredhardwarePlanDatas.add(hardwarePlanData);
			}
		}

		// List<OneTimeSaleData> salesDatas =
		// this.oneTimeSaleReadPlatformService.retrieveClientOneTimeSalesData(clientService.getClientId());
		List<OneTimeSaleData> salesDatas = this.oneTimeSaleReadPlatformService
				.retrieveClientOneTimeSalesDataForActivation(clientService.getClientId());
		List<OneTimeSaleData> allocatedsalesDatas = new ArrayList<OneTimeSaleData>();
		for (OneTimeSaleData salesData : salesDatas) {
			if (!salesData.getHardwareAllocated().equalsIgnoreCase("UNALLOCATED")) {
				allocatedsalesDatas.add(salesData);
			}
		}
		for (OneTimeSaleData allocatedsalesData : allocatedsalesDatas) {
			if (clientService.getId().toString().equalsIgnoreCase(allocatedsalesData.getClientServiceId().toString())) {
				neededSlesDatas.add(allocatedsalesData);
			}
		}
		hardwareAndSalesDetails.put("hardwareMappingDetails", requiredhardwarePlanDatas);
		hardwareAndSalesDetails.put("deviceDetails", neededSlesDatas);
		return hardwareAndSalesDetails;
	}

	private JSONArray retrivedeviceDetailsforProvisioningRequest(List<HardwarePlanData> requiredhardwarePlanDatas,
			List<OneTimeSaleData> salesDatas, JsonCommand command) {
		JSONArray deviceDetails = new JSONArray();
		JSONObject object = null;
		for (HardwarePlanData hardwarePlanData : requiredhardwarePlanDatas) {
			for (OneTimeSaleData salesData : salesDatas) {
				if (hardwarePlanData.getItemClass().toString().equalsIgnoreCase(salesData.getItemClass().toString())) {
					object = new JSONObject();
					object.put("ItemType", salesData.getItemClassName());
					object.put("SerialNo", salesData.getSerialNo());
					object.put("ProvisioningSerialNo", salesData.getProvserialnumber());
					deviceDetails.put(object);
					break;
				}
			}
		}
		deviceDetails = addingPairedDevice(salesDatas, deviceDetails);
		return deviceDetails;

	}

	private JSONArray addingPairedDevice(List<OneTimeSaleData> salesDatas, JSONArray deviceDetails) {
		Configuration config = this.configurationRepository
				.findOneByName(ConfigurationConstants.PAIRED_DEVICE_IN_PROV_REQ);
		if (config != null && config.isEnabled())
			for (OneTimeSaleData salesData : salesDatas) {
				if (null != salesData.getPairedItemCode()) {
					if (!isPairedDeviceExistInDeviceDetails(salesData.getPairedItemCode(), deviceDetails)) {
						for (OneTimeSaleData sale : salesDatas) {
							if (salesData.getPairedItemCode().equalsIgnoreCase(sale.getItemCode())) {
								JSONObject object = new JSONObject();
								object.put("ItemType", sale.getItemClassName());
								object.put("SerialNo", sale.getSerialNo());
								object.put("ProvisioningSerialNo", sale.getProvserialnumber());
								deviceDetails.put(object);
								break;
							}
						}
						break;
					}

				}
			}
		return deviceDetails;
	}

	private boolean isPairedDeviceExistInDeviceDetails(String pairedItemCode, JSONArray deviceDetails) {
		boolean returnValue = false;
		try {
			for (int i = 0; i < deviceDetails.length(); i++) {
				if (pairedItemCode.equalsIgnoreCase(deviceDetails.getJSONObject(i).getString("ItemType"))) {
					returnValue = true;
					break;
				}
			}
		} catch (Exception e) {

		}
		return returnValue;
	}

	private String retriveRequestMessage(final List<Order> orders, ClientService clientService,
			Map<String, Object> provAndSpdetails, JSONArray deviceDetails, Long oldOrderId, JSONArray extraJsonArray,
			String requestType, JsonCommand command) {
		String provisioningSystemId = provAndSpdetails.get("provisioningSystemId").toString();
		Collection<MCodeData> mcodeDatas = (Collection<MCodeData>) provAndSpdetails.get("spMcode");
		try {
			JSONObject object = new JSONObject();
			object.put("request_type", requestType);
			object.put("clientInfo", String.valueOf(this.clientInfoJsonPreparation(clientService.getClientId())));
			object.put("serviceInfo",
					String.valueOf(this.serviceInfoJsonPreparation(clientService, provisioningSystemId, mcodeDatas)));
			object.put("newDeviceInfo", String.valueOf(deviceDetails));
			if (extraJsonArray.length() > 0) {
				object.put("TaskList", String.valueOf(extraJsonArray));
			}
			switch (requestType) {
			case ProvisioningApiConstants.REQUEST_ACTIVATION:
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;
			case ProvisioningApiConstants.REQUEST_ADD_PLAN:
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;
			case ProvisioningApiConstants.REQUEST_DISCONNECTION:
				object.put("oldOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;
			case ProvisioningApiConstants.REQUEST_CHANGE_PLAN:
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				List<Order> oldOrders = new ArrayList<Order>();
				oldOrders.add(this.orderRepository.findOne(oldOrderId));
				object.put("oldOrderList",
						String.valueOf(this.orderInfoJsonPreparation(oldOrders, clientService, provisioningSystemId)));
				break;

			case ProvisioningApiConstants.REQUEST_SUSPENTATION:
				object.put("oldOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;

			case ProvisioningApiConstants.REQUEST_REACTIVATION:
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;
			case ProvisioningApiConstants.REQUEST_TERMINATION:
				object.put("oldDeviceInfo", String.valueOf(deviceDetails));
				object.put("oldOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;
			case ProvisioningApiConstants.REQUEST_SWAPDEVICE:
				object.put("oldOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				object.put("oldDeviceInfo", String.valueOf(this.deviceInfoJsonPreparation(command)));
				break;
			case ProvisioningApiConstants.REQUEST_SWAPSTB:
				object.put("oldOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				object.put("oldDeviceInfo", String.valueOf(this.deviceInfoJsonPreparation(command)));
				break;
			case ProvisioningApiConstants.REQUEST_SWAPVC:
				object.put("oldOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				object.put("oldDeviceInfo", String.valueOf(this.deviceInfoJsonPreparation(command)));
				break;
			case ProvisioningApiConstants.REQUEST_RENEWAL_AE:
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;
			case ProvisioningApiConstants.REQUEST_RENEWAL_BE:
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;
			case ProvisioningApiConstants.REQUEST_RETRACK:
				object.put("oldOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;
			case ProvisioningApiConstants.REQUEST_RESETPIN:
				object.put("oldOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				break;
			case ProvisioningApiConstants.REQUEST_MODIFY_SERVICE:
				object.put("sub_action", command.stringValueOfParameterName("sub_action"));
				object.put("newOrderList",
						String.valueOf(this.orderInfoJsonPreparation(orders, clientService, provisioningSystemId)));
				/*
				 * object.put("passwordchange",command.stringValueOfParameterName(
				 * "passwordchange"));
				 */

				break;
			default:
				throw new PlatformDataIntegrityException("invalid.request", "invalid Request");
			}

			return object.toString();
		} catch (Exception e) {
			throw new PlatformDataIntegrityException("parse.exception.occur",
					"Parse Exception occured" + e.getMessage());
		}

	}

	private JSONArray deviceInfoJsonPreparation(JsonCommand command) {

		JSONArray array = new JSONArray();
		JSONObject object = new JSONObject();
		object.put("ItemType", command.stringValueOfParameterNamed("type"));
		object.put("SerialNo", command.stringValueOfParameterNamed("oldSerialNo"));
		object.put("ProvisioningSerialNo", command.stringValueOfParameterNamed("oldPSerialNo"));
		array.put(object);
		if (null != command.stringValueOfParameterName("pType")) {
			object = new JSONObject();
			object.put("ItemType", command.stringValueOfParameterNamed("pType"));
			object.put("SerialNo", command.stringValueOfParameterNamed("pOldSerialNo"));
			object.put("ProvisioningSerialNo", command.stringValueOfParameterNamed("pOldPSerialNo"));
			array.put(object);
		}
		return array;
	}

	private String toDayMonthYear(Date existDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(existDate);
		int month = calendar.get(Calendar.MONTH) + 1;
		return calendar.get(Calendar.DAY_OF_MONTH) + "/" + month + "/" + calendar.get(Calendar.YEAR);
	}

	private JSONArray clientInfoJsonPreparation(Long clientId) {
		Client client = this.clientRepository.findOne(clientId);
		JSONArray clientInfo = new JSONArray();
		JSONObject clientInfoObject = new JSONObject();
		clientInfoObject.put("clientId", client.getId());
		clientInfoObject.put("accountNo", client.getAccountNo());
		clientInfoObject.put("officeId", client.getOffice().getId());
		clientInfoObject.put("email", client.getEmail());
		clientInfoObject.put("selfcareUsername", client.getLogin());
		clientInfoObject.put("selfcarePassword", client.getPassword());
		clientInfoObject.put("displayName", client.getDisplayName());
		clientInfoObject.put("firstName", client.getFirstname());
		clientInfoObject.put("lastName", client.getLastname());
		clientInfoObject.put("mobile", client.getPhone());
		clientInfoObject.put("login", client.getLogin());
		clientInfoObject.put("password", client.getPassword());
		clientInfo.put(clientInfoObject);
		return clientInfo;

	}

	private JSONArray serviceInfoJsonPreparation(ClientService clientService, String provisioningSystemId,
			Collection<MCodeData> mcodeDatas) {
		JSONArray serviceInfo = new JSONArray();
		JSONObject serviceInfoObject = new JSONObject();
		serviceInfoObject.put("serviceId", clientService.getServiceId());
		serviceInfoObject.put("clientServiceId", clientService.getId());
		if (clientService.getNetworkSubscriberId() == null) {
			serviceInfoObject.put("networkSubscriberId", " ");
		} else {
			serviceInfoObject.put("networkSubscriberId", clientService.getNetworkSubscriberId());
		}
		for (ServiceParameters serviceParameter : clientService.getServiceParameters()) {
			serviceInfoObject.put(this.retriveKey(serviceParameter.getParameterName(), mcodeDatas),
					serviceParameter.getParameterValue());
		}

		serviceInfo.put(serviceInfoObject);

		return serviceInfo;
	}

	private String retriveKey(String parameterName, Collection<MCodeData> mcodeDatas) {
		String returnValue = null;
		for (MCodeData mcodeData : mcodeDatas) {
			if (parameterName.equalsIgnoreCase(mcodeData.getId().toString())) {
				returnValue = mcodeData.getmCodeValue();
				break;
			}
		}
		return returnValue;
	}

	private JSONArray orderInfoJsonPreparation(List<Order> orders, ClientService clientService,
			String provisioningSystemId) {
		ServiceMaster serviceMaster = this.serviceMasterRepository.findOne(clientService.getServiceId());
		boolean isTv = false;
		if (serviceMaster.getServiceType().equalsIgnoreCase("CATV")) {
			isTv = true;
		}
		JSONArray orderInfo = new JSONArray();
		JSONObject object = null;
		for (Order order : orders) {
			object = new JSONObject();
			object.put("orderId", order.getId());
			object.put("planId", order.getPlanId());
			object.put("orderStartDate", this.toDayMonthYear(order.getStartDate()));
			if (null != order.getEndDate()) {
				object.put("orderEndDate", this.toDayMonthYear(order.getEndDate()));
			}
			object.put("products",
					String.valueOf(this.retrivePlanProductCodes(order.getPlanId(), provisioningSystemId, isTv)));
			orderInfo.put(object);
		}
		return orderInfo;
	}

	private JSONArray retrivePlanProductCodes(Long planId, String provisioningSystemId, boolean isTv) {
		JSONArray array = new JSONArray();
		JSONObject object = null;
		// CodeValueData codeValueData =
		// this.codeValueReadPlatformService.retrieveCodeValue(Long.valueOf(provisioningSystemId));
		NetworkElement networkElement = this.networkElementRepository.findOne(Long.valueOf(provisioningSystemId));
		List<ServiceDetailData> serviceDetailDatas = this.serviceMasterReadPlatformService
				.retriveServiceDetailsOfPlan(planId);
		for (ServiceDetailData serviceDetailData : serviceDetailDatas) {
			if (isTv) {
				if (networkElement.getsystemcode().equalsIgnoreCase(serviceDetailData.getCodeParamName())) {
					object = new JSONObject();
					object.put("neProductId", serviceDetailData.getParamValue());
					object.put("productName", serviceDetailData.getServiceCode());
					object.put("paramName", serviceDetailData.getCodeParamName());
					array.put(object);
				}
			} else {
				object = new JSONObject();
				object.put("neProductId", serviceDetailData.getParamValue());
				object.put("productName", serviceDetailData.getServiceCode());
				object.put("paramName", serviceDetailData.getCodeParamName());
				array.put(object);
			}
		}
		return array;
	}

	private JSONArray newProcedureorderInfoJsonPreparation(List<Order> orders, ClientService clientService,
			String provisioningSystemId) {
		CodeValueData codeValueData = this.codeValueReadPlatformService
				.retrieveCodeValue(Long.valueOf(provisioningSystemId));
		JSONArray array = new JSONArray();
		JSONObject object = null;
		for (Order order : orders) {
			List<ServiceDetailData> serviceDetailDatas = this.serviceMasterReadPlatformService
					.retriveServiceDetailsOfPlan(order.getPlanId());
			for (ServiceDetailData serviceDetailData : serviceDetailDatas) {
				if (codeValueData.getName().equalsIgnoreCase(serviceDetailData.getCodeParamName())) {
					object = new JSONObject();
					object.put("planId", order.getPlanId());
					object.put("planId", order.getId());
					object.put("orderStartDate", this.toDayMonthYear(order.getStartDate()));
					if (null != order.getEndDate()) {
						object.put("orderEndDate", this.toDayMonthYear(order.getEndDate()));
					}
					object.put("neProductId", serviceDetailData.getParamValue());
					object.put("productName", "");
					array.put(object);
				}
			}
		}
		return array;
	}

	/* edit send message method */

	@Override
	public CommandProcessingResult updateProvisioningRequestDetailsData(JsonCommand command, Long entityId) {

		try {

			this.fromApiJsonDeserializer.validateForUpdateProvisioning(command.json());
			ProvisioningRequest provisioningRequest = this.retrieveProvisioningRequest(entityId);
			final Map<String, Object> changes = provisioningRequest.update(command);
			Date date = null;
			if (!changes.isEmpty()) {
				date = new Date();
				provisioningRequest.setStatus('N');
				provisioningRequest.setVersion((provisioningRequest.getVersion() + 1));
				provisioningRequest.setEndDate(date);
			}
			if (date == null) {
				provisioningRequest.setEndDate(date);
			}
			this.provisioningRequestRepository.save(provisioningRequest);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(entityId)
					.with(changes).build();
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private ProvisioningRequest retrieveProvisioningRequest(final Long provisioningrequestId) {
		final ProvisioningRequest details = this.provisioningRequestRepository.findOne(provisioningrequestId);
		if (details == null) {
			throw new ProvisioningRequestNotFoundException(provisioningrequestId);
		}
		return details;
	}

}
