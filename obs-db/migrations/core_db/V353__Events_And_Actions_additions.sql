INSERT ignore INTO `b_eventaction_mapping` (`event_name`, `action_name`, `process`, `order_by`, `pre_post`, `process_params`, `is_deleted`, `is_synchronous`) VALUES ('FollowUp Lead', 'Send FollowUp Mail', 'workflow_events', '1', 'N', 'Lead Name, Lead ID, Lead Status, Lead Followup Date', 'N', 'N');
INSERT ignore INTO `b_eventaction_mapping` ( `event_name`, `action_name`, `process`, `order_by`, `pre_post`, `process_params`, `is_deleted`, `is_synchronous`) VALUES ('FollowUp Lead', 'Send FollowUp Sms', 'workflow_events', '1', 'N', 'Lead Name, Lead ID, Lead Status, Lead FollowUp Date', 'N', 'N');


INSERT ignore INTO `b_message_template` (`template_description`, `subject`, `header`, `body`, `footer`, `message_type`, `is_deleted`) VALUES ('FOLLOWUP_LEAD_MAIL', 'FollowUp Mail', 'Dear <PARAM1><br>', 'The ID#<PARAM2> has status\n<PARAM3> and the next communication Date is on <PARAM4>', 'Thanks<br><Reseller Name><br><Contact Name> & <Number>', 'E', 'N');

INSERT ignore INTO `b_message_template` (`template_description`, `subject`, `body`, `message_type`, `is_deleted`) VALUES ('SMS_LEAD_FOLLOWUP', 'FollowUp Message', 'Dear <PARAM1>,\nThe ID#<PARAM2> has status <PARAM3> \n and the next communication Date is on <PARAM4>', 'M', 'N');


INSERT ignore INTO `b_eventaction_mapping` (`event_name`, `action_name`, `process`, `order_by`, `pre_post`, `process_params`, `is_deleted`, `is_synchronous`) VALUES ('Create Ticket', 'Send Ticket Sms', 'workflow_events', '1', 'N', 'Customer Name, Ticket ID, Created Date ', 'N', 'N');

INSERT ignore INTO `b_message_template` (`template_description`, `subject`, `header`, `body`, `footer`, `message_type`, `is_deleted`) VALUES ('CREATE_TICKET', 'Ticket Created', 'Dear <PARAM1><br>', 'The Ticket #<PARAM2> is Created On Date <PARAM3>', 'Thanks<br><Reseller Name><br><Contact Name> & <Number>', 'E',  'N');

INSERT ignore INTO `b_eventaction_mapping` ( `event_name`, `action_name`, `process`, `order_by`, `pre_post`, `process_params`, `is_deleted`, `is_synchronous`) VALUES ( 'Customer Activation', 'SEND  Customer SMS', 'workflow_events', '1', 'N', 'Customer Name:<PARAM1>,userName:<PARAM2>,password:<PARAM3>', 'N', 'N');
INSERT ignore INTO `b_eventaction_mapping` ( `event_name`, `action_name`, `process`, `order_by`, `pre_post`, `process_params`, `is_deleted`, `is_synchronous`) VALUES ( 'Customer Activation', 'Send Customer Email', 'workflow_events', '1', 'N', 'Customer Name:<PARAM1>,userName:<PARAM2>,password:<PARAM3>', 'N', 'N');


INSERT ignore INTO `b_message_template` (`template_description`, `subject`, `header`, `body`, `footer`, `message_type`, `is_deleted`) VALUES ( 'CREATE CUSTOMER', 'Create Customer', 'br<PARAM1>', 'NGB Customer has been successfully created .You can login using the following credentials.Customer Name:<PARAM1>, userName : <PARAM2> , \n password : <PARAM3> .', 'Thankyou', 'E','N');
INSERT ignore INTO `b_message_template` (`template_description`, `subject`,  `body`,  `message_type`, `is_deleted`) 
VALUES
 ('SMS CREATE CUSTOMER', 'Create Customer',  'NGB  Customer has been successfully created .You can login using the following credentials.Customer Name:<PARAM1>, username: <PARAM2> ,password: <PARAM3>',  'M','N');




