package org.mifosplatform.portfolio.order.service;

import java.util.List;

import org.mifosplatform.portfolio.order.data.OrderAddonsData;

public interface OrderAddOnsReadPlaformService {

	List<OrderAddonsData> retrieveAllOrderAddons(Long orderId);

}
