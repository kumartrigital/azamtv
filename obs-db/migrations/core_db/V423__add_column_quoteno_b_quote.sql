
Drop procedure IF EXISTS quoteno;
DELIMITER //
create procedure quoteno() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'quote_no'
     and TABLE_NAME = 'b_quote'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_quote` 
ADD COLUMN `quote_no` VARCHAR(45) NOT NULL AFTER `notes`;
END IF;
END //
DELIMITER ;
call quoteno();
Drop procedure IF EXISTS quoteno;  

