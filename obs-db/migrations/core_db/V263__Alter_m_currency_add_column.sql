
Drop procedure IF EXISTS addCountryCodeColumn;
DELIMITER //
create procedure addCountryCodeColumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'country_code'
     and TABLE_NAME = 'm_currency'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE`m_currency` 
ADD COLUMN `country_code` VARCHAR(3) NULL DEFAULT NULL AFTER `internationalized_name_code`,
ADD COLUMN `country_name` VARCHAR(45) NULL DEFAULT NULL AFTER `country_code`,
ADD COLUMN `type` VARCHAR(15) NOT NULL AFTER `country_name`,
CHANGE COLUMN `internationalized_name_code` `internationalized_name_code` VARCHAR(50) NULL DEFAULT NULL;

END IF;
END //
DELIMITER ;
call addCountryCodeColumn();
Drop procedure IF EXISTS addCountryCodeColumn;



SET SQL_SAFE_UPDATES=0;
UPDATE m_currency SET  type = 'Currency';
SET SQL_SAFE_UPDATES=1;

insert ignore into m_permission values(null,'organisation','CREATE_CURRENCY','CURRENCY','CREATE',0);

ALTER TABLE `b_country_currency` 
ADD COLUMN `valid_from` DATE NULL DEFAULT NULL AFTER `country_ISD`,
ADD COLUMN `valid_to` DATE NULL DEFAULT NULL AFTER `valid_from`,
CHANGE COLUMN `is_deleted` `is_deleted` CHAR(1) NULL DEFAULT NULL AFTER `valid_to`;




