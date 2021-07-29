package org.mifosplatform.portfolio.clientservice.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;

public class ClientServiceData {
	
	


	private Long id;
	private Long serviceId;
	private String serviceCode;
	private String serviceDescription;
	private String status;
	private String casName;
	private LocalDate lastmodifieddate;
	private String clientPoId;
	private LocalDate startdate;
	private Long clientId;
	
	private List<ServiceData> serviceData;
	List<ServiceParameterData> ServiceParameterData;
	private String accountNumber;
	private List<PlanData> planData;
	private String stbNo;
	private String scNo;
	
	private List<Order> ordersList;
	private ClientService clientService;
	private String clientServicePoId;
	public ClientServiceData() {
	}

	public ClientServiceData(Long clientId, Long clientServiceId){
		this.clientId =  clientId;
		this.id = clientServiceId;
	}
	
	public ClientServiceData(Long id, Long serviceId, String serviceCode, String serviceDescription, 
			String status,String clientServicePoId) {
		this.id = id;
		this.serviceId = serviceId;
		this.serviceCode = serviceCode;
		this.serviceDescription = serviceDescription;
		this.status = status;
		this.clientServicePoId = clientServicePoId;
	}


	
	//its used for the purpose of obrm functionality
	public ClientServiceData(String accountNumber,String clientPoId, List<PlanData> planData, 
			String stbNo, String scNo,String casName) {
		this.accountNumber = accountNumber;
		this.clientPoId = clientPoId;
		this.planData = planData;
		this.stbNo = stbNo;
		this.scNo = scNo;
		this.casName = casName;
	}


	public ClientServiceData(final List<org.mifosplatform.provisioning.provisioning.data.ServiceParameterData> serviceParameterData) {
		ServiceParameterData = serviceParameterData;
	}

