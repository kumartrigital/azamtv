package org.mifosplatform.scheduledjobs.scheduledjobs.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.cms.eventmaster.domain.EventMaster;
import org.mifosplatform.cms.eventmaster.domain.EventMasterRepository;
import org.mifosplatform.cms.eventmaster.service.EventMasterWritePlatformService;
import org.mifosplatform.cms.eventprice.domain.EventPrice;
import org.mifosplatform.cms.eventprice.domain.EventPriceRepository;
import org.mifosplatform.cms.eventprice.service.EventPriceWritePlatformService;
import org.mifosplatform.cms.inview.data.Catalogue;
import org.mifosplatform.cms.inview.data.InviewCatalogue;
import org.mifosplatform.cms.ipaywizard.data.Item;
import org.mifosplatform.cms.ipaywizard.data.PaywizardCatalogue;
import org.mifosplatform.cms.media.domain.MediaAsset;
import org.mifosplatform.cms.media.service.MediaAssetWritePlatformService;
import org.mifosplatform.cms.mediadetails.domain.MediaAssetRepository;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.crm.ticketmaster.api.TicketMasterApiResource;
import org.mifosplatform.crm.ticketmaster.data.ClientTicketData;
import org.mifosplatform.crm.ticketmaster.domain.OfficeTicket;
import org.mifosplatform.crm.ticketmaster.domain.OfficeTicketRepository;
import org.mifosplatform.crm.ticketmaster.service.TicketMasterReadPlatformService;
import org.mifosplatform.finance.billingmaster.api.BillingMasterApiResourse;
import org.mifosplatform.finance.chargeorder.api.ChargingOrderApiResourse;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.finance.chargeorder.service.ChargingCustomerOrders;
import org.mifosplatform.finance.paymentsgateway.data.PaymentGatewayData;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGateway;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayRepository;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayReadPlatformService;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayWritePlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.dataqueries.service.ReadReportingService;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.jobs.service.RadiusJobConstants;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.lco.service.LCOWritePlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.message.data.BillingMessageDataForProcessing;
import org.mifosplatform.organisation.message.domain.BillingMessage;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateConstants;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.exception.BillingMessageTemplateNotFoundException;
import org.mifosplatform.organisation.message.service.BillingMessageDataWritePlatformService;
import org.mifosplatform.organisation.message.service.BillingMesssageReadPlatformService;
import org.mifosplatform.organisation.message.service.MessagePlatformEmailService;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.service.OrderAddOnsWritePlatformService;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.provisioning.entitlements.data.ClientEntitlementData;
import org.mifosplatform.provisioning.entitlements.data.EntitlementsData;
import org.mifosplatform.provisioning.entitlements.service.EntitlementReadPlatformService;
import org.mifosplatform.provisioning.entitlements.service.EntitlementWritePlatformService;
import org.mifosplatform.provisioning.preparerequest.data.PrepareRequestData;
import org.mifosplatform.provisioning.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestReadplatformService;
import org.mifosplatform.provisioning.processrequest.service.ProcessRequestWriteplatformService;
import org.mifosplatform.provisioning.processscheduledjobs.service.SheduleJobReadPlatformService;
import org.mifosplatform.provisioning.processscheduledjobs.service.SheduleJobWritePlatformService;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestRepository;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.ScheduleJobData;
import org.mifosplatform.workflow.eventaction.domain.EventAction;
import org.mifosplatform.workflow.eventaction.domain.EventActionRepository;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ProcessEventActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

@Service
public class SheduleJobWritePlatformServiceImpl implements SheduleJobWritePlatformService {

	private final SheduleJobReadPlatformService sheduleJobReadPlatformService;
	private final ChargingCustomerOrders invoiceClient;
	private final BillingMasterApiResourse billingMasterApiResourse;
	private final FromJsonHelper fromApiJsonHelper;
	private final OrderReadPlatformService orderReadPlatformService;
	private final BillingMessageDataWritePlatformService billingMessageDataWritePlatformService;
	private final PrepareRequestReadplatformService prepareRequestReadplatformService;
	private final ProcessRequestReadplatformService processRequestReadplatformService;
	private final ProcessRequestWriteplatformService processRequestWriteplatformService;
	private final ProcessRequestRepository processRequestRepository;
	private final BillingMesssageReadPlatformService billingMesssageReadPlatformService;
	private final MessagePlatformEmailService messagePlatformEmailService;
	private final EntitlementReadPlatformService entitlementReadPlatformService;
	private final EntitlementWritePlatformService entitlementWritePlatformService;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	private final ProcessEventActionService actiondetailsWritePlatformService;
	private final ScheduleJob scheduleJob;
	private final ReadReportingService readExtraDataAndReportingService;
	private final TicketMasterApiResource ticketMasterApiResource;
	private final TicketMasterReadPlatformService ticketMasterReadPlatformService;
	private final OrderRepository orderRepository;
	private final MCodeReadPlatformService codeReadPlatformService;
	private final JdbcTemplate jdbcTemplate;
	private final OrderAddOnsWritePlatformService addOnsWritePlatformService;
	private String ReceiveMessage;
	private final PaymentGatewayRepository paymentGatewayRepository;
	private final PaymentGatewayWritePlatformService paymentGatewayWritePlatformService;
	private final EventActionRepository eventActionRepository;
	private final PaymentGatewayReadPlatformService paymentGatewayReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	private final EventActionReadPlatformService eventActionReadPlatformService;
	private final ProvisioningRequestRepository provisioningRequestRepository;
	private final CrmServices crmServices;
	private final PlanRepository planRepository;
	private final BillingMessageTemplateRepository billingMessageTemplateRepository;
	private final BillingMessageRepository billingMessageRepository;
	private final OfficeTicketRepository officeTicketRepository;
	private final MediaAssetRepository assetRepository;
	private final MediaAssetWritePlatformService mediaAssetWritePlatformService;
	private final EventMasterWritePlatformService eventMasterWritePlatformService;
	private final EventPriceWritePlatformService eventPriceWritePlatformService;
	private final EventMasterRepository eventMasterRepository;
	private final EventPriceRepository eventPricingRepository;
	private final LCOWritePlatformService lCOWritePlatformService;
	private final ChargingOrderApiResourse chargingOrderApiResourse;

	@Autowired
	public SheduleJobWritePlatformServiceImpl(final ChargingCustomerOrders invoiceClient,
			final FromJsonHelper fromApiJsonHelper, final BillingMasterApiResourse billingMasterApiResourse,
			final ProcessRequestRepository processRequestRepository,
			final SheduleJobReadPlatformService sheduleJobReadPlatformService,
			final OrderReadPlatformService orderReadPlatformService,
			final BillingMessageDataWritePlatformService billingMessageDataWritePlatformService,
			final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final ProcessEventActionService actiondetailsWritePlatformService,
			final PrepareRequestReadplatformService prepareRequestReadplatformService,
			final ProcessRequestReadplatformService processRequestReadplatformService,
			final ProcessRequestWriteplatformService processRequestWriteplatformService,
			final BillingMesssageReadPlatformService billingMesssageReadPlatformService,
			final MessagePlatformEmailService messagePlatformEmailService, final ScheduleJob scheduleJob,
			final EntitlementReadPlatformService entitlementReadPlatformService,
			final EntitlementWritePlatformService entitlementWritePlatformService,
			final ReadReportingService readExtraDataAndReportingService, final OrderRepository orderRepository,
			final TicketMasterApiResource ticketMasterApiResource,
			final TicketMasterReadPlatformService ticketMasterReadPlatformService,
			final MCodeReadPlatformService codeReadPlatformService, final TenantAwareRoutingDataSource dataSource,
			final PaymentGatewayRepository paymentGatewayRepository,
			final PaymentGatewayWritePlatformService paymentGatewayWritePlatformService,
			final EventActionRepository eventActionRepository,
			final PaymentGatewayReadPlatformService paymentGatewayReadPlatformService,
			final ConfigurationRepository configurationRepository,
			final EventActionReadPlatformService eventActionReadPlatformService,
			final OrderAddOnsWritePlatformService addOnsWritePlatformService,
			final ProvisioningRequestRepository provisioningRequestRepository, final CrmServices crmServices,
			final PlanRepository planRepository,
			final BillingMessageTemplateRepository billingMessageTemplateRepository,
			final BillingMessageRepository billingMessageRepository,
			final OfficeTicketRepository officeTicketRepository, final MediaAssetRepository assetRepository,
			final MediaAssetWritePlatformService mediaAssetWritePlatformService,
			final EventMasterWritePlatformService eventMasterWritePlatformService,
			final EventPriceWritePlatformService eventPriceWritePlatformService, final PlatformSecurityContext context,
			final EventMasterRepository eventMasterRepository, final EventPriceRepository eventPricingRepository,
			final LCOWritePlatformService lCOWritePlatformService,
			final ChargingOrderApiResourse chargingOrderApiResourse) {

		this.sheduleJobReadPlatformService = sheduleJobReadPlatformService;
		this.invoiceClient = invoiceClient;
		this.billingMasterApiResourse = billingMasterApiResourse;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.orderReadPlatformService = orderReadPlatformService;
		this.billingMessageDataWritePlatformService = billingMessageDataWritePlatformService;
		this.prepareRequestReadplatformService = prepareRequestReadplatformService;
		this.processRequestReadplatformService = processRequestReadplatformService;
		this.processRequestWriteplatformService = processRequestWriteplatformService;
		this.processRequestRepository = processRequestRepository;
		this.billingMesssageReadPlatformService = billingMesssageReadPlatformService;
		this.messagePlatformEmailService = messagePlatformEmailService;
		this.entitlementReadPlatformService = entitlementReadPlatformService;
		this.addOnsWritePlatformService = addOnsWritePlatformService;
		this.entitlementWritePlatformService = entitlementWritePlatformService;
		this.actionDetailsReadPlatformService = actionDetailsReadPlatformService;
		this.actiondetailsWritePlatformService = actiondetailsWritePlatformService;
		this.scheduleJob = scheduleJob;
		this.orderRepository = orderRepository;
		this.readExtraDataAndReportingService = readExtraDataAndReportingService;
		this.ticketMasterApiResource = ticketMasterApiResource;
		this.ticketMasterReadPlatformService = ticketMasterReadPlatformService;
		this.codeReadPlatformService = codeReadPlatformService;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.paymentGatewayRepository = paymentGatewayRepository;
		this.paymentGatewayWritePlatformService = paymentGatewayWritePlatformService;
		this.eventActionRepository = eventActionRepository;
		this.paymentGatewayReadPlatformService = paymentGatewayReadPlatformService;
		this.configurationRepository = configurationRepository;
		this.eventActionReadPlatformService = eventActionReadPlatformService;
		this.provisioningRequestRepository = provisioningRequestRepository;
		this.crmServices = crmServices;
		this.planRepository = planRepository;
		this.billingMessageTemplateRepository = billingMessageTemplateRepository;
		this.billingMessageRepository = billingMessageRepository;
		this.officeTicketRepository = officeTicketRepository;
		this.assetRepository = assetRepository;
		this.mediaAssetWritePlatformService = mediaAssetWritePlatformService;
		this.eventMasterWritePlatformService = eventMasterWritePlatformService;
		this.eventPriceWritePlatformService = eventPriceWritePlatformService;
		this.eventMasterRepository = eventMasterRepository;
		this.eventPricingRepository = eventPricingRepository;
		this.lCOWritePlatformService = lCOWritePlatformService;
		this.chargingOrderApiResourse = chargingOrderApiResourse;
	}

