package org.mifosplatform.portfolio.activationprocess.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;

@Entity
@Table(name = "b_lease_details")
public class LeaseDetails {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "office_id")
	private Long officeId;

	@Column(name = "salutation")
	private String salutation;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@Column(name = "nin_number")
	private String NIN;

	@Column(name = "bvn_number")
	private String BVN;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(name = "country")
	private String country;

	@Column(name = "status")
	private String Status;

	@Column(name = "device_id")
	private String Device;

	@Column(name = "voucher_id")
	private String voucher;

	@Column(name = "otp")
	private String otp;

	@Column(name = "image_path")
	private String imagePath;

	@Column(name = "image_verfication")
	private String imageVerification;

	@Column(name = "bank_name")
	private String bankName;

	@Column(name = "account_no")
	private String accountNo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getNIN() {
		return NIN;
	}

	public void setNIN(String nIN) {
		NIN = nIN;
	}

	public String getBVN() {
		return BVN;
	}

	public void setBVN(String bVN) {
		BVN = bVN;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getDevice() {
		return Device;
	}

	public void setDevice(String device) {
		Device = device;
	}

	public String getVoucher() {
		return voucher;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageVerification() {
		return imageVerification;
	}

	public void setImageVerification(String imageVerification) {
		this.imageVerification = imageVerification;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public LeaseDetails(Long officeId, String firstName, String lastName, String email, String mobileNumber, String nIN,
			String bVN, String city, String state, String country, String device, String voucher) {
		this.officeId = officeId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.NIN = nIN;
		this.BVN = bVN;
		this.city = city;
		this.state = state;
		this.country = country;
		this.Device = device;
		this.voucher = voucher;
	}

	public LeaseDetails() {
	}

	public LeaseDetails fromjson(JsonCommand command) {
		Long officeId = command.longValueOfParameterNamed("officeId");
		String firstName = command.stringValueOfParameterName("forename");
		String lastName = command.stringValueOfParameterName("surname");
		String email = command.stringValueOfParameterName("email");
		String mobileNumber = command.stringValueOfParameterName("mobile");
		String NIN = command.stringValueOfParameterName("NIN");
		String BVN = command.stringValueOfParameterName("BVN");
		String city = command.stringValueOfParameterName("city");
		String state = command.stringValueOfParameterName("state");
		String country = command.stringValueOfParameterName("country");
		String deviceId = command.stringValueOfParameterName("deviceId");
		String voucherId = command.stringValueOfParameterName("voucherId");

		return new LeaseDetails(officeId, firstName, lastName, email, mobileNumber, NIN, BVN, city, state, country,
				deviceId, voucherId);

	}
}