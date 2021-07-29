package org.mifosplatform.finance.paymentsgateway.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.paymentsgateway.data.RecurringPaymentTransactionTypeConstants;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfigurationRepository;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBilling;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBillingRepository;
import org.mifosplatform.finance.paymentsgateway.domain.RecurringBillingHistory;
import org.mifosplatform.finance.paymentsgateway.domain.RecurringBillingHistoryRepository;
import org.mifosplatform.finance.paymentsgateway.exception.PaymentGatewayConfigurationException;
import org.mifosplatform.finance.paymentsgateway.exception.PaypalStatusChangeActionTypeMisMatchException;
import org.mifosplatform.finance.paymentsgateway.serialization.PaymentGatewayCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.exceptions.OrderNotFoundException;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.workflow.eventaction.domain.EventAction;
import org.mifosplatform.workflow.eventaction.domain.EventActionRepository;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import urn.ebay.api.PayPalAPI.ManageRecurringPaymentsProfileStatusReq;
import urn.ebay.api.PayPalAPI.ManageRecurringPaymentsProfileStatusRequestType;
import urn.ebay.api.PayPalAPI.ManageRecurringPaymentsProfileStatusResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.UpdateRecurringPaymentsProfileReq;
import urn.ebay.api.PayPalAPI.UpdateRecurringPaymentsProfileRequestType;
import urn.ebay.api.PayPalAPI.UpdateRecurringPaymentsProfileResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.AckCodeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.ManageRecurringPaymentsProfileStatusRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.StatusChangeActionType;
import urn.ebay.apis.eBLBaseComponents.UpdateRecurringPaymentsProfileRequestDetailsType;

import com.google.gson.JsonObject;
import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;

@Service
public class PaymentGatewayRecurringWritePlatformServiceImpl implements PaymentGatewayRecurringWritePlatformService {

	private final PlatformSecurityContext context;
    private final PortfolioCommandSourceWritePlatformService writePlatformService;
    private final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository;
	private final PaypalRecurringBillingRepository paypalRecurringBillingRepository;
	private final EventActionReadPlatformService eventActionReadPlatformService;
	private final EventActionRepository eventActionRepository;
	private final OrderRepository orderRepository;
	private final PaymentGatewayCommandFromApiJsonDeserializer paymentGatewayCommandFromApiJsonDeserializer;
	private final ConfigurationRepository configurationRepository;
	private final RecurringBillingHistoryRepository recurringBillingHistoryRepository;
	private final HttpClient client = new DefaultHttpClient();
      
    @Autowired
    public PaymentGatewayRecurringWritePlatformServiceImpl(final PlatformSecurityContext context, 
    		final PortfolioCommandSourceWritePlatformService writePlatformService,
    		final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository,
    		final PaypalRecurringBillingRepository paypalRecurringBillingRepository,
    		final EventActionReadPlatformService eventActionReadPlatformService,
    		final EventActionRepository eventActionRepository, final OrderRepository orderRepository,
			final PaymentGatewayCommandFromApiJsonDeserializer paymentGatewayCommandFromApiJsonDeserializer,
			final ConfigurationRepository configurationRepository, 
			final RecurringBillingHistoryRepository recurringBillingHistoryRepository) {

		this.context = context;
		this.writePlatformService = writePlatformService;
		this.paymentGatewayConfigurationRepository = paymentGatewayConfigurationRepository;
		this.paypalRecurringBillingRepository = paypalRecurringBillingRepository;
		this.eventActionReadPlatformService = eventActionReadPlatformService;
		this.eventActionRepository = eventActionRepository;
		this.orderRepository = orderRepository;
		this.paymentGatewayCommandFromApiJsonDeserializer = paymentGatewayCommandFromApiJsonDeserializer;
		this.configurationRepository = configurationRepository;
		this.recurringBillingHistoryRepository = recurringBillingHistoryRepository;

	}
    
