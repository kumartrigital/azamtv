package org.mifosplatform.organisation.voucher.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.Transaction;
import org.mifosplatform.billing.planprice.domain.Price;
import org.mifosplatform.billing.planprice.domain.PriceRepository;
import org.mifosplatform.cms.eventorder.exception.InsufficientAmountException;
import org.mifosplatform.cms.eventorder.service.EventOrderWriteplatformService;
import org.mifosplatform.cms.journalvoucher.domain.JournalVoucher;
import org.mifosplatform.cms.journalvoucher.domain.JournalvoucherRepository;
import org.mifosplatform.finance.billingmaster.domain.BillMaster;
import org.mifosplatform.finance.chargeorder.service.ChargingOrderWritePlatformService;
import org.mifosplatform.finance.officebalance.domain.OfficeBalance;
import org.mifosplatform.finance.officebalance.domain.OfficeBalanceRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.domain.ItemSale;
import org.mifosplatform.logistics.agent.domain.ItemSaleRepository;
import org.mifosplatform.logistics.agent.exception.ItemSaleAlreadyDoneException;
import org.mifosplatform.logistics.agent.exception.ItemSaleIdNotFoundException;
import org.mifosplatform.logistics.agent.exception.ItemSaleNotRegisteredException;
import org.mifosplatform.logistics.agent.exception.OrderQuantityMisMatchedException;
import org.mifosplatform.logistics.grn.service.GrnDetailsWritePlatformService;
import org.mifosplatform.logistics.grn.service.GrnReadPlatformService;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.logistics.item.service.ItemReadPlatformService;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.voucher.data.VoucherData;
import org.mifosplatform.organisation.voucher.domain.ExportVoucher;
import org.mifosplatform.organisation.voucher.domain.ExportVoucherRepository;
import org.mifosplatform.organisation.voucher.domain.Voucher;
import org.mifosplatform.organisation.voucher.domain.VoucherDetails;
import org.mifosplatform.organisation.voucher.domain.VoucherDetailsRepository;
import org.mifosplatform.organisation.voucher.domain.VoucherRepository;
import org.mifosplatform.organisation.voucher.exception.AlreadyProcessedException;
import org.mifosplatform.organisation.voucher.exception.ExportingRequestFailed;
import org.mifosplatform.organisation.voucher.exception.NoMoreRecordsFoundToExportException;
import org.mifosplatform.organisation.voucher.exception.OfficeBalanceIsNotEnoughException;
import org.mifosplatform.organisation.voucher.exception.UnableToCancelVoucherException;
import org.mifosplatform.organisation.voucher.exception.VoucherDetailsNotFoundException;
import org.mifosplatform.organisation.voucher.exception.VoucherLengthMatchException;
import org.mifosplatform.organisation.voucher.serialization.VoucherCommandFromApiJsonDeserializer;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import ch.qos.logback.classic.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * 
 * @author ashokreddy
 * @author rakesh
 *
 */
@Service
public class VoucherWritePlatformServiceImpl implements VoucherWritePlatformService {

	private int remainingKeyLength;
	private char status = 0;
	private Voucher voucher;
	private String generatedKey;
	private Long quantity;
	private String type;
	private char enable = 'N';

	private static final String ALPHA = "Alpha";
	private static final String NUMERIC = "Numeric";
	private static final String TYPE_PRODUCT = "PRODUCT";
	private static final String TYPE_VALUE = "VALUE";
	private static final String ALPHANUMERIC = "AlphaNumeric";

	private final PlatformSecurityContext context;
	private final VoucherRepository voucherRepository;
	private final VoucherDetailsRepository voucherDetailsRepository;
	private final EventOrderWriteplatformService eventOrderWriteplatformService;
	private final VoucherCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final VoucherReadPlatformService voucherReadPlatformService;
	private final OfficeRepository officeRepository;
	private final JournalvoucherRepository journalvoucherRepository;
	private final ChargingOrderWritePlatformService chargingOrderWritePlatformService;
	private final FromJsonHelper fromJsonHelper;
	private final GrnDetailsWritePlatformService grnDetailsWritePlatformService;
	private final GrnReadPlatformService grnReadPlatformService;
	private final ItemSaleRepository itemSaleRepository;
	private final TenantAwareRoutingDataSource dataSource;
	private final ExportVoucherRepository exportRepository;
	private final OfficeBalanceRepository officeBalanceRepository;
	private final ItemReadPlatformService itemReadPlatformService;
	private final PlanRepository planRepository;
	private final PriceRepository priceRepository;

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(VoucherWritePlatformServiceImpl.class);

