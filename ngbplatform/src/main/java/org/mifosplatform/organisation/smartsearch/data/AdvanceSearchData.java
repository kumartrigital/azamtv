package org.mifosplatform.organisation.smartsearch.data;

import org.joda.time.LocalDate;

public class AdvanceSearchData {
	private final Long id;
	private final Long clientId;
	private final String accountNo;
	private final String clientName;
	private final String category;
	private final LocalDate transactionDate;
	private final String status;
	private final String userName;
	

	public AdvanceSearchData(final Long id, final Long clientId, final String accountNo, final String clientName, 
			final LocalDate transactionDate, final String category, final String status, final String userName) {
             
		this.id = id;
		this.clientId = clientId;
		this.accountNo = accountNo;
		this.clientName = clientName;
		this.category = category;
		this.transactionDate = transactionDate;
		this.status = status;
		this.userName = userName;
		
	
	}

}