	@Override
	@CronTarget(jobName = JobName.CHARGE)
	public void processInvoice() {
		System.out.println("charging scheduler started");
		try {
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.CHARGE.toString());
			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.CHARGE.toString() + File.separator
						+ "Invoice_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
						+ ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobParameterDetails(data.getBatchName());

				if (!sheduleDatas.isEmpty()) {
					for (ScheduleJobData scheduleJobData : sheduleDatas) {
						fw.append("ScheduleJobData id= " + scheduleJobData.getId() + " ,BatchName= "
								+ scheduleJobData.getBatchName() + " ,query=" + scheduleJobData.getQuery() + "\r\n");
						List<Long> clientIds = this.sheduleJobReadPlatformService
								.getClientIds(scheduleJobData.getQuery(), data);
						if (!clientIds.isEmpty()) {
							fw.append("Charging the customer services..... \r\n");
							for (Long clientId : clientIds) {
								try {
									if ("Y".equalsIgnoreCase(data.isDynamic())) {
										List<BillItem> invoiceList = this.invoiceClient.invoicingSingleClient(clientId,
												DateUtils.getLocalDateOfTenant());
										for (BillItem invoice : invoiceList) {
											fw.append("ClientId: " + clientId + "\tAmount: "
													+ invoice.getInvoiceAmount().toString() + "\r\n");
										}

									} else {
										List<BillItem> invoiceList = this.invoiceClient.invoicingSingleClient(clientId,
												data.getProcessDate());
										for (BillItem invoice : invoiceList) {
											fw.append("ClientId: " + clientId + "\tAmount: "
													+ invoice.getInvoiceAmount().toString() + "\r\n");
										}
									}
								} catch (Exception dve) {
									handleCodeDataIntegrityIssues(null, dve);
								}
							}
						} else {
							fw.append("Clients are not found \r\n");
						}
					}
				} else {
					fw.append("ScheduleJobData Empty \r\n");
				}
				fw.append("Charges are Generated....." + ThreadLocalContextUtil.getTenant().getTenantIdentifier()
						+ "\r\n");
				fw.flush();
				fw.close();
			}
			System.out.println("Charges are Generated....." + ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (DataIntegrityViolationException exception) {
			exception.printStackTrace();
		} catch (Exception exception) {

			exception.printStackTrace();
		}
	}

	private void handleCodeDataIntegrityIssues(Object object, Exception dve) {

	}

