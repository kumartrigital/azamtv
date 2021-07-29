package org.mifosplatform.portfolio.order.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * @author trigital
 *
 */
@Entity
@Table(name = "b_order_workflow")
public class OrderWorkflow extends AbstractPersistable<Long>{


	@Column(name = "order_id")
	private Long orderId;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "assigned_to")
	private Long assignedTo;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "is_deleted")
	private char isDeleted;
	
	public OrderWorkflow(){
		
	}
	
	

	public OrderWorkflow(Long orderId, String status, Long assignedTo, String description) {
		
		this.orderId = orderId;
		this.status = status;
		this.assignedTo = assignedTo;
		this.description = description;
		this.isDeleted = 'N';
	}



	public static OrderWorkflow formJson(JsonCommand command) {

		Long orderId = command.longValueOfParameterNamed("clientServiceId");
		String status = command.stringValueOfParameterNamed("status");
		Long assignedTo  = command.longValueOfParameterNamed("assignedTo");
		String description  = command.stringValueOfParameterNamed("description");
		
		return new OrderWorkflow(orderId, status, assignedTo,description);
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(Long assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	
	
	
	
}
