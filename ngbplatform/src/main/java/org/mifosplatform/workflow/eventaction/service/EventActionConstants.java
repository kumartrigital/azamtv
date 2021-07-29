package org.mifosplatform.workflow.eventaction.service;

public class EventActionConstants {
	
	//Events
	public static final String EVENT_CREATE_PAYMENT="Create Payment";
	public static final String EVENT_CREATE_CLIENT="Create Client";
	public static final String EVENT_CREATE_LIVE_EVENT="Create Live Event";
	public static final String EVENT_CLOSE_CLIENT="Close Client";
	public static final String EVENT_CREATE_ORDER="Order Booking";
	public static final String EVENT_CHANGE_ORDER="change order";
	public static final String EVENT_ACTIVE_ORDER="Order activation";
	public static final String EVENT_CHANGE_PLAN ="Change Plan";
	public static final String EVENT_ORDER_RENEWAL="Order Renewal";
	public static final String EVENT_CREATE_TICKET="Create Ticket";
	public static final String EVENT_EDIT_TICKET="Add Comment";
	public static final String EVENT_CLOSE_TICKET="Close Ticket";
	public static final String EVENT_EVENT_ORDER="Event Order";
	public static final String EVENT_SCHEDULE_ORDER_SUSPEND="Schedule Order Suspend";
	public static final String EVENT_SCHEDULE_ORDER_DISCONNECT="Schedule Order Disconnect";
	public static final String EVENT_PAYPAL_RECURRING_DISCONNECT_ORDER="Recurring DisConnect Order";
	public static final String EVENT_PAYPAL_RECURRING_RECONNECTION_ORDER="Recurring ReConnection Order";
	public static final String EVENT_PAYPAL_RECURRING_TERMINATE_ORDER="Recurring Terminate Order";
	public static final String EVENT_DISCONNECTION_ORDER="Order disconnection";
	public static final String EVENT_RECONNECTION_ORDER="Order reconnection";
	public static final String EVENT_NOTIFY_PAYMENT="Notify Payment Receipt";
	public static final String EVENT_SEND_PAYMENT = "Send Payment Receipt";
	public static final String EVENT_TOPUP_INVOICE_MAIL = "Topup Invoice Mail";
	public static final String EVENT_NOTIFY_ORDER_TERMINATE="Notify Order Terminate";
	public static final String EVENT_PROVISION_CONFIRM ="Provisioning Confirmation";
	public static final String EVENT_NOTIFY_TECHNICALTEAM = "Notify Technical Team";
	public static final String EVENT_ADD_PLAN="Add Plan";
	public static final String EVENT_SUSPENSION_ORDER="Order Suspension";
	public static final String EVENT_REACTIVE_ORDER="Order Reactivation";
	public static final String EVENT_TERMINATE_ORDER="Order Termination";
	public static final String EVENT_RENEWAL_AE_ORDER="Order Renewal AE";
	public static final String EVENT_CREATE_LEAD = "Create Lead";
	public static final String EVENT_FOLLOWUP_LEAD = "FollowUp Lead";
	public static final String EVENT_CUSTOMER_ACTIVATION="Customer Activation";
	
	public static final String EVENT_CREATE_PAYMENT_ADJ="NOTIFY_PAYMENT_ADJ";
	
	public static final String EVENT_CREATE_PAYMENT_REVERSAL="NOTIFY_PAYMENT_REVERSAL";
	public static final String EVENT_CREATE_ORDER_WORKFLOW="Create Order Workflow";

	//Actions
	public static final String ACTION_SEND_MAIL="Send Mail";
	public static final String ACTION_SEND_CUSTOMER_EMAIL="Send Customer Email";
	public static final String ACTION_SEND_CUSTOMER_SMS="SEND  Customer SMS";
	public static final String ACTION_SEND_ACTIVATION_SMS="SEND_Activation_SMS";

