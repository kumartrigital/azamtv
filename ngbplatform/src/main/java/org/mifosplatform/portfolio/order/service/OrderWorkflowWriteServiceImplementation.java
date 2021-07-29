package org.mifosplatform.portfolio.order.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.domain.Channel;
import org.mifosplatform.organisation.channel.serialization.ChannelCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceWriteplatformService;
import org.mifosplatform.portfolio.order.domain.OrderWorkflow;
import org.mifosplatform.portfolio.order.domain.OrderWorkflowRepository;
import org.mifosplatform.portfolio.order.serialization.OrderWorkFlowCommandFromApiJsonDeserializer;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

@Service
public class OrderWorkflowWriteServiceImplementation implements	OrderWorkflowWritePlaftormService {

	final private PlatformSecurityContext context;
	final private ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	final private ActiondetailsWritePlatformService actiondetailsWritePlatformService;
	final private ClientServiceWriteplatformService clientServiceWriteplatformService;
	final private ClientServiceRepository clientServiceRepository;
	final private OrderWorkFlowCommandFromApiJsonDeserializer apiJsonDeserializer;
    final private OrderWorkflowRepository orderWorkflowRepository;
    final private FromJsonHelper fromJsonHelper;
	
	@Autowired
	public OrderWorkflowWriteServiceImplementation(final PlatformSecurityContext context,
			final ActionDetailsReadPlatformService actionDetailsReadPlatformService, final ActiondetailsWritePlatformService actiondetailsWritePlatformService,
			final ClientServiceWriteplatformService clientServiceWriteplatformService, final ClientServiceRepository clientServiceRepository,
			final OrderWorkFlowCommandFromApiJsonDeserializer apiJsonDeserializer,final OrderWorkflowRepository orderWorkflowRepository,
			final FromJsonHelper fromJsonHelper){
		this.context = context;
		this.actionDetailsReadPlatformService = actionDetailsReadPlatformService;
		this.actiondetailsWritePlatformService = actiondetailsWritePlatformService;
		this.clientServiceWriteplatformService = clientServiceWriteplatformService;
		this.clientServiceRepository = clientServiceRepository;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.orderWorkflowRepository = orderWorkflowRepository;
		this.fromJsonHelper = fromJsonHelper;
	}
	
	@Override
	public CommandProcessingResult createOrderWorkflow(JsonCommand command){
		
		AppUser user = this.context.authenticatedUser();
		
		JSONObject statusObject = new JSONObject();
		String status = command.stringValueOfParameterName("status");
		Long clientId = command.longValueOfParameterNamed("clientId");
		Long clientServiceId = command.longValueOfParameterNamed("clientServiceId");
		String teamEmail = command.stringValueOfParameterName("teamEmail");
		switch(status){
		case "Operation Review" : 
			if(user.hasPermissionForAnyOf("CREATE_OPERATION")){
				final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM);
	            if(!actionDetailsDatas.isEmpty()){
	            	this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,clientId,clientId.toString(),null);
	            }
	            this.orderWorkflow(command);
	            ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
	            clientService.setStatus("Operation Review");
	            this.clientServiceRepository.save(clientService);
			
			}else{
				throw new PlatformDataIntegrityException("you are not allowed to perform this action", "you are not allowed to perform this action", "you are not allowed to perform this action");
			}
			
