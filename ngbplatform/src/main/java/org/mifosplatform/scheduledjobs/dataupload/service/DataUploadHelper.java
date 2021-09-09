
package org.mifosplatform.scheduledjobs.dataupload.service;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.channels.ClosedByInterruptException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.finance.adjustment.data.AdjustmentData;
import org.mifosplatform.finance.adjustment.exception.AdjustmentCodeNotFoundException;
import org.mifosplatform.finance.adjustment.service.AdjustmentReadPlatformService;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.finance.payments.data.PaymentData;
import org.mifosplatform.finance.payments.domain.Payment;
import org.mifosplatform.finance.payments.exception.PaymentCodeNotFoundException;
import org.mifosplatform.finance.payments.exception.PaymentDetailsNotFoundException;
import org.mifosplatform.finance.payments.service.PaymentReadPlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeData;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepository;
import org.mifosplatform.infrastructure.codes.service.CodeReadPlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.configuration.exception.ConfigurationPropertyNotFoundException;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.logistics.itemdetails.data.ItemDetailsData;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsReadPlatformService;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.address.data.CityDetailsData;
import org.mifosplatform.organisation.address.service.AddressReadPlatformService;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.portfolio.client.data.ClientBillInfoData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.client.service.ClientBillInfoReadPlatformService;
import org.mifosplatform.portfolio.client.service.ClientReadPlatformService;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.service.OrderReadPlatformService;
import org.mifosplatform.portfolio.order.service.OrderWritePlatformService;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.clientservice.domain.ClientService;
import org.mifosplatform.portfolio.clientservice.domain.ClientServiceRepository;
import org.mifosplatform.portfolio.contract.domain.Contract;
import org.mifosplatform.portfolio.contract.domain.ContractRepository;
import org.mifosplatform.portfolio.order.domain.Order;
import org.mifosplatform.portfolio.order.domain.OrderRepository;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanDetails;
import org.mifosplatform.portfolio.plan.domain.PlanDetailsRepository;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.mifosplatform.portfolio.plan.service.PlanReadPlatformService;
import org.mifosplatform.portfolio.property.data.PropertyDefinationData;
import org.mifosplatform.portfolio.property.service.PropertyReadPlatformService;
import org.mifosplatform.portfolio.service.domain.ServiceDetail;
import org.mifosplatform.portfolio.service.domain.ServiceDetailRepository;
import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.mifosplatform.portfolio.service.domain.ServiceMasterRepository;
import org.mifosplatform.portfolio.service.service.ServiceMasterReadPlatformService;
import org.mifosplatform.portfolio.servicemapping.service.ServiceMappingReadPlatformService;
import org.mifosplatform.provisioning.provisioning.domain.ModelProvisionMapping;
import org.mifosplatform.provisioning.provisioning.domain.ModelProvisionMappingRepository;
import org.mifosplatform.scheduledjobs.dataupload.data.MRNErrorData;
import org.mifosplatform.scheduledjobs.dataupload.domain.DataUpload;
import org.mifosplatform.scheduledjobs.dataupload.domain.DataUploadRepository;
import org.mifosplatform.useradministration.domain.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.mifosplatform.finance.payments.domain.Payment;
import org.mifosplatform.finance.payments.domain.PaymentRepository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.sf.json.*;

@Service
public class DataUploadHelper {

	private final DataUploadRepository dataUploadRepository;
	private final PaymentRepository paymentRepository;
	private final AdjustmentReadPlatformService adjustmentReadPlatformService;
	private final PaymentReadPlatformService paymodeReadPlatformService;
	private final MCodeReadPlatformService mCodeReadPlatformService;
	private final PropertyReadPlatformService propertyReadPlatformService;
	private final AddressReadPlatformService addressReadPlatformService;
	private final ModelProvisionMappingRepository modelProvisionMappingRepository;
	private final OfficeRepository officeRepository;
	private final CodeValueRepository codeValueRepository;
	private final PlanRepository planRepository;
	private final PlanDetailsRepository planDetailsRepository;
	private final ConfigurationRepository configurationRepository;
	private final ClientRepository clientRepository;
	private final PlanReadPlatformService planReadPlatformService;
	private final OrderRepository orderRepository;
	private final ClientServiceRepository clientServiceRepository;
	private final ServiceMasterReadPlatformService serviceMasterReadPlatformService;
	private final OrderReadPlatformService orderReadPlatformService;
	private final ItemReadPlatformService itemReadPlatformService;
	private final CodeReadPlatformService codeReadPlatformService;
	private final RoleRepository roleRepository;
	private final ClientReadPlatformService clientReadPlatformService;
	private final ContractRepository contractRepository;
	private final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService;
	private final PaymentReadPlatformService paymentReadPlatformService;
	private final ItemDetailsReadPlatformService itemDetailsReadPlatformService;

	String dateFormat = "dd MMMM yyyy";
	String date;
	String officeId;
	private Object[] joe = {};

	@Autowired
	public DataUploadHelper(final DataUploadRepository dataUploadRepository,
			final PaymentRepository paymentRepository,
			final PaymentReadPlatformService paymodeReadPlatformService,
			final AdjustmentReadPlatformService adjustmentReadPlatformService,
			final MCodeReadPlatformService mCodeReadPlatformService,
			final PropertyReadPlatformService propertyReadPlatformService,
			final AddressReadPlatformService addressReadPlatformService,
			final ModelProvisionMappingRepository modelProvisionMappingRepository,
			final OfficeRepository officeRepository, final CodeValueRepository codeValueRepository,
			final PlanRepository planRepository, final ConfigurationRepository configurationRepository,
			ClientRepository clientRepository, final PlanReadPlatformService planReadPlatformService,
			final OrderRepository orderRepository, final ClientServiceRepository clientServiceRepository,
			final OrderReadPlatformService orderReadPlatformService, final PlanDetailsRepository planDetailsRepository,
			final ItemReadPlatformService itemReadPlatformService,
			final CodeReadPlatformService codeReadPlatformService,
			final ServiceMasterReadPlatformService serviceMasterReadPlatformService,
			final RoleRepository roleRepository, final ClientReadPlatformService clientReadPlatformService,
			final ContractRepository contractRepository, final PaymentReadPlatformService paymentReadPlatformService,
			final ClientBillInfoReadPlatformService clientBillInfoReadPlatformService,
			final ItemDetailsReadPlatformService itemDetailsReadPlatformService) {

		this.dataUploadRepository = dataUploadRepository;
		this.paymentRepository=paymentRepository;
		this.paymodeReadPlatformService = paymodeReadPlatformService;
		this.adjustmentReadPlatformService = adjustmentReadPlatformService;
		this.mCodeReadPlatformService = mCodeReadPlatformService;
		this.propertyReadPlatformService = propertyReadPlatformService;
		this.addressReadPlatformService = addressReadPlatformService;
		this.modelProvisionMappingRepository = modelProvisionMappingRepository;
		this.officeRepository = officeRepository;
		this.codeValueRepository = codeValueRepository;
		this.planRepository = planRepository;
		this.configurationRepository = configurationRepository;
		this.clientRepository = clientRepository;
		this.planReadPlatformService = planReadPlatformService;
		this.orderReadPlatformService = orderReadPlatformService;
		this.orderRepository = orderRepository;
		this.clientServiceRepository = clientServiceRepository;
		this.planDetailsRepository = planDetailsRepository;
		this.itemReadPlatformService = itemReadPlatformService;
		this.codeReadPlatformService = codeReadPlatformService;
		this.serviceMasterReadPlatformService = serviceMasterReadPlatformService;
		this.roleRepository = roleRepository;
		this.clientReadPlatformService = clientReadPlatformService;
		this.contractRepository = contractRepository;
		this.paymentReadPlatformService = paymentReadPlatformService;
		this.clientBillInfoReadPlatformService = clientBillInfoReadPlatformService;
		this.itemDetailsReadPlatformService = itemDetailsReadPlatformService;

	}

	public String buildJsonForHardwareItems(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)
			throws JSONException {

		JSONObject jo1 = new JSONObject();
		JSONObject jo2 = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONArray jsonArray1 = new JSONArray();

		// GRN Id
		System.out.println("currentLineData" + currentLineData.length);
		if (currentLineData.length >= 9 && currentLineData[0] != null && !currentLineData[0].isEmpty()) {
			jo1.put("grnId", currentLineData[0]);
		}
		/*
		 * if(currentLineData[6] != null || !currentLineData[6].isEmpty()){
		 * jo2.put("grnId", currentLineData[6]); }
		 */
		if (currentLineData.length >= 9 && currentLineData[6] != null && !currentLineData[6].isEmpty()) {
			jo2.put("grnId", currentLineData[6]);
		}

		// SerialNumber
		if (currentLineData.length >= 9 && currentLineData[1] != null && !currentLineData[1].isEmpty()) {
			jo1.put("SerialNumber", currentLineData[1]);
		}
		/*
		 * if(currentLineData[7] != null || !currentLineData[7].isEmpty()){
		 * jo2.put("SerialNumber", currentLineData[7]); }
		 */

		if (currentLineData.length >= 9 && currentLineData[7] != null && !currentLineData[7].isEmpty()) {
			jo2.put("SerialNumber", currentLineData[7]);
		}

		// isPaired
		if (currentLineData.length >= 9 && currentLineData[3] != null && !currentLineData[3].isEmpty()) {
			jo1.put("isPaired", currentLineData[3]);
		}
		/*
		 * if(currentLineData[8] != null || !currentLineData[8].isEmpty()){
		 * jo2.put("isPaired", currentLineData[8]); }
		 */

		if (currentLineData.length >= 9 && currentLineData[8] != null && !currentLineData[8].isEmpty()) {
			jo2.put("isPaired", currentLineData[8]);
		}
		if (jo1 != null || !jo1.isEmpty()) {
			jsonArray.put(jo1);
		}
		if (jo2 != null || !jo1.isEmpty()) {
			jsonArray.put(jo2);
		}
		System.out.println(jo1);
		System.out.println(jo2);
		System.out.println(currentLineData.length);
		if (currentLineData.length >= 9) {
			for (int j = 0; j < jsonArray.length(); j++) {
				if (jsonArray.getJSONObject(j).has("grnId")) {
					final HashMap<String, String> map = new HashMap<>();

					// grnid
					if (currentLineData[0] != null) {
						ItemDetailsData itemDetailsData = this.itemDetailsReadPlatformService
								.retriveGrnId(jsonArray.getJSONObject(j).getLong("grnId"));
						if (itemDetailsData != null) {
							map.put("grnId", itemDetailsData.getGrnId().toString());
							map.put("itemMasterId", itemDetailsData.getItemMasterId().toString());
						//	map.put("isPairing", jsonArray.getJSONObject(j).getString("isPaired"));
							map.put("isPairing", "y");
							if (jsonArray.getJSONObject(j).getString("isPaired").replaceAll("/", "").equalsIgnoreCase("\"y\"")) {
								map.put("pairedItemId", itemDetailsData.getPairedItemId().toString());
								/* map.put("pairedItemId", "2"); */
							}
						}
					}

					// end
					map.put("serialNumber", jsonArray.getJSONObject(j).getString("SerialNumber"));
					// map.put("grnId",currentLineData[2]);

					/*
					 * if(!currentLineData[1].equals(currentLineData[4])) { errorData.add(new
					 * MRNErrorData((long)i,
					 * "Mismatch in Serial Number and Provisioning Serial Number")); return null; }
					 */
					if (currentLineData.length >= 5 && currentLineData[4] != null && !currentLineData[4].isEmpty()) {
						map.put("provisioningSerialNumber", currentLineData[4]);
					} else {
						map.put("provisioningSerialNumber", jsonArray.getJSONObject(j).getString("SerialNumber"));
					}

					map.put("quality", "good");
					map.put("status", "new");
					map.put("warranty", "12");
					map.put("remarks", "none");
					if (!currentLineData[2].isEmpty() && currentLineData[2] != null) {
						ModelProvisionMapping modelProvisionMapping = this.modelProvisionMappingRepository
								.findOneByModel(currentLineData[2]);
						if (modelProvisionMapping != null) {
							map.put("itemModel", modelProvisionMapping.getId().toString());

						} else if(currentLineData[2] != null){
							map.put("itemModel", "ITEMMODEL");

						}
						else {
							errorData.add(new MRNErrorData((long) i, "invalid model"));
							return null;
						}

					} else {
						map.put("itemModel", "ITEMMODEL");
					}

					/*
					 * if("N".equalsIgnoreCase(currentLineData[6])){ map.put("isPairing", "N");
					 * }else{ if(!currentLineData[7].equals(currentLineData[0]) ){
					 * map.put("isPairing", "Y"); map.put("pairedItemId", currentLineData[7]);
					 * }else{ map.put("isPairing", "N"); } }
					 */
					if (currentLineData.length >= 9 && currentLineData[5] != null) {
						map.put("cartoonNumber", currentLineData[5]);
					}
					map.put("locale", "en");
					jsonArray1.put(map);

				}
			}

			return new Gson().toJson(jsonArray1.toString());
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;

		}
	}

