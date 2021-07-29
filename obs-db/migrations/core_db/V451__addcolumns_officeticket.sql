
Drop procedure IF EXISTS addcolumns;
DELIMITER //
create procedure addcolumns() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'ticket_no'
     and TABLE_NAME = 'b_office_ticket'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_office_ticket` 
ADD COLUMN `ticket_no` VARCHAR(20) NOT NULL AFTER `lastmodified_date`,
ADD COLUMN `title` VARCHAR(200) NULL AFTER `ticket_no`,
ADD COLUMN `is_escalated` CHAR(1) NULL DEFAULT '0' AFTER `title`;


END IF;
END //
DELIMITER ;
call addcolumns();
Drop procedure IF EXISTS addcolumns;
