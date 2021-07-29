/*delete from b_message_template where template_description = "CREATE SELFCARE";

INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('CREATE SELFCARE', 'OBS Selfcare','Dear <PARAM1>','Your Selfcare User Account has been successfully created,Following are the User login Details. <br/> userName : <PARAM2> , <br/> password : <PARAM3> .','Thankyou','E');

delete from b_message_template where template_description = "SELFCARE REGISTRATION";

INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('SELFCARE REGISTRATION','Register Confirmation','Hai','Your Registration has been successfully completed.To approve this Registration please click on this link: <br/> URL : <PARAM1>.','Thankyou','E');

delete from b_message_template where template_description = "NEW SELFCARE PASSWORD";

INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('NEW SELFCARE PASSWORD','Reset Password','Dear <PARAM1>','The password for your SelfCare User Portal Account- <PARAM2>  was reset. . <br/> Password : <PARAM3>.','Thankyou','E');

INSERT IGNORE INTO b_message_template(template_description,subject,header,body,footer,message_type) values ('PROVISION CREDENTIALS','OBS Provision Credentials','Dear <PARAM1>','Your OBS Subscriber Account has been successfully created And Following are the Account Details.  <br/> subscriberUid : <PARAM2> , <br/>  Authpin : <PARAM3> .','Thankyou','E');
*/
Drop procedure IF EXISTS deviceId; 
DELIMITER //
create procedure deviceId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'device_id'
     and TABLE_NAME = 'b_clientuser'
     and TABLE_SCHEMA = DATABASE())THEN 
alter table b_clientuser Add column device_id varchar(20) default null after unique_reference;
END IF;
END //
DELIMITER ;
call deviceId();

Drop procedure IF EXISTS deviceId;

