package org.mifosplatform.finance.clientbalance.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

@Entity
@Table(name = "b_client_balance")
public class ClientBalance {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	@Column(name = "client_id", nullable = false, length = 20)
	private Long clientId;

	@Column(name = "balance_amount", nullable = false, length = 20)
	private BigDecimal balanceAmount;

	@Column(name = "wallet_amount", nullable = false, length = 20)
	private BigDecimal walletAmount;

	@Column(name = "service_id", nullable = false, length = 20)
	private Long clientServiceId;

	@Column(name = "resource_id", nullable = false, length = 20)
	private Long resourceId;

	@Column(name = "valid_from", nullable = false, length = 20)
	private Date validFrom;

	@Column(name = "valid_to", nullable = false, length = 20)
	private Date validTo;

	@Column(name = "disputed_amount", nullable = false, length = 20)
	private BigDecimal disputedAmount;

	@Column(name = "reserved_amount", nullable = false, length = 20)
	private BigDecimal reservedAmount;

	@Column(name = "is_deleted", nullable = false)
	private char deleted = 'N';

	public static ClientBalance create(Long clientId, BigDecimal balanceAmount, char isWalletPayment,
			Long clientServiceId, Long currencyId) {

		return new ClientBalance(clientId, balanceAmount, isWalletPayment, clientServiceId, currencyId);
	}

	public ClientBalance(Long clientId, BigDecimal balanceAmount, char isWalletPayment, Long clientServiceId,
			Long currencyId) {

		this.clientId = clientId;
		if (isWalletPayment == 'Y') {
			this.walletAmount = balanceAmount;
			this.balanceAmount = BigDecimal.ZERO;
		} else {
			this.balanceAmount = balanceAmount;
			this.walletAmount = BigDecimal.ZERO;
		}
		this.clientServiceId = clientServiceId;
		this.resourceId = currencyId;
	}

	public ClientBalance() {

	}

	public ClientBalance(Long clientId, BigDecimal balanceAmount, char isWalletPayment) {

		this.clientId = clientId;
		if (isWalletPayment == 'Y') {
			this.walletAmount = balanceAmount;
			this.balanceAmount = BigDecimal.ZERO;
		} else {
			this.balanceAmount = balanceAmount;
			this.walletAmount = BigDecimal.ZERO;
		}
	}

	public ClientBalance(Long clientId, BigDecimal balanceAmount, char isWalletPayment, Long clientServiceId, Long currencyId,
			LocalDate validFrom, LocalDate validTo) {
		
		this.clientId = clientId;
		if (isWalletPayment == 'Y') {
			this.walletAmount = balanceAmount;
			this.balanceAmount = BigDecimal.ZERO;
		} else {
			this.balanceAmount = balanceAmount;
			this.walletAmount = BigDecimal.ZERO;
		}
		this.clientServiceId = clientServiceId;
		this.resourceId = currencyId;
		this.validFrom=validFrom.toDate();
		this.validTo=validTo.toDate();
		
	}

	

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public Long getId() {
		return id;
	}

	public void setBalanceAmount(BigDecimal balanceAmount, char iswalletEnable) {
		if (iswalletEnable == 'Y') {
			if (this.walletAmount != null)
				this.walletAmount = this.walletAmount.add(balanceAmount);
			else
				this.walletAmount = BigDecimal.ZERO.add(balanceAmount);
		} else {
			this.balanceAmount = this.balanceAmount.add(balanceAmount);
		}
	}

	public void updateClient(Long clientId) {
		this.clientId = clientId;
	}

	public BigDecimal getWalletAmount() {
		return walletAmount;
	}

	public void updateDueAmount(BigDecimal dueAmount) {

	}

	public static ClientBalance fromJson(JsonCommand command) {

		final Long clientId = command.longValueOfParameterNamed("clientId");
		final BigDecimal balance = command.bigDecimalValueOfParameterNamed("balance");
		return new ClientBalance(clientId, balance, 'N');
	}

	public void updateBalance(JsonCommand clientBalanceCommand) {

		final Long clientId = clientBalanceCommand.longValueOfParameterNamed("clientId");
		final String paymentType = clientBalanceCommand.stringValueOfParameterNamed("paymentType");
		BigDecimal amount = clientBalanceCommand.bigDecimalValueOfParameterNamed("amount");
		final Long clientServiceId = clientBalanceCommand.longValueOfParameterNamed("clientServiceId");
		final Long currencyId = clientBalanceCommand.longValueOfParameterNamed("currencyId");
		final String isWalletEnable = clientBalanceCommand.stringValueOfParameterNamed("isWalletEnable");

		if ("CREDIT".equalsIgnoreCase(paymentType)) {
			if (isWalletEnable == "Y") {
				if (this.walletAmount != null)
					this.walletAmount = this.walletAmount.subtract(amount);
				else
					this.walletAmount = BigDecimal.ZERO.subtract(amount);
			} else {
				this.balanceAmount = this.balanceAmount.subtract(amount);
			}
		} else {
			if (isWalletEnable == "Y") {
				if (this.walletAmount != null)
					this.walletAmount = this.walletAmount.add(amount);
				else
					this.walletAmount = BigDecimal.ZERO.add(amount);
			} else {
				this.balanceAmount = this.balanceAmount.add(amount);
			}
		}
		this.clientServiceId = clientServiceId;
		this.resourceId = currencyId;

	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;

	}

	public void setWalletAmount(BigDecimal balance) {
		this.walletAmount = balance;

	}

	public Long getClientServiceId() {
		return clientServiceId;
	}

	public void setClientServiceId(Long clientServiceId) {
		this.clientServiceId = clientServiceId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

}
