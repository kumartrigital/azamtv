package org.mifosplatform.logistics.item.api;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.serial.SerialException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONObject;
import org.mifosplatform.billing.chargecode.data.ChargesData;
import org.mifosplatform.billing.emun.data.EnumValuesData;
import org.mifosplatform.billing.emun.service.EnumReadplaformService;
import org.mifosplatform.cms.mediadevice.exception.DeviceIdNotFoundException;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.configuration.data.ConfigurationPropertyData;
import org.mifosplatform.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.logistics.supplier.data.SupplierData;
import org.mifosplatform.logistics.supplier.service.SupplierReadPlatformService;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.organisation.region.data.RegionData;
import org.mifosplatform.organisation.region.service.RegionReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/items")
@Component
@Scope("singleton")
public class ItemApiResource {

	private static final Set<String> RESPONSE_ITEM_DATA_PARAMETERS = new HashSet<String>(
			Arrays.asList("itemId", "chargedatas", "unitData", "itemclassData", "chargeCode", "unit", "warranty",
					"itemDescription", "itemCode", "unitPrice"));
	private final String resourceNameForPermissions = "ITEM";
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final ApiRequestParameterHelper apiRequestParameterHelper;
	private final DefaultToApiJsonSerializer<ItemData> toApiJsonSerializer;
	private final PlatformSecurityContext context;
	private final ItemReadPlatformService itemReadPlatformService;
	private final RegionReadPlatformService regionReadPlatformService;
	private final ConfigurationReadPlatformService configurationReadPlatformService;
	private final SupplierReadPlatformService supplierReadPlatformService;
	private final EnumReadplaformService enumReadplaformService;
	private final CurrencyReadPlatformService currencyReadPlatformService;
	private final ItemRepository itemRepository;

	@Autowired
	public ItemApiResource(final PlatformSecurityContext context,
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			ApiRequestParameterHelper requestParameterHelper,
			DefaultToApiJsonSerializer<ItemData> defaultToApiJsonSerializer,
			final ItemReadPlatformService itemReadPlatformService,
			final RegionReadPlatformService regionReadPlatformService,
			final ConfigurationReadPlatformService configurationReadPlatformService,
			final CurrencyReadPlatformService currencyReadPlatformService,
			final SupplierReadPlatformService supplierReadPlatformService,
			final EnumReadplaformService enumReadplaformService, final ItemRepository itemRepository) {

		this.context = context;
		this.commandsSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.apiRequestParameterHelper = requestParameterHelper;
		this.toApiJsonSerializer = defaultToApiJsonSerializer;
		this.itemReadPlatformService = itemReadPlatformService;
		this.regionReadPlatformService = regionReadPlatformService;
		this.configurationReadPlatformService = configurationReadPlatformService;
		this.supplierReadPlatformService = supplierReadPlatformService;
		this.enumReadplaformService = enumReadplaformService;
		this.currencyReadPlatformService = currencyReadPlatformService;
		this.itemRepository = itemRepository;
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveAllItems(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch,
			@QueryParam("limit") final Integer limit, @QueryParam("offset") final Integer offset) {

		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final SearchSqlQuery searchItems = SearchSqlQuery.forSearch(sqlSearch, offset, limit);
		Page<ItemData> itemData = this.itemReadPlatformService.retrieveAllItems(searchItems);
		return this.toApiJsonSerializer.serialize(itemData);
	}

	@GET
	@Path("template")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveItemTemplateData(@Context final UriInfo uriInfo) {
		context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		ItemData itemData = this.handleTemplateData(null);
		return this.toApiJsonSerializer.serialize(settings, itemData, RESPONSE_ITEM_DATA_PARAMETERS);
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createNewItem(final String jsonRequestBody) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().createItem().withJson(jsonRequestBody)
				.build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);

	}

