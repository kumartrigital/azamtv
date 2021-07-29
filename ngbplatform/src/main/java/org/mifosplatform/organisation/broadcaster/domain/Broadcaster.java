package org.mifosplatform.organisation.broadcaster.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "b_broadcaster")
public class Broadcaster extends AbstractPersistable<Long>{
	
	@Column(name="brc_code", nullable=false, length=10)
	private String broadcasterCode;
	
	@Column(name="brc_name", nullable=false, length=100)
	private String broadcasterName;
	
	@Column(name="brc_contact_mobile", nullable=false, length=100)
	private Long contactMobile;
	
	@Column(name="brc_contact_no", nullable=false, length=100)
	private Long contactNumber;
	
	@Column(name="brc_contact_name", nullable=false, length=100)
	private String contactName ;
	
	@Column(name="brc_contact_email", nullable=false, length=100)
	private String email;
	
	@Column(name="brc_address", nullable=false, length=250)
	private String address;
	
	@Column(name="br_pin", nullable=false, length=10)
	private String pin;
	
	@Column(name = "is_deleted")
	private char isDeleted;

	public Broadcaster() {
	}


	public Broadcaster(String broadcasterCode, String broadcasterName,
			Long contactMobile, Long contactNumber, String contactName,
			String email, String address, String pin) {
		this.broadcasterCode = broadcasterCode;
		this.broadcasterName = broadcasterName;
		this.contactMobile = contactMobile;
		this.contactNumber = contactNumber;
		this.contactName = contactName;
		this.email = email;
		this.address = address;
		this.pin = pin;
		this.isDeleted = 'N';
	}




	public static Broadcaster formJson(JsonCommand command) {
		
		String broadcasterCode = command.stringValueOfParameterNamed("broadcasterCode");
		String broadcasterName = command.stringValueOfParameterNamed("broadcasterName");
		Long contactMobile = command.longValueOfParameterNamed("contactMobile");
		Long contactNumber = command.longValueOfParameterNamed("contactNumber");
		String contactName = command.stringValueOfParameterNamed("contactName");
		String email = command.stringValueOfParameterNamed("email");
		String address = command.stringValueOfParameterNamed("address");
		String pin = command.stringValueOfParameterNamed("pin");
		
		return new Broadcaster(broadcasterCode, broadcasterName, contactMobile, contactNumber, 
				contactName, email, address, pin);
	}


	public Map<String, Object> update(JsonCommand command) {
		
final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String broadcasterCodeNamedParamName = "broadcasterCode";
		final String broadcasterNameNamedParamName = "broadcasterName";
		final String contactMobileNamedParamName = "contactMobile";
		final String contactNumberNamedParamName = "contactNumber";
		final String contactNameNamedParamName = "contactName";
		final String emailNamedParamName = "email";
		final String addressNamedParamName = "address";
		final String pinNamedParamName = "pin";
		
		
		
		
		if(command.isChangeInStringParameterNamed(broadcasterCodeNamedParamName, this.broadcasterCode)){
			final String newValue = command.stringValueOfParameterNamed(broadcasterCodeNamedParamName);
			actualChanges.put(broadcasterCodeNamedParamName, newValue);
			this.broadcasterCode = StringUtils.defaultIfEmpty(newValue,null);
		}
		if(command.isChangeInStringParameterNamed(broadcasterNameNamedParamName, this.broadcasterName)){
			final String newValue = command.stringValueOfParameterNamed(broadcasterNameNamedParamName);
			actualChanges.put(broadcasterNameNamedParamName, newValue);
			this.broadcasterName = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		
		
		if(command.isChangeInLongParameterNamed(contactMobileNamedParamName,this.contactMobile)){
			final String newValue = command.stringValueOfParameterNamed(contactMobileNamedParamName);
			actualChanges.put(contactMobileNamedParamName, newValue);
			this.contactMobile =new Long(newValue);
		}
		
		if(command.isChangeInLongParameterNamed(contactNumberNamedParamName,this.contactNumber)){
			final Long newValue = command.longValueOfParameterNamed(contactNumberNamedParamName);
			actualChanges.put(contactNumberNamedParamName, newValue);
			this.contactNumber =newValue;
		}
		
		if(command.isChangeInStringParameterNamed(contactNameNamedParamName, this.contactName)){
			final String newValue = command.stringValueOfParameterNamed(contactNameNamedParamName);
			actualChanges.put(contactNameNamedParamName, newValue);
			this.contactName = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		if(command.isChangeInStringParameterNamed(emailNamedParamName, this.email)){
			final String newValue = command.stringValueOfParameterNamed(emailNamedParamName);
			actualChanges.put(emailNamedParamName, newValue);
			this.email = StringUtils.defaultIfEmpty(newValue,null);
		}
		if(command.isChangeInStringParameterNamed(addressNamedParamName, this.address)){
			final String newValue = command.stringValueOfParameterNamed(addressNamedParamName);
			actualChanges.put(addressNamedParamName, newValue);
			this.address = StringUtils.defaultIfEmpty(newValue,null);
		}
		if(command.isChangeInStringParameterNamed(pinNamedParamName, this.pin)){
			final String newValue = command.stringValueOfParameterNamed(pinNamedParamName);
			actualChanges.put(pinNamedParamName, newValue);
			this.pin = newValue;
		}
		return actualChanges;
	
	}


	public char getIsDeleted() {
		return isDeleted;
	}


	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void delete() {
		this.isDeleted = 'Y';
		
	}
	
	

}
