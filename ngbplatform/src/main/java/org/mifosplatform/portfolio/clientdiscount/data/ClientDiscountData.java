package org.mifosplatform.portfolio.clientdiscount.data;

public class ClientDiscountData {

	
	private Long id;
	private String level;
	private String discountType;
	private Long discountValue;
	
	public ClientDiscountData(){
		
	}
	
	public ClientDiscountData(Long id,String level,String discountType,Long discountValue){
		this.id = id;
		this.level = level;
		this.discountType = discountType;
		this.discountValue = discountValue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public Long getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(Long discountValue) {
		this.discountValue = discountValue;
	}
	
	
	
}
