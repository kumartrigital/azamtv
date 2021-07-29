package org.mifosplatform.portfolio.order.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.springframework.http.ResponseEntity;

public interface OrderWritePlatformService {

	CommandProcessingResult createOrder(Long entityId, JsonCommand command, Order oldOrder);// oldOrder must be not null
																							// if its for change plan

	CommandProcessingResult updateOrderPrice(Long orderId, JsonCommand command);

	CommandProcessingResult deleteOrder(Long orderId, JsonCommand command);

	CommandProcessingResult renewalClientOrder(JsonCommand command, Long orderId);

	CommandProcessingResult reconnectOrder(Long entityId, String channel);

	CommandProcessingResult disconnectOrder(JsonCommand command, Long orderId);

	CommandProcessingResult retrackOsdMessage(JsonCommand command);

	CommandProcessingResult changePlan(JsonCommand command, Long entityId);

	CommandProcessingResult applyPromo(JsonCommand command);

	CommandProcessingResult scheduleOrderCreation(Long entityId, JsonCommand command);

	CommandProcessingResult deleteSchedulingOrder(Long entityId, JsonCommand command);

	CommandProcessingResult orderExtension(JsonCommand command, Long entityId);

	CommandProcessingResult orderTermination(JsonCommand command, Long entityId);

	CommandProcessingResult orderSuspention(JsonCommand command, Long entityId);

	CommandProcessingResult reactiveOrder(JsonCommand command, Long entityId);

	void processNotifyMessages(String eventName, Long clientId, String orderId, String actionType);

	void checkingContractPeriodAndBillfrequncyValidation(Long contractPeriod, String paytermCode);

	CommandProcessingResult renewalOrderWithClient(JsonCommand command, Long clientId) throws Exception;

	Plan findOneWithNotFoundDetection(Long planId);

	CommandProcessingResult scheduleOrderUpdation(Long entityId, JsonCommand command);

	CommandProcessingResult ordersSuspention(JsonCommand command, List<Order> orders);

	CommandProcessingResult reactiveOrders(JsonCommand command, List<Order> orders);

	CommandProcessingResult ordersTermination(JsonCommand command, List<Order> orders);

	CommandProcessingResult createMultipleOrder(Long clientId, JsonCommand command, Order oldOrder);

	CommandProcessingResult disconnectMultipleOrder(Long clientId, JsonCommand command, Order oldOrder);

	CommandProcessingResult Osdmessage(JsonCommand command, Long clientServicePoid) throws Exception;

	CommandProcessingResult topUp(JsonCommand command, Long entityId);

	CommandProcessingResult tovdtopUp(JsonCommand command);

	CommandProcessingResult orderUssd(JsonCommand command, Long entityId, String refernceId);
	
	ResponseEntity<?> cmsInview(String SerialNo, String offerId ,String type,String from ,  String util,String requestType);
	



}