	@GET
	@Path("{itemId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSingletemData(@PathParam("itemId") final Long itemId, @Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		ItemData itemData = this.itemReadPlatformService.retrieveSingleItemDetails(null, itemId, null, false); // If you
																												// pass
																												// clientId
																												// set
																												// to
																												// 'true'
																												// else
																												// 'false'
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		if (itemData != null) {
			itemData.setItemPricesDatas(this.itemReadPlatformService.retrieveItemPrice(itemId));
			if (settings.isTemplate()) {
				itemData = this.handleTemplateData(itemData);
			}
		}
		return this.toApiJsonSerializer.serialize(settings, itemData, RESPONSE_ITEM_DATA_PARAMETERS);
	}

	@PUT
	@Path("{itemId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateItem(@PathParam("itemId") final Long itemId, final String jsonRequestBody) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().updateItem(itemId).withJson(jsonRequestBody)
				.build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@DELETE
	@Path("{itemId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteItem(@PathParam("itemId") final Long itemId) {

		final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteItem(itemId).build();
		final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
		return this.toApiJsonSerializer.serialize(result);
	}

	@GET
	@Path("all")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveItemsForDropdown(@Context final UriInfo uriInfo) {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		final List<ItemData> itemData = this.itemReadPlatformService.retrieveAllItemsForDropdown();
		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, itemData, RESPONSE_ITEM_DATA_PARAMETERS);
	}

	@GET
	@Path("supplier/{supplierId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String retrieveSupplierItems(@Context final UriInfo uriInfo,
			@PathParam("supplierId") final Long supplierId) {
		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		List<ItemData> itemData = null;
		final ConfigurationPropertyData configurationData = this.configurationReadPlatformService
				.retrieveGlobalConfigurationByName("item-supplier-mapping");
		if (configurationData.isEnabled()) {
			itemData = this.itemReadPlatformService.retrieveAllSupplierItems(supplierId);
		} else {
			itemData = this.itemReadPlatformService.retrieveAllItems();
		}

		final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper
				.process(uriInfo.getQueryParameters());
		return this.toApiJsonSerializer.serialize(settings, itemData, RESPONSE_ITEM_DATA_PARAMETERS);
	}

	private ItemData handleTemplateData(ItemData itemData) {
		if (itemData == null) {
			itemData = new ItemData();
		}
		itemData.setCurrencyDatas(this.currencyReadPlatformService.retrieveCurrency());
		itemData.setRegionDatas(this.regionReadPlatformService.getRegionDetails());
		itemData.setChargesData(this.itemReadPlatformService.retrieveChargeCode());
		itemData.setItemClassData(this.enumReadplaformService.getEnumValues("item_class"));
		itemData.setUnitData(this.enumReadplaformService.getEnumValues("item_unit_type"));
		final ConfigurationPropertyData configurationPropertyData = this.configurationReadPlatformService
				.retrieveGlobalConfigurationByName("item-supplier-mapping");
		itemData.setConfigurationData(configurationPropertyData);
		if (configurationPropertyData.isEnabled()) {
			itemData.setSupplierData(this.supplierReadPlatformService.retrieveSupplier());
		}
		return itemData;
	}

	@GET
	@Path("{quantity}/{itemId}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String checkAvailablrQuantity(@PathParam("quantity") final Long quantity,
			@PathParam("itemId") final Long itemId, @Context final UriInfo uriInfo) {

		this.context.authenticatedUser().validateHasReadPermission(resourceNameForPermissions);
		ItemMaster itemData = itemRepository.findOne(itemId);

		Long availableQuantity = this.itemReadPlatformService.checkAvailablequantityMSO();

		if (availableQuantity < quantity) {
			throw new DeviceIdNotFoundException("Entered Quantity Not Available");
		}
		BigDecimal amount = itemData.getUnitPrice().multiply(new BigDecimal(quantity));

		JSONObject result = new JSONObject();
		result.put("quantity", quantity);
		result.put("itemId", itemData.getId());
		result.put("itemType", itemData.getItemCode());
		result.put("unitprice", itemData.getUnitPrice());
		result.put("amount", amount);

		return this.toApiJsonSerializer.serialize(result);
	}

}
