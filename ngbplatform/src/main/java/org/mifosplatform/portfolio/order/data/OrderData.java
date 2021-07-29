package org.mifosplatform.portfolio.order.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;

public class OrderData {
	private Long id;
	private Long pdid;
	private Long orderPriceId;
	private Long clientId;
	private String service_code;
	private String planCode;
	private String planDescription;
	private Long planType;
	private String chargeCode;
	private double price;
	private String variant;
	private String status;
	private Long period;
	private LocalDate planStartDate;
	private LocalDate planEndDate;
	private LocalDate startDate;
	private LocalDate currentDate;
	private LocalDate endDate;
	private String billingFrequency;
	private List<PlanCodeData> plandata;
	private List<PaytermData> paytermdata;
	private List<SubscriptionData> subscriptiondata;
	private List<OrderPriceData> orderPriceData;
	private LocalDate activeDate;
	private String contractPeriod;
//	private boolean flag;
	private Collection<MCodeData> disconnectDetails;
	private List<OrderHistoryData> orderHistory;
	private String isPrepaid;
	private String allowtopup;
	private List<OrderData> clientOrders;
	private String userAction;
	private String orderNo;
	private OrderData orderData;
	private String provisioningSys;
	private List<OrderLineData> orderServices;
	private List<OrderDiscountData> orderDiscountDatas;
	private LocalDate invoiceTilldate;
	private Collection<MCodeData> reasons;
	private Collection<MCodeData> extensionPeriodDatas;
	private String groupName;
	private Long planStatus;
	@SuppressWarnings("unused")
	private List<OrderAddonsData> orderAddonsDatas;
	@SuppressWarnings("unused")
	private String autoRenew;
	/* private List<AssociationData> HardwareDatas; */
	List<ClientServiceData> clientServiceData;
	private Long clientServiceId;
	private List<NetworkElementData> provisioningSystems;
	private Collection<OfficeData> officeData;
	private List<PlanCodeData> settlementPlanDatas;
	private List<PlanCodeData> defaultPlanDatas;

	private String poId;
	private String clientPoId;
	private String planPoId;
	private String dealPoId;
	private String clientServicePoId;
	private List<PlanData> oldPlanData;
	private String oldPlanPoId;
	private String oldDealPoId;
	private Long contractPeriodId;
	private Long orderId;
	private Long priceId;
	private Long provisioningSystem;
	private String provisioningSystemName;
	private String commandName;
	private Long currencyId;
	private Long planId;
	private double nonCurrency;
	// private String chargeOwner;
	private String orderStatus;

	public OrderData(List<PlanCodeData> allowedtypes, List<PaytermData> paytermData,
			List<SubscriptionData> contractPeriod, OrderData data, List<ClientServiceData> clientServiceData,
			List<PlanCodeData> settlementPlanDatas) {

		if (data != null) {

			this.id = data.getId();
			this.pdid = data.getPdid();
			this.planCode = data.getPlan_code();
			this.status = null;
			this.period = data.getPeriod();
			this.orderPriceId = data.getOrderPriceId();
			this.service_code = null;
			this.startDate = data.getStartDate();
		}
		this.startDate = DateUtils.getLocalDateOfTenant();
		this.variant = null;
		this.chargeCode = null;
		this.paytermdata = paytermData;
		this.plandata = allowedtypes;
		this.subscriptiondata = contractPeriod;
		this.clientServiceData = clientServiceData;
		this.settlementPlanDatas = settlementPlanDatas;

	}

