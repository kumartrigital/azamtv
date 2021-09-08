package org.mifosplatform.scheduledjobs.dataupload.service;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.*;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.finance.adjustment.exception.AdjustmentCodeNotFoundException;
import org.mifosplatform.finance.payments.exception.PaymentCodeNotFoundException;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.exception.UnsupportedParameterException;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.logistics.itemdetails.data.ItemDetailsData;
import org.mifosplatform.logistics.itemdetails.exception.OrderQuantityExceedsException;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberAlreadyExistException;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberNotFoundException;
import org.mifosplatform.logistics.itemdetails.service.ItemDetailsReadPlatformService;
import org.mifosplatform.logistics.mrn.exception.InvalidMrnIdException;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.order.api.OrdersApiResource;
import org.mifosplatform.portfolio.order.exceptions.NoGrnIdFoundException;
import org.mifosplatform.scheduledjobs.dataupload.command.DataUploadCommand;
import org.mifosplatform.scheduledjobs.dataupload.data.MRNErrorData;
import org.mifosplatform.scheduledjobs.dataupload.domain.DataUpload;
import org.mifosplatform.scheduledjobs.dataupload.domain.DataUploadCommandValidator;
import org.mifosplatform.scheduledjobs.dataupload.domain.DataUploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.*;

@Service
public class DataUploadWritePlatformServiceImp implements DataUploadWritePlatformService {

	private final PlatformSecurityContext context;
	private final DataUploadRepository uploadStatusRepository;
	private final DataUploadHelper dataUploadHelper;
	final private static String MEDIAASSETS_RESOURCE_TYPE = "ASSESTS";
	final private static String EPG_RESOURCE_TYPE = "EPGPROGRAMGUIDE";
	final private static String MRN_RESOURCE_TYPE = "MRNDETAILS";
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	private final OrdersApiResource ordersApiResource;
	private final ItemDetailsReadPlatformService itemDetailsReadPlatformService;

	@Autowired
	public DataUploadWritePlatformServiceImp(final PlatformSecurityContext context,
			final DataUploadRepository uploadStatusRepository,
			final DefaultToApiJsonSerializer<ItemDetailsData> toApiJsonSerializer,
			final DataUploadHelper dataUploadHelper,
			final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
			final OrdersApiResource ordersApiResource,
			final ItemDetailsReadPlatformService itemDetailsReadPlatformService) {

		this.context = context;
		this.dataUploadHelper = dataUploadHelper;
		this.uploadStatusRepository = uploadStatusRepository;
		this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		this.ordersApiResource = ordersApiResource;
		this.itemDetailsReadPlatformService = itemDetailsReadPlatformService;
	}

