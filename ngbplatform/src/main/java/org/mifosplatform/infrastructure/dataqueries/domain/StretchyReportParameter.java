package org.mifosplatform.infrastructure.dataqueries.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "stretchy_report_parameter")
public class StretchyReportParameter extends AbstractPersistable<Long> {



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "report_id")
	private Integer reportId;

	@Column(name = "parameter_id")
	private Integer parameterId;

	@Column(name = "report_parameter_name")
	private String reportParameterName;

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public Integer getParameterId() {
		return parameterId;
	}

	public void setParameterId(Integer parameterId) {
		this.parameterId = parameterId;
	}

	public String getReportParameterName() {
		return reportParameterName;
	}

	public void setReportParameterName(String reportParameterName) {
		this.reportParameterName = reportParameterName;
	}

}