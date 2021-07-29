CREATE TABLE IF NOT EXISTS `b_office_bank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(100) DEFAULT NULL,
  `accno_1` varchar(200) DEFAULT NULL,
  `accno_2` varchar(200) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `bank_name` varchar(100) DEFAULT NULL,
  `branch` varchar(100) DEFAULT NULL,
  `swift_code` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci';

CREATE TABLE IF NOT EXISTS `b_office_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address_name` varchar(100) DEFAULT NULL,
  `line_1` varchar(200) DEFAULT NULL,
  `line_2` varchar(200) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `zip` varchar(20) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `email_id` varchar(50) DEFAULT NULL,
  `company_logo` varchar(20) DEFAULT NULL,
  `TIN` varchar(45) DEFAULT NULL,
  `VRN` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='utf8_general_ci';

Drop procedure IF EXISTS officeAddress; 
DELIMITER //
create procedure officeAddress() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='TIN'  
      and TABLE_NAME = 'b_office_address'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_office_address add column TIN varchar(45) DEFAULT NULL;
alter table b_office_address add  column VRN varchar(45) DEFAULT NULL;

END IF;
END //
DELIMITER ;
call officeAddress();
Drop procedure IF EXISTS officeAddress;

INSERT IGNORE INTO m_permission VALUES (null,'Finance', 'ONLINE_PAYMENTGATEWAY', 'PAYMENTGATEWAY', 'ONLINE', 4);

Drop procedure IF EXISTS paymentDates; 
DELIMITER //
create procedure paymentDates() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE  COLUMN_NAME ='lastmodified_date' 
       and TABLE_NAME = 'b_paymentgateway'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_paymentgateway ADD COLUMN `lastmodified_date` datetime DEFAULT NULL after `created_date`;
ALTER TABLE b_paymentgateway ADD COLUMN `lastmodifiedby_id` bigint(20) DEFAULT NULL after `created_date`;
END IF;
END //
DELIMITER ;
call paymentDates();
Drop procedure IF EXISTS paymentDates;

Drop procedure IF EXISTS clientUser; 
DELIMITER //
create procedure clientUser() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_NAME = 'b_clientuser' and COLUMN_NAME ='username' and COLUMN_KEY != 'UNI'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_clientuser` ADD UNIQUE (username); 
END IF;
END //
DELIMITER ;
call clientUser();
Drop procedure IF EXISTS clientUser;

SET SQL_SAFE_UPDATES = 0;
update b_item_master set units='PIECES' where units='NUMBERS';
update b_onetime_sale set units='PIECES' where units='NUMBERS';


Drop procedure IF EXISTS addsubfieldInPayment; 
DELIMITER //
create procedure addsubfieldInPayment() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME ='is_sub_payment'  
      and TABLE_NAME = 'b_payments'
     and TABLE_SCHEMA = DATABASE())THEN
alter table b_payments add column is_sub_payment char(45) DEFAULT 'N';


END IF;
END //
DELIMITER ;
call addsubfieldInPayment();
Drop procedure IF EXISTS addsubfieldInPayment;