    /**
     * Paypal Recurring Verification Method is Used for Verifying the IPN Handler Request. 
     * Which is Came from Paypal Server For The Changes/Creation of Paypal Recurring Profile.
     * 
     * For this Verification, We have to re-send the Response Parameter to Paypal Server. 
     * 
     * NOTE: "password","username" and "rm" Parameters are Explicity add to Paypal Response Parameters,
     * 			So We have to Delete that Parameters From Verification Sending Paypal Request. Otherwise
     * 			We get INVALID Response From Paypal Server.
     * 
     * PROCESS: Prepare a String Variable with Response Parameter in the Form of Url-Encode.
     * and Add the "requestData=_notify-validate"(Said to Paypal Server as it is a Verification Request) 
     * Content to Before String Variable.
     * 
     * And Send this String to Paypal Server(We can take Paypal Url From c_paymentgateway_conf table )
     */
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public String paypalRecurringVerification(HttpServletRequest request) throws IllegalStateException, ClientProtocolException, IOException, JSONException {

     BufferedReader bufferedReader = null;
     InputStream is = null;
     String paramName, paramValue, paypalUrl, line;
     final String splitParam = "\\?", charSet = "charset";
     
     try {
      
      this.context.authenticatedUser();
    
      PaymentGatewayConfiguration pgConfig = this.paymentGatewayConfigurationRepository.findOneByName(ConfigurationConstants.PAYPAL_PAYMENTGATEWAY);
      
      if (null == pgConfig || null == pgConfig.getValue() || !pgConfig.isEnabled()) {
       throw new PaymentGatewayConfigurationException(ConfigurationConstants.PAYPAL_PAYMENTGATEWAY);
      }
      
      JSONObject pgConfigJsonObj = new JSONObject(pgConfig.getValue());
      paypalUrl = pgConfigJsonObj.getString(ConfigurationConstants.PAYPAL_URL_NAME);
     
      HttpPost post = new HttpPost(paypalUrl.split(splitParam)[0].trim());
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("cmd", "_notify-validate")); //You need to add this parameter to tell PayPal to verify
      for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
    	  paramName = e.nextElement();
    	  paramValue = request.getParameter(paramName);
      
      if (!"password".equalsIgnoreCase(paramName) && !"username".equalsIgnoreCase(paramName) && !"tenantIdentifier".equalsIgnoreCase(paramName) && !"rm".equalsIgnoreCase(paramName)) {
       params.add(new BasicNameValuePair(paramName, paramValue));
      }
      }
      post.setEntity(new UrlEncodedFormEntity(params, request.getParameter(charSet)));
      HttpResponse response = client.execute(post);
      
      is = response.getEntity().getContent();
      bufferedReader = new BufferedReader(new InputStreamReader(is));
      String result = "";
      
