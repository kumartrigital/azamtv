package org.mifosplatform.collectionbatch.timemodel.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "r_timemodel")
public class TimeModel extends AbstractPersistable<Long> {

	@Column(name = "timemodel_name", nullable = false, length = 10)
	private String timeModelName;

	@Column(name = "description", nullable = false, length = 10)
	private String description;
	
	@Column(name = "is_active")
	private char isActive;
	
	/*@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "timeModel", orphanRemoval = true)
	private Set<TimePeriod> timePeriods = new HashSet<TimePeriod>();*/
	
	public TimeModel() {
	
	}
	

	public String getTimeModelName() {
		return timeModelName;
	}

	public void setTimeModelName(String timeModelName) {
		this.timeModelName = timeModelName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	public TimeModel(final String timeModelName, final String description) {

		this.timeModelName = timeModelName;
		this.description = description;
		this.isActive = 'Y';
	}

	public static TimeModel formJson(JsonCommand command) {

		String timeModelName = command.stringValueOfParameterNamed("timeModelName");
		String description = command.stringValueOfParameterNamed("description");

		return new TimeModel(timeModelName, description);

	}
	
/*
	public Set<TimePeriod> getTimePeriods() {
		return timePeriods;
	}

	public void setTimePeriods(Set<TimePeriod> timePeriods) {
		this.timePeriods = timePeriods;
	}

	public void addDetails(TimePeriod timePeriod) {
		timePeriod.update(this);
		this.timePeriods.add(timePeriod);
	}*/
	
	public Map<String, Object> update(JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String timeModelNameNamedParamName = "timeModelName";
		final String descriptionNamedParamName = "description";
		
		/*if(command.isChangeInStringParameterNamed(timeModelNameNamedParamName, this.timeModelName)){
			final String newValue = command.stringValueOfParameterNamed(timeModelNameNamedParamName);
			actualChanges.put(timeModelNameNamedParamName, newValue);
			this.timeModelName = StringUtils.defaultIfEmpty(newValue,null);
		}*/
		if(command.isChangeInStringParameterNamed(timeModelNameNamedParamName, this.timeModelName)){
			final String newValue = command.stringValueOfParameterNamed(timeModelNameNamedParamName);
			actualChanges.put(timeModelNameNamedParamName, newValue);
			this.timeModelName = StringUtils.defaultIfEmpty(newValue,null);
		}
		if(command.isChangeInStringParameterNamed(descriptionNamedParamName, this.description)){
			final String newValue = command.stringValueOfParameterNamed(descriptionNamedParamName);
			actualChanges.put(descriptionNamedParamName, newValue);
			this.description = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		return actualChanges;
		
	}


	
	
	public char getIsActive() {
		return isActive;
	}


	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}


	public void delete() {
		this.isActive = 'Y';
		
	}

	
	}
	
	





