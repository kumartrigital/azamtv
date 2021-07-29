package org.mifosplatform.organisation.feemaster.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_fee_detail", uniqueConstraints = { @UniqueConstraint(columnNames = { "fee_id", "region_id"}, name = "feeid_with_region_uniquekey") })
public class FeeDetail extends AbstractPersistable<Long>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "region_id")
	private String regionId;

	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "is_deleted", nullable = false)
	private char isDeleted='N';
	
	@ManyToOne
    @JoinColumn(name="fee_id")
	private FeeMaster feeMaster;
	
	public FeeDetail(){}

	public FeeDetail(String regionId, BigDecimal amount) {
		
		this.regionId = regionId;
		this.amount = amount;
	}

	public void update(FeeMaster feeMaster) {
		
		this.feeMaster = feeMaster;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public char getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	

}
