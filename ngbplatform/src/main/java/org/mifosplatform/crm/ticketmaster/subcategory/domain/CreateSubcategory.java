package org.mifosplatform.crm.ticketmaster.subcategory.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * @Written by H
 * for adding Subcategory items
 *
 */

@Entity
@Table(name = "b_sub_category")
public class CreateSubcategory  extends AbstractPersistable<Long>{
	
/*	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;*/
	
	@Column(name = "main_category")
	private Integer maincategory;
	
	@Column(name = "sub_category")
	private String subcategory;

	@Column(name = "time_taken")
	private Integer timetaken;


	public CreateSubcategory(){}
	
	

	public CreateSubcategory(Integer maincategory,String subcategory, Integer timetaken ) {
		
		this.maincategory = maincategory;
		this.subcategory = subcategory;
		this.timetaken=timetaken;
		

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

	public static CreateSubcategory fromJson(JsonCommand command) {
		final Integer maincategory = command.integerValueOfParameterNamed("maincategory");
		final String subcategory  = command.stringValueOfParameterNamed("subcategory");
		final Integer timetaken =command.integerValueOfParameterNamed("timetaken");
		return new CreateSubcategory(maincategory, subcategory,timetaken);
	}


	
}
