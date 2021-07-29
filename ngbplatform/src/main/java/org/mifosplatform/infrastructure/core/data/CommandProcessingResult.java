/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.core.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the successful result of an REST API call that results in
 * processing a command.
 */
public class CommandProcessingResult {

    private Long commandId;
    private Long officeId;
    private final Long groupId;
    private final Long clientId;
    private final Long loanId;
    private final Long savingsId;
    private final Long resourceId;
    private final String transactionId;
    private final Map<String, Object> changes;
    @SuppressWarnings("unused")
    private final String resourceIdentifier;
    private Boolean rollbackTransaction;
    private Set<String> substances;
    private Object details;

    public static CommandProcessingResult fromDetails(final Long commandId, final Long officeId, final Long groupId, final Long clientId,
            final Long loanId, final Long savingsId, final String resourceIdentifier, final Long entityId, final String transactionId,
            final Map<String, Object> changes, final Boolean rollbackTransaction) {
        return new CommandProcessingResult(commandId, officeId, groupId, clientId, loanId, savingsId, resourceIdentifier, entityId,
                transactionId, changes,rollbackTransaction);
    }
    
    public static CommandProcessingResult fromDetails(final Long commandId, final Long officeId, final Long groupId, final Long clientId,
            final Long loanId, final Long savingsId, final String resourceIdentifier, final Long entityId, final String transactionId,
            final Map<String, Object> changes, final Boolean rollbackTransaction,final Set<String> substances) {
        return new CommandProcessingResult(commandId, officeId, groupId, clientId, loanId, savingsId, resourceIdentifier, entityId,
                transactionId, changes,rollbackTransaction, substances);
    }

    public static CommandProcessingResult commandOnlyResult(final Long commandId) {
        return new CommandProcessingResult(null, null, commandId, null);
    }
    public static CommandProcessingResult parsingResult(final Object details) {
        return new CommandProcessingResult(details);
    }

    
    public static CommandProcessingResult resourceResult(final Long resourceId, final Long commandId) {
        return new CommandProcessingResult(resourceId, null, commandId, null);
    }

    public static CommandProcessingResult resourceResult(final Long resourceId, final Long commandId, final Map<String, Object> changes) {
        return new CommandProcessingResult(resourceId, null, commandId, changes);
    }

    public static CommandProcessingResult subResourceResult(final Long resourceId, final Long subResourceId, final Long commandId) {
        return new CommandProcessingResult(resourceId, subResourceId, commandId, null);
    }

    public static CommandProcessingResult subResourceResult(final Long resourceId, final Long subResourceId, final Long commandId,
            final Map<String, Object> changes) {
        return new CommandProcessingResult(resourceId, subResourceId, commandId, changes);
    }

    public static CommandProcessingResult withChanges(final Long resourceId, final Map<String, Object> changes) {
        return new CommandProcessingResult(resourceId, null, null, changes);
    }

    public static CommandProcessingResult empty() {
        return new CommandProcessingResult(null, null, null, null);
    }

    /*
     * Deprecated
     */
    public CommandProcessingResult(final Long entityId) {
        if (entityId != null) {
            this.resourceIdentifier = entityId.toString();
        } else {
            this.resourceIdentifier = null;
        }
        this.resourceId = entityId;
        this.officeId = null;
        this.groupId = null;
        this.clientId = null;
        this.loanId = null;
        this.savingsId = null;
        this.transactionId = null;
        this.changes = new HashMap<String, Object>();
    }
    

    public CommandProcessingResult(final Long entityId,final Long clientId) {
        if (entityId != null) {
            this.resourceIdentifier = entityId.toString();
        } else {
            this.resourceIdentifier = null;
        }
        this.resourceId = entityId;
        this.officeId = officeId;
        this.groupId = null;
        this.clientId = clientId;
        this.loanId = null;
        this.savingsId = null;
        this.transactionId = null;
        this.changes = new HashMap<String, Object>();
    }
    

    
    private CommandProcessingResult(final Long commandId, final Long officeId, final Long groupId, final Long clientId, final Long loanId,
            final Long savingsId, final String resourceIdentifier, final Long resourceId, final String transactionId,
            final Map<String, Object> changesOnly,final Boolean rollbackTransaction) {
        this.commandId = commandId;
        this.officeId = officeId;
        this.groupId = groupId;
        this.clientId = clientId;
        this.loanId = loanId;
        this.savingsId = savingsId;
        this.resourceIdentifier = resourceIdentifier;
        this.resourceId = resourceId;
        this.changes = changesOnly;
        this.transactionId = transactionId;
        this.rollbackTransaction = rollbackTransaction;
    }

