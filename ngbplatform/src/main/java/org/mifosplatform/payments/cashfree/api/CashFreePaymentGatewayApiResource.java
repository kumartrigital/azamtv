package org.mifosplatform.payments.cashfree.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.payments.cashfree.data.CashFreeData;
import org.mifosplatform.payments.cashfree.service.CashFreePaymentGatewayWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/cashfree")
@Component
@Scope("singleton")
public class CashFreePaymentGatewayApiResource {

	private final PlatformSecurityContext context;
	private final CashFreePaymentGatewayWritePlatformService cashFreePaymentGatewayWritePlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final ToApiJsonSerializer<CashFreeData> toApiJsonSerializer;
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	
	@Autowired
	public CashFreePaymentGatewayApiResource(final PlatformSecurityContext context, final CashFreePaymentGatewayWritePlatformService cashFreePaymentGatewayWritePlatformService,
			final FromJsonHelper fromJsonHelper, final ToApiJsonSerializer<CashFreeData> toApiJsonSerializer,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
		this.context = context;
		this.cashFreePaymentGatewayWritePlatformService = cashFreePaymentGatewayWritePlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createOrder(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.makePayment(apiRequestBodyAsJson);
		return result;
	}
	

	@POST
	@Path("verify")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String credentials() {
		final CommandWrapper commandRequest = new CommandWrapperBuilder().VerifyCredentials().build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	
    }


	@POST
	@Path("transactions")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createTransactions(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.createTransactions(apiRequestBodyAsJson);
		return result;
	}
	
	@POST
	@Path("status")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createStatus(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.createStatus(apiRequestBodyAsJson);
		return result;
	}
	
	@POST
	@Path("getlink")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getLink(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.getLink(apiRequestBodyAsJson);
		return result;
	}
	
	
	@POST
	@Path("settlements")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String settlements(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.settlements(apiRequestBodyAsJson);
		return result;
	}
	
	
	@POST
	@Path("settlement")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String settlement(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.settlement(apiRequestBodyAsJson);
		return result;
	}
	
	@POST
	@Path("email")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createEmail(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.PaymentEmail(apiRequestBodyAsJson);
		return result;
	}
	
	@POST
	@Path("info")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getDetails(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.PaymentDetails(apiRequestBodyAsJson);
		return result;
	}

	@POST
	@Path("paymentToken")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String paymentTokenGeneration(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		CashFreeData cashFreeData = this.cashFreePaymentGatewayWritePlatformService.generatePaymentToken(apiRequestBodyAsJson);
		return this.toApiJsonSerializer.serialize(cashFreeData);
	}


	@POST
	@Path("refund")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createrefund(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.createRefund(apiRequestBodyAsJson);
		return result;
	}
	
	@POST
	@Path("refunds")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String AllRefunds(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.FetchAllRefunds(apiRequestBodyAsJson);
		return result;
	}

	@POST
	@Path("refundStatus")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String SingleRefund(final String apiRequestBodyAsJson){
		this.context.authenticatedUser();
		String result = this.cashFreePaymentGatewayWritePlatformService.FetchSingleRefunds(apiRequestBodyAsJson);
		return result;
	}
	
}
