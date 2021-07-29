package org.mifosplatform.portfolio.order.service;

import org.mifosplatform.portfolio.order.data.OrderWorkflowData;

public interface OrderWorkflowReadPlatfromService {

	OrderWorkflowData getPresentStatus(Long clientServiceId);
}
