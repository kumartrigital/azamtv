package org.mifosplatform.finance.payments.data;

import java.util.Collection;

import org.joda.time.LocalDate;

public class McodeData {

	private Long id;
	private String mCodeValue;
	private LocalDate startDate;
	private Collection<McodeData> paymodeDatas;

	public static McodeData instance(final Long id, final String paymodeCode) {

		return new McodeData(id, paymodeCode);
	}
	

	public Long getId() {
		return id;
	}

	public String getPaymodeCode() {
		return mCodeValue;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public Collection<McodeData> getPaymodeDatas() {
		return paymodeDatas;
	}

	public McodeData(final Long id, final String paymodeCode) {
		this.id = id;
		this.mCodeValue = paymodeCode;

	}

	public McodeData(final Collection<McodeData> data) {
		this.paymodeDatas = data;
		this.startDate = new LocalDate();
	}


	public static McodeData instance1(final Long codeId) {
		
		return  new McodeData(codeId);
	}
	public McodeData(final Long id){
		this.id=id;
	}
	

}
