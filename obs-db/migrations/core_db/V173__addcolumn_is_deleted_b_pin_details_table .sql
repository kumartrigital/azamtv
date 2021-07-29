DELIMITER //
create procedure deleteVoucherPin() 
Begin
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'is_deleted'
     and TABLE_NAME = 'b_pin_details'
     and TABLE_SCHEMA = DATABASE())THEN
Alter  table b_pin_details add column is_deleted char(2) DEFAULT 'N';
END IF;
END //
DELIMITER ;
call deleteVoucherPin();
Drop procedure IF EXISTS deleteVoucherPin; 

INSERT IGNORE INTO m_permission VALUES (null,'billing', 'UPDATE_VOUCHER', 'VOUCHER', 'UPDATE', 0);
INSERT IGNORE INTO m_permission VALUES (null,'billing', 'DELETE_VOUCHER', 'VOUCHER', 'DELETE', 0);
