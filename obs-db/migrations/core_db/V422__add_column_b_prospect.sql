Drop procedure IF EXISTS preferedservice;
DELIMITER //
create procedure preferedservice() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'prefered_service'
     and TABLE_NAME = 'b_prospect'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_prospect` 
ADD COLUMN `prefered_service` VARCHAR(100) NULL DEFAULT NULL AFTER `district`;
END IF;
END //
DELIMITER ;
call preferedservice();
Drop procedure IF EXISTS preferedservice;  