	public static ClientServiceData instance(Long id, Long serviceId, String serviceCode, String serviceDescription, String status){
		return new ClientServiceData(null,null,serviceCode,null,status,null);
	
	}
	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getServiceId() {
		return serviceId;
	}


	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}


	public String getServiceCode() {
		return serviceCode;
	}


	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}


	public String getServiceDescription() {
		return serviceDescription;
	}


	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public List<ServiceData> getServiceData() {
		return serviceData;
	}


	public void setServiceData(List<ServiceData> serviceData) {
		this.serviceData = serviceData;
	}


	public List<ServiceParameterData> getServiceParameterData() {
		return ServiceParameterData;
	}


	public void setServiceParameterData(List<ServiceParameterData> serviceParameterData) {
		ServiceParameterData = serviceParameterData;
	}

	public void setClientPoId(String clientPoId) {
		this.clientPoId = clientPoId;
	}

	
	
	
	public String getCasName() {
		return casName;
	}


	public void setCasName(String casName) {
		this.casName = casName;
	}

	public LocalDate getLastmodifieddate() {
		return lastmodifieddate;
	}


	public void setLastmodifieddate(LocalDate lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}
	
	public LocalDate getstartdate() {
		return startdate;
	}


	public void setstartdate(LocalDate startdate) {
		this.startdate = startdate;
	}
	

	public String getClientServicePoId() {
		return clientServicePoId;
	}


	public void setClientServicePoId(String clientServicePoId) {
		this.clientServicePoId = clientServicePoId;
	}


	public String getClientPoId() {
		return clientPoId;
	}


	//converting obrm given result to clientService Object
	public static List<ClientServiceData> fromOBRMJson(String result, List<ClientServiceData> clientServiceDatas) throws JSONException{
		List<ClientServiceData> clientserviceDatas = new ArrayList<ClientServiceData>();
		ClientServiceData clientServiceData = null;
		JSONObject object = new JSONObject(result);
		JSONObject serviceObj = null;
		object = object.optJSONObject("brm:MSO_OP_CUST_GET_CUSTOMER_INFO_outputFlist");
		JSONObject serviceInfo = object.optJSONObject("brm:SERVICE_INFO");
		JSONArray servicesArray = null;
		if(serviceInfo !=null){
			servicesArray = serviceInfo.optJSONArray("brm:SERVICES");
			if(servicesArray ==null){
				servicesArray = new JSONArray( "["+serviceInfo.optString("brm:SERVICES")+"]");
			}
		}
		for(int i=0;i<servicesArray.length();i++){
			serviceObj = servicesArray.optJSONObject(i);
			clientServiceData = new ClientServiceData();
			for(ClientServiceData cs:clientServiceDatas){
				if(/*cs.getPoid()*/"0.0.0.1 /service/catv 475006 11".equalsIgnoreCase(serviceObj.getString("brm:POID"))){
					clientServiceData.setId(cs.getId());
					clientServiceData.setServiceId(cs.getServiceId());
					clientServiceData.setServiceDescription(serviceObj.optString("brm:NAME"));
					clientServiceData.setServiceCode(serviceObj.optString("brm:NAME"));
					
					if("10100".equalsIgnoreCase(serviceObj.optString("brm:STATUS"))){
						clientServiceData.setStatus("ACTIVE");	
					}else{
						clientServiceData.setStatus("TERMINATED");	
					}
					
					clientserviceDatas.add(clientServiceData);
				}
				
			}
		}
		return clientserviceDatas;
	}
	

	//converting obrm given result to clientService Object
		public static List<ClientServiceData> fromCelcomJson(String result, List<ClientServiceData> clientServiceDatas) throws JSONException{
			List<ClientServiceData> clientserviceDatas = new ArrayList<ClientServiceData>();
			ClientServiceData clientServiceData = null;
			JSONObject object = new JSONObject(result);
			JSONObject serviceObj = null;
			object = object.optJSONObject("brm:COB_OP_CUST_CUSTOMER_RETRIEVAL_outputFlist");
			JSONObject serviceInfo = object.optJSONObject("brm:SERVICE_INFO");
			JSONArray servicesArray = null;
			if(serviceInfo !=null){
				servicesArray = serviceInfo.optJSONArray("brm:SERVICES");
				if(servicesArray ==null){
					servicesArray = new JSONArray( "["+serviceInfo.optString("brm:SERVICES")+"]");
				}
			}
			for(int i=0;i<servicesArray.length();i++){
				serviceObj = servicesArray.optJSONObject(i);
				clientServiceData = new ClientServiceData();
				for(ClientServiceData cs:clientServiceDatas){
					;
					if(retriveCSPoId(serviceObj.optString("brm:POID")).equalsIgnoreCase(cs.getClientServicePoId())){
						clientServiceData.setId(cs.getId());
						clientServiceData.setServiceId(cs.getServiceId());
						clientServiceData.setServiceDescription(serviceObj.optString("brm:NAME"));
						clientServiceData.setServiceCode(serviceObj.optString("brm:NAME"));
						
						if("10100".equalsIgnoreCase(serviceObj.optString("brm:STATUS"))){
							clientServiceData.setStatus("ACTIVE");	
						}else{
							clientServiceData.setStatus("TERMINATED");	
						}
						
						clientserviceDatas.add(clientServiceData);
					}
					
				}
			}
			return clientserviceDatas;
		}
	
	
	private static String retriveCSPoId(String returnValue) {
		if(returnValue != null){
			String[] args = returnValue.split(" ");
			returnValue = args[2];
			return returnValue;
		}
		else{
			return "abc";
		}
	}


	//preparing input payload for obrm process request
	public String obrmRequestInput() {
		int i=0;
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		sb.append("<MSO_OP_CUST_ACTIVATE_CUSTOMER_inputFlist><ACCOUNT_NO>"+this.accountNumber+"</ACCOUNT_NO>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account 460915 0</ACCOUNT_OBJ><BILLINFO elem=\"0\"><BILL_WHEN>1</BILL_WHEN>");
		sb.append("</BILLINFO><BUSINESS_TYPE>99001100</BUSINESS_TYPE><PAYINFO elem=\"0\">");
		sb.append("<INHERITED_INFO><INV_INFO elem=\"0\"><INDICATOR>0</INDICATOR></INV_INFO></INHERITED_INFO></PAYINFO>");
		sb.append("<PLAN_LIST_CODE>");
		for(PlanData plan:this.planData){
			sb.append("<PLAN elem=\""+i+"\"><CODE>"+plan.getplanCode()+"</CODE><PLAN_OBJ>0.0.0.1 /plan "+plan.getPlanPoid()+" 2</PLAN_OBJ>");
			sb.append("<PURCHASE_START_T>1519534358</PURCHASE_START_T> </PLAN>");
			i=i+1;
		}
		i=0;
		sb.append("</PLAN_LIST_CODE>");
		sb.append("<POID>0.0.0.1 /plan -1 0</POID><PROGRAM_NAME>BULK|OAP|testcsrone</PROGRAM_NAME>");
		sb.append("<SERVICES elem=\"0\"><MSO_FLD_CATV_INFO>");
		sb.append("<MSO_FLD_BOX_INSTALLED_T>0</MSO_FLD_BOX_INSTALLED_T><MSO_FLD_CARF_RECEIVED>0</MSO_FLD_CARF_RECEIVED>");
		sb.append("<MSO_FLD_LCO_BOX_RECV_T>0</MSO_FLD_LCO_BOX_RECV_T><MSO_FLD_SALESMAN_OBJ/></MSO_FLD_CATV_INFO>");
		if(this.stbNo !=null){
			sb.append("<DEVICES elem=\""+i+"\"><MSO_FLD_STB_ID>"+this.stbNo+"</MSO_FLD_STB_ID></DEVICES>");
			i++;
		}
		if(this.scNo !=null){
			sb.append("<DEVICES elem=\""+i+"\"><MSO_FLD_VC_ID>"+this.scNo+"</MSO_FLD_VC_ID></DEVICES>");
		}
		sb.append("<SERVICE_OBJ>0.0.0.1 /service/catv -1 0</SERVICE_OBJ>");
		sb.append("</SERVICES><USERID>0.0.0.1 /account 452699 0</USERID></MSO_OP_CUST_ACTIVATE_CUSTOMER_inputFlist>");
		
		return sb.toString();
	}


	//preparing input payload for obrm process request
		public String celcomRequestInput(String userName,String packageId) {
			int i =0;
			StringBuilder sb = new StringBuilder("<COB_OP_CUST_ACTIVATE_SERVICE_inputFlist>");
			sb.append("<POID>0.0.0.1 /plan -1 8</POID>");
			sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.clientPoId+" 8</ACCOUNT_OBJ>");
			sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
			sb.append("<SERVICES elem=\"0\">");
     		sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv -1 0</SERVICE_OBJ>");
			sb.append("<INHERITED_INFO>");
			sb.append("<COB_FLD_CABLE_INFO>");
            sb.append("<COB_FLD_AGREEMENT_NO>MANTHAN-201</COB_FLD_AGREEMENT_NO>");
			sb.append("<COB_FLD_CAS_SUBSCRIBER_ID>"+this.casName+"-001</COB_FLD_CAS_SUBSCRIBER_ID>");
			sb.append("<CONN_TYPE>0</CONN_TYPE>");
			sb.append("<BILL_WHEN>1</BILL_WHEN>");
			sb.append("<CITY>BANGALORE</CITY>");
			sb.append("<COB_FLD_NETWORK_NODE>"+this.casName+"</COB_FLD_NETWORK_NODE>");
			sb.append("</COB_FLD_CABLE_INFO>");
			sb.append("</INHERITED_INFO>");
			if(this.stbNo !=null){
				sb.append("<DEVICES elem=\""+i+"\">");
				sb.append("<COB_FLD_STB_ID>"+this.stbNo+"</COB_FLD_STB_ID>");
				sb.append("</DEVICES>");i++;
			}
			if(this.scNo !=null){
				sb.append("<DEVICES elem=\""+i+"\">");
				sb.append("<COB_FLD_VC_ID>"+this.scNo+"</COB_FLD_VC_ID>");
				sb.append("</DEVICES>");
			}i=0;
			sb.append("<LOGIN>"+packageId+"</LOGIN>");
			sb.append("<PASSWD_CLEAR>"+packageId+"</PASSWD_CLEAR>");
			sb.append("</SERVICES>");
			sb.append("<PLAN_LIST_CODE>");
			for(PlanData plan:this.planData){
				sb.append("<PLAN elem=\""+i+"\">");
				sb.append("<PLAN_OBJ>0.0.0.1 /plan "+plan.getPlanPoid()+" 2</PLAN_OBJ>");
				sb.append("<PACKAGE_ID>"+packageId+"</PACKAGE_ID>");
				sb.append("</PLAN>");i++;packageId = packageId+"-1";
			}
			sb.append("</PLAN_LIST_CODE>");
			sb.append("</COB_OP_CUST_ACTIVATE_SERVICE_inputFlist>");
		
			System.out.println(sb.toString());
			return sb.toString();
	}
	
	
	public static ClientServiceData fromJsonForOBRM(JsonCommand command) {
		Map<String,String> serialNo = new HashMap<String,String>();
		String casName = null;
		try {
		
			JSONArray clientServiceArray = new JSONArray(command.arrayOfParameterNamed("clientServiceData").toString());
			JSONObject clientService = clientServiceArray.getJSONObject(0);
			String accountNo = clientService.optString("accountNo");
			String clientPoId = clientService.optString("clientPoId");
			JSONArray clientServiceDetailArray = clientService.optJSONArray("clientServiceDetails");
			if(clientServiceDetailArray != null){
				casName = clientServiceDetailArray.optJSONObject(0).optString("parameterValue");
			}
			JSONArray deviceArray = new JSONArray(command.arrayOfParameterNamed("deviceData").toString());
			JSONObject deviceData = deviceArray.getJSONObject(0);
			serialNo = deviceDatafromJson(deviceData,serialNo);
			serialNo = deviceDatafromJson(deviceData.optJSONObject("pairableItemDetails"),serialNo);
			
			JSONArray planArray = new JSONArray(command.arrayOfParameterNamed("planData").toString());
			List<PlanData> planDatas = new ArrayList<PlanData>();
			PlanData planData = null;
			for(int i=0;i<planArray.length();i++){
				JSONObject plan = planArray.getJSONObject(i);
				planData = new PlanData(plan.optString("planName"),plan.optLong("planPoId"),plan.optLong("dealPoId"));
				
				planDatas.add(planData);
				
			}
			return new ClientServiceData(accountNo, clientPoId,planDatas, serialNo.get("stbNo"),serialNo.get("scNo"),casName);
		
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException("error.msg.parse.exception", e.getMessage(), e.getMessage(), e.getMessage());
		}
	}


	
	public static ClientServiceData fromJsonForCelcom(JsonCommand command) {
		Map<String,String> serialNo = new HashMap<String,String>();
		String casName = null;
		try {
		
			JSONArray clientServiceArray = new JSONArray(command.arrayOfParameterNamed("clientServiceData").toString());
			JSONObject clientService = clientServiceArray.getJSONObject(0);
			String accountNo = clientService.optString("accountNo");
			String clientPoId = clientService.optString("clientPoId");
			JSONArray clientServiceDetailArray = clientService.optJSONArray("clientServiceDetails");
			if(clientServiceDetailArray != null){
				casName = clientServiceDetailArray.optJSONObject(0).optString("parameterValue");
			}
			JSONArray deviceArray = new JSONArray(command.arrayOfParameterNamed("deviceData").toString());
			JSONObject deviceData = deviceArray.getJSONObject(0);
			serialNo = deviceDatafromJson(deviceData,serialNo);
			serialNo = deviceDatafromJson(deviceData.optJSONObject("pairableItemDetails"),serialNo);
			
			JSONArray planArray = new JSONArray(command.arrayOfParameterNamed("planData").toString());
			List<PlanData> planDatas = new ArrayList<PlanData>();
			PlanData planData = null;
			for(int i=0;i<planArray.length();i++){
				JSONObject plan = planArray.getJSONObject(i);
				Long planPoId = plan.optLong("planPoId");
				Long dealPoId = plan.optLong("dealPoId");
				planData = new PlanData(plan.optString("planName"),
						planPoId,
						dealPoId);
				
				planDatas.add(planData);
				
			}
			return new ClientServiceData(accountNo,clientPoId, planDatas, serialNo.get("stbNo"),serialNo.get("scNo"),casName);
		
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException("error.msg.parse.exception", e.getMessage(), e.getMessage(), e.getMessage());
		}
	}
	
	
	private static Map<String,String> deviceDatafromJson(JSONObject deviceData,Map<String,String> serialNo) throws JSONException {
			if(deviceData != null){
				JSONArray serialNoArray = new JSONArray(deviceData.getString("serialNumber"));
				JSONObject serialNoObject = serialNoArray.getJSONObject(0);
				if("STB".equalsIgnoreCase(serialNoObject.optString("itemType"))){
					serialNo.put("stbNo",serialNoObject.optString("serialNumber"));
				}else{
					serialNo.put("scNo",serialNoObject.optString("serialNumber"));
				}
			}	
			return serialNo;
	}


	public static ClientServiceData fromJsonToSuspend(JsonCommand command) {
		ClientServiceData data = new ClientServiceData();
		
		data.setClientServicePoId(command.stringValueOfParameterName("clientServicePoId"));
		data.setClientPoId(command.stringValueOfParameterName("clientPoId"));
		return data;
	}


	public String celcomRequestInputForSuspend(String userName) {
		
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_SUSPEND_SERVICE_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.clientPoId+" 0</POID>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<SERVICES elem=\"0\">");
		sb.append("<POID>0.0.0.1 /service/tv "+this.clientServicePoId+" 1</POID>");
		sb.append("<STATUS>10102</STATUS>");
		sb.append("<STATUS_FLAGS>1</STATUS_FLAGS>");
		sb.append("</SERVICES>");
		sb.append("</COB_OP_CUST_SUSPEND_SERVICE_inputFlist>");
		
		return sb.toString();
	}

	public String celcomRequestInputForTerminate(String userName) {
		
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_TERMINATE_SERVICE_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.clientPoId+" 0</POID>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<SERVICES elem=\"0\">");
		sb.append("<POID>0.0.0.1 /service/tv "+this.clientServicePoId+" 1</POID>");
		sb.append("<STATUS>10103</STATUS>");
		sb.append("<STATUS_FLAGS>1</STATUS_FLAGS>");
		sb.append("</SERVICES>");
		sb.append("</COB_OP_CUST_TERMINATE_SERVICE_inputFlist>");
		
		return sb.toString();
	}
	
	
	public String celcomRequestInputForReActivate(String userName) {
		
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_REACTIVATE_SERVICE_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.clientPoId+" 0</POID>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<SERVICES elem=\"0\">");
		sb.append("<POID>0.0.0.1 /service/tv "+this.clientServicePoId+" 1</POID>");
		sb.append("<STATUS>10100</STATUS>");
		sb.append("<STATUS_FLAGS>1</STATUS_FLAGS>");
		sb.append("</SERVICES>");
		sb.append("</COB_OP_CUST_REACTIVATE_SERVICE_inputFlist>");
		
		return sb.toString();
	}


	//preparing input payload for obrm process request
			public String celcomRequestInputForPlanActivation(String userName) {
				int i =0;
				int packageId = 1000 + new Random().nextInt(9000);
				StringBuilder sb = new StringBuilder("<COB_OP_CUST_ACTIVATE_SERVICE_inputFlist>");
				sb.append("<POID>0.0.0.1 /plan -1 8</POID>");
				sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.clientPoId+" 8</ACCOUNT_OBJ>");
				sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
				sb.append("<SERVICES elem=\"0\">");
	     		sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv -1 0</SERVICE_OBJ>");
				sb.append("<INHERITED_INFO>");
				sb.append("<COB_FLD_CABLE_INFO>");
	            sb.append("<COB_FLD_AGREEMENT_NO>MANTHAN-201</COB_FLD_AGREEMENT_NO>");
				sb.append("<COB_FLD_CAS_SUBSCRIBER_ID>"+this.casName+"-001</COB_FLD_CAS_SUBSCRIBER_ID>");
				sb.append("<CONN_TYPE>0</CONN_TYPE>");
				sb.append("<BILL_WHEN>1</BILL_WHEN>");
				sb.append("<CITY>BANGALORE</CITY>");
				sb.append("<COB_FLD_NETWORK_NODE>"+this.casName+"</COB_FLD_NETWORK_NODE>");
				sb.append("</COB_FLD_CABLE_INFO>");
				sb.append("</INHERITED_INFO>");
				if(this.stbNo !=null){
					sb.append("<DEVICES elem=\""+i+"\">");
					sb.append("<COB_FLD_STB_ID>"+this.stbNo+"</COB_FLD_STB_ID>");
					sb.append("</DEVICES>");i++;
				}
				if(this.scNo !=null){
					sb.append("<DEVICES elem=\""+i+"\">");
					sb.append("<COB_FLD_VC_ID>"+this.scNo+"</COB_FLD_VC_ID>");
					sb.append("</DEVICES>");
				}i=0;
				sb.append("<LOGIN>"+packageId+"</LOGIN>");
				sb.append("<PASSWD_CLEAR>"+packageId+"</PASSWD_CLEAR>");
				sb.append("</SERVICES>");
				sb.append("<PLAN_LIST_CODE>");
				for(PlanData plan:this.planData){
					sb.append("<PLAN elem=\""+i+"\">");
					sb.append("<PLAN_OBJ>0.0.0.1 /plan "+plan.getPlanPoid()+" 2</PLAN_OBJ>");
					sb.append("<PACKAGE_ID>"+packageId+"</PACKAGE_ID>");
					sb.append("</PLAN>");i++;packageId++;
				}
				sb.append("</PLAN_LIST_CODE>");
				sb.append("</COB_OP_CUST_ACTIVATE_SERVICE_inputFlist>");
			
				System.out.println(sb.toString());
				return sb.toString();
		}


			public Long getClientId() {
				return clientId;
			}


			public void setClientId(Long clientId) {
				this.clientId = clientId;
			}


			public  ClientServiceData(long id2, long clientId2, Long poId,
					Long clientServicePoId) {
			
				 this.id=id2;
		         this.clientId=clientId2;
		         this.clientPoId = poId.toString();;
		         this.clientServicePoId =clientServicePoId.toString();
				
				
			}
			
			

			public ClientServiceData(long clientServiceId,Long clientServicePoId2, Long clientPoid2) {
				this.id=clientServiceId;
			    this.clientServicePoId =clientServicePoId2.toString();
			    this.clientPoId = clientPoid2.toString();;
			    
			}


			


			
			


			
	/*public static ClientServiceData fromJson(final JsonCommand command) {
		
		final String accountNumber = command.stringValueOfParameterNamed("accountNumber");
		final String stbId = command.stringValueOfParameterNamed("stbId");
		final String vcId = command.stringValueOfParameterNamed("vcId");
		
		return new ClientServiceData(accountNumber,stbId,vcId);
		
	
	}*/


	/*public String obrmRequestInput() {
		StringBuilder cs = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><MSO_OP_CUST_ACTIVATE_CUSTOMER_inputFlist>");
		cs.append("<ACCOUNT_NO>"+this.accountNumber+"<ACCOUNT_NO>");
		cs.append("<MSO_FLD_STB_ID>"+this.stbId+"</MSO_FLD_STB_ID>");
		cs.append("<MSO_FLD_VC_ID>"+this.vcId+"<MSO_FLD_VC_ID>");
		System.out.println(cs.toString());
		return cs.toString();
	}*/
	

}
