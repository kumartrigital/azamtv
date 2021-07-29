SET SQL_SAFE_UPDATES = 0;

update job set name ='Requestor' where display_name='Requestor';

update job set name ='Invoice' where display_name='Invoice';

update job set name ='Messanger' where display_name='Messanger';

update job set name ='Auto Exipiry' where display_name='Auto Exipiry';

update job set name ='EventAction Processor' where display_name='EventAction Processor';

update job set name ='PushNotification' where display_name='PushNotification';

update job set name ='Middleware' where display_name='Middleware Integration';

update job set name ='Report Email' where display_name='Report Email';

update job set name ='Generate Statement' where display_name='Generate Statement';
update job set name ='Generate PDF' where display_name='Generate PDF';
Update r_enum_value set enum_message_property='Exact Prorata' where enum_name='billing_rules' and enum_id=100;
Update r_enum_value set enum_message_property='Full Month' where enum_name='billing_rules' and enum_id=200;
Update r_enum_value set enum_message_property='No Prorata' where enum_name='billing_rules' and enum_id=300;
Update r_enum_value set enum_message_property='Custom Prorata' where enum_name='billing_rules' and enum_id=400;

Drop procedure IF EXISTS addbillmaster; 
DELIMITER //
create procedure addbillmaster() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'group_id'
     and TABLE_NAME = 'b_bill_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_bill_master add group_id bigint(20) default null;

END IF;
  
END //
DELIMITER ;
call addbillmaster();

Drop procedure IF EXISTS addbillmaster;


Drop procedure IF EXISTS addticketmaster; 
DELIMITER //
create procedure addticketmaster() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'source_of_ticket'
     and TABLE_NAME = 'b_ticket_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_ticket_master add column source_of_ticket varchar(50) default NULL; 

END IF;
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'due_date'
     and TABLE_NAME = 'b_ticket_master'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_ticket_master add column due_date datetime default NULL;

END IF;
  
END //
DELIMITER ;
call addticketmaster();

Drop procedure IF EXISTS addticketmaster;

CREATE TABLE IF NOT EXISTS `b_payment_followup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `followup_date` datetime NOT NULL,
  `followup_reason` varchar(50) NOT NULL,
  `followup_desc` varchar(200) NOT NULL,
  `current_status` varchar(20) NOT NULL,
  `requested_status` varchar(10) NOT NULL,
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert IGNORE into m_code VALUES (null,'Suspension Reason',0,'Reason for Order Suspension');
SET @id=(select id from m_code where code_name='Suspension Reason');
insert IGNORE into m_code_value VALUES (null,@id,'Payment Due',0);
insert IGNORE into m_code_value VALUES (null,@id,'Vacations',0);

insert ignore into r_enum_value VALUES ('order_status',7,'REACTIVE','REACTIVE');
insert ignore into m_permission VALUES (null,'Ordering','REACTIVE_ORDER','ORDER','REACTIVE',0);



Drop procedure IF EXISTS addreactivedate;
DELIMITER //
create procedure addreactivedate() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'reactive_date'
     and TABLE_NAME = 'b_payment_followup'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER table b_payment_followup add column `reactive_date` datetime DEFAULT NULL;
END IF;
END //
DELIMITER ;
call addreactivedate();

Drop procedure IF EXISTS addreactivedate;

SET SQL_SAFE_UPDATES = 1;





