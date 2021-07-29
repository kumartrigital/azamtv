package org.mifosplatform.cms.journalvoucher.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_jv_transaction")
public class JournalVoucher extends AbstractPersistable<Long>{
	
	/*@Column(name = "ref_id")
	private Long referenceId;
	
	@Column(name = "client_id")
	private Long clientId;
	
	@Column(name = "jv_date")
	private Date transactionDate;

	@Column(name = "jv_description")
	private String description;

	@Column(name = "credit_amount")
	private Double creditAmount;

	@Column(name = "debit_amount")
	private Double debitAmount;*/
	
	@Column(name = "posting_date")
	private Date postingDate;

	@Column(name = "notes")
	private String notes;
	
	@Column(name = "is_deleted")
	private char isDeleted = 'N';
	
	public JournalVoucher(){
		
	}

	/*public JournalVoucher(Long refId, Date transactionDate, String description, Double creditAmount, Double debitAmount, Long clientId) {
		
		this.referenceId=refId;
		this.transactionDate=transactionDate;
		this.description=description;
		this.creditAmount=creditAmount;
		this.debitAmount=debitAmount;
		this.clientId=clientId;
	}*/
	
	public JournalVoucher(Date postingDate,String notes){
		this.postingDate = postingDate;
		this.notes = notes;
		
	}

	public Date getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	

	
}
