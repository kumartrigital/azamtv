package org.mifosplatform.provisioning.preparerequest.service;


import org.mifosplatform.cms.eventorder.service.PrepareRequestWriteplatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequest;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequsetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class PrepareRequestWriteplatformServiceImpl implements PrepareRequestWriteplatformService{
	private final PrepareRequsetRepository prepareRequsetRepository; 

	@Autowired
	public PrepareRequestWriteplatformServiceImpl(final PrepareRequsetRepository prepareRequsetRepository	) {
		this.prepareRequsetRepository=prepareRequsetRepository;

	}

	@Override
	public CommandProcessingResult prepareNewRequest(final Order order, final Plan plan, final String requestType) {
  
		try{
		
			PrepareRequest prepareRequest=new PrepareRequest(order.getClientId(), order.getId(), requestType, plan.getProvisionSystem(), 'N', "NONE", plan.getPlanCode());
			this.prepareRequsetRepository.save(prepareRequest);
			
			
			return CommandProcessingResult.resourceResult(prepareRequest.getId(), order.getId());
          			
		} catch (DataIntegrityViolationException dve) {
			return CommandProcessingResult.empty();
		
	}
	}

	@Override
	public void prepareRequestForRegistration(Long clientId, String action,String provisioningSystem) {
		
		PrepareRequest prepareRequest=new PrepareRequest(clientId,Long.valueOf(0),action,provisioningSystem, 'N', "NONE",String.valueOf(0));
		this.prepareRequsetRepository.save(prepareRequest);
		
	}

	

}
