Drop procedure IF EXISTS serviceId;
DELIMITER //
create procedure serviceId() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'service_id'
     and TABLE_NAME = 'b_plan_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_plan_detail` 
ADD COLUMN `service_id` INT(10) NOT NULL AFTER `product_id`;
END IF;
END //
DELIMITER ;
call serviceId();
Drop procedure IF EXISTS serviceId;

ALTER TABLE `b_plan_detail` 
ADD INDEX `fk_srId_sc` (`service_id` ASC);

