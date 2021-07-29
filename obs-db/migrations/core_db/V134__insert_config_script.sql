
insert ignore into c_configuration VALUES (null,'CHANGE_PLAN_ALIGN_DATES',1,null);
insert ignore into c_configuration VALUES (null,'Disconnection Credit',1,null);
-- insert ignore  into `b_charge_codes` values (null,'NONE','NONE','NRC',0,'NONE',1,'NONE');

insert ignore into m_permission  VALUES (null,'billing','UPDATEIPSTATUS_IPPOOLMANAGEMENT','IPPOOLMANAGEMENT','UPDATEIPSTATUS',0);               
insert ignore  into m_permission VALUES (null,'billing','ACTIVATE_ACTIVATIONPROCESS','ACTIVATIONPROCESS','ACTIVATE',0);
alter TABLE b_charge modify  `discount_code` varchar(20) NOT NULL;

Drop procedure IF EXISTS clientuser1; 
DELIMITER //
create procedure clientuser1() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'auth_pin'
     and TABLE_NAME = 'b_clientuser'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_clientuser Add column auth_pin varchar(10) default null after unique_reference;
END IF;
END //
DELIMITER ;
call clientuser1();

Drop procedure IF EXISTS clientuser1;
SET FOREIGN_KEY_CHECKS=0; 
delete from b_message_template where template_description = "SELFCARE REGISTRATION";
INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('SELFCARE REGISTRATION','OBS Selfcare','Dear <PARAM1>','Your Selfcare User Account has been successfully created,Following are the User login Details. \n userName : <PARAM2> , \n password : <PARAM3> .','Thankyou','E'); 
SET FOREIGN_KEY_CHECKS=1;


