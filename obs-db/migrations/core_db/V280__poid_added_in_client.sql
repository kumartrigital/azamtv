Drop procedure IF EXISTS addpoidcolumn;
DELIMITER //
create procedure addpoidcolumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'po_id'
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_client` 
ADD COLUMN `po_id` VARCHAR(20) NULL DEFAULT NULL AFTER `parent_id`;
END IF;
END //
DELIMITER ;
call addpoidcolumn();
Drop procedure IF EXISTS addpoidcolumn;

