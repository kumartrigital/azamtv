package org.mifosplatform.collectionbatch.timemodel.data;

public class TimeModelData {
	
	private Long id;
	private Long timemodelId;
	private String timeModelName;
	private Long timeperiodId;
	private String timeperiodName;
	private String description;
	private Long startYear;
	private Long endYear;
	private String startMonth;
	private String endMonth;
	private String startDay;
	private String endDay;
	private String startTime;
	private String endTime;

	public TimeModelData(Long timemodelId, String timeModelName) {
		this.timemodelId=timemodelId;
		this.timeModelName=timeModelName;
	}
	
	public TimeModelData(Long timeperiodId, String timeperiodName,Long id) {
		this.timeperiodId=timeperiodId;
		this.timeperiodName=timeperiodName;
	}

	public TimeModelData(Long timemodelId,String timeModelName2, String description,Long id,String timeperiodName,Long timeperiodId,Long startYear,Long endYear,String startMonth,
			String endMonth,String startDay,String endDay,String startTime,String endTime) {
		
		this.timemodelId=timemodelId;
		this.timeModelName=timeModelName2;
		this.description=description;
		this.timeperiodName=timeperiodName;
		this.timeperiodId=timeperiodId;
		this.startYear=startYear;
		this.endYear=endYear;
		this.startMonth=startMonth;
		this.endMonth=endMonth;
		this.startDay=startDay;
		this.endDay=endDay;
		this.startTime=startTime;
		this.endTime=endTime;
	}

	public String getTimeModelName() {
		return timeModelName;
	}

	public void setTimeModelName(String timeModelName) {
		this.timeModelName = timeModelName;
	}
	
	
	public Long getTimeperiodId() {
		return timeperiodId;
	}

	public void setTimeperiodId(Long timeperiodId) {
		this.timeperiodId = timeperiodId;
	}

	public String getTimeperiodName() {
		return timeperiodName;
	}

	public void setTimeperiodName(String timeperiodName) {
		this.timeperiodName = timeperiodName;
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
 
	
	
}
