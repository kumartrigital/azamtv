package org.mifosplatform.organisation.address.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.domain.Address;
import org.mifosplatform.organisation.address.domain.AddressRepository;
import org.mifosplatform.organisation.address.domain.City;
import org.mifosplatform.organisation.address.domain.CityRepository;
import org.mifosplatform.organisation.address.domain.Country;
import org.mifosplatform.organisation.address.domain.CountryRepository;
import org.mifosplatform.organisation.address.domain.District;
import org.mifosplatform.organisation.address.domain.State;
import org.mifosplatform.organisation.address.domain.StateRepository;
import org.mifosplatform.organisation.address.exception.AddressNoRecordsFoundException;
import org.mifosplatform.organisation.address.exception.CityNotFoundException;
import org.mifosplatform.organisation.address.exception.CountryNotFoundException;
import org.mifosplatform.organisation.address.exception.DistrictNotFoundException;
import org.mifosplatform.organisation.address.exception.StateNotFoundException;
import org.mifosplatform.organisation.address.serialization.LocationValidatorCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.broadcaster.domain.Broadcaster;
import org.mifosplatform.organisation.broadcaster.exception.BroadcatserNotFoundException;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMapping;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMappingRepository;
import org.mifosplatform.portfolio.property.domain.PropertyHistoryRepository;
import org.mifosplatform.portfolio.property.domain.PropertyMaster;
import org.mifosplatform.portfolio.property.domain.PropertyMasterRepository;
import org.mifosplatform.portfolio.property.domain.PropertyTransactionHistory;
import org.mifosplatform.portfolio.property.exceptions.PropertyCodeAllocatedException;
import org.mifosplatform.portfolio.property.exceptions.PropertyDeviceMappingExistException;
import org.mifosplatform.portfolio.property.exceptions.PropertyMasterNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AddressWritePlatformServiceImpl implements AddressWritePlatformService {
	 
	private final static Logger logger = LoggerFactory.getLogger(AddressWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final AddressRepository addressRepository;
	private final CityRepository cityRepository;
	private final StateRepository stateRepository;
	private final CountryRepository countryRepository;
	private final AddressReadPlatformService addressReadPlatformService;
	private final LocationValidatorCommandFromApiJsonDeserializer locationValidatorCommandFromApiJsonDeserializer;
	private final PropertyMasterRepository propertyMasterRepository;
    private final PropertyHistoryRepository propertyHistoryRepository;
    private final ConfigurationRepository configurationRepository;
    private final PropertyDeviceMappingRepository propertyDeviceMappingRepository;
	public static final String ADDRESSTYPE="addressType";
	private final DistrictRepository districtRepository;
	private Object apiJsonDeserializer;
	
	
	


	@Autowired
	public AddressWritePlatformServiceImpl(final PlatformSecurityContext context,final CityRepository cityRepository,final ConfigurationRepository configurationRepository,
			final AddressReadPlatformService addressReadPlatformService,final StateRepository stateRepository,final CountryRepository countryRepository,
			final AddressRepository addressRepository,final LocationValidatorCommandFromApiJsonDeserializer locationValidatorCommandFromApiJsonDeserializer,
			final PropertyMasterRepository propertyMasterRepository, final PropertyHistoryRepository propertyHistoryRepository,
			final PropertyDeviceMappingRepository propertyDeviceMappingRepository,
			final DistrictRepository districtRepository) {
		
		this.context = context;
		this.addressRepository = addressRepository;
		this.cityRepository=cityRepository;
		this.stateRepository=stateRepository;
		this.countryRepository=countryRepository;
		this.addressReadPlatformService=addressReadPlatformService;
		this.locationValidatorCommandFromApiJsonDeserializer = locationValidatorCommandFromApiJsonDeserializer;
		this.propertyMasterRepository = propertyMasterRepository;
		this.propertyDeviceMappingRepository = propertyDeviceMappingRepository;
		this.propertyHistoryRepository = propertyHistoryRepository;
		this.configurationRepository = configurationRepository;
		this.districtRepository=districtRepository;
		
		

	}

	@Override
	public CommandProcessingResult createAddress(final Long clientId,final JsonCommand command) {
		try {
			context.authenticatedUser();
			final Address address = Address.fromJson(clientId,command);
			this.addressRepository.save(address);
			 PropertyMaster propertyMaster=null;
	           Configuration propertyConfiguration=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_PROPERTY_MASTER);
				if(propertyConfiguration != null && propertyConfiguration.isEnabled()) {		
					 propertyMaster=this.propertyMasterRepository.findoneByPropertyCode(command.stringValueOfParameterNamed("addressNo"));
					if(propertyMaster != null && propertyMaster.getClientId() != null ){
						throw new PropertyCodeAllocatedException(propertyMaster.getPropertyCode());
					}
					
					propertyMaster.setClientId(clientId);
					propertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_OCCUPIED);
				    this.propertyMasterRepository.saveAndFlush(propertyMaster);
				    PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),
				    		CodeNameConstants.CODE_PROPERTY_ALLOCATE,clientId,propertyMaster.getPropertyCode());
				    this.propertyHistoryRepository.save(propertyHistory);
				}
				
			return new CommandProcessingResult(address.getId(),clientId);
		} catch (DataIntegrityViolationException dve) {
			 handleCodeDataIntegrityIssues(command, dve);
			return  CommandProcessingResult.empty();
		}
	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause(); 
		String entityCode = command.stringValueOfParameterNamed("entityCode");
		if(realCause.getMessage().contains("country_code")){
			 throw new PlatformDataIntegrityException("error.msg.addressmaster.country.duplicate.countrycode",
					 "Country Code with this '"+entityCode+ "' already exists","countryCode",entityCode);
		}else if(realCause.getMessage().contains("state_code")){
			 throw new PlatformDataIntegrityException( "error.msg.addressmaster.state.duplicate.statecode",
					 "State Code with this '"+entityCode+ "' already exists","stateCode",entityCode);
		}else if(realCause.getMessage().contains("district_code")){
			 throw new PlatformDataIntegrityException( "error.msg.addressmaster.district.duplicate.districtcode",
					 "District Code with this '"+entityCode+ "' already exists","districtCode",entityCode);
		}else if(realCause.getMessage().contains("city_code")){
			 throw new PlatformDataIntegrityException("error.msg.addressmaster.city.duplicate.citycode",
					 "City Code with this '"+entityCode+ "' already exists","cityCode",entityCode);
		}
		  logger.error(dve.getMessage(), dve);
		
	}

	@Override
	public CommandProcessingResult updateAddress(final Long clientId,final JsonCommand command) {
		
		try
		{
			  context.authenticatedUser();
	             Map<String, Object> changes =new HashMap<String, Object>();
	             final List<AddressData> addressDatas =this.addressReadPlatformService.retrieveClientAddressDetails(clientId);
	             final String addressType=command.stringValueOfParameterNamed(ADDRESSTYPE);
	             Configuration  configuration=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_PROPERTY_MASTER);
	             
	             if("BILLING".equalsIgnoreCase(addressType)){
	            	 
	            	 final Address  newAddress=Address.fromJson(clientId, command);
               	     this.addressRepository.saveAndFlush(newAddress);
               	     if(configuration!=null&&configuration.isEnabled()){
               	    	 PropertyMaster propertyMaster=this.propertyMasterRepository.findoneByPropertyCode(newAddress.getAddressNo());
               	    	 propertyMaster.setClientId(clientId);
            			 propertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_OCCUPIED);
                		 this.propertyMasterRepository.saveAndFlush(propertyMaster);
               	    	 PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),CodeNameConstants.CODE_PROPERTY_ALLOCATE,
               	    			newAddress.getClientId(),propertyMaster.getPropertyCode());
            		     this.propertyHistoryRepository.save(propertyHistory);
               	     }
	              }
	              else{
	                  for(AddressData addressData:addressDatas){
	                    	 
	                    	  if("PRIMARY".equalsIgnoreCase(addressData.getAddressType()))
	                    	  {
	                    		  final Address address = retrieveAddressBy(addressData.getAddressId());  
	                    		  if(configuration != null && configuration.isEnabled()) {		
	                    		  final String newPropertyCode=command.stringValueOfParameterNamed("addressNo");
	                    		  PropertyMaster propertyMaster=this.propertyMasterRepository.findoneByPropertyCode(newPropertyCode);
	                    		  if(propertyMaster!=null){
	                    			  if(propertyMaster.getClientId() != null ){
	                  					throw new PropertyCodeAllocatedException(propertyMaster.getPropertyCode());
	                  				}
	                    		  PropertyMaster oldPropertyMaster=this.propertyMasterRepository.findoneByPropertyCode(address.getAddressNo());
	                    		  if(!address.getAddressNo().equalsIgnoreCase(newPropertyCode)&&oldPropertyMaster!=null){
                                 	 oldPropertyMaster.setClientId(null);
	                    			 oldPropertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_VACANT);
	                    			 this.propertyMasterRepository.saveAndFlush(oldPropertyMaster);
	                    			 PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),oldPropertyMaster.getId(),CodeNameConstants.CODE_PROPERTY_FREE,
	                    					 null,oldPropertyMaster.getPropertyCode());
	                    			 this.propertyHistoryRepository.save(propertyHistory);
	                    			 changes = address.update(command);
		                        	 this.addressRepository.saveAndFlush(address);
	                    			 propertyMaster.setClientId(address.getClientId());
	                    			 propertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_OCCUPIED);
		                    		 this.propertyMasterRepository.saveAndFlush(propertyMaster);
		                    		 PropertyTransactionHistory newpropertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),CodeNameConstants.CODE_PROPERTY_ALLOCATE,
		                    					 address.getClientId(),propertyMaster.getPropertyCode());
		                    		 this.propertyHistoryRepository.save(newpropertyHistory);
	                    			 } else{
	                    				 changes = address.update(command);
			                        	 this.addressRepository.saveAndFlush(address);
			                        	 propertyMaster.setClientId(address.getClientId());
		                    			 propertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_OCCUPIED);
			                    		 this.propertyMasterRepository.saveAndFlush(propertyMaster);
			                    		 PropertyTransactionHistory newpropertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),CodeNameConstants.CODE_PROPERTY_ALLOCATE,
			                    					 address.getClientId(),propertyMaster.getPropertyCode());
			                    		 this.propertyHistoryRepository.save(newpropertyHistory);
	                    			 }
	                            }else{
	                            	throw new PropertyMasterNotFoundException(clientId);
	                            }
	                    		}else{
	                    		   changes = address.update(command);
	                        		this.addressRepository.saveAndFlush(address);
	                    	   }
	                    }
            }
	    }
         return new CommandProcessingResultBuilder() 
         .withCommandId(command.commandId()) 
         .withEntityId(clientId) 
         .withClientId(clientId)
         .with(changes) 
         .build();
	} catch (DataIntegrityViolationException dve) {
		 handleCodeDataIntegrityIssues(command,dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	}
}

	private Address retrieveAddressBy(final Long addrId) {
		final Address address=this.addressRepository.findOne(addrId);
	    if(address== null){
		throw new CodeNotFoundException(addrId);
	    }
	return address;
	}
	
	
	
	
	@Override
	public CommandProcessingResult updateAddressNew(final Long addrId, final JsonCommand command) {
	   try{
		   
		   this.context.authenticatedUser();
		   //this.apiJsonDeserializer.update(command.json());
		   final Address address = retrieveNewAddressBy(addrId);
		   final Map<String, Object> changes = address.update(command);
		   if(!changes.isEmpty()){
			   this.addressRepository.saveAndFlush(address);
		   }
		return new CommandProcessingResultBuilder() //
	       .withCommandId(command.commandId()) //
	       .withEntityId(addrId) //
	      // .withClientId(clientId)
	       .with(changes) //
	       .build();
		}catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
		      return new CommandProcessingResult(Long.valueOf(-1));
		  }
		}
		
	
	private Address retrieveNewAddressBy(final Long addrId) {
		final Address address=this.addressRepository.findOne(addrId);
	    if(address== null){
		throw new CodeNotFoundException(addrId);
	    }
	return address;
	}
	
	
	
	
	
	

	@Override
	public CommandProcessingResult createLocation(final JsonCommand command,final String entityType) {
  try{
	  
	  this.context.authenticatedUser();
	 
	  this.locationValidatorCommandFromApiJsonDeserializer.validateForCreate(command.json(),entityType);
	  if(entityType.equalsIgnoreCase("city")){
			final City city = City.fromJson(command);
		  this.cityRepository.save(city);
		  return new CommandProcessingResult(Long.valueOf(city.getId()));
		  
		  
	  }else if(entityType.equalsIgnoreCase("state")){
		  
		  final State state=State.fromJson(command);
		  this.stateRepository.save(state);
		  
		  return new CommandProcessingResult(Long.valueOf(state.getId()));
		  
      }else if(entityType.equalsIgnoreCase("district")){
		  
		  final District district=District.fromJson(command);
		  this.districtRepository.save(district);
		  return new CommandProcessingResult(Long.valueOf(district.getId()));
		  
	  }else{
		  
		  final Country country=Country.fromJson(command);
		  this.countryRepository.save(country);
		  return new CommandProcessingResult(Long.valueOf(country.getId()));
	  }
	  
		  
	  
  } catch (DataIntegrityViolationException dve) {
	  handleCodeDataIntegrityIssues(command,dve);
		return new CommandProcessingResult(Long.valueOf(-1));
	}

	}
	@Override
	public CommandProcessingResult updateLocation(final JsonCommand command,final String entityType, final Long id) {
	  try{
		this.context.authenticatedUser();
		this.locationValidatorCommandFromApiJsonDeserializer.validateForCreate(command.json(),entityType);
		if(entityType.equalsIgnoreCase("city")){
			final City city=cityObjectRetrieveById(id);
		   final Map<String, Object> changes = city.update(command);
		   	if(!changes.isEmpty()){
		   		this.cityRepository.saveAndFlush(city);
		   	}
		   
      	}else if(entityType.equalsIgnoreCase("state")){
			  
      		final State state=stateObjectRetrieveById(id);
  			final Map<String, Object> changes = state.update(command);
	  
  			if(!changes.isEmpty()){
  				this.stateRepository.saveAndFlush(state);
  			}
  			
      	}else if(entityType.equalsIgnoreCase("district")){
			  
      		final District district=districtObjectRetrieveById(id);
  			final Map<String, Object> changes = district.update(command);
	  
  			if(!changes.isEmpty()){
  				this.districtRepository.saveAndFlush(district);
  			} 
		
  	 	}else {
			  
  	 		final Country country=countryObjectRetrieveById(id);
  			final Map<String, Object> changes = country.update(command);
	  
  				if(!changes.isEmpty()){
  					this.countryRepository.saveAndFlush(country);
  				}
  	 		}
		return new CommandProcessingResult(id);
		  
	  	}catch (DataIntegrityViolationException dve) {
	  		if(dve.getCause() instanceof ConstraintViolationException){
	  			handleCodeDataIntegrityIssues(command,dve);
	  		}
	  		return new CommandProcessingResult(Long.valueOf(-1));
	  	}
	}

	private City cityObjectRetrieveById(final Long id){
		final City city=this.cityRepository.findOne(id);
		if (city== null) { throw new CityNotFoundException(id.toString()); }
		return city;
	}
	
	private State stateObjectRetrieveById(final Long id){
		final State state=this.stateRepository.findOne(id);
		if (state== null) { throw new StateNotFoundException(id.toString()); }
		return state;
	}
	private District districtObjectRetrieveById(final Long id){
		final District district=this.districtRepository.findOne(id);
		if (district== null) { throw new DistrictNotFoundException(id.toString()); }
		return district;
	}
	
	private Country countryObjectRetrieveById(final Long id){
		final Country country=this.countryRepository.findOne(id);
		if (country== null) { throw new CountryNotFoundException(id.toString()); }
		return country;
	}

	@Override
	public CommandProcessingResult deleteLocation(final JsonCommand command,final String entityType, final Long id) {
		
		try{
	    	 this.context.authenticatedUser();
	    	 if(entityType.equalsIgnoreCase("city")){
	    		 final City city = this.cityRepository.findOne(id);
	    		 if(city==null){
	        		 throw new CityNotFoundException(id.toString());
	        	 }
	    		 city.delete();
	    		 this.cityRepository.save(city);
	    		 return new CommandProcessingResult(id);
	        	 
	    	 }else if(entityType.equalsIgnoreCase("state")){
	    		 final State state = this.stateRepository.findOne(id);
	    		 if(state==null){
	        		 throw new StateNotFoundException(id.toString());
	        	 }
	    		 state.delete();
	    		 this.stateRepository.save(state);
	    		 return new CommandProcessingResult(id);
	    		 
	    	 }else if(entityType.equalsIgnoreCase("district")){
	    		 final District district = this.districtRepository.findOne(id);
	    		 if(district==null){
	        		 throw new DistrictNotFoundException(id.toString());
	        	 }
	    		 district.delete();
	    		 this.districtRepository.save(district);
	    		 return new CommandProcessingResult(id); 
	        	 
	    	 }else{
	    		 final Country country = this.countryRepository.findOne(id);
	    			if (country== null) { 
	    				throw new CountryNotFoundException(id.toString()); 
	    			}
	    			country.delete();
	    			this.countryRepository.save(country);
	    		 return new CommandProcessingResult(id);
	        	 
	    	 }
	}catch (DataIntegrityViolationException dve) {
		handleCodeDataIntegrityIssues(command,dve);
		return null;
  	}
  }

	@Transactional
	@Override
	public CommandProcessingResult deleteAddress(Long entityId,JsonCommand command) {
  
		 try{
			 this.context.authenticatedUser();
			 Address address=this.addressRepository.findOne(entityId);
			 if(address == null){
				 throw new AddressNoRecordsFoundException("clientAddress");
			 }
			 
		address.delete();
		this.addressRepository.saveAndFlush(address);
		Configuration  configuration=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_IS_PROPERTY_MASTER);
		if(configuration != null && configuration.isEnabled()) {	
			final String newPropertyCode=address.getAddressNo();
			List<PropertyDeviceMapping>  propertyDeviceMapping=this.propertyDeviceMappingRepository.findByPropertyCode(newPropertyCode);
			if(propertyDeviceMapping != null && !propertyDeviceMapping.isEmpty()){
				throw new PropertyDeviceMappingExistException();
			}
   		  	PropertyMaster propertyMaster=this.propertyMasterRepository.findoneByPropertyCode(newPropertyCode);
   		  		if(propertyMaster!=null){
   		  			propertyMaster.setClientId(null);
   		  			propertyMaster.setStatus(CodeNameConstants.CODE_PROPERTY_VACANT);
   		  			this.propertyMasterRepository.saveAndFlush(propertyMaster);
   		  		PropertyTransactionHistory propertyHistory = new PropertyTransactionHistory(DateUtils.getLocalDateOfTenant(),propertyMaster.getId(),
						CodeNameConstants.CODE_PROPERTY_FREE, null,propertyMaster.getPropertyCode());
				this.propertyHistoryRepository.save(propertyHistory);
   		  		}
   		  	}
		return new CommandProcessingResult(entityId);
		
		 }catch(DataIntegrityViolationException dve){
			 handleCodeDataIntegrityIssues(command, dve);
			 return new CommandProcessingResult(Long.valueOf(-1));
			 
		 }
	}

	
}

