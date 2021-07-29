Drop procedure IF EXISTS itemAttribute;
DELIMITER //
create procedure itemAttribute() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_id'
     and TABLE_NAME = 'b_item_attribute'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_model_provision_mapping` 
RENAME TO  `b_item_attribute` ;

END IF;
END //
DELIMITER ;
call itemAttribute();
Drop procedure IF EXISTS itemAttribute;
