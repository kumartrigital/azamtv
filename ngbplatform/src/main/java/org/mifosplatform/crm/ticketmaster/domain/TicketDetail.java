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
@Table(name = "b_ticket_details")
public class TicketDetail {
	
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

	@Column(name = "team_id")
	private String teamCode;
    
	@Column(name = "team_user_id")
	private Long teamUserId;

	
	
	public TicketDetail() {
		
	}
	
	public static TicketDetail fromJson(final JsonCommand command) throws ParseException {
	
		final Long assignedTo = command.longValueOfParameterNamed("assignedTo");
		final String description  = command.stringValueOfParameterNamed("description");
	
		final LocalDate startDate = command.localDateValueOfParameterNamed("ticketDate");
		final String startDateString = startDate.toString() + command.stringValueOfParameterNamed("ticketTime");
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final Date ticketDate = df.parse(startDateString);
		final String teamCode = command.stringValueOfParameterNamed("teamCode");
		final Long teamUserId = command.longValueOfParameterNamed("teamUserId");
		return new TicketDetail(assignedTo, description, ticketDate,teamCode,teamUserId);
	}

	public TicketDetail(final Long assignedTo, final String description, final Date ticketDate) {
		this.assignedTo = assignedTo;
		this.comments = description;
		this.createdDate = ticketDate;
	}

	public TicketDetail(final Long assignedTo, final String description, final Date ticketDate, final String teamId, final Long teamUserId) {
		this.assignedTo = assignedTo;
		this.comments = description;
		this.createdDate = ticketDate;
		this.teamCode=teamId;
		this.teamUserId = teamUserId;
	}
	
	public TicketDetail(final Long ticketId, final String comments, final String fileLocation,
			final Long assignedTo, final Long createdbyId) {
    
		this.ticketId = ticketId;
        this.comments = comments;
        this.attachments = fileLocation;
        this.assignedTo = assignedTo;
        this.createdDate = DateUtils.getLocalDateOfTenant().toDate();
        this.createdbyId = createdbyId;	
       
	}
	public TicketDetail(final Long ticketId, final String comments, final String fileLocation,
			final Long assignedTo, final Long createdbyId,final String teamId, final Long teamUserId) {
    
		this.ticketId = ticketId;
        this.comments = comments;
        this.attachments = fileLocation;
        this.assignedTo = assignedTo;
        this.createdDate = DateUtils.getLocalDateOfTenant().toDate();
        this.createdbyId = createdbyId;	
        this.teamCode=teamId;
		this.teamUserId = teamUserId;
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
	
	public String getTeamCode() {
		return teamCode;
	}

	public void setTeamCode(String teamCode) {
		this.teamCode = teamCode;
	}

	public Long getTeamUserId() {
		return teamUserId;
	}

	public void setTeamUserId(Long teamUserId) {
		this.teamUserId = teamUserId;
	}

	
}