package org.mifosplatform.finance.depositandrefund.service;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.depositandrefund.domain.DepositAndRefund;
import org.mifosplatform.finance.depositandrefund.domain.DepositAndRefundRepository;
import org.mifosplatform.finance.depositandrefund.exception.ItemQualityAndStatusException;
import org.mifosplatform.finance.depositandrefund.serialization.DepositeCommandFromApiJsonDeserializer;
import org.mifosplatform.finance.payments.domain.PaypalEnquireyRepository;
import org.mifosplatform.finance.payments.exception.ReceiptNoDuplicateException;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.organisation.partner.domain.PartnerBalanceRepository;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;

@Service
public class RefundWritePlatformServiceImpl implements RefundWritePlatformService {

	private final static Logger logger = LoggerFactory.getLogger(RefundWritePlatformServiceImpl.class);
  
	private final PlatformSecurityContext context;
	private final ClientBalanceRepository clientBalanceRepository;
	private final PartnerBalanceRepository partnerBalanceRepository;
	private final DepositAndRefundRepository depositAndRefundRepository;
	private final OrderReadPlatformService orderReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	private final DepositeCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final FromJsonHelper fromJsonHelper;


	@Autowired
	public RefundWritePlatformServiceImpl(final PlatformSecurityContext context,final ClientBalanceRepository clientBalanceRepository,
			final ConfigurationRepository globalConfigurationRepository,final PaypalEnquireyRepository paypalEnquireyRepository,
			final PartnerBalanceRepository partnerBalanceRepository, final DepositAndRefundRepository depositAndRefundRepository, 
			final OrderReadPlatformService orderReadPlatformService, 
			final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,
			final DepositeCommandFromApiJsonDeserializer fromApiJsonDeserializer,final FromJsonHelper fromJsonHelper) {
		
		this.context = context;
		this.clientBalanceRepository=clientBalanceRepository;
		this.partnerBalanceRepository = partnerBalanceRepository;
		this.depositAndRefundRepository = depositAndRefundRepository;
		this.orderReadPlatformService = orderReadPlatformService;
		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;
		this.fromApiJsonDeserializer =  fromApiJsonDeserializer;
		this.fromJsonHelper = fromJsonHelper;
		
	}

	@Transactional
	@Override
	public CommandProcessingResult createRefund(final JsonCommand command, Long depositId) {
		
		try {
			
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validaForCreateRefund(command.json());
			final BigDecimal refundAmount = command.bigDecimalValueOfParameterNamed("refundAmount");
			final Long refundMode = command.longValueOfParameterNamed("refundMode");
			DepositAndRefund deposit = this.depositAndRefundRepository.findOne(depositId);
			Long clientId = deposit.getClientId();
			Long itemId = deposit.getItemId();
			Long saleId = deposit.getRefId();
			BigDecimal refundBalance = deposit.getDebitAmount();
			
			
			ClientBalance clientBalance = clientBalanceRepository.findByClientId(clientId);
			BigDecimal clientBalanceAmount = clientBalance.getBalanceAmount();
			
			/*final List<AllocationDetailsData> allocationData = this.oneTimeSaleReadPlatformService.retrieveUnAllocationDetails(saleId, clientId);
			if(!allocationData.isEmpty()){
				
				String serialNumber = allocationData.get(0).getSerialNo();
				String itemQuality = allocationData.get(0).getQuality();
				String hardwareStatus = allocationData.get(0).getHardwareStatus();
				
				final Long activeorders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(clientId,serialNumber);
	    	   	if(activeorders!= 0){
	    	   		throw new ActivePlansFoundException();
	    	   	}
	    	   	if(!itemQuality.equalsIgnoreCase("Good")){
	    	   		throw new ItemQualityAndStatusException(itemQuality);
	    	   	}
	    	   	if(!hardwareStatus.equalsIgnoreCase("unallocated")){
	    	   		throw new ItemQualityAndStatusException();
	    	   	}
			}*/
    	   	
			if(clientBalanceAmount.intValue() == 0){
				processDepositAndRefund(clientId, itemId, refundAmount, "Refund", "Credit", depositId,null);
				processDepositAndRefund(clientId, itemId, refundAmount, "Payment Towards Refund Entry", "Debit", depositId,refundMode);
				
			}else if(clientBalanceAmount.intValue() <= refundBalance.intValue()){
				//MathContext mc = new MathContext(4);
				BigDecimal amountValue = null;
				if(clientBalanceAmount.intValue() > 0){
					amountValue= refundBalance.subtract(clientBalanceAmount);
					processDepositAndRefund(clientId, itemId, amountValue, "Refund", "Credit", depositId,null);
					processDepositAndRefund(clientId, itemId, amountValue, "Payment Towards Refund Entry", "Debit", depositId,refundMode);
					processDepositAndRefund(clientId, itemId, clientBalanceAmount, "Refund Adjustment towards Service Balance", "Credit", depositId,null);
					
					JSONObject clientBalanceObject = new JSONObject();
					try {
					clientBalanceObject.put("clientId",clientId);
					clientBalanceObject.put("amount",clientBalanceAmount);
					clientBalanceObject.put("isWalletEnable", "N");
					clientBalanceObject.put("paymentType", "CREDIT");
					clientBalanceObject.put("locale", "en");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
					JsonCommand clientBalanceCommand = new JsonCommand(null,clientServiceElementNew.toString(),clientServiceElementNew,fromJsonHelper,null,null,null,null,null,null,null,null,null,null,null,null);  
					
					
					// Update Client Balance
					clientBalance.updateBalance(clientBalanceCommand);
					this.clientBalanceRepository.saveAndFlush(clientBalance);
				}else{
					
					processDepositAndRefund(clientId, itemId, refundAmount, "Refund", "Credit", depositId,null);
					processDepositAndRefund(clientId, itemId, refundAmount, "Payment Towards Refund Entry", "Debit", depositId,refundMode);
				}
				
				deposit.setIsRefund('Y');
				this.depositAndRefundRepository.saveAndFlush(deposit);
				/*clientBalance.setBalanceAmount(BigDecimal.valueOf(0));
				this.clientBalanceRepository.saveAndFlush(clientBalance);*/
				
			}else{
				throw new ItemQualityAndStatusException(clientBalanceAmount);
			}
			
			
			
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId((long)1).withClientId(command.entityId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
			}
		}

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
		final Throwable realCause = dve.getMostSpecificCause(); 
		if(realCause.getMessage().contains("receipt_no")){
		          throw new ReceiptNoDuplicateException(command.stringValueOfParameterNamed("receiptNo"));
		}
		
		logger.error(dve.getMessage(), dve);
	}
	
	private void processDepositAndRefund(Long clientId, Long itemId, BigDecimal refundAmount,
				String description, String transType, Long depositId,Long refundMode){
		DepositAndRefund refund = DepositAndRefund.fromJson(clientId, itemId, refundAmount, description, transType);
		refund.setRefId(depositId);
		refund.setRefundMode(refundMode);
		this.depositAndRefundRepository.saveAndFlush(refund);
	}

}




