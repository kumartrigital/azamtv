package org.mifosplatform.infrastructure.jobs.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mifosplatform.organisation.message.data.BillingMessageTemplateData;
import org.mifosplatform.portfolio.plan.data.BillRuleData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.JobParameterData;
import org.mifosplatform.scheduledjobs.scheduledjobs.data.ScheduleJobData;
import org.mifosplatform.template.domain.Template;

public class JobDetailData {

    private final Long jobId;
    private final String displayName;
    private final String name;
    private final Date nextRunTime;
    private final String initializingError;
    private final String cronExpression;
    private final String cronDescription;
    private final boolean active;
    private final boolean currentlyRunning;
    private final JobDetailHistoryData lastRunHistory;
    private  List<ScheduleJobData> queryData;
    private Collection<BillingMessageTemplateData> billingMessageDatas;
	private JobParameterData jobparameters;
	private Long historyId;
	private Collection<BillRuleData> provisionSysData;
	private List<Template> templateData;

    public JobDetailData(final Long jobId, final String displayName,final String name,final Date nextRunTime, final String initializingError,
            final String cronExpression,final boolean active, final boolean currentlyRunning, final JobDetailHistoryData lastRunHistory, 
            String cronDescription, Long historyId) {
        this.jobId = jobId;
        this.displayName = displayName;
        this.nextRunTime = nextRunTime;
        this.initializingError = initializingError;
        this.cronExpression = cronExpression;
        this.active = active;
        this.lastRunHistory = lastRunHistory;
        this.currentlyRunning = currentlyRunning;
        this.cronDescription=cronDescription;
        this.name=name;
        this.queryData=null;
        this.billingMessageDatas=null;
        this.historyId=historyId;
    }

	public Long getJobId() {
		return jobId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Date getNextRunTime() {
		return nextRunTime;
	}

	public String getInitializingError() {
		return initializingError;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public String getCronDescription() {
		return cronDescription;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isCurrentlyRunning() {
		return currentlyRunning;
	}

	public JobDetailHistoryData getLastRunHistory() {
		return lastRunHistory;
	}

	public String getName() {
		return name;
	}

	public void setQueryData(List<ScheduleJobData> queryData) {

		this.queryData=queryData;
		
	}

	public void setMessageData(Collection<BillingMessageTemplateData> templateData) {
		
		this.billingMessageDatas=templateData;
		
	}

	public void setJobParameters(JobParameterData data) {
		this.jobparameters=data;
		
	}

	public Collection<BillRuleData> getProvisionSysData() {
		return provisionSysData;
	}

	public void setProvisionSysData(Collection<BillRuleData> provisionSysData2) {
		this.provisionSysData = provisionSysData2;
	}

	public List<ScheduleJobData> getQueryData() {
		return queryData;
	}

	public Collection<BillingMessageTemplateData> getBillingMessageDatas() {
		return billingMessageDatas;
	}

	public JobParameterData getJobparameters() {
		return jobparameters;
	}

	public Long getHistoryId() {
		return historyId;
	}
	
	public void setTemplateData(List<Template> templateDatas) {
		
		this.templateData = templateDatas;
		
	}

	public List<Template> getTemplateData() {
		return templateData;
	}
    
	
    
	
    
}