	public String buildJsonForHardwareItemsnotpaired(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)
			throws JSONException {
		JSONObject jo1 = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONArray jsonArray1 = new JSONArray();

		jo1.put("grnId", currentLineData[0]);
		jo1.put("SerialNumber", currentLineData[1]);
		jo1.put("isPaired", currentLineData[3]);

		if (jo1 != null || !jo1.isEmpty()) {
			jsonArray.put(jo1);
		}

		if (currentLineData.length >= 5) {

			for (int j = 0; j < jsonArray.length(); j++) {
				if (jsonArray.getJSONObject(j).has("grnId")) {
					final HashMap<String, String> map = new HashMap<>();

// grnid
					if (currentLineData[0] != null) {
						ItemDetailsData itemDetailsData = this.itemDetailsReadPlatformService
								.retriveGrnId(jsonArray.getJSONObject(j).getLong("grnId"));
						if (itemDetailsData != null) {
							map.put("grnId", itemDetailsData.getGrnId().toString());
							map.put("itemMasterId", itemDetailsData.getItemMasterId().toString());
							map.put("isPairing", jsonArray.getJSONObject(j).getString("isPaired"));
							if (jsonArray.getJSONObject(j).getString("isPaired").equalsIgnoreCase("y")) {
								map.put("pairedItemId", itemDetailsData.getPairedItemId().toString());
								map.put("pairedItemId", "2");
							}
						}
					}

					map.put("serialNumber", jsonArray.getJSONObject(j).getString("SerialNumber"));

					if (currentLineData.length >= 5 && currentLineData[4] != null && !currentLineData[4].isEmpty()) {
						map.put("provisioningSerialNumber", currentLineData[4]);
					} else {
						map.put("provisioningSerialNumber", jsonArray.getJSONObject(j).getString("SerialNumber"));
					}

					map.put("quality", "good");
					map.put("status", "new");
					map.put("warranty", "12");
					map.put("remarks", "none");
					if (!currentLineData[2].isEmpty() && currentLineData[2] != null) {
						ModelProvisionMapping modelProvisionMapping = this.modelProvisionMappingRepository
								.findOneByModel(currentLineData[2]);
						if (modelProvisionMapping != null) {
							map.put("itemModel", modelProvisionMapping.getId().toString());

						} else {
							errorData.add(new MRNErrorData((long) i, "invalid model"));
							return null;
						}

					} else {
						map.put("itemModel", currentLineData[2]);
					}
					if (currentLineData.length >= 9 && currentLineData[5] != null) {
						map.put("cartoonNumber", currentLineData[5]);
					}
					map.put("locale", "en");
					jsonArray1.put(map);

				}
			}

			return new Gson().toJson(jsonArray1.toString());
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;

		}
	}

