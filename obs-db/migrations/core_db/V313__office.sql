Drop procedure IF EXISTS settlementPoId;
DELIMITER //
create procedure settlementPoId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'settlement_poId'
     and TABLE_NAME = 'm_office'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_office` 
ADD COLUMN `settlement_poId` VARCHAR(20) NULL DEFAULT NULL AFTER `po_id`;
END IF;
END //
DELIMITER ;
call settlementPoId();
Drop procedure IF EXISTS settlementPoId;
