/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.domain;

import org.mifosplatform.infrastructure.core.service.PlatformEmailService;
import org.mifosplatform.infrastructure.security.service.PlatformPasswordEncoder;
import org.mifosplatform.organisation.message.domain.BillingMessage;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateConstants;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.exception.BillingMessageTemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JpaUserDomainService implements UserDomainService {

    private final AppUserRepository userRepository;
    private final PlatformPasswordEncoder applicationPasswordEncoder;
    private final PlatformEmailService emailService;
    private final BillingMessageRepository messageDataRepository;
    private final BillingMessageTemplateRepository billingMessageTemplateRepository;

    @Autowired
    public JpaUserDomainService(final AppUserRepository userRepository, final PlatformPasswordEncoder applicationPasswordEncoder,
            final PlatformEmailService emailService, final BillingMessageRepository messageDataRepository,
            final BillingMessageTemplateRepository billingMessageTemplateRepository) {
        this.userRepository = userRepository;
        this.applicationPasswordEncoder = applicationPasswordEncoder;
        this.emailService = emailService;
        this.messageDataRepository = messageDataRepository;
        this.billingMessageTemplateRepository = billingMessageTemplateRepository;
    }

    @Transactional
    @Override
    public void create(final AppUser appUser, final Boolean sendPasswordToEmail) {

        generateKeyUsedForPasswordSalting(appUser);

        final String unencodedPassword = appUser.getPassword();

        final String encodePassword = this.applicationPasswordEncoder.encode(appUser);
        appUser.updatePassword(encodePassword);

        this.userRepository.save(appUser);

        if (sendPasswordToEmail.booleanValue()) {
            /*final EmailDetail emailDetail = new EmailDetail(appUser.getOffice().getName(), appUser.getFirstname(), appUser.getEmail(),
                    appUser.getUsername());
            StringBuilder subjectBuilder = new StringBuilder().append("BillingX Prototype Demo: ")
            		.append(emailDetail.getContactName()).append(" user account creation.");*/
            
            BillingMessageTemplate messageDetails=this.billingMessageTemplateRepository.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_USER);
            if(messageDetails!=null){
			String subject=messageDetails.getSubject();
			String body=messageDetails.getBody();
			String footer=messageDetails.getFooter();
			String header=messageDetails.getHeader();
			header=header.replace("<PARAM1>", appUser.getFirstname());
			body=body.replace("<PARAM2>", appUser.getUsername());
			body=body.replace("<PARAM3>", unencodedPassword);
			
			BillingMessage billingMessage = new BillingMessage(header, body, footer, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_EMAIL_FROM, appUser.getEmail(),
					subject, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, messageDetails, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
			
			this.messageDataRepository.save(billingMessage);

           //this.emailService.sendToUserAccount(emailDetail, unencodedPassword);      
        }else{
        	throw new BillingMessageTemplateNotFoundException(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_USER);
        }
       }
    }

    private void generateKeyUsedForPasswordSalting(final AppUser appUser) {
        this.userRepository.save(appUser);
    }
    
}