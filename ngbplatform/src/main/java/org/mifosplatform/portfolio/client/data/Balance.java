package org.mifosplatform.portfolio.client.data;

public class Balance {

	private Long clientId;
	private String currency_id;
	private String amount;
	private String countryCode;

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getCurrency_id() {
		return currency_id;
	}

	public void setCurrency_id(String currency_id) {
		this.currency_id = currency_id;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Balance(Long clientId, String currency_id, String amount, String countryCode) {
		this.clientId = clientId;
		this.currency_id = currency_id;
		this.amount = amount;
		this.countryCode = countryCode;
	}

}
