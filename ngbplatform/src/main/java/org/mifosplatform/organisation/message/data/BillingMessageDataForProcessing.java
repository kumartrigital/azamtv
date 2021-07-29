package org.mifosplatform.organisation.message.data;

/**
 * The class <code>BillingMessageDataForProcessing</code> 
 * is a Bean class.
 * 
 * @author ashokreddy
 *
 */
public class BillingMessageDataForProcessing {

	
	private final String messageTo;
	private final String messageFrom;
	private final String subject;
	private final String header;
	private final String body;
	private final String footer;
	private final Long id;
	private char messageType;
	private final String attachment;
	
	public BillingMessageDataForProcessing(final Long id, final String messageto,
			final String messagefrom, final String subject, final String header, 
			final String body, final String footer,final char messageType,
			final String attachment) {

		// TODO Auto-generated constructor stub
		this.id=id;
		this.messageFrom=messagefrom;
		this.messageTo=messageto;
		this.body=body;
		this.footer=footer;
		this.header=header;
		this.subject=subject;
		this.messageType=messageType;
		this.attachment=attachment;
	}

	public String getMessageTo() {
		return messageTo;
	}

	public String getMessageFrom() {
		return messageFrom;
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

	public Long getId() {
		return id;
	}

	public char getMessageType() {
		return messageType;
	}

	public String getAttachment() {
		return attachment;
	}

}
