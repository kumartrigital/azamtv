package org.mifosplatform.provisioning.processrequest.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mifosplatform.finance.chargeorder.api.ChargingOrderApiResourse;
import org.mifosplatform.finance.chargeorder.domain.BillItem;
import org.mifosplatform.finance.chargeorder.service.ChargingCustomerOrders;
import org.mifosplatform.infrastructure.configuration.domain.EnumDomainService;
import org.mifosplatform.infrastructure.configuration.domain.EnumDomainServiceRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementDetail;
import org.mifosplatform.organisation.ippool.domain.IpPoolManagementJpaRepository;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.domain.ClientStatus;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderAddonsRepository;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.order.domain.UserActionStatusTypeEnum;
import org.mifosplatform.portfolio.order.service.OrderAssembler;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.provisioning.preparerequest.domain.PrepareRequsetRepository;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.provisioning.provisioning.api.ProvisioningApiConstants;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequest;
import org.mifosplatform.provisioning.provisioning.domain.ProvisioningRequestDetail;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParameters;
import org.mifosplatform.provisioning.provisioning.domain.ServiceParametersRepository;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paypal.api.payments.Invoice;


@Service(value = "processRequestWriteplatformService")
public class ProcessRequestWriteplatformServiceImpl implements ProcessRequestWriteplatformService{

      private static final Logger logger =LoggerFactory.getLogger(ProcessRequestReadplatformServiceImpl.class);
      private final PlanRepository planRepository;
      private final IpPoolManagementJpaRepository ipPoolManagementJpaRepository;
      private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
      private final ActiondetailsWritePlatformService actiondetailsWritePlatformService;
      private final PlatformSecurityContext context;
      private final OrderRepository orderRepository;
      private final ClientRepository clientRepository;
      private final OrderReadPlatformService orderReadPlatformService;
      private final ProcessRequestRepository processRequestRepository;
      private final EnumDomainServiceRepository enumDomainServiceRepository;
      private final ServiceParametersRepository serviceParametersRepository;
      private final OrderAddonsRepository orderAddonsRepository;
      private final OrderAssembler orderAssembler;
      private final ChargingCustomerOrders invoiceClient;
      private final ClientServiceRepository clientServiceRepository;
  	private final ChargingOrderApiResourse chargingOrderApiResourse;

      

        @Autowired
        public ProcessRequestWriteplatformServiceImpl(final OrderReadPlatformService orderReadPlatformService,final OrderAssembler orderAssembler,
                final OrderRepository orderRepository,final ProcessRequestRepository processRequestRepository,final PrepareRequsetRepository prepareRequsetRepository,
                final ClientRepository clientRepository,final PlanRepository planRepository,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
                final ActiondetailsWritePlatformService actiondetailsWritePlatformService,final PlatformSecurityContext context,
                final EnumDomainServiceRepository enumDomainServiceRepository,final ServiceParametersRepository parametersRepository,
                final IpPoolManagementJpaRepository ipPoolManagementJpaRepository,final OrderAddonsRepository orderAddonsRepository,
                final ChargingCustomerOrders invoiceClient,final ClientServiceRepository clientServiceRepository,
            	 final ChargingOrderApiResourse chargingOrderApiResourse
) {

            
                this.context = context;
                this.planRepository=planRepository;
                this.ipPoolManagementJpaRepository=ipPoolManagementJpaRepository;
                this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
                this.actiondetailsWritePlatformService=actiondetailsWritePlatformService;
                this.orderAddonsRepository=orderAddonsRepository;
                this.orderRepository=orderRepository;
                this.clientRepository=clientRepository;
                this.orderAssembler = orderAssembler;
                this.serviceParametersRepository=parametersRepository;
                this.processRequestRepository=processRequestRepository;
                this.orderReadPlatformService=orderReadPlatformService;
                this.enumDomainServiceRepository=enumDomainServiceRepository;
                this.invoiceClient = invoiceClient;
                this.clientServiceRepository = clientServiceRepository;
                this.chargingOrderApiResourse = chargingOrderApiResourse;

                 
        }

/*        @Transactional
        @Override
        public void ProcessingRequestDetails() {
            
            final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default");
            ThreadLocalContextUtil.setTenant(tenant);
            List<PrepareRequestData> data=this.prepareRequestReadplatformService.retrieveDataForProcessing();
                for(PrepareRequestData requestData:data){
                       //Get the Order details
                     final List<Long> clientOrderIds = this.prepareRequestReadplatformService.retrieveRequestClientOrderDetails(requestData.getClientId());
                         //Processing the request
                         if(clientOrderIds!=null){
                                     this.processingClientDetails(clientOrderIds,requestData);
                                    //Update RequestData
                                     PrepareRequest prepareRequest=this.prepareRequsetRepository.findOne(requestData.getRequestId());
                                     prepareRequest.updateProvisioning('Y');
                                     this.prepareRequsetRepository.save(prepareRequest);
                         }
                }
            }
                    
        
        private void processingClientDetails(List<Long> clientOrderIds,PrepareRequestData requestData) {
                for(Long orderId:clientOrderIds){
                    final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default");
                    ThreadLocalContextUtil.setTenant(tenant);
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourcePerTenantService.retrieveDataSource());
                }
            }*/

