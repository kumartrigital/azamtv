package org.mifosplatform.organisation.message.data;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;

/**
 * The class <code>BillingMessageData</code> is 
 * a Bean class.
 *  
 * @author ashokreddy
 *
 */
public class BillingMessageTemplateData {
	
	private String templateDescription;
	private String subject;
	private String header;
	private String body;
	private String footer;
	private String parameter;
	private ArrayList<String> messageColumndata;
	private Long clientId;
	private Long id;
	private String messageParameters;
	private List<BillingMessageTemplateData> messageParams;
	private List<MediaEnumoptionData> messageTypes;
	private long deleteButtonId;
	private char messageType;
	

	
	public BillingMessageTemplateData(final Long id, final String templateDescription, 
			final String subject, final String header, final String body, 
			final String footer, final String messageParameters, final char messageType){
		
		// TODO Auto-generated constructor stub
		this.id=id;
		this.templateDescription=templateDescription;
		this.subject=subject;
		this.header=header;
		this.body=body;
		this.footer=footer;
		this.messageParameters=messageParameters;
		this.messageType=messageType;
		
	}
	
	public String getMessageParameters(){
		return this.messageParameters;
	}
	
	public Long getId(){
		return id;
	}


	public BillingMessageTemplateData(Long messageTemplateId,String parameterName) {
		this.deleteButtonId=messageTemplateId;
		// TODO Auto-generated constructor stub
		this.parameter=parameterName;
	}

	public BillingMessageTemplateData(ArrayList<String> rowdata) {
		// TODO Auto-generated constructor stub
		this.messageColumndata=rowdata;
	}

	public ArrayList<String> getMessageColumndata() {
		return messageColumndata;
	}

	public BillingMessageTemplateData(Long commandId) {
		// TODO Auto-generated constructor stub
		this.clientId=commandId;
	}
	public Long getClientId(){
		return clientId;
	}

	public String getParameter() {
		return parameter;
	}

	public String getTemplateDescription() {
		return templateDescription;
	}

	public String getSubject() {
		return subject;
	}

	public String getHeader() {
		return header;
	}

	public String getBody() {
		return body;
	}

	public String getFooter() {
		return footer;
	}

	public void setMessageParams(List<BillingMessageTemplateData> messageParams) {
	this.messageParams=messageParams;
		
	}

	public List<MediaEnumoptionData> getMessageTypes() {
		return messageTypes;
	}

	public void setMessageType(List<MediaEnumoptionData> messageTypes) {
		this.messageTypes = messageTypes;
	}
	
	public BillingMessageTemplateData(){
		
	}

	public char getMessageType() {
		return messageType;
	}

	public void setMessageType(char messageType) {
		this.messageType = messageType;
	}

	public List<BillingMessageTemplateData> getMessageParams() {
		return messageParams;
	}

	public long getDeleteButtonId() {
		return deleteButtonId;
	}
	
	
}
