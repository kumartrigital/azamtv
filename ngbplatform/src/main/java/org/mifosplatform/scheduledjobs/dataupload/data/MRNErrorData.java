package org.mifosplatform.scheduledjobs.dataupload.data;


public class MRNErrorData{
	
	private final Long rowNumber;
	private final String errorMessage;
	
	public MRNErrorData(final Long rowNumber, final Object object){
		this.rowNumber = rowNumber;
		
		if(object instanceof String)
			this.errorMessage = object.toString();
		
		else if(object instanceof Long)
			this.errorMessage = ""+object.toString();
		
		else{
			errorMessage = object.toString();
		}
			
	}
	public Long getRowNumber() {
		return rowNumber;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
}

