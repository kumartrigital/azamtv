package org.mifosplatform.provisioning.entitlements.data;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class EntitlementsData {

	private Long id;
	private Long prdetailsId;
	private String provisioingSystem;

	private Long serviceId;
	private String product;
	private String hardwareId;

	private String requestType;
	private String itemCode;
	private String itemDescription;

	private Long clientId;
	private String accountNo;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String city;
	private String address;
	private String zip;

	private String officeUId;
	private String branch;
	private String regionCode;
	private String regionName;

	private String status;
	private StakerData results;
	private String error;
	private Long planId;
	private String orderNo;

	private String deviceId;
	private String ipAddress;
	private String serviceType;
	private Long orderId;
	private LocalDate startDate;
	private LocalDate endDate;
	
	private Long zebraSubscriberId;
	private BigDecimal itemPrice;
	private Long itemId;
	
	private String displayName;
	private String login;
	private String password;
	private String subscriberId;
	private String selfcareUsername;
	private String selfcarePassword;
	
	private String street;
	private String country;
	private Long countryId;
	private String macId;
	private Long regionId;
	private Long officeId;
	private String countryISD;
	private Long taskNo;
	private Long priority;
	private String jsonDetails;
	private Long version;
	private String action_name;
	private String entity_name;
	private LocalDate made_on_date;
	private Long resource_id;
	private Long client_id;
	private String command_as_json;
	public EntitlementsData(){
		
	}
	
	//Beenius
	public EntitlementsData(Long id, Long prdetailsId,String provisioingSystem, Long serviceId, String product,String hardwareId, 
			String requestType, String itemCode,String itemDescription, Long clientId, String accountNo,String firstName, String lastName,
			String officeUId, String branch,String regionCode, String regionName, String deviceId,String ipAddress, String userName,
			 String selfcarePassword, Long subscriberId,Long orderId,LocalDate startDate,LocalDate endDate) {

		this.id = id;
		this.prdetailsId = prdetailsId;
		this.provisioingSystem = provisioingSystem;
		this.serviceId = serviceId;
		this.product = product;
		this.hardwareId = hardwareId;
		this.requestType = requestType;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.clientId = clientId;
		this.accountNo = accountNo;
        this.selfcarePassword=selfcarePassword;
        this.selfcareUsername=userName;
        this.zebraSubscriberId = subscriberId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.officeUId = officeUId;
		this.branch = branch;
		this.regionCode = regionCode;
		this.regionName = regionName;
		this.deviceId = deviceId;
		this.ipAddress = ipAddress;
		this.orderId =orderId;
		this.startDate = startDate;
		this.endDate = endDate;

	}

	//ZeebraOTT
	public EntitlementsData(Long id, Long clientId, String accountNo,String firstName, String lastName, String email, String phone,
			String city, String zip, String address, String provisioingSystem,Long serviceId, Long prdetailsId, String product,
			String macId,String requestType, Long zebraSubscriberId, BigDecimal itemPrice,
			Long itemId, String itemCode, String itemDescription) {
		
		this.id= id;
		this.clientId = clientId;
		this.accountNo = accountNo;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.city = city;
		this.zip = zip;
		this.address = address;
		this.provisioingSystem = provisioingSystem;
		this.serviceId = serviceId;
		this.prdetailsId = prdetailsId;
		this.product = product;
		this.hardwareId = macId;
		this.zebraSubscriberId = zebraSubscriberId;
		this.itemPrice = itemPrice;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.itemId = itemId;
		this.requestType = requestType;
		
	}

	//General Purpose
	public EntitlementsData(Long id, Long prdetailsId, String requestType,
			String hardwareId, String provisioingSystem, String product,
			Long clientId, Long planId, String orderNo, Long orderId,
			LocalDate startDate, LocalDate endDate, String servicetype,
			String displayName, String login, String password, String userName, 
			String userPassword, String firstName, String lastName, String email, Long officeId, Long taskNo,Long priority) {
		
		this.id = id;
		this.prdetailsId = prdetailsId;
		this.product = product;
		this.requestType = requestType;
		this.hardwareId = hardwareId;
		this.provisioingSystem = provisioingSystem;
		this.clientId = clientId;
		this.planId = planId;
		this.orderNo = orderNo;
		this.orderId = orderId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.serviceType = servicetype;
		this.displayName = displayName;
		this.login = login;
		this.password = password;
		this.selfcareUsername=userName;
		this.selfcarePassword=userPassword;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.officeId = officeId;
		this.taskNo = taskNo;
	
	}

	//cubiware
	public EntitlementsData(Long id, Long clientId, String firstName, String lastName, 
			String phone, String email, String city, String zip, String street, String country, Long countryId,
			String provisioingSystem, Long serviceId, Long prdetailsId, String product, String macId, 
			String deviceId, String requestType, Long zebraSubscriberId, Long regionId, String regionName,
			String selfcareUsername, String selfcarePassword, String countryISD) {
		
		this.id = id;
		this.clientId = clientId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
		this.city = city;
		this.zip = zip;
		this.street = street;
		this.country = country;
		this.countryId = countryId;
		this.provisioingSystem = provisioingSystem;
		this.serviceId = serviceId;
		this.prdetailsId = prdetailsId;
		this.product = product;
		this.macId = macId;
		this.deviceId = deviceId;
		this.requestType = requestType;
		this.zebraSubscriberId = zebraSubscriberId;
		this.regionId = regionId;
		this.regionName = regionName;
		this.selfcareUsername = selfcareUsername;
		this.selfcarePassword = selfcarePassword;
		this.countryISD = countryISD;
		
	}

	
	
	public EntitlementsData(Long id,Long version, Long prdetailsId, String requestType, String jsonDetails, Long taskNo,Long priority) {
		this.id = id;
		this.version = version;
		this.prdetailsId = prdetailsId;
		this.requestType = requestType;
		this.jsonDetails = jsonDetails;
		this.taskNo = taskNo;
		this.priority =priority;
	}

	public EntitlementsData(Long id, Long prdetailsId,
			String provisioingSystem, String product, String macId,
			String requestType, String itemCode, String itemDescription,
			Long clientId, String accountNo, String firstName,
			String lastName, String officeUID, String branch,
			String regionCode, String regionName, String deviceId,
			String ipAddress, String username, String password,
			String subscriberId) {
		
		this.id = id;
		this.clientId = clientId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.provisioingSystem = provisioingSystem;
		this.prdetailsId = prdetailsId;
		this.product = product;
		this.macId = macId;
		this.deviceId = deviceId;
		this.requestType = requestType;
		this.zebraSubscriberId = new Long(subscriberId);;
		this.regionCode = regionCode;
		this.regionName = regionName;
		this.selfcareUsername = username;
		this.selfcarePassword = password;
		this.itemCode = itemCode;
		this.itemDescription = itemDescription;
		this.accountNo = accountNo;
		this.officeUId = officeUID;
		this.branch = branch;
		this.ipAddress = ipAddress;
	}

	public Long getId() {
		return id;
	}

	public Long getPrdetailsId() {
		return prdetailsId;
	}

	public String getProvisioingSystem() {
		return provisioingSystem;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public String getProduct() {
		return product;
	}

	public String getHardwareId() {
		return hardwareId;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getItemCode() {
		return itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getAccountNo() {
		return accountNo;

	}

	public String getFirstName() {
		return firstName;
	}


	public String getBranch() {
		return branch;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public String getRegionName() {
		return regionName;
	}

	public String getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public Long getPlanId() {
		return planId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastName() {
		return lastName;
	}

	public String getOfficeUId() {
		return officeUId;
	}


	public Long getOrderId() {
		return orderId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setResults(StakerData data) {
		this.results=data;
		
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public String getZip() {
		return zip;
	}

	public StakerData getResults() {
		return results;
	}

	public String getServiceType() {
		return serviceType;
	}

	public Long getZebraSubscriberId() {
		return zebraSubscriberId;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public Long getItemId() {
		return itemId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getSelfcareUsername() {
		return selfcareUsername;
	}

	public String getSelfcarePassword() {
		return selfcarePassword;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public String getStreet() {
		return street;
	}

	public String getMacId() {
		return macId;
	}

	public Long getRegionId() {
		return regionId;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public String getCountryISD() {
		return countryISD;
	}
	public void setAction_name(String action_name) {
		this.action_name = action_name;
	}


	public void setEntity_name(String entity_name) {
		this.entity_name = entity_name;
	}


	public void setMade_on_date(LocalDate made_on_date) {
		this.made_on_date = made_on_date;
	}

	

	public void setResource_id(Long resource_id) {
		this.resource_id = resource_id;
	}



	public void setClient_id(Long client_id) {
		this.client_id = client_id;
	}



	public void setCommand_as_json(String command_as_json) {
		this.command_as_json = command_as_json;
	}
}