	public String buildJsonForMrn(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {
		final HashMap<String, String> map = new HashMap<>();
		if (currentLineData.length >= 3) {
			if (!currentLineData[0].isEmpty() && currentLineData[0] != null) {
				map.put("mrnId", currentLineData[0]);
			} else {
				errorData.add(new MRNErrorData((long) i, "enter mrnId"));
			}
			if (!currentLineData[1].isEmpty() && currentLineData[1] != null) {
				map.put("serialNumber", currentLineData[1]);
			} else {
				errorData.add(new MRNErrorData((long) i, "enter serialnumber"));
			}
			map.put("type", currentLineData[2]);
		} else if (currentLineData.length == 2) {
			errorData.add(new MRNErrorData((long) i, "enter type"));
		} else if (currentLineData.length == 1) {
			errorData.add(new MRNErrorData((long) i, "enter serialnumber"));
			errorData.add(new MRNErrorData((long) i, "enter type"));
		} else {
			errorData.add(new MRNErrorData((long) i, "enter serialnumber"));
			errorData.add(new MRNErrorData((long) i, "enter type"));
			errorData.add(new MRNErrorData((long) i, "enter mrnId"));
		}

		map.put("locale", "en");
		return new Gson().toJson(map);
	}

	public String buildJsonForMrnCartoon(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 3) {
			final HashMap<String, String> map = new HashMap<>();
			if (!currentLineData[0].isEmpty()) {
				map.put("mrnId", currentLineData[0]);
			} else {
				errorData.add(new MRNErrorData((long) i, "enter mrnId"));
			}
			if (!currentLineData[1].isEmpty()) {
				map.put("cartoonNumber", currentLineData[1]);
			} else {
				errorData.add(new MRNErrorData((long) i, "enter cartoonNumber"));
			}

			if (!currentLineData[2].isEmpty()) {
				map.put("type", currentLineData[2]);
			} else {
				errorData.add(new MRNErrorData((long) i, "enter type"));
			}
			map.put("locale", "en");
			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	public String buildJsonForMoveItems(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 2) {
			final HashMap<String, String> map = new HashMap<>();
			map.put("itemId", currentLineData[0]);
			map.put("serialNumber", currentLineData[1]);
			map.put("type", currentLineData[2]);
			map.put("locale", "en");
			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}

	public String buildJsonForEpg(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)
			throws ParseException {

		if (currentLineData.length >= 11) {
			final HashMap<String, String> map = new HashMap<>();
			map.put("channelName", currentLineData[0]);
			map.put("channelIcon", currentLineData[1]);
			map.put("programDate", new SimpleDateFormat("dd/MM/yyyy").parse(currentLineData[2]).toString());
			map.put("startTime", currentLineData[3]);
			map.put("stopTime", currentLineData[4]);
			map.put("programTitle", currentLineData[5]);
			map.put("programDescription", currentLineData[6]);
			map.put("type", currentLineData[7]);
			map.put("genre", currentLineData[8]);
			map.put("locale", currentLineData[9]);
			map.put("dateFormat", currentLineData[10]);
			map.put("locale", "en");
			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	public String buildJsonForAdjustment(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 6) {
			final HashMap<String, String> map = new HashMap<>();
			List<AdjustmentData> adjustmentDataList = this.adjustmentReadPlatformService.retrieveAllAdjustmentsCodes();
			if (!adjustmentDataList.isEmpty()) {
				for (AdjustmentData adjustmentData : adjustmentDataList) {
					if (adjustmentData.getAdjustmentCode().equalsIgnoreCase(currentLineData[2].toString())) {

						map.put("adjustment_code", adjustmentData.getId().toString());
						break;
					} else {
						map.put("adjustment_code", String.valueOf(-1));
					}
				}
				String adjustmentCode = map.get("adjustment_code");
				if (adjustmentCode != null && Long.valueOf(adjustmentCode) <= 0) {

					throw new AdjustmentCodeNotFoundException(currentLineData[2].toString());
				}
				map.put("adjustment_date", currentLineData[1]);
				map.put("adjustment_type", currentLineData[3]);
				map.put("amount_paid", currentLineData[4]);
				map.put("Remarks", currentLineData[5]);
				map.put("locale", "en");
				map.put("dateFormat", "dd MMMM yyyy");
				return new Gson().toJson(map);
			} else {
				errorData.add(new MRNErrorData((long) i, "Adjustment Type list is empty"));
				return null;
			}
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}

	public String buildjsonForPayments(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length <= 8) {
			final HashMap<String, String> map = new HashMap<>();
			Collection<McodeData> paymodeDataList = this.paymodeReadPlatformService
					.retrievemCodeDetails("Payment Mode");
			if (!paymodeDataList.isEmpty()) {
				for (McodeData paymodeData : paymodeDataList) {
					if (paymodeData.getPaymodeCode().equalsIgnoreCase(currentLineData[1])) {
						map.put("paymentCode", paymodeData.getId().toString());
						break;
					} else {
						map.put("paymentCode", "-1");
					}
				}
				Client client = this.clientRepository.findOne(Long.parseLong(currentLineData[0]));
				map.put("clientPoId", client.getPoid());
				String paymentCode = map.get("paymentCode");
				if (paymentCode != null && Long.valueOf(paymentCode) <= 0) {
					throw new PaymentCodeNotFoundException(currentLineData[1].toString());
				}
				SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
				map.put("paymentType", currentLineData[1]);
				if (currentLineData[1].equalsIgnoreCase("Cheque")) {
					map.put("chequeNo", currentLineData[5]);
					Date chequeDate = new Date(currentLineData[6]);
					map.put("chequeDate", formatter1.format(chequeDate));
					map.put("bankName", currentLineData[7]);
					map.put("branchName", currentLineData[8]);
					map.put("isChequeSelected", "yes");
				}

				map.put("amountPaid", currentLineData[2]);
				map.put("receiptNo", currentLineData[3]);
				map.put("remarks", currentLineData[4]);
				map.put("currencyCode", "ZWD");
				map.put("locale", "en");
				map.put("dateFormat", "dd MMMM yyyy");
				map.put("isSubscriptionPayment", "false");
				map.put("paymentSource", "null");
				map.put("dateFormat", "dd MMMM yyyy");
				Date date = new Date();
				map.put("paymentDate", formatter1.format(date));

				return new Gson().toJson(map);
			} else {
				errorData.add(new MRNErrorData((long) i, "Paymode type list empty"));
				return null;
			}

		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}


  public String buildjsonForPaymentscancel(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)
  {
  if (currentLineData.length <= 9) {
		final HashMap<String, String> map = new HashMap<>();
		Collection<McodeData> paymodeDataList = this.paymodeReadPlatformService
				.retrievemCodeDetails("Payment Mode");
		if (!paymodeDataList.isEmpty()) {
			for (McodeData paymodeData : paymodeDataList) {
				if (paymodeData.getPaymodeCode().equalsIgnoreCase(currentLineData[2])) {
					map.put("paymentCode", paymodeData.getId().toString());
					break;
				} else {
					map.put("paymentCode", "-1");
				}
			}
			Payment payment = this.paymentRepository.findOne(Long.parseLong(currentLineData[0]));
			Client client = this.clientRepository.findOne(Long.parseLong(currentLineData[1]));
			map.put("clientPoId", client.getPoid());
			String paymentCode = map.get("paymentCode");
			if (paymentCode != null && Long.valueOf(paymentCode) <= 0) {
				throw new PaymentCodeNotFoundException(currentLineData[2].toString());
			}
			SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
			map.put("paymentType", currentLineData[2]);
			if (currentLineData[1].equalsIgnoreCase("Cheque")) {
				map.put("chequeNo", currentLineData[6]);
				Date chequeDate = new Date(currentLineData[7]);
				map.put("chequeDate", formatter1.format(chequeDate));
				map.put("bankName", currentLineData[8]);
				map.put("branchName", currentLineData[9]);
				map.put("isChequeSelected", "yes");
			}

			map.put("amountPaid", currentLineData[3]);
			map.put("receiptNo", currentLineData[4]);
			map.put("cancelRemark", currentLineData[5]);
			map.put("currencyCode", "ZWD");
			map.put("locale", "en");
			map.put("dateFormat", "dd MMMM yyyy");
			map.put("isSubscriptionPayment", "false");
			map.put("paymentSource", "null");
			map.put("dateFormat", "dd MMMM yyyy");
			Date date = new Date();
			map.put("paymentDate", formatter1.format(date));

			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Paymode type list empty"));
			return null;
		}

	} else {
		errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
		return null;
	}
}

public String buildForMediaAsset(Row mediaRow, Row mediaAttributeRow, Row mediaLocationRow) {
		final HashMap<String, String> map = new HashMap<>();
		map.put("mediaTitle", mediaRow.getCell(0).getStringCellValue());// -
		map.put("mediaType", mediaRow.getCell(1).getStringCellValue());// -
		map.put("mediaCategoryId", mediaRow.getCell(2).getStringCellValue());// -
		map.put("image", mediaRow.getCell(3).getStringCellValue());// -
		map.put("duration", mediaRow.getCell(4).getStringCellValue());// -
		map.put("genre", mediaRow.getCell(5).getStringCellValue());// -
		map.put("subject", mediaRow.getCell(6).getStringCellValue());// -
		map.put("overview", mediaRow.getCell(7).getStringCellValue());// -
		map.put("contentProvider", mediaRow.getCell(8).getStringCellValue());// -
		map.put("rated", mediaRow.getCell(9).getStringCellValue());// -
		// map.put("rating",mediaRow.getCell(10).getNumericCellValue());//-
		map.put("rating", mediaRow.getCell(10).getStringCellValue());// -
		map.put("status", mediaRow.getCell(11).getStringCellValue());// -
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
		map.put("releaseDate", formatter.format(mediaRow.getCell(12).getDateCellValue()));
		map.put("dateFormat", mediaRow.getCell(13).getStringCellValue());
		map.put("locale", mediaRow.getCell(14).getStringCellValue());

		JSONArray a = new JSONArray();
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		m.put("attributeType", mediaAttributeRow.getCell(0).getStringCellValue());
		m.put("attributeName", mediaAttributeRow.getCell(1).getNumericCellValue());
		m.put("attributevalue", mediaAttributeRow.getCell(2).getStringCellValue());
		m.put("attributeNickname", mediaAttributeRow.getCell(3).getStringCellValue());
		m.put("attributeImage", mediaAttributeRow.getCell(4).getStringCellValue());

		a.put(m);
		map.put("mediaassetAttributes", a.toString());

		JSONArray b = new JSONArray();
		Map<String, Object> n = new LinkedHashMap<String, Object>();
		n.put("languageId", mediaLocationRow.getCell(0).getNumericCellValue());
		n.put("formatType", mediaLocationRow.getCell(1).getStringCellValue());
		n.put("location", mediaLocationRow.getCell(2).getStringCellValue());
		b.put(n);

		map.put("mediaAssetLocations", b.toString());
		return new Gson().toJson(map);
	}

	public String buildforitemSale(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)
			throws ParseException {
		final HashMap<String, String> map = new HashMap<>();
		if (currentLineData.length >= 6) {
			map.put("agentId", currentLineData[0]);
			map.put("itemId", currentLineData[1]);
			map.put("orderQuantity", currentLineData[2]);
			map.put("chargeAmount", currentLineData[3]);
			map.put("taxPercantage", currentLineData[4]);

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yy");
			Date date = formatter.parse(currentLineData[5]);
			SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");

			map.put("locale", "en");
			map.put("dateFormat", "dd MMMM yyyy");
			map.put("purchaseDate", formatter1.format(date));
			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}

	public String buildJsonForOffice(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)
			throws ParseException {

		if (currentLineData.length >= 9) {
			final HashMap<String, String> map = new HashMap<>();
			// final HashMap<String, String> map1 = new HashMap<>();

			JSONObject map1 = new JSONObject();
			// Map<String, Object> map1 = new LinkedHashMap<String, Object>();
			Map<String, Object> map2 = new LinkedHashMap<String, Object>();
			Map<String, Object> map3 = new LinkedHashMap<String, Object>();

			JSONArray a = new JSONArray();

			// parent name business logic
			if (!currentLineData[0].isEmpty()) {
				Office office = this.officeRepository.findwithName(currentLineData[0]);
				if (office != null) {
					map.put("parentId", office.getId().toString());
					map1.put("officeId", office.getId());
				} else {
					errorData.add(new MRNErrorData((long) i, "invalid parent name"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "parent name not available"));
				return null;
			}

			// office type business logic
			if (!currentLineData[1].isEmpty()) {
				map.put("officeType", currentLineData[1]);
				/*
				 * CodeValue codeValue =
				 * this.codeValueRepository.findOneByCodeValue(currentLineData[1]);
				 * if(codeValue!=null) { map.put("officeType",codeValue.getId().toString());
				 * }else { errorData.add(new MRNErrorData((long)i, "invalid office type"));
				 * return null; }
				 */
			} else {
				errorData.add(new MRNErrorData((long) i, "office type not available"));
				return null;
			}
			// business Type business logic
			if (!currentLineData[2].isEmpty()) {
				CodeValue codeValue = this.codeValueRepository.findOneByCodeValue(currentLineData[2]);
				if (codeValue != null) {
					map.put("businessType", currentLineData[2]);
				} else {
					errorData.add(new MRNErrorData((long) i, "invalid business Type"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "business Type not available"));
				return null;
			}

			// map.put("officeType",currentLineData[1]);
			if (!currentLineData[3].isEmpty()) {
				map.put("name", currentLineData[3]);
			} else {
				errorData.add(new MRNErrorData((long) i, "name  cannot be blank or name should be alphabets"));
				return null;
			} /*
				 * if(!currentLineData[4].isEmpty()) { map.put("externalId",currentLineData[4]);
				 * }else { errorData.add(new MRNErrorData((long)i,
				 * "plz enter office description")); return null; }
				 */
			if (!currentLineData[4].isEmpty()) {
				map.put("contactPerson", currentLineData[4]);
				map1.put("firstname", currentLineData[4]);
			} else {
				errorData.add(new MRNErrorData((long) i, "contact person can not be blank"));
				return null;
			}
			if (!currentLineData[5].isEmpty() && currentLineData[5].length() >= 10) {
				map.put("phoneNumber", currentLineData[5]);
				map1.put("phone", currentLineData[5]);
			} else {
				errorData.add(new MRNErrorData((long) i, "phone number must be in 10 digits"));
				return null;
			}
			if (!currentLineData[6].isEmpty()) {
				map.put("email", currentLineData[6]);
				map1.put("email", currentLineData[6]);
			} else {
				errorData.add(new MRNErrorData((long) i, "email can not be blank"));
				return null;
			}
			if (!currentLineData[7].isEmpty()) {
				map.put("addressName", currentLineData[7]);
				map2.put("addressNo", currentLineData[7]);
				map3.put("addressNo", currentLineData[7]);
			} else {
				errorData.add(new MRNErrorData((long) i, "address name can not be blank"));
				return null;
			}

			if (!currentLineData[8].isEmpty()) {
				final AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(currentLineData[8]);
				if (addressData != null) {
					map.put("city", currentLineData[8]);
					map.put("district", addressData.getDistrict());
					map.put("state", addressData.getState());
					map.put("country", addressData.getCountry());

					map2.put("city", currentLineData[8]);
					map2.put("state", addressData.getState());
					map2.put("country", addressData.getCountry());
					map2.put("district", addressData.getDistrict());

					map3.put("city", currentLineData[8]);
					map3.put("state", addressData.getState());
					map3.put("country", addressData.getCountry());
					map3.put("district", addressData.getDistrict());
				} else {
					errorData.add(new MRNErrorData((long) i, "city is invalid"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "city is not vailable"));
				return null;
			}

			map.put("zip", currentLineData[9]);
			map2.put("zipCode", currentLineData[9]);
			map3.put("zipCode", currentLineData[9]);

			map.put("locale", "en");
			map.put("dateFormat", "dd MMMM yyyy");

			Date date = new Date();
			SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
			map.put("openingDate", formatter1.format(date));

			if (!currentLineData[10].isEmpty()) {
				map.put("pancardNo", currentLineData[10]);
			} else {
				errorData.add(new MRNErrorData((long) i, "Pancard Number cannot be blank"));
				return null;
			}

			if (!currentLineData[11].isEmpty()) {
				map.put("companyRegNo", currentLineData[11]);
			} else {
				errorData.add(new MRNErrorData((long) i, "Company Registration cannot be blank"));
				return null;
			}

			if (!currentLineData[12].isEmpty()) {
				map.put("gstRegNo", currentLineData[12]);
			} else {
				errorData.add(new MRNErrorData((long) i, "GST Registration cannot be blank"));
				return null;
			}

			if (!currentLineData[13].isEmpty()) {
				String commisionModel = currentLineData[13];
				if (commisionModel.equalsIgnoreCase("PostPaid")) {
					commisionModel = "0";
				} else if (commisionModel.equalsIgnoreCase("Prepaid")) {
					commisionModel = "1";
				} else {
					errorData.add(new MRNErrorData((long) i, "Commision Model can be either Postpaid or Prepaid"));
					return null;
				}
				map.put("commisionModel", commisionModel);
			} else {
				errorData.add(new MRNErrorData((long) i, "Commision Model cannot be blank"));
				return null;
			}

			if (!currentLineData[14].isEmpty()) {
				String payment = currentLineData[14];
				if (payment.equalsIgnoreCase("Advance")) {
					payment = "Advance";
				} else if (payment.equalsIgnoreCase("Arrear")) {
					payment = "Arrear";
				} else if (payment.equalsIgnoreCase("Prepaid")) {
					payment = "Prepaid";
				} else {
					errorData.add(new MRNErrorData((long) i, "Payment Type can be either Advance, Arrear or Prepaid"));
					return null;
				}
				map.put("payment", payment);
			} else {
				errorData.add(new MRNErrorData((long) i, "Payment Type can be either Advance, Arrear or Prepaid"));
				return null;
			}

			if (!currentLineData[15].isEmpty()) {
				String subscriberdues = currentLineData[15];
				if (subscriberdues.equalsIgnoreCase("Yes")) {
					subscriberdues = "true";
				} else if (subscriberdues.equalsIgnoreCase("No")) {
					subscriberdues = "false";
				} else {
					errorData.add(new MRNErrorData((long) i, "Subscriber Dues can be either Yes or No"));
					return null;
				}
				map.put("subscriberdues", subscriberdues);
			} else {
				errorData.add(new MRNErrorData((long) i, "Subscriber Dues can be either Yes or No"));
				return null;
			}

			if (!currentLineData[16].isEmpty()) {
				String dasType = currentLineData[16];
				CodeValue codeValue = this.codeValueRepository.findOneByCodeValue(dasType.toUpperCase());
				if (codeValue != null) {
					map.put("dasType", codeValue.getId().toString());
				} else {
					errorData.add(new MRNErrorData((long) i, "The Entered Das Type is incorrect"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "Das Type should not be empty"));
				return null;
			}

			map1.put("entryType", "IND");
			map1.put("clientCategory", 20);
			map1.put("title", "Mr");
			map1.put("lastname", ".");
			map1.put("idKey", 260);
			map1.put("idValue", "00000");
			map1.put("externalId", "");
			map1.put("billMode", "Both");
			map1.put("locale", "en");
			map1.put("active", "true");
			map1.put("dateFormat", "dd MMMM yyyy");
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
			map1.put("activationDate", formatter1.format(date));
			map1.put("flag", "false");

			map2.put("addressType", "PRIMARY");
			map3.put("addressType", "BILLING");

			a.put(map2);
			a.put(map3);

			map1.put("address", a.toString());
			map.put("clientData", map1.toString());

			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	// Add plan
	public JSONObject buildJsonForAddPlan(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)
			throws ParseException, JSONException {

		if (currentLineData.length >= 2) {

			JSONArray plans = new JSONArray();
			JSONObject planObject = new JSONObject();
			final HashMap<String, String> map = new HashMap<>();
			/*
			 * planObject.put("contractPeriod",currentLineData[1]);
			 * planObject.put("paytermCode",currentLineData[2]);
			 */
			ClientServiceData clientServiceData = this.clientReadPlatformService.retriveServiceId(currentLineData[1]);
			if (clientServiceData != null) {
				planObject.put("clientPoId", clientServiceData.getClientPoId());
				planObject.put("clientServicePoId", clientServiceData.getClientServicePoId());
				planObject.put("clientId", clientServiceData.getClientId().toString());
				planObject.put("clientServiceId", clientServiceData.getId());
			}

			ClientBillInfoData clientbilldata = this.clientBillInfoReadPlatformService
					.retriveSingleClientBillInfo(clientServiceData.getClientId());

			if (!currentLineData[0].isEmpty()) {
				final PlanData planData = this.planReadPlatformService.retrievePlanDataPoIdsNew(currentLineData[0]);

				if (planData != null) {
					if (planData.getPlanPoid() != null) {
						planObject.put("planPoId", planData.getPlanPoid().toString());
					}
					if (planData.getDealPoid() != null) {
						planObject.put("dealPoId", planData.getDealPoid().toString());
					}

					planObject.put("planDescription", planData.getplanDescription());
					planObject.put("id", planData.getId());
					planObject.put("planCode", planData.getId().toString());

					if (planData.getIsPrepaid().equalsIgnoreCase("Y")) {
						planObject.put("contractPeriod", clientbilldata.getBillFrequency());
						planObject.put("paytermCode", clientbilldata.getBillFrequencyCode());
					} else {
						planObject.put("contractPeriod", "1");
						planObject.put("paytermCode", clientbilldata.getBillFrequencyCode());
					}

				} else {
					errorData.add(new MRNErrorData((long) i, "plancode is invalid"));
					return null;
				}
			}

			planObject.put("isNewplan", "true");
			planObject.put("locale", "en");
			planObject.put("dateFormat", "dd MMMM yyyy");
			Date today = new Date();
			SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
			planObject.put("start_date", formatter1.format(today));

			/*
			 * plans.put(planObject); map.put("plans",plans.toString()); String returnValue
			 * = new Gson().toJson(map); returnValue = returnValue.replace("\\", "");
			 * returnValue = returnValue.replace("\"{", "{"); returnValue =
			 * returnValue.replace("}\"", "}"); returnValue = returnValue.replace("\"[",
			 * "["); returnValue = returnValue.replace("]\"", "]");
			 */

			return planObject;
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	// change plan
	public String buildJsonForChangePlan(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)
			throws ParseException, JSONException {

		if (currentLineData.length >= 4) {

			final HashMap<String, String> map = new HashMap<>();
			map.put("billAlign", "true");

			ClientServiceData clientServiceData = this.clientReadPlatformService.retriveServiceId(currentLineData[3]);
			if (clientServiceData != null) {
				map.put("clientServiceId", clientServiceData.getId().toString());
				map.put("clientId", clientServiceData.getClientId().toString());
			}
			ClientBillInfoData clientbilldata = this.clientBillInfoReadPlatformService
					.retriveSingleClientBillInfo(clientServiceData.getClientId());

			PlanData planData = null;
			if (!currentLineData[0].isEmpty()) {
				planData = this.planReadPlatformService.retrievePlanDataPoIdsUsingPlanCode(currentLineData[0]);
				if (planData != null) {
					map.put("planCode", planData.getId().toString());
					if (planData.getPlanPoid() != null)
						map.put("planPoId", planData.getPlanPoid().toString());
					if (planData.getDealPoid() != null)
						map.put("dealPoId", planData.getDealPoid().toString());
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "invalid plan code"));
				return null;
			}
			// map.put("planCode",currentLineData[0]);
			// map.put("contractPeriod",currentLineData[1]);
			System.out.println(currentLineData[3]);
			OrderData orderData = this.orderReadPlatformService
					.retrieveAllPoidsRelatedToOrder(Long.parseLong(currentLineData[1]));
			if (orderData != null) {
				map.put("clientPoId", orderData.getClientPoId());
				map.put("clientServicePoId", orderData.getClientServicePoId());
				map.put("oldPlanPoId", orderData.getPlanPoId());
				map.put("oldDealPoId", orderData.getDealPoId());

			}
			if (planData.getIsPrepaid().equalsIgnoreCase("Y")) {
				map.put("paytermCode", clientbilldata.getBillFrequencyCode());
				map.put("contractPeriod", clientbilldata.getBillFrequency().toString());
			} else {
				map.put("paytermCode", clientbilldata.getBillFrequencyCode());
				map.put("contractPeriod", "1");
			}
			// map.put("clientServiceId",currentLineData[3]);

			/*
			 * map.put("dateFormat","dd MMMM yyyy"); SimpleDateFormat formatter = new
			 * SimpleDateFormat("dd MMMM yyyy"); Date
			 * date=formatter.parse(currentLineData[4]); SimpleDateFormat formatter1 = new
			 * SimpleDateFormat("dd MMMM yyyy");
			 * map.put("start_date",formatter1.format(date)); SimpleDateFormat formatter2 =
			 * new SimpleDateFormat("dd MMMM yyyy"); Date
			 * date1=formatter.parse(currentLineData[5]);
			 * map.put("disconnectionDate",formatter1.format(date1));
			 * map.put("disconnectReason",currentLineData[6]);
			 */

			map.put("dateFormat", "dd MMMM yyyy");
			Date today = new Date();
			SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
			map.put("start_date", formatter1.format(today));
			SimpleDateFormat formatter2 = new SimpleDateFormat("dd MMMM yyyy");
			map.put("disconnectionDate", formatter2.format(today));
			map.put("disconnectReason", currentLineData[2]);
			map.put("isNewplan", "true");
			map.put("locale", "en");
			map.put("changePlanDetail", orderData.getPlanCode() + " to " + currentLineData[0]);

			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	// cancel plan
	public String buildJsonForCancelPlan(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i)
			throws ParseException {

		if (currentLineData.length >= 5) {
			final HashMap<String, String> map = new HashMap<>();
			ClientServiceData clientServiceData = this.clientReadPlatformService.retriveServiceId(currentLineData[0]);
			if (clientServiceData != null) {
				map.put("clientPoId", clientServiceData.getClientPoId());
				map.put("clientServicePoId", clientServiceData.getClientServicePoId());
			}
			map.put("disconnectReason", currentLineData[1]);
			map.put("description", currentLineData[2]);
			if (!currentLineData[3].isEmpty()) {

				OrderData orderData = this.orderReadPlatformService
						.retrieveAllPoidsRelatedToOrder(Long.parseLong(currentLineData[4]));
				if (orderData != null) {
					map.put("clientPoId", orderData.getClientPoId());
					map.put("clientServicePoId", orderData.getClientServicePoId());
					map.put("planPoId", orderData.getPlanPoId());
					map.put("dealPoId", orderData.getDealPoId());
					map.put("orderNo", orderData.getOrderNo());
				}
			}

			map.put("locale", "en");
			map.put("dateFormat", "dd MMMM yyyy");
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
			Date date = formatter.parse(currentLineData[3]);
			SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
			map.put("disconnectionDate", formatter1.format(date));

			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}

	public List<Map<String, String>> buildJsonForSuspend(String[] currentLineData, ArrayList<MRNErrorData> errorData,
			int i) throws ParseException {

		if (currentLineData.length >= 3) {

			if (currentLineData[0].equalsIgnoreCase("Account No")) {
				return this.jsonSupportForServiceClient(currentLineData, "account_no", errorData, i);
			} else if (currentLineData[0].equalsIgnoreCase("Chip Id")) {
				return this.jsonSupportForServiceSTB(currentLineData, errorData, i);
			} else if (currentLineData[0].equalsIgnoreCase("Email Id")) {
				return this.jsonSupportForServiceClient(currentLineData, "Email Id", errorData, i);
			} else if (currentLineData[0].equalsIgnoreCase("Legacy No")) {
				return this.jsonSupportForServiceClient(currentLineData, "Legacy No", errorData, i);
			} else if (currentLineData[0].equalsIgnoreCase("Serial No")) {
				return this.jsonSupportForServiceSTB(currentLineData, errorData, i);
			} else {
				errorData.add(new MRNErrorData((long) i, "entered key value is not correct"));
				return null;
			}
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}

	public List<Map<String, String>> jsonSupportForServiceClient(String[] currentLineData, String columnName,
			ArrayList<MRNErrorData> errorData, int i) throws ParseException {

		Date today = new Date();
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");

		List<Map<String, String>> multipleJson = new ArrayList<Map<String, String>>();
		if (!currentLineData[0].isEmpty()) {
			final ClientData clientData = this.clientReadPlatformService.retrieveSearchClientId(columnName,
					currentLineData[1]);
			if (clientData != null) {
				if (currentLineData[3].equalsIgnoreCase("0")) {
					List<ClientService> clientServiceList = this.clientServiceRepository
							.findwithClientId(clientData.getId());
					if (!clientServiceList.isEmpty()) {
						for (ClientService clientService : clientServiceList) {
							final HashMap<String, String> map = new HashMap<>();
							map.put("clientPoId", clientData.getPoid());
							map.put("suspensionDate", formatter1.format(today));
							map.put("suspensionReason", currentLineData[2]);
							map.put("clientServicePoId", clientService.getClientServicePoid());
							map.put("clientId", clientData.getId().toString());
							map.put("locale", "en");
							map.put("dateFormat", "dd MMMM yyyy");
							map.put("id", clientService.getId().toString());
							multipleJson.add(map);
						}
					} else {
						errorData.add(new MRNErrorData((long) i, "The given client has no service"));
						return null;
					}
				} else {
					ClientService clientService = this.clientServiceRepository
							.findOne(Long.parseLong(currentLineData[3]));
					if (clientService != null) {
						final HashMap<String, String> map = new HashMap<>();
						map.put("clientPoId", clientData.getPoid());
						map.put("suspensionDate", formatter1.format(today));
						map.put("suspensionReason", currentLineData[2]);
						map.put("clientServicePoId", clientService.getClientServicePoid());
						map.put("clientId", clientData.getId().toString());
						map.put("locale", "en");
						map.put("dateFormat", "dd MMMM yyyy");
						map.put("id", clientService.getId().toString());
						multipleJson.add(map);
					} else {
						errorData.add(new MRNErrorData((long) i, "The given client service Id entered is not correct"));
						return null;
					}
				}

			}

		} else {
			errorData.add(new MRNErrorData((long) i, "key and value mismatch combination"));
			return null;
		}
		return multipleJson;

	}

	public List<Map<String, String>> jsonSupportForServiceSTB(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) throws ParseException {

		Date today = new Date();
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");

		List<Map<String, String>> multipleJson = new ArrayList<Map<String, String>>();
		if (!currentLineData[0].isEmpty()) {
			ClientServiceData clientServiceData = this.clientReadPlatformService.retriveServiceId(currentLineData[1]);
			final HashMap<String, String> map = new HashMap<>();
			if (!currentLineData[2].isEmpty()) {
				map.put("clientPoId", clientServiceData.getClientPoId());
				map.put("suspensionDate", formatter1.format(today));
				map.put("suspensionReason", currentLineData[2]);
				map.put("clientServicePoId", clientServiceData.getClientServicePoId());
				map.put("clientId", clientServiceData.getClientId().toString());
				map.put("locale", "en");
				map.put("dateFormat", "dd MMMM yyyy");
				map.put("id", clientServiceData.getId().toString());
				multipleJson.add(map);
			} else {
				errorData.add(new MRNErrorData((long) i, "Please enter Reason"));
				return null;

			}
		} else {
			errorData.add(new MRNErrorData((long) i, "key and value mismatch combination"));
			return null;
		}
		return multipleJson;
	}

	// reactive
	public List<Map<String, String>> buildJsonForreactive(String[] currentLineData, ArrayList<MRNErrorData> errorData,
			int i) throws ParseException {

		/*
		 * if(currentLineData.length>=1){
		 * 
		 * final HashMap<String, String> map = new HashMap<>();
		 * 
		 * map.put("clientId", currentLineData[0]);
		 * 
		 * Client
		 * client=this.clientRepository.findOne(Long.parseLong(currentLineData[0]));
		 * map.put("clientPoId", client.getPoid()); ClientService clientService=
		 * this.clientServiceRepository.findOne(Long.parseLong(currentLineData[1]));
		 * map.put("clientServicePoId", clientService.getClientServicePoid()); return
		 * new Gson().toJson(map); }else{ errorData.add(new MRNErrorData((long)i,
		 * "Improper Data in this line")); return null; }
		 */
		if (currentLineData.length >= 2) {

			if (currentLineData[0].equalsIgnoreCase("Account No")) {
				return this.jsonSupportForServiceClients(currentLineData, "account_no", errorData, i);
			} else if (currentLineData[0].equalsIgnoreCase("Chip Id")) {
				return this.jsonSupportForServiceSTBs(currentLineData, errorData, i);
			} else if (currentLineData[0].equalsIgnoreCase("Email Id")) {
				return this.jsonSupportForServiceClients(currentLineData, "Email Id", errorData, i);
			} else if (currentLineData[0].equalsIgnoreCase("Legacy No")) {
				return this.jsonSupportForServiceClients(currentLineData, "Legacy No", errorData, i);
			} else if (currentLineData[0].equalsIgnoreCase("Serial No")) {
				return this.jsonSupportForServiceSTBs(currentLineData, errorData, i);
			} else {
				errorData.add(new MRNErrorData((long) i, "entered key value is not correct"));
				return null;
			}
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}

	public List<Map<String, String>> jsonSupportForServiceClients(String[] currentLineData, String columnName,
			ArrayList<MRNErrorData> errorData, int i) throws ParseException {

		List<Map<String, String>> multipleJson = new ArrayList<Map<String, String>>();
		if (!currentLineData[0].isEmpty()) {
			final ClientData clientData = this.clientReadPlatformService.retrieveSearchClientId(columnName,
					currentLineData[1]);
			if (clientData != null) {
				if (currentLineData[2].equalsIgnoreCase("0")) {
					List<ClientService> clientServiceList = this.clientServiceRepository
							.findwithClientIdSuspend(clientData.getId());
					if (!clientServiceList.isEmpty()) {
						for (ClientService clientService : clientServiceList) {
							final HashMap<String, String> map = new HashMap<>();
							map.put("clientPoId", clientData.getPoid());
							map.put("clientServicePoId", clientService.getClientServicePoid());
							map.put("clientId", clientData.getId().toString());
							map.put("id", clientService.getId().toString());
							multipleJson.add(map);
						}
					} else {
						errorData.add(new MRNErrorData((long) i, "The given client has no service"));
						return null;
					}
				} else {
					ClientService clientService = this.clientServiceRepository
							.findOne(Long.parseLong(currentLineData[2]));
					if (clientService != null) {
						final HashMap<String, String> map = new HashMap<>();
						map.put("clientPoId", clientData.getPoid());
						map.put("clientServicePoId", clientService.getClientServicePoid());
						map.put("clientId", clientData.getId().toString());
						map.put("id", clientService.getId().toString());
						multipleJson.add(map);
					} else {
						errorData.add(new MRNErrorData((long) i, "The given client service Id entered is not correct"));
						return null;
					}
				}

			}

		} else {
			errorData.add(new MRNErrorData((long) i, "key and value mismatch combination"));
			return null;
		}
		return multipleJson;

	}

	public List<Map<String, String>> jsonSupportForServiceSTBs(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) throws ParseException {

		List<Map<String, String>> multipleJson = new ArrayList<Map<String, String>>();
		if (!currentLineData[0].isEmpty()) {
			ClientServiceData clientServiceData = this.clientReadPlatformService.retriveServiceId(currentLineData[1]);
			final HashMap<String, String> map = new HashMap<>();

			map.put("clientPoId", clientServiceData.getClientPoId());
			map.put("clientServicePoId", clientServiceData.getClientServicePoId());
			map.put("clientId", clientServiceData.getClientId().toString());
			map.put("id", clientServiceData.getId().toString());
			multipleJson.add(map);

		} else {
			errorData.add(new MRNErrorData((long) i, "key and value mismatch combination"));
			return null;
		}
		return multipleJson;
	}

	public CommandProcessingResult updateFile(DataUpload dataUpload, Long totalRecordCount, Long processRecordCount,
			ArrayList<MRNErrorData> errorData) {

		dataUpload.setProcessRecords(processRecordCount);
		dataUpload.setUnprocessedRecords(totalRecordCount - processRecordCount);
		dataUpload.setTotalRecords(totalRecordCount);
		writeCSVData(dataUpload.getUploadFilePath(), errorData, dataUpload);
		processRecordCount = 0L;
		totalRecordCount = 0L;
		this.dataUploadRepository.save(dataUpload);
		final String filelocation = dataUpload.getUploadFilePath();
		dataUpload = null;
		writeToFile(filelocation, errorData);
		return new CommandProcessingResult(Long.valueOf(1));
	}

	private void writeToFile(String uploadFilePath, ArrayList<MRNErrorData> errorData) {

		FileWriter fw = null;
		try {
			File f = new File(uploadFilePath.replace(".csv", ".log"));
			if (!f.exists()) {
				f.createNewFile();
			}
			fw = new FileWriter(f, true);
			for (int k = 0; k < errorData.size(); k++) {
				if (!errorData.get(k).getErrorMessage().equalsIgnoreCase("Success.")) {
					fw.append("Data at row: " + errorData.get(k).getRowNumber() + ", Message: "
							+ errorData.get(k).getErrorMessage() + "\n");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null) {
					fw.flush();
					fw.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void writeCSVData(String uploadFilePath, ArrayList<MRNErrorData> errorData, DataUpload uploadStatus) {

		try {

			long processRecords = uploadStatus.getProcessRecords();
			long totalRecords = uploadStatus.getTotalRecords();
			long unprocessRecords = totalRecords - processRecords;
			if (unprocessRecords == 0) {
				uploadStatus.setProcessStatus("Success");
				uploadStatus.setErrorMessage("Data successfully saved");
			} else if (unprocessRecords < totalRecords) {
				uploadStatus.setProcessStatus("Completed");
				uploadStatus.setErrorMessage("Completed with some errors");
			} else if (unprocessRecords == totalRecords) {
				uploadStatus.setProcessStatus("Failed");
				uploadStatus.setErrorMessage("Processing failed");
			}

			uploadStatus.setProcessDate(DateUtils.getDateOfTenant());
			this.dataUploadRepository.save(uploadStatus);
			uploadStatus = null;
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	public String buildjsonForPropertyDefinition(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 10) {
			final HashMap<String, String> map = new HashMap<>();
			map.put("propertyCode", currentLineData[0]);
			final Collection<MCodeData> propertyTypesList = this.mCodeReadPlatformService
					.getCodeValue(CodeNameConstants.CODE_PROPERTY_TYPE);
			if (!propertyTypesList.isEmpty()) {
				for (MCodeData mCodeData : propertyTypesList) {
					if (mCodeData.getmCodeValue().equalsIgnoreCase(currentLineData[1].toString().trim())) {
						map.put("propertyType", mCodeData.getId().toString());
						break;
					}
				}

				final Collection<PropertyDefinationData> unitCodesList = this.propertyReadPlatformService
						.retrievPropertyType(CodeNameConstants.CODE_PROPERTY_UNIT, currentLineData[2].trim());
				if (!unitCodesList.isEmpty()) {
					for (PropertyDefinationData unitData : unitCodesList) {
						if (unitData.getCode().equalsIgnoreCase(currentLineData[2].toString().trim())) {
							map.put("unitCode", currentLineData[2].trim());
							break;
						}
					}

					final Collection<PropertyDefinationData> floorList = this.propertyReadPlatformService
							.retrievPropertyType(CodeNameConstants.CODE_PROPERTY_FLOOR, currentLineData[3].trim());
					if (!floorList.isEmpty()) {
						for (PropertyDefinationData floorData : floorList) {
							if (floorData.getCode().equalsIgnoreCase(currentLineData[3].toString().trim())) {
								map.put("floor", currentLineData[3].trim());
								break;
							}
						}

						final Collection<PropertyDefinationData> buildingCodeList = this.propertyReadPlatformService
								.retrievPropertyType(CodeNameConstants.CODE_PROPERTY_BUILDING,
										currentLineData[4].trim());
						if (!buildingCodeList.isEmpty()) {
							for (PropertyDefinationData buildingCode : buildingCodeList) {
								if (buildingCode.getCode().equalsIgnoreCase(currentLineData[4].toString().trim())) {
									map.put("buildingCode", currentLineData[4].trim());
									break;
								}
							}

							final Collection<PropertyDefinationData> parcelList = this.propertyReadPlatformService
									.retrievPropertyType(CodeNameConstants.CODE_PROPERTY_PARCEL,
											currentLineData[5].trim());
							if (!buildingCodeList.isEmpty()) {
								for (PropertyDefinationData parcel : parcelList) {
									if (parcel.getCode().equalsIgnoreCase(currentLineData[5].toString().trim())) {
										map.put("parcel", currentLineData[5].trim());
										break;
									}
								}
								final List<CityDetailsData> cityDetailsList = this.addressReadPlatformService
										.retrieveAddressDetailsByCityName(currentLineData[6].trim());
								if (!cityDetailsList.isEmpty()) {
									for (CityDetailsData cityDetail : cityDetailsList) {
										if (cityDetail.getCityName()
												.equalsIgnoreCase(currentLineData[6].toString().trim())) {
											map.put("precinct", currentLineData[6].trim());
											break;
										}
									}
									map.put("poBox", currentLineData[7]);
									map.put("street", currentLineData[8]);
									map.put("state", currentLineData[9]);
									map.put("country", currentLineData[10]);
									return new Gson().toJson(map);
								} else {
									errorData.add(new MRNErrorData((long) i, "Precinct list is empty"));
									return null;
								}
							} else {
								errorData.add(new MRNErrorData((long) i, "Parcel list is empty"));
								return null;
							}
						} else {
							errorData.add(new MRNErrorData((long) i, "buildingCode list is empty"));
							return null;
						}
					} else {
						errorData.add(new MRNErrorData((long) i, "floor list is empty"));
						return null;
					}
				} else {
					errorData.add(new MRNErrorData((long) i, "unitCode list is empty"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "Property Types list is empty"));
				return null;
			}
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}

	public String buildjsonForPropertyCodeMaster(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 3) {
			final HashMap<String, String> map = new HashMap<>();
			final Collection<MCodeData> propertyCodeTypesList = this.mCodeReadPlatformService
					.getCodeValue(CodeNameConstants.PROPERTY_CODE_TYPE);
			if (!propertyCodeTypesList.isEmpty()) {
				for (MCodeData mCodeData : propertyCodeTypesList) {
					if (mCodeData.getmCodeValue().equalsIgnoreCase(currentLineData[0].toString())) {
						map.put("propertyCodeType", mCodeData.getmCodeValue());
						break;
					}
				}
				map.put("code", currentLineData[1]);
				map.put("description", currentLineData[2]);
				if (currentLineData.length == 4) {
					map.put("referenceValue", currentLineData[3]);
				}
				return new Gson().toJson(map);
			} else {
				errorData.add(new MRNErrorData((long) i, "Property Code Type list is empty"));
				return null;
			}
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;

		}
	}

	public String buildJsonForSimpleActivation(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {
		if (currentLineData.length >= 10) {
			try {
				JSONObject jsonObject = new JSONObject();
				final String clientData = this.prepareClientJsonForSimpleActivation(currentLineData, errorData, i);
				final String clientServiceData = this.prepareClientServiceJsonForSimpleActivation(currentLineData,
						errorData, i);
				final String deviceData = this.preparedeviceDataJsonForSimpleActivation(currentLineData, errorData, i);
				final JSONArray planData = this.prepareplanDataJsonForSimpleActivation(currentLineData, errorData, i);

				if (clientData != null && clientServiceData != null && deviceData != null && planData != null) {
					String clientDataString = "[" + clientData + "]";
					jsonObject.put("clientData", clientDataString);
					String clientServiceDataString = "[" + clientServiceData + "]";
					jsonObject.put("clientServiceData", clientServiceDataString);
					String deviceDataString = "[" + deviceData + "]";
					jsonObject.put("deviceData", deviceDataString);
					jsonObject.put("planData", String.valueOf(planData));

					return jsonObject.toString();
				} else {
					errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	private String prepareClientJsonForSimpleActivation(String[] currentLineData, ArrayList<MRNErrorData> errorData,
			int i) {
		try {
			JSONArray array = new JSONArray();
			JSONObject clientObject = new JSONObject();
			// adding required fields to clientJson Object
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			date = formatter.format(new Date());

			clientObject.put("activationDate", date);
			clientObject.put("entryType", "IND");

			// for office fun
			if (!currentLineData[0].isEmpty()) {
				Office office = this.officeRepository.findwithName(currentLineData[0]);
				if (office != null) {
					this.officeId = office.getId().toString();
					clientObject.put("officeId", this.officeId);
				} else {
					errorData.add(new MRNErrorData((long) i, "invalid office name"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "office not available"));
				return null;
			}

			// for client category fun
			CodeValue codeValue = this.codeValueRepository.findOneByCodeValue("Normal");
			if (codeValue != null) {
				clientObject.put("clientCategory", codeValue.getId().toString());
			}
			if (codeValue != null) {
				clientObject.put("clientCategory", codeValue.getId().toString());
			} else {
				errorData.add(new MRNErrorData((long) i, "clientCategory"));
				return null;
			}

			clientObject.put("billMode", "both");
			clientObject.put("firstname", currentLineData[1]);
			clientObject.put("lastname", ".");
			//clientObject.put("phone", currentLineData[2]);
			String mobile = currentLineData[2];
			if(mobile.length()>10||mobile.length()<10)
			{
				clientObject.put("phone", "1234567890");
				
			}else {
				clientObject.put("phone", currentLineData[2]);
			}
			Configuration configuration = this.configurationRepository
					.findOneByName(ConfigurationConstants.CONFIG_IS_SELFCAREUSER);
			if (configuration == null) {
				errorData.add(new MRNErrorData((long) i, "is selfcareuser configuration not found"));
			} else if (configuration.isEnabled()) {
				clientObject.put("email", currentLineData[3]);
			}
			JSONArray address = new JSONArray();
			address.put(String.valueOf(
					this.prepareAddressJsonForSimpleCustomerRegistration(currentLineData, errorData, i, "PRIMARY")));
			address.put(String.valueOf(
					this.prepareAddressJsonForSimpleCustomerRegistration(currentLineData, errorData, i, "BILLING")));
			clientObject.put("address", String.valueOf(address));

			clientObject.put("locale", "en");
			clientObject.put("active", "true");// hardcoded
			clientObject.put("dateFormat", dateFormat);
			clientObject.put("flag", "false");

			String returnValue = new Gson().toJson(clientObject);
			returnValue = returnValue.replace("\\", "");
			returnValue = returnValue.replace("\"{", "{");
			returnValue = returnValue.replace("}\"", "}");
			returnValue = returnValue.replace("\"[", "[");
			returnValue = returnValue.replace("]\"", "]");
			return returnValue;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	private JSONObject prepareAddressJsonForSimpleCustomerRegistration(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i, String addressType) {
		try {
			JSONObject addressObject = new JSONObject();
			// adding required fields to clientJson Object

			if (!currentLineData[4].isEmpty()) {
				final AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(currentLineData[4]);
				if (addressData != null) {
					addressObject.put("city", currentLineData[4]);
					addressObject.put("state", addressData.getState());
					addressObject.put("country", addressData.getCountry());
					addressObject.put("district", addressData.getDistrict());
					addressObject.put("addressType", addressType);
					addressObject.put("zipCode", addressData.getZip());
				} else {
					errorData.add(new MRNErrorData((long) i, "city is invalid"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "city is not vailable"));
				return null;
			}
			return addressObject;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}

	}

	private String prepareClientServiceJsonForSimpleActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) {
		try {
			JSONArray array = new JSONArray();
			JSONObject clientserviceObject = new JSONObject();

			ServiceData serviceData = this.serviceMasterReadPlatformService.retriveServiceParam(currentLineData[5]);
			clientserviceObject.put("serviceId", serviceData.getId());

			clientserviceObject.put("clientServiceDetails", String.valueOf(this
					.prepareclientServiceDetailsJsonForSimpleActivation(currentLineData, errorData, i, serviceData)));

			return clientserviceObject.toString();
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	private JSONArray prepareclientServiceDetailsJsonForSimpleActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i, ServiceData serviceData) throws ParseException {
		try {
			JSONArray array = new JSONArray();
			JSONObject clientServiceDetailsObject = new JSONObject();
			// adding required fields to clientserviceJson Object

			clientServiceDetailsObject.put("status", "new");
			clientServiceDetailsObject.put("parameterId", serviceData.getParamName()); // Hardcoded for NE
			//clientServiceDetailsObject.put("parameterId", 192); // Hardcoded for NE

		//	CodeData codeData = this.codeReadPlatformService.retrieveCodeIdOnCodeNameAndCodeValue("Provisioning",
					//currentLineData[6]);
			//clientServiceDetailsObject.put("parameterValue", codeData.getId()); // Hardcoded for ABV
			clientServiceDetailsObject.put("parameterValue", 1); // Hardcoded for ABV

			array.put(clientServiceDetailsObject);

			return array;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	private String preparedeviceDataJsonForSimpleActivation(String[] currentLineData, ArrayList<MRNErrorData> errorData,
			int i) {
		try {
			JSONArray array = new JSONArray();
			JSONObject deviceObject = new JSONObject();

			ItemData itemData = this.itemReadPlatformService.retrieveItemDetailsForItem(currentLineData[7]);
			if (Long.parseLong(this.officeId) != itemData.getOfficeId()) {
				errorData.add(new MRNErrorData((long) i, "Give Device of Same Organization"));
				return null;
			}
			// adding required fields to deviceJson Object
			deviceObject.put("locale", "en");
			deviceObject.put("dateFormat", dateFormat);

			deviceObject.put("officeId", itemData.getOfficeId());
			deviceObject.put("itemId", itemData.getId()); // Hardcoded for SC
			deviceObject.put("chargeCode", "OTC");
			deviceObject.put("unitPrice", itemData.getUnitPrice());
			deviceObject.put("quantity", String.valueOf(1));
			deviceObject.put("discountId", String.valueOf(1));
			deviceObject.put("totalPrice", itemData.getUnitPrice());
			deviceObject.put("saleType", "NEWSALE");
			deviceObject.put("saleDate", date);
			deviceObject.put("serialNumber", String.valueOf(
					this.prepareserialNumberJsonForSimpleActivation(currentLineData, errorData, i, true, itemData)));
			if (!currentLineData[8].isEmpty()) {
				deviceObject.put("isPairing", "Y");
				deviceObject.put("pairableItemDetails", String.valueOf(
						this.preparepairableItemDetailsJsonForSimpleActivation(currentLineData, errorData, i)));
			} else {
				deviceObject.put("isPairing", "N");
			}

			return deviceObject.toString();
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	private JSONArray prepareserialNumberJsonForSimpleActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i, boolean isStb, ItemData itemData) throws ParseException {
		try {
			JSONArray array = new JSONArray();
			JSONObject serialNumberObject = new JSONObject();
			// adding required fields to deviceJson Object
			if (isStb) {
				serialNumberObject.put("serialNumber", currentLineData[7]);
				serialNumberObject.put("status", "allocated");
				serialNumberObject.put("itemMasterId", itemData.getId());
				serialNumberObject.put("isNewHw", "Y");
				serialNumberObject.put("saleType", "NEWSALE");
				serialNumberObject.put("itemType", "STB");
			} else {
				serialNumberObject.put("serialNumber", currentLineData[8]);
				serialNumberObject.put("status", "allocated");
				serialNumberObject.put("itemMasterId", itemData.getId());
				serialNumberObject.put("isNewHw", "Y");
				serialNumberObject.put("saleType", "NEWSALE");
				serialNumberObject.put("itemType", "SC");
			}

			array.put(serialNumberObject);

			return array;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	private JSONObject preparepairableItemDetailsJsonForSimpleActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) throws ParseException {
		try {
			JSONObject ItemDetailsObject = new JSONObject();
			// adding required fields to deviceJson Object
			ItemData itemData = this.itemReadPlatformService.retrieveItemDetailsForItem(currentLineData[8]);
			if (Long.parseLong(this.officeId) != itemData.getOfficeId()) {
				errorData.add(new MRNErrorData((long) i, "Give Device of Same Organization"));
				return null;
			}
			ItemDetailsObject.put("locale", "en");
			ItemDetailsObject.put("dateFormat", dateFormat);
			ItemDetailsObject.put("officeId", officeId);
			ItemDetailsObject.put("itemId", itemData.getId());
			ItemDetailsObject.put("chargeCode", "OTC");
			ItemDetailsObject.put("unitPrice", itemData.getUnitPrice());
			ItemDetailsObject.put("quantity", String.valueOf(1));
			ItemDetailsObject.put("discountId", String.valueOf(1));
			ItemDetailsObject.put("totalPrice", itemData.getUnitPrice());
			ItemDetailsObject.put("saleType", "NEWSALE");
			ItemDetailsObject.put("saleDate", date);
			ItemDetailsObject.put("serialNumber", String.valueOf(
					this.prepareserialNumberJsonForSimpleActivation(currentLineData, errorData, i, false, itemData)));
			ItemDetailsObject.put("isPairing", "N");

			return ItemDetailsObject;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

//plan
	private JSONArray prepareplanDataJsonForSimpleActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) {
		try {
			Plan plan = this.planRepository.findwithName(currentLineData[9]);
			JSONArray array = new JSONArray();
			JSONObject planObject = new JSONObject();
			// adding required fields to planJson Object
			planObject.put("billAlign", "false");
			planObject.put("autoRenew", "");
			planObject.put("planCode", plan.getId());
			planObject.put("contractPeriod", 2);
			planObject.put("paytermCode", "1 Month");
			planObject.put("isNewplan", "true");
			planObject.put("locale", "en");
			planObject.put("dateFormat", dateFormat);
			planObject.put("start_date", date);

			array.put(planObject);
			return array;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	public String buildJsonForServiceActivation(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {
		if (currentLineData.length >= 12) {
			try {
				JSONObject jsonObject = new JSONObject();
				final JSONArray clientServiceData = this.prepareClientServiceJsonForServiceActivation(currentLineData,
						errorData, i);
				final JSONArray deviceData = this.preparedeviceDataJsonForServiceActivation(currentLineData, errorData,
						i);
				final JSONArray planData = this.prepareplanDataJsonForServiceActivation(currentLineData, errorData, i);

				if (clientServiceData != null && deviceData != null && planData != null) {
					jsonObject.put("clientServiceData", String.valueOf(clientServiceData));
					jsonObject.put("deviceData", String.valueOf(deviceData));
					jsonObject.put("planData", String.valueOf(planData));

					return jsonObject.toString();
				} else {
					errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	private JSONArray prepareClientServiceJsonForServiceActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) {
		try {
			JSONArray array = new JSONArray();
			JSONObject clientserviceObject = new JSONObject();
			clientserviceObject.put("clientId", currentLineData[0]);
			Client client = this.clientRepository.findOne(Long.parseLong(currentLineData[0]));
			this.officeId = client.getOffice().getId().toString();
			clientserviceObject.put("serviceId", currentLineData[1]);
			clientserviceObject.put("clientPoId", client.getPoid());
			clientserviceObject.put("accountNo", client.getAccountNo());
			clientserviceObject.put("clientServiceDetails", String
					.valueOf(this.prepareclientServiceDetailsJsonForServiceActivation(currentLineData, errorData, i)));

			array.put(clientserviceObject);
			return array;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	private JSONArray prepareclientServiceDetailsJsonForServiceActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) throws ParseException {
		try {
			JSONArray array = new JSONArray();
			JSONObject clientServiceDetailsObject = new JSONObject();
			// adding required fields to clientserviceJson Object
			clientServiceDetailsObject.put("status", "new");
			clientServiceDetailsObject.put("parameterId", currentLineData[2]); // Hardcoded for NE
			clientServiceDetailsObject.put("parameterValue", currentLineData[3]); // Hardcoded for ABV
			array.put(clientServiceDetailsObject);

			return array;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	private JSONArray preparedeviceDataJsonForServiceActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) {
		try {
			JSONArray array = new JSONArray();
			JSONObject deviceObject = new JSONObject();
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			date = formatter.format(new Date());
			// adding required fields to deviceJson Object
			deviceObject.put("locale", "en");
			deviceObject.put("dateFormat", dateFormat);

			deviceObject.put("officeId", this.officeId);
			deviceObject.put("itemId", currentLineData[5]); // Hardcoded for SC
			deviceObject.put("chargeCode", "OTC");
			deviceObject.put("unitPrice", currentLineData[6]);
			deviceObject.put("quantity", String.valueOf(1));
			deviceObject.put("discountId", String.valueOf(1));
			deviceObject.put("totalPrice", currentLineData[6]);
			deviceObject.put("saleType", currentLineData[11]);
			deviceObject.put("saleDate", date);
			deviceObject.put("serialNumber", String
					.valueOf(this.prepareserialNumberJsonForServiceActivation(currentLineData, errorData, i, true)));
			if (!currentLineData[7].isEmpty()) {
				deviceObject.put("isPairing", "Y");
				deviceObject.put("pairableItemDetails", String.valueOf(
						this.preparepairableItemDetailsJsonForServiceActivation(currentLineData, errorData, i)));
			} else {
				deviceObject.put("isPairing", "N");
			}

			array.put(deviceObject);

			return array;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	private JSONArray prepareserialNumberJsonForServiceActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i, boolean isStb) throws ParseException {
		try {
			JSONArray array = new JSONArray();
			JSONObject serialNumberObject = new JSONObject();
			// adding required fields to deviceJson Object
			if (isStb) {
				serialNumberObject.put("serialNumber", currentLineData[4]);
				serialNumberObject.put("status", "allocated");
				serialNumberObject.put("saleType", currentLineData[11]);
				serialNumberObject.put("itemMasterId", currentLineData[5]);
				serialNumberObject.put("isNewHw", "Y");
				serialNumberObject.put("itemType", "STB");
			} else {
				serialNumberObject.put("serialNumber", currentLineData[7]);
				serialNumberObject.put("status", "allocated");
				serialNumberObject.put("saleType", currentLineData[11]);
				serialNumberObject.put("itemMasterId", currentLineData[8]);
				serialNumberObject.put("isNewHw", "Y");
				serialNumberObject.put("itemType", "SC");
			}

			array.put(serialNumberObject);

			return array;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	public String buildJsonForcreateClient(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {
		if (currentLineData.length >= 11) {
			String returnValue = null;
			JSONArray address = new JSONArray();
			final HashMap<String, String> map = new HashMap<>();
			map.put("officeId", currentLineData[0]);
			if (!currentLineData[0].isEmpty()) {
				Office office = this.officeRepository.findwithName(currentLineData[0]);
				if (office != null) {
					officeId = office.getId().toString();
					map.put("officeId", officeId);
				} else {
					errorData.add(new MRNErrorData((long) i, "invalid office name"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "office not available"));
				return null;
			}
			map.put("firstname", currentLineData[1]);
			map.put("lastname", currentLineData[2]);
			map.put("phone", currentLineData[3]);
			map.put("middlename", currentLineData[4]);
			map.put("email", currentLineData[5]);
			map.put("externalId", currentLineData[8]);
			map.put("billMode", currentLineData[9]);
			map.put("flag", "false");
			map.put("locale", "en");
			map.put("entryType", "IND");
			map.put("clientCategory", "20");
			map.put("dateFormat", "dd MMMM yyyy");
			CodeValue codeValue = this.codeValueRepository.findOneByCodeValue(currentLineData[10]);
			if (codeValue != null) {
				map.put("idKey", codeValue.getId().toString());
			} else {
				errorData.add(new MRNErrorData((long) i, "Check The Identifications Proof And Enter"));
				return null;
			}
			if (!currentLineData[11].isEmpty()) {
				map.put("idValue", currentLineData[11]);
			} else {
				errorData.add(new MRNErrorData((long) i, "Please Enter The Identification Proof Value"));
				return null;
			}
			address.put(String
					.valueOf(this.prepareAddressJsonForCustomerRegistration(currentLineData, errorData, i, "PRIMARY")));
			address.put(String
					.valueOf(this.prepareAddressJsonForCustomerRegistration(currentLineData, errorData, i, "BILLING")));
			map.put("address", String.valueOf(address));
			Date today = new Date();
			SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
			map.put("activationDate", formatter1.format(today));
			returnValue = new Gson().toJson(map);
			returnValue = returnValue.replace("\\", "");
			returnValue = returnValue.replace("\"{", "{");
			returnValue = returnValue.replace("}\"", "}");
			returnValue = returnValue.replace("\"[", "[");
			returnValue = returnValue.replace("]\"", "]");
			return returnValue;
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}

	private JSONObject prepareAddressJsonForCustomerRegistration(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i, String addressType) {
		try {
			JSONObject addressObject = new JSONObject();
			// adding required fields to clientJson Object

			addressObject.put("addressNo", currentLineData[6]);

			if (!currentLineData[4].isEmpty()) {
				final AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(currentLineData[7]);
				if (addressData != null) {
					addressObject.put("city", currentLineData[7]);
					addressObject.put("state", addressData.getState());
					addressObject.put("country", addressData.getCountry());
					addressObject.put("district", addressData.getDistrict());
					addressObject.put("addressType", addressType);
					addressObject.put("zipCode", addressData.getZip());
				} else {
					errorData.add(new MRNErrorData((long) i, "city is invalid"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "city is not vailable"));
				return null;
			}
			return addressObject;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}

	}

	public String buildJsonForCreateProspects(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 13) {

			final HashMap<String, String> map = new HashMap<>();

			map.put("prospectType", "1");
			map.put("firstName", currentLineData[0]);
			map.put("lastName", currentLineData[1]);
			map.put("middleName", currentLineData[2]);
			map.put("mobileNumber", currentLineData[3]);
			/* map.put("address",currentLineData[4]); */
			/* map.put("city",currentLineData[5]); */

			map.put("address", currentLineData[4]);
			if (!currentLineData[4].isEmpty()) {
				final AddressData addressData = this.addressReadPlatformService.retrieveAdressBy(currentLineData[5]);
				if (addressData != null) {
					map.put("city", currentLineData[5]);
					map.put("state", addressData.getState());
					map.put("country", addressData.getCountry());
					map.put("district", addressData.getDistrict());
					map.put("zipCode", addressData.getZip());
				} else {
					errorData.add(new MRNErrorData((long) i, "city is invalid"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "city is not vailable"));
				return null;
			}

			map.put("email", currentLineData[6]);
			map.put("sourceOfPublicity", currentLineData[7]);
			map.put("note", currentLineData[8]);
			map.put("preferredPlan", currentLineData[9]);
			map.put("zipCode", currentLineData[10]);
			/* map.put("cityDistrict",currentLineData[10]); */
			map.put("preferredCallingTime", currentLineData[11]);
			map.put("locale", "en");

			if (!currentLineData[11].isEmpty()) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
				formatter.setLenient(false);
				try {
					Date date = formatter.parse(currentLineData[11]);
					SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
					map.put("preferredCallingTime", formatter1.format(date));
				} catch (ParseException e) {
					errorData.add(new MRNErrorData((long) i,
							"given date is in invalid format and that date format must be like 'dd/MM/YYYY HH:mm:ss ' "));
					return null;
				}
			}

			if (!currentLineData[12].isEmpty()) {
				String officeName = currentLineData[12];
				Office office = this.officeRepository.findwithName(officeName);
				map.put("officeId", office.getId().toString());
			} else {

			}
			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	public String buildJsonForServicePlanActivation(String[] currentLineData, ArrayList<MRNErrorData> errorData,
			int i) {
		if (currentLineData.length >= 13) {
			try {
				JSONObject jsonObject = new JSONObject();
				final JSONArray clientServiceData = this.prepareClientServiceJsonForServiceActivation(currentLineData,
						errorData, i);
				final JSONArray deviceData = this.preparedeviceDataJsonForServiceActivation(currentLineData, errorData,
						i);
				final JSONArray planData = this.prepareplanDataJsonForServicePlanActivation(currentLineData, errorData,
						i);

				if (clientServiceData != null && deviceData != null && planData != null) {
					jsonObject.put("clientServiceData", String.valueOf(clientServiceData));
					jsonObject.put("deviceData", String.valueOf(deviceData));
					jsonObject.put("planData", String.valueOf(planData));

					return jsonObject.toString();
				} else {
					errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	private JSONObject preparepairableItemDetailsJsonForServiceActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) throws ParseException {
		try {
			JSONObject ItemDetailsObject = new JSONObject();
			// adding required fields to deviceJson Object
			ItemDetailsObject.put("locale", "en");
			ItemDetailsObject.put("dateFormat", dateFormat);
			ItemDetailsObject.put("officeId", officeId);
			ItemDetailsObject.put("itemId", currentLineData[5]);
			ItemDetailsObject.put("chargeCode", "OTC");
			ItemDetailsObject.put("unitPrice", currentLineData[9]);
			ItemDetailsObject.put("quantity", String.valueOf(1));
			ItemDetailsObject.put("discountId", String.valueOf(1));
			ItemDetailsObject.put("totalPrice", currentLineData[9]);
			ItemDetailsObject.put("saleType", currentLineData[11]);
			ItemDetailsObject.put("saleDate", date);
			ItemDetailsObject.put("serialNumber", String
					.valueOf(this.prepareserialNumberJsonForServiceActivation(currentLineData, errorData, i, false)));
			ItemDetailsObject.put("isPairing", "N");

			return ItemDetailsObject;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}

	}

	// plan
	private JSONArray prepareplanDataJsonForServiceActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) {
		try {
			JSONArray array = new JSONArray();
			JSONObject planObject = new JSONObject();
			ClientBillInfoData clientbilldata = this.clientBillInfoReadPlatformService
					.retriveSingleClientBillInfo(Long.parseLong(currentLineData[0]));
			Plan paln = this.planRepository.findOne(Long.parseLong(currentLineData[10]));
			// adding required fields to planJson Object

			planObject.put("billAlign", "false");
			planObject.put("autoRenew", "");
			planObject.put("planCode", currentLineData[10]);
			planObject.put("planPoId", paln.getPlanPoid());
			if (paln.getIsPrepaid() == ConfigurationConstants.CONST_IS_Y) {
				planObject.put("contractPeriod", clientbilldata.getBillFrequency());
				planObject.put("paytermCode", clientbilldata.getBillFrequencyCode());
			} else {
				planObject.put("contractPeriod", "1");
				planObject.put("paytermCode", clientbilldata.getBillFrequencyCode());
			}
			planObject.put("isNewplan", "true");
			planObject.put("locale", "en");
			planObject.put("dateFormat", dateFormat);
			planObject.put("start_date", date);

			array.put(planObject);
			return array;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	// hardware plan
	private JSONArray prepareplanDataJsonForServicePlanActivation(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) {
		try {
			JSONArray array = new JSONArray();
			JSONObject planObject = new JSONObject();
			JSONObject planObject1 = new JSONObject();
			Plan paln = this.planRepository.findOne(Long.parseLong(currentLineData[10]));
			Plan paln1 = this.planRepository.findOne(Long.parseLong(currentLineData[12]));
			// adding required fields to planJson Object

			planObject.put("billAlign", "false");
			planObject.put("autoRenew", "");
			planObject.put("planCode", currentLineData[10]);
			planObject.put("planPoId", paln.getPlanPoid());
			planObject.put("contractPeriod", "2");
			planObject.put("paytermCode", "Monthly");
			planObject.put("isNewplan", "true");
			planObject.put("locale", "en");
			planObject.put("dateFormat", dateFormat);
			planObject.put("start_date", date);

			if (!currentLineData[12].isEmpty()) {
				planObject1.put("billAlign", "false");
				planObject1.put("autoRenew", "");
				planObject1.put("planCode", currentLineData[12]);
				planObject1.put("planPoId", paln1.getPlanPoid());
				planObject1.put("contractPeriod", "2");
				planObject1.put("paytermCode", "Monthly");
				planObject1.put("isNewplan", "true");
				planObject1.put("locale", "en");
				planObject1.put("dateFormat", dateFormat);
				planObject1.put("start_date", date);
			}

			array.put(planObject);
			array.put(planObject1);
			return array;
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	public List<Map<String, String>> buildJsonForTerminate(String[] currentLineData, ArrayList<MRNErrorData> errorData,
			int i) {
		/*
		 * try{ if(currentLineData.length>=2){ final HashMap<String, String> map = new
		 * HashMap<>(); map.put("clientId", currentLineData[1]); Client
		 * client=this.clientRepository.findOne(Long.parseLong(currentLineData[1]));
		 * map.put("clientPoId", client.getPoid());
		 * 
		 * ClientService clientService=
		 * this.clientServiceRepository.findOne(Long.parseLong(currentLineData[0]));
		 * map.put("clientServicePoId", clientService.getClientServicePoid());
		 * 
		 * return new Gson().toJson(map); }else{ errorData.add(new MRNErrorData((long)i,
		 * "Improper Data in this line")); return null; } }catch(Exception e){
		 * errorData.add(new MRNErrorData((long)i, e.getMessage())); return null; }
		 */
		try {
			if (currentLineData.length >= 2) {

				if (currentLineData[0].equalsIgnoreCase("Account No")) {
					return this.jsonSupportForServiceClientsn(currentLineData, "account_no", errorData, i);
				} else if (currentLineData[0].equalsIgnoreCase("Chip Id")) {
					return this.jsonSupportForServiceSTBes(currentLineData, errorData, i);
				} else if (currentLineData[0].equalsIgnoreCase("Email Id")) {
					return this.jsonSupportForServiceClientsn(currentLineData, "Email Id", errorData, i);
				} else if (currentLineData[0].equalsIgnoreCase("Legacy No")) {
					return this.jsonSupportForServiceClientsn(currentLineData, "Legacy No", errorData, i);
				} else if (currentLineData[0].equalsIgnoreCase("Serial No")) {
					return this.jsonSupportForServiceSTBes(currentLineData, errorData, i);
				} else {
					errorData.add(new MRNErrorData((long) i, "entered key value is not correct"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
				return null;
			}
		} catch (Exception e) {
			errorData.add(new MRNErrorData((long) i, e.getMessage()));
			return null;
		}
	}

	public List<Map<String, String>> jsonSupportForServiceClientsn(String[] currentLineData, String columnName,
			ArrayList<MRNErrorData> errorData, int i) throws ParseException {

		List<Map<String, String>> multipleJson = new ArrayList<Map<String, String>>();
		if (!currentLineData[0].isEmpty()) {
			final ClientData clientData = this.clientReadPlatformService.retrieveSearchClientId(columnName,
					currentLineData[1]);
			if (clientData != null) {
				if (currentLineData[2].equalsIgnoreCase("0")) {
					List<ClientService> clientServiceList = this.clientServiceRepository
							.findwithClientId1(clientData.getId());
					if (!clientServiceList.isEmpty()) {
						for (ClientService clientService : clientServiceList) {
							final HashMap<String, String> map = new HashMap<>();
							map.put("clientPoId", clientData.getPoid());
							map.put("clientServicePoId", clientService.getClientServicePoid());
							map.put("clientId", clientData.getId().toString());
							map.put("id", clientService.getId().toString());
							multipleJson.add(map);
						}
					} else {
						errorData.add(new MRNErrorData((long) i, "The given client has no service"));
						return null;
					}
				} else {
					ClientService clientService = this.clientServiceRepository
							.findOne(Long.parseLong(currentLineData[2]));
					if (clientService != null) {
						final HashMap<String, String> map = new HashMap<>();
						map.put("clientPoId", clientData.getPoid());
						map.put("clientServicePoId", clientService.getClientServicePoid());
						map.put("clientId", clientData.getId().toString());
						map.put("id", clientService.getId().toString());
						multipleJson.add(map);
					} else {
						errorData.add(new MRNErrorData((long) i, "The given client service Id entered is not correct"));
						return null;
					}
				}

			}

		} else {
			errorData.add(new MRNErrorData((long) i, "key and value mismatch combination"));
			return null;
		}

		return multipleJson;

	}

	public List<Map<String, String>> jsonSupportForServiceSTBes(String[] currentLineData,
			ArrayList<MRNErrorData> errorData, int i) throws ParseException {

		List<Map<String, String>> multipleJson = new ArrayList<Map<String, String>>();
		if (!currentLineData[0].isEmpty()) {
			ClientServiceData clientServiceData = this.clientReadPlatformService.retriveServiceId(currentLineData[1]);
			final HashMap<String, String> map = new HashMap<>();

			map.put("clientPoId", clientServiceData.getClientPoId());
			map.put("clientServicePoId", clientServiceData.getClientServicePoId());
			map.put("clientId", clientServiceData.getClientId().toString());
			map.put("id", clientServiceData.getId().toString());
			multipleJson.add(map);

		} else {
			errorData.add(new MRNErrorData((long) i, "key and value mismatch combination"));
			return null;
		}
		return multipleJson;

	}

	public String buildJsonForCustomerActivation(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 15) {

			final HashMap<String, String> map = new HashMap<>();
			// Office office = this.officeRepository.findwithCode(currentLineData[0]);
			/*
			 * if(office==null){ errorData.add(new MRNErrorData((long)i,
			 * "Error Code Not Entered Correctly")); return null; }else{
			 * map.put("officeId",office.getId().toString()); }
			 */
			if (!currentLineData[0].isEmpty()) {
				Office office = this.officeRepository.findwithCode(currentLineData[0]);
				if (office != null) {
					map.put("officeId", office.getId().toString());
				} else {
					errorData.add(new MRNErrorData((long) i, "invalid parent name"));

				}
			} else {
				errorData.add(new MRNErrorData((long) i, "parent name not available"));
			}

			if (currentLineData.length >= 11 && !currentLineData[10].isEmpty() && currentLineData[10] != null) {
				CodeValue codevalue = this.codeValueRepository.findOneByCodeValue(currentLineData[10]);
				if (codevalue != null) {
					map.put("idKey", codevalue.getId().toString());
				}

			} else {
				/*
				 * errorData.add(new MRNErrorData((long)i,
				 * "Check The Identifications Proof And Enter")); return null;
				 */

				CodeValue codevalue1 = this.codeValueRepository.findOneByCodeValue("Aadhar Card");
				map.put("idKey", codevalue1.getId().toString());
			}

			if (currentLineData.length >= 12 && currentLineData[11] != null && !currentLineData[11].isEmpty()) {
				map.put("idValue", currentLineData[11]);
			} else {
				map.put("idValue", currentLineData[7]);
			}

			map.put("firstname", currentLineData[1]);

			if (currentLineData[2] != null && !currentLineData[2].isEmpty()) {
				map.put("phone", currentLineData[2]);
			} else {
				map.put("phone", "1234567890");
			}

			if (currentLineData[3] != null && !currentLineData[3].isEmpty()) {
				map.put("email", currentLineData[3]);
			} else {
				map.put("email", "rcsb@gmail.com");
			}
			if (!currentLineData[4].isEmpty()) {
				map.put("city", currentLineData[4]);
			} else {
				errorData.add(new MRNErrorData((long) i, "enter the city"));

			}
			map.put("serviceCode", currentLineData[5]);
			map.put("provisioningSystem", currentLineData[6]);
			map.put("stb_serialNumber", currentLineData[7]);
			if (!currentLineData[8].isEmpty()) {
				map.put("pairable_serialNumber", currentLineData[8]);
			}
			if (!currentLineData[9].isEmpty()) {
				map.put("planCode", currentLineData[9]);
			} else {
				errorData.add(new MRNErrorData((long) i, "enter plancode"));

			}

			if (currentLineData.length >= 13 && currentLineData[12] != null && !currentLineData[12].isEmpty()) {
				map.put("addressNo", currentLineData[12]);
			}

			if (currentLineData.length >= 14 && currentLineData[13] != null && !currentLineData[13].isEmpty()) {
				map.put("street", currentLineData[13]);
			}
			if (currentLineData.length >= 15 && currentLineData[14] != null) {
				map.put("externalId", currentLineData[14]);
			}

			return new Gson().toJson(map);
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	public String buildjsonForUsers(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 8) {

			final HashMap<String, String> map = new HashMap<>();

			if (!currentLineData[6].isEmpty()) {
				Office office = this.officeRepository.findwithCode(currentLineData[6]);
				if (office != null) {
					map.put("officeId", office.getId().toString());
				} else {
					errorData.add(new MRNErrorData((long) i, "invalid office code"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "office code not available"));
				return null;
			}
			JSONArray roles = new JSONArray();
			map.put("username", currentLineData[0]);
			map.put("firstname", currentLineData[1]);
			map.put("lastname", currentLineData[2]);
			map.put("email", currentLineData[3]);
			map.put("password", currentLineData[4]);
			map.put("repeatPassword", currentLineData[5]);
			map.put("sendPasswordToEmail", "false");
			if (!currentLineData[7].isEmpty()) {
				org.mifosplatform.useradministration.domain.Role role = this.roleRepository
						.findOneByName(currentLineData[7]);
				if (role != null) {
					roles.put(role.getId());
					map.put("roles", roles.toString());
				} else {
					errorData.add(new MRNErrorData((long) i, "invalid role"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "role not available"));
				return null;
			}

			String json = new Gson().toJson(map);
			json = json.replace("\"[", "[");
			json = json.replace("]\"", "]");
			return json;
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	public String buildjsonForTransfers(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {

		if (currentLineData.length >= 2) {

			final HashMap<String, String> map = new HashMap<>();
			Office office = this.officeRepository.findwithCode(currentLineData[0]);
			if (office == null) {
				errorData.add(new MRNErrorData((long) i, "Error Code Not Entered Correctly"));
				return null;
			} else {
				map.put("officeId", office.getId().toString());
			}
			map.put("serialNo", currentLineData[1]);

			String json = new Gson().toJson(map);
			json = json.replace("\"[", "[");
			json = json.replace("]\"", "]");
			return json;
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}
	}

	public HashMap<String, String> buildJsonForRenewalPlan(String[] currentLineData, ArrayList<MRNErrorData> errorData,
			int i) {

		if (currentLineData.length >= 3) {
			final HashMap<String, String> map = new HashMap<>();
			ClientServiceData clientServiceData = this.clientReadPlatformService.retriveServiceId(currentLineData[0]);
			if (clientServiceData != null) {
				map.put("clientPoId", clientServiceData.getClientPoId());
				map.put("clientServicePoId", clientServiceData.getClientServicePoId());
			}
			OrderData orderData = this.orderReadPlatformService
					.retrieveAllPoidsRelatedToOrderNumbers((currentLineData[1]));
			if (orderData != null) {
				map.put("orderId", orderData.getOrderId().toString());
				map.put("oldPlanPoId", orderData.getPlanPoId());
				map.put("oldDealPoId", orderData.getDealPoId());
				map.put("priceId", orderData.getPriceId().toString());
			}
			// map.put("renewalPeriod",currentLineData[2]);
			if (!currentLineData[2].isEmpty()) {
				Contract contract = this.contractRepository.findwithName(currentLineData[2]);
				if (contract != null) {
					map.put("renewalPeriod", contract.getId().toString());
				} else {
					errorData.add(new MRNErrorData((long) i, "enter valid renewal data"));
					return null;
				}
			} else {
				errorData.add(new MRNErrorData((long) i, "enter valid renewal data"));
				return null;
			}
		//	map.put("disconnectReason", currentLineData[3]);
			map.put("locale", "en");
			map.put("dateFormat", "dd MMMM yyyy");
			Date today = new Date();
			SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
			map.put("disconnectionDate", formatter1.format(today));
			return map;
			/* return new Gson().toJson(map); */
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}

	public JSONObject buildJsonForEditCustomer(String[] currentLineData, ArrayList<MRNErrorData> errorData, int i) {
		//
		final HashMap<String, String> map = new HashMap<>();
		if (currentLineData.length <= 17) {
			if (!currentLineData[0].isEmpty()) {
				List<ClientData> clientDatas = this.clientReadPlatformService.retriveAccountNo(currentLineData[0]);
				for (ClientData clientData : clientDatas) {
					map.put("id", clientData.getId().toString());
					map.put("firstname", clientData.getFirstname());
					map.put("lastname", clientData.getLastname());
					map.put("email", clientData.getEmail());
					map.put("phone", clientData.getPhone());
					map.put("externalId", clientData.getExternalId());
					map.put("homePhoneNumber", clientData.getHomePhoneNumber());
					map.put("userName", clientData.getUserName());
					map.put("password", clientData.getClientPassword());
					// categorytype
					map.put("clientCategory", clientData.getCategoryType());
					map.put("title", clientData.getTitle());
					// idproof
					map.put("idKey", clientData.getIdKey());
					map.put("idValue", clientData.getIdValue());
					// officeid
					map.put("officeId", clientData.getOfficeId().toString());
					// address
					map.put("addressType", clientData.getAddressKey());
					map.put("addressNo", clientData.getAddressNo());
					map.put("street", clientData.getStreet());
					map.put("city", clientData.getCity());
					map.put("district", clientData.getDistrict());
					map.put("state", clientData.getState());
					map.put("country", clientData.getCountry());
					map.put("locale", "en");
					map.put("active", "true");
					map.put("dateFormat", "dd MMMM yyyy");
					Date today = new Date();
					SimpleDateFormat formatter1 = new SimpleDateFormat("dd MMMM yyyy");
					map.put("activationDate", formatter1.format(today));
					break;
				}

				int j = 1;
				switch (j) {
				case 1:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()
							&& !currentLineData[j].equalsIgnoreCase("")) {
						Office office = this.officeRepository.findwithName(currentLineData[j]);
						if (office != null) {
							map.put("officeId", office.getId().toString());
						} else {
							errorData.add(new MRNErrorData((long) i, "invalid parent name"));
							return null;

						}
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}
				case 2:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("title", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}
				case 3:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("firstname", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}
				case 4:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("lastname", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 5:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("email", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 6:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("phone", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 7:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("externalId", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 8:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("homePhoneNumber", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 9:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("password", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 10:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						CodeValue codeValue = this.codeValueRepository.findOneByCodeValue(currentLineData[j]);
						// map.put("clientCategory", currentLineData[j]);
						if (codeValue != null) {
							map.put("clientCategory", codeValue.getId().toString());
						} else {
							errorData.add(new MRNErrorData((long) i, "clientCategory"));
							return null;
						}
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 11:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						CodeValue codeValue = this.codeValueRepository.findOneByCodeValue(currentLineData[j]);
						if (codeValue != null) {
							map.put("idKey", codeValue.getId().toString());
						} else {
							errorData.add(new MRNErrorData((long) i, "Check The Identifications Proof And Enter"));
							return null;
						}
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 12:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()
							&& !currentLineData[j - 1].isEmpty()) {
						map.put("idValue", currentLineData[j]);
					} else {
						errorData.add(new MRNErrorData((long) i, "Please Enter The Identification Proof Value"));
						return null;
					}
					j++;
					int l = currentLineData.length;
					if (currentLineData.length == j) {
						break;
					}
					j++;

				case 13:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("addressType", currentLineData[13]);
						map.put("addressNo", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 14:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						map.put("addressType", currentLineData[13]);
						map.put("street", currentLineData[j]);
					}
					j++;
					if (currentLineData.length == j) {
						break;
					}

				case 15:
					if (currentLineData[j] != null && !currentLineData[j].isEmpty()) {
						final AddressData addressData = this.addressReadPlatformService
								.retrieveAdressBy(currentLineData[j]);
						if (addressData != null) {
							map.put("addressType", currentLineData[13]);
							map.put("city", currentLineData[j]);
							map.put("district", addressData.getDistrict());
							map.put("state", addressData.getState());
							map.put("country", addressData.getCountry());

						} else {
							errorData.add(new MRNErrorData((long) i, "city is invalid"));
							return null;
						}
					}

				default:
					errorData.add(new MRNErrorData((long) i, "plz enter proper data"));
					break;

				}

			}
			JSONObject object = new JSONObject();
			object.putAll(map);
			return object;
		} else {
			errorData.add(new MRNErrorData((long) i, "Improper Data in this line"));
			return null;
		}

	}
}
