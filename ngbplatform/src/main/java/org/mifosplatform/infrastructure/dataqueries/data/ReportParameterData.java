/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.data;

/* used to show list of parameters used by a report and also for getting a list of parameters available (the reportParameterName is left null */
final public class ReportParameterData {

	@SuppressWarnings("unused")
	private Long parameterId;
	@SuppressWarnings("unused")
	private String reportParameterName;
	@SuppressWarnings("unused")
	private String parameterName;
	
	private Long reportId;
	private Long reportParameterId;

	public ReportParameterData(final Long parameterId, final String reportParameterName,
			final String parameterName) {
		this.parameterId = parameterId;
		this.reportParameterName = reportParameterName;
		this.parameterName = parameterName;
	}
	
	public ReportParameterData(final Long reportParamId, final Long reportId, final Long parameterId, final String reportParameterName) {
		this.reportParameterId = reportParameterId;
		this.reportId = reportId;
		this.parameterId = parameterId;
		this.reportParameterName = reportParameterName;
		
	}
	
	public ReportParameterData(Long reportParameterId, Long parameterId, String reportParameterName,
			String parameterName) {
		this.reportParameterId = reportParameterId;
		this.parameterId = parameterId;
		this.reportParameterName = reportParameterName;
		this.parameterName = parameterName;
		
	}
	
	public Long getReportParameterId() {
		return reportParameterId;
	}

	public Long getReportId() {
		return reportId;
	}

	public Long getParameterId() {
		return parameterId;
	}

	public String getReportParameterName() {
		return reportParameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
	
	public void setReportParameterId(Long reportParameterId) {
		this.reportParameterId = reportParameterId;
	}
	
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}

	public void setReportParameterName(String reportParameterName) {
		this.reportParameterName = reportParameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	
	
}