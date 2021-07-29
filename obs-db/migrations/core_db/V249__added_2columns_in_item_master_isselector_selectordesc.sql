
Drop procedure IF EXISTS addisselector;
DELIMITER //
create procedure addisselector() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_selector'
     and TABLE_NAME = 'b_item_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_item_master` 
ADD COLUMN `is_selector` CHAR(1) NULL AFTER `reorder_level`,
ADD COLUMN `selector_description` VARCHAR(200) NULL AFTER `is_selector`;

END IF;
END //
DELIMITER ;
call addisselector();
Drop procedure IF EXISTS addisselector;
