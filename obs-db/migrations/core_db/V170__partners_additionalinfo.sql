
-- INSERT IGNORE INTO c_paymentgateway_conf VALUES(null,'neteller',1,'');
SET SQL_SAFE_UPDATES = 0;
SET foreign_key_checks = 0;
insert ignore into m_code VALUES (null,'Office Type',0,'Office are created and mapped to type');
 
 set @id=(select id from m_code where code_name='Office Type');
insert ignore into m_code_value VALUES (null,@id,'Office',0);
insert ignore into m_code_value VALUES (null,@id,'Agent',0);
 set @codeid=(select id from m_code_value where code_id=@id and code_value='Office');
 update m_office set office_type= @codeid; 
 
INSERT IGNORE INTO m_code VALUES(null,'Partner Type',0,'Partner are created and mapped to type');
SET @id=(select id from m_code where code_name='Partner Type');

INSERT IGNORE INTO m_code_value VALUES(null,@id,'Partner',2);
INSERT IGNORE INTO m_code_value VALUES(null,@id,'Reseller',1);
INSERT IGNORE INTO m_role VALUES(null, 'Partner', 'partners only');

Create table if not exists del_partner (permission_name varchar(50));

Insert ignore into del_partner values ('READ_CLIENT');
Insert ignore into del_partner values ('CREATE_CLIENT');
Insert ignore into del_partner values ('CREATE_CLIENT_CHECKER');
Insert ignore into del_partner values ('UPDATE_CLIENT');
Insert ignore into del_partner values ('UPDATE_CLIENT_CHECKER');
Insert ignore into del_partner values ('DELETE_CLIENT');
Insert ignore into del_partner values ('DELETE_CLIENT_CHECKER');
Insert ignore into del_partner values ('READ_CLIENTIMAGE');
Insert ignore into del_partner values ('CREATE_CLIENTIMAGE');
Insert ignore into del_partner values ('CREATE_CLIENTIMAGE_CHECKER');
Insert ignore into del_partner values ('DELETE_CLIENTIMAGE');
Insert ignore into del_partner values ('DELETE_CLIENTIMAGE_CHECKER');
Insert ignore into del_partner values ('READ_CLIENTNOTE');
Insert ignore into del_partner values ('CREATE_CLIENTNOTE');
Insert ignore into del_partner values ('CREATE_CLIENTNOTE_CHECKER');
Insert ignore into del_partner values ('UPDATE_CLIENTNOTE');
Insert ignore into del_partner values ('UPDATE_CLIENTNOTE_CHECKER');
Insert ignore into del_partner values ('DELETE_CLIENTNOTE');
Insert ignore into del_partner values ('DELETE_CLIENTNOTE_CHECKER');
Insert ignore into del_partner values ('READ_CLIENTIDENTIFIER');
Insert ignore into del_partner values ('CREATE_CLIENTIDENTIFIER');
Insert ignore into del_partner values ('CREATE_CLIENTIDENTIFIER_CHECKER');
Insert ignore into del_partner values ('UPDATE_CLIENTIDENTIFIER');
Insert ignore into del_partner values ('UPDATE_CLIENTIDENTIFIER_CHECKER');
Insert ignore into del_partner values ('DELETE_CLIENTIDENTIFIER');
Insert ignore into del_partner values ('DELETE_CLIENTIDENTIFIER_CHECKER');
Insert ignore into del_partner values ('ACTIVATE_CLIENT');
Insert ignore into del_partner values ('ACTIVATE_CLIENT_CHECKER');
Insert ignore into del_partner values ('CREATE_SERVICE');
Insert ignore into del_partner values ('DELETE_SERVICE');
Insert ignore into del_partner values ('CREATE_ITEM');
Insert ignore into del_partner values ('UPDATE_ITEM');
Insert ignore into del_partner values ('DELETE_ITEM');
Insert ignore into del_partner values ('CREATE_ADDRESS');
Insert ignore into del_partner values ('UPDATE_ADDRESS');
Insert ignore into del_partner values ('DELETE_ADDRESS');
Insert ignore into del_partner values ('CREATE_ORDER');
Insert ignore into del_partner values ('UPDATE_ORDER');
Insert ignore into del_partner values ('DELETE_ORDER');
Insert ignore into del_partner values ('UPDATE_ORDERPRICE');
Insert ignore into del_partner values ('CREATE_PAYMENT');
Insert ignore into del_partner values ('UPDATE_PAYMRNT');
Insert ignore into del_partner values ('DELETE_PAYMENT');
Insert ignore into del_partner values ('CREATE_ADJUSTMENT');
Insert ignore into del_partner values ('UPDATE_ADJUSTMENT');
Insert ignore into del_partner values ('DELETE_ADJUSTMENT');
Insert ignore into del_partner values ('CREATE_BILLMASTER');
Insert ignore into del_partner values ('CREATE_ONETIMESALE');
Insert ignore into del_partner values ('UPDATE_ONETIMESALE');
Insert ignore into del_partner values ('CREATE_TICKET');
Insert ignore into del_partner values ('UPDATE_TICKET');
Insert ignore into del_partner values ('CLOSE_TICKET');
Insert ignore into del_partner values ('READ_INVENTORY');
Insert ignore into del_partner values ('CREATE_INVENTORY');
Insert ignore into del_partner values ('UPDATE_INVENTORY');
Insert ignore into del_partner values ('DELETE_INVENTORY');
Insert ignore into del_partner values ('CREAT_EVENTORDER');
Insert ignore into del_partner values ('CREATE_EVENToRDER');
Insert ignore into del_partner values ('CREATE_GRN');
Insert ignore into del_partner values ('CREATE_ALLOCATION');
Insert ignore into del_partner values ('RENEWAL_ORDER');
Insert ignore into del_partner values ('RECONNECT_ORDER');
Insert ignore into del_partner values ('CREATE_OWNEDHARDWARE');
Insert ignore into del_partner values ('READ_ORDER');
Insert ignore into del_partner values ('READ_DOCUMENT');
Insert ignore into del_partner values ('READ_PAYMENT');
Insert ignore into del_partner values ('READ_ADJUSTMENT');
Insert ignore into del_partner values ('READ_ONETIMESALE');
Insert ignore into del_partner values ('READ_FINANCIALTRANSACTIONS');
Insert ignore into del_partner values ('READ_ADDRESS');
Insert ignore into del_partner values ('READ_CLIENTBALANCE');
Insert ignore into del_partner values ('CREATE_DOCUMENT');
Insert ignore into del_partner values ('READ_OWNEDHARDWARE');
Insert ignore into del_partner values ('UPDATE_DOCUMENT');
Insert ignore into del_partner values ('DELETE_DOCUMENT');
Insert ignore into del_partner values ('READ_ALLOCATION');
Insert ignore into del_partner values ('CREATE_SELFCARE');
Insert ignore into del_partner values ('CREATEPAYPAL_PAYMENT');
Insert ignore into del_partner values ('CREATE_MRN');
Insert ignore into del_partner values ('CREATE_SUPPLIER');
Insert ignore into del_partner values ('MOVE_MRN');
Insert ignore into del_partner values ('UNASSIGNSTAFF_CLIENT');
Insert ignore into del_partner values ('ASSIGNSTAFF_CLIENT');
Insert ignore into del_partner values ('CLOSE_CLIENT');
Insert ignore into del_partner values ('PROPOSETRANSFER_CLIENT');
Insert ignore into del_partner values ('PROPOSETRANSFER_CLIENT_CHECKER');
Insert ignore into del_partner values ('ACCEPTTRANSFER_CLIENT');
Insert ignore into del_partner values ('ACCEPTTRANSFER_CLIENT_CHECKER');
Insert ignore into del_partner values ('REJECTTRANSFER_CLIENT');
Insert ignore into del_partner values ('REJECTTRANSFER_CLIENT_CHECKER');
Insert ignore into del_partner values ('WITHDRAWTRANSFER_CLIENT');
Insert ignore into del_partner values ('WITHDRAWTRANSFER_CLIENT_CHECKER');
Insert ignore into del_partner values ('CREATE_Entitlement');
Insert ignore into del_partner values ('RETRACKOSDMESSAGE_ORDER');
Insert ignore into del_partner values ('CREATE_ASSOCIATION');
Insert ignore into del_partner values ('UPDATE_ASSOCIATION');
Insert ignore into del_partner values ('SWAPPING_HARDWARESWAPPING');
Insert ignore into del_partner values ('DELETE_ONETIMESALE');
Insert ignore into del_partner values ('ACTIVATIONPROCESS_ACTIVATE');
Insert ignore into del_partner values ('CREATE_PROVISIONINGSYSTEM');
Insert ignore into del_partner values ('UPDATE_PROVISIONINGSYSTEM');
Insert ignore into del_partner values ('DELETE_PROVISIONINGSYSTEM');
Insert ignore into del_partner values ('DEASSOCIATION_ASSOCIATION');
Insert ignore into del_partner values ('CANCEL_PAYMENT');
Insert ignore into del_partner values ('CHANGEPLAN_ORDER');
Insert ignore into del_partner values ('APPLYPROMO_ORDER');
Insert ignore into del_partner values ('DEALLOCATE_INVENTORY');
Insert ignore into del_partner values ('DELETE_OWNEDHARDWARE');
Insert ignore into del_partner values ('CREATE_ORDERSCHEDULING');
Insert ignore into del_partner values ('EXTENSION_ORDER');
Insert ignore into del_partner values ('DELETE_ORDERSCHEDULING');
Insert ignore into del_partner values ('ADD_PROVISIONINGSYSTEM');
Insert ignore into del_partner values ('CREATE_GROUPSDETAILS');
Insert ignore into del_partner values ('READ_CREDITDISTRIBUTION');
Insert ignore into del_partner values ('READ_EVENTPRICE');
Insert ignore into del_partner values ('READ_EVENTORDER');
Insert ignore into del_partner values ('READ_EVENT');
Insert ignore into del_partner values ('READ_SEARCH');
Insert ignore into del_partner values ('READ_ASSOCIATION');
Insert ignore into del_partner values ('READ_SUPPLIER');
Insert ignore into del_partner values ('READ_TICKET');
Insert ignore into del_partner values ('READ_MRNDETAILS');
Insert ignore into del_partner values ('READ_MRN');
Insert ignore into del_partner values ('READ_GRN');
Insert ignore into del_partner values ('READ_GROUPSDETAILS');
Insert ignore into del_partner values ('READ_INVOICE');
Insert ignore into del_partner values ('UPDATE_PROVISIONINGSERVICEPARAMS');
Insert ignore into del_partner values ('CREATE_OFFICEPAYMENT');
Insert ignore into del_partner values ('CREATE_CONFIGURATION');
Insert ignore into del_partner values ('CREATE_OFFICEADJUSTMENT');
Insert ignore into del_partner values ('READ_officefinancialTransactions');
Insert ignore into del_partner values ('CREATE_REDEMPTION');
Insert ignore into del_partner values ('MOVEITEM_MRN');
Insert ignore into del_partner values ('UPDATE_CACHE');
Insert ignore into del_partner values ('CREATEENQUIREY_PAYMENT');
Insert ignore into del_partner values ('CREATE_SELFCAREUDP');
Insert ignore into del_partner values ('UPDATE_CLIENTTAXEXEMPTION');
Insert ignore into del_partner values ('UPDATE_CLIENTBILLMODE');
Insert ignore into del_partner values ('READ_ITEM');
Insert ignore into del_partner values ('UPDATESTATUS_CLIENT');
Insert ignore into del_partner values ('TERMINATE_ORDER');
Insert ignore into del_partner values ('UPDATE_GRN');
Insert ignore into del_partner values ('EMAILVERIFICATION_SELFCARE');
Insert ignore into del_partner values ('SUSPEND_ORDER');
Insert ignore into del_partner values ('REACTIVE_ORDER');
Insert ignore into del_partner values ('CREATE_PARENTCLIENT');
Insert ignore into del_partner values ('CONFIRM_PROVISIONINGDETAILS');
Insert ignore into del_partner values ('UPDATE_SELFCARE');
Insert ignore into del_partner values ('ACTIVATE_ACTIVATIONPROCESS');
Insert ignore into del_partner values ('UPDATE_SUPPLIER');
Insert ignore into del_partner values ('CREATE_PROSPECT');
Insert ignore into del_partner values ('UPDATE_PROSPECT');
Insert ignore into del_partner values ('DELETE_PROSPECT');
Insert ignore into del_partner values ('FOLLOWUP_PROSPECT');
Insert ignore into del_partner values ('CONVERT_PROSPECT');
Insert ignore into del_partner values ('READ_PROSPECT');
Insert ignore into del_partner values ('CREATE_NEWSALE');
Insert ignore into del_partner values ('CREATE_SECONDSALE');
Insert ignore into del_partner values ('READ_ONETIMESALE');
Insert ignore into del_partner values ('CREATE_DEVICERENTAL');
Insert ignore into del_partner values ('CREATE_ORDERADDONS');
Insert ignore into del_partner values ('READ_PLANSERVICE');
Insert ignore into del_partner values ('READ_PARTNER');
Insert ignore into del_partner values ('READ_TRANSACTIONHISTORY');
Insert ignore into del_partner values ('READ_EVENTACTIONS');
Insert ignore into del_partner values ('READ_PARTNERDISBURSEMENT');
Insert ignore into del_partner values ('READ_PROVISIONINGSYSTEM');
Insert ignore into m_role_permission 
Select (Select id from m_role where name='Partner') as rid,b.id from del_partner a,m_permission b where a.permission_name=b.code;
drop table if exists del_partner;





