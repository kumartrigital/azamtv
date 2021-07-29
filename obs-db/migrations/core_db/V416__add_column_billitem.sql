Drop procedure IF EXISTS billItem;
DELIMITER //
create procedure billItem() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency_id'
     and TABLE_NAME = 'b_bill_item'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_bill_item 
ADD COLUMN `currency_id` BIGINT(40) NOT NULL AFTER `bill_id`;
END IF;
END //
DELIMITER ;
call billItem();
Drop procedure IF EXISTS billItem;



