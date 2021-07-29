package org.mifosplatform.organisation.officeadjustments.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

	/**
	 * @author hugo
	 *
	 */
	@Entity
	@Table(name = "m_adjustments")
	public class OfficeAdjustments extends AbstractAuditableCustom<AppUser, Long> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Column(name = "office_id", nullable = false, length = 20)
		private Long officeId;

		@Column(name = "adjustment_date", nullable = false)
		private Date adjustmentDate;

		@Column(name = "adjustment_code", nullable = false, length = 20)
		private int adjustmentCode;

		@Column(name = "adjustment_type", nullable = false, length = 12)
		private String adjustmentType;

		@Column(name = "adjustment_amount", nullable = true, length = 22)
		private BigDecimal amountPaid;

		@Column(name = "is_deleted")
		private char isDeleted = 'N';

		@Column(name = "remarks", nullable = true, length = 200)
		private String remarks;
		
		public OfficeAdjustments(){
			
		}
		
		public static OfficeAdjustments fromJson(final JsonCommand command) {
			
			    final LocalDate adjustmentDate = command.localDateValueOfParameterNamed("adjustmentDate");
		        final Long adjustmentCode = command.longValueOfParameterNamed("adjustmentCode");
		        final String adjustmentType = command.stringValueOfParameterNamed("adjustmentType");
		        final BigDecimal amountPaid = command.bigDecimalValueOfParameterNamed("amountPaid");
		        final String remarks = command.stringValueOfParameterNamed("remarks");
		        
				return new OfficeAdjustments(command.entityId(), adjustmentDate, adjustmentCode, adjustmentType, amountPaid, remarks);
		}
		
		public OfficeAdjustments(final Long officeId, final LocalDate adjustmentDate, final Long adjustmentCode, final String adjustmentType,
					final BigDecimal amountPaid, final String remarks) {
			
			this.officeId = officeId;
			this.adjustmentDate = adjustmentDate.toDate();
			this.adjustmentCode =(adjustmentCode).intValue();
			this.adjustmentType = adjustmentType;
			this.amountPaid = amountPaid;
			this.remarks = remarks;
			
		}
		
		public Long getOfficeId() {
			return officeId;
		}

		public void setOfficeId(final Long officeId) {
			this.officeId = officeId;
		}

		public Date getAdjustmentDate() {
			return adjustmentDate;
		}

		public void setAdjustmentDate(final Date adjustmentDate) {
			this.adjustmentDate = adjustmentDate;
		}

		public int getAdjustmentCode() {
			return adjustmentCode;
		}

		public void setAdjustmentCode(final int adjustmentCode) {
			this.adjustmentCode = adjustmentCode;
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

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(final String remarks) {
			this.remarks = remarks;
		}
}
