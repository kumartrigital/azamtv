Drop procedure IF EXISTS clientregister;
DELIMITER //
create procedure clientregister()
Begin
  IF NOT EXISTS (
   SELECT * FROM information_schema.COLUMNS
WHERE COLUMN_NAME = 'otp'
AND TABLE_NAME = 'b_client_register'
AND TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_client_register` 
ADD COLUMN `otp` VARCHAR(6) NOT NULL AFTER `lastmodified_date`;
END IF;
END //
DELIMITER ;
call clientregister();
Drop procedure IF EXISTS clientregister;

