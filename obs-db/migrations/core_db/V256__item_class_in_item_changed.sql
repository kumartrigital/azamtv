

Drop procedure IF EXISTS changeitemClassinItem;
DELIMITER //
create procedure changeitemClassinItem() 
Begin
  IF EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_class'
     and TABLE_NAME = 'b_item_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_item_master` 
CHANGE COLUMN `item_class` `item_class` INT(2) NOT NULL ;
END IF;
END //
DELIMITER ;
call changeitemClassinItem();
Drop procedure IF EXISTS changeitemClassinItem;


