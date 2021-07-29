package org.mifosplatform.billing.planprice.service;

import java.util.List;
import java.util.Map;

import org.mifosplatform.billing.chargecode.domain.ChargeCodeMaster;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeRepository;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.billing.planprice.exceptions.ChargeCOdeExists;
import org.mifosplatform.billing.planprice.exceptions.ChargeCodeAndContractPeriodException;
import org.mifosplatform.billing.planprice.exceptions.ContractNotNullException;
import org.mifosplatform.billing.planprice.exceptions.PriceNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.redemption.exception.PinNumberNotFoundException;
import org.mifosplatform.organisation.voucher.data.VoucherData;
import org.mifosplatform.organisation.voucher.domain.VoucherRepository;
import org.mifosplatform.organisation.voucher.exception.AlreadyProcessedException;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.service.serialization.PriceCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.service.service.ServiceMasterWritePlatformServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * @author hugo
 *
 */
@Service
public class PriceWritePlatformServiceImpl implements PriceWritePlatformService {

	 private final static Logger LOGGER = LoggerFactory.getLogger(ServiceMasterWritePlatformServiceImpl.class);
	 private final PlatformSecurityContext context;
	 private final PriceReadPlatformService priceReadPlatformService;
	 private final PriceCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	 private final PriceRepository priceRepository;
	 private final VoucherRepository voucherRepository;
	 private final ChargeCodeRepository chargeCodeRepository;
	 private final ContractRepository contractRepository;
	 
	@Autowired
	 public PriceWritePlatformServiceImpl(final PlatformSecurityContext context,final PriceReadPlatformService priceReadPlatformService,
			 final PriceCommandFromApiJsonDeserializer fromApiJsonDeserializer,final PriceRepository priceRepository,
			 final VoucherRepository voucherRepository, final ChargeCodeRepository chargeCodeRepository,
			 final ContractRepository contractRepository)
		{
			this.context=context;
			this.priceReadPlatformService=priceReadPlatformService;
			this.fromApiJsonDeserializer=fromApiJsonDeserializer;
			this.priceRepository=priceRepository;
			this.voucherRepository=voucherRepository;
			this.chargeCodeRepository = chargeCodeRepository;
			this.contractRepository = contractRepository;
		}
	
	@Override
	public CommandProcessingResult createPricing(final Long planId,JsonCommand command) {
		
		try{
		context.authenticatedUser();
		this.fromApiJsonDeserializer.validateForCreate(command.json());
		String isPrepaid = command.stringValueOfParameterNamed("isPrepaid");
		String chargeCode = command.stringValueOfParameterNamed("chargeCode");
		String contractPeriod = command.stringValueOfParameterNamed("duration");
		
		ChargeCodeMaster chargeCodeMaster = chargeCodeRepository.findOneByChargeCode(chargeCode);
		Contract contract = contractRepository.findOneByContractId(contractPeriod);
		if(isPrepaid.equalsIgnoreCase("Y")){
			
			if(chargeCodeMaster.getChargeDuration() != contract.getUnits().intValue()){
				throw new ChargeCodeAndContractPeriodException(chargeCode);
			}
		}
		List<ServiceData> serviceData = this.priceReadPlatformService.retrieveServiceCodeDetails(planId);
		
		final Price price =Price.fromJson(command,serviceData,planId);
			
			for (ServiceData data : serviceData) {
				
				System.out.println(data.getChargeCode() != null);
				System.out.println(data.getPlanId() == planId);
				System.out.println(data.getProductCode().equals(price.getProductId().toString()));
				System.out.println(data.getPriceregion().equalsIgnoreCase(price.getPriceRegion().toString()));
				System.out.println(data.getChargeCode().equalsIgnoreCase(price.getChargeCode()));
				System.out.println(data.getCurrencyId().toString().equals(price.getCurrencyId()));
				System.out.println("servicedata chargeOwner"+ data.getChargeOwner());
				System.out.println("Price chargeOwner"+ price.getChargeOwner());

				//System.out.println(data.getChargeOwner().toString().equals(price.getChargeOwner()));
				
					if (data.getChargeCode() != null && data.getChargeOwner()!=null && data.getPlanId() == planId && data.getProductCode().equals(price.getProductId().toString())
							&& data.getPriceregion().equalsIgnoreCase(price.getPriceRegion().toString()) && data.getChargeCode().equalsIgnoreCase(price.getChargeCode())
							&& data.getCurrencyId().toString().equals(price.getCurrencyId()) && data.getChargeOwner().toString().equals(price.getChargeOwner()) ){
						
						throw new ChargeCOdeExists(data.getChargeDescription());
					}
					
			}
			price.setChargeOwner("self");
		this.priceRepository.save(price);
		return new CommandProcessingResult(price.getId());

	} catch (DataIntegrityViolationException dve) {
		LOGGER.error(dve.getMessage());
		 handleCodeDataIntegrityIssues(command, dve);
		return  CommandProcessingResult.empty();
	}
}

