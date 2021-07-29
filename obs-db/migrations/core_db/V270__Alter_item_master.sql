Drop procedure IF EXISTS itemMaster;
DELIMITER //
create procedure itemMaster() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'currency_id'
     and TABLE_NAME = 'b_item_master'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_item_master` 
ADD COLUMN `currency_id` BIGINT(20) NOT NULL AFTER `selector_description`;

END IF;
END //
DELIMITER ;
call itemMaster();
Drop procedure IF EXISTS itemMaster;
