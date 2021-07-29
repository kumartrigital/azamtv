Drop procedure IF EXISTS paymentsource;
DELIMITER //
create procedure paymentsource()
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'payment_source'
     and TABLE_NAME = 'b_payments'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_payments`
ADD COLUMN `payment_source` VARCHAR(45) NULL DEFAULT NULL AFTER `ref_id`;
END IF;
END //
DELIMITER ;
call paymentsource();
Drop procedure IF EXISTS paymentsource;
