package org.mifosplatform.workflow.eventactionmapping.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_eventaction_mapping")
public class EventActionMapping extends AbstractPersistable<Long> {

	@Column(name = "event_name")
	private String eventName;

	@Column(name = "action_name")
	private String actionName;

	@Column(name = "process")
	private String process;
	
	@Column(name = "order_by")
	private Long orderBy;
	
	@Column(name = "pre_post")
	private char prePost;
	
	@Column(name = "process_params")
	private String processParams;

	@Column(name = "is_deleted")
	private char isDeleted = 'N';
	
	@Column(name = "is_synchronous")
	private char issynchronous;

	public EventActionMapping() {
		// TODO Auto-generated constructor stub

	}

	public EventActionMapping(final String eventName, final String actionName,
			final String process,final Long orderBy,final char prePost,final String processParams,final Boolean issynchronous) {

		this.eventName = eventName;
		this.actionName = actionName;
		this.process = process;
		this.orderBy = orderBy;
		this.prePost = prePost;
		this.processParams = processParams;
		if(issynchronous){
	        this.issynchronous='Y';
	    }
	    else{
	        this.issynchronous='N';
	    }
	}

	public String getEventName() {
		return eventName;
	}

	public String getActionName() {
		return actionName;
	}

	public String getProcess() {
		return process;
	}
	
	public Long getOrderBy() {
		return orderBy;
	}
	
	public char getPrePost() {
		return prePost;
	}
	
	public String getProcessParams() {
		return processParams;
	}
	 
	public char getissynchronous() {
		return issynchronous;
	}
	
	public static EventActionMapping fromJson(final JsonCommand command) {

		final String eventName = command.stringValueOfParameterNamed("event");
		final String actionName = command.stringValueOfParameterNamed("action");
		final String process = command.stringValueOfParameterNamed("process");
		final Long orderBy = command.longValueOfParameterNamed("orderBy");
		final boolean prePost = command.booleanPrimitiveValueOfParameterNamed("prePost");
		char prepost=prePost?'y':'n';
		final String processParams = command.stringValueOfParameterNamed("processParams");
		final Boolean issynchronous = command.booleanPrimitiveValueOfParameterNamed("issynchronous");

		return new EventActionMapping(eventName, actionName, process,orderBy,prepost,processParams,issynchronous);
	}

	public Map<String, Object> update(final JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String eventParamName = "event";
		if (command.isChangeInStringParameterNamed(eventParamName, this.eventName)) {
			final String newValue = command.stringValueOfParameterNamed(eventParamName);
			actualChanges.put(eventParamName, newValue);
			this.eventName = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String actionParamName = "action";
		if (command.isChangeInStringParameterNamed(actionParamName, this.actionName)) {
			final String newValue = command.stringValueOfParameterNamed(actionParamName);
			actualChanges.put(actionParamName, newValue);
			this.actionName = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String processParam = "process";
		if (command.isChangeInStringParameterNamed(processParam, this.process)) {
			final String newValue = command.stringValueOfParameterNamed(processParam);
			actualChanges.put(processParam, newValue);
			this.process = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String orderByParamName = "orderBy";
		if (command.isChangeInLongParameterNamed(orderByParamName, this.orderBy)) {
			final Long newValue = command.longValueOfParameterNamed(orderByParamName);
			actualChanges.put(orderByParamName, newValue);
			this.orderBy = newValue;
		}
		
		final String prePostParamName = "prePost";
		boolean prePost = false;
		if(this.prePost == 'Y'){
			prePost =true;
		}
		if (command.isChangeInBooleanParameterNamed(prePostParamName, prePost)) {
			final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(prePostParamName);
			actualChanges.put(prePostParamName, newValue);
			this.prePost = newValue?'Y':'N';
		}
		
		final String processParamsParamName = "processParams";
		if (command.isChangeInStringParameterNamed(processParamsParamName, this.processParams)) {
			final String newValue = command.stringValueOfParameterNamed(processParamsParamName);
			actualChanges.put(processParamsParamName, newValue);
			this.processParams = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String issynchronousNamedParamName = "issynchronous";
		final boolean newvalueOfissynchronous = command.booleanPrimitiveValueOfParameterNamed(issynchronousNamedParamName);
		if(String.valueOf(newvalueOfissynchronous?'Y':'N').equalsIgnoreCase(String.valueOf(this.issynchronous))){
			actualChanges.put(issynchronousNamedParamName, newvalueOfissynchronous);
		}
		this.issynchronous = newvalueOfissynchronous?'Y':'N';

		return actualChanges;

	}

	public void delete() {

		if (this.isDeleted == 'N') {
			this.isDeleted = 'Y';
		} else {
			this.isDeleted = 'N';
		}

	}

}
