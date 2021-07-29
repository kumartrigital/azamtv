package org.mifosplatform.workflow.eventvalidation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_event_validation")
public class EventValidation extends AbstractPersistable<Long> {

	@Column(name = "event_name")
	private String eventName;

	@Column(name = "process")
	private String process;

	@Column(name = "pre_post")
	private char prePost;

	@Column(name = "is_deleted")
	private char isDeleted = 'N';

	public EventValidation() {
		// TODO Auto-generated constructor stub

	}

	public EventValidation(final String eventName, final String process) {

		this.eventName = eventName;
		this.process = process;
	}

	public String getEventName() {
		return eventName;
	}

	public String getProcess() {
		return process;
	}

	public static EventValidation fromJson(final JsonCommand command) {

		final String eventName = command.stringValueOfParameterNamed("event");
		final String process = command.stringValueOfParameterNamed("process");

		return new EventValidation(eventName, process);
	}

	public void delete() {

		if (this.isDeleted == 'N') {
			this.isDeleted = 'Y';
		} else {
			this.isDeleted = 'N';
		}

	}

	public char getPrePost() {
		return prePost;
	}

	public char isDeleted() {
		return isDeleted;
	}
	
	

}