        @Override
        public void notifyProcessingDetails(ProvisioningRequest provisioningRequest,char status) {
                try{
                    if(provisioningRequest!=null && !(provisioningRequest.getRequestType().equalsIgnoreCase(ProvisioningApiConstants.REQUEST_TERMINATE)) && status != 'F'){
                        Plan plan = new Plan();
                        Client client=null;
                        List<ProvisioningRequestDetail>provisioningRequestDetails=provisioningRequest.getProvisioningRequestDetail();
                        boolean action=true;
                        for(ProvisioningRequestDetail provisioningRequestDetail : provisioningRequestDetails){
                            JSONObject jsonObject = new JSONObject(provisioningRequestDetail.getRequestMessage());
                            if(jsonObject.opt("Action")!=null){
                                action=false;
                            }
                        }
                        //action will be false for command center operations
                        if(action){
                            client=this.clientRepository.findOne(provisioningRequest.getClientId());
                        }
                        List<Order> orders = new ArrayList<>();
                        if(!provisioningRequest.getRequestType().equalsIgnoreCase("RETRACK"))
                        	orders= this.retriveOrdersfromProvisioningRequest(provisioningRequest);
                        for(Order order:orders)
                            switch(provisioningRequest.getRequestType()){
                            
                            case ProvisioningApiConstants.REQUEST_ACTIVATION  :
                                
                                if(provisioningRequest.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.ACTIVATION.toString())){
                                    order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
                                    ActionDetaislData provActionDetail=this.actionDetailsReadPlatformService.retrieveEventWithAction(EventActionConstants.EVENT_PROVISION_CONFIRM,EventActionConstants.ACTION_CHANGE_START);
                                     if(provActionDetail != null){
                                        order.setStartDate(DateUtils.getLocalDateOfTenant());
                                        order=this.orderAssembler.setDatesOnOrderActivation(order,DateUtils.getLocalDateOfTenant());
                                     }
                                    client.setStatus(ClientStatus.ACTIVE.getValue());
                                    ClientService clientService = this.clientServiceRepository.findOne(order.getClientServiceId());
                                    if("Provisioned".equalsIgnoreCase(clientService.getStatus())||"PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                        clientService.setStatus("ACTIVE");
                                        this.clientServiceRepository.saveAndFlush(clientService);
                                    }
                                    this.orderRepository.saveAndFlush(order);
                                    List<ActionDetaislData> actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_ACTIVE_ORDER);
                                    if(actionDetaislDatas.size() != 0){
                                            this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
                                    }
                  

                                }
                                    
                                    break;
                                    
                            case ProvisioningApiConstants.REQUEST_ADD_PLAN  :
                                 System.out.println(UserActionStatusTypeEnum.ADD_PLAN.toString());
                                if(provisioningRequest.getRequestType().equalsIgnoreCase(UserActionStatusTypeEnum.ADD_PLAN.toString())){
                                    order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
                                    ActionDetaislData provActionDetail=this.actionDetailsReadPlatformService.retrieveEventWithAction(EventActionConstants.EVENT_PROVISION_CONFIRM,EventActionConstants.ACTION_CHANGE_START);
                                     if(provActionDetail != null){
                                        order.setStartDate(DateUtils.getLocalDateOfTenant());
                                        order=this.orderAssembler.setDatesOnOrderActivation(order,DateUtils.getLocalDateOfTenant());
                                     }
                                    client.setStatus(ClientStatus.ACTIVE.getValue());
                                    ClientService clientService = this.clientServiceRepository.findOne(order.getClientServiceId());
                                    if("PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                        clientService.setStatus("ACTIVE");
                                        this.clientServiceRepository.saveAndFlush(clientService);
                                    }
                                    this.orderRepository.saveAndFlush(order);
                                    List<ActionDetaislData> actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_ADD_PLAN);
                                    if(actionDetaislDatas.size() != 0){
                                            this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
                                    }
                                }
                                    
                                    break;
                                
                                case ProvisioningApiConstants.REQUEST_RECONNECTION :
    
                                       order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
                                       ActionDetaislData provReconnectActionDetail=this.actionDetailsReadPlatformService.retrieveEventWithAction(EventActionConstants.EVENT_PROVISION_CONFIRM,EventActionConstants.ACTION_CHANGE_START);
                                        if(provReconnectActionDetail != null){
                                          order.setStartDate(DateUtils.getLocalDateOfTenant());
                                          order=this.orderAssembler.setDatesOnOrderActivation(order,DateUtils.getLocalDateOfTenant());
                                        }
                                        client.setStatus(ClientStatus.ACTIVE.getValue());
                                        this.orderRepository.saveAndFlush(order);
                                        List<ActionDetaislData> actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_RECONNECTION_ORDER);
                                        if(actionDetaislDatas.size() != 0){
                                                this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
                                        }
                                        
