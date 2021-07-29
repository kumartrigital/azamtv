Drop procedure IF EXISTS chargeTax;
DELIMITER //
create procedure chargeTax() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency_id'
     and TABLE_NAME = 'b_charge_tax'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_charge_tax` 
ADD COLUMN `currency_id` BIGINT(40) NOT NULL AFTER `bill_id`;
END IF;
END //
DELIMITER ;
call chargeTax();
Drop procedure IF EXISTS chargeTax;



Drop procedure IF EXISTS charge;
DELIMITER //
create procedure charge() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency_id'
     and TABLE_NAME = 'b_charge'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_charge`  
ADD COLUMN `currency_id` BIGINT(40) NOT NULL AFTER `bill_id`;
END IF;
END //
DELIMITER ;
call charge();
Drop procedure IF EXISTS charge;


Drop procedure IF EXISTS invoice;
DELIMITER //
create procedure invoice() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currencyId'
     and TABLE_NAME = 'm_invoice'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_invoice` 
ADD COLUMN `currencyId` BIGINT(40) NOT NULL AFTER `lastmodifiedby_id`;
END IF;
END //
DELIMITER ;
call invoice();
Drop procedure IF EXISTS invoice;


Drop procedure IF EXISTS onetimesale;
DELIMITER //
create procedure onetimesale() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'resource_id'
     and TABLE_NAME = 'b_onetime_sale'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_onetime_sale`
ADD COLUMN `resource_id` BIGINT(40) NOT NULL AFTER `device_mode`;
END IF;
END //
DELIMITER ;
call onetimesale();
Drop procedure IF EXISTS onetimesale;


Drop procedure IF EXISTS ukCurrStsBcurr;
DELIMITER //
create procedure ukCurrStsBcurr()
Begin
   IF EXISTS (
        SELECT *
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
    WHERE TC.TABLE_NAME = 'b_currency_exchange'
    AND TC.CONSTRAINT_TYPE = 'UNIQUE')THEN
ALTER TABLE `b_currency_exchange`
DROP INDEX `uk_curr_sts_bcurr` ;
END IF;
END //
DELIMITER ;
Drop procedure IF EXISTS ukCurrStsBcurr;


Drop procedure IF EXISTS ukCurreStsBcurre;
DELIMITER //
create procedure ukCurreStsBcurre()
Begin
  IF NOT EXISTS (
     SELECT *
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC
    WHERE TC.TABLE_NAME = 'b_currency_exchange'
    AND TC.CONSTRAINT_TYPE = 'UNIQUE')THEN
ALTER TABLE b_currency_exchange
ADD UNIQUE INDEX `uk_curre_sts_bcurre` (`currency`,`base_currency`, `is_deleted`);
END IF;
END //
DELIMITER ;
call ukCurreStsBcurre();
Drop procedure IF EXISTS ukCurreStsBcurre;



Drop procedure IF EXISTS baseCurrency;
DELIMITER //
create procedure baseCurrency() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'base_currency'
     and TABLE_NAME = 'b_currency_exchange'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_currency_exchange`
CHANGE COLUMN `base_currency` `base_currency` BIGINT(40) NULL DEFAULT NULL ;
END IF;
END //
DELIMITER ;
call baseCurrency();
Drop procedure IF EXISTS baseCurrency;



