	@Autowired
	public VoucherWritePlatformServiceImpl(final PlatformSecurityContext context,
			final VoucherRepository voucherRepository, final VoucherReadPlatformService voucherReadPlatformService,
			final VoucherCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final VoucherDetailsRepository voucherDetailsRepository, final OfficeRepository officeRepository,
			final JournalvoucherRepository journalvoucherRepository,
			final EventOrderWriteplatformService eventOrderWriteplatformService,
			final ChargingOrderWritePlatformService chargingOrderWritePlatformService,
			final FromJsonHelper fromJsonHelper, final GrnDetailsWritePlatformService grnDetailsWritePlatformService,
			final GrnReadPlatformService grnReadPlatformService, final ItemSaleRepository itemSaleRepository,
			final TenantAwareRoutingDataSource dataSource, final ExportVoucherRepository exportRepository,
			final OfficeBalanceRepository officeBalanceRepository,
			final ItemReadPlatformService itemReadPlatformService,
			final PlanRepository planRepository,
			final PriceRepository priceRepository) {

		this.context = context;
		this.voucherRepository = voucherRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.voucherReadPlatformService = voucherReadPlatformService;
		this.chargingOrderWritePlatformService = chargingOrderWritePlatformService;
		this.eventOrderWriteplatformService = eventOrderWriteplatformService;
		this.voucherDetailsRepository = voucherDetailsRepository;
		this.journalvoucherRepository = journalvoucherRepository;
		this.officeRepository = officeRepository;
		this.fromJsonHelper = fromJsonHelper;
		this.grnDetailsWritePlatformService = grnDetailsWritePlatformService;
		this.grnReadPlatformService = grnReadPlatformService;
		this.itemSaleRepository = itemSaleRepository;
		this.dataSource = dataSource;
		this.exportRepository = exportRepository;
		this.officeBalanceRepository = officeBalanceRepository;
		this.itemReadPlatformService = itemReadPlatformService;
		this.priceRepository = priceRepository;
		this.planRepository = planRepository;
	}

	@Transactional
	@Override
	public CommandProcessingResult createRandomGenerator(final JsonCommand command) {

		try {
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());

			final Long officeId = command.longValueOfParameterNamed("officeId");
			final Office clientOffice = this.officeRepository.findOne(officeId);

			if (clientOffice == null) {
				throw new OfficeNotFoundException(officeId);
			}

			final Long length = command.bigDecimalValueOfParameterNamed("length").longValue();
			final String beginWith = command.stringValueOfParameterNamed("beginWith");
			final int bwLength = beginWith.trim().length();

			if (bwLength == length.intValue()) {

				throw new VoucherLengthMatchException();
			}

			Voucher voucherpin = Voucher.fromJson(command);

			voucherpin.setOfficeId(officeId);
			Random random = new Random();
			Integer rand = random.nextInt(1000);
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmssMs");
			Date date = new Date();
			String dateTime = formatter.format(date).toString();
			String batchName = beginWith + rand.toString() + dateTime + length.toString();
			voucherpin.setBatchName(batchName);

			this.voucherRepository.save(voucherpin);
			/*
			 * String poRefNo=voucherpin.getBatchName(); InventoryGrnData inventoryGrnData =
			 * grnReadPlatformService.retriveGrnIdByPoNo(poRefNo); //updating grn status to
			 * 1=completed CommandProcessingResult result=
			 * grnDetailsWritePlatformService.updateGrnOrderStatus(1,inventoryGrnData.getId(
			 * )); System.out.println(result);
			 */
			return new CommandProcessingResult(voucherpin.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		} catch (ParseException e) {
			return CommandProcessingResult.empty();
		}

	}

	@Transactional
	@Override
	public CommandProcessingResult generateVoucherPinKeys(final Long batchId) {

		try {
			voucher = this.voucherRepository.findOne(batchId);
			if (voucher == null) {
				throw new PlatformDataIntegrityException("error.msg.code.batchId.not.found",
						"VoucherBatch with id :'" + batchId + "'does not exists", "batchId", batchId);
			}
			if (voucher.getIsProcessed() == enable) {
				status = 'F';
				final Long voucherId = generateRandomNumbers();
				status = 'Y';
				return new CommandProcessingResult(voucherId);
			} else {
				throw new AlreadyProcessedException("VoucherPin Already Generated with this " + voucher.getBatchName());
			}
		} finally {
			if (voucher != null && voucher.getIsProcessed() == 'N') {
				voucher.setIsProcessed(status);
				this.voucherRepository.save(voucher);
			}
		}

	}

