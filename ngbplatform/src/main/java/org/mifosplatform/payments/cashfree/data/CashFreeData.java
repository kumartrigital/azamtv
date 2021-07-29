package org.mifosplatform.payments.cashfree.data;

public class CashFreeData {
	private String paymentToken;
	private String transactionId;
	
	public CashFreeData(){
		
	}
	
	public CashFreeData(String paymenToken, String transactionId){
		this.paymentToken = paymenToken;
		this.transactionId = transactionId;
	}
	
}
