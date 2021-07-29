package org.mifosplatform.finance.adjustment.data;

import java.util.ArrayList;
import java.util.List;

import org.mifosplatform.billing.discountmaster.data.DiscountValues;

public class AdjustmentCodeData {

	private final List<AdjustmentData> data;
	private List<DiscountValues> discountOptions;

	public AdjustmentCodeData(final List<AdjustmentData> data) {
		this.data=data;
		this.discountOptions=setadjustment_type();
	}

	public List<AdjustmentData> getData() {
		return data;
	}

	public List<DiscountValues> setadjustment_type() {

		discountOptions = new ArrayList<DiscountValues>();
		discountOptions.add(new DiscountValues("CREDIT"));
		discountOptions.add(new DiscountValues("DEBIT"));

		return discountOptions;
	}


}
