/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.DataValidatorBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "stretchy_report", uniqueConstraints = { @UniqueConstraint(columnNames = { "report_name" }, name = "unq_report_name") })
public class Report extends AbstractPersistable<Long> {

    @Column(name = "report_name", nullable = false, unique = true)
    private String reportName;

    @Column(name = "report_type", nullable = false)
    private String reportType;

    @Column(name = "report_subtype")
    private String reportSubType;

    @Column(name = "report_category")
    private String reportCategory;

    @Column(name = "description")
    private String description;

    @Column(name = "core_report", nullable = false)
    private boolean coreReport;

    // only defines if report should appear in reference app UI List
    @Column(name = "use_report", nullable = false)
    private boolean useReport;

    @Column(name = "report_sql")
    private String reportSql;

    /*
     * @ManyToMany
     * 
     * @JoinTable(name = "stretchy_report_parameter", joinColumns =
     * 
     * @JoinColumn(name = "reportn_id"), inverseJoinColumns = @JoinColumn(name =
     * "parameter_id")) private List<BillItem> charges;
     */
    
    @LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "report", orphanRemoval = true)
	private List<ReportParameter> reportParameters = new ArrayList<ReportParameter>();
    
    public List<ReportParameter> getReportParameter() {
		return reportParameters;
	}

    public static Report fromJson(final JsonCommand command) {

        final String reportName = command.stringValueOfParameterNamed("reportName");
        final String reportType = command.stringValueOfParameterNamed("reportType");
        final String reportSubType = command.stringValueOfParameterNamed("reportSubType");
        final String reportCategory = command.stringValueOfParameterNamed("reportCategory");
        final String description = command.stringValueOfParameterNamed("description");
        final boolean useReport = command.booleanPrimitiveValueOfParameterNamed("useReport");
        final String reportSql = command.stringValueOfParameterNamed("reportSql");

        return new Report(reportName, reportType, reportSubType, reportCategory, description, useReport, reportSql);
    }

    protected Report() {
        //
    }

    public Report(final String reportName, final String reportType, final String reportSubType, final String reportCategory,
            final String description, final boolean useReport, final String reportSql) {
        this.reportName = reportName;
        this.reportType = reportType;
        this.reportSubType = reportSubType;
        this.reportCategory = reportCategory;
        this.description = description;
        this.coreReport = false;
        this.useReport = useReport;
        this.reportSql = reportSql;
        validate();
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(8);

        String paramName = "reportName";
        if (command.isChangeInStringParameterNamed(paramName, this.reportName)) {
            final String newValue = command.stringValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            this.reportName = StringUtils.defaultIfEmpty(newValue, null);
        }
        paramName = "reportType";
        if (command.isChangeInStringParameterNamed(paramName, this.reportType)) {
            final String newValue = command.stringValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            this.reportType = StringUtils.defaultIfEmpty(newValue, null);
        }
        paramName = "reportSubType";
        if (command.isChangeInStringParameterNamed(paramName, this.reportSubType)) {
            final String newValue = command.stringValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            this.reportSubType = StringUtils.defaultIfEmpty(newValue, null);
        }
        paramName = "reportCategory";
        if (command.isChangeInStringParameterNamed(paramName, this.reportCategory)) {
            final String newValue = command.stringValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            this.reportCategory = StringUtils.defaultIfEmpty(newValue, null);
        }
        paramName = "description";
        if (command.isChangeInStringParameterNamed(paramName, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            this.description = StringUtils.defaultIfEmpty(newValue, null);
        }
        paramName = "useReport";
        if (command.isChangeInBooleanParameterNamed(paramName, this.useReport)) {
            final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            this.useReport = newValue;
        }
        paramName = "reportSql";
        if (command.isChangeInStringParameterNamed(paramName, this.reportSql)) {
            final String newValue = command.stringValueOfParameterNamed(paramName);
            actualChanges.put(paramName, newValue);
            this.reportSql = StringUtils.defaultIfEmpty(newValue, null);
        }

        validate();

        if (!actualChanges.isEmpty()) {
            if (isCoreReport()) {
                for (final String key : actualChanges.keySet()) {
                    if (!(key.equals("useReport"))) { throw new PlatformDataIntegrityException(
                            "error.msg.only.use.report.can.be.updated.for.core.report",
                            "Only the Use Report field can be updated for Core Reports", key); }
                }
            }
        }

        return actualChanges;
    }

    public boolean isCoreReport() {
        return coreReport;
    }

    private void validate() {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("report");

        baseDataValidator.reset().parameter("reportName").value(this.reportName).notBlank().notExceedingLengthOf(100);

        baseDataValidator.reset().parameter("reportType").value(this.reportType).notBlank()
                .isOneOfTheseValues(new Object[] { "Table", "Pentaho", "Chart" });

        baseDataValidator.reset().parameter("reportSubType").value(this.reportSubType).notExceedingLengthOf(20);

        if (StringUtils.isNotBlank(this.reportType)) {
            if (this.reportType.equals("Chart")) {
                baseDataValidator.reset().parameter("reportSubType").value(this.reportSubType)
                        .cantBeBlankWhenParameterProvidedIs("reportType", this.reportType)
                        .isOneOfTheseValues(new Object[] { "Bar", "Pie","Bubble","Timeline" });
            } else {
                baseDataValidator.reset().parameter("reportSubType").value(this.reportSubType)
                        .mustBeBlankWhenParameterProvidedIs("reportType", this.reportType);
            }
        }

        baseDataValidator.reset().parameter("reportCategory").value(this.reportCategory).notExceedingLengthOf(45);

        if (StringUtils.isNotBlank(this.reportType)) {
            if ((this.reportType.equals("Table")) || (this.reportType.equals("Chart"))) {
                baseDataValidator.reset().parameter("reportSql").value(this.reportSql)
                        .cantBeBlankWhenParameterProvidedIs("reportType", this.reportType);
            } else {
                baseDataValidator.reset().parameter("reportSql").value(this.reportSql)
                        .mustBeBlankWhenParameterProvidedIs("reportType", this.reportType);
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

    public String getReportName() {
        return reportName;
    }

    public void addDetails(ReportParameter reportParameter) {
    	reportParameter.update(this);
		this.reportParameters.add(reportParameter);

	}

}
