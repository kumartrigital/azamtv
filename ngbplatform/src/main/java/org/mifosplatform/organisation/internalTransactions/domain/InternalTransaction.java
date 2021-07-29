package org.mifosplatform.organisation.internalTransactions.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.finance.adjustment.domain.Adjustment;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;



@Entity
@Table(name = "b_internal_transaction")
public class InternalTransaction extends AbstractPersistable<Long> {


	@Column(name = "office_id", nullable = false)
	private Long officeId;

	@Column(name = "client_id", nullable = false)
	private Long clientId;
	
	@Column(name = "transaction_amount", nullable = false)
	private BigDecimal transactionAmount;

	@Column(name = "transaction_date", nullable = false)
	private Date transactionDate;

	
	@Column(name = "is_deleted", nullable = false, length = 200)
	private char isDeleted;



	public InternalTransaction() {

	}
	

	public InternalTransaction(final Long officeId, final Long clientId,
			final BigDecimal transactionAmount, final LocalDate transactionDate) {
		this.officeId = officeId;
		this.clientId = clientId;
		this.transactionAmount = transactionAmount;
		this.transactionDate = transactionDate.toDate();
		this.isDeleted = 'N';

	}

	
	public static InternalTransaction fromJson(final JsonCommand command) {
		
		final Long officeId = command.longValueOfParameterNamed("officeId");
	    final Long clientId = command.longValueOfParameterNamed("clientId");
	    final BigDecimal transactionAmount = command.bigDecimalValueOfParameterNamed("transactionAmount");
        final LocalDate transactionDate = command.localDateValueOfParameterNamed("transactionDate");
        return new InternalTransaction(officeId,clientId,transactionAmount,transactionDate);
    }
	
	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	
	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void delete(){
		this.isDeleted = 'Y';
	}
	

}
