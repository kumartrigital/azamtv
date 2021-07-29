package org.mifosplatform.logistics.mrn.service;

import java.text.ParseException;
import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.domain.ItemSale;
import org.mifosplatform.logistics.agent.domain.ItemSaleRepository;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetails;
import org.mifosplatform.logistics.itemdetails.domain.ItemDetailsRepository;
import org.mifosplatform.logistics.itemdetails.exception.SerialNumberNotFoundException;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistory;
import org.mifosplatform.logistics.mrn.domain.InventoryTransactionHistoryJpaRepository;
import org.mifosplatform.logistics.mrn.domain.MRNDetails;
import org.mifosplatform.logistics.mrn.domain.MRNDetailsJpaRepository;
import org.mifosplatform.logistics.mrn.exception.InvalidMrnIdException;
import org.mifosplatform.logistics.mrn.serialization.MRNDetailsCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.voucher.service.VoucherReadPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MRNDetailsWritePlatformServiceImp implements MRNDetailsWritePlatformService {

	private final static Logger LOGGER = (Logger) LoggerFactory.getLogger(MRNDetailsWritePlatformServiceImp.class);

	private final PlatformSecurityContext context;
	private final ItemSaleRepository itemSaleRepository;
	private final MRNDetailsJpaRepository mrnDetailsJpaRepository;
	private final MRNDetailsReadPlatformService mrnDetailsReadPlatformService;
	private final MRNDetailsCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ItemDetailsRepository inventoryItemDetailsRepository;
	private final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository;
	private final VoucherReadPlatformService voucherReadPlatformService;

	@Autowired
	public MRNDetailsWritePlatformServiceImp(final MRNDetailsJpaRepository mrnDetailsJpaRepository,
			final PlatformSecurityContext context, final MRNDetailsCommandFromApiJsonDeserializer apiJsonDeserializer,
			final MRNDetailsReadPlatformService mrnDetailsReadPlatformService,
			final InventoryTransactionHistoryJpaRepository inventoryTransactionHistoryJpaRepository,
			final ItemDetailsRepository inventoryItemDetailsRepository, final ItemSaleRepository itemSaleRepository,
			final VoucherReadPlatformService voucherReadPlatformService) {

		this.context = context;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.itemSaleRepository = itemSaleRepository;
		this.mrnDetailsJpaRepository = mrnDetailsJpaRepository;
		this.mrnDetailsReadPlatformService = mrnDetailsReadPlatformService;
		this.inventoryItemDetailsRepository = inventoryItemDetailsRepository;
		this.inventoryTransactionHistoryJpaRepository = inventoryTransactionHistoryJpaRepository;
		this.voucherReadPlatformService = voucherReadPlatformService;

	}

	@Transactional
	@Override
	public CommandProcessingResult createMRNDetails(final JsonCommand command) {

		try {
			context.authenticatedUser();
			apiJsonDeserializer.validateForCreate(command.json());
			final MRNDetails mrnDetails = MRNDetails.formJson(command);
			mrnDetailsJpaRepository.save(mrnDetails);
			return new CommandProcessingResultBuilder().withEntityId(mrnDetails.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResultBuilder().withEntityId(Long.valueOf(-1)).build();

		} catch (ParseException e) {
			throw new PlatformDataIntegrityException("invalid.date.format", "invalid.date.format", "purchaseDate");
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult moveMRN(JsonCommand command) {

		/*
		 * try { context.authenticatedUser();
		 * apiJsonDeserializer.validateForMove(command.json()); final Long mrnId =
		 * command.longValueOfParameterNamed("mrnId"); final String
		 * serialNumber=command.stringValueOfParameterNamed("serialNumber"); final
		 * MRNDetails mrnDetails = mrnDetailsJpaRepository.findOne(mrnId); if(mrnDetails
		 * == null){ throw new InvalidMrnIdException(mrnId.toString()); } final
		 * List<String> serialNumbers =
		 * mrnDetailsReadPlatformService.retriveSerialNumbers(mrnId);
		 * if(!serialNumbers.contains(serialNumber)){ throw new
		 * PlatformDataIntegrityException("invalid.serialnumber.allocation",
		 * "invalid.serialnumber.allocation", "serialNumber",""); }
		 * 
		 * ItemDetails details =
		 * inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(serialNumber
		 * ); if(details == null){ throw new ItemNotFoundException(serialNumber); }
		 * if(details.getOfficeId().equals(mrnDetails.getToOffice())){ throw new
		 * PlatformDataIntegrityException("invalid.move.operation",
		 * "invalid.move.operation", "invalid.move.operation"); }
		 * 
		 * details.setOfficeId(mrnDetails.getToOffice());
		 * if(mrnDetails.getReceivedQuantity() < mrnDetails.getOrderdQuantity()){
		 * mrnDetails.setReceivedQuantity(mrnDetails.getReceivedQuantity()+1);
		 * mrnDetails.setStatus("Pending"); } else
		 * if(mrnDetails.getReceivedQuantity().equals(mrnDetails.getOrderdQuantity())){
		 * throw new PlatformDataIntegrityException("received.quantity.is.full",
		 * "received.quantity.is.full", "received quantity is full"); }
		 * 
		 * InventoryTransactionHistory transactionHistory =
		 * InventoryTransactionHistory.logTransaction(mrnDetails.getRequestedDate(),
		 * mrnId,"MRN", serialNumber,mrnDetails.getItemMasterId(),
		 * mrnDetails.getFromOffice(), mrnDetails.getToOffice());
		 * 
		 * details.setOfficeId(mrnDetails.getToOffice());
		 * inventoryItemDetailsRepository.save(details);
		 * inventoryTransactionHistoryJpaRepository.save(transactionHistory);
		 * 
		 * if(mrnDetails.getOrderdQuantity().equals(mrnDetails.getReceivedQuantity())){
		 * mrnDetails.setStatus("Completed"); }
		 * mrnDetailsJpaRepository.save(mrnDetails); return new
		 * CommandProcessingResultBuilder().withEntityId(transactionHistory.getId()).
		 * build();
		 * 
		 * } catch (DataIntegrityViolationException dve) {
		 * handleDataIntegrityIssues(command, dve); return new
		 * CommandProcessingResultBuilder().withEntityId(Long.valueOf(-1)).build(); }
		 */

		try {
			context.authenticatedUser();
			apiJsonDeserializer.validateForMove(command.json());
			final Long mrnId = command.longValueOfParameterNamed("mrnId");
			final String serialNumber = command.stringValueOfParameterNamed("serialNumber");
			final String serialNumbers = mrnDetailsReadPlatformService.retriveSerialNumbersFromMrn(serialNumber, mrnId);
			if (!serialNumbers.contains(serialNumber)) {
				throw new PlatformDataIntegrityException("invalid.serialnumber.allocation",
						"invalid.serialnumber.allocation", "serialNumber", "");
			}

			final MRNDetails mrnDetails = mrnDetailsJpaRepository.findOne(mrnId);
			if (mrnDetails == null) {
				throw new InvalidMrnIdException(mrnId.toString());
			}

			ItemDetails details = inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(serialNumber);
			if (details == null) {
				throw new ItemNotFoundException(serialNumber);
			}
			if (details.getOfficeId().equals(mrnDetails.getToOffice())) {
				throw new PlatformDataIntegrityException("invalid.move.operation", "invalid.move.operation",
						"invalid.move.operation");
			}

			details.setOfficeId(mrnDetails.getToOffice());
			if (mrnDetails.getReceivedQuantity() < mrnDetails.getOrderdQuantity()) {
				mrnDetails.setReceivedQuantity(mrnDetails.getReceivedQuantity() + 1);
				mrnDetails.setStatus("Pending");
			} else if (mrnDetails.getReceivedQuantity().equals(mrnDetails.getOrderdQuantity())) {
				throw new PlatformDataIntegrityException("received.quantity.is.full", "received.quantity.is.full",
						"received quantity is full");
			}

			InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(
					mrnDetails.getRequestedDate(), mrnId, "MRN", serialNumber, mrnDetails.getItemMasterId(),
					mrnDetails.getFromOffice(), mrnDetails.getToOffice());

			details.setOfficeId(mrnDetails.getToOffice());
			inventoryItemDetailsRepository.save(details);
			inventoryTransactionHistoryJpaRepository.save(transactionHistory);

			if (mrnDetails.getOrderdQuantity().equals(mrnDetails.getReceivedQuantity())) {
				mrnDetails.setStatus("Completed");
			}
			mrnDetailsJpaRepository.save(mrnDetails);
			return new CommandProcessingResultBuilder().withEntityId(transactionHistory.getId()).build();

		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResultBuilder().withEntityId(Long.valueOf(-1)).build();
		}

	}

	private void handleDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

		Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("serial_no_constraint")) {
			throw new PlatformDataIntegrityException("validation.error.msg.inventory.mrn.duplicate.entry",
					"validation.error.msg.inventory.mrn.duplicate.entry",
					"validation.error.msg.inventory.mrn.duplicate.entry", "");

		}
		LOGGER.error(dve.getMessage(), dve);
	}

	@Override
	public CommandProcessingResult moveItemSale(JsonCommand command) {

		try {
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForMove(command.json());

			final Long itemId = command.longValueOfParameterNamed("itemId");
			final String serialNumber = command.stringValueOfParameterNamed("serialNumber");
			final ItemSale itemSale = itemSaleRepository.findOne(itemId);

			final List<String> serialNumbers = mrnDetailsReadPlatformService.retriveSerialNumbersForItems(itemId,
					serialNumber);
			if (serialNumbers == null || serialNumbers.isEmpty()) {
				throw new SerialNumberNotFoundException(serialNumber);
			}

			final ItemDetails details = inventoryItemDetailsRepository.getInventoryItemDetailBySerialNum(serialNumber);
			details.setOfficeId(itemSale.getPurchaseBy());
			details.setItemsaleId(itemId);

			if (itemSale.getReceivedQuantity() < itemSale.getOrderQuantity()) {
				itemSale.setReceivedQuantity(itemSale.getReceivedQuantity() + 1);
				itemSale.setStatus("Pending");

			} else if (itemSale.getReceivedQuantity().equals(itemSale.getOrderQuantity())) {
				throw new PlatformDataIntegrityException("received.quantity.is.full", "received.quantity.is.full",
						"received.quantity.is.full");
			}

			InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(
					DateUtils.getDateOfTenant(), itemId, "Move ItemSale", serialNumber, itemSale.getItemId(),
					itemSale.getPurchaseFrom(), itemSale.getPurchaseBy());

			details.setOfficeId(itemSale.getPurchaseBy());
			this.inventoryItemDetailsRepository.save(details);
			this.inventoryTransactionHistoryJpaRepository.save(transactionHistory);
			if (itemSale.getOrderQuantity().equals(itemSale.getReceivedQuantity())) {
				itemSale.setStatus("Completed");
			}
			// itemSaleRepository.save(itemSale);

			itemSaleRepository.saveDetails(itemSale.getReceivedQuantity(), itemSale.getStatus(), itemSale.getItemId());

			return new CommandProcessingResultBuilder().withEntityId(transactionHistory.getId()).build();
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResultBuilder().withEntityId(Long.valueOf(-1)).build();

		}

	}

	@Override
	public CommandProcessingResult movemrncarton(JsonCommand command) {

		try {
			context.authenticatedUser();
			apiJsonDeserializer.validateForMovemrncarton(command.json());
			final Long mrnId = command.longValueOfParameterNamed("mrnId");
			final String cartoonNumber = command.stringValueOfParameterNamed("cartoonNumber");
			final MRNDetails mrnDetails = mrnDetailsJpaRepository.findOne(mrnId);
			if (mrnDetails == null) {
				throw new InvalidMrnIdException(mrnId.toString());
			}
			final List<String> CartoonNumber = mrnDetailsReadPlatformService.retriveCartonNumber(mrnId);
			if (!cartoonNumber.contains(cartoonNumber)) {
				throw new PlatformDataIntegrityException("invalid.cartonNumber.allocation",
						"invalid.cartonNumber.allocation", "cartoonNumber", "");
			}

			List<ItemDetails> details = inventoryItemDetailsRepository.getInventoryItemDetailByCartonNum(cartoonNumber);
			if (details.isEmpty()) {
				throw new ItemNotFoundException(cartoonNumber);
			}
			long i = 0;
			for (ItemDetails itemDetail : details) {
				if (itemDetail.getcartoonNumber().equalsIgnoreCase(cartoonNumber)) {

					if (itemDetail.getOfficeId().equals(mrnDetails.getToOffice())) {
						throw new PlatformDataIntegrityException("invalid.move.operation", "invalid.move.operation",
								"invalid.move.operation");
					}

					itemDetail.setOfficeId(mrnDetails.getToOffice());
					if (mrnDetails.getReceivedQuantity() < mrnDetails.getOrderdQuantity()) {
						mrnDetails.setReceivedQuantity(mrnDetails.getReceivedQuantity() + 1);
						mrnDetails.setStatus("Pending");
					} else if (mrnDetails.getReceivedQuantity().equals(mrnDetails.getOrderdQuantity())) {
						throw new PlatformDataIntegrityException("received.quantity.is.full",
								"received.quantity.is.full", "received quantity is full");
					}

					InventoryTransactionHistory transactionHistory = InventoryTransactionHistory.logTransaction(
							mrnDetails.getRequestedDate(), mrnId, "MRN", cartoonNumber, mrnDetails.getItemMasterId(),
							mrnDetails.getFromOffice(), mrnDetails.getToOffice());

					itemDetail.setOfficeId(mrnDetails.getToOffice());
					inventoryItemDetailsRepository.save(details);
					inventoryTransactionHistoryJpaRepository.save(transactionHistory);

					if (mrnDetails.getOrderdQuantity().equals(mrnDetails.getReceivedQuantity())) {
						mrnDetails.setStatus("Completed");
					}
					mrnDetailsJpaRepository.save(mrnDetails);
					i++;

				} else {

					throw new PlatformDataIntegrityException("cartoonNuber.is.not.valid", "cartoonNuber.is.not.valid",
							"cartoonNuber.is.not.valid");
				}
			}
			return new CommandProcessingResultBuilder().withEntityId(i).build();
		} catch (DataIntegrityViolationException dve) {
			handleDataIntegrityIssues(command, dve);
			return new CommandProcessingResultBuilder().withEntityId(Long.valueOf(-1)).build();
		}
	}

	/*
	 * public void itemSaleMovement(String statusType, Long quantity) {
	 * List<VoucherData> voucherData = null; String csv =
	 * "/home/trigital/voucherData.csv"; try { voucherData =
	 * this.voucherReadPlatformService.getAllVoucherByStatus(statusType, quantity);
	 * String stringArray[] = voucherData.toArray(new String[voucherData.size()]);
	 * for (String k : stringArray) { System.out.println(k); } CSVWriter csvWriter =
	 * new CSVWriter(new FileWriter(csv)); csvWriter.writeNext(stringArray);
	 * csvWriter.close();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

}
