package org.mifosplatform.workflow.eventaction.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import net.java.dev.obs.beesmart.AddExternalBeesmartMethod;

import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.mifosplatform.cms.eventmaster.domain.EventMaster;
import org.mifosplatform.cms.eventmaster.domain.EventMasterRepository;
import org.mifosplatform.cms.eventorder.domain.EventOrder;
import org.mifosplatform.cms.eventorder.domain.EventOrderRepository;
import org.mifosplatform.cms.eventorder.domain.EventOrderdetials;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.data.ClientProspectData;
import org.mifosplatform.crm.clientprospect.service.ClientProspectReadPlatformService;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.domain.TicketMaster;
import org.mifosplatform.crm.ticketmaster.domain.TicketMasterRepository;
import org.mifosplatform.crm.ticketmaster.service.TicketMasterReadPlatformService;
import org.mifosplatform.crm.userchat.domain.UserChat;
import org.mifosplatform.crm.userchat.domain.UserChatRepository;
import org.mifosplatform.finance.chargeorder.api.ChargingOrderApiResourse;
import org.mifosplatform.finance.chargeorder.service.ReverseCharges;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBilling;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBillingRepository;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayRecurringWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.organisation.message.domain.BillingMessage;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateConstants;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.exception.BillingMessageTemplateNotFoundException;
import org.mifosplatform.organisation.message.exception.EmailNotFoundException;
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.exception.HardwareDetailsNotFoundException;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientWritePlatformServiceJpaRepositoryImpl;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceWriteplatformService;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.contract.service.ContractPeriodReadPlatformService;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.useradministration.data.AppUserData;
import org.mifosplatform.useradministration.service.AppUserReadPlatformService;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.data.EventActionProcedureData;
import org.mifosplatform.workflow.eventaction.data.OrderNotificationData;
import org.mifosplatform.workflow.eventaction.domain.EventAction;
import org.mifosplatform.workflow.eventaction.domain.EventActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;

