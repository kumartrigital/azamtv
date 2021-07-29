drop view netrevenue_vw_2015;

drop view netrevenueDtls_vw;

insert ignore into b_eventaction_mapping(`event_name`,
`action_name`,
`process`,
`is_deleted`,
`is_synchronous`) 
values('Add Plan', 'Invoice', 'workflow_events', 'N', 'Y');

insert ignore into b_eventaction_mapping(`event_name`,
`action_name`,
`process`,
`is_deleted`,
`is_synchronous`) 
values('Add Plan', 'Notify Activation', 'workflow_events', 'N', 'N');

insert ignore into b_eventaction_mapping(`event_name`,
`action_name`,
`process`,
`is_deleted`,
`is_synchronous`) 
values('Add Plan', 'Notify_SMS_Activation', 'workflow_events', 'N', 'N');


insert ignore into b_eventaction_mapping(`event_name`,
`action_name`,
`process`,
`is_deleted`,
`is_synchronous`) 
values('Order disconnection', 'Invoice', 'workflow_events', 'N', 'Y');

insert ignore into b_eventaction_mapping(`event_name`,
`action_name`,
`process`,
`is_deleted`,
`is_synchronous`) 
values('Order Suspension', 'Invoice', 'workflow_events', 'N', 'Y');

insert ignore into b_eventaction_mapping(`event_name`,
`action_name`,
`process`,
`is_deleted`,
`is_synchronous`) 
values('Order Reactivation', 'Invoice', 'workflow_events', 'N', 'Y');

insert ignore into b_eventaction_mapping(`event_name`,
`action_name`,
`process`,
`is_deleted`,
`is_synchronous`) 
values('Change Plan', 'Invoice', 'workflow_events', 'N', 'Y');

insert ignore into b_eventaction_mapping(`event_name`,
`action_name`,
`process`,
`is_deleted`,
`is_synchronous`) 
values('Order Termination', 'Invoice', 'workflow_events', 'N', 'Y');




