/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.RandomPasswordGenerator;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.domain.Address;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.portfolio.client.api.ClientApiConstants;
import org.mifosplatform.portfolio.group.domain.Group;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "m_client", uniqueConstraints = { @UniqueConstraint(columnNames = { "account_no" }, name = "account_no_UNIQUE"),
		@UniqueConstraint(columnNames = { "email" }, name = "email_key"),@UniqueConstraint(columnNames = { "login" }, name = "login_key")})
public final class Client extends AbstractPersistable<Long> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "account_no", length = 20, unique = true, nullable = false)
    private String accountNumber;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    /**
     * A value from {@link ClientStatus}.
     */
    @Column(name = "status_enum", nullable = false)
    private Integer status;

    @Column(name = "activation_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date activationDate;

    @Column(name = "firstname", length = 50)
    private String firstname;
    
    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "middlename", length = 50)
    private String middlename;

    @Column(name = "lastname", length = 50)
    private String lastname;

    @Column(name = "fullname", length = 100)
    private String fullname;

    @Column(name = "display_name", length = 100, nullable = false)
    private String displayName;

    @Column(name = "external_id", length = 100, nullable = true, unique = true)
    private String externalId;

    @Column(name = "image_key", length = 500, nullable = true)
    private String imageKey;
    
    @Column(name = "category_type", length = 5)
    private Long categoryType;
    
    @Column(name = "email", length = 100)
    private String email;

   	@Column(name = "phone", length = 100)
    private String phone;
    
    @Column(name = "home_phone_number", length = 20)
    private String homePhoneNumber;
    
    @Column(name = "login", length = 100)
    private String login;
    @Column(name = "password", length = 100)
    private String password;
    @Column(name = "group_Id", length = 100)
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closure_reason_cv_id", nullable = true)
    private CodeValue closureReason;
    
    @ManyToOne(optional = true)
    @JoinColumn(name = "closedon_userid", nullable = true)
    private AppUser closeddBy;
    

    @ManyToMany
    @JoinTable(name = "m_group_client", joinColumns = @JoinColumn(name = "client_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups;

    @Transient
    private boolean accountNumberRequiresAutoGeneration = false;

    @Column(name = "closedon_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date closureDate;
    
    
    @Column(name = "exempt_tax",nullable = false)
	private char taxExemption='N';
    

    @Column(name = "bill_mode",nullable = true)
   	private String billMode;

    @Column(name = "is_indororp",nullable = false)
 	private String entryType;
    
    @Column(name = "parent_id",nullable = true)
 	 private Long parentId;

    @Column(name = "po_id",nullable = true)	
     private String poid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_key",nullable = true)	
    private CodeValue idkey;

    @Column(name = "id_value",nullable = true)	
    private String idValue;

    public static Client createNew(final Office clientOffice, final Group clientParentGroup, 
    		final JsonCommand command , String accountNumber) {

        String accountNo = command.stringValueOfParameterNamed(ClientApiConstants.accountNoParamName);
        
        if(StringUtils.isBlank(accountNo)){
        	if(accountNumber!=null) {
        		accountNo=accountNumber;
        	}else {
        		
        	}
        }
        final String externalId = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);

        final String firstname = command.stringValueOfParameterNamed(ClientApiConstants.firstnameParamName);
        final String middlename = command.stringValueOfParameterNamed(ClientApiConstants.middlenameParamName);
        final String lastname = command.stringValueOfParameterNamed(ClientApiConstants.lastnameParamName);
        final String fullname = command.stringValueOfParameterNamed(ClientApiConstants.fullnameParamName);
        final Long categoryType=command.longValueOfParameterNamed(ClientApiConstants.clientCategoryParamName);
        final String phone = command.stringValueOfParameterNamed(ClientApiConstants.phoneParamName);
        final String title = command.stringValueOfParameterNamed(ClientApiConstants.titleParamName);
        final String homePhoneNumber = command.stringValueOfParameterNamed(ClientApiConstants.homePhoneNumberParamName);
	    String email = command.stringValueOfParameterNamed(ClientApiConstants.emailParamName);
	    String login=command.stringValueOfParameterNamed(ClientApiConstants.loginParamName);
	    final String entryType = command.stringValueOfParameterNamed(ClientApiConstants.entryTypeParamName);

	    final String password=command.stringValueOfParameterNamed(ClientApiConstants.passwordParamName);
	    final Long groupName=command.longValueOfParameterNamed(ClientApiConstants.groupParamName);

	    final String billMode = command.stringValueOfParameterNamed(ClientApiConstants.billModeParamName);
	    
	 
	    if(email.isEmpty()){
	    	email=null;
	    }
	    if(login.isEmpty()){
	    	login=null;
	    }
	   /* if(groupName.isEmpty()){
	    	groupName=null;
	    }*/
	    ClientStatus status =  ClientStatus.NEW;
	    final boolean active = true;
       

        LocalDate activationDate = command.localDateValueOfParameterNamed(ClientApiConstants.activationDateParamName);
        if (active) {
            status = ClientStatus.NEW;
            if(activationDate==null) {
            activationDate = DateUtils.getLocalDateOfTenant(); 
            }
        }
        final String idValue = command.stringValueOfParameterName("idValue");
        
        return new Client(status, clientOffice, clientParentGroup, accountNo, firstname, middlename, lastname, fullname, activationDate,
                externalId,categoryType,email,phone,homePhoneNumber,login,password,groupName,entryType,title,billMode,idValue);
    }

    public CodeValue getIdkey() {
		return idkey;
	}

	public void setIdkey(CodeValue idkey) {
		this.idkey = idkey;
	}

	protected Client() {
        //
    }

    private Client(final ClientStatus status, final Office office, final Group clientParentGroup, final String accountNo,
            final String firstname, final String middlename, final String lastname, final String fullname, final LocalDate activationDate,
            final String externalId, final Long categoryType, final String email, final String phone,final String homePhoneNumber,
            final String login, final String password,final Long groupName,final String entryType,final String title,String billMode, final String idValue) {
    	if(StringUtils.isBlank(accountNo)){
    		this.accountNumber = "CR-"+Calendar.getInstance().getTimeInMillis();
    	}else{
    		this.accountNumber = accountNo;
    	}
        this.status = status.getValue();
        this.office = office;
        this.categoryType=categoryType;
        this.email=email;
        this.phone=phone;
        this.homePhoneNumber=homePhoneNumber;
        this.login=login;
        this.password=password;
        this.groupId = groupName;
        this.entryType=entryType;
        if (StringUtils.isNotBlank(externalId)) {
            this.externalId = externalId.trim();
        } else {
            this.externalId = null;
        }
        if (activationDate != null) {
            this.activationDate = activationDate.toDateMidnight().toDate();
        }
        if (StringUtils.isNotBlank(firstname)) {
            this.firstname = firstname.trim();
        } else {
            this.firstname = null;
        }
          this.title = title;
        if (StringUtils.isNotBlank(middlename)) {
            this.middlename = middlename.trim();
        } else {
            this.middlename = null;
        }

        if (StringUtils.isNotBlank(lastname)) {
            this.lastname = lastname.trim();
        } else {
            this.lastname = null;
        }

        if (StringUtils.isNotBlank(fullname)) {
            this.fullname = fullname.trim();
        } else {
            this.fullname = null;
        }

        if (clientParentGroup != null) {
            this.groups = new HashSet<Group>();
            this.groups.add(clientParentGroup);
        }
        this.billMode = billMode;
        this.idValue = idValue;
        deriveDisplayName(entryType);
        validateNameParts();
    }
    
    
    

    public boolean isAccountNumberRequiresAutoGeneration() {
        return this.accountNumberRequiresAutoGeneration;
    }

    public void setAccountNumberRequiresAutoGeneration(final boolean accountNumberRequiresAutoGeneration) {
        this.accountNumberRequiresAutoGeneration = accountNumberRequiresAutoGeneration;
    }

    public boolean identifiedBy(final String identifier) {
        return identifier.equalsIgnoreCase(this.externalId);
    }

    public boolean identifiedBy(final Long clientId) {
        return getId().equals(clientId);
    }

    public void updateAccountNo(final String accountIdentifier, final Collection<MCodeData> NUMBERGENERATOR) {
       for(MCodeData data:NUMBERGENERATOR){
    	   if(data.getOrderPossition() == 1){
    		   	this.accountNumber = data.getmCodeValue()+accountIdentifier;
    	        this.accountNumberRequiresAutoGeneration = false;
    	        break;
    	   }
       }
    	
    }

    public void activate(final DateTimeFormatter formatter, final LocalDate activationLocalDate) {
        if (isActive()) {
            final String defaultUserMessage = "Cannot activate client. Client is already active.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.already.active", defaultUserMessage,
                    ClientApiConstants.activationDateParamName, activationLocalDate.toString(formatter));

            final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }

        if (isDateInTheFuture(activationLocalDate)) {

            final String defaultUserMessage = "Activation date cannot be in the future.";
            final ApiParameterError error = ApiParameterError.parameterError("error.msg.clients.activationDate.in.the.future",
                    defaultUserMessage, ClientApiConstants.activationDateParamName, activationLocalDate);

            final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(dataValidationErrors);
        }

        this.activationDate = activationLocalDate.toDate();
        this.status = ClientStatus.ACTIVE.getValue();
    }

    public boolean isNotActive() {
        return !isActive();
    }

    public boolean isActive() {
        return ClientStatus.fromInt(this.status).isActive();
    }

    public boolean isNotPending() {
        return !isNew();
    }

    public boolean isNew() {
        return ClientStatus.fromInt(this.status).isNew();
    }

    private boolean isDateInTheFuture(final LocalDate localDate) {
        return localDate.isAfter(DateUtils.getLocalDateOfTenant());
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(9);
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.titleParamName, this.title)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.titleParamName);
            this.title = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInIntegerParameterNamed(ClientApiConstants.statusParamName, this.status)) {
            final Integer newValue = command.integerValueOfParameterNamed(ClientApiConstants.statusParamName);
            actualChanges.put(ClientApiConstants.statusParamName, ClientEnumerations.status(newValue));
            this.status = ClientStatus.fromInt(newValue).getValue();
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.accountNoParamName, this.accountNumber)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.accountNoParamName);
            actualChanges.put(ClientApiConstants.accountNoParamName, newValue);
            this.accountNumber = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.externalIdParamName, this.externalId)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.externalIdParamName);
            actualChanges.put(ClientApiConstants.externalIdParamName, newValue);
            this.externalId = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.firstnameParamName, this.firstname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.firstnameParamName);
            actualChanges.put(ClientApiConstants.firstnameParamName, newValue);
            this.firstname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.middlenameParamName, this.middlename)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.middlenameParamName);
            actualChanges.put(ClientApiConstants.middlenameParamName, newValue);
            this.middlename = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.lastnameParamName, this.lastname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.lastnameParamName);
            actualChanges.put(ClientApiConstants.lastnameParamName, newValue);
            this.lastname = StringUtils.defaultIfEmpty(newValue, null);
        }

        if (command.isChangeInStringParameterNamed(ClientApiConstants.fullnameParamName, this.fullname)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.fullnameParamName);
            actualChanges.put(ClientApiConstants.fullnameParamName, newValue);
            this.fullname = newValue;
        }
        
        if (command.isChangeInLongParameterNamed(ClientApiConstants.clientCategoryParamName, this.categoryType)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.clientCategoryParamName);
            actualChanges.put(ClientApiConstants.clientCategoryParamName, newValue);
            this.categoryType = new Long(newValue);
        }
        
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.emailParamName, this.email)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.emailParamName);
            actualChanges.put(ClientApiConstants.emailParamName, newValue);
            this.email = StringUtils.defaultIfEmpty(newValue,null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.phoneParamName,this.phone)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.phoneParamName);
            actualChanges.put(ClientApiConstants.phoneParamName, newValue);
            this.phone= StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.homePhoneNumberParamName,this.homePhoneNumber)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.homePhoneNumberParamName);
            actualChanges.put(ClientApiConstants.homePhoneNumberParamName, newValue);
            this.homePhoneNumber= StringUtils.defaultIfEmpty(newValue, null);
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.loginParamName, this.login)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.loginParamName);
            actualChanges.put(ClientApiConstants.loginParamName, newValue);
            this.login = StringUtils.defaultIfEmpty(newValue,null);
        }
        if (command.isChangeInStringParameterNamed(ClientApiConstants.passwordParamName, this.password)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.passwordParamName);
            actualChanges.put(ClientApiConstants.passwordParamName, newValue);
            this.password = StringUtils.defaultIfEmpty(newValue,null);
        }
        if (command.isChangeInLongParameterNamed(ClientApiConstants.groupParamName, this.groupId)) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.groupParamName);
            actualChanges.put(ClientApiConstants.groupParamName, newValue);
            this.groupId = newValue;
        }
        
        if (command.isChangeInStringParameterNamed(ClientApiConstants.entryTypeParamName, this.entryType)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.entryTypeParamName);
            actualChanges.put(ClientApiConstants.entryTypeParamName, newValue);
            this.entryType = StringUtils.defaultIfEmpty(newValue,null);
        }
        
        if (this.idkey != null && command.isChangeInLongParameterNamed(ClientApiConstants.idKeyParamName, this.idkey.getId())) {
            final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.idKeyParamName);
            actualChanges.put(ClientApiConstants.idKeyParamName, newValue);
        }
        
        if (this.idValue!=null && command.isChangeInStringParameterNamed(ClientApiConstants.idValueParamName, this.idValue)) {
            final String newValue = command.stringValueOfParameterNamed(ClientApiConstants.idValueParamName);
            actualChanges.put(ClientApiConstants.idValueParamName, newValue);
            this.idValue = StringUtils.defaultIfEmpty(newValue,null);
        }
        
        validateNameParts();

        final String dateFormatAsInput = command.dateFormat();
        final String localeAsInput = command.locale();

        if (command.isChangeInLocalDateParameterNamed(ClientApiConstants.activationDateParamName, getActivationLocalDate())) {
            final String valueAsInput = command.stringValueOfParameterNamed(ClientApiConstants.activationDateParamName);
            actualChanges.put(ClientApiConstants.activationDateParamName, valueAsInput);
            actualChanges.put(ClientApiConstants.dateFormatParamName, dateFormatAsInput);
            actualChanges.put(ClientApiConstants.localeParamName, localeAsInput);

            final LocalDate newValue = command.localDateValueOfParameterNamed(ClientApiConstants.activationDateParamName);
            this.activationDate = newValue.toDate();
        }

        deriveDisplayName(this.entryType);
        return actualChanges;
    }

    private void validateNameParts() {
        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("client");

        if (StringUtils.isNotBlank(this.fullname)) {

            baseDataValidator.reset().parameter(ClientApiConstants.firstnameParamName).value(this.firstname)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.fullnameParamName, this.fullname);

            baseDataValidator.reset().parameter(ClientApiConstants.middlenameParamName).value(this.middlename)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.fullnameParamName, this.fullname);

            baseDataValidator.reset().parameter(ClientApiConstants.lastnameParamName).value(this.lastname)
                    .mustBeBlankWhenParameterProvided(ClientApiConstants.fullnameParamName, this.fullname);
        }

        if (StringUtils.isBlank(this.fullname)) {
            baseDataValidator.reset().parameter(ClientApiConstants.firstnameParamName).value(this.firstname).notBlank()
                    .notExceedingLengthOf(50);
            baseDataValidator.reset().parameter(ClientApiConstants.middlenameParamName).value(this.middlename).ignoreIfNull()
                    .notExceedingLengthOf(50);
            baseDataValidator.reset().parameter(ClientApiConstants.lastnameParamName).value(this.lastname).notBlank()
                    .notExceedingLengthOf(50);
        }

        if (this.activationDate != null) {
            if (office.isOpeningDateAfter(getActivationLocalDate())) {
                final String defaultUserMessage = "Client activation date cannot be a date before the office opening date.";
                final ApiParameterError error = ApiParameterError.parameterError(
                        "error.msg.clients.activationDate.cannot.be.before.office.activation.date", defaultUserMessage,
                        ClientApiConstants.activationDateParamName, getActivationLocalDate());
                dataValidationErrors.add(error);
            }
        }

        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }

    private void deriveDisplayName(final String entryType) {
    	 
    	StringBuilder nameBuilder = new StringBuilder();
    	
    	if(entryType.equalsIgnoreCase("IND")){
    	    
    		if (StringUtils.isNotBlank(this.firstname)) {
    	       nameBuilder.append(this.firstname).append(' ');
    	    }
    	    if (StringUtils.isNotBlank(this.lastname)) {
    	            nameBuilder.append(this.lastname);
    	    }
    	    if (StringUtils.isNotBlank(this.middlename)) {
    	            nameBuilder.append(this.middlename).append(' ');
    	    }
    	}else{

    		if (StringUtils.isNotBlank(this.firstname)) {
     	       nameBuilder.append(this.firstname).append(' ');
     	    }
    	}
        if (StringUtils.isNotBlank(this.fullname)) {
            nameBuilder = new StringBuilder(this.fullname);
        }
        this.displayName = nameBuilder.toString();
    }

    public LocalDate getActivationLocalDate() {
        LocalDate activationLocalDate = null;
        if (this.activationDate != null) {
            activationLocalDate = LocalDate.fromDateFields(this.activationDate);
        }
        return activationLocalDate;
    }

    public boolean isOfficeIdentifiedBy(final Long officeId) {
        return this.office.identifiedBy(officeId);
    }

    public String imageKey() {
        return imageKey;
    }

    public void updateImageKey(final String imageKey) {
        this.imageKey = imageKey;
    }

    
    public Long getGroupId() {
		return groupId;
	}

	public CodeValue getClosureReason() {
		return closureReason;
	}

	public AppUser getCloseddBy() {
		return closeddBy;
	}

	public Date getClosureDate() {
		return closureDate;
	}

	public String getEntryType() {
		return entryType;
	}

	public Long officeId() {
        return this.office.getId();
    }
    
    public String getName(){
    	return displayName;
    }
    public Date getActivationDate() {
    	final Date date=DateUtils.getDateOfTenant();
		return date;
	}
    public String getAccountNo(){
    	final String accountno=this.accountNumber;
    	return accountno;
    }

	public String getAccountNumber() {
		return accountNumber;
	}

	public Office getOffice() {
		return office;
	}

	public Integer getStatus() {
		return status;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public String getLastname() {
		return lastname;
	}

	public String getFullname() {
		return fullname;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getExternalId() {
		return externalId;
	}

	public String getImageKey() {
		return imageKey;
	}

	public Long getCategoryType() {
		return categoryType;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHomePhoneNumber() {
		return homePhoneNumber;
	}
	
	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
	public Long getGroupName(){
		return groupId;
	}
	
	public Set<Group> getGroups() {
		return groups;
	}
	public void setOffice(final Office office) {
		this.office = office;
	}

	public void setStatus(final Integer status) {
		this.status=status;
		
	}
	
   public char getTaxExemption() {
		return taxExemption;
	}

	public void setTaxExemption(final char taxExemption) {
		this.taxExemption = taxExemption;
	}
	
	public String getBillMode() {
		return billMode;
	}
	
	

	public void setBillMode(final String billMode) {
		this.billMode = billMode;
	}

	public void close(final AppUser currentUser, final CodeValue closureReason, final Date closureDate) {
	        this.closureReason = closureReason;
	        this.closureDate = closureDate;
	        this.closeddBy = currentUser;
	        this.status = ClientStatus.CLOSED.getValue();
	    }

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(final Long parentId) {
		this.parentId = parentId;
	}

	public String getPoid() {
		return poid;
	}

	public void setPoid(String poid) {
		this.poid = poid;
	}

	
	
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String obrmRequestInput() {
	
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_CREATE_SUBSCRIBER_inputFlist>");
		sb.append("<POID>0.0.0.1 /plan -1 0</POID>");
		sb.append("<PROGRAM_NAME>CRM|COBADMIN</PROGRAM_NAME>");
		sb.append("<LOGIN>"+this.accountNumber+"</LOGIN>");
		sb.append("<PASSWD_CLEAR>COBAPPSUB11</PASSWD_CLEAR>");
		sb.append("<FLAGS>0</FLAGS>");
		sb.append("<PARENT_FLAGS>0</PARENT_FLAGS>");
		sb.append("<NAMEINFO elem=\"1\">");
		sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME>");
		sb.append("<MIDDLE_NAME>"+this.middlename+"</MIDDLE_NAME>");
		sb.append("<FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
		sb.append("<SALUTATION>"+this.title+"</SALUTATION>");
		sb.append("<PHONES elem=\"5\">");
        sb.append("<PHONE>"+this.phone+"</PHONE>");
		sb.append("<TYPE>5</TYPE>");
		sb.append("</PHONES>");
		sb.append("<EMAIL_ADDR>"+this.email+"</EMAIL_ADDR>");
		sb.append("<COUNTRY>"+this.office.getOfficeAddress().getCountry()+"</COUNTRY>");
		sb.append("<ZIP>"+this.office.getOfficeAddress().getzip()+"</ZIP>");
		sb.append("<STATE>"+this.office.getOfficeAddress().getState()+"</STATE>");
		sb.append("<CITY>"+this.office.getOfficeAddress().getCity()+"</CITY>");
		sb.append("<ADDRESS>"+this.office.getOfficeAddress().getAddressName()+"</ADDRESS>");
		sb.append("<COB_FLD_AREA>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_AREA>");
		sb.append("<COB_FLD_LOCATION>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_LOCATION>");
		sb.append("<COB_FLD_STREET>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_STREET>");
		sb.append("<COB_FLD_BUILDING>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_BUILDING>");
		sb.append("<COB_FLD_LANDMARK>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_LANDMARK>");
		sb.append("</NAMEINFO>");
		sb.append("<NAMEINFO elem=\"2\">");
		sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME>");
		sb.append("<MIDDLE_NAME>"+this.middlename+"</MIDDLE_NAME>");
		sb.append("<FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
		sb.append("<SALUTATION>"+this.title+"</SALUTATION>");
		sb.append("<PHONES elem=\"5\">");
		sb.append("<PHONE>"+this.phone+"</PHONE>");
		sb.append("<TYPE>5</TYPE>");
		sb.append("</PHONES>");
		sb.append("<EMAIL_ADDR>"+this.email+"</EMAIL_ADDR>");
		sb.append("<COUNTRY>"+this.office.getOfficeAddress().getCountry()+"</COUNTRY>");
		sb.append("<ZIP>"+this.office.getOfficeAddress().getzip()+"</ZIP>");
		sb.append("<STATE>"+this.office.getOfficeAddress().getState()+"</STATE>");
		sb.append("<CITY>"+this.office.getOfficeAddress().getCity()+"</CITY>");
		sb.append("<ADDRESS>"+this.office.getOfficeAddress().getAddressName()+"</ADDRESS>");
		sb.append("<COB_FLD_AREA>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_AREA>");
		sb.append("<COB_FLD_LOCATION>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_LOCATION>");
		sb.append("<COB_FLD_STREET>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_STREET>");
		sb.append("<COB_FLD_BUILDING>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_BUILDING>");
		sb.append("<COB_FLD_LANDMARK>"+this.office.getOfficeAddress().getAddressName()+"</COB_FLD_LANDMARK>");
		sb.append("</NAMEINFO>");
		sb.append("<ACCTINFO elem=\"0\">");
		sb.append("<POID>0.0.0.1 /account -1 0</POID>");
		sb.append("<BUSINESS_TYPE>90000000</BUSINESS_TYPE>");
		sb.append("<CURRENCY>356</CURRENCY>");
		sb.append("<AAC_SOURCE></AAC_SOURCE>");
		sb.append("<ACCOUNT_NO>"+this.accountNumber+"</ACCOUNT_NO>");
		sb.append("<BAL_INFO elem=\"0\">");
		sb.append("</BAL_INFO>");
		sb.append("</ACCTINFO>");
		sb.append("<PROFILES elem=\"0\">");
		sb.append("<INHERITED_INFO>");
		sb.append("<CUSTOMER_CARE_INFO>");
		sb.append("<PARENT>0.0.0.1 /account 309903 10</PARENT>");
		sb.append("<COB_FLD_RMAIL>"+this.email+"</COB_FLD_RMAIL>");
		sb.append("<COB_FLD_RMN>"+this.phone+"</COB_FLD_RMN>");//Updated
		sb.append("<COB_FLD_CONTACT_PREF>1</COB_FLD_CONTACT_PREF>");// need to updated.
		sb.append("</CUSTOMER_CARE_INFO>");
		sb.append("</INHERITED_INFO>");
		sb.append("<PROFILE_OBJ>0.0.0.1 /profile/customer_care -1 0</PROFILE_OBJ>");
		sb.append("</PROFILES>");
		sb.append("</COB_OP_CUST_CREATE_SUBSCRIBER_inputFlist>");
		return sb.toString();			
			
			
		/*StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><MSO_OP_CUST_REGISTER_CUSTOMER_inputFlist>");
		sb.append(" <MSO_FLD_ROLES>SELF CARE</MSO_FLD_ROLES>");
		sb.append(" <ACCTINFO elem=\"0\">");
		sb.append(" <MSO_FLD_AREA>AP|AP-D07-C01|AP-D07-C01-A01</MSO_FLD_AREA>");
		sb.append("<MSO_FLD_CONTACT_PREF>0</MSO_FLD_CONTACT_PREF>");
		sb.append("<MSO_FLD_REGION>0</MSO_FLD_REGION>");
		sb.append(" <MSO_FLD_RMAIL>"+this.email+"</MSO_FLD_RMAIL>");
		sb.append(" <MSO_FLD_RMN>"+this.phone+"</MSO_FLD_RMN><AAC_SOURCE>OAP_BULK_ACT</AAC_SOURCE>");
		sb.append("<AAC_VENDOR>OAP</AAC_VENDOR><BAL_INFO elem=\"0\"/>");
		sb.append("<BUSINESS_TYPE>99001100</BUSINESS_TYPE><CURRENCY>356</CURRENCY>");
		sb.append("<POID>0.0.0.1 /account -1 0</POID> </ACCTINFO>");
		sb.append("<DELIVERY_PREFER>0</DELIVERY_PREFER> <FLAGS>0</FLAGS><LOGIN>"+this.accountNumber+"</LOGIN>");
		sb.append("<NAMEINFO elem=\"1\"><MSO_FLD_AREA_NAME>HighTechCity</MSO_FLD_AREA_NAME>");
		sb.append("<MSO_FLD_BUILDING_NAME>b01</MSO_FLD_BUILDING_NAME>");
		sb.append("<MSO_FLD_DISTRICT_NAME>Hyderabad</MSO_FLD_DISTRICT_NAME>");
		sb.append("<MSO_FLD_LANDMARK>st</MSO_FLD_LANDMARK><MSO_FLD_LOCATION_NAME>st</MSO_FLD_LOCATION_NAME>");
		sb.append("<MSO_FLD_STREET_NAME>st</MSO_FLD_STREET_NAME><ADDRESS>12</ADDRESS>");
		sb.append("<CITY>Madhapur</CITY><COUNTRY>INDIA</COUNTRY><EMAIL_ADDR>"+this.email+"</EMAIL_ADDR>");
		sb.append("<FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
		sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME>");
		sb.append("<MIDDLE_NAME>csr_C1-48</MIDDLE_NAME>");
		sb.append("<PHONES elem=\"5\">");
		sb.append("<PHONE>"+this.phone+"</PHONE> <TYPE>5</TYPE>");
		sb.append("</PHONES><SALUTATION>"+this.title+".</SALUTATION>");
		sb.append("<STATE>ANDHRA PRADESH</STATE><ZIP>500018</ZIP></NAMEINFO><NAMEINFO elem=\"2\">");
		sb.append("<MSO_FLD_AREA_NAME>HighTechCity</MSO_FLD_AREA_NAME>");
		sb.append("<MSO_FLD_BUILDING_NAME>b01</MSO_FLD_BUILDING_NAME>");
		sb.append("<MSO_FLD_DISTRICT_NAME>Hyderabad</MSO_FLD_DISTRICT_NAME>");
		sb.append("<MSO_FLD_LANDMARK>st</MSO_FLD_LANDMARK>");
		sb.append("<MSO_FLD_LOCATION_NAME>st</MSO_FLD_LOCATION_NAME>");
		sb.append("<MSO_FLD_STREET_NAME>st</MSO_FLD_STREET_NAME>");
		sb.append("<ADDRESS>12</ADDRESS><CITY>Madhapur</CITY><COUNTRY>INDIA</COUNTRY>");
		sb.append("<EMAIL_ADDR>"+this.email+"</EMAIL_ADDR><FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
		sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME><MIDDLE_NAME>csr_C1-48</MIDDLE_NAME>");
		sb.append("<PHONES elem=\"5\"><PHONE>6</PHONE><TYPE>5</TYPE></PHONES><SALUTATION>"+this.title+".</SALUTATION>");
		sb.append("<STATE>ANDHRA PRADESH</STATE><ZIP>500018</ZIP></NAMEINFO><PARENT_FLAGS>0</PARENT_FLAGS>");
		sb.append("<PASSWD_CLEAR>XXXX</PASSWD_CLEAR><POID>0.0.0.1 /plan -1 0</POID>");
		sb.append("<PROFILES elem=\"0\"><INHERITED_INFO><CUSTOMER_CARE_INFO>");
		sb.append("<MSO_FLD_SALESMAN_OBJ/>");
		sb.append("<MSO_FLD_SALES_CLOSE_TYPE>0</MSO_FLD_SALES_CLOSE_TYPE>");
		sb.append("<PARENT>0.0.0.1 /account 452699 7</PARENT></CUSTOMER_CARE_INFO></INHERITED_INFO>");
		sb.append("<PROFILE_OBJ>0.0.0.1 /profile/customer_care -1 0</PROFILE_OBJ>");
		sb.append("</PROFILES><PROGRAM_NAME>BULK|OAP|testcsrone</PROGRAM_NAME>");
		sb.append("<TYPE_OF_SERVICE>1</TYPE_OF_SERVICE><USERID>0.0.0.1 /account 452699 0</USERID>");
		sb.append("</MSO_OP_CUST_REGISTER_CUSTOMER_inputFlist>");
		return sb.toString();*/
		
	}
	
public String celcomRequestInput(String userName, Address primaryAddress, Address billingAddress,String parent) {
		

		/*
		 Business type
		 90 -- [Account Type] Catv Customer fixed one
		 
		 XX -- [Subscriber Type] mapp to Category type mention the enum / switch 
		       
		       { Switch case category
		       		Case 20 then   03 // Normal
		       		Case 21 then   09 //Staff
		       		Case 22 then   02  //VIP  }
		       
		 X --  [Account Model] 1 - Residential or 2-Commertical  our entitytype ind = 1 orp = 2
			if Indorp = true then 1 else 2 ;

		 X --  [Parent Type]  refere m_client (parent_id)
		 	Null -- 0
		 	0 --  1
		 	>0 then 2 and take the account_poid of that client and pass it to <PARENT> String 
		 
		 XX -- [Service type or Reserved ] hardcode for now with 
		 01 as catv
		 90022201 
		 */
		
		String categoryBusinessType=this.categoryType.toString();
		
		switch (categoryBusinessType) {
		case "20":
			categoryBusinessType="03";
			break;

		case "21":
			categoryBusinessType="09";
			break;
		
		case "22":
			categoryBusinessType="02";
			break;

		default:
			categoryBusinessType="00";
			break;
		}

        String entryType1=null;
		if(this.entryType.equalsIgnoreCase("IND")){
			entryType1="1";
		}else {
			entryType1="2";
		}
		String parentBusinessType=null;
		String parentFlags="0";
		String parentPoId="-1";
		if(this.parentId!=null){
			if(this.parentId==0){
				parentBusinessType="0";
				parentFlags="0";
			}else{
				parentBusinessType="2";
				parentFlags="2";
				parentPoId=parent;
			}
		}else{
			parentBusinessType="0";
			parentFlags="0";
		}
		
		int contactPreference=0;
		if(this.billMode.equalsIgnoreCase("Email")){
    		contactPreference=1;
    	}else if (this.billMode.equalsIgnoreCase("Both")) {
			contactPreference=2;
		}    	
    	

		
		String businessType="90"+categoryBusinessType+entryType1+parentBusinessType+"00";
		//String businessType="90031000";
		String lcoPoId=this.office.getPoId();
		String idKey=null;
		if(this.idkey!=null){
			idKey = this.idkey.getLabel();
		}else{
			idKey = "Aadhar";
			if(this.idValue!=null){
				this.idValue = "default";
			}
		}
		
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_CREATE_SUBSCRIBER_inputFlist>");
		sb.append("<POID>0.0.0.1 /plan -1 0</POID>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<LOGIN>"+this.accountNumber+"</LOGIN>");
		sb.append("<PASSWD_CLEAR>abc@123</PASSWD_CLEAR>");
		sb.append("<FLAGS>0</FLAGS>");
		sb.append("<PARENT_FLAGS>"+parentFlags+"</PARENT_FLAGS>");
		sb.append("<NAMEINFO elem=\"1\">");
		sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME>");
		sb.append("<MIDDLE_NAME>"+this.middlename+"</MIDDLE_NAME>");
		sb.append("<FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
		sb.append("<SALUTATION>"+this.title+"</SALUTATION>");
		sb.append("<PHONES elem=\"5\">");
        sb.append("<PHONE>"+this.phone+"</PHONE>");
		sb.append("<TYPE>5</TYPE>");
		sb.append("</PHONES>");
		sb.append("<EMAIL_ADDR>"+this.email+"</EMAIL_ADDR>");
		sb.append("<COUNTRY>"+primaryAddress.getCountry()+"</COUNTRY>");
		sb.append("<ZIP>"+primaryAddress.getZip()+"</ZIP>");
		sb.append("<STATE>"+primaryAddress.getState()+"</STATE>");
		sb.append("<CITY>"+primaryAddress.getCity()+"</CITY>");
		sb.append("<ADDRESS>"+primaryAddress.getAddressNo()+"</ADDRESS>");
		sb.append("<COB_FLD_AREA>"+primaryAddress.getAddressNo()+"</COB_FLD_AREA>");
		sb.append("<COB_FLD_LOCATION>"+primaryAddress.getAddressNo()+"</COB_FLD_LOCATION>");
		sb.append("<COB_FLD_STREET>"+primaryAddress.getStreet()+"</COB_FLD_STREET>");
		sb.append("<COB_FLD_BUILDING>"+primaryAddress.getAddressNo()+"</COB_FLD_BUILDING>");
		sb.append("<COB_FLD_DISTRICT>"+primaryAddress.getDistrict()+"</COB_FLD_DISTRICT>");
		sb.append("<COB_FLD_LANDMARK>"+primaryAddress.getAddressNo()+"</COB_FLD_LANDMARK>");
		sb.append("</NAMEINFO>");
		sb.append("<NAMEINFO elem=\"2\">");
		sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME>");
		sb.append("<MIDDLE_NAME>"+this.middlename+"</MIDDLE_NAME>");
		sb.append("<FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
		sb.append("<SALUTATION>"+this.title+"</SALUTATION>");
		sb.append("<PHONES elem=\"5\">");
		sb.append("<PHONE>"+this.phone+"</PHONE>");
		sb.append("<TYPE>5</TYPE>");
		sb.append("</PHONES>");
		sb.append("<EMAIL_ADDR>"+this.email+"</EMAIL_ADDR>");
		sb.append("<COUNTRY>"+billingAddress.getCountry()+"</COUNTRY>");
		sb.append("<ZIP>"+billingAddress.getZip()+"</ZIP>");
		sb.append("<STATE>"+billingAddress.getState()+"</STATE>");
		sb.append("<CITY>"+billingAddress.getCity()+"</CITY>");
		sb.append("<ADDRESS>"+billingAddress.getAddressNo()+"</ADDRESS>");
		sb.append("<COB_FLD_AREA>"+billingAddress.getAddressNo()+"</COB_FLD_AREA>");
		sb.append("<COB_FLD_LOCATION>"+billingAddress.getAddressNo()+"</COB_FLD_LOCATION>");
		sb.append("<COB_FLD_STREET>"+billingAddress.getStreet()+"</COB_FLD_STREET>");
		sb.append("<COB_FLD_BUILDING>"+billingAddress.getAddressNo()+"</COB_FLD_BUILDING>");
		sb.append("<COB_FLD_DISTRICT>"+billingAddress.getDistrict()+"</COB_FLD_DISTRICT>");
		sb.append("<COB_FLD_LANDMARK>"+billingAddress.getAddressNo()+"</COB_FLD_LANDMARK>");
		sb.append("</NAMEINFO>");
		sb.append("<ACCTINFO elem=\"0\">");
		sb.append("<POID>0.0.0.1 /account -1 0</POID>");
		sb.append("<BUSINESS_TYPE>"+businessType+"</BUSINESS_TYPE>");
		sb.append("<CURRENCY>356</CURRENCY>");
		sb.append("<AAC_SOURCE></AAC_SOURCE>");
		sb.append("<ACCOUNT_NO>"+this.accountNumber+"</ACCOUNT_NO>");
		sb.append("<BAL_INFO elem=\"0\">");
		sb.append("</BAL_INFO>");
		sb.append("</ACCTINFO>");
		
		if(this.parentId!=null){
			if(this.parentId>0){
				sb.append("<GROUP_INFO>");
				sb.append("<PARENT>0.0.0.1 /account "+parentPoId+"</PARENT>");
				sb.append("</GROUP_INFO>");
				
			}
		}
		sb.append("<PROFILES elem=\"0\">");
		sb.append("<INHERITED_INFO>");
		sb.append("<CUSTOMER_CARE_INFO>");
		sb.append("<PARENT>0.0.0.1 /account "+lcoPoId+"</PARENT>");
		sb.append("<COB_FLD_RMAIL>"+this.email+"</COB_FLD_RMAIL>");
		sb.append("<COB_FLD_RMN>"+this.phone+"</COB_FLD_RMN>");
		sb.append("<COB_FLD_IDENTITY_TYPE>"+idKey+"</COB_FLD_IDENTITY_TYPE>");
        sb.append("<COB_FLD_IDENTITY_VALUE>"+this.idValue+"</COB_FLD_IDENTITY_VALUE>");
		sb.append("<COB_FLD_CONTACT_PREF>"+contactPreference+"</COB_FLD_CONTACT_PREF>");
		sb.append("</CUSTOMER_CARE_INFO>");
		sb.append("</INHERITED_INFO>");
		sb.append("<PROFILE_OBJ>0.0.0.1 /profile/customer_care -1 0</PROFILE_OBJ>");
		sb.append("</PROFILES>");
		sb.append("</COB_OP_CUST_CREATE_SUBSCRIBER_inputFlist>");
		return sb.toString();			
			
			
		/*StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><MSO_OP_CUST_REGISTER_CUSTOMER_inputFlist>");
		sb.append(" <MSO_FLD_ROLES>SELF CARE</MSO_FLD_ROLES>");
		sb.append(" <ACCTINFO elem=\"0\">");
		sb.append(" <MSO_FLD_AREA>AP|AP-D07-C01|AP-D07-C01-A01</MSO_FLD_AREA>");
		sb.append("<MSO_FLD_CONTACT_PREF>0</MSO_FLD_CONTACT_PREF>");
		sb.append("<MSO_FLD_REGION>0</MSO_FLD_REGION>");
		sb.append(" <MSO_FLD_RMAIL>"+this.email+"</MSO_FLD_RMAIL>");
		sb.append(" <MSO_FLD_RMN>"+this.phone+"</MSO_FLD_RMN><AAC_SOURCE>OAP_BULK_ACT</AAC_SOURCE>");
		sb.append("<AAC_VENDOR>OAP</AAC_VENDOR><BAL_INFO elem=\"0\"/>");
		sb.append("<BUSINESS_TYPE>99001100</BUSINESS_TYPE><CURRENCY>356</CURRENCY>");
		sb.append("<POID>0.0.0.1 /account -1 0</POID> </ACCTINFO>");
		sb.append("<DELIVERY_PREFER>0</DELIVERY_PREFER> <FLAGS>0</FLAGS><LOGIN>"+this.accountNumber+"</LOGIN>");
		sb.append("<NAMEINFO elem=\"1\"><MSO_FLD_AREA_NAME>HighTechCity</MSO_FLD_AREA_NAME>");
		sb.append("<MSO_FLD_BUILDING_NAME>b01</MSO_FLD_BUILDING_NAME>");
		sb.append("<MSO_FLD_DISTRICT_NAME>Hyderabad</MSO_FLD_DISTRICT_NAME>");
		sb.append("<MSO_FLD_LANDMARK>st</MSO_FLD_LANDMARK><MSO_FLD_LOCATION_NAME>st</MSO_FLD_LOCATION_NAME>");
		sb.append("<MSO_FLD_STREET_NAME>st</MSO_FLD_STREET_NAME><ADDRESS>12</ADDRESS>");
		sb.append("<CITY>Madhapur</CITY><COUNTRY>INDIA</COUNTRY><EMAIL_ADDR>"+this.email+"</EMAIL_ADDR>");
		sb.append("<FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
		sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME>");
		sb.append("<MIDDLE_NAME>csr_C1-48</MIDDLE_NAME>");
		sb.append("<PHONES elem=\"5\">");
		sb.append("<PHONE>"+this.phone+"</PHONE> <TYPE>5</TYPE>");
		sb.append("</PHONES><SALUTATION>"+this.title+".</SALUTATION>");
		sb.append("<STATE>ANDHRA PRADESH</STATE><ZIP>500018</ZIP></NAMEINFO><NAMEINFO elem=\"2\">");
		sb.append("<MSO_FLD_AREA_NAME>HighTechCity</MSO_FLD_AREA_NAME>");
		sb.append("<MSO_FLD_BUILDING_NAME>b01</MSO_FLD_BUILDING_NAME>");
		sb.append("<MSO_FLD_DISTRICT_NAME>Hyderabad</MSO_FLD_DISTRICT_NAME>");
		sb.append("<MSO_FLD_LANDMARK>st</MSO_FLD_LANDMARK>");
		sb.append("<MSO_FLD_LOCATION_NAME>st</MSO_FLD_LOCATION_NAME>");
		sb.append("<MSO_FLD_STREET_NAME>st</MSO_FLD_STREET_NAME>");
		sb.append("<ADDRESS>12</ADDRESS><CITY>Madhapur</CITY><COUNTRY>INDIA</COUNTRY>");
		sb.append("<EMAIL_ADDR>"+this.email+"</EMAIL_ADDR><FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
		sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME><MIDDLE_NAME>csr_C1-48</MIDDLE_NAME>");
		sb.append("<PHONES elem=\"5\"><PHONE>6</PHONE><TYPE>5</TYPE></PHONES><SALUTATION>"+this.title+".</SALUTATION>");
		sb.append("<STATE>ANDHRA PRADESH</STATE><ZIP>500018</ZIP></NAMEINFO><PARENT_FLAGS>0</PARENT_FLAGS>");
		sb.append("<PASSWD_CLEAR>XXXX</PASSWD_CLEAR><POID>0.0.0.1 /plan -1 0</POID>");
		sb.append("<PROFILES elem=\"0\"><INHERITED_INFO><CUSTOMER_CARE_INFO>");
		sb.append("<MSO_FLD_SALESMAN_OBJ/>");
		sb.append("<MSO_FLD_SALES_CLOSE_TYPE>0</MSO_FLD_SALES_CLOSE_TYPE>");
		sb.append("<PARENT>0.0.0.1 /account 452699 7</PARENT></CUSTOMER_CARE_INFO></INHERITED_INFO>");
		sb.append("<PROFILE_OBJ>0.0.0.1 /profile/customer_care -1 0</PROFILE_OBJ>");
		sb.append("</PROFILES><PROGRAM_NAME>BULK|OAP|testcsrone</PROGRAM_NAME>");
		sb.append("<TYPE_OF_SERVICE>1</TYPE_OF_SERVICE><USERID>0.0.0.1 /account 452699 0</USERID>");
		sb.append("</MSO_OP_CUST_REGISTER_CUSTOMER_inputFlist>");
		return sb.toString();*/
		
	}

    public  String celcomRequestInputForCLIENTSCelcom(String userName, Address address, 
    		AddressData primaryAddressData, AddressData billingAddressData, String lcoPoId) {
    	
    	int contactPreference=0;
    	if(this.billMode.equals("Email")){
    		contactPreference=1;
    	}else if (this.billMode.equals("both")) {
			contactPreference=2;
		}    	
    	StringBuilder sb = new StringBuilder("<COB_OP_CUST_UPDATE_CUSTOMER_inputFlist>");
    	/*sb.append("<POID>0.0.0.1 /plan -1 0</POID>");*/
    	sb.append("<POID>0.0.0.1 /account "+this.poid+" 0</POID>");
    	sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
    	sb.append("<NAMEINFO elem=\"1\">");
    	sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME>");
    	sb.append("<MIDDLE_NAME>"+this.middlename+"</MIDDLE_NAME>");
    	sb.append("<FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
    	sb.append("<SALUTATION>"+this.title+"</SALUTATION>");
    	sb.append("<PHONES elem=\"5\">");
    	sb.append("<PHONE>"+this.phone+"</PHONE>");
    	sb.append("<TYPE>5</TYPE>");
    	sb.append("</PHONES>");
    	sb.append("<EMAIL_ADDR>"+this.email+"</EMAIL_ADDR>");
    	if(address.getAddressKey().equalsIgnoreCase("Primary")) {
    		sb.append("<COUNTRY>"+address.getCountry()+"</COUNTRY>");
        	sb.append("<ZIP>"+address.getZip()+"</ZIP>");
    		sb.append("<STATE>"+address.getState()+"</STATE>");
    		sb.append("<CITY>"+address.getCity()+"</CITY>");
    		sb.append("<ADDRESS>"+address.getAddressNo()+"</ADDRESS>");
    		sb.append("<COB_FLD_AREA></COB_FLD_AREA>");
    		sb.append("<COB_FLD_LOCATION></COB_FLD_LOCATION>");
    		sb.append("<COB_FLD_STREET></COB_FLD_STREET>");
    		sb.append("<COB_FLD_BUILDING></COB_FLD_BUILDING>");
    		sb.append("<COB_FLD_LANDMARK></COB_FLD_LANDMARK>");
    		sb.append("<COB_FLD_DISTRICT>"+address.getDistrict()+"</COB_FLD_DISTRICT>");
    		
    	}else {
    		sb.append("<COUNTRY>"+primaryAddressData.getCountry()+"</COUNTRY>");
        	sb.append("<ZIP>"+primaryAddressData.getZip()+"</ZIP>");
    		sb.append("<STATE>"+primaryAddressData.getState()+"</STATE>");
    		sb.append("<CITY>"+primaryAddressData.getCity()+"</CITY>");
    		sb.append("<ADDRESS>"+primaryAddressData.getAddressNo()+"</ADDRESS>");
    		sb.append("<COB_FLD_AREA></COB_FLD_AREA>");
    		sb.append("<COB_FLD_LOCATION></COB_FLD_LOCATION>");
    		sb.append("<COB_FLD_STREET></COB_FLD_STREET>");
    		sb.append("<COB_FLD_BUILDING></COB_FLD_BUILDING>");
    		sb.append("<COB_FLD_LANDMARK></COB_FLD_LANDMARK>");
    		sb.append("<COB_FLD_DISTRICT>"+primaryAddressData.getDistrict()+"</COB_FLD_DISTRICT>");
    		
    	}
    	sb.append("</NAMEINFO>");
		sb.append("<NAMEINFO elem=\"2\">");
		sb.append("<LAST_NAME>"+this.lastname+"</LAST_NAME>");
		sb.append("<MIDDLE_NAME>"+this.middlename+"</MIDDLE_NAME>");
		sb.append("<FIRST_NAME>"+this.firstname+"</FIRST_NAME>");
		sb.append("<SALUTATION>"+this.title+"</SALUTATION>");
		sb.append("<PHONES elem=\"5\">");
		sb.append("<PHONE>"+this.phone+"</PHONE>");
		sb.append("<TYPE>5</TYPE>");
		sb.append("</PHONES>");
		sb.append("<EMAIL_ADDR>"+this.email+"</EMAIL_ADDR>");
		
		if(address.getAddressKey().equalsIgnoreCase("Billing")) {
			
			sb.append("<COUNTRY>"+address.getCountry()+"</COUNTRY>");
        	sb.append("<ZIP>"+address.getZip()+"</ZIP>");
    		sb.append("<STATE>"+address.getState()+"</STATE>");
    		sb.append("<CITY>"+address.getCity()+"</CITY>");
    		sb.append("<ADDRESS>"+address.getAddressNo()+"</ADDRESS>");
    		sb.append("<COB_FLD_AREA></COB_FLD_AREA>");
    		sb.append("<COB_FLD_LOCATION></COB_FLD_LOCATION>");
    		sb.append("<COB_FLD_STREET></COB_FLD_STREET>");
    		sb.append("<COB_FLD_BUILDING></COB_FLD_BUILDING>");
    		sb.append("<COB_FLD_LANDMARK></COB_FLD_LANDMARK>");
    		sb.append("<COB_FLD_DISTRICT>"+address.getDistrict()+"</COB_FLD_DISTRICT>");
    	}else {
			sb.append("<COUNTRY>"+billingAddressData.getCountry()+"</COUNTRY>");
			sb.append("<ZIP>"+billingAddressData.getZip()+"</ZIP>");
			sb.append("<STATE>"+billingAddressData.getState()+"</STATE>");
			sb.append("<CITY>"+billingAddressData.getCity()+"</CITY>");
			sb.append("<ADDRESS>"+billingAddressData.getAddressNo()+"</ADDRESS>");
			sb.append("<COB_FLD_AREA></COB_FLD_AREA>");
			sb.append("<COB_FLD_LOCATION></COB_FLD_LOCATION>");
			sb.append("<COB_FLD_STREET></COB_FLD_STREET>");
			sb.append("<COB_FLD_BUILDING></COB_FLD_BUILDING>");
			sb.append("<COB_FLD_LANDMARK></COB_FLD_LANDMARK>");
			sb.append("<COB_FLD_DISTRICT>"+billingAddressData.getDistrict()+"</COB_FLD_DISTRICT>");
		}
		sb.append("</NAMEINFO>");
		sb.append("<PROFILES elem=\"0\">");
		sb.append("<INHERITED_INFO>");
		sb.append("<CUSTOMER_CARE_INFO>");
		sb.append("<PARENT>0.0.0.1 /account "+lcoPoId+" 0</PARENT>");
		/*sb.append("<PARENT>0.0.0.1 /account 0 </PARENT>");*/
		sb.append("<COB_FLD_RMAIL>"+this.email+"</COB_FLD_RMAIL>");
		sb.append("<COB_FLD_RMN>1111111111</COB_FLD_RMN>");
		sb.append("<COB_FLD_CONTACT_PREF>"+contactPreference+"</COB_FLD_CONTACT_PREF>");
		sb.append("</CUSTOMER_CARE_INFO>");
		sb.append("</INHERITED_INFO>");
		/*sb.append("<PROFILE_OBJ>0.0.0.1 /profile/customer_care -1 0</PROFILE_OBJ>");*/
		sb.append("</PROFILES>");
		sb.append("</COB_OP_CUST_UPDATE_CUSTOMER_inputFlist>");
		return sb.toString();	
		
	
   }

    
    public  String celcomRequestInputForLCOMovement(String userName, String lcoPoId) {
    	
    	StringBuilder sb = new StringBuilder("<COB_OP_CUST_LCO_MOVEMENT_inputFlist>");
    	sb.append("<POID>0.0.0.1 /account "+this.poid+" 0</POID>");
    	sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.poid+" 0</ACCOUNT_OBJ>");
    	sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
    	sb.append("<PROFILES elem=\"0\">");
    	sb.append("<INHERITED_INFO>");
    	sb.append("<CUSTOMER_CARE_INFO>");
    	sb.append("<PARENT>0.0.0.1 /account "+lcoPoId+" 0</PARENT>");
    	sb.append("<COB_FLD_LCO_OBJ>0.0.0.1 /account "+lcoPoId+" 0</COB_FLD_LCO_OBJ>");
    	sb.append("</CUSTOMER_CARE_INFO>");
    	sb.append("</INHERITED_INFO>");
    	sb.append("</PROFILES>");
    	sb.append("</COB_OP_CUST_LCO_MOVEMENT_inputFlist>");
    	
    	return sb.toString();	
   }

	

}