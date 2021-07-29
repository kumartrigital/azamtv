Drop procedure IF EXISTS addversionprovisioning;
DELIMITER //
create procedure addversionprovisioning() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
    WHERE COLUMN_NAME = 'version'
     and TABLE_NAME = 'b_provisioning_request'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_provisioning_request` 
ADD COLUMN `version` INT(10) NULL DEFAULT '1' AFTER `lastmodifiedby_id`;
END IF;
END //
DELIMITER ;
call addversionprovisioning();
Drop procedure IF EXISTS addversionprovisioning;


