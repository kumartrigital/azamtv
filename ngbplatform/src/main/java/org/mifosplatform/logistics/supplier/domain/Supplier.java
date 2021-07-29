package org.mifosplatform.logistics.supplier.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;


@Entity
@Table(name = "b_supplier", uniqueConstraints = { @UniqueConstraint(columnNames = { "supplier_code" }, name = "supplier_code") })
public class Supplier extends AbstractAuditableCustom<AppUser, Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="supplier_code", nullable=false, length=10)
	private String supplierCode;
	
	@Column(name="supplier_description", nullable=true, length=100)
	private String supplierDescription;
	
	@Column(name="supplier_address", nullable=true, length=100)
	private String supplierAddress;
	
	
	public Supplier() {}
	
	public Supplier(String supplierCode,String supplierDescription,String supplierAddress) {
		
		this.supplierCode = supplierCode;
		this.supplierDescription = supplierDescription;
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}

	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	/**
	 * @return the supplierDescription
	 */
	public String getSupplierDescription() {
		return supplierDescription;
	}

	/**
	 * @param supplierDescription the supplierDescription to set
	 */
	public void setSupplierDescription(String supplierDescription) {
		this.supplierDescription = supplierDescription;
	}

	/**
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	public static Supplier formJson(JsonCommand command) {
		final String supplierCode = command.stringValueOfParameterNamed("supplierCode");
		final String supplierDescription = command.stringValueOfParameterNamed("supplierDescription");
		final String supplierAddress =command.stringValueOfParameterNamed("supplierAddress");
		return new Supplier(supplierCode, supplierDescription, supplierAddress);
	}

	public Map<String, Object> update(JsonCommand command){
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String supplierCodeNamedParamName = "supplierCode";
		final String supplierDescriptionNamedParamName = "supplierDescription";
		final String supplierAddressNamedParamName = "supplierAddress";
		
		if(command.isChangeInStringParameterNamed(supplierCodeNamedParamName, this.supplierCode)){
			final String newValue = command.stringValueOfParameterNamed(supplierCodeNamedParamName);
			actualChanges.put(supplierCodeNamedParamName, newValue);
			this.supplierCode = StringUtils.defaultIfEmpty(newValue,null);
		}
		if(command.isChangeInStringParameterNamed(supplierDescriptionNamedParamName, this.supplierDescription)){
			final String newValue = command.stringValueOfParameterNamed(supplierDescriptionNamedParamName);
			actualChanges.put(supplierDescriptionNamedParamName, newValue);
			this.supplierDescription = StringUtils.defaultIfEmpty(newValue, null);
		}
		if(command.isChangeInStringParameterNamed(supplierAddressNamedParamName,this.supplierAddress)){
			final String newValue = command.stringValueOfParameterNamed(supplierAddressNamedParamName);
			actualChanges.put(supplierAddressNamedParamName, newValue);
			this.supplierAddress =StringUtils.defaultIfEmpty(newValue,null);
		}		
		return actualChanges;
	
	}
}
