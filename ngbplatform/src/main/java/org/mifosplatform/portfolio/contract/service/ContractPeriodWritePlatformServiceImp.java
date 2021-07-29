package org.mifosplatform.portfolio.contract.service;

import java.util.Map;

import org.mifosplatform.infrastructure.codes.exception.CodeNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.contract.serialization.ContractCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


@Service
public class ContractPeriodWritePlatformServiceImp implements ContractPeriodWritePlatformService {

	private final PlatformSecurityContext context;
	private final ContractCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ContractRepository subscriptionRepository;

	@Autowired
	public ContractPeriodWritePlatformServiceImp(final PlatformSecurityContext context,
			final ContractRepository subscriptionRepository,
			final ContractCommandFromApiJsonDeserializer fromApiJsonDeserializer) {
		this.context = context;
		this.subscriptionRepository = subscriptionRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
	}

	@Override
	public CommandProcessingResult createContract(final JsonCommand command) {
		try {
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final Contract contract = Contract.fromJson(command);
			this.subscriptionRepository.save(contract);
			return new CommandProcessingResult(contract.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("contract_period_key")) {
			final String name = command.stringValueOfParameterNamed("subscriptionPeriod");
			throw new PlatformDataIntegrityException("error.msg.contract.code.duplicate.name","A code with name '" + name + "' already exists", name);
		} else if (realCause.getMessage().contains("dur_uni_code")) {

			throw new PlatformDataIntegrityException("error.msg.contract.type.with.given.units.already exists","A contract type with given units already exists", "subscriptionType");
		}

		throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue","Unknown data integrity issue with resource: "+ realCause.getMessage());

	}

	@Override
	public CommandProcessingResult updateContract(final Long contractId,final JsonCommand command) {
		
		try {
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final Contract contract = retrieveCodeBy(contractId);
			final Map<String, Object> changes = contract.update(command);
			if (!changes.isEmpty()) {
				this.subscriptionRepository.saveAndFlush(contract);
			}
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(contractId).with(changes).build();

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private Contract retrieveCodeBy(final Long contractId) {
		final Contract contract = this.subscriptionRepository.findOne(contractId);
		if (contract == null) {
			throw new CodeNotFoundException(contractId.toString());
		}
		return contract;
	}

	@Override
	public CommandProcessingResult deleteContract(final Long contractId) {
		
		context.authenticatedUser();
		final Contract contract = retrieveCodeBy(contractId);
		contract.delete();
		this.subscriptionRepository.save(contract);
		return new CommandProcessingResultBuilder().withEntityId(contractId).build();
	}

}
