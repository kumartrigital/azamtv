package org.mifosplatform.vendoragreement.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.broadcaster.domain.Broadcaster;
import org.mifosplatform.organisation.broadcaster.exception.BroadcatserNotFoundException;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.vendoragreement.data.VendorAgreementData;
import org.mifosplatform.vendoragreement.domain.VendorAgreement;
import org.mifosplatform.vendoragreement.domain.VendorAgreementDetail;
import org.mifosplatform.vendoragreement.domain.VendorAgreementDetailRepository;
import org.mifosplatform.vendoragreement.domain.VendorAgreementRepository;
import org.mifosplatform.vendoragreement.exception.VendorNotFoundException;
import org.mifosplatform.vendoragreement.serialization.VendorAgreementCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

	
@Service
public class VendorAgreementWritePlatformServiceImpl implements VendorAgreementWritePlatformService{
	
	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(VendorAgreementWritePlatformServiceImpl.class);	
	
	private PlatformSecurityContext context;
	private VendorAgreementRepository vendorRepository; 
	private VendorAgreementCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final FromJsonHelper fromApiJsonHelper;
	private final VendorAgreementDetailRepository vendorDetailRepository;
	 
	@Autowired
	public VendorAgreementWritePlatformServiceImpl(final PlatformSecurityContext context, 
			final VendorAgreementRepository vendorRepository, 
			final VendorAgreementCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final FromJsonHelper fromApiJsonHelper,
			final VendorAgreementDetailRepository vendorDetailRepository) {
		this.context = context;
		this.vendorRepository = vendorRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.vendorDetailRepository = vendorDetailRepository;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult createVendorAgreement(
			JsonCommand command) {
		
		try{
			
		 this.context.authenticatedUser();
		 this.fromApiJsonDeserializer.validateForCreate(command.json());
		 String fileLocation = command.stringValueOfParameterNamed("fileLocation");
		 final Long vendorId = command.longValueOfParameterNamed("vendorId");
		 final LocalDate agreementStartDate = command.localDateValueOfParameterNamed("startDate");
		 List<VendorAgreement> vendorCheck = this.vendorRepository.findOneByVendorId(vendorId);
		 
		 
		 final VendorAgreement vendor = VendorAgreement.fromJson(command, fileLocation);
		 
		 final JsonArray vendorDetailsArray = command.arrayOfParameterNamed("vendorDetails").getAsJsonArray();
			String[] vendorDetails = null;
			vendorDetails = new String[vendorDetailsArray.size()];
			
			for(int i = 0; i < vendorDetailsArray.size(); i++){
				vendorDetails[i] = vendorDetailsArray.get(i).toString();
			}
			
			//For VendorDetails
			for (final String vendorDetailsData : vendorDetails) {
							 
				final JsonElement element = fromApiJsonHelper.parse(vendorDetailsData);
				
				final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", element);
				
				validationForStartAndEndDate(vendorCheck, agreementStartDate, contentCode);
				
				final String loyaltyType = fromApiJsonHelper.extractStringNamed("loyaltyType", element);
				final BigDecimal loyaltyShare = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("loyaltyShare", element);
				final Long priceRegion = fromApiJsonHelper.extractLongNamed("priceRegion", element);
				final BigDecimal contentCost = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentCost", element);
				final BigDecimal contentSellPrice = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentSellPrice", element);
				final Long durationId = fromApiJsonHelper.extractLongNamed("durationId", element);
				
				final VendorAgreementDetail vendorDetail = new VendorAgreementDetail(contentCode, loyaltyType, loyaltyShare, priceRegion,
						contentCost, contentSellPrice,durationId);
				vendor.addVendorDetails(vendorDetail);
			}		 
			
			this.vendorRepository.save(vendor);
			return new CommandProcessingResult(vendor.getId());
		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	

	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		
		if (realCause.getMessage().contains("bvad_uq3")&&realCause.getMessage().contains("-0")) {
			throw new PlatformDataIntegrityException("error.msg.vendor.contentcode.lType.duplicate", "Duplicate Loyaltytype for Same Content Code");
		}else if (realCause.getMessage().contains("bvad_uq3")) {
			throw new PlatformDataIntegrityException("error.msg.vendor.contentcode.lType.duration.duplicate", "Duplicate Loyaltytype for Same Content Code and duration");
		}

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());

	}

	@Override
	public CommandProcessingResult updateVendorAgreement(Long vendorAgreementId, JsonCommand command) {
		
		try{
			
		this.context.authenticatedUser();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		String fileLocation = command.stringValueOfParameterNamed("fileLocation");
		VendorAgreement vendor=retrieveCodeBy(vendorAgreementId);
		
		final Long vendorId = command.longValueOfParameterNamed("vendorId");
		final LocalDate agreementStartDate = command.localDateValueOfParameterNamed("startDate");
		List<VendorAgreement> vendorCheck = this.vendorRepository.findOneByVendorId(vendorId);
		
		final Map<String, Object> changes = vendor.update(command,fileLocation);
		
		 final JsonArray vendorDetailsArray = command.arrayOfParameterNamed("vendorDetails").getAsJsonArray();
		 final JsonArray removevendorDetailsArray = command.arrayOfParameterNamed("removeVendorDetails").getAsJsonArray();
		
		 String[] vendorDetails = new String[vendorDetailsArray.size()];
		
		 for(int i = 0; i < vendorDetailsArray.size(); i++){
			 vendorDetails[i] = vendorDetailsArray.get(i).toString();
		 }
		 
		 for (final String vendorDetailsData : vendorDetails) {
						 
			final JsonElement element = fromApiJsonHelper.parse(vendorDetailsData);
			
			final Long vendorDetailId = fromApiJsonHelper.extractLongNamed("id", element);
			final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", element);
			
			final String loyaltyType = fromApiJsonHelper.extractStringNamed("loyaltyType", element);
			final Long priceRegion = fromApiJsonHelper.extractLongNamed("priceRegion", element);
			final BigDecimal loyaltyShare = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("loyaltyShare", element);
			final BigDecimal contentCost = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentCost", element);
			final BigDecimal contentSellPrice = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentSellPrice", element);
			final Long durationId = fromApiJsonHelper.extractLongNamed("durationId", element);
			
			if(vendorDetailId != null){
				
				VendorAgreementDetail vendordetail =this.vendorDetailRepository.findOne(vendorDetailId);
				if(changes.containsKey("startDate") || changes.containsKey("endDate") ||
						(!vendordetail.getContentCode().equalsIgnoreCase(contentCode) && (!vendordetail.getId().equals(vendorDetailId)))){
					validationForStartAndEndDate(vendorCheck, agreementStartDate, contentCode);
				}
				vendordetail.setContentCode(contentCode);
				vendordetail.setLoyaltyType(loyaltyType);
				vendordetail.setLoyaltyShare(loyaltyShare);
				vendordetail.setPriceRegion(priceRegion);
				vendordetail.setContentCost(contentCost);
				vendordetail.setContentSellPrice(contentSellPrice);
				vendordetail.setDurationId(durationId);
				/*if("NONE".equalsIgnoreCase(loyaltyType)){
					vendordetail.setContentCost(contentCost);
				}else{
					vendordetail.setContentCost(null);
				}*/
				this.vendorDetailRepository.saveAndFlush(vendordetail);
 				
			}else{
				validationForStartAndEndDate(vendorCheck, agreementStartDate, contentCode);
				final VendorAgreementDetail vendordetail = new VendorAgreementDetail(contentCode, loyaltyType, loyaltyShare, priceRegion, contentCost, contentSellPrice, durationId);
				vendor.addVendorDetails(vendordetail);
			}

		 }	
		 
		 if(removevendorDetailsArray.size() != 0){
				 
				String[] removedvendorDetails = new String[removevendorDetailsArray.size()];
	 			
	 			 for(int i = 0; i < removevendorDetailsArray.size(); i++){
	 				removedvendorDetails[i] = removevendorDetailsArray.get(i).toString();
	 			 }
	 			 
	 			 for (final String removedvendorDetailsData : removedvendorDetails) {
	 							 
	 				final JsonElement element = fromApiJsonHelper.parse(removedvendorDetailsData);
	 				final Long vendorDetailId = fromApiJsonHelper.extractLongNamed("id", element);
	 				final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", element);
		 			
	 				if(vendorDetailId != null){
	 					VendorAgreementDetail vendorDetail =this.vendorDetailRepository.findOne(vendorDetailId);
	 					vendorDetail.setContentCode(contentCode+"_"+vendorDetailId+"_"+"Y");
	 	 				vendorDetail.setIsDeleted("Y");
	 	 				vendorDetailRepository.saveAndFlush(vendorDetail);
	 				}	
	 			 }	
			 }
		
		this.vendorRepository.save(vendor);
		return new CommandProcessingResultBuilder() //
	       .withCommandId(command.commandId()) //
	       .withEntityId(vendorAgreementId) //
	       .with(changes) //
	       .build();
		}catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	private VendorAgreement retrieveCodeBy(final Long vendorId) {
        final VendorAgreement vendor = this.vendorRepository.findOne(vendorId);
        if (vendor == null) { throw new VendorNotFoundException(vendorId.toString()); }
        return vendor;
    }
	
	private void validationForStartAndEndDate(List<VendorAgreement> vendorCheck, LocalDate agreementStartDate, String contentCode){
		for (VendorAgreement vendorAgmt : vendorCheck) {
			 final int resultCheck = agreementStartDate.toDate().compareTo(vendorAgmt.getAgreementEnddate());
			 
			 if(resultCheck == -1){
				 List<VendorAgreementDetail> details = vendorDetailRepository.findOneByAgreementId(vendorAgmt);
				 for(VendorAgreementDetail vendorDetail :details){
					 if(vendorDetail.getContentCode().equalsIgnoreCase(contentCode)){
						 throw new PlatformDataIntegrityException("error.msg.vendor.active.agreement.with.this.dates", "Already Active agreement there between this dates");
					 }
				 }
			 }
		 }
	}

	@Override
	public CommandProcessingResult createNewVendorAgreement(JsonCommand command) {

		
		try{
			
		 this.context.authenticatedUser();
		 this.fromApiJsonDeserializer.validateForCreate(command.json());
		 String fileLocation = command.stringValueOfParameterNamed("fileLocation");
		 final Long vendorId = command.longValueOfParameterNamed("vendorId");
		 final LocalDate agreementStartDate = command.localDateValueOfParameterNamed("startDate");
		 List<VendorAgreement> vendorCheck = this.vendorRepository.findOneByVendorId(vendorId);
		 
		 
		 final VendorAgreement vendor = VendorAgreement.fromJson(command, fileLocation);
		 
		 final JsonArray vendorDetailsArray = command.arrayOfParameterNamed("vendorDetails").getAsJsonArray();
			String[] vendorDetails = null;
			vendorDetails = new String[vendorDetailsArray.size()];
			
			for(int i = 0; i < vendorDetailsArray.size(); i++){
				vendorDetails[i] = vendorDetailsArray.get(i).toString();
			}
			
			//For VendorDetails
			for (final String vendorDetailsData : vendorDetails) {
							 
				final JsonElement element = fromApiJsonHelper.parse(vendorDetailsData);
				
				final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", element);
				
				validationForStartAndEndDate(vendorCheck, agreementStartDate, contentCode);
				
				final String loyaltyType = fromApiJsonHelper.extractStringNamed("loyaltyType", element);
				final BigDecimal loyaltyShare = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("loyaltyShare", element);
				final Long priceRegion = fromApiJsonHelper.extractLongNamed("priceRegion", element);
				final BigDecimal contentCost = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentCost", element);
				final BigDecimal contentSellPrice = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentSellPrice", element);
				final Long durationId = fromApiJsonHelper.extractLongNamed("durationId", element);
				
				final VendorAgreementDetail vendorDetail = new VendorAgreementDetail(contentCode, loyaltyType, loyaltyShare, priceRegion,
						contentCost, contentSellPrice,durationId);
				vendor.addVendorDetails(vendorDetail);
			}		 
			
			this.vendorRepository.save(vendor);
			return new CommandProcessingResult(vendor.getId());
		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	
	}

	@Override
	public CommandProcessingResult updateNewVendorAgreement(JsonCommand command, Long vendorAgreementId) {
		
		try{
			
		this.context.authenticatedUser();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		String fileLocation = command.stringValueOfParameterNamed("fileLocation");
		VendorAgreement vendor=retrieveCodeBy(vendorAgreementId);
		
		final Long vendorId = command.longValueOfParameterNamed("vendorId");
		final LocalDate agreementStartDate = command.localDateValueOfParameterNamed("startDate");
		List<VendorAgreement> vendorCheck = this.vendorRepository.findOneByVendorId(vendorId);
		
		final Map<String, Object> changes = vendor.update(command,fileLocation);
		
		 final JsonArray vendorDetailsArray = command.arrayOfParameterNamed("vendorDetails").getAsJsonArray();
		 final JsonArray removevendorDetailsArray = command.arrayOfParameterNamed("removeVendorDetails").getAsJsonArray();
		
		 String[] vendorDetails = new String[vendorDetailsArray.size()];
		
		 for(int i = 0; i < vendorDetailsArray.size(); i++){
			 vendorDetails[i] = vendorDetailsArray.get(i).toString();
		 }
		 
		 for (final String vendorDetailsData : vendorDetails) {
						 
			final JsonElement element = fromApiJsonHelper.parse(vendorDetailsData);
			
			final Long vendorDetailId = fromApiJsonHelper.extractLongNamed("id", element);
			final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", element);
			
			final String loyaltyType = fromApiJsonHelper.extractStringNamed("loyaltyType", element);
			final Long priceRegion = fromApiJsonHelper.extractLongNamed("priceRegion", element);
			final BigDecimal loyaltyShare = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("loyaltyShare", element);
			final BigDecimal contentCost = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentCost", element);
			final BigDecimal contentSellPrice = fromApiJsonHelper.extractBigDecimalWithLocaleNamed("contentSellPrice", element);
			final Long durationId = fromApiJsonHelper.extractLongNamed("durationId", element);
			
			if(vendorDetailId != null){
				
				VendorAgreementDetail vendordetail =this.vendorDetailRepository.findOne(vendorDetailId);
				if(changes.containsKey("startDate") || changes.containsKey("endDate") ||
						(!vendordetail.getContentCode().equalsIgnoreCase(contentCode) && (!vendordetail.getId().equals(vendorDetailId)))){
					validationForStartAndEndDate(vendorCheck, agreementStartDate, contentCode);
				}
				vendordetail.setContentCode(contentCode);
				vendordetail.setLoyaltyType(loyaltyType);
				vendordetail.setLoyaltyShare(loyaltyShare);
				vendordetail.setPriceRegion(priceRegion);
				vendordetail.setContentCost(contentCost);
				vendordetail.setContentSellPrice(contentSellPrice);
				vendordetail.setDurationId(durationId);
				/*if("NONE".equalsIgnoreCase(loyaltyType)){
					vendordetail.setContentCost(contentCost);
				}else{
					vendordetail.setContentCost(null);
				}*/
				this.vendorDetailRepository.saveAndFlush(vendordetail);
 				
			}else{
				validationForStartAndEndDate(vendorCheck, agreementStartDate, contentCode);
				final VendorAgreementDetail vendordetail = new VendorAgreementDetail(contentCode, loyaltyType, loyaltyShare, priceRegion, contentCost, contentSellPrice, durationId);
				vendor.addVendorDetails(vendordetail);
			}

		 }	
		 
		 if(removevendorDetailsArray.size() != 0){
				 
				String[] removedvendorDetails = new String[removevendorDetailsArray.size()];
	 			
	 			 for(int i = 0; i < removevendorDetailsArray.size(); i++){
	 				removedvendorDetails[i] = removevendorDetailsArray.get(i).toString();
	 			 }
	 			 
	 			 for (final String removedvendorDetailsData : removedvendorDetails) {
	 							 
	 				final JsonElement element = fromApiJsonHelper.parse(removedvendorDetailsData);
	 				final Long vendorDetailId = fromApiJsonHelper.extractLongNamed("id", element);
	 				final String contentCode = fromApiJsonHelper.extractStringNamed("contentCode", element);
		 			
	 				if(vendorDetailId != null){
	 					VendorAgreementDetail vendorDetail =this.vendorDetailRepository.findOne(vendorDetailId);
	 					vendorDetail.setContentCode(contentCode+"_"+vendorDetailId+"_"+"Y");
	 	 				vendorDetail.setIsDeleted("Y");
	 	 				vendorDetailRepository.saveAndFlush(vendorDetail);
	 				}	
	 			 }	
			 }
		
		this.vendorRepository.save(vendor);
		return new CommandProcessingResultBuilder() //
	       .withCommandId(command.commandId()) //
	       .withEntityId(vendorAgreementId) //
	       .with(changes) //
	       .build();
		}catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	



}
