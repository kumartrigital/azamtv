/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.dataqueries.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.dataqueries.data.ReportParameterData;
import org.mifosplatform.infrastructure.dataqueries.domain.Report;
import org.mifosplatform.infrastructure.dataqueries.domain.ReportParameter;
import org.mifosplatform.infrastructure.dataqueries.domain.ReportParameterRepository;
import org.mifosplatform.infrastructure.dataqueries.domain.ReportRepository;
import org.mifosplatform.infrastructure.dataqueries.exception.ReportNotFoundException;
import org.mifosplatform.infrastructure.dataqueries.serialization.ReportCommandFromApiJsonDeserializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.domain.PlanDetails;
import org.mifosplatform.portfolio.product.domain.Product;
import org.mifosplatform.portfolio.service.domain.ServiceDetails;
import org.mifosplatform.useradministration.domain.Permission;
import org.mifosplatform.useradministration.domain.PermissionRepository;
import org.mifosplatform.useradministration.exception.PermissionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ReportWritePlatformServiceImpl implements
		ReportWritePlatformService {

	private final static Logger logger = LoggerFactory
			.getLogger(ReportWritePlatformServiceImpl.class);

	private final PlatformSecurityContext context;
	private final ReportCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final ReportRepository reportRepository;
	private final PermissionRepository permissionRepository;
	private final FromJsonHelper fromApiJsonHelper;
	private final ReportParameterRepository reportParameterRepository;
	private final ReadReportingService readReportingService;

	@Autowired
	public ReportWritePlatformServiceImpl(
			final PlatformSecurityContext context,
			final ReportCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final ReportRepository reportRepository,
			final PermissionRepository permissionRepository,final FromJsonHelper fromApiJsonHelper,
			final ReportParameterRepository reportParameterRepository, final ReadReportingService readReportingService) {
		
		this.context = context;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.reportRepository = reportRepository;
		this.permissionRepository = permissionRepository;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.reportParameterRepository = reportParameterRepository;
		this.readReportingService = readReportingService;
	}

	@Transactional
	@Override
	public CommandProcessingResult createReport(final JsonCommand command) {

		try {
			context.authenticatedUser();

			this.fromApiJsonDeserializer.validate(command.json());

			Report report = Report.fromJson(command);
			//final Permission permission = new Permission("report",report.getReportName(),"READ");

		    final JsonArray reportParametersArray = command.arrayOfParameterNamed("reportParameters").getAsJsonArray();
		    report = assembleSetOfReportParameters(reportParametersArray, report);
			this.reportRepository.save(report);

			return new CommandProcessingResultBuilder()
					.withCommandId(command.commandId())
					.withEntityId(report.getId()).build();
		} catch (DataIntegrityViolationException dve) {
			handleReportDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult updateReport(final Long reportId,
			final JsonCommand command) {

		try {
			context.authenticatedUser();

			this.fromApiJsonDeserializer.validate(command.json());

			final Report report = this.reportRepository.findOne(reportId);
			if (report == null) {
				throw new ReportNotFoundException(reportId);
			}
			
			List<ReportParameter> reportParameter=new ArrayList<>(report.getReportParameter());
			final JsonArray reportParametersArray = command.arrayOfParameterNamed("reportParameters").getAsJsonArray();
	        String[] childReportParametersArray =null;
	        childReportParametersArray=new String[reportParametersArray.size()];
		    for(int i=0; i<reportParametersArray.size();i++){
		    	childReportParametersArray[i] =reportParametersArray.get(i).toString();
		     }
			 for (String childReportParameters : childReportParametersArray) {
				  
				    final JsonElement element = fromApiJsonHelper.parse(childReportParameters);
				    final Long reportParameterId = fromApiJsonHelper.extractLongNamed("reportParameterId", element);
					final Long parameterId = fromApiJsonHelper.extractLongNamed("parameterId", element);
					final String reportParameterName = fromApiJsonHelper.extractStringNamed("reportParameterName", element);
					if(reportParameterId != null){
						ReportParameter reportParameters =this.reportParameterRepository.findOne(reportParameterId);
					if(reportParameters != null){
						reportParameters.setParameterId(parameterId);
						reportParameters.setReportParameterName(reportParameterName);
						this.reportParameterRepository.saveAndFlush(reportParameters);
						if(reportParameter.contains(reportParameters)){
							reportParameter.remove(reportParameters);
						}
					 }
					}else {
						ReportParameter newDetails = new ReportParameter(parameterId, reportParameterName);
						report.addDetails(newDetails);
					}
					
			  }
			report.getReportParameter().removeAll(reportParameter);
			

			final Map<String, Object> changes = report.update(command);
			if (!changes.isEmpty()) {
				this.reportRepository.saveAndFlush(report);
			}

			return new CommandProcessingResultBuilder()
					.withCommandId(command.commandId())
					.withEntityId(report.getId()).with(changes).build();
		} catch (DataIntegrityViolationException dve) {
			handleReportDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}
	}

	@Transactional
	@Override
	public CommandProcessingResult deleteReport(final Long reportId) {

		final Report report = this.reportRepository.findOne(reportId);
		if (report == null) {
			throw new ReportNotFoundException(reportId);
		}

		if (report.isCoreReport()) {
			throw new PlatformDataIntegrityException(
					"error.msg.cant.delete.core.report",
					"Core Reports Can't be Deleted", "");
		}
		

		/*final Permission permission = this.permissionRepository.findOneByCode("READ" + "_" + report.getReportName());
		if (permission == null) {
			throw new PermissionNotFoundException("READ" + "_" + report.getReportName());
		}*/

		this.reportRepository.delete(report);
		//this.permissionRepository.delete(permission);

		return new CommandProcessingResultBuilder().withEntityId(reportId)
				.build();
	}

	/*
	 * Guaranteed to throw an exception no matter what the data integrity issue
	 * is.
	 */
	private void handleReportDataIntegrityIssues(final JsonCommand command,
			DataIntegrityViolationException dve) {

		Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("unq_report_name")) {
			final String name = command
					.stringValueOfParameterNamed("reportName");
			throw new PlatformDataIntegrityException(
					"error.msg.report.duplicate.name", "A report with name '"
							+ name + "' already exists", "name", name);
		}

		logger.error(dve.getMessage(), dve);
		throw new PlatformDataIntegrityException(
				"error.msg.report.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "
						+ realCause.getMessage());
	}
	
	private Report assembleSetOfReportParameters(JsonArray reportParametersArray, Report report) {

		String[]  childReportParametersArray = null;
		childReportParametersArray = new String[reportParametersArray.size()];
		if(reportParametersArray.size() > 0){
			for(int i = 0; i < reportParametersArray.size(); i++){
				childReportParametersArray[i] = reportParametersArray.get(i).toString();
			}
			
			for (final String childReportParameters : childReportParametersArray) {
				final JsonElement element = fromApiJsonHelper.parse(childReportParameters);
				final Long parameterId = fromApiJsonHelper.extractLongNamed("parameterId", element);
				final String reportParameterName = fromApiJsonHelper.extractStringNamed("reportParameterName", element);
				
				ReportParameter reportParameter = new ReportParameter(parameterId,reportParameterName);
				report.addDetails(reportParameter);
				
			}
		}
        

        return report;
    }
}