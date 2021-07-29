package org.mifosplatform.portfolio.property.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_property_history")
public class PropertyTransactionHistory extends AbstractAuditableCustom<AppUser,Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "transaction_date")
	private Date transactionDate;

	@Column(name = "ref_id")
	private Long refId;

	@Column(name = "ref_desc")
	private String refDesc;

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "property_code")
	private String propertyCode;

	
    public PropertyTransactionHistory() {
		
	}
	
	public PropertyTransactionHistory(final LocalDate transactionDate,final Long refId,final String refDesc, 
			final Long clientId, final String propertyCode) {
		
		this.transactionDate = transactionDate.toDate();
		this.refId = refId;
		this.refDesc = refDesc;
		this.clientId = (clientId !=null) ? clientId : null;
		this.propertyCode = propertyCode;
	}
	
	
	
}
