package org.mifosplatform.infrastructure.jobs.service;

public class RadiusJobConstants {
	
	 public static final String ProvisioningSystem = "Radius";

	 public static final String Activation = "ACTIVATION";
	 public static final String ReConnection = "RECONNECTION";
	 public static final String DisConnection = "DISCONNECTION";
	 public static final String Message = "MESSAGE";
	 public static final String ChangePlan = "CHANGE_PLAN";
	 public static final String STBChange = "DEVICE_SWAP";
	 public static final String Terminate = "TERMINATE";
	 public static final String Client_Activation = "CLIENT ACTIVATION";
	 public static final String RENEWAL_AE = "RENEWAL_AE";
	 
	 public static final String FAILURE = "failure : ";
	 
	 public static final String RADIUS_VERSION_ONE = "version-1";
	 public static final String RADIUS_VERSION_TWO = "version-2";
	 public static final String RADIUS_HOTSPOT = "hotspot";
	 public static final String RADIUS_PPPOE = "pppoe";
	 
	 //for RADIUS_VERSION_TWO
	 public static final String RADCHECK_V2_CREATE_OUTPUT = "Radcheck is created";
	 public static final String RADIUS_V2_DELETE_OUTPUT = "Record is deleted";
	 
	 //for RADIUS_VERSION_ONE
	 public static final String RADCHECK_OUTPUT = "User added to database with ID:";
	 public static final String RADUSER_CREATE_OUTPUT = "User is created";
	 public static final String RADIUS_DELETE_OUTPUT = "the record is deleted";
}