	@Override
	@CronTarget(jobName = JobName.REQUESTOR)
	public void processRequest() {

		try {
			System.out.println("Processing Request Details.......");
			List<PrepareRequestData> data = this.prepareRequestReadplatformService.retrieveDataForProcessing();

			if (!data.isEmpty()) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.REQUESTOR.toString() + File.separator
						+ "Requester_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
						+ ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing Request Details....... \r\n");

				for (PrepareRequestData requestData : data) {

					fw.append("Prepare Request id=" + requestData.getRequestId() + " ,clientId="
							+ requestData.getClientId() + " ,orderId=" + requestData.getOrderId() + " ,HardwareId="
							+ requestData.getHardwareId() + " ,planName=" + requestData.getPlanName()
							+ " ,Provisiong system=" + requestData.getProvisioningSystem() + "\r\n");

					this.prepareRequestReadplatformService.processingClientDetails(requestData);
				}

				fw.append(" Requestor Job is Completed...." + ThreadLocalContextUtil.getTenant().getTenantIdentifier()
						+ "\r\n");
				fw.flush();
				fw.close();
			}

			System.out.println(
					" Requestor Job is Completed...." + ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public List<Catalogue> getInviewCatelogue() {
		System.out.println("getInviewCatelogue()");
		String CatelogueAPIUrl = "https://api.inview.ng/vod/catalogue";
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Inview-Auth", "6HL0QHjj5BGYyd1m/t6g5g==");
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		ResponseEntity<InviewCatalogue> response = restTemplate.exchange(CatelogueAPIUrl, HttpMethod.GET, entity,
				InviewCatalogue.class);

		List<Catalogue> inviewesult = response.getBody().getCatalogue();
		return inviewesult;
	}

	public List<Item> getPaywizardCatelogue() {
		System.out.println("getPaywizardCatelogue()");
		String paywizardCatelogueAPIUrl = "https://nbc.paywizard.com/apis/catalogues/v2/103/items/active?currencies=NGN";
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<PaywizardCatalogue> response = restTemplate.exchange(paywizardCatelogueAPIUrl, HttpMethod.GET,
				entity, PaywizardCatalogue.class);

		List<Item> paywizardResult = response.getBody().getItems().getItem();
		return paywizardResult;

	}

	@Override
	@CronTarget(jobName = JobName.CATALOGUE)
	public void processCatalogue() {
		try {

			System.out.println("Processing INview catalogue Details.......");
			List<Catalogue> inviewCatelogueResponse = this.getInviewCatelogue();
			List<Item> paywizardrespo = getPaywizardCatelogue();

			HashMap<String, Item> paywizardcat = new HashMap<String, Item>();

			for (Item paywizardCatalogue : paywizardrespo) {
				paywizardcat.put(paywizardCatalogue.getName(), paywizardCatalogue);
			}

			for (Catalogue inviewCat : inviewCatelogueResponse) {

				MediaAsset mediaDetails = assetRepository.findOneByTitle(inviewCat.getMetadata().getTitle());

				if (mediaDetails == null) {

					JSONObject mediaLocations = new JSONObject();
					mediaLocations.put("languageId", "57");
					mediaLocations.put("formatType", "SD");
					mediaLocations.put("location", "paywizard");
					org.json.simple.JSONArray mediaAssetLocations = new org.json.simple.JSONArray();
					mediaAssetLocations.add(mediaLocations);

					JSONObject mediAttributes = new JSONObject();
					mediAttributes.put("attributeType", "Cast");
					mediAttributes.put("attributeName", "34");
					mediAttributes.put("attributevalue", "Venkat");
					mediAttributes.put("attributeNickname", "Ven");
					mediAttributes.put("attributeImage", "V.img");
					org.json.simple.JSONArray mediaassetAttributes = new org.json.simple.JSONArray();
					mediaassetAttributes.add(mediAttributes);

					JSONObject assestJson = new JSONObject();
					assestJson.put("catageoryId", "18");
					assestJson.put("status", "ACTIVE");
					assestJson.put("mediatype", "M");
					assestJson.put("contentProvider", "247");
					assestJson.put("mediaTitle", inviewCat.getMetadata().getTitle());
					assestJson.put("subject", inviewCat.getMetadata().getTitle());
					assestJson.put("duration", "3");
					assestJson.put("overview", inviewCat.getMetadata().getTvod().getOffer_id());
					assestJson.put("locale", "en");
					assestJson.put("dateFormat", "dd MMMM yyyy");
					assestJson.put("releaseDate", "11 May 2020");
					assestJson.put("mediaTypeCheck", "ADVANCEMEDIA");
					assestJson.put("mediaAssetLocations", mediaAssetLocations);
					assestJson.put("mediaassetAttributes", mediaassetAttributes);
					mediaAssetLocations.clear();
					mediaAssetLocations.clear();

					System.out.println("creating media item");
					final JsonElement assestJsonElement = fromApiJsonHelper.parse(assestJson.toString());

					JsonCommand createMediaAssetCommand = new JsonCommand(null, assestJsonElement.toString(),
							assestJsonElement, fromApiJsonHelper, null, null, null, null, null, null, null, null, null,
							null, null, null);
					mediaAssetWritePlatformService.createMediaAsset(createMediaAssetCommand);

					MediaAsset mediaAssest = assetRepository.findOneByTitle(inviewCat.getMetadata().getTitle());

					Long mediaId = mediaAssest.getId();

					List<String> mediaData = new ArrayList<String>();
					mediaData.add(mediaId.toString());

					JSONObject eventmaster = new JSONObject();
					eventmaster.put("status", 1);
					eventmaster.put("eventDescription", inviewCat.getMetadata().getTitle());
					eventmaster.put("eventName", inviewCat.getMetadata().getTvod().getOffer_id());
					eventmaster.put("eventCategory", "VOD");
					eventmaster.put("chargeCode", "OTC");
					eventmaster.put("locale", "en");
					eventmaster.put("dateFormat", "dd MMMM yyyy");
					eventmaster.put("eventStartDate", "01 May 2020");
					eventmaster.put("eventEndDate", "06 June 2050");
					eventmaster.put("eventValidity", "03 May 2020");
					eventmaster.put("mediaData", mediaData);
					mediaData.clear();

					System.out.println("creating event master");

					// eventMasterApiResource.createEventMaster(eventmaster.toString());
					final JsonElement eventmasterElement = fromApiJsonHelper.parse(eventmaster.toString());
					JsonCommand createEventMasterCommand = new JsonCommand(null, eventmasterElement.toString(),
							eventmasterElement, fromApiJsonHelper, null, null, null, null, null, null, null, null, null,
							null, null, null);
					eventMasterWritePlatformService.createEventMaster(createEventMasterCommand);

				}

				Item paywizardItem = paywizardcat.get(inviewCat.getMetadata().getTitle());

				if (paywizardItem == null) {
					System.out.println("movie not found in paywizard" + inviewCat.getMetadata().getTitle());
				}

				else if (inviewCat.getMetadata().getTitle().equals(paywizardItem.getName())) {

					MediaAsset mediaDetailBYTitle = assetRepository.findOneByTitle(inviewCat.getMetadata().getTitle());

					JSONObject mediaLocations = new JSONObject();
					mediaLocations.put("languageId", "57");
					mediaLocations.put("formatType", "SD");
					mediaLocations.put("location", paywizardItem.getId());
					org.json.simple.JSONArray mediaAssetLocations = new org.json.simple.JSONArray();
					mediaAssetLocations.add(mediaLocations);

					JSONObject mediAttributes = new JSONObject();
					mediAttributes.put("attributeType", "Cast");
					mediAttributes.put("attributeName", "34");
					mediAttributes.put("attributevalue", "Venkat");
					mediAttributes.put("attributeNickname", "Ven");
					mediAttributes.put("attributeImage", "V.img");
					org.json.simple.JSONArray mediaassetAttributes = new org.json.simple.JSONArray();
					mediaassetAttributes.add(mediAttributes);

					JSONObject assestUpdatingData = new JSONObject();
					System.out.println(
							"SheduleJobWritePlatformServiceImpl.processCatalogue()" + mediaDetailBYTitle.toString());
					assestUpdatingData.put("mediaTitle", mediaDetailBYTitle.getTitle());
					assestUpdatingData.put("status", "ACTIVE");
					assestUpdatingData.put("releaseDate", "11 May 2020");
					assestUpdatingData.put("catageoryId", "18");
					assestUpdatingData.put("contentProvider", "247");
					assestUpdatingData.put("ratingCount", "0");
					assestUpdatingData.put("cpShareValue", "3");
					assestUpdatingData.put("mediatype", "null");
					assestUpdatingData.put("location", paywizardItem.getId());
					assestUpdatingData.put("dateFormat", "dd MMMM yyyy");
					assestUpdatingData.put("locale", "en");
					assestUpdatingData.put("mediaTypeCheck", "EDITMEDIA");
					assestUpdatingData.put("formatType", "SD");
					assestUpdatingData.put("languageId", "English");
					assestUpdatingData.put("mediaAssetLocations", mediaAssetLocations);
					assestUpdatingData.put("mediaassetAttributes", mediaassetAttributes);
					mediaAssetLocations.clear();
					mediaassetAttributes.clear();

					// mediaAssestApiResource.updateMediaAssetData(mediaDetails.getId(),
					// assestUpdatingData.toString());

					System.out.println("updating  media item with paywizard");

					final JsonElement updateMediaElement = fromApiJsonHelper.parse(assestUpdatingData.toString());

					JsonCommand updateMediaAssetCommand = new JsonCommand(null, updateMediaElement.toString(),
							updateMediaElement, fromApiJsonHelper, null, mediaDetailBYTitle.getId(), null, null, null,
							null, null, null, null, null, null, null);

					mediaAssetWritePlatformService.updateMediaAsset(updateMediaAssetCommand);

					EventMaster evenMaster = eventMasterRepository.findOneByDescription(paywizardItem.getName());

					JSONObject eventpriceJson = new JSONObject();
					eventpriceJson.put("eventId", evenMaster.getId());
					eventpriceJson.put("clientType", "20");
					eventpriceJson.put("formatType", "SD");
					eventpriceJson.put("discountId", "1");
					eventpriceJson.put("optType", "RENT");
					eventpriceJson.put("currencyId", "104");
					eventpriceJson.put("price", paywizardItem.getPricing().getPrice().getAmount());
					eventpriceJson.put("locale", "en");

					final JsonElement eventpriceElement = fromApiJsonHelper.parse(eventpriceJson.toString());

					Long eventPriceId = null;
					for (EventPrice eventPrice : evenMaster.getEventPricings()) {
						eventPriceId = eventPrice.getId();
					}

					System.out.println("data" + evenMaster.getEventPricings());

					System.out.println("data" + eventPriceId);

					if (eventPriceId == null) {
						System.out.println("create  event price with paywizard");

						JsonCommand EventPriceCommand = new JsonCommand(null, eventpriceElement.toString(),
								eventpriceElement, fromApiJsonHelper, null, evenMaster.getId(), null, null, null, null,
								null, null, null, null, null, null);

						eventPriceWritePlatformService.createEventPrice(EventPriceCommand);

					} else {
						System.out.println("updating  event price with paywizard" + eventPriceId);

						JsonCommand updateEventPriceCommand = new JsonCommand(null, eventpriceElement.toString(),
								eventpriceElement, fromApiJsonHelper, null, eventPriceId, null, null, null, null, null,
								null, null, null, null, null);
						eventPriceWritePlatformService.updateEventPrice(updateEventPriceCommand);
					}
				}
			}

		} catch (

		Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	@CronTarget(jobName = JobName.SIMULATOR)
	public void processSimulator() {
		try {
			System.out.println("Processing Simulator Details.......");
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.SIMULATOR.toString());
			if (data != null) {
				// List<ProcessingDetailsData> processingDetails =
				// this.processRequestReadplatformService.retrieveUnProcessingDetails();
				List<ProvisioningRequest> provisioningRequests = this.provisioningRequestRepository
						.findUnProcessedProvisioningRequests();

				if (data.getUpdateStatus().equalsIgnoreCase("Y")) {
					if (!provisioningRequests.isEmpty()) {
						MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
						final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
						LocalTime date = new LocalTime(zone);
						String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_"
								+ date.getSecondOfMinute();
						String path = FileUtils.generateLogFileDirectory() + JobName.SIMULATOR.toString()
								+ File.separator + "Simulator_"
								+ DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
								+ ".log";
						File fileHandler = new File(path.trim());
						fileHandler.createNewFile();
						FileWriter fw = new FileWriter(fileHandler);
						FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
						fw.append("Processing Simulator Details....... \r\n");

						for (ProvisioningRequest provisioningRequest : provisioningRequests) {
							fw.append("simulator Process Request id=" + provisioningRequest.getId()
									+ " ,Provisiong System=" + provisioningRequest.getProvisioningSystem()
									+ " ,RequestType=" + provisioningRequest.getRequestType() + "\r\n");
							// ProcessRequest processRequest =
							// this.processRequestRepository.findOne(detailsData.getId());
							provisioningRequest.setStatus('Y');
							this.billPlanFun(provisioningRequest);
							this.provisioningRequestRepository.saveAndFlush(provisioningRequest);
							this.processRequestWriteplatformService.notifyProcessingDetails(provisioningRequest, 'Y');
						}
						fw.append("Simulator Job is Completed..."
								+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + " \r\n");
						fw.flush();
						fw.close();
					}
				}
				if (data.getcreateTicket().equalsIgnoreCase("Y")) {

					for (ProvisioningRequest provisioningRequest : provisioningRequests) {
						List<Order> orders = this.processRequestWriteplatformService
								.retriveOrdersfromProvisioningRequest(provisioningRequest);
						for (Order order : orders) {
							Collection<MCodeData> problemsData = this.codeReadPlatformService
									.getCodeValue(CodeNameConstants.CODE_PROBLEM_CODE);
							List<EnumOptionData> priorityData = this.ticketMasterReadPlatformService
									.retrievePriorityData();
							Long userId = 0L;
							JSONObject jsonobject = new JSONObject();
							DateTimeFormatter formatter1 = DateTimeFormat.forPattern("dd MMMM yyyy");
							DateTimeFormatter formatter2 = DateTimeFormat.fullTime();
							jsonobject.put("locale", "en");
							jsonobject.put("dateFormat", "dd MMMM yyyy");
							jsonobject.put("ticketTime", " " + new LocalTime().toString(formatter2));
							if (order != null) {
								jsonobject.put("description",
										"ClientId" + provisioningRequest.getClientId() + " Order No:"
												+ order.getOrderNo() + " Request Type:"
												+ provisioningRequest.getRequestType() + " Generated at:"
												+ new LocalTime().toString(formatter2));
							}
							jsonobject.put("ticketDate", formatter1.print(DateUtils.getLocalDateOfTenant()));
							jsonobject.put("sourceOfTicket", "Phone");
							jsonobject.put("assignedTo", userId);
							jsonobject.put("priority", priorityData.get(0).getValue());
							jsonobject.put("problemCode", problemsData.iterator().next().getId());
							this.ticketMasterApiResource.processCreateTicket(provisioningRequest.getClientId(),
									jsonobject.toString());
						}
					}
				}
			}
			System.out.println(
					"Simulator Job is Completed..." + ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (DataIntegrityViolationException exception) {

		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			exception.printStackTrace();
		}
	}

	private void billPlanFun(ProvisioningRequest provisioningRequest) {
		switch (provisioningRequest.getRequestType()) {
		case ProvisioningApiConstants.REQUEST_ACTIVATION:
		case ProvisioningApiConstants.REQUEST_ADD_PLAN:
		case ProvisioningApiConstants.REQUEST_CHANGE_PLAN:
			List<Order> orders = this.processRequestWriteplatformService
					.retriveOrdersfromProvisioningRequest(provisioningRequest);
			for (Order order : orders) {
				Plan plan = this.planRepository.findOne(order.getPlanId());
				if (plan.isPrepaid() == 'N') {
					this.crmServices.createBillPlan(provisioningRequest);
				}
			}
			break;
		default:
			break;
		}
	}

	@Override
	@CronTarget(jobName = JobName.GENERATE_BILLING)
	public void generateStatment() {

		try {
			System.out.println("Process the billing Details.......");
			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.GENERATE_BILLING.toString());

			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.GENERATE_BILLING.toString()
						+ File.separator + "statement_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "")
						+ "_" + dateTime + ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Process the billing Details....... \r\n");
				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobParameterDetails(data.getBatchName());

				if (!sheduleDatas.isEmpty()) {
					for (ScheduleJobData scheduleJobData : sheduleDatas) {
						fw.append("ScheduleJobData id= " + scheduleJobData.getId() + " ,BatchName= "
								+ scheduleJobData.getBatchName() + " ,query=" + scheduleJobData.getQuery() + "\r\n");
						List<Long> clientIds = this.sheduleJobReadPlatformService
								.getClientIds(scheduleJobData.getQuery(), data);
						if (!clientIds.isEmpty()) {
							fw.append("generate bills for the clients..... \r\n");
							for (Long clientId : clientIds) {
								fw.append("processing clientId: " + clientId + " \r\n");
								JSONObject jsonobject = new JSONObject();
								DateTimeFormatter formatter1 = DateTimeFormat.forPattern("dd MMMM yyyy");
								String formattedDate;
								if ("Y".equalsIgnoreCase(data.isDynamic())) {
									formattedDate = formatter1.print(DateUtils.getLocalDateOfTenant().plusDays(7));
								} else {
									formattedDate = formatter1.print(data.getDueDate());
								}
								jsonobject.put("dueDate", formattedDate);
								jsonobject.put("locale", "en");
								jsonobject.put("dateFormat", "dd MMMM YYYY");
								jsonobject.put("message", data.getPromotionalMessage());
								fw.append("sending jsonData for bill generation is: " + jsonobject.toString()
										+ " . \r\n");
								try {
									this.billingMasterApiResourse.generateBillStatement(clientId,
											jsonobject.toString());
								} catch (BillingOrderNoRecordsFoundException e) {
									e.getMessage();
								}
							}
						} else {
							fw.append("no records are available for bill generation \r\n");
						}
					}
				} else {
					fw.append("ScheduleJobData Empty \r\n");
				}
				fw.append("Billing Job is Completed..." + ThreadLocalContextUtil.getTenant().getTenantIdentifier()
						+ " . \r\n");
				fw.flush();
				fw.close();
			}
			System.out.println(
					"statement Job is Completed..." + ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			exception.printStackTrace();
		}
	}

	@Override
	@CronTarget(jobName = JobName.MESSAGE_MERGE)
	public void processingMessages() {
		try {
			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.MESSAGE_MERGE.toString());

			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.MESSAGE_MERGE.toString() + File.separator
						+ "Messanger_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
						+ ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing the Messanger....... \r\n");
				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobDetails(data.getBatchName());

				if (sheduleDatas.isEmpty()) {
					fw.append("ScheduleJobData Empty \r\n");
				}

				for (ScheduleJobData scheduleJobData : sheduleDatas) {

					fw.append("ScheduleJobData id= " + scheduleJobData.getId() + " ,BatchName= "
							+ scheduleJobData.getBatchName() + " ,query=" + scheduleJobData.getQuery() + "\r\n");
					fw.append("Selected Message Template Name is :" + data.getMessageTemplate() + " \r\n");
					Long messageId = this.sheduleJobReadPlatformService.getMessageId(data.getMessageTemplate());
					fw.append("Selected Message Template id is :" + messageId + " \r\n");

					if (messageId != null) {
						fw.append("generating the message....... \r\n");
						this.billingMessageDataWritePlatformService.createMessageData(messageId,
								scheduleJobData.getQuery());
						// this.billingMessageDataWritePlatformService.createMessageTemplate(messageId,scheduleJobData.getQuery());
						fw.append("messages are generated successfully....... \r\n");

					}
				}
				fw.append("Messanger Job is Completed..." + ThreadLocalContextUtil.getTenant().getTenantIdentifier()
						+ " . \r\n");
				fw.flush();
				fw.close();
			}
			System.out.println("Messanger Job is Completed..."
					+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + " \r\n");
		} catch (Exception dve) {
			System.out.println(dve.getMessage());
			handleCodeDataIntegrityIssues(null, dve);
		}
	}

	@Override
	@CronTarget(jobName = JobName.AUTO_EXIPIRY)
	public void processingAutoExipryOrders() {

		try {
			System.out.println("Processing Auto Exipiry Details.......");
			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.AUTO_EXIPIRY.toString());
			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.AUTO_EXIPIRY.toString() + File.separator
						+ "AutoExipiry_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
						+ ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing Auto Exipiry Details....... \r\n");
				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobParameterDetails(data.getBatchName());
				LocalDate exipirydate = null;
				if (sheduleDatas.isEmpty()) {
					fw.append("ScheduleJobData Empty \r\n");
				}
				if (data.isDynamic().equalsIgnoreCase("Y")) {
					exipirydate = DateUtils.getLocalDateOfTenant();
				} else {
					exipirydate = data.getExipiryDate();
				}
				for (ScheduleJobData scheduleJobData : sheduleDatas) {
					fw.append("ScheduleJobData id= " + scheduleJobData.getId() + " ,BatchName= "
							+ scheduleJobData.getBatchName() + " ,query=" + scheduleJobData.getQuery() + "\r\n");
					List<Long> clientIds = new ArrayList<Long>();
					clientIds = this.sheduleJobReadPlatformService.getClientIds(scheduleJobData.getQuery(), data);
					/*
					 * String clientList[] = new String[clientIds.size()]; for(int j
					 * =0;j<clientIds.size();j++){ clientList[j] = clientIds.get(j).toString(); }
					 */
					if (clientIds.isEmpty()) {
						fw.append("no records are available for Auto Expiry \r\n");
					}
					for (Long clientId : clientIds) {

						fw.append("processing client id :" + clientId + "\r\n");
						List<OrderData> orderDatas = this.orderReadPlatformService.retrieveClientOrderDetails(clientId);

						if (orderDatas.isEmpty()) {
							fw.append("No Orders are Found for :" + clientId + "\r\n");
						}
						for (OrderData orderData : orderDatas) {
							if (orderData.getAutoRenew().equalsIgnoreCase("N")
									&& orderData.getStatus().equals("ACTIVE")) {
								// need to change condidtion
								Boolean isSufficientAmountForRenewal = this.lCOWritePlatformService
										.clientBalanceCheck(clientId, orderData.getId());
								this.scheduleJob.ProcessAutoExipiryDetails(orderData, fw, exipirydate, data, clientId,
										isSufficientAmountForRenewal);
							} else if (orderData.getAutoRenew().equalsIgnoreCase("Y")
									&& orderData.getStatus().equals("ACTIVE")) {
								this.scheduleJob.ProcessAutoExipiryDetails(orderData, fw, exipirydate, data, clientId,
										true);
								System.out.println("Processing  autoexpire .......");
							}
						}
					}
				}
				if (data.getAddonExipiry().equalsIgnoreCase("Y")) {
					fw.append("Processing Order Addons for disconnection..."
							+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + " . \r\n");
					List<Long> addonIds = this.sheduleJobReadPlatformService
							.retrieveAddonsForDisconnection(DateUtils.getLocalDateOfTenant());
					for (Long addonId : addonIds) {
						fw.append("Addon Id..." + addonId + " . \r\n");
						this.addOnsWritePlatformService.disconnectOrderAddon(null, addonId);
					}
					fw.append("Order Addons processing is done ..."
							+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + " . \r\n");
				}
				fw.append("Auto Exipiry Job is Completed..." + ThreadLocalContextUtil.getTenant().getTenantIdentifier()
						+ " . \r\n");

				fw.flush();
				fw.close();
				System.out.println(
						"Auto Exipiry Job is Completed..." + ThreadLocalContextUtil.getTenant().getTenantIdentifier());
			}
		} catch (IOException exception) {
			System.out.println(exception);
		} catch (Exception dve) {
			System.out.println(dve.getMessage());
			handleCodeDataIntegrityIssues(null, dve);
		}
	}

