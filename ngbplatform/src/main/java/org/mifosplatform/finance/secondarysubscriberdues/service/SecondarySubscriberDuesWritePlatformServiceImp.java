package org.mifosplatform.finance.secondarysubscriberdues.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.cms.journalvoucher.domain.JournalVoucher;
import org.mifosplatform.cms.journalvoucher.domain.JournalVoucherDetails;
import org.mifosplatform.cms.journalvoucher.domain.JournalvoucherDetailsRepository;
import org.mifosplatform.cms.journalvoucher.domain.JournalvoucherRepository;
import org.mifosplatform.finance.adjustment.service.AdjustmentWritePlatformService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.internalTransactions.domain.InternalTransaction;
import org.mifosplatform.organisation.internalTransactions.domain.InternalTransactionRepository;
import org.mifosplatform.organisation.officeadjustments.service.OfficeAdjustmentsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;

@Service
public class SecondarySubscriberDuesWritePlatformServiceImp implements SecondarySubscriberDuesWritePlatformService{
	
	private final FromJsonHelper fromJsonHelper;
	private final AdjustmentWritePlatformService adjustmentWritePlatformService;
	private final OfficeAdjustmentsWritePlatformService officeAdjustmentsWritePlatformService;
	private final InternalTransactionRepository internalTransactionRepository;
	private final JournalvoucherDetailsRepository journalvoucherDetailsRepository;
	private final JournalvoucherRepository journalvoucherRepository;
	
	
	@Autowired
	public SecondarySubscriberDuesWritePlatformServiceImp(final FromJsonHelper fromJsonHelper,final AdjustmentWritePlatformService adjustmentWritePlatformService,
		   final OfficeAdjustmentsWritePlatformService officeAdjustmentsWritePlatformService,final InternalTransactionRepository internalTransactionRepository,
		   final JournalvoucherDetailsRepository journalvoucherDetailsRepository,final JournalvoucherRepository journalvoucherRepository){
		
		this.fromJsonHelper = fromJsonHelper;
		this.adjustmentWritePlatformService = adjustmentWritePlatformService;
		this.officeAdjustmentsWritePlatformService = officeAdjustmentsWritePlatformService;
		this.internalTransactionRepository = internalTransactionRepository;
		this.journalvoucherDetailsRepository = journalvoucherDetailsRepository;
		this.journalvoucherRepository = journalvoucherRepository;
	}
	
	
	@Override
	public CommandProcessingResult secondarySubscriberDues(Long clientId,Long officeId, BigDecimal price) {
	
	try {
		JSONObject officeObject = new JSONObject();
		JSONObject clientObject = new JSONObject();
		JSONObject internalTransactionObject = new JSONObject();
		String dateFormat = "dd MMMM yyyy";
		String transactionDate = new SimpleDateFormat(dateFormat).format(DateUtils.getDateOfTenant());
		


			//This is client adjustments
    		 
		     clientObject.put("adjustment_type", "CREDIT");
		     clientObject.put("withtax", false);	            		    
			 clientObject.put("adjustment_code", 28);
		     clientObject.put("amount_paid", price);
		     clientObject.put("Remarks","amount transaction" );
		     clientObject.put("locale", "en");
		     clientObject.put("dateFormat",dateFormat);
		     clientObject.put("adjustment_date",transactionDate);
		  
		     /*final CommandWrapper commandRequest = new CommandWrapperBuilder().createAdjustment(clientId).withJson(clientObject.toString()).build(); //
		     final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		    */
		     final JsonElement clientObjectElement = fromJsonHelper.parse(clientObject.toString());
			  JsonCommand clientObjectCommand = new JsonCommand(null,clientObjectElement.toString(),clientObjectElement, fromJsonHelper,
			  null, clientId, null, null, null, null, null, null, null, null, 
			  null, null);
			  this.adjustmentWritePlatformService.createAdjustment(clientObjectCommand);
		     
		   //end
		     
		   //This is for office adjustments
			  officeObject.put("adjustmentCode",28);
			  officeObject.put("amountPaid",price);
			  officeObject.put("adjustmentType", "DEBIT");
			  officeObject.put("remarks","amount transaction" );
			  officeObject.put("dateFormat",dateFormat);
			  officeObject.put("adjustmentDate",transactionDate);
			  officeObject.put("locale", "en");
			
			  
			  /*final CommandWrapper commandRequester = new CommandWrapperBuilder().createOfficeAdjustment(officeId).withJson(officeObject.toString()).build();
			  final CommandProcessingResult results = this.commandsSourceWritePlatformService.logCommandSource(commandRequester);
		       */
		      final JsonElement officeObjectElement = fromJsonHelper.parse(officeObject.toString());
			  JsonCommand officeObjectCommand = new JsonCommand(null,officeObjectElement.toString(),officeObjectElement, fromJsonHelper,
					null, officeId, null, null, null, null, null, null, null, null, 
					null, null);
			  this.officeAdjustmentsWritePlatformService.createOfficeAdjustment(officeObjectCommand);
			    
		      //end
		  
		  /*//This is for internal transaction table
		      internalTransactionObject.put("officeId", officeId);
		      internalTransactionObject.put("clientId", clientId);
		      internalTransactionObject.put("transactionAmount",price);
		      internalTransactionObject.put("dateFormat",dateFormat);
		      internalTransactionObject.put("transactionDate", transactionDate);
		      internalTransactionObject.put("locale", "en");
		      
		      
		      final JsonElement internalTransactionElement = fromJsonHelper.parse(internalTransactionObject.toString());
				  JsonCommand internalTransactionCommand = new JsonCommand(null,internalTransactionElement.toString(), internalTransactionElement, fromJsonHelper,
						null, null, null, null, null, null, null, null, null, null, 
						null, null);
		      final InternalTransaction internalTransaction = InternalTransaction.fromJson(internalTransactionCommand);
		      this.internalTransactionRepository.saveAndFlush(internalTransaction);
		  //end
*/		      
		  //This is for journal table
		  
		      JournalVoucher journalVoucher=new JournalVoucher(DateUtils.getDateOfTenant(),"secondarySubscriberDues");
				this.journalvoucherRepository.saveAndFlush(journalVoucher);
				JournalVoucherDetails journalVoucherDetail=new JournalVoucherDetails(journalVoucher.getId(),officeId.toString(),"Entity","Debit","secondarySubscriberDues",price.doubleValue());
				JournalVoucherDetails journalVoucherDetails=new JournalVoucherDetails(journalVoucher.getId(),clientId.toString(),"Client","Credit","secondarySubscriberDues",price.doubleValue());
				this.journalvoucherDetailsRepository.saveAndFlush(journalVoucherDetail);
				this.journalvoucherDetailsRepository.saveAndFlush(journalVoucherDetails); 
		      
		  //end
	
	 } catch (JSONException e) {
			throw new PlatformDataIntegrityException("Configuration value in JSON is not formed Correctly", "", "");
		}
		return null;
		
		
	}

}
