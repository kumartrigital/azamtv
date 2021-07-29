/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.clientservice.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.clientservice.serialization.ClientServiceDataValidator;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.order.service.OrderWorkflowWritePlaftormService;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.service.domain.ServiceDetails;
import org.mifosplatform.provisioning.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParametersRepository;
import org.mifosplatform.provisioning.provisioning.service.ProvisioningWritePlatformService;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ClientServiceWritePlatformServiceJpaRepositoryImpl implements ClientServiceWriteplatformService {

	private final static Logger logger = LoggerFactory
			.getLogger(ClientServiceWritePlatformServiceJpaRepositoryImpl.class);

	private final PlatformSecurityContext context;
	private final ClientServiceRepository clientServiceRepository;
	private final ClientServiceDataValidator fromApiJsonDeserializer;
	private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	private final FromJsonHelper fromApiJsonHelper;
	private final OrderRepository orderRepository;
	private final ProvisioningWritePlatformService provisioningWritePlatformService;
	private final OrderWritePlatformService orderWritePlatformService;
	private final CrmServices crmServices;
	private final ServiceParametersRepository serviceParametersRepository;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	private final ActiondetailsWritePlatformService actiondetailsWritePlatformService;
	private final OrderWorkflowWritePlaftormService orderWorkflowWritePlaftormService;
	private final FromJsonHelper fromJsonHelper;


	@Autowired
	public ClientServiceWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
			final ClientServiceRepository clientServiceRepository,
			final ClientServiceDataValidator fromApiJsonDeserializer,
			final PrepareRequestReadplatformService prepareRequestReadplatformService,
			final ClientServiceReadPlatformService clientServiceReadPlatformService,
			final FromJsonHelper fromApiJsonHelper, final OrderRepository orderRepository,
			final ProvisioningWritePlatformService provisioningWritePlatformService,
			final OrderWritePlatformService orderWritePlatformService, final CrmServices crmServices,
			final ServiceParametersRepository serviceParametersRepository,
			@Lazy final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			@Lazy final ActiondetailsWritePlatformService actiondetailsWritePlatformService,
			@Lazy final OrderWorkflowWritePlaftormService orderWorkflowWritePlaftormService,
			final FromJsonHelper fromJsonHelper) {

		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.clientServiceRepository = clientServiceRepository;
		this.clientServiceReadPlatformService = clientServiceReadPlatformService;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.orderRepository = orderRepository;
		this.provisioningWritePlatformService = provisioningWritePlatformService;
		this.orderWritePlatformService = orderWritePlatformService;
		this.crmServices = crmServices;
		this.serviceParametersRepository = serviceParametersRepository;
		this.actionDetailsReadPlatformService = actionDetailsReadPlatformService;
		this.actiondetailsWritePlatformService = actiondetailsWritePlatformService;
		this.orderWorkflowWritePlaftormService = orderWorkflowWritePlaftormService;
		this.fromJsonHelper = fromJsonHelper;
	}

	@Transactional
	@Override
	public CommandProcessingResult createClientService(final JsonCommand command) {

		
		try {
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());

			final Long clientId = command.longValueOfParameterNamed("clientId");
			final Long serviceId = command.longValueOfParameterNamed("serviceId");

			
			final boolean isAutoRenew = command.booleanPrimitiveValueOfParameterNamed("autoRenew");
			char autoRenew = isAutoRenew ? 'Y' : 'N';	
			final String status = "NEW";
			final String clientServicePoid = command.stringValueOfParameterName("clientservicePoId");
			ClientService clientService = ClientService.createNew_ClientServicePoid(clientId, serviceId, status,
					clientServicePoid);
			final JsonArray detailsArray = command.arrayOfParameterNamed("clientServiceDetails").getAsJsonArray();
			clientService = this.detailFun(detailsArray, clientService, clientId);
			clientService.setAutoRenewal(autoRenew);
			this.clientServiceRepository.saveAndFlush(clientService);

			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withOfficeId(null)
					.withClientId(clientId).withResourceIdAsString(clientService.getId().toString()).withGroupId(null)
					.withEntityId(clientService.getId()).build();
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}


	@Override
	public CommandProcessingResult createClientServiceActivation(Long clientServiceId, JsonCommand command) {
		try {
			ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);

			Long clientId = command.longValueOfParameterNamed("clientId");
			String output = "false";
			if ("New".equalsIgnoreCase(clientService.getStatus())) {
				final List<ActionDetaislData> actionDetailsDatas = this.actionDetailsReadPlatformService
						.retrieveActionDetails(EventActionConstants.EVENT_CREATE_ORDER_WORKFLOW);
				if (!actionDetailsDatas.isEmpty()) {
					output = this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas, clientId,
							clientServiceId.toString(), null);
				}
				if (output.equals("false")) {
					final List<ActionDetaislData> actionDatas = this.actionDetailsReadPlatformService
							.retrieveActionDetails(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM);
					if (!actionDatas.isEmpty()) {
						this.actiondetailsWritePlatformService.AddNewActions(actionDatas, clientId,
								clientServiceId.toString(), "Operation");
					}
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("clientServiceId", clientServiceId);
					jsonObject.put("status", "DRAFT");
					jsonObject.put("assignedTo", "0");
					jsonObject.put("description", "Draft is Completed");
					final JsonElement orderWorkFlowElement = fromJsonHelper.parse(jsonObject.toString());
					command = new JsonCommand(null, orderWorkFlowElement.toString(), orderWorkFlowElement,
							fromJsonHelper, null, null, null, null, null, null, null, null, null, null, null, null);
					this.orderWorkflowWritePlaftormService.orderWorkflow(command);
					clientService.setStatus("DRAFT");
					this.clientServiceRepository.save(clientService);
				}
			}
			return new CommandProcessingResultBuilder().withEntityId(clientServiceId).withClientId(clientId).build();
		} catch (DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		} catch (JSONException e) {
			e.printStackTrace();
			return CommandProcessingResult.empty();
		}
	}

	@Override
	public void createProvisioningForModifyService(Long clientId, Long clientServiceId, String password, String sub_action) {
		List<Order> orders = new ArrayList<Order>();
		List<Order> provOrders = new ArrayList<Order>();
		if (this.isThisClientServiceHasProvisioning(clientServiceId)) {
			orders = this.orderRepository.findOrdersByClientService(clientServiceId, clientId);
			this.fromApiJsonDeserializer.validateForOrders(orders);
			for (Order order : orders) {
				if (order.getStatus().toString().equalsIgnoreCase(
						OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId().toString())
						|| order.getStatus().toString().equalsIgnoreCase(
								OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.NEW).getId().toString())) {
					provOrders.add(this.handleOrderStatusifNew(order));
				}
			}
			JsonObject provisioningObject = new JsonObject();
			provisioningObject.addProperty("requestType", UserActionStatusTypeEnum.MODIFY_SERVICE.toString());
			provisioningObject.addProperty("clientServiceId", clientServiceId);
			provisioningObject.addProperty("sub_action", sub_action);
			
			JsonObject passwordchange = new JsonObject();
			passwordchange.addProperty("password", password);
			provisioningObject.addProperty("passwordchange", passwordchange.toString());
			JsonCommand com = new JsonCommand(null, provisioningObject.toString(), provisioningObject,
					fromApiJsonHelper, null, null, null, null, null, null, null, null, null, null, null, null);
			this.provisioningWritePlatformService.createProvisioningRequest(provOrders, com, true);
		}

	}

	@Override
	public void createProvisioningService(Long clientId, Long clientServiceId) {
		List<Order> orders = new ArrayList<Order>();
		List<Order> provOrders = new ArrayList<Order>();
		if (this.isThisClientServiceHasProvisioning(clientServiceId)) {
			orders = this.orderRepository.findOrdersByClientService(clientServiceId, clientId);
			this.fromApiJsonDeserializer.validateForOrders(orders);
			for (Order order : orders) {
				if (order.getStatus().toString().equalsIgnoreCase(
						OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId().toString())
						|| order.getStatus().toString().equalsIgnoreCase(
								OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.NEW).getId().toString())) {
					provOrders.add(this.handleOrderStatusifNew(order));
				}
			}
			JsonObject provisioningObject = new JsonObject();
			provisioningObject.addProperty("requestType", UserActionStatusTypeEnum.ACTIVATION.toString());
			provisioningObject.addProperty("clientServiceId", clientServiceId);
			JsonCommand com = new JsonCommand(null, provisioningObject.toString(), provisioningObject,
					fromApiJsonHelper, null, null, null, null, null, null, null, null, null, null, null, null);
			this.provisioningWritePlatformService.createProvisioningRequest(provOrders, com, true);
		}

	}

	private Order handleOrderStatusifNew(Order order) {
		if (order.getStatus().toString()
				.equalsIgnoreCase(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.NEW).getId().toString())) {
			order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.PENDING).getId());
			this.orderRepository.saveAndFlush(order);
		}
		return order;
	}

	@Override
	public CommandProcessingResult suspendClientService(Long clientServiceId, JsonCommand command) {

		try {
			if (new JSONObject(command.json()).optBoolean("fromNGB")) {
				this.crmServices.suspendClientService(command);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// this.crmServices.suspendClientService(command);
		Long clientId = null;
		// Long clientServicePoid = command.getLoanId();
		Long clientServicePoid = command.longValueOfParameterNamed("clientServicePoId");
		if (clientServicePoid != null && clientServicePoid != 0) {
			ClientService clientServices = this.clientServiceRepository
					.findClientServiceByPoId(clientServicePoid.toString());
			clientServiceId = clientServices.getId();
			clientId = clientServices.getClientId();
		} else {
			clientId = command.longValueOfParameterNamed("clientId");
		}

		List<Order> orders = this.orderRepository.findOrdersByClientService(clientServiceId, clientId);
		List<Order> provOrders = new ArrayList<Order>();
		for (Order order : orders) {
			if (order.getStatus().toString().equalsIgnoreCase(StatusTypeEnum.ACTIVE.getValue().toString())) {
				provOrders.add(order);
			}
		}
		// generate prov req
		if (!provOrders.isEmpty()) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("suspensionDate", command.stringValueOfParameterNamed("suspensionDate"));
			jsonObject.addProperty("dateFormat", command.stringValueOfParameterNamed("dateFormat"));
			jsonObject.addProperty("locale", command.stringValueOfParameterNamed("locale"));
			jsonObject.addProperty("suspensionReason", "Due to client service suspention");

			JsonCommand com = new JsonCommand(null, jsonObject.toString(), jsonObject, fromApiJsonHelper, null, null,
					null, null, null, null, null, null, null, null, null, null);
			this.orderWritePlatformService.ordersSuspention(com, provOrders);
		}
		this.updateClientServiceStatus(clientServiceId);
		return new CommandProcessingResultBuilder().withEntityId(clientServiceId).withClientId(clientId).build();
	}

	@Override
	public CommandProcessingResult reactiveClientService(Long clientServiceId, JsonCommand command) {
		Long clientId = command.longValueOfParameterNamed("clientId");

		this.crmServices.reactivateClientService(command);

		List<Order> orders = this.orderRepository.findOrdersByClientService(clientServiceId, clientId);
		List<Order> provOrders = new ArrayList<Order>();
		for (Order order : orders) {
			if (order.getStatus().toString().equalsIgnoreCase(StatusTypeEnum.SUSPENDED.getValue().toString())) {
				provOrders.add(order);
			}
		}
		if (!provOrders.isEmpty()) {
			this.orderWritePlatformService.reactiveOrders(null, provOrders);
		}
		this.updateClientServiceStatus(clientServiceId);
		return new CommandProcessingResultBuilder().withEntityId(clientServiceId).withClientId(clientId).build();
	}

	@Override
	public CommandProcessingResult terminateClientService(Long clientServiceId, JsonCommand command) {
		Long clientId = command.longValueOfParameterNamed("clientId");
		this.crmServices.terminateClientService(command);
		final List<Order> orders = this.retriveSuspendaleOrders(clientServiceId, clientId);
		this.orderWritePlatformService.ordersTermination(null, orders);
		this.updateClientServiceStatus(clientServiceId);
		return new CommandProcessingResultBuilder().withEntityId(clientServiceId).withClientId(clientId).build();
	}

	private List<Order> retriveSuspendaleOrders(Long clientServiceId, Long clientId) {
		final List<Order> ordersList = this.orderRepository.findOrdersByClientService(clientServiceId, clientId);
		List<Order> orders = new ArrayList<Order>();
		for (Order order : ordersList) {
			if (order.getStatus().toString()
					.equalsIgnoreCase(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId().toString())
					|| order.getStatus().toString().equalsIgnoreCase(
							OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.SUSPENDED).getId().toString())) {
				orders.add(order);
			}
		}

		return orders;
	}

	private ClientService detailFun(JsonArray detailsArray, ClientService clientService, Long clientId) {
		String[] details = new String[detailsArray.size()];
		ServiceParameters serviceParameters = null;
		if (detailsArray.size() > 0) {
			for (int i = 0; i < detailsArray.size(); i++) {
				details[i] = detailsArray.get(i).toString();
			}

			for (final String detail : details) {
				final JsonElement element = this.fromApiJsonHelper.parse(detail);
				final String parameterId = this.fromApiJsonHelper.extractStringNamed("parameterId", element);
				final String parameterValue = this.fromApiJsonHelper.extractStringNamed("parameterValue", element);
				final String status = this.fromApiJsonHelper.extractStringNamed("status", element);
				serviceParameters = new ServiceParameters(clientId, parameterId, parameterValue, status);
				clientService.addDetails(serviceParameters);

			}
		}
		return clientService;
	}

	private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
		logger.error(dve.getMessage(), dve);
	}

	/*
	 * Guaranteed to throw an exception no matter what the data integrity issue
	 * is.
	 */
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("external_id")) {

			final String externalId = command.stringValueOfParameterNamed("externalId");
			throw new PlatformDataIntegrityException("error.msg.client.duplicate.externalId",
					"Client with externalId `" + externalId + "` already exists", "externalId", externalId);

		} else if (realCause.getMessage().contains("account_no_UNIQUE")) {
			final String accountNo = command.stringValueOfParameterNamed("accountNo");
			throw new PlatformDataIntegrityException("error.msg.client.duplicate.accountNo",
					"Client with accountNo `" + accountNo + "` already exists", "accountNo", accountNo);
		} else if (realCause.getMessage().contains("username")) {
			final String username = command.stringValueOfParameterNamed("username");
			throw new PlatformDataIntegrityException("error.msg.client.duplicate.username",
					"Client with username" + username + "` already exists", "username", username);
		} else if (realCause.getMessage().contains("email_key")) {
			final String email = command.stringValueOfParameterNamed("email");
			throw new PlatformDataIntegrityException("error.msg.client.duplicate.email",
					"Client with email `" + email + "` already exists", "email", email);

		} else if (realCause.getMessage().contains("login_key")) {
			final String login = command.stringValueOfParameterNamed("login");
			throw new PlatformDataIntegrityException("error.msg.client.duplicate.login",
					"Client with login `" + login + "` already exists", "login", login);
		}

		logAsErrorUnexpectedDataIntegrityException(dve);
		throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

	@Override
	public void updateClientServiceStatus(Long clientServiceId) {
		ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
		clientService.setStatus("PROCESSING");
		this.clientServiceRepository.saveAndFlush(clientService);
	}

	private boolean isThisClientServiceHasProvisioning(Long clientServiceId) {
		boolean isThisClientServiceHasProvisioning = false;
		List<ServiceParameterData> serviceParameterDatas = this.clientServiceReadPlatformService
				.retriveClientServiceDetails(clientServiceId);
		for (ServiceParameterData serviceParameterData : serviceParameterDatas) {
			if (!"None".equalsIgnoreCase(serviceParameterData.getType())) {
				isThisClientServiceHasProvisioning = true;
			}
			break;
		}
		return isThisClientServiceHasProvisioning;
	}

	@Override
	public CommandProcessingResult updateServiceparameter(JsonCommand command, Long serviceParameterDataId) {

		ServiceParameters serviceParameters = this.serviceParametersRepository.findOne(serviceParameterDataId);
		serviceParameters.setParameterValue(command.stringValueOfParameterName("paramValue"));
		this.serviceParametersRepository.saveAndFlush(serviceParameters);
		this.createProvisioningForModifyService(command.longValueOfParameterNamed("clientId"), command.longValueOfParameterNamed("clientServiceId"), command.stringValueOfParameterName("paramValue"), command.stringValueOfParameterName("sub_action"));
		return new CommandProcessingResult(Long.valueOf(1));
	}

}