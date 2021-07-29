package org.mifosplatform.organisation.office.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_office_address", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "phone_number" }, name = "phonenumber_org"),
		@UniqueConstraint(columnNames = { "email_id" }, name = "emailid_org") })
public class OfficeAddress extends AbstractPersistable<Long> {

	/**
		 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "address_name")
	private String addressName;

	@Column(name = "contact_person")
	private String contactPerson;

	@Column(name = "business_type")
	private String businessType;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "country")
	private String country;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "office_number")
	private String officeNumber;

	@Column(name = "email_id")
	private String email;

	@Column(name = "zip")
	private String zip;

	@Column(name = "company_logo")
	private String companyLogo;

	@Column(name = "VRN")
	private String vrn;

	@Column(name = "TIN")
	private String tin;

	@Column(name = "district")
	private String district;


	@OneToOne
	@JoinColumn(name = "office_id", insertable = true, updatable = true, nullable = true, unique = true)
	private Office office;

	public OfficeAddress() {
		// TODO Auto-generated constructor stub
	}

	public static OfficeAddress fromJson(final JsonCommand command, Office office) {

		final String officeNumber = command.stringValueOfParameterNamed("officeNumber");
		final String phoneNumber = command.stringValueOfParameterNamed("phoneNumber");
		final String email = command.stringValueOfParameterNamed("email");
		final String city = command.stringValueOfParameterNamed("city");
		final String state = command.stringValueOfParameterNamed("state");
		final String country = command.stringValueOfParameterNamed("country");
		final String addressName = command.stringValueOfParameterNamed("addressName");
		final String contactPerson = command.stringValueOfParameterNamed("contactPerson");
		final String zip = command.stringValueOfParameterNamed("zip");
		final String bussinessType = command.stringValueOfParameterNamed("businessType");
		final String district = command.stringValueOfParameterNamed("district");
		String companyLogo = null;
		if (command.parameterExists("companyLogo")) {
			companyLogo = command.stringValueOfParameterNamed("companyLogo");
		}

		return new OfficeAddress(officeNumber, phoneNumber, email, city, state, country, companyLogo, office,
				addressName, contactPerson, zip, bussinessType, district);
	}

	public static OfficeAddress fromJsonUpdateCrm(final String json, Office office) {

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			final String officeNumber = jsonObject.optString("officeNumber");
			final String phoneNumber = jsonObject.optString("phoneNumber");
			final String email = jsonObject.optString("email");
			final String city = jsonObject.optString("city");
			final String state = jsonObject.optString("state");
			final String country = jsonObject.optString("country");
			final String addressName = jsonObject.optString("addressName");
			final String contactPerson = jsonObject.optString("contactPerson");
			final String zip = jsonObject.optString("zip");
			final String bussinessType = jsonObject.optString("businessType");
			final String district = jsonObject.optString("district");
			String companyLogo = null;
			if (jsonObject.optString("companyLogo") != null) {
				companyLogo = jsonObject.optString("companyLogo");
			}

			return new OfficeAddress(officeNumber, phoneNumber, email, city, state, country, companyLogo, office,
					addressName, contactPerson, zip, bussinessType, district);
		} catch (JSONException e) {
			return null;
		}
	}

	public Map<String, Object> update(final JsonCommand command) {
		final Map<String, Object> actualChanges = new ConcurrentHashMap<String, Object>(1);
		final String organizationParamName = "officeNumber";
		if (command.isChangeInStringParameterNamed(organizationParamName, this.officeNumber)) {
			final String newValue = command.stringValueOfParameterNamed(organizationParamName);
			actualChanges.put(organizationParamName, newValue);
			this.officeNumber = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String phoneParamName = "phoneNumber";
		if (command.isChangeInStringParameterNamed(phoneParamName, this.phoneNumber)) {
			final String newValue = command.stringValueOfParameterNamed(phoneParamName);
			actualChanges.put(phoneParamName, newValue);
			this.phoneNumber = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String emailParamName = "email";
		if (command.isChangeInStringParameterNamed(emailParamName, this.email)) {
			final String newValue = command.stringValueOfParameterNamed(emailParamName);
			actualChanges.put(emailParamName, newValue);
			this.email = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String cityParamName = "city";
		if (command.isChangeInStringParameterNamed(cityParamName, this.city)) {
			final String newValue = command.stringValueOfParameterNamed(cityParamName);
			actualChanges.put(cityParamName, newValue);
			this.city = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String stateParamName = "state";
		if (command.isChangeInStringParameterNamed(stateParamName, this.state)) {
			final String newValue = command.stringValueOfParameterNamed(stateParamName);
			actualChanges.put(stateParamName, newValue);
			this.state = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String countryParamName = "country";
		if (command.isChangeInStringParameterNamed(countryParamName, this.country)) {
			final String newValue = command.stringValueOfParameterNamed(countryParamName);
			actualChanges.put(countryParamName, newValue);
			this.country = StringUtils.defaultIfEmpty(newValue, null);
		}

		if (command.parameterExists("companyLogo")) {
			final String companyLogoParamName = "companyLogo";
			if (command.isChangeInStringParameterNamed(companyLogoParamName, this.companyLogo)) {
				final String newValue = command.stringValueOfParameterNamed(companyLogoParamName);
				actualChanges.put(companyLogoParamName, newValue);
				this.companyLogo = StringUtils.defaultIfEmpty(newValue, null);
			}
		}

		if (command.parameterExists("addressName")) {
			final String addressNameParamName = "addressName";
			if (command.isChangeInStringParameterNamed(addressNameParamName, this.addressName)) {
				final String newValue = command.stringValueOfParameterNamed(addressNameParamName);
				actualChanges.put(addressNameParamName, newValue);
				this.addressName = StringUtils.defaultIfEmpty(newValue, null);
			}
		}

		final String contactParamName = "contactPerson";
		if (command.isChangeInStringParameterNamed(contactParamName, this.contactPerson)) {
			final String newValue = command.stringValueOfParameterNamed(contactParamName);
			actualChanges.put(contactParamName, newValue);
			this.contactPerson = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String zipParamName = "zip";
		if (command.isChangeInStringParameterNamed(zipParamName, this.zip)) {
			final String newValue = command.stringValueOfParameterNamed("zip");
			actualChanges.put(zipParamName, newValue);
			this.zip = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String businessTypeParamName = "businessType";
		if (command.isChangeInStringParameterNamed(businessTypeParamName, this.businessType)) {
			final String newValue = command.stringValueOfParameterNamed(businessTypeParamName);
			actualChanges.put(businessTypeParamName, newValue);
			this.businessType = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String districtParamName = "district";
		if (command.isChangeInStringParameterNamed(districtParamName, this.district)) {
			final String newValue = command.stringValueOfParameterNamed(districtParamName);
			actualChanges.put(districtParamName, newValue);
			this.district = StringUtils.defaultIfEmpty(newValue, null);
		}

		return actualChanges;

	}

	public OfficeAddress(final String officeNumber, final String phoneNumber, final String email, final String city,
			final String state, final String country, final String companyLogo, final Office office, String addressName,
			final String contactPerson, final String zip, final String businessType, final String district) {

		this.officeNumber = officeNumber;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.city = city;
		this.state = state;
		this.country = country;
		this.companyLogo = (companyLogo != null) ? companyLogo : null;
		this.office = office;
		this.addressName = addressName;
		this.contactPerson = contactPerson;
		this.zip = zip;
		this.businessType = businessType;
		this.district = district;
	}

	public String getAddressName() {
		return addressName;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getOfficeNumber() {
		return officeNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getcontactPerson() {
		return contactPerson;
	}

	public String getzip() {
		return zip;
	}

	public String getbusinessType() {
		return businessType;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public void setCompanyLogo(final String imageLocation) {

		this.companyLogo = imageLocation;
	}

	public String getDistrict() {
		return district;
	}



	public String obrmRequestInput() {
		StringBuilder sb = new StringBuilder("<MSO_OP_CUST_REGISTER_CUSTOMER_inputFlist>");
		sb.append("<MSO_FLD_CATV_ACCOUNT_OBJ/>");
		sb.append("<MSO_FLD_ROLES/>");
		sb.append("<ACCTINFO elem=\"0\">");
		sb.append("<MSO_FLD_AREA></MSO_FLD_AREA>");
		sb.append("<MSO_FLD_CONTACT_PREF>0</MSO_FLD_CONTACT_PREF>");
		sb.append("<MSO_FLD_ET_ZONE/>");
		sb.append("<MSO_FLD_RMAIL>" + this.email + "</MSO_FLD_RMAIL>");
		sb.append("<MSO_FLD_RMN>" + this.phoneNumber + "</MSO_FLD_RMN>");
		sb.append("<MSO_FLD_VAT_ZONE/>");
		sb.append("<BAL_INFO elem=\"0\"/>");
		sb.append("<BUSINESS_TYPE>13000000</BUSINESS_TYPE>");
		sb.append("<CURRENCY>356</CURRENCY>");
		sb.append("<POID>0.0.0.1 /account -1 0</POID>");
		sb.append("</ACCTINFO>");
		sb.append("<FLAGS>0</FLAGS>");
		sb.append("<LOGIN>" + this.phoneNumber + "</LOGIN>");
		sb.append("<NAMEINFO elem=\"1\">");
		sb.append("<MSO_FLD_AREA_NAME>HighTechCity - Madhapur</MSO_FLD_AREA_NAME>");
		sb.append("<MSO_FLD_BUILDING_NAME>sapphire</MSO_FLD_BUILDING_NAME>");
		sb.append("<MSO_FLD_DISTRICT_NAME>Hyderabad</MSO_FLD_DISTRICT_NAME>");
		sb.append("<MSO_FLD_LANDMARK>PNB</MSO_FLD_LANDMARK>");
		sb.append("<MSO_FLD_LOCATION_NAME>loc</MSO_FLD_LOCATION_NAME>");
		sb.append("<MSO_FLD_STREET_NAME>21 North</MSO_FLD_STREET_NAME>");
		sb.append("<ADDRESS>32</ADDRESS>");
		sb.append("<CITY>" + this.city + "</CITY>");
		sb.append("<COMPANY>" + this.office.getName() + "+</COMPANY>");
		sb.append("<COUNTRY>" + this.country + "</COUNTRY>");
		sb.append("<EMAIL_ADDR>" + this.email + "</EMAIL_ADDR>");
		sb.append("<FIRST_NAME>test</FIRST_NAME>");
		sb.append("<LASTSTAT_CMNT/>");
		sb.append("<LASTSTAT_CMNT/>");
		sb.append("<LAST_NAME>one</LAST_NAME>");
		sb.append("<MIDDLE_NAME/>");
		sb.append("<PHONES elem=\"1\">");
		sb.append("<PHONE>" + this.phoneNumber + "</PHONE>");
		sb.append("<TYPE>1</TYPE>");
		sb.append("</PHONES>");
		sb.append("<PHONES elem=\"5\">");
		sb.append("<PHONE>" + this.phoneNumber + "</PHONE>");
		sb.append("<TYPE>5</TYPE>");
		sb.append("</PHONES>");
		sb.append("<PHONES elem=\"2\">");
		sb.append("<PHONE>" + this.phoneNumber + "</PHONE>");
		sb.append("<TYPE>2</TYPE>");
		sb.append("</PHONES>");
		sb.append("<SALUTATION>M/s.</SALUTATION>");
		sb.append("<STATE>" + this.state + "</STATE>");
		sb.append("<ZIP>" + this.zip + "</ZIP>");
		sb.append("</NAMEINFO>");
		sb.append("<PASSWD_CLEAR>XXXX</PASSWD_CLEAR>");
		sb.append("<POID>0.0.0.1 /plan -1 0</POID>");
		sb.append("<PROFILES elem=\"0\">");
		sb.append("<INHERITED_INFO>");
		sb.append("<MSO_FLD_WHOLESALE_INFO>");
		sb.append("<MSO_FLD_AGREEMENT_NO>LCO-AGR-001</MSO_FLD_AGREEMENT_NO>");
		sb.append("<MSO_FLD_COMMISION_MODEL>0</MSO_FLD_COMMISION_MODEL>");
		sb.append("<MSO_FLD_COMMISION_SERVICE>1</MSO_FLD_COMMISION_SERVICE>");
		sb.append("<MSO_FLD_DAS_TYPE>DAS-I</MSO_FLD_DAS_TYPE>");
		sb.append("<MSO_FLD_ENT_TAX_NO>E001</MSO_FLD_ENT_TAX_NO>");
		sb.append("<MSO_FLD_ERP_CONTROL_ACCT_ID>1972</MSO_FLD_ERP_CONTROL_ACCT_ID>");
		sb.append("<MSO_FLD_FROM_DATE>1519669800</MSO_FLD_FROM_DATE>");
		sb.append("<MSO_FLD_PAN_NO>ASDFG1872A</MSO_FLD_PAN_NO>");
		sb.append("<MSO_FLD_POSTAL_REG_NO>PR001</MSO_FLD_POSTAL_REG_NO>");
		sb.append("<MSO_FLD_PP_TYPE>0</MSO_FLD_PP_TYPE>");
		sb.append("<MSO_FLD_PREF_DOM>1</MSO_FLD_PREF_DOM>");
		sb.append("<MSO_FLD_ST_REG_NO>S001</MSO_FLD_ST_REG_NO>");
		sb.append("<MSO_FLD_TO_DATE>1546194600</MSO_FLD_TO_DATE>");
		sb.append("<MSO_FLD_VAT_TAX_NO>V001</MSO_FLD_VAT_TAX_NO>");
		sb.append("<PARENT>0.0.0.1 /account 458264 7</PARENT>");
		sb.append("</MSO_FLD_WHOLESALE_INFO>");
		sb.append("</INHERITED_INFO>");
		sb.append("<PROFILE_OBJ>0.0.0.1 /profile/wholesale -1 0</PROFILE_OBJ>");
		sb.append("</PROFILES>");
		sb.append("<PROGRAM_NAME>OAP|testcsrone</PROGRAM_NAME>");
		sb.append("<USERID>0.0.0.1 /account 452699 0</USERID>");
		sb.append("</MSO_OP_CUST_REGISTER_CUSTOMER_inputFlist>");

		System.out.println(sb.toString());
		return sb.toString();
	}

}
