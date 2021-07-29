package org.mifosplatform.organisation.partner.data;

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.billing.currency.data.CountryCurrencyData;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.monetary.data.ApplicationCurrencyConfigurationData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.partneragreement.data.AgreementData;

public class PartnersData {
    
	private Long id;
	private Long officeId;
	private Long additionalinfoId;
	private String partnerName; 
	private BigDecimal creditLimit;
	private String currency;
	private Long parentId;
	private String parentName;
	private String officeType;
	private LocalDate openingDate;
	private String loginName;
	private String city; 
	private String state; 
	private String country; 
	private String email; 
	private String phoneNumber;
	private boolean isCollective;
	private BigDecimal balanceAmount;
	private List<String> countryData;
	private List<String> statesData;
	private List<String> citiesData;
	private Collection<CodeValueData> officeTypes;
	private ApplicationCurrencyConfigurationData currencyData;
	private Collection<OfficeData> allowedParents;
	private Collection<CountryCurrencyData> configCurrency;
	private List<AgreementData> agreementData;
	private String officeNumber;
	private String contactName;
	private Long userId;
	private String imageKey;
	private String companyLogo;
	private String district;
	private String businessType;
	private List<String> districtData; 
	Collection<MCodeData> businessTypes;
    private String externalId;
    private Long poId;
	private String settlementPoId;
	private String nameDecorated;
	private String AccountType;
	
	public PartnersData(List<String> countryData, List<String> statesData,
			List<String> citiesData,List<String> districtData, Collection<CodeValueData> officeTypes,
			ApplicationCurrencyConfigurationData currencyData, Collection<OfficeData> allowedParents,
			Collection<CountryCurrencyData> configCurrency,
			Collection<MCodeData> businessTypes) {

		this.citiesData = citiesData;
		this.currencyData = currencyData;
		this.countryData = countryData;
		this.officeTypes = officeTypes;
		this.allowedParents = allowedParents;
		this.statesData = statesData;
		this.configCurrency = configCurrency;
		this.districtData=districtData;
		this.businessTypes=businessTypes;
		this.openingDate=DateUtils.getLocalDateOfTenant();
		
	}

	public PartnersData(final Long officeId,final Long additionalinfoId,final String partnerName, final BigDecimal creditLimit, 
			final String currency,final Long parentId, final String parentName, final String officeType,final LocalDate openingDate, 
			final String loginName,final String city, final String state,final String country, final String email, final String phoneNumber,
			final String isCollective,final BigDecimal balanceAmount,final String officeNumber,final String contactName,final Long userId,
			final String companyLogo,final String district,final String businessType,final String externalId,final Long poId,final String settlementPoId) {
		
		this.id = additionalinfoId;
		this.officeId = officeId;
		this.partnerName = partnerName;
		this.creditLimit = creditLimit;
		this.currency = currency;
		this.parentId = parentId;
		this.parentName = parentName;
		this.officeType = officeType;
		this.openingDate = openingDate;
		this.loginName = loginName;
		this.city = city;
		this.state = state;
		this.country = country;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.isCollective = isCollective.contains("Y");
		this.balanceAmount = balanceAmount;
		this.officeNumber = officeNumber;
		this.contactName = contactName;
		this.userId = userId;
		this.companyLogo = companyLogo !=null ? new File(companyLogo).getName() : companyLogo;
		this.district = district;
		this.businessType=businessType;
		this.externalId=externalId;
		this.poId = poId;
		this.settlementPoId = settlementPoId;

	}

	public PartnersData(Long id,final String partnerName, final String imageKey) {
		this.id = id;
		this.partnerName = partnerName;
		this.imageKey = imageKey;
	}

	public Long getId() {
		return id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public Long getAdditionalinfoId() {
		return additionalinfoId;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public BigDecimal getPartnerType() {
		return creditLimit;
	}

	public String getCurrency() {
		return currency;
	}

	public Long getParentId() {
		return parentId;
	}

	
	public String getParentName() {
		return parentName;
	}

	public String getOfficeType() {
		return officeType;
	}

	public LocalDate getOpeningDate() {
		return openingDate;
	}
	
	public String getLoginName() {
		return loginName;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public  boolean getIsCollective() {
		return isCollective;
	}
	
	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	
	public String getOfficeNumber() {
		return officeNumber;
	}

	public String getContactName() {
		return contactName;
	}

	public Long getUserId() {
		return userId;
	}

	public List<String> getCountryData() {
		return countryData;
	}

	public List<String> getStatesData() {
		return statesData;
	}

	public List<String> getCitiesData() {
		return citiesData;
	}


	public ApplicationCurrencyConfigurationData getCurrencyData() {
		return currencyData;
	}

	public Collection<OfficeData> getAllowedParents() {
		return allowedParents;
	}

	public Collection<CodeValueData> getOfficeTypes() {
		return officeTypes;
	}

	public List<AgreementData> getAgreementData() {
		return agreementData;
	}

	public Collection<CountryCurrencyData> getConfigCurrency() {
		return configCurrency;
	}
	
	public void setAgreementData(List<AgreementData> agreementData) {
		this.agreementData = agreementData;
	}


	public void setConfigCurrency(Collection<CountryCurrencyData> configCurrency) {
		this.configCurrency = configCurrency;
	}

	public void setCountryData(List<String> countryData) {
		this.countryData = countryData;
	}

	public void setStatesData(List<String> statesData) {
		this.statesData = statesData;
	}

	public void setCitiesData(List<String> citiesData) {
		this.citiesData = citiesData;
	}

	public void setOfficeTypes(Collection<CodeValueData> officeTypes) {
		this.officeTypes = officeTypes;
	}

	public void setCurrencyData(ApplicationCurrencyConfigurationData currencyData) {
		this.currencyData = currencyData;
	}

	public void setAllowedParents(Collection<OfficeData> allowedParents) {
		this.allowedParents = allowedParents;
	}
	
	public String getImageKey() {
		return this.imageKey;
	}

	public boolean imageKeyExists() {
		return StringUtils.isNotBlank(this.imageKey);
	}
	
	public String getCompanyLogo() {
		return companyLogo;
	}

	public String getDistrict() {
		return district;
	}
	public void setDistrictData(List<String> districtData) {
		this.districtData = districtData;
	}
	public Collection<MCodeData> getBussinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(Collection<MCodeData> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public String getExternalId() {
		return externalId;
	}
	
	public Long getPoId() {
		return poId;
	}
	
	public String getSettlementPoId() {
		return settlementPoId;
	}
	
	public String getNameDecorated() {
		return nameDecorated;
	}

	public void setNameDecorated(String nameDecorated) {
		this.nameDecorated = nameDecorated;
	}
	public String getAccountType() {
		return this.AccountType;
	}

	public void setAccountType(String accountType) {
		this.AccountType = accountType;
	}


}
