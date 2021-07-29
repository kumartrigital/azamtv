package org.mifosplatform.portfolio.order.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class OrderWorkflowData {

	public Collection<MCodeData> statusDatas;
	public List<TicketTeamMappingData> ticketmappingDatas;
	public String status;
	public OrderWorkflowData() {
		// TODO Auto-generated constructor stub
	}
	
	public OrderWorkflowData(String status) {
		this.status = status;
	}
	
	public OrderWorkflowData addDropdowns(Collection<MCodeData> statusDatas, List<TicketTeamMappingData> ticketmappingDatas){
		this.statusDatas = statusDatas;
		this.ticketmappingDatas = ticketmappingDatas;
		return this;
	}
}