      while ((line = bufferedReader.readLine()) != null) {
      result += line;
      }
      return result;
      
     } finally {
      if(null != is) is.close();
      if(null != bufferedReader) bufferedReader.close();
     }

    }

	/**
	 * This recurringEventUpdate(HttpServletRequest request) Method Used For Creating/Renewal/Change the Orders
	 * From EventAction Table.
	 * 
	 * @param recurringSubscriberSignUp(HttpServletRequest request) is used for Storing ProfileId in b_recurring table if Not stored.
	 * 
	 * @Param retrievePendingRecurringRequest(Long clientId) method is used for get the Recurring(Status='R') Orders from b_event_action.
	 */
	@Override
	public void recurringEventUpdate(HttpServletRequest request, RecurringBillingHistory recurringBillingHistory) throws JSONException {
		
		this.context.authenticatedUser();
		
		String ProfileId = request.getParameter(RecurringPaymentTransactionTypeConstants.SUBSCRID);
		PaypalRecurringBilling paypalRecurringBilling = this.paypalRecurringBillingRepository.findOneBySubscriberId(ProfileId);
		
		if(null == paypalRecurringBilling) return;
		
		String jsonObj = customDataReturn(request);
		
		JSONObject obj = new JSONObject(jsonObj);
		
		Long clientId = obj.getLong(RecurringPaymentTransactionTypeConstants.CLIENTID);
		
		List<EventActionData> eventActionDatas = this.eventActionReadPlatformService.retrievePendingRecurringRequest(clientId);
		
		for(EventActionData eventActionData:eventActionDatas){
			
			System.out.println("EventAction Id: "+eventActionData.getId()+", clientId: "+clientId + ", actionName: "+ eventActionData.getActionName());
		
			EventAction eventAction = this.eventActionRepository.findOne(eventActionData.getId());
			
			recurringBillingHistory.setClientId(clientId);
			
			if (eventAction.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_NEW)) {
				
				Long planId   = obj.getLong(RecurringPaymentTransactionTypeConstants.PLANID);
				String paytermCode = obj.getString(RecurringPaymentTransactionTypeConstants.PAYTERMCODE);
				Long contractPeriod = obj.getLong(RecurringPaymentTransactionTypeConstants.CONTRACTPERIOD);
				
				JSONObject createOrder = new JSONObject(eventAction.getCommandAsJson());
				
				Long ePlanCode   = createOrder.getLong(RecurringPaymentTransactionTypeConstants.PLANCODE);
				String ePaytermCode = createOrder.getString(RecurringPaymentTransactionTypeConstants.PAYTERMCODE);
				Long eContractPeriod = createOrder.getLong(RecurringPaymentTransactionTypeConstants.CONTRACTPERIOD);
				
				if(planId.equals(ePlanCode) && paytermCode.equalsIgnoreCase(ePaytermCode) && contractPeriod.equals(eContractPeriod)) {
					
					// creating order and assign Recurring Details.
					try {
						CommandWrapper commandRequest = new CommandWrapperBuilder().createOrder(clientId).withJson(createOrder.toString()).build();
						CommandProcessingResult resultOrder = this.writePlatformService.logCommandSource(commandRequest);

						if (null == resultOrder || resultOrder.resourceId() <= 0) {
							recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
							recurringBillingHistory.setObsDescription("OrderBooking Failed...");
							eventAction.updateStatus('F');
							eventAction.setDescription("orderBooking Failed with id=" + resultOrder.resourceId());
						}
						
						if(null != paypalRecurringBilling){
							createOrder.remove("start_date");
							eventAction.updateStatus('Y');
							eventAction.setTransDate(DateUtils.getLocalDateOfTenant().toDate());
							createOrder.put("start_date", DateUtils.getLocalDateOfTenant().toDate());
							eventAction.setCommandAsJson(createOrder.toString());
							eventAction.setDescription("Success");										
							paypalRecurringBilling.setOrderId(resultOrder.resourceId());		
							this.paypalRecurringBillingRepository.save(paypalRecurringBilling);
							recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUCCESS);
							recurringBillingHistory.setObsDescription("OrderBooking Success...");
						}
						
					} catch (Exception e) {
						recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
						recurringBillingHistory.setObsDescription("OrderBooking Failed, Reason:"+stackTraceToString(e));
						eventAction.updateStatus('F');
						eventAction.setDescription("Reason:" + stackTraceToString(e));
					}
				}
				
			} else if (eventAction.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_CHNAGE_PLAN)) {
				
				//Do the Change Order Functionality Here
				Long planId   = obj.getLong(RecurringPaymentTransactionTypeConstants.PLANID);
				String paytermCode = obj.getString(RecurringPaymentTransactionTypeConstants.PAYTERMCODE);
				Long contractPeriod = obj.getLong(RecurringPaymentTransactionTypeConstants.CONTRACTPERIOD);
				Long orderId   = obj.getLong(RecurringPaymentTransactionTypeConstants.ORDERID);
				
				JSONObject changeOrder = new JSONObject(eventAction.getCommandAsJson());
				
				Long ePlanCode   = changeOrder.getLong(RecurringPaymentTransactionTypeConstants.PLANCODE);
				String ePaytermCode = changeOrder.getString(RecurringPaymentTransactionTypeConstants.PAYTERMCODE);
				Long eContractPeriod = changeOrder.getLong(RecurringPaymentTransactionTypeConstants.CONTRACTPERIOD);
				
				if(planId.equals(ePlanCode) && paytermCode.equalsIgnoreCase(ePaytermCode) && contractPeriod.equals(eContractPeriod)){
					
					try {
						CommandWrapper commandRequest = new CommandWrapperBuilder().changePlan(orderId).withJson(changeOrder.toString()).build();
						CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
						
						if (null == result || result.resourceId() <= 0) {
							recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
							recurringBillingHistory.setObsDescription("Change Plan Action Failed...");
							eventAction.updateStatus('F');
							eventAction.setDescription("result.resourceId=" + result.resourceId());
						}
						
						if(null != paypalRecurringBilling){
							
							if(changeOrder.has("start_date")){
								changeOrder.remove("start_date");
								changeOrder.put("start_date", DateUtils.getLocalDateOfTenant().toDate());
							}
							
							eventAction.updateStatus('Y');
							eventAction.setTransDate(DateUtils.getLocalDateOfTenant().toDate());
							eventAction.setCommandAsJson(changeOrder.toString());
							eventAction.setDescription("Success");
							recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUCCESS);
							recurringBillingHistory.setObsDescription("Change Plan Completed");
							
							paypalRecurringBilling.setOrderId(result.resourceId());
							this.paypalRecurringBillingRepository.save(paypalRecurringBilling);
						}
						
					} catch (Exception e) {
						recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
						recurringBillingHistory.setObsDescription("change Plan Action Failed, Reason:" + stackTraceToString(e));
						eventAction.updateStatus('F');
						eventAction.setDescription("StackTrace:" + stackTraceToString(e));
					}	
				}
				
			} else if (eventAction.getActionName().equalsIgnoreCase(EventActionConstants.ACTION_RENEWAL)) {
				
				Long renewalPeriod = obj.getLong(RecurringPaymentTransactionTypeConstants.RENEWALPERIOD);
				Long priceId = obj.getLong(RecurringPaymentTransactionTypeConstants.PRICEID);
				Long orderId   = obj.getLong(RecurringPaymentTransactionTypeConstants.ORDERID);
				
				JSONObject renewalOrder = new JSONObject(eventAction.getCommandAsJson());
				
				Long eRenewalPeriod   = renewalOrder.getLong(RecurringPaymentTransactionTypeConstants.RENEWALPERIOD);
				Long ePriceId = renewalOrder.getLong(RecurringPaymentTransactionTypeConstants.PRICEID);
				Long eOrderId = renewalOrder.getLong(RecurringPaymentTransactionTypeConstants.ORDERID);
				
				if(renewalOrder.has(RecurringPaymentTransactionTypeConstants.CLIENTID)){
					renewalOrder.remove(RecurringPaymentTransactionTypeConstants.CLIENTID);
				}
				
				if(renewalOrder.has(RecurringPaymentTransactionTypeConstants.ORDERID)){
					renewalOrder.remove(RecurringPaymentTransactionTypeConstants.ORDERID);
				}
				
				if(renewalPeriod.equals(eRenewalPeriod) && priceId.equals(ePriceId) && orderId.equals(eOrderId)){
					
					try {
						final CommandWrapper commandRequest = new CommandWrapperBuilder().renewalOrder(orderId).withJson(renewalOrder.toString()).build();
						final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);
						
						if (null == result || result.resourceId() <= 0) {
							recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
							recurringBillingHistory.setObsDescription("Renewal Order Action Failed...");
							eventAction.updateStatus('F');
							eventAction.setDescription("result.resourceId=" + result.resourceId());
						}
						
						if(null != paypalRecurringBilling){
							eventAction.updateStatus('Y');
							eventAction.setTransDate(DateUtils.getLocalDateOfTenant().toDate());
							eventAction.setDescription("Success");
							recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUCCESS);
							recurringBillingHistory.setObsDescription("Renewal Order Completed");
							paypalRecurringBilling.setOrderId(orderId);
							this.paypalRecurringBillingRepository.save(paypalRecurringBilling);
						}
						
					} catch (Exception e) {
						recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
						recurringBillingHistory.setObsDescription("Renewal Order Action Failed, Reason:" + stackTraceToString(e));
						eventAction.updateStatus('F');
						eventAction.setDescription("StackTrace:" + stackTraceToString(e));
					}
				}
				
			} else{
				recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
				recurringBillingHistory.setObsDescription("Didn't Develop this Functionality");
				System.out.println("Does Not Implement the Code....");
			}
			
			this.eventActionRepository.save(eventAction);
			this.recurringBillingHistoryRepository.save(recurringBillingHistory);
		}
	}

	/**
	 * Use This Method to Storing Paypal Recurring Profile SubscriberId For Tracking the Recurring Profile.
	 * We can Use this SubscriberId to Identify the Recurring Profile in the Paypal Server.
	 * 
	 * Using this SubscriberId We can change the Recurring Profile Information.
	 * 
	 * @param clientId,subscriberId is Storing in the table. One SubscriberId Belongs to one Customer/ClientId only
	 * 
	 */
	@Override
	public PaypalRecurringBilling recurringSubscriberSignUp(HttpServletRequest request, RecurringBillingHistory recurringBillingHistory) {
		
		try {
			this.context.authenticatedUser();
			
			String ProfileId = request.getParameter(RecurringPaymentTransactionTypeConstants.SUBSCRID);
			
			String customData = customDataReturn(request);
			
			PaypalRecurringBilling billing = this.paypalRecurringBillingRepository.findOneBySubscriberId(ProfileId);
			
			if(null == customData){
				
				recurringBillingHistory.setClientId(0L);
				recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
				recurringBillingHistory.setObsDescription("Profile Creation Checking Failed Due to custom Parameter Data Not Found in the Request");
				this.recurringBillingHistoryRepository.save(recurringBillingHistory);
				return null;
			}
			
			if (null == billing) {
	
				JSONObject object = new JSONObject(customData);
				if(!object.has(RecurringPaymentTransactionTypeConstants.CLIENTID)){
					recurringBillingHistory.setClientId(0L);
					recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
					recurringBillingHistory.setObsDescription("clientId Not Found in the custom Parameter Data");
					this.recurringBillingHistoryRepository.save(recurringBillingHistory);
					return null;
				}
				Long clientId = object.getLong(RecurringPaymentTransactionTypeConstants.CLIENTID);
				
				billing = new PaypalRecurringBilling(clientId, ProfileId);
				this.paypalRecurringBillingRepository.save(billing);
				
				String currencyCode = null;
				
				if(request.getParameterMap().containsKey(RecurringPaymentTransactionTypeConstants.MC_CURRENCY)){
					currencyCode = request.getParameter(RecurringPaymentTransactionTypeConstants.MC_CURRENCY);
				}
				
				updateRecurring(ProfileId, 0, null, currencyCode);
				
				recurringBillingHistory.setClientId(clientId);
				recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUCCESS);
				recurringBillingHistory.setObsDescription("Recurring Billing Profile Successfully Created, ProfileId="+ ProfileId);
				this.recurringBillingHistoryRepository.save(recurringBillingHistory);
			}
			return billing;
			
		} catch (JSONException e) {
			recurringBillingHistory.setClientId(0L);
			recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			recurringBillingHistory.setObsDescription("Profile Creation Failed Due to JsonException on custom Parameter Data," +
					" Reason:="+ stackTraceToString(e));
			this.recurringBillingHistoryRepository.save(recurringBillingHistory);
			
			return null;
		} catch (Exception e) {
			recurringBillingHistory.setClientId(0L);
			recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			recurringBillingHistory.setObsDescription("Profile Creation Failed Due to Exception Throwing," +
					" Reason:="+ stackTraceToString(e));
			this.recurringBillingHistoryRepository.save(recurringBillingHistory);
			
			return null;
		}
		
		
	}

	/**
	 * Using this createJsonForOnlineMethod(HttpServletRequest request) Method for Preparing JsonData For 
	 * PaymentGatewayApiResource.java's OnlinePaymentMethod(final String apiRequestBodyAsJson).
	 * 
	 * @return JsonData String to process Payment for the Client.
	 */
	@Override
	public String createJsonForOnlineMethod(HttpServletRequest request) throws JSONException {
		
		String status;
		
		String paymentStatus = request.getParameter(RecurringPaymentTransactionTypeConstants.PAYMENTSTATUS);
		String currency = request.getParameter(RecurringPaymentTransactionTypeConstants.MCCURRENCY);
		BigDecimal amount = new BigDecimal(request.getParameter(RecurringPaymentTransactionTypeConstants.MCGROSS));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		String date = dateFormat.format(new Date());
		
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("paymentDate", date);
		jsonObj.addProperty("payerEmail", request.getParameter("payer_email"));
		jsonObj.addProperty("customer_name", request.getParameter("first_name"));
		jsonObj.addProperty("receiverEmail", request.getParameter("receiver_email"));
		jsonObj.addProperty("payerStatus", request.getParameter("payer_status"));
		jsonObj.addProperty("currency", currency);
		jsonObj.addProperty("paymentStatus", paymentStatus);
		
		String customData = customDataReturn(request);

		JSONObject custom = new JSONObject(customData);
		Long clientId = custom.getLong(RecurringPaymentTransactionTypeConstants.CLIENTID);
		
		JSONObject jsonObject = new JSONObject();
		
		if(paymentStatus.equalsIgnoreCase(RecurringPaymentTransactionTypeConstants.COMPLETED)){
			
			status = RecurringPaymentTransactionTypeConstants.SUCCESS;
			
		} else if (paymentStatus.equalsIgnoreCase(RecurringPaymentTransactionTypeConstants.PENDING)) {
			
			status = RecurringPaymentTransactionTypeConstants.PENDING;
			String error = request.getParameter(RecurringPaymentTransactionTypeConstants.PENDINGREASON);
			jsonObj.addProperty("pendingReason", error);
			jsonObject.put("error", error);
		
		} else{
			status = paymentStatus;
		}
				
		jsonObject.put("source", RecurringPaymentTransactionTypeConstants.PAYPAL);
		jsonObject.put("transactionId", request.getParameter(RecurringPaymentTransactionTypeConstants.TRANSACTIONID));
		jsonObject.put("currency", currency);
		jsonObject.put("clientId", clientId);
		jsonObject.put("total_amount", amount);
		jsonObject.put("locale", "en");
		jsonObject.put("dateFormat", "dd MMMM yyyy");
		jsonObject.put("otherData", jsonObj.toString());
		jsonObject.put("status", status);
	
		return jsonObject.toString();
		
	}

	/**
	 * We can use this Method to Updating Paypal Recurring Profile Using SubscriberId. 
	 * 
	 * @param maxFailedPayments --> Adding maxFailedPayments value to Profile.
	 * @param subscriberId --> subscriberId of the Paypal Recurring Table.
	 * @param billingCycles --> Adding Additional Billing Cycles to Existing Profile. 
	 * @param totalAmount --> Updating the Recurring Profile Price to this Parameter value.
	 * NOTE: This Updation Only Work, When the Increasing Price is lessthan 20% to Profile Price. 
	 * @param currency 
	 * @return
	 */
	private Map<String,Object> updateRecurring(String subscriberId, int billingCycles, String totalAmount, String currencyCode){
		
		Map<String,Object> mapDetails = new HashMap<String,Object>();
		
		try {
			Configuration config = this.configurationRepository.findOneByName(ConfigurationConstants.PAYPAL_MAX_FAILED_COUNT);
			
			int maxFailedPayments = 5;
			
			if(null !=config && config.isEnabled() && !config.getValue().isEmpty()){
				maxFailedPayments = Integer.parseInt(config.getValue());
			}
			
			UpdateRecurringPaymentsProfileRequestDetailsType updateRecurringPaymentsProfileRequestDetails = new UpdateRecurringPaymentsProfileRequestDetailsType();
			
			updateRecurringPaymentsProfileRequestDetails.setProfileID(subscriberId);
			
			if(billingCycles>0){
				
				updateRecurringPaymentsProfileRequestDetails.setAdditionalBillingCycles(billingCycles);
			}

			if(null != totalAmount){
				
				BasicAmountType amount = new BasicAmountType();

				amount.setCurrencyID(CurrencyCodeType.valueOf(currencyCode));

				amount.setValue(totalAmount);

				updateRecurringPaymentsProfileRequestDetails.setAmount(amount);
			}

			updateRecurringPaymentsProfileRequestDetails.setMaxFailedPayments(maxFailedPayments);

			UpdateRecurringPaymentsProfileRequestType updateRecurringPaymentsProfileRequest = new UpdateRecurringPaymentsProfileRequestType();

			updateRecurringPaymentsProfileRequest.setUpdateRecurringPaymentsProfileRequestDetails(updateRecurringPaymentsProfileRequestDetails);

			UpdateRecurringPaymentsProfileReq profile = new UpdateRecurringPaymentsProfileReq();

			profile.setUpdateRecurringPaymentsProfileRequest(updateRecurringPaymentsProfileRequest);

			// Create the PayPalAPIInterfaceServiceService service object to make the API call
			PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(getMapConfigDetails());

			UpdateRecurringPaymentsProfileResponseType manageProfileStatusResponse = service.updateRecurringPaymentsProfile(profile);
			
			if (manageProfileStatusResponse.getAck().equals(AckCodeType.FAILURE)
					|| (manageProfileStatusResponse.getErrors() != null && manageProfileStatusResponse.getErrors().size() > 0)) {
		
				String error = manageProfileStatusResponse.getErrors().get(0).getLongMessage();
				System.out.println("Paypal Profile Update Failed due to Reason:"+ error);
				mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
				mapDetails.put("error", error);
				
			} else {
				System.out.println("Paypal Profile Update Successfully Completed.");
				mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUCCESS);
				mapDetails.put("error", "");
			}	

		} catch (JSONException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (SSLConfigurationException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (InvalidCredentialException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (HttpErrorException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (InvalidResponseDataException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (ClientActionRequiredException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (MissingCredentialException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (OAuthException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (IOException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (InterruptedException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (ParserConfigurationException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (SAXException e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		} catch (Exception e) {
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		}
		
		return mapDetails;
		
	}

	/**
	 * 
	 * We are Internally Using updateRecurring(String subscriberId, int billingCycles, String totalAmount) Method to
	 * Change/Update the Recurring Profile. 
	 */
	@Override
	public CommandProcessingResult updatePaypalRecurring(JsonCommand command) {
		// TODO Auto-generated method stub
		
		Map<String,Object> mapDetails;
		
		try {

			Long orderId = command.longValueOfParameterNamed("orderId");
			int billingCycles = 0;
			String totalAmount = null, currencyCode="USD";
			
			PaypalRecurringBilling paypalRecurringBilling = this.paypalRecurringBillingRepository.findOneByOrderId(orderId);
			
			if(null == paypalRecurringBilling) throw new OrderNotFoundException(orderId);
			
			if(command.hasParameter("billingCycles")) billingCycles = command.integerValueOfParameterNamed("billingCycles");

			if(command.hasParameter("totalAmount")) totalAmount = command.stringValueOfParameterNamed("totalAmount");
			
			if(command.hasParameter("currencyCode")) currencyCode = command.stringValueOfParameterNamed("currencyCode");
			
			mapDetails = updateRecurring(paypalRecurringBilling.getSubscriberId(), billingCycles, totalAmount, currencyCode);
			
		} catch (Exception e) {
			
			mapDetails = new HashMap<>();
			mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			mapDetails.put("error", stackTraceToString(e));
		}
		
		return new CommandProcessingResultBuilder().with(mapDetails).build();
	}

	private String stackTraceToString(Throwable e) {
		
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private Map<String, String> getMapConfigDetails() throws JSONException {

		PaymentGatewayConfiguration pgConfig = this.paymentGatewayConfigurationRepository.findOneByName(ConfigurationConstants.PAYPAL_RECURRING_PAYMENT_DETAILS);

		if (null == pgConfig || null == pgConfig.getValue() || !pgConfig.isEnabled()) {
			throw new PaymentGatewayConfigurationException(ConfigurationConstants.PAYPAL_RECURRING_PAYMENT_DETAILS);
		}

		JSONObject pgConfigObject = new JSONObject(pgConfig.getValue());
		String username = pgConfigObject.getString("userName");
		String password = pgConfigObject.getString("password");
		String signature = pgConfigObject.getString("signature");
		String serverType = pgConfigObject.getString("serverType");

		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put(RecurringPaymentTransactionTypeConstants.SERVER_MODE, serverType);
		sdkConfig.put(RecurringPaymentTransactionTypeConstants.API_USERNAME, username);
		sdkConfig.put(RecurringPaymentTransactionTypeConstants.API_PASSWORD, password);
		sdkConfig.put(RecurringPaymentTransactionTypeConstants.API_SIGNATURE, signature);
		
		return sdkConfig;
	}

	/**
	 * Using this Method we are Updating Paypal Profile Status to "Active" or "Suspend" or "ReActive".
	 * 
	 * @param recurringStatus Parameter value must get From JsonObject.
	 * 
	 * @throw OrderNotFoundException(orderId) exception, when the Order is not found with this orderId
	 * 
	 * @throw PaypalStatusChangeActionTypeMisMatchException(status) exception, when the StatusChangeActionType Enum Value is
	 * not found with this status
	 */
	@Override
	public CommandProcessingResult updatePaypalProfileStatus(JsonCommand command) {

		Map<String,Object> mapDetails = new HashMap<String,Object>();
		
		try {

			Long orderId = command.longValueOfParameterNamed("orderId");
			String status = command.stringValueOfParameterNamed("recurringStatus");

			PaypalRecurringBilling paypalRecurringBilling = this.paypalRecurringBillingRepository.findOneByOrderId(orderId);

			if (null == paypalRecurringBilling) {
				throw new OrderNotFoundException(orderId);
			}
			
			StatusChangeActionType statusChangeActionType = StatusChangeActionType.valueOf(status);
			
			if(null == statusChangeActionType){
				throw new PaypalStatusChangeActionTypeMisMatchException(status);
			}

			ManageRecurringPaymentsProfileStatusRequestType request = new ManageRecurringPaymentsProfileStatusRequestType();

			ManageRecurringPaymentsProfileStatusRequestDetailsType details = new ManageRecurringPaymentsProfileStatusRequestDetailsType();

			request.setManageRecurringPaymentsProfileStatusRequestDetails(details);

			details.setProfileID(paypalRecurringBilling.getSubscriberId());

			details.setAction(statusChangeActionType);

			// Invoke the API
			ManageRecurringPaymentsProfileStatusReq wrapper = new ManageRecurringPaymentsProfileStatusReq();
			wrapper.setManageRecurringPaymentsProfileStatusRequest(request);

			// Create the PayPalAPIInterfaceServiceService service object to make the API call
			PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(getMapConfigDetails());

			ManageRecurringPaymentsProfileStatusResponseType manageProfileStatusResponse = service.manageRecurringPaymentsProfileStatus(wrapper);

			if(manageProfileStatusResponse.getAck().equals(AckCodeType.SUCCESS) || 
					manageProfileStatusResponse.getAck().equals(AckCodeType.SUCCESSWITHWARNING)){
				
				mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUCCESS);
				mapDetails.put("acknoledgement", manageProfileStatusResponse.getAck());
				mapDetails.put("error", "");
			
			} else if (manageProfileStatusResponse.getAck().equals(AckCodeType.FAILURE) || 
					manageProfileStatusResponse.getAck().equals(AckCodeType.FAILUREWITHWARNING) ||
					 (manageProfileStatusResponse.getErrors() != null && manageProfileStatusResponse.getErrors().size() > 0)) {
		
				String error = manageProfileStatusResponse.getErrors().get(0).getLongMessage();
				
				mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
				mapDetails.put("acknoledgement", manageProfileStatusResponse.getAck());
				mapDetails.put("error", error);
				
			} else {
				mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
				mapDetails.put("acknoledgement", manageProfileStatusResponse.getAck());
				mapDetails.put("error", "");
			}	

			return new CommandProcessingResultBuilder().with(mapDetails).build();
			
		} catch (JSONException e) {		
			mapDetails.put("error", stackTraceToString(e));
		} catch (SSLConfigurationException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (InvalidCredentialException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (HttpErrorException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (InvalidResponseDataException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (ClientActionRequiredException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (MissingCredentialException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (OAuthException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (IOException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (InterruptedException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (ParserConfigurationException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (SAXException e) {
			mapDetails.put("error", stackTraceToString(e));
		} catch (Exception e) {
			mapDetails.put("error", stackTraceToString(e));
		}

		mapDetails.put("result", RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
		mapDetails.put("acknoledgement", "");
		
		return new CommandProcessingResultBuilder().with(mapDetails).build();
		
	}

	/**
	 * Note: Usually this Method is cally when the "MaxFailedPayment" Event Fired.
	 * 
	 * Using this disConnectOrder(String profileId) Method to Disconnect the Order using subscriberId.
	 * 
	 * @return getOrderIdFromSubscriptionId(profileId) Method returns orderId From b_recurring table.
	 * 
	 * @return getOrderStatus(orderId) Method returns status of the Order. if Status id Active then only
	 * We perform Disconnect Operation.
	 * 
	 */
	@Override
	public void disConnectOrder(String profileId, RecurringBillingHistory recurringBillingHistory) {
		
		this.context.authenticatedUser();
		
		Long orderId = getOrderIdFromSubscriptionId(profileId);
		
		if(null == orderId){
			recurringBillingHistory.setClientId(0L);
			recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			recurringBillingHistory.setObsDescription("order is Not with this ProfileId:" + profileId);
			this.recurringBillingHistoryRepository.save(recurringBillingHistory);
			
			return;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		
		String date = dateFormat.format(new Date());
		
		JSONObject object = new JSONObject();
		
		try {
			
			String status = getOrderStatus(orderId);
			
			if (null != status && status.equalsIgnoreCase(StatusTypeEnum.ACTIVE.toString())) {
				Order order = this.orderRepository.findOne(orderId);
				object.put("disconnectionDate", date);
				object.put("disconnectReason", "DisConnecting this Order Based on Paypal Recurring Billing Instruction");
				final CommandWrapper commandRequest = new CommandWrapperBuilder().updateOrder(orderId).withJson(object.toString()).build();
				final CommandProcessingResult result = this.writePlatformService.logCommandSource(commandRequest);

				if (result != null && result.resourceId() > 0) {
					recurringBillingHistory.setClientId(order.getClientId());
					recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUCCESS);
					recurringBillingHistory.setObsDescription("Order DisConnection Completed...");
				} else {
					recurringBillingHistory.setClientId(order.getClientId());
					recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
					recurringBillingHistory.setObsDescription("Order DisConnection Failed...");
				}
			} else {
				Order order = this.orderRepository.findOne(orderId);
				recurringBillingHistory.setClientId(order.getClientId());
				recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_UNKNOWN);
				recurringBillingHistory.setObsDescription("Status of Order=" + status);
			}
			
		} catch (JSONException e) {
			System.out.println("JsonException: "+ e.getMessage());
			recurringBillingHistory.setClientId(0L);
			recurringBillingHistory.setObsStatus(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_FAILURE);
			recurringBillingHistory.setObsDescription("JsonException Order Termination Failed, Reason: " + stackTraceToString(e));
		}
		
		this.recurringBillingHistoryRepository.save(recurringBillingHistory);
		
	}
	
	@Override
	public String getOrderStatus(Long orderId){
		
		Order order = this.orderRepository.findOne(orderId);
		if(order == null){
			System.out.println("order Not found with this orderId:"+orderId);
			return null;
		 }
		EnumOptionData Enumstatus=OrderStatusEnumaration.OrderStatusType(order.getStatus().intValue());
		return Enumstatus.getValue();
	}
	
	@Override
	public PaypalRecurringBilling getRecurringBillingObject(String profileId) {
		
		PaypalRecurringBilling billing = this.paypalRecurringBillingRepository.findOneBySubscriberId(profileId);
		
		if(billing == null){
			System.out.println("Recurring Billing Profile Not found with this SubscriberId:" + profileId);	
			return null;
		}
		
		return billing;
	}
	
	private Long getOrderIdFromSubscriptionId(String profileId){
		
		PaypalRecurringBilling billing = getRecurringBillingObject(profileId);
		
		if(null == billing || null == billing.getOrderId()){		
			
			System.out.println("orderId Not found with this SubscriberId:"+profileId);	
			return null;
		}
		
		return billing.getOrderId();
	}

	@Override
	public Long updateRecurringBillingTable(String profileId) {
		

		PaypalRecurringBilling billing = getRecurringBillingObject(profileId);
		
		if(null == billing){		
			System.out.println("orderId Not found with this SubscriberId:"+profileId);	
			return null;
		}
		
		billing.updateStatus();
		this.paypalRecurringBillingRepository.save(billing);
		return billing.getOrderId();
	}

	@Override
	public CommandProcessingResult deleteRecurringBilling(JsonCommand command) {
		
		this.context.authenticatedUser();
		
		String subscriptionId = command.stringValueOfParameterNamed(RecurringPaymentTransactionTypeConstants.SUBSCRID);
		
		CommandProcessingResult result = updatePaypalProfileStatus(command);
		
		if(null != result){
			Map<String, Object> resultmap = result.getChanges();
			
			String outputRes = resultmap.get("result").toString();
			
			if(outputRes.equalsIgnoreCase(RecurringPaymentTransactionTypeConstants.RECURRING_PAYMENT_SUCCESS)){
				
				Long orderId = updateRecurringBillingTable(subscriptionId);
				
				return new CommandProcessingResult(orderId);
			} else {
				return new CommandProcessingResult(0L);
			}
			
		} else{
			return new CommandProcessingResult(new Long(-1));
		}
	}
	
	/**
	 * Using this Method we getCustom Data. 
	 * 
	 * Custom Parameter is exist in the Parameters List, if we do Payment with Paypal site through WEB.
	 *  
	 * rp_invoice_id Parameter is exist in the Parameter List, if and only if we Pay the Money with Paypal Api Internally.
	 * 
	 * Note: in the Case of using Paypal Api, We didnot have the Custom Parameter to set the UserDefined Data. So we are 
	 * Saving the CustomData in the EventAction table and get the Id of that Record and send that id as invoice_id in Paypal Api.
	 * 
	 * 
	 */
	private String customDataReturn(HttpServletRequest request){
		
		if(request.getParameterMap().containsKey(RecurringPaymentTransactionTypeConstants.CUSTOM)){
			 
			return request.getParameter(RecurringPaymentTransactionTypeConstants.CUSTOM);
			 
		} else if (request.getParameterMap().containsKey(RecurringPaymentTransactionTypeConstants.RP_INVOICE_ID)) {
			
			String eventID = request.getParameter(RecurringPaymentTransactionTypeConstants.RP_INVOICE_ID);
			
			EventAction eventAction = this.eventActionRepository.findOne(new Long(eventID));
			
			if(null != eventAction) return eventAction.getCommandAsJson();
			
			else return null;	
			
		} else {	
			return null;	
		}
	}

	@Override
	public String getRequestParameters(HttpServletRequest request) {
		
		Enumeration enumeration = request.getParameterNames();
		JsonObject object = new JsonObject();
		while (enumeration.hasMoreElements()) {
			String paramName = (String) enumeration.nextElement();
			String paramValue = request.getParameter(paramName);	
			if (!"password".equalsIgnoreCase(paramName)) object.addProperty(paramName, paramValue);
		}
		return object.toString();	
	}

}
