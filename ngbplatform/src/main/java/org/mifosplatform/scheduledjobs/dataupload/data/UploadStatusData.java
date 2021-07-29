package org.mifosplatform.scheduledjobs.dataupload.data;

import org.joda.time.LocalDate;

public class UploadStatusData {
	private final Long id;
	private final String uploadProcess;
	private final String uploadFilePath;
	private final LocalDate processDate;
	private final String processStatus;
	private final Long processRecords;
	private final String errorMessage;
	private final boolean flag;
	private final Long unprocessRecords;
	private final Long totalRecords;
	
	
	
	public UploadStatusData(final Long id,final String uploadProcess,final String uploadFilePath,final LocalDate processDate,
			final String processStatus,final Long processRecords,final Long unprocessRecords,final String errorMessage,final  Long totalRecords)
	{
		this.id=id;
		this.uploadProcess=uploadProcess;
		this.uploadFilePath=uploadFilePath;
		this.processDate=processDate;
		this.processRecords=processRecords;
		this.processStatus=processStatus;
		this.errorMessage=errorMessage;
		if(processStatus!=null){
	    this.flag=processStatus.equalsIgnoreCase("Processed")?true:false;
		}else{
			this.flag=false;
		}
	    this.unprocessRecords=unprocessRecords;
	    this.totalRecords=totalRecords;
		
	}


	public Long getId() {
		return id;
	}


	public String getUploadProcess() {
		return uploadProcess;
	}


	public String getUploadFilePath() {
		return uploadFilePath;
	}


	public LocalDate getProcessDate() {
		return processDate;
	}


	public String getProcessStatus() {
		return processStatus;
	}


	public Long getProcessRecords() {
		return processRecords;
	}


	public boolean isFlag() {
		return flag;
	}


	public String getErrorMessage() {
		return errorMessage;
	}
	
	
}
