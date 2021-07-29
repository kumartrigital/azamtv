package org.mifosplatform.portfolio.slabRate.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.planprice.data.PriceData;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.adjustment.service.AdjustmentWritePlatformService;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.clientbalance.domain.ClientBalanceRepository;
import org.mifosplatform.finance.officebalance.data.OfficeBalanceData;
import org.mifosplatform.finance.officebalance.domain.OfficeBalanceRepository;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.organisation.internalTransactions.domain.InternalTransactionRepository;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.organisation.officeadjustments.service.OfficeAdjustmentsWritePlatformService;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.order.domain.OrderPrice;
import org.mifosplatform.portfolio.order.service.OrderDetailsReadPlatformServices;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.slabRate.data.SlabRateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.tools.xjc.generator.bean.ImplStructureStrategy.Result;

@Service
public class SlabRateWritePlatformServiceImpl implements SlabRateWritePlatformService{

	private final PlanRepository planRepository;
	private final OrderDetailsReadPlatformServices orderDetailsReadPlatformServices;
	private final SlabRateReadPlatformService slabRateReadPlatformService;
	private final OfficeReadPlatformService officeReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	private final OfficeBalanceRepository officeBalanceRepository;
	private final ClientBalanceRepository clientBalanceRepository;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final FromJsonHelper fromJsonHelper;
	
	@Autowired
	public SlabRateWritePlatformServiceImpl(final PlanRepository planRepository,final OrderDetailsReadPlatformServices orderDetailsReadPlatformServices,
			final SlabRateReadPlatformService slabRateReadPlatformService,final OfficeReadPlatformService officeReadPlatformService,final ClientReadPlatformService clientReadPlatformService,
			ConfigurationRepository configurationRepository,final OfficeBalanceRepository officeBalanceRepository,final ClientBalanceRepository clientBalanceRepository,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,final FromJsonHelper fromJsonHelper){
		
		this.planRepository = planRepository;
		this.orderDetailsReadPlatformServices = orderDetailsReadPlatformServices;
		this.slabRateReadPlatformService = slabRateReadPlatformService;
		this.clientReadPlatformService = clientReadPlatformService;
		this.officeReadPlatformService = officeReadPlatformService;
		this.configurationRepository   = configurationRepository;
		this.officeBalanceRepository = officeBalanceRepository;
		this.clientBalanceRepository = clientBalanceRepository;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.fromJsonHelper = fromJsonHelper;
	}

