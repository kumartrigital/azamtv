package org.mifosplatform.collectionbatch.usageratebalance.service;

import java.math.BigDecimal;

import org.mifosplatform.collectionbatch.usageratebalance.domain.UsageBalance;
import org.mifosplatform.collectionbatch.usageratebalance.domain.UsagebalanceRepository;
import org.mifosplatform.collectionbatch.usageratebalance.serialization.UsageBalanceCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class UsageBalanceWritePlatformServiceImpl implements UsageBalanceWritePlatformService {
	private final static Logger logger = (Logger) LoggerFactory.getLogger(UsageBalanceWritePlatformServiceImpl.class);
	private final PlatformSecurityContext context;
	private final UsageBalanceCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final UsagebalanceRepository usagebalanceRepository;
	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public UsageBalanceWritePlatformServiceImpl(PlatformSecurityContext context,
			UsageBalanceCommandFromApiJsonDeserializer apiJsonDeserializer,
			UsagebalanceRepository usagebalanceRepository,
			final FromJsonHelper fromApiJsonHelper) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.usagebalanceRepository = usagebalanceRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;

	}

	@Override
	public CommandProcessingResult createUsagebalance(JsonCommand command) {
		/*try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			final UsageBalance usageBalance = UsageBalance.formJson(command);
			this.usagebalanceRepository.save(usageBalance);
			return new CommandProcessingResultBuilder().withEntityId(usageBalance.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}*/
		
		
		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			Long ratePlanId = command.longValueOfParameterNamed("ratePlanId");
			final JsonArray rateArray = command.arrayOfParameterNamed("ratebalance").getAsJsonArray();
			UsageBalance usageBalance = null;
			String[]  ratebalance = null;
			ratebalance = new String[rateArray.size()];
			if(rateArray.size() > 0){
				for(int i = 0; i < rateArray.size(); i++){
					ratebalance[i] = rateArray.get(i).toString();
				}
						for (final String ratebalances : ratebalance) {
				final JsonElement element = fromApiJsonHelper.parse(ratebalances);
				final Long glId = fromApiJsonHelper.extractLongNamed("glId",element);
				final Long currencyId = fromApiJsonHelper.extractLongNamed("currencyId",element);
				final Long tierId = fromApiJsonHelper.extractLongNamed("tierId", element);
				final Long rum = fromApiJsonHelper.extractLongNamed("rum", element);
				final Long uom = fromApiJsonHelper.extractLongNamed("uom", element);
				final Long timeperiodId = fromApiJsonHelper.extractLongNamed("timeperiodId", element);
				final Long unit = fromApiJsonHelper.extractLongNamed("unit", element);
				final String ratesting=fromApiJsonHelper.extractStringNamed("rate", element);
				BigDecimal rate=new BigDecimal(ratesting);
				usageBalance= new UsageBalance(ratePlanId,glId,currencyId,tierId,rum,uom,timeperiodId,unit,rate);
			    this.usagebalanceRepository.save(usageBalance);
				
			}	 
		}
			
			
	
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					        .withEntityId(usageBalance.getId()).build();
			
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
		
	}

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		final Throwable realCause = dve.getMostSpecificCause();

		throw new PlatformDataIntegrityException("error.msg.client.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource.");
	}

}
