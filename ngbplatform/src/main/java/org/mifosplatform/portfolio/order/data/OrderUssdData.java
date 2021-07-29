package org.mifosplatform.portfolio.order.data;

public class OrderUssdData {

	private String status;

	private String message;

	private Double amount;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public OrderUssdData(String status, String message, Double amount) {
		super();
		this.status = status;
		this.message = message;
		this.amount = amount;
	}

	public OrderUssdData(String status, String message) {
		// TODO Auto-generated constructor stub
		this.status = status;
		this.message = message;
	}

	@Override
	public String toString() {
		return "OrderUssdData [status=" + status + ", message=" + message + ", amount=" + amount + "]";
	}

}
