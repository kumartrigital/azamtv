package org.mifosplatform.logistics.item.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_item_audit")
public class ItemMasterAudit extends AbstractPersistable<Long>{

	
	@Column(name = "itemmaster_id")
	private Long itemMasterId;
	
	@Column(name = "region_id")
	private String regionId;
	
	@Column(name = "item_code")
	private String itemCode;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;
	
	@Column(name = "changed_date")
	private Date changedDate;
	
	public ItemMasterAudit(){}
	
	
	public ItemMasterAudit(Long itemId,int existingUnitPrice, String regionId, JsonCommand command) {
		
		final String itemCode = command.stringValueOfParameterNamed("itemCode");
		final BigDecimal unitPrice = new BigDecimal(existingUnitPrice);
		//final LocalDate changedDate = command.localDateValueOfParameterNamed("changedDate");
		final Date changedDate = DateUtils.getDateOfTenant();
		this.itemMasterId = itemId;
		this.itemCode = itemCode;
		this.unitPrice = unitPrice;
		this.changedDate = changedDate;
		this.regionId = regionId;
		
	}


	public String getItemCode() {
		return itemCode;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public Long getItemMasterId() {
		return itemMasterId;
	}

}
