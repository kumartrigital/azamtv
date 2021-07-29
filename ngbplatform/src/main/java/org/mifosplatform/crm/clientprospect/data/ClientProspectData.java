package org.mifosplatform.crm.clientprospect.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

/**
 * @author Praveen
 * 
 */
public class ClientProspectData {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Short prospectType;
	private Long entity;
	private String username;
	private String firstName;
	private String middleName;
	private String lastName;
	private String homePhoneNumber;
	private String workPhoneNumber;
	private String mobileNumber;
	private String email;
	private String sourceOfPublicity;
	private String sourceOfPublicityInt;
	private String preferredPlan;
	private Long preferredPlanInt;
	private Date preferredCallingTime;
	private String note;
	private String address;
	private String streetArea;
	private String cityDistrict;
	private String district;//added 
	private String state;
	private String country;
	private String status;
	private String isDeleted;
	private Collection<MCodeData> sourceOfPublicityData;
	private Collection<ProspectPlanCodeData> planData;
	private List<String> countryData, stateData, cityData;
	private String statusRemark;
	private String zipCode;
	private LocalDate createdDate;
	private String fullName ; 
	private String officeName;
	private String officeMail;
	private String externalId;
	private String preferedService;
	private String[] arrOfStr;



	public ClientProspectData() {

	}

	public ClientProspectData(final Short prospectType, final String firstName,
			final String middleName, final String lastName,
			final String homePhoneNumber, final String workPhoneNumber,
			final String mobileNumber, final String email,
			final String sourceOfPublicity, final Date preferredCallingTime,
			final String note, final String address, final String streetArea,
			final String cityDistrict, final String state, final String country) {
		this.prospectType = prospectType;
		
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.homePhoneNumber = homePhoneNumber;
		this.workPhoneNumber = workPhoneNumber;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.sourceOfPublicity = sourceOfPublicity;
		this.preferredCallingTime = preferredCallingTime;
		this.note = note;
		this.address = address;
		this.streetArea = streetArea;
		this.cityDistrict = cityDistrict;
		this.state = state;
		this.country = country;
	}

	public ClientProspectData(final Long id, final Short prospectType,final String username,
			final String firstName, final String middleName,
			final String lastName, final String homePhoneNumber,
			final String workPhoneNumber, final String mobileNumber,
			final String email, final String sourceOfPublicity,
			final Date preferredCallingTime, final String note,
			final String address, final String streetArea,
			final String cityDistrict, final String state,
			final String country, final String preferredPlan,
			final String status, final String statusRemark,
			final String isDeleted) {
		this.id = id;
		this.prospectType = prospectType;
		this.username=username;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.homePhoneNumber = homePhoneNumber;
		this.workPhoneNumber = workPhoneNumber;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.sourceOfPublicity = sourceOfPublicity;
		this.preferredPlan = preferredPlan;
		this.preferredCallingTime = preferredCallingTime;
		this.note = note;
		this.address = address;
		this.streetArea = streetArea;
		this.cityDistrict = cityDistrict;
		this.state = state;
		this.country = country;
		this.status = status;
		this.statusRemark = statusRemark;
		this.isDeleted = isDeleted;
	}

	public ClientProspectData(final Long id, final Short prospectType,
			final String externalId,final String officeName,
			final String firstName, final String middleName,
			final String lastName, final String homePhoneNumber,
			final String workPhoneNumber, final String mobileNumber,
			final String email, final String sourceOfPublicity,
			final Date preferredCallingTime, final String note,
			final String address, final String streetArea,
			final String cityDistrict, final String state,
			final String country, final Long preferredPlan,
			final String status, final String statusRemark,
			final String isDeleted, final String zipCode) {
		this.id = id;
		this.prospectType = prospectType;
		this.externalId=externalId;
		this.officeName=officeName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.homePhoneNumber = homePhoneNumber;
		this.workPhoneNumber = workPhoneNumber;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.sourceOfPublicityInt = sourceOfPublicity;
		this.preferredPlanInt = preferredPlan;
		this.preferredCallingTime = preferredCallingTime;
		this.note = note;
		this.address = address;
		this.streetArea = streetArea;
		this.cityDistrict = cityDistrict;
		this.state = state;
		this.country = country;
		this.status = status;
		this.statusRemark = statusRemark;
		this.isDeleted = isDeleted;
		this.zipCode = zipCode;
	}
	
	/*
	added District*/
	public ClientProspectData(final Long id, final Short prospectType,
			final String firstName, final String middleName,
			final String lastName, final String homePhoneNumber,
			final String workPhoneNumber, final String mobileNumber,
			final String email, final String sourceOfPublicity,
			final Date preferredCallingTime, final String note,
			final String address, final String streetArea,
			final String cityDistrict, final String state,
			final String country, final Long preferredPlan,
			final String status, final String statusRemark,
			final String isDeleted, final String zipCode,final String district) {
		this.id = id;
		this.prospectType = prospectType;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.homePhoneNumber = homePhoneNumber;
		this.workPhoneNumber = workPhoneNumber;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.sourceOfPublicityInt = sourceOfPublicity;
		this.preferredPlanInt = preferredPlan;
		this.preferredCallingTime = preferredCallingTime;
		this.note = note;
		this.address = address;
		this.streetArea = streetArea;
		this.cityDistrict = cityDistrict;
		this.state = state;
		this.country = country;
		this.status = status;
		this.statusRemark = statusRemark;
		this.isDeleted = isDeleted;
		this.zipCode = zipCode;
		this.district = district;
	}
	