	public Long generateRandomNumbers() {

		final Long lengthofVoucher = voucher.getLength();

		final int length = (int) lengthofVoucher.longValue();

		quantity = voucher.getQuantity();

		type = voucher.getPinCategory();

		final int beginKeyLength = voucher.getBeginWith().length();

		remainingKeyLength = length - beginKeyLength;

		if (remainingKeyLength == 0) {

			throw new VoucherLengthMatchException();
		}

		final Long serialNo = voucher.getSerialNo();

		String minSerialSeries = "";
		String maxSerialSeries = "";

		for (int serialNoValidator = 0; serialNoValidator < serialNo; serialNoValidator++) {

			if (serialNoValidator > 0) {
				minSerialSeries = minSerialSeries + "0";
				maxSerialSeries = maxSerialSeries + "9";
			} else {
				minSerialSeries = minSerialSeries + "1";
				maxSerialSeries = maxSerialSeries + "9";
			}
		}

		final Long minNo = Long.parseLong(minSerialSeries);
		final Long maxNo = Long.parseLong(maxSerialSeries);

		long currentSerialNumber = this.voucherReadPlatformService.retrieveMaxNo(minNo, maxNo);

		if (currentSerialNumber == 0) {
			currentSerialNumber = minNo;
		}

		return randomValueGeneration(currentSerialNumber);

	}

	private Long randomValueGeneration(Long currentSerialNumber) {

		int quantityValidator;

		for (quantityValidator = 0; quantityValidator < quantity; quantityValidator++) {

			String name = voucher.getBeginWith() + generateRandomSingleCode();

			String value = this.voucherReadPlatformService.retrieveIndividualPin(name);

			if (value == null) {

				currentSerialNumber = currentSerialNumber + 1;
				Long officeId = voucher.getOfficeId();
				VoucherDetails voucherDetails = new VoucherDetails(name, currentSerialNumber, voucher, officeId);

				this.voucherDetailsRepository.save(voucherDetails);

			} else {
				quantityValidator = quantityValidator - 1;
			}

		}

		return voucher.getId();
	}