		case "Order Accepted" : 
			if(user.hasPermissionForAnyOf("CREATE_OPERATIONS")){
				final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM);
	            if(!actionDetailsDatas.isEmpty()){
	            	this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,clientId,clientId.toString(),teamEmail);
		        }
	            this.orderWorkflow(command);
	            ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
	            clientService.setStatus("Order Accepted");
	            this.clientServiceRepository.save(clientService);
			
			}else{
				throw new PlatformDataIntegrityException("you are not allowed to perform this action", "you are not allowed to perform this action", "you are not allowed to perform this action");
			}
			break;
		case "Surveying" : 
			if(user.hasPermissionForAnyOf("CREATE_SURVEYING")){
				final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM);
	            if(!actionDetailsDatas.isEmpty()){
	            	this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,clientId,clientId.toString(),null);
		        }
	            CommandProcessingResult result = this.orderWorkflow(command);
	            OrderWorkflow orderWorkflow = this.orderWorkflowRepository.findOne(result.getResourceId());
	            orderWorkflow.setStatus("Surveying");
	            this.orderWorkflowRepository.save(orderWorkflow); 
	            ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
	            clientService.setStatus("Order Accepted");
	            this.clientServiceRepository.save(clientService);
			
			}else{
				throw new PlatformDataIntegrityException("you are not allowed to perform this action", "you are not allowed to perform this action", "you are not allowed to perform this action");
			}
			
		case "Survey Completed" : 
			if(user.hasPermissionForAnyOf("CREATE_SURVEYING")){
				final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM);
	            if(!actionDetailsDatas.isEmpty()){
	            	this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,clientId,clientId.toString(),teamEmail);
		        }
	            this.orderWorkflow(command);
	            ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
	            clientService.setStatus("Survey Completed");
	            this.clientServiceRepository.save(clientService);
			
			}else{
				throw new PlatformDataIntegrityException("you are not allowed to perform this action", "you are not allowed to perform this action", "you are not allowed to perform this action");
			}
			break;
			
		case "Installation Assigned" : 
			if(user.hasPermissionForAnyOf("CREATE_INSTALLATIONS")){
				final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM);
	            if(!actionDetailsDatas.isEmpty()){
	            	this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,clientId,clientId.toString(),null);
		        }
	            CommandProcessingResult result = this.orderWorkflow(command);
	            OrderWorkflow orderWorkflow = this.orderWorkflowRepository.findOne(result.getResourceId());
	            orderWorkflow.setStatus("Installation Assigned");
	            this.orderWorkflowRepository.save(orderWorkflow); 
	            ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
	            clientService.setStatus("Survey Completed");
	            this.clientServiceRepository.save(clientService);
			
			}else{
				throw new PlatformDataIntegrityException("you are not allowed to perform this action", "you are not allowed to perform this action", "you are not allowed to perform this action");
			}
		/*case "Installation Completed":
			if(user.hasPermissionForAnyOf("CREATE_INSTALLATIONS")){
				final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM);
	            if(!actionDetailsDatas.isEmpty()){
	            	this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,clientId,clientId.toString(),teamEmail);
		        }
	            this.orderWorkflow(command);
	            ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
	            clientService.setStatus("Installation Completed");
	            this.clientServiceRepository.save(clientService);
			}else{
				throw new PlatformDataIntegrityException("", "", "");
			}
			break;*/
		
		case "Installation Completed" : 
			if(user.hasPermissionForAnyOf("CREATE_INSTALLATIONS")){
				final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM);
	            if(!actionDetailsDatas.isEmpty()){
	            	this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,clientId,clientId.toString(),teamEmail);
		        }
	            this.orderWorkflow(command);

	            ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
	            if(!clientService.getStatus().equalsIgnoreCase("ACTIVE")){
		            this.clientServiceWriteplatformService.createProvisioningService(clientId, clientServiceId);
		            clientService.setStatus("Provisioned");
		            this.clientServiceRepository.save(clientService);
	            }
			
			}else{
				throw new PlatformDataIntegrityException("you are not allowed to perform this action", "you are not allowed to perform this action", "you are not allowed to perform this action");
			}
			break;
		case "Reject" : 
			throw new PlatformDataIntegrityException("The reject has not been implemented yet", "The reject has not been implemented yet", "The reject has not been implemented yet");
			/*	final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_NOTIFY_TECHNICALTEAM);
	            if(!actionDetailsDatas.isEmpty()){
	            	this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,clientId,clientId.toString(),teamEmail);
		        }
	            this.orderWorkflow(command);
	            this.clientServiceWriteplatformService.createProvisioningService(clientId, clientServiceId);
	            ClientService clientService = this.clientServiceRepository.findOne(clientServiceId);
	            clientService.setStatus("Provisioned");
	            this.clientServiceRepository.save(clientService);
			*/
		
		default: 
			throw new PlatformDataIntegrityException("", "", "");
		}
		return new CommandProcessingResult((long)1);
	}
	
	@Override
	public CommandProcessingResult orderWorkflow(JsonCommand command) {
		try{
			context.authenticatedUser();
			apiJsonDeserializer.validateForCreate(command.json());
			OrderWorkflow orderWorkflow = OrderWorkflow.formJson(command);
			orderWorkflowRepository.saveAndFlush(orderWorkflow);
			return new CommandProcessingResultBuilder().withEntityId(orderWorkflow.getId()).build();
			
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}
	
	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
        
        throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }
	
	
}
