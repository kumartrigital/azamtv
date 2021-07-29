package org.mifosplatform.organisation.hardwareplanmapping.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.hardwareplanmapping.domain.HardwarePlanMapper;
import org.mifosplatform.organisation.hardwareplanmapping.domain.HardwarePlanMapperRepository;
import org.mifosplatform.organisation.hardwareplanmapping.exception.HardwarePlanmappingNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class HardwarePlanWritePlatformServiceImpl implements HardwarePlanWritePlatformService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(HardwarePlanWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final HardwarePlanMapperRepository hardwarePlanMapperRepository;
	private final HardwarePlanCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final HardwarePlanReadPlatformService hardwarePlanReadPlatformService;

	@Autowired
	public HardwarePlanWritePlatformServiceImpl(
			final PlatformSecurityContext context,
			final HardwarePlanMapperRepository hardwarePlanMapperRepository,
			final HardwarePlanCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final HardwarePlanReadPlatformService hardwarePlanReadPlatformService) {

		this.context = context;
		this.hardwarePlanMapperRepository = hardwarePlanMapperRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.hardwarePlanReadPlatformService = hardwarePlanReadPlatformService;

	}

	@Override
	public CommandProcessingResult createHardwarePlan(JsonCommand command) {

		try {
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final HardwarePlanMapper harwarePlan = HardwarePlanMapper.fromJson(command);
			
			this.hardwarePlanMapperRepository.saveAndFlush(harwarePlan);
			return new CommandProcessingResult(Long.valueOf(harwarePlan.getId()));

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {

		


    	final Throwable realCause = dve.getMostSpecificCause();
    	LOGGER.error(dve.getMessage(), dve);
        if (realCause.getMessage().contains("k_hwplnmapping_all")) {

            throw new PlatformDataIntegrityException("error.msg.item.code.already.configured", "Item Code already Configured with this plan and provisioning system");
            
        } 

        throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");

	}

	@Override
	public CommandProcessingResult updatePlanMapping(final Long planMapId, final JsonCommand command) {

		try {

			this.fromApiJsonDeserializer.validateForUpdate(command.json());		
			final HardwarePlanMapper hardwarePlanMapper = this.retrieveHardwarePlanMappingById(planMapId);
			final Map<String, Object> changes = hardwarePlanMapper.update(command);
			this.hardwarePlanMapperRepository.saveAndFlush(hardwarePlanMapper); 

			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(planMapId).with(changes).build();
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}
	
	private HardwarePlanMapper retrieveHardwarePlanMappingById(final Long planId) {
		final HardwarePlanMapper hardwarePlanMapper = this.hardwarePlanMapperRepository.findOne(planId);
		if (hardwarePlanMapper == null) {
			throw new HardwarePlanmappingNotFoundException(planId);
		}
		return hardwarePlanMapper;
	}

}
