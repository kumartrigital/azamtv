package org.mifosplatform.finance.adjustment.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.finance.clientbalance.domain.ClientBalance;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_adjustments")
public class Adjustment extends AbstractAuditableCustom<AppUser, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "client_id", nullable = false, length = 20)
	private Long clientId;

	@Column(name = "adjustment_date", nullable = false)
	private Date adjustmentDate;

	@Column(name = "adjustment_code", nullable = false, length = 10)
	private int adjustmentCode;

	@Column(name = "adjustment_type", nullable = false, length = 20)
	private String adjustmentType;

	@Column(name = "adjustment_amount", nullable = false, length = 20)
	private BigDecimal amountPaid;

	@Column(name = "bill_id", nullable = false, length = 20)
	private Long billPd;

	@Column(name = "external_id", nullable = false, length = 20)
	private Long externalId;


	@Column(name = "remarks", nullable = false, length = 200)
	private String remarks;

	@OrderBy(value = "id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "clientId", orphanRemoval = true)
	private List<ClientBalance> clientBalances = new ArrayList<ClientBalance>();

	@OrderBy(value = "id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "clientId", orphanRemoval = true)
	private List<Adjustment> adjustment = new ArrayList<Adjustment>();

	
	public static Adjustment fromJson(final JsonCommand command) {
        final LocalDate adjustmentDate = command.localDateValueOfParameterNamed("adjustment_date");
        final Long adjustmentCode = command.longValueOfParameterNamed("adjustment_code");
        final String adjustmentType = command.stringValueOfParameterNamed("adjustment_type");
        final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amount_paid");
        
        final Long billId = command.longValueOfParameterNamed("bill_id");
        final Long externalId = command.longValueOfParameterNamed("external_id");
        final String remarks = command.stringValueOfParameterNamed("Remarks");
        
        return new Adjustment(command.entityId(),adjustmentDate,adjustmentCode,adjustmentType,amountPaid,billId,externalId,remarks);
    }
	
	

	public Adjustment(final Long clientId, final LocalDate adjustmentDate,
			final Long adjustmentCode, final String adjustmentType,
			final BigDecimal amountPaid, final Long billId, final Long externalId,
			final String remarks) {
		this.clientId = clientId;
		this.adjustmentDate = adjustmentDate.toDate();
		this.adjustmentCode = (adjustmentCode).intValue();
		this.adjustmentType = adjustmentType;
		this.amountPaid = amountPaid;
		this.billPd = billId;
		this.externalId = externalId;
		this.remarks = remarks;

	}

	public static Adjustment fromJson(final Long clientId, final LocalDate adjustmentDate,
			final Long adjustmentCode, final String adjustmentType,
			final BigDecimal amountPaid, final Long billId, final Long externalId,
			final String remarks) {
		return new Adjustment(clientId, adjustmentDate, adjustmentCode,
				adjustmentType, amountPaid, billId, externalId, remarks);
	}

	public Adjustment() {

	}

	public void updateclientBalances(final ClientBalance clientBalance) {
		clientBalance.updateClient(clientId);
		this.clientBalances.add(clientBalance);

	}

	public void updateAdjustmen(final Adjustment adjustment)
	{
		adjustment.updateAdjustmen(adjustment);
		this.adjustment.add(adjustment);
	}


	public List<ClientBalance> getClientBalances() {
		return clientBalances;
	}

	public void updateBillId(final Long billId) {
	this.billPd=billId;

	}



	public Long getClientId() {
		return clientId;
	}



	public void setClientId(final Long clientId) {
		this.clientId = clientId;
	}



	public Date getAdjustmentDate() {
		return adjustmentDate;
	}



	public void setAdjustmentDate(final Date adjustmentDate) {
		this.adjustmentDate = adjustmentDate;
	}



	public Long getAdjustmentCode() {
		return Long.valueOf(adjustmentCode);
	}



	public void setAdjustmentCode(final Long adjustmentCode) {
		this.adjustmentCode = adjustmentCode.intValue();
	}



	public String getAdjustmentType() {
		return adjustmentType;
	}



	public void setAdjustmentType(final String adjustmentType) {
		this.adjustmentType = adjustmentType;
	}



	public BigDecimal getAmountPaid() {
		return amountPaid;
	}



	public void setAmountPaid(final BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}



	public Long getBillId() {
		return billPd;
	}



	public void setBillId(final Long billId) {
		this.billPd = billId;
	}



	public Long getExternalId() {
		return externalId;
	}



	public void setExternalId(final Long externalId) {
		this.externalId = externalId;
	}



	public String getRemarks() {
		return remarks;
	}



	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}



	public List<Adjustment> getAdjustment() {
		return adjustment;
	}



	public void setAdjustment(final List<Adjustment> adjustment) {
		this.adjustment = adjustment;
	}



	public void setClientBalancesfinal (final List<ClientBalance> clientBalances) {
		this.clientBalances = clientBalances;
	}
	
	

}