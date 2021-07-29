package org.mifosplatform.workflow.eventaction.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.workflow.eventaction.data.OrderNotificationData;
import org.mifosplatform.workflow.eventaction.data.VolumeDetailsData;

public interface EventActionReadPlatformService {

	VolumeDetailsData retrieveVolumeDetails(Long id);
	
	Page<EventActionData> retriveAllEventActions(SearchSqlQuery searchTicketMaster, String statusType);
	
	List<EventActionData> retrievePendingActionRequest(Long paymentgatewayId);

	List<EventActionData> retrievePendingRecurringRequest(Long clientId);
	
	OrderNotificationData retrieveNotifyDetails(Long clientId, Long orderId);

}
