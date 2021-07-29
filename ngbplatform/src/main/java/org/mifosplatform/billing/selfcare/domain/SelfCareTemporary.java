package org.mifosplatform.billing.selfcare.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name="b_client_register")
public class SelfCareTemporary extends AbstractAuditableCustom<AppUser, Long>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="username")
	private String userName;
	
	@Column(name="generate_key")
	private String generatedKey;
	
	@Column(name="status")
	private String status;
	
	@Column(name="payment_status")
	private String paymentStatus;
	
	@Column(name="payment_data")
	private String paymentData;
	
	@Column(name="otp")
	private String otp;
	
	
	public SelfCareTemporary(){
		
	}
	
	public SelfCareTemporary(String userName, String generatedKey){
		
		this.userName = userName;
		this.generatedKey = generatedKey;
		this.status="INACTIVE";
		this.otp="xxxx";
		
	}
	public static SelfCareTemporary fromJson(JsonCommand command) {
		String userName = command.stringValueOfParameterNamed("username");
		SelfCareTemporary selfCareTemporary = new SelfCareTemporary();
		selfCareTemporary.setUserName(userName);
		selfCareTemporary.setStatus("INACTIVE");
		selfCareTemporary.paymentStatus = "INACTIVE";
		selfCareTemporary.paymentData = "NULL";
		return selfCareTemporary;
		
	}
	public  SelfCareTemporary(String otp,String userName, String generatedKey){
		this.userName = userName;
		this.otp = otp;	
		this.status="INACTIVE";
		this.generatedKey = generatedKey;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGeneratedKey() {
		return generatedKey;
	}

	public void setGeneratedKey(String generatedKey) {
		this.generatedKey = generatedKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentData() {
		return paymentData;
	}

	public void setPaymentData(String paymentData) {
		this.paymentData = paymentData;
	}
	
	public String getotp() {
		return otp;
	}

	public void setotp(String otp) {
		this.otp = otp;
	}

	public Map<String, Object> update(JsonCommand command) {
		
	final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
	
	 final String userNameNamedParamName = "userName";
	 final String generatedKeyNamedParamName = "generatedKey";
	 final String statusNamedParamName = "status";
	 final String paymentStatusNamedParamName = "paymentStatus";
	 final String paymentDataNamedParamName = "paymentData";
	 final String otpNamedParamName = "otp";
	 
	
	 if(command.isChangeInStringParameterNamed(userNameNamedParamName, this.userName)){
			final String newValue = command.stringValueOfParameterNamed(userNameNamedParamName);
			actualChanges.put(userNameNamedParamName, newValue);
			this.userName = StringUtils.defaultIfEmpty(newValue,null);
		}
	 if(command.isChangeInStringParameterNamed(generatedKeyNamedParamName, this.generatedKey)){
			final String newValue = command.stringValueOfParameterNamed(generatedKeyNamedParamName);
			actualChanges.put(generatedKeyNamedParamName, newValue);
			this.generatedKey = StringUtils.defaultIfEmpty(newValue,null);
		}
	 if(command.isChangeInStringParameterNamed(statusNamedParamName, this.status)){
			final String newValue = command.stringValueOfParameterNamed(statusNamedParamName);
			actualChanges.put(statusNamedParamName, newValue);
			this.status = StringUtils.defaultIfEmpty(newValue,null);
		}
	 if(command.isChangeInStringParameterNamed(paymentStatusNamedParamName, this.paymentStatus)){
			final String newValue = command.stringValueOfParameterNamed(paymentStatusNamedParamName);
			actualChanges.put(paymentStatusNamedParamName, newValue);
			this.paymentStatus = StringUtils.defaultIfEmpty(newValue,null);
		}
	 if(command.isChangeInStringParameterNamed(paymentDataNamedParamName, this.paymentData)){
			final String newValue = command.stringValueOfParameterNamed(paymentDataNamedParamName);
			actualChanges.put(paymentDataNamedParamName, newValue);
			this.paymentData = StringUtils.defaultIfEmpty(newValue,null);
		}
	 if(command.isChangeInStringParameterNamed(otpNamedParamName, this.otp)){
			final String newValue = command.stringValueOfParameterNamed(otpNamedParamName);
			actualChanges.put(otpNamedParamName, newValue);
			this.otp = StringUtils.defaultIfEmpty(newValue,null);
		}
	 

		return actualChanges;
	}
	
	
	
	public void delete() {
         this.generatedKey = "del_"+getId()+"_"+this.getGeneratedKey();
         this.userName = "del_"+getId()+"_"+this.userName;
		
	}

	
	
	

}
