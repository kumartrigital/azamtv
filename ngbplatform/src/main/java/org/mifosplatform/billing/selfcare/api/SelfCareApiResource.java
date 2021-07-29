package org.mifosplatform.billing.selfcare.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.axis2.dataretrieval.ServiceData;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.loginhistory.domain.LoginHistory;
import org.mifosplatform.billing.loginhistory.domain.LoginHistoryRepository;
import org.mifosplatform.billing.loginhistory.service.LoginHistoryReadPlatformService;
import org.mifosplatform.billing.selfcare.data.SelfCareData;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.billing.selfcare.exception.SelfCareIdNotFoundException;
import org.mifosplatform.billing.selfcare.service.ExceededNumberOfViewersException;
import org.mifosplatform.billing.selfcare.service.SelfCareReadPlatformService;
import org.mifosplatform.billing.selfcare.service.SelfCareRepository;
import org.mifosplatform.billing.selfcare.service.SelfCareWritePlatformService;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.service.TicketMasterReadPlatformService;
import org.mifosplatform.finance.billingmaster.service.BillMasterReadPlatformService;
import org.mifosplatform.finance.chargeorder.data.BillDetailsData;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.finance.clientbalance.service.ClientBalanceReadPlatformService;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.finance.payments.service.PaymentReadPlatformService;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.service.ClientServiceReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.product.service.ProductReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.stereotype.Component;

import com.sun.mail.handlers.message_rfc822;

@Path("selfcare")
@Component
@Scope("singleton")
public class SelfCareApiResource {

	private PlatformSecurityContext context;
	private final String resourceNameForPermissions = "SELFCARE";
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final DefaultToApiJsonSerializer<SelfCareData> toApiJsonSerializerForItem;
	private final SelfCareReadPlatformService selfCareReadPlatformService;
	private final ClientReadPlatformService clientReadPlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	private final ClientBalanceReadPlatformService clientBalanceReadPlatformService;
	private final OrderReadPlatformService orderReadPlatformService;
	private final BillMasterReadPlatformService billMasterReadPlatformService;
	private final PaymentReadPlatformService paymentReadPlatformService;
	private final TicketMasterReadPlatformService ticketMasterReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	private final SelfCareRepository selfCareRepository;
	private final LoginHistoryReadPlatformService loginHistoryReadPlatformService;
	private final LoginHistoryRepository loginHistoryRepository;
	private final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository;
	private final SelfCareWritePlatformService selfCareWritePlatformService;
	private final ClientServiceReadPlatformService clientServiceReadPlatformService;
	private final ProductReadPlatformService productreadplatformservice;
	