	public OrderData(List<PlanCodeData> allowedtypes, List<PaytermData> paytermData,
			List<SubscriptionData> contractPeriod, OrderData data, List<ClientServiceData> clientServiceData,
			List<PlanCodeData> settlementPlanDatas, List<PlanData> oldPlanData, List<PlanCodeData> defaultPlanDatas) {

		if (data != null) {

			this.id = data.getId();
			this.pdid = data.getPdid();
			this.planCode = data.getPlan_code();
			this.status = null;
			this.period = data.getPeriod();
			this.orderPriceId = data.getOrderPriceId();
			this.service_code = null;
			this.startDate = data.getStartDate();
		}
		this.startDate = DateUtils.getLocalDateOfTenant();
		this.variant = null;
		this.chargeCode = null;
		this.paytermdata = paytermData;
		this.plandata = allowedtypes;
		this.subscriptiondata = contractPeriod;
		this.clientServiceData = clientServiceData;
		this.settlementPlanDatas = settlementPlanDatas;
		this.oldPlanData = oldPlanData;
		this.defaultPlanDatas = defaultPlanDatas;
	}

	public OrderData(Long id, Long planId, String plancode, Long planType, String status, LocalDate startDate,
			LocalDate endDate, double price, String contractPeriod, String isprepaid, String allowtopup,
			String userAction, String provisioningSys, String orderNo, LocalDate invoiceTillDate, LocalDate activaDate,
			String groupName, String autoRenew, Long clientServiceId, String planName, Long planPoid, Long dealPoId) {
		this.id = id;
		this.pdid = planId;
		this.planCode = plancode;
		this.planType = planType;
		this.status = status;
		this.period = null;
		this.startDate = startDate;
		this.currentDate = DateUtils.getLocalDateOfTenant();
		this.endDate = endDate;
		this.orderPriceId = null;
		this.service_code = null;
		this.price = price;
		this.variant = null;
		this.chargeCode = null;
		this.paytermdata = null;
		this.plandata = null;
		this.subscriptiondata = null;
		this.contractPeriod = contractPeriod;
		this.isPrepaid = isprepaid;
		this.allowtopup = allowtopup;
		this.userAction = userAction;
		this.provisioningSys = provisioningSys;
		this.orderNo = orderNo;
		this.invoiceTilldate = invoiceTillDate;
		this.activeDate = activaDate;
		this.groupName = groupName;
		this.autoRenew = autoRenew;
		this.clientServiceId = clientServiceId;
		this.planDescription = planName;
		this.planPoId = planPoid.toString();
		this.dealPoId = dealPoId.toString();
	}

	public OrderData(List<OrderPriceData> priceDatas, List<OrderHistoryData> historyDatas, OrderData orderDetailsData,
			List<OrderLineData> services, List<OrderDiscountData> discountDatas,
			List<OrderAddonsData> orderAddonsDatas) {
		this.orderPriceData = priceDatas;
		this.orderHistory = historyDatas;
		this.orderData = orderDetailsData;
		this.orderServices = services;
		this.orderDiscountDatas = discountDatas;
		this.orderAddonsDatas = orderAddonsDatas;

	}

	public OrderData(final Collection<MCodeData> disconnectDetails, final List<SubscriptionData> subscriptionDatas) {
		this.disconnectDetails = disconnectDetails;
		this.subscriptiondata = subscriptionDatas;
	}

	public OrderData(Long clientId, List<OrderData> clientOrders) {
		this.clientId = clientId;
		this.clientOrders = clientOrders;
	}
	/*
	 * public OrderData(Long clientId, List<OrderData>
	 * clientOrders,List<AssociationData> HardwareDatas) { this.clientId=clientId;
	 * this.clientOrders=clientOrders; this.setHardwareDatas(HardwareDatas); }
	 */

	public OrderData(Long orderId, String planCode, String planDescription, String billingFreq, String contractPeriod,
			Double price, LocalDate endDate) {

		this.id = orderId;
		this.planCode = planCode;
		this.planDescription = planDescription;
		this.billingFrequency = billingFreq;
		this.contractPeriod = contractPeriod;
		this.price = price;
		this.endDate = endDate;

	}

	public OrderData(String clientPoId, String clientServicePoId, String planPoId, String dealPoId, String orderNo) {
		this.clientPoId = clientPoId;
		this.clientServicePoId = clientServicePoId;
		this.planPoId = planPoId;
		this.dealPoId = dealPoId;
		this.orderNo = orderNo;
	}

