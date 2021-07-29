package org.mifosplatform.organisation.officeadjustments.service;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.crm.service.CrmServices;
import org.mifosplatform.finance.officebalance.data.OfficeBalanceData;
import org.mifosplatform.finance.officebalance.domain.OfficeBalance;
import org.mifosplatform.finance.officebalance.domain.OfficeBalanceRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.domain.Office;
import org.mifosplatform.organisation.office.domain.OfficeRepository;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.organisation.officeadjustments.domain.OfficeAdjustments;
import org.mifosplatform.organisation.officeadjustments.domain.OfficeAdjustmentsRepository;
import org.mifosplatform.organisation.officeadjustments.serializer.OfficeAdjustmentsCommandFromApiJsonDeserializer;
import org.mifosplatform.organisation.officeadjustments.serializer.OfficeCreditLimitCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.mifosplatform.celcom.service.CelcomWritePlatformService;


/**
 * @author hugo
 * 
 */
@Service
public class OfficeAdjustmentsWritePlatformServiceImpl implements OfficeAdjustmentsWritePlatformService {

	private final PlatformSecurityContext context;
	private final OfficeAdjustmentsRepository officeAdjustmentsRepository;
	private final OfficeAdjustmentsCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final OfficeAdjustmentsReadPaltformService officeAdjustmentsReadPaltformService;
	private final OfficeBalanceRepository officeBalanceRepository;
	private final OfficeRepository officeRepository;
	private final  CrmServices crmServices;
	private final OfficeCreditLimitCommandFromApiJsonDeserializer fromApiCreditLimitJsonDeserializer;
	private final CelcomWritePlatformService celcomWritePlatformService;

