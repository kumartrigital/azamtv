package org.mifosplatform.collectionbatch.timeperiod.domain;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.collectionbatch.timemodel.domain.TimePeriod;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.security.service.RandomPasswordGenerator;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "r_time_period")
public class TimePeriodsNew extends AbstractPersistable<Long> {

	@Column(name = "timeperiod_name", nullable = false, length = 10)
	private String timeperiodName;

	@Column(name = "timemodel_id", nullable = false, length = 100)
	private Long timemodelId;

	@Column(name = "startyear", nullable = false, length = 100)
	private Long startYear;

	@Column(name = "endyear", nullable = false, length = 100)
	private Long endYear;

	@Column(name = "startmonth", nullable = false, length = 100)
	private String startMonth;

	@Column(name = "endmonth", nullable = false, length = 100)
	private String endMonth;

	@Column(name = "startday", nullable = false, length = 250)
	private String startDay;

	@Column(name = "endday", nullable = false, length = 10)
	private String endDay;

	@Column(name = "starttime", nullable = false, length = 10)
	private String startTime;

	@Column(name = "endtime", nullable = false, length = 10)
	private String endTime;

	@Column(name = "is_deleted")
	private char isDeleted;

	public TimePeriodsNew() {

	}

	public String getTimeperiodName() {
		return timeperiodName;
	}

	public void setTimeperiodName(String timeperiodName) {
		this.timeperiodName = timeperiodName;
	}

	public Long getTimemodelId() {
		return timemodelId;
	}

	public void setTimemodelId(Long timemodelId) {
		this.timemodelId = timemodelId;
	}

	public Long getStartYear() {
		return startYear;
	}

	public void setStartYear(Long startYear) {
		this.startYear = startYear;
	}

	public Long getEndYear() {
		return endYear;
	}

	public void setEndYear(Long endYear) {
		this.endYear = endYear;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

	public String getStartDay() {
		return startDay;
	}

	public void setStartDay(String startDay) {
		this.startDay = startDay;
	}

	public String getEndDay() {
		return endDay;
	}

	public void setEndDay(String endDay) {
		this.endDay = endDay;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public TimePeriodsNew(String timeperiodName, Long timemodelId, Long startYear, Long endYear, String startMonth,
			String endMonth, String startDay, String endDay, String startTime, String endTime) {

		this.timeperiodName = timeperiodName;
		this.timemodelId = timemodelId;
		this.startYear = startYear;
		this.endYear = endYear;
		this.startMonth = startMonth;
		this.endMonth = endMonth;
		this.startDay = startDay;
		this.endDay = endDay;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isDeleted = 'N';

	}

	public static TimePeriodsNew formJson(JsonCommand command) {

		String timeperiodName = command.stringValueOfParameterNamed("timeperiodName");
		Long timemodelId = command.longValueOfParameterNamed("timemodelId");
		String dateFormat = "dd MM yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		Calendar c = Calendar.getInstance();

		LocalDate localDate = new LocalDate(new Date());
		
		String startTime = command.stringValueOfParameterNamed("startTime");
		
		if(startTime != null ){
			startTime=startTime;
		}else{
			startTime = "0";	 
		}
		/*Time endTime = Time.valueOf("00:00:00");*/
		String endTime = command.stringValueOfParameterNamed("endTime");
		
		if(endTime != null ){
			endTime=endTime;
		}else{
			endTime = "0";	 
		}
		Long startYear = command.longValueOfParameterNamed("startYear");
		
		if(startYear != null ){
			startYear=startYear;
		}else{
				 
		}
		
		Long endYear = command.longValueOfParameterNamed("endYear");
		
		if(endYear != null ){
			endYear=endYear;
		}else{
			
		}
		
		String startMonth = command.stringValueOfParameterNamed("startMonth");
		
		if(startMonth != null ){
			startMonth=startMonth;
		}else{
			startMonth = "0";
		}
		
		String endMonth = command.stringValueOfParameterNamed("endMonth");
		if(endMonth != null ){
			endMonth=endMonth;
		}else{
			endMonth = "0";
		}
		
		String startDay = command.stringValueOfParameterNamed("startDay");
		
		if(startDay != null ){
			startDay=startDay;
		}else{
			startDay = "0";
		}
		
		String endDay = command.stringValueOfParameterNamed("endDay");
		
		if(endDay != null ){
			endDay=endDay;
		}else{
			endDay = "0";
		}

		return new TimePeriodsNew(timeperiodName, timemodelId, startYear, endYear, startMonth, endMonth, startDay,
				endDay, startTime, endTime);

	}

	public Map<String, Object> update(JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);

		final String timeperiodNameNamedParamName = "timeperiodName";
		final String startYearNamedParamName = "startYear";
		final String endYearNamedParamName = "endYear";
		final String startMonthNamedParamName = "startMonth";
		final String endMonthNamedParamName = "endMonth";
		final String startDayNamedParamName = "startDay";
		final String endDayNamedParamName = "endDay";
		final String startTimeNamedParamName = "startTime";
		final String endTimeNamedParamName = "endTime";
		
		if (command.isChangeInStringParameterNamed(timeperiodNameNamedParamName, this.timeperiodName)) {
			final String newValue = command.stringValueOfParameterNamed(timeperiodNameNamedParamName);
			actualChanges.put(timeperiodNameNamedParamName, newValue);
			this.timeperiodName = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInLongParameterNamed(startYearNamedParamName, this.startYear)) {
			final Long newValue = command.longValueOfParameterNamed(startYearNamedParamName);
			actualChanges.put(startYearNamedParamName, newValue);
			this.startYear = newValue;
		}

		if (command.isChangeInLongParameterNamed(endYearNamedParamName, this.endYear)) {
			final Long newValue = command.longValueOfParameterNamed(endYearNamedParamName);
			actualChanges.put(endYearNamedParamName, newValue);
			this.endYear = newValue;
		}
		if (command.isChangeInStringParameterNamed(startMonthNamedParamName, this.startMonth)) {
			final String newValue = command.stringValueOfParameterNamed(startMonthNamedParamName);
			actualChanges.put(startMonthNamedParamName, newValue);
			this.startMonth = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInStringParameterNamed(endMonthNamedParamName, this.endMonth)) {
			final String newValue = command.stringValueOfParameterNamed(endMonthNamedParamName);
			actualChanges.put(endMonthNamedParamName, newValue);
			this.endMonth = StringUtils.defaultIfEmpty(newValue, null);
			
		}
		if (command.isChangeInStringParameterNamed(startDayNamedParamName, this.startDay)) {
			final String newValue = command.stringValueOfParameterNamed(startDayNamedParamName);
			actualChanges.put(startDayNamedParamName, newValue);
			this.startDay = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInStringParameterNamed(endDayNamedParamName, this.endDay)) {
			final String newValue = command.stringValueOfParameterNamed(endDayNamedParamName);
			actualChanges.put(endDayNamedParamName, newValue);
			this.endDay = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInStringParameterNamed(startTimeNamedParamName, this.startTime)) {
			final String newValue = command.stringValueOfParameterNamed(startTimeNamedParamName);
			actualChanges.put(startTimeNamedParamName, newValue);
			this.startTime = StringUtils.defaultIfEmpty(newValue, null);
		}
		if (command.isChangeInStringParameterNamed(endTimeNamedParamName, this.endTime)) {
			final String newValue = command.stringValueOfParameterNamed(endTimeNamedParamName);
			actualChanges.put(endTimeNamedParamName, newValue);
			this.endTime = StringUtils.defaultIfEmpty(newValue, null);
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
