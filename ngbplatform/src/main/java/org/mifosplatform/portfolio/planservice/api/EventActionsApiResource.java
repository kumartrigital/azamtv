package org.mifosplatform.portfolio.planservice.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleReadPlatformService;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.order.data.SchedulingOrderData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMapping;
import org.mifosplatform.portfolio.property.domain.PropertyDeviceMappingRepository;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.EventActionData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Path("/eventactions")
@Component
@Scope("singleton")
public class EventActionsApiResource {
	
	private final Set<String> RESPONSE_DATA_PARAMETERS=new HashSet<String>(Arrays.asList("serviceid","clientId","channelName","image","url","eventaction", "entityName", "actionName", "json"));
    private final String resourceNameForPermissions = "EVENTACTIONS";
	private final PlatformSecurityContext context;
	private final DefaultToApiJsonSerializer<SchedulingOrderData> toApiJsonSerializer;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService; 
	private final PlanRepository planRepository;
	private final ContractRepository subscriptionRepository;
	private final EventActionReadPlatformService eventActionReadPlatformService;
	private final DefaultToApiJsonSerializer<EventActionData> toApiJsonSerializerEventsAction;
	private final PropertyDeviceMappingRepository propertyDeviceMappingRepository;
	
	    
	     @Autowired
	    public EventActionsApiResource(final PlatformSecurityContext context,final DefaultToApiJsonSerializer<SchedulingOrderData> toApiJsonSerializer, 
	    		final ApiRequestParameterHelper apiRequestParameterHelper,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
	    		final PlanRepository planRepository,final ContractRepository subscriptionRepository,
	    		final EventActionReadPlatformService eventActionReadPlatformService,
	    		final DefaultToApiJsonSerializer<EventActionData> toApiJsonSerializerEventsAction,
	    		final PropertyDeviceMappingRepository propertyDeviceMappingRepository)
	     {
		        this.context = context;
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.apiRequestParameterHelper = apiRequestParameterHelper;
		        this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
		        this.planRepository =planRepository;
		        this.subscriptionRepository=subscriptionRepository;
		        this.eventActionReadPlatformService = eventActionReadPlatformService;
		        this.toApiJsonSerializerEventsAction = toApiJsonSerializerEventsAction;
		        this.propertyDeviceMappingRepository= propertyDeviceMappingRepository;
		    }

	        @GET
	        @Path("{clientId}")
			@Consumes({ MediaType.APPLICATION_JSON })
			@Produces({ MediaType.APPLICATION_JSON })
			public String getClientPlanService(@PathParam("clientId") final Long clientId,@Context final UriInfo uriInfo) {
	        	
			   context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
				final Collection<SchedulingOrderData> schedulingOrderDatas = this.actionDetailsReadPlatformService.retrieveClientSchedulingOrders(clientId);
				for(SchedulingOrderData orderData:schedulingOrderDatas){
					Plan plan=this.planRepository.findOne(orderData.getPlanId());
					Contract contract=this.subscriptionRepository.findOne(orderData.getContractId());
					orderData.setPlandesc(plan.getPlanCode());
					orderData.setContract(contract.getSubscriptionPeriod());
					if(orderData.getIsSerialnum()==true){
						String serialNumber = orderData.getSerialnum();
						PropertyDeviceMapping deviceMapping = this.propertyDeviceMappingRepository.findByCustomerSerailNumber(serialNumber,clientId);
						if(deviceMapping!=null)
						orderData.setPropertyCode(deviceMapping.getPropertyCode());
					}
					
				}
				final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
				return this.toApiJsonSerializer.serialize(settings, schedulingOrderDatas, RESPONSE_DATA_PARAMETERS);
			}
	        
	        @GET
	        @Consumes({ MediaType.APPLICATION_JSON })
	        @Produces({ MediaType.APPLICATION_JSON })
	        public String retrieveAllEventActions(@Context final UriInfo uriInfo,@QueryParam("sqlSearch") final String sqlSearch,
	    			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset,
	    			@QueryParam("statusType") final String statusType) {

	            context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
	            final SearchSqlQuery searchTicketMaster = SearchSqlQuery.forSearch(sqlSearch, offset,limit );
	            final Page<EventActionData> data = this.eventActionReadPlatformService.retriveAllEventActions(searchTicketMaster,statusType);
	            
	            return this.toApiJsonSerializerEventsAction.serialize(data);
	        }
}
