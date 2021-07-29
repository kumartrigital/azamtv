Drop procedure IF EXISTS addcolumn;
DELIMITER //
create procedure addcolumn() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE COLUMN_NAME = 'param_category'
     and TABLE_NAME = 'b_service_detail'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE `b_service_detail` 
ADD COLUMN `param_category` VARCHAR(1) default NULL AFTER `param_value`;

END IF;
END //
DELIMITER ;
call addcolumn();
Drop procedure IF EXISTS addcolumn;