	public ClientProspectData(final Long id, final String fullName,final String officeMail, final String mail, final LocalDate createdDate, final String status, final LocalDate preferredCalllingTime) {
		this.id=id;
		this.fullName= fullName;
		this.officeMail = officeMail;
		this.email = mail;
		this.createdDate = createdDate;
		this.status = status;
		this.preferredCallingTime = preferredCalllingTime.toDate();
	}

	
	public ClientProspectData(final Long id, final Short prospectType,final Long entity,final String username,
			final String firstName, final String middleName,
			final String lastName, final String homePhoneNumber,
			final String workPhoneNumber, final String mobileNumber,
			final String email, final String sourceOfPublicity,
			final Date preferredCallingTime, final String note,
			final String address, final String streetArea,
			final String cityDistrict, final String state,
			final String country, final String preferredPlan,
			final String status, final String statusRemark,
			final String isDeleted) {
		this.id = id;
		this.prospectType = prospectType;
		this.setEntity(entity);
		this.username=username;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.homePhoneNumber = homePhoneNumber;
		this.workPhoneNumber = workPhoneNumber;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.sourceOfPublicity = sourceOfPublicity;
		this.preferredPlan = preferredPlan;
		this.preferredCallingTime = preferredCallingTime;
		this.note = note;
		this.address = address;
		this.streetArea = streetArea;
		this.cityDistrict = cityDistrict;
		this.state = state;
		this.country = country;
		this.status = status;
		this.statusRemark = statusRemark;
		this.isDeleted = isDeleted;
	}
	

	public Short getProspectType() {
		return prospectType;
	}

	public void setProspectType(Short prospectType) {
		this.prospectType = prospectType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getHomePhoneNumber() {
		return homePhoneNumber;
	}

	public void setHomePhoneNumber(String homePhoneNumber) {
		this.homePhoneNumber = homePhoneNumber;
	}

	public String getWorkPhoneNumber() {
		return workPhoneNumber;
	}

	public void setWorkPhoneNumber(String workPhoneNumber) {
		this.workPhoneNumber = workPhoneNumber;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSourceOfPublicity() {
		return sourceOfPublicity;
	}

	public void setSourceOfPublicity(String sourceOfPublicity) {
		this.sourceOfPublicity = sourceOfPublicity;
	}

	public Date getPreferredCallingTime() {
		return preferredCallingTime;
	}

	public void setPreferredCallingTime(Date preferredCallingTime) {
		this.preferredCallingTime = preferredCallingTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStreetArea() {
		return streetArea;
	}

	public void setStreetArea(String streetArea) {
		this.streetArea = streetArea;
	}

	public String getCityDistrict() {
		return cityDistrict;
	}

	public void setCityDistrict(String cityDistrict) {
		this.cityDistrict = cityDistrict;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPreferredPlan() {
		return preferredPlan;
	}

	public void setPreferredPlan(String preferredPlan) {
		this.preferredPlan = preferredPlan;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Collection<MCodeData> getSourceOfPublicityData() {
		return sourceOfPublicityData;
	}

	public void setSourceOfPublicityData(
			Collection<MCodeData> sourceOfPublicityData) {
		this.sourceOfPublicityData = sourceOfPublicityData;
	}

	public Collection<ProspectPlanCodeData> getPlanData() {
		return planData;
	}

	public void setPlanData(Collection<ProspectPlanCodeData> planData) {
		this.planData = planData;
	}

	public String getStatusRemark() {
		return statusRemark;
	}

	public void setStatusRemark(String statusRemark) {
		this.statusRemark = statusRemark;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(String isDeleted) {
		this.isDeleted = isDeleted;
	}

	public List<String> getCountryData() {
		return countryData;
	}

	public void setCountryData(List<String> countryData) {
		this.countryData = countryData;
	}

	public List<String> getStateData() {
		return stateData;
	}

	public void setStateData(List<String> stateData) {
		this.stateData = stateData;
	}

	public List<String> getCityData() {
		return cityData;
	}

	public void setCityData(List<String> cityData) {
		this.cityData = cityData;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getSourceOfPublicityInt() {
		return sourceOfPublicityInt;
	}

	public Long getPreferredPlanInt() {
		return preferredPlanInt;
	}
/*
	added District*/
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getOfficeMail() {
		return officeMail;
	}

	public void setOfficeMail(String officeMail) {
		this.officeMail = officeMail;
	}

	/**
	 * @return the entity
	 */
	public Long getEntity() {
		return entity;
	}

	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Long entity) {
		this.entity = entity;
	}

	public String getPreferedService() {
		return preferedService;
	}

	public void setPreferedService(String preferedService) {
		this.preferedService = preferedService;
	}

	public void setarrOfStr(String[] arrOfStr) {
		this.arrOfStr = arrOfStr;
		
	}

	
	
	

}
