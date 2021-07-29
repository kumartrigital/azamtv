package org.mifosplatform.logistics.agent.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.billing.taxmapping.domain.TaxMap;
import org.mifosplatform.billing.taxmapping.domain.TaxMapRepository;
import org.mifosplatform.finance.officebalance.domain.OfficeBalance;
import org.mifosplatform.finance.officebalance.domain.OfficeBalanceRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.agent.domain.ItemSale;
import org.mifosplatform.logistics.agent.domain.ItemSaleInvoice;
import org.mifosplatform.logistics.agent.domain.ItemSaleInvoiceRepository;
import org.mifosplatform.logistics.agent.domain.ItemSaleRepository;
import org.mifosplatform.logistics.agent.serialization.AgentItemSaleCommandFromApiJsonDeserializer;
import org.mifosplatform.logistics.item.domain.ItemMaster;
import org.mifosplatform.logistics.item.domain.ItemRepository;
import org.mifosplatform.logistics.item.exception.ItemNotFoundException;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hugo
 *
 */
@Service
public class ItemSaleWriteplatformServiceImpl implements ItemSaleWriteplatformService {

	private final PlatformSecurityContext context;
	private final ItemSaleRepository itemSaleRepository;
	private final AgentItemSaleCommandFromApiJsonDeserializer apiJsonDeserializer;
	private final ItemRepository itemRepository;
	private final TaxMapRepository taxMapRepository;
	private final ItemSaleInvoiceRepository itemSaleInvoiceRepository;
	private final OfficeBalanceRepository officeBalanceRepository;

	@Autowired
	public ItemSaleWriteplatformServiceImpl(final PlatformSecurityContext context,
			final ItemSaleRepository itemSaleRepository,
			final AgentItemSaleCommandFromApiJsonDeserializer apiJsonDeserializer, final ItemRepository itemRepository,
			final TaxMapRepository taxMapRepository, final ItemSaleInvoiceRepository itemSaleInvoiceRepository,
			final OfficeBalanceRepository officeBalanceRepository) {

		this.context = context;
		this.itemSaleRepository = itemSaleRepository;
		this.taxMapRepository = taxMapRepository;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.itemRepository = itemRepository;
		this.itemSaleInvoiceRepository = itemSaleInvoiceRepository;
		this.officeBalanceRepository = officeBalanceRepository;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * #createNewItemSale(org.mifosplatform.infrastructure.core.api.JsonCommand)
	 */
	@Transactional
	@Override
	public CommandProcessingResult createNewItemSale(final JsonCommand command) {

		try {

			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			Long purchaseByOffice = null;
			String purchaseBy = command.stringValueOfParameterName("purchaseBy");
			if (!purchaseBy.matches("[0-9]+")) {
				purchaseByOffice = this.itemSaleRepository.getOffice(purchaseBy);
				if (purchaseByOffice == null) {
					throw new PlatformDataIntegrityException("invalid.username", "invalid.move.operation",
							"No office found under this email or username");
				}
			}
			final ItemSale itemSale = ItemSale.fromJson(command);

			String itemtype = command.stringValueOfParameterName("type");

			if (itemtype!=null) {
				itemSale.setType("STB_SALE");
			}

			if (purchaseByOffice != null) {
				itemSale.setPurchaseBy(purchaseByOffice);
			}
			if (itemSale.getPurchaseFrom().equals(itemSale.getPurchaseBy())) {

				throw new PlatformDataIntegrityException("invalid.move.operation", "invalid.move.operation",
						"invalid.move.operation");
			}

			final ItemMaster itemMaster = this.itemRepository.findOne(itemSale.getItemId());
			final List<TaxMap> taxMaps = this.taxMapRepository.findOneByChargeCode(itemSale.getChargeCode());
			ItemSaleInvoice invoice = ItemSaleInvoice.fromJson(command);
			BigDecimal taxAmount = BigDecimal.ZERO;
			BigDecimal taxRate = BigDecimal.ZERO;

			for (TaxMap taxMap : taxMaps) {
				taxRate = taxMap.getRate();
				if (taxMap.getTaxType().equalsIgnoreCase("percentage")) {
					taxAmount = invoice.getChargeAmount().multiply(taxRate.divide(new BigDecimal(100)));
				} else {
					taxAmount = invoice.getChargeAmount().add(taxRate);
				}
			}
			if (itemMaster == null) {
				throw new ItemNotFoundException(itemSale.getItemId().toString());
			}
			invoice.updateAmounts(taxAmount);
			invoice.setTaxpercentage(taxRate);
			// invoice.setCurrencyId(356L);
			itemSale.setItemSaleInvoice(invoice);
			this.itemSaleRepository.save(itemSale);

			/* ItemSale invoice Balance update in office balance table */
			/*
			 * if (itemSale.getPurchaseFrom() != null) { OfficeBalance officeBalance =
			 * this.officeBalanceRepository .findOneByOfficeId(itemSale.getPurchaseFrom());
			 * 
			 * if (officeBalance != null) { officeBalance.updateBalance("CREDIT",
			 * itemSale.getItemSaleInvoice().getInvoiceAmount());
			 * 
			 * } else if (officeBalance == null) { BigDecimal balance =
			 * BigDecimal.ZERO.subtract(itemSale.getItemSaleInvoice().getInvoiceAmount());
			 * officeBalance = OfficeBalance.create(itemSale.getPurchaseFrom(), balance); }
			 * this.officeBalanceRepository.saveAndFlush(officeBalance); }
			 */
			if (itemSale.getPurchaseBy() != null) {
				OfficeBalance officeBalances = this.officeBalanceRepository.findOneByOfficeId(itemSale.getPurchaseBy());

				if (officeBalances != null) {
					officeBalances.updateBalance("DEBIT", itemSale.getItemSaleInvoice().getInvoiceAmount());

				} else if (officeBalances == null) {

					BigDecimal balance = itemSale.getItemSaleInvoice().getInvoiceAmount();
					officeBalances = OfficeBalance.create(itemSale.getPurchaseBy(), balance);
				}
				this.officeBalanceRepository.saveAndFlush(officeBalances);
			}

			return new CommandProcessingResult(itemSale.getId());
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1L));

		}

	}

	private void handleCodeDataIntegrityIssues(JsonCommand command, DataIntegrityViolationException dve) {

		Throwable realCause = dve.getMostSpecificCause();
		throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: " + realCause.getMessage());

	}

}
