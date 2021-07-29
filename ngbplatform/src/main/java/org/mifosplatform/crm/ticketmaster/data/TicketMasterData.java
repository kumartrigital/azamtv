package org.mifosplatform.crm.ticketmaster.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class TicketMasterData {
	
	private List<TicketMasterData> masterData;
	private Collection<MCodeData> statusType;
	private List<EnumOptionData> priorityType;
    private Collection<MCodeData> problemsDatas;
    private List<UsersData> usersData;
    private Collection<MCodeData> sourceData;
    private Long id;
    private String priority;
    private String status;
    private String assignedTo;
    private LocalDate ticketDate;
    private int userId;
    private String lastComment;
    private String problemDescription;
    private String userName;
    private Integer statusCode;
    private Integer problemCode;
    private String statusDescription;
	private LocalDate createdDate;
	private String attachedFile;
	private String sourceOfTicket;
	private Date dueDate;
	private String resolutionDescription;
	private String ticketstatus;
	private List<SubCategoryData> subCategory;
	private Collection<MCodeData> statusData;
	private LocalDate closedDate;
	private List<TicketTeamMappingData> TicketTeamMappingData;
	private String teamCode;
	private List<TicketTeamMappingData> teamUsers;
	private Long teamUserId;
	private Long teamId;
	private String teamUser;
	private String subCategorys;
	private LocalDate Comment_date;
	private Object subCategoryes;
	private String subCategoryStatus;
	private String title;
	private String ticketNumber;
	private String fileName;
	private Long DocumentId;
	private Long clientId;
	private String subCategry;
	private String description;
	private Long createdBy;
	


	public TicketMasterData(){
		
	}
	
  	public TicketMasterData(final List<EnumOptionData> statusType,
			final List<EnumOptionData> priorityType) {
		this.priorityType = priorityType;
		this.problemsDatas = null;
	}

	public TicketMasterData(final Collection<MCodeData> datas, final List<UsersData> userData,
			final List<EnumOptionData> priorityData, final Collection<MCodeData> sourceData,
			final Collection<MCodeData> statusData, final List<TicketTeamMappingData> ticketmappingData) {
		
		this.problemsDatas = datas;
		this.usersData = userData;
		this.ticketDate = DateUtils.getLocalDateOfTenant();
		this.priorityType = priorityData;
		this.sourceData = sourceData;
		this.statusData = statusData;
		this.closedDate = DateUtils.getLocalDateOfTenant();
		this.TicketTeamMappingData = ticketmappingData;
		
		
	}

	public TicketMasterData(final Long id, final String priority, final String status, final Integer assignedTo, 
			final LocalDate ticketDate, final String lastComment, final String problemDescription, final String userName, 
			final String sourceOfTicket, final Date dueDate, final String description, final String resolutionDescription,
			final Integer problemCode, final Integer statusCode, String ticketstatus,String teamCode,String teamUser,Long teamUserId,Long teamId,final String subCategoryes) {
		
		this.id = id;
		this.priority = priority;
		this.status = status;
		if(assignedTo!=null)
			this.userId = assignedTo;
		this.ticketDate = ticketDate;
		this.lastComment = lastComment;
		if(problemDescription!=null)
		this.problemDescription = problemDescription;
		this.userName = userName;
		if(sourceOfTicket!=null)
		   this.sourceOfTicket = sourceOfTicket;
		this.dueDate = dueDate;
		if(statusDescription!=null)
		   this.statusDescription = description;
		if(resolutionDescription!=null)
           this.resolutionDescription = resolutionDescription;
		this.problemCode = problemCode;
		this.statusCode = statusCode;
		if(ticketstatus!=null)
		   this.ticketstatus = ticketstatus;
		this.teamCode=teamCode;
		this.teamUser=teamUser;
		this.teamUserId=teamUserId;
		this.teamId=teamId;
		this.subCategoryes=subCategoryes;
	
	
		
		
	}
	public TicketMasterData(final Long id, final String priority, final String status, final Integer assignedTo, 
			final LocalDate ticketDate, final String lastComment, final String problemDescription, final String userName, 
			final String sourceOfTicket, final Date dueDate, final String description, final String resolutionDescription,
			final Integer problemCode, final Integer statusCode, String ticketstatus,final Long DocumentId,final String fileName) {
		
		this.id = id;
		this.priority = priority;
		this.status = status;
		this.userId = assignedTo;
		this.ticketDate = ticketDate;
		this.lastComment = lastComment;
		this.problemDescription = problemDescription;
		this.userName = userName;
		this.sourceOfTicket = sourceOfTicket;
		this.dueDate = dueDate;
		this.statusDescription = description;
		this.resolutionDescription = resolutionDescription;
		this.problemCode = problemCode;
		this.statusCode = statusCode;
		this.ticketstatus = ticketstatus;
		this.DocumentId = DocumentId;
		this.fileName = fileName;
		
	}

	public TicketMasterData(final Integer statusCode, final String statusDesc) {
	     this.statusCode = statusCode;
	     this.statusDescription = statusDesc;
	 
	}

	public TicketMasterData(final Long id, final LocalDate createdDate,
			final String assignedTo, final String description, final String fileName,final String teamCode) {
		 this.id = id;
		 this.createdDate = createdDate;
		 this.assignedTo = assignedTo;
	     this.attachedFile = fileName;
	     this.statusDescription = description;
	     this.teamCode=teamCode;
	}

	public TicketMasterData(final String description, final List<TicketMasterData> data) {
		this.problemDescription = description;
		this.masterData = data;

	}
	
	public TicketMasterData(final Long id, final String priority, final LocalDate ticketDate,final String problemDescription,
			                final String status,final String subCategorys,final LocalDate Comment_date,
			                final String lastComment,final String teamCode,final String assignedTo)
	 {
		this.id = id;
		this.priority=priority;
		this.ticketDate = ticketDate;
		this.problemDescription=problemDescription;
		this.status=status;
		this.subCategorys=subCategorys;
		this.Comment_date=Comment_date;
		this.lastComment=lastComment;
		this.teamCode=teamCode;
		this.assignedTo = assignedTo;
		
	  }
	
	public TicketMasterData(final List<SubCategoryData> subCategoryDatas) {
		this.subCategory=subCategoryDatas;
	}

	public TicketMasterData(final List<TicketTeamMappingData> ticketmappingDatas,final String userslist)
	{
		this.teamUsers=ticketmappingDatas;
	 }

	

	public TicketMasterData(Long id, Long clientId, String priority, String status, String lastComment,
			String problemDescription, LocalDate ticketDate, String assignedTo,
			String sourceOfTicket, Date dueDate, String description, String resolutionDescription,
			Integer problemCode, String subcategory, LocalDate commentDate, LocalDate closedDate, Long createdBy) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.clientId = clientId;
		this.priority=priority;
		this.status = status;
		this.lastComment = lastComment;
		this.problemDescription=problemDescription;
		this.ticketDate = ticketDate;
		this.assignedTo = assignedTo;
		this.sourceOfTicket = sourceOfTicket;
		this.dueDate = dueDate;
		this.description = description;
		this.resolutionDescription = resolutionDescription;
		this.problemCode = problemCode;
		this.subCategry = subcategory;
		this.Comment_date = commentDate;
		this.closedDate = closedDate;
		this.createdBy = createdBy;
	
		
		
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public List<EnumOptionData> getPriorityType() {
		return priorityType;
	}

	public Collection<MCodeData> getProblemsDatas() {
		return problemsDatas;
	}

	public List<UsersData> getUsersDatas() {
		return usersData;
	}

	public List<UsersData> getUsersData() {
		return usersData;
	}

	public Long getId() {
		return id;
	}

	public String getPriority() {
		return priority;
	}

	public String getStatus() {
		return status;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public LocalDate getTicketDate() {
		return ticketDate;
	}

	public int getUserId() {
		return userId;
	}

	public String getLastComment() {
		return lastComment;
	}

	public String getProblemDescription() {
		return problemDescription;
	}

	public String getUserName() {
		return userName;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	
	public void setStatusData(final Collection<MCodeData> statusdata) {
		
		this.statusType = statusdata;
	}

	public String getResolutionDescription() {
		return resolutionDescription;
	}

	public void setResolutionDescription(final String resolutionDescription) {
		this.resolutionDescription = resolutionDescription;
	}

	public void setUsersData(final List<UsersData> usersData) {
		this.usersData = usersData;
	}

	public void setPriorityType(List<EnumOptionData> priorityType) {
		this.priorityType = priorityType;
	}

	public void setProblemsDatas(Collection<MCodeData> problemsDatas) {
		this.problemsDatas = problemsDatas;
	}

	public LocalDate getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(LocalDate closedDate) {
		this.closedDate = closedDate;
	}

	public List<TicketTeamMappingData> getTicketTeamMappingData() {
		return TicketTeamMappingData;
	}

	public void setTicketTeamMappingData(List<TicketTeamMappingData> ticketTeamMappingData) {
		TicketTeamMappingData = ticketTeamMappingData;
	}

	public String getTeamCode() {
		return teamCode;
	}

	public Long getTeamUserId() {
		return teamUserId;
	}

	public String getSubCategoryStatus() {
		return subCategoryStatus;
	}

	public void setSubCategoryStatus(String subCategoryStatus) {
		this.subCategoryStatus = subCategoryStatus;
	}
	
	public LocalDate getCommentDate() {
		return Comment_date;
	}
	
	public void setCommentDate(LocalDate Comment_date) {
		this.Comment_date = Comment_date;
	}

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	


	public Long getDocumentId() {
		return DocumentId;
	}

	public void setDocumentId(Long documentId) {
		DocumentId = documentId;
	}
}