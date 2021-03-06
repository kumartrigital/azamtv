package org.mifosplatform.infrastructure.jobs.service;

public enum JobName {
//CHARING
    CHARGE("CHARGE"), 
    REQUESTOR("REQUESTOR"), 
    RESPONSOR("Responser"), 
    SIMULATOR("SIMULATOR"), 
    PUSH_NOTIFICATION("MESSAGING"),
    GENERATE_BILLING("BILLING"), 
    MESSAGE_MERGE("MERGE_MESSAGE"), 
    AUTO_EXIPIRY("AUTO_EXPIRY"), 
    Middleware("MIDDLEWARE"),
    EVENT_ACTION_PROCESSOR("EVENT_ACTIONS"), 
    REPORT_EMAIL("REPORTER"), 
    MAKE_INVOICE("INVOICE"), 
    RADIUS("RADIUS"), 
    EXPORT_DATA("EXPORTDATA"),
    RESELLER_COMMISSION("RESELLERCOMMISSION"), 
    REPROCESS("REPROCESS"), 
    AGING_DISTRIBUTION("AGINGDISTRIBUTION"),
    DISCONNECT_UNPAID_CUSTOMERS("DISCONNECT_UNPAID_CUSTOMERS"), 
    USAGE_CHARGES("USAGECHARGES"),  
    ESCALATION("ESCALATION"), 
    CATALOGUE("CATALOGUE"), 
    INACTIVECUSTLEASECHARGING("INACTIVECUSTLEASECHARGING"); 
	


    private final String name;

    JobName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}