package org.mifosplatform.crm.ticketmaster.ticketteam.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_team")
public class TicketTeam extends AbstractPersistable<Long>{

	@Column(name="user_id", nullable=false, length=10)
	private Long userId;
	
	@Column(name="team_code", nullable=false, length=100)
	private String teamCode;
	
	@Column(name="team_description", nullable=false, length=100)
	private String teamDescription;
	
	@Column(name="team_category", nullable=false, length=100)
	private String teamCategory;
	
	@Column(name="status", nullable=false, length=100)
	private String status ;
	
	@Column(name = "is_deleted")
	private char isDeleted;
	
	@Column(name = "team_email")
	private String teamEmail;
	
	
	public TicketTeam() {
	}

	
   public TicketTeam(final Long userId,final String teamCode,final String teamDescription,final String teamCategory,final String status,final String teamEmail) {
	
	this.userId = userId;
	this.teamCode=teamCode;
	this.teamDescription=teamDescription;
	this.teamCategory=teamCategory;
	this.status=status;
	this.isDeleted = 'N';
	this.teamEmail=teamEmail;
	
	}



   public static TicketTeam formJson(JsonCommand command) {
		
	    Long userId = command.longValueOfParameterNamed("userId");
		String teamCode = command.stringValueOfParameterNamed("teamCode");
		String teamDescription = command.stringValueOfParameterNamed("teamDescription");
		String teamCategory = command.stringValueOfParameterNamed("teamCategory");
		String status = command.stringValueOfParameterNamed("status");
		String teamEmail = command.stringValueOfParameterNamed("teamEmail");
		
		return new TicketTeam(userId, teamCode, teamDescription, teamCategory, status,teamEmail);
	}
	
	
   public Map<String, Object> update(JsonCommand command) {
		
	   final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
	   		
	   		final String teamCodeNamedParamName = "teamCode";
	   		final String teamDescriptionNameNamedParamName = "teamDescription";
	   		final String teamCategoryNamedParamName = "teamCategory";
	   		final String statusNamedParamName = "status";
	   		final String teamEmailNamedParamName = "teamEmail";
	   		
	   		
	   		
	   		if(command.isChangeInStringParameterNamed(teamCodeNamedParamName, this.teamCode)){
	   			final String newValue = command.stringValueOfParameterNamed(teamCodeNamedParamName);
	   			actualChanges.put(teamCodeNamedParamName, newValue);
	   			this.teamCode = StringUtils.defaultIfEmpty(newValue,null);
	   			
	   		}
	   		if(command.isChangeInStringParameterNamed(teamDescriptionNameNamedParamName, this.teamDescription)){
	   			final String newValue = command.stringValueOfParameterNamed(teamDescriptionNameNamedParamName);
	   			actualChanges.put(teamDescriptionNameNamedParamName, newValue);
	   			this.teamDescription = StringUtils.defaultIfEmpty(newValue, null);
	   		}
	   		
	   		if(command.isChangeInStringParameterNamed(teamCategoryNamedParamName,this.teamCategory)){
	   			final String newValue = command.stringValueOfParameterNamed(teamCategoryNamedParamName);
	   			actualChanges.put(teamCategoryNamedParamName, newValue);
	   			this.teamCategory =StringUtils.defaultIfEmpty(newValue, null);
	   		}
	   		
	   		if(command.isChangeInStringParameterNamed(statusNamedParamName,this.status)){
	   			final String newValue = command.stringValueOfParameterNamed(statusNamedParamName);
	   			actualChanges.put(statusNamedParamName, newValue);
	   			this.status =StringUtils.defaultIfEmpty(newValue, null);
	   		}
	   		
	   		if(command.isChangeInStringParameterNamed(teamEmailNamedParamName,this.teamEmail)){
	   			final String newValue = command.stringValueOfParameterNamed(teamEmailNamedParamName);
	   			actualChanges.put(teamEmailNamedParamName, newValue);
	   			this.teamEmail =StringUtils.defaultIfEmpty(newValue, null);
	   		}
	   		
	   		return actualChanges;
	   	
	   	}


	   	public char getIsDeleted() {
	   		return isDeleted;
	   	}


	   	public void setIsDeleted(char isDeleted) {
	   		this.isDeleted = isDeleted;
	   	}

	   	public void delete() {
	   		this.isDeleted = 'Y';
	   		
	   	}


	
	
	
	
	
	
	
	
}
