Drop procedure IF EXISTS collectionBy;
DELIMITER //
create procedure collectionBy()
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
    WHERE COLUMN_NAME = 'collection_by'
     and TABLE_NAME = 'm_payments'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_payments` 
ADD COLUMN `collection_by` BIGINT(20) NOT NULL AFTER `cancel_remark`;
END IF;
END //
DELIMITER ;
call collectionBy();
Drop procedure IF EXISTS collectionBy;

