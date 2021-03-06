/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.commands.domain;

public class CommandWrapper {

	private final Long commandId;
	@SuppressWarnings("unused")
	private final Long officeId;
	private final Long groupId;
	private final Long clientId;
	private final Long loanId;
	private final Long savingsId;
	private final String actionName;
	private final String entityName;
	private final String taskPermissionName;
	private final Long entityId;
	private final Long subentityId;
	private final String href;
	private final String json;
	private final Long codeId;
	private final String transactionId;
	private final String supportedEntityType;
	private final Long supportedEntityId;

	public static CommandWrapper wrap(final String actionName, final String entityName, final Long resourceId,
			final Long subresourceId, final Long loginId) {
		return new CommandWrapper(null, actionName, entityName, resourceId, subresourceId, null, null);
	}

	public static CommandWrapper fromExistingCommand(final Long commandId, final String actionName,
			final String entityName, final Long resourceId, final Long subresourceId, final String resourceGetUrl,
			final Long clientId) {
		return new CommandWrapper(commandId, actionName, entityName, resourceId, subresourceId, resourceGetUrl,
				clientId);
	}

	private CommandWrapper(final Long commandId, final String actionName, final String entityName,
			final Long resourceId, final Long subresourceId, final String resourceGetUrl, final Long clientId) {
		this.commandId = commandId;
		this.officeId = null;
		this.groupId = null;
		this.clientId = clientId;
		this.loanId = null;
		this.savingsId = null;
		this.actionName = actionName;
		this.entityName = entityName;
		this.taskPermissionName = actionName + "_" + entityName;
		this.entityId = (clientId == null) ? resourceId : clientId;
		this.subentityId = subresourceId;
		this.codeId = null;
		this.supportedEntityType = null;
		this.supportedEntityId = null;
		this.href = resourceGetUrl;
		this.json = null;
		this.transactionId = null;

	}

	public CommandWrapper(final Long officeId, final Long groupId, final Long clientId, final Long loanId,
			final Long savingsId, final String actionName, final String entityName, final Long entityId,
			final Long subentityId, final Long codeId, final String supportedEntityType, final Long supportedEntityId,
			final String href, final String json, final String transactionId, final Long loginId) {
		this.commandId = null;
		this.officeId = officeId;
		this.groupId = groupId;
		this.clientId = clientId;
		this.loanId = loanId;
		this.savingsId = savingsId;
		this.actionName = actionName;
		this.entityName = entityName;
		this.taskPermissionName = actionName + "_" + entityName;
		this.entityId = entityId;
		this.subentityId = subentityId;
		this.codeId = codeId;
		this.supportedEntityType = supportedEntityType;
		this.supportedEntityId = supportedEntityId;
		this.href = href;
		this.json = json;
		this.transactionId = transactionId;
	}

	public Long commandId() {
		return this.commandId;
	}

	public String actionName() {
		return this.actionName;
	}

	public String entityName() {
		return this.entityName;
	}

	public Long resourceId() {
		return this.entityId;
	}

	public Long subresourceId() {
		return this.subentityId;
	}

	public String taskPermissionName() {
		return this.actionName + "_" + this.entityName;
	}

	public boolean isCreate() {
		return this.actionName.equalsIgnoreCase("CREATE");
	}
	
	public boolean isTransfer() {
		return this.actionName.equalsIgnoreCase("Transfer");
	}

	public boolean isOwnUpdate() {
		return this.actionName.equalsIgnoreCase("UPDATE");
	}

	public boolean isOwnDelete() {
		return this.actionName.equalsIgnoreCase("DELETE");
	}

	public String getTaskPermissionName() {
		return this.taskPermissionName;
	}

	public Long getCodeId() {
		return this.codeId;
	}

	public String getHref() {
		return this.href;
	}