@Service
public class EventActionWritePlatformServiceImpl implements ActiondetailsWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(EventActionWritePlatformServiceImpl.class);

	private final OrderRepository orderRepository;
	private final TicketMasterRepository repository;
	private final ClientRepository clientRepository;
	private final EventOrderRepository eventOrderRepository;
	private final EventMasterRepository eventMasterRepository;
	private final EventActionRepository eventActionRepository;
	private final BillingMessageRepository messageDataRepository;
	private final AppUserReadPlatformService readPlatformService;
	private final ChargingOrderApiResourse chargingOrderApiResourse;
	private final ProcessRequestRepository processRequestRepository;
	private final BillingMessageTemplateRepository messageTemplateRepository;
	private final TicketMasterReadPlatformService ticketMasterReadPlatformService;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
	private final HardwareAssociationReadplatformService hardwareAssociationReadplatformService;
	private final PaymentGatewayRecurringWritePlatformService paymentGatewayRecurringWritePlatformService;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PaypalRecurringBillingRepository paypalRecurringBillingRepository;
	private final EventActionReadPlatformService eventActionReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	private final UserChatRepository userChatRepository;
	private final PlanRepository planRepository;
	private final ReverseCharges reverseInvoice;
	private final ItemDetailsRepository itemDetailsRepository;

	private BillingMessageTemplate activationTemplates;
	private BillingMessageTemplate reConnectionTemplates;
	private BillingMessageTemplate disConnectionTemplates;
	private BillingMessageTemplate paymentTemplates;
	private BillingMessageTemplate changePlanTemplates;
	private BillingMessageTemplate orderTerminationTemplates;

	private BillingMessageTemplate smsActivationTemplates;
	private BillingMessageTemplate smsDisconnectionTemplates;
	private BillingMessageTemplate smsReConnectionTemplates;
	private BillingMessageTemplate smsPaymentTemplates;
	private BillingMessageTemplate smsChangePlanTemplates;
	private BillingMessageTemplate smsOrderTerminationTemplates;
	private final BillingMessageTemplateRepository billingMessageTemplateRepository;
	private BillingMessageTemplate notifyTechicalTeam;
	private final ClientReadPlatformService clientReadPlatformService;
	private final ClientProspectReadPlatformService clientProspectReadPlatformService;
	private BillingMessageTemplate suspendService;
	private BillingMessageTemplate terminateService;
	private BillingMessageTemplate paymentAdj;
	private BillingMessageTemplate paymentReversal;
	private BillingMessageTemplate paymentAdjSms;
	private BillingMessageTemplate paymentReversalSms;
	private ClientServiceWriteplatformService clientServiceWriteplatformService;

	@Autowired
	public EventActionWritePlatformServiceImpl(final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final EventActionRepository eventActionRepository,
			final HardwareAssociationReadplatformService hardwareAssociationReadplatformService,
			final ContractPeriodReadPlatformService contractPeriodReadPlatformService,
			final OrderRepository orderRepository, final TicketMasterRepository repository,
			final ProcessRequestRepository processRequestRepository,
			final ChargingOrderApiResourse chargingOrderApiResourse,
			final BillingMessageRepository messageDataRepository, final ClientRepository clientRepository,
			final BillingMessageTemplateRepository messageTemplateRepository,
			final EventMasterRepository eventMasterRepository, final EventOrderRepository eventOrderRepository,
			final TicketMasterReadPlatformService ticketMasterReadPlatformService,
			final AppUserReadPlatformService readPlatformService,
			final PaymentGatewayRecurringWritePlatformService paymentGatewayRecurringWritePlatformService,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final PaypalRecurringBillingRepository paypalRecurringBillingRepository,
			final EventActionReadPlatformService eventActionReadPlatformService,
			final ConfigurationRepository configurationRepository, final UserChatRepository userChatRepository,
			final BillingMessageTemplateRepository billingMessageTemplateRepository,
			final PlanRepository planRepository, final ReverseCharges reverseInvoice,
			final ClientReadPlatformService clientReadPlatformService,
			final ClientProspectReadPlatformService clientProspectReadPlatformService,
			@Lazy final ClientServiceWriteplatformService clientServiceWriteplatformService,
			final ItemDetailsRepository itemDetailsRepository) {
		this.repository = repository;
		this.orderRepository = orderRepository;
		this.clientRepository = clientRepository;
		this.readPlatformService = readPlatformService;
		this.eventOrderRepository = eventOrderRepository;
		this.eventActionRepository = eventActionRepository;
		this.eventMasterRepository = eventMasterRepository;
		this.messageDataRepository = messageDataRepository;
		this.chargingOrderApiResourse = chargingOrderApiResourse;
		this.processRequestRepository = processRequestRepository;
		this.messageTemplateRepository = messageTemplateRepository;
		this.ticketMasterReadPlatformService = ticketMasterReadPlatformService;
		this.actionDetailsReadPlatformService = actionDetailsReadPlatformService;
		this.contractPeriodReadPlatformService = contractPeriodReadPlatformService;
		this.hardwareAssociationReadplatformService = hardwareAssociationReadplatformService;
		this.paymentGatewayRecurringWritePlatformService = paymentGatewayRecurringWritePlatformService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.paypalRecurringBillingRepository = paypalRecurringBillingRepository;
		this.eventActionReadPlatformService = eventActionReadPlatformService;
		this.configurationRepository = configurationRepository;
		this.userChatRepository = userChatRepository;
		this.planRepository = planRepository;
		this.reverseInvoice = reverseInvoice;
		this.clientReadPlatformService = clientReadPlatformService;
		this.billingMessageTemplateRepository = billingMessageTemplateRepository;
		this.clientProspectReadPlatformService = clientProspectReadPlatformService;
		this.clientServiceWriteplatformService = clientServiceWriteplatformService;
		this.itemDetailsRepository = itemDetailsRepository;
	}

	@Override
	public String AddNewActions(List<ActionDetaislData> actionDetaislDatas, final Long clientId,
			final String resourceId, String resourceString) {

		try {

			if (actionDetaislDatas != null) {
				EventAction eventAction = null;
				String headerMessage = null, bodyMessage = null, footerMessage = null;
				BillingMessage billingMessage = null;
				OrderNotificationData orderData = null;
				BillingMessageTemplate template = null;

				ClientData clientData = null;
				/*
				 * if(actionDetaislDatas.contains(EventActionConstants.
				 * ACTION_SEND_MAIL)||actionDetaislDatas.contains(
				 * EventActionConstants.ACTION_SEND_MESSAGE)){ }
				 */
				ClientProspectData clientProspectData = null;
				for (ActionDetaislData detailData : actionDetaislDatas) {
					if (detailData.getEventName().equalsIgnoreCase(EventActionConstants.EVENT_CREATE_CLIENT)
							|| detailData.getEventName().equalsIgnoreCase(EventActionConstants.EVENT_EDIT_TICKET)
							|| detailData.getEventName().equalsIgnoreCase(EventActionConstants.EVENT_CLOSE_TICKET)
							|| detailData.getEventName().equalsIgnoreCase(EventActionConstants.EVENT_CREATE_TICKET)
							|| detailData.getEventName()
									.equalsIgnoreCase(EventActionConstants.EVENT_CUSTOMER_ACTIVATION)) {
						clientData = this.clientReadPlatformService.retriveClientDetailsForEvents(clientId);

						break;
					} else if (detailData.getEventName().equalsIgnoreCase(EventActionConstants.EVENT_CREATE_LEAD)) {
						clientProspectData = this.clientProspectReadPlatformService
								.retriveLeadForEventActionCreateLead(clientId);

						break;
					} else if (detailData.getEventName().equalsIgnoreCase(EventActionConstants.EVENT_FOLLOWUP_LEAD)) {
						clientProspectData = this.clientProspectReadPlatformService
								.retriveLeadForEventActionFollowUp(clientId);

						break;
					}

				}
				for (ActionDetaislData detailsData : actionDetaislDatas) {

					EventActionProcedureData actionProcedureData = this.actionDetailsReadPlatformService
							.checkCustomeValidationForEvents(clientId, detailsData.getEventName(),
									detailsData.getActionName(), resourceId);

					JSONObject jsonObject = new JSONObject();
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
					if (actionProcedureData.isCheck()) {
						List<SubscriptionData> subscriptionDatas = this.contractPeriodReadPlatformService
								.retrieveSubscriptionDatabyContractType("Month(s)", 1);

						/* Notifications for Customer Creation */
						switch (detailsData.getActionName()) {

						// Email Notifications for customer creation
						case EventActionConstants.ACTION_SEND_MAIL:

							BillingMessageTemplate createClientMessageDetails = null;
							if (null == createClientMessageDetails) {
								createClientMessageDetails = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_USER);
							}

							if (createClientMessageDetails != null) {

								String subject = createClientMessageDetails.getSubject();
								String body = createClientMessageDetails.getBody();
								String footer = createClientMessageDetails.getFooter();
								String header = createClientMessageDetails.getHeader().replace("<PARAM1>",
										clientData.getFullname() + ",");
								body = body.replace("<PARAM2>", clientData.getUserName().trim());
								body = body.replace("<PARAM3>", clientData.getClientPassword().trim());

								billingMessage = new BillingMessage(header, body, footer, clientData.getOfficeMail(),
										clientData.getEmail(), subject,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										createClientMessageDetails,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

								this.messageDataRepository.save(billingMessage);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_SELFCARE);

							break;

						// SMS Notifications of Customer Creation

						case EventActionConstants.ACTION_SEND_MESSAGE:
							BillingMessageTemplate createClientMessageDetailsForSMS = null;
							if (null == createClientMessageDetailsForSMS) {
								createClientMessageDetailsForSMS = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE);
							}

							if (createClientMessageDetailsForSMS != null) {

								String subject = createClientMessageDetailsForSMS.getSubject();
								String body = createClientMessageDetailsForSMS.getBody();
								body = body.replace("<PARAM1>", clientData.getUserName().trim());
								body = body.replace("<PARAM2>", clientData.getClientPassword().trim());

								billingMessage = new BillingMessage(null, body, null, clientData.getOfficeMail(),
										clientData.getPhone(), subject,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										createClientMessageDetailsForSMS,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

								this.messageDataRepository.save(billingMessage);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE);

							break;

						case EventActionConstants.ACTION_SEND_ACTIVATION_SMS:

							BillingMessageTemplate createCustomersMessageDetails1 = null;
							if (null == createCustomersMessageDetails1) {
								createCustomersMessageDetails1 = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_CUSTOMER);
							}

							if (createCustomersMessageDetails1 != null) {

								/*
								 * String subject = createCustomersMessageDetails1.getSubject(); String body =
								 * createCustomersMessageDetails1.getBody(); body = body.replace("<PARAM1>",
								 * clientData.getUserName().trim()); body = body.replace("<PARAM2>",
								 * clientData.getClientPassword().trim());
								 */
								// add method sms
								// String mobileNo = clientData.getPhone();
								String mobileNo = "2348035537154";
								ItemDetails itemDetails = this.itemDetailsRepository.getItemDetailByClientId(clientId);
								String Message = "You BOX has been activated " + itemDetails.getSerialNumber() + ".";

								sendMessage(mobileNo, Message);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_CUSTOMER);

							break;
						}
						/* Close of Notifications for Customer Creation */

						/* Notifications for Order Management */
						switch (detailsData.getActionName()) {

						case EventActionConstants.ACTION_NOTIFY_ACTIVATION:

 							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
									new Long(resourceId));

							template = getTemplate(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_ACTIVATION);
//read phone number pass the message 
							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
							bodyMessage = bodyMessage.replaceAll("<Activation Date>",
									dateFormat.format(orderData.getActivationDate().toDate()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;
						case EventActionConstants.ACTION_NOTIFY_DISCONNECTION:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
									new Long(resourceId));

							template = getTemplate(
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_DISCONNECTION);

							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
							bodyMessage = bodyMessage.replaceAll("<Disconnection Date>",
									dateFormat.format(orderData.getEndDate().toDate()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;

						case EventActionConstants.ACTION_NOTIFY_RECONNECTION:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
									new Long(resourceId));

							template = getTemplate(
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_RECONNECTION);

							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
							bodyMessage = bodyMessage.replaceAll("<Reconnection Date>",
									dateFormat.format(orderData.getStartDate().toDate()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;
						// Notifications for payments
						case EventActionConstants.ACTION_NOTIFY_PAYMENT:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId, null);

							template = getTemplate(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT);

							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Amount>", resourceId);
							bodyMessage = bodyMessage.replaceAll("<Payment Date>", dateFormat.format(new Date()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;

						// Notifications for payment adjustments
						case EventActionConstants.ACTION_NOTIFY_PAYMENT_ADJ:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId, null);

							template = getTemplate(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT_ADJ);

							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Amount>", resourceId);
							bodyMessage = bodyMessage.replaceAll("<Payment Date>", dateFormat.format(new Date()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;

						// Notifications for payment Reversal
						case EventActionConstants.ACTION_NOTIFY_PAYMENT_REVERSAL:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId, null);

							template = getTemplate(
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT_REVERSAL);

							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Amount>", resourceId);
							bodyMessage = bodyMessage.replaceAll("<Payment Date>", dateFormat.format(new Date()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;

						case EventActionConstants.ACTION_NOTIFY_CHANGEPLAN:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
									new Long(resourceId));

							template = getTemplate(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_CHANGEPLAN);

							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
							bodyMessage = bodyMessage.replaceAll("<Activation Date>",
									dateFormat.format(orderData.getActivationDate().toDate()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;

						case EventActionConstants.ACTION_NOTIFY_ORDER_TERMINATE:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
									new Long(resourceId));

							template = getTemplate(
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_ORDERTERMINATION);

							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
							bodyMessage = bodyMessage.replaceAll("<Disconnection Date>", dateFormat.format(new Date()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;

						case EventActionConstants.ACTION_NOTIFY_SMS_CHANGEPLAN:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId, null);

							template = getTemplate(
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_CHANGEPLAN);

							bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
							bodyMessage = bodyMessage.replaceAll("<Activation Date>",
									dateFormat.format(orderData.getActivationDate().toDate()));

							billingMessage = new BillingMessage(null, bodyMessage, null, orderData.getOfficeEmail(),
									orderData.getClientPhone(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;

						// Notifications for Service Suspend.
						case EventActionConstants.ACTION_NOTIFY_SUSPENSION:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
									new Long(resourceId));

							template = getTemplate(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SERVICE_SUSPEND);

							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
							bodyMessage = bodyMessage.replaceAll("<Disconnection Date>",
									dateFormat.format(orderData.getEndDate().toDate()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;
						// Notifications for Service Termination.
						case EventActionConstants.ACTION_NOTIFY_TERMINATION:

							orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
									new Long(resourceId));

							template = getTemplate(
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SERVICE_TERMINATION);

							headerMessage = template.getHeader().replaceAll("<CustomerName>",
									orderData.getFirstName() + " " + orderData.getLastName());
							bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
							if (orderData.getEndDate() == null) {
								orderData.setEndDate(new LocalDate(new Date()));
							}
							bodyMessage = bodyMessage.replaceAll("<Disconnection Date>",
									dateFormat.format(orderData.getEndDate().toDate()));

							footerMessage = template.getFooter().replaceAll("<Reseller Name>",
									orderData.getOfficeName());
							footerMessage = footerMessage.replaceAll("<Contact Name>", orderData.getOfficeEmail());
							footerMessage = footerMessage.replaceAll("<Number>", orderData.getOfficePhoneNo());

							billingMessage = new BillingMessage(headerMessage, bodyMessage, footerMessage,
									orderData.getOfficeEmail(), orderData.getEmailId(), template.getSubject(),
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

							this.messageDataRepository.save(billingMessage);

							break;

						default:
							break;

						}
						/*
						 * Close of Switch case Notifications for Order Management
						 */

						/* Notifications for customer Activation */
						switch (detailsData.getActionName()) {

						// Notifications for customer Activation
						case EventActionConstants.ACTION_SEND_CUSTOMER_EMAIL:

							BillingMessageTemplate createCustomerMessageDetails = null;
							if (null == createCustomerMessageDetails) {
								createCustomerMessageDetails = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_CUSTOMER);
							}

							if (createCustomerMessageDetails != null) {

								String subject = createCustomerMessageDetails.getSubject();
								String body = createCustomerMessageDetails.getBody();
								String footer = createCustomerMessageDetails.getFooter();
								String header = createCustomerMessageDetails.getHeader().replace("<PARAM1>",
										clientData.getFullname() + ",");
								body = body.replace("<PARAM2>", clientData.getUserName().trim());
								body = body.replace("<PARAM3>", clientData.getClientPassword().trim());

								billingMessage = new BillingMessage(header, body, footer, clientData.getOfficeMail(),
										clientData.getEmail(), subject,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										createCustomerMessageDetails,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

								this.messageDataRepository.save(billingMessage);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_CUSTOMER);

							break;
						case EventActionConstants.ACTION_SEND_CUSTOMER_SMS:

							BillingMessageTemplate createCustomersMessageDetails = null;
							if (null == createCustomersMessageDetails) {
								createCustomersMessageDetails = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_CUSTOMER);
							}

							if (createCustomersMessageDetails != null) {

								String subject = createCustomersMessageDetails.getSubject();
								String body = createCustomersMessageDetails.getBody();
								/*
								 * String footer = createCustomersMessageDetails.getFooter(); String header =
								 * createCustomersMessageDetails.getHeader().replace("<PARAM1>",
								 * clientData.getFullname() + ",");
								 */ body = body.replace("<PARAM1>", clientData.getUserName().trim());
								body = body.replace("<PARAM2>", clientData.getClientPassword().trim());

								// add method sms

								String mobileNo = clientData.getPhone();

								billingMessage = new BillingMessage(null, body, null, clientData.getOfficeMail(),
										clientData.getEmail(), subject,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										createCustomersMessageDetails,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

								this.messageDataRepository.save(billingMessage);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_CUSTOMER);

							break;

						default:
							break;
						}

						/* Notifications for Tickets */
						switch (detailsData.getActionName()) {

						case EventActionConstants.ACTION_SEND_CREATE_TICKET_MAIL:

							TicketMasterData data = this.ticketMasterReadPlatformService.retrieveTicket(clientId,
									new Long(resourceId));
							TicketMaster ticketMaster = this.repository.findOne(new Long(resourceId));
							AppUserData user = this.readPlatformService.retrieveUser(new Long(data.getUserId()));

							BillingMessageTemplate billingMessageTemplate = null;
							billingMessageTemplate = this.messageTemplateRepository.findByTemplateDescription(
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_TICKET_TEMPLATE);
							if (billingMessageTemplate != null) {
								String value = resourceString;
								String removeUrl = "<br/><b>URL : </b>" + "<a href=" + value + ">View Ticket</a>";
								// removeUrl.replaceAll("(PARAMURL)",
								// ticketURL+""+resourceId);

								if (!user.getEmail().isEmpty()) {
									billingMessage = new BillingMessage("CREATE TICKET",
											data.getProblemDescription() + "<br/>" + ticketMaster.getDescription()
													+ "\n" + removeUrl,
											"", user.getEmail(), user.getEmail(), "Ticket:" + resourceId,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
											billingMessageTemplate,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
									this.messageDataRepository.save(billingMessage);

									billingMessage = new BillingMessage("CREATE TICKET",
											data.getProblemDescription() + "<br/>" + ticketMaster.getDescription()
													+ "\n" + removeUrl,
											"", clientData.getEmail(), clientData.getEmail(), "Ticket:" + resourceId,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
											billingMessageTemplate,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
									this.messageDataRepository.save(billingMessage);

								} else {
									if (actionProcedureData.getEmailId().isEmpty()) {

										throw new EmailNotFoundException(new Long(data.getUserId()));
									} else {

										billingMessage = new BillingMessage("CREATE TICKET",
												data.getProblemDescription() + "<br/>" + ticketMaster.getDescription()
														+ "\n" + removeUrl,
												"", actionProcedureData.getEmailId(), actionProcedureData.getEmailId(),
												"Ticket:" + resourceId,
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
												billingMessageTemplate,
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
										this.messageDataRepository.save(billingMessage);
									}
								}
							}

							break;

						case EventActionConstants.ACTION_EDIT_TICKET_MAIL:

							TicketMasterData ticketdata = this.ticketMasterReadPlatformService.retrieveTicket(clientId,
									new Long(resourceId));
							TicketMaster ticketMasterdata = this.repository.findOne(new Long(resourceId));
							AppUserData appUser = this.readPlatformService
									.retrieveUser(new Long(ticketdata.getUserId()));

							BillingMessageTemplate editTicketMessageTemplate = null;
							editTicketMessageTemplate = this.messageTemplateRepository.findByTemplateDescription(
									BillingMessageTemplateConstants.MESSAGE_TEMPLATE_TICKET_TEMPLATE);
							if (editTicketMessageTemplate != null) {
								String value = resourceString;
								String removeUrl = "<br/><b>URL : </b>" + "<a href=" + value + ">View Ticket</a>";
								// removeUrl.replaceAll("(PARAMURL)",
								// ticketURL+""+resourceId);

								if (!appUser.getEmail().isEmpty()) {
									billingMessage = new BillingMessage("TICKET FOLLOWUP ",
											ticketdata.getProblemDescription() + "<br/>"
													+ ticketMasterdata.getDescription() + "\n" + removeUrl,
											"", appUser.getEmail(), appUser.getEmail(), "Ticket:" + resourceId,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
											editTicketMessageTemplate,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
									this.messageDataRepository.save(billingMessage);

									billingMessage = new BillingMessage("TICKET FOLLOWUP ",
											ticketdata.getProblemDescription() + "<br/>"
													+ ticketMasterdata.getDescription() + "\n" + removeUrl,
											"", clientData.getEmail(), clientData.getEmail(), "Ticket:" + resourceId,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
											editTicketMessageTemplate,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
									this.messageDataRepository.save(billingMessage);

								} else {
									if (actionProcedureData.getEmailId().isEmpty()) {

										throw new EmailNotFoundException(new Long(ticketdata.getUserId()));
									} else {

										billingMessage = new BillingMessage("TICKET FOLLOWUP ",
												ticketdata.getProblemDescription() + "<br/>"
														+ ticketMasterdata.getDescription() + "\n" + removeUrl,
												"", actionProcedureData.getEmailId(), actionProcedureData.getEmailId(),
												"Ticket:" + resourceId,
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
												editTicketMessageTemplate,
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
										this.messageDataRepository.save(billingMessage);
									}
								}
							}

							break;

						case EventActionConstants.ACTION_CLOSE_TICKET_MAIL:

							TicketMasterData ticketMasterData = this.ticketMasterReadPlatformService
									.retrieveTicket(clientId, new Long(resourceId));
							TicketMaster ticketData = this.repository.findOne(new Long(resourceId));
							AppUserData logedInUser = this.readPlatformService
									.retrieveUser(new Long(ticketMasterData.getUserId()));

							BillingMessageTemplate closeTicketBillingMessageTemplate = null;
							closeTicketBillingMessageTemplate = this.messageTemplateRepository
									.findByTemplateDescription(
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_TICKET_TEMPLATE);
							if (closeTicketBillingMessageTemplate != null) {
								String value = resourceString;
								String removeUrl = "<br/><b>URL : </b>" + "<a href=" + value + ">View Ticket</a>";
								// removeUrl.replaceAll("(PARAMURL)",
								// ticketURL+""+resourceId);

								if (!logedInUser.getEmail().isEmpty()) {
									billingMessage = new BillingMessage("TICKET CLOSED",
											ticketMasterData.getProblemDescription() + "<br/>"
													+ ticketData.getDescription() + "\n" + removeUrl,
											"", logedInUser.getEmail(), logedInUser.getEmail(), "Ticket:" + resourceId,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
											closeTicketBillingMessageTemplate,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
									this.messageDataRepository.save(billingMessage);

									billingMessage = new BillingMessage("TICKET CLOSED",
											ticketMasterData.getProblemDescription() + "<br/>"
													+ ticketData.getDescription() + "\n" + removeUrl,
											"", clientData.getEmail(), clientData.getEmail(), "Ticket:" + resourceId,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
											closeTicketBillingMessageTemplate,
											BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
									this.messageDataRepository.save(billingMessage);

								} else {
									if (actionProcedureData.getEmailId().isEmpty()) {

										throw new EmailNotFoundException(new Long(ticketMasterData.getUserId()));
									} else {

										billingMessage = new BillingMessage("TICKET CLOSED",
												ticketMasterData.getProblemDescription() + "<br/>"
														+ ticketData.getDescription() + "\n" + removeUrl,
												"", actionProcedureData.getEmailId(), actionProcedureData.getEmailId(),
												"Ticket:" + resourceId,
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
												closeTicketBillingMessageTemplate,
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
										this.messageDataRepository.save(billingMessage);
									}
								}
							}

							break;

						}

						/* Notifications for Leads */
						switch (detailsData.getActionName()) {

						case EventActionConstants.ACTION_SEND_LEAD_MAIL:

							BillingMessageTemplate createLeadMessageDetails = null;

							if (null == createLeadMessageDetails) {
								createLeadMessageDetails = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_LEAD);
							}

							if (createLeadMessageDetails != null) {

								String subject = createLeadMessageDetails.getSubject();
								String body = createLeadMessageDetails.getBody();
								String footer = createLeadMessageDetails.getFooter();
								String header = createLeadMessageDetails.getHeader().replace("<PARAM1>",
										clientProspectData.getFullName() + ",");
								body = body.replace("<PARAM2>", clientProspectData.getId().toString());
								body = body.replace("<PARAM3>", clientProspectData.getCreatedDate().toString());

								billingMessage = new BillingMessage(header, body, footer,
										clientProspectData.getOfficeMail(), clientProspectData.getEmail(), subject,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										createLeadMessageDetails,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

								billingMessage = this.messageDataRepository.save(billingMessage);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_SELFCARE);
							break;

						case EventActionConstants.ACTION_SEND_FOLLOWUP_MAIL:

							BillingMessageTemplate followupLeadMessageDetails = null;

							if (null == followupLeadMessageDetails) {
								followupLeadMessageDetails = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_FOLLOWUP_LEAD);
							}

							if (followupLeadMessageDetails != null) {

								String subject = followupLeadMessageDetails.getSubject();
								String body = followupLeadMessageDetails.getBody();
								String footer = followupLeadMessageDetails.getFooter();
								String header = followupLeadMessageDetails.getHeader().replace("<PARAM1>",
										clientProspectData.getFullName() + ",");
								body = body.replace("<PARAM2>", clientProspectData.getId().toString());
								body = body.replace("<PARAM3>", clientProspectData.getStatus());
								body = body.replace("<PARAM4>",
										clientProspectData.getPreferredCallingTime().toString());

								billingMessage = new BillingMessage(header, body, footer,
										clientProspectData.getOfficeMail(), clientProspectData.getEmail(), subject,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										followupLeadMessageDetails,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

								billingMessage = this.messageDataRepository.save(billingMessage);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_SELFCARE);
							break;

						case EventActionConstants.ACTION_SEND_LEAD_SMS:
							BillingMessageTemplate createleadMessageDetailsForSMS = null;
							if (null == createleadMessageDetailsForSMS) {
								createleadMessageDetailsForSMS = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_LEAD);
							}

							if (createleadMessageDetailsForSMS != null) {

								String subject = createleadMessageDetailsForSMS.getSubject();
								String body = createleadMessageDetailsForSMS.getBody();
								String footer = createleadMessageDetailsForSMS.getFooter();

								body = body.replace("<PARAM1>", clientProspectData.getFullName());
								body = body.replace("<PARAM2>", clientProspectData.getId().toString());
								body = body.replace("<PARAM3>", clientProspectData.getCreatedDate().toString());

								billingMessage = new BillingMessage(null, body, footer,
										clientProspectData.getOfficeMail(), clientProspectData.getEmail(), subject,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										createleadMessageDetailsForSMS,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

								this.messageDataRepository.save(billingMessage);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE);

							break;

						case EventActionConstants.ACTION_SEND_FOLLOWUP_SMS:
							BillingMessageTemplate followupleadMessageDetailsForSMS = null;
							if (null == followupleadMessageDetailsForSMS) {
								followupleadMessageDetailsForSMS = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_FOLLOWUP_LEAD);
							}

							if (followupleadMessageDetailsForSMS != null) {

								String subject = followupleadMessageDetailsForSMS.getSubject();
								String body = followupleadMessageDetailsForSMS.getBody();
								String footer = followupleadMessageDetailsForSMS.getFooter();
								String header = followupleadMessageDetailsForSMS.getHeader();
								body = body.replace("<PARAM1>", clientProspectData.getFullName());
								body = body.replace("<PARAM2>", clientProspectData.getId().toString());
								body = body.replace("<PARAM3>", clientProspectData.getStatus());
								body = body.replace("<PARAM4>",
										clientProspectData.getPreferredCallingTime().toString());

								billingMessage = new BillingMessage(header, body, footer,
										clientProspectData.getOfficeMail(), clientProspectData.getEmail(), subject,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										followupleadMessageDetailsForSMS,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

								this.messageDataRepository.save(billingMessage);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE);

							break;

						}
						/* Close of Switch ,Notifications for Leads */

						switch (detailsData.getActionName()) {

						/*
						 * case EventActionConstants.ACTION_SEND_MAIL :
						 * 
						 * BillingMessageTemplate createClientMessageDetails= null; if(null ==
						 * createClientMessageDetails){ createClientMessageDetails =
						 * this.billingMessageTemplateRepository. findByTemplateDescription(
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_CREATE_USER); }
						 * 
						 * if (createClientMessageDetails != null) {
						 * 
						 * String subject = createClientMessageDetails.getSubject(); String body =
						 * createClientMessageDetails.getBody(); String footer =
						 * createClientMessageDetails.getFooter(); String header =
						 * createClientMessageDetails.getHeader().replace( "<PARAM1>",
						 * clientData.getFullname()+ ","); body = body.replace("<PARAM2>",
						 * clientData.getUserName().trim()); body = body.replace("<PARAM3>",
						 * clientData.getClientPassword().trim());
						 * 
						 * billingMessage = new BillingMessage(header, body, footer,
						 * clientData.getOfficeMail(), clientData.getEmail(), subject,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_STATUS,
						 * createClientMessageDetails, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * 
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * } else throw new BillingMessageTemplateNotFoundException(
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_CREATE_SELFCARE);
						 * 
						 * break;
						 */

						case EventActionConstants.ACTION_ACTIVE:

							AssociationData associationData = this.hardwareAssociationReadplatformService
									.retrieveSingleDetails(actionProcedureData.getOrderId());
							if (associationData == null) {
								throw new HardwareDetailsNotFoundException(actionProcedureData.getOrderId().toString());
							}
							jsonObject.put("renewalPeriod", subscriptionDatas.get(0).getId());
							jsonObject.put("description", "Order Renewal By Scheduler");
							eventAction = new EventAction(DateUtils.getDateOfTenant(), "CREATE", "PAYMENT",
									EventActionConstants.ACTION_RENEWAL.toString(), "/orders/renewal",
									Long.parseLong(resourceId), jsonObject.toString(), actionProcedureData.getOrderId(),
									clientId);
							this.eventActionRepository.save(eventAction);
							break;

						case EventActionConstants.ACTION_NEW:

							jsonObject.put("billAlign", "false");
							jsonObject.put("contractPeriod", subscriptionDatas.get(0).getId());
							jsonObject.put("dateFormat", "dd MMMM yyyy");
							jsonObject.put("locale", "en");
							jsonObject.put("paytermCode", "Monthly");
							jsonObject.put("planCode", actionProcedureData.getPlanId());
							jsonObject.put("isNewplan", "true");
							jsonObject.put("start_date", dateFormat.format(DateUtils.getDateOfTenant()));
							eventAction = new EventAction(DateUtils.getDateOfTenant(), "CREATE", "PAYMENT",
									actionProcedureData.getActionName(), "/orders/" + clientId,
									Long.parseLong(resourceId), jsonObject.toString(), null, clientId);
							this.eventActionRepository.save(eventAction);

							break;

						case EventActionConstants.ACTION_DISCONNECT:

							eventAction = new EventAction(DateUtils.getDateOfTenant(), "CREATE", "PAYMENT",
									EventActionConstants.ACTION_ACTIVE.toString(), "/orders/reconnect/" + clientId,
									Long.parseLong(resourceId), jsonObject.toString(), actionProcedureData.getOrderId(),
									clientId);
							this.eventActionRepository.save(eventAction);

							break;

						/*
						 * case EventActionConstants.ACTION_SEND_LEAD_MAIL :
						 * 
						 * BillingMessageTemplate createLeadMessageDetails= null;
						 * 
						 * if(null == createLeadMessageDetails){ createLeadMessageDetails =
						 * this.billingMessageTemplateRepository. findByTemplateDescription(
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_CREATE_LEAD); }
						 * 
						 * if (createLeadMessageDetails != null) {
						 * 
						 * String subject = createLeadMessageDetails.getSubject(); String body =
						 * createLeadMessageDetails.getBody(); String footer =
						 * createLeadMessageDetails.getFooter(); String header =
						 * createLeadMessageDetails.getHeader().replace( "<PARAM1>",
						 * clientProspectData.getFullName()+ ","); body = body.replace("<PARAM2>",
						 * clientProspectData.getId().toString()); body = body.replace("<PARAM3>",
						 * clientProspectData.getCreatedDate().toString());
						 * 
						 * billingMessage = new BillingMessage(header, body, footer,
						 * clientProspectData.getOfficeMail(), clientProspectData.getEmail(), subject,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_STATUS,
						 * createLeadMessageDetails, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * 
						 * billingMessage=this.messageDataRepository.save( billingMessage);
						 * 
						 * } else throw new BillingMessageTemplateNotFoundException(
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_CREATE_SELFCARE); break;
						 */

						/*
						 * case EventActionConstants.ACTION_SEND_FOLLOWUP_MAIL :
						 * 
						 * BillingMessageTemplate followupLeadMessageDetails= null;
						 * 
						 * if(null == followupLeadMessageDetails){ followupLeadMessageDetails =
						 * this.billingMessageTemplateRepository. findByTemplateDescription(
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_FOLLOWUP_LEAD); }
						 * 
						 * if (followupLeadMessageDetails != null) {
						 * 
						 * String subject = followupLeadMessageDetails.getSubject(); String body =
						 * followupLeadMessageDetails.getBody(); String footer =
						 * followupLeadMessageDetails.getFooter(); String header =
						 * followupLeadMessageDetails.getHeader().replace( "<PARAM1>",
						 * clientProspectData.getFullName()+ ","); body = body.replace("<PARAM2>",
						 * clientProspectData.getId().toString()); body = body.replace("<PARAM3>",
						 * clientProspectData.getStatus()); body = body.replace("<PARAM4>",
						 * clientProspectData.getPreferredCallingTime().toString ());
						 * 
						 * billingMessage = new BillingMessage(header, body, footer,
						 * clientProspectData.getOfficeMail(), clientProspectData.getEmail(), subject,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_STATUS,
						 * followupLeadMessageDetails, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * 
						 * billingMessage=this.messageDataRepository.save( billingMessage);
						 * 
						 * } else throw new BillingMessageTemplateNotFoundException(
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_CREATE_SELFCARE); break;
						 */

						/*
						 * case EventActionConstants.ACTION_SEND_CREATE_TICKET_MAIL :
						 * 
						 * TicketMasterData data = this.ticketMasterReadPlatformService.retrieveTicket(
						 * clientId,new Long(resourceId)); TicketMaster
						 * ticketMaster=this.repository.findOne(new Long(resourceId)); AppUserData user
						 * = this.readPlatformService.retrieveUser(new Long(data.getUserId()));
						 * 
						 * BillingMessageTemplate billingMessageTemplate=null; billingMessageTemplate =
						 * this.messageTemplateRepository. findByTemplateDescription(
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_TICKET_TEMPLATE);
						 * if(billingMessageTemplate !=null){ String value=ticketURL+""+resourceId;
						 * String removeUrl= "<br/><b>URL : </b>"+"<a href="+value+ ">View Ticket</a>";
						 * // removeUrl.replaceAll("(PARAMURL)", ticketURL+""+resourceId);
						 * 
						 * if(!user.getEmail().isEmpty()){ billingMessage = new
						 * BillingMessage("CREATE TICKET", data.getProblemDescription()+"<br/>"
						 * +ticketMaster.getDescription()+"\n"+removeUrl, "", user.getEmail(),
						 * user.getEmail(), "Ticket:"+resourceId, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_STATUS, billingMessageTemplate,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * billingMessage = new BillingMessage("CREATE TICKET",
						 * data.getProblemDescription()+"<br/>"
						 * +ticketMaster.getDescription()+"\n"+removeUrl, "", clientData.getEmail(),
						 * clientData.getEmail(), "Ticket:"+resourceId, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_STATUS, billingMessageTemplate,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * }else{ if(actionProcedureData.getEmailId().isEmpty()){
						 * 
						 * throw new EmailNotFoundException(new Long(data.getUserId())); }else{
						 * 
						 * billingMessage = new BillingMessage("CREATE TICKET",
						 * data.getProblemDescription()+"<br/>"
						 * +ticketMaster.getDescription()+"\n"+removeUrl, "",
						 * actionProcedureData.getEmailId(), actionProcedureData.getEmailId(),
						 * "Ticket:"+resourceId, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_STATUS, billingMessageTemplate,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * this.messageDataRepository.save(billingMessage); } } }
						 * 
						 * break;
						 * 
						 * case EventActionConstants.ACTION_EDIT_TICKET_MAIL :
						 * 
						 * TicketMasterData ticketdata =
						 * this.ticketMasterReadPlatformService.retrieveTicket( clientId,new
						 * Long(resourceId)); TicketMaster ticketMasterdata=this.repository.findOne(new
						 * Long(resourceId)); AppUserData appUser =
						 * this.readPlatformService.retrieveUser(new Long(ticketdata.getUserId()));
						 * 
						 * BillingMessageTemplate editTicketMessageTemplate=null;
						 * editTicketMessageTemplate = this.messageTemplateRepository.
						 * findByTemplateDescription( BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_TICKET_TEMPLATE); if(editTicketMessageTemplate !=null){
						 * String value=ticketURL+""+resourceId; String removeUrl=
						 * "<br/><b>URL : </b>"+"<a href="+value+ ">View Ticket</a>"; //
						 * removeUrl.replaceAll("(PARAMURL)", ticketURL+""+resourceId);
						 * 
						 * if(!appUser.getEmail().isEmpty()){ billingMessage = new
						 * BillingMessage("TICKET FOLLOWUP ", ticketdata.getProblemDescription()+"<br/>"
						 * +ticketMasterdata.getDescription()+"\n"+removeUrl, "", appUser.getEmail(),
						 * appUser.getEmail(), "Ticket:"+resourceId, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_STATUS, editTicketMessageTemplate,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * billingMessage = new BillingMessage( "TICKET FOLLOWUP ",
						 * ticketdata.getProblemDescription()+"<br/>"
						 * +ticketMasterdata.getDescription()+"\n"+removeUrl, "", clientData.getEmail(),
						 * clientData.getEmail(), "Ticket:"+resourceId, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_STATUS, editTicketMessageTemplate,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * }else{ if(actionProcedureData.getEmailId().isEmpty()){
						 * 
						 * throw new EmailNotFoundException(new Long(ticketdata.getUserId())); }else{
						 * 
						 * billingMessage = new BillingMessage( "TICKET FOLLOWUP ",
						 * ticketdata.getProblemDescription()+"<br/>"
						 * +ticketMasterdata.getDescription()+"\n"+removeUrl, "",
						 * actionProcedureData.getEmailId(), actionProcedureData.getEmailId(),
						 * "Ticket:"+resourceId, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_STATUS, editTicketMessageTemplate,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * this.messageDataRepository.save(billingMessage); } } }
						 * 
						 * break;
						 * 
						 * 
						 * 
						 * case EventActionConstants.ACTION_CLOSE_TICKET_MAIL :
						 * 
						 * TicketMasterData ticketMasterData =
						 * this.ticketMasterReadPlatformService.retrieveTicket( clientId,new
						 * Long(resourceId)); TicketMaster ticketData=this.repository.findOne(new
						 * Long(resourceId)); AppUserData logedInUser =
						 * this.readPlatformService.retrieveUser(new
						 * Long(ticketMasterData.getUserId()));
						 * 
						 * BillingMessageTemplate closeTicketBillingMessageTemplate=null;
						 * closeTicketBillingMessageTemplate = this.messageTemplateRepository.
						 * findByTemplateDescription( BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_TICKET_TEMPLATE); if(closeTicketBillingMessageTemplate
						 * !=null){ String value=ticketURL+""+resourceId; String removeUrl=
						 * "<br/><b>URL : </b>"+"<a href="+value+ ">View Ticket</a>"; //
						 * removeUrl.replaceAll("(PARAMURL)", ticketURL+""+resourceId);
						 * 
						 * if(!logedInUser.getEmail().isEmpty()){ billingMessage = new
						 * BillingMessage("TICKET CLOSED",
						 * ticketMasterData.getProblemDescription()+"<br/>"
						 * +ticketData.getDescription()+"\n"+removeUrl, "", logedInUser.getEmail(),
						 * logedInUser.getEmail(), "Ticket:"+resourceId,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_STATUS,
						 * closeTicketBillingMessageTemplate, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * billingMessage = new BillingMessage("TICKET CLOSED",
						 * ticketMasterData.getProblemDescription()+"<br/>"
						 * +ticketData.getDescription()+"\n"+removeUrl, "", clientData.getEmail(),
						 * clientData.getEmail(), "Ticket:"+resourceId, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_STATUS, closeTicketBillingMessageTemplate,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * }else{ if(actionProcedureData.getEmailId().isEmpty()){
						 * 
						 * throw new EmailNotFoundException(new Long(ticketMasterData.getUserId()));
						 * }else{
						 * 
						 * billingMessage = new BillingMessage("TICKET CLOSED",
						 * ticketMasterData.getProblemDescription()+"<br/>"
						 * +ticketData.getDescription()+"\n"+removeUrl, "",
						 * actionProcedureData.getEmailId(), actionProcedureData.getEmailId(),
						 * "Ticket:"+resourceId, BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_STATUS, closeTicketBillingMessageTemplate,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						 * this.messageDataRepository.save(billingMessage); } } }
						 * 
						 * break;
						 */

						default:
							break;
						}

					}

					switch (detailsData.getActionName()) {

					case EventActionConstants.ACTION_PROVISION_IT:

						Client client = this.clientRepository.findOne(clientId);
						EventOrder eventOrder = this.eventOrderRepository.findOne(Long.valueOf(resourceId));
						EventMaster eventMaster = this.eventMasterRepository.findOne(eventOrder.getEventId());
						// String response=
						// AddExternalBeesmartMethod.addVodPackage(client.getOffice().getExternalId().toString(),client.getAccountNo(),
						// eventMaster.getEventName());

						ProcessRequest processRequest = new ProcessRequest(Long.valueOf(0), eventOrder.getClientId(),
								eventOrder.getId(), ProvisioningApiConstants.PROV_BEENIUS,
								ProvisioningApiConstants.REQUEST_ACTIVATION_VOD, 'Y', 'Y');
						List<EventOrderdetials> eventDetails = eventOrder.getEventOrderdetials();
						// EventMaster
						// eventMaster=this.eventMasterRepository.findOne(eventOrder.getEventId());
						// JSONObject jsonObject=new JSONObject();
						jsonObject.put("officeUid", client.getOffice().getExternalId());
						jsonObject.put("subscriberUid", client.getAccountNo());
						jsonObject.put("vodUid", eventMaster.getEventName());

						for (EventOrderdetials details : eventDetails) {
							ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(details.getId(),
									details.getEventDetails().getId(), jsonObject.toString(), null, null,
									eventMaster.getEventStartDate(), eventMaster.getEventEndDate(),
									DateUtils.getDateOfTenant(), DateUtils.getDateOfTenant(), 'N',
									ProvisioningApiConstants.REQUEST_ACTIVATION_VOD, null);
							processRequest.add(processRequestDetails);
						}
						this.processRequestRepository.save(processRequest);

						break;

					case EventActionConstants.ACTION_SEND_PROVISION:

						eventAction = new EventAction(DateUtils.getDateOfTenant(), "CLOSE", "Client",
								EventActionConstants.ACTION_SEND_PROVISION.toString(), "/processrequest/" + clientId,
								Long.parseLong(resourceId), jsonObject.toString(), clientId, clientId);
						this.eventActionRepository.save(eventAction);

						break;

					case EventActionConstants.ACTION_ACTIVE_LIVE_EVENT:
						eventMaster = this.eventMasterRepository.findOne(Long.valueOf(resourceId));

						eventAction = new EventAction(eventMaster.getEventStartDate(), "Create", "Live Event",
								EventActionConstants.ACTION_ACTIVE_LIVE_EVENT.toString(), "/eventmaster",
								Long.parseLong(resourceId), jsonObject.toString(), Long.valueOf(0), Long.valueOf(0));
						this.eventActionRepository.saveAndFlush(eventAction);

						eventAction = new EventAction(eventMaster.getEventEndDate(), "Disconnect", "Live Event",
								EventActionConstants.ACTION_INACTIVE_LIVE_EVENT.toString(), "/eventmaster",
								Long.parseLong(resourceId), jsonObject.toString(), Long.valueOf(0), Long.valueOf(0));
						this.eventActionRepository.saveAndFlush(eventAction);

						break;

					case EventActionConstants.ACTION_INVOICE:

						Order order = this.orderRepository.findOne(new Long(resourceId));
						final Plan plan = this.planRepository.findOne(order.getPlanId());

						if (detailsData.getEventName().equalsIgnoreCase(EventActionConstants.EVENT_DISCONNECTION_ORDER)
								|| detailsData.getEventName()
										.equalsIgnoreCase(EventActionConstants.EVENT_SUSPENSION_ORDER)
								|| detailsData.getEventName()
										.equalsIgnoreCase(EventActionConstants.EVENT_TERMINATE_ORDER)) {
							if (plan.getBillRule() != 400 && plan.getBillRule() != 300 && plan.isPrepaid() == 'N') {
								this.reverseInvoice.reverseInvoiceServices(order.getId(), order.getClientId(),
										DateUtils.getLocalDateOfTenant());
							}
						} else {
							jsonObject.put("dateFormat", "dd MMMM yyyy");
							jsonObject.put("locale", "en");
							jsonObject.put("systemDate", dateFormat.format(order.getStartDate()));
							if (detailsData.IsSynchronous().equalsIgnoreCase("N")) {
								eventAction = new EventAction(DateUtils.getDateOfTenant(), "CREATE",
										EventActionConstants.EVENT_ACTIVE_ORDER.toString(),
										EventActionConstants.ACTION_INVOICE.toString(), "/billingorder/" + clientId,
										Long.parseLong(resourceId), jsonObject.toString(), Long.parseLong(resourceId),
										clientId);
								this.eventActionRepository.save(eventAction);

							} else {

								jsonObject.put("dateFormat", "dd MMMM yyyy");
								jsonObject.put("locale", "en");
								jsonObject.put("systemDate", dateFormat.format(order.getStartDate()));
								//check flag isAdvance
								
									if(plan.getIsPrepaid()=='N' || plan.getIsPrepaid()=='N') {
									//System.out.println("charging"+plan.getIsAdvance());
								this.chargingOrderApiResourse.createChargesToOrders(order.getClientId(),
										jsonObject.toString());
								}
								else {
									System.out.println("do not charge"+plan.getIsPrepaid());

								}
							}
						}
						break;

					case EventActionConstants.ACTION_ACTIVATION_REQUEST:
						if (detailsData.IsSynchronous().equalsIgnoreCase("Y")) {
							this.clientServiceWriteplatformService.createProvisioningService(clientId,
									Long.valueOf(resourceId));
							this.clientServiceWriteplatformService.updateClientServiceStatus(Long.valueOf(resourceId));
							return "true";
						} else {
							return "false";
						}

					case EventActionConstants.ACTION_SEND_PAYMENT:

						eventAction = new EventAction(DateUtils.getDateOfTenant(), "SEND", "Payment Receipt",
								EventActionConstants.ACTION_SEND_PAYMENT.toString(),
								"/billmaster/payment/" + clientId + "/" + Long.parseLong(resourceId),
								Long.parseLong(resourceId), jsonObject.toString(), Long.parseLong(resourceId),
								clientId);
						this.eventActionRepository.save(eventAction);
						break;

					case EventActionConstants.ACTION_TOPUP_INVOICE_MAIL:
						eventAction = new EventAction(DateUtils.getDateOfTenant(), "SEND",
								EventActionConstants.EVENT_TOPUP_INVOICE_MAIL.toString(),
								EventActionConstants.ACTION_TOPUP_INVOICE_MAIL.toString(),
								"/billmaster/invoice/" + clientId + "/" + resourceId, Long.parseLong(resourceId),
								jsonObject.toString(), Long.parseLong(resourceId), clientId);
						this.eventActionRepository.save(eventAction);
						break;

					case EventActionConstants.ACTION_RECURRING_DISCONNECT:

						JsonObject apiRequestBodyAsJson = new JsonObject();
						apiRequestBodyAsJson.addProperty("orderId", Long.parseLong(resourceId));
						apiRequestBodyAsJson.addProperty("recurringStatus", "SUSPEND");

						final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePaypalProfileStatus()
								.withJson(apiRequestBodyAsJson.toString()).build();
						final CommandProcessingResult result = this.commandsSourceWritePlatformService
								.logCommandSource(commandRequest);

						Map<String, Object> resultMap = result.getChanges();

						JsonObject resultJson = new JsonObject();
						resultJson.addProperty("result", resultMap.get("result").toString());
						resultJson.addProperty("acknoledgement", resultMap.get("acknoledgement").toString());
						resultJson.addProperty("error", resultMap.get("error").toString());

						EventAction eventAction1 = new EventAction(DateUtils.getDateOfTenant(), "Recurring Disconnect",
								"Recurring Disconnect", EventActionConstants.ACTION_RECURRING_DISCONNECT.toString(),
								"/eventmaster", Long.parseLong(resourceId), resultJson.toString(), Long.valueOf(0),
								Long.valueOf(0));

						eventAction1.updateStatus('Y');
						this.eventActionRepository.saveAndFlush(eventAction1);

						break;

					case EventActionConstants.ACTION_RECURRING_RECONNECTION:

						JsonObject JsonString = new JsonObject();
						JsonString.addProperty("orderId", Long.parseLong(resourceId));
						JsonString.addProperty("recurringStatus", "REACTIVATE");

						final CommandWrapper commandRequestForReconn = new CommandWrapperBuilder()
								.updatePaypalProfileStatus().withJson(JsonString.toString()).build();
						final CommandProcessingResult commandResult = this.commandsSourceWritePlatformService
								.logCommandSource(commandRequestForReconn);

						Map<String, Object> resultMapObj = commandResult.getChanges();

						JsonObject resultJsonObj = new JsonObject();
						resultJsonObj.addProperty("result", resultMapObj.get("result").toString());
						resultJsonObj.addProperty("acknoledgement", resultMapObj.get("acknoledgement").toString());
						resultJsonObj.addProperty("error", resultMapObj.get("error").toString());

						EventAction eventActionObj = new EventAction(DateUtils.getDateOfTenant(),
								"Recurring Reconnection", "Recurring Reconnection",
								EventActionConstants.ACTION_RECURRING_RECONNECTION.toString(), "/eventmaster",
								Long.parseLong(resourceId), resultJsonObj.toString(), Long.valueOf(0), Long.valueOf(0));

						eventActionObj.updateStatus('Y');
						this.eventActionRepository.saveAndFlush(eventActionObj);

						break;

					case EventActionConstants.ACTION_RECURRING_TERMINATION:

						Long orderId = Long.parseLong(resourceId);

						PaypalRecurringBilling billing = this.paypalRecurringBillingRepository
								.findOneByOrderId(orderId);

						if (billing.getDeleted() == 'N') {
							JsonObject terminationObj = new JsonObject();
							terminationObj.addProperty("orderId", orderId);
							terminationObj.addProperty("recurringStatus", "CANCEL");

							final CommandWrapper terminateCommandRequest = new CommandWrapperBuilder()
									.updatePaypalProfileStatus().withJson(terminationObj.toString()).build();
							final CommandProcessingResult terminateResult = this.commandsSourceWritePlatformService
									.logCommandSource(terminateCommandRequest);

							Map<String, Object> resultMapForTerminate = terminateResult.getChanges();

							JsonObject resultJsonObject = new JsonObject();
							resultJsonObject.addProperty("result", resultMapForTerminate.get("result").toString());
							resultJsonObject.addProperty("acknoledgement",
									resultMapForTerminate.get("acknoledgement").toString());
							resultJsonObject.addProperty("error", resultMapForTerminate.get("error").toString());

							EventAction eventActionTermination = new EventAction(DateUtils.getDateOfTenant(),
									"Cancel Recurring", "Cancel Recurring Profile",
									EventActionConstants.ACTION_RECURRING_TERMINATION.toString(), "/eventmaster",
									Long.parseLong(resourceId), resultJsonObject.toString(), Long.valueOf(0),
									Long.valueOf(0));

							eventActionTermination.updateStatus('Y');
							this.eventActionRepository.saveAndFlush(eventActionTermination);
						}

						break;

					case EventActionConstants.ACTION_NOTIFY_TECHNICALTEAM:

						if (resourceString != null) {
							BillingMessageTemplate notifyTechnicalTeamMessageDetails = null;
							if (null == notifyTechnicalTeamMessageDetails) {
								notifyTechnicalTeamMessageDetails = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_TECHNICAL_TEAM);
							}

							if (notifyTechnicalTeamMessageDetails != null) {

								String subject = notifyTechnicalTeamMessageDetails.getSubject();
								String body = notifyTechnicalTeamMessageDetails.getBody();
								String footer = notifyTechnicalTeamMessageDetails.getFooter();
								String header = notifyTechnicalTeamMessageDetails.getHeader();
								billingMessage = new BillingMessage(header, body, footer, resourceString,
										resourceString, subject,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										notifyTechnicalTeamMessageDetails,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

								this.messageDataRepository.save(billingMessage);

							} else
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_SELFCARE);

							break;
						}
						/*
						 * String userName = "billing";
						 * 
						 * Configuration configValue = this.configurationRepository
						 * .findOneByName(ConfigurationConstants.CONFIG_APPUSER);
						 * 
						 * if (null != configValue && configValue.isEnabled() && configValue.getValue()
						 * != null && !configValue.getValue().isEmpty()) { userName =
						 * configValue.getValue(); }
						 * 
						 * String data = userName.replace("{", "").replace("}", "").trim();
						 * 
						 * String[] valArray = data.split(",");
						 * 
						 * template = getTemplate(BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_NOTIFY_TECHNICAL_TEAM);
						 * 
						 * bodyMessage = template.getBody().replaceAll("<ActionType>", ticketURL);
						 * bodyMessage = bodyMessage.replaceAll("<clientId>", clientId.toString());
						 * bodyMessage = bodyMessage.replaceAll("<id>", resourceId == null ? "" :
						 * resourceId);
						 * 
						 * final LocalDate messageDate = DateUtils.getLocalDateOfTenant();
						 * 
						 * for (String val : valArray) { UserChat userChat = new UserChat(val,
						 * messageDate.toDate(), bodyMessage, ConfigurationConstants.OBSUSER);
						 * this.userChatRepository.save(userChat); }
						 */

						/*
						 * case EventActionConstants.ACTION_NOTIFY_ACTIVATION :
						 * 
						 * orderData = this.eventActionReadPlatformService.retrieveNotifyDetails
						 * (clientId, new Long(resourceId));
						 * 
						 * template = getTemplate(BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_NOTIFY_ACTIVATION);
						 * 
						 * headerMessage = template.getHeader().replaceAll("<CustomerName>",
						 * orderData.getFirstName() + " " + orderData.getLastName()); bodyMessage =
						 * template.getBody().replaceAll("<Service name>", orderData.getPlanName());
						 * bodyMessage = bodyMessage.replaceAll("<Activation Date>",
						 * dateFormat.format(orderData.getActivationDate().toDate()) );
						 * 
						 * footerMessage = template.getFooter().replaceAll( "<Reseller Name>",
						 * orderData.getOfficeName()); footerMessage =
						 * footerMessage.replaceAll("<Contact Name>" , orderData.getOfficeEmail());
						 * footerMessage = footerMessage.replaceAll("<Number>",
						 * orderData.getOfficePhoneNo());
						 * 
						 * billingMessage = new BillingMessage(headerMessage, bodyMessage,
						 * footerMessage, orderData.getOfficeEmail(), orderData.getEmailId(),
						 * template.getSubject(),
						 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null );
						 * 
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * break;
						 * 
						 * case EventActionConstants.ACTION_NOTIFY_DISCONNECTION :
						 * 
						 * orderData = this.eventActionReadPlatformService.retrieveNotifyDetails
						 * (clientId, new Long(resourceId));
						 * 
						 * template = getTemplate(BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_NOTIFY_DISCONNECTION);
						 * 
						 * headerMessage = template.getHeader().replaceAll("<CustomerName>",
						 * orderData.getFirstName() + " " + orderData.getLastName()); bodyMessage =
						 * template.getBody().replaceAll("<Service name>", orderData.getPlanName());
						 * bodyMessage = bodyMessage.replaceAll("<Disconnection Date>",
						 * dateFormat.format(orderData.getEndDate().toDate()));
						 * 
						 * footerMessage = template.getFooter().replaceAll( "<Reseller Name>",
						 * orderData.getOfficeName()); footerMessage =
						 * footerMessage.replaceAll("<Contact Name>" , orderData.getOfficeEmail());
						 * footerMessage = footerMessage.replaceAll("<Number>",
						 * orderData.getOfficePhoneNo());
						 * 
						 * billingMessage = new BillingMessage(headerMessage, bodyMessage,
						 * footerMessage, orderData.getOfficeEmail(), orderData.getEmailId(),
						 * template.getSubject(),
						 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null );
						 * 
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * break;
						 * 
						 * case EventActionConstants.ACTION_NOTIFY_RECONNECTION :
						 * 
						 * orderData = this.eventActionReadPlatformService.retrieveNotifyDetails
						 * (clientId, new Long(resourceId));
						 * 
						 * template = getTemplate(BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_NOTIFY_RECONNECTION);
						 * 
						 * headerMessage = template.getHeader().replaceAll("<CustomerName>",
						 * orderData.getFirstName() + " " + orderData.getLastName()); bodyMessage =
						 * template.getBody().replaceAll("<Service name>", orderData.getPlanName());
						 * bodyMessage = bodyMessage.replaceAll("<Reconnection Date>",
						 * dateFormat.format(orderData.getStartDate().toDate()));
						 * 
						 * footerMessage = template.getFooter().replaceAll( "<Reseller Name>",
						 * orderData.getOfficeName()); footerMessage =
						 * footerMessage.replaceAll("<Contact Name>" , orderData.getOfficeEmail());
						 * footerMessage = footerMessage.replaceAll("<Number>",
						 * orderData.getOfficePhoneNo());
						 * 
						 * billingMessage = new BillingMessage(headerMessage, bodyMessage,
						 * footerMessage, orderData.getOfficeEmail(), orderData.getEmailId(),
						 * template.getSubject(),
						 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null );
						 * 
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * break;
						 * 
						 * case EventActionConstants.ACTION_NOTIFY_PAYMENT :
						 * 
						 * orderData = this.eventActionReadPlatformService.retrieveNotifyDetails
						 * (clientId, null);
						 * 
						 * template = getTemplate(BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_NOTIFY_PAYMENT);
						 * 
						 * headerMessage = template.getHeader().replaceAll("<CustomerName>",
						 * orderData.getFirstName() + " " + orderData.getLastName()); bodyMessage =
						 * template.getBody().replaceAll("<Amount>", resourceId); bodyMessage =
						 * bodyMessage.replaceAll("<Payment Date>", dateFormat.format(new Date()));
						 * 
						 * footerMessage = template.getFooter().replaceAll( "<Reseller Name>",
						 * orderData.getOfficeName()); footerMessage =
						 * footerMessage.replaceAll("<Contact Name>" , orderData.getOfficeEmail());
						 * footerMessage = footerMessage.replaceAll("<Number>",
						 * orderData.getOfficePhoneNo());
						 * 
						 * billingMessage = new BillingMessage(headerMessage, bodyMessage,
						 * footerMessage, orderData.getOfficeEmail(), orderData.getEmailId(),
						 * template.getSubject(),
						 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null );
						 * 
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * break;
						 * 
						 * case EventActionConstants.ACTION_NOTIFY_CHANGEPLAN :
						 * 
						 * orderData = this.eventActionReadPlatformService.retrieveNotifyDetails
						 * (clientId, new Long(resourceId));
						 * 
						 * template = getTemplate(BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_NOTIFY_CHANGEPLAN);
						 * 
						 * headerMessage = template.getHeader().replaceAll("<CustomerName>",
						 * orderData.getFirstName() + " " + orderData.getLastName()); bodyMessage =
						 * template.getBody().replaceAll("<Service name>", orderData.getPlanName());
						 * bodyMessage = bodyMessage.replaceAll("<Activation Date>",
						 * dateFormat.format(orderData.getActivationDate().toDate()) );
						 * 
						 * footerMessage = template.getFooter().replaceAll( "<Reseller Name>",
						 * orderData.getOfficeName()); footerMessage =
						 * footerMessage.replaceAll("<Contact Name>" , orderData.getOfficeEmail());
						 * footerMessage = footerMessage.replaceAll("<Number>",
						 * orderData.getOfficePhoneNo());
						 * 
						 * billingMessage = new BillingMessage(headerMessage, bodyMessage,
						 * footerMessage, orderData.getOfficeEmail(), orderData.getEmailId(),
						 * template.getSubject(),
						 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null );
						 * 
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * break;
						 * 
						 * case EventActionConstants.ACTION_NOTIFY_ORDER_TERMINATE :
						 * 
						 * orderData = this.eventActionReadPlatformService.retrieveNotifyDetails
						 * (clientId, new Long(resourceId));
						 * 
						 * template = getTemplate(BillingMessageTemplateConstants.
						 * MESSAGE_TEMPLATE_NOTIFY_ORDERTERMINATION);
						 * 
						 * headerMessage = template.getHeader().replaceAll("<CustomerName>",
						 * orderData.getFirstName() + " " + orderData.getLastName()); bodyMessage =
						 * template.getBody().replaceAll("<Service name>", orderData.getPlanName());
						 * bodyMessage = bodyMessage.replaceAll("<Disconnection Date>",
						 * dateFormat.format(new Date()));
						 * 
						 * footerMessage = template.getFooter().replaceAll( "<Reseller Name>",
						 * orderData.getOfficeName()); footerMessage =
						 * footerMessage.replaceAll("<Contact Name>" , orderData.getOfficeEmail());
						 * footerMessage = footerMessage.replaceAll("<Number>",
						 * orderData.getOfficePhoneNo());
						 * 
						 * billingMessage = new BillingMessage(headerMessage, bodyMessage,
						 * footerMessage, orderData.getOfficeEmail(), orderData.getEmailId(),
						 * template.getSubject(),
						 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
						 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_MESSAGE_TYPE, null );
						 * 
						 * this.messageDataRepository.save(billingMessage);
						 * 
						 * break;
						 * 
						 * default: break;
						 */
					}

					switch (detailsData.getActionName()) {

					case EventActionConstants.ACTION_NOTIFY_SMS_ACTIVATION:

						orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
								new Long(resourceId));

						template = getTemplate(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_ACTIVATION);
						
						bodyMessage = template.getBody().replaceAll("<Box Id>", orderData.getSerialNo());


						/*
						 * bodyMessage = template.getBody().replaceAll("<Service name>",
						 * orderData.getPlanName()); bodyMessage =
						 * bodyMessage.replaceAll("<Activation Date>",
						 * dateFormat.format(orderData.getActivationDate().toDate()));
						 */

						billingMessage = new BillingMessage(null, bodyMessage, null, orderData.getOfficeEmail(),
								orderData.getClientPhone(), template.getSubject(),
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

						this.messageDataRepository.save(billingMessage);

						break;

					case EventActionConstants.ACTION_NOTIFY_SMS_DISCONNECTION:

						orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
								new Long(resourceId));

						template = getTemplate(
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_DISCONNECTION);

						bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
						bodyMessage = bodyMessage.replaceAll("<Disconnection Date>",
								dateFormat.format(orderData.getEndDate().toDate()));

						billingMessage = new BillingMessage(null, bodyMessage, null, orderData.getOfficeEmail(),
								orderData.getClientPhone(), template.getSubject(),
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

						this.messageDataRepository.save(billingMessage);

						break;

					case EventActionConstants.ACTION_NOTIFY_SMS_RECONNECTION:

						orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId,
								new Long(resourceId));

						template = getTemplate(
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_RECONNECTION);

						bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
						bodyMessage = bodyMessage.replaceAll("<Reconnection Date>",
								dateFormat.format(orderData.getStartDate().toDate()));

						billingMessage = new BillingMessage(null, bodyMessage, null, orderData.getOfficeEmail(),
								orderData.getClientPhone(), template.getSubject(),
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

						this.messageDataRepository.save(billingMessage);

						break;

					case EventActionConstants.ACTION_NOTIFY_SMS_PAYMENT:

						orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId, null);

						template = getTemplate(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT);

						bodyMessage = template.getBody().replaceAll("<Amount>", resourceId);
						bodyMessage = bodyMessage.replaceAll("<Payment Date>", dateFormat.format(new Date()));

						billingMessage = new BillingMessage(null, bodyMessage, null, orderData.getOfficeEmail(),
								orderData.getClientPhone(), template.getSubject(),
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

						this.messageDataRepository.save(billingMessage);

						break;

					// Notifications for Payments Adj
					case EventActionConstants.ACTION_NOTIFY_SMS_PAYMENT_ADJ:

						orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId, null);

						template = getTemplate(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_SMS_PAYMENT_ADJ);

						bodyMessage = template.getBody().replaceAll("<Amount>", resourceId);
						bodyMessage = bodyMessage.replaceAll("<Payment Date>", dateFormat.format(new Date()));

						billingMessage = new BillingMessage(null, bodyMessage, null, orderData.getOfficeEmail(),
								orderData.getClientPhone(), template.getSubject(),
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

						this.messageDataRepository.save(billingMessage);

						break;

					// Notifications for Payments Reversal
					case EventActionConstants.ACTION_NOTIFY_SMS_PAYMENT_REVERSAL:

						orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId, null);

						template = getTemplate(
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_SMS_PAYMENT_REVERSAL);

						bodyMessage = template.getBody().replaceAll("<Amount>", resourceId);
						bodyMessage = bodyMessage.replaceAll("<Payment Date>", dateFormat.format(new Date()));

						billingMessage = new BillingMessage(null, bodyMessage, null, orderData.getOfficeEmail(),
								orderData.getClientPhone(), template.getSubject(),
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

						this.messageDataRepository.save(billingMessage);

						break;
					/*
					 * case EventActionConstants.ACTION_NOTIFY_SMS_CHANGEPLAN :
					 * 
					 * orderData = this.eventActionReadPlatformService.retrieveNotifyDetails
					 * (clientId, null);
					 * 
					 * template = getTemplate(BillingMessageTemplateConstants.
					 * MESSAGE_TEMPLATE_SMS_NOTIFY_CHANGEPLAN);
					 * 
					 * bodyMessage = template.getBody().replaceAll( "<Service name>",
					 * orderData.getPlanName()); bodyMessage =
					 * bodyMessage.replaceAll("<Activation Date>",
					 * dateFormat.format(orderData.getActivationDate().toDate()) );
					 * 
					 * billingMessage = new BillingMessage(null, bodyMessage, null,
					 * orderData.getOfficeEmail(), orderData.getClientPhone(),
					 * template.getSubject(),
					 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
					 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_SMS_TYPE, null );
					 * 
					 * this.messageDataRepository.save(billingMessage);
					 * 
					 * break;
					 */

					case EventActionConstants.ACTION_NOTIFY_SMS_ORDER_TERMINATE:

						orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(clientId, null);

						template = getTemplate(
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_ORDERTERMINATION);

						bodyMessage = template.getBody().replaceAll("<Service name>", orderData.getPlanName());
						bodyMessage = bodyMessage.replaceAll("<Disconnection Date>", dateFormat.format(new Date()));

						billingMessage = new BillingMessage(null, bodyMessage, null, orderData.getOfficeEmail(),
								orderData.getClientPhone(), template.getSubject(),
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, template,
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

						this.messageDataRepository.save(billingMessage);

						break;

					/*
					 * case EventActionConstants.ACTION_SEND_MESSAGE : BillingMessageTemplate
					 * createClientMessageDetailsForSMS= null; if(null ==
					 * createClientMessageDetailsForSMS){ createClientMessageDetailsForSMS =
					 * this.billingMessageTemplateRepository.
					 * findByTemplateDescription(BillingMessageTemplateConstants
					 * .MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE); }
					 * 
					 * 
					 * if (createClientMessageDetailsForSMS != null) {
					 * 
					 * String subject = createClientMessageDetailsForSMS.getSubject(); String body =
					 * createClientMessageDetailsForSMS.getBody(); body = body.replace("<PARAM1>",
					 * clientData.getUserName().trim()); body = body.replace("<PARAM2>",
					 * clientData.getClientPassword().trim());
					 * 
					 * billingMessage = new BillingMessage(null, body, null,
					 * clientData.getOfficeMail(), clientData.getPhone(), subject,
					 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
					 * createClientMessageDetailsForSMS, BillingMessageTemplateConstants.
					 * MESSAGE_TEMPLATE_SMS_TYPE, null);
					 * 
					 * this.messageDataRepository.save(billingMessage);
					 * 
					 * } else throw new BillingMessageTemplateNotFoundException(
					 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE);
					 * 
					 * break;
					 */

					/*
					 * case EventActionConstants.ACTION_SEND_LEAD_SMS : BillingMessageTemplate
					 * createleadMessageDetailsForSMS= null; if(null ==
					 * createleadMessageDetailsForSMS){ createleadMessageDetailsForSMS =
					 * this.billingMessageTemplateRepository.
					 * findByTemplateDescription(BillingMessageTemplateConstants
					 * .MESSAGE_TEMPLATE_SMS_CREATE_LEAD); }
					 * 
					 * 
					 * if (createleadMessageDetailsForSMS != null) {
					 * 
					 * 
					 * String subject = createleadMessageDetailsForSMS.getSubject(); String body =
					 * createleadMessageDetailsForSMS.getBody(); String footer =
					 * createleadMessageDetailsForSMS.getFooter(); String header =
					 * createleadMessageDetailsForSMS.getHeader().replace( "<PARAM1>",
					 * clientProspectData.getFullName()+ ","); body = body.replace("<PARAM1>",
					 * clientProspectData.getFullName()); body = body.replace("<PARAM2>",
					 * clientProspectData.getId().toString()); body = body.replace("<PARAM3>",
					 * clientProspectData.getCreatedDate().toString());
					 * 
					 * billingMessage = new BillingMessage(header, body, footer,
					 * clientProspectData.getOfficeMail(), clientProspectData.getEmail(), subject,
					 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
					 * createleadMessageDetailsForSMS, BillingMessageTemplateConstants.
					 * MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
					 * 
					 * 
					 * this.messageDataRepository.save(billingMessage);
					 * 
					 * } else throw new BillingMessageTemplateNotFoundException(
					 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE);
					 * 
					 * break;
					 * 
					 * case EventActionConstants.ACTION_SEND_FOLLOWUP_SMS : BillingMessageTemplate
					 * followupleadMessageDetailsForSMS= null; if(null ==
					 * followupleadMessageDetailsForSMS){ followupleadMessageDetailsForSMS =
					 * this.billingMessageTemplateRepository.
					 * findByTemplateDescription(BillingMessageTemplateConstants
					 * .MESSAGE_TEMPLATE_SMS_FOLLOWUP_LEAD); }
					 * 
					 * 
					 * if (followupleadMessageDetailsForSMS != null) {
					 * 
					 * 
					 * String subject = followupleadMessageDetailsForSMS.getSubject(); String body =
					 * followupleadMessageDetailsForSMS.getBody(); String footer =
					 * followupleadMessageDetailsForSMS.getFooter(); String header =
					 * followupleadMessageDetailsForSMS.getHeader(); body = body.replace("<PARAM1>",
					 * clientProspectData.getFullName()); body = body.replace("<PARAM2>",
					 * clientProspectData.getId().toString()); body = body.replace("<PARAM3>",
					 * clientProspectData.getStatus()); body = body.replace("<PARAM4>",
					 * clientProspectData.getPreferredCallingTime().toString());
					 * 
					 * billingMessage = new BillingMessage(header, body, footer,
					 * clientProspectData.getOfficeMail(), clientProspectData.getEmail(), subject,
					 * BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
					 * followupleadMessageDetailsForSMS, BillingMessageTemplateConstants.
					 * MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
					 * 
					 * 
					 * this.messageDataRepository.save(billingMessage);
					 * 
					 * } else throw new BillingMessageTemplateNotFoundException(
					 * BillingMessageTemplateConstants. MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE);
					 * 
					 * break;
					 */

					default:
						break;
					}

					// Configuration configuration =
					// this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_SMS);

				}
			}
			return null;
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}

	}

	private BillingMessageTemplate getTemplate(String templateName) {

		if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_ACTIVATION.equalsIgnoreCase(templateName)) {

			if (null == activationTemplates) {
				activationTemplates = this.messageTemplateRepository
						.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_ACTIVATION);
			}
			return activationTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_DISCONNECTION
				.equalsIgnoreCase(templateName)) {

			if (null == disConnectionTemplates) {
				disConnectionTemplates = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_DISCONNECTION);
			}
			return disConnectionTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_RECONNECTION
				.equalsIgnoreCase(templateName)) {

			if (null == reConnectionTemplates) {
				reConnectionTemplates = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_RECONNECTION);
			}
			return reConnectionTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT.equalsIgnoreCase(templateName)) {

			if (null == paymentTemplates) {
				paymentTemplates = this.messageTemplateRepository
						.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT);
			}
			return paymentTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_CHANGEPLAN.equalsIgnoreCase(templateName)) {

			if (null == changePlanTemplates) {
				changePlanTemplates = this.messageTemplateRepository
						.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_CHANGEPLAN);
			}
			return changePlanTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_ORDERTERMINATION
				.equalsIgnoreCase(templateName)) {

			if (null == orderTerminationTemplates) {
				orderTerminationTemplates = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_ORDERTERMINATION);
			}
			return orderTerminationTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_ACTIVATION
				.equalsIgnoreCase(templateName)) {

			if (null == smsActivationTemplates) {
				smsActivationTemplates = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_ACTIVATION);
			}
			return smsActivationTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_DISCONNECTION
				.equalsIgnoreCase(templateName)) {

			if (null == smsDisconnectionTemplates) {
				smsDisconnectionTemplates = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_DISCONNECTION);
			}
			return smsDisconnectionTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_RECONNECTION
				.equalsIgnoreCase(templateName)) {

			if (null == smsReConnectionTemplates) {
				smsReConnectionTemplates = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_RECONNECTION);
			}
			return smsReConnectionTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_PAYMENT.equalsIgnoreCase(templateName)) {

			if (null == smsPaymentTemplates) {
				smsPaymentTemplates = this.messageTemplateRepository
						.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_PAYMENT);
			}
			return smsPaymentTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_CHANGEPLAN
				.equalsIgnoreCase(templateName)) {

			if (null == smsChangePlanTemplates) {
				smsChangePlanTemplates = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_CHANGEPLAN);
			}
			return smsChangePlanTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_ORDERTERMINATION
				.equalsIgnoreCase(templateName)) {

			if (null == smsOrderTerminationTemplates) {
				smsOrderTerminationTemplates = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NOTIFY_ORDERTERMINATION);
			}
			return smsOrderTerminationTemplates;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_TECHNICAL_TEAM
				.equalsIgnoreCase(templateName)) {
			if (null == notifyTechicalTeam) {
				notifyTechicalTeam = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_TECHNICAL_TEAM);
			}
			return notifyTechicalTeam;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SERVICE_SUSPEND.equalsIgnoreCase(templateName)) {

			if (null == suspendService) {
				suspendService = this.messageTemplateRepository
						.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SERVICE_SUSPEND);
			}
			return suspendService;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SERVICE_TERMINATION
				.equalsIgnoreCase(templateName)) {

			if (null == terminateService) {
				terminateService = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SERVICE_TERMINATION);
			}
			return terminateService;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT_ADJ.equalsIgnoreCase(templateName)) {

			if (null == paymentAdj) {
				paymentAdj = this.messageTemplateRepository
						.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT_ADJ);
			}
			return paymentAdj;
			// MESSAGE_TEMPLATE_NOTIFY_PAYMENT_REVERSAL
		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT_REVERSAL
				.equalsIgnoreCase(templateName)) {

			if (null == paymentReversal) {
				paymentReversal = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_PAYMENT_REVERSAL);
			}
			return paymentReversal;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_SMS_PAYMENT_ADJ
				.equalsIgnoreCase(templateName)) {

			if (null == paymentAdjSms) {
				paymentAdjSms = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_SMS_PAYMENT_ADJ);
			}
			return paymentAdjSms;

		} else if (BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_SMS_PAYMENT_REVERSAL
				.equalsIgnoreCase(templateName)) {

			if (null == paymentReversalSms) {
				paymentReversalSms = this.messageTemplateRepository.findByTemplateDescription(
						BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_SMS_PAYMENT_REVERSAL);
			}
			return paymentReversalSms;

		}

		else {
			throw new BillingMessageTemplateNotFoundException(templateName);
		}

	}

	static RestTemplate restTemplate = new RestTemplate();

	public static void sendMessage(String moblieNo, String Message) {

		String url = "https://secure.xwireless.net/api/v2/SendSMS?SenderId=FREETV&Is_Unicode=false&Is_Flash=false"
				+ "&Message=" + Message + "&MobileNumbers=" + moblieNo
				+ "&ApiKey=U3WEY4V9D3YBwxUOc5YmG6zFG9Kzv08cH5DsioX9coA=&ClientId=e4048eee-d3f6-44bb-bed8-db625ceff36c";
		System.out.println(url);

		ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		System.out.println(result);

	}

	public static void main(String args[]) {
		sendMessage("2348035537154", "You BOX has been activated 223480355371");
	}

}
