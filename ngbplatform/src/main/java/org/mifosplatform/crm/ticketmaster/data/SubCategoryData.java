package org.mifosplatform.crm.ticketmaster.data;

public class SubCategoryData {

	 private Long id;
	 private Long mainCategory;
	 private String subCategory;
	
	 public SubCategoryData(final Long id , final Long mainCategory, final String subCategory)
	 {
		 this.id=id;
		 this.mainCategory=mainCategory;
		 this.subCategory=subCategory;
		 
	 }
	 public SubCategoryData(final Long id , final String subCategory)
	 {
		 this.id=id;
		 this.subCategory=subCategory;
		 
	 }
	 
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getMainCtaegory() {
		return mainCategory;
	}
	public void setMainCtaegory(long mainCategory) {
		this.mainCategory = mainCategory;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
}
