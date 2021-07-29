package org.mifosplatform.crm.ticketmaster.ticketmapping.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "b_team_user")
public class TicketTeamMapping extends AbstractAuditableCustom<AppUser, Long>{
	
	
	@Column(name="team_id", nullable=false, length=10)
	private Long teamId;
	
	@Column(name="user_id", nullable=false, length=10)
	private Long userId;
	
	@Column(name="user_role", nullable=false, length=100)
	private String userRole;
	
	@Column(name="is_team_lead", nullable=false, length=100)
	private char isTeamLead;
	
	@Column(name="status", nullable=false, length=100)
	private String status ;
	
	
	
	@Column(name = "is_deleted")
	private char isDeleted;
	
	
	//private Set<TicketTeamMappingData> ticketteamMappingData = new HashSet<TicketTeamMappingData>();
	
	public TicketTeamMapping() {
	}
	public TicketTeamMapping(final Long teamId,final String userRole,final boolean isTeamLead,final String status) {
		
		this.teamId=teamId;
		/*this.userId = userId;*/
		this.userRole=userRole;
		this.isTeamLead = isTeamLead?'Y':'N';
		this.status=status;
		this.isDeleted = 'N';
		
		}

    public TicketTeamMapping(Long teamId, Long userId) {

    	this.teamId = teamId;
    	this.userId = userId;
    	this.isDeleted = 'N';
    }
	public static TicketTeamMapping formJson(JsonCommand command) {
    	
    	 Long teamId = command.longValueOfParameterNamed("teamId"); 
    	 
		 String userRole = command.stringValueOfParameterNamed("userRole");
		 boolean isTeamLead  = command.booleanPrimitiveValueOfParameterNamed("isTeamLead");
		 String status = command.stringValueOfParameterNamed("status");
			
		 return new TicketTeamMapping(teamId, userRole, isTeamLead,status);
		}
    
       /*public Map<String, Object> update(JsonCommand command) {
		
 	   final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
 	   		
 	   		final String teamIdNamedParamName="teamId";
 	   		final String userRoleNamedParamName = "userRole";
 	   	    final String isTeamLeadNamedParamName = "isTeamLead";
 	   		final String userIdNamedParamName = "userId";
 	   		
 	      	if(command.isChangeInLongParameterNamed(teamIdNamedParamName,this.teamId)){
			final String newValue = command.stringValueOfParameterNamed(teamIdNamedParamName);
			actualChanges.put(teamIdNamedParamName, newValue);
			this.teamId =new Long(newValue);
		    }
 	   		
 	       final String userIdParamName = "userId";
	        if (command.isChangeInArrayParameterNamed(userIdParamName, getUsersAsIdStringArray())) {
	            final String[] newValue = command.arrayValueOfParameterNamed(userIdParamName);
	            actualChanges.put(userIdParamName, newValue);
	        }
		
 	   		return actualChanges;
 	   	
 	    	}*/
       
           public void setUserId(long userId) {
        	   this.userId = userId;
           }
           
           public void setTeamId(long teamId) {
        	   this.teamId = teamId;
           }
       
	        public Long getUserId() {
	        	return userId;
	        }
	        
	        public Long getTeamId() {
	        	return teamId;
	        }

 	    	private String[] getUsersAsIdStringArray() {
	
 	    		return null;
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
