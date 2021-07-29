Drop procedure IF EXISTS removeUniqueIndex;
DELIMITER //
create procedure removeUniqueIndex() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'status'
     and TABLE_NAME = 'b_country_currency'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_country_currency` 
DROP INDEX `country_ISD_UNIQUE` ,
DROP INDEX `country_key` ;

END IF;
END //
DELIMITER ;
call removeUniqueIndex();
Drop procedure IF EXISTS removeUniqueIndex;




Drop procedure IF EXISTS addUniqueIndex;
DELIMITER //
create procedure addUniqueIndex() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency'
     and TABLE_NAME = 'b_country_currency'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_country_currency` 
ADD UNIQUE INDEX `uk_curr_sts_bcurr` (`status` ASC, `base_currency` ASC, `currency` ASC);

END IF;
END //
DELIMITER ;
call addUniqueIndex();
Drop procedure IF EXISTS addUniqueIndex;





