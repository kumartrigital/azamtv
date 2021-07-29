package org.mifosplatform.sample.comapany.data;

public class CompanyRegistrationData {
	
	private Long id;
	private String companyName;
	private String companyLoc;
	private String contact;
	private String founder;
	private String type;
	
	public CompanyRegistrationData(Long id, String companyName, String companyLoc, String contact, String founder,
			String type) {
		super();
		this.id = id;
		this.companyName = companyName;
		this.companyLoc = companyLoc;
		this.contact = contact;
		this.founder = founder;
		this.type = type;
	}
	public CompanyRegistrationData()
	{
		
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyLoc() {
		return companyLoc;
	}
	public void setCompanyLoc(String companyLoc) {
		this.companyLoc = companyLoc;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getFounder() {
		return founder;
	}
	public void setFounder(String founder) {
		this.founder = founder;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
