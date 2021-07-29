
Drop procedure IF EXISTS addingPriorityInProvisioning;
DELIMITER //
create procedure addingPriorityInProvisioning() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'priority'
     and TABLE_NAME = 'b_provisioning_request'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_provisioning_request` 
ADD COLUMN `priority` INT(2) NOT NULL DEFAULT 1 AFTER `end_date`;
END IF;
END //
DELIMITER ;
call addingPriorityInProvisioning();
Drop procedure IF EXISTS addingPriorityInProvisioning;
