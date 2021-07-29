/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.commands.data;

import org.joda.time.DateTime;

/**
 * Immutable data object representing client data.
 */
public final class AuditData {

	
	private final Long id;
	private final String actionName;
	private final String entityName;
	private final Long resourceId;
	private final Long subresourceId;
	private final String maker;
	private final DateTime madeOnDate;
	private final String checker;
	private final DateTime checkedOnDate;
	private final String processingResult;
	private final String commandAsJson;
	private final String officeName;
	private final String groupName;
	private final String clientName;


	public AuditData(final Long id, final String actionName,final String entityName, final Long resourceId,
			final Long subresourceId, final String maker,final DateTime madeOnDate, final String checker,
			final DateTime checkedOnDate, final String processingResult,final String commandAsJson, final String officeName, 
			final String groupName,final String clientName) {

		this.id = id;
		this.actionName = actionName;
		this.entityName = entityName;
		this.resourceId = resourceId;
		this.subresourceId = subresourceId;
		this.maker = maker;
		this.madeOnDate = madeOnDate;
		this.checker = checker;
		this.checkedOnDate = checkedOnDate;
		this.commandAsJson = commandAsJson;
		this.processingResult = processingResult;
		this.officeName = officeName;
		this.groupName = groupName;
		this.clientName = clientName;
	}

	public Long getId() {
		return id;
	}

	public String getActionName() {
		return actionName;
	}

	public String getEntityName() {
		return entityName;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public Long getSubresourceId() {
		return subresourceId;
	}

	public String getMaker() {
		return maker;
	}

	public DateTime getMadeOnDate() {
		return madeOnDate;
	}

	public String getChecker() {
		return checker;
	}
	
	public DateTime getCheckedOnDate() {
		return checkedOnDate;
	}
	
	public String getProcessingResult() {
		return processingResult;
	}

	public String getCommandAsJson() {
		return commandAsJson;
	}

	public String getOfficeName() {
		return officeName;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getClientName() {
		return clientName;
	}
	
}