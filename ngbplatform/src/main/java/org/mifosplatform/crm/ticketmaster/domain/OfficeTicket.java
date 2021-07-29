package org.mifosplatform.crm.ticketmaster.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.crm.ticketmaster.command.TicketMasterCommand;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.service.DateUtils;

@Entity
@Table(name = "b_office_ticket")
public class OfficeTicket {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "office_id", length = 65536)
	private Long officeId;

	@Column(name = "priority")
	private String priority;

	@Column(name = "problem_code")
	private Integer problemCode;
	
	@Column(name = "description")
	private String description;

	@Column(name = "ticket_date")
	private Date ticketDate;

	@Column(name = "status")
	private String status;
	
	@Column(name = "status_code")
	private Long statusCode;

	@Column(name = "resolution_description")
	private String resolutionDescription;
	
	@Column(name = "sub_category")
	private String subCategory;
	
	@Column(name = "assigned_to")
	private Long assignedTo;

	@Column(name = "source")
	private String source;
	
	@Column(name = "closed_date")
	private Date closedDate;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "createdby_id") 
	private Long createdbyId;

	@Column(name="source_of_ticket", length=50 )
	private String sourceOfTicket;
	
	@Column(name = "due_date")
	private Date dueDate;
	
	@Column(name = "lastmodifiedby_id")
	private Long lastModifyId;
	
	@Column(name = "lastmodified_date")
	private Date lastModifydate;
	

	@Column(name = "ticket_no")
	private String ticketno;

	@Column(name = "title")
	private String title;
	

	@Column(name = "is_escalated")
	private char isescalated ;
	
	@Column(name = "type")
	private String type;

	public OfficeTicket() {
		
	}
	
	public static OfficeTicket fromJson(final JsonCommand command) throws ParseException {
	
		final String priority = command.stringValueOfParameterNamed("priority");
		final Integer problemCode = command.integerValueOfParameterNamed("problemCode");
		final String description = command.stringValueOfParameterNamed("description");
		final Long assignedTo = command.longValueOfParameterNamed("assignedTo");
		
		final LocalDate startDate = command.localDateValueOfParameterNamed("ticketDate");
		final String startDateString = startDate.toString() + command.stringValueOfParameterNamed("ticketTime");
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final Date ticketDate = df.parse(startDateString);
	
		final String statusCode = command.stringValueOfParameterNamed("problemDescription");
		final Long clientId = command.getClientId();
		final String sourceOfTicket = command.stringValueOfParameterNamed("sourceOfTicket");
		final String dueDate = command.stringValueOfParameterNamed("dueTime");
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final String type = command.stringValueOfParameterNamed("type");

		Date dueTime;
		if(dueDate.equalsIgnoreCase("")){
				dueTime=null;
		}else{
			dueTime = dateFormat.parse(dueDate);
		}
		
		final String subCategory = command.stringValueOfParameterNamed("subCategory");
		LocalDate localDate = new LocalDate(new Date());
		String ticketno = localDate.getYear()+String.valueOf(System.currentTimeMillis() / 1000L);
        
		final String title = command.stringValueOfParameterNamed("title");
		
		
		return new OfficeTicket(clientId, priority,ticketDate, problemCode,description,statusCode, null, 
					assignedTo, null, null, null, sourceOfTicket, dueTime,subCategory,ticketno,title,type);
	}

	public OfficeTicket(final Long statusCode, final Long assignedTo) {
		
		this.officeId = null;
		this.priority = null;
		this.ticketDate = null;
		this.problemCode = null;
		this.description = null;
		this.status = null;
		this.statusCode = statusCode;
		this.source = null;
		this.resolutionDescription = null;
		this.assignedTo = assignedTo;	
		this.createdDate = null;
		this.createdbyId = null;
		this.subCategory=null;
	}

	public OfficeTicket(final Long officeId, final String priority, final Date ticketDate, final Integer problemCode,
			final String description, final String status, final String resolutionDescription, 
			final Long assignedTo, final Long statusCode, final Date createdDate, final Integer createdbyId,
			final String sourceOfTicket, final Date dueTime, final String subCategory,final String ticketno,final String title,String type) {
		
		this.officeId = officeId;
		this.priority = priority;
		this.ticketDate = ticketDate;
		this.problemCode = problemCode;
		this.description = description;
		this.status = "OPEN";
		this.statusCode = statusCode;
		this.source = "Manual";
		this.resolutionDescription = resolutionDescription;
		this.assignedTo = assignedTo;	
		//this.createdDate = DateUtils.getDateOfTenant();
		this.createdDate = new Date();
		this.lastModifydate = new Date();
		this.createdbyId = null;
		this.sourceOfTicket = sourceOfTicket;
		this.dueDate = dueTime;
		this.subCategory=subCategory;
		this.ticketno=ticketno;
		this.title=title;
		this.isescalated='0';
		this.type = type;
		
	}

	public String getSource() {
		return source;
	}

	public Long getId() {
		return id;
	}

	public Long getOfficeId() {
		return this.officeId;
	}

	public String getPriority() {
		return priority;
	}

	public Integer getProblemCode() {
		return problemCode;
	}

	public String getDescription() {
		return description;
	}

	public Date getTicketDate() {
		return ticketDate;
	}

	public String getStatus() {
		return status;
	}
	
	public Long getStatusCode() {
		return statusCode;
	}
	
	public String getSubCategory()
	{
		return subCategory;
	}
	public String getResolutionDescription() {
		return resolutionDescription;
	}

	public Long getAssignedTo() {
		return assignedTo;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void updateTicket(final TicketMasterCommand command) {
		this.status =command.getStatus()!=null?command.getStatus():"OPEN";
		this.statusCode=command.getStatusCode();
		this.assignedTo = command.getAssignedTo();
		this.priority = command.getPriority();
		this.problemCode = command.getProblemCodeId();
		this.subCategory=command.getSubCategory();
		}

	public Map<String, Object> update(JsonCommand command, long userId) {
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String statusNamedParamName = "status";
		final String statusCodeNamedParamName = "statusCode";
		final String assignedToNamedParamName = "assignedTo";
		final String priorityNamedParamName = "priority";
		final String problemCodeNamedParamName = "problemCode";
		final String subCategoryNamedParamName = "subCategory";
		final String descriptionNamedParamName = "description";
		
		
		if(command.isChangeInStringParameterNamed(statusNamedParamName, this.status)){
			final String newValue = command.stringValueOfParameterNamed(statusNamedParamName);
			actualChanges.put(statusNamedParamName, newValue);
			this.status = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		if(command.isChangeInLongParameterNamed(statusCodeNamedParamName, this.statusCode)){
			final Long newValue = command.longValueOfParameterNamed(statusCodeNamedParamName);
			actualChanges.put(statusCodeNamedParamName, newValue);
			this.statusCode = newValue;
		}
		
		if(command.isChangeInLongParameterNamed(assignedToNamedParamName, this.assignedTo)){
			final Long newValue = command.longValueOfParameterNamed(assignedToNamedParamName);
			actualChanges.put(assignedToNamedParamName, newValue);
			this.assignedTo = newValue;
		}
		
		
		if(command.isChangeInStringParameterNamed(priorityNamedParamName, this.priority)){
			final String newValue = command.stringValueOfParameterNamed(priorityNamedParamName);
			actualChanges.put(priorityNamedParamName, newValue);
			this.priority = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		
		if(command.isChangeInIntegerParameterNamed(problemCodeNamedParamName, this.problemCode)){
			final Integer newValue = command.integerValueOfParameterNamed(problemCodeNamedParamName);
			actualChanges.put(problemCodeNamedParamName, newValue);
			this.problemCode = newValue;
		}
		
		if(command.isChangeInStringParameterNamed(subCategoryNamedParamName, this.subCategory)){
			final String newValue = command.stringValueOfParameterNamed(subCategoryNamedParamName);
			actualChanges.put(subCategoryNamedParamName, newValue);
			this.subCategory = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		if(command.isChangeInStringParameterNamed(descriptionNamedParamName, this.description)){
			final String newValue = command.stringValueOfParameterNamed(descriptionNamedParamName);
			actualChanges.put(descriptionNamedParamName, newValue);
			this.description = StringUtils.defaultIfEmpty(newValue,null);
		}
		this.lastModifyId = userId;
		this.lastModifydate = new Date();
		return actualChanges;
	
		
		}

	
	public void closeTicket(final JsonCommand command, final Long userId) {
		
		this.status = "CLOSED";
	    this.statusCode = Long.parseLong(command.stringValueOfParameterNamed("status"));
		this.resolutionDescription = command.stringValueOfParameterNamed("resolutionDescription");
		this.closedDate = DateUtils.getDateOfTenant();
		this.lastModifyId = userId;
		this.lastModifydate = DateUtils.getDateOfTenant();
		
	}
	
	public Date getClosedDate() {
		return closedDate;
	}

	/**
	 * @return the createdbyId
	 */
	public Long getCreatedbyId() {
		return createdbyId;
	}

	/**
	 * @param createdbyId the createdbyId to set
	 */
	public void setCreatedbyId(final Long createdbyId) {
		this.createdbyId = createdbyId;
	}

	public void setAssignedTo(Long assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	public char getIsescalated() {
		return isescalated;
	}

	public void setIsescalated(char isescalated) {
		this.isescalated = isescalated;
	}

}