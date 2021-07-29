Drop procedure IF EXISTS addcurrencyId;
DELIMITER //
create procedure addCurrencyId() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency'
     and TABLE_NAME = 'b_plan_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_plan_master` 
CHANGE COLUMN `currency` `currencyId` BIGINT(40) NOT NULL ;
END IF;
END //
DELIMITER ;
call addcurrencyId();
Drop procedure IF EXISTS addcurrencyId;



Drop procedure IF EXISTS addcurrId;
DELIMITER //
create procedure addcurrId() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency'
     and TABLE_NAME = 'b_plan_pricing'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_plan_pricing` 
CHANGE COLUMN `currency` `currencyId` BIGINT(40) NOT NULL ;
END IF;
END //
DELIMITER ;
call addcurrId();
Drop procedure IF EXISTS addcurrId;



Drop procedure IF EXISTS deletecountry;
DELIMITER //
create procedure deletecountry() 
Begin
  IF  EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'country'
     and TABLE_NAME = 'b_country_currency'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_country_currency` 
DROP COLUMN `country`;
END IF;
END //
DELIMITER ;
call deletecountry();
Drop procedure IF EXISTS deletecountry;

