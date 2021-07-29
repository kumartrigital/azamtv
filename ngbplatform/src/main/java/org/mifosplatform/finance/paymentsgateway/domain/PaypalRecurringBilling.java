package org.mifosplatform.finance.paymentsgateway.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * 
 * @author ashokreddy
 *
 */

@Entity
@Table(name = "b_recurring")
public class PaypalRecurringBilling extends AbstractPersistable<Long> {

	@Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "subscriber_id", nullable = false)
    private String subscriberId;
    
    @Column(name = "order_id", nullable = true)
    private Long orderId;
    
    @Column(name = "is_deleted", nullable = false)
	private char deleted='N';
    
    public PaypalRecurringBilling(){
    	
    }
    
    public PaypalRecurringBilling(Long clientId, String subscriberId){
    	
    	this.clientId = clientId;
    	this.subscriberId = subscriberId;
    }


	public String getSubscriberId() {
		return subscriberId;
	}


	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}


	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public char getDeleted() {
		return deleted;
	}

	public void updateStatus() {
		this.deleted = 'Y';
	}
	
	
    
}