package org.mifosplatform.infrastructure.dataqueries.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.portfolio.service.domain.ServiceMaster;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "stretchy_report_parameter")
public class ReportParameter extends AbstractPersistable<Long>{
	
	
	/*@Column(name = "report_id")
    private Long reportId;*/

    @Column(name = "parameter_id")
    private Long parameterId;
    
    @Column(name = "report_parameter_name")
    private String reportParameterName;
    
    @ManyToOne
	@JoinColumn(name = "report_id")
	private Report report;
    
    public ReportParameter(){}
    
    
public ReportParameter(Long parameterId, String reportParameterName){
	
    	this.parameterId = parameterId;
    	this.reportParameterName = reportParameterName;
    	
    }



	public Long getParameterId() {
		return parameterId;
	}


	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}


	public String getReportParameterName() {
		return reportParameterName;
	}


	public void setReportParameterName(String reportParameterName) {
		this.reportParameterName = reportParameterName;
	}
    
	public void update(Report report) {
		this.report = report;
	}

}
