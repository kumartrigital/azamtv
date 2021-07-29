package org.mifosplatform.portfolio.client.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "additional_client_fields")
public class ClientAdditionalfields extends AbstractPersistable<Long>{


	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "job_title")
	private String jobTitle;
	

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", nullable = true)
    private CodeValue gender;

	@Column(name = "finance_id")
	private String financeId;
	
	@Column(name = "uts_customer_id")
	private String utsCustomerId;
	
	@Column(name = "date_of_birth", nullable = true)
	private Date dateOfBirth;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nationality_id", nullable = true)
	private CodeValue nationality;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "age_group_id", nullable = true)
	private CodeValue ageGroup;
	   
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_type", nullable = true)
	private CodeValue customerIdentifier;

	@Column(name = "id_number")
	private String idNumber;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prefere_lan_id", nullable = true)
	private CodeValue prefereLan;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prefere_communication_id", nullable = true)
	private CodeValue prefereCommunication;
	
	@Column(name = "remarks")
	private String remarks;

	public ClientAdditionalfields(){
		
	}

	public ClientAdditionalfields(Long clientId, String jobTitle, String finanaceId, String utsCustomerId, LocalDate dateOfBirth, String identificationNumber, String remarks,
			   CodeValue gender, CodeValue nationality, CodeValue customerIdentifier, CodeValue preferLang, CodeValue preferCommunication, CodeValue ageGroup) {
		
		this.jobTitle =  jobTitle;
		this.financeId = finanaceId;
		this.utsCustomerId = utsCustomerId;
		this.gender =gender;
		this.dateOfBirth = dateOfBirth != null?dateOfBirth.toDate():null;
		this.nationality = nationality;
		this.customerIdentifier = customerIdentifier;
		this.idNumber = identificationNumber;
		this.remarks = remarks;
		this.clientId = clientId;
		this.prefereCommunication = preferCommunication;
		this.prefereLan  =  preferLang;
		this.ageGroup = ageGroup;
		
		
          
	}


	public static ClientAdditionalfields fromJson(Long clientId, CodeValue gender,CodeValue nationality, CodeValue customerIdentifier,
			CodeValue preferLang, CodeValue preferCommunication,CodeValue ageGroup, JsonCommand command) {
	
		    final String jobTitle = command.stringValueOfParameterNamed(ClientApiConstants.jobTitleParamName);
		    final String finanaceId = command.stringValueOfParameterNamed(ClientApiConstants.fiananceIdParamName);
		    final String utsCustomerId = command.stringValueOfParameterNamed(ClientApiConstants.utsCustomerIdParamName);
		    final LocalDate dateOfBirth =command.localDateValueOfParameterNamed(ClientApiConstants.dateOfBirthParamName);
		    final String identificationNumber = command.stringValueOfParameterNamed(ClientApiConstants.idNumberParamName);
		    final String remarks = command.stringValueOfParameterNamed(ClientApiConstants.remarksParamName);
		    
		    return new ClientAdditionalfields(clientId,jobTitle,finanaceId,utsCustomerId,dateOfBirth,identificationNumber,remarks,gender,nationality,
		    		customerIdentifier,preferLang,preferCommunication,ageGroup);
	}


	public void upadate(CodeValue gender, CodeValue nationality,CodeValue customerIdentifier, CodeValue preferLang,
			CodeValue preferCommunication, CodeValue ageGroup,JsonCommand command) {

        
		if (command.isChangeInStringParameterNamed(ClientApiConstants.jobTitleParamName, this.jobTitle)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.jobTitleParamName);
            this.jobTitle = StringUtils.defaultIfEmpty(newValue, null);
        }
		
		if (command.isChangeInStringParameterNamed(ClientApiConstants.fiananceIdParamName, this.financeId)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.fiananceIdParamName);
            this.financeId = StringUtils.defaultIfEmpty(newValue, null);
        }
		
		if (command.isChangeInStringParameterNamed(ClientApiConstants.utsCustomerIdParamName, this.utsCustomerId)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.utsCustomerIdParamName);
            this.utsCustomerId = StringUtils.defaultIfEmpty(newValue, null);
        }
		
		if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.dateOfBirthParamName, dateOfBirthLocalDate())) {
			 final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.dateOfBirthParamName);
			 
			 this.dateOfBirth = newValue !=null?newValue.toDate():null;
        }
		
		if (command.isChangeInStringParameterNamed(ClientApiConstants.idNumberParamName, this.idNumber)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.idNumberParamName);
            this.idNumber = StringUtils.defaultIfEmpty(newValue, null);
        }
		
		if (command.isChangeInStringParameterNamed(ClientApiConstants.remarksParamName, this.remarks)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.remarksParamName);
            this.remarks = StringUtils.defaultIfEmpty(newValue, null);
        }
		
		this.gender =gender;
		this.nationality = nationality;
		this.customerIdentifier = customerIdentifier;
		this.prefereCommunication = preferCommunication;
		this.prefereLan  =  preferLang;
		this.ageGroup = ageGroup;
       
	}
	
	 public LocalDate dateOfBirthLocalDate() {
		 LocalDate dateOfBirth = null;
		 if (this.dateOfBirth != null) {
		 dateOfBirth = LocalDate.fromDateFields(this.dateOfBirth);
		 }
		 return dateOfBirth;
		 }

	
	}
	
	
			

	


