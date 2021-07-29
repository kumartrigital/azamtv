package org.mifosplatform.cms.journalvoucher.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_jv_transaction_details")
public class JournalVoucherDetails extends AbstractPersistable<Long>{
	
	@Column(name="transaction_id")
	private Long transactionId;
	
	@Column(name="party_id")
	private String partyId;
	
	@Column(name="party_type")
	private String partyType;
	
	@Column(name="type")
	private String type;
	
	@Column(name="account")
	private String account;
	
	@Column(name="amount")
	private Double amount;
	
	@Column(name = "is_deleted")
	private char isDeleted = 'N';
	
	public JournalVoucherDetails(){
		
	}
	
    public JournalVoucherDetails(Long transactionId,String partyId,String partyType,String type,String account,Double amount){
		this.transactionId = transactionId;
		this.partyId = partyId;
		this.partyType = partyType;
		this.type = type;
		this.account = account;
		this.amount = amount;
	}
	

}
