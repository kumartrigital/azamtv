INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('NOTIFY ACTIVATION','Order Confirmation','Dear <CustomerName>','Thanks for subscribing to our services. 
Your service with <Service name> is activated on <Activation Date>.','Thanks<br><Reseller Name><br><Contact Name> & <Number>','E');

INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('NOTIFY DISCONNECTION','Disconnected Customers','Dear <CustomerName>','Your service with <Service name> is disconnected on <Disconnection Date>.Please call us or do the renew or Top-up through your selfcare portal.','Thanks<br><Reseller Name><br><Contact Name> & <Number>','E');

INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('NOTIFY RECONNECTION','Reconnected Customer','Dear <CustomerName>','Thanks for subscribing to our services. 
Your service with <Service name> is activated on <Reconnection Date>.','Thanks<br><Reseller Name><br><Contact Name> & <Number>','E');

INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('NOTIFY PAYMENT',' Payment Conformation','Dear <CustomerName>','We confirm the receipts of your payment <Amount> with Dated: <Payment Date>, done towards the Services.','Thanks<br><Reseller Name><br><Contact Name> & <Number>','E');

INSERT IGNORE INTO b_eventaction_mapping (event_name,action_name,process,is_deleted,is_synchronous) values ('Order activation','Notify Activation','workflow_events','Y','N');

INSERT IGNORE INTO b_eventaction_mapping (event_name,action_name,process,is_deleted,is_synchronous) values ('Order disconnection','Notify Disconnection','workflow_events','Y','N');

INSERT IGNORE INTO b_eventaction_mapping (event_name,action_name,process,is_deleted,is_synchronous) values ('Order reconnection','Notify Reconnection','workflow_events','Y','N');

INSERT IGNORE INTO b_eventaction_mapping (event_name,action_name,process,is_deleted,is_synchronous) values ('Notify Payment Receipt','Notify Payment','workflow_events','Y','N');

Drop procedure IF EXISTS gatewayDescription; 
DELIMITER //
create procedure gatewayDescription() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'description'
     and TABLE_NAME = 'c_paymentgateway_conf'
     and TABLE_SCHEMA = DATABASE())THEN
Alter table c_paymentgateway_conf ADD COLUMN description LONGTEXT  DEFAULT NULL;
END IF;
END //
DELIMITER ;
call gatewayDescription();
Drop procedure IF EXISTS gatewayDescription;

Drop procedure IF EXISTS eventActionDescription; 
DELIMITER //
create procedure eventActionDescription() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'description'
     and TABLE_NAME = 'b_event_actions'
     and TABLE_SCHEMA = DATABASE())THEN
Alter table b_event_actions ADD COLUMN description LONGTEXT  DEFAULT NULL;
END IF;
END //
DELIMITER ;
call eventActionDescription();
Drop procedure IF EXISTS eventActionDescription;


