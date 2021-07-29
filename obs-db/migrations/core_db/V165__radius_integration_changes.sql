
insert ignore into m_code_value values(null,13,'Radius',7);
SET SQL_SAFE_UPDATES = 0;
update job set name='RADIUS', display_name='Radius Integration',job_key='1RADIUSJobDetaildefault _ DEFAULT' where name="MIDDLEWARE";
update job_parameters set param_value='Radius' where param_name='ProvSystem';
update job_parameters set param_value='hugo' where param_name='Username';
update job_parameters set param_value='hugoadmin' where param_name='Password';
update job_parameters set param_value='http://v241.streamingmedia.is:8556/' where param_name='URL';


Drop procedure IF EXISTS paymentDates; 
DELIMITER //
create procedure paymentDates() 
Begin
IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE  COLUMN_NAME ='lastmodifiedby_id' 
       and TABLE_NAME = 'b_paymentgateway'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_paymentgateway modify COLUMN `lastmodifiedby_id` bigint(20) DEFAULT NULL after `created_date`;
END IF;
END //
DELIMITER ;
call paymentDates();
Drop procedure IF EXISTS paymentDates;

delete from c_configuration where name='user-login';
delete from c_configuration where name='reschedule-future-repayments';
delete from c_configuration where name='reschedule-repayments-on-holidays';
delete from c_configuration where name='allow-transactions-on-holiday';
delete from c_configuration where name='reschedule-future-repayments';
delete from c_configuration where name='allow-transactions-on-non_workingday';
delete from c_configuration where name='date-format';
delete from c_configuration where name='renewal';
delete from c_configuration where name='device-agrement-type';
delete from c_configuration where name='is-paypal-for-ios';
delete from c_configuration where name='active-devices';
delete from c_configuration where name='online-paymode';

update c_configuration set name='balance-check' WHERE name='forcible-balance-check';
update c_configuration set name='cuncerrent-sessions' WHERE name='active-viewers';
--update c_configuration set name='systemadmin-emailId' WHERE name='media-crash-email';


insert ignore into `b_eventaction_mapping`(`id`,`event_name`,`action_name`,`process`,`is_deleted`,`is_synchronous`) values (null,'Create Live Event','Active Live Event','workflow_events','N','Y');




Drop procedure IF EXISTS CreateIndexclientList; 
DELIMITER //
create procedure CreateIndexclientList() 
Begin
 
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE
`TABLE_CATALOG` = 'def' AND `TABLE_SCHEMA` = DATABASE() AND
`TABLE_NAME` = 'b_owned_hardware' AND `INDEX_NAME` = 'idx_boh_cid')THEN
create index idx_boh_cid on b_owned_hardware (client_id) ;
END IF;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE
`TABLE_CATALOG` = 'def' AND `TABLE_SCHEMA` = DATABASE() AND
`TABLE_NAME` = 'b_owned_hardware' AND `INDEX_NAME` = 'idx_boh_del')THEN
create index idx_boh_del on b_owned_hardware (is_deleted) ;
END IF;

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE
`TABLE_CATALOG` = 'def' AND `TABLE_SCHEMA` = DATABASE() AND
`TABLE_NAME` = 'b_allocation' AND `INDEX_NAME` = 'idx_alo_del')THEN
create index idx_alo_del on b_allocation (is_deleted) ;
END IF;


IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE
`TABLE_CATALOG` = 'def' AND `TABLE_SCHEMA` = DATABASE() AND
`TABLE_NAME` = 'b_allocation' AND `INDEX_NAME` = 'idx_bca_addkey')THEN
create index idx_bca_addkey on b_allocation (is_deleted) ;
END IF;

END //
DELIMITER ;
call CreateIndexclientList();

Drop procedure IF EXISTS CreateIndexclientList;

SET SQL_SAFE_UPDATES = 1;