	@Override
	@CronTarget(jobName = JobName.PUSH_NOTIFICATION)
	public void processNotify() {

		Long id = null;
		try {
			System.out.println("Processing Notify Details.......");
			List<BillingMessageDataForProcessing> billingMessageDataForProcessings = this.billingMesssageReadPlatformService
					.retrieveMessageDataForProcessing(id);

			if (!billingMessageDataForProcessings.isEmpty()) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.PUSH_NOTIFICATION.toString()
						+ File.separator + "PushNotification_"
						+ DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime + ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing Notify Details....... \r\n");

				for (BillingMessageDataForProcessing emailDetail : billingMessageDataForProcessings) {
					fw.append(
							"BillingMessageData id=" + emailDetail.getId() + " ,MessageTo=" + emailDetail.getMessageTo()
									+ " ,MessageType=" + emailDetail.getMessageType() + " ,MessageFrom="
									+ emailDetail.getMessageFrom() + " ,Message=" + emailDetail.getBody() + "\r\n");
					if (emailDetail.getMessageType() == 'E') {
						String Result = this.messagePlatformEmailService.sendToUserEmail(emailDetail);
						fw.append("b_message_data processing id=" + emailDetail.getId() + "-- and Result :" + Result
								+ " ... \r\n");
					} else if (emailDetail.getMessageType() == 'M') {
						String message = this.sheduleJobReadPlatformService.retrieveMessageData(emailDetail.getId());
						String Result = this.messagePlatformEmailService.sendToUserMobile(message, emailDetail.getId(),
								emailDetail.getMessageTo(), emailDetail.getBody());
						fw.append("b_message_data processing id=" + emailDetail.getId() + "-- and Result:" + Result
								+ " ... \r\n");
					}
					/*
					 * else if(emailDetail.getMessageType()=='M'){ String message =
					 * this.sheduleJobReadPlatformService.retrieveMessageData(emailDetail.getId());
					 * String
					 * Result=this.messagePlatformEmailService.sendToMobile(message,emailDetail.
					 * getId(),emailDetail.getMessageTo(),emailDetail.getBody());
					 * fw.append("b_message_data processing id="+emailDetail.getId()
					 * +"-- and Result:"+Result+" ... \r\n"); }
					 */else {
						fw.append("Message Type Unknown ..\r\n");
					}
				}
				fw.append("Notify Job is Completed.... \r\n");
				fw.flush();
				fw.close();
			} else {
				System.out.println("push Notification data is empty...");
			}
			System.out.println("Notify Job is Completed..." + ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (DataIntegrityViolationException exception) {
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}

	@Override
	public void process(Long id) {
		try {
			System.out.println("Processing Notify Details.......");
			List<BillingMessageDataForProcessing> billingMessageDataForProcessings = this.billingMesssageReadPlatformService
					.retrieveMessageDataForProcessing(id);

			if (!billingMessageDataForProcessings.isEmpty()) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.PUSH_NOTIFICATION.toString()
						+ File.separator + "PushNotification_"
						+ DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime + ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing Notify Details....... \r\n");

				for (BillingMessageDataForProcessing emailDetail : billingMessageDataForProcessings) {
					fw.append(
							"BillingMessageData id=" + emailDetail.getId() + " ,MessageTo=" + emailDetail.getMessageTo()
									+ " ,MessageType=" + emailDetail.getMessageType() + " ,MessageFrom="
									+ emailDetail.getMessageFrom() + " ,Message=" + emailDetail.getBody() + "\r\n");
					if (emailDetail.getMessageType() == 'E') {
						String Result = this.messagePlatformEmailService.sendToUserEmail(emailDetail);
						fw.append("b_message_data processing id=" + emailDetail.getId() + "-- and Result :" + Result
								+ " ... \r\n");
					} else if (emailDetail.getMessageType() == 'M') {
						String message = this.sheduleJobReadPlatformService.retrieveMessageData(emailDetail.getId());
						String Result = this.messagePlatformEmailService.sendToUserMobile(message, emailDetail.getId(),
								emailDetail.getMessageTo(), emailDetail.getBody());
						fw.append("b_message_data processing id=" + emailDetail.getId() + "-- and Result:" + Result
								+ " ... \r\n");
					}
					/*
					 * else if(emailDetail.getMessageType()=='M'){ String message =
					 * this.sheduleJobReadPlatformService.retrieveMessageData(emailDetail.getId());
					 * String
					 * Result=this.messagePlatformEmailService.sendToMobile(message,emailDetail.
					 * getId(),emailDetail.getMessageTo(),emailDetail.getBody());
					 * fw.append("b_message_data processing id="+emailDetail.getId()
					 * +"-- and Result:"+Result+" ... \r\n"); }
					 */else {
						fw.append("Message Type Unknown ..\r\n");
					}
				}
				fw.append("Notify Job is Completed.... \r\n");
				fw.flush();
				fw.close();
			} else {
				System.out.println("push Notification data is empty...");
			}
			System.out.println("Notify Job is Completed..." + ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (DataIntegrityViolationException exception) {
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}

	private static String processRadiusRequests(String url, String encodePassword, String data, FileWriter fw)
			throws IOException {

		HttpClient httpPostClient = new DefaultHttpClient();
		fw.append("data Sending to Server is: " + data + " \r\n");
		StringEntity se = new StringEntity(data.trim());
		fw.append("Request Send to :" + url + "\r\n");

		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader("Authorization", "Basic " + encodePassword);
		postRequest.setHeader("Content-Type", "application/json");
		postRequest.setEntity(se);

		HttpResponse response = httpPostClient.execute(postRequest);

		if (response.getStatusLine().getStatusCode() == 404) {
			System.out.println(
					"ResourceNotFoundException : HTTP error code : " + response.getStatusLine().getStatusCode());
			fw.append("ResourceNotFoundException : HTTP error code : " + response.getStatusLine().getStatusCode()
					+ ", Request url:" + url + "is not Found. \r\n");

			return "ResourceNotFoundException";

		} else if (response.getStatusLine().getStatusCode() == 401) {
			System.out
					.println(" Unauthorized Exception : HTTP error code : " + response.getStatusLine().getStatusCode());
			fw.append(" Unauthorized Exception : HTTP error code : " + response.getStatusLine().getStatusCode()
					+ " , The UserName or Password you entered is incorrect." + "\r\n");

			return "UnauthorizedException";

		} else if (response.getStatusLine().getStatusCode() != 200) {
			System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			fw.append("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + " \r\n");
		} else {
			fw.append("Request executed Successfully... \r\n");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		String output, output1 = "";

		while ((output = br.readLine()) != null) {
			output1 = output1 + output;
		}

		br.close();
		httpPostClient.getConnectionManager().shutdown();

		return output1;

	}

	private static String processRadiusDeleteRequests(String url, String encodePassword, FileWriter fw)
			throws IOException {

		HttpClient httpDeleteClient = new DefaultHttpClient();

		fw.append("Request Send to :" + url + "\r\n");

		HttpDelete deleteRequest = new HttpDelete(url);
		deleteRequest.setHeader("Authorization", "Basic " + encodePassword);
		deleteRequest.setHeader("Content-Type", "application/json");

		HttpResponse response = httpDeleteClient.execute(deleteRequest);

		if (response.getStatusLine().getStatusCode() == 404) {
			System.out.println(
					"ResourceNotFoundException : HTTP error code : " + response.getStatusLine().getStatusCode());
			fw.append("ResourceNotFoundException : HTTP error code : " + response.getStatusLine().getStatusCode()
					+ ", Request url:" + url + "is not Found. \r\n");

			return "ResourceNotFoundException";

		} else if (response.getStatusLine().getStatusCode() == 401) {
			System.out
					.println(" Unauthorized Exception : HTTP error code : " + response.getStatusLine().getStatusCode());
			fw.append(" Unauthorized Exception : HTTP error code : " + response.getStatusLine().getStatusCode()
					+ " , The UserName or Password you entered is incorrect." + "\r\n");

			return "UnauthorizedException";

		} else if (response.getStatusLine().getStatusCode() != 200) {
			System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			fw.append("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + " \r\n");
		} else {
			fw.append("Request executed Successfully... \r\n");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		String output, output1 = "";

		while ((output = br.readLine()) != null) {
			output1 = output1 + output;
		}

		br.close();
		httpDeleteClient.getConnectionManager().shutdown();

		return output1;
	}

	private static String processRadiusGetRequests(String url, String encodePassword, FileWriter fw)
			throws IOException {

		HttpClient httpDeleteClient = new DefaultHttpClient();

		fw.append("Request Send to :" + url + "\r\n");

		HttpGet getRequest = new HttpGet(url);
		getRequest.setHeader("Authorization", "Basic " + encodePassword);
		getRequest.setHeader("Content-Type", "application/json");

		HttpResponse response = httpDeleteClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() == 404) {
			System.out.println(
					"ResourceNotFoundException : HTTP error code : " + response.getStatusLine().getStatusCode());
			fw.append("ResourceNotFoundException : HTTP error code : " + response.getStatusLine().getStatusCode()
					+ ", Request url:" + url + "is not Found. \r\n");

			return "ResourceNotFoundException";

		} else if (response.getStatusLine().getStatusCode() == 401) {
			System.out
					.println(" Unauthorized Exception : HTTP error code : " + response.getStatusLine().getStatusCode());
			fw.append(" Unauthorized Exception : HTTP error code : " + response.getStatusLine().getStatusCode()
					+ " , The UserName or Password you entered is incorrect." + "\r\n");

			return "UnauthorizedException";

		} else if (response.getStatusLine().getStatusCode() != 200) {
			System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			fw.append("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + " \r\n");
		} else {
			fw.append("Request executed Successfully... \r\n");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		String output, output1 = "";

		while ((output = br.readLine()) != null) {
			output1 = output1 + output;
		}

		br.close();
		httpDeleteClient.getConnectionManager().shutdown();

		return output1;
	}

	private void processRadiusSessionOperation(String mikrotikData, String userName, String requestType, String value) {

		try {

			if (null != mikrotikData && null != userName) {
				String prefixCommand = null;
				JSONObject object = new JSONObject(mikrotikData);
				String hostAddress = object.getString("ip");
				String hostUName = object.getString("userName");
				String password = object.getString("password");
				String type = object.getString("type");
				int port = Integer.parseInt(object.getString("port"));

				ApiConnection con = ApiConnection.connect(hostAddress, port);
				con.login(hostUName, password);

				if (type != null && type.equalsIgnoreCase(RadiusJobConstants.RADIUS_HOTSPOT))
					prefixCommand = "/ip/hotspot/";

				if (type != null && type.equalsIgnoreCase(RadiusJobConstants.RADIUS_PPPOE))
					prefixCommand = "/ppp/";

				if (prefixCommand != null && requestType != null && userName != null && !userName.isEmpty()) {

					if (requestType.equalsIgnoreCase(RadiusJobConstants.DisConnection)) {
						List<Map<String, String>> res = con
								.execute(prefixCommand + "active/print where name=" + userName);
						for (Map<String, String> attr : res) {
							String id = attr.get(".id");
							con.execute(prefixCommand + "active/remove .id=" + id);
							System.out.println("Session Deleted For " + userName);
						}
					} else if (requestType.equalsIgnoreCase(RadiusJobConstants.ChangePlan)) {
						if (null != value) {
							String name = "<" + type + "-" + userName + ">";
							String printCommand = "/queue/simple/print where name='" + name + "'";
							System.out.println("Specific user command : " + printCommand);
							List<Map<String, String>> res = con.execute(printCommand);

							for (Map<String, String> attr : res) {
								String id = attr.get(".id");
								String command = "/queue/simple/set max-limit=" + value + " limit-at=" + value + " .id="
										+ id;
								System.out.println("Executing command : " + command);
								con.execute(command);
								System.out.println("plan changed Successfully. bandwidth=" + value);
							}
						}
					}

				} else {
					System.out.println("Please Configure the Mikrotik Data Properly");

				}

			}

		} catch (JSONException e) {
			System.out.println("Mikrotik JobParameter is not a JsonObject");
		} catch (MikrotikApiException e) {
			System.out.println("Mikrotik Api Exception:" + e.getCause().getMessage());
		} catch (InterruptedException e) {
			System.out.println("Interrupted Exception:" + e.getCause().getMessage());
		}
	}

	/*
	 * private void processRadiusSessionDisConnection(String mikrotikData, String
	 * userName) {
	 * 
	 * try{
	 * 
	 * if(null != mikrotikData && null != userName){ String prefixCommand = null;
	 * JSONObject object = new JSONObject(mikrotikData); String hostAddress =
	 * object.getString("ip"); String hostUName = object.getString("userName");
	 * String password = object.getString("password"); String type =
	 * object.getString("type"); int port =
	 * Integer.parseInt(object.getString("port"));
	 * 
	 * ApiConnection con = ApiConnection.connect(hostAddress,port);
	 * con.login(hostUName,password);
	 * 
	 * if(type != null &&
	 * type.equalsIgnoreCase(RadiusJobConstants.RADIUS_HOTSPOT))prefixCommand =
	 * "/ip/hotspot/";
	 * 
	 * if(type != null && type.equalsIgnoreCase(RadiusJobConstants.RADIUS_PPPOE))
	 * prefixCommand = "/ppp/";
	 * 
	 * if(prefixCommand !=null){ List<Map<String, String>> res =
	 * con.execute(prefixCommand + "active/print where name=" + userName); for
	 * (Map<String, String> attr : res) { String id = attr.get(".id");
	 * con.execute(prefixCommand + "active/remove .id=" + id);
	 * System.out.println("Session Deleted For "+ userName); } } else{
	 * System.out.println("Please Configure the Mikrotik Data Properly");
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (JSONException e) {
	 * System.out.println("Mikrotik JobParameter is not a JsonObject"); } catch
	 * (MikrotikApiException e) { System.out.println("Mikrotik Api Exception:" +
	 * e.getCause().getMessage()); } catch (InterruptedException e) {
	 * System.out.println("Interrupted Exception:"+ e.getCause().getMessage()); } }
	 */

	@Override
	@CronTarget(jobName = JobName.RADIUS)
	public void processMiddleware() {
		try {

			System.out.println("Processing Radius Details.......");
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.RADIUS.toString());
			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String credentials = data.getUsername().trim() + ":" + data.getPassword().trim();
				byte[] encoded = Base64.encodeBase64(credentials.getBytes());
				String encodePassword = new String(encoded);
				HttpClient httpClient = new DefaultHttpClient();

				List<EntitlementsData> entitlementDataForProcessings = this.entitlementReadPlatformService
						.getProcessingData(new Long(100), RadiusJobConstants.ProvisioningSystem, null);

				if (!entitlementDataForProcessings.isEmpty()) {

					String path = FileUtils.generateLogFileDirectory() + JobName.RADIUS.toString() + File.separator
							+ "radius_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
							+ ".log";

					File fileHandler = new File(path.trim());
					fileHandler.createNewFile();
					FileWriter fw = new FileWriter(fileHandler);
					FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
					fw.append("Processing Radius Details....... \r\n");
					fw.append("Radius Server Details.....\r\n");
					fw.append("UserName of Radius:" + data.getUsername() + " \r\n");
					fw.append("password of Radius: ************** \r\n");
					fw.append("url of Radius:" + data.getUrl() + " \r\n");

					for (EntitlementsData entitlementsData : entitlementDataForProcessings) {
						fw.append("EntitlementsData id=" + entitlementsData.getId() + " ,clientId="
								+ entitlementsData.getClientId() + " ,RequestType=" + entitlementsData.getRequestType()
								+ "\r\n");
						Long clientId = entitlementsData.getClientId();
						if (clientId == null || clientId == 0) {
							throw new ClientNotFoundException(clientId);
						}
						ReceiveMessage = "";
						ClientEntitlementData clientdata = this.entitlementReadPlatformService.getClientData(clientId);

						if (clientdata == null || clientdata.getSelfcareUsername() == null) {
							String output = "Selfcare Not Created with this ClientId: " + clientId + " Properly.";
							fw.append(output + " \r\n");
							ReceiveMessage = RadiusJobConstants.FAILURE + output;

						} else if (entitlementsData.getRequestType().equalsIgnoreCase(RadiusJobConstants.ChangePlan)) {
							try {
								String planIdentification = null;
								JSONObject jsonObject = new JSONObject(entitlementsData.getProduct());

								if (jsonObject != null && jsonObject.has("planIdentification")) {
									planIdentification = jsonObject.getString("planIdentification");
								}
								if (planIdentification == null || planIdentification.isEmpty()
										|| planIdentification.equalsIgnoreCase("")) {
									fw.append("Plan Identification should Not Mapped Properly, Plan Identification="
											+ planIdentification + " \r\n");
									ReceiveMessage = RadiusJobConstants.FAILURE + "Plan Identification "
											+ " should Not Mapped Properly and Plan Identification="
											+ planIdentification;
								}
								if (data.getProvSystem().equalsIgnoreCase(RadiusJobConstants.RADIUS_VERSION_ONE)) {

									String userName = clientdata.getSelfcareUsername();
									String deletePlanUrl = data.getUrl() + "raduser/" + userName;
									String deleteOutput = processRadiusDeleteRequests(deletePlanUrl, encodePassword,
											fw);

									fw.append("Output from Server For Delete Plan: " + deleteOutput + " \r\n");

									if (deleteOutput.trim().contains(RadiusJobConstants.RADIUS_DELETE_OUTPUT)) {

										JSONObject object = new JSONObject();
										object.put("username", clientdata.getSelfcareUsername());
										object.put("groupname", planIdentification);
										object.put("priority", Long.valueOf(1));
										String url = data.getUrl() + "raduser";
										String output = processRadiusRequests(url, encodePassword, object.toString(),
												fw);

										if (output.trim().equalsIgnoreCase(RadiusJobConstants.RADUSER_CREATE_OUTPUT)) {

											String groupUrl = data.getUrl() + "radservice/group?groupname="
													+ planIdentification;
											String groupData = processRadiusGetRequests(groupUrl, encodePassword, fw);
											String value = null;
											JSONObject groupObject = (JSONObject) new JSONArray(groupData).get(0);

											if (groupObject.has("value")) {
												value = groupObject.getString("value");
											}

											processRadiusSessionOperation(data.getMikrotikApi().trim(), userName,
													RadiusJobConstants.ChangePlan, value);
											ReceiveMessage = "Success";

										} else if (output.equalsIgnoreCase("UnauthorizedException")
												|| output.equalsIgnoreCase("ResourceNotFoundException"))
											return;

										else
											ReceiveMessage = RadiusJobConstants.FAILURE + output;

										fw.append("Output from Server For Create Plan: " + output + " \r\n");

									} else if (deleteOutput.equalsIgnoreCase("UnauthorizedException")
											|| deleteOutput.equalsIgnoreCase("ResourceNotFoundException"))
										return;
									else
										ReceiveMessage = RadiusJobConstants.FAILURE + deleteOutput;

								} else if (data.getProvSystem()
										.equalsIgnoreCase(RadiusJobConstants.RADIUS_VERSION_TWO)) {

								} else {
									String output = "UNKNOWN Radius Version, Please check in RadiusJobConstants.java";
									fw.append(output + " \r\n");
									ReceiveMessage = RadiusJobConstants.FAILURE + output;
								}
							} catch (JSONException e) {
								fw.append("JSON Exeception throwing. StockTrace:" + e.getMessage() + " \r\n");
								ReceiveMessage = RadiusJobConstants.FAILURE + e.getMessage();
							}
						} else if (entitlementsData.getRequestType().equalsIgnoreCase(RadiusJobConstants.Activation)
								|| entitlementsData.getRequestType().equalsIgnoreCase(RadiusJobConstants.ReConnection)
								|| entitlementsData.getRequestType().equalsIgnoreCase(RadiusJobConstants.RENEWAL_AE)) {
							try {
								JSONObject jsonObject = new JSONObject(entitlementsData.getProduct());
								String planIdentification = jsonObject.getString("planIdentification");
								if (planIdentification == null || planIdentification.isEmpty()
										|| planIdentification.equalsIgnoreCase("")) {

									fw.append("Plan Identification should Not Mapped Properly, Plan Identification="
											+ planIdentification + " \r\n");
									ReceiveMessage = RadiusJobConstants.FAILURE + "Plan Identification "
											+ " should Not Mapped Properly and Plan Identification="
											+ planIdentification;
								}

								if (data.getProvSystem().equalsIgnoreCase(RadiusJobConstants.RADIUS_VERSION_ONE)) {
									JSONObject createUser = new JSONObject();
									createUser.put("username", clientdata.getSelfcareUsername());
									createUser.put("attribute", "Cleartext-Password");
									createUser.put("op", ":=");
									createUser.put("value", clientdata.getSelfcarePassword());
									String createUrl = data.getUrl() + "radcheck";

									String createOutput = processRadiusRequests(createUrl, encodePassword,
											createUser.toString(), fw);

									if (createOutput.trim().contains(RadiusJobConstants.RADCHECK_OUTPUT)) {

										JSONObject object = new JSONObject();
										object.put("username", clientdata.getSelfcareUsername());
										object.put("groupname", planIdentification);
										object.put("priority", Long.valueOf(1));
										String url = data.getUrl() + "raduser";

										String output = processRadiusRequests(url, encodePassword, object.toString(),
												fw);

										if (output.trim().equalsIgnoreCase(RadiusJobConstants.RADUSER_CREATE_OUTPUT))
											ReceiveMessage = "Success";
										else if (output.equalsIgnoreCase("UnauthorizedException")
												|| output.equalsIgnoreCase("ResourceNotFoundException"))
											return;
										else
											ReceiveMessage = RadiusJobConstants.FAILURE + output;

										fw.append("Output from Server For Create Plan: " + output + " \r\n");

									} else if (createOutput.equalsIgnoreCase("UnauthorizedException")
											|| createOutput.equalsIgnoreCase("ResourceNotFoundException"))
										return;
									else
										ReceiveMessage = RadiusJobConstants.FAILURE + createOutput;

									fw.append("Output from Server For Create User: " + createOutput + " \r\n");

								} else if (data.getProvSystem()
										.equalsIgnoreCase(RadiusJobConstants.RADIUS_VERSION_TWO)) {

									JSONObject object = new JSONObject();
									object.put("username", clientdata.getSelfcareUsername());
									object.put("password", clientdata.getSelfcarePassword());
									object.put("srvid", planIdentification);
									object.put("firstname", clientdata.getFirstName());
									object.put("lastname", clientdata.getLastName());
									object.put("expiration", "");
									object.put("createdon", data.getUsername());
									object.put("email", clientdata.getEmailId());
									String createUrl = data.getUrl() + "raduser2";

									String createOutput = processRadiusRequests(createUrl, encodePassword,
											object.toString(), fw);

									if (createOutput.trim()
											.equalsIgnoreCase(RadiusJobConstants.RADCHECK_V2_CREATE_OUTPUT))
										ReceiveMessage = "Success";
									else if (createOutput.equalsIgnoreCase("UnauthorizedException")
											|| createOutput.equalsIgnoreCase("ResourceNotFoundException"))
										return;
									else
										ReceiveMessage = RadiusJobConstants.FAILURE + createOutput;

									fw.append(
											"Output from Server For Create User With Plan: " + createOutput + " \r\n");

								} else {
									String output = "UNKNOWN Radius Version, Please check in RadiusJobConstants.java";
									fw.append(output + " \r\n");
									ReceiveMessage = RadiusJobConstants.FAILURE + output;
								}
							} catch (JSONException e) {
								fw.append("JSON Exeception throwing. StockTrace:" + e.getMessage() + " \r\n");
								ReceiveMessage = RadiusJobConstants.FAILURE + e.getMessage();
							}

						} else if (entitlementsData.getRequestType()
								.equalsIgnoreCase(RadiusJobConstants.DisConnection)) {

							try {
								if (data.getProvSystem().equalsIgnoreCase(RadiusJobConstants.RADIUS_VERSION_ONE)) {
									String userName = clientdata.getSelfcareUsername();
									String deletePlanUrl = data.getUrl() + "raduser/" + userName;
									String deleteUserUrl = data.getUrl() + "radcheck/" + userName;
									String deleteOutput = processRadiusDeleteRequests(deletePlanUrl, encodePassword,
											fw);

									if (deleteOutput.trim().contains(RadiusJobConstants.RADIUS_DELETE_OUTPUT)) {

										String deleteUserOutput = processRadiusDeleteRequests(deleteUserUrl,
												encodePassword, fw);

										if (deleteUserOutput.trim().contains(RadiusJobConstants.RADIUS_DELETE_OUTPUT)) {
											processRadiusSessionOperation(data.getMikrotikApi().trim(), userName,
													RadiusJobConstants.DisConnection, null);
											ReceiveMessage = "Success";
										} else if (deleteUserOutput.equalsIgnoreCase("UnauthorizedException")
												|| deleteUserOutput.equalsIgnoreCase("ResourceNotFoundException"))
											return;
										else
											ReceiveMessage = RadiusJobConstants.FAILURE + deleteUserOutput;

										fw.append("Output from Server For Delete User: " + deleteUserOutput + " \r\n");

									} else if (deleteOutput.equalsIgnoreCase("UnauthorizedException")
											|| deleteOutput.equalsIgnoreCase("ResourceNotFoundException"))
										return;
									else
										ReceiveMessage = RadiusJobConstants.FAILURE + deleteOutput;

									fw.append("Output from Server For Delete Plan: " + deleteOutput + " \r\n");

								} else if (data.getProvSystem()
										.equalsIgnoreCase(RadiusJobConstants.RADIUS_VERSION_TWO)) {

									String userName = clientdata.getSelfcareUsername();
									String deleteUserUrl = data.getUrl() + "raduser2/" + userName;
									String deleteOutput = processRadiusDeleteRequests(deleteUserUrl, encodePassword,
											fw);

									if (deleteOutput.trim().contains(RadiusJobConstants.RADIUS_V2_DELETE_OUTPUT)) {

										processRadiusSessionOperation(data.getMikrotikApi().trim(), userName,
												RadiusJobConstants.DisConnection, null);
										ReceiveMessage = "Success";
									}

									else if (deleteOutput.equalsIgnoreCase("UnauthorizedException")
											|| deleteOutput.equalsIgnoreCase("ResourceNotFoundException"))
										return;
									else
										ReceiveMessage = RadiusJobConstants.FAILURE + deleteOutput;

									fw.append("Output from Server For Delete Plan: " + deleteOutput + " \r\n");

								} else {
									String output = "UNKNOWN Radius Version, Please check in RadiusJobConstants.java";
									fw.append(output + " \r\n");
									ReceiveMessage = RadiusJobConstants.FAILURE + output;
								}

							} catch (Exception e) {
								fw.append("Exeception throwing. StockTrace:" + e.getMessage() + " \r\n");
								ReceiveMessage = RadiusJobConstants.FAILURE + e.getMessage();
							}

						} else {
							try {
								fw.append("Request Type is:" + entitlementsData.getRequestType());
								fw.append(
										"Unknown Request Type for Server (or) This Request Type is Not Handle in Radius Job");
								ReceiveMessage = RadiusJobConstants.FAILURE + "Unknown Request Type for Server";
							} catch (Exception e) {
								fw.append("Exeception throwing. StockTrace:" + e.getMessage() + " \r\n");
								ReceiveMessage = RadiusJobConstants.FAILURE + e.getMessage();
							}
						}
						// Updating the Response and status in
						// b_process_request.
						JsonObject object = new JsonObject();
						object.addProperty("prdetailsId", entitlementsData.getPrdetailsId());
						object.addProperty("receivedStatus", new Long(1));
						object.addProperty("receiveMessage", ReceiveMessage);
						String entityName = "ENTITLEMENT";
						fw.append("sending json data to EntitlementApi is:" + object.toString() + "\r\n");
						final JsonElement element1 = fromApiJsonHelper.parse(object.toString());
						JsonCommand comm = new JsonCommand(null, object.toString(), element1, fromApiJsonHelper,
								entityName, entitlementsData.getId(), null, null, null, null, null, null, null, null,
								null, null);
						CommandProcessingResult result = this.entitlementWritePlatformService.create(comm);
						fw.append("Result From the EntitlementApi is:" + result.resourceId() + " \r\n");
					}

					fw.append("Radius Job is Completed..." + ThreadLocalContextUtil.getTenant().getTenantIdentifier()
							+ " /r/n");
					fw.flush();
					fw.close();

				} else {
					System.out.println("Radius data is Empty...");
				}
				httpClient.getConnectionManager().shutdown();
				System.out.println("Radius Job is Completed...");
			}

		} catch (DataIntegrityViolationException exception) {
			System.out.println("catching the DataIntegrityViolationException, StockTrace: " + exception.getMessage());
		} catch (IOException e) {
			System.out.println("catching the IOException, StockTrace: " + e.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}

	@Override
	@CronTarget(jobName = JobName.EVENT_ACTION_PROCESSOR)
	public void eventActionProcessor() {
		try {
			System.out.println("Processing Event Actions.....");
			MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
			final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
			LocalTime date = new LocalTime(zone);
			String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();

			// Retrieve Event Actions
			String path = FileUtils.generateLogFileDirectory() + JobName.EVENT_ACTION_PROCESSOR.toString()
					+ File.separator + "Activationprocess_"
					+ DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime + ".log";
			File fileHandler = new File(path.trim());
			fileHandler.createNewFile();
			FileWriter fw = new FileWriter(fileHandler);
			FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();

			List<EventActionData> actionDatas = this.actionDetailsReadPlatformService
					.retrieveAllActionsForProccessing();

			for (EventActionData eventActionData : actionDatas) {
				fw.append("Process Response id=" + eventActionData.getId() + " ,orderId=" + eventActionData.getOrderId()
						+ " ,Provisiong System=" + eventActionData.getActionName() + " \r\n");
				System.out.println(eventActionData.getId());
				this.actiondetailsWritePlatformService.processEventActions(eventActionData);
			}

			System.out.println("Event Actions are Processed....");
			fw.append("Event Actions are Completed.... \r\n");
			fw.flush();
			fw.close();
		} catch (DataIntegrityViolationException e) {
			System.out.println(e.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}

	@Override
	@CronTarget(jobName = JobName.REPORT_EMAIL)
	public void reportEmail() {

		System.out.println("Processing report email.....");
		try {
			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.REPORT_EMAIL.toString());
			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String fileLocation = FileUtils.MIFOSX_BASE_DIR + File.separator + JobName.REPORT_EMAIL.toString()
						+ File.separator + "ReportEmail_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "")
						+ "_" + dateTime;
				// Retrieve Event Actions
				String path = FileUtils.generateLogFileDirectory() + JobName.REPORT_EMAIL.toString() + File.separator
						+ "ReportEmail_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
						+ ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobDetails(data.getBatchName());

				if (sheduleDatas.isEmpty()) {
					fw.append("ScheduleJobData Empty with this Stretchy_report :" + data.getBatchName() + "\r\n");
				}
				for (ScheduleJobData scheduleJobData : sheduleDatas) {
					fw.append("Processing report email.....\r\n");
					fw.append("ScheduleJobData id= " + scheduleJobData.getId() + " ,BatchName= "
							+ scheduleJobData.getBatchName() + " ,query=" + scheduleJobData.getQuery() + "\r\n");
					Map<String, String> reportParams = new HashMap<String, String>();
					String pdfFileName = this.readExtraDataAndReportingService
							.generateEmailReport(scheduleJobData.getBatchName(), "report", reportParams, fileLocation);

					if (pdfFileName != null) {

						fw.append("PDF file location is :" + pdfFileName + " \r\n");
						fw.append("Sending the Email....... \r\n");
						String result = this.messagePlatformEmailService.createEmail(pdfFileName, data.getEmailId());

						if (result.equalsIgnoreCase("Success")) {
							fw.append("Email sent successfully to " + data.getEmailId() + " \r\n");
						} else {
							fw.append("Email sending failed to " + data.getEmailId() + ", \r\n");

						}
						if (pdfFileName.isEmpty()) {
							fw.append("PDF file name is Empty and PDF file doesnot Create Properly \r\n");
						}
					} else {
						fw.append("PDF file Creation Failed \r\n");
					}
				}
				fw.append("Report Emails Job is Completed....\r\n");
				fw.flush();
				fw.close();
			}
			System.out.println("Report Emails are Processed....");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * @Transactional
	 * 
	 * @Override
	 * 
	 * @CronTarget(jobName = JobName.MESSAGE_MERGE) public void processInstances() {
	 * System.out.println("Just Instance of Message......"); }
	 */
	@Override
	@CronTarget(jobName = JobName.MAKE_INVOICE)
	public void reportStatmentPdf() {
		try {
			System.out.println("Processing statement pdf files....");
			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.MAKE_INVOICE.toString());

			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.MAKE_INVOICE.toString() + File.separator
						+ "statement_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
						+ ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing invoice files....... \r\n");
				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobParameterDetails(data.getBatchName());

				if (!sheduleDatas.isEmpty()) {
					for (ScheduleJobData scheduleJobData : sheduleDatas) {
						fw.append("ScheduleJobData id= " + scheduleJobData.getId() + " ,BatchName= "
								+ scheduleJobData.getBatchName() + ",query=" + scheduleJobData.getQuery() + "\r\n");
						List<Long> billIds = this.sheduleJobReadPlatformService.getBillIds(scheduleJobData.getQuery(),
								data);
						if (!billIds.isEmpty()) {
							fw.append("generate statement pdf files for the  statment bills..... \r\n");
							for (Long billId : billIds) {
								fw.append("processing statement: " + billId + " \r\n");
								this.billingMasterApiResourse.printStatement(billId);
							}
						} else {
							fw.append("no records are available for generate statement pdf files \r\n");
						}
					}
				} else {
					fw.append("ScheduleJobData Empty \r\n");
				}
				fw.append("statement pdf files Job is Completed..."
						+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + " . \r\n");
				fw.flush();
				fw.close();
			}
			System.out.println("statement  pdf file Job is Completed..."
					+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			exception.printStackTrace();
		}
	}

	@Override
	@CronTarget(jobName = JobName.EXPORT_DATA)
	public void processExportData() {

		try {
			System.out.println("Processing export data....");
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.EXPORT_DATA.toString());
			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.EXPORT_DATA.toString() + File.separator
						+ "ExportData_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
						+ ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing export data....\r\n");

				// procedure calling
				SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.jdbcTemplate);
				MapSqlParameterSource parameterSource = new MapSqlParameterSource();

				simpleJdbcCall.setProcedureName("p_int_fa");// p --> procedure int --> integration fa --> financial
															// account s/w {p_todt=2014-12-30}

				if ("Y".equalsIgnoreCase(data.isDynamic())) {
					parameterSource.addValue("p_todt", DateUtils.getLocalDateOfTenant().toString(), Types.DATE);
				} else {
					parameterSource.addValue("p_todt", data.getProcessDate().toString(), Types.DATE);
				}

				Map<String, Object> output = simpleJdbcCall.execute(parameterSource);

				if (output.isEmpty()) {
					fw.append("Exporting data failed....." + ThreadLocalContextUtil.getTenant().getTenantIdentifier()
							+ "\r\n");
				} else {
					fw.append("No of records inserted :" + output.values() + "\r\n");
					fw.append("Exporting data successfully....."
							+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + "\r\n");
				}
				fw.flush();
				fw.close();
			}
			System.out.println(
					"Exporting data successfully done....." + ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (DataIntegrityViolationException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	@CronTarget(jobName = JobName.RESELLER_COMMISSION)
	public void processPartnersCommission() {

		try {
			System.out.println("Processing reseller commission data....");
			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.RESELLER_COMMISSION.toString());
			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.RESELLER_COMMISSION.toString()
						+ File.separator + "Commission_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "")
						+ "_" + dateTime + ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing reseller commission data....\r\n");

				// procedure calling
				SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.jdbcTemplate);
				String sql = "SELECT count(*) FROM Information_schema.Routines WHERE Routine_schema ='"
						+ tenant.getSchemaName()
						+ "'AND specific_name = 'proc_office_commission' AND Routine_Type = 'PROCEDURE'";
				String procdeureStatus = simpleJdbcCall.getJdbcTemplate().queryForObject(sql, String.class);
				if (Integer.valueOf(procdeureStatus) >= 1) {
					simpleJdbcCall.setProcedureName("proc_office_commission");
					Map<String, Object> output = simpleJdbcCall.execute();
					if (output.isEmpty()) {
						fw.append("Reseller commission process failed....."
								+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + "\r\n");
					} else {
						fw.append("No of records inserted :" + output.values() + "\r\n");
						fw.append("Reseller commission processed successfully....."
								+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + "\r\n");
					}
					fw.flush();
					fw.close();
					System.out.println("Reseller commission processed successfully....."
							+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
				} else {
					fw.append("Procedure with name 'proc_office_commission' not exists....."
							+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + "\r\n");
					fw.flush();
					fw.close();
					System.out.println("Reseller commission processed failed....."
							+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
				}
			}
		} catch (DataIntegrityViolationException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	@CronTarget(jobName = JobName.AGING_DISTRIBUTION)
	public void processAgingDistribution() {

		try {
			System.out.println("Processing aging distribution data....");
			// JobParameterData data =
			// this.sheduleJobReadPlatformService.getJobParameters(JobName.EXPORT_DATA.toString());
			/// if (data != null) {
			MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
			final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
			LocalTime date = new LocalTime(zone);
			String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
			String path = FileUtils.generateLogFileDirectory() + JobName.AGING_DISTRIBUTION.toString() + File.separator
					+ "Distribution" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
					+ ".log";
			File fileHandler = new File(path.trim());
			fileHandler.createNewFile();
			FileWriter fw = new FileWriter(fileHandler);
			FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
			fw.append("Processing aging distribution data....\r\n");

			// procedure calling
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.jdbcTemplate);
			String sql = "SELECT count(*) FROM Information_schema.Routines WHERE Routine_schema ='"
					+ tenant.getSchemaName() + "'AND specific_name = 'proc_distrib' AND Routine_Type = 'PROCEDURE'";
			String procdeureStatus = simpleJdbcCall.getJdbcTemplate().queryForObject(sql, String.class);
			if (Integer.valueOf(procdeureStatus) >= 1) {
				simpleJdbcCall.setProcedureName("proc_distrib");
				Map<String, Object> output = simpleJdbcCall.execute();
				if (output.isEmpty()) {
					fw.append("Aging Distribution data failed....."
							+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + "\r\n");
				} else {
					fw.append("No of records inserted :" + output.values() + "\r\n");
					fw.append("Aging Distribution data successfully completed....."
							+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + "\r\n");
				}
				fw.flush();
				fw.close();
				System.out.println("Aging Distribution data successfully completed....."
						+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
			} else {
				fw.append("Procedure with name 'proc_distrib' not exists....."
						+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + "\r\n");
				fw.flush();
				fw.close();
				System.out.println("Aging Distribution data failed....."
						+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
			}
		} catch (DataIntegrityViolationException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	@CronTarget(jobName = JobName.REPROCESS)
	public void reProcessEventAction() {
		try {

			System.out.println("Processing ReProcess Request Job .....");
			MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
			final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
			LocalTime date = new LocalTime(zone);
			String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();

			String path = FileUtils.generateLogFileDirectory() + JobName.REPROCESS.toString() + File.separator
					+ "ReProcess_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
					+ ".log";
			File fileHandler = new File(path.trim());
			fileHandler.createNewFile();
			FileWriter fw = new FileWriter(fileHandler);
			FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();

			List<PaymentGatewayData> datas = this.paymentGatewayReadPlatformService.retrievePendingDetails();
			SimpleDateFormat dateformat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

			for (PaymentGatewayData data : datas) {

				JSONObject reProcessObject;
				boolean processingFlag;

				Configuration reProcessInterval = this.configurationRepository
						.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_REPROCESS_INTERVAL);
				int intervalTime = 0;

				if (reProcessInterval != null && reProcessInterval.getValue() != null
						&& !reProcessInterval.getValue().isEmpty()) {
					intervalTime = Integer.parseInt(reProcessInterval.getValue());
				}

				PaymentGateway paymentGateway = this.paymentGatewayRepository.findOne(data.getId());

				if (null == paymentGateway.getReProcessDetail() || paymentGateway.getReProcessDetail().isEmpty()) {
					processingFlag = true;
					reProcessObject = new JSONObject();
					reProcessObject.put("id", 0);
					reProcessObject.put("response", "");
					reProcessObject.put("processTime", "");

				} else {
					reProcessObject = new JSONObject(data.getReprocessDetail());

					Date reProcessingDate = dateformat.parse(reProcessObject.get("processTime").toString());
					Date newDate = DateUtils.getDateOfTenant();
					long diff = newDate.getTime() - reProcessingDate.getTime();
					long hours = diff / (60 * 60 * 1000);

					if (intervalTime <= hours)
						processingFlag = true;
					else
						processingFlag = false;
				}

				if (processingFlag) {

					if (paymentGateway.getSource().equalsIgnoreCase(ConfigurationConstants.GLOBALPAY_PAYMENTGATEWAY)) {

						final String formattedDate = dateformat.format(DateUtils.getDateOfTenant());

						int id = reProcessObject.getInt("id");
						reProcessObject.remove("id");
						reProcessObject.remove("response");
						reProcessObject.remove("processTime");
						reProcessObject.put("id", id + 1);

						JSONObject object = new JSONObject(paymentGateway.getRemarks());

						String transactionId = object.getString("transactionId");

						String output = this.paymentGatewayWritePlatformService.globalPayProcessing(transactionId,
								paymentGateway.getRemarks());

						final JSONObject json = new JSONObject(output);

						String status = json.getString("status");
						String error = json.getString("error");

						reProcessObject.put("response", error);
						reProcessObject.put("processTime", formattedDate);

						if (status.equalsIgnoreCase(ConfigurationConstants.PAYMENTGATEWAY_SUCCESS)) {

							List<EventActionData> eventActionDatas = this.eventActionReadPlatformService
									.retrievePendingActionRequest(paymentGateway.getId());

							for (EventActionData eventActionData : eventActionDatas) {

								fw.append("Process Response id=" + eventActionData.getId() + " ,PaymentGatewayId="
										+ eventActionData.getResourceId() + " ,Provisiong System="
										+ eventActionData.getActionName() + " \r\n");
								System.out.println("EventAction Id:" + eventActionData.getId() + ", PaymentGatewayId:"
										+ eventActionData.getResourceId());

								EventAction eventAction = this.eventActionRepository.findOne(eventActionData.getId());

								if (eventAction.getActionName()
										.equalsIgnoreCase(EventActionConstants.EVENT_CREATE_PAYMENT)) {

									JSONObject paymentObject = new JSONObject(eventActionData.getJsonData());
									paymentObject.put("paymentDate", formattedDate);

									eventAction.updateStatus('N');
									eventAction.setTransDate(DateUtils.getLocalDateOfTenant().toDate());
									eventAction.setCommandAsJson(paymentObject.toString());

								} else if (eventAction.getActionName()
										.equalsIgnoreCase(EventActionConstants.ACTION_NEW)) {

									JSONObject createOrder = new JSONObject(eventAction.getCommandAsJson());
									createOrder.remove("start_date");
									eventAction.updateStatus('N');
									eventAction.setTransDate(DateUtils.getLocalDateOfTenant().toDate());
									createOrder.put("start_date", DateUtils.getLocalDateOfTenant().toDate());
									eventAction.setCommandAsJson(createOrder.toString());

								} else {
									System.out.println("Does Not Implement the Code....");
								}

								this.eventActionRepository.save(eventAction);
							}

						} else if (status.equalsIgnoreCase(ConfigurationConstants.PAYMENTGATEWAY_FAILURE)) {

							List<EventActionData> eventActionDatas = this.eventActionReadPlatformService
									.retrievePendingActionRequest(paymentGateway.getId());

							for (EventActionData eventActionData : eventActionDatas) {

								fw.append("Process Response id=" + eventActionData.getId() + " ,PaymentGatewayId="
										+ eventActionData.getResourceId() + " ,Provisiong System="
										+ eventActionData.getActionName() + " \r\n");
								System.out.println("EventAction Id:" + eventActionData.getId() + ", PaymentGatewayId:"
										+ eventActionData.getResourceId());

								EventAction eventAction = this.eventActionRepository.findOne(eventActionData.getId());

								if (eventAction.getActionName()
										.equalsIgnoreCase(EventActionConstants.EVENT_CREATE_PAYMENT)
										|| eventAction.getActionName()
												.equalsIgnoreCase(EventActionConstants.EVENT_CREATE_ORDER)) {

									eventAction.updateStatus('F');
									eventAction.setTransDate(DateUtils.getLocalDateOfTenant().toDate());

								} else {
									System.out.println("Does Not Implement the Code....");
								}

								this.eventActionRepository.save(eventAction);

							}

						} else {
							System.out.println("Still get Pending Response from PaymentGateway");
						}

						paymentGateway.setStatus(status);

						paymentGateway.setReProcessDetail(reProcessObject.toString());

						this.paymentGatewayRepository.save(paymentGateway);
					}

				}
			}

			System.out.println("ReProcess Requests are Processed....");
			fw.append("ReProcess Requests are Completed.... \r\n");
			fw.flush();
			fw.close();

		} catch (DataIntegrityViolationException e) {
			System.out.println(e.getMessage());
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}

	@Override
	@CronTarget(jobName = JobName.DISCONNECT_UNPAID_CUSTOMERS)
	public void processingDisconnectUnpaidCustomers() {

		try {
			System.out.println("Processing Unpaid Customers Details.......");
			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.DISCONNECT_UNPAID_CUSTOMERS.toString());
			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.DISCONNECT_UNPAID_CUSTOMERS.toString()
						+ File.separator + "UnpaidCustomers_"
						+ DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime + ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing Unpaid Customers Details....... \r\n");
				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobParameterDetails(data.getBatchName());

				if (!sheduleDatas.isEmpty()) {
					for (ScheduleJobData scheduleJobData : sheduleDatas) {
						fw.append("ScheduleJobData id=" + scheduleJobData.getId() + " ,BatchName="
								+ scheduleJobData.getBatchName() + " ,query=" + scheduleJobData.getQuery() + "\r\n");
						List<Long> clientIds = this.sheduleJobReadPlatformService
								.getClientIds(scheduleJobData.getQuery(), data);
						if (!clientIds.isEmpty()) {
							for (Long clientId : clientIds) {
								fw.append("processing Unpaid Customer id :" + clientId + "\r\n");
								List<OrderData> orders = this.orderReadPlatformService
										.retrieveCustomerActiveOrders(clientId);
								if (!orders.isEmpty()) {
									for (OrderData order : orders) {
										this.scheduleJob.ProcessDisconnectUnPaidCustomers(order, fw, clientId);
									}
								} else {
									fw.append("No Orders are Found for :" + clientId + "\r\n");
								}
							}
						} else {
							fw.append("no records are available for Disconnect Unpaid Customers \r\n");
						}
					}
				} else {
					fw.append("Unpaid Customers ScheduleJobData Empty \r\n");
				}
				fw.append("Disconnect Unpaid Customers Job is Completed..."
						+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + " . \r\n");
				fw.flush();
				fw.close();
			}
			System.out.println("Disconnect Unpaid Customers Job is Completed..."
					+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (DataIntegrityViolationException | IOException e) {
			System.out.println(e);
			e.printStackTrace();
		} catch (Exception dve) {
			System.out.println(dve.getMessage());
			handleCodeDataIntegrityIssues(null, dve);
		}
	}

	@Override
	@CronTarget(jobName = JobName.ESCALATION)
	public void processingEscalations() {

		try {

			System.out.println("Processing Ticket Escalation ");
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.ESCALATION.toString());
			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.ESCALATION.toString() + File.separator
						+ "Escalation_" + DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime
						+ ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("Processing Ticket Escalation Details....... \r\n");
				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobParameterDetails(data.getBatchName());
				if (sheduleDatas.isEmpty()) {
					fw.append("ScheduleJobData Empty \r\n");
				}
				BillingMessage billingMessage = null;
				BillingMessageTemplate ticketEscalationMessageDetails = null;
				for (ScheduleJobData scheduleJobData : sheduleDatas) {
					fw.append("ScheduleJobData id= " + scheduleJobData.getId() + " ,BatchName= "
							+ scheduleJobData.getBatchName() + " ,query=" + scheduleJobData.getQuery() + "\r\n");
					List<ClientTicketData> escalatedTicketDatas = this.sheduleJobReadPlatformService
							.getEscaltedTickets(scheduleJobData.getQuery(), data);
					if (!escalatedTicketDatas.isEmpty()) {
						for (ClientTicketData escalatedTicketData : escalatedTicketDatas) {
							if (null == ticketEscalationMessageDetails) {
								ticketEscalationMessageDetails = this.billingMessageTemplateRepository
										.findByTemplateDescription(
												BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_TICKET_ESCALATION);
							}
							if (ticketEscalationMessageDetails != null) {
								String subject = ticketEscalationMessageDetails.getSubject();
								String body = ticketEscalationMessageDetails.getBody();
								String footer = ticketEscalationMessageDetails.getFooter();
								String header = ticketEscalationMessageDetails.getHeader().replace("<ManagerName>",
										escalatedTicketData.getManagerName());
								body = body.replace("<ticketNo>", escalatedTicketData.getTicketNo());
								body = body.replace("<createdDateTime>",
										escalatedTicketData.getTicketDate().toString());
								billingMessage = new BillingMessage(header, body, footer,
										escalatedTicketData.getManagerName(), escalatedTicketData.getManagerEmail(),
										subject, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
										ticketEscalationMessageDetails,
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
								this.billingMessageRepository.save(billingMessage);
								// this.processNotify(billingMessage.getId()); // need to remove while using
								// these scheduler
							} else {
								throw new BillingMessageTemplateNotFoundException(
										BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NOTIFY_TICKET_ESCALATION);
							}
							OfficeTicket officeTicket = this.officeTicketRepository
									.findOne(escalatedTicketData.getId());
							officeTicket.setAssignedTo((long) 1);
							officeTicket.setIsescalated('1');
							this.officeTicketRepository.save(officeTicket);
						}
					}

				}

				fw.append("Ticket Escalation Job is Completed..."
						+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + " . \r\n");

				fw.flush();
				fw.close();
				System.out.println("Ticket Escalation Job is Completed..."
						+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
			}
		} catch (IOException exception) {
			System.out.println(exception);
		} catch (Exception dve) {
			System.out.println(dve.getMessage());
			handleCodeDataIntegrityIssues(null, dve);
		}
	}

	@Override
	@CronTarget(jobName = JobName.INACTIVECUSTLEASECHARGING)
	public void inActiveCustomersWithSTBLease() {

		try {
			System.out.println("Processing osd renewal Details.......");
			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.INACTIVECUSTLEASECHARGING.toString());
			if (data != null) {
				MifosPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
				final DateTimeZone zone = DateTimeZone.forID(tenant.getTimezoneId());
				LocalTime date = new LocalTime(zone);
				String dateTime = date.getHourOfDay() + "_" + date.getMinuteOfHour() + "_" + date.getSecondOfMinute();
				String path = FileUtils.generateLogFileDirectory() + JobName.INACTIVECUSTLEASECHARGING.toString()
						+ File.separator + "INACTIVECUSTOMERSWITHLEASESTB"
						+ DateUtils.getLocalDateOfTenant().toString().replace("-", "") + "_" + dateTime + ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				FileWriter fw = new FileWriter(fileHandler);
				FileUtils.BILLING_JOB_PATH = fileHandler.getAbsolutePath();
				fw.append("ProcessingINACTIVECUSTOMERSWITHLEASESTB....... \r\n");

				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobParameterDetails(data.getBatchName());

				if (!sheduleDatas.isEmpty()) {
					for (ScheduleJobData scheduleJobData : sheduleDatas) {
						fw.append("ScheduleJobData id= " + scheduleJobData.getId() + " ,BatchName= "
								+ scheduleJobData.getBatchName() + ",query=" + scheduleJobData.getQuery() + "\r\n");
						List<Long> clientIds = this.sheduleJobReadPlatformService
								.getClientIds(scheduleJobData.getQuery(), data);
						if (!clientIds.isEmpty()) {
							fw.append("generate statement pdf files for the  statment bills..... \r\n");
							Configuration isstbLeasePlanCode = configurationRepository
									.findOneByName(ConfigurationConstants.LEASE_STB_PLANCODE);
							String planCode = null;
							if (null != isstbLeasePlanCode && isstbLeasePlanCode.isEnabled()) {
								final JSONObject object = new JSONObject(isstbLeasePlanCode.getValue());
								planCode = object.getString("stb_lease_plancode");

							}
							Plan plan = this.planRepository.findwithName(planCode);
							for (Long clientId : clientIds) {

								Order order = this.orderRepository.findOrderByClientIdWithLeasePlanActive(clientId,
										plan.getId());
								if (order != null) {
									fw.append("processing statement: " + clientId + " \r\n");
									JSONObject jsonObject = new JSONObject();
									SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
									try {
										jsonObject.put("dateFormat", "dd MMMM yyyy HH:mm:ss");
										jsonObject.put("locale", "en");
										jsonObject.put("systemDate", dateFormat.format(new Date()));
										this.chargingOrderApiResourse.createChargesToOrders(order.getClientId(),
												jsonObject.toString());
									} catch (Exception e) {
										throw new PlatformDataIntegrityException("error.msg.charge.exception",
												"error.message.charging.exception");
									}
								}
							}
						} else {
							fw.append("no records are available for generate statement pdf files \r\n");
						}
					}
				} else {
					fw.append("ScheduleJobData Empty \r\n");
				}

				fw.append("box lease charging Job is Completed..."
						+ ThreadLocalContextUtil.getTenant().getTenantIdentifier() + " . \r\n");

				fw.flush();
				fw.close();
				System.out.println("LEASE BOX CUSTOMER Job is Completed..."
						+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
			}

		} catch (IOException exception) {
			System.out.println(exception);
		} catch (Exception dve) {
			System.out.println(dve.getMessage());
			handleCodeDataIntegrityIssues(null, dve);
		}
	}

}
