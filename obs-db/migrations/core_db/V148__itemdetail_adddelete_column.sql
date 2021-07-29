DELIMITER //
create procedure deleteItem() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_deleted'
     and TABLE_NAME = 'b_item_detail'
     and TABLE_SCHEMA = DATABASE())THEN
Alter  table b_item_detail add column is_deleted char(2) DEFAULT 'N';
END IF;
END //
DELIMITER ;
call deleteItem();
Drop procedure IF EXISTS deleteItem; 
