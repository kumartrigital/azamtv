
Drop procedure IF EXISTS additemtypecolumn;
DELIMITER //
create procedure additemtypecolumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'item_type'
     and TABLE_NAME = 'b_model_provision_mapping'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_model_provision_mapping` 
ADD COLUMN `item_type` VARCHAR(10) NOT NULL AFTER `model`,
ADD COLUMN `make` VARCHAR(60) NULL AFTER `item_type`;

END IF;
END //
DELIMITER ;
call additemtypecolumn();
Drop procedure IF EXISTS additemtypecolumn;