	public String getJson() {
		return this.json;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public Long getEntityId() {
		return this.entityId;
	}

	public Long getSubentityId() {
		return this.subentityId;
	}

	public Long getGroupId() {
		return this.groupId;
	}

	public Long getClientId() {
		return this.clientId;
	}

	public Long getLoanId() {
		return this.loanId;
	}

	public Long getSavingsId() {
		return this.savingsId;
	}

	public Long getSupportedEntityId() {
		return this.supportedEntityId;
	}

	public String getSupportedEntityType() {
		return this.supportedEntityType;
	}

	public boolean isUpdate() {
		// permissions resource has special update which involves no resource.
		return (isPermissionResource() && isUpdateOperation()) || (isCurrencyResource() && isUpdateOperation())
				|| (isUpdateOperation() && this.entityId != null);
	}

	public boolean isUpdateOperation() {
		return this.actionName.equalsIgnoreCase("UPDATE");
	}

	public boolean isDelete() {
		return isDeleteOperation() && this.entityId != null;
	}

	private boolean isDeleteOperation() {
		return this.actionName.equalsIgnoreCase("DELETE");
	}

	public boolean isUpdateRolePermissions() {
		return this.actionName.equalsIgnoreCase("PERMISSIONS") && this.entityId != null;
	}

	public boolean isConfigurationResource() {
		return this.entityName.equalsIgnoreCase("CONFIGURATION")
				|| this.entityName.equalsIgnoreCase("SMTPCONFIGURATION");
	}

	public boolean isPermissionResource() {
		return this.entityName.equalsIgnoreCase("PERMISSION");
	}

	public boolean isRoleResource() {
		return this.entityName.equalsIgnoreCase("ROLE");
	}

	public boolean isUserResource() {
		return this.entityName.equalsIgnoreCase("USER");
	}

	public boolean isCurrencyResource() {
		return this.entityName.equalsIgnoreCase("CURRENCY");
	}

	public boolean isCodeResource() {
		return this.entityName.equalsIgnoreCase("CODE");
	}

	public boolean isCodeValueResource() {
		return this.entityName.equalsIgnoreCase("CODEVALUE");
	}

	public boolean isStaffResource() {
		return this.entityName.equalsIgnoreCase("STAFF");
	}

	public boolean isGuarantorResource() {
		return this.entityName.equalsIgnoreCase("GUARANTOR");
	}

	public boolean isGLAccountResource() {
		return this.entityName.equalsIgnoreCase("GLACCOUNT");
	}

	public boolean isGLClosureResource() {
		return this.entityName.equalsIgnoreCase("GLCLOSURE");
	}

	public boolean isJournalEntryResource() {
		return this.entityName.equalsIgnoreCase("JOURNALENTRY");
	}

	public boolean isRevertJournalEntry() {
		return this.actionName.equalsIgnoreCase("REVERSE") && this.entityName.equalsIgnoreCase("JOURNALENTRY");
	}

	public boolean isFundResource() {
		return this.entityName.equalsIgnoreCase("FUND");
	}

	public boolean isOfficeResource() {
		return this.entityName.equalsIgnoreCase("OFFICE");
	}

	public boolean isOfficeTransactionResource() {
		return this.entityName.equalsIgnoreCase("OFFICETRANSACTION");
	}

	public boolean isChargeDefinitionResource() {
		return this.entityName.equalsIgnoreCase("CHARGE");
	}

	public boolean isLoanProductResource() {
		return this.entityName.equalsIgnoreCase("LOANPRODUCT");
	}

	public boolean isClientResource() {
		return this.entityName.equalsIgnoreCase("CLIENT");
	}

	public boolean isActivationProcessResource() {
		return this.actionName.equalsIgnoreCase("ACTIVATE") && this.entityName.equalsIgnoreCase("ACTIVATIONPROCESS");
	}

	public boolean isClientActivation() {
		return this.actionName.equalsIgnoreCase("ACTIVATE") && this.entityName.equalsIgnoreCase("CLIENT");
	}

	public boolean isGroupActivation() {
		return this.actionName.equalsIgnoreCase("ACTIVATE") && this.entityName.equalsIgnoreCase("GROUP");
	}

	public boolean isCenterActivation() {
		return this.actionName.equalsIgnoreCase("ACTIVATE") && this.entityName.equalsIgnoreCase("CENTER");
	}

	public boolean isClientIdentifierResource() {
		return this.entityName.equals("CLIENTIDENTIFIER");
	}

	public boolean isClientNoteResource() {
		return this.entityName.equals("CLIENTNOTE");
	}

	public boolean isLoanResource() {
		return this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isLoanChargeResource() {
		return this.entityName.equalsIgnoreCase("LOANCHARGE");
	}

	public boolean isCollateralResource() {
		return this.entityName.equalsIgnoreCase("COLLATERAL");
	}

	public boolean isApproveLoanApplication() {
		return this.actionName.equalsIgnoreCase("APPROVE") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isUndoApprovalOfLoanApplication() {
		return this.actionName.equalsIgnoreCase("APPROVALUNDO") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isApplicantWithdrawalFromLoanApplication() {
		return this.actionName.equalsIgnoreCase("WITHDRAW") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isRejectionOfLoanApplication() {
		return this.actionName.equalsIgnoreCase("REJECT") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isDisbursementOfLoan() {
		return this.actionName.equalsIgnoreCase("DISBURSE") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isUndoDisbursementOfLoan() {
		return this.actionName.equalsIgnoreCase("DISBURSALUNDO") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isLoanRepayment() {
		return this.actionName.equalsIgnoreCase("REPAYMENT") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isLoanRepaymentAdjustment() {
		return this.actionName.equalsIgnoreCase("ADJUST") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isWaiveInterestPortionOnLoan() {
		return this.actionName.equalsIgnoreCase("WAIVEINTERESTPORTION") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isLoanWriteOff() {
		return this.actionName.equalsIgnoreCase("WRITEOFF") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isCloseLoanAsObligationsMet() {
		return this.actionName.equalsIgnoreCase("CLOSE") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isCloseLoanAsRescheduled() {
		return this.actionName.equalsIgnoreCase("CLOSEASRESCHEDULED") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isAddLoanCharge() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("LOANCHARGE");
	}

	public boolean isDeleteLoanCharge() {
		return isDeleteOperation() && this.entityName.equalsIgnoreCase("LOANCHARGE");
	}

	public boolean isUpdateLoanCharge() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("LOANCHARGE");
	}

	public boolean isWaiveLoanCharge() {
		return this.actionName.equalsIgnoreCase("WAIVE") && this.entityName.equalsIgnoreCase("LOANCHARGE");
	}

	public boolean isUpdateLoanOfficer() {
		return this.actionName.equalsIgnoreCase("UPDATELOANOFFICER") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isRemoveLoanOfficer() {
		return this.actionName.equalsIgnoreCase("REMOVELOANOFFICER") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isBulkUpdateLoanOfficer() {
		return this.actionName.equalsIgnoreCase("BULKREASSIGN") && this.entityName.equalsIgnoreCase("LOAN");
	}

	public boolean isUnassignStaff() {
		return this.actionName.equalsIgnoreCase("UNASSIGNSTAFF") && this.entityName.equalsIgnoreCase("GROUP");
	}

	public String commandName() {
		return this.actionName + "_" + this.entityName;
	}

	public boolean isUpdateOfOwnUserDetails(final Long loggedInUserId) {
		return this.isUserResource() && isUpdate() && loggedInUserId.equals(this.entityId);
	}

	public boolean isDepositProductResource() {
		return this.entityName.equalsIgnoreCase("DEPOSITPRODUCT");
	}

	public boolean isDepositAccountResource() {
		return this.entityName.equalsIgnoreCase("DEPOSITACCOUNT");
	}

	public boolean isApprovalOfDeposit() {
		return this.actionName.equalsIgnoreCase("APPROVE") && this.entityName.equalsIgnoreCase("DEPOSITACCOUNT");
	}

	public boolean isWithdrawDepositAmount() {
		return this.actionName.equalsIgnoreCase("WITHDRAWAL") && this.entityName.equalsIgnoreCase("DEPOSITACCOUNT");
	}

	public boolean isWithdrawInterest() {
		return this.actionName.equalsIgnoreCase("INTEREST") && this.entityName.equalsIgnoreCase("DEPOSITACCOUNT");
	}

	public boolean isRenewOfDepositApplicaion() {
		return this.actionName.equalsIgnoreCase("RENEW") && this.entityName.equalsIgnoreCase("DEPOSITACCOUNT");
	}

	public boolean isRejectionOfDepositApplicaion() {
		return this.actionName.equalsIgnoreCase("REJECT") && this.entityName.equalsIgnoreCase("DEPOSITACCOUNT");
	}

	public boolean isWithdrawByApplicant() {
		return this.actionName.equalsIgnoreCase("WITHDRAW") && this.entityName.equalsIgnoreCase("DEPOSITACCOUNT");
	}

	public boolean isUndoApprovalOfDepositApplication() {
		return this.actionName.equalsIgnoreCase("APPROVALUNDO") && this.entityName.equalsIgnoreCase("DEPOSITACCOUNT");
	}

	public boolean isSavingsProductResource() {
		return this.entityName.equalsIgnoreCase("SAVINGSPRODUCT");
	}

	public boolean isSavingsAccountResource() {
		return this.entityName.equalsIgnoreCase("SAVINGSACCOUNT");
	}

	public boolean isSavingsAccountActivation() {
		return this.actionName.equalsIgnoreCase("ACTIVATE") && this.entityName.equalsIgnoreCase("SAVINGSACCOUNT");
	}

	public boolean isSavingsAccountDeposit() {
		return this.actionName.equalsIgnoreCase("DEPOSIT") && this.entityName.equalsIgnoreCase("SAVINGSACCOUNT");
	}

	public boolean isSavingsAccountWithdrawal() {
		return this.actionName.equalsIgnoreCase("WITHDRAWAL") && this.entityName.equalsIgnoreCase("SAVINGSACCOUNT");
	}

	public boolean isSavingsAccountInterestCalculation() {
		return this.actionName.equalsIgnoreCase("CALCULATEINTEREST")
				&& this.entityName.equalsIgnoreCase("SAVINGSACCOUNT");
	}

	public boolean isSavingsAccountInterestPosting() {
		return this.actionName.equalsIgnoreCase("POSTINTEREST") && this.entityName.equalsIgnoreCase("SAVINGSACCOUNT");
	}

	public boolean isCalendarResource() {
		return this.entityName.equalsIgnoreCase("CALENDAR");
	}

	public boolean isNoteResource() {
		boolean isnoteResource = false;
		if (this.entityName.equalsIgnoreCase("CLIENTNOTE") || this.entityName.equalsIgnoreCase("LOANNOTE")
				|| this.entityName.equalsIgnoreCase("LOANTRANSACTIONNOTE")
				|| this.entityName.equalsIgnoreCase("SAVINGNOTE") || this.entityName.equalsIgnoreCase("GROUPNOTE")) {
			isnoteResource = true;
		}
		return isnoteResource;
	}

	public boolean isGroupResource() {
		return this.entityName.equalsIgnoreCase("GROUP");
	}

	public boolean isCollectionSheetResource() {
		return this.entityName.equals("COLLECTIONSHEET");
	}

	public boolean isCenterResource() {
		return this.entityName.equalsIgnoreCase("CENTER");
	}

	public boolean isReportResource() {
		return this.entityName.equalsIgnoreCase("REPORT");
	}

	public boolean isServiceResource() {
		return this.entityName.equalsIgnoreCase("SERVICE");
	}

	public boolean isContractResource() {
		return this.entityName.equalsIgnoreCase("CONTRACT");

	}

	public boolean isPlanResource() {
		return this.entityName.equalsIgnoreCase("PLAN");
	}

	public boolean isPriceResource() {
		return this.entityName.equalsIgnoreCase("PRICE");
	}

	public boolean isOrderResource() {
		return this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isOneTimeSaleResource() {
		return this.entityName.equalsIgnoreCase("ONETIMESALE");
	}

	public boolean isItemResource() {
		return this.entityName.equalsIgnoreCase("ITEM");
	}

	public boolean isUpdatePrice() {
		return this.actionName.equalsIgnoreCase("UPDATE");
	}

	public boolean isDisconnect() {
		return this.actionName.equalsIgnoreCase("DISCONNECT");
	}

	public boolean isAddressResource() {
		return this.entityName.equalsIgnoreCase("address");
	}

	public boolean isOrderPriceResource() {
		return this.entityName.equalsIgnoreCase("ORDERPRICE");
	}

	public boolean isInvoiceResource() {
		return this.entityName.equalsIgnoreCase("INVOICE");
	}

	public boolean isAdjustmentResource() {
		return this.entityName.equalsIgnoreCase("ADJUSTMENT");
	}

	public boolean isPaymentResource() {
		return this.entityName.equalsIgnoreCase("PAYMENT");
	}

	public boolean isPaymodeResource() {
		return this.entityName.equalsIgnoreCase("PAYMODE");
	}

	public boolean isBillMasterResource() {
		return this.entityName.equalsIgnoreCase("BILLMASTER");
	}

	public boolean isCalculatePrice() {
		return this.entityName.equalsIgnoreCase("ONETIMESALE");
	}

	public boolean isInventoryResource() {
		return this.entityName.equalsIgnoreCase("INVENTORY");
	}

	public boolean isUpdateInventoryItem() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("INVENTORY");
	}

	public boolean isDeleteInventoryItem() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("INVENTORY");
	}

	public boolean isAllocateHardwareResource() {
		return this.entityName.equalsIgnoreCase("ALLOCATION");
	}

	public boolean isDataUploadResource() {
		return this.entityName.equalsIgnoreCase("DATAUPLOADS");
	}

	public boolean isTicketResource() {
		return this.entityName.equalsIgnoreCase("TICKET");
	}

	public boolean isCreateTicket() {
		return this.actionName.equalsIgnoreCase("Create") && this.entityName.equalsIgnoreCase("TICKET");
	}

	public boolean isUpdateTicket() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("TICKET");
	}

	public boolean isCloseTicket() {
		return this.actionName.equalsIgnoreCase("CLOSE") && this.entityName.equalsIgnoreCase("TICKET");
	}

	public boolean isEventResource() {
		return this.entityName.equalsIgnoreCase("EVENT");
	}

	public boolean isCreateEvent() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("EVENT");
	}

	public boolean isUpdateEvent() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("EVENT");
	}

	public boolean isCloseEvent() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("EVENT");
	}

	public boolean isEventPricingResource() {
		return this.entityName.equalsIgnoreCase("EVENTPRICE");
	}

	public boolean isCreateEventPrice() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("EVENTPRICE");
	}

	public boolean isUpdateEventPrice() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("EVENTPRICE");
	}

	public boolean isCloseEventPrice() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("EVENTPRICE");
	}

	public boolean isCreateLocation() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("LOCATION");
	}

	public boolean isUpdateLocation() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("LOCATION");
	}

	public boolean isDeleteLocation() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("LOCATION");
	}

	public boolean isEventOrderResource() {
		return this.entityName.equalsIgnoreCase("EVENTORDER");
	}

	public boolean isCreateEventOrder() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("EVENTORDER");
	}

	public boolean isInventoryItemAllocatable() {
		return this.actionName.equalsIgnoreCase("INSERT") && this.entityName.equalsIgnoreCase("ALLOCATION");
	}

	public boolean isGrnResource() {
		return this.entityName.equalsIgnoreCase("GRN");
	}

	public boolean isMediaAssetResource() {
		return this.entityName.equalsIgnoreCase("MEDIAASSET");
	}

	public boolean isCreateMediaAsset() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("MEDIAASSET");
	}

	public boolean isUpdateMediaAsset() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("MEDIAASSET");
	}

