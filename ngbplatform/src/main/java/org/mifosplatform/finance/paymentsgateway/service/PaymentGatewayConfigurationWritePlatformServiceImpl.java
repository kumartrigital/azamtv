package org.mifosplatform.finance.paymentsgateway.service;

import java.util.Map;

import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.serialization.ConfigurationFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author ashokreddy
 *
 */
@Service
public class PaymentGatewayConfigurationWritePlatformServiceImpl implements PaymentGatewayConfigurationWritePlatformService {

	
	private final PlatformSecurityContext context;
    private final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository;
    private final ConfigurationFromApiJsonDeserializer configurationFromApiJsonDeserializer;

    @Autowired
    public PaymentGatewayConfigurationWritePlatformServiceImpl(final PlatformSecurityContext context,
            final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository,
            final ConfigurationFromApiJsonDeserializer configurationFromApiJsonDeserializer) {
        this.context = context;
        this.paymentGatewayConfigurationRepository = paymentGatewayConfigurationRepository;
        this.configurationFromApiJsonDeserializer=configurationFromApiJsonDeserializer;
    }
    
	@Transactional
	@Override
	public CommandProcessingResult updatePaymentGatewayConfig(final Long configId, JsonCommand command) {
		
        try {
        	this.context.authenticatedUser();
            this.configurationFromApiJsonDeserializer.validateForUpdate(command);
            final PaymentGatewayConfiguration configItemForUpdate = this.paymentGatewayConfigurationRepository.findOne(configId);
            final Map<String, Object> changes = configItemForUpdate.update(command);
            this.paymentGatewayConfigurationRepository.save(configItemForUpdate);

            return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(configId).with(changes).build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command,dve);
            return CommandProcessingResult.empty();
        }
	}
	
	private void handleDataIntegrityIssues(final JsonCommand command,final DataIntegrityViolationException dve) {

        final Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("name_config")) {
            final String username = command.stringValueOfParameterNamed(ConfigurationConstants.NAME);
            final StringBuilder defaultMessageBuilder = new StringBuilder("Name with").append(username)
                    .append(" already exists.");
            throw new PlatformDataIntegrityException("error.msg.smtp.duplicate.name", defaultMessageBuilder.toString(), "username",
                    username);
        }
    
        throw new PlatformDataIntegrityException("error.msg.PaymentGatewayConfiguration.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }

	@Override
	public CommandProcessingResult createPaymentGatewayConfig(JsonCommand command) {

		try{	
			this.context.authenticatedUser();
			final String name = command.stringValueOfParameterNamed(ConfigurationConstants.NAME);
			final String value = command.stringValueOfParameterNamed(ConfigurationConstants.VALUE);
			final boolean enabled = command.booleanPrimitiveValueOfParameterNamed(ConfigurationConstants.ENABLED);
		
			final PaymentGatewayConfiguration paymentGatewayConfiguration = new PaymentGatewayConfiguration(name, enabled, value);
	        
			this.paymentGatewayConfigurationRepository.save(paymentGatewayConfiguration);
			
			return new CommandProcessingResultBuilder().withEntityId(paymentGatewayConfiguration.getId()).build();
		}
		catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command,dve);
            return CommandProcessingResult.empty();
        }
	}
	
}
