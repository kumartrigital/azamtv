/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.commands.service;

import org.mifosplatform.commands.domain.CommandWrapper;

public class CommandWrapperBuilder {

	private Long officeId;
	private Long groupId;
	private Long clientId;
	private Long loanId;
	private Long savingsId;
	private String actionName;
	private String entityName;
	private Long entityId;
	private Long subentityId;
	private String href;
	private String json = "{}";
	private Long codeId;
	private String transactionId;
	private String supportedEntityType;
	private Long supportedEntityId;
	private Long loginId;

	public CommandWrapper build() {
		return new CommandWrapper(this.officeId, this.groupId, this.clientId, this.loanId, this.savingsId,
				this.actionName, this.entityName, this.entityId, this.subentityId, this.codeId,
				this.supportedEntityType, this.supportedEntityId, this.href, this.json, this.transactionId,
				this.loanId);
	}

	public CommandWrapperBuilder withLoanId(final Long withLoanId) {
		this.loanId = withLoanId;
		return this;
	}

	public CommandWrapperBuilder withClientId(final Long withClientId) {
		this.clientId = withClientId;
		return this;
	}

	public CommandWrapperBuilder withJson(final String withJson) {
		this.json = withJson;
		return this;
	}

	public CommandWrapperBuilder updateConfiguration(Long configId) {
		this.actionName = "UPDATE";
		this.entityName = "CONFIGURATION";
		this.entityId = configId;
		this.href = "/configurations";
		return this;
	}

	public CommandWrapperBuilder updatePermissions() {
		this.actionName = "UPDATE";
		this.entityName = "PERMISSION";
		this.entityId = null;
		this.href = "/permissions";
		return this;
	}

	public CommandWrapperBuilder createRole() {
		this.actionName = "CREATE";
		this.entityName = "ROLE";
		this.href = "/roles/template";
		return this;
	}

	public CommandWrapperBuilder updateRole(final Long roleId) {
		this.actionName = "UPDATE";
		this.entityName = "ROLE";
		this.entityId = roleId;
		this.href = "/roles/" + roleId;
		return this;
	}

	public CommandWrapperBuilder updateRolePermissions(final Long roleId) {
		this.actionName = "PERMISSIONS";
		this.entityName = "ROLE";
		this.entityId = roleId;
		this.href = "/roles/" + roleId + "/permissions";
		return this;
	}

	public CommandWrapperBuilder createUser() {
		this.actionName = "CREATE";
		this.entityName = "USER";
		this.entityId = null;
		this.href = "/users/template";
		return this;
	}

	public CommandWrapperBuilder updateUser(final Long userId) {
		this.actionName = "UPDATE";
		this.entityName = "USER";
		this.entityId = userId;
		this.href = "/users/" + userId;
		return this;
	}

	public CommandWrapperBuilder deleteUser(final Long userId) {
		this.actionName = "DELETE";
		this.entityName = "USER";
		this.entityId = userId;
		this.href = "/users/" + userId;
		return this;
	}
	public CommandWrapperBuilder generateSecret(final Long userId) {
		this.actionName = "UPDATE";
		this.entityName = "USER";
		this.entityId = userId;
		this.href = "/users/" + userId;
		return this;
	}
	
	public CommandWrapperBuilder createOffice() {
		this.actionName = "CREATE";
		this.entityName = "OFFICE";
		this.entityId = null;
		this.href = "/offices/template";
		return this;
	}

	public CommandWrapperBuilder updateOffice(final Long officeId) {
		this.actionName = "UPDATE";
		this.entityName = "OFFICE";
		this.entityId = officeId;
		this.href = "/offices/" + officeId;
		return this;
	}

	public CommandWrapperBuilder createOfficeTransaction() {
		this.actionName = "CREATE";
		this.entityName = "OFFICETRANSACTION";
		this.href = "/officetransactions/template";
		return this;
	}

	public CommandWrapperBuilder deleteOfficeTransaction(final Long transactionId) {
		this.actionName = "DELETE";
		this.entityName = "OFFICETRANSACTION";
		this.entityId = transactionId;
		this.href = "/officetransactions/" + transactionId;
		return this;
	}

	public CommandWrapperBuilder createStaff() {
		this.actionName = "CREATE";
		this.entityName = "STAFF";
		this.entityId = null;
		this.href = "/staff/template";
		return this;
	}

	public CommandWrapperBuilder updateStaff(final Long staffId) {
		this.actionName = "UPDATE";
		this.entityName = "STAFF";
		this.entityId = staffId;
		this.href = "/staff/" + staffId;
		return this;
	}

	public CommandWrapperBuilder createGuarantor(final Long loanId) {
		this.actionName = "CREATE";
		this.entityName = "GUARANTOR";
		this.entityId = null;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/guarantors/template";
		return this;
	}

	public CommandWrapperBuilder updateGuarantor(final Long loanId, final Long guarantorId) {
		this.actionName = "UPDATE";
		this.entityName = "GUARANTOR";
		this.entityId = guarantorId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/guarantors/" + guarantorId;
		return this;
	}

	public CommandWrapperBuilder deleteGuarantor(final Long loanId, final Long guarantorId) {
		this.actionName = "DELETE";
		this.entityName = "GUARANTOR";
		this.entityId = guarantorId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/guarantors/" + guarantorId;
		return this;
	}

	public CommandWrapperBuilder createFund() {
		this.actionName = "CREATE";
		this.entityName = "FUND";
		this.entityId = null;
		this.href = "/funds/template";
		return this;
	}

	public CommandWrapperBuilder updateFund(final Long fundId) {
		this.actionName = "UPDATE";
		this.entityName = "FUND";
		this.entityId = fundId;
		this.href = "/funds/" + fundId;
		return this;
	}

	public CommandWrapperBuilder createReport() {
		this.actionName = "CREATE";
		this.entityName = "REPORT";
		this.entityId = null;
		this.href = "/reports/template";
		return this;
	}

	public CommandWrapperBuilder updateReport(final Long id) {
		this.actionName = "UPDATE";
		this.entityName = "REPORT";
		this.entityId = id;
		this.href = "/reports/" + id;
		return this;
	}

	public CommandWrapperBuilder deleteReport(final Long id) {
		this.actionName = "DELETE";
		this.entityName = "REPORT";
		this.entityId = id;
		this.href = "/reports/" + id;
		return this;
	}

	public CommandWrapperBuilder createCode() {
		this.actionName = "CREATE";
		this.entityName = "CODE";
		this.entityId = null;
		this.href = "/codes/template";
		return this;
	}

	public CommandWrapperBuilder updateCode(final Long codeId) {
		this.actionName = "UPDATE";
		this.entityName = "CODE";
		this.entityId = codeId;
		this.href = "/codes/" + codeId;
		return this;
	}

	public CommandWrapperBuilder deleteCode(final Long codeId) {
		this.actionName = "DELETE";
		this.entityName = "CODE";
		this.entityId = codeId;
		this.href = "/codes/" + codeId;
		return this;
	}

	public CommandWrapperBuilder createCharge() {
		this.actionName = "CREATE";
		this.entityName = "CHARGE";
		this.entityId = null;
		this.href = "/charges/template";
		return this;
	}

	public CommandWrapperBuilder updateCharge(final Long chargeId) {
		this.actionName = "UPDATE";
		this.entityName = "CHARGE";
		this.entityId = chargeId;
		this.href = "/charges/" + chargeId;
		return this;
	}

	public CommandWrapperBuilder deleteCharge(final Long chargeId) {
		this.actionName = "DELETE";
		this.entityName = "CHARGE";
		this.entityId = chargeId;
		this.href = "/charges/" + chargeId;
		return this;
	}

	public CommandWrapperBuilder createLoanProduct() {
		this.actionName = "CREATE";
		this.entityName = "LOANPRODUCT";
		this.entityId = null;
		this.href = "/loanproducts/template";
		return this;
	}

	public CommandWrapperBuilder updateLoanProduct(final Long productId) {
		this.actionName = "UPDATE";
		this.entityName = "LOANPRODUCT";
		this.entityId = productId;
		this.href = "/loanproducts/" + productId;
		return this;
	}

	public CommandWrapperBuilder createClientIdentifier(final Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "CLIENTIDENTIFIER";
		this.entityId = null;
		this.clientId = clientId;
		this.href = "/clients/" + clientId + "/identifiers/template";
		return this;
	}

