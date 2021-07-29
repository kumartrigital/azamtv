Drop procedure IF EXISTS glid;
DELIMITER //
create procedure glid() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'gl_id'
     and TABLE_NAME = 'b_plan_pricing'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_plan_pricing` 
ADD COLUMN `gl_id` VARCHAR(10) NULL DEFAULT NULL AFTER `currencyId`,
ADD COLUMN `rounding_type` VARCHAR(100) NULL DEFAULT NULL AFTER `gl_id`;
END IF;
END //
DELIMITER ;
call glid();
Drop procedure IF EXISTS glid;