	@Override 
	public CommandProcessingResult Calculation(String jsonRequestBody) {
		try {
			
			List<PriceData> datas = new ArrayList<PriceData>();
			OrderPrice price = null;
			Long count = null;
			JSONObject obj = new JSONObject(jsonRequestBody);
			//JsonCommand command = new JsonCommand(null, jsonRequestBody, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
			String clientId=obj.optString("clientId");
			JSONArray nonBouqueProducts = obj.optJSONArray("nonBouqueProducts");
			JSONArray bouqueProducts = obj.optJSONArray("bouqueProducts");
			
			Plan plan = this.planRepository.findPlanCheckDeletedStatus(obj
					.getLong("planCode"));
			Long bouqueProductsCount =obj.optLong("bouqueProductsCount");
			Long nonBouqueProductsCount = obj.optLong("nonBouqueProductsCount");
			count = Long.valueOf(bouqueProductsCount)+Long.valueOf(nonBouqueProductsCount);
			//Order order=Order.fromJson(Long.valueOf(clientId), command);
			datas=this.orderDetailsReadPlatformServices.retrieveAllPrices(obj.getLong("planCode"),obj.getString("paytermCode"),Long.valueOf(clientId));
			BigDecimal nonBouqueprices = new BigDecimal(0);
			BigDecimal Bouqueprices = new BigDecimal(0);
			BigDecimal total = new BigDecimal(0);
			BigDecimal slabPrice = null;
			for (PriceData data : datas) {
				
				Long productId = data.getProductId();
					if(productId == 0){
						List<SlabRateData> slabRates = this.slabRateReadPlatformService.retrieveSlabRates();
						
						for(SlabRateData slabrate : slabRates ){
							if(count >= Long.valueOf(slabrate.getSlabFrom()) && count <= Long.valueOf(slabrate.getSlabTo())){								
								 slabPrice = BigDecimal.valueOf(slabrate.getRate());							
							}							
						}
						
					}
					for(int j=0;j<nonBouqueProducts.length();j++){
						JSONObject productObject =  null;
						productObject = nonBouqueProducts.optJSONObject(j);
						Long product =productObject.optLong("productId");
						if(productId.equals(product)){
						nonBouqueprices = nonBouqueprices.add(data.getPrice());	
					
							
						}
					}
					for(int j=0;j<bouqueProducts.length();j++){
						JSONObject productObject =  null;
						productObject = bouqueProducts.optJSONObject(j);
						Long product =productObject.optLong("productId");
						if(productId.equals(product)){
							Bouqueprices = Bouqueprices.add(data.getPrice());	
					
							
						}
					}
					
			}
		 total = total.add(slabPrice);
		 total = total.add(Bouqueprices);
		 total = total.add(nonBouqueprices);
		 Map<String, Object> returns = new HashMap<String,Object>();
		 returns.put("slabPrice", slabPrice.toString());
		 returns.put("bouqueprices", Bouqueprices.toString());
		 returns.put("nonBouqueprices", nonBouqueprices.toString());
		 returns.put("total", total.toString());
		 return new CommandProcessingResultBuilder()
				 .with(returns) 
                 .build();
		} catch (DataIntegrityViolationException e) {
			handleCodeDataIntegrityIssues(jsonRequestBody, e);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (JSONException e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
	}

	private void handleCodeDataIntegrityIssues(String jsonRequestBody, DataIntegrityViolationException e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CommandProcessingResult prepaidService(Long clientId, BigDecimal price) {
		Configuration prepaidConfiguration=this.configurationRepository.findOneByName(ConfigurationConstants.PREPAID_SERVICE );
		 
		 try {

   		     JSONObject officeObject = new JSONObject();
   		     JSONObject clientObject = new JSONObject();
   		     JSONObject internalTransactionObject = new JSONObject();
      	     BigDecimal value = BigDecimal.ZERO;
      	     BigDecimal clientAmount = null;
      	     BigDecimal officeAmount = null;
      	     BigDecimal clientAmounts = BigDecimal.ZERO;
      	     BigDecimal subBalance=BigDecimal.ZERO;
      	     BigDecimal addBalance=BigDecimal.ZERO;
      	     String dateFormat = "dd MMMM yyyy";
      	     String transactionDate = new SimpleDateFormat(dateFormat).format(DateUtils.getDateOfTenant());
			 String configurations  = prepaidConfiguration.getValue();
			 JSONObject configurationObject = new JSONObject(configurations);
				//if(prepaidConfiguration.isEnabled()){
		                 OfficeData officeData =this.officeReadPlatformService.retriveOfficeDetail(clientId);
			              if(officeData.getBusinessType().equalsIgnoreCase("Primary Points")){
			            	  if(configurationObject.optString("prepaid").equalsIgnoreCase("true")){
			            	  ClientBalanceData clientBalance = this.clientReadPlatformService.findClientBalance(clientId);
			            	  clientAmounts = clientBalance.getBalanceAmount();
			            	  if(clientAmounts == null){
			            		  throw new PlatformDataIntegrityException("No client balance","No client balance","No client balance"); 
			            	  }
			            	  if(clientAmounts.compareTo(value) < 0){
			            		   clientAmount  = clientAmounts.abs();
			            	  }else{
			            		  clientAmount  = clientAmounts.negate();
			            	  }
			            	  if(clientAmount.compareTo(price) < 0){
			            		  throw new PlatformDataIntegrityException("Insufficient client balance","Insufficient client balance","Insufficient client balance"); 
			            	  }
			               }
			              }
			              else{
			            	  if(configurationObject.optString("prepaid").equalsIgnoreCase("true")){
			            		  OfficeBalanceData officeBalanceData = this.officeReadPlatformService.retriveOfficebalanceDetail(officeData.getId());
				            	  if(officeBalanceData.getSubscriberDues()){
					            	  BigDecimal officeAmounts = officeBalanceData.getBalanceAmount();
					            	  if(officeAmounts == null){
					            		  throw new PlatformDataIntegrityException("No office balance","No office balance","No office balance"); 
					            	  }
					            	  if(officeAmounts.compareTo(value) < 0){
					            		  officeAmount  = officeAmounts.abs();
					            	  }else{
					            		  officeAmount  = officeAmounts.negate();
					            	  }
					            	  if(officeAmount.compareTo((price)) < 0){
					            		  throw new PlatformDataIntegrityException("Insufficient office balance","Insufficient office balance","Insufficient office balance"); 
					            	  }
				            	  }else{
				            		  throw new PlatformDataIntegrityException("No SubscriberDue","No SubscriberDue","No SubscriberDue"); 
				            	  }
			            	  }
			              }
				 
		  
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException("Configuration value in JSON is not formed Correctly", "", "");
		}
		
		 return new CommandProcessingResult(Long.valueOf(-1));
	}

}
