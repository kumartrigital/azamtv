package org.mifosplatform.finance.paymentsgateway.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.paymentsgateway.data.RecurringData;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayRepository;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBilling;
import org.mifosplatform.finance.paymentsgateway.domain.PaypalRecurringBillingRepository;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayReadPlatformService;
import org.mifosplatform.finance.paymentsgateway.service.PaymentGatewayRecurringWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.workflow.eventaction.domain.EventActionRepository;
import org.mifosplatform.workflow.eventaction.service.EventActionReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/recurringpayments")
@Component
@Scope("singleton")

/**
 * The class <code>PaymentGatewayRecurringApiResource</code> is developed for
 * the processing of Third party PaymentGateway's Recurring Payments.
 * Using the below API to Communicate OBS with Adapters/Third-Party servers. 
 * 
 * @author ashokreddy
 *
 */
public class PaymentGatewayRecurringApiResource {

	private final PlatformSecurityContext context;
	private final PaymentGatewayReadPlatformService readPlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<RecurringData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final PaymentGatewayRecurringWritePlatformService paymentGatewayRecurringWritePlatformService;
	private final PaymentGatewayRepository paymentGatewayRepository;
	private final PaypalRecurringBillingRepository paypalRecurringBillingRepository;
	private final EventActionReadPlatformService eventActionReadPlatformService;
	private final EventActionRepository eventActionRepository;

	
	@Autowired
	public PaymentGatewayRecurringApiResource(final PlatformSecurityContext context,final PaymentGatewayReadPlatformService readPlatformService,
			final DefaultToApiJsonSerializer<RecurringData> toApiJsonSerializer,final ApiRequestParameterHelper apiRequestParameterHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
    		final PaymentGatewayRecurringWritePlatformService paymentGatewayRecurringWritePlatformService,
    		final PaymentGatewayRepository paymentGatewayRepository, 
    		final PaypalRecurringBillingRepository paypalRecurringBillingRepository,
    		final EventActionReadPlatformService eventActionReadPlatformService,
    		final EventActionRepository eventActionRepository) {

		this.toApiJsonSerializer = toApiJsonSerializer;
		this.context=context;
		this.readPlatformService=readPlatformService;
		this.apiRequestParameterHelper=apiRequestParameterHelper;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    	this.paymentGatewayRecurringWritePlatformService = paymentGatewayRecurringWritePlatformService;
    	this.paymentGatewayRepository = paymentGatewayRepository;
    	this.paypalRecurringBillingRepository = paypalRecurringBillingRepository;
    	this.eventActionReadPlatformService = eventActionReadPlatformService;
    	this.eventActionRepository = eventActionRepository;

	}
	
	/**
	 * This method is using for Changing the status of Recurring Billing.
	 * @return 
	 */
	@POST
	@Path("changestatus")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String paypalChangeRecurringStatus(final String apiRequestBodyAsJson) {

			final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePaypalProfileStatus().withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
	} 
	
	/**
	 * This method is using for posting data to create payment using paypal
	 * @return 
	 */
	@PUT
	@Path("updaterecurring")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String paypalUpdateRecurringProfile(final String apiRequestBodyAsJson) {

			final CommandWrapper commandRequest = new CommandWrapperBuilder().updatePaypalProfileRecurring().withJson(apiRequestBodyAsJson).build();
			final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
			return this.toApiJsonSerializer.serialize(result);
	} 
	
	@GET
	@Path("{orderId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getRecurringProfileSubscriberId(@PathParam("orderId") final Long orderId, @Context final UriInfo uriInfo) {

		this.context.authenticatedUser();
		
		PaypalRecurringBilling billing = this.paypalRecurringBillingRepository.findOneByOrderId(orderId);
		
		if(null == billing){
			return null;
		}
		
		RecurringData data = new RecurringData(billing.getId(), billing.getClientId(), billing.getOrderId(), billing.getSubscriberId());
	
		return this.toApiJsonSerializer.serialize(data);
	}
	
	@PUT
	@Path("delSubscription")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteRecurringSubscriber(final String apiRequestBodyAsJson) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteRecurringBilling().withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);

	}
	
}
