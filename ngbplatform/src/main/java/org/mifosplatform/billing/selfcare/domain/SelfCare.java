package org.mifosplatform.billing.selfcare.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_clientuser",uniqueConstraints = @UniqueConstraint(name = "username", columnNames = { "username","unique_reference"}))
public class SelfCare extends AbstractPersistable<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="client_id")
	private Long clientId;
	
	@Column(name="username")
	private String userName;
	
	@Column(name="password")
	private String password;
	
	@Column(name="unique_reference")
	private String uniqueReference;
	
	@Column(name="status")
	private String status;
	
	@Column(name="is_deleted")
	private Boolean isDeleted=false;
	
	@Column(name="auth_pin")
	private String authPin;
	
	@Column(name="korta_token")
	private String token;
	
	@Column(name="device_id")
	private String deviceId;
	
	@Column(name="auth_token")
	private String authToken;
	
	@Column(name="latitude")
	private String latitude;
	
	@Column(name="longitude")
	private String longitude;
	
	@Column(name="zebra_subscriber_id")
	private Long zebraSubscriberId;
	
	@Column(name="firsttime_login_remaining")
	private Integer firstTimeLogInRemaining;
	
	@Column(name="nonexpired")
	private Integer nonExpired;
	
	@Column(name="nonlocked")
	private Integer nonLocked;
	
	@Column(name="nonexpired_credentials")
	private Integer nonExpiredCredentials;
	
	@Column(name="enabled")
	private Integer enabled;
	
	public SelfCare() {
		
	}
	
	public SelfCare(Long clientId,String userName, String password, String uniqueReference, Boolean isDeleted,String device,
			String authToken, String latitude, String longitude){
		this.clientId = clientId;
		this.userName = userName;
		this.password = password;
		this.uniqueReference = uniqueReference;
		this.isDeleted = isDeleted;
		this.status="INACTIVE";
		this.deviceId=device;
		this.authToken = authToken;
		if(latitude!=null&&longitude!=null){
			this.latitude = latitude;
			this.longitude = longitude;
		}
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

	public static SelfCare fromJson(JsonCommand command) {
		String uniqueReference = command.stringValueOfParameterNamed("uniqueReference");
		String device = command.stringValueOfParameterNamed("device");
		Long clientId = command.longValueOfParameterNamed("clientId");
		String authToken = command.stringValueOfParameterName("authToken");
		String latitude = command.stringValueOfParameterName("lat");
		String longitude = command.stringValueOfParameterName("long");
		return new SelfCare(clientId, uniqueReference, null, uniqueReference, false, device, authToken, latitude , longitude);
	}
	
	public static SelfCare fromJsonODP(JsonCommand command) {
		String uniqueReference = command.stringValueOfParameterNamed("uniqueReference");
		String password = command.stringValueOfParameterNamed("password");
		SelfCare selfCare = new SelfCare();
		selfCare.setUserName(uniqueReference);
		selfCare.setUniqueReference(uniqueReference);
		selfCare.setPassword(password);
		selfCare.setIsDeleted(false);
		selfCare.setStatus("ACTIVE");
		return selfCare;
		
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
		this.uniqueReference ="DEL_"+getId()+"_"+this.uniqueReference;
		this.userName = "DEL_"+getId()+"_"+this.userName;
		
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAuthPin() {
		return authPin;
	}
	public void setAuthPin(String authPin) {
		this.authPin = authPin;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getZebraSubscriberId() {
		return zebraSubscriberId;
	}
	public void setZebraSubscriberId(Long zebraSubscriberId) {
		this.zebraSubscriberId = zebraSubscriberId;
	}
	
	
	
}
