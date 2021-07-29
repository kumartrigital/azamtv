package org.mifosplatform.finance.clientbalance.data;

import java.math.BigDecimal;

public class ClientBalanceData {
	private Long id;
	private Long clientId;
	private BigDecimal balanceAmount;
	private BigDecimal walletAmount;
	private Long clientServiceId;
	private Long resourceId;

	public ClientBalanceData(Long id, Long clientId, BigDecimal balanceAmount) {
		this.id = id;
		this.clientId = clientId;
		this.balanceAmount = balanceAmount;
	}

	public ClientBalanceData(BigDecimal balance) {
		this.balanceAmount = balance;
	}

	public ClientBalanceData(Long id, Long clientId, BigDecimal balanceAmount, BigDecimal walletAmount,
			Long clientServiceId, Long resourceId) {
		
		this.id = id;
		this.clientId = clientId;
		this.balanceAmount = balanceAmount;
		this.walletAmount = walletAmount;
		this.clientServiceId = clientServiceId;
		this.resourceId = resourceId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public BigDecimal getWalletAmount() {
		return walletAmount;
	}

	public void setWalletAmount(BigDecimal walletAmount) {
		this.walletAmount = walletAmount;
	}
	public Long getClientServiceId() {
		return clientServiceId;
	}

	public void setClientServiceI(Long clientServiceId) {
		this.clientServiceId = clientServiceId;
	}
	
	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

}
