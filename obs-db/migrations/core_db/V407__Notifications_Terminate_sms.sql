INSERT IGNORE INTO `b_eventaction_mapping`( `event_name`,`action_name`,`process`,`order_by`,`pre_post`,`process_params`,`is_deleted`,`is_synchronous`) VALUES ('Order Termination','SMS_Order_Terminate','workflow_events',1,'N','','N','N');


INSERT IGNORE INTO `b_message_template` ( `template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) 

VALUES ('SMS_SERVICE_TERMINATION','Terminate Service','','Your service with <Service name> is Terminated on <Disconnection Date>.Please call us for any issues','','M',NULL,NULL,NULL,NULL,'N');
