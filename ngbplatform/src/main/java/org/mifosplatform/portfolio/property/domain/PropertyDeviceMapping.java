package org.mifosplatform.portfolio.property.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_propertydevice_mapping")
public class PropertyDeviceMapping  extends AbstractAuditableCustom<AppUser, Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "allocate_date")
	private Date allocateDate;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "property_code")
	private String propertyCode;

	@Column(name = "is_deleted")
	private char isDeleted = 'N';

	public PropertyDeviceMapping(){
		
	}

	public PropertyDeviceMapping(Long clientId, String propertyCode,String serialNumber, Date allocationDate) {
         
		this.clientId = clientId;
		this.propertyCode = propertyCode;
		this.serialNumber =  serialNumber;
		this.allocateDate=allocationDate;
	
	}

	public static PropertyDeviceMapping fromJson(Long clientId,JsonCommand command) {
         
		final String propertyCode = command.stringValueOfParameterNamed("propertCode");
        final String serialNumber = command.stringValueOfParameterNamed("serialNumber");
        
        return new PropertyDeviceMapping(clientId,propertyCode,serialNumber,new Date());
	
	
	}

	public void setPropertyCode(String propertyCode) {

		  this.propertyCode = propertyCode;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public void setAllocateDate(Date allocateDate) {
		this.allocateDate = allocateDate;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}
	

	public String getPropertyCode() {
		return propertyCode;
	}

	public void delete() {
		this.isDeleted = 'Y';
		this.serialNumber =this.serialNumber+"_Y"; 
		
	}
	
	
	
}
