package org.mifosplatform.billing.selfcare.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.loginhistory.domain.LoginHistory;
import org.mifosplatform.billing.loginhistory.domain.LoginHistoryRepository;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.billing.selfcare.domain.SelfCareTemporary;
import org.mifosplatform.billing.selfcare.domain.SelfCareTemporaryRepository;
import org.mifosplatform.billing.selfcare.exception.SelfCareAlreadyVerifiedException;
import org.mifosplatform.billing.selfcare.exception.SelfCareEmailIdDuplicateException;
import org.mifosplatform.billing.selfcare.exception.SelfCareTemporaryGeneratedKeyNotFoundException;
import org.mifosplatform.billing.selfcare.exception.SelfcareEmailIdNotFoundException;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.security.service.RandomPasswordGenerator;
import org.mifosplatform.organisation.message.domain.BillingMessage;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateConstants;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.exception.BillingMessageTemplateNotFoundException;
import org.mifosplatform.organisation.message.service.MessagePlatformEmailService;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.client.exception.ClientStatusException;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.processscheduledjobs.service.SheduleJobWritePlatformService;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisionActions;
import org.mifosplatform.provisioning.provsionactions.domain.ProvisioningActionsRepository;
import org.mifosplatform.workflow.eventaction.data.OrderNotificationData;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;




@Service
public class SelfCareWritePlatformServiceImp implements SelfCareWritePlatformService{
	
	private PlatformSecurityContext context;
	private ClientRepository clientRepository;
	private SelfCareRepository selfCareRepository;
	private final LoginHistoryRepository loginHistoryRepository;
	private MessagePlatformEmailService messagePlatformEmailService;
	private SelfCareReadPlatformService selfCareReadPlatformService;
	private SelfCareTemporaryRepository selfCareTemporaryRepository;
	private final BillingMessageTemplateRepository billingMessageTemplateRepository;
	private SelfCareCommandFromApiJsonDeserializer selfCareCommandFromApiJsonDeserializer;
	private final BillingMessageRepository messageDataRepository;
	private final ConfigurationRepository configurationRepository;
	private final static Logger logger = (Logger) LoggerFactory.getLogger(SelfCareWritePlatformServiceImp.class);
	private BillingMessageTemplate createSelfcareMessageDetails = null;
	private BillingMessageTemplate registerSelfcareMessageDetails = null;
	private BillingMessageTemplate newSelfcarePasswordMessageDetails = null;
	private BillingMessageTemplate createSelfcareMessageDetailsForSMS = null;
	private BillingMessageTemplate newSelfcarePasswordMessageDetailsForSMS = null;
	private BillingMessageTemplate createOTPMessageDetails ;
	private final ProvisioningActionsRepository provisioningActionsRepository;
	private final ProcessRequestRepository processRequestRepository;
	private final EventActionReadPlatformService eventActionReadPlatformService;
	private final OrderWritePlatformService orderWritePlatformService;
	private final SheduleJobWritePlatformService sheduleJobWritePlatformService;

	@Autowired
	public SelfCareWritePlatformServiceImp(final PlatformSecurityContext context, 
			final SelfCareRepository selfCareRepository, 
		    final SelfCareCommandFromApiJsonDeserializer selfCareCommandFromApiJsonDeserializer,
		    final SelfCareReadPlatformService selfCareReadPlatformService, 
			final SelfCareTemporaryRepository selfCareTemporaryRepository,
			final BillingMessageTemplateRepository billingMessageTemplateRepository,
			final MessagePlatformEmailService messagePlatformEmailService,
			final ClientRepository clientRepository,
			final LoginHistoryRepository loginHistoryRepository,
			final BillingMessageRepository messageDataRepository,
			final ConfigurationRepository configurationRepository,
			final ProvisioningActionsRepository provisioningActionsRepository,
			final ProcessRequestRepository processRequestRepository,
			final EventActionReadPlatformService eventActionReadPlatformService,
			final OrderWritePlatformService orderWritePlatformService,
			final SheduleJobWritePlatformService sheduleJobWritePlatformService) {
		
		this.context = context;
		this.selfCareRepository = selfCareRepository;
		this.selfCareCommandFromApiJsonDeserializer = selfCareCommandFromApiJsonDeserializer;
		this.selfCareReadPlatformService = selfCareReadPlatformService;
		this.selfCareTemporaryRepository = selfCareTemporaryRepository;
		this.messagePlatformEmailService = messagePlatformEmailService;
		this.billingMessageTemplateRepository = billingMessageTemplateRepository;
		this.messagePlatformEmailService= messagePlatformEmailService;
		this.clientRepository=clientRepository;
		this.loginHistoryRepository=loginHistoryRepository;
		this.messageDataRepository = messageDataRepository;
		this.configurationRepository = configurationRepository;
		this.provisioningActionsRepository = provisioningActionsRepository;		
		this.processRequestRepository = processRequestRepository;
		this.eventActionReadPlatformService = eventActionReadPlatformService;
		this.orderWritePlatformService = orderWritePlatformService;
		this.sheduleJobWritePlatformService=sheduleJobWritePlatformService;
	
	}
	
