CREATE TABLE IF NOT EXISTS `temp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `generate_key` varchar(33) NOT NULL,
  `status` varchar(10) NOT NULL,
  `payment_data` varchar(500) DEFAULT 'NULL',
  `payment_status` varchar(15) DEFAULT 'INACTIVE',
  `createdby_id` bigint(20) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `lastmodifiedby_id` bigint(20) DEFAULT NULL,
  `lastmodified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `generate_key` (`generate_key`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='latin1_swedish_ci';


Drop procedure IF EXISTS temp; 
DELIMITER //
create procedure temp() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'payment_status'
     and TABLE_NAME = 'temp'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table temp add column payment_status varchar(20) DEFAULT 'INACTIVE';

END IF;
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'payment_data'
     and TABLE_NAME = 'temp'
     and TABLE_SCHEMA = DATABASE())THEN
alter  table temp add column payment_data varchar(500) DEFAULT NULL;
END IF;
END //
DELIMITER ;
call temp();

Drop procedure IF EXISTS temp;

INSERT IGNORE INTO `c_configuration`(`id`,`name`,`enabled`,`value`) values (null,'Registration_requires_device',1,'');


