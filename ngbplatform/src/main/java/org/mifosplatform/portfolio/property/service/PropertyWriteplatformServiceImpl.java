package org.mifosplatform.portfolio.property.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeMaster;
import org.mifosplatform.billing.chargecode.domain.ChargeCodeRepository;
import org.mifosplatform.billing.chargecode.exception.ChargeCodeNotFoundException;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.onetimesale.service.InvoiceOneTimeSale;
import org.mifosplatform.organisation.address.domain.Address;
import org.mifosplatform.organisation.address.domain.AddressRepository;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.portfolio.association.data.AssociationData;
import org.mifosplatform.portfolio.association.service.HardwareAssociationReadplatformService;
import org.mifosplatform.portfolio.property.domain.PropertyCodesMaster;
import org.mifosplatform.portfolio.property.domain.PropertyCodesMasterRepository;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMapping;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMappingRepository;
import org.mifosplatform.portfolio.property.domain.PropertyHistoryRepository;
import org.mifosplatform.portfolio.property.domain.PropertyMaster;
import org.mifosplatform.portfolio.property.domain.PropertyMasterRepository;
import org.mifosplatform.portfolio.property.domain.PropertyTransactionHistory;
import org.mifosplatform.portfolio.property.exceptions.PropertyCodeAllocatedException;
import org.mifosplatform.portfolio.property.exceptions.PropertyMasterNotFoundException;
import org.mifosplatform.portfolio.property.serialization.PropertyCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PropertyWriteplatformServiceImpl implements PropertyWriteplatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(PropertyWriteplatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final PropertyCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final PropertyMasterRepository propertyMasterRepository;
	private final PropertyHistoryRepository propertyHistoryRepository;
	private final PropertyCodesMasterRepository propertyCodesMasterRepository;
	private final AddressRepository addressRepository;
	private final ChargeCodeRepository chargeCodeRepository;
	private final InvoiceOneTimeSale invoiceOneTimeSale;
	private final PropertyReadPlatformService propertyReadPlatformService;
	private final PropertyDeviceMappingRepository propertyDeviceMappingRepository;
	private final HardwareAssociationReadplatformService associationReadplatformService;

	@Autowired
	public PropertyWriteplatformServiceImpl(final PlatformSecurityContext context,final PropertyCommandFromApiJsonDeserializer apiJsonDeserializer,
			final PropertyMasterRepository propertyMasterRepository,final PropertyHistoryRepository propertyHistoryRepository,
			final PropertyCodesMasterRepository propertyCodesMasterRepository,final AddressRepository addressRepository,
		    final ChargeCodeRepository chargeCodeRepository,final InvoiceOneTimeSale invoiceOneTimeSale,
		    final PropertyReadPlatformService propertyReadPlatformService,
            final PropertyDeviceMappingRepository propertyDeviceMappingRepository,
            final HardwareAssociationReadplatformService associationReadplatformService) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.propertyMasterRepository = propertyMasterRepository;
		this.propertyHistoryRepository = propertyHistoryRepository;
		this.propertyCodesMasterRepository = propertyCodesMasterRepository;
		this.addressRepository = addressRepository;
		this.propertyDeviceMappingRepository = propertyDeviceMappingRepository;
		this.chargeCodeRepository = chargeCodeRepository;
		this.invoiceOneTimeSale = invoiceOneTimeSale;
		this.propertyReadPlatformService = propertyReadPlatformService;
		 this.associationReadplatformService=associationReadplatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult createProperty(final JsonCommand command) {

		try {
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			PropertyMaster propertyMaster = PropertyMaster.fromJson(command);
			this.propertyMasterRepository.save(propertyMaster);
			//history calling
			PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),CodeNameConstants.CODE_PROPERTY_DEFINE,null,propertyMaster.getPropertyCode());
			this.propertyHistoryRepository.save(propertyHistory);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					    .withEntityId(propertyMaster.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(1L));
		}

	}
	
	@Transactional
	@Override
	public CommandProcessingResult updateProperty(final Long entityId,final JsonCommand command) {
		
		try{
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			PropertyMaster propertyMaster=this.propertyRetrieveById(entityId);
			final Map<String,Object> changes=propertyMaster.update(command);
			if (!changes.isEmpty()) {
				this.propertyMasterRepository.saveAndFlush(propertyMaster);
			}
			//history calling
			PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),CodeNameConstants.CODE_PROPERTY_UPDATE,propertyMaster.getClientId(),propertyMaster.getPropertyCode());
			this.propertyHistoryRepository.save(propertyHistory);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
				       .withEntityId(propertyMaster.getId()).with(changes).build();
		}catch(DataIntegrityViolationException dve){
			
			if (dve.getCause() instanceof ConstraintViolationException) {
				handleCodeDataIntegrityIssues(command, dve);
			}
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	 }

	@Transactional
	@Override
	public CommandProcessingResult deleteProperty(final Long entityId) {

		PropertyMaster propertyMaster = null;
		try {
			this.context.authenticatedUser();
			propertyMaster = this.propertyRetrieveById(entityId);
			if(propertyMaster.getStatus()!=null){
			  if (propertyMaster.getStatus().equalsIgnoreCase(CodeNameConstants.CODE_PROPERTY_VACANT)) {
				propertyMaster.delete();
				this.propertyMasterRepository.save(propertyMaster);
			  } else {
				throw new PropertyMasterNotFoundException(propertyMaster.getPropertyCode());
			  }
		   }
		 return new CommandProcessingResult(entityId);
	   } catch (final DataIntegrityViolationException dve) {
			throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
					"Unknown data integrity issue with resource: "+ dve.getMessage());
		}

	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {
		
		final Throwable realCause = dve.getMostSpecificCause();
		
		if (realCause.getMessage().contains("property_code_constraint")) {
			final String code = command.stringValueOfParameterNamed("propertyCode");
			throw new PlatformDataIntegrityException("error.msg.property.duplicate.code",
					"A property with Code'" + code + "'already exists","propertyCode", code);
		}
		else if (realCause.getMessage().contains("property_code_type_with_its_code")) {
            final String name = command.stringValueOfParameterNamed("propertyCodeType");
            throw new PlatformDataIntegrityException("error.msg.propertycode.master.propertyCodeType.duplicate.name", 
            		"A Property Code Type with name '" + name + "' already exists","code");
        }

		LOGGER.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "+ realCause.getMessage());

	}


	@Transactional
	@Override
	public CommandProcessingResult createServiceTransfer(final Long clientId,final JsonCommand command) {

		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForServiceTransfer(command.json());
			final String oldPropertyCode = command.stringValueOfParameterNamed("oldPropertyCode");
			final String newPropertyCode = command.stringValueOfParameterNamed("newPropertyCode");
			final String serialNumber = command.stringValueOfParameterNamed("serialNumber");
			final BigDecimal shiftChargeAmount = command.bigDecimalValueOfParameterNamed("shiftChargeAmount");
			final String chargeCode = command.stringValueOfParameterNamed("chargeCode");
			final boolean serialNumberFlag =command.booleanPrimitiveValueOfParameterNamed("serialNumberFlag");
			Address clientAddress =null;
			List<AssociationData> associationDatas = this.associationReadplatformService.retrieveClientAssociationDetails(clientId,serialNumber);
			PropertyTransactionHistory transactionHistory = null;
			if(oldPropertyCode != null){
				clientAddress = this.addressRepository.findOneByClientIdAndPropertyCode(clientId,oldPropertyCode);	
			}else{
				clientAddress = this.addressRepository.findOneByClientId(clientId);
			}
			

			if (clientAddress != null && !StringUtils.isEmpty(newPropertyCode)) {
				
				PropertyMaster newpropertyMaster = this.propertyMasterRepository.findoneByPropertyCode(newPropertyCode);
				
				if (oldPropertyCode !=null&&!StringUtils.isEmpty(oldPropertyCode)) {
					
					PropertyMaster oldPropertyMaster = this.propertyMasterRepository.findoneByPropertyCode(oldPropertyCode);
					
					if (newpropertyMaster != null && newpropertyMaster.getClientId() != null) {
						if (!newpropertyMaster.getClientId().equals(clientId)) {
							throw new PropertyCodeAllocatedException(newpropertyMaster.getPropertyCode());
						}
					}
					// check shifting property same or not
					if (!oldPropertyCode.equalsIgnoreCase(newPropertyCode) && oldPropertyMaster != null && newpropertyMaster != null
							&& newpropertyMaster.getClientId() == null) {
						/*if(serialNumberFlag == true && associationDatas.size() ==0 && "PRIMARY".equalsIgnoreCase(clientAddress.getAddressKey())){*/
						if(associationDatas.size() !=0 && "PRIMARY".equalsIgnoreCase(clientAddress.getAddressKey())){
						List<PropertyDeviceMapping>	proertyallocation = this.propertyDeviceMappingRepository.findByPropertyCode(oldPropertyCode);
						if(proertyallocation.size() <= 1){
							oldPropertyMaster.setClientId(null);
							oldPropertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_VACANT);
							this.propertyMasterRepository.saveAndFlush(oldPropertyMaster);
							PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),oldPropertyMaster.getId(),
									CodeNameConstants.CODE_PROPERTY_ALLOCATE, null,oldPropertyMaster.getPropertyCode());
							this.propertyHistoryRepository.save(propertyHistory);
						}
						clientAddress.setAddressKey("BILLING1");
						clientAddress = new Address(clientId, "PRIMARY", newpropertyMaster.getPropertyCode(), newpropertyMaster.getStreet(), newpropertyMaster.getPrecinct(),
								   newpropertyMaster.getState(), newpropertyMaster.getCountry(), newpropertyMaster.getPoBox(),clientAddress.getPhone(),clientAddress.getEmail(), newpropertyMaster.getDistrict());	
						
						}else if("BILLING1".equalsIgnoreCase(clientAddress.getAddressKey())){	
							List<PropertyDeviceMapping>	proertyallocation = this.propertyDeviceMappingRepository.findByPropertyCode(oldPropertyCode);
							if(proertyallocation.size() <= 1){
								oldPropertyMaster.setClientId(null);
								oldPropertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_VACANT);
								PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),oldPropertyMaster.getId(),
										CodeNameConstants.CODE_PROPERTY_ALLOCATE, null,oldPropertyMaster.getPropertyCode());
								this.propertyHistoryRepository.save(propertyHistory);
							}	
							oldPropertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_VACANT);
							this.propertyMasterRepository.saveAndFlush(oldPropertyMaster);
							clientAddress = new Address(clientId, "BILLING", newpropertyMaster.getPropertyCode(), newpropertyMaster.getStreet(), newpropertyMaster.getPrecinct(),
									   newpropertyMaster.getState(), newpropertyMaster.getCountry(), newpropertyMaster.getPoBox(),null,null, newpropertyMaster.getDistrict());	
							String addressNo = oldPropertyCode;
							Address address=this.addressRepository.findOne(addressNo);
								address.delete();
								this.addressRepository.saveAndFlush(address);
							
						}else{
						   clientAddress = new Address(clientId, "BILLING", newpropertyMaster.getPropertyCode(), newpropertyMaster.getStreet(), newpropertyMaster.getPrecinct(),
								   newpropertyMaster.getState(), newpropertyMaster.getCountry(), newpropertyMaster.getPoBox(), null,null,newpropertyMaster.getDistrict());	
						}

						newpropertyMaster.setClientId(clientId);
						newpropertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_OCCUPIED);
						this.propertyMasterRepository.saveAndFlush(newpropertyMaster);
						this.addressRepository.save(clientAddress);
					}
					
					
				} else {
					
					newpropertyMaster.setClientId(clientId);
					newpropertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_OCCUPIED);
					this.propertyMasterRepository.saveAndFlush(newpropertyMaster);
					clientAddress.setAddressNo(newpropertyMaster.getPropertyCode());
					clientAddress.setStreet(newpropertyMaster.getStreet());
					clientAddress.setCity(newpropertyMaster.getPrecinct());
					clientAddress.setState(newpropertyMaster.getState());
					clientAddress.setCountry(newpropertyMaster.getCountry());
					clientAddress.setZip(newpropertyMaster.getPoBox());
					clientAddress.setDistrict(newpropertyMaster.getDistrict());
					this.addressRepository.save(clientAddress);

				}
				if(!serialNumber.isEmpty() && null!=serialNumber)	{
					
				
				PropertyDeviceMapping  deviceMapping=this.propertyDeviceMappingRepository.findBySerailNumber(serialNumber);
					deviceMapping.setPropertyCode(newPropertyCode);
					this.propertyDeviceMappingRepository.saveAndFlush(deviceMapping);
				transactionHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),newpropertyMaster.getId(),
						CodeNameConstants.CODE_PROPERTY_SERVICE_TRANSFER,newpropertyMaster.getClientId(),newpropertyMaster.getPropertyCode());
				this.propertyHistoryRepository.save(transactionHistory);
				
				}else{
					throw new SerianumberMappingNotFoundException(serialNumber,oldPropertyCode);
					
					/*System.out.println("serial number is not mapped with plan");*/
				}
				

			} else {

				throw new PropertyMasterNotFoundException(clientId,oldPropertyCode);
			}
			/* call one time invoice for service transfer */
			if (!StringUtils.isEmpty(chargeCode)) {

				ChargeCodeMaster chargeMaster = this.chargeCodeRepository.findOneByChargeCode(chargeCode);
				BillItem invoice = this.invoiceOneTimeSale.calculateAdditionalFeeCharges(chargeMaster,transactionHistory.getId(), Long.valueOf(-1L),clientId, shiftChargeAmount);
				return new CommandProcessingResult(invoice.getId(), clientId);

			} else {
				throw new ChargeCodeNotFoundException(chargeCode);

			}
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1L), clientId);
		}

	}

	@Transactional
	@Override
	public CommandProcessingResult createPropertyMasters(final JsonCommand command) {
	
		try
		{
			context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreatePropertyMaster(command.json());
			final PropertyCodesMaster propertyCodeMaster = PropertyCodesMaster.fromJson(command);
			this.propertyCodesMasterRepository.save(propertyCodeMaster);
				return new CommandProcessingResult(propertyCodeMaster.getId());

		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return  CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updatePropertyMaster(final Long entityId,final JsonCommand command) {
		
		try
		{
			context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreatePropertyMaster(command.json());
			PropertyCodesMaster propertyCodesMaster=this.propertyCodesMasterRetrieveById(entityId);
			final Map<String,Object> changes=propertyCodesMaster.update(command);
			if (!changes.isEmpty()) {
				this.propertyCodesMasterRepository.saveAndFlush(propertyCodesMaster);
			}
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
				       .withEntityId(propertyCodesMaster.getId()).with(changes).build();
			
		}catch(DataIntegrityViolationException dve){
			
			if (dve.getCause() instanceof ConstraintViolationException) {
				handleCodeDataIntegrityIssues(command, dve);
			}
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	
	}
	
	@Transactional
	@Override
	public CommandProcessingResult deletePropertyMaster(final Long entityId) {
	
		PropertyCodesMaster propertyCodesMaster = null;
		try {
			this.context.authenticatedUser();
			propertyCodesMaster = this.propertyCodesMasterRetrieveById(entityId);
			if(propertyCodesMaster.getCode() !=null && propertyCodesMaster.getPropertyCodeType() !=null){	
		      Boolean checkPropertyMaster=this.propertyReadPlatformService.retrievePropertyMasterCount(propertyCodesMaster.getCode(),propertyCodesMaster.getPropertyCodeType());
		       if(!checkPropertyMaster){
		    	   propertyCodesMaster.deleted();
		    	   this.propertyCodesMasterRepository.save(propertyCodesMaster);
		       }else{
				throw new PropertyCodeAllocatedException();
			}
		  }	
		 return new CommandProcessingResult(entityId);
		}catch (final DataIntegrityViolationException dve) {
			throw new PlatformDataIntegrityException("error.msg.could.unknown.data.integrity.issue",
					"Unknown data integrity issue with resource: "+ dve.getMessage());
		 }
	}
	
	private PropertyMaster propertyRetrieveById(final Long entityId) {

		PropertyMaster propertyMaster = this.propertyMasterRepository.findOne(entityId);
		if (propertyMaster == null) {
			throw new PropertyMasterNotFoundException(entityId);
		}
		return propertyMaster;
	}
	
	private PropertyCodesMaster propertyCodesMasterRetrieveById(final Long entityId) {

		PropertyCodesMaster propertyCodesMaster = this.propertyCodesMasterRepository.findOne(entityId);
		if (propertyCodesMaster == null) {
			throw new PropertyMasterNotFoundException(entityId);
		}
		return propertyCodesMaster;
	}
	@Transactional
	@Override
	public CommandProcessingResult allocatePropertyDevice(Long entityId,JsonCommand command) {
        try{
        	this.context.authenticatedUser();
        	PropertyDeviceMapping propertyDeviceMapping = PropertyDeviceMapping.fromJson(entityId,command);
        	this.propertyDeviceMappingRepository.save(propertyDeviceMapping);
        	
        	/*PropertyMaster propertyMaster = this.propertyMasterRepository.findoneByPropertyCode(propertyDeviceMapping.getPropertyCode());
        	if(propertyMaster != null){
        		PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),CodeNameConstants.CODE_MAPPED,entityId,propertyMaster.getPropertyCode());
        		this.propertyHistoryRepository.save(propertyHistory);
        	}*/
			
        	return new CommandProcessingResult(propertyDeviceMapping.getId());
        }catch(DataIntegrityViolationException dve){
        	handleCodeDataIntegrityIssues(command, dve);
        	return new CommandProcessingResult(Long.valueOf(-1));
        }
	}
	
}
