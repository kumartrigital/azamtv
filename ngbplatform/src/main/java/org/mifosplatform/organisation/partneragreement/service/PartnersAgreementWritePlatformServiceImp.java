package org.mifosplatform.organisation.partneragreement.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfo;
import org.mifosplatform.organisation.office.domain.OfficeAdditionalInfoRepository;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.partner.service.PartnersWritePlatformServiceImp;
import org.mifosplatform.organisation.partneragreement.data.AgreementData;
import org.mifosplatform.organisation.partneragreement.domain.Agreement;
import org.mifosplatform.organisation.partneragreement.domain.AgreementDetails;
import org.mifosplatform.organisation.partneragreement.domain.AgreementDetailsRepository;
import org.mifosplatform.organisation.partneragreement.domain.AgreementRepository;
import org.mifosplatform.organisation.partneragreement.exception.AgreementNotFoundException;
import org.mifosplatform.organisation.partneragreement.serialization.PartnersAgreementCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class PartnersAgreementWritePlatformServiceImp implements PartnersAgreementWritePlatformService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PartnersWritePlatformServiceImp.class);
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromApiJsonHelper;
	private final PartnersAgreementCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final AgreementRepository agreementRepository;
	private final OfficeAdditionalInfoRepository officeAdditionalInfoRepository;
	private final AgreementDetailsRepository agreementDetailsRepository;
	private final FromJsonHelper fromJsonHelper;
	private final OfficeRepository officeRepository;
	private final CrmServices crmServices;
	private final OrderReadPlatformService orderReadPlatformService;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	
	
	@Autowired
	public PartnersAgreementWritePlatformServiceImp(final PlatformSecurityContext context,final FromJsonHelper fromApiJsonHelper,
			final PartnersAgreementCommandFromApiJsonDeserializer apiJsonDeserializer,final AgreementRepository agreementRepository,
		    final OfficeAdditionalInfoRepository officeAdditionalInfoRepository,final AgreementDetailsRepository agreementDetailsRepository,
		    final FromJsonHelper fromJsonHelper,final OfficeRepository officeRepository,final CrmServices crmServices,
		    final OrderReadPlatformService orderReadPlatformService,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.agreementRepository = agreementRepository;
		this.officeAdditionalInfoRepository = officeAdditionalInfoRepository;
		this.agreementDetailsRepository = agreementDetailsRepository;
		this.fromJsonHelper = fromJsonHelper;
		this.officeRepository = officeRepository;
		this.crmServices = crmServices;
		this.orderReadPlatformService = orderReadPlatformService;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;

	}

	@Override
	public CommandProcessingResult createNewPartnerAgreement(final JsonCommand command) {

		
		try{
			this.context.authenticatedUser();
			//this.apiJsonDeserializer.validateForCreate(command.json());
			JSONObject json = null;
			try {
				json = new JSONObject(command.json());
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	
			CommandProcessingResult commandProcessingResult=this.crmServices.createAgreement(command);
			String[] substancesArray = null;
			if (commandProcessingResult != null) {
				Set<String> subtances = commandProcessingResult.getSubstances();
				substancesArray = subtances.toArray(new String[subtances.size()]);
			}
			
			Long clientId = null;
			final JsonArray multiplePlans = command.arrayOfParameterNamed("plans").getAsJsonArray();
			for(JsonElement planElement : multiplePlans){
				clientId=this.fromApiJsonHelper.extractLongNamed("clientId", planElement);
			}
			
			
			//final JsonElement element = fromJsonHelper.parse(command.json());
			//final Long officeId = command.longValueOfParameterNamed("officeId");
			//final OfficeAdditionalInfo additionalInfo=this.officeAdditionalInfoRepository.findOne(officeId);
			//final Office additionalInfo=this.officeRepository.findOne(officeId);
			//Agreement agreement=Agreement.fromJosn(command,additionalInfo.getId());
			
			/*final Long planId = command.longValueOfParameterNamed("planCode");
			final Long contractPeriod = command.longValueOfParameterNamed("contractPeriod");
			final String billingFrequency = command.stringValueOfParameterNamed("paytermCode");
			final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
			final LocalDate endDate = command.localDateValueOfParameterNamed("endDate");
			final Long clientId = command.longValueOfParameterNamed("clientId");
			final Long clientServiceId = command.longValueOfParameterNamed("clientServiceId");
			*/
			/*AgreementDetails details=new AgreementDetails(planId,contractPeriod,billingFrequency,startDate,endDate,purchaseProductId,packageId);
			int i = 0;
			if (substancesArray != null) {
				details.setPurchaseProductPoId(this.retreivePurchaseProductPoid(substancesArray[i]));
				details.setPackageId(this.retreiveOrderNo(substancesArray[i]));
			}*/
			
			
			
			final CommandWrapper commandRequest = new CommandWrapperBuilder().createMultipleOrder(clientId).withClientId(clientId).withJson(json.toString()).build();
	        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	        
			
			/*if(result !=null){
				details.setPurchaseProductPoId(result.getResourceIdentifier());
				details.setPackageId(result.getTransactionId());
           }*/
			//agreement.addAgreementDetails(details);
			
			//final JsonArray partnerAgreementArray = command.arrayOfParameterNamed("sourceData").getAsJsonArray();
			/*if(partnerAgreementArray.size() !=0){
			for(int i=0; i<partnerAgreementArray.size();i++){ 
				
				final JsonElement element = fromApiJsonHelper.parse(partnerAgreementArray.get(i).toString());
				final Long source = fromApiJsonHelper.extractLongNamed("source", element);
				final String shareType = fromApiJsonHelper.extractStringNamed("shareType", element);
				final BigDecimal shareAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("shareAmount",element);
				final Long planId = fromApiJsonHelper.extractLongNamed("planId", element);
				final Long contractPeriod = fromApiJsonHelper.extractLongNamed("contractPeriod", element);
				final String billingFrequency = fromApiJsonHelper.extractStringNamed("billingFrequency",element);
				final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
				final LocalDate endDate = command.localDateValueOfParameterNamed("endDate");
				AgreementDetails details=new AgreementDetails(planId,contractPeriod,billingFrequency,startDate,endDate);
				agreement.addAgreementDetails(details);
			}
		 }*/
			//this.agreementRepository.save(agreement);
			//this.orderReadPlatformService.insertOrderDetails(clientId,planId,startDate,endDate,clientServiceId,details.getPackageId());
		
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).build();
			//.withEntityId(agreement.getId()).withOfficeId(agreement.getOfficeId()).build();
		  }catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private String retreiveOrderNo(String substancesArray) {
		String[] split = substancesArray.split("_");
		return split[0];
	}

	private String retreivePurchaseProductPoid(String substancesArray) {
		String[] split = substancesArray.split("_");
		return split[3];
	}
	
	private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();
		LOGGER.error(dve.getMessage(), dve);
		
		if(dve.getMostSpecificCause().getMessage().contains("agreement_dtl_ai_ps_mc_uniquekey")){
			 throw new PlatformDataIntegrityException("error.msg.agreement.duplicate.source.data.entry.issue","A Agreement with sourceCategory " +
			 		"already exists","source");
		}
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "+ realCause.getMessage());

	}

	@Override
	public CommandProcessingResult UpdatePartnerAgreement(final JsonCommand command) {
		
		try{
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForUpdate(command.json());
			Agreement agreement=this.agreementRetrieveById(command.entityId());
			final Map<String, Object> changes = agreement.update(command);
			final JsonArray partnerAgreementArray = command.arrayOfParameterNamed("sourceData").getAsJsonArray();
			final JsonArray removeAgreementDetails = command.arrayOfParameterNamed("removeSourceData").getAsJsonArray();
			
			if (removeAgreementDetails.size() != 0) {
				for (int i = 0; i < removeAgreementDetails.size(); i++) {
					final JsonElement element = fromApiJsonHelper.parse(removeAgreementDetails.get(i).toString());
					final Long detailId = fromApiJsonHelper.extractLongNamed("detailId", element);
					AgreementDetails detail = this.agreementDetailsRepository.findOne(detailId);
					//detail.setSourceType(Long.valueOf((detail.getSourceType().toString()+detail.getId().toString())));
					detail.setPlanId(Long.valueOf((detail.getPlanId().toString()+detail.getId().toString())));
					detail.setEndDate(DateUtils.getDateOfTenant());
					detail.setIsDeleted('Y');
					this.agreementDetailsRepository.saveAndFlush(detail);
				}
			}

			this.agreementRepository.saveAndFlush(agreement);
			
			  if(partnerAgreementArray.size() !=0){
				 for(int i=0; i<partnerAgreementArray.size(); i++){
					 
						final JsonElement element = fromApiJsonHelper.parse(partnerAgreementArray.get(i).toString());
						final Long detailId = fromApiJsonHelper.extractLongNamed("detailId", element);
						/*final Long source = fromApiJsonHelper.extractLongNamed("source", element);
						final String shareType = fromApiJsonHelper.extractStringNamed("shareType", element);
						final BigDecimal shareAmount = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("shareAmount",element);*/
						final Long planId = fromApiJsonHelper.extractLongNamed("planId", element);
						final Long contractPeriod = fromApiJsonHelper.extractLongNamed("contractPeriod", element);
						final String billingFrequency = fromApiJsonHelper.extractStringNamed("billingFrequency",element);
						final LocalDate startDate = command.localDateValueOfParameterNamed("startDate");
						final LocalDate endDate = command.localDateValueOfParameterNamed("endDate");
						if(detailId !=null){
						AgreementDetails detail=this.agreementDetailsRepository.findOne(detailId);
						/*detail.setSourceType(source);
						detail.setShareType(shareType);
						detail.setShareAmount(shareAmount);*/
						detail.setPlanId(planId);
						detail.setContractPeriod(contractPeriod);
						detail.setBillingFrequency(billingFrequency);
						detail.setStartDate(startDate.toDate());
						if(endDate!=null){detail.setEndDate(endDate.toDate());}
						this.agreementDetailsRepository.saveAndFlush(detail);
						}else{
							AgreementDetails details=new AgreementDetails(planId,contractPeriod,billingFrequency,startDate,endDate);
							agreement.addAgreementDetails(details);
						}
				}
			}
		  
			  this.agreementRepository.saveAndFlush(agreement);
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(agreement.getId())
					 .withOfficeId(agreement.getOfficeId()).with(changes).build();
		  
		}catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}	   
        
   }

	@Override
	public CommandProcessingResult deletePartnerAgreement(final JsonCommand command) {

		try {
			context.authenticatedUser();
			Long orderId = command.longValueOfParameterNamed("orderId");
			JSONObject json = null;
			try {
				json = new JSONObject(command.json());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			this.crmServices.deleteAgreement(command);
			//final Agreement agreement = this.agreementRetrieveById(agreementId);
			
			final CommandWrapper commandRequest = new CommandWrapperBuilder().disconnectOrder(orderId).withJson(json.toString()).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			
			/*final Agreement agreement = this.agreementRetrieveById(command.entityId());
			List<AgreementDetails> details = agreement.getDetails();
			for (AgreementDetails detail : details) {
				detail.setSourceType(Long.valueOf((detail.getSourceType().toString() + detail.getId().toString())));
				//detail.setPlanId(Long.valueOf((detail.getPlanId().toString() + detail.getId().toString())));
				detail.setEndDate(DateUtils.getDateOfTenant());
				detail.setIsDeleted('Y');
			}
			agreement.delete();
			this.agreementRepository.save(agreement);*/
			return new CommandProcessingResultBuilder()
					.withEntityId(result.getGroupId()).build();

		} catch (DataIntegrityViolationException dve) {

			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}
	
	
	private Agreement agreementRetrieveById(final Long entityId) {

		Agreement agreement = this.agreementRepository.findOne(entityId);
		if (agreement == null) {
			throw new AgreementNotFoundException(entityId);
		}
		return agreement;
	}
	
}