    private CommandProcessingResult(final Long commandId, final Long officeId, final Long groupId, final Long clientId, final Long loanId,
            final Long savingsId, final String resourceIdentifier, final Long resourceId, final String transactionId,
            final Map<String, Object> changesOnly,final Boolean rollbackTransaction,Set<String> substances) {
        this.commandId = commandId;
        this.officeId = officeId;
        this.groupId = groupId;
        this.clientId = clientId;
        this.loanId = loanId;
        this.savingsId = savingsId;
        this.resourceIdentifier = resourceIdentifier;
        this.resourceId = resourceId;
        this.changes = changesOnly;
        this.transactionId = transactionId;
        this.rollbackTransaction = rollbackTransaction;
        this.substances = substances;
    }
    
    private CommandProcessingResult(final Long resourceId, final Long officeId, final Long commandId, final Map<String, Object> changesOnly) {
        if (resourceId != null) {
            this.resourceIdentifier = resourceId.toString();
        } else {
            this.resourceIdentifier = null;
        }
        this.resourceId = resourceId;
        this.officeId = officeId;
        this.groupId = null;
        this.clientId = null;
        this.loanId = null;
        this.savingsId = null;
        this.transactionId = null;
        this.commandId = commandId;
        this.changes = changesOnly;
    }
   public CommandProcessingResult(final String entityId){
	   this.transactionId=null;
	   this.commandId = null;
       this.officeId = null;
       this.groupId = null;
       this.clientId = null;
       this.loanId = null;
       this.savingsId = null;
       this.resourceIdentifier = entityId;
       this.resourceId = null;
       this.changes = null;
   }
   public CommandProcessingResult(final String entityId,final Long clientId){
	   this.transactionId=null;
	   this.commandId = null;
       this.officeId = null;
       this.groupId = null;
       this.clientId = clientId;
       this.loanId = null;
       this.savingsId = null;
       this.resourceIdentifier = entityId;
       this.resourceId = null;
       this.changes = null;
   }
   
  
    
    public CommandProcessingResult(Object details) {
	// TODO Auto-generated constructor stub
    	this.transactionId=null;
 	   this.commandId = null;
        this.officeId = null;
        this.groupId = null;
        this.clientId = null;
        this.loanId = null;
        this.savingsId = null;
        this.resourceIdentifier = null;
        this.resourceId = null;
        this.changes = null;
    	this.details = details;
}

	public Long commandId() {
        return this.commandId;
    }

    public Long resourceId() {
        return this.getResourceId();
    }

    public Long getOfficeId() {
        return this.officeId;
    }

    public void setOfficeId(final Long officeId) {
        this.officeId = officeId;
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

    public String getTransactionId() {
        return this.transactionId;
    }

    public Map<String, Object> getChanges() {
        Map<String, Object> checkIfEmpty = null;
        if (this.changes != null && !this.changes.isEmpty()) {
            checkIfEmpty = this.changes;
        }
        return checkIfEmpty;
    }

    public boolean hasChanges() {
        final boolean noChanges = this.changes == null || this.changes.isEmpty();
        return !noChanges;
    }
    
    public boolean isRollbackTransaction() {
    	return this.rollbackTransaction != null && this.rollbackTransaction;
    	}
    	public void setRollbackTransaction(Boolean rollbackTransaction) {
    	this.rollbackTransaction = rollbackTransaction;
    	}

		public String getResourceIdentifier() {
			return resourceIdentifier;
		}

		public Set<String> getSubstances() {
			return substances;
		}

		public void setSubstances(Set<String> substances) {
			this.substances = substances;
		}

		public Long getResourceId() {
			return resourceId;
		}

		
    	
		
}