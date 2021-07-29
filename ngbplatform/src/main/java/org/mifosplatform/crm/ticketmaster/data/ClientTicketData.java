 package org.mifosplatform.crm.ticketmaster.data;


import org.joda.time.LocalDate;

public class ClientTicketData {
	
	private final Long id;
    private final String priority;
    private final String status;
    private final Long userId;
    private final LocalDate ticketDate;
    private final String lastComment;
    private final String problemDescription;
    private final String userName;
    private final Long clientId;
	private String timeElapsed;
	private Object clientName;
	private final String createUser;
	private final String closedByuser;
	private  LocalDate closedDate;
	private String managerName;
	private String managerEmail;
	private Long escalation;
	private String ticketNo;
	private String type;
	
	public ClientTicketData(long id, String status, long escalation, String managerName, String managerEmail, String ticketNo , LocalDate ticketDate){
		this.id = id;
		this.priority = null;
		this.status = status;
		this.userId = null;
		this.ticketDate = ticketDate;
		this.lastComment = null;
		this.problemDescription =null;
		this.userName = null;
		this.clientId = null;
		this.createUser = null;
		this.closedByuser = null;
		this.escalation = escalation;
		this.managerName = managerName;
		this.managerEmail = managerEmail;
		this.ticketNo = ticketNo;
	}
	
	public ClientTicketData(final Long id, final String priority, final String status, final Long assignedTo, final LocalDate ticketDate,
			final String lastComment, final String problemDescription, final String userName, final Long clientId) {
		
		this.id = id;
		this.priority = priority;
		this.status = status;
		this.userId = assignedTo;
		this.ticketDate = ticketDate;
		this.lastComment = lastComment;
		this.problemDescription = problemDescription;
		this.userName = userName;
		this.clientId = clientId;
		this.closedByuser = null;
		this.createUser = null;
	
	}
	public ClientTicketData(final Long id, final String priority, final String status, final Long assignedTo, final LocalDate ticketDate,
			final String lastComment, final String problemDescription, final String userName, final Long clientId,
			final String timeElapsed, final String clientName, final String createUser, final String closedByuser ,String type) {
	
		this.id = id;
		this.priority = priority;
		this.status = status;
		this.userId = assignedTo;
		this.ticketDate = ticketDate;
		this.lastComment = lastComment;
		this.problemDescription = problemDescription;
		this.userName = userName;
		this.clientId = clientId;
		this.timeElapsed = timeElapsed;
		this.clientName = clientName;
		this.createUser = createUser;
		this.closedByuser = closedByuser;
		this.type  =  type;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * @return the ticketDate
	 */
	public LocalDate getTicketDate() {
		return ticketDate;
	}

	/**
	 * @return the lastComment
	 */
	public String getLastComment() {
		return lastComment;
	}

	/**
	 * @return the problemDescription
	 */
	public String getProblemDescription() {
		return problemDescription;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the clientId
	 */
	public Long getClientId() {
		return clientId;
	}
	
	public String getTimeElapsed() {
		return timeElapsed;
	}
	
	public void setTimeElapsed(final String timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
	
	public Object getClientName() {
		return clientName;
	}
	
	public void setClientName(final Object clientName) {
		this.clientName = clientName;
	}
	
	public String getCreateUser() {
		return createUser;
	}
	
	public String getClosedByuser() {
		return closedByuser;
	}
	public LocalDate getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(LocalDate closedDate) {
		this.closedDate = closedDate;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerEmail() {
		return managerEmail;
	}

	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}

	public Long getEscalation() {
		return escalation;
	}

	public void setEscalation(Long escalation) {
		this.escalation = escalation;
	}

	public String getTicketNo() {
		return ticketNo;
	}

	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

}