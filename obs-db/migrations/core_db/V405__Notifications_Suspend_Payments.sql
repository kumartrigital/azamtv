INSERT IGNORE INTO `b_message_template` (`template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) VALUES ('SERVICE_SUSPEND','Suspend Service','Dear <CustomerName><br>','Your service with <Service name> is expired on <Disconnection Date>.Please renew or top-up to avail services. Please call us or do the renew through your selfcare portal','Thanks <br/><Reseller Name> <br/> <Contact Name> & <Number>','E',NULL,NULL,NULL,NULL,'N');

INSERT IGNORE INTO `b_message_template` ( `template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) VALUES ('SERVICE_TERMINATION','Terminate Service','Dear <CustomerName><br>','Your service with <Service name> is Terminated on <Disconnection Date>.Please call us for any issues','Thanks <br/><Reseller Name> <br/> <Contact Name> & <Number>','E',NULL,NULL,NULL,NULL,'N');

INSERT IGNORE INTO `b_message_template` ( `template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) VALUES ('NOTIFY PAYMENT ADJUSTMENT',' Payment Adjustment Confirmation','Dear <CustomerName>','Adjustment of your payment <Amount> with Dated: <Payment Date>, \ndone towards the Services.','Thanks<br><Reseller Name><br><Contact Name> & <Number>','E',NULL,NULL,NULL,NULL,'N');


INSERT IGNORE INTO `b_message_template` ( `template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) VALUES ('NOTIFY PAYMENT REVERSAL','Payment Reversal Confirmation','Dear <CustomerName>','Reversal of your payment <Amount> with Dated: <Payment Date>, \ndone towards the Services.','Thanks<br><Reseller Name><br><Contact Name> & <Number>','E',NULL,NULL,NULL,NULL,'N');

INSERT IGNORE INTO `b_message_template` ( `template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) VALUES ('NOTIFY SMS PAYMENT ADJUSTMENT','Notify Payment Adjustment',NULL,'We confirm the Adjustment of your payment <Amount> with Dated: <Payment Date>, done towards the Services.',NULL,'M',NULL,NULL,NULL,NULL,'N');

INSERT IGNORE INTO `b_message_template` ( `template_description`,`subject`,`header`,`body`,`footer`,`message_type`,`createdby_id`,`created_date`,`lastmodifiedby_id`,`lastmodified_date`,`is_deleted`) VALUES ('NOTIFY SMS PAYMENT REVERSAL','Notify Payment Reversal',NULL,'We confirm the Reversal of your payment <Amount> with Dated: <Payment Date>, done towards the Services.',NULL,'M',NULL,NULL,NULL,NULL,'N');


INSERT IGNORE INTO `b_eventaction_mapping` ( `event_name`,`action_name`,`process`,`order_by`,`pre_post`,`process_params`,`is_deleted`,`is_synchronous`) VALUES ('Order Suspension','Notify Suspension','workflow_events',1,'N','','N','N');
INSERT IGNORE INTO `b_eventaction_mapping` ( `event_name`,`action_name`,`process`,`order_by`,`pre_post`,`process_params`,`is_deleted`,`is_synchronous`) VALUES ('Order Suspension','Notify_SMS_Suspension','workflow_events',1,'N','','N','N');
INSERT IGNORE INTO `b_eventaction_mapping` ( `event_name`,`action_name`,`process`,`order_by`,`pre_post`,`process_params`,`is_deleted`,`is_synchronous`) VALUES ('Order Termination','Notify Termination','workflow_events',1,'N','','N','N');
INSERT IGNORE INTO `b_eventaction_mapping` ( `event_name`,`action_name`,`process`,`order_by`,`pre_post`,`process_params`,`is_deleted`,`is_synchronous`) VALUES ('NOTIFY_PAYMENT_ADJ','Notify Payment Adjustment','workflow_events',1,'N','','N','N');
INSERT IGNORE INTO `b_eventaction_mapping` ( `event_name`,`action_name`,`process`,`order_by`,`pre_post`,`process_params`,`is_deleted`,`is_synchronous`) VALUES ('NOTIFY_PAYMENT_REVERSAL','Notify Payment Reversal','workflow_events',1,'N','','N','N');
INSERT IGNORE INTO `b_eventaction_mapping` ( `event_name`,`action_name`,`process`,`order_by`,`pre_post`,`process_params`,`is_deleted`,`is_synchronous`) VALUES ('NOTIFY_PAYMENT_ADJ','Notify_SMS_Payment_ADJ','workflow_events',1,'N',NULL,'N','N');
INSERT IGNORE INTO `b_eventaction_mapping` ( `event_name`,`action_name`,`process`,`order_by`,`pre_post`,`process_params`,`is_deleted`,`is_synchronous`) VALUES ('NOTIFY_PAYMENT_REVERSAL','Notify_SMS_Payment_REV','workflow_events',1,'N',NULL,'N','N');



