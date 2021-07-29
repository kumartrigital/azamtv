package org.mifosplatform.organisation.usercataloge.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.salescataloge.domain.SalesCataloge;
import org.mifosplatform.organisation.salescataloge.domain.SalesCatalogeRepository;
import org.mifosplatform.organisation.salescatalogemapping.domain.SalesCatalogeMapping;
import org.mifosplatform.organisation.usercataloge.domain.UserCataloge;
import org.mifosplatform.organisation.usercataloge.domain.UserCatalogeRepository;
import org.mifosplatform.organisation.usercataloge.exception.UserCatalogeNotFoundException;
import org.mifosplatform.organisation.usercataloge.serialization.UserCatalogeCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
public class UserCatalogeWritePlatformServiceImpl implements UserCatalogeWritePlatformService{

	
	private final PlatformSecurityContext context;
	private final UserCatalogeCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final UserCatalogeRepository userCatalogeRepository;
	private final SalesCatalogeRepository salesCatalogeRepository;
	
	
	@Autowired
	public UserCatalogeWritePlatformServiceImpl(final PlatformSecurityContext context,
			final UserCatalogeCommandFromApiJsonDeserializer apiJsonDeserializer,
			final UserCatalogeRepository userCatalogeRepository,final SalesCatalogeRepository salesCatalogeRepository) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.userCatalogeRepository = userCatalogeRepository;
		this.salesCatalogeRepository = salesCatalogeRepository;
	}


	@Transactional
	@Override
	public CommandProcessingResult create(JsonCommand command) {
		
		try{
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			UserCataloge usercataloge = UserCataloge.formJson(command);
			Long userId  = command.longValueOfParameterNamed("userId");
			final String[] salesCataloges = command.arrayValueOfParameterNamed("salesCatalogeDetails");
		    /*final Set<UserCataloge> selectedSalesCataloges = assembleSetOfSalesCataloges(salesCataloges);
		    usercataloge.addSalesCataloge(selectedSalesCataloges);
			this.userCatalogeRepository.saveAndFlush(usercataloge);*/
			for (final String salescatalogeId : salesCataloges) {
				Long catalogeId  = Long.valueOf(salescatalogeId);
				this.userCatalogeRepository.saveAndFlush( new UserCataloge(userId,catalogeId));				 
            }
			return new CommandProcessingResultBuilder().withEntityId(usercataloge.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
	    }
		
	}
		
	/*private Set<UserCataloge> assembleSetOfSalesCataloges(String[] salesCatalogeArray) {

        final Set<UserCataloge> allSalesCataloges = new HashSet<>();
        if (!ObjectUtils.isEmpty(salesCatalogeArray)) {
            for (final String salescatalogeId : salesCatalogeArray) {
                final SalesCataloge salesCataloge = this.salesCatalogeRepository.findOne(Long.valueOf(salescatalogeId));
                if (salesCataloge != null) { 
                	UserCataloge detail=new UserCataloge(salesCataloge.getId());
                	allSalesCataloges.add(detail);
                }
            }
        }

        return allSalesCataloges;
    }*/



	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause();
        
    	throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");	
	}


	@Transactional
	@Override
	public CommandProcessingResult updateUserCataloge(JsonCommand command, Long userId) {


		   try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   Set<UserCataloge> usercataloge = this.retrieveUserCataloge(userId);
			   //final Map<String, Object> changes = ((UserCataloge) usercataloge).update(command);
			   /*final Map<String, Object> changes = ((UserCataloge) usercataloge).update(command);
			    
			   List<UserCataloge> usercataloges = this.detailFun(usercataloge_current,command.arrayValueOfParameterNamed("salesCatalogeDetails"),userId);
			   if (changes.containsKey("salesCatalogeDetails")) {
	            	final String[] salescatalogeIds = (String[]) changes.get("salesCatalogeDetails");
	            	final Set<UserCataloge> selectedSalesCataloges = assembleSetOfSalesCataloges(salescatalogeIds);
	            	((UserCataloge) usercataloge).addSalesCataloge(selectedSalesCataloges);
	            }
			   
			   if(!usercataloge.isEmpty()){
				   this.userCatalogeRepository.save(usercataloge);
		       }*/
			   
			   Set<UserCataloge> usercataloges = this.detailFun(usercataloge,command.arrayValueOfParameterNamed("salesCatalogeDetails"),userId);
			   this.userCatalogeRepository.save(usercataloges);
			   
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(userId) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
	}

	private Set<UserCataloge> detailFun(Set<UserCataloge> usercataloges, String[] salesCatalogeArray,Long userId) {
		/*Set<UserCataloge> usercataloges = new Set<UserCataloge>();*/
		for(UserCataloge usercataloge:usercataloges){
			boolean deletable = true;
			for (final String salescatalogeId : salesCatalogeArray) {
				if(usercataloge.getCatalogeId().toString().equalsIgnoreCase(salescatalogeId)){
					deletable = false;
				}
			}
			if(deletable){
				usercataloge.delete();
				/*usercataloges.add(usercataloge);*/
			}
			
		}
		
		for(final String salescatalogeId : salesCatalogeArray) {
			boolean isNew= true;
			for(UserCataloge usercataloge:usercataloges){
				if(usercataloge.getCatalogeId().toString().equalsIgnoreCase(salescatalogeId)){
					isNew = false;break;
				}
			}
			if(isNew){
				usercataloges.add(new UserCataloge(userId,Long.valueOf(salescatalogeId)));
			}
			
        }
		
		return usercataloges;
	}


	/*@Override
	public CommandProcessingResult deleteUserCataloge(Long usercatalogeId) {

		try{
			this.context.authenticatedUser();
			List<UserCataloge> usercataloge = this.retrieveUserCataloge(usercatalogeId);
			if(usercataloge.getIsDeleted()=='Y'){
				throw new UserCatalogeNotFoundException(usercatalogeId);
			}
			usercataloge.delete();
			this.userCatalogeRepository.saveAndFlush(usercataloge);
			return new CommandProcessingResultBuilder().withEntityId(usercatalogeId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
			
	}*/
	

	private Set<UserCataloge> retrieveUserCataloge(Long userId) {
		Set<UserCataloge> usercataloges = this.userCatalogeRepository.findByUserIdValue(userId);
		if (usercataloges == null) { throw new UserCatalogeNotFoundException(userId); }
		return usercataloges;
	
	}




}
