package org.mifosplatform.crm.ticketmaster.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;

import org.mifosplatform.crm.ticketmaster.command.TicketMasterCommand;
import org.mifosplatform.crm.ticketmaster.domain.OfficeTicket;
import org.mifosplatform.crm.ticketmaster.domain.OfficeTicketDetail;
import org.mifosplatform.crm.ticketmaster.domain.OfficeTicketDetailsRepository;
import org.mifosplatform.crm.ticketmaster.domain.OfficeTicketRepository;
import org.mifosplatform.crm.ticketmaster.domain.TicketDetail;
import org.mifosplatform.crm.ticketmaster.domain.TicketDetailsRepository;
import org.mifosplatform.crm.ticketmaster.domain.TicketMaster;
import org.mifosplatform.crm.ticketmaster.domain.TicketMasterRepository;
import org.mifosplatform.crm.ticketmaster.serialization.TicketMasterFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.documentmanagement.command.DocumentCommand;
import org.mifosplatform.infrastructure.documentmanagement.exception.DocumentManagementException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketMasterWritePlatformServiceImpl implements TicketMasterWritePlatformService{
	
	private PlatformSecurityContext context;
	private TicketMasterRepository repository;
	private TicketDetailsRepository ticketDetailsRepository;
	private TicketMasterFromApiJsonDeserializer fromApiJsonDeserializer;
	private TicketMasterRepository ticketMasterRepository;
	private TicketDetailsRepository detailsRepository;
	private final OrderWritePlatformService orderWritePlatformService;
	private final OfficeTicketRepository officeTicketRepository;
	private final OfficeTicketDetailsRepository officeTicketDetailsRepository;
	
	@Autowired
	public TicketMasterWritePlatformServiceImpl(final PlatformSecurityContext context,
			final TicketMasterRepository repository,final TicketDetailsRepository ticketDetailsRepository, 
			final TicketMasterFromApiJsonDeserializer fromApiJsonDeserializer,final TicketMasterRepository ticketMasterRepository,
			final TicketDetailsRepository detailsRepository,final OrderWritePlatformService orderWritePlatformService,
			final OfficeTicketRepository officeTicketRepository, final OfficeTicketDetailsRepository officeTicketDetailsRepository) {
		
		this.context = context;
		this.repository = repository;
		this.ticketDetailsRepository = ticketDetailsRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.ticketMasterRepository = ticketMasterRepository;
		this.detailsRepository = detailsRepository;
		this.orderWritePlatformService = orderWritePlatformService;
		this.officeTicketRepository=officeTicketRepository;
		this.officeTicketDetailsRepository=officeTicketDetailsRepository;
	}

	private void handleDataIntegrityIssues(final TicketMasterCommand command,
			final DataIntegrityViolationException dve) {
		
	}

	@Override
	public Long upDateTicketDetails(TicketMasterCommand ticketMasterCommand,
			DocumentCommand documentCommand, Long ticketId, InputStream inputStream, String ticketURL) {
		
	 	try {
		 String fileUploadLocation = FileUtils.generateFileParentDirectory(documentCommand.getParentEntityType(),
                 documentCommand.getParentEntityId());

         /** Recursively create the directory if it does not exist **/
         if (!new File(fileUploadLocation).isDirectory()) {
             new File(fileUploadLocation).mkdirs();
         }
         String fileLocation = null;
         if(documentCommand.getFileName() != null){
          fileLocation = FileUtils.saveToFileSystem(inputStream, fileUploadLocation, documentCommand.getFileName());
         }
         Long createdbyId = context.authenticatedUser().getId();
         
         TicketDetail detail = new TicketDetail(ticketId,ticketMasterCommand.getComments(),fileLocation,ticketMasterCommand.getAssignedTo(),createdbyId);
         /*TicketMaster master = new TicketMaster(ticketMasterCommand.getStatusCode(), ticketMasterCommand.getAssignedTo());*/
         TicketMaster ticketMaster = this.ticketMasterRepository.findOne(ticketId);
         ticketMaster.updateTicket(ticketMasterCommand);
         this.ticketMasterRepository.save(ticketMaster);
         ticketURL=ticketURL+"/"+ticketMaster.getId();//added
         this.ticketDetailsRepository.save(detail);
          
  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_EDIT_TICKET, ticketMaster.getClientId(), ticketMaster.getId().toString(), ticketURL);
  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM, ticketMaster.getClientId(), ticketMaster.getId().toString(), "UPDATE TICKET");
		
         return detail.getId();

	 	}
	 	catch (DataIntegrityViolationException dve) {
		handleDataIntegrityIssues(ticketMasterCommand, dve);
		return Long.valueOf(-1);
		
	 	} catch (IOException e) {
         throw new DocumentManagementException(documentCommand.getName());
	 	}
		
	}

	@Override
	public CommandProcessingResult closeTicket( final JsonCommand command) {
		TicketMaster ticketMaster = null;
		try {
			this.context.authenticatedUser();
			
			this.fromApiJsonDeserializer.validateForClose(command.json());
			String ticketURL = command.stringValueOfParameterNamed("ticketURL");
			ticketMaster = this.repository.findOne(command.entityId());
			ticketURL=ticketURL+"/"+ticketMaster.getId();//added 
			if (!ticketMaster.getStatus().equalsIgnoreCase("CLOSED")) {
				ticketMaster.closeTicket(command,this.context.authenticatedUser().getId());
				this.repository.save(ticketMaster);
			
		  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_CLOSE_TICKET, ticketMaster.getClientId(), ticketMaster.getId().toString(), ticketURL);
		  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM, ticketMaster.getClientId(), ticketMaster.getId().toString(), "CLOSE TICKET");
				
			} else {
				
			}
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssuesforJson(command, dve);
		}
		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(command.entityId()).withClientId(ticketMaster.getClientId()).build();
	}

	private void handleDataIntegrityIssuesforJson(final JsonCommand command,
			final DataIntegrityViolationException dve) {
		
	}

	@Override
	public String retrieveTicketProblems(final Long ticketId) {
		try {
			final TicketMaster master = this.repository.findOne(ticketId);
			final String description = master.getDescription();
			return description;
		}catch (final DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(null, dve);
			return "";
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult createTicketMaster(final JsonCommand command) {
		
		 try {
			 Long created = null;
			 SecurityContext context = SecurityContextHolder.getContext();
			 if (context.getAuthentication() != null) {
				 final AppUser appUser = this.context.authenticatedUser();
				 created = appUser.getId();
	        }else{
	        		created = new Long(0);
	        }	 
			 this.fromApiJsonDeserializer.validateForCreate(command.json());
			String ticketURL = command.stringValueOfParameterNamed("ticketURL");
			final TicketMaster ticketMaster = TicketMaster.fromJson(command);
			ticketMaster.setCreatedbyId(created);
			this.repository.saveAndFlush(ticketMaster);
			ticketURL=ticketURL+"/"+ticketMaster.getId(); //added
			final TicketDetail details = TicketDetail.fromJson(command);
			details.setAttachments(command.stringValueOfParameterNamed("fileLocation"));
			details.setTicketId(ticketMaster.getId());
			details.setCreatedbyId(created);
			this.detailsRepository.saveAndFlush(details);
			
			this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_CREATE_TICKET, command.getClientId(), ticketMaster.getId().toString(), ticketURL);
			
			this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM, command.getClientId(), ticketMaster.getId().toString(), "CREATE TICKET");
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(ticketMaster.getId()).withClientId(command.getClientId()).build();
		 } catch (DataIntegrityViolationException dve) {
			 	return new CommandProcessingResult(Long.valueOf(-1));
		   } catch (ParseException e) {
			 throw new PlatformDataIntegrityException("invalid.date.format", "invalid.date.format", "ticketDate","invalid.date.format");
		 	 }
	}
	
	@Override
	public CommandProcessingResult updateTicketMaster(JsonCommand command, Long ticketId) {
		
	 	try {
		 /*String fileUploadLocation = FileUtils.generateFileParentDirectory(documentCommand.getParentEntityType(),
                 documentCommand.getParentEntityId());

         *//** Recursively create the directory if it does not exist **//*
         if (!new File(fileUploadLocation).isDirectory()) {
             new File(fileUploadLocation).mkdirs();
         }
         String fileLocation = null;
         if(documentCommand.getFileName() != null){
          fileLocation = FileUtils.saveToFileSystem(inputStream, fileUploadLocation, documentCommand.getFileName());
         }*/
         Long createdbyId = context.authenticatedUser().getId();
         String comments = command.stringValueOfParameterNamed("comments");
         Long assignedTo= command.longValueOfParameterNamed("assignedTo");
         String ticketURL = command.stringValueOfParameterNamed("ticketURL");
         String teamCode = command.stringValueOfParameterName("teamId");
         Long teamUserId = command.longValueOfParameterNamed("teamUserId");
         TicketDetail detail = new TicketDetail(ticketId,comments,null,assignedTo,createdbyId,teamCode,teamUserId);
         /*TicketMaster master = new TicketMaster(ticketMasterCommand.getStatusCode(), ticketMasterCommand.getAssignedTo());*/
         TicketMaster ticketMaster = this.ticketMasterRepository.findOne(ticketId);
         //ticketMaster.update(command);
         final Map<String, Object> changes = ticketMaster.update(command);
         this.ticketMasterRepository.save(ticketMaster);
         ticketURL=ticketURL+"/"+ticketMaster.getId(); //added
         this.ticketDetailsRepository.save(detail);
          
  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_EDIT_TICKET, ticketMaster.getClientId(), ticketMaster.getId().toString(), ticketURL);
  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM, ticketMaster.getClientId(), ticketMaster.getId().toString(), "UPDATE TICKET");
		
         //return detail.getId();
  		return new CommandProcessingResultBuilder() //
 		       .withCommandId(command.commandId()) //
 		       .withEntityId(ticketId) //
 		       .with(changes) //
 		       .build();

	 	}
	 	catch (DataIntegrityViolationException dve) {
		handleDataIntegrityIssue(command, dve);
	    return new CommandProcessingResult(Long.valueOf(-1));
		
	 	}
		
	}
	private void handleDataIntegrityIssue(final JsonCommand command,
			final DataIntegrityViolationException dve) {
		
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createOfficeTicket(final JsonCommand command) {
		
		 try {
			 Long created = null;
			 SecurityContext context = SecurityContextHolder.getContext();
			 if (context.getAuthentication() != null) {
				 final AppUser appUser = this.context.authenticatedUser();
				 created = appUser.getId();
	        }else{
	        		created = new Long(0);
	        }	 
			 this.fromApiJsonDeserializer.validateForOfficeCreate(command.json());
			String ticketURL = command.stringValueOfParameterNamed("ticketURL");
			final OfficeTicket officeTicket = OfficeTicket.fromJson(command);
			officeTicket.setCreatedbyId(created);
			
			this.officeTicketRepository.saveAndFlush(officeTicket);
			final OfficeTicketDetail details = OfficeTicketDetail.fromJson(command);
			details.setAttachments(command.stringValueOfParameterNamed("fileLocation"));
			details.setTicketId(officeTicket.getId());
			details.setCreatedbyId(created);
			this.officeTicketDetailsRepository.saveAndFlush(details);
			
			this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_CREATE_TICKET, command.getClientId(), officeTicket.getId().toString(), ticketURL);
			
			this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM, command.getClientId(), officeTicket.getId().toString(), "CREATE TICKET");
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(officeTicket.getId()).build();
		 } catch (DataIntegrityViolationException dve) {
			 	return new CommandProcessingResult(Long.valueOf(-1));
		   } catch (ParseException e) {
			 throw new PlatformDataIntegrityException("invalid.date.format", "invalid.date.format", "ticketDate","invalid.date.format");
		 	 }
	}
	
	@Override
	public CommandProcessingResult updateOfficeTicket(JsonCommand command, Long ticketId) {
		
	 	try {
		
         Long createdbyId = context.authenticatedUser().getId();
         String comments = command.stringValueOfParameterNamed("comments");
         Long assignedTo= command.longValueOfParameterNamed("assignedTo");
         String ticketURL = command.stringValueOfParameterNamed("ticketURL");
         
         OfficeTicketDetail detail = new OfficeTicketDetail(ticketId,comments,null,assignedTo,createdbyId);
         /*TicketMaster master = new TicketMaster(ticketMasterCommand.getStatusCode(), ticketMasterCommand.getAssignedTo());*/
         OfficeTicket officeTicket = this.officeTicketRepository.findOne(ticketId);
         //ticketMaster.update(command);
         AppUser currentUser = this.context.authenticatedUser();
         final Map<String, Object> changes = officeTicket.update(command, currentUser.getId());
         this.officeTicketRepository.save(officeTicket);
         this.officeTicketDetailsRepository.save(detail);
          
  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_EDIT_TICKET, officeTicket.getOfficeId(), officeTicket.getId().toString(), ticketURL);
  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM, officeTicket.getOfficeId(), officeTicket.getId().toString(), "UPDATE TICKET");
		
         //return detail.getId();
  		return new CommandProcessingResultBuilder() //
 		       .withCommandId(command.commandId()) //
 		       .withEntityId(ticketId) //
 		       .with(changes) //
 		       .build();

	 	}
	 	catch (DataIntegrityViolationException dve) {
		handleDataIntegrityIssue(command, dve);
	    return new CommandProcessingResult(Long.valueOf(-1));
		
	 	}
		
	}
	
	@Override
	public CommandProcessingResult closeOfficeTicket( final JsonCommand command) {
		OfficeTicket officeTicket = null;
		try {
			this.context.authenticatedUser();
			
			this.fromApiJsonDeserializer.validateForClose(command.json());
			String ticketURL = command.stringValueOfParameterNamed("ticketURL");
			officeTicket = this.officeTicketRepository.findOne(command.entityId());
			
			if (!officeTicket.getStatus().equalsIgnoreCase("CLOSED")) {
				officeTicket.closeTicket(command,this.context.authenticatedUser().getId());
				this.officeTicketRepository.save(officeTicket);
		  		 
		  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_CLOSE_TICKET, officeTicket.getOfficeId(), officeTicket.getId().toString(), ticketURL);
		  		this.orderWritePlatformService.processNotifyMessages(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM, officeTicket.getOfficeId(), officeTicket.getId().toString(), "CLOSE TICKET");
				
			} else {
				
			}
		}catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssuesforJson(command, dve);
		}
		return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(command.entityId()).build();
	}


}