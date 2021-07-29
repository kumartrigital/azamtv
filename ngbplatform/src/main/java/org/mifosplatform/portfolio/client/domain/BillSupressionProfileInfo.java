package org.mifosplatform.portfolio.client.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "c_supression_profile")
public class BillSupressionProfileInfo {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	private  Long id;
	
	@Column(name = "min_bill_value")
	private  Long  minBillValue;
	
	@Column(name = "no_of_cycle_suppress")
	private  Long  noOfCycleSuppress;
	
	
	
	public BillSupressionProfileInfo(Long id, Long minBillValue, Long noOfCycleSuppress){
		  
		 this.id = id;
		 this.minBillValue = minBillValue;
		 this.noOfCycleSuppress = noOfCycleSuppress;
		 
	 }


	public Long getMinBillValue() {
		return minBillValue;
	}


	public void setMinBillValue(Long minBillValue) {
		this.minBillValue = minBillValue;
	}


	public Long getNoOfCycleSuppress() {
		return noOfCycleSuppress;
	}


	public void setNoOfCycleSuppress(Long noOfCycleSuppress) {
		this.noOfCycleSuppress = noOfCycleSuppress;
	}


	public Long getId() {
		return id;
	}


	public void setClientId(Long id) {
		this.id = id;
	}
	

}
