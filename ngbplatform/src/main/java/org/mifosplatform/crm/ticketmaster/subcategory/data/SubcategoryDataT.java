package org.mifosplatform.crm.ticketmaster.subcategory.data;

/**
 * @Written by H
 * for adding Subcategory items
 *
 */

public class SubcategoryDataT {
	
	private Long id;
	private Integer maincategory;
	private String subcategory;
	private Integer timetaken;

	
	public SubcategoryDataT(Long id,Integer maincategory, String subcategory,Integer timetaken){
		this.id=id;
		this.maincategory=maincategory;
		this.subcategory=subcategory;
		this.timetaken=timetaken;
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMaincategory() {
		return maincategory;
	}

	public void setMaincategory(Integer maincategory) {
		this.maincategory = maincategory;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public Integer getTimetaken() {
		return timetaken;
	}


	public void setTimetaken(Integer timetaken) {
		this.timetaken = timetaken;
	}

	

}
