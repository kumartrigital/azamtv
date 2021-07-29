INSERT IGNORE INTO `c_configuration`( name, enabled, value, module, description) 
VALUES ('config-appUser', '0', '{billing}', '', 'description');


INSERT IGNORE INTO `b_eventaction_mapping`( event_name, action_name, process, is_deleted, is_synchronous) 
VALUES ('Notify Technical Team', 'Notify Technical Team', 'workflow_events', 'N', 'N');

INSERT IGNORE INTO `b_message_template`( template_description, subject, header, body, footer, message_type) 
VALUES ('NOTIFY_TECHNICAL_TEAM', 'Technical Team','' ,'You get Notification for <ActionType>. 
clientId: <clientId>,
order/ticket Id : <id>.','' ,'M');