	public boolean isCloseMediaAsset() {
		return this.actionName.equalsIgnoreCase("DELETE" + "") && this.entityName.equalsIgnoreCase("MEDIAASSET");

	}

	public boolean isRenewOrder() {
		return this.actionName.equalsIgnoreCase("RENEWAL") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean istopUp() {
		return this.actionName.equalsIgnoreCase("TOPUP") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isTOVDtopUp() {
		return this.actionName.equalsIgnoreCase("TOVDTOPUP") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isOrderUssd() {
		return this.actionName.equalsIgnoreCase("ORDERUSSD") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isReconnectOrder() {
		return this.actionName.equalsIgnoreCase("RECONNECT") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isCreateBillingMessage() {

		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("BILLINGMESSAGE");
	}

	public boolean isCreateMessageData() {

		return this.actionName.equalsIgnoreCase("CREATEDATA") && this.entityName.equalsIgnoreCase("BILLINGMESSAGE");
	}

	public boolean isMessageResource() {

		return this.entityName.equalsIgnoreCase("BILLINGMESSAGE");
	}

	public boolean isUpdateBillingMessage() {

		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("BILLINGMESSAGE");
	}

	public boolean isTaxMapResource() {
		return this.entityName.equalsIgnoreCase("TAXMAPPING");
	}

	public boolean isChargeCodeResource() {
		return this.entityName.equalsIgnoreCase("CHARGECODE");
	}

	public boolean isUpdateChargeCode() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("CHARGECODE");
	}

	public boolean isUpdateTaxMap() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("TAXMAPPING");
	}

	public boolean isBatch() {
		return this.entityName.equalsIgnoreCase("BATCH");
	}

	public boolean isSchedulling() {
		return this.entityName.equalsIgnoreCase("SCHEDULERJOBPARAMETER");
	}

	public boolean isDeleteBillingMessage() {

		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("BILLINGMESSAGE");
	}

	public boolean isClientProspect() {
		return this.entityName.equalsIgnoreCase("PROSPECT");
	}

	public boolean isFollowUpProspect() {
		return this.entityName.equalsIgnoreCase("PROSPECT") && this.actionName.equalsIgnoreCase("FOLLOWUP");
	}

	public boolean isDeleteProspect() {
		return this.entityName.equalsIgnoreCase("PROSPECT") && this.actionName.equalsIgnoreCase("DELETE");
	}

	public boolean isConvertClientProspect() {
		return this.entityName.equalsIgnoreCase("PROSPECT") && this.actionName.equalsIgnoreCase("CONVERT");
	}

	public boolean isOwnedHardware() {
		return this.entityName.equalsIgnoreCase("OWNEDHARDWARE");/* && this.actionName.equalsIgnoreCase("CREATE") */
	}

	public boolean isCountryCurrencyResource() {
		return this.entityName.equalsIgnoreCase("COUNTRYCURRENCY");
	}

	public boolean isEpgProgramGuide() {
		return this.entityName.equalsIgnoreCase("EPGPROGRAMGUIDE") && this.actionName.equalsIgnoreCase("CREATE");
	}

	public boolean isAssetResource() {
		return this.entityName.equalsIgnoreCase("MEDIAASSET");
	}

	public boolean isUpdateAsset() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("MEDIAASSET");
	}

	public boolean isDeleteAsset() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("MEDIAASSET");
	}

	public boolean isUpdateCurrencyConfig() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("COUNTRYCURRENCY");
	}

