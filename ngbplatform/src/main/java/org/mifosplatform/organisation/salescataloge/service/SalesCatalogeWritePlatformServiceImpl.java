package org.mifosplatform.organisation.salescataloge.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.exception.ChannelNotFoundException;
import org.mifosplatform.organisation.salescataloge.domain.SalesCataloge;
import org.mifosplatform.organisation.salescataloge.domain.SalesCatalogeRepository;
import org.mifosplatform.organisation.salescataloge.exception.SalesCatalogeNotFoundException;
import org.mifosplatform.organisation.salescataloge.serialization.SalesCatalogeCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.salescatalogemapping.domain.SalesCatalogeMapping;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanDetails;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
public class SalesCatalogeWritePlatformServiceImpl implements SalesCatalogeWritePlatformService{

	private final PlatformSecurityContext context;
	private final SalesCatalogeCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final SalesCatalogeRepository salescatalogeRepository;
	private final PlanRepository planRepository;
	
	@Autowired
	public SalesCatalogeWritePlatformServiceImpl(PlatformSecurityContext context,
			SalesCatalogeCommandFromApiJsonDeserializer apiJsonDeserializer, SalesCatalogeRepository salescatalogeRepository,
			final PlanRepository planRepository) {
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.salescatalogeRepository = salescatalogeRepository;
		this.planRepository = planRepository;
	}

	@Transactional
	@Override
	public CommandProcessingResult create(JsonCommand command) {
	
		try{
			
		this.context.authenticatedUser();
		this.apiJsonDeserializer.validateForCreate(command.json());
		SalesCataloge salescataloge = SalesCataloge.formJson(command);
		final String[] plans = command.arrayValueOfParameterNamed("planDetails");
	    final Set<SalesCatalogeMapping> selectedPlans = assembleSetOfPlans(plans);
	    salescataloge.addPlan(selectedPlans);
		this.salescatalogeRepository.saveAndFlush(salescataloge);
		return new CommandProcessingResultBuilder().withEntityId(salescataloge.getId()).build();
		
		}catch (DataIntegrityViolationException dve) {
		        handleDataIntegrityIssues(command, dve);
		        return  CommandProcessingResult.empty();
		}
	}

	private Set<SalesCatalogeMapping> assembleSetOfPlans(String[] planArray) {

        final Set<SalesCatalogeMapping> allPlans = new HashSet<>();
        if (!ObjectUtils.isEmpty(planArray)) {
            for (final String planId : planArray) {
                final Plan plan = this.planRepository.findOne(Long.valueOf(planId));
                if (plan != null) { 
                	SalesCatalogeMapping detail=new SalesCatalogeMapping(plan.getId());
                allPlans.add(detail);
                }
            }
        }

        return allPlans;
    }


	private void handleDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {
	
         final Throwable realCause = dve.getMostSpecificCause();
    	  throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
                  "Unknown data integrity issue with resource.");
		
	}


	@Override
	public CommandProcessingResult updateSalesCataloge(JsonCommand command, Long salescatalogeId) {

		   try{
			   
			   this.context.authenticatedUser();
			   this.apiJsonDeserializer.validateForCreate(command.json());
			   final SalesCataloge salescataloge = this.retrieveSalesCataloge(salescatalogeId);
			   final Map<String, Object> changes = salescataloge.update(command);
			   
			   if (changes.containsKey("planDetails")) {
	            	final String[] planIds = (String[]) changes.get("planDetails");
	            	final Set<SalesCatalogeMapping> selectedPlans = assembleSetOfPlans(planIds);
	            	salescataloge.addPlan(selectedPlans);
	            }
			   
			   this.salescatalogeRepository.saveAndFlush(salescataloge);
			   /*if(!changes.isEmpty()){
				   this.salescatalogeRepository.save(salescataloge);
			   }*/
			   return new CommandProcessingResultBuilder() //
		       .withCommandId(command.commandId()) //
		       .withEntityId(salescatalogeId) //
		       .with(changes) //
		       .build();
			}catch (DataIntegrityViolationException dve) {
				handleDataIntegrityIssues(command, dve);
			      return new CommandProcessingResult(Long.valueOf(-1));
			  }
	}


	private SalesCataloge retrieveSalesCataloge(Long salescatalogeId) {
		SalesCataloge salescataloge = this.salescatalogeRepository.findOne(salescatalogeId);
		if (salescataloge == null) { throw new SalesCatalogeNotFoundException(salescatalogeId); }
		return salescataloge;
	}

	@Transactional
	@Override
	public CommandProcessingResult deleteSalesCataloge(JsonCommand command, Long salescatalogeId) {

		try{
			this.context.authenticatedUser();
			SalesCataloge salescataloge = this.retrieveSalesCataloge(salescatalogeId);
			if(salescataloge.getIsDeleted()=='Y'){
				throw new SalesCatalogeNotFoundException(salescatalogeId);
			}
			salescataloge.delete();
			this.salescatalogeRepository.saveAndFlush(salescataloge);
			return new CommandProcessingResultBuilder().withEntityId(salescatalogeId).build();
			
		}catch(DataIntegrityViolationException dve){
			handleDataIntegrityIssues(null, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
			
	}
	
}
