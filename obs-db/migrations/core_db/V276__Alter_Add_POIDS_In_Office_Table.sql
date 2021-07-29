Drop procedure IF EXISTS addpoidinoffice;
DELIMITER //
create procedure addpoidinoffice() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'po_id'
     and TABLE_NAME = 'm_office'
     and TABLE_SCHEMA = DATABASE())THEN
     ALTER TABLE `m_office` 
     ADD COLUMN `po_id` VARCHAR(20) NULL;

END IF;
END //
DELIMITER ;
call addpoidinoffice();
Drop procedure IF EXISTS addpoidinoffice;