                                        break;
                                        
                                case ProvisioningApiConstants.REQUEST_CHANGE_PLAN :
                                        if(order.getStatus()!= 3){
                                        order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
                                        ActionDetaislData provChangePlanActionDetail=this.actionDetailsReadPlatformService.retrieveEventWithAction(EventActionConstants.EVENT_PROVISION_CONFIRM,EventActionConstants.ACTION_CHANGE_START);
                                         if(provChangePlanActionDetail != null){
                                              order.setStartDate(DateUtils.getLocalDateOfTenant());
                                              order=this.orderAssembler.setDatesOnOrderActivation(order,DateUtils.getLocalDateOfTenant());
                                          }
                                        client.setStatus(ClientStatus.ACTIVE.getValue());
                                        this.orderRepository.saveAndFlush(order);
                                        actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_CHANGE_PLAN);
                                        if(actionDetaislDatas.size() != 0){
                                                this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
                                        }
                                        
                                        break;    
                                        }
                                    
                                case ProvisioningApiConstants.REQUEST_DISCONNECTION :
                                    
                                    order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.DISCONNECTED).getId());
                                    this.orderRepository.saveAndFlush(order);
                                    Long activeOrders=this.orderReadPlatformService.retrieveClientActiveOrderDetails(order.getClientId(), null,null);
                                    if(activeOrders == 0){
                                        client.setStatus(ClientStatus.DEACTIVE.getValue());
                                        /*ClientService clientService = this.clientServiceRepository.findOne(order.getClientServiceId());
                                        if("PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                            clientService.setStatus("DEACTIVE");
                                            this.clientServiceRepository.saveAndFlush(clientService);
                                        }*/
                                    }
                                    actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_DISCONNECTION_ORDER);
                                    if(actionDetaislDatas.size() != 0){
                                            this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
                                    }
                                    
                                    break;
                                    
                              case ProvisioningApiConstants.REQUEST_SUSPENTATION :
                                    
                                      order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.SUSPENDED).getId());
                                    this.orderRepository.saveAndFlush(order);
                                    Long noOfActiveOrders = this.orderReadPlatformService.retrieveClientActiveOrderDetails(order.getClientId(), null,null);
                                    if(noOfActiveOrders == 0){
                                        client.setStatus(ClientStatus.DEACTIVE.getValue());
                                    }
                                    Long noOfActiveOrdersInClientService = this.orderReadPlatformService.retrieveClientActiveOrderDetails(order.getClientId(), null,order.getClientServiceId());
                                    if(noOfActiveOrdersInClientService == 0){
                                        ClientService clientService = this.clientServiceRepository.findOne(order.getClientServiceId());
                                        if("PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                            clientService.setStatus("SUSPENDED");
                                            this.clientServiceRepository.saveAndFlush(clientService);
                                        }
                                    }
                                    actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_SUSPENSION_ORDER);
                                    if(actionDetaislDatas.size() != 0){
                                            this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
                                    }
                                  
                                    break;
                                    
                              case ProvisioningApiConstants.REQUEST_REACTIVATION :
                                    
                                    
                                  EnumDomainService enumDomainService = this.enumDomainServiceRepository.findOneByEnumMessageProperty(StatusTypeEnum.ACTIVE.toString());
                                    order.setStatus(enumDomainService.getEnumId());
                                    this.orderRepository.saveAndFlush(order);
                                    
                                    noOfActiveOrdersInClientService = this.orderReadPlatformService.retrieveClientActiveOrderDetails(order.getClientId(), null,order.getClientServiceId());
                                    if(noOfActiveOrdersInClientService != 0){
                                        ClientService clientService = this.clientServiceRepository.findOne(order.getClientServiceId());
                                        if("PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                            clientService.setStatus("ACTIVE");
                                            this.clientServiceRepository.saveAndFlush(clientService);
                                        }
                                        if(!client.getStatus().toString().equalsIgnoreCase(ClientStatus.ACTIVE.getValue().toString())){client.setStatus(ClientStatus.ACTIVE.getValue());}
                                    }
                                    
                                    actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_REACTIVE_ORDER);
                                    if(actionDetaislDatas.size() != 0){
                                            this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
                                    }
                                    break;
                                    
                                case ProvisioningApiConstants.REQUEST_TERMINATION :
    
                                    order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.TERMINATED).getId());
                                    this.orderRepository.saveAndFlush(order);
                                    ClientService clientService = this.clientServiceRepository.findOne(order.getClientServiceId());
                                    if("PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                        clientService.setStatus("TERMINATED");
                                        this.clientServiceRepository.saveAndFlush(clientService);
                                    }
                                    noOfActiveOrders = this.orderReadPlatformService.retrieveClientActiveOrderDetails(order.getClientId(), null,null);
                                    if(noOfActiveOrders == 0){
                                        client.setStatus(ClientStatus.CLOSED.getValue());this.clientRepository.saveAndFlush(client);
                                    }
                                    
                                    actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_TERMINATE_ORDER);
                                    if(actionDetaislDatas.size() != 0){
                                            this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
                                    }
                                

                                    break;
                                    
                                    /*case ProvisioningApiConstants.REQUEST_ADDON_ACTIVATION :
                                        
                                        List<ProcessRequestDetails> requestDetails=provisioningRequest.getProcessRequestDetails();
                                        JSONObject jsonObject=new JSONObject(requestDetails.get(0).getSentMessage());
                                        JSONArray array=jsonObject.getJSONArray("services");
                                        for(int i=0;i<array.length();i++){
                                            JSONObject object=array.getJSONObject(i);
                                            Long addonId=object.getLong("addonId");
                                            OrderAddons addons=this.orderAddonsRepository.findOne(addonId);
                                            addons.setStatus(StatusTypeEnum.ACTIVE.toString());
                                            this.orderAddonsRepository.saveAndFlush(addons);
                                        }
                                        
                                    break;
                                    case ProvisioningApiConstants.REQUEST_ADDON_DISCONNECTION :
                                        
                                         requestDetails=detailsData.getProcessRequestDetails();
                                         jsonObject=new JSONObject(requestDetails.get(0).getSentMessage());
                                         array=jsonObject.getJSONArray("services");
                                        for(int i=0;i<array.length();i++){
                                            JSONObject object=array.getJSONObject(i);
                                            Long addonId=object.getLong("addonId");
                                            OrderAddons addons=this.orderAddonsRepository.findOne(addonId);
                                            addons.setStatus(StatusTypeEnum.DISCONNECTED.toString());
                                            this.orderAddonsRepository.saveAndFlush(addons);
                                        }
                                        
                                    break;*/
                                    case ProvisioningApiConstants.REQUEST_RENEWAL_AE:
                                        
                                        /*if (provisioningRequest.getOrderId() != null && provisioningRequest.getOrderId() > 0) {
                                            order = this.orderRepository.findOne(provisioningRequest.getOrderId());
                                            plan = this.planRepository.findOne(order.getPlanId());
                                        }*/
                                        
                                         client=this.clientRepository.findOne(provisioningRequest.getClientId());
                                            order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
                                            ActionDetaislData provRenewalAfterActionDetail=this.actionDetailsReadPlatformService.retrieveEventWithAction(EventActionConstants.EVENT_PROVISION_CONFIRM,EventActionConstants.ACTION_CHANGE_START);
                                            if(provRenewalAfterActionDetail != null){
                                              order=this.orderAssembler.setDatesOnOrderActivation(order,DateUtils.getLocalDateOfTenant());
                                            }
                                            client.setStatus(ClientStatus.ACTIVE.getValue());
                                            this.orderRepository.saveAndFlush(order);
    
                                         if(plan.isPrepaid() == 'Y'){
                                              List<BillItem> invoiceList=this.invoiceClient.singleOrderInvoice(order.getId(),order.getClientId(),new LocalDate(order.getStartDate()).plusDays(1));
                                              for(BillItem invoice: invoiceList)
                                              {
                                               if(invoice!=null){
                                                  List<ActionDetaislData> actionDetaislData=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_TOPUP_INVOICE_MAIL);
                                                  if(actionDetaislData.size() != 0){
                                                      this.actiondetailsWritePlatformService.AddNewActions(actionDetaislData,order.getClientId(), invoice.getId().toString(),null);
                                                  }
                                              }
                                              }
                                         }
                                        
                                         actionDetaislDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_RECONNECTION_ORDER);
                                         if(actionDetaislDatas.size() != 0){
                                                 this.actiondetailsWritePlatformService.AddNewActions(actionDetaislDatas,order.getClientId(), order.getId().toString(),null);
                                         }
                                        
                                    break;
                                    case ProvisioningApiConstants.REQUEST_RENEWAL_BE:
                                        
                                        if(order != null){
                                            order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
                                            this.orderRepository.saveAndFlush(order);
                                        }
                                        client.setStatus(ClientStatus.ACTIVE.getValue());
                                        this.clientRepository.saveAndFlush(client);
                                        clientService = this.clientServiceRepository.findOne(order.getClientServiceId());

                                        if("PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                            clientService.setStatus("ACTIVE");
                                            this.clientServiceRepository.saveAndFlush(clientService);
                                        }
                                        plan = this.planRepository.findOne(order.getPlanId());
                                        if(plan.isPrepaid() == 'Y'){
                                        	JSONObject jsonObject = new JSONObject();
                        					SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                        	jsonObject.put("dateFormat", "dd MMMM yyyy");
            								jsonObject.put("locale", "en");
            								jsonObject.put("systemDate", dateFormat.format(order.getEndDate()));
            								this.chargingOrderApiResourse.createChargesToOrders(order.getClientId(),
            										jsonObject.toString());
                                       }
                                      
                                       
                                      
                                        
                                    break;
                                        
                                    
                                    default :
    
                                        if(order != null){
                                            order.setStatus(OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE).getId());
                                            this.orderRepository.saveAndFlush(order);
                                            /*ClientService clientService = this.clientServiceRepository.findOne(order.getClientServiceId());
                                            if("PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                                clientService.setStatus("ACTIVE");
                                                this.clientServiceRepository.saveAndFlush(clientService);
                                            }*/
                                        }
                                        client.setStatus(ClientStatus.ACTIVE.getValue());
                                        this.clientRepository.saveAndFlush(client);
                                        clientService = this.clientServiceRepository.findOne(order.getClientServiceId());

                                        if("PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                            clientService.setStatus("ACTIVE");
                                            this.clientServiceRepository.saveAndFlush(clientService);
                                        }
                                        break;
                            }
                        
                        /*if(order != null){
                            ClientService clientService = this.clientServiceRepository.findOne(order.getClientServiceId());
                                    if("PROCESSING".equalsIgnoreCase(clientService.getStatus())){
                                        clientService.setStatus("ACTIVE");
                                        this.clientServiceRepository.saveAndFlush(clientService);
                                    }
                        }*/
                        if(action){
                        	this.clientRepository.saveAndFlush(client);
                        }
                        //detailsData.setNotify();
                    }
                        //this.processRequestRepository.saveAndFlush(detailsData);
                }catch(Exception exception){
                    exception.printStackTrace();
                }
        }
        
        @Override
        public List<Order> retriveOrdersfromProvisioningRequest(ProvisioningRequest provisioningRequest) {
            List<Order> orders = new ArrayList<Order>();
            JSONObject requestMessageObject = null;
            try{
                List<ProvisioningRequestDetail> details = provisioningRequest.getProvisioningRequestDetail();
            
                for(ProvisioningRequestDetail detail:details){
                    requestMessageObject = new JSONObject(detail.getRequestMessage());
                    JSONArray orderArray = null;
                    if(requestMessageObject.has("newOrderList")){
                        orderArray = new JSONArray();
                        orderArray = requestMessageObject.getJSONArray("newOrderList");
                        JSONObject object = null;
                        for(int i=0;i<orderArray.length();i++){
                            object = orderArray.getJSONObject(i);
                            orders.add(this.orderRepository.findOne(object.getLong("orderId")));
                        }
                    }
                    if(requestMessageObject.has("oldOrderList")){
                        orderArray = new JSONArray();
                        orderArray = requestMessageObject.getJSONArray("oldOrderList");
                        JSONObject object = null;
                        for(int i=0;i<orderArray.length();i++){
                            object = orderArray.getJSONObject(i);
                            orders.add(this.orderRepository.findOne(object.getLong("orderId")));
                        }
                    }
                }
                return orders;
            }catch(Exception e){
                throw new PlatformDataIntegrityException("exception.occured", e.getMessage());
            }
        }

        @Transactional
        @Override
        public CommandProcessingResult addProcessRequest(JsonCommand command){
            
            try{
                this.context.authenticatedUser();
                ProcessRequest processRequest = ProcessRequest.fromJson(command);
                ProcessRequestDetails processRequestDetails = ProcessRequestDetails.fromJson(processRequest,command);    
                processRequest.add(processRequestDetails);
                this.processRequestRepository.save(processRequest);
                return    new CommandProcessingResult(Long.valueOf(processRequest.getPrepareRequestId()),processRequest.getClientId());

            }catch(DataIntegrityViolationException dve){
                handleCodeDataIntegrityIssues(command,dve);
                return CommandProcessingResult.empty();
            }
            
        }
        
         private void handleCodeDataIntegrityIssues(JsonCommand command,DataIntegrityViolationException dve) {
                 Throwable realCause = dve.getMostSpecificCause();
                    logger.error(dve.getMessage(), dve);
                    throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                            "Unknown data integrity issue with resource: " + realCause.getMessage());
            }

        
    /*    @Transactional
        @Override
        public void postProvisioningdetails(Client client,EventOrder eventOrder,String requestType,String provSystem, String response) {
            try{
                
                
                this.context.authenticatedUser();
                ProcessRequest processRequest=new ProcessRequest(Long.valueOf(0), eventOrder.getClientId(),eventOrder.getId(),ProvisioningApiConstants.PROV_BEENIUS,
                        ProvisioningApiConstants.REQUEST_ACTIVATION_VOD);
                List<EventOrderdetials> eventDetails=eventOrder.getEventOrderdetials();
                EventMaster eventMaster=this.eventMasterRepository.findOne(eventOrder.getEventId());
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("officeUid",client.getOffice().getExternalId());
                jsonObject.put("subscriberUid",client.getAccountNo());
                jsonObject.put("vodUid",eventMaster.getEventName());
                        
                    for(EventOrderdetials details:eventDetails){
                        ProcessRequestDetails processRequestDetails=new ProcessRequestDetails(details.getId(),details.getEventDetails().getId(),jsonObject.toString(),
                                response,null,eventMaster.getEventStartDate(), eventMaster.getEventEndDate(),new Date(),new Date(),'N',
                                ProvisioningApiConstants.REQUEST_ACTIVATION_VOD,null);
                        processRequest.add(processRequestDetails);
                    }
                this.processRequestRepository.save(processRequest);
            }catch(DataIntegrityViolationException dve){
                handleCodeDataIntegrityIssues(null, dve);
                
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            
        }*/
}