
Drop procedure IF EXISTS renameline1column;
DELIMITER //
create procedure renameline1column() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'line_1'
     and TABLE_NAME = 'b_office_address'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_office_address` 
CHANGE COLUMN `line_1` `contact_person` VARCHAR(200) NULL DEFAULT NULL ;

END IF;
END //
DELIMITER ;
call renameline1column();
Drop procedure IF EXISTS renameline1column;