	public boolean isDeleteCurrencyConfig() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("COUNTRYCURRENCY");
	}

	public boolean isRegionResource() {
		return this.entityName.equalsIgnoreCase("REGION");
	}

	public boolean isCreateRegion() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("REGION");
	}

	public boolean isUpdateRegion() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("REGION");
	}

	public boolean isSelfCare() {
		return this.entityName.equalsIgnoreCase("SELFCARE");
	}

	public boolean isSelfCareUDP() {
		return this.entityName.equalsIgnoreCase("SELFCAREUDP");
	}

	public boolean isDeleteRegion() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("REGION");
	}

	public boolean isCreatePaypal() {
		return this.actionName.equalsIgnoreCase("CREATEPAYPAL") && this.entityName.equalsIgnoreCase("PAYMENT");
	}

	public boolean isDiscountResource() {
		return this.entityName.equalsIgnoreCase("DISCOUNT");
	}

	public boolean isCreateDiscount() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("DISCOUNT");
	}

	public boolean isUpdateDiscount() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("DISCOUNT");
	}

	public boolean isDeleteDiscount() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("DISCOUNT");
	}

	public boolean isMRN() {
		return this.entityName.equalsIgnoreCase("MRN");
	}

	public boolean isCreateMRN() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("MRN");
	}

	public boolean moveMRN() {
		return this.actionName.equalsIgnoreCase("MOVE") && this.entityName.equalsIgnoreCase("MRN");
	}

	public boolean isMoveItemSale() {
		return this.actionName.equalsIgnoreCase("MOVEITEM") && this.entityName.equalsIgnoreCase("MRN");
	}

	public boolean isSupplier() {
		return this.entityName.equalsIgnoreCase("SUPPLIER");
	}

	public boolean isCreateSupplier() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("SUPPLIER");
	}

	public boolean isVoucherResource() {

		return this.entityName.equalsIgnoreCase("VOUCHER");
	}

	public boolean isCreateVoucherGroup() {

		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("VOUCHER");
	}

	public boolean isSchedulerResource() {
		return this.entityName.equalsIgnoreCase("SCHEDULER");
	}

	public boolean isEntitlementResource() {
		return this.entityName.equalsIgnoreCase("ENTITLEMENT");
	}

	public boolean isCreateEntitlement() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("ENTITLEMENT");

	}

	public boolean isClientBalance() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("BALANCE");
	}

	public boolean isEditClientProspect() {
		return this.entityName.equalsIgnoreCase("PROSPECT") && this.actionName.equalsIgnoreCase("EDIT");
	}

	public boolean isRetrackOsdMessageOrder() {
		return this.actionName.equalsIgnoreCase("RETRACKOSDMESSAGE") && this.entityName.equalsIgnoreCase("ORDER");

	}

	public boolean PlanMappingResource() {
		return this.entityName.equalsIgnoreCase("PLANMAPPING");
	}

	public boolean isUpdatePlanMapping() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("PLANMAPPING");
	}

	public boolean isServiceMappingResource() {
		return this.entityName.equalsIgnoreCase("SERVICEMAPPING");
	}

	public boolean isAssociationResource() {
		return this.entityName.equalsIgnoreCase("ASSOCIATION");
	}

	public boolean isCreateAssociation() {
		return this.actionName.equalsIgnoreCase("CREATE");
	}

	public boolean isUpdateAssociation() {
		return this.actionName.equalsIgnoreCase("UPDATE");
	}

	public boolean isUpdateDeAssociation() {
		return this.actionName.equalsIgnoreCase("DEASSOCIATION");
	}

	public boolean isPaymentGatewayResource() {
		return this.entityName.equalsIgnoreCase("PAYMENTGATEWAY");
	}

	public boolean isCreatePaymentGateway() {
		return this.actionName.equalsIgnoreCase("CREATE");
	}

	public boolean isUpdateEventOrderPrice() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("EVENTORDER");
	}

	public boolean isHardwareSwapping() {
		return this.entityName.equalsIgnoreCase("HARDWARESWAPPING");
	}

	public boolean isDoSwapping() {
		return this.actionName.equalsIgnoreCase("SWAPPING");
	}

	public boolean isProvisioningSystem() {
		return this.entityName.equalsIgnoreCase("PROVISIONINGSYSTEM");
	}

	public boolean isUpdateProvisioning() {
		return this.actionName.equalsIgnoreCase("UPDATE");
	}

	public boolean isDeleteProvisioning() {
		return this.actionName.equalsIgnoreCase("DELETE");
	}

	public boolean isUserChatResource() {
		return this.entityName.equalsIgnoreCase("USERCHATMESSAGE");
	}

	public boolean isUpdatePaymentGateway() {
		return this.actionName.equalsIgnoreCase("UPDATE");
	}

	public boolean isEventActionMappingResource() {
		return this.entityName.equalsIgnoreCase("EVENTACTIONMAP");
	}

	public boolean isCreateEventActionMapping() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("EVENTACTIONMAP");
	}

	public boolean isUpdateEventActionMapping() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("EVENTACTIONMAP");
	}

	public boolean isDeleteEventActionMapping() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("EVENTACTIONMAP");
	}

	public boolean isCancel() {
		return this.actionName.equalsIgnoreCase("CANCEL");
	}

	public boolean isChangePlan() {
		return this.actionName.equalsIgnoreCase("CHANGEPLAN") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isPromotionCodeResource() {
		return this.entityName.equalsIgnoreCase("PROMOTIONCODE");
	}

	public boolean isCreatePromotionCode() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("PROMOTIONCODE");
	}

	public boolean isUpdatePromotionCode() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("PROMOTIONCODE");
	}

	public boolean isDeletePrmotionCode() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("PROMOTIONCODE");
	}

	public boolean isApplyPormo() {

		return this.actionName.equalsIgnoreCase("APPLYPROMO") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isDeAllocateHardwareResource() {
		return this.actionName.equalsIgnoreCase("DEALLOCATE") && this.entityName.equalsIgnoreCase("INVENTORY");
	}

	public boolean isCacheResource() {
		return this.entityName.equalsIgnoreCase("CACHE");
	}

	public boolean isAddressMasterResource() {
		return this.entityName.equalsIgnoreCase("LOCATION");
	}

	public boolean isRevPayResource() {
		return this.entityName.equalsIgnoreCase("REVPAY");
	}

	public boolean isNINResource() {
		return this.entityName.equalsIgnoreCase("NIN");
	}

	public boolean isLeaseResource() {
		return this.entityName.equalsIgnoreCase("LEASE");
	}

	public boolean isCreateLease() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("LEASE");
	}

	public boolean isVerificationLease() {
		return this.actionName.equalsIgnoreCase("VALIDATION") && this.entityName.equalsIgnoreCase("LEASE");
	}
	public boolean isResedOtp() {
		return this.actionName.equalsIgnoreCase("OTPMESSAGE") && this.entityName.equalsIgnoreCase("LEASE");
	}

	public boolean isCreditDistributionResource() {
		return this.entityName.equalsIgnoreCase("CREDITDISTRIBUTION");
	}

	public boolean isOrderScheduling() {

		return this.entityName.equalsIgnoreCase("ORDERSCHEDULING");
	}

	public boolean isOrderExtension() {
		return this.actionName.equalsIgnoreCase("EXTENSION") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isOrderTerminate() {
		return this.actionName.equalsIgnoreCase("TERMINATE") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isGroupDetailsResource() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("GROUPSDETAILS");

	}

	public boolean isCreateProvisioning() {

		return this.actionName.equalsIgnoreCase("ADD") && this.entityName.equalsIgnoreCase("PROVISIONINGSYSTEM");
	}

	public boolean isCreateIpPoolManagement() {
		return this.entityName.equalsIgnoreCase("IPPOOLMANAGEMENT");

	}

	public boolean isProvisioningParams() {
		return this.entityName.equalsIgnoreCase("PROVISIONINGSERVICEPARAMS");
	}

	public boolean isItemSale() {
		return this.entityName.equalsIgnoreCase("ITEMSALE");
	}

	public boolean isProvisioningDetails() {
		return this.entityName.equalsIgnoreCase("PROVISIONINGDETAILS");
	}

	public boolean isOfficeAdjustmentResource() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("OFFICEADJUSTMENT");
	}

	// Credit Limit Hemanth
	public boolean isUpdateOfficeCreditLimitResource() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("OFFICECREDITLIMIT");
	}

	public boolean isOfficePaymentResource() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("OFFICEPAYMENT");

	}

	public boolean isPaypalEnquirey() {
		return this.actionName.equalsIgnoreCase("CREATEENQUIREY") && this.entityName.equalsIgnoreCase("PAYMENT");
	}

	public boolean isClientCardDetailsResource() {
		return this.entityName.equals("CLIENTCARDDETAILS");
	}

	public boolean isGroupsDetailsProvisionResource() {

		return this.entityName.equalsIgnoreCase("GROUPSPROVISION");
	}

	public boolean isTaxExemptionResource() {

		return this.entityName.equalsIgnoreCase("CLIENTTAXEXEMPTION");
	}

	public boolean isBillModeResource() {

		return this.entityName.equalsIgnoreCase("CLIENTBILLMODE");
	}

	public boolean isDatatableResource() {
		return this.href.startsWith("/datatables/");
	}

	public boolean isCreateDatatable() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.href.startsWith("/datatables/")
				&& this.entityId == null;
	}

	public boolean isDeleteDatatable() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.href.startsWith("/datatables/")
				&& this.entityId == null;
	}

	public boolean isUpdateDatatable() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.href.startsWith("/datatables/")
				&& this.entityId == null;
	}

	public boolean isRegisterDatatable() {
		return this.actionName.equalsIgnoreCase("REGISTER") && this.href.startsWith("/datatables/")
				&& this.entityId == null;
	}

	public boolean isDeleteOneToOne() {
		/* also covers case of deleting all of a one to many */
		return isDatatableResource() && isDeleteOperation() && this.subentityId == null;
	}

	public boolean isDeleteMultiple() {
		return isDatatableResource() && isDeleteOperation() && this.subentityId != null;
	}

	public boolean isUpdateOneToOne() {
		return isDatatableResource() && isUpdateOperation() && this.subentityId == null;
	}

	public boolean isUpdateMultiple() {
		return isDatatableResource() && isUpdateOperation() && this.subentityId != null;
	}

	public boolean isIpStatus() {

		return this.entityName.equalsIgnoreCase("IPSTATUS");
	}

	public boolean isCreateStatment() {
		return this.actionName.equalsIgnoreCase("CREATESTATMENT");
	}

	public boolean isGenerateVoucherPin() {
		return this.actionName.equalsIgnoreCase("PROCESS") && this.entityName.equalsIgnoreCase("VOUCHER");
	}

	public boolean isCreateSelfCareUDP() {
		return this.entityName.equalsIgnoreCase("SELFCAREUDP") && this.actionName.equalsIgnoreCase("CREATE");
	}

	public boolean isMediaDeviceDetails() {

		return this.entityName.equalsIgnoreCase("MEDIADEVICE");
	}

	public boolean isRedemptionResource() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("REDEMPTION");
	}

	public boolean isClientStatus() {

		return this.actionName.equalsIgnoreCase("UPDATESTATUS") && this.entityName.equalsIgnoreCase("CLIENT");

	}

	public boolean isProvisioningPlanMappingResource() {
		return this.entityName.equalsIgnoreCase("PROVISIONINGPLANMAPPING");
	}

	public boolean isUpdateMediaDeviceCrash() {
		return this.actionName.equalsIgnoreCase("UPDATECRASH");
	}

	public boolean isUpdateSelfCareUDP() {
		return this.entityName.equalsIgnoreCase("SELFCAREUDP") && this.actionName.equalsIgnoreCase("UPDATE");
	}

	public boolean isForgotSelfCareUDP() {
		return this.entityName.equalsIgnoreCase("SELFCAREUDP") && this.actionName.equalsIgnoreCase("MAIL");
	}

	public boolean isUpdateLoginStatus() {
		return this.entityName.equalsIgnoreCase("LOGINHISTORY") && this.actionName.equalsIgnoreCase("UPDATE");
	}

	public boolean isIpDescription() {
		return this.entityName.equalsIgnoreCase("IPDESCRIPTION");
	}

	public boolean isEventValidationResource() {
		return this.entityName.equalsIgnoreCase("EVENTVALIDATION");
	}

	public boolean isCreateEventValidation() {
		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("EVENTVALIDATION");
	}

	public boolean isDeleteEventValidation() {
		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("EVENTVALIDATION");
	}

	public boolean isParentResource() {
		return this.entityName.equalsIgnoreCase("PARENTCLIENT");
	}

	public boolean isUpdateIpStatus() {
		return this.actionName.equalsIgnoreCase("UPDATEIPSTATUS")
				&& this.entityName.equalsIgnoreCase("IPPOOLMANAGEMENT");
	}

	public boolean isOrderSuspend() {
		return this.actionName.equalsIgnoreCase("SUSPEND") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isSelfCareRegister() {
		return this.actionName.equalsIgnoreCase("REGISTER") && this.entityName.equalsIgnoreCase("SELFCARE");
	}

	public boolean isSelfCareEmailVerification() {
		return this.actionName.equalsIgnoreCase("EMAILVERIFICATION") && this.entityName.equalsIgnoreCase("SELFCARE");
	}

	public boolean isSelfRegistrationResource() {
		return this.actionName.equalsIgnoreCase("SELFREGISTRATION") && this.entityName.equalsIgnoreCase("ACTIVATE");

	}

	public boolean isIpDetails() {
		return this.entityName.equalsIgnoreCase("IPDETAILPARAMS");
	}

	public boolean isOrderReactive() {
		return this.actionName.equalsIgnoreCase("REACTIVE") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isConfirm() {
		return this.actionName.equalsIgnoreCase("CONFIRM") && this.entityName.equalsIgnoreCase("PROVISIONINGDETAILS");

	}

	public boolean isSelfCareGeneratePassword() {
		return this.actionName.equalsIgnoreCase("GENERATENEWPASSWORD") && this.entityName.equalsIgnoreCase("SELFCARE");
	}

	public boolean isChangePassword() {
		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("SELFCARE");
	}

	public boolean isMediaAssetLocationResource() {
		return this.entityName.equalsIgnoreCase("MEDIAASSETLOCATIONATTRIBUTES");
	}

	public boolean isLocationAttributeMediaAsset() {
		return this.actionName.equalsIgnoreCase("CREATE")
				&& this.entityName.equalsIgnoreCase("MEDIAASSETLOCATIONATTRIBUTES");
	}

	public boolean isProcess() {
		return this.actionName.equalsIgnoreCase("PROCESS") && this.entityName.equalsIgnoreCase("DATAUPLOADS");
	}

	public boolean isDeviceSaleResource() {
		return this.entityName.equalsIgnoreCase("NEWSALE") || this.entityName.equalsIgnoreCase("SECONDSALE")
				|| this.entityName.equalsIgnoreCase("DEVICERENTAL");
	}

	public boolean isProvisionActionResource() {
		return this.entityName.equalsIgnoreCase("PROVISIONACTIONS");
	}

	public boolean isActive() {
		return this.entityName.equalsIgnoreCase("PROVISIONACTIONS") && this.actionName.equalsIgnoreCase("ACTIVE");
	}

	public boolean isPaymentGatewayConfigResource() {
		return this.entityName.equalsIgnoreCase("PAYMENTGATEWAYCONFIG");
	}

	public boolean isOnlinePaymentGateway() {
		return this.actionName.equalsIgnoreCase("ONLINE") && this.entityName.equalsIgnoreCase("PAYMENTGATEWAY");
	}

	public boolean isOrderAddons() {
		return this.entityName.equalsIgnoreCase("ORDERADDONS");
	}

	public boolean isAddons() {
		return this.entityName.equalsIgnoreCase("ADDONS");
	}

	public boolean isLinkupAccount() {
		return this.entityName.equalsIgnoreCase("LINKUPACCOUNT");
	}

	public boolean isPartner() {
		return this.entityName.equalsIgnoreCase("PARTNER");
	}

	public boolean isPartnerAgreement() {

		return this.entityName.equalsIgnoreCase("PARTNERAGREEMENT");
	}

	public boolean isMessageTemplate() {

		return this.entityName.equalsIgnoreCase("TEMPLATE");
	}

	public boolean isRadService() {
		return this.entityName.equalsIgnoreCase("RADSERVICE");
	}

	public boolean isVendorManagement() {
		return this.entityName.equalsIgnoreCase("VENDORMANAGEMENT");
	}

	public boolean isVendorAgreement() {
		return this.entityName.equalsIgnoreCase("VENDORAGREEMENT");

	}

	public boolean isPropertyResource() {
		return this.entityName.equalsIgnoreCase("PROPERTY");

	}

	public boolean isClientAdditionalInfo() {
		return this.entityName.equalsIgnoreCase("CLIENTADDITIONALINFO");

	}

	public boolean isServiceTransferResource() {
		return this.entityName.equalsIgnoreCase("SERVICETRANSFER");

	}

	public boolean isFeeMasterResource() {
		return this.entityName.equalsIgnoreCase("FEEMASTER");
	}

	public boolean isPlanQualifier() {
		return this.entityName.equalsIgnoreCase("PLANQUALIFIER");
	}

	public boolean isStaticIp() {
		return this.entityName.equalsIgnoreCase("STATICIP");
	}

	public boolean isPropertyMasterResource() {
		return this.entityName.equalsIgnoreCase("PROPERTYMASTER");
	}

	public boolean isUpdatePaypalProfile() {
		return this.actionName.equalsIgnoreCase("UPDATEPAYPALPROFILE");
	}

	public boolean isUpdatePaypalProfileStatus() {
		return this.actionName.equalsIgnoreCase("UPDATEPAYPALPROFILESTATUS");
	}

	public boolean isdeleteRecurringBilling() {
		return this.actionName.equalsIgnoreCase("DELETERECURRINGBILLING");
	}

	public boolean isAllocateDevice() {
		return this.actionName.equalsIgnoreCase("ALLOCATEDEVICE");
	}

	public boolean isRefund() {
		return this.entityName.equalsIgnoreCase("REFUND");
	}

	public boolean isDeposit() {
		return this.entityName.equalsIgnoreCase("DEPOSIT");
	}

	public boolean isUsageCharge() {
		return this.entityName.equalsIgnoreCase("CHARGES");
	}

	public boolean isRenewOrderWithClient() {
		return this.actionName.equalsIgnoreCase("RENEWALWITHCLIENT") && this.entityName.equalsIgnoreCase("ORDER");
	}

	public boolean isBeesmartClient() {
		return this.entityName.equalsIgnoreCase("BEESMARTCLIENT");
	}

	public boolean isUpdateBeesmartClient() {

		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("BEESMARTCLIENT");
	}

	public boolean isDeleteBeesmartClient() {

		return this.actionName.equalsIgnoreCase("DELETE") && this.entityName.equalsIgnoreCase("BEESMARTCLIENT");
	}

	public boolean isCreateRevPay() {

		return this.actionName.equalsIgnoreCase("CREATE") && this.entityName.equalsIgnoreCase("REVPAY");
	}

	public boolean islockRevOrder() {

		return this.actionName.equalsIgnoreCase("UPDATE") && this.entityName.equalsIgnoreCase("REVPAY");
	}

	public boolean iscompleteRevOrder() {

		return this.actionName.equalsIgnoreCase("COMPLETE") && this.entityName.equalsIgnoreCase("REVPAY");
	}

	public boolean isVerifyNIN() {

		return this.actionName.equalsIgnoreCase("VERIFY") && this.entityName.equalsIgnoreCase("NIN");
	}

	// command type annotation functionality implemented,please refer once

}