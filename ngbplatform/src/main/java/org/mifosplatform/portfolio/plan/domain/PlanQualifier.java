package org.mifosplatform.portfolio.plan.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "b_plan_qualifier")
public class PlanQualifier {
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;


	@ManyToOne
    @JoinColumn(name="plan_id")
    private Plan plan;
	
	@Column(name = "partner_id")
	private Long partnerId;
	
	@Column(name = "is_deleted")
	private char isDeleted ='N';
	
	
	public PlanQualifier(){
		
	}
	public PlanQualifier(Long partnerId){
		
		this.partnerId = partnerId;
	}
	public void update(Plan plan) {
        this.plan = plan;
		
	}
}