	@Autowired
	public SelfCareApiResource(final PlatformSecurityContext context,final SelfCareRepository selfCareRepository,
			final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,final LoginHistoryRepository loginHistoryRepository, 
			final DefaultToApiJsonSerializer<SelfCareData> toApiJsonSerializerForItem,final AddressReadPlatformService addressReadPlatformService,  
			final SelfCareReadPlatformService selfCareReadPlatformService,final PaymentReadPlatformService paymentReadPlatformService,  
			final ClientBalanceReadPlatformService balanceReadPlatformService, final ClientReadPlatformService clientReadPlatformService, 
			final OrderReadPlatformService  orderReadPlatformService, final BillMasterReadPlatformService billMasterReadPlatformService,
			final TicketMasterReadPlatformService ticketMasterReadPlatformService,final ConfigurationRepository configurationRepository,
			final LoginHistoryReadPlatformService loginHistoryReadPlatformService,final PaymentGatewayConfigurationRepository gatewayConfigurationRepository, 
			final SelfCareWritePlatformService selfCareWritePlatformService, final ClientServiceReadPlatformService clientServiceReadPlatformService,
			final ProductReadPlatformService productreadplatformservice) {
		
				this.context = context;
				this.commandsSourceWritePlatformService = commandSourceWritePlatformService;
				this.toApiJsonSerializerForItem = toApiJsonSerializerForItem;
				this.selfCareReadPlatformService = selfCareReadPlatformService;
				this.paymentReadPlatformService = paymentReadPlatformService;
				this.addressReadPlatformService = addressReadPlatformService;
				this.paymentGatewayConfigurationRepository=gatewayConfigurationRepository;
				this.clientBalanceReadPlatformService = balanceReadPlatformService;
				this.clientReadPlatformService = clientReadPlatformService;
				this.orderReadPlatformService = orderReadPlatformService;
				this.billMasterReadPlatformService = billMasterReadPlatformService;
				this.ticketMasterReadPlatformService = ticketMasterReadPlatformService;
				this.configurationRepository=configurationRepository;
				this.selfCareRepository=selfCareRepository;
				this.loginHistoryReadPlatformService=loginHistoryReadPlatformService;
				this.loginHistoryRepository=loginHistoryRepository;
				this.selfCareWritePlatformService=selfCareWritePlatformService;
				this.clientServiceReadPlatformService = clientServiceReadPlatformService;
				this.productreadplatformservice = productreadplatformservice;
	}
	

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createSelfCareClient(final String jsonRequestBody) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createSelfCare().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	    return this.toApiJsonSerializerForItem.serialize(result);	
	}

	@Path("password")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createSelfCareClientUDPassword(final String jsonRequestBody,@Context HttpServletRequest request) throws JSONException {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		JSONObject json = new JSONObject(jsonRequestBody); 
		json.put("session", request.getSession().getId());
		json.put("ipAddress", request.getRemoteHost());
		String jsonRequestBody1=json.toString();		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createSelfCareUDP().withJson(jsonRequestBody1).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			
	    return this.toApiJsonSerializerForItem.serialize(result);	
	}	
   
	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String logIn(@QueryParam("username")  String username, @QueryParam("password") final String password, 
			@QueryParam("serialNo") final String serialNo,@QueryParam("authToken") final String authToken, @QueryParam("otp") String otp,
			final String apiRequestJson,@Context HttpServletRequest request){
        
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
        
      
        try{
        	
        	if(username!=null && password==null){
        		if(otp==null&&apiRequestJson!=null){
            		JSONObject object = new JSONObject(apiRequestJson);
            		otp = object.optString("otp");
            	}
            	
            	if(password==null||(otp!=null&&username!=null)){
            		List<SelfCareData> selfCareDatas=this.selfCareReadPlatformService.authlogin(authToken, username);
            		if(!selfCareDatas.isEmpty()&&selfCareDatas!=null){
                		for(SelfCareData selfCareData: selfCareDatas){
                			String returnValue = this.logIn(selfCareData.getUserName(), selfCareData.getPassword(), null,authToken, null, null, request);
                			return returnValue;
                		}
            		}else{
               			this.registerSelfCare(apiRequestJson);
               			return apiRequestJson;
            		}
            	}
        	}
        		
        final SelfCareData selfcare = this.selfCareReadPlatformService.login(username, password);
        
      /*  if(selfcare == null && selfcare.getClientId() == null){
        	  MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
        	   throw new BadCredentialsException(messages.getMessage(
                       "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }*/
      
        if(selfcare != null && selfcare.getPassword().equals(password) && selfcare.getClientId()>0){
        	  SelfCareData careData = new SelfCareData();
        	  careData.setNonExpired(selfcare.getNonExpired());
        	  careData.setNonExpiredCredentials(selfcare.getNonExpiredCredentials());
        	  careData.setNonLocked(selfcare.getNonLocked());
        	  careData.setFirstTimeLogInRemaining(selfcare.getFirstTimeLogInRemaining());
        	  careData.setEnabled(selfcare.getEnabled());
        	  Long clientId = selfcare.getClientId();
        	  //adding Is_paypal Global Data by Ashok
            PaymentGatewayConfiguration paypalConfigData=this.paymentGatewayConfigurationRepository.findOneByName(ConfigurationConstants.PAYMENTGATEWAY_IS_PAYPAL_CHECK);
            careData.setPaypalConfigData(paypalConfigData);
     
          /*Configuration viewers=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_IS_ACTIVE_VIEWERS);
          if(viewers != null && viewers.isEnabled()){
          int maxViewersAllowed=Integer.parseInt(viewers.getValue());*/
          
          //Long maxViewersAllowed = this.productreadplatformservice.retrieveSession(username);
            Configuration isMaxActiveSessionsConfigValues = configurationRepository
    				.findOneByName(ConfigurationConstants.CONFIG_MAX_ACTIVE_SESSIONS);
            Long maxViewersAllowed = null;
            try {
        		if (null != isMaxActiveSessionsConfigValues && isMaxActiveSessionsConfigValues.isEnabled()) {
        			final JSONObject object = new JSONObject(isMaxActiveSessionsConfigValues.getValue());
        			maxViewersAllowed	 =	object.getLong("max_active_sessions");
        		}
        		}catch(Exception e)
        		{
        			e.printStackTrace();
        		}
          if(maxViewersAllowed != null){
           
         int activeUsers=0;
          
         /* if(username.contains("@")){
        	  
        	  SelfCare selfcare=this.selfCareRepository.findOneByEmail(username);
        	  username=selfcare.getUserName();
          }*/
          activeUsers=this.loginHistoryReadPlatformService.retrieveNumberOfUsers(username);
          
          /*int activeUsers=this.ownedHardwareReadPlatformService.retrieveNoOfActiveUsers(clientId);*/
          /**
           * Condition
           * for checking the number of active users
           * */
          if(activeUsers >=maxViewersAllowed ){
         	 throw new ExceededNumberOfViewersException(clientId);
         }
          }else{
        	  throw new PlatformDataIntegrityException("Please Create Session Param in Plan For the given user", "Please Create Session Param in Plan For the given user");
          }
        String ipAddress=request.getRemoteHost();
        String sessionId=request.getSession().getId();
        Long loginHistoryId=null;
        careData.setClientId(clientId);
       // if(request.getSession().isNew()){
        	LoginHistory loginHistory=new LoginHistory(ipAddress, serialNo, sessionId, DateUtils.getDateOfTenant(),null , username,"ACTIVE", authToken);
        	this.loginHistoryRepository.save(loginHistory);
        	loginHistoryId=loginHistory.getId();
        //}
        ClientData clientsData = this.clientReadPlatformService.retrieveOne(clientId);
        List<ClientServiceData> clientServices = this.clientServiceReadPlatformService.retriveClientServices(clientId);
        ClientBalanceData balanceData = this.clientBalanceReadPlatformService.retrieveBalance(clientId);
        List<AddressData> addressData = this.addressReadPlatformService.retrieveAddressDetailsBy(clientId,null);
        final List<OrderData> clientOrdersData = this.orderReadPlatformService.retrieveClientOrderDetails(clientId);
        final SearchSqlQuery searchCodes = SearchSqlQuery.forSearch(null, null,null);;
        final Page<BillDetailsData> statementsData = this.billMasterReadPlatformService.retrieveStatments(searchCodes,clientId);
        List<PaymentData> paymentsData = paymentReadPlatformService.retrivePaymentsData(clientId);
        final List<TicketMasterData> ticketMastersData = this.ticketMasterReadPlatformService.retrieveClientTicketDetails(clientId);
   
        careData.setDetails(clientsData,balanceData,addressData,clientOrdersData,statementsData,paymentsData,ticketMastersData,loginHistoryId, clientServices);
      
        Configuration balanceCheck=this.configurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_BALANCE_CHECK);
        clientsData.setBalanceCheck(balanceCheck.isEnabled());
        
        PaymentGatewayConfiguration paypalConfigDataForIos=this.paymentGatewayConfigurationRepository.findOneByName(ConfigurationConstants.PAYMENTGATEWAY_IS_PAYPAL_CHECK);
        careData.setPaypalConfigDataForIos(paypalConfigDataForIos);
        return this.toApiJsonSerializerForItem.serialize(careData);
        }else{
        	MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
      	    throw new BadCredentialsException(messages.getMessage(
                     "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        }catch(EmptyResultDataAccessException e){
        	throw new PlatformDataIntegrityException("result.set.is.null","result.set.is.null","result.set.is.null");
        } catch (JSONException e) {
			// TODO Auto-generated catch block
        	throw new PlatformDataIntegrityException("JSON formed is not correct","JSON formed is not correct","JSON formed is not correct");
		}
       
        
	}
	
    @PUT
    @Path("status/{clientId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateLoginStatus(@PathParam("clientId")Long clientId,final String apiRequestBodyAsJson) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder() //
                .updateClientStatus(clientId) //
                .withJson(apiRequestBodyAsJson) //
                .build(); //

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializerForItem.serialize(result);
    }
    
    @POST
    @Path("register")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String registerSelfCare(final String jsonRequestBody) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().registerSelfCareRegister().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	    return this.toApiJsonSerializerForItem.serialize(result);	
	    
	}
    
    @PUT
    @Path("register")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String SelfCareEmailVerication(final String jsonRequestBody) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().SelfCareEmailVerification().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	    return this.toApiJsonSerializerForItem.serialize(result);	
	    
	}
    
    @PUT
    @Path("forgotpassword")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createNewSelfCarePassword(final String jsonRequestBody) {
    	final CommandWrapper commandRequest = new CommandWrapperBuilder().createNewSelfCarePassword().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	    return this.toApiJsonSerializerForItem.serialize(result);
	    
	    
	}
    
    @PUT
    @Path("resetpassword")
    @Consumes({ MediaType.APPLICATION_JSON})
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateSelfCareUDPassword(final String apiRequestBodyAsJson){
    	
    	final CommandWrapper commandRequest = new CommandWrapperBuilder() //
        .updateSelfCareUDPassword() //
        .withJson(apiRequestBodyAsJson) //
        .build(); //

    	final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

    	return this.toApiJsonSerializerForItem.serialize(result);
    	 /*	SelfCareData careData = new SelfCareData(email,password);
            Long clientId=this.selfCareWritePlatformService.updateSelfCareUDPassword(careData);
			return this.toApiJsonSerializerForItem.serialize(CommandProcessingResult.resourceResult(clientId, null));*/
        	  	
    }
   
	@POST
	 @Path("/forgotpassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public String forgotSelfCareUDPassword(final String apiRequestBodyAsJson){
    	final CommandWrapper commandRequest = new CommandWrapperBuilder() //
        .forgetSelfCareUDPassword() //
        .withJson(apiRequestBodyAsJson) //
        .build(); //
    	final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializerForItem.serialize(result);
   	
    }
    
    @PUT
    @Path("changepassword")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String selfCareChangePassword(final String jsonRequestBody) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateSelfcarePassword().withJson(jsonRequestBody).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	    return this.toApiJsonSerializerForItem.serialize(result);	
	    
	}
    
    @PUT
    @Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String selfCareKortaTokenStore(@PathParam("clientId") Long clientId, final String jsonRequestBody) {

		try {
			context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
			JSONObject object = new JSONObject(jsonRequestBody);
			String token = (String) object.get("kortaToken");

			SelfCare selfcare = this.selfCareRepository.findOneByClientId(clientId);
			if (selfcare != null) {
				selfcare.setToken(token);
			} else {
				throw new SelfCareIdNotFoundException(clientId);
			}

			this.selfCareRepository.save(selfcare);
			return selfcare.getId().toString();

		} catch (JSONException e) {
			throw new UnknownError(e.getLocalizedMessage());
		}

	}
    
    @POST
    @Path("OTP")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String selfCareOTP(final String jsonRequestBody) {

		/*final CommandWrapper commandRequest = new CommandWrapperBuilder().CreateOTP().build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);*/
    	CommandProcessingResult result = this.selfCareWritePlatformService.CreateOTP(jsonRequestBody);
	    return this.toApiJsonSerializerForItem.serialize(result);	
	    
	}
	
}