INSERT IGNORE INTO m_code VALUES(null ,'Agreement Type', '0','Describe the agreement status');
SET @id=(select id from m_code where code_name='Agreement Type');

INSERT IGNORE INTO m_code_value VALUES(null, @id, 'Signed', '0');
INSERT IGNORE INTO m_code_value VALUES(null, @id, 'Pending', '0');


INSERT IGNORE INTO m_code VALUES(null ,'Source Category', '0','Describe the different sources');
SET @id=(select id from m_code where code_name='Source Category');

INSERT IGNORE INTO m_code_value VALUES(null, @id, 'Subscriptions', '0');
INSERT IGNORE INTO m_code_value VALUES(null, @id, 'Hardware', '1');
INSERT IGNORE INTO m_code_value VALUES(null, @id, 'On-demand', '2');

INSERT IGNORE INTO  m_permission VALUES (null,'organization', 'CREATE_PARTNER', 'PARTNER', 'CREATE', 0);
INSERT IGNORE INTO  m_permission VALUES (null, 'organisation', 'READ_PARTNER', 'PARTNER', 'READ', 0);
INSERT IGNORE INTO  m_permission VALUES (null, 'organisation', 'UPDATE_PARTNER', 'PARTNER', 'UPDATE', 0);
INSERT IGNORE INTO  m_permission VALUES (null,'organization', 'CREATE_PARTNERAGREEMENT', 'PARTNERAGREEMENT', 'CREATE', 0);
INSERT IGNORE INTO  m_permission VALUES (null,'organization', 'UPDATE_PARTNERAGREEMENT', 'PARTNERAGREEMENT', 'UPDATE', 0);
INSERT IGNORE INTO  m_permission VALUES(null, 'organisation', 'DELETE_PARTNERAGREEMENT', 'PARTNERAGREEMENT', 'DELETE', 0);
INSERT IGNORE INTO  m_permission VALUES(null, 'organisation', 'READ_PARTNERDISBURSEMENT', 'PARTNERDISBURSEMENT', 'READ', 0);
INSERT IGNORE INTO  m_permission VALUES(null, 'Client&orders', 'READ_EVENTACTIONS', 'EVENTACTIONS', 'READ', 0);