	public CommandWrapperBuilder updateClientIdentifier(final Long clientId, final Long clientIdentifierId) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENTIDENTIFIER";
		this.entityId = clientIdentifierId;
		this.clientId = clientId;
		this.href = "/clients/" + clientId + "/identifiers/" + clientIdentifierId;
		return this;
	}

	public CommandWrapperBuilder deleteClientIdentifier(final Long clientId, final Long clientIdentifierId,
			final Long fileId) {
		this.actionName = "DELETE";
		this.entityName = "CLIENTIDENTIFIER";
		this.entityId = clientIdentifierId;
		this.subentityId = fileId;
		this.clientId = clientId;
		this.href = "/clients/" + clientId + "/identifiers/" + clientIdentifierId;
		return this;
	}

	public CommandWrapperBuilder createClient() {
		this.actionName = "CREATE";
		this.entityName = "CLIENT";
		this.href = "/clients/template";
		return this;
	}

	public CommandWrapperBuilder activateClient(final Long clientId) {
		this.actionName = "ACTIVATE";
		this.entityName = "CLIENT";
		this.entityId = clientId;
		this.clientId = clientId;
		this.href = "/clients/" + clientId + "?command=activate&template=true";
		return this;
	}

	public CommandWrapperBuilder updateClient(final Long clientId) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENT";
		this.entityId = clientId;
		this.clientId = clientId;
		this.href = "/clients/" + clientId;
		return this;
	}

	public CommandWrapperBuilder deleteClient(final Long clientId) {
		this.actionName = "DELETE";
		this.entityName = "CLIENT";
		this.entityId = clientId;
		this.clientId = clientId;
		this.href = "/clients/" + clientId;
		// this.json = "{}";
		return this;
	}

	public CommandWrapperBuilder createLoanCharge(final Long loanId) {
		this.actionName = "CREATE";
		this.entityName = "LOANCHARGE";
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/charges";
		return this;
	}

	public CommandWrapperBuilder updateLoanCharge(final Long loanId, final Long loanChargeId) {
		this.actionName = "UPDATE";
		this.entityName = "LOANCHARGE";
		this.entityId = loanChargeId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/charges/" + loanChargeId;
		return this;
	}

	public CommandWrapperBuilder waiveLoanCharge(final Long loanId, final Long loanChargeId) {
		this.actionName = "WAIVE";
		this.entityName = "LOANCHARGE";
		this.entityId = loanChargeId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/charges/" + loanChargeId;
		return this;

	}

	public CommandWrapperBuilder deleteLoanCharge(final Long loanId, final Long loanChargeId) {
		this.actionName = "DELETE";
		this.entityName = "LOANCHARGE";
		this.entityId = loanChargeId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/charges/" + loanChargeId;
		return this;
	}

	public CommandWrapperBuilder loanRepaymentTransaction(final Long loanId) {
		this.actionName = "REPAYMENT";
		this.entityName = "LOAN";
		this.entityId = null;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/transactions/template?command=repayment";
		return this;
	}

	public CommandWrapperBuilder waiveInterestPortionTransaction(final Long loanId) {
		this.actionName = "WAIVEINTERESTPORTION";
		this.entityName = "LOAN";
		this.entityId = null;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/transactions/template?command=waiveinterest";
		return this;
	}

	public CommandWrapperBuilder writeOffLoanTransaction(final Long loanId) {
		this.actionName = "WRITEOFF";
		this.entityName = "LOAN";
		this.entityId = null;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/transactions/template?command=writeoff";
		return this;
	}

	public CommandWrapperBuilder closeLoanAsRescheduledTransaction(final Long loanId) {
		this.actionName = "CLOSEASRESCHEDULED";
		this.entityName = "LOAN";
		this.entityId = null;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/transactions/template?command=close-rescheduled";
		return this;
	}

	public CommandWrapperBuilder closeLoanTransaction(final Long loanId) {
		this.actionName = "CLOSE";
		this.entityName = "LOAN";
		this.entityId = null;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/transactions/template?command=close";
		return this;
	}

	public CommandWrapperBuilder adjustTransaction(final Long loanId, final Long transactionId) {
		this.actionName = "ADJUST";
		this.entityName = "LOAN";
		this.entityId = transactionId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/transactions/" + transactionId;
		return this;
	}

	public CommandWrapperBuilder createLoanApplication() {
		this.actionName = "CREATE";
		this.entityName = "LOAN";
		this.entityId = null;
		this.loanId = null;
		this.href = "/loans";
		return this;
	}

	public CommandWrapperBuilder updateLoanApplication(final Long loanId) {
		this.actionName = "UPDATE";
		this.entityName = "LOAN";
		this.entityId = loanId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder deleteLoanApplication(final Long loanId) {
		this.actionName = "DELETE";
		this.entityName = "LOAN";
		this.entityId = loanId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder rejectLoanApplication(final Long loanId) {
		this.actionName = "REJECT";
		this.entityName = "LOAN";
		this.entityId = loanId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder withdrawLoanApplication(final Long loanId) {
		this.actionName = "WITHDRAW";
		this.entityName = "LOAN";
		this.entityId = loanId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder approveLoanApplication(final Long loanId) {
		this.actionName = "APPROVE";
		this.entityName = "LOAN";
		this.entityId = loanId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder disburseLoanApplication(final Long loanId) {
		this.actionName = "DISBURSE";
		this.entityName = "LOAN";
		this.entityId = loanId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder undoLoanApplicationApproval(final Long loanId) {
		this.actionName = "APPROVALUNDO";
		this.entityName = "LOAN";
		this.entityId = loanId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder undoLoanApplicationDisbursal(final Long loanId) {
		this.actionName = "DISBURSALUNDO";
		this.entityName = "LOAN";
		this.entityId = loanId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder assignLoanOfficer(final Long loanId) {
		this.actionName = "UPDATELOANOFFICER";
		this.entityName = "LOAN";
		this.entityId = null;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder unassignLoanOfficer(final Long loanId) {
		this.actionName = "REMOVELOANOFFICER";
		this.entityName = "LOAN";
		this.entityId = null;
		this.loanId = loanId;
		this.href = "/loans/" + loanId;
		return this;
	}

	public CommandWrapperBuilder assignLoanOfficersInBulk() {
		this.actionName = "BULKREASSIGN";
		this.entityName = "LOAN";
		this.href = "/loans/loanreassignment";
		return this;
	}

	public CommandWrapperBuilder createCodeValue(final Long codeId) {
		this.actionName = "CREATE";
		this.entityName = "CODEVALUE";
		this.codeId = codeId;
		this.href = "/codes/" + codeId + "/codevalues/template";
		return this;
	}

	public CommandWrapperBuilder updateCodeValue(final Long codeId, final Long codeValueId) {
		this.actionName = "UPDATE";
		this.entityName = "CODEVALUE";
		this.entityId = codeValueId;
		this.codeId = codeId;
		this.href = "/codes/" + codeId + "/codevalues/" + codeValueId;
		return this;
	}

	public CommandWrapperBuilder deleteCodeValue(final Long codeId, final Long codeValueId) {
		this.actionName = "DELETE";
		this.entityName = "CODEVALUE";
		this.entityId = codeValueId;
		this.codeId = codeId;
		this.href = "/codes/" + codeId + "/codevalues/" + codeValueId;
		return this;
	}

	public CommandWrapperBuilder createGLClosure() {
		this.actionName = "CREATE";
		this.entityName = "GLCLOSURE";
		this.entityId = null;
		this.href = "/glclosures/template";
		return this;
	}

	public CommandWrapperBuilder updateGLClosure(final Long glClosureId) {
		this.actionName = "UPDATE";
		this.entityName = "GLCLOSURE";
		this.entityId = glClosureId;
		this.href = "/glclosures/" + glClosureId;
		return this;
	}

	public CommandWrapperBuilder deleteGLClosure(final Long glClosureId) {
		this.actionName = "DELETE";
		this.entityName = "GLCLOSURE";
		this.entityId = glClosureId;
		this.href = "/glclosures/" + glClosureId;
		return this;
	}

	public CommandWrapperBuilder createGLAccount() {
		this.actionName = "CREATE";
		this.entityName = "GLACCOUNT";
		this.entityId = null;
		this.href = "/glaccounts/template";
		return this;
	}

	public CommandWrapperBuilder updateGLAccount(final Long glAccountId) {
		this.actionName = "UPDATE";
		this.entityName = "GLACCOUNT";
		this.entityId = glAccountId;
		this.href = "/glaccounts/" + glAccountId;
		return this;
	}

	public CommandWrapperBuilder deleteGLAccount(final Long glAccountId) {
		this.actionName = "DELETE";
		this.entityName = "GLACCOUNT";
		this.entityId = glAccountId;
		this.href = "/glaccounts/" + glAccountId;
		return this;
	}

	public CommandWrapperBuilder createJournalEntry() {
		this.actionName = "CREATE";
		this.entityName = "JOURNALENTRY";
		this.entityId = null;
		this.href = "/journalentries/template";
		return this;
	}

	public CommandWrapperBuilder reverseJournalEntry(final String transactionId) {
		this.actionName = "REVERSE";
		this.entityName = "JOURNALENTRY";
		this.entityId = null;
		this.transactionId = transactionId;
		this.href = "/journalentries/" + transactionId;
		return this;
	}

	public CommandWrapperBuilder createSavingProduct() {
		this.actionName = "CREATE";
		this.entityName = "SAVINGSPRODUCT";
		this.entityId = null;
		this.href = "/savingsproducts/template";
		return this;
	}

	public CommandWrapperBuilder updateSavingProduct(final Long productId) {
		this.actionName = "UPDATE";
		this.entityName = "SAVINGSPRODUCT";
		this.entityId = productId;
		this.href = "/savingsproducts/" + productId;
		return this;
	}

	public CommandWrapperBuilder deleteSavingProduct(final Long productId) {
		this.actionName = "DELETE";
		this.entityName = "SAVINGSPRODUCT";
		this.entityId = productId;
		this.href = "/savingsproducts/" + productId;
		return this;
	}

	public CommandWrapperBuilder createSavingsAccount() {
		this.actionName = "CREATE";
		this.entityName = "SAVINGSACCOUNT";
		this.entityId = null;
		this.href = "/savingsaccounts/template";
		return this;
	}

	public CommandWrapperBuilder updateSavingsAccount(final Long accountId) {
		this.actionName = "UPDATE";
		this.entityName = "SAVINGSACCOUNT";
		this.entityId = accountId;
		this.href = "/savingsaccounts/" + accountId;
		return this;
	}

	public CommandWrapperBuilder deleteSavingsAccount(final Long accountId) {
		this.actionName = "DELETE";
		this.entityName = "SAVINGSACCOUNT";
		this.entityId = accountId;
		this.href = "/savingsaccounts/" + accountId;
		return this;
	}

	public CommandWrapperBuilder savingsAccountActivation(final Long accountId) {
		this.actionName = "ACTIVATE";
		this.entityName = "SAVINGSACCOUNT";
		this.savingsId = accountId;
		this.entityId = null;
		this.href = "/savingsaccounts/" + accountId + "?command=activate";
		return this;
	}

	public CommandWrapperBuilder savingsAccountDeposit(final Long accountId) {
		this.actionName = "DEPOSIT";
		this.entityName = "SAVINGSACCOUNT";
		this.savingsId = accountId;
		this.entityId = null;
		this.href = "/savingsaccounts/" + accountId + "/transactions";
		return this;
	}

	public CommandWrapperBuilder savingsAccountWithdrawal(final Long accountId) {
		this.actionName = "WITHDRAWAL";
		this.entityName = "SAVINGSACCOUNT";
		this.savingsId = accountId;
		this.entityId = null;
		this.href = "/savingsaccounts/" + accountId + "/transactions";
		return this;
	}

	public CommandWrapperBuilder savingsAccountInterestCalculation(final Long accountId) {
		this.actionName = "CALCULATEINTEREST";
		this.entityName = "SAVINGSACCOUNT";
		this.savingsId = accountId;
		this.entityId = accountId;
		this.href = "/savingsaccounts/" + accountId + "?command=calculateInterest";
		return this;
	}

	public CommandWrapperBuilder savingsAccountInterestPosting(final Long accountId) {
		this.actionName = "POSTINTEREST";
		this.entityName = "SAVINGSACCOUNT";
		this.savingsId = accountId;
		this.entityId = accountId;
		this.href = "/savingsaccounts/" + accountId + "?command=postInterest";
		return this;
	}

	public CommandWrapperBuilder createCalendar(final String supportedEntityType, final Long supportedEntityId) {
		this.actionName = "CREATE";
		this.entityName = "CALENDAR";
		this.supportedEntityType = supportedEntityType;
		this.supportedEntityId = supportedEntityId;
		this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/calendars/template";
		return this;
	}

	public CommandWrapperBuilder updateCalendar(final String supportedEntityType, final Long supportedEntityId,
			final Long calendarId) {
		this.actionName = "UPDATE";
		this.entityName = "CALENDAR";
		this.entityId = calendarId;
		this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/calendars/" + calendarId;
		return this;
	}

	public CommandWrapperBuilder deleteCalendar(final String supportedEntityType, final Long supportedEntityId,
			final Long calendarId) {
		this.actionName = "DELETE";
		this.entityName = "CALENDAR";
		this.entityId = calendarId;
		this.href = "/" + supportedEntityType + "/" + supportedEntityId + "/calendars/" + calendarId;
		return this;
	}

	public CommandWrapperBuilder createNote(final String entityName, final String resourceType, final Long resourceId) {
		this.actionName = "CREATE";
		this.entityName = entityName;// Note supports multiple resources. Note
		// Permissions are set for each resource.
		this.supportedEntityType = resourceType;
		this.supportedEntityId = resourceId;
		this.href = "/" + resourceType + "/" + resourceId + "/notes/template";
		return this;
	}

	public CommandWrapperBuilder updateNote(final String entityName, final String resourceType, final Long resourceId,
			final Long noteId) {
		this.actionName = "UPDATE";
		this.entityName = entityName;// Note supports multiple resources. Note
		// Permissions are set for each resource.
		this.entityId = noteId;
		this.supportedEntityType = resourceType;
		this.supportedEntityId = resourceId;
		this.href = "/" + resourceType + "/" + resourceId + "/notes";
		return this;
	}

	public CommandWrapperBuilder deleteNote(final String entityName, final String resourceType, final Long resourceId,
			final Long noteId) {
		this.actionName = "DELETE";
		this.entityName = entityName;// Note supports multiple resources. Note
		// Permissions are set for each resource.
		this.entityId = noteId;
		this.supportedEntityType = resourceType;
		this.supportedEntityId = resourceId;
		this.href = "/" + resourceType + "/" + resourceId + "/calendars/" + noteId;
		return this;
	}

	public CommandWrapperBuilder createGroup() {
		this.actionName = "CREATE";
		this.entityName = "GROUP";
		this.href = "/groups/template";
		return this;
	}

	public CommandWrapperBuilder updateGroup(final Long groupId) {
		this.actionName = "UPDATE";
		this.entityName = "GROUP";
		this.entityId = groupId;
		this.groupId = groupId;
		this.href = "/groups/" + groupId;
		return this;
	}

	public CommandWrapperBuilder activateGroup(final Long groupId) {
		this.actionName = "ACTIVATE";
		this.entityName = "GROUP";
		this.entityId = groupId;
		this.groupId = groupId;
		this.href = "/groups/" + groupId + "?command=activate";
		return this;
	}

	public CommandWrapperBuilder deleteGroup(final Long groupId) {
		this.actionName = "DELETE";
		this.entityName = "GROUP";
		this.entityId = groupId;
		this.groupId = groupId;
		this.href = "/groups/" + groupId;
		return this;
	}

	public CommandWrapperBuilder unassignStaff(final Long groupId) {
		this.actionName = "UNASSIGNSTAFF";
		this.entityName = "GROUP";
		this.entityId = groupId;
		this.groupId = groupId;
		this.href = "/groups/" + groupId;
		return this;
	}

	public CommandWrapperBuilder createCollateral(final Long loanId) {
		this.actionName = "CREATE";
		this.entityName = "COLLATERAL";
		this.entityId = null;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/collaterals/template";
		return this;
	}

	public CommandWrapperBuilder updateCollateral(final Long loanId, final Long collateralId) {
		this.actionName = "UPDATE";
		this.entityName = "COLLATERAL";
		this.entityId = collateralId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/collaterals/" + collateralId;
		return this;
	}

	public CommandWrapperBuilder deleteCollateral(final Long loanId, final Long collateralId) {
		this.actionName = "DELETE";
		this.entityName = "COLLATERAL";
		this.entityId = collateralId;
		this.loanId = loanId;
		this.href = "/loans/" + loanId + "/collaterals/" + collateralId;
		return this;
	}

	public CommandWrapperBuilder updateCollectionSheet(final Long groupId) {
		this.actionName = "UPDATE";
		this.entityName = "COLLECTIONSHEET";
		this.entityId = groupId;
		this.href = "/groups/" + groupId + "/collectionsheet";
		return this;
	}

	public CommandWrapperBuilder createCenter() {
		this.actionName = "CREATE";
		this.entityName = "CENTER";
		this.href = "/centers/template";
		return this;
	}

	public CommandWrapperBuilder updateCenter(final Long centerId) {
		this.actionName = "UPDATE";
		this.entityName = "CENTER";
		this.entityId = centerId;
		this.href = "/centers/" + centerId;
		return this;
	}

	public CommandWrapperBuilder deleteCenter(final Long centerId) {
		this.actionName = "DELETE";
		this.entityName = "CENTER";
		this.entityId = centerId;
		this.href = "/centers/" + centerId;
		return this;
	}

	public CommandWrapperBuilder activateCenter(final Long centerId) {
		this.actionName = "ACTIVATE";
		this.entityName = "CENTER";
		this.entityId = centerId;
		this.groupId = centerId;
		this.href = "/centers/" + centerId + "?command=activate";
		return this;
	}

	public CommandWrapperBuilder createAccountingRule() {
		this.actionName = "CREATE";
		this.entityName = "ACCOUNTINGRULE";
		this.entityId = null;
		this.href = "/accountingrules/template";
		return this;
	}

	public CommandWrapperBuilder updateAccountingRule(final Long accountingRuleId) {
		this.actionName = "UPDATE";
		this.entityName = "ACCOUNTINGRULE";
		this.entityId = accountingRuleId;
		this.href = "/accountingrules/" + accountingRuleId;
		return this;
	}

	public CommandWrapperBuilder deleteAccountingRule(final Long accountingRuleId) {
		this.actionName = "DELETE";
		this.entityName = "ACCOUNTINGRULE";
		this.entityId = accountingRuleId;
		this.href = "/accountingrules/" + accountingRuleId;
		return this;
	}

	public CommandWrapperBuilder createService() {
		this.actionName = "CREATE";
		this.entityName = "SERVICE";
		this.entityId = null;
		this.href = "/services/template";
		return this;
	}

	public CommandWrapperBuilder updateService(final Long serviceId) {
		this.actionName = "UPDATE";
		this.entityName = "SERVICE";
		this.entityId = serviceId;
		this.href = "/servicemasters/" + serviceId;
		return this;
	}

	public CommandWrapperBuilder deleteService(final Long serviceId) {
		this.actionName = "DELETE";
		this.entityName = "SERVICE";
		this.entityId = serviceId;
		this.href = "/servicemasters/" + serviceId;
		return this;
	}

	public CommandWrapperBuilder createContract() {
		this.actionName = "CREATE";
		this.entityName = "CONTRACT";
		this.entityId = null;
		this.href = "/subscriptions/template";
		return this;
	}

	public CommandWrapperBuilder updateContract(final Long contractId) {
		this.actionName = "UPDATE";
		this.entityName = "CONTRACT";
		this.entityId = contractId;
		this.href = "/subscriptions/" + contractId;
		return this;
	}

	public CommandWrapperBuilder deleteContract(final Long contractId) {
		this.actionName = "DELETE";
		this.entityName = "CONTRACT";
		this.entityId = contractId;
		this.href = "/subscriptions/" + contractId;
		return this;
	}

	public CommandWrapperBuilder createPlan() {
		this.actionName = "CREATE";
		this.entityName = "PLAN";
		this.entityId = null;
		this.href = "/plans/template";
		return this;
	}

	public CommandWrapperBuilder updatePlan(final Long planId) {
		this.actionName = "UPDATE";
		this.entityName = "PLAN";
		this.entityId = planId;
		this.href = "/plans/" + planId;
		return this;
	}

	public CommandWrapperBuilder deletePlan(final Long planId) {
		this.actionName = "DELETE";
		this.entityName = "PLAN";
		this.entityId = planId;
		this.href = "/plans/" + planId;
		return this;
	}

	public CommandWrapperBuilder createPrice(Long planId) {
		this.actionName = "CREATE";
		this.entityName = "PRICE";
		this.entityId = planId;
		this.href = "/prices/template";
		return this;
	}

	public CommandWrapperBuilder updatePrice(final Long priceId) {
		this.actionName = "UPDATE";
		this.entityName = "PRICE";
		this.entityId = priceId;
		this.href = "/prices/" + priceId;
		return this;
	}

	public CommandWrapperBuilder deletePrice(final Long priceId) {
		this.actionName = "DELETE";
		this.entityName = "PRICE";
		this.entityId = priceId;
		this.href = "/prices/" + priceId;
		return this;
	}

	public CommandWrapperBuilder createOrder(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "ORDER";
		this.entityId = clientId;
		this.href = "/orders/template";
		return this;
	}

	public CommandWrapperBuilder updateOrderPrice(final Long orderId) {
		this.actionName = "UPDATE";
		this.entityName = "ORDERPRICE";
		this.entityId = orderId;
		this.href = "/orders/" + orderId;
		return this;
	}

	public CommandWrapperBuilder deleteOrder(final Long orderId) {
		this.actionName = "DELETE";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "/prices/" + orderId;
		return this;
	}

	public CommandWrapperBuilder createoneTimeSale(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "ONETIMESALE";
		this.entityId = clientId;
		this.href = "/onetimesale/template";
		return this;
	}

	public CommandWrapperBuilder updateOneTimeSale(final Long saleId) {
		this.actionName = "UPDATE";
		this.entityName = "ONETIMESALE";
		this.entityId = saleId;
		this.href = "/onetimesale/" + saleId;
		return this;
	}

	public CommandWrapperBuilder cancelOneTimeSale(final Long saleId) {
		this.actionName = "DELETE";
		this.entityName = "ONETIMESALE";
		this.entityId = saleId;
		this.href = "/onetimesale/" + saleId;
		return this;
	}

	public CommandWrapperBuilder createAddress(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "Address";
		this.entityId = clientId;
		this.href = "/address/template";
		return this;
	}

	public CommandWrapperBuilder updateAddress(final Long clientId) {
		this.actionName = "UPDATE";
		this.entityName = "ADDRESS";
		this.entityId = clientId;
		this.href = "/address/" + clientId;
		return this;
	}

	public CommandWrapperBuilder deleteAddress(final Long addrId) {
		this.actionName = "DELETE";
		this.entityName = "ADDRESS";
		this.entityId = addrId;
		this.href = "/address/" + addrId;
		return this;
	}

	public CommandWrapperBuilder createItem() {
		this.actionName = "CREATE";
		this.entityName = "ITEM";
		this.entityId = null;
		this.href = "/items/template";
		return this;
	}

	public CommandWrapperBuilder updateItem(Long itemId) {
		this.actionName = "UPDATE";
		this.entityName = "ITEM";
		this.entityId = itemId;
		this.href = "/items/" + itemId;
		return this;
	}

	public CommandWrapperBuilder deleteItem(Long itemId) {
		this.actionName = "DELETE";
		this.entityName = "ITEM";
		this.entityId = itemId;
		this.href = "/items/" + itemId;
		return this;
	}

	public CommandWrapperBuilder createCharge(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "INVOICE";
		this.entityId = clientId;
		this.href = "/billingorder/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createAdjustment(final Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "ADJUSTMENT";
		this.entityId = clientId;
		this.href = "/adjustments/" + clientId;
		return this;
	}
	public CommandWrapperBuilder createAdjustmentjvTransaction() {
		this.actionName = "CREATE";
		this.entityName = "ADJUSTMENT";
		this.entityId = clientId;
		this.href = "/adjustments/jvtransaction";
		return this;
	}

	public CommandWrapperBuilder updateAdjustment(final Long adjustmentId) {
		this.actionName = "UPDATE";
		this.entityName = "ADJUSTMENT";
		this.entityId = adjustmentId;
		this.href = "/adjustments/" + adjustmentId;
		return this;
	}

	public CommandWrapperBuilder createPayment(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "PAYMENT";
		this.entityId = clientId;
		this.href = "/payments/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createPaymode() {
		this.actionName = "CREATE";
		this.entityName = "PAYMODE";
		this.entityId = null;
		this.href = "/paymodes";
		return this;
	}

	public CommandWrapperBuilder updatePaymode(Long paymodeId) {
		this.actionName = "UPDATE";
		this.entityName = "PAYMODE";
		this.entityId = paymodeId;
		this.href = "/paymodes/" + paymodeId;
		return this;
	}

	public CommandWrapperBuilder deletePaymode(Long paymodeId) {
		this.actionName = "DELETE";
		this.entityName = "PAYMODE";
		this.entityId = paymodeId;
		this.href = "/paymodes/" + paymodeId;
		return this;
	}

	public CommandWrapperBuilder createStatement(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "BILLMASTER";
		this.entityId = clientId;
		this.href = "/billmaster/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createOneTimeSale(Long clientId, String devicesaleTpye) {
		this.actionName = "CREATE";
		this.entityName = devicesaleTpye;
		this.entityId = clientId;
		this.href = "/onetimesale/template";
		return this;
	}
	
	public CommandWrapperBuilder createOneTimeSaleNonserailizedItem(Long clientId, String devicesaleTpye) {
		this.actionName = "CREATE";
		this.entityName = devicesaleTpye;
		this.entityId = clientId;
		this.href = "/onetimesale/NonSerialItem";
		return this;
	}

	public CommandWrapperBuilder calculatePrice(Long itemId) {
		this.actionName = "CREATE";
		this.entityName = "ONETIMESALE";
		this.entityId = itemId;
		this.href = "/onetimesale/template";
		return this;
	}

	public CommandWrapperBuilder createInventoryItem(final Long flag) {
		this.actionName = "CREATE";
		this.entityName = "INVENTORY";
		this.entityId = flag;
		this.href = "/itemdetails/template";
		return this;
	}

	public CommandWrapperBuilder createUploadStatus() {
		this.actionName = "CREATE";
		this.entityName = "UPLOADSTATUS";

		this.entityId = null;
		this.href = "/uploadstatus/template";
		return this;
	}

	public CommandWrapperBuilder createTicketMaster(final Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "TICKET";
		this.clientId = clientId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder deleteTicketMaster(final Long ticketId) {
		this.actionName = "CLOSE";
		this.entityName = "TICKET";
		this.entityId = ticketId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder createEventMaster() {
		this.entityName = "EVENT";
		this.actionName = "CREATE";
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder updateEventMaster(Long eventId) {
		this.entityName = "EVENT";
		this.actionName = "UPDATE";
		this.entityId = eventId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder deleteEventMaster(Long eventId) {
		this.entityName = "EVENT";
		this.actionName = "DELETE";
		this.entityId = eventId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder createEventPrice(Long eventId) {
		this.actionName = "CREATE";

		this.entityName = "EVENTPRICE";
		this.entityId = eventId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder updateEventPrice(Long eventPriceId) {
		this.actionName = "UPDATE";
		this.entityName = "EVENTPRICE";
		this.entityId = eventPriceId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder deleteEventPrice(Long eventPriceId) {
		this.actionName = "DELETE";
		this.entityName = "EVENTPRICE";
		this.entityId = eventPriceId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder allocateHardware(final Long id) {
		this.actionName = "CREATE";
		this.entityName = "ALLOCATION";
		this.entityId = id;
		this.href = "/itemdetails/allocation";
		return this;
	}

	public CommandWrapperBuilder allocateHardware() {
		this.actionName = "CREATE";
		this.entityName = "ALLOCATION";
		this.entityId = null;
		this.href = "itemdetails/allocation";
		return this;
	}

	public CommandWrapperBuilder createGrn() {
		this.actionName = "CREATE";
		this.entityName = "GRN";
		this.entityId = null;
		this.href = "itemdetails/addgrn";
		return this;
	}

	public CommandWrapperBuilder createEventOrder(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "EVENTORDER";
		this.entityId = clientId;
		this.href = "eventorder/template";
		return this;
	}

	public CommandWrapperBuilder createMediaAsset() {
		this.actionName = "CREATE";
		this.entityName = "MEDIAASSET";
		this.entityId = null;
		this.href = "mediaasset/template";
		return this;
	}

	public CommandWrapperBuilder updateOrder(Long orderId) {
		this.actionName = "UPDATE";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "order/disconnect";
		return this;
	}

	public CommandWrapperBuilder renewalOrder(Long orderId) {
		this.actionName = "RENEWAL";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "order/renewal";
		return this;
	}
	
	

	public CommandWrapperBuilder topUp(Long orderId) {
		this.actionName = "TOPUP";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "order/topup";
		return this;
	}

	public CommandWrapperBuilder TOVDtopUp() {
		this.actionName = "TOVDTOPUP";
		this.entityName = "ORDER";
		this.entityId = null;
		this.href = "order/topup";
		return this;
	}

	public CommandWrapperBuilder orderUssd(Long orderId, String transactionId) {
		this.actionName = "ORDERUSSD";
		this.entityName = "ORDER";
		this.transactionId = transactionId;
		this.entityId = orderId;
		this.href = "order/{orderId}/acquire/{referenceId}";
		return this;
	}

	public CommandWrapperBuilder reconnectOrder(final Long orderId) {
		this.actionName = "RECONNECT";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "order/reconnect";
		return this;
	}

	public CommandWrapperBuilder createChargeCode() {
		this.actionName = "CREATE";
		this.entityName = "CHARGECODE";
		this.entityId = null;
		this.href = "chargecode";
		return this;
	}

	public CommandWrapperBuilder createTaxMap(String chargeCode) {
		this.actionName = "CREATE";
		this.entityName = "TAXMAPPING";
		this.supportedEntityType = chargeCode;
		this.href = "taxmap/addtaxmap";
		return this;
	}

	public CommandWrapperBuilder updateChargeCode(final Long chargeCodeId) {
		this.actionName = "UPDATE";
		this.entityName = "CHARGECODE";
		this.entityId = chargeCodeId;
		this.href = "chargecode/" + chargeCodeId;
		return this;
	}

	public CommandWrapperBuilder updateTaxMap(final Long taxMapId) {
		this.actionName = "UPDATE";
		this.entityName = "TAXMAPPING";
		this.entityId = taxMapId;
		this.href = "taxmap/" + taxMapId;
		return this;
	}

	public CommandWrapperBuilder createBillingMessage() {
		this.actionName = "CREATE";
		this.entityName = "BILLINGMESSAGE";
		this.entityId = null;
		this.href = "/billingMessage";
		return this;
	}

	public CommandWrapperBuilder createMessageData(Long clientId) {
		this.actionName = "CREATEDATA";
		this.entityName = "BILLINGMESSAGE";
		this.entityId = clientId;
		this.href = "/billingMessage/" + entityId;
		return this;
	}

	public CommandWrapperBuilder updateBillingMessage(Long clientId) {
		this.actionName = "UPDATE";
		this.entityName = "BILLINGMESSAGE";
		this.entityId = clientId;
		this.href = "/billingMessage/" + entityId;
		return this;
	}

	public CommandWrapperBuilder createBatch() {
		this.actionName = "CREATE";
		this.entityName = "BATCH";
		this.entityId = null;
		this.href = "/batchs";
		return this;
	}

	public CommandWrapperBuilder createSchedule() {
		this.actionName = "CREATE";
		this.entityName = "SCHEDULE";
		this.entityId = null;
		this.href = "/jobschedules";
		return this;
	}

	public CommandWrapperBuilder deleteBillingMessage(Long messageId) {

		// TODO Auto-generated method stub
		this.actionName = "DELETE";
		this.entityName = "BILLINGMESSAGE";
		this.entityId = messageId;
		this.href = "/message/" + entityId;
		return this;
	}

	public CommandWrapperBuilder createProspect() {

		this.actionName = "CREATE";
		this.entityName = "PROSPECT";
		this.entityId = null;
		this.href = "/prospects";
		return this;
	}

	public CommandWrapperBuilder followUpProspect(final Long prospectId) {
		this.actionName = "FOLLOWUP";
		this.entityName = "PROSPECT";
		this.entityId = prospectId;
		this.href = "/prospects/" + prospectId;
		return this;
	}

	public CommandWrapperBuilder deleteProspect(final Long deleteProspectId) {

		this.actionName = "DELETE";
		this.entityName = "PROSPECT";
		this.entityId = deleteProspectId;
		this.href = "/prospects/" + deleteProspectId;
		return this;
	}

	public CommandWrapperBuilder convertProspectToClient(final Long prospectId) {

		this.actionName = "CONVERT";
		this.entityName = "PROSPECT";
		this.entityId = prospectId;
		this.href = "/prospect/converttoclient/" + prospectId;
		return this;
	}

	public CommandWrapperBuilder createOwnedHardware(final Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "OWNEDHARDWARE";
		this.entityId = clientId;
		this.href = "/ownedhardware";
		return this;
	}

	public CommandWrapperBuilder updateOwnedHardware(final Long id) {
		this.actionName = "UPDATE";
		this.entityName = "OWNEDHARDWARE";
		this.entityId = id;
		this.href = "/ownedhardware";
		return this;
	}

	public CommandWrapperBuilder deleteOwnHardware(final Long id) {
		this.actionName = "DELETE";
		this.entityName = "OWNEDHARDWARE";
		this.entityId = id;
		this.href = "/ownedhardware";
		return this;
	}

	public CommandWrapperBuilder createCountryCurrency() {
		this.actionName = "CREATE";
		this.entityName = "COUNTRYCURRENCY";
		this.href = "/countrycurrency";
		return this;
	}

	public CommandWrapperBuilder createEpg() {

		this.actionName = "CREATE";
		this.entityName = "EPGPROGRAMGUIDE";
		this.entityId = null;
		this.href = "/epgprogramguide";
		return this;
	}

	public CommandWrapperBuilder updateMediaAsset(Long assetId) {
		this.actionName = "UPDATE";
		this.entityName = "MEDIAASSET";
		this.entityId = assetId;
		this.href = "assets/" + entityId;
		return this;
	}

	public CommandWrapperBuilder deleteMediaAsset(Long assetId) {
		this.actionName = "DELETE";
		this.entityName = "MEDIAASSET";
		this.entityId = assetId;
		this.href = "assets/" + entityId;
		return this;
	}

	public CommandWrapperBuilder updateCountryCurrency(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "COUNTRYCURRENCY";
		this.entityId = id;
		this.href = "countrycurrency/" + entityId;
		return this;
	}

	public CommandWrapperBuilder deleteCountryCurrency(Long id) {
		this.actionName = "DELETE";
		this.entityName = "COUNTRYCURRENCY";
		this.entityId = id;
		this.href = "countrycurrency/" + entityId;
		return this;
	}

	public CommandWrapperBuilder createRegion() {
		this.actionName = "CREATE";
		this.entityName = "REGION";
		this.entityId = null;
		this.href = "regions/template";
		return this;
	}

	public CommandWrapperBuilder createSelfCare() {
		this.actionName = "CREATE";
		this.entityName = "SELFCARE";
		this.entityId = null;
		this.href = "/selfcare";
		return this;
	}

	public CommandWrapperBuilder createSelfCareUDP() {
		this.actionName = "CREATE";
		this.entityName = "SELFCAREUDP";
		this.entityId = null;
		this.href = "/selfcare";
		return this;
	}

	public CommandWrapperBuilder updateRegion(Long regionId) {
		this.actionName = "UPDATE";
		this.entityName = "REGION";
		this.entityId = regionId;
		this.href = "regions/" + regionId;
		return this;
	}

	public CommandWrapperBuilder deleteregion(Long regionId) {
		this.actionName = "DELETE";
		this.entityName = "REGION";
		this.entityId = regionId;
		this.href = "region/" + entityId;
		return this;
	}

	public CommandWrapperBuilder createDiscount() {
		this.actionName = "CREATE";
		this.entityName = "DISCOUNT";
		this.entityId = null;
		this.href = "/discount";
		return this;
	}

	public CommandWrapperBuilder updateDiscount(Long discountId) {
		this.actionName = "UPDATE";
		this.entityName = "DISCOUNT";
		this.entityId = discountId;
		this.href = "/discount";
		return this;
	}

	public CommandWrapperBuilder deleteDiscount(Long discountId) {
		this.actionName = "DELETE";
		this.entityName = "DISCOUNT";
		this.entityId = discountId;
		this.href = "/discount";
		return this;
	}

	public CommandWrapperBuilder createMRN() {
		this.actionName = "CREATE";
		this.entityName = "MRN";
		this.entityId = null;
		this.href = "/mrndetails";
		return this;
	}

	public CommandWrapperBuilder moveMRN() {
		this.actionName = "MOVE";
		this.entityName = "MRN";
		this.entityId = null;
		this.href = "/mrndetails/movemrn/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createSupplier() {
		this.actionName = "CREATE";
		this.entityName = "SUPPLIER";
		this.entityId = null;
		this.href = "/supplier";
		return this;
	}

	public CommandWrapperBuilder createVoucherGroup() {
		this.actionName = "CREATE";
		this.entityName = "VOUCHER";
		this.href = "/vouchers";
		return this;

	}

	public CommandWrapperBuilder updateJobDetail(final Long jobId) {
		this.actionName = "UPDATE";
		this.entityName = "SCHEDULER";
		this.entityId = jobId;
		this.href = "/updateJobDetail/" + jobId + "/updateJobDetail";
		return this;
	}

	public CommandWrapperBuilder addNewJob() {
		this.actionName = "CREATE";
		this.entityName = "SCHEDULER";
		this.href = "/job";
		return this;
	}

	public CommandWrapperBuilder deleteJob(Long jobId) {
		this.actionName = "DELETE";
		this.entityName = "SCHEDULER";
		this.entityId = jobId;
		this.href = "/job/" + jobId;
		return this;
	}

	public CommandWrapperBuilder createEntitlement(Long id) {

		this.actionName = "CREATE";
		this.entityName = "ENTITLEMENT";
		this.entityId = id;
		this.href = "/entitlements";
		return this;

	}

	public CommandWrapperBuilder createEpgXsls(Long i) {

		this.actionName = "CREATE";
		this.entityName = "EPGPROGRAMGUIDE";
		this.entityId = i;
		this.href = "/epgprogramguide";
		return this;
	}

	public CommandWrapperBuilder updateJobParametersDetail(Long jobId) {
		this.actionName = "UPDATE";
		this.entityName = "SCHEDULERJOBPARAMETER";
		this.entityId = jobId;
		this.href = "/job/" + jobId + "/jobparameters";
		return this;
	}

	public CommandWrapperBuilder createBalance() {
		this.actionName = "CREATE";
		this.entityName = "BALANCE";
		this.href = "/clientBalance";
		return this;
	}

	public CommandWrapperBuilder updateProspect(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "PROSPECT";
		this.entityId = id;
		this.href = "/prospects/edit/" + id;
		return this;
	}

	public CommandWrapperBuilder retrackOsdmessage(final Long clientServiceId) {

		this.actionName = "RETRACKOSDMESSAGE";
		this.entityName = "ORDER";
		this.entityId = clientServiceId;
		this.href = "order/retrackOsdmessage";
		return this;
	}

	public CommandWrapperBuilder updatePlanMapping(Long planMapId) {

		this.actionName = "UPDATE";
		this.entityName = "PLANMAPPING";
		this.entityId = planMapId;
		this.href = "planmapping/" + planMapId;
		return this;
	}

	public CommandWrapperBuilder createServiceMapping() {
		this.actionName = "CREATE";
		this.entityName = "SERVICEMAPPING";
		this.entityId = null;
		this.href = "servicemapping";
		return this;
	}

	public CommandWrapperBuilder updateServiceMapping(Long serviceMapId) {
		this.actionName = "UPDATE";
		this.entityName = "SERVICEMAPPING";
		this.entityId = serviceMapId;
		this.href = "servicemapping/" + serviceMapId;
		return this;
	}

	public CommandWrapperBuilder createAssociation(Long clientId) {
		// TODO Auto-generated method stub
		this.actionName = "CREATE";
		this.entityName = "ASSOCIATION";
		this.entityId = clientId;
		this.href = "associations/" + clientId;
		return this;
	}

	public CommandWrapperBuilder updateAssociation(Long id) {
		// TODO Auto-generated method stub
		this.actionName = "UPDATE";
		this.entityName = "ASSOCIATION";
		this.entityId = id;
		this.href = "associations/" + id + "/update/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createHardwarePlan() {
		this.actionName = "CREATE";
		this.entityName = "PLANMAPPING";
		this.entityId = null;
		this.href = "/hardwareplans/template";
		return this;
	}

	public CommandWrapperBuilder updateDeAssociation(Long associationId) {

		this.actionName = "DEASSOCIATION";
		this.entityName = "ASSOCIATION";
		this.entityId = associationId;
		this.href = "associations/deassociations/" + associationId;
		return this;
	}

	public CommandWrapperBuilder createPaymentGateway() {

		this.actionName = "CREATE";
		this.entityName = "PAYMENTGATEWAY";
		this.entityId = null;
		this.href = "/paymentgateways";
		return this;
	}

	public CommandWrapperBuilder updateEventOrderPrice() {
		this.actionName = "UPDATE";
		this.entityName = "EVENTORDER";
		this.entityId = clientId;
		this.href = "eventorder";
		return this;
	}

	public CommandWrapperBuilder hardwareSwapping(Long clientId) {
		this.actionName = "SWAPPING";
		this.entityName = "HARDWARESWAPPING";
		this.entityId = clientId;
		this.href = "/hardwaremapping/" + clientId;
		return this;
	}

	public CommandWrapperBuilder activateProcess() {
		this.actionName = "ACTIVATE";
		this.entityName = "ACTIVATIONPROCESS";
		this.href = "/clients/template";
		return this;

	}

	public CommandWrapperBuilder createSimpleActivation() {
		this.actionName = "CREATE";
		this.entityName = "SIMPLEACTIVATION";
		this.href = "/activationprocess/simpleactivation";
		return this;

	}

	public CommandWrapperBuilder provisiongSystem() {
		this.actionName = "CREATE";
		this.entityName = "PROVISIONINGSYSTEM";
		this.href = "/provisionings";
		return this;
	}

	public CommandWrapperBuilder updateprovisiongSystem(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "PROVISIONINGSYSTEM";
		this.entityId = id;
		this.href = "/provisionings/" + id;
		return this;
	}

	public CommandWrapperBuilder deleteProvisiongSystem(Long id) {
		this.actionName = "DELETE";
		this.entityName = "PROVISIONINGSYSTEM";
		this.entityId = id;
		this.href = "/provisionings/" + id;
		return this;
	}

	public CommandWrapperBuilder createUserChat() {

		this.actionName = "CREATE";
		this.entityName = "USERCHATMESSAGE";
		this.entityId = null;
		this.href = "/userchat/";
		return this;
	}

	public CommandWrapperBuilder createEventActionMapping() {
		this.actionName = "CREATE";
		this.entityName = "EVENTACTIONMAP";
		this.entityId = null;
		this.href = "/eventactionmapping";
		return this;
	}

	public CommandWrapperBuilder updateEventActionMapping(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "EVENTACTIONMAP";
		this.entityId = id;
		this.href = "/eventactionmapping";
		return this;
	}

	public CommandWrapperBuilder deleteEventActionMapping(Long id) {
		this.actionName = "DELETE";
		this.entityName = "EVENTACTIONMAP";
		this.entityId = id;
		this.href = "/eventactionmapping";
		return this;
	}

	public CommandWrapperBuilder updatePaymentGateway(Long id) {

		this.actionName = "UPDATE";
		this.entityName = "PAYMENTGATEWAY";
		this.entityId = id;
		this.href = "/paymentgateways/" + this.entityId;
		return this;
	}

	public CommandWrapperBuilder cancelPayment(final Long paymentId) {

		this.actionName = "CANCEL";
		this.entityName = "PAYMENT";
		this.entityId = paymentId;
		this.href = "/payments/cancelpayment/" + this.entityId;
		return this;

	}

	public CommandWrapperBuilder changePlan(Long orderId) {

		this.actionName = "CHANGEPLAN";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "orders/changePlan" + orderId;

		return this;
	}

	public CommandWrapperBuilder updateInventoryItem(final Long id) {
		this.actionName = "UPDATE";
		this.entityName = "INVENTORY";
		this.entityId = id;
		this.href = "/itemdetails/template";
		return this;
	}

	public CommandWrapperBuilder deleteInventoryItem(final Long id) {
		this.actionName = "DELETE";
		this.entityName = "INVENTORY";
		this.entityId = id;
		this.href = "/itemdetails" + id;
		return this;
	}

	public CommandWrapperBuilder createPromotionCode() {
		this.actionName = "CREATE";
		this.entityName = "PROMOTIONCODE";
		this.entityId = null;
		this.href = "/promotioncode";
		return this;
	}

	public CommandWrapperBuilder applyPromo(Long orderId) {
		this.actionName = "APPLYPROMO";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "/orders/applyPromo";
		return this;
	}

	public CommandWrapperBuilder deAllocate(Long id) {
		this.actionName = "DEALLOCATE";
		this.entityName = "INVENTORY";
		this.entityId = id;
		this.href = "/itemdetails/template";
		return this;
	}

	public CommandWrapperBuilder updatePromotionCode(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "PROMOTIONCODE";
		this.entityId = id;
		this.href = "/promotioncode";
		return this;
	}

	public CommandWrapperBuilder deletePromotionCode(Long id) {
		this.actionName = "DELETE";
		this.entityName = "PROMOTIONCODE";
		this.entityId = id;
		this.href = "/promotioncode";
		return this;
	}

	public CommandWrapperBuilder updateUsermessage(Long meesageId) {

		this.actionName = "UPDATE";
		this.entityName = "USERCHATMESSAGE";
		this.entityId = meesageId;
		this.href = "/userchats/" + this.entityId;
		return this;
	}

	public CommandWrapperBuilder deleteUserChatmessage(Long meesageId) {

		this.actionName = "DELETE";
		this.entityName = "USERCHATMESSAGE";
		this.entityId = meesageId;
		this.href = "/userchats/" + this.entityId;
		return this;
	}

	public CommandWrapperBuilder createLocation(final String entityType) {
		this.actionName = "CREATE";
		this.entityName = "LOCATION";
		this.supportedEntityType = entityType;
		this.href = "/address/" + entityType;
		return this;
	}

	public CommandWrapperBuilder updateLocation(final String entityType, Long entityId) {
		this.actionName = "UPDATE";
		this.entityName = "LOCATION";
		this.entityId = entityId;
		this.supportedEntityType = entityType;
		this.href = "/address/" + entityType + "/" + entityId;
		return this;
	}

	public CommandWrapperBuilder deleteLocation(final String entityType, Long entityId) {
		this.actionName = "DELETE";
		this.entityName = "LOCATION";
		this.entityId = entityId;
		this.supportedEntityType = entityType;
		this.href = "/address/" + entityType + "/" + entityId;
		return this;
	}

	public CommandWrapperBuilder updateCache() {
		this.actionName = "UPDATE";
		this.entityName = "CACHE";
		this.href = "/cache";
		return this;
	}

	public CommandWrapperBuilder createCreditDistribution(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "CREDITDISTRIBUTION";
		this.href = "/creditdistribution";
		return this;
	}

	public CommandWrapperBuilder createSchedulingOrder(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "ORDERSCHEDULING";
		this.entityId = clientId;
		this.href = "/orders/scheduling" + clientId;
		return this;
	}

	public CommandWrapperBuilder updateSchedulingOrder(Long orderId) {
		this.actionName = "UPDATE";
		this.entityName = "ORDERSCHEDULING";
		this.entityId = orderId;
		this.href = "/orders/scheduling" + clientId;
		return this;
	}

	public CommandWrapperBuilder deleteSchedulOrder(Long orderId) {

		this.actionName = "DELETE";
		this.entityName = "ORDERSCHEDULING";
		this.entityId = orderId;
		this.href = "/orders/scheduling" + clientId;
		return this;
	}

	public CommandWrapperBuilder extensionOrder(Long orderId) {
		this.actionName = "EXTENSION";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "/orders/extenstion" + clientId;
		return this;
	}

	public CommandWrapperBuilder addNewProvisioning(Long clientId) {

		this.actionName = "ADD";
		this.entityName = "PROVISIONINGSYSTEM";
		this.entityId = clientId;
		this.href = "/provisionings/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createGroupsDetails() {
		this.actionName = "CREATE";
		this.entityName = "GROUPSDETAILS";
		this.entityId = null;
		this.href = "/groupsdetails";
		return this;
	}

	public CommandWrapperBuilder createIpPoolManagement() {

		this.actionName = "CREATE";
		this.entityName = "IPPOOLMANAGEMENT";
		this.href = "/ippooling";
		return this;

	}

	public CommandWrapperBuilder updateprovisiongServiceParams(Long id) {

		this.actionName = "UPDATE";
		this.entityName = "PROVISIONINGSERVICEPARAMS";
		this.entityId = id;
		this.href = "/serviceparams/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createItemSale() {
		this.actionName = "CREATE";
		this.entityName = "ITEMSALE";
		this.href = "/agents/";
		return this;
	}

	public CommandWrapperBuilder updateprovisiongDetails(Long processrequestId) {

		this.actionName = "UPDATE";
		this.entityName = "PROVISIONINGDETAILS";
		this.entityId = processrequestId;
		this.href = "/provisioning/updateprovisiondetails" + processrequestId;
		return this;
	}

	public CommandWrapperBuilder confirnProvisiongDetails(Long processrequestId) {

		this.actionName = "CONFIRM";
		this.entityName = "PROVISIONINGDETAILS";
		this.entityId = processrequestId;
		this.href = "/provisioning/confirm" + processrequestId;
		return this;
	}

	public CommandWrapperBuilder createSmtpConfiguration() {
		this.actionName = "CREATE";
		this.entityName = "SMTPCONFIGURATION";
		// this.entityId=configId;
		this.href = "/configurations";
		return this;
	}

	public CommandWrapperBuilder paypalEnquireyPayment(final Long clientId) {
		this.actionName = "CREATEENQUIREY";
		this.entityName = "PAYMENT";
		this.entityId = clientId;
		this.href = "/payments/paypalEnquirey" + clientId;
		return this;
	}

	public CommandWrapperBuilder updateMediaStatus(String deviceId) {

		this.actionName = "UPDATE";
		this.entityName = "MEDIADEVICE";
		this.supportedEntityType = deviceId;
		this.href = "/mediadevices/" + deviceId;
		return this;
	}

	public CommandWrapperBuilder updateClientStatus(Long clientId) {
		this.actionName = "UPDATESTATUS";
		this.entityName = "CLIENT";
		this.entityId = clientId;
		this.href = "/clients/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createOfficeAdjustment(Long officeId) {
		this.actionName = "CREATE";
		this.entityName = "OFFICEADJUSTMENT";
		this.entityId = officeId;
		this.href = "/officeadjustments/" + officeId;
		return this;
	}

	public CommandWrapperBuilder createOfficePayment(Long officeId) {
		this.actionName = "CREATE";
		this.entityName = "OFFICEPAYMENT";
		this.entityId = officeId;
		this.href = "/officepayments/" + officeId;
		return this;
	}

	public CommandWrapperBuilder createRedemption() {
		this.actionName = "CREATE";
		this.entityName = "REDEMPTION";
		this.entityId = clientId;
		this.href = "/redemption/" + clientId + "/";
		return this;
	}

	public CommandWrapperBuilder moveItemSale() {
		this.actionName = "MOVEITEM";
		this.entityName = "MRN";
		this.entityId = null;
		this.href = "/mrndetails/movemrn/itemsale" + clientId;
		return this;
	}

	public CommandWrapperBuilder createClientCardDetails(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "CLIENTCARDDETAILS";
		this.entityId = null;
		this.clientId = clientId;
		this.href = "/clients/" + clientId + "/carddetails";
		return this;
	}

	public CommandWrapperBuilder updateCreditCardDetail(Long clientId, Long id, String cardType) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENTCARDDETAILS";
		this.entityId = clientId;
		this.subentityId = id;
		this.supportedEntityType = cardType;
		this.href = "/clients/" + clientId + "/carddetails/" + id + "/" + cardType;
		return this;
	}

	public CommandWrapperBuilder deleteClientCardDetails(Long id, Long clientId) {
		this.actionName = "DELETE";
		this.entityName = "CLIENTCARDDETAILS";
		this.entityId = clientId;
		this.subentityId = id;
		this.href = "/clients/" + clientId + "/carddetails/" + id;
		return this;
	}

	public CommandWrapperBuilder createGroupsDetailsProvision(Long prepareRequestId) {
		this.actionName = "CREATE";
		this.entityName = "GROUPSPROVISION";
		this.entityId = prepareRequestId;
		this.href = "/provision/" + prepareRequestId;
		return this;
	}

	public CommandWrapperBuilder updateClientTaxExemption(Long clientId) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENTTAXEXEMPTION";
		this.entityId = clientId;
		this.href = "/taxexemption/" + clientId;
		return this;
	}

	public CommandWrapperBuilder generateVoucherPin(Long batchId) {
		this.actionName = "PROCESS";
		this.entityName = "VOUCHER";
		this.entityId = batchId;
		this.href = "/vouchers/" + batchId;
		return this;
	}

	public CommandWrapperBuilder updateIpPoolManagement(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "IPPOOLMANAGEMENT";
		this.entityId = id;
		this.href = "/ippooling/" + id;
		return this;
	}

	public CommandWrapperBuilder updateClientBillMode(Long clientId) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENTBILLMODE";
		this.entityId = clientId;
		this.href = "/billmode/" + clientId;
		return this;
	}

	public CommandWrapperBuilder updateIpStatus(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "IPSTATUS";
		this.entityId = id;
		this.href = "/ippooling/status";
		return this;
	}

	public CommandWrapperBuilder terminateOrder(Long orderId) {

		this.actionName = "TERMINATE";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "/orders/terminate/" + orderId;
		return this;
	}

	public CommandWrapperBuilder createGroupsStatment(Long clientId) {

		this.actionName = "CREATESTATMENT";
		this.entityName = "GROUPS";
		this.entityId = clientId;
		this.href = "/groups/statment/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createProvisioningPlanMapping() {
		this.actionName = "CREATE";
		this.entityName = "PROVISIONINGPLANMAPPING";
		this.href = "/planmapping";
		return this;
	}

	public CommandWrapperBuilder updateProvisioningPlanMapping(Long planMappingId) {
		this.actionName = "UPDATE";
		this.entityName = "PROVISIONINGPLANMAPPING";
		this.entityId = planMappingId;
		this.href = "/planmapping/" + planMappingId;
		return this;
	}

	public CommandWrapperBuilder createDatatable(final String json) {
		this.actionName = "CREATE";
		this.entityName = "DATATABLE";
		this.href = "/datatables/";
		this.json = json;
		return this;
	}

	public CommandWrapperBuilder updateDatatable(final String datatable, final String json) {
		this.actionName = "UPDATE";
		this.entityName = "DATATABLE";
		this.href = "/datatables/" + datatable;
		this.json = json;
		return this;
	}

	public CommandWrapperBuilder deleteDatatable(final String datatable) {
		this.actionName = "DELETE";
		this.entityName = "DATATABLE";
		this.href = "/datatables/" + datatable;
		return this;
	}

	public CommandWrapperBuilder registerDBDatatable(final String datatable, final String apptable) {
		this.actionName = "REGISTER";
		this.entityName = "DATATABLE";
		this.entityId = null;
		this.href = "/datatables/register/" + datatable + "/" + apptable;
		return this;
	}

	public CommandWrapperBuilder createDatatableEntry(final String datatable, final Long apptableId,
			final Long datatableId) {
		this.actionName = "CREATE";
		commonDatatableSettings(datatable, apptableId, datatableId);
		return this;
	}

	public CommandWrapperBuilder updateDatatable(final String datatable, final Long apptableId,
			final Long datatableId) {
		this.actionName = "UPDATE";
		commonDatatableSettings(datatable, apptableId, datatableId);
		return this;
	}

	public CommandWrapperBuilder deleteDatatable(final String datatable, final Long apptableId,
			final Long datatableId) {
		this.actionName = "DELETE";
		commonDatatableSettings(datatable, apptableId, datatableId);
		return this;
	}

	private void commonDatatableSettings(final String datatable, final Long apptableId, final Long datatableId) {

		this.entityName = datatable;
		this.entityId = apptableId;
		this.subentityId = datatableId;
		if (datatableId == null) {
			this.href = "/datatables/" + datatable + "/" + apptableId;
		} else {
			this.href = "/datatables/" + datatable + "/" + apptableId + "/" + datatableId;
		}
	}

	public CommandWrapperBuilder updateIpDescription() {

		this.actionName = "UPDATE";
		this.entityName = "IPDESCRIPTION";
		this.href = "/ippooling/description";
		return this;

	}

	public CommandWrapperBuilder updateMediaCrashDetails(Long clientId) {

		this.actionName = "UPDATECRASH";
		this.entityName = "MEDIADEVICE";
		this.entityId = clientId;
		this.href = "/mediadevices/client/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createEventValidation() {
		this.actionName = "CREATE";
		this.entityName = "EVENTVALIDATION";
		this.entityId = null;
		this.href = "/eventvalidation";
		return this;
	}

	public CommandWrapperBuilder deleteEventValidation(Long id) {
		this.actionName = "DELETE";
		this.entityName = "EVENTVALIDATION";
		this.entityId = id;
		this.href = "/eventvalidation";
		return this;
	}

	public CommandWrapperBuilder editGrn(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "GRN";
		this.entityId = id;
		this.href = "itemdetails/editgrn";
		return this;
	}

	public CommandWrapperBuilder updateGrnOrderStatus(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "GRNORDERSTATUS";
		this.entityId = id;
		this.href = "/" + id + "/orderStatus";
		// this.href = "itemdetails/editgrn";
		return this;
	}

	public CommandWrapperBuilder createParentClient(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "PARENTCLIENT";
		this.entityId = clientId;
		this.href = "/parentclient/" + clientId;
		return this;
	}

	public CommandWrapperBuilder deleteChildFromParentClient(Long childId) {
		this.actionName = "DELETE";
		this.entityName = "PARENTCLIENT";
		this.entityId = childId;
		this.href = "/parentclient/" + childId;
		return this;

	}

	public CommandWrapperBuilder updateIpAddressStatus() {
		this.actionName = "UPDATEIPSTATUS";
		this.entityName = "IPPOOLMANAGEMENT";
		this.href = "itemdetails/editgrn";
		return this;
	}

	public CommandWrapperBuilder orderSuspend(Long orderId) {

		this.actionName = "SUSPEND";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "suspend/" + orderId;
		return this;
	}

	public CommandWrapperBuilder registerSelfCareRegister() {
		this.actionName = "REGISTER";
		this.entityName = "SELFCARE";
		this.href = "/selfcare/register";
		return this;
	}

	public CommandWrapperBuilder SelfCareEmailVerification() {
		this.actionName = "EMAILVERIFICATION";
		this.entityName = "SELFCARE";
		this.href = "/selfcare/register";
		return this;
	}

	public CommandWrapperBuilder selfRegistrationProcess() {
		this.actionName = "SELFREGISTRATION";
		this.entityName = "ACTIVATE";
		this.href = "/activationprocess/selfregistration";
		return this;
	}

	public CommandWrapperBuilder updateIpDetails(Long orderId) {
		this.actionName = "UPDATE";
		this.entityName = "IPDETAILPARAMS";
		this.entityId = orderId;
		this.href = "/ipdetails/" + orderId;
		return this;
	}

	public CommandWrapperBuilder orderReactive(Long orderId) {
		this.actionName = "REACTIVE";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "reactive/" + orderId;
		return this;
	}

	public CommandWrapperBuilder createNewSelfCarePassword() {
		this.actionName = "GENERATENEWPASSWORD";
		this.entityName = "SELFCARE";
		this.href = "/selfcare/forgotpassword";
		return this;
	}

	public CommandWrapperBuilder updateSelfcarePassword() {
		this.actionName = "UPDATE";
		this.entityName = "SELFCARE";
		this.href = "/selfcare/changepassword";
		return this;
	}

	public CommandWrapperBuilder createMediaAssetLocationAttribute(Long assetId) {
		this.actionName = "CREATE";
		this.entityName = "MEDIAASSETLOCATIONATTRIBUTES";
		this.entityId = assetId;
		this.href = "mediaasset/template";
		return this;
	}

	public CommandWrapperBuilder updateSupplier(Long supplierId) {
		this.actionName = "UPDATE";
		this.entityName = "SUPPLIER";
		this.entityId = supplierId;
		this.href = "/supplier";
		return this;
	}

	public CommandWrapperBuilder updateSelfCareUDPassword() {
		this.actionName = "UPDATE";
		this.entityName = "SELFCAREUDP";
		this.href = "/selfcare/resetpassword";
		return this;
	}

	public CommandWrapperBuilder forgetSelfCareUDPassword() {
		this.actionName = "MAIL";
		this.entityName = "SELFCAREUDP";
		this.href = "/selfcare/forgotpassword";
		return this;
	}

	public CommandWrapperBuilder cancelBillStatement(Long billId) {

		this.actionName = "DELETE";
		this.entityName = "BILLMASTER";
		this.entityId = billId;
		this.href = "/billmaster/" + billId;
		return this;

	}

	public CommandWrapperBuilder updateUploadFile(Long uploadFileId) {

		this.actionName = "PROCESS";
		this.entityName = "DATAUPLOADS";
		this.entityId = uploadFileId;
		this.href = "/dataupload/" + clientId;
		return this;
	}

	public CommandWrapperBuilder activeProvisionActions(Long provisionActionId) {

		this.actionName = "ACTIVE";
		this.entityName = "PROVISIONACTIONS";
		this.entityId = provisionActionId;
		this.href = "/provisioningactions/" + provisionActionId;
		return this;
	}

	public CommandWrapperBuilder updatePaymentGatewayConfig(Long configId) {
		this.actionName = "UPDATE";
		this.entityName = "PAYMENTGATEWAYCONFIG";
		this.entityId = configId;
		this.href = "/paymentgatewayconfigs";
		return this;
	}

	public CommandWrapperBuilder createPaymentGatewayConfig() {
		this.actionName = "CREATE";
		this.entityName = "PAYMENTGATEWAYCONFIG";
		this.href = "/paymentgatewayconfigs";
		return this;
	}

	public CommandWrapperBuilder OnlinePaymentGateway() {
		this.actionName = "ONLINE";
		this.entityName = "PAYMENTGATEWAY";
		this.href = "/paymentgateways/onlinepayment";
		return this;
	}

	public CommandWrapperBuilder createOrderAddons(Long orderId) {

		this.actionName = "CREATE";
		this.entityName = "ORDERADDONS";
		this.entityId = orderId;
		this.href = "/orders/addons/" + orderId;
		return this;
	}

	public CommandWrapperBuilder createAddons() {
		this.actionName = "CREATE";
		this.entityName = "ADDONS";
		this.href = "/addons/";
		return this;
	}

	public CommandWrapperBuilder updateAddons(Long addonId) {
		this.actionName = "UPDATE";
		this.entityName = "ADDONS";
		this.entityId = addonId;
		this.href = "/addons/" + addonId;
		return this;
	}

	public CommandWrapperBuilder deleteAddons(Long addonId) {
		this.actionName = "DELETE";
		this.entityName = "ADDONS";
		this.entityId = addonId;
		this.href = "/addons/" + addonId;
		return this;
	}

	public CommandWrapperBuilder linkUpAccount() {
		this.actionName = "CREATE";
		this.entityName = "LINKUPACCOUNT";
		this.href = "/linkupaccount/template";
		return this;

	}

	public CommandWrapperBuilder createPartner() {
		this.actionName = "CREATE";
		this.entityName = "PARTNER";
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder updatePartner(final Long partnerId) {
		this.actionName = "UPDATE";
		this.entityName = "PARTNER";
		this.entityId = partnerId;
		this.href = "/partner/" + partnerId;
		return this;
	}

	public CommandWrapperBuilder createPartnerAgreement(final Long partnerId) {
		this.actionName = "CREATE";
		this.entityName = "PARTNERAGREEMENT";
		this.entityId = partnerId;
		this.href = "/agreements/" + partnerId;
		return this;
	}

	public CommandWrapperBuilder updateAgreement(final Long agreementId) {
		this.actionName = "UPDATE";
		this.entityName = "PARTNERAGREEMENT";
		this.entityId = agreementId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder deleteAgreement(final Long agreementId) {
		this.actionName = "DELETE";
		this.entityName = "PARTNERAGREEMENT";
		this.entityId = agreementId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder updateVoucherPin(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "VOUCHER";
		this.entityId = id;
		this.href = "/vouchers";
		return this;
	}

	public CommandWrapperBuilder deleteVoucherPin(Long id) {
		this.actionName = "DELETE";
		this.entityName = "VOUCHER";
		this.entityId = id;
		this.href = "/vouchers";
		return this;
	}

	public CommandWrapperBuilder createTemplate() {
		this.actionName = "CREATE";
		this.entityName = "TEMPLATE";
		this.entityId = null;
		this.href = "/templates";
		return this;
	}

	public CommandWrapperBuilder updateTemplate(final Long templateId) {
		this.actionName = "UPDATE";
		this.entityName = "TEMPLATE";
		this.entityId = templateId;
		this.href = "/templates/" + templateId;
		return this;
	}

	public CommandWrapperBuilder deleteTemplate(final Long templateId) {
		this.actionName = "DELETE";
		this.entityName = "TEMPLATE";
		this.entityId = templateId;
		this.href = "/templates/" + templateId;
		return this;
	}

	public CommandWrapperBuilder cancelVoucherPin(Long id) {

		this.actionName = "CANCEL";
		this.entityName = "VOUCHER";
		this.entityId = id;
		this.href = "/vouchers";
		return this;
	}

	public CommandWrapperBuilder updateRadService(final Long radServiceId) {

		this.actionName = "UPDATE";
		this.entityName = "RADSERVICE";
		this.entityId = radServiceId;
		this.href = "/radservice/" + radServiceId;
		return this;
	}

	public CommandWrapperBuilder deleteRadService(final Long radServiceId) {

		this.actionName = "DELETE";
		this.entityName = "RADSERVICE";
		this.entityId = radServiceId;
		this.href = "/radservice/" + radServiceId;
		return this;

	}

	public CommandWrapperBuilder createVendorManagement() {
		this.actionName = "CREATE";
		this.entityName = "VENDORMANAGEMENT";
		this.entityId = null;
		this.href = "/vendormanagement/template";
		return this;
	}

	public CommandWrapperBuilder updateVendorManagement(final Long vendorId) {
		this.actionName = "UPDATE";
		this.entityName = "VENDORMANAGEMENT";
		this.entityId = vendorId;
		this.href = "/vendormanagement/" + vendorId;
		return this;
	}

	public CommandWrapperBuilder deleteVendorManagement(final Long vendorId) {
		this.actionName = "DELETE";
		this.entityName = "VENDORMANAGEMENT";
		this.entityId = vendorId;
		this.href = "/vendormanagement/" + vendorId;
		return this;
	}

	public CommandWrapperBuilder createVendorAgreement() {
		this.actionName = "CREATE";
		this.entityName = "VENDORAGREEMENT";
		this.entityId = null;
		this.href = "/vendoragreement/template";
		return this;
	}

	public CommandWrapperBuilder updateVendorAgreement(final Long vendorAgreementId) {
		this.actionName = "UPDATE";
		this.entityName = "VENDORAGREEMENT";
		this.entityId = vendorAgreementId;
		this.href = "/vendoragreement/" + vendorAgreementId;
		return this;
	}

	public CommandWrapperBuilder createProperty() {

		this.actionName = "CREATE";
		this.entityName = "PROPERTY";
		this.entityId = null;
		this.href = "/property/";
		return this;
	}

	public CommandWrapperBuilder deleteProperty(final Long propertyId) {

		this.actionName = "DELETE";
		this.entityName = "PROPERTY";
		this.entityId = propertyId;
		this.href = "/property/" + propertyId;
		return this;
	}

	public CommandWrapperBuilder updateProperty(final Long propertyId) {

		this.actionName = "UPDATE";
		this.entityName = "PROPERTY";
		this.entityId = propertyId;
		this.href = "/property/" + propertyId;
		return this;
	}

	public CommandWrapperBuilder createClientAdditional(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "CLIENTADDITIONALINFO";
		this.entityId = clientId;
		this.href = "/additionalinfo/";
		return this;
	}

	public CommandWrapperBuilder updateClientAdditional(Long clientId) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENTADDITIONALINFO";
		this.entityId = clientId;
		this.href = "/additionalinfo/";
		return this;
	}

	public CommandWrapperBuilder createServiceTransfer(final Long clientId) {

		this.actionName = "CREATE";
		this.entityName = "SERVICETRANSFER";
		this.entityId = clientId;
		this.href = "/servicetransfer/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createFeeMaster() {
		this.actionName = "CREATE";
		this.entityName = "FEEMASTER";
		this.entityId = null;
		this.href = "/feemaster";
		return this;
	}

	public CommandWrapperBuilder updateFeeMaster(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "FEEMASTER";
		this.entityId = id;
		this.href = "/feemaster/" + id;
		return this;
	}

	public CommandWrapperBuilder deleteFeeMaster(Long id) {
		this.actionName = "DELETE";
		this.entityName = "FEEMASTER";
		this.entityId = id;
		this.href = "/feemaster/" + id;
		return this;
	}

	public CommandWrapperBuilder updateStaticIpAddress() {
		this.actionName = "UPDATE";
		this.entityName = "STATICIP";
		this.href = "ippooling/staticip";
		return this;
	}

	public CommandWrapperBuilder updatePlanQualifier(Long planId) {
		this.actionName = "UPDATE";
		this.entityName = "PLANQUALIFIER";
		this.entityId = planId;
		this.href = "/planqualifier/" + planId;
		return this;
	}

	public CommandWrapperBuilder createPropertyMaster() {
		this.actionName = "CREATE";
		this.entityName = "PROPERTYMASTER";
		this.entityId = null;
		this.href = "/propertycodemaster";
		return this;
	}

	public CommandWrapperBuilder disconnectOrder(Long orderId) {

		this.actionName = "DISCONNECT";
		this.entityName = "ORDER";
		this.entityId = orderId;
		this.href = "/orderdisconnect/" + orderId;
		return this;
	}

	public CommandWrapperBuilder updatePaypalProfileRecurring() {
		this.actionName = "UPDATEPAYPALRECURRING";
		this.entityName = "PAYMENTGATEWAY";
		this.entityId = null;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder updatePropertyMaster(final Long codeId) {
		this.actionName = "UPDATE";
		this.entityName = "PROPERTYMASTER";
		this.entityId = codeId;
		this.href = "/propertycodemaster/" + codeId;
		return this;
	}

	public CommandWrapperBuilder deletePropertyMaster(final Long codeId) {
		this.actionName = "DELETE";
		this.entityName = "PROPERTYMASTER";
		this.entityId = codeId;
		this.href = "/propertycodemaster/" + codeId;
		return this;
	}

	public CommandWrapperBuilder deleteRecurringBilling() {
		this.actionName = "DELETERECURRINGBILLING";
		this.entityName = "PAYMENTGATEWAY";
		this.entityId = null;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder updatePaypalProfileStatus() {
		this.actionName = "UPDATEPAYPALPROFILESTATUS";
		this.entityName = "PAYMENTGATEWAY";
		this.entityId = null;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder createRefundAmount(Long depositId) {
		this.actionName = "CREATE";
		this.entityName = "REFUND";
		this.entityId = depositId;
		this.href = "/refund/" + depositId;
		return this;
	}

	public CommandWrapperBuilder createDeposite() {
		this.actionName = "CREATE";
		this.entityName = "DEPOSIT";
		this.entityId = null;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder createDeposit(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "DEPOSIT";
		this.entityId = clientId;
		this.href = "/deposit/" + clientId;
		return this;
	}

	public CommandWrapperBuilder allocateProperty(Long clientId) {

		this.actionName = "ALLOCATEDEVICE";
		this.entityName = "PROPERTY";
		this.entityId = clientId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder disconnectOrderAddon(Long orderAddonId) {

		this.actionName = "DISCONNECT";
		this.entityName = "ORDERADDONS";
		this.entityId = orderAddonId;
		this.href = "/orders/addons/" + orderAddonId;
		return this;
	}

	public CommandWrapperBuilder renewalOrderWithClient(Long clientId) {

		this.actionName = "RENEWALWITHCLIENT";
		this.entityName = "ORDER";
		this.entityId = clientId;
		this.href = "/orderRenewal/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createUsageChargesRawData() {

		this.actionName = "CREATE";
		this.entityName = "CHARGES";
		this.entityId = null;
		this.href = "/charges";
		return this;
	}

	public CommandWrapperBuilder updateBeesmartClient() {
		this.actionName = "UPDATE";
		this.entityName = "BEESMARTCLIENT";
		this.entityId = null;
		this.href = "/beesmart";
		return this;
	}

	public CommandWrapperBuilder deleteBeesmartClient(Long clientId) {
		this.actionName = "DELETE";
		this.entityName = "BEESMARTCLIENT";
		this.entityId = clientId;
		this.href = "/beesmart";
		return this;
	}

	public CommandWrapperBuilder createClientService() {
		this.actionName = "CREATE";
		this.entityName = "CLIENTSERVICE";
		this.href = "/clientservice";
		return this;
	}

	public CommandWrapperBuilder createProvisioningRequest(Long orderId) {
		this.actionName = "CREATE";
		this.entityName = "PROVISIONINGREQUEST";
		this.entityId = orderId;
		this.href = "/provisioningactions/" + orderId;
		return this;
	}

	public CommandWrapperBuilder createClientServiceActivation(Long clientServiceId) {
		this.actionName = "CREATE";
		this.entityName = "CLIENTSERVICEACTIVATION";
		this.entityId = clientServiceId;
		this.href = "/clientservice/" + clientServiceId;
		return this;
	}

	public CommandWrapperBuilder suspendClientService(Long clientServiceId) {
		this.actionName = "SUSPEND";
		this.entityName = "CLIENTSERVICE";
		this.entityId = clientServiceId;
		this.href = "/clientservice/suspend/" + clientServiceId;
		return this;
	}

	public CommandWrapperBuilder createStudent() {
		this.actionName = "CREATE";
		this.entityName = "STUDENT";
		this.entityId = null;
		this.href = "/students";
		return this;
	}

	public CommandWrapperBuilder createChannel() {
		this.actionName = "CREATE";
		this.entityName = "CHANNEL";
		this.entityId = null;
		this.href = "/channel";
		return this;
	}

	public CommandWrapperBuilder createCompany() {
		this.actionName = "CREATE";
		this.entityName = "COMPANYREGISTRATION";
		this.entityId = null;
		this.href = "/companyregistration";
		return this;
	}

	public CommandWrapperBuilder createBroadcaster() {
		this.actionName = "CREATE";
		this.entityName = "BROADCASTER";
		this.entityId = null;
		this.href = "/broadcaster";
		return this;
	}

	public CommandWrapperBuilder updateBroadcaster(Long broadcasterId) {
		this.actionName = "UPDATE";
		this.entityName = "BROADCASTER";
		this.entityId = broadcasterId;
		this.href = "/broadcaster";
		return this;

	}

	public CommandWrapperBuilder deleteBroadcaster(Long broadcasterId) {
		this.actionName = "DELETE";
		this.entityName = "BROADCASTER";
		this.entityId = broadcasterId;
		this.href = "/broadcaster/" + broadcasterId;
		return this;
	}

	public CommandWrapperBuilder updateChannel(Long channelId) {
		this.actionName = "UPDATE";
		this.entityName = "CHANNEL";
		this.entityId = channelId;
		this.href = "/channel";
		return this;

	}

	public CommandWrapperBuilder deleteChannel(Long channelId) {
		this.actionName = "DELETE";
		this.entityName = "CHANNEL";
		this.entityId = channelId;
		this.href = "/channel/" + channelId;
		return this;
	}

	public CommandWrapperBuilder createChannelMapping() {
		this.actionName = "CREATE";
		this.entityName = "CHANNELMAPPING";
		this.entityId = null;
		this.href = "/channelmapping";
		return this;
	}

	public CommandWrapperBuilder updateChannelMapping(Long productId) {
		this.actionName = "UPDATE";
		this.entityName = "CHANNELMAPPING";
		this.entityId = productId;
		this.href = "/channelmapping/" + productId;
		return this;
	}

	public CommandWrapperBuilder reactiveClientService(Long clientServiceId) {
		this.actionName = "REACTIVE";
		this.entityName = "CLIENTSERVICE";
		this.entityId = clientServiceId;
		this.href = "/clientservice/reactive/" + clientServiceId;
		return this;
	}

	public CommandWrapperBuilder deleteChannelMapping(Long productId) {
		this.actionName = "DELETE";
		this.entityName = "CHANNELMAPPING";
		this.entityId = productId;
		this.href = "/channelmapping/" + productId;
		return this;
	}

	public CommandWrapperBuilder terminateClientService(Long clientServiceId) {
		this.actionName = "TERMINATE";
		this.entityName = "CLIENTSERVICE";
		this.entityId = clientServiceId;
		this.href = "/clientservice/terminate/" + clientServiceId;
		return this;
	}

	public CommandWrapperBuilder swapDevice() {
		this.actionName = "SWAP";
		this.entityName = "HARDWAREDEVICE";
		this.entityId = null;
		this.href = "/itemdetails/swapDevice";
		return this;
	}

	public CommandWrapperBuilder createProduct() {
		this.actionName = "CREATE";
		this.entityName = "PRODUCT";
		this.entityId = null;
		this.href = "/product";
		return this;
	}

	public CommandWrapperBuilder updateProduct(Long productId) {
		this.actionName = "UPDATE";
		this.entityName = "PRODUCT";
		this.entityId = productId;
		this.href = "/product/" + productId;
		return this;
	}

	public CommandWrapperBuilder deleteProduct(Long productId) {
		this.actionName = "DELETE";
		this.entityName = "PRODUCT";
		this.entityId = productId;
		this.href = "/product/" + productId;
		return this;
	}

	public CommandWrapperBuilder createProvisionCodeMapping() {

		this.actionName = "CREATE";
		this.entityName = "PROVISIONCODEMAPPING";
		this.entityId = null;
		this.href = "/provisioncodemapping";
		return this;

	}

	public CommandWrapperBuilder updateProvisionCodeMapping(Long provisioncodemappingId) {
		this.actionName = "UPDATE";
		this.entityName = "PROVISIONCODEMAPPING";
		this.entityId = provisioncodemappingId;
		this.href = "/provisioncodemapping/" + provisioncodemappingId;
		return this;
	}

	public CommandWrapperBuilder deleteProvisionCodeMapping(Long provisioncodemappingId) {
		this.actionName = "DELETE";
		this.entityName = "PROVISIONCODEMAPPING";
		this.entityId = provisioncodemappingId;
		this.href = "/provisioncodemapping/" + provisioncodemappingId;
		return this;
	}

	public CommandWrapperBuilder createModelProvisionMapping() {
		this.actionName = "CREATE";
		this.entityName = "MODELPROVISIONMAPPING";
		this.entityId = null;
		this.href = "/modelprovisionmapping";
		return this;
	}

	public CommandWrapperBuilder updateModelProvisionMapping(Long modelProvisionMappingId) {
		this.actionName = "UPDATE";
		this.entityName = "MODELPROVISIONMAPPING";
		this.entityId = modelProvisionMappingId;
		this.href = "/modelprovisionmapping/" + modelProvisionMappingId;
		return this;
	}

	public CommandWrapperBuilder deleteModelProvisionMapping(Long modelProvisionMappingId) {
		this.actionName = "DELETE";
		this.entityName = "MODELPROVISIONMAPPING";
		this.entityId = modelProvisionMappingId;
		this.href = "/modelprovisionmapping/" + modelProvisionMappingId;
		return this;
	}

	public CommandWrapperBuilder createSalesCataloge() {
		this.actionName = "CREATE";
		this.entityName = "SALESCATALOGE";
		this.entityId = null;
		this.href = "/salescataloge";
		return this;
	}

	public CommandWrapperBuilder updateSalesCataloge(Long salescatalogeId) {
		this.actionName = "UPDATE";
		this.entityName = "SALESCATALOGE";
		this.entityId = salescatalogeId;
		this.href = "/salescataloge/" + salescatalogeId;
		return this;
	}

	public CommandWrapperBuilder deleteSalesCataloge(Long salescatalogeId) {
		this.actionName = "DELETE";
		this.entityName = "SALESCATALOGE";
		this.entityId = salescatalogeId;
		this.href = "/salescataloge/" + salescatalogeId;
		return this;
	}

	public CommandWrapperBuilder createSalesCatalogeMapping() {
		this.actionName = "CREATE";
		this.entityName = "SALESCATALOGEMAPPING";
		this.entityId = null;
		this.href = "/salescatalogemapping";
		return this;
	}

	public CommandWrapperBuilder updateSalesCatalogeMapping(Long salescatalogemappingId) {
		this.actionName = "UPDATE";
		this.entityName = "SALESCATALOGEMAPPING";
		this.entityId = salescatalogemappingId;
		this.href = "/salescatalogemapping/" + salescatalogemappingId;
		return this;
	}

	public CommandWrapperBuilder deleteSalesCatalogeMapping(Long salescatalogemappingId) {
		this.actionName = "DELETE";
		this.entityName = "SALESCATALOGEMAPPING";
		this.entityId = salescatalogemappingId;
		this.href = "/salescatalogemapping/" + salescatalogemappingId;
		return this;
	}

	public CommandWrapperBuilder createUserCataloge() {
		this.actionName = "CREATE";
		this.entityName = "USERCATALOGE";
		this.entityId = null;
		this.href = "/usercataloge";
		return this;
	}

	public CommandWrapperBuilder updateUserCataloge(Long userId) {
		this.actionName = "UPDATE";
		this.entityName = "USERCATALOGE";
		this.entityId = userId;
		this.href = "/usercataloge/" + userId;
		return this;
	}

	public CommandWrapperBuilder deleteUserCataloge(Long usercatalogeId) {
		this.actionName = "DELETE";
		this.entityName = "USERCATALOGE";
		this.entityId = usercatalogeId;
		this.href = "/usercataloge/" + usercatalogeId;
		return this;
	}

	public CommandWrapperBuilder createCurrency() {
		this.actionName = "CREATE";
		this.entityName = "CURRENCY";
		this.entityId = null;
		this.href = "/currencies";
		return this;
	}

	public CommandWrapperBuilder updateCurrency(Long currencyId) {
		this.actionName = "UPDATE";
		this.entityName = "CURRENCY";
		this.entityId = currencyId;
		this.href = "/currencies/" + currencyId;
		return this;
	}

	public CommandWrapperBuilder createClientSimpleActivation(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "CLIENTSIMPLEACTIVATION";
		this.entityId = clientId;
		this.href = "/activationprocess/simpleactivation/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createClientBillProfile() {
		this.actionName = "CREATE";
		this.entityName = "BILLPROFILE";
		this.entityId = null;
		this.href = "/clientbillprofile";
		return this;
	}

	public CommandWrapperBuilder updateClientBillInfo(Long clientId) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENTBILLPROFILE";
		this.entityId = clientId;
		this.href = "/clientbillprofile/" + clientId;
		return this;

	}

	public CommandWrapperBuilder createOBRMClient() {
		this.actionName = "CREATE";
		this.entityName = "OBRMCLIENT";
		this.entityId = null;
		this.href = "/obrm/createclient";
		return this;

	}

	public CommandWrapperBuilder createCelcomClient() {
		this.actionName = "CREATE";
		this.entityName = "CELCOMCLIENT";
		this.entityId = null;
		this.href = "/celcom/createclient";
		return this;

	}

	public CommandWrapperBuilder createOBRMClientSimpleActivation() {
		this.actionName = "CREATE";
		this.entityName = "OBRMCLIENTSIMPLEACTIVATION";
		this.entityId = null;
		this.href = "/obrm/createClientSimpleActivation";
		return this;
	}

	public CommandWrapperBuilder createCelcomClientSimpleActivation() {
		this.actionName = "CREATE";
		this.entityName = "CELCOMCLIENTSIMPLEACTIVATION";
		this.entityId = null;
		this.href = "/celcom/createClientSimpleActivation";
		return this;
	}

	public CommandWrapperBuilder createOBRMPlan() {
		this.actionName = "CREATE";
		this.entityName = "OBRMPLAN";
		this.entityId = null;
		this.href = "/obrm/syncplan";
		return this;
	}

	public CommandWrapperBuilder createOBRMOffice() {
		this.actionName = "CREATE";
		this.entityName = "OBRMOFFICE";
		this.entityId = null;
		this.href = "/obrm/createoffice";
		return this;
	}

	public CommandWrapperBuilder createCelcomOffice() {
		this.actionName = "CREATE";
		this.entityName = "CELCOMOFFICE";
		this.entityId = null;
		this.href = "/celcom/createoffice";
		return this;
	}

	public CommandWrapperBuilder renewalLCOclients() {
		this.actionName = "RENEWAL";
		this.entityName = "LCO";
		this.entityId = null;
		this.href = "/lco/renewalS";
		return this;
	}

	public CommandWrapperBuilder createHardwarePlans(Long clientId, Long clientServiceId) {
		this.actionName = "CREATE";
		this.entityName = "HARDWAREPLAN";
		this.entityId = clientId;
		this.subentityId = clientServiceId;
		this.href = "/hardwareplan/addhardwareplan/" + clientId + "/" + clientServiceId;
		return this;
	}

	public CommandWrapperBuilder createHardwareDevicePlan(Long clientId, String devicesaleType) {
		this.actionName = "CREATE";
		this.entityName = "CREATEHARDWAREDEVICEPLAN";
		this.entityId = clientId;
		this.supportedEntityType = devicesaleType;
		this.href = "/hardwaredeviceplan/template";
		return this;
	}

	/**
	 * create sub category
	 * 
	 * @added by H
	 */
	public CommandWrapperBuilder createSubcategory() {
		this.actionName = "CREATE";
		this.entityName = "SUBCATEGORY";
		this.entityId = null;
		this.href = "/createSubcategory";
		return this;
	}

	public CommandWrapperBuilder updateAddressNew(final Long addrId) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENTADDRESS";
		this.entityId = addrId;
		this.href = "/address/rec/" + addrId;
		return this;
	}

	public CommandWrapperBuilder syncPlanCelcom() {
		this.actionName = "SYNC";
		this.entityName = "CELCOMPLAN";
		this.entityId = null;
		this.href = "/celcom/syncplan";
		return this;
	}

	public CommandWrapperBuilder movemrncarton() {
		this.actionName = "MOVE";
		this.entityName = "MRNCARTON";
		this.entityId = null;
		this.href = "/mrndetails/movemrncarton/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createCelcomPayment() {
		this.actionName = "CREATE";
		this.entityName = "CELCOMPAYMENT";
		this.entityId = null;
		this.href = "/celcom/payments";
		return this;
	}

	public CommandWrapperBuilder createAdjustmentsCelcom() {
		this.actionName = "CREATE";
		this.entityName = "ADJUSTMENTSCELCOM";
		this.entityId = null;
		this.href = "/celcom/adjustments";
		return null;
	}

	public CommandWrapperBuilder createMultipleOrder(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "MULTIPLEORDERS";
		this.entityId = clientId;
		this.href = "/orders/" + clientId;
		return this;
	}

	public CommandWrapperBuilder disconnectMultipleOrder(Long clientId) {
		this.actionName = "DISCONNECT";
		this.entityName = "MULTIPLEORDERS";
		this.entityId = clientId;
		this.href = "/orderdisconnect/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createClientHardwarePlanActivation(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "CLIENTHARDWAREPLANACTIVATION";
		this.entityId = clientId;
		this.href = "/activationprocess/hardwareplanactivation/" + clientId;
		return this;

	}

	public CommandWrapperBuilder createCelcomAgreement() {
		this.actionName = "CREATE";
		this.entityName = "CELCOMAGREEMENT";
		this.entityId = null;
		this.href = "/celcom/createagreement";
		return this;
	}

	public CommandWrapperBuilder Renewalplan() {
		this.actionName = "RENEWAL";
		this.entityName = "RENEWALPLAN";
		this.entityId = null;
		this.href = "/celcom/Renewalplan";
		return this;
	}

	public CommandWrapperBuilder createGRV() {
		this.actionName = "CREATE";
		this.entityName = "GRV";
		this.entityId = null;
		this.href = "/grv";
		return this;
	}

	public CommandWrapperBuilder updateCelcomClient(final Long clientId) {
		this.actionName = "UPDATE";
		this.entityName = "CELCOMCLIENT";
		this.entityId = clientId;
		this.clientId = clientId;
		this.href = "/celcom/" + clientId;
		return this;
	}

	public CommandWrapperBuilder moveGRV() {
		this.actionName = "MOVE";
		this.entityName = "GRV";
		this.entityId = null;
		this.href = "/mrndetails/movegrv/" + clientId;
		return this;
	}

	public CommandWrapperBuilder createCustomerActivation() {
		this.actionName = "CREATE";
		this.entityName = "CUSTOMERACTIVATION";
		this.entityId = null;
		this.href = "/activationprocess/customeractivation";
		return this;
	}

	public CommandWrapperBuilder createCelcomOfficePayment() {
		this.actionName = "CREATE";
		this.entityName = "CELCOMOFFICEPAYMENT";
		this.entityId = null;
		this.href = "/celcom/officepayments";
		return this;

	}

	public CommandWrapperBuilder updateProvisioningRequestDetailsData(Long provisioningrequestId) {
		this.actionName = "UPDATE";
		this.entityName = "PROVISIONINGDETAILSMESSAGE";
		this.entityId = provisioningrequestId;
		this.href = "/provisioning/message/" + provisioningrequestId;
		return this;
	}

	public CommandWrapperBuilder updateTicketMaster(final Long ticketId) {
		this.actionName = "UPDATE";
		this.entityName = "TICKETMASTER";
		this.entityId = ticketId;
		this.href = "/ticketing/" + ticketId;
		return this;
	}

	public CommandWrapperBuilder createTicketTeam() {
		this.actionName = "CREATE";
		this.entityName = "TICKETTEAM";
		this.entityId = null;
		this.href = "/ticketteam";
		return this;

	}

	public CommandWrapperBuilder updateTicketteam(Long ticketteamId) {
		this.actionName = "UPDATE";
		this.entityName = "TICKETTEAM";
		this.entityId = ticketteamId;
		this.href = "/ticketteamId";
		return this;
	}

	public CommandWrapperBuilder deleteTicketteam(Long ticketteamId) {
		this.actionName = "DELETE";
		this.entityName = "TICKETTEAM";
		this.entityId = ticketteamId;
		this.href = "/ticketteam/" + ticketteamId;
		return this;
	}

	public CommandWrapperBuilder createTicketMapping() {
		this.actionName = "CREATE";
		this.entityName = "TICKETMAPPING";
		this.entityId = null;
		this.href = "/ticketmapping";
		return this;

	}

	public CommandWrapperBuilder updateTicketmapping(Long ticketmappingId) {
		this.actionName = "UPDATE";
		this.entityName = "TICKETMAPPING";
		this.entityId = ticketmappingId;
		this.href = "/ticketmappingId";
		return this;
	}

	public CommandWrapperBuilder deleteTicketmapping(Long ticketmappingId) {

		this.actionName = "DELETE";
		this.entityName = "TICKETMAPPING";
		this.entityId = ticketmappingId;
		this.href = "/ticketmapping/" + ticketmappingId;
		return this;
	}

	public CommandWrapperBuilder createTicketOffice(final Long officeId) {
		this.actionName = "CREATE";
		this.entityName = "OFFICETICKET";
		this.clientId = officeId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder updateOfficeTicket(final Long ticketId) {
		this.actionName = "UPDATE";
		this.entityName = "OFFICETICKET";
		this.entityId = ticketId;
		this.href = "/ticketing/" + ticketId;
		return this;
	}

	public CommandWrapperBuilder deleteOfficeTicket(final Long ticketId) {
		this.actionName = "CLOSE";
		this.entityName = "OFFICETICKET";
		this.entityId = ticketId;
		this.href = "";
		return this;
	}

	public CommandWrapperBuilder createNetworkElement() {
		this.actionName = "CREATE";
		this.entityName = "NETWORKELEMENT";
		this.entityId = null;
		this.href = "/networkelement";
		return this;
	}

	public CommandWrapperBuilder updateNetworkelement(Long networkelementId) {
		this.actionName = "UPDATE";
		this.entityName = "NETWORKELEMENT";
		this.entityId = networkelementId;
		this.href = "/networkelement";
		return this;
	}

	public CommandWrapperBuilder deleteNetworkElement(Long networkelementId) {
		this.actionName = "DELETE";
		this.entityName = "NETWORKELEMENT";
		this.entityId = networkelementId;
		this.href = "/networkelement/" + networkelementId;
		return this;

	}

	public CommandWrapperBuilder createClientDiscount() {
		this.actionName = "CREATE";
		this.entityName = "CLIENTDISCOUNT";
		this.entityId = null;
		this.href = "/clientdiscount";
		return this;

	}

	public CommandWrapperBuilder updateClientDiscount(Long clientDiscountId) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENTDISCOUNT";
		this.entityId = clientDiscountId;
		this.href = "/channel";
		return this;

	}

	public CommandWrapperBuilder deleteClientDiscount(Long clientDiscountId) {
		this.actionName = "DELETE";
		this.entityName = "CLIENTDISCOUNT";
		this.entityId = clientDiscountId;
		this.href = "/channel/" + clientDiscountId;
		return this;
	}

	public CommandWrapperBuilder cancelofficepayment(Long paymentId) {

		this.actionName = "DELETE";
		this.entityName = "OFFICEPAYMENT";
		this.entityId = paymentId;
		this.href = "/officepayments/cancelpayment/" + this.entityId;
		return this;
	}

	// TODO HEMANTH: CREDIT LIMIT
	public CommandWrapperBuilder updateOfficeCreditLimit(Long officeId) {
		this.actionName = "UPDATE";
		this.entityName = "OFFICECREDITLIMIT";
		this.entityId = officeId;
		this.href = "/officeadjustments/" + officeId;
		return this;
	}

	public CommandWrapperBuilder Osdmessage(Long clientServicePoid) {
		this.actionName = "CREATE";
		this.entityName = "OSDMESSAGE";
		this.entityId = clientServicePoid;
		this.href = "orders/Osdmessage/" + clientServicePoid;
		return this;
	}

	public CommandWrapperBuilder productMappingCelcom(Long productId) {
		this.actionName = "POST";
		this.entityName = "PRODUCTMAPPING";
		this.entityId = productId;
		this.href = "/pushbroadcaster/";
		return this;
	}

	public CommandWrapperBuilder DeleteTaxMap(Long taxMapId) {
		this.actionName = "DELETE";
		this.entityName = "TAXMAPPING";
		this.entityId = taxMapId;
		this.href = "taxmap/" + taxMapId;
		return this;
	}

	public CommandWrapperBuilder CreateOTP() {
		this.actionName = "CREATE";
		this.entityName = "OTP";
		this.href = "/selfcare/OTP";
		return this;
	}

	public CommandWrapperBuilder deleteCurrency(Long currencyId) {
		this.actionName = "DELETE";
		this.entityName = "CURRENCY";
		this.entityId = currencyId;
		this.href = "/currencies/" + currencyId;
		return this;
	}

	public CommandWrapperBuilder updateNewCurrency(Long currencyId) {
		this.actionName = "UPDATE";
		this.entityName = "CURRENCYS";
		this.entityId = currencyId;
		this.href = "/currencies/update/" + currencyId;
		return this;

	}

	public CommandWrapperBuilder createServiceActivationWithoutDevice(Long clientId) {
		this.actionName = "CREATE";
		this.entityName = "SERVICEACTIVATIONWITHOUTDEVICE";
		this.entityId = clientId;
		this.href = "/activationprocess/serviceactivationwod/" + clientId;
		return this;
	}
	public CommandWrapperBuilder verifyNIN(Long NINID) {
		this.actionName = "VERIFY";
		this.entityName = "NIN";
		this.entityId = NINID;
		this.href = "/activationprocess/verifyNIN/" + NINID;
		return this;
	}

	
	public CommandWrapperBuilder leaseActivation() {
		this.actionName = "CREATE";
		this.entityName = "LEASE";
		this.href = "/activationprocess/lease";
		return this;
	}
	
	public CommandWrapperBuilder leaseValidation() {
		this.actionName = "VALIDATION";
		this.entityName = "LEASE";
		this.href = "/activationprocess/lease";
		return this;
	}
	
	public CommandWrapperBuilder createQuote() {
		this.actionName = "CREATE";
		this.entityName = "QUOTES";
		this.entityId = null;
		this.href = "/quotes";
		return this;
	}

	public CommandWrapperBuilder updateServiceparameter(Long serviceParameterDataId) {
		this.actionName = "UPDATE";
		this.entityName = "CLIENTSERVICE";
		this.entityId = serviceParameterDataId;
		this.href = "/clientservice/serviceparams/" + serviceParameterDataId;
		return this;
	}

	public CommandWrapperBuilder createServiceAvailability(Long addressId) {
		this.actionName = "CREATE";
		this.entityName = "SERVICEAVAILBILITY";
		this.entityId = addressId;
		this.href = "/address/service/" + addressId;
		return this;
	}

	public CommandWrapperBuilder createNewVendorAgreement(Long vendorAgreementId) {
		this.actionName = "CREATE";
		this.entityName = "VENDORAGREEMENT";
		this.entityId = vendorAgreementId;
		this.href = "/vendoragreement/New/" + vendorAgreementId;
		return this;
	}

	public CommandWrapperBuilder updateNewVendorAgreement(Long vendorAgreementId) {
		this.actionName = "UPDATE";
		this.entityName = "NEWVENDORAGREEMENT";
		this.entityId = vendorAgreementId;
		this.href = "/vendoragreement/" + vendorAgreementId;
		return this;
	}

	public CommandWrapperBuilder createOrderWorkflow() {
		this.actionName = "CREATE";
		this.entityName = "ORDERWORKFLOW";
		this.entityId = null;
		this.href = "/orderworkflow";
		return this;
	}

	public CommandWrapperBuilder deleteQuotation(Long leadId) {
		this.actionName = "DELETE";
		this.entityName = "QUOTATION";
		this.entityId = leadId;
		this.href = "/quotes/" + leadId;
		return this;
	}

	public CommandWrapperBuilder updateQuotation(Long leadId) {
		this.actionName = "UPDATE";
		this.entityName = "QUOTATION";
		this.entityId = leadId;
		this.href = "/quotes/" + leadId;
		return this;
	}

	public CommandWrapperBuilder elevateProspect(Long prospectId) {
		this.actionName = "UPDATE";
		this.entityName = "ELEVATEPROSPECT";
		this.entityId = prospectId;
		this.href = "/prospects/elevate/" + prospectId;
		return this;
	}

	public CommandWrapperBuilder updateQuotationStatus(Long leadId) {
		this.actionName = "UPDATE";
		this.entityName = "QUOTATIONSTATUS";
		this.entityId = leadId;
		this.href = "/quotes/status/" + leadId;
		return this;
	}

	public CommandWrapperBuilder createTemplates() {
		this.actionName = "CREATE";
		this.entityName = "TEMPLATES";
		this.entityId = null;
		this.href = "/template";
		return this;
	}

	public CommandWrapperBuilder updateTemplates(Long templateId) {
		this.actionName = "UPDATE";
		this.entityName = "TEMPLATES";
		this.entityId = templateId;
		this.href = "/template/" + templateId;
		return this;
	}

	public CommandWrapperBuilder VerifyCredentials() {
		this.entityName = "CREDENTIALS";
		this.entityId = null;
		this.href = "/credentials";
		return this;
	}

	public CommandWrapperBuilder createRatableUsageMetric() {
		this.actionName = "CREATE";
		this.entityName = "RATABLEUSAGEMETRIC";
		this.entityId = null;
		this.href = "/ratableusagemetric";
		return this;
	}

	public CommandWrapperBuilder updateRatableUsageMetric(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "RATABLEUSAGEMETRIC";
		this.entityId = id;
		this.href = "/ratableusagemetric";
		return this;
	}

	public CommandWrapperBuilder deleteRatableUsageMetric(Long id) {
		this.actionName = "DELETE";
		this.entityName = "RATABLEUSAGEMETRIC";
		this.entityId = id;
		this.href = "/ratableusagemetric/" + id;
		return this;
	}

	public CommandWrapperBuilder createUnitOfmeasurement() {
		this.actionName = "CREATE";
		this.entityName = "UNITOFMEASUREMENT";
		this.entityId = null;
		this.href = "/unitofmeasurement";
		return this;
	}

	public CommandWrapperBuilder updateUnitOfmeasurement(Long id) {
		this.actionName = "UPDATE";
		this.entityName = "UNITOFMEASUREMENT";
		this.entityId = id;
		this.href = "/unitofmeasurement";
		return this;
	}

	public CommandWrapperBuilder createTimePeriod() {
		this.actionName = "CREATE";
		this.entityName = "TIMEPERIOD";
		this.entityId = null;
		this.href = "/timeperiod";
		return this;
	}

	public CommandWrapperBuilder createTimemodel() {
		this.actionName = "CREATE";
		this.entityName = "TIMEMODEL";
		this.entityId = null;
		this.href = "/timemodel";
		return this;
	}

	public CommandWrapperBuilder createUsageplan() {
		this.actionName = "CREATE";
		this.entityName = "USAGEPLAN";
		this.entityId = null;
		this.href = "/rateplan";
		return this;
	}

	public CommandWrapperBuilder createUsagebalance() {
		this.actionName = "CREATE";
		this.entityName = "USAGEBALANCE";
		this.entityId = null;
		this.href = "/usageratebalance";
		return this;
	}

	public CommandWrapperBuilder createUsageRateQuantityTier() {
		this.actionName = "CREATE";
		this.entityName = "USAGERATEQUANTITYTIER";
		this.entityId = null;
		this.href = "/usageratequantitytier";
		return this;
	}

	public CommandWrapperBuilder updateTimemodel(Long timemodelId) {
		this.actionName = "UPDATE";
		this.entityName = "TIMEMODEL";
		this.entityId = timemodelId;
		this.href = "/timemodel";
		return this;

	}

	public CommandWrapperBuilder deleteTimemodel(Long timemodelId) {
		this.actionName = "DELETE";
		this.entityName = "TIMEMODEL";
		this.entityId = timemodelId;
		this.href = "/timemodel/" + timemodelId;
		return this;
	}

	public CommandWrapperBuilder createTimeperiod() {
		this.actionName = "CREATE";
		this.entityName = "TIMEPERIOD";
		this.entityId = null;
		this.href = "/timeperiod";
		return this;
	}

	public CommandWrapperBuilder updateTimeperiod(Long timeperiodId) {
		this.actionName = "UPDATE";
		this.entityName = "TIMEPERIOD";
		this.entityId = timeperiodId;
		this.href = "/timeperiod";
		return this;
	}

	public CommandWrapperBuilder deleteTimePeriod(Long timeperiodId) {
		this.actionName = "DELETE";
		this.entityName = "TIMEPERIOD";
		this.entityId = timeperiodId;
		this.href = "/timeperiod";
		return this;
	}

	public CommandWrapperBuilder moveVoucher(Long officeId) {
		// TODO Auto-generated method stub
		this.actionName = "UPDATE";
		this.entityName = "VOUCHER_UPDATE";
		this.entityId = officeId;
		this.href = "/vouchers/" + officeId;
		return this;
	}

	public CommandWrapperBuilder exportVoucher(Long itemSaleId) {
		// TODO Auto-generated method stub
		this.actionName = "INSERT";
		this.entityName = "EXPORT_VOUCHER";
		this.entityId = itemSaleId;
		this.href = "/vouchers/" + itemSaleId;
		return this;
	}

	public CommandWrapperBuilder createRevOrder() {
		// TODO Auto-generated method stub
		this.actionName = "CREATE";
		this.entityName = "REVPAY";
		this.entityId = null;
		this.href = "/revpay/createorder";
		return this;
	}

	public CommandWrapperBuilder lockRevOrder(Long txref, String flwref) {
		// TODO Auto-generated method stub
		this.actionName = "UPDATE";
		this.entityName = "REVPAY";
		this.entityId = txref;
		this.supportedEntityType = flwref;
		this.href = "/revpay/orderlock";
		return this;
	}

	public CommandWrapperBuilder completeRevOrder() {
		// TODO Auto-generated method stub
		this.actionName = "COMPLETE";
		this.entityName = "REVPAY";
		this.entityId = null;
		this.href = "/revpay/orderCompleted";
		return this;
	}

	public CommandWrapperBuilder generateKey(Long userId) {
		// TODO Auto-generated method stub
		this.actionName = "GENSECRETKEY";
		this.entityName = "APPUSER";
		this.entityId = userId;
		this.href = "/generatekey/" + userId;
		return this;
	}

	public CommandWrapperBuilder validateKey(Long userId) {
		// TODO Auto-generated method stub
		this.actionName = "VALIDATEKEY";
		this.entityName = "APPUSER";
		this.entityId = userId;
		this.href = "/validatekey/"+ userId;
		return this;
	}

	public CommandWrapperBuilder resendOtpMessage() {
		this.actionName = "OTPMESSAGE";
		this.entityName = "LEASE";
		this.href = "/activationprocess/resendotp";
		return this;
		// TODO Auto-generated method stub
	}
}
