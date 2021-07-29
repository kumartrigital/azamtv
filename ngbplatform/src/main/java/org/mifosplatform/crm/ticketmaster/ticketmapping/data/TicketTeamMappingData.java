package org.mifosplatform.crm.ticketmaster.ticketmapping.data;

import java.util.List;

import org.mifosplatform.useradministration.data.AppUserData;

public class TicketTeamMappingData {

	private Long id;
	private Long teamId;
	private Long userId;
	private String userRole;
	private Boolean isTeamLead;
	private String status;
	private String teamCode;
	private String teamDescription;
	private List<TicketTeamMappingData> TicketTeamMappingData;
	private List<AppUserData> AppUserData;
	private List<TicketTeamMappingData> selectedUsers;
	private List<org.mifosplatform.useradministration.data.AppUserData> availableUsers;
	private String username;
	private String teamEmail;
	
	
	
	
	public TicketTeamMappingData() {
	}
	
	public TicketTeamMappingData(final Long id,final Long teamId,final Long userId, final String userRole, final Boolean isTeamLead, String status, String teamCode) {
		
		this.id = id;
		this.teamId = teamId;
        this.userId = userId;
        this.userRole = userRole;
		this.isTeamLead = isTeamLead;
		this.status = status;
		this.teamCode = teamCode;
		
		
    }
	
	
   public TicketTeamMappingData(Long id, String teamCode,String teamDescription) {
		
		this.id = id;
		this.teamCode = teamCode;
		this.teamDescription = teamDescription;
	}



	public TicketTeamMappingData(Long id, String userName) {
	  
		this.id = id;
		this.username =userName;		
    }

	public TicketTeamMappingData(Long id, String username,Long userId) {
		  
		this.id = id;
		this.username =username;
		this.userId = userId;
	}
	
	public TicketTeamMappingData(Long id, String teamCode,String teamDescription, String email) {
		  
		this.id = id;
		this.teamCode =teamCode;
		this.teamDescription = teamDescription;
		this.teamEmail = email;
	}
	public Long getId() {
		return id;
	}

	public Long getTeamId() {
		return teamId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUserRole() {
		return userRole;
	}
	
	public Boolean getIsTeamLead() {
		return isTeamLead;
	}

	public String getStatus() {
		return status;
	}


	public void setTicketTeam(List<TicketTeamMappingData> TicketTeamMappingData) {
		this.TicketTeamMappingData = TicketTeamMappingData;
	}
	public List<TicketTeamMappingData> getTicketTeamMappingData() {
		return TicketTeamMappingData;
	}

	public String getTeamCode() {
		return teamCode;
	}

	public String getTeamDescription() {
		return teamDescription;
	}

	public void setAppUserDatas(List<AppUserData> AppUserData) {
		this.AppUserData = AppUserData;
	}

	public List<AppUserData> getAppUserData() {
		return AppUserData;
	}

	public void setSelectedUsersDatas(List<TicketTeamMappingData> selectedUsers) {
		this.selectedUsers=selectedUsers;
		
	}

	public List<TicketTeamMappingData> getSelectedUsers() {
		return selectedUsers;
	}

	public void setAvailableUsersDatas(List<AppUserData> availableUsers) {
		this.availableUsers=availableUsers;
		
	}

	public List<AppUserData> getAvailableUsers() {
		return availableUsers;
	}
	
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	public void setTeamCode(String teamCode) {
		this.teamCode=teamCode;
		
	}

	

	

	

	
	
	

}