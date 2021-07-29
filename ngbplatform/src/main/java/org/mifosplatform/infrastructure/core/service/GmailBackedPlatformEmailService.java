/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.core.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.domain.EmailDetail;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class GmailBackedPlatformEmailService implements PlatformEmailService {
	 private final ConfigurationRepository repository;

	/*
	 * String mailId; String encodedPassword; String decodePassword; String
	 * hostName; String starttlsValue;
	 * */
	 private String authuser;
		private String encodedPassword;
		private String authpwd;
		private String hostName;
		private int portNumber;
		private String port;
		private String starttlsValue;
		private String setContentString;
	 
	 @Autowired
	 
     public GmailBackedPlatformEmailService(final ConfigurationRepository repository) {
         this.repository = repository;
     }
    @Override
    public void sendToUserAccount(final EmailDetail emailDetail, final String unencodedPassword) {      
        	/*
			JSONObject object =new JSONObject(value);
			mailId=(String) object.get("mailId");
			encodedPassword=(String) object.get("password");
			decodePassword=new String(Base64.decodeBase64(encodedPassword));
			hostName=(String) object.get("hostName");
			starttlsValue=(String)object.get("starttls");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        String authuserName =mailId;// "Open Billing System Community";
        
        String authuser = mailId;//"info@openbillingsystem.com";
        String authpwd =decodePassword; //"openbs@13";
        
        // Very Important, Don't use email.setAuthentication()
        email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
        email.setDebug(false); // true if you want to debug
        email.setHostName(hostName);
        try {
            email.getMailSession().getProperties().put("mail.smtp.starttls.enable", starttlsValue);
            email.setFrom(authuser, authuserName);

            StringBuilder subjectBuilder = new StringBuilder().append("BillingX Prototype Demo: ").append(emailDetail.getContactName()).append(" user account creation.");

            email.setSubject(subjectBuilder.toString());

            String sendToEmail = emailDetail.getAddress();

            StringBuilder messageBuilder = new StringBuilder().append("You are receiving this email as your email account: ")
            		.append(sendToEmail).append(" has being used to create a user account for an organisation named [")
            		.append(emailDetail.getOrganisationName()).append("] on BillingX Prototype Demo.")
            		.append("You can login using the following credentials: username: ").append(emailDetail.getUsername()).append(" password: ").append(unencodedPassword);

            email.setMsg(messageBuilder.toString());

            email.addTo(sendToEmail, emailDetail.getContactName());
            email.send();
            */ 
    	
	}

}