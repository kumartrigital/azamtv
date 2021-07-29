package org.mifosplatform.sample.comapany.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.broadcaster.domain.Broadcaster;

@Entity
@Table(name = "company_registration")
public class CompanyRegistration {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String companyName;
	private String companyLoc;
	private String contact;
	private String founder;
	private String type;
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
	public CompanyRegistration(Long id, String companyName, String companyLoc, String contact, String founder,
			String type) {
		super();
		this.id = id;
		this.companyName = companyName;
		this.companyLoc = companyLoc;
		this.contact = contact;
		this.founder = founder;
		this.type = type;
	}

	public static CompanyRegistration formJson(JsonCommand command) {
		
		Long id = command.longValueOfParameterNamed("id");
		String companyName = command.stringValueOfParameterNamed("companyName");
		String companyLoc = command.stringValueOfParameterNamed("companyLoc");
		String contact = command.stringValueOfParameterNamed("contact");
		String founder = command.stringValueOfParameterNamed("founder");
		String type = command.stringValueOfParameterNamed("type");
		
		return new CompanyRegistration(id, companyName, companyLoc, contact,founder, type);
	}
	
	

}