	private String generateRandomSingleCode() {

		if (type.equalsIgnoreCase(ALPHA)) {
			generatedKey = RandomStringUtils.randomAlphabetic(remainingKeyLength);
		}

		if (type.equalsIgnoreCase(NUMERIC)) {
			generatedKey = RandomStringUtils.randomNumeric(remainingKeyLength);
		}

		if (type.equalsIgnoreCase(ALPHANUMERIC)) {
			generatedKey = RandomStringUtils.randomAlphanumeric(remainingKeyLength);
		}

		return generatedKey;

	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {
		Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("batch_name")) {
			final String name = command.stringValueOfParameterNamed("batchName");
			throw new PlatformDataIntegrityException("error.msg.code.duplicate.batchname",
					"A batch with name'" + name + "'already exists", "displayName", name);
		}
		if (realCause.getMessage().contains("serial_no_key")) {
			throw new PlatformDataIntegrityException("error.msg.code.duplicate.serial_no_key",
					"A serial_no_key already exists", "displayName", "serial_no");
		}

		throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());
	}

	@Override
	public CommandProcessingResult updateUpdateVoucherPins(Long voucherId, JsonCommand command) {
		try {

			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForUpdate(command.json(), true);

			final String[] services = command.arrayValueOfParameterNamed("voucherIds");
			final String status = command.stringValueOfParameterNamed("status");

			for (final String id : services) {

				final VoucherDetails voucherpinDetails = voucherDetailsRetrieveById(Long.valueOf(id));
				if (!voucherpinDetails.getStatus().equalsIgnoreCase("USED")) {
					voucherpinDetails.setStatus(status);
					this.voucherDetailsRepository.save(voucherpinDetails);
				}
			}

			return new CommandProcessingResult(voucherId);

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return null;
		}

	}

	private VoucherDetails voucherDetailsRetrieveById(final Long id) {

		final VoucherDetails voucherDetails = this.voucherDetailsRepository.findOne(id);

		if (voucherDetails == null) {
			throw new VoucherDetailsNotFoundException(id);
		}
		return voucherDetails;
	}

	@Override
	public CommandProcessingResult deleteUpdateVoucherPins(Long voucherId, JsonCommand command) {

		try {
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForUpdate(command.json(), false);

			final String[] services = command.arrayValueOfParameterNamed("voucherIds");

			for (final String id : services) {
				final VoucherDetails voucherpinDetails = voucherDetailsRetrieveById(Long.valueOf(id));
				if (!voucherpinDetails.getStatus().equalsIgnoreCase("USED")) {
					voucherpinDetails.setIsDeleted('Y');
					this.voucherDetailsRepository.save(voucherpinDetails);
				}
			}

			return new CommandProcessingResult(voucherId);

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return null;
		}
	}

	@Override
	public CommandProcessingResult cancelVoucherPins(Long entityId, JsonCommand command) {

		try {
			this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCancel(command.json(), false);
			VoucherDetails voucherDetails = this.voucherDetailsRetrieveById(entityId);
			voucher = voucherDetails.getVoucher();
			BigDecimal value = new BigDecimal(voucher.getPinValue());

			if (voucher.getPinType().equalsIgnoreCase(TYPE_VALUE)) {
				boolean isSufficient = this.eventOrderWriteplatformService.checkClientBalance(value.doubleValue(),
						voucherDetails.getClientId(), true);
				if (!isSufficient) {
					throw new InsufficientAmountException("cancelvoucher");
				}

				JsonObject clientBalanceObject = new JsonObject();
				clientBalanceObject.addProperty("clientId", voucherDetails.getClientId());
				clientBalanceObject.addProperty("amount", value);
				clientBalanceObject.addProperty("isWalletEnable", false);
				clientBalanceObject.addProperty("locale", "en");

				final JsonElement clientServiceElementNew = fromJsonHelper.parse(clientBalanceObject.toString());
				JsonCommand clientBalanceCommand = new JsonCommand(null, clientServiceElementNew.toString(),
						clientServiceElementNew, fromJsonHelper, null, null, null, null, null, null, null, null, null,
						null, null, null);

				this.chargingOrderWritePlatformService.updateClientBalance(clientBalanceCommand);
			} else if (voucher.getPinType().equalsIgnoreCase(TYPE_PRODUCT)) {
				throw new UnableToCancelVoucherException();
			}

			/*
			 * JournalVoucher journalVoucher=new
			 * JournalVoucher(voucherDetails.getId(),DateUtils.getDateOfTenant()
			 * ,"VOUCHER CANCEL",null,value.doubleValue(), voucherDetails.getClientId());
			 */
			JournalVoucher journalVoucher = new JournalVoucher(DateUtils.getDateOfTenant(), "VOUCHER CANCEL");
			this.journalvoucherRepository.save(journalVoucher);

			voucherDetails.update(command.stringValueOfParameterNamed("cancelReason"));
			this.voucherDetailsRepository.save(voucherDetails);

			return new CommandProcessingResult(voucherDetails.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}

	}

	@Transactional
	@Override
	public CommandProcessingResult moveVouchers(JsonCommand command, Long toOffice) {
		// TODO Auto-generated method stub
		context.authenticatedUser();
		try {
			// check if office has enough balance of the voucher request
			Boolean isProduct =false;
			final Office clientOffice = this.officeRepository.findOne(toOffice);
			if (clientOffice == null) {
				throw new OfficeNotFoundException(toOffice);
			}
			OfficeBalance officeBalance = this.officeBalanceRepository.findOneByOfficeId(toOffice);
			if (officeBalance.getBalanceAmount().longValue() > 0L) {
				throw new OfficeBalanceIsNotEnoughException(officeBalance.getBalanceAmount());
			}

			Long saleRefNo = command.longValueOfParameterNamed("saleRefNo");
			Long fromOffice = command.longValueOfParameterNamed("fromOffice");
			final Office Office = this.officeRepository.findOne(fromOffice);
			if (Office == null) {
				throw new OfficeNotFoundException(fromOffice);
			}

			ItemSale itemSale = itemSaleRepository.findOne(saleRefNo);
			System.out.println("Itemsale object::" + itemSale.getPurchaseBy());
			System.out.println("toOffice::" + toOffice);

			if (itemSale == null) {
				throw new ItemSaleIdNotFoundException(saleRefNo);
			}
			if (itemSale.getStatus().equalsIgnoreCase("Completed")) {
				throw new ItemSaleAlreadyDoneException(saleRefNo);
			}

			if (!itemSale.getPurchaseBy().toString().equals(toOffice.toString())) {
				/*
				 * if (itemSale.getPurchaseBy() != toOffice) { throw new
				 * ItemSaleNotRegisteredException(toOffice); }
				 */
				throw new ItemSaleNotRegisteredException(toOffice);
			}
			if (!itemSale.getPurchaseFrom().toString().equals(fromOffice.toString())) {
				/*
				 * if (itemSale.getPurchaseBy() != toOffice) { throw new
				 * ItemSaleNotRegisteredException(toOffice); }
				 */
				throw new ItemSaleNotRegisteredException(fromOffice);
			}
			/*
			 * if (itemSale.getPurchaseFrom() != fromOffice) { throw new
			 * ItemSaleNotRegisteredException(fromOffice); }
			 */
			BigDecimal unitPrice = itemSale.getUnitPrice();
			Long orderdQuantity = itemSale.getOrderQuantity();
			ItemData itemData = this.itemReadPlatformService.retrieveSingleItemDetails(null, itemSale.getItemId(), null,
					false);
			List<String> planList = new ArrayList<String>();
			planList = this.planRepository.findAllPlanCode();
			if(planList.contains(itemData.getItemCode())) {
				Plan plan = this.planRepository.findwithPlanCode(itemData.getItemCode());
				Price price = this.priceRepository.findplansByPlanIdChargeOwnerSelf(plan.getId());	
				this.voucherDetailsRepository.updateProductTypeVoucherOffice(toOffice, orderdQuantity, saleRefNo,
						fromOffice,price.getId());
			}
			 else {
				this.voucherDetailsRepository.updateVoucherOffice(toOffice, orderdQuantity, saleRefNo, fromOffice,
						unitPrice);
			}
			// update itemsale status to completed and received quantity=ordered quantity
			itemSale.setStatus("Completed");
			itemSale.setReceivedQuantity(orderdQuantity);
			itemSaleRepository.saveAndFlush(itemSale);
			return new CommandProcessingResult(toOffice);
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return null;
		}

	}

	@Transactional
	@Override
	public CommandProcessingResult exportVoucher(JsonCommand command, Long saleRefId) {
		// TODO Auto-generated method stub
		ExportVoucher exportVoucher = new ExportVoucher();
		ItemSale itemSale = itemSaleRepository.findOne(saleRefId);
		if (itemSale == null) {
			throw new ItemSaleIdNotFoundException(saleRefId);
		}
		
		
		Integer quantity = command.integerValueOfParameterNamed("quantity");

		java.util.List<VoucherData> voucherData = voucherReadPlatformService.retrieveVocherDetailsBySaleRefId(saleRefId,
		quantity);
		Long voucherDateSize = (long) voucherData.size();

		if (!voucherDateSize.toString().equals(quantity.toString())) {
		throw new NoMoreRecordsFoundToExportException(
		"Avaliable quatity :" + voucherDateSize + " Request quatity :" + quantity);

		}
		System.out.println("No of Vouchers Exporting :" +voucherDateSize);
		if (voucherData.size() > 0) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmssMs");
		Date date = new Date();
		String dateTime = formatter.format(date).toString();
		String exportReqId = dateTime + "_" + saleRefId.toString() + "_" + quantity.toString();

		for (VoucherData v : voucherData) {
		String pinNum = v.getPinNo();
		this.voucherDetailsRepository.updateExportReqId(exportReqId, pinNum);
		}
		exportVoucher.setReqId(exportReqId);
		exportVoucher.setQuantity(Long.parseLong(quantity.toString()));
		// exportVoucher.setQuantity(quantity);

		exportVoucher.setRequestBy(itemSale.getPurchaseBy());
		exportVoucher.setRequestDate(date);
		exportVoucher.setSaleRefNo(saleRefId);
		exportVoucher.setStatus("EXPORTED");
		this.exportRepository.saveAndFlush(exportVoucher);

		} else {
			throw new NoMoreRecordsFoundToExportException();
		}
		return CommandProcessingResult.parsingResult(exportVoucher.getReqId());
	}

	public static void main(String args[]) {
		List<Integer> test = new ArrayList<Integer>();

		if (test.size() != 0) {
			System.out.println("VoucherWritePlatformServiceImpl.main()");
		}
		System.out.println("khdb");

	}

}
