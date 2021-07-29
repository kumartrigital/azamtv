package org.mifosplatform.crm.ticketmaster.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.service.DateUtils;

@Entity
@Table(name = "b_office_ticket_detail")
public class OfficeTicketDetail {
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "ticket_id", length = 65536)
	private Long ticketId;

	@Column(name = "comments")
	private String comments;

	@Column(name = "attachments")
	private String attachments;
	
	@Column(name = "assigned_to")
	private Long assignedTo;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "createdby_id")
	private Long createdbyId;

	
	 public OfficeTicketDetail() {
		// TODO Auto-generated constructor stub
	}
	
	public static OfficeTicketDetail fromJson(final JsonCommand command) throws ParseException {
	
		final Long assignedTo = command.longValueOfParameterNamed("assignedTo");
		final String description  = command.stringValueOfParameterNamed("description");
	
		final LocalDate startDate = command.localDateValueOfParameterNamed("ticketDate");
		final String startDateString = startDate.toString() + command.stringValueOfParameterNamed("ticketTime");
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final Date ticketDate = df.parse(startDateString);
	
		return new OfficeTicketDetail(assignedTo, description, ticketDate);
	}

	public OfficeTicketDetail(final Long assignedTo, final String description, final Date ticketDate) {
		this.assignedTo = assignedTo;
		this.comments = description;
		this.createdDate = ticketDate;
	}

	public OfficeTicketDetail(final Long ticketId, final String comments, final String fileLocation,
			final Long assignedTo, final Long createdbyId) {
    
		this.ticketId = ticketId;
        this.comments = comments;
        this.attachments = fileLocation;
        this.assignedTo = assignedTo;
        this.createdDate = DateUtils.getLocalDateOfTenant().toDate();
        this.createdbyId = createdbyId;	
	}

	public Long getId() {
		return id;
	}
	
	public void setTicketId(final Long ticketId) {
		this.ticketId = ticketId;
	}

	public Long getTicketId() {
		return ticketId;
	}

	public String getComments() {
		return comments;
	}

	public String getAttachments() {
		return attachments;
	}

	public Long getAssignedTo() {
		return assignedTo;
	}

	public void setCreatedbyId(final Long createdbyId) {
		this.createdbyId = createdbyId;
	}
	
	public Long getCreatedbyId() {
		return createdbyId;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

}