CREATE TABLE IF NOT EXISTS `m_office_additional_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `contact_name` varchar(200) DEFAULT NULL,
  `credit_limit` decimal(6,2) DEFAULT NULL,
  `partner_currency` varchar(50) DEFAULT NULL,
  `is_collective` char(2) DEFAULT 'N',
  PRIMARY KEY (`id`),
  KEY `partner_office_key` (`office_id`),
  CONSTRAINT `partner_office_key` FOREIGN KEY (`office_id`) REFERENCES `m_office` (`id`)
)  ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `m_controlaccount_balance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `account_type` varchar(50) NOT NULL,
  `balance_amount`  decimal(6,2) NOT NULL,
  `createdby_id` int(5) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `m_office_balance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `office_id` bigint(20) NOT NULL,
  `balance_amount`  decimal(6,2) NOT NULL,
  `createdby_id` int(5) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
  ) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

Drop procedure if exists officeAddress;
DELIMITER //
create procedure officeAddress()
BEGIN
IF NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='office_id' 
AND TABLE_NAME='b_office_address'
AND TABLE_SCHEMA=DATABASE())THEN
ALTER TABLE b_office_address add column `office_id` bigint(20) not null,
 ADD CONSTRAINT `partner_officeaddress_key`
 FOREIGN KEY(`office_id`)
 REFERENCES `m_office` (`id`);
END IF;
END // 
DELIMITER ;
call officeAddress();

Drop procedure if exists officeAddress;


Drop procedure if exists officeAddr1;
DELIMITER //
create procedure officeAddr1()
BEGIN
IF NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='office_number' 
AND TABLE_NAME='b_office_address'
AND TABLE_SCHEMA=DATABASE())THEN
ALTER TABLE `b_office_address` ADD COLUMN `office_number` VARCHAR(100) NULL DEFAULT NULL  AFTER `phone_number`;
END IF;
END // 
DELIMITER ;
call officeAddr1();
Drop procedure if exists officeInfo;

Drop procedure if exists officeInfo;
DELIMITER //
create procedure officeInfo()
BEGIN
IF NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.COLUMNS
WHERE COLUMN_NAME ='contact_name' 
AND TABLE_NAME='m_office_additional_info'
AND TABLE_SCHEMA=DATABASE())THEN
ALTER TABLE `m_office_additional_info` ADD COLUMN `contact_name` VARCHAR(200) NULL DEFAULT NULL  AFTER `office_id` ;
END IF;
END // 
DELIMITER ;
call officeInfo();
Drop procedure if exists officeInfo;

SET SQL_SAFE_UPDATES=1;
SET FOREIGN_KEY_CHECKS=1;

