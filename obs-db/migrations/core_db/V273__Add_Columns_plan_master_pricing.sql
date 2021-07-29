Drop procedure IF EXISTS addcurrency1;
DELIMITER //
create procedure addCurrency1() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency'
     and TABLE_NAME = 'b_plan_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_plan_master` 
ADD COLUMN `currency` BIGINT(40) NOT NULL AFTER `plan_type`;
END IF;
END //
DELIMITER ;
call addcurrency1();
Drop procedure IF EXISTS addcurrency1;




Drop procedure IF EXISTS addcurrency2;
DELIMITER //
create procedure addCurrency2() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency'
     and TABLE_NAME = 'b_plan_pricing'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_plan_pricing` 
ADD COLUMN `currency` BIGINT(40) NOT NULL AFTER `duration`;
END IF;
END //
DELIMITER ;
call addcurrency2();
Drop procedure IF EXISTS addcurrency2;












