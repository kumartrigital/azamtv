package org.mifosplatform.crm.clientprospect.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.JSONArray;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.domain.ClientProspect;
import org.mifosplatform.crm.clientprospect.domain.ClientProspectJpaRepository;
import org.mifosplatform.crm.clientprospect.domain.ProspectDetail;
import org.mifosplatform.crm.clientprospect.domain.ProspectDetailJpaRepository;
import org.mifosplatform.crm.clientprospect.serialization.ClientProspectCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepository;
import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.domain.Address;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientProspectWritePlatformServiceImp implements
		ClientProspectWritePlatformService {

	private final static Logger LOGGER = (Logger) LoggerFactory
			.getLogger(ClientProspectWritePlatformServiceImp.class);

	private final PlatformSecurityContext context;
	private final ClientProspectJpaRepository clientProspectJpaRepository;
	private final ProspectDetailJpaRepository prospectDetailJpaRepository;
	private final ClientProspectCommandFromApiJsonDeserializer clientProspectCommandFromApiJsonDeserializer;
	private final FromJsonHelper fromApiJsonHelper;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final CodeValueRepository codeValueRepository;
	private final OfficeRepository officeRepository;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	private final ActiondetailsWritePlatformService actiondetailsWritePlatformService;
	private final ClientRepository clientRepository;
	@Autowired
	public ClientProspectWritePlatformServiceImp(
			final PlatformSecurityContext context,
			final ClientProspectJpaRepository clientProspectJpaRepository,
			final ClientProspectCommandFromApiJsonDeserializer clientProspectCommandFromApiJsonDeserializer,
			final FromJsonHelper fromApiJsonHelper,final ClientRepository clientRepository,
			final ProspectDetailJpaRepository prospectDetailJpaRepository,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final CodeValueRepository codeValueRepository, final OfficeRepository officeRepository,
			final ActionDetailsReadPlatformService actionDetailsReadPlatformService, final ActiondetailsWritePlatformService actiondetailsWritePlatformService) {
		this.context = context;
		this.clientProspectJpaRepository = clientProspectJpaRepository;
		this.clientProspectCommandFromApiJsonDeserializer = clientProspectCommandFromApiJsonDeserializer;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.prospectDetailJpaRepository = prospectDetailJpaRepository;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.codeValueRepository=codeValueRepository;
		this.officeRepository=officeRepository;
		this.actionDetailsReadPlatformService = actionDetailsReadPlatformService;
		this.actiondetailsWritePlatformService = actiondetailsWritePlatformService;
		this.clientRepository = clientRepository;
	}

	@Transactional
	@Override
	public CommandProcessingResult createProspect(JsonCommand command) {

		try {
			context.authenticatedUser();
			this.clientProspectCommandFromApiJsonDeserializer.validateForCreate(command.json());
			
			final ClientProspect entity = ClientProspect.fromJson(fromApiJsonHelper, command);
			String officeId=command.stringValueOfParameterNamed("officeId");
			entity.setOffice(this.officeRepository.findOne(Long.parseLong(officeId)));
			this.clientProspectJpaRepository.save(entity);

			
			final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_CREATE_LEAD);
            if(!actionDetailsDatas.isEmpty()){
            this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,entity.getId(),entity.getId().toString(),null);
            }
			
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(entity.getId()).build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (ParseException pe) {
			throw new PlatformDataIntegrityException(
					"invalid.date.and.time.format",
					"invalid.date.and.time.format",
					"invalid.date.and.time.format");
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult followUpProspect(final JsonCommand command, final Long prospectId) {
		try {
			context.authenticatedUser();
			this.clientProspectCommandFromApiJsonDeserializer.validateForUpdate(command.json());
			
			final ProspectDetail prospectDetail = ProspectDetail.fromJson(command, prospectId);
			CodeValue codeValue=this.codeValueRepository.findOne(Long.parseLong(prospectDetail.getCallStatus()));
			prospectDetailJpaRepository.save(prospectDetail);
			final ClientProspect clientProspect = retrieveCodeBy(command.entityId());
			clientProspect.setStatus(codeValue.getLabel());
			this.clientProspectJpaRepository.save(clientProspect);
			
			final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_FOLLOWUP_LEAD);
            if(!actionDetailsDatas.isEmpty()){
            this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,clientProspect.getId(),clientProspect.getId().toString(),null);
            }
        
            return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(prospectDetail.getProspectId()).build();
		
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		} catch (ParseException e) {
			throw new PlatformDataIntegrityException(
					"invalid.date.and.time.format",
					"invalid.date.and.time.format",
					"invalid.date.and.time.format");
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult deleteProspect(JsonCommand command) {
		
		context.authenticatedUser();
		final ClientProspect clientProspect = retrieveCodeBy(command.entityId());
		clientProspect.setIsDeleted('Y');
		clientProspect.setStatus("Cancelled");//In place of Canceled we have updated it to converted 
		clientProspect.setStatusRemark(command.stringValueOfParameterNamed("statusRemark"));
		
		this.clientProspectJpaRepository.saveAndFlush(clientProspect);
		
		return new CommandProcessingResultBuilder().withEntityId(
				clientProspect.getId()).build();
	}

	private ClientProspect retrieveCodeBy(final Long prospectId) {
		
		final ClientProspect clientProspect = this.clientProspectJpaRepository.findOne(prospectId);
		
		if (clientProspect == null) {
			throw new CodeNotFoundException(prospectId.toString());
		}
		
		return clientProspect;
	}

	@Override
	public CommandProcessingResult convertToClient(final Long entityId) {

		final AppUser currentUser = context.authenticatedUser();
		final ClientProspect clientProspect = retrieveCodeBy(entityId);

		Long clientId = null;

		final JSONObject newClientJsonObject = new JSONObject();
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
			String activationDate = formatter.format(DateUtils.getDateOfTenant());

			final Long officeId = currentUser.getOffice().getId();
			newClientJsonObject.put("dateFormat", "dd MMMM yyyy");
			newClientJsonObject.put("locale", "en");
			newClientJsonObject.put("officeId", officeId);
			newClientJsonObject.put("firstname", clientProspect.getFirstName());
			newClientJsonObject.put("middlename", clientProspect.getMiddleName());
			newClientJsonObject.put("lastname", clientProspect.getLastName());
			newClientJsonObject.put("fullname", "");
			newClientJsonObject.put("externalId", "");
			newClientJsonObject.put("clientCategory", "20");
			// newClientJsonObject.put("active","300");
			newClientJsonObject.put("activationDate", activationDate);
			newClientJsonObject.put("active", "true");
			newClientJsonObject.put("email", clientProspect.getEmail());
			newClientJsonObject.put("phone", clientProspect.getMobileNumber());
			newClientJsonObject.put("flag", false);
			/*
			 * newClientJsonObject.put("login","");
			 * newClientJsonObject.put("password","");
			 */
			newClientJsonObject.put("officeId", clientProspect.getOffice().getId());
			newClientJsonObject.put("billMode","phone");
			JSONArray address = new JSONArray();
			
			for(int i=0;i<2;i++){
				final JSONObject clientPrimaryAddressJsonObject = new JSONObject();
				clientPrimaryAddressJsonObject.put("addressNo", clientProspect.getAddress());
				clientPrimaryAddressJsonObject.put("street", clientProspect.getStreetArea());
				clientPrimaryAddressJsonObject.put("city", clientProspect.getCityDistrict());
				clientPrimaryAddressJsonObject.put("zipCode", clientProspect.getZipCode());
				clientPrimaryAddressJsonObject.put("state", clientProspect.getState());
				clientPrimaryAddressJsonObject.put("country", clientProspect.getCountry());
				if(i==0){
					clientPrimaryAddressJsonObject.put("addressType", "PRIMARY");
				}else{
					clientPrimaryAddressJsonObject.put("addressType", "BILLING");
				}
				address.put(clientPrimaryAddressJsonObject);
			}
			
			newClientJsonObject.put("address", address);
			
			newClientJsonObject.put("flag", "false");
			String newClientJsonObjectString=newClientJsonObject.toString();
			newClientJsonObjectString = newClientJsonObjectString.replace("\\","");
			newClientJsonObjectString = newClientJsonObjectString.replace("\"{","{");
			newClientJsonObjectString = newClientJsonObjectString.replace("}\"","}");
			newClientJsonObjectString = newClientJsonObjectString.replace("\"[","[");
			newClientJsonObjectString = newClientJsonObjectString.replace("]\"","]");

			final CommandWrapper commandNewClient = new CommandWrapperBuilder().createClient()
					.withJson(newClientJsonObjectString).build(); //
			
			final CommandProcessingResult clientResult = this.commandsSourceWritePlatformService.logCommandSource(commandNewClient);
			/*
			 * final CommandWrapper commandRequest = new
			 * CommandWrapperBuilder().
			 * createAddress(clientResult.getClientId()).
			 * withJson(newClientAddressObject.toString().toString()).build();
			 * final CommandProcessingResult addressResult =
			 * this.commandsSourceWritePlatformService
			 * .logCommandSource(commandRequest);
			 */

			Client newClient = this.clientRepository.findOne(clientResult.getClientId());
			clientProspect.setStatusRemark(newClient.getAccountNo());
			clientId = clientResult.getClientId();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		clientProspect.setStatus("Converted");//In place of Canceled we have updated it to converted 
		// clientProspect.setIsDeleted('Y');

		// clientProspect.setStatusRemark(command.stringValueOfParameterNamed("statusRemark"));
		
		this.clientProspectJpaRepository.saveAndFlush(clientProspect);
		
		return new CommandProcessingResultBuilder().withEntityId(clientId).build();

	}

	@Override
	public CommandProcessingResult updateProspect(JsonCommand command) {
		
		try {
			context.authenticatedUser();
			this.clientProspectCommandFromApiJsonDeserializer.validateForCreate(command.json());

			final ClientProspect pros = retrieveCodeBy(command.entityId());
			final Map<String, Object> changes = pros.update(command);

			if (!changes.isEmpty()) {
				this.clientProspectJpaRepository.save(pros);
			}

			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(pros.getId()) //
					.with(changes) //
					.build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
		}
		return new CommandProcessingResultBuilder().withEntityId(-1L).build();
	}

	private void handleDataIntegrityIssues(final JsonCommand element,
			final DataIntegrityViolationException dve) {

		Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("serial_no_constraint")) {
			throw new PlatformDataIntegrityException(
					"validation.error.msg.inventory.item.duplicate.serialNumber",
					"validation.error.msg.inventory.item.duplicate.serialNumber",
					"validation.error.msg.inventory.item.duplicate.serialNumber",
					"");
		}

		LOGGER.error(dve.getMessage(), dve);
	}

	@Override
	public CommandProcessingResult elevateProspect(JsonCommand command, Long entityId) {
		

		
		try {
			context.authenticatedUser();
			final ClientProspect pros = retrieveCodeBy(command.entityId());
			pros.setStatus("Prospect");

			this.clientProspectJpaRepository.save(pros);
		
		
			return new CommandProcessingResultBuilder() //
					.withCommandId(command.commandId()) //
					.withEntityId(pros.getId()) //
					.build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
		}
		return new CommandProcessingResultBuilder().withEntityId(-1L).build();
		}

}
