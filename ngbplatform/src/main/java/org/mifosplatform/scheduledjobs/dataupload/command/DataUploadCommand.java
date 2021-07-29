package org.mifosplatform.scheduledjobs.dataupload.command;

import java.io.InputStream;
import java.util.Set;

import org.joda.time.LocalDate;


public class DataUploadCommand {
	
	private final String uploadProcess;
	private final String uploadFilePath;
	private final LocalDate processDate;
	private final String processStatus;
	private final Long processRecords;
	private final String errorMessage;
	private final String description;
	private final String fileName;
	private final InputStream inputStream;
	private final String fileUploadLocation;

	
	private final Set<String> modifiedParameters;
	
	/*public DataUploadCommand()
	{
		this.modifiedParameters=null;
	}*/
	
	public DataUploadCommand(final String uploadProcess,final String uploadFilePath,final LocalDate processDate,
			final  String processStatus,final Long processRecords,final String errorMessage,final Set<String> modifiedParameters,
			final String description,final String fileName,final InputStream inputStream,final String fileUploadLocation)
	    
	    {
		this.uploadProcess=uploadProcess;
		this.uploadFilePath=uploadFilePath;
		this.processDate=processDate;
		this.processStatus=processStatus;
		this.processRecords=processRecords;
		this.errorMessage=errorMessage;
		this.modifiedParameters=modifiedParameters;
		this.description=description;
		this.fileName=fileName;
		this.inputStream=inputStream;
		this.fileUploadLocation= fileUploadLocation;
		
	}

	
	public String getFileUploadLocation() {
		return fileUploadLocation;
	}



	public InputStream getInputStream() {
		return inputStream;
	}

	
	public String getFileName() {
		return fileName;
	}



	public String getDescription() {
		return description;
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


	public String getErrorMessage() {
		return errorMessage;
	}


	public Set<String> getModifiedParameters() {
		return modifiedParameters;
	}  
}
