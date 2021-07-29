/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.message.service;

import org.mifosplatform.organisation.message.data.BillingMessageDataForProcessing;

/**
 * 
 * @author ashokreddy
 *
 */
public interface MessagePlatformEmailService {

    String sendToUserEmail(BillingMessageDataForProcessing emailDetail);

	String sendToUserMobile(String message, Long id, String messageTo, String messageBody);

	String createEmail(String pdfFileName, String emailId);

	String sendGeneralMessage(String uniqueReference, String body, String subject);
	
	String sendTicketMessage(BillingMessageDataForProcessing emailDetail);

	/*String sendToMobile(String message, Long id, String messageTo, String body);*/
	
	
	
	
}