

Drop procedure IF EXISTS changeitemClassinItem;
DELIMITER //
create procedure changeitemClassinItem() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_code'
     and TABLE_NAME = 'b_hw_plan_mapping'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_hw_plan_mapping` 
CHANGE COLUMN `item_code` `item_class` INT(10) NULL DEFAULT NULL ;
END IF;
END //
DELIMITER ;
call changeitemClassinItem();
Drop procedure IF EXISTS changeitemClassinItem;