	@Override
	public CommandProcessingResult createSelfCare(JsonCommand command) {
		
		try{
			this.context.authenticatedUser();
			this.selfCareCommandFromApiJsonDeserializer.validateForCreate(command);
			SelfCare selfCare = SelfCare.fromJson(command);
			String password = command.stringValueOfParameterNamed("password");
			boolean mailnotification = command.booleanPrimitiveValueOfParameterNamed("mailNotification");
			
			if(null == selfCare.getClientId() || selfCare.getClientId()<=0L){
				throw new PlatformDataIntegrityException("client does not exist", "client not registered","clientId", "client is null ");
			}
			System.out.println("selfcarewriteplatform password::"+password);
			if(password == null || password.isEmpty()){
				selfCare.setPassword(new RandomPasswordGenerator(8).generate().toString());
			}else{
				selfCare.setPassword(password);
			}
			selfCare.setFirstTimeLogInRemaining(1);
			System.out.println("selfcare object::"+selfCare.toString());
			this.selfCareRepository.save(selfCare);
			
			
			
			/*The below code is Notification Message while Creating Self User*/
			
			
			Client client = this.clientRepository.findOne(selfCare.getClientId());
			
			OrderNotificationData orderData = this.eventActionReadPlatformService.retrieveNotifyDetails(client.getId(), null);
			
			Configuration configuration = this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_SMS);
			
			if(null != configuration && configuration.isEnabled()) {
				
				if(null == createSelfcareMessageDetailsForSMS){
					createSelfcareMessageDetailsForSMS = this.billingMessageTemplateRepository.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE);
				}
				
				if (createSelfcareMessageDetailsForSMS != null) {
					
					String subject = createSelfcareMessageDetailsForSMS.getSubject();
					String body = createSelfcareMessageDetailsForSMS.getBody();
					body = body.replace("<PARAM1>", selfCare.getUserName().trim());
					body = body.replace("<PARAM2>", selfCare.getPassword().trim());
					
					BillingMessage billingMessage = new BillingMessage(null, body, null, orderData.getOfficeEmail(), 
							orderData.getClientPhone(), subject, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, 
							createSelfcareMessageDetailsForSMS, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

					this.messageDataRepository.save(billingMessage);

				} else throw new BillingMessageTemplateNotFoundException(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_CREATE_SELFCARE);
			}
			
			if (mailnotification) {

				if(null == createSelfcareMessageDetails){
					createSelfcareMessageDetails = this.billingMessageTemplateRepository.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_SELFCARE);
				}
				
				if (createSelfcareMessageDetails != null) {
					
					String subject = createSelfcareMessageDetails.getSubject();
					String body = createSelfcareMessageDetails.getBody();
					String footer = createSelfcareMessageDetails.getFooter();
					String header = createSelfcareMessageDetails.getHeader().replace("<PARAM1>", client.getDisplayName()==null || client.getDisplayName().isEmpty()?client.getFirstname(): client.getDisplayName() + ",");
					body = body.replace("<PARAM2>", selfCare.getUserName().trim());
					body = body.replace("<PARAM3>", selfCare.getPassword().trim());

					BillingMessage billingMessage = new BillingMessage(header, body, footer, 
							orderData.getOfficeEmail(), client.getEmail(), subject,
							BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, createSelfcareMessageDetails, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

					this.messageDataRepository.save(billingMessage);

				} else throw new BillingMessageTemplateNotFoundException(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_CREATE_SELFCARE);
				
			}	
			
			this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM, selfCare.getClientId(), null, "Client Creation");
			
