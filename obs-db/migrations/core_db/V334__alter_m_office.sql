Drop procedure IF EXISTS subscriberDues;
DELIMITER //
create procedure subscriberDues() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
    WHERE COLUMN_NAME = 'PaymentType` '
     and TABLE_NAME = 'm_office'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `m_office` 
ADD COLUMN `Subscriber_dues` TINYINT(4) NULL DEFAULT 0 AFTER `commision_model`,
ADD COLUMN `Payment_Type` CHAR(1) NULL DEFAULT NULL AFTER `Subscriber_dues`;
END IF;
END //
DELIMITER ;
call subscriberDues();
Drop procedure IF EXISTS subscriberDues;