	public OrderData(Long orderId, String planPoId, String dealPoId, Long priceId) {
		this.orderId = orderId;
		this.planPoId = planPoId;
		this.dealPoId = dealPoId;
		this.priceId = priceId;
	}

	public OrderData(String clientPoId, String clientServicePoId, String planPoId, String dealPoId, String orderNo,
			String oldPlanPoId, String oldDealPoId) {
		this.clientPoId = clientPoId;
		this.clientServicePoId = clientServicePoId;
		this.planPoId = planPoId;
		this.dealPoId = dealPoId;
		this.orderNo = orderNo;
		this.oldPlanPoId = oldPlanPoId;
		this.oldDealPoId = oldDealPoId;
	}

	public OrderData(Collection<MCodeData> extensionPeriodDatas, Collection<MCodeData> extensionReasonDatas) {

		this.extensionPeriodDatas = extensionPeriodDatas;
		this.reasons = extensionReasonDatas;
	}

	public OrderData(Long planId, Long planStatus) {
		this.planStatus = planStatus;
		this.pdid = planId;
	}

	public OrderData(Collection<MCodeData> disconnectDetails) {
		this.disconnectDetails = disconnectDetails;
	}

	public OrderData(Long orderId) {
		this.id = orderId;
	}

	public OrderData(Long id, Long clientId, Long clientServiceId, Long provisioningSystem,
			String provisioningSystemName, String commandName, String status) {

		this.id = id;
		this.clientId = clientId;
		this.clientServiceId = clientServiceId;
		this.provisioningSystem = provisioningSystem;
		this.provisioningSystemName = provisioningSystemName;
		this.commandName = commandName;
		this.status = status;

	}

	public OrderData(Long orderId, Long clientId, Long clientServiceId, Long planId, String isPrepaid) {
		this.id = orderId;
		this.clientId = clientId;
		this.clientServiceId = clientServiceId;
		this.planId = planId;
		this.isPrepaid = isPrepaid;
		// this.chargeOwner = chargeOwner;
	}

	public OrderData(Long orderId, Long clientId, Long clientServiceId, Long planId, String orderNo, String orderStatus,
			String planName, Long planType, String planDescription, LocalDate startDate, LocalDate endDate,
			LocalDate activeDate, LocalDate orderstartDate, LocalDate orderEndDate) {
		this.id = orderId;
		this.clientId = clientId;
		this.clientServiceId = clientServiceId;
		this.planId = planId;
		this.orderNo = orderNo;
		this.orderStatus = orderStatus;
		this.planType = planType;
		this.planCode = planName;
		this.planDescription = planDescription;
		this.planStartDate = startDate;
		this.planEndDate = endDate;
		this.activeDate = activeDate;
		this.startDate = orderstartDate;
		this.endDate = orderEndDate;

		// this.chargeOwner = chargeOwner;
	}

	public OrderData() {
	}

	public void setId(Long Id) {
		this.id = id;
	}

	public Long getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(Long planStatus) {
		this.planStatus = planStatus;
	}

	public Long getId() {
		return id;
	}

	public Long getPdid() {
		return pdid;
	}

