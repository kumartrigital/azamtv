Drop procedure IF EXISTS addbillmode;
DELIMITER //
create procedure addbillmode() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'bill_mode'
     and TABLE_NAME = 'm_client'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_client` 
CHANGE COLUMN `bill_mode` `bill_mode` VARCHAR(30) NULL DEFAULT NULL ;
END IF;
END //
DELIMITER ;
call addbillmode();
Drop procedure IF EXISTS addbillmode;
