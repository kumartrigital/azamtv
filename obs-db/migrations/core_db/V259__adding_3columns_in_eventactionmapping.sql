Drop procedure IF EXISTS addorderby;
DELIMITER //
create procedure addorderby() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'order_by'
     and TABLE_NAME = 'b_eventaction_mapping'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_eventaction_mapping` 
ADD COLUMN `order_by` INT(2) NULL DEFAULT NULL AFTER `process`,
ADD COLUMN `pre_post` CHAR(1) NULL DEFAULT NULL AFTER `order_by`,
ADD COLUMN `process_params` TEXT NULL AFTER `pre_post`;
END IF;
END //
DELIMITER ;
call addorderby();
Drop procedure IF EXISTS addorderby;