	public void setPdid(Long pdid) {
		this.pdid = pdid;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getPlanDescription() {
		return planDescription;
	}

	public LocalDate getCurrentDate() {
		return currentDate;
	}

	public LocalDate getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(LocalDate activeDate) {
		this.activeDate = activeDate;
	}

	public String getContractPeriod() {
		return contractPeriod;
	}

	public Collection<MCodeData> getDisconnectDetails() {
		return disconnectDetails;
	}

	public List<OrderHistoryData> getOrderHistory() {
		return orderHistory;
	}

	public String getIsPrepaid() {
		return isPrepaid;
	}

	public String getAllowtopup() {
		return allowtopup;
	}

	public List<OrderData> getClientOrders() {
		return clientOrders;
	}

	public String getUserAction() {
		return userAction;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public OrderData getOrderData() {
		return orderData;
	}

	public String getProvisioningSys() {
		return provisioningSys;
	}

	public List<OrderLineData> getOrderServices() {
		return orderServices;
	}

	public List<OrderDiscountData> getOrderDiscountDatas() {
		return orderDiscountDatas;
	}

	public LocalDate getInvoiceTilldate() {
		return invoiceTilldate;
	}

	public Collection<MCodeData> getReasons() {
		return reasons;
	}

	public Collection<MCodeData> getExtensionPeriodDatas() {
		return extensionPeriodDatas;
	}

	public String getGroupName() {
		return groupName;
	}

	public List<OrderPriceData> getOrderPriceData() {
		return orderPriceData;
	}

	public Long getOrderPriceId() {
		return orderPriceId;
	}

	public String getService_code() {
		return service_code;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getPeriod() {
		return period;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getPlan_code() {
		return planCode;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getVariant() {
		return variant;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public List<PlanCodeData> getPlandata() {
		return plandata;
	}

	public List<PaytermData> getPaytermdata() {
		return paytermdata;
	}

	public List<SubscriptionData> getSubscriptiondata() {
		return subscriptiondata;
	}

	public void setPaytermData(List<PaytermData> data) {
		this.paytermdata = data;
	}

	public void setDisconnectDetails(Collection<MCodeData> disconnectDetails) {
		this.disconnectDetails = disconnectDetails;

	}

	public void setDuration(String duration) {
		this.contractPeriod = duration;

	}

	public void setplanType(String planType) {
		this.isPrepaid = planType;

	}

	/*
	 * public List<AssociationData> getHardwareDatas() { return HardwareDatas; }
	 * 
	 * public void setHardwareDatas(List<AssociationData> hardwareDatas) {
	 * HardwareDatas = hardwareDatas; }
	 */

	public Long getClientServiceId() {
		return clientServiceId;
	}

	public void setClientServiceId(Long clientServiceId) {
		this.clientServiceId = clientServiceId;
	}

	public List<NetworkElementData> getProvisioningSystems() {
		return provisioningSystems;
	}

	public void setOfficeData(Collection<OfficeData> officeData) {
		this.officeData = officeData;
	}

	public void setProvisioningSystems(List<NetworkElementData> provisioningSystems) {
		this.provisioningSystems = provisioningSystems;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poid) {
		this.poId = poid;
	}

	public static LocalDate getDate(long milli) {
		LocalDate d = new LocalDate(milli);
		return d;
	}

	public List<PlanCodeData> getSettlementPlandatas() {
		return settlementPlanDatas;
	}

	public String getClientPoId() {
		return clientPoId;
	}

	public void setClientPoId(String clientPoId) {
		this.clientPoId = clientPoId;
	}

	public String getPlanPoId() {
		return planPoId;
	}

	public void setPlanPoId(String planPoId) {
		this.planPoId = planPoId;
	}

	public String getDealPoId() {
		return dealPoId;
	}

	public void setDealPoId(String dealPoId) {
		this.dealPoId = dealPoId;
	}

	public String getClientServicePoId() {
		return clientServicePoId;
	}

	public void setClientServicePoId(String clientServicePoId) {
		this.clientServicePoId = clientServicePoId;
	}

	public Long getContractPeriodId() {
		return contractPeriodId;
	}

	public void setContractPeriodId(Long contractPeriodId) {
		this.contractPeriodId = contractPeriodId;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public long getPlanId() {
		return planId;
	}

	public void setPlanIdId(Long planId) {
		this.planId = planId;
	}

	public static List<OrderData> fromOBRMJson(String result, List<OrderData> orderDatas)
			throws JSONException, ParseException {
		List<OrderData> orders = new ArrayList<OrderData>();
		OrderData order = null;
		JSONObject object = new JSONObject(result);
		JSONObject serviceObj = null;
		object = object.optJSONObject("brm:MSO_OP_CUST_GET_CUSTOMER_INFO_outputFlist");
		JSONObject serviceInfo = object.optJSONObject("brm:SERVICE_INFO");
		JSONArray servicesArray = null;
		if (serviceInfo != null) {
			servicesArray = serviceInfo.optJSONArray("brm:SERVICES");
			if (servicesArray == null) {
				servicesArray = new JSONArray("[" + serviceInfo.optString("brm:SERVICES") + "]");
			}
		}
		JSONArray planListArray = null;
		for (int i = 0; i < servicesArray.length(); i++) {
			serviceObj = servicesArray.optJSONObject(i);
			planListArray = serviceObj.optJSONArray("brm:PLAN_LISTS");
			if (planListArray == null) {
				planListArray = new JSONArray("[" + serviceInfo.optString("brm:PLAN_LISTS") + "]");
			}
			for (int j = 0; j < planListArray.length(); j++) {
				JSONObject planListObject = planListArray.optJSONObject(j);
				JSONArray planArray = planListObject.optJSONArray("brm:PLAN");
				if (planArray == null) {
					planArray = new JSONArray("[" + serviceInfo.optString("brm:PLAN") + "]");
				}
				for (int k = 0; k < planArray.length(); k++) {
					JSONObject plan = planArray.optJSONObject(k);
					for (OrderData orderData : orderDatas) {
						if (/* orderData.getPoid() */"0.0.0.1 /plan 106151 2"
								.equalsIgnoreCase(plan.optString("brm:PLAN_OBJ"))) {
							order = orderData;
							break;
						}
					}
					orders.add(order);
				}
			}
		}
		return orders;
	}

	public static List<OrderData> fromCelcomJson(String result, List<OrderData> orderDatas)
			throws JSONException, ParseException {
		List<OrderData> orders = new ArrayList<OrderData>();
		OrderData order = null;
		JSONObject object = new JSONObject(result);
		JSONObject serviceObj = null;
		object = object.optJSONObject("brm:COB_OP_CUST_CUSTOMER_RETRIEVAL_outputFlist");
		JSONObject serviceInfo = object.optJSONObject("brm:SERVICE_INFO");
		JSONArray servicesArray = null;
		if (serviceInfo != null) {
			servicesArray = serviceInfo.optJSONArray("brm:SERVICES");
			if (servicesArray == null) {
				servicesArray = new JSONArray("[" + serviceInfo.optString("brm:SERVICES") + "]");
			}
		}
		JSONArray planListArray = null;
		for (int i = 0; i < servicesArray.length(); i++) {
			serviceObj = servicesArray.optJSONObject(i);
			planListArray = serviceObj.optJSONArray("brm:PLAN_LISTS");
			if (planListArray == null) {
				planListArray = new JSONArray("[" + serviceInfo.optString("brm:PLAN_LISTS") + "]");
			}
			for (int j = 0; j < planListArray.length(); j++) {
				JSONObject planListObject = planListArray.optJSONObject(j);
				JSONArray planArray = planListObject.optJSONArray("brm:PLAN");
				if (planArray == null) {
					planArray = new JSONArray("[" + serviceInfo.optString("brm:PLAN") + "]");
				}
				for (int k = 0; k < planArray.length(); k++) {
					JSONObject plan = planArray.optJSONObject(k);
					for (OrderData orderData : orderDatas) {
						if (/* orderData.getPoid() */"0.0.0.1 /plan 106151 2"
								.equalsIgnoreCase(plan.optString("brm:PLAN_OBJ"))) {
							order = orderData;
							break;
						}
					}
					orders.add(order);
				}
			}
		}
		return orders;
	}

	public static OrderData fromJsonToAddplan(String result) {
		String clientPoId = null, clientservicePoId = null, planPoId = null, dealPoId = null;
		JSONObject object;
		try {
			object = new JSONObject(result);
			clientservicePoId = object.getString("clientServicePoId");
			clientPoId = object.getString("clientPoId");
			planPoId = object.getString("planPoId");
			dealPoId = object.getString("dealPoId");
			return new OrderData(clientPoId, clientservicePoId, planPoId, dealPoId, null);
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException("error.msg.number.generatsor.not.available", "", "", "");
		}
	}

	public static OrderData fromJsonToCancelplan(String json) {
		String clientPoId = null, clientServicePoId = null, planPoId = null, dealPoId = null, orderNo = null;
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			clientPoId = jsonObject.getString("clientPoId");
			clientServicePoId = jsonObject.getString("clientServicePoId");
			planPoId = jsonObject.getString("planPoId");
			dealPoId = jsonObject.getString("dealPoId");
			orderNo = jsonObject.getString("orderNo");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new OrderData(clientPoId, clientServicePoId, planPoId, dealPoId, orderNo);
	}

	public static OrderData fromJsonToChangePlan(String json) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String newPlanPoId = jsonObject.optString("planPoId");
		String newDealPoId = jsonObject.optString("dealPoId");
		String clientPoId = jsonObject.optString("clientPoId");
		String clientServicePoId = jsonObject.optString("clientServicePoId");
		String oldPlanPoId = jsonObject.optString("oldPlanPoId");
		String oldDealPoId = jsonObject.optString("oldDealPoId");
		String orderNo = jsonObject.optString("orderNo");

		return new OrderData(clientPoId, clientServicePoId, newPlanPoId.toString(), newDealPoId, orderNo, oldPlanPoId,
				oldDealPoId);
	}

	public String celcomRequestInput(String userName) {
		int packageId = 10000 + new Random().nextInt(89999);
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_ADD_PLAN_inputFlist>");
		sb.append("<POID>0.0.0.1 /account " + this.clientPoId + " 0</POID>");
		sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv " + this.clientServicePoId + " 0</SERVICE_OBJ>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account " + this.clientPoId + " 0</ACCOUNT_OBJ>");
		sb.append("<PROGRAM_NAME>CRM|" + userName + "</PROGRAM_NAME>");
		sb.append("<PLAN_LIST_CODE>");
		sb.append("<PLAN elem=\"0\">");
		sb.append("<PLAN_OBJ>0.0.0.1 /plan " + this.planPoId + " 2</PLAN_OBJ>");
		sb.append("<PACKAGE_ID>" + packageId + "</PACKAGE_ID>");
		sb.append("<DEALS elem=\"0\">");
		sb.append("<DEAL_OBJ>0.0.0.1 /deal " + this.dealPoId + " 1</DEAL_OBJ>");
		sb.append("</DEALS>");
		sb.append("</PLAN>");
		sb.append("</PLAN_LIST_CODE>");
		sb.append("</COB_OP_CUST_ADD_PLAN_inputFlist>");
		System.out.println(sb.toString());
		return sb.toString();
	}

	public String celcomRequestInputForCancelPlan(String userName) {

		StringBuilder sb = new StringBuilder("<COB_OP_CUST_CANCEL_PLAN_inputFlist>");
		sb.append("<POID>0.0.0.1 /account " + this.clientPoId + " 0</POID>");
		sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv " + this.clientServicePoId + " 0</SERVICE_OBJ>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account " + this.clientPoId + " 0</ACCOUNT_OBJ>");
		sb.append("<PROGRAM_NAME>CRM|" + userName + "</PROGRAM_NAME>");
		sb.append("<PLAN_LIST_CODE>");
		sb.append("<PLAN elem=\"0\">");
		sb.append("<PLAN_OBJ>0.0.0.1 /plan " + this.planPoId + " 2</PLAN_OBJ>");
		sb.append("<DEALS elem=\"0\">");
		sb.append("<PACKAGE_ID>" + this.orderNo + "</PACKAGE_ID>");
		sb.append("<DEAL_OBJ>0.0.0.1 /deal " + this.dealPoId + " 1</DEAL_OBJ>");
		sb.append("</DEALS>");
		sb.append("</PLAN>");
		sb.append("</PLAN_LIST_CODE>");
		sb.append("</COB_OP_CUST_CANCEL_PLAN_inputFlist>");
		return sb.toString();

	}

	public String celcomRequestInputForChangePlan(String userName, String order) {

		int packageId = 10000 + new Random().nextInt(89999);
		int i = 0;
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_CHANGE_PLAN_inputFlist>");
		sb.append("<POID>0.0.0.1 /account " + this.clientPoId + " 0</POID>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account " + this.clientPoId + " 0</ACCOUNT_OBJ>");
		sb.append("<PROGRAM_NAME>CRM|" + userName + "</PROGRAM_NAME>");
		sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv " + this.clientServicePoId + " 0</SERVICE_OBJ>");
		sb.append("<FLAGS>0</FLAGS>");
		sb.append("<PLAN_LIST_CODE>");
		sb.append("<PLAN elem=\"0\">");
		sb.append("<PLAN_OBJ>0.0.0.1 /plan " + this.oldPlanPoId + " 2</PLAN_OBJ>");
		sb.append("<PACKAGE_ID>" + this.orderNo + "</PACKAGE_ID>");
		sb.append("<DEALS elem=\"0\">");
		sb.append("<DEAL_OBJ>0.0.0.1 /deal " + this.oldDealPoId + " 1</DEAL_OBJ>");
		sb.append("</DEALS>");
		sb.append("</PLAN>");
		sb.append("<PLAN elem=\"1\">");
		sb.append("<PLAN_OBJ>0.0.0.1 /plan " + this.planPoId + " 2</PLAN_OBJ>");
		sb.append("<PACKAGE_ID>" + order + "</PACKAGE_ID>");
		sb.append("<DEALS elem=\"0\">");
		sb.append("<DEAL_OBJ>0.0.0.1 /deal " + this.dealPoId + " 1</DEAL_OBJ>");
		sb.append("</DEALS>");
		sb.append("</PLAN>");
		sb.append("</PLAN_LIST_CODE>");
		sb.append("</COB_OP_CUST_CHANGE_PLAN_inputFlist>");
		return sb.toString();

	}

	public static String celcomRequestInputForAddPlans(List<OrderData> multiplePlans, String userName, String orderNo) {

		int i = 0;

		StringBuilder sb = new StringBuilder("<COB_OP_CUST_ADD_PLAN_inputFlist>");
		for (OrderData plan : multiplePlans) {
			if (i == 0) {
				sb.append("<POID>0.0.0.1 /account " + plan.getClientPoId() + " 0</POID>");
				sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv " + plan.getClientServicePoId() + " 0</SERVICE_OBJ>");
				sb.append("<ACCOUNT_OBJ>0.0.0.1 /account " + plan.getClientPoId() + " 0</ACCOUNT_OBJ>");
				sb.append("<PROGRAM_NAME>CRM|" + userName + "</PROGRAM_NAME>");
				sb.append("<PLAN_LIST_CODE>");
			}
			sb.append("<PLAN elem=\"" + i + "\">");
			sb.append("<PLAN_OBJ>0.0.0.1 /plan " + plan.getPlanPoId() + " 2</PLAN_OBJ>");
			if (i == 0) {
				sb.append("<PACKAGE_ID>" + (orderNo) + "</PACKAGE_ID>");
			} else {
				sb.append("<PACKAGE_ID>" + (Long.parseLong(orderNo) + 1) + "</PACKAGE_ID>");
			}
			sb.append("<DEALS elem=\"" + i + "\">");
			sb.append("<DEAL_OBJ>0.0.0.1 /deal " + plan.getDealPoId() + " 1</DEAL_OBJ>");
			sb.append("</DEALS>");
			sb.append("</PLAN>");
			i++;
		}
		sb.append("</PLAN_LIST_CODE>");
		sb.append("</COB_OP_CUST_ADD_PLAN_inputFlist>");
		return sb.toString();
	}

	public static String celcomRequestInputForCancelPlans(List<OrderData> multiplePlans, String userName) {

		int packageId = 10000 + new Random().nextInt(89999);
		int i = 0;

		StringBuilder sb = new StringBuilder("<COB_OP_CUST_CANCEL_PLAN_inputFlist>");
		for (OrderData plan : multiplePlans) {
			if (i == 0) {
				sb.append("<POID>0.0.0.1 /account " + plan.getClientPoId() + " 0</POID>");
				sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv " + plan.getClientServicePoId() + " 0</SERVICE_OBJ>");
				sb.append("<ACCOUNT_OBJ>0.0.0.1 /account " + plan.getClientPoId() + " 0</ACCOUNT_OBJ>");
				sb.append("<PROGRAM_NAME>CRM|" + userName + "</PROGRAM_NAME>");
				sb.append("<PLAN_LIST_CODE>");
			}
			sb.append("<PLAN elem=\"" + i + "\">");
			sb.append("<PLAN_OBJ>0.0.0.1 /plan " + plan.getPlanPoId() + " 2</PLAN_OBJ>");
			sb.append("<DEALS elem=\"" + i + "\">");
			sb.append("<PACKAGE_ID>" + plan.getOrderNo() + "</PACKAGE_ID>");
			sb.append("<DEAL_OBJ>0.0.0.1 /deal " + plan.getDealPoId() + " 1</DEAL_OBJ>");
			sb.append("</DEALS>");
			sb.append("</PLAN>");
			i++;
		}
		sb.append("</PLAN_LIST_CODE>");
		sb.append("</COB_OP_CUST_CANCEL_PLAN_inputFlist>");
		return sb.toString();
	}

	public static OrderData fromJsonToRenewalPlan(JsonCommand command) {

		String clientPoId = command.stringValueOfParameterName("clientPoId");
		String clientServicePoId = command.stringValueOfParameterName("clientServicePoId");
		String planPoId = command.stringValueOfParameterName("planPoId");
		String dealPoId = command.stringValueOfParameterName("dealPoId");
		String orderNo = command.stringValueOfParameterName("orderNo");

		return new OrderData(clientPoId, clientServicePoId, planPoId, dealPoId, orderNo);
	}

	public String celcomRequestInputForRenewalPlan(String logedInUser) {
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_RENEW_PLAN_inputFlist>");
		sb.append("<POID>0.0.0.1 /account " + this.clientPoId + " 0</POID>");
		sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv " + this.clientServicePoId + " 0</SERVICE_OBJ>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account " + this.clientPoId + " 0</ACCOUNT_OBJ>");
		sb.append("<PROGRAM_NAME>CRM|" + logedInUser + "</PROGRAM_NAME>");
		sb.append("<FLAGS>0</FLAGS>");
		sb.append("<PLAN_LIST_CODE>");
		sb.append("<PLAN elem=\"0\">");
		sb.append("<PLAN_OBJ>0.0.0.1 /plan " + this.planPoId + " 2</PLAN_OBJ>");
		sb.append("<PACKAGE_ID>" + this.orderNo + "</PACKAGE_ID>");
		sb.append("<DEALS elem=\"0\">");
		sb.append("<DEAL_OBJ>0.0.0.1 /deal " + this.dealPoId + " 1</DEAL_OBJ>");
		sb.append("<PURCHASE_END_T>1527791400</PURCHASE_END_T>");
		sb.append("</DEALS>");
		sb.append("</PLAN>");
		sb.append("</PLAN_LIST_CODE>");
		sb.append("</COB_OP_CUST_RENEW_PLAN_inputFlist>");

		return sb.toString();
	}

	public Long getOrderId() {
		return orderId;
	}

	public Long getPriceId() {
		return priceId;
	}

	public Long getProvisioningSystem() {
		return provisioningSystem;
	}

	public double getNonCurrency() {
		return nonCurrency;
	}

	public void setNonCurrency(double nonCurrency) {
		this.nonCurrency = nonCurrency;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getAutoRenew() {
		return autoRenew;
	}

	public void setAutoRenew(String autoRenew) {
		this.autoRenew = autoRenew;
	}

	
}
