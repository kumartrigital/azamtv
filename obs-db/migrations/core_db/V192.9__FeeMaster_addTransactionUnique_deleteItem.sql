Drop procedure IF EXISTS feeItem; 
DELIMITER //
create procedure feeItem() 
Begin
IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_id'
     and TABLE_NAME = 'b_fee_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_fee_master` DROP COLUMN `item_id`;
END IF;
END //
DELIMITER ;
call feeItem();
Drop procedure IF EXISTS feeItem;

Drop procedure IF EXISTS feeTransactionUnique; 
DELIMITER //
create procedure feeTransactionUnique() 
Begin
IF NOT EXISTS(SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE
CONSTRAINT_CATALOG = 'def' AND CONSTRAINT_TYPE='UNIQUE' AND TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'b_fee_master' AND CONSTRAINT_NAME='fee_transaction_type')THEN
ALTER TABLE b_fee_master ADD UNIQUE fee_transaction_type(`transaction_type`);
END IF;
END //
DELIMITER ;
call feeTransactionUnique();
Drop procedure IF EXISTS feeTransactionUnique;



















