package org.mifosplatform.finance.officebalance.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;
import org.mifosplatform.infrastructure.core.api.JsonCommand;


@Entity
@Table(name = "m_office_balance")
public class OfficeBalance extends AbstractAuditableCustom<AppUser,Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "office_id", nullable = false, length = 20)
	private Long officeId;

	@Column(name = "balance_amount", nullable = false, length = 20)
	private BigDecimal balanceAmount;
	
	@Column(name = "credit_limit", nullable = false, length = 20)
	private BigDecimal creditLimit;

	public static OfficeBalance create(final Long officeId,final BigDecimal balanceAmount) {

		return new OfficeBalance(officeId, balanceAmount);
	}

	public OfficeBalance(Long officeId, BigDecimal balanceAmount) {

		this.officeId = officeId;
		this.balanceAmount = balanceAmount;

	}

	public OfficeBalance() {

	}

	public OfficeBalance(Long entityId, BigDecimal balanceAmount, BigDecimal creditLimitAmount) {
		this.officeId = officeId;
		this.balanceAmount = balanceAmount;
		this.creditLimit = creditLimitAmount;	
	}

	public Long getofficeId() {
		return officeId;
	}

	public void setofficeId(Long officeId) {
		this.officeId = officeId;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {

		this.balanceAmount = balanceAmount;

	}
	
	public BigDecimal getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}
	
	public void updateBalance(String paymentType, BigDecimal amountPaid) {

		if ("CREDIT".equalsIgnoreCase(paymentType)) {
			this.balanceAmount = this.balanceAmount.subtract(amountPaid);
		} else {
			this.balanceAmount = this.balanceAmount.add(amountPaid);
		}

	}

	
	
	/**
	 * Get Credit Limit amount from request json.
	 * @param command
	 * @return
	 */
	
	// TODO HEMANTH: CREDIT LIMIT
	public static OfficeBalance officeCreditLimitFromJson(final JsonCommand command) {
        final BigDecimal creditLimitAmount = command.bigDecimalValueOfParameterNamed("creditLimitAmount");
		return new OfficeBalance(command.entityId(), null, creditLimitAmount);
	}
	
}


