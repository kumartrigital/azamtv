package org.mifosplatform.portfolio.order.service;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderDiscountData;
import org.mifosplatform.portfolio.order.data.OrderHistoryData;
import org.mifosplatform.portfolio.order.data.OrderLineData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.order.data.OrderUssdData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public interface OrderReadPlatformService {

	List<PlanCodeData> retrieveAllPlatformData(Long planId, Long clientId,String state,String country,Long salesCatalogeId,Long clientServiceId);

	List<PaytermData> retrieveAllPaytermData();
	
	List<OrderPriceData> retrieveOrderPriceData(Long orderId);
	
	List<PaytermData> getChargeCodes(Long planCode, Long clientId);
	
	List<OrderPriceData> retrieveOrderPriceDetails(Long orderId, Long clientId);
	
	List<OrderData> retrieveClientOrderDetails(Long clientId);
	
	List<OrderData> retrieveClientServiceOrderDetails(Long clientId,Long clientServiceId);
	
	//List<OrderHistoryData> retrieveOrderHistoryDetails(String orderNo);
	
	List<OrderHistoryData> retrieveOrderHistoryDetails(Long orderId);
	
	List<OrderData> getActivePlans(Long clientId, String planType);
	
	OrderData retrieveOrderDetails(Long orderId);
	
	Long getRetrackId(Long entityId);
	
	String getOSDTransactionType(Long id);
	
	String checkRetrackInterval(Long entityId);
	
	List<OrderLineData> retrieveOrderServiceDetails(Long orderId);
	
	List<OrderDiscountData> retrieveOrderDiscountDetails(Long orderId);

	Long retrieveClientActiveOrderDetails(Long clientId, String serialNo,Long clientServiceId);

	List<OrderData> retrieveCustomerActiveOrders(Long clientId);
	
	List<Long> retrieveOrderActiveAndDisconnectionIds(Long clientId,Long planId);

	List<PlanCodeData> retrieveAllPlatformDatas(String planTypeName);

	OrderData retrieveAllPoidsRelatedToOrder(Long orderId);

	List<PlanCodeData> retrieveDefaultPlatformDatas(String catalogeName,Long planId);

	//Long retriveMaxOrderId(Long clientId);

	OrderData retrieveAllPoidsRelatedToOrderNumbers(String orderNo);

	OrderData retrieveClientServicePoid(Long clientServicePoid);

	Long retriveMaxOrderId();

	Integer insertOrderDetails(Long clientId, Long planId, LocalDate startDate, LocalDate endDate, Long clientServiceId,
			String packageId);
    List<OrderData> orderDetailsForClientBalance(Long orderId);

	OrderData getRenewalOrdersByClient(Long clientId, Long planType);

	OrderUssdData getOrderDetailsBySerialNo(String orderId);
    
    
    
}
