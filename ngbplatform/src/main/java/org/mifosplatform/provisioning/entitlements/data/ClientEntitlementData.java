package org.mifosplatform.provisioning.entitlements.data;

public class ClientEntitlementData {

	private String emailId;
	private String fullName;
	private String status;
	private String login;
	private String password;
	private String selfcareUsername;
	private String selfcarePassword;
	private boolean results;
	private String firstName;
	private String lastName;

	public ClientEntitlementData(String emailId, String firstName, 
			String lastName, String selfcareUsername, String selfcarePassword) {
	
		this.emailId = emailId;
		this.selfcareUsername = selfcareUsername;
		this.selfcarePassword = selfcarePassword;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public ClientEntitlementData(String status, boolean results) {	
		// TODO Auto-generated constructor stub
		this.status=status;
		this.results=results;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getFullName() {
		return fullName;
	}

	public boolean isResults() {
		return results;
	}

	public String getStatus() {
		return status;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getSelfcareUsername() {
		return selfcareUsername;
	}

	public String getSelfcarePassword() {
		return selfcarePassword;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	

}
