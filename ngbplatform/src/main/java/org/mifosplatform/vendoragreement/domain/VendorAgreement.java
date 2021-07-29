package org.mifosplatform.vendoragreement.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_vendor_agreement")
public class VendorAgreement extends  AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "vendor_id")
	private Long vendorId;
	
	@Column(name = "vendor_agmt_status")
	private String agreementStatus;

	@Column(name = "vendor_agmt_startdate")
	private Date agreementStartdate;
	
	@Column(name = "vendor_agmt_enddate")
	private Date agreementEnddate;

	@Column(name = "content_type")
	private String contentType;
	
	@Column(name = "vendor_agmt_document")
	private String vendorAgmtDocument;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "vendor", orphanRemoval = true)
	private List<VendorAgreementDetail> vendorDetails = new ArrayList<VendorAgreementDetail>();

	public  VendorAgreement() {
		
	}

	public VendorAgreement(Long vendorId, String agreementStatus, Date agreementStartdate, Date agreementEnddate,
			String contentType, String vendorAgmtDocument) {
		
		this.vendorId = vendorId;
		this.agreementStatus = agreementStatus;
		this.agreementStartdate = agreementStartdate;
		this.agreementEnddate = agreementEnddate;
		this.contentType = contentType;
		this.vendorAgmtDocument = vendorAgmtDocument;
		
	}

	public List<VendorAgreementDetail> getMediaassetLocations() {
		return vendorDetails;
	}

	
	public static VendorAgreement fromJson(final JsonCommand command, String fileLocation) {
		
		 final String agreementStatus = command.stringValueOfParameterNamed("agreementStatus");
		 final String contentType = command.stringValueOfParameterNamed("contentType");
		 final LocalDate agreementStartDate = command.localDateValueOfParameterNamed("startDate");
		 final LocalDate agreementEndDate = command.localDateValueOfParameterNamed("endDate");
		 final Long vendorId = command.longValueOfParameterNamed("vendorId");
		 final String vendorAgmtDocument = fileLocation;
	
		 return new VendorAgreement(vendorId, agreementStatus, agreementStartDate.toDate(), agreementEndDate.toDate(),
				 contentType, vendorAgmtDocument);
	}


	public void addVendorDetails(final VendorAgreementDetail vendorDetails) {
		vendorDetails.update(this);
        this.vendorDetails.add(vendorDetails);
	}
	
	public Map<String, Object> update(JsonCommand command, String fileLocation){
	
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String vendorIdParamName = "vendorId";
		if(command.isChangeInLongParameterNamed(vendorIdParamName, this.vendorId)){
			final Long newValue = command.longValueOfParameterNamed(vendorIdParamName);
			actualChanges.put(vendorIdParamName, newValue);
			this.vendorId = newValue;
		}
		
		final String agreementStatusParamName = "agreementStatus";
		if(command.isChangeInStringParameterNamed(agreementStatusParamName, this.agreementStatus)){
			final String newValue = command.stringValueOfParameterNamed(agreementStatusParamName);
			actualChanges.put(agreementStatusParamName, newValue);
			this.agreementStatus = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		final String agreementStartDateParamName = "startDate";
		if(command.isChangeInDateParameterNamed(agreementStartDateParamName, this.agreementStartdate)){
			final LocalDate newValue = command.localDateValueOfParameterNamed(agreementStartDateParamName);
			actualChanges.put(agreementStartDateParamName,newValue.toDate());
			this.agreementStartdate = newValue.toDate();
		}
		
		final String agreementEndDateParamName = "endDate";
		if(command.isChangeInDateParameterNamed(agreementEndDateParamName, this.agreementEnddate)){
			final LocalDate newValue = command.localDateValueOfParameterNamed(agreementEndDateParamName);
			actualChanges.put(agreementEndDateParamName,newValue.toDate());
			this.agreementEnddate = newValue.toDate();
		}
		
		final String contentTypeParamName = "contentType";
		if(command.isChangeInStringParameterNamed(contentTypeParamName, this.contentType)){
			final String newValue = command.stringValueOfParameterNamed(contentTypeParamName);
			actualChanges.put(contentTypeParamName, newValue);
			this.contentType = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		if(fileLocation != null){
			final String fileLocationParamName = "fileLocation";
			if(command.isChangeInStringParameterNamed(fileLocationParamName, this.vendorAgmtDocument)){
				final String newValue = command.stringValueOfParameterNamed(fileLocationParamName);
				actualChanges.put(fileLocationParamName, newValue);
				this.vendorAgmtDocument = StringUtils.defaultIfEmpty(newValue,null);
			}
		}
		
		return actualChanges;
	
	}

	public Date getAgreementStartdate() {
		return agreementStartdate;
	}

	public Date getAgreementEnddate() {
		return agreementEnddate;
	}

	public void setAgreementStartdate(Date agreementStartdate) {
		this.agreementStartdate = agreementStartdate;
	}

	public void setAgreementEnddate(Date agreementEnddate) {
		this.agreementEnddate = agreementEnddate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public String getAgreementStatus() {
		return agreementStatus;
	}

	public String getContentType() {
		return contentType;
	}

	public String getVendorAgmtDocument() {
		return vendorAgmtDocument;
	}

	public List<VendorAgreementDetail> getVendorDetails() {
		return vendorDetails;
	}
	
	
	
}