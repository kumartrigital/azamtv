/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.commands.service;

import java.util.Map;

import org.joda.time.DateTime;
import org.mifosplatform.commands.domain.CommandSource;
import org.mifosplatform.commands.domain.CommandSourceRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.exception.RollbackTransactionAsCommandIsNotApprovedByCheckerException;
import org.mifosplatform.commands.exception.UnsupportedCommandException;
import org.mifosplatform.commands.handler.CommandHandlerProvider;
import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationDomainService;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SynchronousCommandProcessingService implements CommandProcessingService {

	private final PlatformSecurityContext context;
	private final ApplicationContext applicationContext;
	private final ToApiJsonSerializer<Map<String, Object>> toApiJsonSerializer;
	private CommandSourceRepository commandSourceRepository;
	private final ConfigurationDomainService configurationDomainService;
	private final CommandHandlerProvider commandHandlerProvider;

	@Autowired
	public SynchronousCommandProcessingService(final PlatformSecurityContext context,
			final ApplicationContext applicationContext,
			final ToApiJsonSerializer<Map<String, Object>> toApiJsonSerializer,
			final CommandSourceRepository commandSourceRepository,
			final ConfigurationDomainService configurationDomainService,
			final CommandHandlerProvider commandHandlerProvider) {

		this.context = context;
		this.applicationContext = applicationContext;
		this.toApiJsonSerializer = toApiJsonSerializer;
		this.commandSourceRepository = commandSourceRepository;
		this.commandSourceRepository = commandSourceRepository;
		this.configurationDomainService = configurationDomainService;
		this.commandHandlerProvider = commandHandlerProvider;
	}

	// @Transactional
	@Override
	public CommandProcessingResult processAndLogCommand(final CommandWrapper wrapper, final JsonCommand command,
			final boolean isApprovedByChecker) {

		final boolean rollbackTransaction = this.configurationDomainService
				.isMakerCheckerEnabledForTask(wrapper.taskPermissionName());

		final NewCommandSourceHandler handler = findCommandHandler(wrapper);
		final CommandProcessingResult result = handler.processCommand(command);

		final AppUser maker = context.authenticatedUser(wrapper);

		CommandSource commandSourceResult = null;
		if (command.commandId() != null) {
			commandSourceResult = this.commandSourceRepository.findOne(command.commandId());
			commandSourceResult.markAsChecked(maker, DateTime.now());
		} else {
			commandSourceResult = CommandSource.fullEntryFrom(wrapper, command, maker);
		}
		commandSourceResult.updateResourceId(result.resourceId());
		commandSourceResult.updateClientId(result.getClientId());
		commandSourceResult.updateForAudit(result.getOfficeId(), result.getGroupId(), result.getClientId());

		String changesOnlyJson = null;
		if (result.hasChanges()) {
			changesOnlyJson = this.toApiJsonSerializer.serializeResult(result.getChanges());
			commandSourceResult.updateJsonTo(changesOnlyJson);
		}
		if (!result.hasChanges() && wrapper.isUpdateOperation()) {
			commandSourceResult.updateJsonTo(null);
		}

		if (commandSourceResult.hasJson()) {
			commandSourceRepository.save(commandSourceResult);
		}

		if ((rollbackTransaction || result.isRollbackTransaction()) && !isApprovedByChecker) {

			/*
			 * JournalEntry will generate a new transactionId every time. Updating the
			 * transactionId with old transactionId, because as there are no entries are
			 * created with new transactionId, will throw an error when checker approves the
			 * transaction
			 * 
			 * // commandSourceResult.updateTransaction(command.getTransactionId());
			 * 
			 * Update CommandSource json data with JsonCommand json data, line 77 and 81 may
			 * update the json data
			 */
			commandSourceResult.updateJsonTo(command.json());
			throw new RollbackTransactionAsCommandIsNotApprovedByCheckerException(commandSourceResult);
		}
		result.setRollbackTransaction(null);

		// publishEvent(wrapper.entityName(), wrapper.actionName(), result);
		return result;
	}

	@Transactional
	@Override
	public CommandProcessingResult logCommand(CommandSource commandSourceResult) {

		commandSourceResult.markAsAwaitingApproval();
		commandSourceResult = this.commandSourceRepository.save(commandSourceResult);

		return new CommandProcessingResultBuilder().withCommandId(commandSourceResult.getId())
				.withEntityId(commandSourceResult.getResourceId()).build();
	}

	private NewCommandSourceHandler findCommandHandler(final CommandWrapper wrapper) {
		NewCommandSourceHandler handler = null;

		if (wrapper.isConfigurationResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createSmtpConfigurationCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("updateConfigurationCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isDatatableResource()) {
			if (wrapper.isCreateDatatable()) {
				handler = this.applicationContext.getBean("createDatatableCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteDatatable()) {
				handler = this.applicationContext.getBean("deleteDatatableCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateDatatable()) {
				handler = this.applicationContext.getBean("updateDatatableCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createDatatableEntryCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateMultiple()) {
				handler = applicationContext.getBean("updateOneToManyDatatableEntryCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateOneToOne()) {
				handler = applicationContext.getBean("updateOneToOneDatatableEntryCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteMultiple()) {
				handler = applicationContext.getBean("deleteOneToManyDatatableEntryCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteOneToOne()) {
				handler = applicationContext.getBean("deleteOneToOneDatatableEntryCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isRegisterDatatable()) {
				handler = this.applicationContext.getBean("registerDatatableCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isNoteResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createNoteCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateNoteCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteNoteCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isClientIdentifierResource()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createClientIdentifierCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateClientIdentifierCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteClientIdentifierCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isClientCardDetailsResource()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createClientCardDetailsCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateClientCardDetailsCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteClientCardDetailsCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isClientResource() && !wrapper.isClientNoteResource()
				&& !wrapper.isClientIdentifierResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createClientCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateClientCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteClientCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isClientActivation()) {
				handler = applicationContext.getBean("activateClientCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isClientStatus()) {
				handler = applicationContext.getBean("updateClientStatusCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
			// end of client
		} else if (wrapper.isActivationProcessResource()) {
			handler = applicationContext.getBean("createActivationProcessHandler", NewCommandSourceHandler.class);
		} else if (wrapper.isUpdateRolePermissions()) {
			handler = applicationContext.getBean("updateRolePermissionsCommandHandler", NewCommandSourceHandler.class);
		} else if (wrapper.isPermissionResource()) {
			handler = applicationContext.getBean("updateMakerCheckerPermissionsCommandHandler",
					NewCommandSourceHandler.class);
		} else if (wrapper.isRoleResource()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createRoleCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateRoleCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isUserResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createUserCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateUserCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteUserCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isStaffResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createStaffCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateStaffCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isGuarantorResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createGuarantorCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateGuarantorCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteGuarantorCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isCollateralResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createCollateralCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateCollateralCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteCollateralCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isCodeResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createCodeCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateCodeCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteCodeCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isCodeValueResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createCodeValueCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateCodeValueCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteCodeValueCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} /*
			 * else if (wrapper.isCurrencyResource()) { handler =
			 * applicationContext.getBean("updateCurrencyCommandHandler",
			 * NewCommandSourceHandler.class); }
			 */else if (wrapper.isFundResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createFundCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateFundCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isOfficeResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createOfficeCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateOfficeCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isOfficeTransactionResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createOfficeTransactionCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteOfficeTransactionCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isChargeDefinitionResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createChargeDefinitionCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateChargeDefinitionCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteChargeDefinitionCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isLoanProductResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createLoanProductCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateLoanProductCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isLoanResource()) {
			if (wrapper.isApproveLoanApplication()) {
				handler = applicationContext.getBean("loanApplicationApprovalCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUndoApprovalOfLoanApplication()) {
				handler = applicationContext.getBean("loanApplicationApprovalUndoCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isApplicantWithdrawalFromLoanApplication()) {
				handler = applicationContext.getBean("loanApplicationWithdrawnByApplicantCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isRejectionOfLoanApplication()) {
				handler = applicationContext.getBean("loanApplicationRejectedCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDisbursementOfLoan()) {
				handler = applicationContext.getBean("disburseLoanCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUndoDisbursementOfLoan()) {
				handler = applicationContext.getBean("undoDisbursalLoanCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isLoanRepayment()) {
				handler = applicationContext.getBean("loanRepaymentCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isLoanRepaymentAdjustment()) {
				handler = applicationContext.getBean("loanRepaymentAdjustmentCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isWaiveInterestPortionOnLoan()) {
				handler = applicationContext.getBean("waiveInterestPortionOnLoanCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isLoanWriteOff()) {
				handler = applicationContext.getBean("writeOffLoanCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCloseLoanAsObligationsMet()) {
				handler = applicationContext.getBean("closeLoanCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCloseLoanAsRescheduled()) {
				handler = applicationContext.getBean("closeLoanAsRescheduledCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateLoanOfficer()) {
				handler = applicationContext.getBean("updateLoanOfficerCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isRemoveLoanOfficer()) {
				handler = applicationContext.getBean("removeLoanOfficerCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isBulkUpdateLoanOfficer()) {
				handler = applicationContext.getBean("bulkUpdateLoanOfficerCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isCreate()) {
				handler = applicationContext.getBean("loanApplicationSubmittalCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("loanApplicationModificationCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("loanApplicationDeletionCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isLoanChargeResource()) {
			if (wrapper.isAddLoanCharge()) {
				handler = applicationContext.getBean("addLoanChargeCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteLoanCharge()) {
				handler = applicationContext.getBean("deleteLoanChargeCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateLoanCharge()) {
				handler = applicationContext.getBean("updateLoanChargeCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isWaiveLoanCharge()) {
				handler = applicationContext.getBean("waiveLoanChargeCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isGLAccountResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createGLAccountCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateGLAccountCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteGLAccountCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isGLClosureResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createGLClosureCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateGLClosureCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteGLClosureCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isJournalEntryResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createJournalEntryCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isRevertJournalEntry()) {
				handler = applicationContext.getBean("reverseJournalEntryCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isSavingsProductResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createSavingsProductCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateSavingsProductCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteSavingsProductCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isSavingsAccountResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createSavingsAccountCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateSavingsAccountCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteSavingsAccountCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isSavingsAccountDeposit()) {
				handler = applicationContext.getBean("depositSavingsAccountCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isSavingsAccountWithdrawal()) {
				handler = applicationContext.getBean("withdrawSavingsAccountCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isSavingsAccountActivation()) {
				handler = applicationContext.getBean("activateSavingsAccountCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isSavingsAccountInterestCalculation()) {
				handler = applicationContext.getBean("calculateInterestSavingsAccountCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isSavingsAccountInterestPosting()) {
				handler = applicationContext.getBean("postInterestSavingsAccountCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isCalendarResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createCalendarCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateCalendarCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteCalendarCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isGroupResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createGroupCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateGroupCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUnassignStaff()) {
				handler = applicationContext.getBean("unassignStaffCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteGroupCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isGroupActivation()) {
				handler = applicationContext.getBean("activateGroupCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isCenterResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createCenterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateCenterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteCenterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCenterActivation()) {
				handler = applicationContext.getBean("activateCenterCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isCollectionSheetResource()) {

			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateCollectionSheetCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isReportResource()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createReportCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateReportCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteReportCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isServiceResource()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createServiceCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateServiceCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteServiceCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		}

		else if (wrapper.isContractResource()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createContractCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateContractCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteContractCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isPlanResource()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createPlanCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updatePlanCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deletePlanCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isPlanQualifier()) {
			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updatePlanQualifierCommandHandle", NewCommandSourceHandler.class);
			}

		} else if (wrapper.isPriceResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createPriceCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updatePriceCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deletePriceCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		}

		else if (wrapper.isOrderResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createOrderCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateOrderCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDisconnect()) {
				handler = applicationContext.getBean("disconnectOrderCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteOrderCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdatePrice()) {
				handler = applicationContext.getBean("updateOrderCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isRenewOrder()) {
				handler = applicationContext.getBean("renewalOrderCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.istopUp()) {
				handler = applicationContext.getBean("topUpCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isTOVDtopUp()) {
				handler = applicationContext.getBean("tovdTopUpCommandHandler", NewCommandSourceHandler.class);
			}

			else if (wrapper.isOrderUssd()) {
				handler = applicationContext.getBean("orderUssdCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isReconnectOrder()) {
				handler = applicationContext.getBean("reconnectOrderCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isChangePlan()) {
				handler = applicationContext.getBean("changePlanCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isApplyPormo()) {
				handler = applicationContext.getBean("orderPromoCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isRetrackOsdMessageOrder()) {
				handler = applicationContext.getBean("retrackOsdMessageOrderCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isOrderExtension()) {
				handler = applicationContext.getBean("orderExtensionCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isOrderTerminate()) {
				handler = applicationContext.getBean("orderTerminationCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isOrderSuspend()) {
				handler = applicationContext.getBean("orderSuspendCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isOrderReactive()) {
				handler = applicationContext.getBean("orderReactiveCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isRenewOrderWithClient()) {
				handler = applicationContext.getBean("renewalOrderWithClientCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isOrderScheduling()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("schedulingOrderCreationCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("schedulingOrderDeletionCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("schedulingOrderUpdationCommandHandler",
						NewCommandSourceHandler.class);
			}

		}

		else if (wrapper.isOrderAddons()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createOrderAddonsCommandHandler", NewCommandSourceHandler.class);

			} else if (wrapper.isDisconnect()) {
				handler = applicationContext.getBean("disconnectOrderAddonsCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isAddons()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createAddonsCommandHandler", NewCommandSourceHandler.class);

			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateAddonsCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteAddonsCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isOrderPriceResource()) {
			if (wrapper.isUpdatePrice()) {
				handler = applicationContext.getBean("updateOrderPriceCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isDeviceSaleResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createOneTimeSaleCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isOneTimeSaleResource()) {
			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateOneTimeSaleCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("cancelOneTimeSaleCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCalculatePrice()) {
				handler = applicationContext.getBean("calculateOneTimeSalePriceCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isAddressResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createAddressCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateAddressCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteAddressCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isAddressMasterResource()) {
			if (wrapper.isCreateLocation()) {
				handler = applicationContext.getBean("createLocationCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateLocation()) {
				handler = applicationContext.getBean("updateLocationCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteLocation()) {
				handler = applicationContext.getBean("deleteLocationCommandHandler", NewCommandSourceHandler.class);
			}

		}

		else if (wrapper.isItemResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createItemCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateItemCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteItemCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isInvoiceResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createInvoiceCommandHandler", NewCommandSourceHandler.class);
			}

		} else if (wrapper.isAdjustmentResource()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createAdjustmentCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isTransfer()) {
				handler = applicationContext.getBean("createjvTransactionAdjustmentCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateAdjustmentCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteAdjustmentCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isPaymentResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createPaymentCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCancel()) {
				handler = applicationContext.getBean("cancelPaymentCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isPaypalEnquirey()) {
				handler = applicationContext.getBean("paypalEnquireyPaymentCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isPaymodeResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createPaymodeCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updatePaymodeCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deletePaymodeCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isBillMasterResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createBillMasterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateBillMasterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteBillMasterCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isInventoryResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createInventoryItemsCommandHandler",
						NewCommandSourceHandler.class);

			} else if (wrapper.isUpdateInventoryItem()) {
				handler = applicationContext.getBean("updateInventoryItemsCommandHandler",
						NewCommandSourceHandler.class);

			} else if (wrapper.isDeleteInventoryItem()) {
				handler = applicationContext.getBean("deleteInventoryItemsCommandHandler",
						NewCommandSourceHandler.class);

			} else if (wrapper.isDeAllocateHardwareResource()) {
				handler = applicationContext.getBean("deAllocateItemCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isGrnResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createInventoryGrnCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("editInventoryGrnCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isAllocateHardwareResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createInventoryItemAllocationCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isDataUploadResource()) {
			if (wrapper.isProcess()) {
				handler = applicationContext.getBean("processDataUploadCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isChargeCodeResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createChargeCodeCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.isUpdateChargeCode()) {
				handler = applicationContext.getBean("updateChargeCodeCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isTaxMapResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createTaxMapCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.isUpdateTaxMap()) {
				handler = applicationContext.getBean("updateTaxMapCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isTicketResource()) {
			if (wrapper.isCreateTicket()) {
				handler = applicationContext.getBean("createTicketMasterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateTicket()) {
				handler = applicationContext.getBean("updateTicketMasterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCloseTicket()) {
				handler = applicationContext.getBean("deleteTicketMasterCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isEventResource()) {
			if (wrapper.isCreateEvent()) {
				handler = applicationContext.getBean("createEventMasterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateEvent()) {
				handler = applicationContext.getBean("updateEventMasterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCloseEvent()) {
				handler = applicationContext.getBean("deleteEventMasterCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isEventPricingResource()) {
			if (wrapper.isCreateEventPrice()) {
				handler = applicationContext.getBean("createEventPriceCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateEventPrice()) {
				handler = applicationContext.getBean("updateEventPriceCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCloseEventPrice()) {
				handler = applicationContext.getBean("deleteEventPriceCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isEventOrderResource()) {
			if (wrapper.isCreateEventOrder()) {
				handler = applicationContext.getBean("createEventOrderCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateEventPrice()) {
				handler = applicationContext.getBean("updateEventPriceCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateEventOrderPrice()) {
				handler = applicationContext.getBean("updateEventOrderPriceCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isCloseEventPrice()) {
				handler = applicationContext.getBean("closeEventPriceCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isMessageResource()) {
			if (wrapper.isCreateBillingMessage()) {
				handler = applicationContext.getBean("createBillingMessageTemplateCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateBillingMessage()) {
				handler = applicationContext.getBean("updateBillingMessageTemplateCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteBillingMessage()) {
				handler = applicationContext.getBean("deleteBillingMessageTemplateCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isCreateMessageData()) {
				handler = applicationContext.getBean("createMessageDataCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isInventoryItemAllocatable()) {
			handler = applicationContext.getBean("addInventoryItemAllocationCommandHandler",
					NewCommandSourceHandler.class);
		} else if (wrapper.isMediaAssetResource()) {
			if (wrapper.isCreateMediaAsset()) {
				handler = applicationContext.getBean("createMediaAssetCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateMediaAsset()) {
				handler = applicationContext.getBean("updateMediaAssetCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCloseMediaAsset()) {
				handler = applicationContext.getBean("deleteMediaAssetCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isBatch()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createBatchJobCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isClientProspect()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createProspectCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateProspectCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.isDeleteProspect()) {
				handler = applicationContext.getBean("deleteProspectCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.isFollowUpProspect()) {
				handler = applicationContext.getBean("followUpProspectCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.isConvertClientProspect()) {
				handler = applicationContext.getBean("convertProspectToClientCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isClientBalance()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createClientBalanceCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isClientAdditionalInfo()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createClientAdditionalInfoCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateClientAdditionalInfoCommandHandler",
						NewCommandSourceHandler.class);
			}

		} else if (wrapper.isOwnedHardware()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createOwnedHardwareCommandHandler",
						NewCommandSourceHandler.class);
			}
			if (wrapper.isOwnUpdate()) {
				handler = applicationContext.getBean("updateOwnedHardwareCommandHandler",
						NewCommandSourceHandler.class);
			}
			if (wrapper.isOwnDelete()) {
				handler = applicationContext.getBean("deleteOwnedHardwareCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isSchedulling()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createBatchJobSchedulingCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateJobParameterCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isCountryCurrencyResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createCountryCurrencyCommandHandler",
						NewCommandSourceHandler.class);
			}
			if (wrapper.isUpdateCurrencyConfig()) {
				handler = applicationContext.getBean("updateCountryCurrencyCommandHandler",
						NewCommandSourceHandler.class);
			}
			if (wrapper.isDeleteCurrencyConfig()) {
				handler = applicationContext.getBean("deleteCountryCurrencyCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isEpgProgramGuide()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createEpgProgramGuideCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isAssetResource()) {
			if (wrapper.isUpdateAsset()) {
				handler = applicationContext.getBean("updateAssetCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteAsset()) {
				handler = applicationContext.getBean("deleteAssetCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isRegionResource()) {
			if (wrapper.isCreateRegion()) {
				handler = applicationContext.getBean("createRegionCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateRegion()) {
				handler = applicationContext.getBean("updateRegionCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteRegion()) {
				handler = applicationContext.getBean("deleteRegionCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isSelfCare()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createSelfCareCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isSelfCareRegister()) {
				handler = applicationContext.getBean("registerSelfCareCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isSelfCareEmailVerification()) {
				handler = applicationContext.getBean("emailVerificationSelfCareCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isSelfCareGeneratePassword()) {
				handler = applicationContext.getBean("generateNewSelfCarePasswordCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isChangePassword()) {
				handler = applicationContext.getBean("selfcareChangePasswordCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isSelfCareUDP()) {

			if (wrapper.isCreateSelfCareUDP()) {
				handler = applicationContext.getBean("createSelfCareUDPCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateSelfCareUDP()) {
				handler = applicationContext.getBean("updateSelfcareUDPCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isForgotSelfCareUDP()) {
				handler = applicationContext.getBean("forgotSelfcareUDPCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isDiscountResource()) {
			if (wrapper.isCreateDiscount()) {
				handler = applicationContext.getBean("createDiscountCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateDiscount()) {
				handler = applicationContext.getBean("updateDiscountCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteDiscount()) {
				handler = applicationContext.getBean("deleteDiscountCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isEventActionMappingResource()) {
			if (wrapper.isCreateEventActionMapping()) {
				handler = applicationContext.getBean("createEventActionMappingCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateEventActionMapping()) {
				handler = applicationContext.getBean("updateEventActionMappingCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteEventActionMapping()) {
				handler = applicationContext.getBean("deleteEventActionMappingCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isPromotionCodeResource()) {
			if (wrapper.isCreatePromotionCode()) {
				handler = applicationContext.getBean("createPromotionCodeCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdatePromotionCode()) {
				handler = applicationContext.getBean("updatePromotionCodeCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDeletePrmotionCode()) {
				handler = applicationContext.getBean("deletePromotionCodeCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		}

		else if (wrapper.isMRN()) {
			if (wrapper.isMoveItemSale()) {
				handler = applicationContext.getBean("createItemSaleDetailsMoveCommandHandler",
						NewCommandSourceHandler.class);
			}
			if (wrapper.isCreateMRN()) {
				handler = applicationContext.getBean("createMRNDetailsCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.moveMRN()) {
				handler = applicationContext.getBean("createMRNDetailsMoveCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isSupplier()) {
			if (wrapper.isCreateSupplier()) {
				handler = applicationContext.getBean("createSupplierCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("updateSupplierCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isVoucherResource()) {

			if (wrapper.isCreateVoucherGroup()) {
				handler = applicationContext.getBean("createVoucherGroupCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isGenerateVoucherPin()) {
				handler = applicationContext.getBean("generateVoucherPinCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("updateVoucherPinCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteVoucherPinCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCancel()) {
				handler = applicationContext.getBean("cancelVoucherPinCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isSchedulerResource()) {
			if (wrapper.isUpdateOperation()) {
				handler = this.applicationContext.getBean("updateJobDetailCommandhandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isCreate()) {
				handler = this.applicationContext.getBean("createJobDetailCommandhandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = this.applicationContext.getBean("deleteJobDetailCommandhandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isCacheResource()) {
			if (wrapper.isUpdateOperation()) {
				handler = this.applicationContext.getBean("updateCacheCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isEntitlementResource()) {
			if (wrapper.isCreateEntitlement()) {
				handler = applicationContext.getBean("createEntitlementCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.PlanMappingResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createHardwarePlanCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdatePlanMapping()) {
				handler = applicationContext.getBean("updatePlanMappingCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isServiceMappingResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createServiceMappingCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateServiceMappingCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isAssociationResource()) {
			if (wrapper.isCreateAssociation()) {
				handler = applicationContext.getBean("createAssociationCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateAssociation()) {
				handler = this.applicationContext.getBean("updateAssociationCommandhandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateDeAssociation()) {
				handler = this.applicationContext.getBean("updateDeAssociationCommandhandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isPaymentGatewayResource()) {
			if (wrapper.isCreatePaymentGateway()) {
				handler = applicationContext.getBean("createPaymentGatewayCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdatePaymentGateway()) {
				handler = this.applicationContext.getBean("updatePaymentGatewayCommandhandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isOnlinePaymentGateway()) {
				handler = this.applicationContext.getBean("onlinePaymentGatewayCommandhandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdatePaypalProfile()) {
				handler = this.applicationContext.getBean("updatePaypalProfileRecurringCommandhandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdatePaypalProfileStatus()) {
				handler = this.applicationContext.getBean("updatePaypalProfileStatusCommandhandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isdeleteRecurringBilling()) {
				handler = this.applicationContext.getBean("deleteRecurringBillingCommandhandler",
						NewCommandSourceHandler.class);
			}

		} else if (wrapper.isHardwareSwapping()) {
			if (wrapper.isDoSwapping()) {
				handler = applicationContext.getBean("hardwareSwappingCommandHandler", NewCommandSourceHandler.class);
			}
		} else if (wrapper.isProvisioningSystem()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("provisioningCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdateProvisioning()) {
				handler = applicationContext.getBean("updatingProvisioningCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteProvisioning()) {
				handler = applicationContext.getBean("deleteProvisioningCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isCreateProvisioning()) {
				handler = applicationContext.getBean("createProvisioningCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isProvisioningDetails()) {
			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateProvisioningDetailsCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isConfirm()) {
				handler = applicationContext.getBean("confirmProvisioningDetailsCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isUserChatResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createUserChatCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateUserChatMessageCommandHandler",
						NewCommandSourceHandler.class);

			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteUserChatMessageCommandHandler",
						NewCommandSourceHandler.class);
			}

		} else if (wrapper.isCreditDistributionResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createCreditDistributionCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());

			}
		} else if (wrapper.isGroupDetailsResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createGroupsDetailsCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isCreateStatment()) {
				handler = applicationContext.getBean("createGroupsStatmentCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isCreateIpPoolManagement()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createIpPoolManagementCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateIpPoolManagementCommandHandler",
						NewCommandSourceHandler.class);

			} else if (wrapper.isUpdateIpStatus()) {
				handler = applicationContext.getBean("updateIpAddressStatusCommandHandler",
						NewCommandSourceHandler.class);

			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isProvisioningParams()) {
			if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("updateProvisioningServiceParamsCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isItemSale()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createItemSaleCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isOfficeAdjustmentResource()) {

			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createOfficeAdjustmentsCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} // TODO HEMANTH CREDIT LIMIT
		else if (wrapper.isUpdateOfficeCreditLimitResource()) {

			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateOfficeCreditLimitCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isOfficePaymentResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createOfficePaymentsCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isProvisioningDetails()) {
			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateProvisioningDetailsCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isMediaDeviceDetails()) {
			if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("updateMediaDeviceDetailsCommandHandler",
						NewCommandSourceHandler.class);

			} else if (wrapper.isUpdateMediaDeviceCrash()) {

				handler = applicationContext.getBean("updateMediaDeviceCrashDetailsCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isRedemptionResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createRedemptionCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isProvisioningPlanMappingResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createProvisioningPlanMappingCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateProvisioningPlanMappingCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isUpdateLoginStatus()) {
			handler = applicationContext.getBean("updateLoginStatusCommandHandler", NewCommandSourceHandler.class);

		} else if (wrapper.isGroupsDetailsProvisionResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createGroupsDetailsProvisionCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isTaxExemptionResource()) {
			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateClientTaxExemptionCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isBillModeResource()) {
			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateClientBillModeCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isIpStatus()) {
			if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateIpStatusCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isIpDescription()) {

			if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("updateIpDescriptionCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isProvisioningPlanMappingResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createProvisioningPlanMappingCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateProvisioningPlanMappingCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isEventValidationResource()) {
			if (wrapper.isCreateEventValidation()) {
				handler = applicationContext.getBean("createEventValidationCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteEventValidation()) {
				handler = applicationContext.getBean("deleteEventValidationCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isParentResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createParentClientHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteChildFromParentClientCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isSelfRegistrationResource()) {
			if (wrapper.isSelfRegistrationResource()) {
				handler = applicationContext.getBean("createSelfRegistrationCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isIpDetails()) {
			if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("updateIpDetailsCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isMediaAssetLocationResource()) {
			if (wrapper.isLocationAttributeMediaAsset()) {
				handler = applicationContext.getBean("createMediaAssetLocationAttributeCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isProvisionActionResource()) {
			if (wrapper.isActive()) {
				handler = applicationContext.getBean("updateProvisionActionStatusCommandHandler",
						NewCommandSourceHandler.class);
			}
		} else if (wrapper.isPaymentGatewayConfigResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createPaymentGatewayConfigCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updatePaymentGatewayConfigCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isLinkupAccount()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createLinkupAccountCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isPartner()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createPartnerCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updatePartnerCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isPartnerAgreement()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createPartnerAgreementCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updatePartnerAgreementCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deletePartnerAgreementCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isMessageTemplate()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createTemplateCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateTemplateCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteTemplateCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isRadService()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createRadServiceCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateRadServiceCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteRadServiceCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isVendorManagement()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createVendorManagementCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateVendorManagementCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteVendorManagementCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isPropertyResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createPropertyCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updatePropertyCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deletePropertyCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isAllocateDevice()) {
				handler = applicationContext.getBean("allocateDevicePropertyCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isVendorAgreement()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createVendorAgreementCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateVendorAgreementCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isServiceTransferResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createServiceTransferCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isFeeMasterResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createFeeMasterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updateFeeMasterCommandHandler", NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deleteFeeMasterCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}

		} else if (wrapper.isStaticIp()) {

			if (wrapper.isUpdateOperation()) {
				handler = applicationContext.getBean("updateStaticIpCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isPropertyMasterResource()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createPropertyMasterCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isUpdate()) {
				handler = applicationContext.getBean("updatePropertyMasterCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDelete()) {
				handler = applicationContext.getBean("deletePropertyMasterCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isRefund()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createRefundAmountCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isDeposit()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createDepositeCommandHandler", NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isUsageCharge()) {
			if (wrapper.isCreate()) {
				handler = applicationContext.getBean("createUsageChargesRawDataCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		} else if (wrapper.isBeesmartClient()) {
			if (wrapper.isUpdateBeesmartClient()) {
				handler = applicationContext.getBean("updateBeesmartClientCommandHandler",
						NewCommandSourceHandler.class);
			} else if (wrapper.isDeleteBeesmartClient()) {
				handler = applicationContext.getBean("deleteBeesmartClientCommandHandler",
						NewCommandSourceHandler.class);
			} else {
				throw new UnsupportedCommandException(wrapper.commandName());
			}
		}

		else if (wrapper.isRevPayResource()) {

			if (wrapper.isCreateRevPay()) {
				handler = applicationContext.getBean("createRevOrderCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.islockRevOrder()) {
				handler = applicationContext.getBean("lockRevOrderCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.iscompleteRevOrder()) {
				handler = applicationContext.getBean("completeRevOrderCommandHandler", NewCommandSourceHandler.class);
			}

		} else if (wrapper.isLeaseResource()) {
			if (wrapper.isCreateLease()) {
				handler = applicationContext.getBean("leaseActivationCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.isVerificationLease()) {
				handler = applicationContext.getBean("leaseValidationCommandHandler", NewCommandSourceHandler.class);
			}
			if (wrapper.isResedOtp()) {
				handler = applicationContext.getBean("resendOtpMessageCommandHandler", NewCommandSourceHandler.class);
			}
		}

		else if (wrapper.isNINResource()) {
			if (wrapper.isVerifyNIN()) {
				handler = applicationContext.getBean("verifyNINCommandHandler", NewCommandSourceHandler.class);
			}
		} else {
			handler = this.commandHandlerProvider.getHandler(wrapper.entityName(), wrapper.actionName());
		}
		return handler;
	}
}