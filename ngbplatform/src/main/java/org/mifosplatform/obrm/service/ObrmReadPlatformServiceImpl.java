package org.mifosplatform.obrm.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.obrm.api.ObrmApiConstants;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanDetails;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.product.domain.Product;
import org.mifosplatform.portfolio.product.domain.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObrmReadPlatformServiceImpl implements ObrmReadPlatformService {

	private final ClientReadPlatformService clientReadPlatformService;
	private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	private final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService;
	private final OrderReadPlatformService orderReadPlatformService;
	private final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService;
	private final ProductRepository productRepository;
	private final PlanRepository planRepository;
	private final PriceRepository priceRepository;

	@Autowired
	public ObrmReadPlatformServiceImpl(
			ClientReadPlatformService clientReadPlatformService,
			final ClientServiceReadPlatformService clientServiceReadPlatformService,
			final OneTimeSaleReadPlatformService oneTimeSaleReadPlatformService,
			final OrderReadPlatformService orderReadPlatformService,
			final ObrmReadWriteConsiliatrService obrmReadWriteConsiliatrService,
			final ProductRepository productRepository,
			final PlanRepository planRepository,
			final PriceRepository priceRepository) {

		this.clientReadPlatformService = clientReadPlatformService;
		this.clientServiceReadPlatformService = clientServiceReadPlatformService;
		this.oneTimeSaleReadPlatformService = oneTimeSaleReadPlatformService;
		this.orderReadPlatformService = orderReadPlatformService;
		this.obrmReadWriteConsiliatrService = obrmReadWriteConsiliatrService;
		this.productRepository=productRepository;
		this.planRepository=planRepository;
		this.priceRepository=priceRepository;
	}

	@Override
	public ClientData retriveClientTotalData(String key, String value) {
		try {
			ClientData clientData = this.clientReadPlatformService
					.retrieveClientForcrm(key, value);
			String result = this.obrmReadWriteConsiliatrService
					.processObrmRequest(ObrmApiConstants.READCLIENT_OPCODE,
							clientData.obrmRequestInput());
			if (result != null) {
				clientData = ClientData.fromJson(result, clientData);

				// client service data preparation
				List<ClientServiceData> clientServiceDatas = this.clientServiceReadPlatformService
						.retriveClientServices(clientData.getId());
				if (!clientServiceDatas.isEmpty()) {
					clientServiceDatas = ClientServiceData.fromOBRMJson(result,
							clientServiceDatas);
					clientData.setClientServiceData(clientServiceDatas);
				}

				// onetimesale data preparation
				List<OneTimeSaleData> oneTimeSaleDatas = this.oneTimeSaleReadPlatformService
						.retrieveClientOneTimeSalesData(clientData.getId());
				if (!oneTimeSaleDatas.isEmpty()) {
					oneTimeSaleDatas = OneTimeSaleData.fromOBRMJson(result,
							oneTimeSaleDatas);
					clientData.setOneTimeSaleData(oneTimeSaleDatas);
				}

				// orders data preparation
				List<OrderData> orderDatas = this.orderReadPlatformService
						.retrieveClientOrderDetails(clientData.getId());
				if (!orderDatas.isEmpty()) {
					orderDatas = OrderData.fromOBRMJson(result, orderDatas);
					clientData.setOrderData(orderDatas);
				}

				return clientData;
			} else {
				throw new PlatformDataIntegrityException("", "");
			}
		} catch (JSONException e) {
			throw new PlatformDataIntegrityException(
					"error.msg.jsonexception.occured", e.getMessage());
		} catch (ParseException e) {
			throw new PlatformDataIntegrityException(
					"error.msg.parse.exception.occured", e.getMessage());
		}
	}

	
	@SuppressWarnings("null")
	@Override
	public String syncPlanToNGB(String key, String value) {
		/*String result=null;
		try{
			result = this.obrmReadWriteConsiliatrService.processObrmRequest(ObrmApiConstants.READPLAN_OPCODE,null);
			JSONObject object = new JSONObject(result);
			object=object.getJSONObject("brm:COB_OP_CUST_SEARCH_PLAN_outputFlist");
			JSONArray plansArray=null;
			if(object.optJSONArray("brm:PLAN")!=null){
				plansArray = object.optJSONArray("brm:PLAN");
			}else{
				plansArray = new JSONArray("["+object.optJSONObject("brm:PLAN")+"]");
			}
			for(int i=0;i<plansArray.length();i++){
				JSONObject planObject=plansArray.getJSONObject(i);
				Plan plan=Plan.fromCelcomJson(planObject);
				Long planPoid=plan.getPlanPoid();
				Plan planNGB=this.planRepository.findwithPlanPoid(planPoid);
				if(planNGB==null){
					planNGB=plan;
				}else{
					Set<PlanDetails> UpdatedList=planNGB.updatePlanDetails(planNGB.getPlanDetails());
					planNGB.update(plan);
					planNGB.setPlanDetails(UpdatedList);
				}
				this.planRepository.save(plan);
				JSONArray dealsArray;
				if(object.optJSONArray("brm:DEALS")!=null){
					dealsArray = planObject.optJSONArray("brm:DEALS");
				}else{
					dealsArray = new JSONArray("["+planObject.optJSONObject("brm:DEALS")+"]");
				}
				for(int j=0;j<dealsArray.length();j++){
					JSONObject deals=dealsArray.getJSONObject(j);
					JSONArray productsArray;
					if(object.optJSONArray("brm:PRODUCTS")!=null){
						productsArray = deals.optJSONArray("brm:PRODUCTS");
					}else{
						productsArray = new JSONArray("["+deals.optJSONObject("brm:PRODUCTS")+"]");
					}
					for(int k=0;k<productsArray.length();k++){
						
						JSONObject productObject=productsArray.getJSONObject(k);
						Product product = Product.fromCelcomJson(productObject);
						Long productPoid=product.getProductPoid();
						Product productNGB=this.productRepository.findOneByProductPoid(productPoid);
						if(productNGB !=null){
							productNGB.update(product);
						}else{
							productNGB = product;
						}
						this.productRepository.save(product);
						
						Price price = Price.fromCelcomJson(productObject,plan.getId(),product.getId());
						List<Price> priceListNGB=this.priceRepository.findOneByPlanAndProduct(plan.getId(),product.getId());
						List<Price> priceUpdatedList=null;
						if(priceListNGB==null){
							Price priceNGB=price;
							this.priceRepository.save(priceNGB);
						}
						else{
							for(Price priceNGB: priceListNGB){
								priceNGB=priceNGB.update(price);
								priceUpdatedList.add(priceNGB); 
							}
							this.priceRepository.save(priceUpdatedList);
						}
					}
				}
			}
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage());
		}*/
		/*	
			
			List<Product> productList = Product.fromCelcomJson(result);
		List<Product> products = new ArrayList<Product>();
		for(Product product:productList){
			Long productPoid=product.getProductPoid();
			Product productNGB=this.productRepository.findOneByProductPoid(productPoid);
			if(productNGB !=null){
				productNGB.update(product);
			}else{
				productNGB = product;
			}
			products.add(productNGB);
		}
		if(!products.isEmpty()){
			this.productRepository.save(products);
		}
		
		
		List<Plan> planList=Plan.fromCelcomJson(result);
		List<Plan> planListSaveNGB=null;
		for(Plan plan:planList){
			Long planPoid=plan.getPlanPoid();
			Plan planNGB=this.planRepository.findwithPlanPoid(planPoid);
			if(planNGB==null){
				planNGB=plan;
			}else{
				Set<PlanDetails> UpdatedList=planNGB.updatePlanDetails(planNGB.getPlanDetails());
				planNGB.update(plan);
				planNGB.setPlanDetails(UpdatedList);
			}
			planListSaveNGB.add(planNGB);
		}
		if(!planListSaveNGB.isEmpty()){
			this.planRepository.save(planListSaveNGB);
		}
		
		
		List<Price> priceList= Price.fromJson(result,plan.getId());
		for(Price price:priceList){
			List<Price> priceUpdatedList=null;
			Long productId=price.getProductId();
			Long planId=price.getPlanCode();
			List<Price> priceListNGB=this.priceRepository.findOneByPlanAndProduct(planId, productId);
			if(priceListNGB==null){
				Price priceNGB=price;
				this.priceRepository.save(priceNGB);
			}
			else{
				for(Price priceNGB: priceListNGB){
					priceNGB=priceNGB.update(price);
					priceUpdatedList.add(priceNGB); 
				}
			}
			if(!priceUpdatedList.isEmpty()){
				this.priceRepository.save(priceUpdatedList);
			}
		}
	*/	
		return null;
	}
}
