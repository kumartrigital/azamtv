Drop procedure IF EXISTS cartoonNo;
DELIMITER //
create procedure cartoonNo() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'cartoon_no'
     and TABLE_NAME = 'b_item_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_item_detail` 
ADD COLUMN `cartoon_no` VARCHAR(100) NULL DEFAULT NULL AFTER `paired_item_id`;
END IF;
END //
DELIMITER ;
call cartoonNo();
Drop procedure IF EXISTS cartoonNo;

