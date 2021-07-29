package org.mifosplatform.finance.chargeorder.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.finance.chargeorder.service.ChargingCustomerOrders;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;


@Path("/chargingorder")
@Component
@Scope("singleton")
public class ChargingOrderApiResourse {

    private final DefaultToApiJsonSerializer<CodeData> toApiJsonSerializer;
    private final ChargingCustomerOrders chargingCustomerOrders;
    private final FromJsonHelper fromApiJsonHelper;
    
	@Autowired
	public ChargingOrderApiResourse(final DefaultToApiJsonSerializer<CodeData> toApiJsonSerializer,final ChargingCustomerOrders chargingCustomerOrders,
			final FromJsonHelper fromApiJsonHelper){
		
        this.toApiJsonSerializer = toApiJsonSerializer;
		this.chargingCustomerOrders=chargingCustomerOrders;
		this.fromApiJsonHelper=fromApiJsonHelper;
	}
	
	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createChargesToOrders(@PathParam("clientId") final Long clientId,final String apiRequestBodyAsJson) {
	
		final CommandWrapper wrapper = new CommandWrapperBuilder().createCharge(clientId).withJson(apiRequestBodyAsJson).build();
		 final JsonElement parsedCommand = this.fromApiJsonHelper.parse(wrapper.getJson());
		 final JsonCommand command = JsonCommand.from(wrapper.getJson(),parsedCommand,this.fromApiJsonHelper, wrapper.getEntityName(),
						wrapper.getEntityId(), wrapper.getSubentityId(),wrapper.getGroupId(), wrapper.getClientId(),
						wrapper.getLoanId(), wrapper.getSavingsId(),wrapper.getCodeId(), wrapper.getSupportedEntityType(),
						wrapper.getSupportedEntityId(), wrapper.getTransactionId(),null);
		final CommandProcessingResult result=this.chargingCustomerOrders.createNewCharges(command); 
		return this.toApiJsonSerializer.serialize(result);
	}
	
}
