package org.mifosplatform.finance.depositandrefund.service;


import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.finance.chargeorder.service.ChargingOrderWritePlatformService;
import org.mifosplatform.finance.depositandrefund.domain.DepositAndRefund;
import org.mifosplatform.finance.depositandrefund.domain.DepositAndRefundRepository;
import org.mifosplatform.finance.depositandrefund.exception.InvalidDepositException;
import org.mifosplatform.finance.depositandrefund.serialization.DepositeCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.feemaster.data.FeeMasterData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;

/**
 * @author hugo
 * 
 */
@Service
public class DepositeWritePlatformServiceImp implements DepositeWritePlatformService {

	private final static Logger LOGGER = LoggerFactory.getLogger(DepositeWritePlatformServiceImp.class);

	private final PlatformSecurityContext context;
	private final DepositeCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final DepositAndRefundRepository depositAndRefundRepository;
	private final DepositeReadPlatformService depositeReadPlatformService;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final FromJsonHelper fromJsonHelper;

	
	
	@Autowired
	public DepositeWritePlatformServiceImp(final PlatformSecurityContext context, final DepositAndRefundRepository depositAndRefundRepository,
			final DepositeCommandFromApiJsonDeserializer apiJsonDeserializer,final DepositeReadPlatformService depositeReadPlatformService,
			final ChargingOrderWritePlatformService chargingOrderWritePlatformService,final FromJsonHelper fromJsonHelper) {
		
		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
		this.depositAndRefundRepository = depositAndRefundRepository;
		this.depositeReadPlatformService = depositeReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;

	}

	@Transactional
	@Override
	public CommandProcessingResult createDeposite(final JsonCommand command) {
		
		try{
			context.authenticatedUser();
			this.apiJsonDeserializer.validaForCreate(command.json());
			final String feeId = command.stringValueOfParameterNamed("feeId");
			final String clientId = command.stringValueOfParameterNamed("clientId");
		    FeeMasterData  feeMasterData= this.depositeReadPlatformService.retrieveDepositDetails(Long.valueOf(feeId),Long.valueOf(clientId));
		    if(feeMasterData == null){
		    	throw new InvalidDepositException(Long.valueOf(feeId));
		    }
		    DepositAndRefund depositfund=new DepositAndRefund(Long.valueOf(clientId),Long.valueOf(feeId),feeMasterData.getDefaultFeeAmount(),DateUtils.getDateOfTenant(),feeMasterData.getTransactionType());
			this.depositAndRefundRepository.save(depositfund);
			
			JSONObject clientBalanceObject = new JSONObject();
			try {
			clientBalanceObject.put("clientId",Long.valueOf(clientId));
			clientBalanceObject.put("amount",feeMasterData.getDefaultFeeAmount());
			clientBalanceObject.put("isWalletEnable", false);
			clientBalanceObject.put("clientServiceId",Long.valueOf(0) );
			clientBalanceObject.put("locale", "en");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
			JsonCommand clientBalanceCommand = new JsonCommand(null,clientServiceElementNew.toString(),clientServiceElementNew,fromJsonHelper,null,null,null,null,null,null,null,null,null,null,null,null);  
			
			
			// Update Client Balance
			this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(depositfund.getId()).build();
			
		}catch(final DataIntegrityViolationException dve){
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1L));
		}
	
	}

	private void handleDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
	
		
	}
	
}
