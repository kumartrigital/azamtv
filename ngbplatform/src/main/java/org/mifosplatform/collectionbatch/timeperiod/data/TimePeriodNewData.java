package org.mifosplatform.collectionbatch.timeperiod.data;

public class TimePeriodNewData {

	private Long timeperiodId;
	private Long timemodelId;
	private String timeperiodName;
	private String timeModelName;
	private Long startYear ;
	private Long endYear ;
	private String startMonth ;
	private String endMonth ;
	private String startDay ;
	private String endDay ;
	
	
	public TimePeriodNewData(Long timeperiodId, Long timemodelId, String timeperiodName) {
		this.timeperiodId=timeperiodId;
		this.timemodelId=timemodelId;
		this.timeperiodName=timeperiodName;
	}

	public TimePeriodNewData(Long timeperiodId,Long timemodelId,String timeperiodName,String timeModelName,Long startYear,Long endYear,String startMonth,String endMonth,
			                  String startDay,String endDay ) {
		this.timeperiodId=timeperiodId;
		this.timemodelId=timemodelId;
		this.timeModelName=timeModelName;
		this.timeperiodName=timeperiodName;
		this.startYear=startYear;
		this.endYear=endYear;
		this.startMonth=startMonth;
		this.endMonth=endMonth;
		this.startDay=startDay;
		this.endDay=endDay;
	}

	public Long getTimeperiodId() {
		return timeperiodId;
	}


	public void setTimeperiodId(Long timeperiodId) {
		this.timeperiodId = timeperiodId;
	}


	public Long getTimemodelId() {
		return timemodelId;
	}


	public void setTimemodelId(Long timemodelId) {
		this.timemodelId = timemodelId;
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
