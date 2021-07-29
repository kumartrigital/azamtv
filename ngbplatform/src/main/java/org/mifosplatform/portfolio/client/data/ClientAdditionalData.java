package org.mifosplatform.portfolio.client.data;

import java.util.Collection;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class ClientAdditionalData {
	
	private final Long id;
	private final Long clientId;
	private final String jobTitle;
	private final String customerIdentificationNumber;
	private final Long nationalityId;
	private final Long customerIdentificationTypeId;
	private final String financeId;
	private final String utsCustomerId;
	private final LocalDate dateOfBirth;
	private final Long ageGroupId;
	private final Long preferCommId;
	private final Long preferLanId;
	private final Long genderId;
	private final String remarks;
	private Collection<MCodeData> genderDatas;
	private Collection<MCodeData> nationalityDatas;
	private Collection<MCodeData> customeridentificationDatas;
	private Collection<MCodeData> cummunitcationDatas;
	private Collection<MCodeData> languagesDatas;
	private  Collection<MCodeData> ageGroupDatas;
	
	

	public ClientAdditionalData(Collection<MCodeData> genderDatas,Collection<MCodeData> nationalityDatas,
			Collection<MCodeData> customeridentificationDatas,Collection<MCodeData> cummunitcationDatas,
			Collection<MCodeData> languagesDatas, Collection<MCodeData> ageGroupDatas) {
		
		this.id= null;
		this.clientId = null;
		this.jobTitle = null;
		this.ageGroupId = null;
		this.genderId = null;
		this.financeId = null;
		this.utsCustomerId = null;
		this.customerIdentificationNumber = null;
		this.dateOfBirth = null;
		this.nationalityId = null;
		this.customerIdentificationTypeId = null;
		this.preferCommId = null;
		this.preferLanId = null;
		this.remarks = null;
		this.genderDatas = genderDatas;
		this.nationalityDatas = nationalityDatas;
		this.customeridentificationDatas = customeridentificationDatas;
		this.languagesDatas = languagesDatas;
		this.cummunitcationDatas = cummunitcationDatas;
		this.ageGroupDatas = ageGroupDatas;
		
	}


	public ClientAdditionalData(Long id, Long clientId, String financeId,String utsCustomerId, String customerIdentification,
			String jobTitle, LocalDate dob, Long nationalityId,Long ageGroupId, Long customerIdType, Long preferCommId,
			Long preferLanId, Long genderId, String remarks) {
		
		this.id= id;
		this.clientId = clientId;
		this.jobTitle = jobTitle;
		this.ageGroupId = ageGroupId;
		this.genderId = genderId;
		this.financeId = financeId;
		this.utsCustomerId = utsCustomerId;
		this.customerIdentificationNumber = customerIdentification;
		this.dateOfBirth = dob;
		this.nationalityId = nationalityId;
		this.customerIdentificationTypeId = customerIdType;
		this.preferCommId = preferCommId;
		this.preferLanId = preferLanId;
		this.remarks = remarks;
		this.genderDatas = null;
		this.nationalityDatas = null;
		this.customeridentificationDatas = null;
		this.languagesDatas = null;
		this.cummunitcationDatas = null;
		this.ageGroupDatas = null;
		
	}


	public Long getId() {
		return id;
	}


	public Long getClientId() {
		return clientId;
	}


	public String getJobTitle() {
		return jobTitle;
	}


	public String getCustomerIdentificationNumber() {
		return customerIdentificationNumber;
	}


	public Long getNationalityId() {
		return nationalityId;
	}


	public Long getCustomerIdentificationTypeId() {
		return customerIdentificationTypeId;
	}


	public String getFinanceId() {
		return financeId;
	}


	public String getUtsCustomerId() {
		return utsCustomerId;
	}


	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}


	public Long getAgeGroupId() {
		return ageGroupId;
	}


	public Long getPreferCommId() {
		return preferCommId;
	}


	public Long getPreferLanId() {
		return preferLanId;
	}


	public Long getGenderId() {
		return genderId;
	}


	public String getRemarks() {
		return remarks;
	}


	public Collection<MCodeData> getGenderDatas() {
		return genderDatas;
	}


	public Collection<MCodeData> getNationalityDatas() {
		return nationalityDatas;
	}


	public Collection<MCodeData> getCustomeridentificationDatas() {
		return customeridentificationDatas;
	}


	public Collection<MCodeData> getCummunitcationDatas() {
		return cummunitcationDatas;
	}


	public Collection<MCodeData> getLanguagesDatas() {
		return languagesDatas;
	}


	public Collection<MCodeData> getAgeGroupDatas() {
		return ageGroupDatas;
	}


	public void setGenderDatas(Collection<MCodeData> genderDatas) {
		this.genderDatas = genderDatas;
	}


	public void setNationalityDatas(Collection<MCodeData> nationalityDatas) {
		this.nationalityDatas = nationalityDatas;
	}


	public void setCustomeridentificationDatas(
			Collection<MCodeData> customeridentificationDatas) {
		this.customeridentificationDatas = customeridentificationDatas;
	}


	public void setCummunitcationDatas(Collection<MCodeData> cummunitcationDatas) {
		this.cummunitcationDatas = cummunitcationDatas;
	}


	public void setLanguagesDatas(Collection<MCodeData> languagesDatas) {
		this.languagesDatas = languagesDatas;
	}


	public void setAgeGroupDatas(Collection<MCodeData> ageGroupDatas) {
		this.ageGroupDatas = ageGroupDatas;
	}
	
	
	

}
