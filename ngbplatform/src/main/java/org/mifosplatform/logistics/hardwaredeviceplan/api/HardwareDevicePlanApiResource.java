package org.mifosplatform.logistics.hardwaredeviceplan.api;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.logistics.onetimesale.domain.ItemPairing;
import org.mifosplatform.logistics.onetimesale.domain.ItemPairingRepository;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleWritePlatformService;
import org.mifosplatform.logistics.onetimesale.service.OneTimeSaleWritePlatformServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Path("/hardwaredeviceplan")
@Component
@Scope("singleton")
public class HardwareDevicePlanApiResource {

	
	private final ItemPairingRepository itemPairingRepository;
	private final OneTimeSaleWritePlatformService oneTimeSaleWritePlatformService;
	private final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	private final ToApiJsonSerializer toApiJsonSerializer;
	
	

    @Autowired
	public HardwareDevicePlanApiResource(final ItemPairingRepository itemPairingRepository,final OneTimeSaleWritePlatformService oneTimeSaleWritePlatformService,
		final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService,final ToApiJsonSerializer toApiJsonSerializer) {
		this.itemPairingRepository = itemPairingRepository;
		this.oneTimeSaleWritePlatformService = oneTimeSaleWritePlatformService;
		this.commandSourceWritePlatformService = commandSourceWritePlatformService;
		this.toApiJsonSerializer = toApiJsonSerializer;
	}


	@Transactional
	@POST
	@Path("{clientId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createHardwareDevicePlanSale(@PathParam("clientId") final Long clientId,
			@QueryParam("devicesaleType") final String devicesaleType,final String apiRequestBodyAsJson) {
		try {
			JSONObject object = new JSONObject(apiRequestBodyAsJson);
			String returnData = this.createNewHardwareDevicePlanSale(clientId, devicesaleType, apiRequestBodyAsJson);
			if(object.has("pairableItemDetails")){
				String pairableItemDetails = object.getString("pairableItemDetails");
				returnData = this.createNewHardwareDevicePlanSale(clientId, devicesaleType, pairableItemDetails);
				this.pairingDeviceFun(object,new JSONObject(pairableItemDetails),clientId);
			}
			return returnData;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PlatformDataIntegrityException("exception.occure", "exception occured");
		}
		
	}
	
	
	private void pairingDeviceFun(JSONObject firstDevice, JSONObject secondDevice,Long clientId) throws JSONException {
		JSONArray array1 = firstDevice.getJSONArray("serialNumber");
		JSONArray array2 = secondDevice.getJSONArray("serialNumber");
		String serialNo1 = array1.getJSONObject(0).getString("serialNumber");
		String itemType1 = array1.getJSONObject(0).getString("itemType");
		String serialNo2 = array2.getJSONObject(0).getString("serialNumber");
		String itemType2 = array2.getJSONObject(0).getString("itemType");
		Long clientServiceId = firstDevice.getLong("clientServiceId");
		ItemPairing itemPairing = new ItemPairing(clientId, clientServiceId, new Date(), null, "allocated", serialNo1, itemType1, serialNo2, itemType2);
		 this.itemPairingRepository.saveAndFlush(itemPairing);
	}
	
	public String createNewHardwareDevicePlanSale(final Long clientId,final String devicesaleType,final String apiRequestBodyAsJson) {
		
		final CommandWrapper commandRequest = new CommandWrapperBuilder().createHardwareDevicePlan(clientId,devicesaleType).withJson(apiRequestBodyAsJson).build();
		final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

}
