Drop procedure IF EXISTS addSubCategory;
DELIMITER //
create procedure addSubCategory() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'sub_category'
     and TABLE_NAME = 'b_ticket_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_ticket_master` 
ADD COLUMN `sub_category` VARCHAR(100) AFTER `resolution_description`;

END IF;
END //
DELIMITER ;
call addSubCategory();
Drop procedure IF EXISTS addSubCategory;
