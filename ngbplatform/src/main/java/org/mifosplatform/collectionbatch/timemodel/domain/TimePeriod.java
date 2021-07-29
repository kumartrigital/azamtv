package org.mifosplatform.collectionbatch.timemodel.domain;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;



@Entity
@Table(name = "r_time_period")
public class TimePeriod extends AbstractPersistable<Long>{
	
	/**
	 * 
	 */
	
	@Column(name="timeperiod_name", nullable=false, length=10)
	private String timeperiodName;
	/*
	@Column(name="timemodel_id", nullable=false, length=100)
	private Long timemodelId;*/
	

	@ManyToOne
	@JoinColumn(name = "timemodel_id")
	private TimeModel timeModel;
	
	@Column(name="startyear", nullable=false, length=100)
	private Long startYear;
	
	@Column(name="endyear", nullable=false, length=100)
	private Long endYear;
	
	@Column(name="startmonth", nullable=false, length=100)
	private Long startMonth ;
	
	@Column(name="endmonth", nullable=false, length=100)
	private Long endMonth;
	
	@Column(name="startday", nullable=false, length=250)
	private Long startDay;
	
	@Column(name="endday", nullable=false, length=10)
	private Long endDay;
	
	@Column(name="starttime", nullable=false, length=10)
	private Time startTime;
	
	@Column(name="endtime", nullable=false, length=10)
	private Time endTime;
	
	


	public TimePeriod(String timeperiodName, Long startYear, Long endYear, 
			Long startMonth, Long endMonth, Long startDay, Long endDay, Time startTime, Time endTime) {

		this.timeperiodName = timeperiodName;
		this.startYear = startYear;
		this.endYear = endYear;
		this.startMonth = startMonth;
		this.endMonth = endMonth;
		this.startDay = startDay;
		this.endDay = endDay;
		this.startTime = startTime;
		this.endTime = endTime;
	
	}


	public TimePeriod(String timeperiodName, Long startYear, Long endYear, Long startMonth, Long endMonth,
			Long startDay, Long endDay, Time startTime, Time endTime, Long id) {
	
		this.timeperiodName = timeperiodName;
		//this.timemodelId = timemodelId;
		this.startYear = startYear;
		this.endYear = endYear;
		this.startMonth = startMonth;
		this.endMonth = endMonth;
		this.startDay = startDay;
		this.endDay = endDay;
		this.startTime = startTime;
		this.endTime = endTime;
	
		
	}


	public static TimePeriod formJson(JsonCommand command) {
		
		String timeperiodName = command.stringValueOfParameterNamed("timeperiodName");
		Long timemodelId = command.longValueOfParameterNamed("timemodelId");
		String dateFormat = "dd MM yyyy";        
   	 	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
   	 	
   	 	Calendar c = Calendar.getInstance();
		
   	 
   	/* c.set(Calendar.YEAR, 2017);
   	c.set(Calendar.MONTH, 11); // 11 = december
   	c.set(Calendar.DAY_OF_MONTH, 31); // new years eve
*/	
   	 	
   	
   /*	c.clear();
   	c.set(Calendar.MONTH, c.MONTH);
   	c.set(Calendar.YEAR, c.getWeekYear());
 	c.set(Calendar.DAY_OF_MONTH, c.DAY_OF_MONTH);
   	Date date = c.getTime();
   
   	    Long startYear = Long.valueOf(c.getInstance().get(Calendar.YEAR));
	    Long startMonth = Long.valueOf(c.getInstance().get(Calendar.MONTH));
	    Long startDay = Long.valueOf(c.getInstance().get(Calendar.DAY_OF_MONTH));*/
	    //String startTime = String.valueOf(date.getTime());
   	 	LocalDate localDate = new LocalDate(new Date());
   	 	long startYear = localDate.getYear();
   	 	long startMonth = localDate.getMonthOfYear();
   	 	long startDay = localDate.getDayOfMonth();
	    Time startTime = new Time(new Date().getTime());
	    Time endTime = new Time(new Date().getTime());
   	    
   	    //Long startTime = Long.valueOf(c.getTime());
		//LocalDate startYear = command.localDateValueOfParameterNamed("startYear");
		Long endYear = command.longValueOfParameterNamed("endYear");
		//Long startMonth = command.longValueOfParameterNamed("startMonth");
		Long endMonth = command.longValueOfParameterNamed("endMonth");
		//Long startDay = command.longValueOfParameterNamed("startDay");
		Long endDay = command.longValueOfParameterNamed("endDay");
		//Long startTime = command.longValueOfParameterNamed("startTime");
		//Long endTime = command.longValueOfParameterNamed("endTime");
		
		
		
		/*
		 * Calendar calendar = Calendar.getInstance(); calendar.clear();
		 * calendar.set(Calendar.MONTH, month); calendar.set(Calendar.YEAR,
		 * year); Date date = calendar.getTime();
		 */
		
		
		return new TimePeriod(timeperiodName, startYear, endYear, 
				startMonth, endMonth, startDay, endDay, startTime, endTime);
		
		
		
	}


	public String getTimeperiodName() {
		return timeperiodName;
	}


	public void setTimeperiodName(String timeperiodName) {
		this.timeperiodName = timeperiodName;
	}


	/*public Long getTimemodelId() {
		return timemodelId;
	}


	public void setTimemodelId(Long timemodelId) {
		this.timemodelId = timemodelId;
	}*/


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


	public Long getStartMonth() {
		return startMonth;
	}


	public void setStartMonth(Long startMonth) {
		this.startMonth = startMonth;
	}


	public Long getEndMonth() {
		return endMonth;
	}


	public void setEndMonth(Long endMonth) {
		this.endMonth = endMonth;
	}


	public Long getStartDay() {
		return startDay;
	}


	public void setStartDay(Long startDay) {
		this.startDay = startDay;
	}


	public Long getEndDay() {
		return endDay;
	}


	public void setEndDay(Long endDay) {
		this.endDay = endDay;
	}


	public Time getStartTime() {
		return startTime;
	}


	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}


	public Time getEndTime() {
		return endTime;
	}


	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}


	public TimeModel getTimeModel() {
		return timeModel;
	}


	public void setTimeModel(TimeModel timeModel) {
		this.timeModel = timeModel;
	}


	public void update(TimeModel timeModel) {
		this.timeModel=timeModel;
		
	}


	
	
	
	
	
	

	
	
	
	
	
	

	
	

}
