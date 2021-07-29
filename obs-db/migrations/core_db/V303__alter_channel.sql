Drop procedure IF EXISTS language;
DELIMITER //
create procedure language() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'language'
     and TABLE_NAME = 'b_channel'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_channel` 
ADD COLUMN `language` VARCHAR(45) NULL DEFAULT NULL AFTER `is_deleted`;
END IF;
END //
DELIMITER ;
call language();
Drop procedure IF EXISTS language;