	@Autowired
	public OfficeAdjustmentsWritePlatformServiceImpl(final PlatformSecurityContext context,
			final OfficeAdjustmentsRepository officeAdjustmentsRepository,
			final OfficeAdjustmentsCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final OfficeAdjustmentsReadPaltformService officeAdjustmentsReadPaltformService,
			final OfficeBalanceRepository officeBalanceRepository,
			final OfficeRepository officeRepository,final  CrmServices crmServices,
			final OfficeCreditLimitCommandFromApiJsonDeserializer fromApiCreditLimitJsonDeserializer,
			final CelcomWritePlatformService celcomWritePlatformService) {
		
		this.context = context;
		this.officeAdjustmentsRepository = officeAdjustmentsRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.officeAdjustmentsReadPaltformService = officeAdjustmentsReadPaltformService;
		this.officeBalanceRepository = officeBalanceRepository;
		this.officeRepository = officeRepository;
		this.crmServices = crmServices;
		this.fromApiCreditLimitJsonDeserializer=fromApiCreditLimitJsonDeserializer;
		this.celcomWritePlatformService = celcomWritePlatformService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * #createOfficeAdjustment(org.mifosplatform.infrastructure.core.api.JsonCommand
	 * )
	 */
	@Transactional
	@Override
	public CommandProcessingResult createOfficeAdjustment(final JsonCommand command) {

		try {
			/*context.authenticatedUser();*/
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			this.crmServices.createOfficeAdjustmentsCelcom(command);
			final OfficeAdjustments officeAdjustment = OfficeAdjustments.fromJson(command);
			
			//check exsists or not
			final List<OfficeBalanceData> balanceDatas = this.officeAdjustmentsReadPaltformService.retrieveOfficeBalance(command.entityId());
			OfficeBalance officeBalance = null;
			if (balanceDatas.size() != 0 ) {
				officeBalance = this.officeBalanceRepository.findOne(balanceDatas.get(0).getId());
			}
			if (officeBalance != null) {
				officeBalance = doUpdateAdjustmentOfficeBalance(command,officeBalance);

			} else if (officeBalance == null) {
				officeBalance = createAdjustmentOfficeBalance(command,officeBalance);
			}

		    this.saveOfficeBalanceEntity(officeBalance);
			this.officeAdjustmentsRepository.save(officeAdjustment);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(officeAdjustment.getId()).withOfficeId(command.entityId()).build();
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1L));
		}
	}

	public OfficeBalance createAdjustmentOfficeBalance(final JsonCommand command,OfficeBalance officeBalance) {

		officeBalance = calculateCreateOfficeBalance(OfficeAdjustments.fromJson(command).getAdjustmentType(), OfficeAdjustments
						.fromJson(command).getAmountPaid(), officeBalance,command.entityId());
		return officeBalance;
	}

	public OfficeBalance doUpdateAdjustmentOfficeBalance(final JsonCommand command, OfficeBalance officeBalance) {

		officeBalance = calculateUpdateOfficeBalance(OfficeAdjustments.fromJson(command).getAdjustmentType(), OfficeAdjustments
				.fromJson(command).getAmountPaid(), officeBalance);
		return officeBalance;
	}

	public OfficeBalance calculateUpdateOfficeBalance(final String transactionType, BigDecimal amount,OfficeBalance officeBalance) {

		if (amount == null)
			amount = new BigDecimal(0);
		if (transactionType.equalsIgnoreCase("DEBIT")) {
	    	officeBalance.setBalanceAmount(officeBalance.getBalanceAmount().add(amount));
		} else if (transactionType.equalsIgnoreCase("CREDIT")) {
			officeBalance.setBalanceAmount(officeBalance.getBalanceAmount().subtract(amount));
		}
		return officeBalance;

	}

	public OfficeBalance calculateCreateOfficeBalance(final String transactionType, BigDecimal amount,OfficeBalance officeBalance, 
			  final Long officeId) {
		
		BigDecimal balanceAmount = BigDecimal.ZERO;
		
		if (transactionType.equalsIgnoreCase("DEBIT")) {
			balanceAmount = amount;
		} else if (transactionType.equalsIgnoreCase("CREDIT")) {
			balanceAmount = BigDecimal.ZERO.subtract(amount);
		}
		officeBalance = new OfficeBalance(officeId, balanceAmount);

		return officeBalance;

	}
	
	public OfficeBalance saveOfficeBalanceEntity(OfficeBalance officeBalance) {

		Office office = this.officeRepository.findOne(officeBalance.getofficeId());
		if (office == null) {
			throw new OfficeNotFoundException(officeBalance.getofficeId());
		}
		OfficeBalance resultantOfficeBalance = this.officeBalanceRepository.save(officeBalance);
		return resultantOfficeBalance;
	}
	
	
	/**
	 * To update the credit limit.
	 */
	// TODO HEMANTH: CREDIT LIMIT 
	@Transactional
	@Override
	public CommandProcessingResult updateOfficeCreditLimit(JsonCommand command) {
		try {
			context.authenticatedUser();
			this.fromApiCreditLimitJsonDeserializer.validateOfficeCreditLimitDetails(command.json());
			final List<OfficeBalanceData> balanceDataList = this.officeAdjustmentsReadPaltformService.retrieveOfficeBalance(command.entityId());
			OfficeBalance officeBalance = null;
			if (balanceDataList.size() != 0 ) {
				officeBalance = this.officeBalanceRepository.findOne(balanceDataList.get(0).getId());
			}
			if (officeBalance != null) {
				//officeBalance.setCreditLimit(officeBalance.getCreditLimit().add(OfficeBalance.officeCreditLimitFromJson(command).getCreditLimit()));
				officeBalance.setCreditLimit(OfficeBalance.officeCreditLimitFromJson(command).getCreditLimit());
				// Celcom BRM Call to update the credit limit.
				celcomWritePlatformService.updateCreditLimit(command,officeBalance);
			}
		    this.saveOfficeBalanceEntity(officeBalance);
			return new CommandProcessingResultBuilder().withCommandId(command.commandId())
					.withEntityId(officeBalance.getId()).withOfficeId(officeBalance.getofficeId()).build();
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1L));
		}
	}

}