	public static final String ACTION_SEND_MESSAGE="SEND SMS";
	public static final String ACTION_RENEWAL="Renewal";
	public static final String ACTION_INVOICE="Invoice";
	public static final String ACTION_NEW="New";
	public static final String ACTION_ACTIVE="Active";
	public static final String ACTION_DISCONNECT="DISCONNECTED";
	public static final String ACTION_CREATE_PLAN="CREATE PLAN";
	public static final String ACTION_CHNAGE_PLAN="CHANGE PLAN";
	public static final String ACTION_SUSPEND="SUSPEND";
	public static final String ACTION_SEND_PROVISION="SEND PROVISION";
	public static final String ACTION_SEND_EMAIL="Send Email";
	public static final String ACTION_PROVISION_IT="Provision IT";
	public static final String ACTION_ACTIVE_LIVE_EVENT = "Active Live Event";
	public static final String ACTION_INACTIVE_LIVE_EVENT = "InActive Live Event";
	public static final String ACTION_CREATE_PAYMENT="Create Payment";
	public static final String ACTION_CARD_PAYMENT="Card Payment";
	public static final String ACTION_TOPUP_INVOICE_MAIL = "Invoice Mail";
	public static final String ACTION_NOTIFY_TECHNICALTEAM = "Notify Technical Team";
	 
	
	public static final String ACTION_RECURRING_DISCONNECT="RecurringDisconnect";
	public static final String ACTION_RECURRING_RECONNECTION="RecurringReconnection";
	public static final String ACTION_RECURRING_TERMINATION="RecurringTerminate";
	
	public static final String ACTION_NOTIFY_ACTIVATION="Notify Activation";
	public static final String ACTION_NOTIFY_DISCONNECTION="Notify Disconnection";
	public static final String ACTION_NOTIFY_RECONNECTION="Notify Reconnection";
	public static final String ACTION_NOTIFY_PAYMENT="Notify Payment";
	public static final String ACTION_SEND_PAYMENT = "Send Payment";
	public static final String ACTION_NOTIFY_CHANGEPLAN = "Notify ChangePlan";
	public static final String ACTION_NOTIFY_ORDER_TERMINATE="Notify Order Terminate";
	
	public static final String ACTION_NOTIFY_SMS_ACTIVATION="Notify_SMS_Activation";
	public static final String ACTION_NOTIFY_SMS_DISCONNECTION="Notify_SMS_Disconnection";
	public static final String ACTION_NOTIFY_SMS_RECONNECTION="Notify_SMS_Reconnection";
	public static final String ACTION_NOTIFY_SMS_PAYMENT="Notify_SMS_Payment";
	public static final String ACTION_NOTIFY_SMS_CHANGEPLAN = "Notify_SMS_ChangePlan";
	public static final String ACTION_NOTIFY_SMS_ORDER_TERMINATE="SMS_Order_Terminate";
	public static final String ACTION_CHANGE_START ="Change Start";
	
	public static final String ACTION_SEND_LEAD_MAIL ="Send Lead Mail";
	public static final String ACTION_SEND_LEAD_SMS ="Send Lead Sms";
	public static final String ACTION_SEND_FOLLOWUP_MAIL="Send FollowUp Mail";
	public static final String ACTION_SEND_FOLLOWUP_SMS="Send FollowUp Sms";
	public static final String ACTION_SEND_CREATE_TICKET_MAIL="Send Ticket Email";
	public static final String ACTION_EDIT_TICKET_MAIL="Add Comment Email";
	public static final String ACTION_CLOSE_TICKET_MAIL="Close Ticket Email";
	
	public static final String ACTION_NOTIFY_SUSPENSION="Notify Suspension";
	public static final String ACTION_NOTIFY_TERMINATION="Notify Termination";
	
	public static final String ACTION_NOTIFY_PAYMENT_ADJ="Notify Payment Adjustment";
	public static final String ACTION_NOTIFY_SMS_PAYMENT_ADJ="Notify_SMS_Payment_ADJ";
	
	public static final String ACTION_NOTIFY_PAYMENT_REVERSAL="Notify Payment Reversal";
	public static final String ACTION_NOTIFY_SMS_PAYMENT_REVERSAL="Notify_SMS_Payment_REV";
	public static final String ACTION_ACTIVATION_REQUEST = "Activation_request";
}