	private void handleCodeDataIntegrityIssues(final JsonCommand command,DataIntegrityViolationException dve) {
		LOGGER.error(dve.getMessage(),dve);
		 throw new PlatformDataIntegrityException("error.msg.planprice.unknown.data.integrity.issue",
	                "Unknown data integrity issue with resource.");
		
	}
	@Override
	public CommandProcessingResult updatePrice(final Long priceId, JsonCommand command) {
		
		try{
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			String isPrepaid = command.stringValueOfParameterNamed("isPrepaid");
			String chargeCode = command.stringValueOfParameterNamed("chargeCode");
			String contractPeriod = command.stringValueOfParameterNamed("duration");
			
			ChargeCodeMaster chargeCodeMaster = chargeCodeRepository.findOneByChargeCode(chargeCode);
			Contract contract = contractRepository.findOneByContractId(contractPeriod);
			if(isPrepaid.equalsIgnoreCase("Y")){
				if(contract == null){
					throw new ContractNotNullException();
				}
				if(chargeCodeMaster.getChargeDuration() != contract.getUnits().intValue()){
					throw new ChargeCodeAndContractPeriodException(chargeCode);
				}
			}
				
			final Price price = retrievePriceBy(priceId);
			final  Map<String, Object> changes = price.update(command);
			if (changes.containsKey("duration")) {
				
				final List<VoucherData> voucherData= this.priceReadPlatformService.retrieveVoucherDatas(priceId);
				if(!voucherData.isEmpty()){
					throw new AlreadyProcessedException(price.getContractPeriod(),priceId);
				}
			}
			if(!changes.isEmpty())
				this.priceRepository.save(price);
  
			return new CommandProcessingResultBuilder() //
			.withCommandId(command.commandId()) //
			.withEntityId(priceId) //
			.with(changes) //
			.build();

         } catch (DataIntegrityViolationException dve) {
    		 handleCodeDataIntegrityIssues(command, dve);
    		return  CommandProcessingResult.empty();
    	}
	
	
}
	private Price retrievePriceBy(final Long priceId) {
		
		final Price price=this.priceRepository.findOne(priceId);
		if(price==null){
			{ throw new PriceNotFoundException(priceId.toString()); }
		}
		return price;
	}
	@Override
	public CommandProcessingResult deletePrice(final Long priceId) {
		  try {
				 Price price=this.priceRepository.findOne(priceId);
				 	if(price!= null){
				 		final List<VoucherData> voucherData= this.priceReadPlatformService.retrieveVoucherDatas(priceId);
						if(!voucherData.isEmpty()){
							throw new AlreadyProcessedException(price.getContractPeriod(),priceId);
						}
						price.delete();	
				 	}
			     this.priceRepository.save(price);
			     return new CommandProcessingResultBuilder().withEntityId(priceId).build();
		  	
		  } catch (DataIntegrityViolationException dve) {
			  handleCodeDataIntegrityIssues(null, dve);
		  		return new CommandProcessingResultBuilder().withEntityId(Long.valueOf(-1)).build();
			}
		  
		  }
}
