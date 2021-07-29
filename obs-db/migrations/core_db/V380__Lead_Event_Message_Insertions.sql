INSERT ignore INTO `b_eventaction_mapping` (`event_name`, `action_name`, `process`, `order_by`, `pre_post`, `process_params`, `is_deleted`, `is_synchronous`) VALUES ('Create Lead', 'Send Lead Mail', 'workflow_events', '1', 'N', 'Lead Name,Lead Id,Lead Created Date', 'N', 'N');


INSERT ignore INTO `b_eventaction_mapping` (`event_name`, `action_name`, `process`, `order_by`, `pre_post`, `process_params`, `is_deleted`, `is_synchronous`) VALUES ('Create Lead', 'Send Lead Sms', 'workflow_events', '1', 'N', 'Lead Name,Lead Id,Lead Created Date', 'N', 'N');




INSERT ignore INTO `b_message_template` (`template_description`, `subject`, `header`, `body`, `footer`, `message_type`,  `is_deleted`) VALUES ('CREATE_LEAD', 'Lead Creation', 'Dear <PARAM1><br>', 'The Lead is created with <PARAM2>  on <PARAM3>', 'Thank you', 'E', 'N');



INSERT ignore INTO `b_message_template` (`template_description`, `subject`, `body`, `message_type`,`is_deleted`) VALUES ('SMS_CREATE_LEAD', 'Sms Creation','Dear <PARAM1>,The Lead is created with <PARAM2>  on <PARAM3>',  'M', 'N');