	@SuppressWarnings("unused")
	@Override
	public CommandProcessingResult processDatauploadFile(final Long fileId) {

		this.context.authenticatedUser();
		// BufferedReader csvFileBufferedReader = null;
		String line = null;
		Long processRecordCount = 0L;
		Long totalRecordCount = 0L;
		try {
			final DataUpload uploadStatus = this.uploadStatusRepository.findOne(fileId);
			final String uploadProcess = uploadStatus.getUploadProcess();
			String jsonString = null;
			final String fileLocation = uploadStatus.getUploadFilePath();
			ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
			final String splitLineRegX = ",";
			int i = 1;

			uploadStatus.setProcessStatus("Running...");
			this.uploadStatusRepository.save(uploadStatus);
			BufferedReader csvFileBufferedReader = new BufferedReader(new FileReader(fileLocation));
			line = csvFileBufferedReader.readLine();

			if (uploadProcess.equalsIgnoreCase("Hardware Items") && new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}

						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);

						if (currentLineData.length >= 9) {
							jsonString = this.dataUploadHelper.buildJsonForHardwareItems(currentLineData, errorData, i);
						} else {
							jsonString = this.dataUploadHelper.buildJsonForHardwareItemsnotpaired(currentLineData,
									errorData, i);
						}
						jsonString = jsonString.replace("\\", "");
						jsonString = jsonString.replace("\"[", "[");
						jsonString = jsonString.replace("]\"", "]");
						jsonString = jsonString.replace("\"{", "{");
						jsonString = jsonString.replace("}\"", "}");
						JSONObject json = null;
						CommandProcessingResult result = null;
						JSONArray jsonArray = new JSONArray(jsonString);
						for (int j = 0; j < jsonArray.length(); j++) {
							json = jsonArray.getJSONObject(j);
							if (json != null) {
								final CommandWrapper commandRequest = new CommandWrapperBuilder()
										.createInventoryItem(null).withJson(json.toString()).build();
								result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

							}

						}
						if (result != null) {
							processRecordCount++;
							errorData.add(new MRNErrorData((long) i, "Success."));
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;

				}

				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("Mrn") && new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForMrn(currentLineData, errorData, i);
						if (jsonString != null) {
							context.authenticatedUser().validateHasReadPermission(MRN_RESOURCE_TYPE);
							final CommandWrapper commandRequest = new CommandWrapperBuilder().moveMRN()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						// errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("MoveItemsale")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}

						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForMoveItems(currentLineData, errorData, i);
						if (jsonString != null) {
							context.authenticatedUser().validateHasReadPermission(MRN_RESOURCE_TYPE);
							final CommandWrapper commandRequest = new CommandWrapperBuilder().moveItemSale()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("Epg") && new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForEpg(currentLineData, errorData, i);
						if (jsonString != null) {
							context.authenticatedUser().validateHasReadPermission(EPG_RESOURCE_TYPE);
							final CommandWrapper commandRequest = new CommandWrapperBuilder().createEpgXsls(0L)
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}

			} else if (uploadProcess.equalsIgnoreCase("Adjustments")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForAdjustment(currentLineData, errorData, i);

						if (jsonString != null) {
							final CommandWrapper commandRequest = new CommandWrapperBuilder()
									.createAdjustment(Long.valueOf(currentLineData[0])).withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("Payments")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildjsonForPayments(currentLineData, errorData, i);
						if (jsonString != null) {
							final CommandWrapper commandRequest = new CommandWrapperBuilder()
									.createPayment(Long.valueOf(currentLineData[0])).withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}

			} else if (uploadProcess.equalsIgnoreCase("Cancel Payments")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildjsonForPaymentscancel(currentLineData, errorData, i);
						if (jsonString != null) {
							final CommandWrapper commandRequest = new CommandWrapperBuilder()
									.createPayment(Long.valueOf(currentLineData[0])).withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}

			}else if (uploadProcess.equalsIgnoreCase("MediaAssets")) {
				DataUpload uploadStatusForMediaAsset = this.uploadStatusRepository.findOne(fileId);
				Workbook wb = null;
				processRecordCount = 0L;
				totalRecordCount = 0L;
				try {
					wb = WorkbookFactory.create(new File(fileLocation));
					final Sheet mediaSheet = wb.getSheetAt(0);
					Sheet mediaAttributeSheet = wb.getSheetAt(1);
					Sheet mediaLocationSheet = wb.getSheetAt(2);
					final int msNumberOfRows = mediaSheet.getPhysicalNumberOfRows();
					System.out.println("Number of rows : " + msNumberOfRows);
					for (i = 1; i < msNumberOfRows; i++) {
						Row mediaRow = mediaSheet.getRow(i);
						final Row mediaAttributeRow = mediaAttributeSheet.getRow(i);
						Row mediaLocationRow = mediaLocationSheet.getRow(i);
						try {
							if (mediaRow.getCell(0).getStringCellValue().equalsIgnoreCase("EOF")) {
								this.dataUploadHelper.updateFile(uploadStatusForMediaAsset, totalRecordCount,
										processRecordCount, errorData);
							}
							jsonString = this.dataUploadHelper.buildForMediaAsset(mediaRow, mediaAttributeRow,
									mediaLocationRow);

							context.authenticatedUser().validateHasReadPermission(MEDIAASSETS_RESOURCE_TYPE);
							final CommandWrapper commandRequest = new CommandWrapperBuilder().createMediaAsset()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								// Long rsId = result.resourceId();
								errorData.add(new MRNErrorData((long) i, "Success"));
								processRecordCount++;
							}
						} catch (Exception e) {
							handleDataIntegrityIssues(i, errorData, e);
						}
					}
					totalRecordCount++;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidFormatException e) {
					e.getStackTrace();
				}
			} else if (uploadProcess.equalsIgnoreCase("ItemSale")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);

						jsonString = this.dataUploadHelper.buildforitemSale(currentLineData, errorData, i);
						if (jsonString != null) {
							final CommandWrapper commandRequest = new CommandWrapperBuilder().createItemSale()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					i++;
					totalRecordCount++;
				}
				// writeToFile(fileLocation,errorData);
			} else if (uploadProcess.equalsIgnoreCase("Property Data")
					&& new File(fileLocation).getName().contains(".csv")) {
				while ((line = csvFileBufferedReader.readLine()) != null) {
					try {
						line = line.replace(";", " ");
						final String[] currentLineData = line.split(splitLineRegX);
						if (currentLineData != null && currentLineData[0].equalsIgnoreCase("EOF")) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						jsonString = this.dataUploadHelper.buildjsonForPropertyDefinition(currentLineData, errorData,
								i);

						if (jsonString != null) {

							final CommandWrapper commandRequest = new CommandWrapperBuilder().createProperty()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("Property Master")
					&& new File(fileLocation).getName().contains(".csv")) {
				while ((line = csvFileBufferedReader.readLine()) != null) {
					try {
						line = line.replace(";", " ");
						final String[] currentLineData = line.split(splitLineRegX);
						if (currentLineData != null && currentLineData[0].equalsIgnoreCase("EOF")) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						jsonString = this.dataUploadHelper.buildjsonForPropertyCodeMaster(currentLineData, errorData,
								i);

						if (jsonString != null) {

							final CommandWrapper commandRequest = new CommandWrapperBuilder().createPropertyMaster()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("Organization")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForOffice(currentLineData, errorData, i);
						jsonString = jsonString.replace("\\", "");
						jsonString = jsonString.replace("\"{", "{");
						jsonString = jsonString.replace("}\"", "}");
						jsonString = jsonString.replace("\"[", "[");
						jsonString = jsonString.replace("]\"", "]");

						if (jsonString != null) {

							final CommandWrapper commandRequest = new CommandWrapperBuilder().createOffice()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);
				// add plan
			} else if (uploadProcess.equalsIgnoreCase("Add Plan")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = null;
						final HashMap<String, String> map = new HashMap<>();
						net.sf.json.JSONObject jsonObject = this.dataUploadHelper.buildJsonForAddPlan(currentLineData,
								errorData, i);
						CommandProcessingResult result = null;
						if (!jsonObject.isEmpty() && jsonObject != null) {
							JSONArray plans = new JSONArray();
							plans.put(jsonObject);
							map.put("plans", plans.toString());
							String clientId = (String) jsonObject.get("clientId");

							jsonString = new Gson().toJson(map);
							jsonString = jsonString.replace("\\", "");
							jsonString = jsonString.replace("\"{", "{");
							jsonString = jsonString.replace("}\"", "}");
							jsonString = jsonString.replace("\"[", "[");
							jsonString = jsonString.replace("]\"", "]");

							final CommandWrapper commandRequest = new CommandWrapperBuilder()
									.createMultipleOrder(Long.parseLong(clientId)).withJson(jsonString).build();
							result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
						}
						if (result != null) {
							processRecordCount++;
							errorData.add(new MRNErrorData((long) i, "Success."));

						}
					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);
//change plan
			} else if (uploadProcess.equalsIgnoreCase("Change Plan")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForChangePlan(currentLineData, errorData, i);

						if (jsonString != null) {

							final CommandWrapper commandRequest = new CommandWrapperBuilder()
									.changePlan(Long.valueOf(currentLineData[1])).withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);
//cancel plan		   
			} else if (uploadProcess.equalsIgnoreCase("CancelPlan")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForCancelPlan(currentLineData, errorData, i);

						if (jsonString != null) {

							String result = this.ordersApiResource.updateOrder(Long.parseLong(currentLineData[4]),
									jsonString);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);
			} else if (uploadProcess.equalsIgnoreCase("ClientService Suspend")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						/*
						 * while((line = csvFileBufferedReader.readLine()) != null){ try{
						 */
						line = line.replace(";", " ");
						final String[] currentLineData = line.split(splitLineRegX);
						/*
						 * if(currentLineData!=null && currentLineData[0].equalsIgnoreCase("EOF")){
						 * return this.dataUploadHelper.updateFile(uploadStatus,totalRecordCount,
						 * processRecordCount,errorData); }
						 */
						jsonString = null;
						List<Map<String, String>> multipleJson = this.dataUploadHelper
								.buildJsonForSuspend(currentLineData, errorData, i);
						if (!multipleJson.isEmpty() && multipleJson != null) {
							for (Map<String, String> map : multipleJson) {
								String id = map.get("id");
								jsonString = new Gson().toJson(map);
								final CommandWrapper commandRequest = new CommandWrapperBuilder()
										.suspendClientService(Long.valueOf(id)).withJson(jsonString).build();
								CommandProcessingResult result = this.commandsSourceWritePlatformService
										.logCommandSource(commandRequest);
								if (result != null) {
									errorData.add(new MRNErrorData((long) i, "Success."));
									processRecordCount++;
								}
							}
							errorData.add(new MRNErrorData((long) i, "Success."));
							processRecordCount++;
						}
					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);
			} else if (uploadProcess.equalsIgnoreCase("ClientService reactive")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}

						/*
						 * while((line = csvFileBufferedReader.readLine()) != null){ try{
						 */
						line = line.replace(";", " ");
						final String[] currentLineData = line.split(splitLineRegX);
						/*
						 * if(currentLineData!=null && currentLineData[0].equalsIgnoreCase("EOF")){
						 * return this.dataUploadHelper.updateFile(uploadStatus,totalRecordCount,
						 * processRecordCount,errorData); }
						 */
						jsonString = null;
						List<Map<String, String>> multipleJson = this.dataUploadHelper
								.buildJsonForreactive(currentLineData, errorData, i);
						if (!multipleJson.isEmpty() && multipleJson != null) {
							for (Map<String, String> map : multipleJson) {
								String id = map.get("id");
								jsonString = new Gson().toJson(map);
								final CommandWrapper commandRequest = new CommandWrapperBuilder()
										.reactiveClientService(Long.valueOf(id)).withJson(jsonString).build();
								final CommandProcessingResult result = this.commandsSourceWritePlatformService
										.logCommandSource(commandRequest);
								if (result != null) {
									errorData.add(new MRNErrorData((long) i, "Success."));
									processRecordCount++;
								}
							}

						}
					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
			} else if (uploadProcess.equalsIgnoreCase("ClientService Termination")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						/*
						 * while((line = csvFileBufferedReader.readLine()) != null){ try{
						 */
						line = line.replace(";", " ");
						final String[] currentLineData = line.split(splitLineRegX);
						/*
						 * if(currentLineData!=null && currentLineData[0].equalsIgnoreCase("EOF")){
						 * return this.dataUploadHelper.updateFile(uploadStatus,totalRecordCount,
						 * processRecordCount,errorData); }
						 */
						jsonString = null;
						List<Map<String, String>> multipleJson = this.dataUploadHelper
								.buildJsonForTerminate(currentLineData, errorData, i);
						if (!multipleJson.isEmpty() && multipleJson != null) {
							for (Map<String, String> map : multipleJson) {
								String id = map.get("id");
								jsonString = new Gson().toJson(map);
								final CommandWrapper commandRequest = new CommandWrapperBuilder()
										.terminateClientService(Long.valueOf(id)).withJson(jsonString).build();
								final CommandProcessingResult result = this.commandsSourceWritePlatformService
										.logCommandSource(commandRequest);

								if (result != null) {
									errorData.add(new MRNErrorData((long) i, "Success."));
									processRecordCount++;
								}
							}

						}
					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);
				// writeToFile(fileLocation,errorData);

				/*
				 * }else if(uploadProcess.equalsIgnoreCase("Office") && new
				 * File(fileLocation).getName().contains(".csv")){ LinkedList<Object> recordlist
				 * = new LinkedList<>(); //int totalRecordCount = 0; //int processRecordCount1 =
				 * 0; Long processRecordCount1=0L; Long totalRecordCount1=0L; Thread t1 = new
				 * Thread(new Runnable(){
				 * 
				 * @Override public void run() { try { BufferedReader csvFileBufferedReader =
				 * new BufferedReader(new FileReader(fileLocation));
				 * produceOfficeRecords(recordlist,csvFileBufferedReader,totalRecordCount1,
				 * errorData); } catch(InterruptedException | IOException e) {
				 * e.printStackTrace(); } } }); Thread t2 = new Thread(new Runnable() {
				 * 
				 * @Override public void run() { try {
				 * consumeOfficeRecords(recordlist,processRecordCount1,errorData); }
				 * catch(InterruptedException e) { e.printStackTrace(); } } }); // Start both
				 * threads t1.start(); t2.start();
				 * 
				 * // t1 finishes before t2 t1.join(); t2.join();
				 * 
				 * return this.dataUploadHelper.updateFile(uploadStatus,totalRecordCount,
				 * processRecordCount,errorData);
				 */
			} else if (uploadProcess.equalsIgnoreCase("Simple Activation")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForSimpleActivation(currentLineData, errorData, i);

						if (jsonString != null) {

							System.out
									.println("DataUploadWritePlatformServiceImp.processDatauploadFile()" + jsonString);
							final CommandWrapper commandRequest = new CommandWrapperBuilder().createSimpleActivation()
									.withJson(jsonString).build();

							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("Service Activation")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);

						jsonString = this.dataUploadHelper.buildJsonForServiceActivation(currentLineData, errorData, i);

						if (jsonString != null) {

							final CommandWrapper commandRequest = new CommandWrapperBuilder()
									.createClientSimpleActivation(Long.valueOf(currentLineData[0])).withJson(jsonString)
									.build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			}

			else if (uploadProcess.equalsIgnoreCase("MrnCartoon")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);

						jsonString = this.dataUploadHelper.buildJsonForMrnCartoon(currentLineData, errorData, i);
						if (jsonString != null) {
							context.authenticatedUser().validateHasReadPermission(MRN_RESOURCE_TYPE);
							final CommandWrapper commandRequest = new CommandWrapperBuilder().movemrncarton()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						// errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			}

			else if (uploadProcess.equalsIgnoreCase("CustomerRegistration")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);

						jsonString = this.dataUploadHelper.buildJsonForcreateClient(currentLineData, errorData, i);
						if (jsonString != null) {
							final CommandWrapper commandRequest = new CommandWrapperBuilder().createClient()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						// errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			}

			else if (uploadProcess.equalsIgnoreCase("Create Prospects")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);

						jsonString = this.dataUploadHelper.buildJsonForCreateProspects(currentLineData, errorData, i);
						if (jsonString != null) {
							final CommandWrapper commandRequest = new CommandWrapperBuilder().createProspect()
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						// errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}

			} else if (uploadProcess.equalsIgnoreCase("Service Plan Activation")
					&& new File(fileLocation).getName().contains(".csv")) {
				while ((line = csvFileBufferedReader.readLine()) != null) {
					try {
						line = line.replace(";", " ");
						final String[] currentLineData = line.split(splitLineRegX);
						if (currentLineData != null && currentLineData[0].equalsIgnoreCase("EOF")) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}

						jsonString = this.dataUploadHelper.buildJsonForServicePlanActivation(currentLineData, errorData,
								i);

						if (jsonString != null) {

							final CommandWrapper commandRequest = new CommandWrapperBuilder()
									.createClientHardwarePlanActivation(Long.valueOf(currentLineData[0]))
									.withJson(jsonString).build();
							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("Customer Activation")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForCustomerActivation(currentLineData, errorData,
								i);

						if (jsonString != null) {

							final CommandWrapper commandRequest = new CommandWrapperBuilder() //
									.createCustomerActivation() //
									.withJson(jsonString) //
									.build(); //

							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("Create Users")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);

						jsonString = this.dataUploadHelper.buildjsonForUsers(currentLineData, errorData, i);

						if (jsonString != null) {

							final CommandWrapper commandRequest = new CommandWrapperBuilder() //
									.createUser() //
									.withJson(jsonString) //
									.build();

							final CommandProcessingResult result = this.commandsSourceWritePlatformService
									.logCommandSource(commandRequest);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);
			} else if (uploadProcess.equalsIgnoreCase("Cancel Plan")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = this.dataUploadHelper.buildJsonForCancelPlan(currentLineData, errorData, i);

						if (jsonString != null) {

							String result = this.ordersApiResource.updateOrder(Long.parseLong(currentLineData[3]),
									jsonString);
							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}

			} else if (uploadProcess.equalsIgnoreCase("RenewalPlan")
					&& new File(fileLocation).getName().contains(".csv")) {

				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = null;

						HashMap<String, String> map = this.dataUploadHelper.buildJsonForRenewalPlan(currentLineData,
								errorData, i);
						CommandProcessingResult result = null;
						if (!map.isEmpty()) {
							String orderId = (String) map.get("orderId");
							jsonString = new Gson().toJson(map);
							final CommandWrapper commandRequest = new CommandWrapperBuilder()
									.renewalOrder(Long.parseLong(orderId)).withJson(jsonString).build();
							result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

						}
						if (result != null) {
							processRecordCount++;
							errorData.add(new MRNErrorData((long) i, "Success."));

						}
					} catch (Exception e) {

						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}

			} else if (uploadProcess.equalsIgnoreCase("Edit Customer")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}

						line = line.replace(";", "");

						final String[] currentLineData = line.split(splitLineRegX);
						jsonString = null;
						final HashMap<String, String> map = new HashMap<>();
						net.sf.json.JSONObject jsonObject = this.dataUploadHelper
								.buildJsonForEditCustomer(currentLineData, errorData, i);
						CommandProcessingResult result = null;
						if (!jsonObject.isEmpty() && jsonObject != null) {

							// jsonString = new Gson().toJson(map);
							String clientId = (String) jsonObject.get("id");

							final CommandWrapper commandRequest = new CommandWrapperBuilder()
									.updateClient(Long.parseLong(clientId)).withJson(jsonObject.toString()).build();
							result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

						}
						if (result != null) {
							processRecordCount++;
							errorData.add(new MRNErrorData((long) i, "Success."));

						}
					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}

				/*
				 * final String[] currentLineData = line.split(splitLineRegX);
				 * 
				 * jsonString=this.dataUploadHelper.buildJsonForEditCustomer(currentLineData,
				 * errorData,i);
				 * 
				 * if(jsonString != null){ final CommandWrapper commandRequest = new
				 * CommandWrapperBuilder().updateClient(clientId) .withJson(jsonString).build();
				 * final CommandProcessingResult result =
				 * this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
				 * if(result!=null){ processRecordCount++; errorData.add(new
				 * MRNErrorData((long)i, "Success.")); } }
				 * 
				 * }catch (Exception e) { handleDataIntegrityIssues(i,errorData,e); }
				 * totalRecordCount++; i++;
				 * 
				 * }
				 */

				// writeToFile(fileLocation,errorData);

			} else if (uploadProcess.equalsIgnoreCase("Transfer")
					&& new File(fileLocation).getName().contains(".csv")) {
				while (true) {
					try {
						line = csvFileBufferedReader.readLine();
						if (line == null) {
							return this.dataUploadHelper.updateFile(uploadStatus, totalRecordCount, processRecordCount,
									errorData);
						}
						line = line.replace(";", "");
						final String[] currentLineData = line.split(splitLineRegX);

						jsonString = this.dataUploadHelper.buildjsonForTransfers(currentLineData, errorData, i);

						if (jsonString != null) {
							Integer result = this.itemDetailsReadPlatformService.updateItemDetail(jsonString);

							if (result != null) {
								processRecordCount++;
								errorData.add(new MRNErrorData((long) i, "Success."));
							}
						}

					} catch (Exception e) {
						handleDataIntegrityIssues(i, errorData, e);
					}
					totalRecordCount++;
					i++;
				}
				// writeToFile(fileLocation,errorData);

			}

			return new CommandProcessingResult(Long.valueOf(1));
		} catch (FileNotFoundException e) {
			throw new PlatformDataIntegrityException("file.not.found", "file.not.found", "file.not.found",
					"file.not.found");
		} catch (Exception e) {
			// errorData.add(new MRNErrorData((long)i, "Error:
			// "+e.getCause().getLocalizedMessage()));
			// return new CommandProcessingResult(Long.valueOf(-1));
		}
		/*
		 * finally{
		 * 
		 * if(this.csvFileBufferedReader!=null){ try{ csvFileBufferedReader.close();
		 * }catch(Exception e){ e.printStackTrace();
		 * 
		 * } }
		 * 
		 * }
		 */

		return new CommandProcessingResult(Long.valueOf(1));
	}
	// consume method
	/*
	 * protected void consumeOfficeRecords(LinkedList<Object> recordlist, Long
	 * processRecordCount, ArrayList<MRNErrorData> errorData) throws
	 * InterruptedException{ // TODO Auto-generated method stub
	 * //ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
	 * 
	 * String jsonString; while (true) { try{ synchronized (recordlist) { //
	 * consumer thread waits while list // is empty while (recordlist.size()==0)
	 * wait();
	 * 
	 * //to retrive the ifrst job in the list jsonString = (String)
	 * recordlist.getFirst();
	 * 
	 * if(jsonString !=null && jsonString.toString().equals("EOF")) {
	 * 
	 * //Long totalRecordCount = (long) 0;
	 * //this.dataUploadHelper.updateFile(uploadStatus,totalRecordCount,
	 * processRecordCount,errorData); break; } else { recordlist.removeFirst(); } }
	 * if(jsonString !=null){ JSONObject jsonObj = new JSONObject(jsonString); final
	 * CommandWrapper commandRequest = new
	 * CommandWrapperBuilder().createOffice().withJson(jsonString).build(); final
	 * CommandProcessingResult result =
	 * this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
	 * if(result!=null){ synchronized (processRecordCount){ processRecordCount++; }
	 * synchronized (errorData){ errorData.add(new
	 * MRNErrorData(jsonObj.getLong("recordNo"), "Success.")); } } }
	 * 
	 * }catch (Exception e) { handleDataIntegrityIssues(1, errorData,e); } // Wake
	 * up producer thread notify(); // and sleep Thread.sleep(1000); } } //produce
	 * method protected void produceOfficeRecords(LinkedList<Object> recordlist,
	 * BufferedReader csvFileBufferedReader, Long totalRecordCount,
	 * ArrayList<MRNErrorData> errorData) throws InterruptedException, IOException {
	 * String line; //ArrayList<MRNErrorData> errorData = new
	 * ArrayList<MRNErrorData>(); //Long totalRecordCount = (long) 0; int i=0;
	 * Object jsonString = null; final String splitLineRegX = ","; while((line =
	 * csvFileBufferedReader.readLine()) != null){ try{ line=line.replace(";"," ");
	 * final String[] currentLineData = line.split(splitLineRegX);
	 * if(currentLineData!=null && currentLineData[0].equalsIgnoreCase("EOF")){
	 * synchronized (recordlist){
	 * //this.dataUploadHelper.updateFile(uploadStatus,totalRecordCount,
	 * processRecordCount,errorData); recordlist.add(new String("EOF")); notify(); }
	 * } else{ synchronized (errorData){ jsonString =
	 * this.dataUploadHelper.buildJsonForOffice(currentLineData, errorData, i); }
	 * if(jsonString !=null){
	 * 
	 * synchronized (recordlist){ while (recordlist.size()==1000) wait();
	 * 
	 * recordlist.add(jsonString); notify(); } Thread.sleep(1000); synchronized
	 * (totalRecordCount){ totalRecordCount++; } } } }catch (Exception e) {
	 * 
	 * synchronized (errorData){ handleDataIntegrityIssues(i, errorData,e); } }
	 * //totalRecordCount++; i++;
	 * 
	 * }
	 * 
	 * 
	 * }
	 */

	private void handleDataIntegrityIssues(final int i, final ArrayList<MRNErrorData> errorData, final Exception dve) {

		if (dve instanceof OrderQuantityExceedsException) {
			errorData.add(new MRNErrorData((long) i,
					"Error: " + ((AbstractPlatformDomainRuleException) dve).getDefaultUserMessage()));

		} else if (dve instanceof NoGrnIdFoundException) {
			errorData.add(new MRNErrorData((long) i,
					"Error: " + ((AbstractPlatformDomainRuleException) dve).getDefaultUserMessage()));

		} else if (dve instanceof ItemNotFoundException) {
			errorData.add(new MRNErrorData((long) i,
					"Error: " + ((AbstractPlatformDomainRuleException) dve).getDefaultUserMessage()));

		} else if (dve instanceof PlatformApiDataValidationException) {
			errorData.add(new MRNErrorData((long) i,
					"Error: " + ((PlatformApiDataValidationException) dve).getErrors().get(0).getParameterName() + " : "
							+ ((PlatformApiDataValidationException) dve).getErrors().get(0).getDefaultUserMessage()));

		} else if (dve instanceof NullPointerException) {
			errorData.add(new MRNErrorData((long) i, "Error: value cannot be null"));

		} else if (dve instanceof IllegalStateException) {
			errorData.add(new MRNErrorData((long) i,
					((PlatformApiDataValidationException) dve).getErrors().get(0).getDefaultUserMessage()));

		} else if (dve instanceof AdjustmentCodeNotFoundException) {
			errorData.add(new MRNErrorData((long) i,
					"Error: " + ((AbstractPlatformDomainRuleException) dve).getDefaultUserMessage()));

		} else if (dve instanceof PlatformDataIntegrityException) {
			errorData.add(
					new MRNErrorData((long) i, "Error: " + ((PlatformDataIntegrityException) dve).getParameterName()
							+ " : " + ((PlatformDataIntegrityException) dve).getDefaultUserMessage()));

		} else if (dve instanceof SerialNumberNotFoundException) {
			errorData.add(new MRNErrorData((long) i,
					"Error: " + ((AbstractPlatformDomainRuleException) dve).getDefaultUserMessage()));

		} else if (dve instanceof SerialNumberAlreadyExistException) {
			errorData.add(new MRNErrorData((long) i,
					"Error: " + ((AbstractPlatformDomainRuleException) dve).getDefaultUserMessage()));

		} else if (dve instanceof DataIntegrityViolationException) {

			errorData.add(new MRNErrorData((long) i, "Error: " + ((DataIntegrityViolationException) dve).getMessage()));

		} else if (dve instanceof ClientNotFoundException) {
			errorData.add(new MRNErrorData((long) i,
					"Error: " + ((AbstractPlatformDomainRuleException) dve).getDefaultUserMessage()));

		} else if (dve instanceof PaymentCodeNotFoundException) {
			errorData.add(new MRNErrorData((long) i,
					"Error: " + ((AbstractPlatformDomainRuleException) dve).getDefaultUserMessage()));

		} else if (dve instanceof EOFException) {
			errorData.add(new MRNErrorData((long) i, "Completed: End Of Record"));

		} else if (dve instanceof ItemNotFoundException) {
			errorData.add(new MRNErrorData((long) i, "Invalid Item id"));

		} else if (dve instanceof InvalidMrnIdException) {
			errorData.add(new MRNErrorData((long) i, "Invalid Mrn id"));

		} else if (dve instanceof UnsupportedParameterException) {
			errorData.add(new MRNErrorData((long) i, "Row Contains Improper data "));
		} else {
			errorData.add(new MRNErrorData((long) i, "Data insertion is failed"));
		}
	}

	@Override
	public CommandProcessingResult addItem(DataUploadCommand command) {
		DataUpload uploadStatus;

		try {

			this.context.authenticatedUser();
			DataUploadCommandValidator validator = new DataUploadCommandValidator(command);
			validator.validateForCreate();
			String fileLocation = null;
			fileLocation = FileUtils.saveToFileSystem(command.getInputStream(), command.getFileUploadLocation(),
					command.getFileName());

			uploadStatus = DataUpload.create(command.getUploadProcess(), fileLocation, command.getProcessDate(),
					command.getProcessStatus(), command.getProcessRecords(), command.getErrorMessage(),
					command.getDescription(), command.getFileName());

			this.uploadStatusRepository.save(uploadStatus);
			return new CommandProcessingResult(uploadStatus.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (IOException e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	private void handleCodeDataIntegrityIssues(final DataUploadCommand command,
			final DataIntegrityViolationException dve) {
		Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("file_name_key")) {
			final String name = command.getFileName();

			throw new PlatformDataIntegrityException("error.msg.file.duplicate.name",
					"A file with name'" + name + "'already exists", "displayName", name);
		}

//        logger.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}

}
