package org.mifosplatform.billing.selfcare.data;

import java.util.List;

import javax.persistence.Column;

import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.finance.chargeorder.data.BillDetailsData;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.order.data.OrderData;


public class SelfCareData {

private Long clientId;
	
	private String userName;
	private String password;
	private String uniqueReference;
	private Boolean isDeleted;
	private String email;
	private ClientData clientData;
	private ClientBalanceData clientBalanceData;
	private List<AddressData> addressData;
	private List<OrderData> clientOrdersData;
	private Page<BillDetailsData> statementsData;
	private List<PaymentData> paymentsData;
	private List<TicketMasterData> ticketMastersData;
	private PaymentGatewayConfiguration paypalConfigData;
	private String authPin;
	private PaymentGatewayConfiguration paypalConfigDataForIos;
	private Long loginHistoryId; 
	private List<ClientServiceData> clientServices;
	
	private Integer firstTimeLogInRemaining;
	private Integer nonExpired;
	private Integer nonLocked;
	private Integer nonExpiredCredentials;
	private Integer enabled;

	public SelfCareData(Long clientId, String email) {
		this.clientId = clientId;
		this.email = email;
	}

	public SelfCareData() {
		// TODO Auto-generated constructor stub
	}



	public SelfCareData(String username, Long clientId, String password) {
		this.userName = username;
		this.clientId = clientId;
		this.password = password;

	}

	public SelfCareData(String userName, Long clientId, String password, Integer firsttimeLoginRemaining,
			Integer nonExpired, Integer nonLocked, Integer nonExpiredCredentials, Integer enabled) {
		// TODO Auto-generated constructor stub
		this.userName = userName;
		this.clientId = clientId;
		this.password = password;
		this.firstTimeLogInRemaining = firsttimeLoginRemaining;
		this.nonExpired = nonExpired;
		this.nonLocked = nonLocked;
		this.nonExpiredCredentials = nonExpiredCredentials;
		this.enabled = enabled;
		
	}

	public Long getClientId() {
		return clientId;
	}


	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUniqueReference() {
		return uniqueReference;
	}


	public void setUniqueReference(String uniqueReference) {
		this.uniqueReference = uniqueReference;
	}


	public Boolean getIsDeleted() {
		return isDeleted;
	}


	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public void setDetails(ClientData clientsData,
			ClientBalanceData balanceData, List<AddressData> addressData,
			List<OrderData> clientOrdersData,
			Page<BillDetailsData> statementsData,
			List<PaymentData> paymentsData,
			List<TicketMasterData> ticketMastersData,
			Long loginHistoryId, List<ClientServiceData> clientServices) {
		
		
		this.clientData = clientsData;
		this.clientBalanceData = balanceData;
		this.addressData = addressData;
		this.clientOrdersData = clientOrdersData;
		this.statementsData = statementsData;
		this.paymentsData = paymentsData;
		this.ticketMastersData = ticketMastersData;
		this.loginHistoryId=loginHistoryId;
		this.clientServices = clientServices;
	}



	public Integer getFirstTimeLogInRemaining() {
		return firstTimeLogInRemaining;
	}

	public void setFirstTimeLogInRemaining(Integer firstTimeLogInRemaining) {
		this.firstTimeLogInRemaining = firstTimeLogInRemaining;
	}

	public Integer getNonExpired() {
		return nonExpired;
	}

	public void setNonExpired(Integer nonExpired) {
		this.nonExpired = nonExpired;
	}

	public Integer getNonLocked() {
		return nonLocked;
	}

	public void setNonLocked(Integer nonLocked) {
		this.nonLocked = nonLocked;
	}

	public Integer getNonExpiredCredentials() {
		return nonExpiredCredentials;
	}

	public void setNonExpiredCredentials(Integer nonExpiredCredentials) {
		this.nonExpiredCredentials = nonExpiredCredentials;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public PaymentGatewayConfiguration getPaypalConfigData() {
		return paypalConfigData;
	}



	public void setPaypalConfigData(PaymentGatewayConfiguration paypalConfigData) {
		this.paypalConfigData = paypalConfigData;
	}

	public void setPaypalConfigDataForIos(PaymentGatewayConfiguration paypalConfigDataForIos) {
		this.paypalConfigDataForIos =paypalConfigDataForIos;
		
	}
	
	

}
