package org.mifosplatform.scheduledjobs.dataupload.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name="b_data_uploads")
public class DataUpload extends AbstractPersistable<Long>{

	@Column(name="file_name", nullable=true, length=100)
	private String fileName;

	@Column(name="upload_process", nullable=false, length=20)
	private String uploadProcess;
	
	@Column(name="upload_filepath", nullable=true, length=100)
	private String uploadFilePath;
	
	@Column(name="process_date", nullable=false, length=20)
	private Date processDate;
	
	@Column(name="process_status",nullable=true,length=100)
	private String processStatus="New Unprocessed";
	
	@Column(name="process_records",nullable=true,length=20)
	private Long processRecords;
	
	@Column(name="description",nullable=true,length=100)
	private String description;
	
	@Column(name="error_message",nullable=true,length=20)
	private String errorMessage;
	
	@Column(name="is_deleted",nullable=false, length=20)
	private char isDeleted='N';

	@Column(name="unprocess_records",nullable=true,length=20)
	private Long unprocessedRecords;
	
	@Column(name="total_records",nullable=true,length=20)
	private Long totalRecords;

	public DataUpload(){}
	
	
	public DataUpload(final String uploadProcess,final String uploadFilePath,final LocalDate processDate,final String processStatus,final Long processRecords,
			final String errorMessage,final String description,final  String fileName){
		
		this.uploadProcess=uploadProcess;
		this.uploadFilePath=uploadFilePath;
		this.processDate=processDate.toDate();
		this.processStatus=DataUploadEnum.NEW.toString();
		this.processRecords=processRecords;
		this.errorMessage=errorMessage;
		this.description=description;
		this.fileName=fileName;
		
		
	}
	
	public static DataUpload create(final String uploadProcess,final String uploadFilePath,final LocalDate processDate,final String processStatus,
			final Long processRecords,final String errorMessage,final String description,final String fileName){
		
		return new DataUpload(uploadProcess,uploadFilePath,processDate,processStatus,processRecords,errorMessage,description,fileName);
	}
	
	public void update(LocalDate currentDate,String processStatus,Long processRecords,Long unprocessedRecords, String errorMessage, Long totalRecords) {
			this.processDate = currentDate.toDate();
			this.processStatus=unprocessedRecords > 0?DataUploadEnum.ERROR.toString():DataUploadEnum.COMPLETED.toString();
			this.processRecords=processRecords;
			this.errorMessage=unprocessedRecords > 0?DataUploadEnum.ERROR.toString():DataUploadEnum.COMPLETED.toString();
			this.unprocessedRecords=unprocessedRecords;
			this.totalRecords=totalRecords;
			
		}


	public String getUploadProcess() {
		return uploadProcess;
	}


	public String getUploadFilePath() {
		return uploadFilePath;
	}


	public Date getProcessDate() {
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


	public char getIsDeleted() {
		return isDeleted;
	}


	public String getFileName() {
		// TODO Auto-generated method stub
		return fileName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Long getUnprocessedRecords() {
		return unprocessedRecords;
	}


	public void setUnprocessedRecords(final Long unprocessedRecords) {
		this.unprocessedRecords = unprocessedRecords;
	}


	public Long getTotalRecords() {
		return totalRecords;
	}


	public void setTotalRecords(Long totalRecords) {
		this.totalRecords = totalRecords;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public void setUploadProcess(String uploadProcess) {
		this.uploadProcess = uploadProcess;
	}


	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}


	public void setProcessDate(final Date processDate) {
		this.processDate = processDate;
	}


	public void setProcessStatus(final String processStatus) {
		this.processStatus = processStatus;
	}


	public void setProcessRecords(final Long processRecords) {
		this.processRecords = processRecords;
	}


	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}


	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}


	
	
}
