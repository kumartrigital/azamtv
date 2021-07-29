package org.mifosplatform.crm.ticketmaster.ticketteam.data;

import java.util.List;

import org.mifosplatform.useradministration.data.AppUserData;

public class TicketTeamData {
    
	private Long id;
	private Long userId;
	private String teamCode;
	private String teamDescription;
	private String teamCategory;
	private String status;
	private String userName;
	private String teamEmail;
	private List<AppUserData> AppUserData;
	
	
    public TicketTeamData() {
		
	}
	public TicketTeamData(Long id,Long userId, String teamCode, String teamDescription, String teamCategory, String status, String userName,String teamEmail) {
		this.id = id;
		this.userId = userId;
        this.teamCode = teamCode;
        this.teamDescription = teamDescription;
		this.teamCategory = teamCategory;
		this.status = status;
		this.userName= userName;
		this.teamEmail= teamEmail;
		
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public String getTeamCode() {
		return teamCode;
	}

	public String getTeamDescription() {
		return teamDescription;
	}

	public String getTeamCategory() {
		return teamCategory;
	}

	public String getStatus() {
		return status;
	}
	
	public String getteamEmail() {
		return teamEmail;
	}
		
	public void setAppUserDatas(List<AppUserData> AppUserData) {
		this.AppUserData = AppUserData;
	}

	public List<AppUserData> getAppUserData() {
		return AppUserData;
	}	
	
			
	}