			return new CommandProcessingResultBuilder().withEntityId(selfCare.getId()).withClientId(selfCare.getClientId()).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			throw new PlatformDataIntegrityException("duplicate.username", "duplicate.username","duplicate.username", "duplicate.username");
		}catch(EmptyResultDataAccessException emp){
			throw new PlatformDataIntegrityException("empty.result.set", "empty.result.set");
		}
		
		
	}
	
	@Override
	public CommandProcessingResult createSelfCareUDPassword(JsonCommand command) {
		SelfCare selfCare = null;
		Long clientId = null;
		String ipAddress=command.stringValueOfParameterNamed("ipAddress");
		String session=command.stringValueOfParameterNamed("");
		Long loginHistoryId=null;
		
		try{
			context.authenticatedUser();
			selfCareCommandFromApiJsonDeserializer.validateForCreateUDPassword(command);
			selfCare = SelfCare.fromJsonODP(command);
			try{
			clientId = selfCareReadPlatformService.getClientId(selfCare.getUniqueReference());
			if(clientId == null || clientId <= 0 ){
				throw new PlatformDataIntegrityException("client does not exist", "this user is not registered","clientId", "client is null ");
			}
			selfCare.setClientId(clientId);

			selfCareRepository.save(selfCare);
			String username=selfCare.getUserName();
			LoginHistory loginHistory=new LoginHistory(ipAddress,null,session,DateUtils.getDateOfTenant(),null,username,"ACTIVE");
    		this.loginHistoryRepository.save(loginHistory);
    		loginHistoryId=loginHistory.getId();
			}
			catch(EmptyResultDataAccessException dve){
				throw new PlatformDataIntegrityException("invalid.account.details","invalid.account.details","this user is not registered");
			}
			
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			throw new PlatformDataIntegrityException("duplicate.email", "duplicate.email","duplicate.email", "duplicate.email");
		}catch(EmptyResultDataAccessException emp){
			throw new PlatformDataIntegrityException("empty.result.set", "empty.result.set");
		}
		
		return new CommandProcessingResultBuilder().withEntityId(loginHistoryId).withClientId(clientId).build();
	}
		
	@Override
	public CommandProcessingResult updateSelfCareUDPassword(JsonCommand command) {
		   SelfCare selfCare=null;
		   context.authenticatedUser();
		   selfCareCommandFromApiJsonDeserializer.validateForUpdateUDPassword(command);
		   String email=command.stringValueOfParameterNamed("uniqueReference");
		   String password=command.stringValueOfParameterNamed("password");
		   selfCare=this.selfCareRepository.findOneByEmail(email);
		   if(selfCare==null){
			   throw new ClientNotFoundException(email);
		   }
		   selfCare.setPassword(password);
		   this.selfCareRepository.save(selfCare);
		   return new CommandProcessingResultBuilder().withEntityId(selfCare.getClientId()).build();
	}	
	
	@Override
	public CommandProcessingResult forgotSelfCareUDPassword(JsonCommand command) {
		SelfCare selfCare=null;
		context.authenticatedUser();
		selfCareCommandFromApiJsonDeserializer.validateForForgotUDPassword(command);
		String email=command.stringValueOfParameterNamed("uniqueReference");
		selfCare=this.selfCareRepository.findOneByEmail(email);
		if(selfCare == null){
			throw new ClientNotFoundException(email);
		}
		String password=selfCare.getPassword();
		Client client= this.clientRepository.findOne(selfCare.getClientId());
		String body="Dear "+client.getDisplayName()+","+"\n"+"Your login information is mentioned below."+"\n"+"Email Id : "+email+"\n"+"Password :"+password+"\n"+"Thanks";
		String subject="Login Information";
		messagePlatformEmailService.sendGeneralMessage(email, body, subject);
		return new CommandProcessingResult(selfCare.getClientId());
	}

	private void handleDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
		Throwable realCause = dve.getMostSpecificCause();
		   logger.error(dve.getMessage(), dve);
		         if (realCause.getMessage().contains("username") && 
		            command.stringValueOfParameterNamed("uniqueReference").equalsIgnoreCase(command.stringValueOfParameterNamed("userName"))){
		          throw new PlatformDataIntegrityException("validation.error.msg.selfcare.duplicate.email", "email: " + command.stringValueOfParameterNamed("uniqueReference")+ " already exists", "email", command.stringValueOfParameterNamed("uniqueReference"));
		          
		          
		         }else if (realCause.getMessage().contains("unique_reference")){
		          throw new PlatformDataIntegrityException("validation.error.msg.selfcare.duplicate.email", "email: " + command.stringValueOfParameterNamed("uniqueReference")+ " already exists", "email", command.stringValueOfParameterNamed("uniqueReference"));
		          
		         }else if(realCause.getMessage().contains("username")){
		          
		          throw new PlatformDataIntegrityException("validation.error.msg.selfcare.duplicate.userName", "User Name: " + command.stringValueOfParameterNamed("userName")+ " already exists", "userName", command.stringValueOfParameterNamed("userName"));
		         }

	}
	
	@Override
	public CommandProcessingResult updateClientStatus(JsonCommand command,Long entityId) {
            try{
            	
            	this.context.authenticatedUser();
            	String status=command.stringValueOfParameterNamed("status");
            	SelfCare client=this.selfCareRepository.findOneByClientId(entityId);
            	if(client == null){
            		throw new ClientNotFoundException(entityId);
            	}
            	if(status.equalsIgnoreCase("ACTIVE")){
            	
            		if(status.equals(client.getStatus())){
            			throw new ClientStatusException(entityId);
            		}
            	}
            	client.setStatus(status);
            	this.selfCareRepository.save(client);
            	return new CommandProcessingResult(Long.valueOf(entityId));
            	
            }catch(DataIntegrityViolationException dve){
            	handleDataIntegrityIssues(command, dve);
            	return new CommandProcessingResult(Long.valueOf(-1));
            }

	}

	@Override
	public CommandProcessingResult registerSelfCare(JsonCommand command) {
		
		//SelfCareTemporary selfCareTemporary = null;
		Long clientId = 0L;
		try {
			context.authenticatedUser();
			selfCareCommandFromApiJsonDeserializer.validateForCreate(command);
			String uniqueReference = command.stringValueOfParameterNamed("username");
			String returnUrl = command.stringValueOfParameterNamed("returnUrl");
			SelfCare repository = selfCareRepository.findOneByEmail(uniqueReference);
			
			if (null == repository) {

				String generatedKey =null;
				
				/*String unencodedPassword = RandomStringUtils.randomAlphanumeric(27);
				selfCareTemporary.setGeneratedKey(unencodedPassword);*/
				
				
				
				SelfCareTemporary selfCaretemporary = this.selfCareTemporaryRepository.findOneByEmailId(uniqueReference);

				if(command.hasParameter("otp")){
					String otp = command.stringValueOfParameterNamed("otp");
					
					if(!otp.equals(selfCaretemporary.getotp())){
						throw new PlatformDataIntegrityException("Incorrect OTP", "Incorrect OTP","Incorrect OTP", "Incorrect OTP");
					}
					final Map<String, Object> changes = selfCaretemporary.update(command);
					generatedKey = selfCaretemporary.getGeneratedKey() + BillingMessageTemplateConstants.SELFCARE_REGISTRATION_CONSTANT;
					uniqueReference = selfCaretemporary.getUserName();
					if(!changes.isEmpty()){
						   this.selfCareTemporaryRepository.saveAndFlush(selfCaretemporary);
					   }
				}else{
					generatedKey = command.stringValueOfParameterName("generatedKey");
					selfCaretemporary = new SelfCareTemporary(uniqueReference,generatedKey);
					this.selfCareTemporaryRepository.save(selfCaretemporary);
				}
				
				
				

			/*	if(null == registerSelfcareMessageDetails){
					registerSelfcareMessageDetails = this.billingMessageTemplateRepository.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SELFCARE_REGISTER);
				}
			*/	 
/*				if (registerSelfcareMessageDetails != null) {
					
					String subject = registerSelfcareMessageDetails.getSubject();
					String body = registerSelfcareMessageDetails.getBody();
					String header = registerSelfcareMessageDetails.getHeader() + ",";
					String footer = registerSelfcareMessageDetails.getFooter();

					body = body.replace("<PARAM1>", returnUrl + generatedKey);
					
					BillingMessage billingMessage = new BillingMessage(header, body, footer, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_EMAIL_FROM,
							uniqueReference, subject, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS,
							registerSelfcareMessageDetails, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);

					this.messageDataRepository.save(billingMessage);
					this.sheduleJobWritePlatformService.processNotify(billingMessage.getId());
					 
					return new CommandProcessingResultBuilder().withClientId(clientId).withResourceIdAsString(returnUrl + generatedKey).build();

				} else throw new BillingMessageTemplateNotFoundException(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SELFCARE_REGISTER);
*/
				return new CommandProcessingResultBuilder().withClientId(clientId).withResourceIdAsString(returnUrl + generatedKey).build();

			} else throw new SelfCareEmailIdDuplicateException(uniqueReference);

		} catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			throw new PlatformDataIntegrityException("duplicate.username", "duplicate.username","duplicate.username", "duplicate.username");
		} catch(EmptyResultDataAccessException emp){
			throw new PlatformDataIntegrityException("empty.result.set", "empty.result.set");
		}
	}

	@Override
	public CommandProcessingResult selfCareEmailVerification(JsonCommand command) {
	
		try{
		
			this.context.authenticatedUser();
			this.selfCareCommandFromApiJsonDeserializer.validateForCreate(command);
			
			String verificationKey = command.stringValueOfParameterNamed("verificationKey");
			String uniqueReference = command.stringValueOfParameterNamed("uniqueReference");
			
			SelfCareTemporary selfCareTemporary = this.selfCareTemporaryRepository.findOneByGeneratedKey(verificationKey,uniqueReference);

			if(null == selfCareTemporary){				
				throw new SelfCareTemporaryGeneratedKeyNotFoundException(verificationKey,uniqueReference);				
			} else {
				if (selfCareTemporary.getStatus().equalsIgnoreCase("INACTIVE")
						|| selfCareTemporary.getStatus().equalsIgnoreCase("PENDING"))
					selfCareTemporary.setStatus("PENDING");
				else throw new SelfCareAlreadyVerifiedException(verificationKey);
			}
			
			return new CommandProcessingResultBuilder().withEntityId(selfCareTemporary.getId()).withClientId(0L).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			throw new PlatformDataIntegrityException("duplicate.username", "duplicate.username","duplicate.username", "duplicate.username");
		}catch(EmptyResultDataAccessException emp){
			throw new PlatformDataIntegrityException("empty.result.set", "empty.result.set");
		}
		
	}

	@Override
	public CommandProcessingResult generateNewSelfcarePassword(JsonCommand command) {
		
		try{
			this.context.authenticatedUser();
			this.selfCareCommandFromApiJsonDeserializer.validateForCreate(command);
			String uniqueReference = command.stringValueOfParameterNamed("uniqueReference");
			Client clientDetails = this.clientRepository.findwithEmail(uniqueReference);
			
			SelfCare selfCare =selfCareRepository.findOneByAccountNo(clientDetails.getAccountNo());
			
			if(selfCare == null){				
				throw new SelfcareEmailIdNotFoundException(uniqueReference);			
			}else{		
				String generatedKey = RandomStringUtils.randomAlphabetic(10);	
				selfCare.setPassword(generatedKey);
				
				Client client = this.clientRepository.findOne(selfCare.getClientId());
				Configuration configuration = this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_SMS);
				
				if(null != configuration && configuration.isEnabled()) {
					
					if(null == newSelfcarePasswordMessageDetailsForSMS){
						newSelfcarePasswordMessageDetailsForSMS = this.billingMessageTemplateRepository.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NEW_SELFCARE_PASSWORD);
					}
					
					if (newSelfcarePasswordMessageDetailsForSMS != null) {
						
						String subject = newSelfcarePasswordMessageDetailsForSMS.getSubject();
						String body = newSelfcarePasswordMessageDetailsForSMS.getBody();
						body = body.replace("<PARAM1>", selfCare.getUserName().trim());
						body = body.replace("<PARAM2>", selfCare.getPassword().trim());
						
						BillingMessage billingMessage = new BillingMessage(null, body, null, 
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_EMAIL_FROM, client.getPhone(), subject,
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, newSelfcarePasswordMessageDetailsForSMS, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);
						
						this.messageDataRepository.save(billingMessage);

					} else throw new BillingMessageTemplateNotFoundException(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_NEW_SELFCARE_PASSWORD);
				}
				
				if(null == newSelfcarePasswordMessageDetails){
					newSelfcarePasswordMessageDetails =this.billingMessageTemplateRepository.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NEW_SELFCARE_PASSWORD);
				}
				
				if(newSelfcarePasswordMessageDetails != null){
				String subject = newSelfcarePasswordMessageDetails.getSubject();
				String body = newSelfcarePasswordMessageDetails.getBody();
				String footer = newSelfcarePasswordMessageDetails.getFooter();
				String header = newSelfcarePasswordMessageDetails.getHeader().replace("<PARAM1>", client.getDisplayName()==null || client.getDisplayName().isEmpty()?client.getFirstname(): client.getDisplayName() + ",");
				body = body.replace("<PARAM2>", selfCare.getUserName().trim());
				body = body.replace("<PARAM3>", generatedKey);
				
				BillingMessage billingMessage = new BillingMessage(header, body, footer, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_EMAIL_FROM, client.getEmail(),
						subject, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, newSelfcarePasswordMessageDetails, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
				
				this.messageDataRepository.save(billingMessage);
				
				} else throw new BillingMessageTemplateNotFoundException(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_NEW_SELFCARE_PASSWORD);
				
			}
			
			return new CommandProcessingResultBuilder().withEntityId(selfCare.getId()).withClientId(selfCare.getClientId()).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			throw new PlatformDataIntegrityException("duplicate.username", "duplicate.username","duplicate.username", "duplicate.username");
		}catch(EmptyResultDataAccessException emp){
			throw new PlatformDataIntegrityException("empty.result.set", "empty.result.set");
		}
		
		
	}

	@Override
	public CommandProcessingResult selfcareChangePassword(JsonCommand command) {
		
		try{
			//first tim elogin=0
			this.context.authenticatedUser();
			this.selfCareCommandFromApiJsonDeserializer.validateForCreate(command);
			String uniqueReference = command.stringValueOfParameterNamed("uniqueReference");
			String userName = command.stringValueOfParameterNamed("userName");
			String password = command.stringValueOfParameterNamed("password");
			SelfCare selfCare =selfCareRepository.findOneByEmail(uniqueReference);
			if(selfCare == null){				
				throw new SelfcareEmailIdNotFoundException(uniqueReference);			
			}
			String existingUserName = selfCare.getUserName();
			String existingPassword = selfCare.getPassword();
			
				/*if(command.parameterExists("userName")){
					String userName = command.stringValueOfParameterNamed("userName");
					selfCare.setUserName(userName);
				}
				selfCare.setPassword(password);
				this.selfCareRepository.save(selfCare);
				*/
				
				if((userName != null && password != null) && (!userName.isEmpty() && !password.isEmpty()) &&
                		((existingUserName.equalsIgnoreCase(userName)) && (!existingPassword.equalsIgnoreCase(password)))){				
                	
                	selfCare.setUserName(userName);
                	selfCare.setPassword(password);
                	selfCare.setFirstTimeLogInRemaining(0);
    				this.selfCareRepository.save(selfCare);
               
    				ProvisionActions provisionActions=this.provisioningActionsRepository.findOneByProvisionType(ProvisioningApiConstants.PROV_EVENT_Change_CREDENTIALS);
    				if(provisionActions.getIsEnable() == 'Y'){
    					JSONObject object = new JSONObject();
    					try {
    						object.put("newUserName", userName);
    						object.put("newPassword", password);
    						object.put("existingUserName", existingUserName);
    						object.put("existingPassword", existingPassword);
    					} catch (JSONException e) {
    						e.printStackTrace();
    					}
    					ProcessRequest processRequest = new ProcessRequest(Long.valueOf(0), selfCare.getClientId(), Long.valueOf(0),
							 provisionActions.getProvisioningSystem(),provisionActions.getAction(), 'N', 'N');

    					ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(Long.valueOf(0),
							 Long.valueOf(0), object.toString(), "Recieved",
							 null, new Date(), null, null, null, 'N', provisionActions.getAction(), null);

    					processRequest.add(processRequestDetails);
    					this.processRequestRepository.save(processRequest);
					
    				}
				}
			
			
			return new CommandProcessingResultBuilder().withEntityId(selfCare.getId()).withClientId(selfCare.getClientId()).build();
			
		} catch(EmptyResultDataAccessException emp){
			throw new PlatformDataIntegrityException("empty.result.set", "empty.result.set");
		}
		
	}
	
	@Override
	public CommandProcessingResult CreateOTP(String jsonRequestBody) {
		JSONObject obj;
		try {
			
			 int randomPin=(int)(Math.random()*9000)+1000;
		     String otp=String.valueOf(randomPin);
			 obj = new JSONObject(jsonRequestBody);
			 String username=obj.optString("username");
			 BillingMessage billingMessage = null;
			
			   Configuration configuration = this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_SMS);
			   if(null != configuration && configuration.isEnabled()) {
			 
				  /*if(null == createOTPMessageDetailsForSMS){
					 createOTPMessageDetailsForSMS = this.billingMessageTemplateRepository.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_Send_OTP);
					}
				 
				    if (createOTPMessageDetailsForSMS != null) {
					   
					    String subject = createOTPMessageDetailsForSMS.getSubject();
						String body = createOTPMessageDetailsForSMS.getBody();
						body = body.replace("<PARAM1>", otp);
					   
						BillingMessage billingMessage = new BillingMessage(null, body, null, 
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_EMAIL_FROM, client.getPhone(), subject,
								BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, createOTPMessageDetailsForSMS, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);

						       this.messageDataRepository.save(billingMessage);
				   }*/
				
			    }

			   SelfCareTemporary selfCareTemporary = this.selfCareTemporaryRepository.findOneByEmailId(username);
			   String generatedKey = null;
			if(selfCareTemporary!=null){
				selfCareTemporary.setotp(otp);
			}else{
				   generatedKey = RandomStringUtils.randomAlphanumeric(27);
				   selfCareTemporary = new SelfCareTemporary(otp, username,generatedKey);
			}
			   this.selfCareTemporaryRepository.save(selfCareTemporary);
		
			   if(null == createOTPMessageDetails){
				createOTPMessageDetails = this.billingMessageTemplateRepository.findByTemplateDescription(BillingMessageTemplateConstants.MESSAGE_TEMPLATE_Send_OTP);
			   }
			 
			 if (createOTPMessageDetails != null) {
		
				String subject = createOTPMessageDetails.getSubject();
				String body = createOTPMessageDetails.getBody();
				String footer = createOTPMessageDetails.getFooter();
				String header = createOTPMessageDetails.getHeader();
				body = body.replace("<PARAM1>",otp);
				
				
				final Pattern PHONE_REGEX = Pattern.compile("^[0-9]{10}$");
			    final Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
				if(EMAIL_REGEX.matcher(username).matches()) {
				
				 billingMessage = new BillingMessage(header, body, footer, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_EMAIL_FROM, username,
						subject, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, createOTPMessageDetails, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_MESSAGE_TYPE, null);
						
				}
				else if(PHONE_REGEX.matcher(username).matches()) {
					billingMessage = new BillingMessage(header, body, footer, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_EMAIL_FROM, username,
							subject, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_STATUS, createOTPMessageDetails, BillingMessageTemplateConstants.MESSAGE_TEMPLATE_SMS_TYPE, null);
				}
			 }
			 this.messageDataRepository.save(billingMessage);
			// this.sheduleJobWritePlatformService.processNotify(billingMessage.getId());
			 Map<String,Object> outputMap = new HashMap<String, Object>();
			 outputMap.put("otp", otp);
			 outputMap.put("generatedKey", generatedKey);
			 return new CommandProcessingResultBuilder().with(outputMap).build();
			   
		 }  catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			   return null;
		}
		
	  }
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
